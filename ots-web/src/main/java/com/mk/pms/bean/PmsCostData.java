package com.mk.pms.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.mk.pms.myenum.PmsCostSourceEnum;
import com.mk.pms.myenum.PmsCostTypeEnum;

/**
 *
 * @author shellingford
 * @version 2015年1月12日
 */
public class PmsCostData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6142043287486472120L;

	private String id;
	private String customerno;
	private PmsCostTypeEnum costType;
	private PmsCostSourceEnum contSource;
	private BigDecimal price;
	private BigDecimal roomcost;
	private Date time;
	private Date optime;
	private String opuser;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCustomerno() {
		return customerno;
	}
	public void setCustomerno(String customerno) {
		this.customerno = customerno;
	}
	public PmsCostTypeEnum getCostType() {
		return costType;
	}
	public void setCostType(PmsCostTypeEnum costType) {
		this.costType = costType;
	}
	public PmsCostSourceEnum getContSource() {
		return contSource;
	}
	public void setContSource(PmsCostSourceEnum contSource) {
		this.contSource = contSource;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public BigDecimal getRoomcost() {
		return roomcost;
	}
	public void setRoomcost(BigDecimal roomcost) {
		this.roomcost = roomcost;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Date getOptime() {
		return optime;
	}
	public void setOptime(Date optime) {
		this.optime = optime;
	}
	public String getOpuser() {
		return opuser;
	}
	public void setOpuser(String opuser) {
		this.opuser = opuser;
	}
	
}
