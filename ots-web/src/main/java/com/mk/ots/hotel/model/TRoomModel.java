package com.mk.ots.hotel.model;

import java.math.BigDecimal;

/**
 * TRoomModel.
 * @author chuaiqing.
 *
 */
public class TRoomModel {
    private Long id;

    private Long roomtypeid;

    private String name;

    private String pms;

    private String islock;

    private String floor;

    private BigDecimal score;
    
    private String isWindow;
    
    private String roomTypeName;
    

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoomtypeid() {
        return roomtypeid;
    }

    public void setRoomtypeid(Long roomtypeid) {
        this.roomtypeid = roomtypeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPms() {
        return pms;
    }

    public void setPms(String pms) {
        this.pms = pms == null ? null : pms.trim();
    }

    public String getIslock() {
        return islock;
    }

    public void setIslock(String islock) {
        this.islock = islock == null ? null : islock.trim();
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor == null ? null : floor.trim();
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

	public String getIsWindow() {
		return isWindow;
	}

	public void setIsWindow(String isWindow) {
		this.isWindow = isWindow;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}
    
	
	
    
}