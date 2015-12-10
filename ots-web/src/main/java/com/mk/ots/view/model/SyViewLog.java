package com.mk.ots.view.model;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by jeashi on 2015/12/9.
 */
//sy_view_log
public class SyViewLog {
    private  Long  id;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String cityCode;
    private String ip;
    private String callMethod;
    private String version;
    private String wifiMacaddr;
    private String  biMacaddr;
    private Date createTime;
    private  String   simsn;
    private  String   fromUrl;
    private  String   toUrl;
    private  Long   mid;
    private  String   params;
    private  String   bussinessId;
    private  Integer   bussinessType;
    private  String   actionType;
    private  String   viewCode;
    private  String   imei;
    private  String  hardwarecode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCallMethod() {
        return callMethod;
    }

    public void setCallMethod(String callMethod) {
        this.callMethod = callMethod;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getWifiMacaddr() {
        return wifiMacaddr;
    }

    public void setWifiMacaddr(String wifiMacaddr) {
        this.wifiMacaddr = wifiMacaddr;
    }

    public String getBiMacaddr() {
        return biMacaddr;
    }

    public void setBiMacaddr(String biMacaddr) {
        this.biMacaddr = biMacaddr;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getSimsn() {
        return simsn;
    }

    public void setSimsn(String simsn) {
        this.simsn = simsn;
    }

    public String getFromUrl() {
        return fromUrl;
    }

    public void setFromUrl(String fromUrl) {
        this.fromUrl = fromUrl;
    }

    public String getToUrl() {
        return toUrl;
    }

    public void setToUrl(String toUrl) {
        this.toUrl = toUrl;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getBussinessId() {
        return bussinessId;
    }

    public void setBussinessId(String bussinessId) {
        this.bussinessId = bussinessId;
    }

    public Integer getBussinessType() {
        return bussinessType;
    }

    public void setBussinessType(Integer bussinessType) {
        this.bussinessType = bussinessType;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getViewCode() {
        return viewCode;
    }

    public void setViewCode(String viewCode) {
        this.viewCode = viewCode;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getHardwarecode() {
        return hardwarecode;
    }

    public void setHardwarecode(String hardwarecode) {
        this.hardwarecode = hardwarecode;
    }
}
