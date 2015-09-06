package com.mk.ots.promo.model;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
//b_promotion_price

@Component
@DbTable(name="b_promotion_price", pkey="id")
public class BPromotionPrice extends BizModel<BPromotionPrice> {
    private static final long serialVersionUID = -434027864719974886L;
    public static final BPromotionPrice dao = new BPromotionPrice();
    
    public BPromotion getPromotionDAO(){
    	return BPromotion.dao.findById(getLong("promotion"));
    }
    
    public BigDecimal getPromotionPrice() {
		return this.getBigDecimal("price");
	}
    
	public BigDecimal getPromotionOfflinePrice() {
		return this.getBigDecimal("offlineprice");
	}
	
	private Long id;

    private Long promotion;

    public Long getPromotion() {
		return promotion;
	}

	public void setPromotion(Long promotion) {
		this.promotion = promotion;
	}

	public BigDecimal getPrice() {
		return price;
	}

	private BigDecimal price;

    private BigDecimal offlineprice;

    private Long otaorderid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getOfflineprice() {
        return offlineprice;
    }

    public void setOfflineprice(BigDecimal offlineprice) {
        this.offlineprice = offlineprice;
    }

    public Long getOtaorderid() {
        return otaorderid;
    }

    public void setOtaorderid(Long otaorderid) {
        this.otaorderid = otaorderid;
    }

	@Override
	public String toString() {
		return "BPromotionPrice [id=" + id + ", promotion=" + promotion
				+ ", price=" + price + ", offlineprice=" + offlineprice
				+ ", otaorderid=" + otaorderid + "]";
	}
}
