package com.mk.pms.room.bean;

import java.util.Date;

public class RoomLockPo {
    private Long id;

    private Long pmsorderid;

    private String roomjson;

    private Date createtime;

    private Date updatetime;
    
    private Date beginTime;
	private Date endTime;
	private String status;

    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(Date beginTime) {
		this.beginTime = beginTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPmsorderid() {
        return pmsorderid;
    }

    public void setPmsorderid(Long pmsorderid) {
        this.pmsorderid = pmsorderid;
    }

    public String getRoomjson() {
        return roomjson;
    }

    public void setRoomjson(String roomjson) {
        this.roomjson = roomjson == null ? null : roomjson.trim();
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

	@Override
	public String toString() {
		return "RoomLockPo [id=" + id + ", pmsorderid=" + pmsorderid + ", roomjson=" + roomjson + ", createtime="
				+ createtime + ", updatetime=" + updatetime + ", beginTime=" + beginTime + ", endTime=" + endTime
				+ ", status=" + status + "]";
	}
    
}