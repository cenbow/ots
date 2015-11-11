package com.mk.ots.roomsale.model;

public class TRoomSaleCity {
    private Integer id;
    private Integer saleTypeId;
    private String provinceCode;
    private String cityCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSaleTypeId() {
        return saleTypeId;
    }

    public void setSaleTypeId(Integer saleTypeId) {
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
