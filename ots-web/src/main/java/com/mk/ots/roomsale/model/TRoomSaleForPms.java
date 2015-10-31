package com.mk.ots.roomsale.model;

import java.util.ArrayList;
import java.util.List;

public class TRoomSaleForPms {
    private Integer type;
    private String begin;
    private Long continues;
    private String confShow;
    private String showBegin;
    private Long showContinue;
    private String noConfShow;
    private List<TRoomTypeForPms>info=new ArrayList<>();

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBegin() {
        return begin;
    }

    public void setBegin(String begin) {
        this.begin = begin;
    }

    public Long getContinues() {
        return continues;
    }

    public void setContinues(Long continues) {
        this.continues = continues;
    }

    public String getConfShow() {
        return confShow;
    }

    public void setConfShow(String confShow) {
        this.confShow = confShow;
    }

    public String getShowBegin() {
        return showBegin;
    }

    public void setShowBegin(String showBegin) {
        this.showBegin = showBegin;
    }

    public Long getShowContinue() {
        return showContinue;
    }

    public void setShowContinue(Long showContinue) {
        this.showContinue = showContinue;
    }

    public String getNoConfShow() {
        return noConfShow;
    }

    public void setNoConfShow(String noConfShow) {
        this.noConfShow = noConfShow;
    }

    public List<TRoomTypeForPms> getInfo() {
        return info;
    }

    public void setInfo(List<TRoomTypeForPms> info) {
        this.info = info;
    }
}
