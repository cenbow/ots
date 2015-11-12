package com.mk.ots.order.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thinkpad on 2015/11/12.
 */
public class TopPmsRoomOrderQuery {
    private Date now;
    private Long limitBegin;
    private Long limitEen;
    private Integer count;
    private List<PmsRoomOrder> pmsRoomOrderList = new ArrayList<PmsRoomOrder>();

    public Date getNow() {
        return now;
    }

    public void setNow(Date now) {
        this.now = now;
    }

    public Long getLimitBegin() {
        return limitBegin;
    }

    public void setLimitBegin(Long limitBegin) {
        this.limitBegin = limitBegin;
    }

    public Long getLimitEen() {
        return limitEen;
    }

    public void setLimitEen(Long limitEen) {
        this.limitEen = limitEen;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<PmsRoomOrder> getPmsRoomOrderList() {
        return pmsRoomOrderList;
    }

    public void setPmsRoomOrderList(List<PmsRoomOrder> pmsRoomOrderList) {
        this.pmsRoomOrderList = pmsRoomOrderList;
    }
}
