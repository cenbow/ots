package com.mk.ots.hotel.model;

import java.math.BigDecimal;
import java.util.Date;

public class EHotelModel {
    private Long id;

    private String hotelname;

    private String hotelcontactname;

    private Date regtime;

    private Integer disid;

    private String detailaddr;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Date opentime;

    private Date repairtime;

    private Integer roomnum;

    private String businesslicensefront;

    private String businesslicenseback;

    private String pms;

    private Integer state;

    private String visible;

    private String reason;

    private Date reasontime;

    private Date updatetime;

    private Integer pricestate;

    private String pricereason;

    private Integer pmsstatus;

    private String pmsuser;

    private String idcardfront;

    private String idcardback;

    private String retentiontime;

    private String defaultleavetime;

    private String hotelphone;

    private String isnewpms;
    
    private String introduction;

    private String traffic;

    private String hotelpic;

    private String peripheral;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname == null ? null : hotelname.trim();
    }

    public String getHotelcontactname() {
        return hotelcontactname;
    }

    public void setHotelcontactname(String hotelcontactname) {
        this.hotelcontactname = hotelcontactname == null ? null : hotelcontactname.trim();
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public Integer getDisid() {
        return disid;
    }

    public void setDisid(Integer disid) {
        this.disid = disid;
    }

    public String getDetailaddr() {
        return detailaddr;
    }

    public void setDetailaddr(String detailaddr) {
        this.detailaddr = detailaddr == null ? null : detailaddr.trim();
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

    public Date getOpentime() {
        return opentime;
    }

    public void setOpentime(Date opentime) {
        this.opentime = opentime;
    }

    public Date getRepairtime() {
        return repairtime;
    }

    public void setRepairtime(Date repairtime) {
        this.repairtime = repairtime;
    }

    public Integer getRoomnum() {
        return roomnum;
    }

    public void setRoomnum(Integer roomnum) {
        this.roomnum = roomnum;
    }

    public String getBusinesslicensefront() {
        return businesslicensefront;
    }

    public void setBusinesslicensefront(String businesslicensefront) {
        this.businesslicensefront = businesslicensefront == null ? null : businesslicensefront.trim();
    }

    public String getBusinesslicenseback() {
        return businesslicenseback;
    }

    public void setBusinesslicenseback(String businesslicenseback) {
        this.businesslicenseback = businesslicenseback == null ? null : businesslicenseback.trim();
    }

    public String getPms() {
        return pms;
    }

    public void setPms(String pms) {
        this.pms = pms == null ? null : pms.trim();
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible == null ? null : visible.trim();
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public Date getReasontime() {
        return reasontime;
    }

    public void setReasontime(Date reasontime) {
        this.reasontime = reasontime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public Integer getPricestate() {
        return pricestate;
    }

    public void setPricestate(Integer pricestate) {
        this.pricestate = pricestate;
    }

    public String getPricereason() {
        return pricereason;
    }

    public void setPricereason(String pricereason) {
        this.pricereason = pricereason == null ? null : pricereason.trim();
    }

    public Integer getPmsstatus() {
        return pmsstatus;
    }

    public void setPmsstatus(Integer pmsstatus) {
        this.pmsstatus = pmsstatus;
    }

    public String getPmsuser() {
        return pmsuser;
    }

    public void setPmsuser(String pmsuser) {
        this.pmsuser = pmsuser == null ? null : pmsuser.trim();
    }

    public String getIdcardfront() {
        return idcardfront;
    }

    public void setIdcardfront(String idcardfront) {
        this.idcardfront = idcardfront == null ? null : idcardfront.trim();
    }

    public String getIdcardback() {
        return idcardback;
    }

    public void setIdcardback(String idcardback) {
        this.idcardback = idcardback == null ? null : idcardback.trim();
    }

    public String getRetentiontime() {
        return retentiontime;
    }

    public void setRetentiontime(String retentiontime) {
        this.retentiontime = retentiontime == null ? null : retentiontime.trim();
    }

    public String getDefaultleavetime() {
        return defaultleavetime;
    }

    public void setDefaultleavetime(String defaultleavetime) {
        this.defaultleavetime = defaultleavetime == null ? null : defaultleavetime.trim();
    }

    public String getHotelphone() {
        return hotelphone;
    }

    public void setHotelphone(String hotelphone) {
        this.hotelphone = hotelphone == null ? null : hotelphone.trim();
    }

    public String getIsnewpms() {
        return isnewpms;
    }

    public void setIsnewpms(String isnewpms) {
        this.isnewpms = isnewpms == null ? null : isnewpms.trim();
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic == null ? null : traffic.trim();
    }

    public String getHotelpic() {
        return hotelpic;
    }

    public void setHotelpic(String hotelpic) {
        this.hotelpic = hotelpic == null ? null : hotelpic.trim();
    }

    public String getPeripheral() {
        return peripheral;
    }

    public void setPeripheral(String peripheral) {
        this.peripheral = peripheral == null ? null : peripheral.trim();
    }
    
    
}