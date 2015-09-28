package com.mk.ots.order.model;

import java.math.BigDecimal;
import java.util.Date;

public class BOtaorder {
    private Long id;

    private Long hotelid;

    private String hotelname;

    private String hotelpms;

    private Integer ordermethod;

    private Integer ordertype;

    private Integer pricetype;

    private Date begintime;

    private Date endtime;

    private Long mid;

    private Long mlevel;

    private Date createtime;

    private String promotion;

    private String coupon;

    private BigDecimal totalprice;

    private BigDecimal price;

    private Integer breakfastnum;

    private String contacts;

    private String contactsphone;

    private String contactsemail;

    private String contactsweixin;

    private Integer orderstatus;

    private Integer paystatus;

    private String receipt;

    private Date updatetime;

    private String canshow;

    private String hiddenorder;

    private String ostype;

    private Integer version;

    private Long spreaduser;

    private Integer daynumber;

    private String isscore;

    private Long activeid;

    private Integer invalidreason;

    private String note;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelid() {
        return hotelid;
    }

    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname == null ? null : hotelname.trim();
    }

    public String getHotelpms() {
        return hotelpms;
    }

    public void setHotelpms(String hotelpms) {
        this.hotelpms = hotelpms == null ? null : hotelpms.trim();
    }

    public Integer getOrdermethod() {
        return ordermethod;
    }

    public void setOrdermethod(Integer ordermethod) {
        this.ordermethod = ordermethod;
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public Integer getPricetype() {
        return pricetype;
    }

    public void setPricetype(Integer pricetype) {
        this.pricetype = pricetype;
    }

    public Date getBegintime() {
        return begintime;
    }

    public void setBegintime(Date begintime) {
        this.begintime = begintime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getMlevel() {
        return mlevel;
    }

    public void setMlevel(Long mlevel) {
        this.mlevel = mlevel;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion == null ? null : promotion.trim();
    }

    public String getCoupon() {
        return coupon;
    }

    public void setCoupon(String coupon) {
        this.coupon = coupon == null ? null : coupon.trim();
    }

    public BigDecimal getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(BigDecimal totalprice) {
        this.totalprice = totalprice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getBreakfastnum() {
        return breakfastnum;
    }

    public void setBreakfastnum(Integer breakfastnum) {
        this.breakfastnum = breakfastnum;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    public String getContactsphone() {
        return contactsphone;
    }

    public void setContactsphone(String contactsphone) {
        this.contactsphone = contactsphone == null ? null : contactsphone.trim();
    }

    public String getContactsemail() {
        return contactsemail;
    }

    public void setContactsemail(String contactsemail) {
        this.contactsemail = contactsemail == null ? null : contactsemail.trim();
    }

    public String getContactsweixin() {
        return contactsweixin;
    }

    public void setContactsweixin(String contactsweixin) {
        this.contactsweixin = contactsweixin == null ? null : contactsweixin.trim();
    }

    public Integer getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(Integer orderstatus) {
        this.orderstatus = orderstatus;
    }

    public Integer getPaystatus() {
        return paystatus;
    }

    public void setPaystatus(Integer paystatus) {
        this.paystatus = paystatus;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt == null ? null : receipt.trim();
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getCanshow() {
        return canshow;
    }

    public void setCanshow(String canshow) {
        this.canshow = canshow == null ? null : canshow.trim();
    }

    public String getHiddenorder() {
        return hiddenorder;
    }

    public void setHiddenorder(String hiddenorder) {
        this.hiddenorder = hiddenorder == null ? null : hiddenorder.trim();
    }

    public String getOstype() {
        return ostype;
    }

    public void setOstype(String ostype) {
        this.ostype = ostype == null ? null : ostype.trim();
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Long getSpreaduser() {
        return spreaduser;
    }

    public void setSpreaduser(Long spreaduser) {
        this.spreaduser = spreaduser;
    }

    public Integer getDaynumber() {
        return daynumber;
    }

    public void setDaynumber(Integer daynumber) {
        this.daynumber = daynumber;
    }

    public String getIsscore() {
        return isscore;
    }

    public void setIsscore(String isscore) {
        this.isscore = isscore == null ? null : isscore.trim();
    }

    public Long getActiveid() {
        return activeid;
    }

    public void setActiveid(Long activeid) {
        this.activeid = activeid;
    }

    public Integer getInvalidreason() {
        return invalidreason;
    }

    public void setInvalidreason(Integer invalidreason) {
        this.invalidreason = invalidreason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note == null ? null : note.trim();
    }
}