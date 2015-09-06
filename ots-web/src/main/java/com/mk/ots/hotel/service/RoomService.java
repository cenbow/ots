package com.mk.ots.hotel.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import cn.com.winhoo.mikeweb.orderpojo.CostTemp;
import cn.com.winhoo.mikeweb.util.DateTools;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.dao.CityDAO;
import com.mk.ots.hotel.dao.CostTempDAO;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.dao.RoomDAO;
import com.mk.ots.hotel.dao.RoomRepairDAO;
import com.mk.ots.hotel.dao.RoomTypeDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.TRoomMapper;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.price.bean.TBasePrice;
import com.mk.ots.price.bean.TPrice;
import com.mk.ots.price.dao.BasePriceDAO;
import com.mk.ots.price.dao.PriceDAO;
import com.mk.pms.order.dao.PmsOrderDAO;
import com.mk.pms.order.dao.PmsRoomOrderDAO;
/**
 * 房间
 * @author LYN
 *
 */
@Service
public class RoomService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());
        
    private final String syncPre="hotelRoomTypeSyn~"; 
    @Autowired 
    RoomDAO roomDAO=null;
    @Autowired
    private HotelDAO hotelDAO = null;
    @Autowired
    private CityDAO cityDAO = null;
    @Autowired
    private RoomTypeDAO roomTypeDAO = null;
    @Autowired
    private CostTempDAO costTempDAO = null;

    @Autowired
    private PmsRoomOrderDAO pmsRoomOrderDAO = null;

    @Autowired
    private PmsOrderDAO pmsOrderDAO = null;

    @Autowired
    private RoomRepairDAO roomRepairDAO = null;
    
    @Autowired
    private PriceDAO priceDAO = null;

    @Autowired
    private BasePriceDAO basePriceDAO = null;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private TRoomMapper tRoomMapper;
    
    @Autowired
    private OtsCacheManager cacheManager;


    /**
     * 检查房间是否可以预定
     * @param dateStr
     * @param endStr
     * @param roomid
     * @return
     */
    private boolean checkRoomISVC(String citycode,Long startL,Long endL,String roomid){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long diff = (  endL-startL) / (1000 * 3600 * 24);
        Calendar calendar = Calendar.getInstance();
        
        logger.debug("房态查询是否可订:citycode:{},startL{},endL:{},roomid{}",citycode,startL,roomid);
        Jedis jedis = cacheManager.getNewJedis();//获取jedis
        try{
            int count=0;
            for (int i = 0; i <= diff; i++) {
                calendar.setTime(new Date(startL));
                calendar.add(Calendar.DATE, i);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                Date day = calendar.getTime();
                String dayStr = sdf.format(day);                    
                String cacheKey=Constant.CACHENAME_BOOKEDROOM_PRE+citycode+Constant.CACHENAME_SEPARATOR+roomid+Constant.CACHENAME_SEPARATOR+dayStr;
                try{
                    if(jedis.exists(cacheKey)){
                        count++;
                        break;
                    }
                }catch(Exception e){                
                    logger.error("查询房态redis{}",e);
                    return false;
                }
            }
            return count==0;
        }finally{
            jedis.close();
        }
      }
    
    public TRoom findTRoomByRoomId(Long id){
        return TRoom.dao.findFirst("select * from t_room where id= ?",id);
    }
    
    
    /**
     * 计算每天的房间价格
     * @return
     */
    public boolean calCostPrice(Long hotelid,Long roomtypeid){
        logger.info("开始重新计算价格:hotelid:{},roomtypeid:{}",hotelid,roomtypeid);
        /**
         * 1、删除过期数据（昨天之前） 2、这个酒店、这个房型的房间数量（总数） 3、查找未结束的酒店预订单（PmsOrder 条件
         * cancel是false orderNum 和planNum不想等的 begintime和endtime 在3个月内 ）
         * 4、按照每天用总数减去 orderNum和planNum的差值 再加上当天客单数量： costtemp数值，剩余房间数量
         * 总数-当天 （orderNum-planNum）+当天客单数量 一共10间，预定5间 排房2间
         * 写入costtemp数据7间 5、查询所有未结束的客单（PmsRoomOrder 条件 begintime和endtime
         * 在3个月内,日租的订单），结果直接写入roomtemp
         */
        // 1,初始化数据,得到需要计算缓存的时间列表
        String reserveDayNumStr = "0";
        try {
            reserveDayNumStr = SysConfig.getInstance().getSysValueByKey(Constant.RESERVE_DAY_NUM);
        } catch (Exception e) {
            logger.error("取得系统参数配置 {} 出错：{}", Constant.RESERVE_DAY_NUM, e.getMessage());
        }
        logger.info("重新计算房价开始时间，系统配置表数据reserveDayNumStr{}",reserveDayNumStr);
        if (reserveDayNumStr == null || StringUtils.isBlank(reserveDayNumStr)){
            reserveDayNumStr = "0";
        }
        try {
            Integer.parseInt(reserveDayNumStr);
        } catch(Exception e) {
            logger.error("系统参数配置 {} 值 {} 无效数字.", Constant.RESERVE_DAY_NUM, reserveDayNumStr);
            reserveDayNumStr = "0";
        }
        Integer reserveDayNum = Integer.parseInt(reserveDayNumStr) + 2;// 系统配置预定天数
                                                                        // //(1),多两天
        Date now = new Date();
        Long nowMills = now.getTime();
        Long current = DateTools.getBeginMilliS(nowMills, -2, hotelid);// 计算缓存开始时间
                                                                        // //(2),后退两天
        Long end = DateTools.getBeginMilliS(nowMills, reserveDayNum,hotelid);
        List<Date> dateList = DateTools.getBeginDateList(new Date(current), new Date(end));
        logger.info("重新计算房价时间列表hotelid:{},roomtypeid:{}",hotelid,roomtypeid);

        Long curZero = DateTools.getMilliseconds(nowMills, -2);// 零点开始时间
        Long endZero = DateTools.getMilliseconds(nowMills,reserveDayNum);// 零点结束时间

        // 2,找到酒店和房型
        THotel hotel = hotelDAO.findHotelByHotelid("" + hotelid);
        if(hotel==null){
            logger.info("重新计算房价未找到酒店hotelid:{}",hotelid);
            ////throw MyErrorEnum.errorParm.getMyException("重新计算房价未找到酒店"+hotelid);
            return false;
        }
        Bean roomType = hotelDAO.findRoomTypeByRoomtypeid(""+ roomtypeid);

        String disid = hotel.get("disid").toString();
        Bean cityBean = cityDAO.getCityByDisId(disid);
        String cityid = cityBean.get("cityid").toString();
        String citycode = cityBean.get("citycode").toString();
        String thotelid = hotel.get("id").toString();

        // 4,删除过期数据（计算缓存开始时间 之前的数据）
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dateStr = sdf.format(new Date(current));
    
        //清空酒店房型日期之前的价格过期数据         
        logger.info("重新计算房价删除costtemp过期数据:citycode:{},thotelid:{},roomtypeid:{},date:{}",citycode,thotelid,roomtypeid,dateStr);
        costTempDAO.deleteDateBefore(citycode, thotelid, roomtypeid,dateStr);

        // 这个酒店、这个房型的房间数量（总数）
        //TODO: roomtypeNum 可以从roomList条数
        List<Bean> roomList = roomDAO.findRoom(roomtypeid);
        Integer roomTypeNum= roomList.size(); //= roomDAO.countHotelIdAndRoomType(roomtypeid).intValue();

        // 所有客单 PmsRoomOrder 条件 begintime和endtime 在未来3个月内（系统配置预定天数）,日租的客单
        logger.info("重新计算房价:hotelid:{},roomtypeid:{}",hotelid,roomtypeid);
        Map<String, BigDecimal> priceMap = this.getCost(roomType,hotelid,roomtypeid, null, dateList);
        BigDecimal cost = null;
        CostTemp costTemp = null;
        Long roomId = null;
        String dateKey = null;
        //循环添加costtem,roomtemp
        logger.debug("重新计算房价costtem,");
        List<Map<String,Object>> uplist = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> inlist = new ArrayList<Map<String,Object>>();
        Long costTempNum=0L;//计算房间可预定数，不在此计算，重新添加方法,暂时为0
        while (current <= end) {
            dateKey = sdf.format(new Date(current));// yyyyMMdd
            // 价格
            cost = priceMap.get(dateKey);
            cost = cost.setScale(0, BigDecimal.ROUND_UP);
            // 保存该房型剩余预定数量到缓存表中
            Map<String,Object> upmap= new HashMap<String,Object>();
            upmap.put("hotelid", hotelid);
            upmap.put("roomtypeid",roomtypeid);
            upmap.put("datekey", dateKey);
            upmap.put("costtempnum", costTempNum);//计算房间可预定数，不在此计算，重新添加方法
            upmap.put("cost", cost);
            uplist.add(upmap);
            Map<String,Object> inmap = new HashMap<String,Object>();
            inmap.put("cost", cost.setScale(0, BigDecimal.ROUND_UP));
            inmap.put("hotelid", hotelid);
            inmap.put("roomtypeid", roomtypeid);
            inmap.put("costtempnum", costTempNum);//计算房间可预定数，不在此计算，重新添加方法
            inmap.put("datekey", dateKey);
            inlist.add(inmap);
            logger.debug("重新计算房价添加到插入数据hotelid:{},roomtypeid:{}",hotelid,roomtypeid);
            
            current = DateTools.getBeginMilliS(current, 1, hotelid);
        }
        roomDAO.calCostPriceBatch(citycode,uplist,inlist);
        return true;
    }
    
    /**
     * 计算房间可预定数量
     */
    public void calCanBookRoomNum(){
        /*暂时不计算可预定房间数
        // 所有客单 PmsRoomOrder 条件 begintime和endtime 在未来3个月内（系统配置预定天数）,日租的客单
        Map<String, Integer> roomOrderNumMap = pmsRoomOrderDAO
                .getRoomOrderNumByDay(roomtypeid, current, end,statusList);
        logger.info("计算房态 获取未来3个月内PMS订单: roomTypeId:{},current:{},end:{},roomOrderNumMap:{}",roomtypeid, current, end,statusList, roomOrderNumMap);
        
        //PMS 预留房间
        Map<String, Integer> orderNoPlanNumMap = pmsOrderDAO
                .getOrderNoPlanNumByDay(roomtypeid, current, end);
        logger.info("获取PMS预留房间: roomTypeId:{},current:{},end:{},orderNoPlanNumMap:{}", roomtypeid, current, end,orderNoPlanNumMap);
        // Map<String,Integer> repairNumMap =
        // roomRepairDAO.getRepairNumByDay(roomTypeId,reserveDayNum);   
        
        Map<String, Integer> repairNumMap = roomRepairDAO
                .getRepairNumByDay(roomtypeid, curZero, endZero);
        logger.info("获取维修房间数量:roomTypeId:{},curZero:{},endZero:{},repairNumMap:{}",roomtypeid,curZero,endZero,repairNumMap);
        //清空对应酒店房型下的已经占房间
            logger.info("计算房态 全部roomtemp删除已经占用房间数据:citycode:{},thotelid:{},roomtypeid:{},date:{}",citycode,thotelid,roomtypeid,null);
        */
        Integer roomOrderNum = null;
        Integer orderNoPlanNum = null;
        Integer repairNum = null;
        Integer costTempNum = null;
        BigDecimal cost = null;
    }
    
    
    

    /**
     * 计算房价
     * @param hotelId
     * @param roomTypeId
     * @param mId
     * @param dateList
     * @return
     */
    public Map<String, BigDecimal> getCost(Bean roomTypeBean ,Long hotelId, Long roomTypeId, Long mId, List<Date> dateList) {
        // THotel hotel=HotelCacheManager.getInstance().getTHotel(hotelId);
        //TRoomType roomType = roomTypeDAO.findTRoomType(roomTypeId);
        //Bean roomTypeBean= roomTypeDAO.findRoomTypeBean(roomTypeId);
        // 价格规则 数据
        List<TPrice> priceList = priceDAO.findPrice(roomTypeId);// 房型 时间策略 价格
        logger.info("++++++获取价格日期策略priceDAO.findPrice({}),{}",roomTypeId,priceList.toString());
        TBasePrice basePrice = basePriceDAO.findBasePrice(roomTypeId);
        logger.info("++++++获取基础价格basePriceDAO.findBasePrice({}),basePrice:{}",roomTypeId,basePrice);

        // for (TPrice tPrice : priceList) {
        // Logger.getLogger(getClass()).warn("策略："+tPrice.getPriceTime().getCron()+"==="+tPrice.getPrice().intValue());
        // }

        Map<String, BigDecimal> costMap = new HashMap<String, BigDecimal>();

        // // 1.房型基本价格
        BigDecimal $cost = roomTypeBean.getBigDecimal("cost");
        logger.info("++++++ 基本价格：$cost{},roomtypeBean:{}",$cost,roomTypeBean);
        // // 2.base price价格
        if (basePrice != null) {
            if (basePrice.get("price") != null) {
                $cost = basePrice.getBigDecimal("price");
            } else if (basePrice.get("subper") != null) {// 上下浮动百分比
                //$cost = $cost.multiply(basePrice.getBigDecimal("subper").add(new BigDecimal(1)));
                //现在只处理下浮全为正数，
                $cost = $cost.multiply(new BigDecimal(1).subtract(basePrice.getBigDecimal("subper")));
            } else if (basePrice.get("subprice") != null) {// 上下浮动固定价格
                //现在只处理下浮全为正数，
                //$cost = $cost.add(basePrice.getBigDecimal("subprice"));
                $cost = $cost.subtract(basePrice.getBigDecimal("subprice"));
            }
        }
        logger.info("++++++ 获取basePrice后:$cost{},baseprice:{}",$cost,basePrice);
        // // 3.价格策略算出的价格
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        CronExpression cronExpre = null;
        //TPriceTime tPriceTime = null;
        String subCronExpreStr = null;
        String cronExpreStr = null;
        String addCronExpreStr = null;
        String[] subCronExpreArr = null;
        String[] cronExpreArr = null;
        String[] addCronExpreArr = null;
        TPrice tempPrice = null;
        Date pBegDate = null;
        Date pEndDate = null;
        boolean cronFlag = true;// true：包含此价格策略 false：去除此价格策略
        boolean breakfalg = false; // true :跳过下方
        StringBuffer msg = new StringBuffer();
        for (Date date : dateList) {
            tempPrice = null;
            cronFlag = true;
            for (TPrice tPrice : priceList) {
                breakfalg = false;
                String timeid = tPrice.get("timeid").toString();
                //tPriceTime = tPrice.getTpriceTime(timeid);
                pBegDate = tPrice.getDate("begintime");
                // if(pBegDate!=null&&!date.after(pBegDate)){
                // Logger.getLogger(getClass()).warn("跳过策略："+dateformat.format(date)+"=="+tPrice.getPriceTime().getCron());
                // continue;
                // }
                pEndDate = tPrice.getDate("endtime");
                // if(pEndDate!=null&&!date.before(pEndDate)){
                // Logger.getLogger(getClass()).warn("跳过策略2："+dateformat.format(date)+"=="+tPrice.getPriceTime().getCron());
                // continue;
                // }
                if (checkDate(date, pBegDate, pEndDate) == false) {
                    // Logger.getLogger(getClass()).warn("跳过策略2："+dateformat.format(date)+"=="+tPrice.getPriceTime().getCron());
                    // if(pBegDate!=null && pEndDate!=null){
                    // Logger.getLogger(getClass()).warn("跳过策略2："+dateformat2.format(date)+"=="+dateformat2.format(pBegDate)+"=="+dateformat2.format(pEndDate));
                    // }else if(pEndDate!=null){
                    // Logger.getLogger(getClass()).warn("跳过策略2："+dateformat2.format(date)+"==结束=="+dateformat2.format(pEndDate));
                    // }else if(pBegDate!=null){
                    // Logger.getLogger(getClass()).warn("跳过策略2："+dateformat2.format(date)+"==开始=="+dateformat2.format(pBegDate));
                    // }
                    continue;
                }
                addCronExpreStr = tPrice.get("addcron");
                subCronExpreStr = tPrice.get("subcron");
                cronExpreStr = tPrice.get("cron");
                try {
                    if (StringUtils.isNotBlank(subCronExpreStr)) {
                        subCronExpreArr = subCronExpreStr.split("\\|");
                        for (int i = 0; i < subCronExpreArr.length; i++) {
                            cronExpre = new CronExpression(subCronExpreArr[i]);
                            if (cronExpre.isSatisfiedBy(date)) {
                                // tempPrice = tPrice;
                                // cronFlag = false;
                                breakfalg = true;
                            }
                        }
                    }
                    if (breakfalg == false
                            && StringUtils.isNotBlank(cronExpreStr)) {
                        cronExpreArr = cronExpreStr.split("\\|");
                        for (int i = 0; i < cronExpreArr.length; i++) {
                            cronExpre = new CronExpression(cronExpreArr[i]);
                            if (cronExpre.isSatisfiedBy(date)) {
                                tempPrice = tPrice;
                                cronFlag = true;
                                breakfalg = true;
                                break;
                            } else {
                                // Logger.getLogger(getClass()).warn("跳过策略4："+dateformat.format(date)+"=="+tPrice.getPriceTime().getCron());
                            }
                        }
                    }
                    if (breakfalg == false
                            && StringUtils.isNotBlank(addCronExpreStr)) {
                        addCronExpreArr = addCronExpreStr.split("\\|");
                        for (int i = 0; i < addCronExpreArr.length; i++) {
                            cronExpre = new CronExpression(addCronExpreArr[i]);
                            if (cronExpre.isSatisfiedBy(date)) {
                                tempPrice = tPrice;
                                cronFlag = true;
                                break;
                            } else {
                                // Logger.getLogger(getClass()).warn("跳过策略3："+dateformat.format(date)+"=="+tPrice.getPriceTime().getCron());
                            }
                        }
                    }
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            //msg.append(MessageFormat.format("::是否包含策略价：cronFlag{}, $cost{}", new String[] { String.valueOf(cronFlag), String.valueOf($cost) }));
//          logger.info("++++++是否包含策略价：{}, {}, {}",cronFlag,$cost,tempPrice);
            BigDecimal cost = $cost;            
            if (cronFlag) {// 包含此价格策略
                if (tempPrice != null) {
                    // Logger.getLogger(getClass()).warn("时间："+dateformat.format(date)+"找到策略");
                    if (tempPrice.get("price") != null) {
                        cost = tempPrice.get("price");
                    } else if (tempPrice.get("subper") != null) {// 上下浮动百分比
                        //现在做下浮，不做上浮
                        cost = cost.multiply(new BigDecimal(1).subtract(tempPrice.getBigDecimal("subper")));
                    } else if (tempPrice.get("subprice") != null) {// 上下浮动固定价格
                        //现在做下浮，不做上浮
                        cost = cost.subtract(tempPrice.getBigDecimal("subprice"));
                    }
                } else {
                    // Logger.getLogger(getClass()).warn("时间："+dateformat.format(date)+"未找到策略");
                }
            }
            cost = cost.setScale(0, BigDecimal.ROUND_UP);
            msg.append("::算完策略后时间："+dateformat.format(date)+"房价："+cost.intValue());
            costMap.put(dateformat.format(date), cost);
        }
        logger.info(msg.toString());
        logger.info("++++++获取策略价:",costMap);
        // 如果价格都没有
        if (costMap == null || costMap.size() == 0) {
            logger.error("++++++获取价格失败", costMap);
            MyErrorEnum.saveOrderCost.getMyException("价格获取错误 ");
        }
        return costMap;
    }
    
    /**
     * 获取每日的价格
     * 切客走门市价、非切客走眯客价
     * t_roomtype: 门市价   --pms在房型上设置的价格
     * t_baseprice: 基本价   --hms管理后台设置的眯客价
     * t_price, t_pricetime: 策略价   -- hms酒店老板设置的价格
     * @param roomTypeBean
     * @param hotelId
     * @param roomTypeId
     * @param mId
     * @param dateList
     * @param isQieKe
     * @return
     */
    public Map<String, BigDecimal> getCost(Long orderId, Bean roomTypeBean ,Long hotelId, Long roomTypeId, Long mId, List<Date> dateList, boolean isQieKe) {
    	// 价格规则 数据【策略价】
    	List<TPrice> priceList = priceDAO.findPrice(roomTypeId);// 房型 时间策略 价格
    	logger.info("++++++获取价格日期策略orderid:{},roomTypeId:{},priceList:{}",orderId,roomTypeId,priceList.toString());
    	TBasePrice basePrice = basePriceDAO.findBasePrice(roomTypeId);
    	logger.info("++++++获取基础价格orderid:{},basePrice:{}",orderId,basePrice);
    	logger.info("++++++获取基础价格orderId:{},dateList:{}",orderId,dateList);
    	Map<String, BigDecimal> costMap = new HashMap<String, BigDecimal>();
    	// // 1.房型基本价格【门市价】
    	BigDecimal $cost = roomTypeBean.getBigDecimal("cost");
    	logger.info("++++++ 基本价格：orderid:{},$cost:{},roomtypeBean:{}",orderId,$cost,roomTypeBean);
    	// // 2.base price价格【眯客价】
    	if (basePrice != null && !isQieKe) {
    		if (basePrice.get("price") != null) {
    			$cost = basePrice.getBigDecimal("price");
    		} else if (basePrice.get("subper") != null) {// 上下浮动百分比
    			$cost = $cost.multiply(new BigDecimal(1).subtract(basePrice.getBigDecimal("subper")));
    		} else if (basePrice.get("subprice") != null) {// 上下浮动固定价格
    			$cost = $cost.subtract(basePrice.getBigDecimal("subprice"));
    		}
    	}
    	logger.info("++++++ 获取basePrice后:orderid:{},$cost:{},baseprice:{}",orderId,$cost,basePrice);
    	// // 3.价格策略算出的价格
    	SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
    	CronExpression cronExpre = null;
    	//TPriceTime tPriceTime = null;
    	String subCronExpreStr = null;
    	String cronExpreStr = null;
    	String addCronExpreStr = null;
    	String[] subCronExpreArr = null;
    	String[] cronExpreArr = null;
    	String[] addCronExpreArr = null;
    	TPrice tempPrice = null;
    	Date pBegDate = null;
    	Date pEndDate = null;
    	boolean cronFlag = true;// true：包含此价格策略 false：去除此价格策略
    	boolean breakfalg = false; // true :跳过下方
    	StringBuffer msg = new StringBuffer();
    	for (Date date : dateList) {
    		tempPrice = null;
    		cronFlag = true;
    		for (TPrice tPrice : priceList) {
    			breakfalg = false;
    			pBegDate = tPrice.getDate("begintime");
    			pEndDate = tPrice.getDate("endtime");
    			if (checkDate(date, pBegDate, pEndDate) == false) {
    				continue;
    			}
    			addCronExpreStr = tPrice.get("addcron");
    			subCronExpreStr = tPrice.get("subcron");
    			cronExpreStr = tPrice.get("cron");
    			try {
    				if (StringUtils.isNotBlank(subCronExpreStr)) {
    					subCronExpreArr = subCronExpreStr.split("\\|");
    					for (int i = 0; i < subCronExpreArr.length; i++) {
    						cronExpre = new CronExpression(subCronExpreArr[i]);
    						if (cronExpre.isSatisfiedBy(date)) {
    							breakfalg = true;
    						}
    					}
    				}
    				if (breakfalg == false && StringUtils.isNotBlank(cronExpreStr)) {
    					cronExpreArr = cronExpreStr.split("\\|");
    					for (int i = 0; i < cronExpreArr.length; i++) {
    						cronExpre = new CronExpression(cronExpreArr[i]);
    						if (cronExpre.isSatisfiedBy(date)) {
    							tempPrice = tPrice;
    							cronFlag = true;
    							breakfalg = true;
    							break;
    						}
    					}
    				}
    				if (breakfalg == false && StringUtils.isNotBlank(addCronExpreStr)) {
    					addCronExpreArr = addCronExpreStr.split("\\|");
    					for (int i = 0; i < addCronExpreArr.length; i++) {
    						cronExpre = new CronExpression(addCronExpreArr[i]);
    						if (cronExpre.isSatisfiedBy(date)) {
    							tempPrice = tPrice;
    							cronFlag = true;
    							break;
    						}
    					}
    				}
    			} catch (ParseException e) {
    				throw new RuntimeException(e);
    			}
    		}
    		BigDecimal cost = $cost;
    		logger.info("++++++计算单条价钱，orderid:{},date:{},cronFlag:{},isQieKe:{},平台价?{}",
    				orderId, dateformat.format(date), cronFlag, isQieKe, (cronFlag && !isQieKe && tempPrice != null));
    		if (cronFlag && !isQieKe) {// 包含此价格策略
    			if (tempPrice != null) {
    				if (tempPrice.get("price") != null) {
    					cost = tempPrice.get("price");
    				} else if (tempPrice.get("subper") != null) {// 上下浮动百分比
    					cost = cost.multiply(new BigDecimal(1).subtract(tempPrice.getBigDecimal("subper")));
    				} else if (tempPrice.get("subprice") != null) {// 上下浮动固定价格
    					cost = cost.subtract(tempPrice.getBigDecimal("subprice"));
    				}
    			}
    		}
    		cost = cost.setScale(0, BigDecimal.ROUND_UP);
    		msg.append("::算完策略后时间："+dateformat.format(date)+"房价："+cost.intValue());
    		costMap.put(dateformat.format(date), cost);
    	}
    	logger.info("++++++获取策略价:orderid:{},costMap:{}",orderId,costMap);
    	// 如果价格都没有
    	if (costMap == null || costMap.size() == 0) {
    		logger.error("++++++获取价格失败", costMap);
    		MyErrorEnum.saveOrderCost.getMyException("价格获取错误 ");
    	}
    	return costMap;
    }
    
    public boolean checkDate(Date check, Date begin, Date end) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(check);
        ca.set(Calendar.HOUR, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 1);
        ca.set(Calendar.MILLISECOND, 1);
        check = ca.getTime();
        ca.setTime(begin);
        ca.set(Calendar.HOUR, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        begin = ca.getTime();

        ca.setTime(end);
        ca.set(Calendar.HOUR, 23);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        ca.set(Calendar.MILLISECOND, 0);
        end = ca.getTime();
        if (begin != null && check.before(begin)) {
            return false;
        }
        if (end != null && check.after(end)) {
            return false;
        }
        return true;
    }
    

    public Bean findRoomStatus(String hotelid, String roomtypeid, String roomid, String date) {
        return roomDAO.findRoomStatus(hotelid,roomtypeid,roomid,date);
    }
    
    /**
     * 计算房价缓存表
     * @param hotelid
     */
    public void refreshHotelPrices(Long hotelid){
        logger.info("开始刷新酒店 Hotelid: {} 价格",hotelid);
        THotel hotel=hotelDAO.findHotelByHotelid(""+hotelid);
        if(hotel==null){
            logger.info("无此酒店hotelid: {}",hotelid);
            ////throw MyErrorEnum.errorParm.getMyException("无此酒店hotelid:"+hotelid);
            return;
        }
        List<Bean> roomTypeList= hotelDAO.findRoomTypeByHotelid(""+hotelid, "");
        for (Bean roomType : roomTypeList) {
            if(null != roomType){
                Long roomtypeid= roomType.getLong("roomtypeid");
                if(roomtypeid!=null){
                    logger.info("++++++开始调用 计算缓存价格 hotelid:{},roomtypeid:{}",hotelid,roomtypeid);
                    this.calCostPrice(hotelid, roomtypeid);                  
                }
            }
        }
        
        /*房型不存在或者已经被删除*/
        logger.info("++++++缓存价格结束，删除价格不存在的数据:");
        Bean city= cityDAO.getCityByDisId(""+hotel.getLong("disid"));
        String citycode= city.get("citycode").toString();
        List<Bean> allList = costTempDAO.findRoomTypeIdList(citycode,""+hotelid);
        for(Bean roomtype: roomTypeList){
            Long roomtypeid= roomtype.getLong("roomtypeid");
            for(Bean croomtype: allList){
                Long croomtypeid= croomtype.getLong("roomtypeid");
                if(roomtypeid.longValue()== croomtypeid.longValue()){
                    allList.remove(croomtype);
                    break;
                }
            }
        }
        logger.debug("costtemp中的roomtypeid",allList.toString());
        for (Bean temp : allList) {
            //删除该价格的所有缓存数据
            Long roomtypeid= temp.getLong("roomtypeid");
            costTempDAO.deleteDateBefore(citycode,""+hotelid,roomtypeid,null);
        }
    }
    
    
    /**
     * 
     * @return
     */
    public Map<String, Object> initAllRoomsPrice() {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        try {
            String sql = "select id, thotelid from  t_roomtype where 1=1 ";
            List<Bean> beans = Db.find(sql);
            for (Bean bean : beans) {
                String hotelid = bean.get("thotelid").toString();
                String roomtypeid = bean.get("id").toString();
                calCostPrice(Long.valueOf(hotelid), Long.valueOf(roomtypeid));
            }
            rtnMap.put("success", true);
            logger.info("初始化房价成功.");
        } catch (Exception e) {
            logger.error("初始化房价出错: {} ", e.getMessage());
            rtnMap.put("success", true);
            rtnMap.put("errcode", "-1");
            rtnMap.put("errmsg", "初始化房价出错: {} " + e.getMessage());
        }
        return rtnMap;
    }

    /**
     * 查询酒店房型价格
     * 
     * @param hotelId
     * @param Start yyyyMMdd
     * @param end	yyyyMMdd
     * @return
     */
    public Map<String, BigDecimal> findPriceByHotelIdStartEnd(Long hotelId, String start, String end){
    	List<Bean> pl = priceDAO.findCostTempPrice(hotelId, start, end);
    	if(pl==null || pl.size()==0){
    		this.refreshHotelPrices(hotelId);    		
    		pl = priceDAO.findCostTempPrice(hotelId, start, end);
    	}    	
    	Map<String, BigDecimal> priceMap = new HashMap<String, BigDecimal>();
    	for(Bean priceBean: pl) {
    		if(priceBean.get("cost") != null ){
    			BigDecimal price = priceBean.get("cost");
    			String roomtypeid = priceBean.get("roomtypeid").toString();
    			priceMap.put(roomtypeid, price);
    		}
    	}
    	return priceMap;
    }

    public int saveTRoom(TRoomModel tRoom){
    	return tRoomMapper.saveTRoom(tRoom);
    }

    
	public int delRoomByRoomTypeId(Long roomTypeId) {
		return tRoomMapper.delRoomByRoomTypeId(roomTypeId);
	}

	public int updateRoom(TRoomModel tRoom) {
		return tRoomMapper.updateRoom(tRoom);
	}

	public int delRoomById(Long id) {
		return tRoomMapper.delRoomById(id);
	}

	public List<TRoomModel> findRoomsByHotelId(Long hotelId) {		
		return tRoomMapper.findRoomsByHotelId(hotelId);
	}
	public void deleteAllRoomRepairs(Long hotelid){
    	roomRepairDAO.deleteAllRoomRepairs(hotelid);
    }
		
}
