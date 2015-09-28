package com.mk.ots.wallet;

import com.mk.ots.wallet.dao.IUWalletDAO;
import com.mk.ots.wallet.model.UWallet;
import com.mk.ots.wallet.service.impl.WalletService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: Thinkpad
 * Date: 15-9-11
 * Time: 下午1:54
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WalletServiceTest {
    private IUWalletDAO iuWalletDAOMock;
    private WalletService walletServiceStub;

    @Before
    public void runBeforeTest() {
        iuWalletDAOMock = mock(IUWalletDAO.class);

        walletServiceStub = new WalletService();
        walletServiceStub.setIuWalletDAO(iuWalletDAOMock);

    }

    @Test
    public void queryBalanceWalletExist() {
        UWallet uWallet=new UWallet();
        uWallet.setBalance(BigDecimal.TEN);
        when(iuWalletDAOMock.findWalletByMid(anyLong())).thenReturn(uWallet);

        BigDecimal actual = walletServiceStub.queryBalance(Long.parseLong("1"));

        Assert.assertEquals(BigDecimal.TEN, actual);
        verify(iuWalletDAOMock.findWalletByMid(anyLong()));
        verify(iuWalletDAOMock.insert(any(UWallet.class)));
    }

    @Test
    public void queryBalanceWalletNotExist() {

        when(iuWalletDAOMock.findWalletByMid(anyLong())).thenReturn(null);

        BigDecimal actual = walletServiceStub.queryBalance(Long.parseLong("1"));

        Assert.assertEquals(BigDecimal.ZERO, actual);

        verify(iuWalletDAOMock.findWalletByMid(anyLong()));
        verify(iuWalletDAOMock.insert(any(UWallet.class)));

    }
}
