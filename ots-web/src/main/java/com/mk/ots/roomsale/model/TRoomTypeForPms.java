package com.mk.ots.roomsale.model;

public class TRoomTypeForPms {
    private String roomTypeId;
    private Integer minCount;
    private Integer currCount;
    private String isEdit;

    public String getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Integer getMinCount() {
        return minCount;
    }

    public void setMinCount(Integer minCount) {
        this.minCount = minCount;
    }

    public Integer getCurrCount() {
        return currCount;
    }

    public void setCurrCount(Integer currCount) {
        this.currCount = currCount;
    }

    public String getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(String isEdit) {
        this.isEdit = isEdit;
    }
}
