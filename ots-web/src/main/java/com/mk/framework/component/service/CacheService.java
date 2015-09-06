package com.mk.framework.component.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.web.ServiceOutput;


@Service
public class CacheService {
    
    @Autowired
    private OtsCacheManager cacheManager;
    
    public Map<String, Object> removeRedisCache(String key) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Jedis jedis = cacheManager.getNewJedis();
        try {
            if(jedis.exists(key)){
                jedis.del(key);
            }
            resultMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } finally {
            jedis.close();
        }
        return resultMap;
    }
    
    public void update(String cachename, String key, Object value) {
        cacheManager.put(cachename, key, value);
    }
    
    public Object find(String cacheName, String key) {
        Object value = cacheManager.get(cacheName, key);
        return value;
    }
    
    public Map<String, Object> findAll(String cacheName, String keyStr) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (StringUtils.isBlank(cacheName)) {
            cacheName = "";
        }
        if (StringUtils.isBlank(keyStr)) {
            keyStr = "";
        }
        Jedis jedis = cacheManager.getNewJedis();
        try {
            Set<String> ss=jedis.keys(keyStr);
            for(String key: ss){
                resultMap.put(key, jedis.get(key));     
            }
            resultMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
            resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
        } finally {
            jedis.close();
        }
        return resultMap;
    }
    
    public Map<String, Object> clearRedisCache() {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Jedis jedis = null;
        try {
            jedis = cacheManager.getNewJedis();
            Set<String> ss=jedis.keys("*");
            for(String key: ss){
                jedis.del(key);
            }
            resultMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
        } catch (Exception e) {
            resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
            resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
            resultMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
        } finally {
            if (jedis != null) {
                try {
                    jedis.close();
                } catch (Exception e2) {
                    resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
                    resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
                    resultMap.put(ServiceOutput.STR_MSG_ERRMSG, e2.getMessage());
                }
            }
        }
        return resultMap;
    }
}
