package com.mk.ots.card.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.card.model.BCard;

public interface IBCardDAO extends BaseDao<BCard, Long> {
    public BCard findActivatedByPwd(String pwd);
}
