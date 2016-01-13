package com.mk.ots.view.thread;

import com.mk.framework.AppUtils;
import com.mk.framework.MkJedisConnectionFactory;
import com.mk.ots.utils.SpringContextUtil;
import com.mk.ots.view.listen.SyViewLogListen;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.mk.ots.manager.OtsCacheManager;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * Created by jeashi on 2015/12/16.
 */


public class SyViewLogThread  extends    Thread{

    public void run() {
        MkJedisConnectionFactory jedisFactory = AppUtils.getBean(MkJedisConnectionFactory.class);
        Jedis jedis =  jedisFactory.getJedis();
        try{
            while(true){

                SyViewLogListen  sll =  new SyViewLogListen();
                String message =  jedis.lpop("SYVIEWWLOG");
                sll.onMessage(message);
                Thread.sleep(1000L);

            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            jedis.close();
        }
    }
}
