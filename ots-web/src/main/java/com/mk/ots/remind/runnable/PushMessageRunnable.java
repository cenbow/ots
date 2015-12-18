package com.mk.ots.remind.runnable;

import com.mk.ots.remind.model.Remind;
import com.mk.ots.remind.model.RemindType;
import com.mk.ots.remind.service.RemindService;

/**
 * Created by admin on 2015/12/17.
 */
public class PushMessageRunnable implements Runnable {

    private RemindType type;
    private Remind remind;
    private RemindService service;
    public PushMessageRunnable(RemindType type, Remind remind , RemindService service) {
        this.type = type;
        this.remind = remind;
        this.service = service;
    }
    @Override
    public void run() {
        if (null != service || null != type || null != remind) {
            service.pushMessage(type,remind);
        }
    }
}
