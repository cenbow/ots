package com.mk.ots.wallet.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.wallet.dao.IUWalletCashFlowDAO;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by nolan on 15/9/9.
 */
@Component
public class UWalletCashFlowDAO extends MyBatisDaoImpl<UWalletCashFlow, Long> implements IUWalletCashFlowDAO {


    public UWalletCashFlow findConsumeCashflow(Long mid, Long orderid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mid", mid);
        param.put("orderid", orderid);
        return this.findOne("findConsumeCashflow", param);
    }

    @Override
    public UWalletCashFlow findByTypeAndSourceid(Long mid, CashflowTypeEnum cashflowType, Long sourceid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mid", mid);
        param.put("cashflowtype", cashflowType.getId());
        param.put("sourceid", sourceid);
        return this.findOne("findByTypeAndSourceid", param);
    }

    public List<UWalletCashFlow> findCashflowsByMid(Long mid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mid", mid);
        return this.find("findCashflowsByMid", param);
    }

}
