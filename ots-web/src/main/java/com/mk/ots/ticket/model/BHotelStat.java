package com.mk.ots.ticket.model;

import java.util.Date;

/**
 * Created by jjh on 15/7/29.
 */
public class BHotelStat {

    /**
     * 自增主键id
     */
    private Long id;

    /**
     * 用户编号
     */
    private Long mid;

    /**
     * 酒店编号
     */
    private Long hotelId;

    /**
     * 统计时是否有效记录：1、未统计领取；2、统计领取过了
     */
    private int statisticInvalid;

    /**
     * 订单号
     */
    private Long otaOrderId;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 记录更新时间
     */
    private Date updateTime;
    
    /**
     * 是否删除（c端操作）
     */
    private String isDelete;
    
    private Long roomTypeId;
    private String roomNo;
    private String roomTypeName;
    
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

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public int getStatisticInvalid() {
        return statisticInvalid;
    }

    public void setStatisticInvalid(int statisticInvalid) {
        this.statisticInvalid = statisticInvalid;
    }

    public Long getOtaOrderId() {
        return otaOrderId;
    }

    public void setOtaOrderId(Long otaOrderId) {
        this.otaOrderId = otaOrderId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
    
}
