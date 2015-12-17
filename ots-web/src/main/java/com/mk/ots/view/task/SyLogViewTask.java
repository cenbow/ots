package com.mk.ots.view.task;

import com.mk.ots.view.thread.SyViewLogThread;
import org.springframework.beans.factory.InitializingBean;

/**
 * Created by jeashi on 2015/12/17.
 */
public class SyLogViewTask implements InitializingBean {

    public void afterPropertiesSet()
    {
        System.out.println("开始执行|||||||||||||||||||");
        try{
            SyViewLogThread  slt  = new SyViewLogThread();
            slt.start();

        }catch(Exception e1){
            System.out.println("执行进入异常|||||||||||||||||||");
            e1.printStackTrace();
        }finally{
            System.out.println("执行结束|||||||||||||||||||");

        }
    }

}
