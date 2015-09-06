package com.mk.ots.hotel.dao;

import java.util.List;

public class RoomSubmitSqlGroup {

    private String inSql = null;

    private String upSql = null;

    private String inRoomTempSql = null;

    private List<?> upList = null;

    private List<?> inList = null;
    
    private Object[][] inParams = null;

    private List<?> inRoomTempList = null;

    public String getInSql() {
        return inSql;
    }

    public void setInSql(String inSql) {
        this.inSql = inSql;
    }

    public String getUpSql() {
        return upSql;
    }

    public void setUpSql(String upSql) {
        this.upSql = upSql;
    }

    public String getInRoomTempSql() {
        return inRoomTempSql;
    }

    public void setInRoomTempSql(String inRoomTempSql) {
        this.inRoomTempSql = inRoomTempSql;
    }

    public List<?> getUpList() {
        return upList;
    }

    public void setUpList(List<?> upList) {
        this.upList = upList;
    }

    public List<?> getInList() {
        return inList;
    }

    public void setInList(List<?> inList) {
        this.inList = inList;
    }
    
    public Object[][] getInParams() {
        return inParams;
    }
    
    public void setInParams(Object[][] inParams) {
        this.inParams = inParams;
    }

    public List<?> getInRoomTempList() {
        return inRoomTempList;
    }

    public void setInRoomTempList(List<?> inRoomTempList) {
        this.inRoomTempList = inRoomTempList;
    }
}
