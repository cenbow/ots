package com.mk.ots.hotel.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Id;

/**
 * THotelModel.
 * @author chuaiqing.
 *
 */
public class THotelModel {
    @Id
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

    private String visible;

    private String online;

    private String idcardfront;

    private String idcardback;

    private String retentiontime;

    private String defaultleavetime;

    private String needwait;

    private String hotelphone;

    private String isnewpms;
    
    private String introduction;

    private String traffic;

    private String hotelpic;

    private String peripheral;
    
    private Long cityid;
    
    private String citycode;
    
    private String province;
    
    private String cityname;
    
    private String disname;
    
    private Integer priority;
    
    private Integer rulecode; //20150731 add
    
    private Long pmsopttime;
    
    
    //jianghe add begin 住店历史
    private Long roomTypeId;
    private String roomNo;
    private String roomTypeName;
    private Date createTime;
    ////jianghe add end
    
    

    public Long getPmsopttime() {
		return pmsopttime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getRoomTypeId() {
		return roomTypeId;
	}

	public void setRoomTypeId(Long roomTypeId) {
		this.roomTypeId = roomTypeId;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public void setPmsopttime(Long pmsopttime) {
		this.pmsopttime = pmsopttime;
	}

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

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible == null ? null : visible.trim();
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online == null ? null : online.trim();
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

    public String getNeedwait() {
        return needwait;
    }

    public void setNeedwait(String needwait) {
        this.needwait = needwait == null ? null : needwait.trim();
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

    public Long getCityid() {
        return cityid;
    }

    public void setCityid(Long cityid) {
        this.cityid = cityid;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getDisname() {
        return disname;
    }

    public void setDisname(String disname) {
        this.disname = disname;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

	public Integer getRulecode() {
		return rulecode;
	}

	public void setRulecode(Integer rulecode) {
		this.rulecode = rulecode;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}
    
}