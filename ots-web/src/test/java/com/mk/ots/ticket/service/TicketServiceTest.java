package com.mk.ots.ticket.service;

import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.ticket.model.BPrizeInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by jjh on 15/7/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextConfiguration(locations = {"classpath*:spring/applicationContext*.xml",
            "file:src/main/webapp/WEB-INF/spring/webmvc-config.xml"})
public class TicketServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private ITicketService ticketService;
    @Autowired
    private IPromoService promoService;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    @Rollback(true)
    public void saveOrUpdateHotelStatTest() {
        /*OtaOrder otaOrder = new OtaOrder();
        otaOrder.setMid(1116L);
        otaOrder.setHotelId(15L);
        otaOrder.setId(555L);

        ticketService.saveOrUpdateHotelStat(otaOrder);*/
    }
    
    @Test
    @Transactional
    @Rollback(true)
    public void genTicketByActiveTest() {
        List<BPrizeInfo> bPrizeInfos = promoService.tryLuckByActive(16l, 41014l, "1");
        System.out.println(bPrizeInfos.toString());
    }

}
