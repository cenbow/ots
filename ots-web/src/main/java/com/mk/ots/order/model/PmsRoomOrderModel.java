package com.mk.ots.order.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

/**
 * PmsRoomOrder Model.
 * @author chuaiqing.
 *
 */
public class PmsRoomOrderModel {
    @Id
    private Long id;
    private String pmsroomorderno;  
    private Long hotelid;
    private String hotelpms;       
    private Long pmsorderid;  
    private String status;
    private String roomtypepms;          
    private String pmsorderno;         
    private Long roomtypeid;    
    private Long roomid;
    private String roomno;
    private String roomtypename;       
    private String roompms;  
    private Date begintime; 
    private Date endtime;
    private Date checkintime;   
    private Date checkouttime;      
    private Integer ordertype;
    private BigDecimal roomcost;       
    private BigDecimal othercost;      
    private BigDecimal mikepay; 
    private BigDecimal otherpay;    
    private String opuser;
    private String visible;
    private String roomstate;
    
    /** getters and setters: begin */
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getPmsroomorderno() {
        return pmsroomorderno;
    }
    public void setPmsroomorderno(String pmsroomorderno) {
        this.pmsroomorderno = pmsroomorderno;
    }
    public Long getHotelid() {
        return hotelid;
    }
    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }
    public String getHotelpms() {
        return hotelpms;
    }
    public void setHotelpms(String hotelpms) {
        this.hotelpms = hotelpms;
    }
    public Long getPmsorderid() {
        return pmsorderid;
    }
    public void setPmsorderid(Long pmsorderid) {
        this.pmsorderid = pmsorderid;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRoomtypepms() {
        return roomtypepms;
    }
    public void setRoomtypepms(String roomtypepms) {
        this.roomtypepms = roomtypepms;
    }
    public String getPmsorderno() {
        return pmsorderno;
    }
    public void setPmsorderno(String pmsorderno) {
        this.pmsorderno = pmsorderno;
    }
    public Long getRoomtypeid() {
        return roomtypeid;
    }
    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }
    public Long getRoomid() {
        return roomid;
    }
    public void setRoomid(Long roomid) {
        this.roomid = roomid;
    }
    public String getRoomno() {
        return roomno;
    }
    public void setRoomno(String roomno) {
        this.roomno = roomno;
    }
    public String getRoomtypename() {
        return roomtypename;
    }
    public void setRoomtypename(String roomtypename) {
        this.roomtypename = roomtypename;
    }
    public String getRoompms() {
        return roompms;
    }
    public void setRoompms(String roompms) {
        this.roompms = roompms;
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
    public Date getCheckintime() {
        return checkintime;
    }
    public void setCheckintime(Date checkintime) {
        this.checkintime = checkintime;
    }
    public Date getCheckouttime() {
        return checkouttime;
    }
    public void setCheckouttime(Date checkouttime) {
        this.checkouttime = checkouttime;
    }
    public Integer getOrdertype() {
        return ordertype;
    }
    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }
    public BigDecimal getRoomcost() {
        return roomcost;
    }
    public void setRoomcost(BigDecimal roomcost) {
        this.roomcost = roomcost;
    }
    public BigDecimal getOthercost() {
        return othercost;
    }
    public void setOthercost(BigDecimal othercost) {
        this.othercost = othercost;
    }
    public BigDecimal getMikepay() {
        return mikepay;
    }
    public void setMikepay(BigDecimal mikepay) {
        this.mikepay = mikepay;
    }
    public BigDecimal getOtherpay() {
        return otherpay;
    }
    public void setOtherpay(BigDecimal otherpay) {
        this.otherpay = otherpay;
    }
    public String getOpuser() {
        return opuser;
    }
    public void setOpuser(String opuser) {
        this.opuser = opuser;
    }
    public String getVisible() {
        return visible;
    }
    public void setVisible(String visible) {
        this.visible = visible;
    }
    public String getRoomstate() {
        return roomstate;
    }
    public void setRoomstate(String roomstate) {
        this.roomstate = roomstate;
    }
    
    /** getters and setters: end   */
    
}
