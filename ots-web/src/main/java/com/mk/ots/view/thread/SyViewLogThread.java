package com.mk.ots.view.thread;

import com.mk.framework.MkJedisConnectionFactory;
import com.mk.ots.utils.SpringContextUtil;
import com.mk.ots.view.listen.SyViewLogListen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by jeashi on 2015/12/16.
 */

public class SyViewLogThread  extends    Thread{

    private static MkJedisConnectionFactory jedisFactory = SpringContextUtil.getApplicationContext().getBean(MkJedisConnectionFactory.class);

    public void run() {
         SyViewLogListen  listener = new SyViewLogListen();
        jedisFactory.getJedis().subscribe(listener, "SYVIEWwLOG");
    }
}
