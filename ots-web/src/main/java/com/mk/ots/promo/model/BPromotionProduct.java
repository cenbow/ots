package com.mk.ots.promo.model;

import java.math.BigDecimal;
import java.util.Date;

public class BPromotionProduct {
    private Long id;

    private Long productid;

    private String name;

    private Long promotionid;

    private BigDecimal price;

    private Integer buytimelimit;

    private String createby;

    private String updateby;

    private Date createtime;

    private Date updatetime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductid() {
        return productid;
    }

    public void setProductid(Long productid) {
        this.productid = productid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getPromotionid() {
        return promotionid;
    }

    public void setPromotionid(Long promotionid) {
        this.promotionid = promotionid;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getBuytimelimit() {
        return buytimelimit;
    }

    public void setBuytimelimit(Integer buytimelimit) {
        this.buytimelimit = buytimelimit;
    }

    public String getCreateby() {
        return createby;
    }

    public void setCreateby(String createby) {
        this.createby = createby == null ? null : createby.trim();
    }

    public String getUpdateby() {
        return updateby;
    }

    public void setUpdateby(String updateby) {
        this.updateby = updateby == null ? null : updateby.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }
}