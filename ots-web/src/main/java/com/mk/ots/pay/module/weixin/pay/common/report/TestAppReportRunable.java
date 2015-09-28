package com.mk.ots.pay.module.weixin.pay.common.report;

import com.mk.ots.pay.module.weixin.pay.common.WxType;
import com.mk.ots.pay.module.weixin.pay.common.report.service.ReportService;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * User: rizenguo
 * Date: 2014/12/3
 * Time: 16:34
 */
public class TestAppReportRunable implements Runnable {

    private ReportService reportService ;

    TestAppReportRunable(ReportService rs){
        reportService = rs;
    }

 
    public void run() {
        try {
            reportService.request(WxType.test_app);
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
