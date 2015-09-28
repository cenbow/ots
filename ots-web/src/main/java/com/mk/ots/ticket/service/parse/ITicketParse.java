package com.mk.ots.ticket.service.parse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import org.elasticsearch.common.base.Strings;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.XMLUtils;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.PlatformTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.system.model.UToken;
import com.mk.ots.ticket.model.UTicket;

/**
 * 接口规范，使用接口顺序为
 * 1、init初始化
 * 2、parse解析所有数据,同时计算订单内的pms价格
 * 3、获取附属信息（这里不再进行解析）
 * 4、清空clear，以便重新调用parse
 * @author shellingford
 * @version 2015年1月29日
 */
public abstract class ITicketParse {
	private static Logger logger = LoggerFactory.getLogger(ITicketParse.class);
	
	protected BPromotion promotion;
	
	protected UTicket ticket;
	
	protected BigDecimal onlineprice = new BigDecimal(0);
	
	protected BigDecimal offlineprice = new BigDecimal(0);

	protected boolean needUse;
	
	protected OrderTypeEnum orderType;
	protected OtaOrder otaOrder;
	
	/**
	 * 
	 * @param promotion TODO
	 * @param uticket
	 * @param bPromotion
	 */
	public void init(UTicket uTicket, BPromotion promotion){
		this.ticket = uTicket;
		this.promotion = promotion;
		try {
			if((promotion.getOnlineprice()!=null && promotion.getOnlineprice().compareTo(BigDecimal.ZERO)>0) || (promotion.getOfflineprice()!=null && promotion.getOfflineprice().compareTo(BigDecimal.ZERO)>0 )){
				this.onlineprice = promotion.getOnlineprice();
				this.offlineprice = promotion.getOfflineprice();
			} else if(!Strings.isNullOrEmpty(promotion.getInfo())){
				Document doc = (Document) XMLUtils.StringtoXML(promotion.getInfo());
				this.onlineprice = new BigDecimal(Double.valueOf(getEleValueByName(doc.getRootElement(),"subprice")));
				this.offlineprice = new BigDecimal(Double.valueOf(getEleValueByName(doc.getRootElement(),"offlinesubprice")));
			} else {
				logger.error("info为空. promotion:{}", promotion);
				this.onlineprice = new BigDecimal(0);
				this.offlineprice  = new BigDecimal(0);
			}
		} catch (JDOMException | IOException e) {
			throw MyErrorEnum.xmlParseError.getMyException();
		}
	}
	/**
	 * 解析订单
	 * @param otaOrder
	 */
	public  void parse(OtaOrder otaOrder){
		if(otaOrder != null){
			orderType = OrderTypeEnum.getByID(otaOrder.getOrderType());
			this.otaOrder = otaOrder;
		}
	}
	
	/**
	 * 还原成初始化状态
	 */
	public void clear(){
		this.promotion = null;
		this.ticket = null;
		this.onlineprice = null;
		this.offlineprice = null;
		this.orderType = null;
	}
	
