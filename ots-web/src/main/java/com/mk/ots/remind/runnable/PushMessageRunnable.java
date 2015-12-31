package com.mk.ots.remind.runnable;

import com.mk.framework.AppUtils;
import com.mk.ots.remind.model.Remind;
import com.mk.ots.remind.model.RemindType;
import com.mk.ots.remind.service.RemindService;

/**
 * Created by admin on 2015/12/17.
 */
public class PushMessageRunnable implements Runnable {

    private RemindType type;
    private Remind remind;
    public PushMessageRunnable(RemindType type, Remind remind) {
        this.type = type;
        this.remind = remind;
    }
    @Override
    public void run() {

        System.out.println(Thread.currentThread().getName() + "正在执行。。。");
        RemindService service = AppUtils.getBean(RemindService.class);
        if (null != service || null != type || null != remind) {
            service.pushMessage(type,remind);
        }
    }
}
