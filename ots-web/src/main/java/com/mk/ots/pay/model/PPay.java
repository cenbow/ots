package com.mk.ots.pay.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.mk.ots.common.enums.NeedReturnEnum;
import com.mk.ots.common.enums.PaySrcEnum;
import com.mk.ots.common.enums.PayTypeEnum;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.member.model.UMember;

 

/**
 *
 * @author shellingford
 * @version 2015年1月19日
 */
public class PPay implements Serializable{
	private static final long serialVersionUID = 1L;
	//流水详情
	private List<PPayInfo> payInfos = Lists.newArrayList(); 
	//对账单
	private POrderLog pOrderLog;
	//是否足额支付
	private Boolean enoughPaid;
	
	private Long id;

    private UMember umember;

    private Long orderid;

    private BigDecimal orderprice;

    private BigDecimal lezhu;

    private Date time;

    private String pmsorderno;

    private THotel hotel;

    private PayTypeEnum status;

    private NeedReturnEnum needreturn;

    private Long neworderid;

    private PaySrcEnum paysrc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UMember getMember() {
        return umember;
    }

    public void setMember(UMember member) {
        this.umember = member;
    }

    public UMember getUMember() {
        return umember;
    }

    public void setUMember(UMember member) {
        this.umember = member;
    }
    
    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public BigDecimal getOrderprice() {
        return orderprice;
    }

    public void setOrderprice(BigDecimal orderprice) {
        this.orderprice = orderprice;
    }

    public BigDecimal getLezhu() {
        return lezhu;
    }

    public void setLezhu(BigDecimal lezhu) {
        this.lezhu = lezhu;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getPmsorderno() {
        return pmsorderno;
    }

    public void setPmsorderno(String pmsorderno) {
        this.pmsorderno = pmsorderno == null ? null : pmsorderno.trim();
    }

    public THotel getHotel() {
        return hotel;
    }

    public void setHotel(THotel hotel) {
        this.hotel = hotel;
    }

    public PayTypeEnum getStatus() {
        return status;
    }

    public void setStatus(PayTypeEnum status) {
        this.status = status;
    }

    public NeedReturnEnum getNeedreturn() {
        return needreturn;
    }

    public void setNeedreturn(NeedReturnEnum needreturn) {
        this.needreturn = needreturn;
    }

    public Long getNeworderid() {
        return neworderid;
    }

    public void setNeworderid(Long neworderid) {
        this.neworderid = neworderid;
    }

    public PaySrcEnum getPaysrc() {
        return paysrc;
    }

    public void setPaysrc(PaySrcEnum paysrc) {
        this.paysrc = paysrc;
    }

	public List<PPayInfo> getPayInfos() {
		return payInfos;
	}

	public void setPayInfos(List<PPayInfo> payInfos) {
		this.payInfos = payInfos;
	}

	public POrderLog getpOrderLog() {
		return pOrderLog;
	}

	public void setpOrderLog(POrderLog pOrderLog) {
		this.pOrderLog = pOrderLog;
	}

	public Boolean getEnoughPaid() {
		return enoughPaid;
	}

	public void setEnoughPaid(Boolean enoughPaid) {
		this.enoughPaid = enoughPaid;
	}

	@Override
	public String toString() {
		return "PPay [payInfos=" + payInfos + ", pOrderLog=" + pOrderLog
				+ ", enoughPaid=" + enoughPaid + ", id=" + id + ", umember="
				+ umember + ", orderid=" + orderid + ", orderprice="
				+ orderprice + ", lezhu=" + lezhu + ", time=" + time
				+ ", pmsorderno=" + pmsorderno + ", hotel=" + hotel
				+ ", status=" + status + ", needreturn=" + needreturn
				+ ", neworderid=" + neworderid + ", paysrc=" + paysrc + "]";
	}
}
