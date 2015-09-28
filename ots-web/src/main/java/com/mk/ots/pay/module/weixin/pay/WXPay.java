package com.mk.ots.pay.module.weixin.pay;

import com.mk.ots.pay.module.weixin.pay.business.DownloadBillBusiness;
import com.mk.ots.pay.module.weixin.pay.business.RefundBusiness;
import com.mk.ots.pay.module.weixin.pay.business.RefundQueryBusiness;
import com.mk.ots.pay.module.weixin.pay.business.ScanPayBusiness;
import com.mk.ots.pay.module.weixin.pay.common.WxType;
import com.mk.ots.pay.module.weixin.pay.protocol.downloadbill_protocol.DownloadBillReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.pay_protocol.ScanPayReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_protocol.RefundReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_query_protocol.RefundQueryReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.reverse_protocol.ReverseReqData;
import com.mk.ots.pay.module.weixin.pay.service.*;

/**
 * SDK总入口
 */
public class WXPay {

    /**
     * 请求支付服务
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的数据
     * @throws Exception
     */
    public static String requestScanPayService(ScanPayReqData scanPayReqData,WxType type) throws Exception{
        return new ScanPayService(type).request(scanPayReqData,type);
    }

    /**
     * 请求支付查询服务
     * @param scanPayQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestScanPayQueryService(ScanPayQueryReqData scanPayQueryReqData,WxType type) throws Exception{
		return new ScanPayQueryService(type).request(scanPayQueryReqData,type);
	}

    /**
     * 请求退款服务
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public static String requestRefundService(RefundReqData refundReqData,WxType type) throws Exception{
        return new RefundService(type).request(refundReqData,type);
    }

    /**
     * 请求退款查询服务
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestRefundQueryService(RefundQueryReqData refundQueryReqData,WxType type) throws Exception{
		return new RefundQueryService(type).request(refundQueryReqData,type);
	}

    /**
     * 请求撤销服务
     * @param reverseReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
	public static String requestReverseService(ReverseReqData reverseReqData,WxType type) throws Exception{
		return new ReverseService(type).request(reverseReqData,type);
	}

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @return API返回的XML数据
     * @throws Exception
     */
    public static String requestDownloadBillService(DownloadBillReqData downloadBillReqData,WxType type) throws Exception{
        return new DownloadBillService(type).request(downloadBillReqData,type);
    }

    /**
     * 直接执行被扫支付业务逻辑（包含最佳实践流程）
     * @param scanPayReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public static void doScanPayBusiness(ScanPayReqData scanPayReqData, ScanPayBusiness.ResultListener resultListener,WxType type) throws Exception {
        new ScanPayBusiness(type).run(scanPayReqData, resultListener,type);
    }

    /**
     * 调用退款业务逻辑
     * @param refundReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 业务逻辑可能走到的结果分支，需要商户处理
     * @throws Exception
     */
    public static void doRefundBusiness(RefundReqData refundReqData, RefundBusiness.ResultListener resultListener,WxType type) throws Exception {
        new RefundBusiness(type).run(refundReqData,resultListener,type);
    }

    /**
     * 运行退款查询的业务逻辑
     * @param refundQueryReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @throws Exception
     */
    public static void doRefundQueryBusiness(RefundQueryReqData refundQueryReqData,RefundQueryBusiness.ResultListener resultListener,WxType type) throws Exception {
        new RefundQueryBusiness(type).run(refundQueryReqData,resultListener,type);
    }

    /**
     * 请求对账单下载服务
     * @param downloadBillReqData 这个数据对象里面包含了API要求提交的各种数据字段
     * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
     * @return API返回的XML数据
     * @throws Exception
     */
    public static void doDownloadBillBusiness(DownloadBillReqData downloadBillReqData,DownloadBillBusiness.ResultListener resultListener,WxType type) throws Exception {
        new DownloadBillBusiness(type).run(downloadBillReqData,resultListener,type);
    }


}
