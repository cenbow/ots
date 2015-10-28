package com.mk.ots.roomsale.model;

public class TRoomSaleConfigForPms {
    private String hotelId;
    private String roomId;
    private String roomTypeId;
    private Integer newCount;

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Integer getNewCount() {
        return newCount;
    }

    public void setNewCount(Integer newCount) {
        this.newCount = newCount;
    }
}
