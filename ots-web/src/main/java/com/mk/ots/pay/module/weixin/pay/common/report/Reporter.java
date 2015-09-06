package com.mk.ots.pay.module.weixin.pay.common.report;

import com.mk.ots.pay.module.weixin.pay.common.WxType;
import com.mk.ots.pay.module.weixin.pay.common.report.protocol.ReportReqData;
import com.mk.ots.pay.module.weixin.pay.common.report.service.ReportService;

/**
 * User: rizenguo
 * Date: 2014/12/3
 * Time: 11:42
 */
public class Reporter {

//    private ReportRunable r;
    private Thread t;
    private ReportService rs;

    /**
     * 请求统计上报API
     * @param reportReqData 这个数据对象里面包含了API要求提交的各种数据字段
     */
    public Reporter(ReportReqData reportReqData){
    	System.out.println("去银行了。。。。。。。。。。。。。。");
        rs = new ReportService(reportReqData);
    }

    public void run(WxType type){
    	if(type.getId().intValue()==WxType.app.getId().intValue()){
    		AppReportRunable  r=new AppReportRunable(rs);
    		t = new Thread(r);
    	    t.setDaemon(true);  //后台线程
    	    t.start();
    	}else if(type.getId().intValue()==WxType.wechat.getId().intValue()){
    		WxReportRunable  r=new WxReportRunable(rs);
    		t = new Thread(r);
    	    t.setDaemon(true);  //后台线程
    	    t.start();
    	}else if(type.getId().intValue()==WxType.test_wechat.getId().intValue()){
    		TestWxReportRunable  r=new TestWxReportRunable(rs);
    		t = new Thread(r);
    	    t.setDaemon(true);  //后台线程
    	    t.start();
    	}
    		
       
    }
}
