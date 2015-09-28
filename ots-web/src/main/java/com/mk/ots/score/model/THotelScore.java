package com.mk.ots.score.model;

import java.math.BigDecimal;
import java.util.Date;

public class THotelScore {
    private Long id;

    private Long roomid;

    private Long roomtypeid;

    private Long hotelid;

    private String score;

    private Date createtime;

    private Long orderid;

    private BigDecimal grade;

    private String hotelscore;

    private String servicescore;

    private Integer isreply;

    private Integer isaduit;

    private Date hotelscoretime;

    private Date servicescoretime;

    private String isvisible;

    private Integer status;

    private Integer type;

    private Long parentid;

    private Long mid;

    private String userCode;

    private String userName;

    private String isdefault;

    private String iscashbacked;

    private BigDecimal backcashcost;

    private String pics;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomid() {
        return roomid;
    }

    public void setRoomid(Long roomid) {
        this.roomid = roomid;
    }

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public Long getHotelid() {
        return hotelid;
    }

    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public String getHotelscore() {
        return hotelscore;
    }

    public void setHotelscore(String hotelscore) {
        this.hotelscore = hotelscore == null ? null : hotelscore.trim();
    }

    public String getServicescore() {
        return servicescore;
    }

    public void setServicescore(String servicescore) {
        this.servicescore = servicescore == null ? null : servicescore.trim();
    }

    public Integer getIsreply() {
        return isreply;
    }

    public void setIsreply(Integer isreply) {
        this.isreply = isreply;
    }

    public Integer getIsaduit() {
        return isaduit;
    }

    public void setIsaduit(Integer isaduit) {
        this.isaduit = isaduit;
    }

    public Date getHotelscoretime() {
        return hotelscoretime;
    }

    public void setHotelscoretime(Date hotelscoretime) {
        this.hotelscoretime = hotelscoretime;
    }

    public Date getServicescoretime() {
        return servicescoretime;
    }

    public void setServicescoretime(Date servicescoretime) {
        this.servicescoretime = servicescoretime;
    }

    public String getIsvisible() {
        return isvisible;
    }

    public void setIsvisible(String isvisible) {
        this.isvisible = isvisible == null ? null : isvisible.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getParentid() {
        return parentid;
    }

    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode == null ? null : userCode.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getIsdefault() {
        return isdefault;
    }

    public void setIsdefault(String isdefault) {
        this.isdefault = isdefault == null ? null : isdefault.trim();
    }

    public String getIscashbacked() {
        return iscashbacked;
    }

    public void setIscashbacked(String iscashbacked) {
        this.iscashbacked = iscashbacked == null ? null : iscashbacked.trim();
    }

    public BigDecimal getBackcashcost() {
        return backcashcost;
    }

    public void setBackcashcost(BigDecimal backcashcost) {
        this.backcashcost = backcashcost;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics == null ? null : pics.trim();
    }
}