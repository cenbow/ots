package com.mk.ots.rpc.service;

import com.dianping.cat.Cat;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.framework.util.NetUtils;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.bean.Db;
import com.mk.orm.plugin.hessian.HessianHelper;
import com.mk.ots.annotation.HessianService;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.hotel.model.HotelOnlineInfo;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.mapper.HotelOnlineInfoMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.rpc.IPmsSoapService;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.order.control.PmsUtilController;
import net.sf.json.JSONObject;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@Service
@HessianService(value="/pmshotel", implmentInterface=IPmsSoapService.class)
public class PmsSoapServiceImpl implements IPmsSoapService {

    /**
     * 
     */
    private static final long serialVersionUID = 5682364048224843381L;
    
    final Logger logger = LoggerFactory.getLogger(PmsSoapServiceImpl.class);
    
    @Autowired
    private ElasticsearchProxy esProxy;
    
    @Autowired
    private HmsHotelService hmsHotelService;
    
    @Autowired
    private HotelOnlineInfoMapper hotelOnlineInfoMapper;
    @Autowired
    private THotelMapper tHotelMapper;
    
    /**
     * PMS SOAP 酒店上线
     */
    @Override
    public Map<String, Object> pmsHotelOnline(String hotelid) {
        logger.info("--======================= pmsHotelOnline method begin... =====================--");
        logger.info("--======================= parameter name is hotelid and value is : {} =====================--", hotelid);
        
        logger.info("PmsSoapServiceImpl::pmsHotelOnline: send message to bms begin...");
        String service = "/BMS_SENDMESSAGEOFHOTELSTAUPDATE.sendMesOfHotelStaUpdate.do";
        try {
            String address = HessianHelper.getUrl(HessianHelper.hessian_bms, service);
            Map<String, String> paramsMap = new HashMap<String, String>();
            paramsMap.put("hotelId", hotelid);
            paramsMap.put("onLine", Constant.STR_TRUE);
            JSONObject obj = NetUtils.dopost(address, paramsMap);
            logger.info("PmsSoapServiceImpl::pmsHotelOnline: send message to bms: {}", JsonKit.toJson(obj));
        } catch (Exception e) {
            logger.error("PmsSoapServiceImpl::pmsHotelOnline: send message to bms is error: {}", e.getMessage());
        }
        logger.info("PmsSoapServiceImpl::pmsHotelOnline: send message to bms end.");
        
        Map<String, Object> rtnMap = new HashMap<String, Object>();
        String sql = "update t_hotel set online = '" + Constant.STR_TRUE + "' where id = ?";
        try {
            logger.info("prepare to update sql is: {} ", sql);
            int updateCount = Db.update(sql, hotelid);
            if (updateCount > 0) {
                
                //// PMS酒店在线处理: 开始
                logger.info("PMS酒店在线处理, 更新ES酒店online属性 begin...");
                logger.info("search Hotel : {} from ES...", hotelid);
                SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
                for (int i = 0; i < searchHits.length; i++) {
                    String _id = searchHits[i].getId();
                    logger.info("_id: {}, Hotel: {} has fund and prepare update online property from ES store...", _id, hotelid);
                    esProxy.updateDocument(_id, "online", "T");
                    logger.info("-id: {}, Hotel: {} has updated from ES store. field: online, value: T.", _id, hotelid);
                    Cat.logEvent("PSMHotelOnline",hotelid,"SUCCESS","T");
                }
                logger.info("PMS酒店在线处理, 更新ES酒店online属性 end.");
                //// PMS酒店在线处理: 结束
                
                logger.info("PMS Hotel id: {} Online success.", hotelid);
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
            } else {
                logger.info("PMS Hotel id: {} not exists in OTS t_hotel.", hotelid);
                Cat.logEvent("PSMHotelOnline", hotelid, "Failed", "PMS Hotel id: " + hotelid + " not exists in OTS t_hotel.");
                rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "PMS Hotel id: " + hotelid + " not exists in OTS t_hotel.");
            }
        } catch (Exception e) {
            rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, "PMS Hotel id: " + hotelid + " Online error: " + e.getMessage());
            logger.error("PMS Hotel id: {0} Online error: {1} ", hotelid, e.getMessage());
            Cat.logError("PSMHotelOnlineException",e);
        }
        logger.info("--======================= pmsHotelOnline method end... =====================--");
        logger.info("pmsHotelOnline method return data: {} ", rtnMap);
        return rtnMap;
    }
    
    /**
     * PMS SOAP 酒店下线
     */
    @Override
    public Map<String, Object> pmsHotelOffline(final String hotelid) {


        logger.info("--======================= pmsHotelOffline method begin... =====================--");
        logger.info("--======================= parameter name is hotelid and value is : {} =====================--", hotelid);

        Map<String, Object> rtnMap = new HashMap<String, Object>();
        rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);

        //多线程并行处理提高速度
        //发短信
        PmsUtilController.pool.execute(new Runnable() {
            @Override
            public void run() {
                String service = "/BMS_SENDMESSAGEOFHOTELSTAUPDATE.sendMesOfHotelStaUpdate.do";
                logger.info("PmsSoapServiceImpl::pmsHotelOffline: send message to bms begin...");
                try {
                    String address = HessianHelper.getUrl(HessianHelper.hessian_bms, service);
                    Map<String, String> paramsMap = new HashMap<String, String>();
                    paramsMap.put("hotelId", hotelid);
                    paramsMap.put("onLine", Constant.STR_FALSE);
                    JSONObject obj = NetUtils.dopost(address, paramsMap);
                    logger.info("PmsSoapServiceImpl::pmsHotelOffline: send message to bms: {}", JsonKit.toJson(obj));
                } catch (Exception e) {
                    logger.error("PmsSoapServiceImpl::pmsHotelOffline: send message to bms is error: {}", e.getMessage());
                }
                logger.info("PmsSoapServiceImpl::pmsHotelOffline: send message to bms end.");
            }
        });

        //更新数据库及es
        PmsUtilController.pool.execute(new Runnable() {
            @Override
            public void run() {
                //String sql = "update t_hotel set online = '" + Constant.STR_FALSE + "' where id = ?";
                try {
                    //logger.info("prepare to update sql is: {} ", sql);
                    //int updateCount = Db.update(sql, hotelid);
                    THotelModel thotel = new THotelModel();
                    thotel.setId(Long.parseLong(hotelid));
                    thotel.setOnline(Constant.STR_FALSE);
                    int updateCount = tHotelMapper.updateTHotel(thotel);
                    if (updateCount > 0) {

                        //// PMS酒店离线处理: 开始
                        logger.info("PMS酒店离线处理, 更新ES酒店online属性 begin...");
                        logger.info("search Hotel : {} from ES...", hotelid);
                        SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
                        for (int i = 0; i < searchHits.length; i++) {
                            String _id = searchHits[i].getId();
                            logger.info("_id: {}, Hotel: {} has fund and prepare update online property from ES store...", _id, hotelid);
                            esProxy.updateDocument(_id, "online", "F");
                            logger.info("-id: {}, Hotel: {} has updated from ES store. field: online, value: F.", _id, hotelid);
                            Cat.logEvent("PSMHotelOffline",hotelid,"SUCCESS","T");
                        }
                        logger.info("PMS酒店离线处理, 更新ES酒店online属性 end.");
                        //// PMS酒店离线处理: 结束

                        logger.info("PMS Hotel id: {} Offline success.", hotelid);

                    } else {
                        logger.error("PMS Hotel id: {} not exists in OTS t_hotel.", hotelid);
                        Cat.logEvent("PSMHotelOffline", hotelid, "Failed", "PMS Hotel id: " + hotelid + " not exists in OTS t_hotel.");
                    }
                } catch (Exception e) {
                    Cat.logError("PSMHotelOfflineException", e);
                    logger.error("PMS Hotel id: {0} Offline error: {1} ", hotelid, e.getMessage());
                }
            }
        });

        //记录酒店离线日志
        PmsUtilController.pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    logger.info("酒店离线日志添加,begin...{}", hotelid);
                    HotelOnlineInfo record = new HotelOnlineInfo();
                    THotelModel thotelModel = tHotelMapper.selectById(Long.parseLong(hotelid));

                    record.setHotelid(Long.parseLong(hotelid));
                    record.setHotelname(thotelModel.getHotelname());
                    record.setOpt(1);
                    record.setCreatetime(Calendar.getInstance().getTime());
                    hotelOnlineInfoMapper.insertSelective(record);
                    logger.info("酒店离线日志添加,end...{}", hotelid);
                } catch (Exception e) {
                    logger.info("酒店离线日志添加,error...{}", hotelid);
                }
            }
        });
        logger.info("--======================= pmsHotelOffline method end... =====================--");
        logger.info("pmsHotelOffline method return data: {} ", rtnMap);
        return rtnMap;
    }

}






