package com.mk.ots.activity.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.activity.dao.IUActiveCDKeyLogDao;
import com.mk.ots.activity.model.UActiveCDKeyLog;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author nolan
 */
@Component
public class UActiveCDKeyLogDaoImpl extends MyBatisDaoImpl<UActiveCDKeyLog, Long> implements IUActiveCDKeyLogDao {

    @Override
    public boolean log(Long mid, Long activeid, Long channelid, Long promotionid, String code, boolean isgen, boolean ispush, String hardwarecode) {
        UActiveCDKeyLog log = new UActiveCDKeyLog();
        log.setActiveid(activeid);
        log.setMid(mid);
        log.setChannelid(channelid);
        log.setPromotionid(promotionid);
        log.setCode(code);
        log.setSuccess(isgen);
        log.setPush(ispush);
        log.setCreatetime(new Date());
        log.setHardwarecode(hardwarecode);
        this.insert(log);
        return log.getId() != null;
    }

}
