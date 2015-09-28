package com.mk.ots.wallet.service;

import com.mk.framework.model.Page;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;

import java.math.BigDecimal;

/**
 * 钱包明细
 * <p/>
 * Created by nolan on 15/9/9.
 */
public interface IWalletCashflowService {

    /**
     * 查询用户钱包明细
     *
     * @param mid 用户id
     * @return 明细列表
     */
    Page<UWalletCashFlow> findPage(Long mid, Long sourceid, int pageindex, int datesize);

    /**
     * 返现入帐
     *
     * @param mid          用户mid
     * @param cashflowtype 业务类别
     * @param sourceid     业务记录id
     * @return T/F
     */
    BigDecimal entry(Long mid, CashflowTypeEnum cashflowtype, Long sourceid);

    /**
     * 回退订单金额
     *
     * @param orderid 订单id
     * @return T/F
     */
    boolean refund(Long mid, Long orderid);

    /**
     * 订单消费
     *
     * @param mid     用户mid
     * @param orderid 订单id
     * @param price   消费金额
     * @return T/F
     */
    boolean pay(Long mid, Long orderid, BigDecimal price);

    /**
     * 订单确认
     *
     * @param orderid 订单id
     * @return
     */
    boolean confirmCashFlow(Long mid, Long orderid);

    /**
     * 解冻订单冻结金额
     *
     * @param mid     用户mid
     * @param orderid 订单id
     * @return
     */
    boolean unLockCashFlow(Long mid, Long orderid);
}
