package com.mk.framework.component.cache.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import redis.clients.jedis.Jedis;

import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.web.ServiceOutput;


@Controller
@RequestMapping(value="/cache")
public class CacheController {
	
	@Autowired
	OtsCacheManager cacheManager;
	
	@RequestMapping(value="/remove")
    public void remove(String cachename, String key) {
		cacheManager.remove(cachename, key);
    }
	@RequestMapping(value="/del")
	public void del(String key) {
		//keyStr="saveCustomerNo_"+hotelId+"_"+customNo.get("customerno")+"_"+keyStr;
		cacheManager.del(key);
    }
	
	@RequestMapping(value="/update")
	public void update(String cachename, String key, Object value) {
		cacheManager.put(cachename, key, value);
	}
	
	@RequestMapping(value="/find")
	public ResponseEntity<String> find(String cacheName,String key){
		Object value=cacheManager.get(cacheName, key);
		return new ResponseEntity<String>(value.toString(),HttpStatus.OK);
	}
	
	@RequestMapping(value="/findAll")
	public ResponseEntity<Map<String, Object>> findAll(String cacheName,String keyStr){
	    Map<String, Object> resultMap = new HashMap<String, Object>();
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
		return new ResponseEntity<Map<String, Object>>(resultMap,HttpStatus.OK);
	}
}
