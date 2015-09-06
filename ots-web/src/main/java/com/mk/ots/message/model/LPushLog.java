package com.mk.ots.message.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mk.framework.util.MessageTypeSerializer;
import com.mk.framework.util.TFBooleanSerializer;

/**
 * push实体
 * @author nolan
 *
 */
public class LPushLog {
    /**
     * 
     */
	@JsonProperty("msgid")
    private Long id;

    /**
     * 
     */
	@JsonProperty("title")
    private String title;

    /**
     * 
     */
	@JsonProperty("text")
    private String content;
    
    // 必填(1-用户消息；2-广播消息)
	@JsonSerialize(using=MessageTypeSerializer.class)
	@JsonProperty("msgtype")
    private MessageType type;

    /**
     * 
     */
	@JsonFormat(pattern="yyyyMMddHHmmss")
	@JsonProperty("msgtime")
    private Date time;

    /**
     * 
     */
	@JsonSerialize(using=TFBooleanSerializer.class)
    @JsonProperty("msgstatus")
    private Boolean readstatus;

    /**
     * 
     */
	@JsonIgnore
    private Long mid;

    /**
     * 
     */
	@JsonIgnore
    private String phone;

    /**
     * 
     */
	@JsonIgnore
    private String deviceno;

    /**
     * 
     */
	@JsonIgnore
    private String devicetype;

    /**
     * 
     */
	@JsonIgnore
    private String fromsource;

    /**
     * 
     */
	@JsonIgnore
    private String fromip;
	
    @JsonProperty("url")
	private String url;
    
    @JsonIgnore
    private Long activeid;

    private Boolean success;
    
    private String pushid;
    
    private String groupid;
    
    public String getGroupid() {
		return groupid;
	}

	public void setGroupid(String groupid) {
		this.groupid = groupid;
	}

	public String getPushid() {
		return pushid;
	}

	public void setPushid(String pushid) {
		this.pushid = pushid;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getMid() {
        return mid;
    }

    public void setMid(Long mid) {
        this.mid = mid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getDeviceno() {
        return deviceno;
    }

    public void setDeviceno(String deviceno) {
        this.deviceno = deviceno == null ? null : deviceno.trim();
    }

    public String getDevicetype() {
        return devicetype;
    }

    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype == null ? null : devicetype.trim();
    }

    public String getFromsource() {
        return fromsource;
    }

    public void setFromsource(String fromsource) {
        this.fromsource = fromsource == null ? null : fromsource.trim();
    }

    public String getFromip() {
        return fromip;
    }

    public void setFromip(String fromip) {
        this.fromip = fromip == null ? null : fromip.trim();
    }

	public Boolean getReadstatus() {
		return readstatus;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}

	public void setReadstatus(Boolean readstatus) {
		this.readstatus = readstatus;
	}



	@Override
	public String toString() {
		return "LPushLog [id=" + id + ", title=" + title + ", content="
				+ content + ", type=" + type + ", time=" + time
				+ ", readstatus=" + readstatus + ", mid=" + mid + ", phone="
				+ phone + ", deviceno=" + deviceno + ", devicetype="
				+ devicetype + ", fromsource=" + fromsource + ", fromip="
				+ fromip + ", url=" + url + ", activeid=" + activeid
				+ ", success=" + success + ", pushid=" + pushid + ", groupid="
				+ groupid + "]";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getActiveid() {
		return activeid;
	}

	public void setActiveid(Long activeid) {
		this.activeid = activeid;
	}
	
	
}