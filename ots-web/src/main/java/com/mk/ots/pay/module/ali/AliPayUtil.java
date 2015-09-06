package com.mk.ots.pay.module.ali;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.pay.model.PPayInfo;

/**
 * futao.xiao
 * 2015年5月13日 15:44:01
 * 支付宝退款，查询订单总入口
 * 优化过的接口
 */
public class AliPayUtil {
	
	private static Logger logger = LoggerFactory.getLogger(AliPayUtil.class);
	
	
	/**
	 * 支付宝退款总入口【有密退款】
	 * @param weixinpayid 支付成功后微信返回的ID
	 * @param orderid  本地ID，会原样返回
	 * @param price    【单位是元】
	 * @return  支付宝退款ID
	 */
	 public static String refundPWD(int no){
		String  notify_url="http://www.imike.cc";
		int batch_num=2;
//      String batch_no=UtilDate.getDate()+list.get(0).getPay().getId();
//		String detail_data = getDetailData(list);
		String batch_no = UtilDate.getDate() + "0014";
		String detail_data ="2015052600001000490056919888^31.00^协商退款#2015052600001000490056918415^1.00^协商退款";
		
		//把请求参数打包成数组
		Map<String, String> sParaTemp = new HashMap<String, String>();
		sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
		sParaTemp.put("partner", AlipayConfig.partner);
		sParaTemp.put("_input_charset", AlipayConfig.input_charset);
		sParaTemp.put("notify_url", notify_url);
		sParaTemp.put("seller_email", AlipayConfig.seller_email);
		sParaTemp.put("refund_date", UtilDate.getDateFormatter());
		sParaTemp.put("batch_no", batch_no);
		sParaTemp.put("batch_num", batch_num+"");
		sParaTemp.put("detail_data", detail_data);
		//建立请求
		String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
		return sHtmlText;
	 }
	
	
	
	
	
	
	
	
	
	
 /**
 * 支付宝退款总入口【有密退款】
 * @param weixinpayid 支付成功后微信返回的ID
 * @param orderid  本地ID，会原样返回
 * @param price    【单位是元】
 * @return  支付宝退款ID
 */
 public static String refundPWD(List<PPayInfo>  list,String notifyUrl){
	 if(list==null || list.size()==0){
		return "";
	 }
	 int batch_num=list.size();
     if(batch_num > 1000){
    	//TODO 分几个url 
    	 return manyRefundPWDUrl(list, notifyUrl);
     }else{
//    	 String  batch_no=UtilDate.getDate()+list.get(0).getPay().getId();
    	 String  batch_no=UtilDate.getDate()+"0003";
    	 String  detail_data=getDetailData(list);
    	 System.out.println(detail_data);
    	 return refundPWD(batch_num, batch_no,detail_data,notifyUrl);
     }
 }
  
 
 //超过1000条的数据
 private static String  manyRefundPWDUrl(List<PPayInfo>  list,String notifyUrl){
	 return "";
 }
 
 
  
 private static String  getDetailData(List<PPayInfo>  list){
	 String returnStr="";
	 String s="";
	 for(PPayInfo info: list){
		 s=info.getOtherno()+"^"+info.getCost()+"^协商退款";
		 returnStr=returnStr+s+"#";
	 }
	 returnStr=returnStr.substring(0, returnStr.length()-1 );
	 return returnStr;
 }

   /**
   * 支付宝退款总入口【有密退款】
   * @param weixinpayid 支付成功后微信返回的ID
   * @param orderid  本地ID，会原样返回
   * @param price    【单位是元】
   * @return  支付宝退款ID
   */
 public  static String refundPWD(String orderid,String payid,String price,String notifyUrl){
     String  detail_data=payid+"^"+price+"^协商退款";
     String  batch_no=UtilDate.getDate()+orderid;
  	return refundPWD(1,batch_no,detail_data,notifyUrl);
  }
	
	
 private  static String refundPWD(int  batch_num,String batch_no,String  detail_data,String notifyUrl){
	  	String s=null;
	  	Map<String, String> sPara =new TreeMap<String, String>();
	  	sPara.put("service", AlipayConfig.aliPayRefund);
	  	sPara.put("partner",AlipayConfig.partner);
	  	sPara.put("_input_charset", AlipayConfig.input_charset);
	  	sPara.put("seller_email", AlipayConfig.seller_email);     
//	  	sPara.put("seller_user_id", AlipayConfig.partner);
	  	sPara.put("refund_date", UtilDate.getDateFormatter());
	    sPara.put("batch_no", batch_no);
	    sPara.put("notify_url", notifyUrl);
	    sPara.put("batch_num", batch_num+"");
	  	sPara.put("detail_data", detail_data);
//	    sPara.put("batch_num", 2+"");
//		sPara.put("detail_data", "2015052200001000950052605189^15.00^协商退款#2015052300001000280056348161^1.00^协商退款");
//	  	sPara.put("detail_data", "2015052200001000950052605189^15.00^协商退款");
//		sPara.put("detail_data", "2015052200001000950052605353^15.00^协商退款");
//	  	sPara.put("detail_data", "2015052300001000280056348161^1.00^协商退款");
		
	  	try {
				s= AlipaySubmit.getRefundUrl(sPara);
			} catch (Exception e) {
				e.printStackTrace();
		    }
	  return s;
   }
	
	
	
	
	
	
	

