package com.mk.ots.wallet.dao;

import com.mk.ots.wallet.dao.impl.UWalletDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created with IntelliJ IDEA.
 * User: Thinkpad
 * Date: 15-9-14
 * Time: 下午9:15
 * To change this template use File | Settings | File Templates.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-db.xml"})
public class WalletDaoTest {

    private IUWalletDAO iuWalletDAOMock=new UWalletDAO();

    @Test
    public void findWalletByMid() {

        iuWalletDAOMock.findWalletByMid(Long.parseLong("1"));
    }
}
