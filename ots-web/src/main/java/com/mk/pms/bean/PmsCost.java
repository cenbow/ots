package com.mk.pms.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

/** 
 * 
 * @author shellingford
 * @version 2014年12月22日
 */
@Component
@DbTable(name="b_PmsCost", pkey="id")
public class PmsCost extends BizModel<PmsCost> {
    
    public static final PmsCost dao = new PmsCost();

	/**
	 * 
	 */
	private static final long serialVersionUID = -517043056210340049L;
	
	public PmsCost saveOrUpdate(){
		if (get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
	
	public void setHotelId(Long hotelid){
		set("hotelid", hotelid);
	}
	public void setHotelPms(String hotelid){
		set("hotelpms", hotelid);
	}
	public void setRoomCostNo(String hotelid){
		set("roomcostno", hotelid);
	}
//	@DbFieldMapping(dbColName = "id", isPrimaryKey = true, isAutoPrimaryKey=true)
//	private Long id;
//	@DbFieldMapping(dbColName = "hotelId")
//	private Long hotelId;
//	@DbFieldMapping(dbColName = "hotelPms")
//	private String hotelPms;
//	@DbFieldMapping(dbColName = "roomCostNo")
//	private String roomCostNo;
//	@DbFieldMapping(dbColName = "costtime",saveBigintByDate=true)
//	private Date costtime;
//	@DbFieldMapping(dbColName = "costType")
//	private PmsCostTypeEnum costType;
//	@DbFieldMapping(dbColName = "source")
//	private PmsCostSourceEnum source;
//	@DbFieldMapping(dbColName = "roomCost")
//	private BigDecimal roomCost;
//	@DbFieldMapping(dbColName = "otherCost")
//	private BigDecimal otherCost;
//	@DbFieldMapping(dbColName = "optime",saveBigintByDate=true)
//	private Date optime;
//	@DbFieldMapping(dbColName = "opuser")
//	private String opuser;
//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
//	public Long getHotelId() {
//		return hotelId;
//	}
//	public void setHotelId(Long hotelId) {
//		this.hotelId = hotelId;
//	}
//	public String getHotelPms() {
//		return hotelPms;
//	}
//	public void setHotelPms(String hotelPms) {
//		this.hotelPms = hotelPms;
//	}
//	public String getRoomCostNo() {
//		return roomCostNo;
//	}
//	public void setRoomCostNo(String roomCostNo) {
//		this.roomCostNo = roomCostNo;
//	}
//	public Date getCosttime() {
//		return costtime;
//	}
//	public void setCosttime(Date costtime) {
//		this.costtime = costtime;
//	}
//	public PmsCostTypeEnum getCostType() {
//		return costType;
//	}
//	public void setCostType(PmsCostTypeEnum costType) {
//		this.costType = costType;
//	}
//	public PmsCostSourceEnum getSource() {
//		return source;
//	}
//	public void setSource(PmsCostSourceEnum source) {
//		this.source = source;
//	}
//	public BigDecimal getRoomCost() {
//		return roomCost;
//	}
//	public void setRoomCost(BigDecimal roomCost) {
//		this.roomCost = roomCost;
//	}
//	public BigDecimal getOtherCost() {
//		return otherCost;
//	}
//	public void setOtherCost(BigDecimal otherCost) {
//		this.otherCost = otherCost;
//	}
//	public Date getOptime() {
//		return optime;
//	}
//	public void setOptime(Date optime) {
//		this.optime = optime;
//	}
//	public String getOpuser() {
//		return opuser;
//	}
//	public void setOpuser(String opuser) {
//		this.opuser = opuser;
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
//		PmsCost other = (PmsCost) obj;
//		if (id == null) {
//			if (other.id != null)
//				return false;
//		} else if (!id.equals(other.id))
//			return false;
//		return true;
//	}
	public void setCustomerno(String customerno) {
		set("customerno", customerno);
	}
	public void setCosttime(Date time) {
		set("Costtime", time);
	}
	public void setCostType(String costType) {
		set("CostType", costType);
	}
	public void setOpuser(String opuser) {
		set("Opuser", opuser);
	}
	public void setRoomCost(BigDecimal roomcost) {
		set("RoomCost",roomcost);
	}
	public void setOtherCost(BigDecimal price) {
		set("OtherCost", price);
	}
	public void setSource(String contSource) {
		set("Source", contSource);
	}

}