	  /**
     * 支付宝查询订单号
     * @param alipayid 支付成功后微信返回的ID
     * @param orderid  本地ID，会原样返回
     * @param price    【单位是分】支付的金额， 会全部退回
     * @return  支付宝退款ID
     */
    public static String queryOrder(String alipayid,String orderid){
    	String s="";
    	Map<String, String> sPara =new HashMap<String, String>();
    	sPara.put("service", AlipayConfig.aliPayQuery);
    	sPara.put("partner",AlipayConfig.partner);
    	sPara.put("_input_charset", AlipayConfig.input_charset);
    	sPara.put("trade_no",alipayid);    
     	sPara.put("out_trade_no",orderid);   
    	try {
			s= AlipaySubmit.buildRequest("","",sPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return s;
    }
	
    

	  /**
   * 支付宝退款总入口【无密退款】
   * @param weixinpayid 支付成功后微信返回的ID
   * @param orderid  本地ID，会原样返回
   * @param price    【单位是分】支付的金额， 会全部退回
   * @return  支付宝退款ID
   */
  public static String refund(String orderid,String payid, String price){
		String s=null;
    	Map<String, String> sPara =new HashMap<String, String>();
    	sPara.put("service", AlipayConfig.aliPayNopwdRefund);
    	sPara.put("partner",AlipayConfig.partner);
    	sPara.put("_input_charset", AlipayConfig.input_charset);
    	sPara.put("seller_email", AlipayConfig.seller_email);     
    	sPara.put("seller_user_id", AlipayConfig.partner);
    	sPara.put("refund_date", UtilDate.getDateFormatter());
       	sPara.put("batch_no", UtilDate.getDate()+orderid);
       	sPara.put("batch_num", "1");
    	sPara.put("detail_data", payid+"^"+price+"^退款");
    	sPara.put("notify_url", "http://www.imike.com/pay.htm");
    	sPara.put("return_type", "xml");
    	try {
			s= AlipaySubmit.buildRequest("","",sPara);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return s;
    }
    
    
    
	/**
	 * 自动打开浏览器的方法
	 */
	public static void openURL(String url) {  
	       try {  
	           browse(url);  
	       } catch (Exception e) {  
	       }  
	}  
	 
	private static void browse(String url) throws Exception {  
	       //获取操作系统的名字  
	       String osName = System.getProperty("os.name", "");  
	       if (osName.startsWith("Mac OS")) {  
	           //苹果的打开方式  
	           Class fileMgr = Class.forName("com.apple.eio.FileManager");  
	           Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] { String.class });  
	           openURL.invoke(null, new Object[] { url });  
	       } else if (osName.startsWith("Windows")) {  
	          //windows的打开方式。  
	           Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);  
	       } else {  
	           // Unix or Linux的打开方式  
	           String[] browsers = { "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };  
	           String browser = null;  
	           for (int count = 0; count < browsers.length && browser == null; count++)  
	               //执行代码，在brower有值后跳出，  
	               //这里是如果进程创建成功了，==0是表示正常结束。  
	               if (Runtime.getRuntime().exec(new String[] { "which", browsers[count] }).waitFor() == 0)  
	                   browser = browsers[count];  
	           if (browser == null)  
	               throw new Exception("Could not find web browser");  
	           else  
	               //这个值在上面已经成功的得到了一个进程。  
	               Runtime.getRuntime().exec(new String[] { browser, url });  
	    }  
	}  
    
    
    public static String getUrl(HttpServletRequest request){
    	String path = request.getContextPath();
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    }
    
    public static String getUrl(){
    	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();  
    	String path = request.getContextPath();
    	return request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    }
    
    
    /**把支付宝返回的信息做一个判断【单笔订单查询专用】*/
    public static boolean refundQuery(String orderid,String xmlResult){
    	boolean b=false;
    	try {
    		 Element root=getElement(xmlResult);
        	 if(root!=null){
        		if(root.getChildText("is_success").equals("T")){
        			Element root2 =root.getChild("response");
        			if(root2 !=null){
        				Element root3 =root2.getChild("trade");
        				if(root3!=null){
        					if(root3.getChildText("refund_status").equals("REFUND_SUCCESS")){
                    			b=true;
                    		}
        				}
        			}
        		}
        	 }
		} catch (Exception e) {
			logger.error("支付宝查询 退款 订单出错 orderid={} ,返回的结果是：{}",orderid,xmlResult);
		}
    	return b;
    }
    
    
    
    
    
    
   
    /**把支付宝返回的信息做一个判断【单笔订单查询专用】*/
    public static String query(String orderid,String xmlResult){
    	String alipayid=null;
    	try {
    		 Element root=getElement(xmlResult);
        	 if(root!=null){
        		if(root.getChildText("is_success").equals("T")){
        			Element root2 =root.getChild("response");
        			if(root2 !=null){
        				Element root3 =root2.getChild("trade");
        				if(root3!=null){
        					if(root3.getChildText("trade_status").equals("TRADE_SUCCESS")){
                    			alipayid=root3.getChildText("trade_no");
                    		}
        				}
        			}
        		}
        	 }
		} catch (Exception e) {
			logger.error("支付宝查询订单出错 orderid={} ,返回的结果是：{}",orderid,xmlResult);
		}
    	return alipayid;
    }
    
    
    private static Element getElement(String xmlResult){
    	 Element root=null;
    	 try {
 			root = getElementRoot(xmlResult);
 		} catch (Exception e) {
 			e.printStackTrace();
 		}
    	 return root;
    }
    
    
    
    
    private static Element getElementRoot(String xmlResult) throws Exception {
  	  Element root =null;
        if(xmlResult!=null){
        	try {
          	Document doc = XMLUtils.StringtoXML(xmlResult);
        	    root=doc.getRootElement();
        	} catch (Exception e) {
        			e.printStackTrace();
        	}
        }
       return root;
    }
    
    
    public static boolean refundXml(String xmlstring){
    	boolean b=false;
		try {
			SAXBuilder builder = new SAXBuilder(false);
			StringReader read = new StringReader(xmlstring);
			Document  doc = builder.build(read);
			Element elroot =doc.getRootElement();
			if(elroot.getChildText("is_success").equals("T")){
				b=true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return b;
    }
    
    
    
    /**客服查询专用*/
    public static String onlyQuery(String orderid,String xmlResult){
    	String rs=null;
    	try {
    		 Element root=getElement(xmlResult);
        	 if(root!=null){
        		if(root.getChildText("is_success").equals("T")){
        			Element root2 =root.getChild("response");
        			if(root2 !=null){
        				Element root3 =root2.getChild("trade");
        				if(root3!=null){
        					String trade_status=root3.getChildText("trade_status");
        					if(trade_status==null){
        						rs="订单号"+orderid+"去{支付宝}查询到结果，返回结果有误，不能作为支付或者退款凭证。";;

        					}else{
        						if(trade_status.equals("TRADE_SUCCESS")){
        							String refund_status=root3.getChildText("refund_status");
        							if(refund_status==null){
        								rs="订单号"+orderid+"去{支付宝}查询到结果，状态是【已支付】，支付金额:"+root3.getChildText("price")+"【元】，能作为支付凭证。";;
        							}else if(refund_status.equals("REFUND_SUCCESS")){
        								rs="订单号"+orderid+"去{支付宝}查询到结果，状态是【已退款】，支付金额:"+root3.getChildText("price")+"【元】，能作为凭证。";;
        							}
        						}else  if(trade_status.equals("TRADE_CLOSED")){
        							rs="订单号"+orderid+"去{支付宝}查询到结果，状态是【未付款超时（超时作废）】，交易金额:"+root3.getChildText("price")+"【元】，能作为未付款凭证。";;
								}else if(trade_status.equals("TRADE_FINISHED")){
	                    			rs="订单号"+orderid+"去{支付宝}查询到结果，状态是【已支付且不能退款】，交易金额:"+root3.getChildText("price")+"【元】，能作为支付凭证。";;
								}
        					}
        					
        				}
        			}
        		}
        	 }
		} catch (Exception e) {
			logger.error("支付宝查询订单出错 orderid={} ,返回的结果是：{}",orderid,xmlResult);
		}
    	return rs;
    }
	
}