	/**
	 * 优惠券是否可用
	 * @return TODO
	 * @throws ParseException 
	 */
	public boolean checkUsable() {
		Date currentDate = new Date();
		return this.getPromotion()!=null 
				&& this.getPromotion().getBegintime().before(currentDate) 
				&& this.getPromotion().getEndtime().after(currentDate)
				&& this.checkPlatFormUseable(this.getPromotion());
	}
	public boolean checkUsableByTime() {
		Date currentDate = new Date();
		return this.getPromotion().getBegintime().before(currentDate) && this.getPromotion().getEndtime().after(currentDate);
	}
	
	
	/**
	 * 校验当前平台是否可以使用优惠券
	 * @return
	 */
	private  boolean checkPlatFormUseable(BPromotion bPromotion){
		UToken uToken = MyTokenUtils.getToken("");
		if(null == uToken){
			logger.info("uToken==null,优惠券所有的平台都不可以使用");
			return false;
		}

        if(bPromotion==null){
        	logger.info("bPromotion==null,优惠券所有的平台不可以使用");
            return false;
        }

		//平台类型为空时，认为是全部平台都可用
		if(bPromotion.getPlatformtype()==null){
			logger.info("bPromotion.getPlatformtype()==null,优惠券所有的平台都可以使用，token 类型："+uToken.getType());
			return true;
		}
		if(PlatformTypeEnum.ALL.getId().equals(bPromotion.getPlatformtype())){
			logger.info("bPromotion.getPlatformtype()==PlatformTypeEnum.ALL,优惠券所有的平台都可以使用，token 类型："+uToken.getType());
			return true;
		}
		PlatformTypeEnum platformTypeEnum = null;
		if (TokenTypeEnum.APP.equals(uToken.getType())) {
			platformTypeEnum = PlatformTypeEnum.APP; // 手机app
		} else if (TokenTypeEnum.WX.equals(uToken.getType())) {
			platformTypeEnum = PlatformTypeEnum.WEIXIN;
		} else {
			platformTypeEnum = PlatformTypeEnum.ALL;
		}
		
		if(platformTypeEnum==null){
			logger.info("订单平台来源为空，优惠券所有的平台都不允许使用，token 类型："+uToken.getType());
			return false;
		}

		
		//当平台适配时，返回true
		if(platformTypeEnum.getId().equals(bPromotion.getPlatformtype())){
			logger.info("订单是"+platformTypeEnum.getName()+"，优惠券所有的平台都可以使用，token 类型："+uToken.getType());
			return true;
		}
		return false;
	}
	
	/**
	 * 下给pms的补贴（即认为这部分钱已经使用了ota乐住币支付了）
	 * @return
	 */
	public BigDecimal otaSubsidy() {
		if(!promotion.getIsota()){
			return BigDecimal.ZERO;
		}
		return allSubSidy();
	}
	/**
	 * 商户补贴
	 * @return
	 */
	public BigDecimal hotelSubsidy() {
		if(promotion.getIsota()){
			return BigDecimal.ZERO;
		}
		return allSubSidy();
	}
	/**
	 * 获取优惠券金额
	 * @return
	 */
	public BigDecimal allSubSidy() {
		//线下到付
		if(OrderTypeEnum.PT.equals(orderType)){
			return this.offlineprice;
		}
		return this.onlineprice;
	}
	
	

	/**
	 * 获取ota实际需要补贴金额
	 * @return
	 */
	public BigDecimal realOtaSubsidy() {
		if (!promotion.getIsota()) {
			return BigDecimal.ZERO;
		}
		return allSubSidy().multiply(promotion.getOtapre());
	}
	/**
	 * 获取优惠码信息
	 * @return
	 */
	public BPromotion getPromotion(){
		return this.promotion;
	}
	/**
	 * 获取优惠券信息
	 * @return
	 */
	public UTicket getTicket(){
		return this.ticket;
	}
	/**
	 * 获取线下优惠价格
	 * @return
	 */
	public BigDecimal getOfflinePrice(){
		return this.offlineprice;
	}
	
	/**
	 * 获取线上价格
	 * @return
	 */
	public BigDecimal getOnlinePrice(){
		return this.onlineprice;
	}
	
	/**
	 * 判断是否需要使用优惠券/码
	 * @return
	 */
	public Boolean needUse(){
		return this.needUse;
	}
	
	/**
	 * 设置是否需要使用
	 */
	public void setNeedUse(Boolean needUse){
		this.needUse = needUse;
	}
	
	/**
	 * 获取子元素的值
	 * 
	 * @param root
	 * @param eleName
	 * @return
	 */
	protected String getEleValueByName(Element root, String eleName) {
		Element ele = root.getChild(eleName);
		if (ele == null) {
			return null;
		}
		String value = ele.getAttributeValue("value");
		if (!Strings.isNullOrEmpty(value)) {
			return value.trim();
		}
		return null;
	}
	public OtaOrder getOtaOrder() {
		return otaOrder;
	}
	public void setOtaOrder(OtaOrder otaOrder) {
		this.otaOrder = otaOrder;
	}
	
	
}
