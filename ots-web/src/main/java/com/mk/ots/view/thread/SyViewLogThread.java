package com.mk.ots.view.thread;

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
        System.out.println("aaaaaabbbbbbbbbbb32123");
        BeanFactory factory = new ClassPathXmlApplicationContext("applicationContext.xml");
        MkJedisConnectionFactory jedisFactory = factory.getBean(MkJedisConnectionFactory.class);
        System.out.println("aaaaaaaaaaaaaaaaaaaaa1");
        try{
//            Jedis jedis = cacheManager.getNewJedis();
            SyViewLogListen listener = new SyViewLogListen();
            System.out.println("aaaaaaaaaaaaaaaaaaaaa2");
//            jedis.subscribe(listener, "SYVIEWwLOG");
        }catch(Exception e){
            e.printStackTrace();
        }

//        jedisFactory.getJedis().subscribe(listener, "SYVIEWwLOG");
    }
}
