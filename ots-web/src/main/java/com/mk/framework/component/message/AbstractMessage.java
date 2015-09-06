package com.mk.framework.component.message;

import java.util.List;

import org.elasticsearch.common.base.Strings;

import com.google.common.base.Splitter;

public abstract class AbstractMessage implements ITips {
	private Long   msgid;
	private String title = "";
    private String[] receivers;
    private String url;
    private String content = "";
    private String msgtype = ITips.PUSH_TYPE_SINGLE;
    
    public ITips setReceivers(String receivers) {
    	if(!Strings.isNullOrEmpty(receivers)){
    		if(receivers.indexOf(",")!=-1){
    			List idsList= Splitter.on(',').trimResults().omitEmptyStrings().splitToList(receivers);
    	        String[] swapArray = new String[idsList.size()];
    	        idsList.toArray(swapArray);
    	        this.receivers = swapArray;
    		}else{
    			this.receivers = new String[]{ receivers };
    		}
    	}
        return this;
    }

	public ITips setUrl(String url) {
		this.url = url;
		return this;
	}

	public ITips setContent(String content) {
        this.content = content;
        return this;
    }

    public ITips setMsgtype(String msgtype) {
        this.msgtype = msgtype;
        return this;
    }
    
    public ITips setMsgId(Long msgid) {
    	this.msgid = msgid;
		return this;
	}
    
    public ITips setTitle(String title) {
        this.title = title;
        return this;
    }

    public Long getMsgid() {
		return msgid;
	}
    
	public String getTitle() {
		return title;
	}

	public String getUrl() {
			return url;
	}
	 
	public String getReceiver() {
		if(receivers!=null&&receivers.length>0){
			return receivers[0];
		}
		return "";
	}
	
	public String[] getReceivers() {
		return this.receivers;
	}

	public String getContent() {
		return content;
	}

	public String getMsgtype() {
		return msgtype;
	}

	public abstract boolean send() throws Exception;
}
