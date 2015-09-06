package com.mk.es;

import org.elasticsearch.common.geo.GeoPoint;

/**
 * Hotel 
 *
 */
public class Hotel {

    private String name = null;

    private String district = null;

    private Boolean hasRoom = Boolean.TRUE;

    private GeoPoint pin = null;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistrict() {
        return this.district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Boolean getHasRoom() {
        return this.hasRoom;
    }

    public void setHasRoom(Boolean hasRoom) {
        this.hasRoom = hasRoom;
    }

    public GeoPoint getPin() {
        return this.pin;
    }

    public void setPin(GeoPoint pin) {
        this.pin = pin;
    }

}
