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

//    @Autowired
//    private OtsCacheManager cacheManager;
//    private volatile OtsCacheManager cacheManager;
//    private static MkJedisConnectionFactory jedisFactory = SpringContextUtil.getBean(MkJedisConnectionFactory.class);

    public void run() {
        try{
            while(true){
                System.out.println("aaaaaaaaaaaaaaaaaaaaa222");
                MkJedisConnectionFactory jedisFactory = AppUtils.getBean(MkJedisConnectionFactory.class);
                SyViewLogListen listener = new SyViewLogListen();
                jedisFactory.getJedis().subscribe(listener, "SYVIEWwLOG");
                System.out.println("aaaaaaaaaaaaaaaaaaaaa2");
                Thread.sleep(300);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
