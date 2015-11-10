package com.mk.ots.roomsale.model;

import java.util.Date;

public class TRoomSaleCity {
    private Long id;
    private Long saleTypeId;
    private String provinceCode;
    private String cityCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSaleTypeId() {
        return saleTypeId;
    }

    public void setSaleTypeId(Long saleTypeId) {
        this.saleTypeId = saleTypeId;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}
