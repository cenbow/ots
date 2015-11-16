package com.mk.ots.order.bean;

import com.mk.ots.common.utils.Constant;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thinkpad on 2015/11/12.
 */
public class TopPmsRoomOrderQuery {
    private Long hotelId;
    private String todayStr;
    private String yesterdayStr;
    private Integer limitBegin;
    private Integer limitEen;
    private Integer count;
    private Integer basePageSize = Constant.QIE_KE_TOP_NUM * 3;
    private List<PmsRoomOrder> pmsRoomOrderList = new ArrayList<PmsRoomOrder>();

    public String getTodayStr() {
        return todayStr;
    }

    public void setTodayStr(String todayStr) {
        this.todayStr = todayStr;
    }

    public String getYesterdayStr() {
        return yesterdayStr;
    }

    public void setYesterdayStr(String yesterdayStr) {
        this.yesterdayStr = yesterdayStr;
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

    public Integer getLimitBegin() {
        return limitBegin;
    }

    public void setLimitBegin(Integer limitBegin) {
        this.limitBegin = limitBegin;
    }

    public Integer getLimitEen() {
        return limitEen;
    }

    public void setLimitEen(Integer limitEen) {
        this.limitEen = limitEen;
    }

    public Integer getBasePageSize() {
        return basePageSize;
    }

    public void setBasePageSize(Integer basePageSize) {
        this.basePageSize = basePageSize;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}
