package com.mk.ots.pay.module.weixin.pay;

import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.pay.module.query.BankPayStatusEnum;
import com.mk.ots.pay.module.query.QueryPayPram;
import com.mk.ots.pay.module.weixin.pay.WXPay;
import com.mk.ots.pay.module.weixin.pay.common.Configure;
import com.mk.ots.pay.module.weixin.pay.common.WxType;
import com.mk.ots.pay.module.weixin.pay.common.XmlUtil;
import com.mk.ots.pay.module.weixin.pay.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.pay_query_protocol.ScanPayQueryResData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_protocol.RefundReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_protocol.RefundResData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_query_protocol.RefundQueryReqData;
import com.mk.ots.pay.module.weixin.pay.protocol.refund_query_protocol.RefundQueryResData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
/**
 * futao.xiao
 * 2015年5月12日 15:38:05
 * 微信退款，查询订单总入口
 * 优化过的接口
 */
public class Weixin {
 	private static Logger logger = LoggerFactory.getLogger(Weixin.class);
	
    /**
     * 退款总入口
     * @param weixinpayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是分】支付的金额， 会全部退回
     * @return  微信退款ID
     */
    public static String refund(String weixinpayid,String orderid,int price ,WxType type){
    	logger.info("订单号：" + orderid + "进入微信退款,微信支付Id:"+weixinpayid+",金额:"+price+",微信支付环境:"+type);
    	String refundid=null;
    	//退款参数
        RefundReqData rfrd=new RefundReqData(weixinpayid ,orderid,price,type);
		try {
			String s = WXPay.requestRefundService(rfrd,type);
			logger.info("订单号：" + orderid + "微信公共帐号返回的信息是："+s);
		    //这个跟上面的方法一样，都可以解析xml 到对象
		    RefundResData rf=XmlUtil.getRefundResData(s);
		    if(rf.getReturn_code().equals("SUCCESS")){
		    	logger.info("订单号：" + orderid + "Return_code返回 SUCCESS");
		    	if(rf.getResult_code().equals("SUCCESS")){
		    		logger.info("订单号：" + orderid + "Result_code返回 SUCCESS");
		    		//退款成功，返回退款单号
		    		refundid=rf.getRefund_id();
		    	}else{ //抛出错误信息
		    		throw MyErrorEnum.errorParm.getMyException("订单号：" + orderid + "微信退款错误代码："+rf.getErr_code()+",错误信息："+rf.getErr_code_des());
		    	}
		    }
		} catch (Exception e) {
			
			logger.error("订单号：" + orderid + "调用微信退款异常!",e);
		}
		return refundid;
    }
    
 
    
    /**
     * 查询订单是否是  【支付中】
     * @param orderid  weixin的orderId
     * @return  boolean
     */
    public static  boolean  queryIsPayING(String orderid,WxType type){
    	logger.info("微信查询订单是否是支付中：{} WxType=={}",orderid,type);
    	boolean b=false;
    	//微信把本地orderId处理为
    	ScanPayQueryResData spq=queryGetScanPayQueryResData(null,orderid,type);
    	if(spq!=null){
    		String trade_state=spq.getTrade_state();
    		logger.info("微信查询  orderid：{}, trade_state==：{} ",orderid,trade_state);
    		if(trade_state!=null && trade_state.equals("USERPAYING")){
    			b=true;
    		}
    	}
		return b;
    }
    
    
    /**
     * 验证返回信息【appid,mch_id】
     * @param HttpServletRequest request
     * @return  boolean
     */
    public static  boolean  verify(HttpServletRequest request,WxType type){
    	boolean b=false;
		String appid=request.getParameter("appid");
		String mch_id=request.getParameter("mch_id");
		if(appid.equals(Configure.getAppid(type)) && mch_id.equals(Configure.getMchid(type))){
			b=true;
		}
		return b;
    }
    
    
    /**
     * 查询订单总入口
     * @param orderid  本地ID，两个ID至少一个
     * @param payid    微信的ID
     * @return  微信退款ID
     */
    public static  String  queryByBankId(String bankId,WxType type){
    	logger.info("查询支付状况 bankId={},WxType=={}",bankId,type);
    	String weixinId=null;
    	ScanPayQueryResData spq=queryGetScanPayQueryResData(bankId,null,type);
    	if(spq==null){
    		return null;
    	}else{
    		String trade_state=spq.getTrade_state();
    		logger.info("查询支付状况 bankId={},WxType=={}  trade_state==={}  支付金额是{}",bankId,type,trade_state,spq.getTotal_fee());
    		if(trade_state!=null && trade_state.equals("SUCCESS")){
    			logger.info("查询支付状况 bankId={},WxType=={}  trade_state==={}  支付金额是{}",bankId,type,trade_state,spq.getTotal_fee());
	    		weixinId=spq.getTransaction_id();
    			logger.info("查询支付状况 结束，支付成功 bankId==={}",bankId );
    		}
    	}
    	logger.info("查询支付状况 结束，方法执行完bankId={},weixinId={}",bankId ,weixinId);
		return weixinId;
    }
    
    
 
