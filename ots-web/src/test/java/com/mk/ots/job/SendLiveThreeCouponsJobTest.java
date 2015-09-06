package com.mk.ots.job;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jjh on 15/7/31.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration(value = "src/main/webapp")
@ContextConfiguration(locations = { "classpath*:spring/applicationContext*.xml",
        "file:src/main/webapp/WEB-INF/spring/webmvc-config.xml" })
public class SendLiveThreeCouponsJobTest extends AbstractJUnit4SpringContextTests {

    private SendLiveThreeCouponsJob sendLiveThreeCouponsJob;

    @Before
    public void setUp() throws Exception {
        sendLiveThreeCouponsJob = new SendLiveThreeCouponsJob();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @Transactional
    @Rollback(true)
    public void executeInternalTest() {
        try {
            sendLiveThreeCouponsJob.executeInternal(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }

    }

}
