package com.mk.ots.wallet.dao.impl;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.wallet.dao.IUWalletDAO;
import com.mk.ots.wallet.model.UWallet;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 钱包数据处理类
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Component
public class UWalletDAO extends MyBatisDaoImpl<UWallet, Long> implements IUWalletDAO {

    /**
     * @param mid 用户mid
     * @return UWallet
     */
    public UWallet findWalletByMid(Long mid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("mid", mid);
        return super.findOne("findWalletByMid", param);
    }
}
