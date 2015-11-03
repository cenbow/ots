package com.mk.ots.wallet.service.impl;

import com.mk.ots.wallet.dao.IUWalletDAO;
import com.mk.ots.wallet.model.UWallet;
import com.mk.ots.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包信息服务类
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Service
public class WalletService implements IWalletService {

    @Autowired
    private IUWalletDAO iuWalletDAO;

    public void setIuWalletDAO(IUWalletDAO iuWalletDAO) {
        this.iuWalletDAO = iuWalletDAO;
    }

    @Override
    public BigDecimal queryBalance(Long mid) {
        UWallet uWallet = iuWalletDAO.findWalletByMid(mid);
        if (uWallet == null) {
            uWallet = new UWallet();
            uWallet.setMid(mid);
            uWallet.setBalance(BigDecimal.ZERO);
            uWallet.setLastmodify(new Date());
            iuWalletDAO.insert(uWallet);
        }

        //金额取整
        uWallet.setBalance(uWallet.getBalance().setScale(0,uWallet.getBalance().ROUND_HALF_UP));
        return uWallet != null && uWallet.getBalance().compareTo(BigDecimal.ZERO) > 0 ? uWallet.getBalance() : BigDecimal.ZERO;
    }

    @Override
    public boolean updateBalance(Long mid, BigDecimal total) {
        UWallet uWallet = iuWalletDAO.findWalletByMid(mid);
        if (uWallet == null) {
            uWallet = new UWallet();
            uWallet.setMid(mid);
            uWallet.setBalance(total);
            uWallet.setLastmodify(new Date());
            iuWalletDAO.insert(uWallet);
            return uWallet.getId() != null;
        } else {
            uWallet.setBalance(total);
            uWallet.setLastmodify(new Date());
            return iuWalletDAO.update(uWallet) > 0;
        }
    }
}
