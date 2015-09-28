package com.mk.ots.message.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class BMessageCopywriter {
    private Long id;

    private Integer copywriterType;

    private String copywriter;

    private Integer msgType;

    private Integer enabled;

    private Date createtime;

    private Date updatetime;

    private String createby;

    private String updateby;
    
    private String url;
    
    private String title;
    
    /**
     * 批量参数
     */
    private Map<String,String> param = new HashMap<String,String>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCopywriterType() {
        return copywriterType;
    }

    public void setCopywriterType(Integer copywriterType) {
        this.copywriterType = copywriterType;
    }

    public String getCopywriter() {
        return copywriter;
    }

    public void setCopywriter(String copywriter) {
        this.copywriter = copywriter == null ? null : copywriter.trim();
    }

    public Integer getMsgType() {
        return msgType;
    }

    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
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

    public String getCreateby() {
        return createby;
    }

    public void setCreateby(String createby) {
        this.createby = createby == null ? null : createby.trim();
    }

    public String getUpdateby() {
        return updateby;
    }

    public void setUpdateby(String updateby) {
        this.updateby = updateby == null ? null : updateby.trim();
    }

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Map getParam() {
		return param;
	}

	public void setParam(Map param) {
		this.param = param;
	}
	
	@Override
	public String toString() {
		StringBuilder sf = new StringBuilder();
		sf.append("copywriterType=").append(copywriterType).append("#");
		sf.append("copywriter=").append(copywriter).append("#");
		sf.append("msgType=").append(msgType).append("#");
		sf.append("enabled=").append(enabled).append("#");
		sf.append("createtime=").append(createtime==null?"":createtime).append("#");
		sf.append("updatetime=").append(updatetime==null?"":updatetime).append("#");
		sf.append("createby=").append(createby).append("#");
		sf.append("updateby=").append(updateby).append("#");
		sf.append("url=").append(url).append("#");
		sf.append("title=").append(title).append("#");
		
		sf.append("param=").append("{");
		if(MapUtils.isNotEmpty(param)){
			for (Map.Entry entry : param.entrySet()) {
				sf.append(entry.getKey()).append(entry.getValue()).append("#");
			}
		}
		sf.append("}");
		return sf.toString();
	}
    
    
}