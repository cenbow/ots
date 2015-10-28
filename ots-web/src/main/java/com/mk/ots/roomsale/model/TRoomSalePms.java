package com.mk.ots.roomsale.model;

import java.sql.Time;

public class TRoomSalePms {
    private Integer id;
    private String text;
    private Time showBegin;
    private Integer showContinue;
    private Integer type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Time getShowBegin() {
        return showBegin;
    }

    public void setShowBegin(Time showBegin) {
        this.showBegin = showBegin;
    }

    public Integer getShowContinue() {
        return showContinue;
    }

    public void setShowContinue(Integer showContinue) {
        this.showContinue = showContinue;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
