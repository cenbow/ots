/**
 * 2015年7月17日下午6:14:55
 * zhaochuanbin
 */
package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.mk.ots.order.bean.OtaOrder;

/**
 * pms定时退款
 *
 */
public class ReSendLeZhu  implements Serializable {
	
	private static final long serialVersionUID = -8449513006221915802L;

	private OtaOrder order;
	
	private long payid;
	
	private Long pmsSendId;
	
	private BigDecimal price;
	
	private String memberName;

	/**
	 * @return the order
	 */
	public OtaOrder getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(OtaOrder order) {
		this.order = order;
	}

	/**
	 * @return the payid
	 */
	public long getPayid() {
		return payid;
	}

	/**
	 * @param payid the payid to set
	 */
	public void setPayid(long payid) {
		this.payid = payid;
	}

	/**
	 * @return the price
	 */
	public BigDecimal getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	/**
	 * @return the memberName
	 */
	public String getMemberName() {
		return memberName;
	}

	/**
	 * @param memberName the memberName to set
	 */
	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}
	
	
	
	/**
	 * @return the pmsSendId
	 */
	public Long getPmsSendId() {
		return pmsSendId;
	}

	/**
	 * @param pmsSendId the pmsSendId to set
	 */
	public void setPmsSendId(Long pmsSendId) {
		this.pmsSendId = pmsSendId;
	}

	public ReSendLeZhu(){
		
	}

	/**
	 * 2015年7月20日下午3:07:51
	 * @param order
	 * @param payid
	 * @param pmsSendId
	 * @param price
	 * @param memberName
	 */
	public ReSendLeZhu(OtaOrder order, long payid, Long pmsSendId,
			BigDecimal price, String memberName) {
		super();
		this.order = order;
		this.payid = payid;
		this.pmsSendId = pmsSendId;
		this.price = price;
		this.memberName = memberName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReSendLeZhu [order=" + order + ", payid=" + payid
				+ ", pmsSendId=" + pmsSendId + ", price=" + price
				+ ", memberName=" + memberName + "]";
	}
}
