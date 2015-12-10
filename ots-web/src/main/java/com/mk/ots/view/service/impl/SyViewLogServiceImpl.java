package com.mk.ots.view.service.impl;

import com.mk.ots.promo.dao.IBPromotionDao;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.model.SyViewLog;
import com.mk.ots.view.service.ISyViewLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

/**
 * Created by jeashi on 2015/12/9.
 */
public class SyViewLogServiceImpl implements ISyViewLogService {
    final Logger logger = LoggerFactory.getLogger(SyViewLogServiceImpl.class);

    @Autowired
    private ISyViewLogDao SyViewLogDao;

    public  Boolean saveSyViewLog(HashMap map){
//        map.get("toUrl").toString();
        SyViewLog  syViewLog = new  SyViewLog();
        SyViewLogDao.saveOrUpdate(syViewLog);
        return  true;
    }


}
