package com.mk.ots.hotel.model;

import java.util.Date;

/**
 * TRoomRepairModel.
 * 酒店房间维修记录实体类.
 * @author chuaiqing.
 *
 */
public class TRoomRepairModel {
    private Long id;

    private Long hotelid;

    private String repairid;

    private Long roomid;

    private Date begintime;

    private Date endtime;

    private Long roomtypeid;

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

    public String getRepairid() {
        return repairid;
    }

    public void setRepairid(String repairid) {
        this.repairid = repairid == null ? null : repairid.trim();
    }

    public Long getRoomid() {
        return roomid;
    }

    public void setRoomid(Long roomid) {
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

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }
}