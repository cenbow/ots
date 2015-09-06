package com.mk.ots.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.system.dao.impl.SysConfigDAO;
import com.mk.ots.system.model.SysConfigBean;

/**
 * system configuration.
 */
@Service
public class SysConfigService {
    
    private final String BASEKEY = "config_";
    private final String KEY_CHAR = "`";
    
	@Autowired
	SysConfigDAO sysConfigDAO;
	
	@Autowired
	OtsCacheManager otsCacheManager;
	
	/**
	 * 
	 * @return
	 */
	public void loadAll() {
	    List<SysConfigBean> configs;
	    Jedis jedis = otsCacheManager.getNewJedis();
	    try {
            configs = sysConfigDAO.findAll();
            for (SysConfigBean config : configs) {
                String stype = config.getStype();
                String skey = config.getSkey();
                String svalue = config.getSvalue();
                jedis.set(BASEKEY + stype + KEY_CHAR + skey, svalue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(jedis != null && jedis.isConnected()){
        		jedis.close();
        	}
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param value
	 * @throws Exception
	 */
	public void setSysConfig(String stype, String skey, String svalue) throws Exception {
		SysConfigBean bean;
        Jedis jedis = otsCacheManager.getNewJedis();
		try {
		    boolean isSucc = false;
		    bean = sysConfigDAO.findByKey(stype, skey);
	        if (bean == null) {
	            bean = new SysConfigBean();
	            bean.set("stype", stype);
	            bean.set("skey", skey);
	            bean.set("svalue", svalue);
	            isSucc = bean.save();
	        } else {
	            bean.set("svalue", svalue);
	            isSucc = bean.update();
	        }
	        if (isSucc) {
	            // 更新缓存
                jedis.set(BASEKEY + stype + KEY_CHAR + skey, svalue);
	        }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(jedis != null && jedis.isConnected()){
        		jedis.close();
        	}
		}
	}
	
	/**
	 * 
	 * @param stype
	 * @param skey
	 * @return
	 * @throws Exception
	 */
	public String getSysConfig(String stype, String skey) {
	    String svalue = "";
	    Jedis jedis = otsCacheManager.getNewJedis();
	    try {
	        svalue = String.valueOf(jedis.get(BASEKEY + stype + KEY_CHAR + skey));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	if(jedis != null && jedis.isConnected()){
        		jedis.close();
        	}
		}
	    return svalue;
	}
}
