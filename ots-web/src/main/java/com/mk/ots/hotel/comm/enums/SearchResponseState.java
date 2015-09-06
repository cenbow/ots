package com.mk.ots.hotel.comm.enums;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 酒店综合查询结果状态枚举类
 * @author chuaiqing.
 *
 */
public enum SearchResponseState {
    ;
    
    private String state = "";
    private String content = "";
    
    private SearchResponseState(String state, String content) {
        this.state = state;
        this.content = content;
    }
    
    public String getState() {
        return this.state;
    }
    
    public String getContent() {
        return this.content;
    }

    @Override
    public String toString() {
        Map<String, String> msg = new HashMap<String, String>();
        msg.put(state, content);
        String str = JSONObject.toJSONString(msg);
        return str;
    }
}
