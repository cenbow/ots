package com.mk.ots.wallet.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.wallet.model.TBackMoneyRule;
import com.mk.ots.wallet.model.UWalletCashFlow;

import java.util.List;

/**
 * Created by jeashi on 2015/12/10.
 */
public interface ITBackMoneyRuleDao extends BaseDao<TBackMoneyRule, Long> {
    public List<Bean> getBackMoneyByHotelCityCode(String cityCode,Integer  type,Integer bussinessType);
}
