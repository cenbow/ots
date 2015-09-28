package com.mk.ots.wallet.service;

import java.math.BigDecimal;

/**
 * 钱包
 * <p/>
 * Created by nolan on 15/9/9.
 */
public interface IWalletService {

    /**
     * 余额查询
     *
     * @param mid 用户mid
     * @return T/F
     */
    BigDecimal queryBalance(Long mid);

    /**
     * 更新余额
     *
     * @param mid   用户mid
     * @param total 余额
     * @return T/F
     */
    boolean updateBalance(Long mid, BigDecimal total);

}
