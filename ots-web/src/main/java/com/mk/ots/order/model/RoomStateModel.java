package com.mk.ots.order.model;

import java.util.Date;

/**
 * RoomState Model
 * @author chuaiqing.
 *
 */
public class RoomStateModel {
    private long hotelid;
    private long roomtypid;
    private long id;
    private long name;
    private long pms;
    private long pmsroomorderid;
    private String status;
    private long roomid;
    private Date begintime;
    private Date endtime;
    private Date otsbegindate;
    private Date otsenddate;
    private long otsdays;
    private String roomstatus;
    public long getHotelid() {
        return hotelid;
    }
    public void setHotelid(long hotelid) {
        this.hotelid = hotelid;
    }
    public long getRoomtypid() {
        return roomtypid;
    }
    public void setRoomtypid(long roomtypid) {
        this.roomtypid = roomtypid;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getName() {
        return name;
    }
    public void setName(long name) {
        this.name = name;
    }
    public long getPms() {
        return pms;
    }
    public void setPms(long pms) {
        this.pms = pms;
    }
    public long getPmsroomorderid() {
        return pmsroomorderid;
    }
    public void setPmsroomorderid(long pmsroomorderid) {
        this.pmsroomorderid = pmsroomorderid;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public long getRoomid() {
        return roomid;
    }
    public void setRoomid(long roomid) {
        this.roomid = roomid;
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
    public Date getOtsbegindate() {
        return otsbegindate;
    }
    public void setOtsbegindate(Date otsbegindate) {
        this.otsbegindate = otsbegindate;
    }
    public Date getOtsenddate() {
        return otsenddate;
    }
    public void setOtsenddate(Date otsenddate) {
        this.otsenddate = otsenddate;
    }
    public long getOtsdays() {
        return otsdays;
    }
    public void setOtsdays(long otsdays) {
        this.otsdays = otsdays;
    }
    public String getRoomstatus() {
        return roomstatus;
    }
    public void setRoomstatus(String roomstatus) {
        this.roomstatus = roomstatus;
    }
    
}
