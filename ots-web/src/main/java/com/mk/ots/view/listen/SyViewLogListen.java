package com.mk.ots.view.listen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.CardMethodEnum;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.dao.impl.SyViewLogDaoImpl;
import com.mk.ots.view.model.SyViewLog;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import java.util.Date;

/**
 * Created by jeashi on 2015/12/16.
 */
@Service
public class SyViewLogListen {

    public void onMessage(String message){
      JSONArray jsAry = JSONArray.parseArray(message);
      if(null==jsAry){
       return ;
      }
     for(int i=0;i<jsAry.size();i++){
      JSONObject  json = jsAry.getJSONObject(i);
      SyViewLog syViewLog = new  SyViewLog();
      syViewLog.setParams(message);
      syViewLog.setCreateTime(new Date());
      syViewLog.setCallTime(json.getTimestamp("timeStamp"));
      syViewLog.setActionType(json.getString("eventType"));
      String  os = json.getString("os");
      syViewLog.setCallMethod(CardMethodEnum.getByName(os).getId());
      syViewLog.setModel(json.getString("model"));
      syViewLog.setPackageName(json.getString("packageName"));
      syViewLog.setVersion(json.getString("version"));
      syViewLog.setDtName(json.getString("dataName"));
      syViewLog.setHardwarecode(json.getString("deviceId"));
      syViewLog.setChannel(json.getString("channel"));
      syViewLog.setCityName(json.getString("district"));
      syViewLog.setPhone(json.getString("phone"));

         ISyViewLogDao syViewLogDaoImpl = AppUtils.getBean(SyViewLogDaoImpl.class);
        syViewLogDaoImpl.save(syViewLog);
     }
    }

}
