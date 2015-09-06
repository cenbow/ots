package com.mk.ots.hotel.bean;

import java.io.Serializable;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.orm.plugin.bean.Db;

@Component
@DbTable(name="e_hotel", pkey="id")
public class EHotel extends BizModel<EHotel> implements Serializable {
	
	public static final EHotel dao = new EHotel();
	public static final String tableName = "e_hotel";

	private static final long serialVersionUID = 3318371186540021645L;
	public String getHotelName(){
		return get("hotelname");
	}
	public String getCityCode(){
		String disId = String.valueOf(get("disId"));
		Bean d = Db.findFirst("select c.Code code from t_city c, t_district d where d.CityID = c.cityid and d.id=?", disId);
		return d.getStr("code");
	}
//
////@DbClassMapping("e_hotel")
//public class EHotel implements Serializable{
//
//	/**
//	 * 
//	 */
//	//所有
//	public static final String ALL="all";
//	//PMS未绑定
//	public static final String NO_PMS="noPms";
//	//未提交
//	public static final String NO_COMMIT ="noCommit";
//	//已上线
//	public static final String ONLINE="online";
//	//等待审核
//	public static final String AUDIT="audit";
//
////	@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
//	private Long id;
////	@DbFieldMapping(dbColName = "hotelName")
//	private String hotelName;
////	@DbFieldMapping(dbColName = "hotelContactName")
//	private String hotelContactName;
////	@DbFieldMapping(dbColName = "regTime",saveBigintByDate=true)
//	private Date regTime;
////	@DbFieldMapping(dbColName = "disId" ,ORMappingType = ORMappingType.MANY_TO_ONE, relateClass = TDistrict.class)
//	private TDistrict dis;
////	@DbFieldMapping(dbColName = "detailAddr")
//	private String detailAddr;
////	@DbFieldMapping(dbColName = "longitude")
//	private BigDecimal longitude;
////	@DbFieldMapping(dbColName = "latitude")
//	private BigDecimal latitude;
////	@DbFieldMapping(dbColName = "openTime",saveBigintByDate=true)
//	private Date openTime;
////	@DbFieldMapping(dbColName = "repairTime",saveBigintByDate=true)
//	private Date repairTime;
////	@DbFieldMapping(dbColName = "roomNum")
//	private Integer roomNum;
////	@DbFieldMapping(dbColName = "introduction")
//	private String introduction;
////	@DbFieldMapping(dbColName = "traffic")
//	private String traffic;
////	@DbFieldMapping(dbColName = "hotelpic")
//	private String hotelpic;
////	@DbFieldMapping(dbColName = "peripheral")
//	private String peripheral;
////	@DbFieldMapping(dbColName = "businessLicenseFront")
//	private String businessLicenseFront;
////	@DbFieldMapping(dbColName = "businessLicenseBack")
//	private String businessLicenseBack;
////	@DbFieldMapping(dbColName = "idCardFront")
//	private String idCardFront;
////	@DbFieldMapping(dbColName = "idCardBack")
//	private String idCardBack;
////	@DbFieldMapping(dbColName = "pms")
//	private String pms;
////	@DbFieldMapping(dbColName = "visible")
//	private Boolean visible;
////	@DbFieldMapping(dbColName = "state")
//	private Integer state;
////	@DbFieldMapping(dbColName = "reason")
//	private String reason;
////	@DbFieldMapping(dbColName = "reasonTime",saveBigintByDate=true)
//	private Date reasonTime;
////	@DbFieldMapping(dbColName = "updateTime",saveBigintByDate=true)
//	private Date updateTime;
////	@DbFieldMapping(dbColName = "pmsUser")
//	private String pmsUser;
////	@DbFieldMapping(dbColName = "retentionTime")
//	private String retentionTime;
////	@DbFieldMapping(dbColName = "defaultLeaveTime")
//	private String defaultLeaveTime;
//	
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public String getHotelName() {
//		return hotelName;
//	}
//	public void setHotelName(String hotelName) {
//		this.hotelName = hotelName;
//	}
//	public String getHotelContactName() {
//		return hotelContactName;
//	}
//	public void setHotelContactName(String hotelContactName) {
//		this.hotelContactName = hotelContactName;
//	}
//	public Date getRegTime() {
//		return regTime;
//	}
//	public void setRegTime(Date regTime) {
//		this.regTime = regTime;
//	}
//	public TDistrict getDis() {
//		return dis;
//	}
//	public void setDis(TDistrict dis) {
//		this.dis = dis;
//	}
//	public String getDetailAddr() {
//		return detailAddr;
//	}
//	public void setDetailAddr(String detailAddr) {
//		this.detailAddr = detailAddr;
//	}
//	
//	public BigDecimal getLongitude() {
//		return longitude;
//	}
//	public void setLongitude(BigDecimal longitude) {
//		this.longitude = longitude;
//	}
//	public BigDecimal getLatitude() {
//		return latitude;
//	}
//	public void setLatitude(BigDecimal latitude) {
//		this.latitude = latitude;
//	}
//	public Date getOpenTime() {
//		return openTime;
//	}
//	public void setOpenTime(Date openTime) {
//		this.openTime = openTime;
//	}
//	public Date getRepairTime() {
//		return repairTime;
//	}
//	public void setRepairTime(Date repairTime) {
//		this.repairTime = repairTime;
//	}
//
//	public Integer getRoomNum() {
//		return roomNum;
//	}
//	public void setRoomNum(Integer roomNum) {
//		this.roomNum = roomNum;
//	}
//	public String getIntroduction() {
//		return introduction;
//	}
//	public void setIntroduction(String introduction) {
//		this.introduction = introduction;
//	}
//	public String getTraffic() {
//		return traffic;
//	}
//	public void setTraffic(String traffic) {
//		this.traffic = traffic;
//	}
//	public String getHotelpic() {
//		return hotelpic;
//	}
//	public void setHotelpic(String hotelpic) {
//		this.hotelpic = hotelpic;
//	}
//	public String getPeripheral() {
//		return peripheral;
//	}
//	public void setPeripheral(String peripheral) {
//		this.peripheral = peripheral;
//	}
//	public String getBusinessLicenseFront() {
//		return businessLicenseFront;
//	}
//	public void setBusinessLicenseFront(String businessLicenseFront) {
//		this.businessLicenseFront = businessLicenseFront;
//	}
//	public String getBusinessLicenseBack() {
//		return businessLicenseBack;
//	}
//	public void setBusinessLicenseBack(String businessLicenseBack) {
//		this.businessLicenseBack = businessLicenseBack;
//	}
//	public String getPms() {
//		return pms;
//	}
//	public void setPms(String pms) {
//		this.pms = pms;
//	}
//	public Boolean getVisible() {
//		return visible;
//	}
//	public void setVisible(Boolean visible) {
//		this.visible = visible;
//	}
//	public Integer getState() {
//		return state;
//	}
//	public void setState(Integer state) {
//		this.state = state;
//	}
//	public String getReason() {
//		return reason;
//	}
//	public void setReason(String reason) {
//		this.reason = reason;
//	}
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + ((id == null) ? 0 : id.hashCode());
//		return result;
//	}
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		EHotel other = (EHotel) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
//	public Date getReasonTime() {
//		return reasonTime;
//	}
//	public void setReasonTime(Date reasonTime) {
//		this.reasonTime = reasonTime;
//	}
//	public Date getUpdateTime() {
//		return updateTime;
//	}
//	public void setUpdateTime(Date updateTime) {
//		this.updateTime = updateTime;
//	}
//	public String getPmsUser() {
//		return pmsUser;
//	}
//	public void setPmsUser(String pmsUser) {
//		this.pmsUser = pmsUser;
//	}
	public Long getId() {
		return get("id");
	}

}
