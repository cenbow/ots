package com.mk.ots.view.service.impl;

import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.model.SyViewLog;
import com.mk.ots.view.service.ISyViewLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by jeashi on 2015/12/9.
 */
@Service
public class SyViewLogServiceImpl implements ISyViewLogService {
    final Logger logger = LoggerFactory.getLogger(SyViewLogServiceImpl.class);

    @Autowired
    private ISyViewLogDao syViewLogDao;

    public  Boolean saveSyViewLog(HashMap<String, Object> map){

        SyViewLog  syViewLog = new  SyViewLog();
        syViewLog.setToUrl(map.get("toUrl").toString());
        syViewLog.setActionType(map.get("actionType").toString());
        syViewLog.setFromUrl(null == map.get("fromUrl") ? null : map.get("fromUrl").toString());
        syViewLog.setParams(null == map.get("params") ? null : map.get("params").toString());
        if(null!=map.get("longitude")){
            String longitudeStr = map.get("longitude").toString();
            syViewLog.setLongitude(BigDecimal.valueOf(Double.parseDouble(longitudeStr)));
        }
        if(null!=map.get("latitude")){
            String latitudeStr = map.get("latitude").toString();
            syViewLog.setLatitude(BigDecimal.valueOf(Double.parseDouble(latitudeStr)));
        }
        syViewLog.setCityCode(null == map.get("cityCode") ? null : map.get("cityCode").toString());
        syViewLog.setIp(null == map.get("ip") ? null : map.get("ip").toString());
        syViewLog.setCallMethod(null == map.get("callMethod") ? null : map.get("callMethod").toString());
        syViewLog.setVersion(null == map.get("version") ? null : map.get("version").toString());
        syViewLog.setWifiMacaddr(null == map.get("wifiMacaddr") ? null : map.get("wifiMacaddr").toString());
        syViewLog.setBiMacaddr(null == map.get("biMacaddr") ? null : map.get("biMacaddr").toString());
        syViewLog.setSimsn(null == map.get("simsn") ? null : map.get("simsn").toString());
        if(null!=map.get("mid")){
            String mideStr = map.get("mid").toString();
            syViewLog.setMid(Long.parseLong(mideStr));
        }
        if(null!=map.get("bussinessType")){
            String bussinessTypeStr = map.get("bussinessType").toString();
            syViewLog.setBussinessType(Integer.parseInt(bussinessTypeStr));
        }
        syViewLog.setBussinessId(null == map.get("bussinessId") ? null : map.get("bussinessId").toString());
        syViewLog.setHardwarecode(null == map.get("hardwarecode") ? null : map.get("hardwarecode").toString());
        syViewLog.setImei(null == map.get("imei") ? null : map.get("imei").toString());
        syViewLog.setCreateTime(new Date());
        Boolean bl = false;
        try{
            syViewLogDao.save(syViewLog);
            bl = true;
        }catch (Exception e){
            logger.error("添加日志失败");
            bl = false;
        }finally {
            return  bl;
        }

    }


}