    /**
     * 查询订单总入口
     * @param orderid  本地ID，两个ID至少一个
     * @param payid    微信的ID
     * @return  微信退款ID
     */
    public static  String  queryById(String orderid,String payid,WxType type){
    	logger.info("查询支付状况 orderid={},WxType=={}",orderid,type);
    	String weixinId=null;
    	ScanPayQueryResData spq=queryGetScanPayQueryResData(null,orderid,type);
    	if(spq==null){
    		return null;
    	}else{
    		String trade_state=spq.getTrade_state();
    		logger.info("查询支付状况 orderid={},WxType=={}  trade_state==={}  支付金额是{}",orderid,type,trade_state,spq.getTotal_fee());
    		if(trade_state!=null && trade_state.equals("SUCCESS")){
    			logger.info("查询支付状况 orderid={},WxType=={}  trade_state==={}  支付金额是{}",orderid,type,trade_state,spq.getTotal_fee());
	    		weixinId=spq.getTransaction_id();
    			logger.info("查询支付状况 结束，支付成功 orderid==={}",orderid );
    		}
    	}
    	logger.info("查询支付状况 结束，方法执行完orderid={},weixinId={}",orderid ,weixinId);
		return weixinId;
    }
    
    public static String queryByPrice(String orderid,String payid,int price,WxType type){
    	logger.info("查询支付状况 orderid={},WxType=={}",orderid,type);
    	String weixinId=null;
    	ScanPayQueryResData spq=queryGetScanPayQueryResData(payid,orderid,type);
    	if(spq==null){
    		return null;
    	}else{
    		String trade_state=spq.getTrade_state();
    		logger.info("查询支付状况 orderid={},WxType=={}  trade_state==={}  支付金额是{}",orderid,type,trade_state,spq.getTotal_fee());
    		if(trade_state!=null && trade_state.equals("SUCCESS")){
    			logger.info("查询支付状况 orderid={},WxType=={}  trade_state==={}  支付金额是{}",orderid,type,trade_state,spq.getTotal_fee());
    			int p=Integer.valueOf(spq.getTotal_fee()).intValue();
	    		if(p>=price){
	    			weixinId=spq.getTransaction_id();
    			    logger.info("查询支付状况 结束，支付成功 orderid==={}",orderid );
    		    }
    		}
    	}
    	logger.info("查询支付状况 结束，方法执行完",orderid );
		return weixinId;
    }
    
    
    /**
     * 查询订单总入口
     * @param orderid  本地ID，会原样返回
     */
    private static  ScanPayQueryResData  queryGetScanPayQueryResData(String payid,String orderid,WxType type){
     	logger.info("查询支付queryGetScanPayQueryResData payid:{}, orderid:{} ,WxType:{}", payid, orderid,type);
    	ScanPayQueryResData  res=null;
    	ScanPayQueryReqData spr=null;
    	if((payid==null || payid.trim().length()==0) && (orderid==null || orderid.trim().length()==0)){
    		return null;
    	}
    	//参数
    	if(payid==null || payid.trim().length()==0){
    		spr=new ScanPayQueryReqData("",orderid,type);
    	}else if(orderid==null || orderid.trim().length()==0){
    		spr=new ScanPayQueryReqData(payid,"",type);
    	}else{
    		spr=new ScanPayQueryReqData(payid,orderid,type);
    	}
		try {
		    String s= WXPay.requestScanPayQueryService(spr,type);
		    logger.info("查询支付情况  orderid={} 请求返回信息：{}",orderid,s);
		    res=XmlUtil.getQueryResData(s);
			logger.info("查询支付情况  orderid={} 请求返回信息【xml】：{}",orderid,res);
		} catch (Exception e) {
			logger.error("queryGetScanPayQueryResData 发生错误.", e);
			e.printStackTrace();
		}
		return res;
    }
     
    
    public static boolean refundQuery(String payid,WxType type){
    	logger.info("退款查询况 weixinpayid={},WxType=={}",payid,type);
    	boolean b=false;
    	RefundQueryResData res=refundQueryRefundQueryReqData(payid,type);
    	if(res==null){
    		return false;
    	}else{
    		logger.info("退款查询结果 RefundQueryResData==={}",res.toString());
    		if(res.getReturn_code()!=null && res.getReturn_code().equals("SUCCESS")){
    			if(res.getResult_code()!=null && res.getResult_code().equals("SUCCESS")){
    				if(res.getReturn_msg()!=null && res.getReturn_msg().equals("OK")){
            			b=true;
            		}
        		}
    		}
    	}
    	logger.info("查询支付状况 结束，方法执行完 weixinpayid=={}", payid);
		return b;
    }
    
    
    
