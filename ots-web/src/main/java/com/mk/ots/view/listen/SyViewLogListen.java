package com.mk.ots.view.listen;

import com.alibaba.fastjson.JSONObject;
import com.mk.framework.AppUtils;
import com.mk.ots.utils.SpringContextUtil;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.dao.impl.SyViewLogDaoImpl;
import com.mk.ots.view.model.SyViewLog;
import com.mk.ots.view.service.ISyViewLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by jeashi on 2015/12/16.
 */
@Service
public class SyViewLogListen  extends JedisPubSub {

    public void onMessage(String channel, String message){
        SyViewLog syViewLog = JSONObject.parseObject(message,SyViewLog.class);

        ISyViewLogDao  syViewLogDaoImpl = AppUtils.getBean(SyViewLogDaoImpl.class);
        syViewLogDaoImpl.save(syViewLog);
    }

}
