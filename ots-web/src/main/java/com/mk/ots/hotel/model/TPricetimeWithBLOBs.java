package com.mk.ots.hotel.model;

public class TPricetimeWithBLOBs extends TPricetime {
    private String addcron;

    private String subcron;

    public String getAddcron() {
        return addcron;
    }

    public void setAddcron(String addcron) {
        this.addcron = addcron;
    }

    public String getSubcron() {
        return subcron;
    }

    public void setSubcron(String subcron) {
        this.subcron = subcron;
    }
}