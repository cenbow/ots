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
        
    @Autowired 
    RoomDAO roomDAO=null;
    @Autowired
    private CostTempDAO costTempDAO = null;
    @Autowired
    private PmsRoomOrderDAO pmsRoomOrderDAO = null;
    @Autowired
    private RoomRepairDAO roomRepairDAO = null;
    @Autowired
    private PriceDAO priceDAO = null;
    @Autowired
    private BasePriceDAO basePriceDAO = null;
    @Autowired
    private TRoomMapper tRoomMapper;

    
    public TRoom findTRoomByRoomId(Long id){
        return TRoom.dao.findFirst("select * from t_room where id= ?",id);
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
