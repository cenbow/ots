package com.mk.ots.wallet.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.wallet.model.UWallet;

/**
 * 用户钱包
 * <p/>
 * Created by nolan on 15/9/9.
 */
public interface IUWalletDAO extends BaseDao<UWallet, Long> {
    /**
     * 查询钱包信息
     *
     * @param mid 用户mid
     * @return UWallet
     */
    UWallet findWalletByMid(Long mid);
}
