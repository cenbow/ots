package com.mk.ots.system.model;

import java.io.Serializable;
import java.util.Date;

import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.member.model.UMember;

public class UToken implements Serializable{

	private static final long serialVersionUID = 2486280468421076584L;

	public UToken(){
	}
	
	public UToken(Long id, Long mid, Date time, String accesstoken, TokenTypeEnum type, OSTypeEnum ostype) {
		this.id = id;
		this.mid = mid;
		this.time = time;
		this.accesstoken = accesstoken;
		this.type = type;
		this.ostype = ostype;
	}

	private Long id;

    private Long mid;

    private Date time;

    private String accesstoken;

    private TokenTypeEnum type;

    private OSTypeEnum ostype;
    
    public OSTypeEnum getOstype() {
		return ostype;
	}

	public void setOstype(OSTypeEnum ostype) {
		this.ostype = ostype;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken == null ? null : accesstoken.trim();
    }

    public TokenTypeEnum getType() {
        return type;
    }

    public void setType(TokenTypeEnum type) {
        this.type = type;
    }
}
