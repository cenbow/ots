package com.mk.ots.hotel.model;

public class ERoomtypeFacilityModel {
    private Long id;

    private Long roomtypeid;

    private Long facid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public Long getFacid() {
        return facid;
    }

    public void setFacid(Long facid) {
        this.facid = facid;
    }
}