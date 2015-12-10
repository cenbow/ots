package com.mk.ots.wallet.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.wallet.dao.ITBackMoneyRuleDao;
import com.mk.ots.wallet.dao.IUWalletCashFlowDAO;
import com.mk.ots.wallet.model.TBackMoneyRule;
import com.mk.ots.wallet.model.UWalletCashFlow;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by jeashi on 2015/12/10.
 */
@Component
public class TBackMoneyRuleDaoImpl extends MyBatisDaoImpl<TBackMoneyRule, Long> implements ITBackMoneyRuleDao {

    public List<Bean>  getBackMoneyByHotelCityCode(String cityCode,Integer  type,Integer  bussinessType){
        String sql = " select  *  from  t_backmoney_rule  where type = ? and hotel_city_code =?  and  status =1 and  bussiness_type = ?  order  by  id  desc  limit 1 " +
                " union all  select  *  from  t_backmoney_rule  where hotel_city_code =-1  and  status =1 and type = ?  and bussiness_type = ? order  by  id  desc  limit 1 ";
        List<Bean> bedList = Db.find(sql,type, cityCode,bussinessType,type,bussinessType);
        return  bedList;
    }

}
