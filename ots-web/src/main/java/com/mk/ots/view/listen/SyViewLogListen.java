package com.mk.ots.view.listen;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.CardMethodEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.dao.impl.SyViewLogDaoImpl;
import com.mk.ots.view.model.SyViewLog;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jeashi on 2015/12/16.
 */
@Service

public class SyViewLogListen   {

    public void onMessage( String message){

         JSONArray jsAry = JSONArray.parseArray(message);
         if(null==jsAry){
                return ;
         }

        ISyViewLogDao syViewLogDao = AppUtils.getBean(SyViewLogDaoImpl.class);

        List<SyViewLog>  insertList = new ArrayList<SyViewLog>();
        int  batchInsertMaxSize = 20;
        int  insetTime = 0;

        for(int i=0;i<jsAry.size();i++){
              JSONObject  json = jsAry.getJSONObject(i);
              SyViewLog syViewLog = new  SyViewLog();
              syViewLog.setParams(json.toString());
              syViewLog.setCreateTime(new Date());

              syViewLog.setCallTime(new Date(json.getString("timeStamp")));
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

             insertList.add(syViewLog);

            if(insertList.size()>=batchInsertMaxSize){
                try{
                    syViewLogDao.insertBatch(insertList);
                    insertList.clear();
                }catch(Exception e){
                    insertList.clear();
                    e.printStackTrace();
                }finally{
                    continue;
                }
            }
        }
        if(insertList.size()>0){
            syViewLogDao.insertBatch(insertList);
            insertList.clear();
        }
    }

}
