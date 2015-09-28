package com.mk.ots.common.utils;

import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.google.common.collect.Maps;

/**
 * OTS版本类.
 * @author chuaiqing.
 *
 */
public final class OtsVersion {
    /** OTS服务平台版本 */
    public static final String OTS_VER = "v2.0";
    /** 产品内部发布版本 */
    public static final String INNER_VER = "mike3.0";
    
    /**
     * 
     */
    public Map<String, String> getVersionInfo() {
        Map<String, String> versionInfo = Maps.newHashMap();
        try {
            versionInfo.put("OTS版本", OtsVersion.OTS_VER);
            versionInfo.put("内部版本", OtsVersion.INNER_VER);
        } catch (Exception e) {
            versionInfo.put("errors", e.getLocalizedMessage());
        }
        return versionInfo;
    }
    
}
