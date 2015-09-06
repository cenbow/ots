package com.mk.ots.order.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
@Component
@DbTable(name="u_useticket_record", pkey="id")
public class UUseTicketRecord extends BizModel<UUseTicketRecord> {
    
    private static final long serialVersionUID = -434027864719974886L;
    public static final UUseTicketRecord dao = new UUseTicketRecord();
    
    private Long id;

    private Date usetime;

    private Long tid;

    private Long payid;

    private boolean isreturn;

    private Long mid;

    private BigDecimal cost;

    private Long promotionid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getUsetime() {
        return usetime;
    }

    public void setUsetime(Date usetime) {
        this.usetime = usetime;
    }

    public Long getTid() {
        return tid;
    }

    public void setTid(Long tid) {
        this.tid = tid;
    }

    public Long getPayid() {
        return payid;
    }

    public void setPayid(Long payid) {
        this.payid = payid;
    }

    public boolean isIsreturn() {
		return isreturn;
	}

	public void setIsreturn(boolean isreturn) {
		this.isreturn = isreturn;
	}

	public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public Long getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(Long promotionid) {
        this.promotionid = promotionid;
    }
}

