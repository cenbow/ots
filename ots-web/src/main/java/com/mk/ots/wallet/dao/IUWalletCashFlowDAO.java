package com.mk.ots.wallet.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;

import java.util.List;

/**
 * 用户钱包交易明细
 * <p/>
 * Created by nolan on 15/9/9.
 */
public interface IUWalletCashFlowDAO extends BaseDao<UWalletCashFlow, Long> {
    /**
     * 查询用户此订单最后一条消费明细
     *
     * @param mid     用户id
     * @param orderid 订单id
     * @return UWalletCashFlow
     */
    UWalletCashFlow findConsumeCashflow(Long mid, Long orderid);

    /**
     * 查询消费明细
     *
     * @param cashflowType
     * @param sourceid
     * @return
     */
    UWalletCashFlow findByTypeAndSourceid(Long mid, CashflowTypeEnum cashflowType, Long sourceid);

    /**
     * 查询用户消费明细
     * @param mid
     * @return
     */
    List<UWalletCashFlow> findCashflowsByMid(Long mid);
}
