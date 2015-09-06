package com.mk.ots.rpc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.keyvalue.AbstractMapEntry;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mk.es.entities.OtsHotel;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.dao.CityDAO;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.rpc.IHotelService;
import com.mk.ots.web.ServiceOutput;

@Service
public class HmsHotelService implements IHotelService {
    
    private Logger logger = org.slf4j.LoggerFactory.getLogger(HmsHotelService.class);
    
    @Autowired
    private ElasticsearchProxy esProxy;
    
    @Autowired
    private CityDAO cityDAO;
    
    @Autowired
    private RoomService roomService;
    
    @Resource
    private HotelService hotelService;
    
    @Autowired
    private RoomstateService roomstateService;
    
    @Autowired
    protected SqlSessionFactory sqlSessionFactory;
    
    /**
     * 
     * @param hotelid
     * @return
     */
    public Map<String, Object> addHmsHotelById(String hotelid) {
        logger.info("--=================  HmsHotelService addHmsHotelById method begin ... =================--");
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        
        SqlSession session = null;
        StringBuffer bfSql = new StringBuffer();
        try {
            Date day = new Date();
            OtsHotel hotel = new OtsHotel();
            // H端添加酒店：从t_hotel表中取数据，不要从e_hotel表中取数据.
            session = sqlSessionFactory.openSession();
            // 查酒店信息
            THotelMapper thotelMapper = session.getMapper(THotelMapper.class);
            THotelModel thotelModel = thotelMapper.selectById(Long.valueOf(hotelid));
            if (thotelModel == null) {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "hotelid：" + hotelid + "酒店不存在.");
                return rtnMap;
            }
            hotel.setHotelid(hotelid);
            
            // 酒店商圈信息
            List<Map<String, Object>> businessZones = new ArrayList<Map<String, Object>>();
            bfSql.setLength(0);
            bfSql.append("select a.*,b.name from t_hotelbussinesszone a "
                    + " left outer join t_businesszone b on a.businesszoneid=b.id "
                    + " where hotelid=?");
            List<Bean> list = Db.find(bfSql.toString(), hotelid);
            for (Bean item : list) {
                businessZones.add(item.getColumns());
            }
            hotel.setBusinesszone(businessZones);
            
            hotel.setCratetime(day.getTime());
            hotel.setDetailaddr(thotelModel.getDetailaddr());
            hotel.setFlag(1);
            hotel.setGrade(BigDecimal.ZERO);
            
            String disid = thotelModel.getDisid().toString();
            Bean cityBean = cityDAO.getCityByDisId(disid);
            hotel.setHotelcity(String.valueOf(cityBean.get("citycode")));
            
            hotel.setHoteldis(disid);
            hotel.setHoteldisc(thotelModel.getIntroduction());
            hotel.setHotelname(thotelModel.getHotelname());
            // 处理酒店图片信息
            // hotel pics
            List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
            try {
                String hotelpic = thotelModel.getHotelpic();
                logger.info("得到酒店图片 Hotelid: {} pics is {} ", hotelpic);
                if (!StringUtils.isBlank(hotelpic)) {
                    pics = (new Gson()).fromJson(hotelpic, List.class);
                }
            } catch (Exception e) {
                logger.error("获取酒店图片信息出错: {} ", e.getMessage());
            }
            hotel.setHotelpic(pics);
            
            // 处理酒店设施信息
            // hotel facilities
            logger.info("prepare to get Hotelid: {} facilities...", hotelid);
            List<Map<String, Object>> facies = new ArrayList<Map<String, Object>>();
            try {
                bfSql.setLength(0);
                bfSql.append("select a.facid,b.facname from t_hotel_facility a "
                        + " left outer join t_facility b on a.facId=b.id "
                        + " where a.hotelid=? and b.visible='T' order by b.facsort");
                List<Bean> facilities = Db.find(bfSql.toString(), hotelid);
                for (Bean item : facilities) {
                    facies.add(item.getColumns());
                }
            } catch (Exception e) {
                logger.error("获取酒店设施信息出错: {} ", e.getMessage());
            }
            hotel.setHotelfacility(facies);
            
            // H端添加的酒店默认为签约酒店
            hotel.setIspms(1);
            
            // 获取酒店价格
            hotel.setMaxprice(BigDecimal.ZERO);
            hotel.setMinprice(BigDecimal.ZERO);
            // H端新建酒店审核，强制刷新酒店眯客价格缓存。
            logger.info("H端新建酒店, 强制刷新眯客价格缓存: 开始");
            try {
                roomstateService.updateHotelMikepricesCache(Long.valueOf(hotelid), null, true);
                String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
                String beginDate = strCurDay;
                String endDate = strCurDay;
                String[] prices = roomstateService.getHotelMikePrices(Long.valueOf(hotelid), beginDate, endDate);
                hotel.setMaxprice(new BigDecimal(prices[0]));
                hotel.setMinprice(new BigDecimal(prices[1]));
                logger.info("新建酒店 Hotelid: {} 刷新眯客价格缓存成功.", hotelid);
            } catch (Exception e) {
                logger.info("新建酒店 Hotelid: {} 刷新眯客价格缓存出错: {}", hotelid, e.getMessage());
                e.printStackTrace();
            }
            logger.info("H端新建酒店, 强制刷新眯客价格缓存: 结束");
            
            hotel.setModifytime(day.getTime());
            
            double lat = thotelModel.getLatitude().doubleValue();
            double lon = thotelModel.getLongitude().doubleValue();
            hotel.setPin(new GeoPoint(lat, lon));
            
            // 新增字段
            hotel.setVisible(thotelModel.getVisible());
            hotel.setOnline(thotelModel.getOnline());
            hotel.setNumroomtype1(1l);
            hotel.setNumroomtype2(1l);
            hotel.setNumroomtype3(1l);
            hotel.setRoomnum(thotelModel.getRoomnum());
            // modified by chuaiqing at 2015-08-17 15:40:45
            hotel.setIsnewpms(thotelModel.getIsnewpms());
            // 酒店权重
            hotel.setPriority(thotelModel.getPriority());
            //酒店规则值
            hotel.setHotelrulecode(thotelModel.getRulecode());    //20150806 add
            // added by chuaiqing at 2015-08-17 15:49:55
            hotel.setPmsopttime(thotelModel.getPmsopttime());
            
            //添加酒店总图片数 by LYN  at 2015-08-22 21:47
            int hotelpicnum = hotelService.readonlyGetPicCount(Long.valueOf(hotelid));
			hotel.setHotelpicnum(hotelpicnum);
			
            
            boolean sucess = this.save(hotel);
            if (sucess) {
                rtnMap.put("success", true);
            } else {
                rtnMap.put("success", false);
                rtnMap.put("errcode", "-1");
                rtnMap.put("errmsg", "HMS添加酒店失败。");
                logger.error("HmsHotelService save HMS hotel is fault.");
            }
        } catch (Exception e) {
            rtnMap.put("success", false);
            rtnMap.put("errcode", "-1");
            rtnMap.put("errmsg", e.getMessage());
            logger.info("--=================  HmsHotelService addHmsHotelById method is error: {}\n ... =================--", e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        logger.info("--=================  HmsHotelService addHmsHotelById method end ... =================--");
        return rtnMap;
    }

    /**
     * add THmsHotel
     */
    @Override
    public boolean save(OtsHotel hotel) {
        logger.info("--=================  HmsHotelService save method begin ... =================--");
        boolean result = false;
        try {
            String hotelid = hotel.getHotelid();
            SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
            if (searchHits != null) {
                logger.error("hotelid: "+ hotelid + " has exists.");
                for (int i = 0; i < searchHits.length; i++) {
                    // delete es hotel data first
                    SearchHit searchHit = searchHits[i];
                    esProxy.deleteDocument(searchHit.getId());
                    logger.info("hotelid: "+ hotelid + " has deleted.");
                }
            }
            
            // 更新酒店眯客价
//            hotelService.updateEsMikePrice(Long.valueOf(hotelid));
            BeanMap beanMap = new BeanMap(hotel);
            Map otsHotelMap = new HashMap();
            Set<AbstractMapEntry> es = beanMap.entrySet();
            for(AbstractMapEntry e:es){
                String key = (String) e.getKey();
                Object value = e.getValue();
                if("class".equalsIgnoreCase(key) || value == null){
                    continue;
                }
                otsHotelMap.put(e.getKey(), e.getValue());
            }
            String startdate = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
            for (int d = 0; d < HotelService.MIKEPRICE_DAYS; d++) {
                Date mikepriceDate = DateUtils.addDays(DateUtils.getDateFromString(startdate), d);
                String startdateday = DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
                String enddateday = startdateday;
                // 取眯客价
                String[] prices = roomstateService.getHotelMikePrices(Long.valueOf(hotelid), startdateday, enddateday);
                BigDecimal mikePriceValue = new BigDecimal(prices[0]);
                String mikePriceKey = HotelService.MIKE_PRICE_PROP + DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
                otsHotelMap.put(mikePriceKey, mikePriceValue);
                logger.info("更新眯客价属性: {}, 值: {}", mikePriceKey, mikePriceValue);
            }

            // save es hotel data
            esProxy.signleAddDocument(otsHotelMap);

            logger.info("hotelid: "+ hotelid + " has added.");
            result = true;
            logger.info("--=================  HmsHotelService save HMS hotel success. =================--");
        } catch (Exception e) {
            result = false;
            logger.error("HmsHotelService save hotel is error:\n" + e.getMessage());
        }
        logger.info("--=================  HmsHotelService save method end ... =================--");
        return result;
    }
    
    /**
     * 
     * @param hotelid
     * @return
     */
    public Map<String, Object> updateHotelById(String hotelid) {
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        
        SqlSession session = null;
        StringBuffer bfSql = new StringBuffer();
        try {
            Date day = new Date();
            OtsHotel hotel = new OtsHotel();
            // H端添加酒店：从t_hotel表中取数据，不要从e_hotel表中取数据.
            session = sqlSessionFactory.openSession();
            // 查酒店信息
            THotelMapper thotelMapper = session.getMapper(THotelMapper.class);
            THotelModel thotelModel = thotelMapper.selectById(Long.valueOf(hotelid));
            if (thotelModel == null) {
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "hotelid：" + hotelid + "酒店不存在.");
                return rtnMap;
            }
            hotel.setHotelid(hotelid);
            
            // 酒店商圈信息
            List<Map<String, Object>> businessZones = new ArrayList<Map<String, Object>>();
            bfSql.setLength(0);
            bfSql.append("select a.*,b.name from t_hotelbussinesszone a "
                    + " left outer join t_businesszone b on a.businesszoneid=b.id "
                    + " where hotelid=?");
            List<Bean> list = Db.find(bfSql.toString(), hotelid);
            for (Bean item : list) {
                businessZones.add(item.getColumns());
            }
            hotel.setBusinesszone(businessZones);
            
            hotel.setCratetime(day.getTime());
            hotel.setDetailaddr(thotelModel.getDetailaddr());
            hotel.setFlag(1);
            hotel.setGrade(BigDecimal.ZERO);
            
            String disid = thotelModel.getDisid().toString();
            Bean cityBean = cityDAO.getCityByDisId(disid);
            hotel.setHotelcity(String.valueOf(cityBean.get("citycode")));
            
            hotel.setHoteldis(disid);
            hotel.setHoteldisc(thotelModel.getIntroduction());
            hotel.setHotelname(thotelModel.getHotelname());
            // 处理酒店图片信息
            // hotel pics
            List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
            try {
                String hotelpic = thotelModel.getHotelpic();
                logger.info("得到酒店图片 Hotelid: {} pics is {} ", hotelpic);
                if (!StringUtils.isBlank(hotelpic)) {
                    pics = (new Gson()).fromJson(hotelpic, List.class);
                }
            } catch (Exception e) {
                logger.error("获取酒店图片信息出错: {} ", e.getMessage());
            }
            hotel.setHotelpic(pics);
            
            // 处理酒店设施信息
            // hotel facilities
            logger.info("prepare to get Hotelid: {} facilities...", hotelid);
            List<Map<String, Object>> facies = new ArrayList<Map<String, Object>>();
            try {
                bfSql.setLength(0);
                bfSql.append("select a.facid,b.facname from t_hotel_facility a "
                        + " left outer join t_facility b on a.facId=b.id "
                        + " where a.hotelid=? and b.visible='T' order by b.facsort");
                List<Bean> facilities = Db.find(bfSql.toString(), hotelid);
                for (Bean item : facilities) {
                    facies.add(item.getColumns());
                }
            } catch (Exception e) {
                logger.error("获取酒店设施信息出错: {} ", e.getMessage());
            }
            hotel.setHotelfacility(facies);
            
            // H端添加的酒店默认为签约酒店
            hotel.setIspms(1);
            
            // 获取酒店价格
            hotel.setMaxprice(BigDecimal.ZERO);
            hotel.setMinprice(BigDecimal.ZERO);
            // H端修改酒店审核，强制刷新酒店眯客价格缓存。
            logger.info("H端修改酒店, 强制刷新眯客价格缓存: 开始");
            try {
                roomstateService.updateHotelMikepricesCache(Long.valueOf(hotelid), null, true);
                String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
                String beginDate = strCurDay;
                String endDate = strCurDay;
                String[] prices = roomstateService.getHotelMikePrices(Long.valueOf(hotelid), beginDate, endDate);
                hotel.setMaxprice(new BigDecimal(prices[0]));
                hotel.setMinprice(new BigDecimal(prices[1]));
                logger.info("修改酒店 Hotelid: {} 刷新眯客价格缓存成功.", hotelid);
            } catch (Exception e) {
                logger.info("修改酒店 Hotelid: {} 刷新眯客价格缓存出错: {}", hotelid, e.getMessage());
                e.printStackTrace();
            }
            logger.info("H端修改酒店, 强制刷新眯客价格缓存: 结束");
            
            hotel.setModifytime(day.getTime());
            
            double lat = thotelModel.getLatitude().doubleValue();
            double lon = thotelModel.getLongitude().doubleValue();
            hotel.setPin(new GeoPoint(lat, lon));
            
            // 新增字段
            hotel.setVisible(thotelModel.getVisible());
            hotel.setOnline(thotelModel.getOnline());
            hotel.setNumroomtype1(1l);
            hotel.setNumroomtype2(1l);
            hotel.setNumroomtype3(1l);
            hotel.setRoomnum(thotelModel.getRoomnum());
            // modified by chuaiqing at 2015-08-17 15:51:55
            hotel.setIsnewpms(thotelModel.getIsnewpms());
            // 酒店权重
            hotel.setPriority(thotelModel.getPriority()==null? 3 : thotelModel.getPriority());
            //酒店规则值
            hotel.setHotelrulecode(thotelModel.getRulecode());    //20150806 add
            // added by chuaiqing at 2015-08-17 15:52:30
            hotel.setPmsopttime(thotelModel.getPmsopttime());
            
            //添加酒店总图片数 by LYN  at 2015-08-22 21:47
            int hotelpicnum= hotelService.readonlyGetPicCount(Long.valueOf(hotelid));
			hotel.setHotelpicnum(hotelpicnum);
            
            boolean success = update(hotel);
            if (success) {
                rtnMap.put("success", true);
            } else {
                rtnMap.put("success", false);
                rtnMap.put("errcode", "-1");
                rtnMap.put("errmsg", "HMS修改酒店失败。");
            }
        } catch (Exception e) {
            rtnMap.put("success", false);
            rtnMap.put("errcode", "-1");
            rtnMap.put("errmsg", e.getMessage());
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return rtnMap;
    }
    
    /**
     * @param hotel
     * update THmsHotel
     */
    @Override
    public boolean update(OtsHotel hotel) {
        boolean result = false;
        try {
            String hotelid = hotel.getHotelid();
            SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
            for (int i = 0; i < searchHits.length; i++) {
                SearchHit searchHit = searchHits[i];
                // delete es hotel data first
                esProxy.deleteDocument(searchHit.getId());
                logger.info("hotelid: "+ hotelid + " has deleted.");
            }
            
            // 更新酒店眯客价
            BeanMap beanMap = new BeanMap(hotel);
            Map otsHotelMap = new HashMap();
            Set<AbstractMapEntry> es = beanMap.entrySet();
            for(AbstractMapEntry e:es){
                String key = (String) e.getKey();
                Object value = e.getValue();
                if("class".equalsIgnoreCase(key) || value == null){
                    continue;
                }
                otsHotelMap.put(e.getKey(), e.getValue());
            }
            String startdate = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
            for (int d = 0; d < HotelService.MIKEPRICE_DAYS; d++) {
                Date mikepriceDate = DateUtils.addDays(DateUtils.getDateFromString(startdate), d);
                String startdateday = DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
                String enddateday = startdateday;
                // 取眯客价
                String[] prices = roomstateService.getHotelMikePrices(Long.valueOf(hotelid), startdateday, enddateday);
                BigDecimal mikePriceValue = new BigDecimal(prices[0]);
                String mikePriceKey = HotelService.MIKE_PRICE_PROP + DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
                otsHotelMap.put(mikePriceKey, mikePriceValue);
                logger.info("更新眯客价属性: {}, 值: {}", mikePriceKey, mikePriceValue);
            }
            // add es hotel data
            esProxy.signleAddDocument(otsHotelMap);
            logger.info("hotelid: "+ hotelid + " has added.");
            result = true;
        } catch (Exception e) {
            result = false;
            logger.error("HmsHotelService update method is error:\n" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 
     */
    @Override
    public boolean online(String hotelid) {
        Map<String, Object> rtnMap = this.updateHotelById(hotelid);
        return Boolean.valueOf(String.valueOf(rtnMap.get(ServiceOutput.STR_MSG_SUCCESS)));
    }
    
    /**
     * 
     */
    @Override
    public boolean offline(String hotelid) {
        logger.info("HMS hotelid: {} prepare to offline...", hotelid);
        try {
            SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
            for (int i = 0; i < searchHits.length; i++) {
                SearchHit searchHit = searchHits[i];
                // delete es hotel data
                esProxy.deleteDocument(searchHit.getId());
                logger.info("hotelid: "+ hotelid + " has deleted.");
            }
            logger.info("HMS hotelid: {} offline success.", hotelid);
            return true;
        } catch (Exception e) {
            logger.error("HMS hotelid: {} offline error: {} ", hotelid, e.getMessage());
        }
        return false;
    }
    
}
