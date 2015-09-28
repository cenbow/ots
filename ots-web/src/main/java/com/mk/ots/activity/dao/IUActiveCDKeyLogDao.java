package com.mk.ots.activity.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.activity.model.UActiveCDKeyLog;

public interface IUActiveCDKeyLogDao extends BaseDao<UActiveCDKeyLog, Long>{

    /**
     * 用户兑换曝日志
     *
     * @param mid
     * @param activeid
     * @param channelid
     * @param promotionid
     * @param code
     * @param isgen
     * @param ispush
     * @param hardwarecode
     * @return
     */
    public boolean log(Long mid, Long activeid, Long channelid, Long promotionid, String code, boolean isgen, boolean ispush, String hardwarecode);

}