    /**
     * 退款查询订单总入口
     * @param weixinpayid  微信支付的ID
     */
    private static  RefundQueryResData  refundQueryRefundQueryReqData(String weixinpayid,WxType type){
     	logger.info("退款查询 refundQueryRefundQueryReqData weixinpayid:{} ,WxType:{}", weixinpayid,type);
     	RefundQueryResData  res=null;
    	//参数
     	RefundQueryReqData  req=new RefundQueryReqData(weixinpayid,type);
		try {
		    String s= WXPay.requestRefundQueryService(req,type);
		    logger.info("退款查询情况  weixinpayid={} 请求返回信息：{}",weixinpayid,s);
		    res=XmlUtil.getRefundQueryResData(s);
			logger.info("退款查询情况  weixinpayid={} 请求返回信息【xml】：{}",weixinpayid,res);
		} catch (Exception e) {
			logger.error("getRefundQueryResData 发生错误.", e);
		}
		return res;
    }
    
    
    
    
    /**仅仅作为查看支付情况的时候用*/
    public static QueryPayPram  onlyQuery(String orderid,WxType type,QueryPayPram pram){
    	logger.info("订单号："+orderid+"支付金额查询");
    	ScanPayQueryResData  pqr=queryGetScanPayQueryResData("", orderid+"", type);
    	if(pqr==null){
    		return null;
    	}else{
    		logger.info("退款查询结果 ScanPayQueryResData==={}",pqr.toString());
    		if(pqr.getReturn_code()!=null && pqr.getReturn_code().equals("SUCCESS")){
    			if(pqr.getResult_code()!=null && pqr.getResult_code().equals("SUCCESS")){
    				if(pqr.getReturn_msg()!=null && pqr.getReturn_msg().equals("OK")){
//    					System.out.println(pqr.toString());
    					
    					if(pqr.getTrade_state()==null ){

    					}else if( pqr.getTrade_state().equals("SUCCESS")){

    						        pram.setSuccess(true);
    						        pram.setPrice(f2Y(pqr.getTotal_fee()));
    						        if(type==WxType.app){
    						        	 pram.setBanktype(PPayInfoOtherTypeEnum.wxpay);
    						        }else if(type==WxType.wechat){
    						        	 pram.setBanktype(PPayInfoOtherTypeEnum.wechatpay);
    						        }else{
    						        	logger.info("订单："+pram.getOrderid()+"是测试公众帐号");
    						        	pram.setBanktype(PPayInfoOtherTypeEnum.wechatpay);
    						        }
    						        pram.setPaystatus(BankPayStatusEnum.success);
    						        pram.setBankno(pqr.getTransaction_id());
    						        
//	    						   if(isService){
//	    							   rs="去{"+type.getName()+"}查询到结果，状态是【已支付】，金额:"+f2Y(pqr.getTotal_fee())+"元，能作为支付凭证。";
//									}else{
//										rs="【已支付】，金额:"+f2Y(pqr.getTotal_fee())+"元";
//									}
    							}else if( pqr.getTrade_state().equals("REFUND")){
    								pram.setSuccess(true);
    						        pram.setPrice(f2Y(pqr.getTotal_fee()));
    						        if(type==WxType.app){
	   						        	 pram.setBanktype(PPayInfoOtherTypeEnum.wxpay);
	   						        }else if(type==WxType.wechat){
	   						        	 pram.setBanktype(PPayInfoOtherTypeEnum.wechatpay);
	   						        }else{
	   						        	logger.info("订单："+pram.getOrderid()+"是测试公众帐号");
	   						        	pram.setBanktype(PPayInfoOtherTypeEnum.wechatpay);
	   						        }
    						        pram.setPaystatus(BankPayStatusEnum.refund);
    						        pram.setBankno(pqr.getTransaction_id());
//    								if(isService){
//    									rs="去{"+type.getName()+"}查询到结果，状态是【已退款】，金额:"+f2Y(pqr.getTotal_fee())+"【元】，能作为退款凭证。";
//       								}else{
//       									rs="【已退款】，金额:"+f2Y(pqr.getTotal_fee())+"元";
//       								}
//    					}else if( pqr.getTrade_state().equals("NOTPAY")){
//    						rs="去{"+type.getName()+"}查询到结果，状态是【未付款】，能作为没有付款凭证。";
//    					}else{
//    						rs="去{"+type.getName()+"}查询到结果，但结果有误，返回的状态是："+pqr.getTrade_state()+"，金额:"+f2Y(pqr.getTotal_fee())+"【元】，不能作为支付或者退款凭证。";
    					}
            		}
        		}
    		}
    		logger.info("查询支付状况 结束，方法执行完 weixinpayid=={}", orderid);
    		return pram;
    	}
    }
     
     
    /**分转换为元*/
    private static BigDecimal f2Y(String p){
    	return new BigDecimal(p).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
    
    
    
}
