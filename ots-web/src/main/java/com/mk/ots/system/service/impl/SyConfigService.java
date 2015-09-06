package com.mk.ots.system.service.impl;

import org.elasticsearch.common.base.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.system.dao.ISyConfigDao;
import com.mk.ots.system.dao.impl.ISyConfigService;

@Service
public class SyConfigService implements ISyConfigService {

    private final String BASEKEY = "config_";
    private final String KEY_CHAR = "`";
    
	@Autowired
	private ISyConfigDao iSyConfigDao;
	
	@Autowired
	private OtsCacheManager cacheManager;
	
	
	@Override
	public String findValue(String key, String type){
		String rtnValue = String.valueOf(cacheManager.get(BASEKEY, skey(key,type)));
		if(Strings.isNullOrEmpty(rtnValue) || "null".equalsIgnoreCase(rtnValue)){
			rtnValue = this.iSyConfigDao.findByKeyAndType(key, type);
			cacheManager.put(BASEKEY, skey(key,type), rtnValue);
		}
		return rtnValue;
	}
	
	@Override
	public void update(String key, String type, String value){
		this.iSyConfigDao.updateByKeyAndType(key, type, value);
		cacheManager.remove(BASEKEY, skey(key,type));
		findValue(key, type); //TODO 修改配置后重新load一下,待日后重构系统工具类
	}
	
	private String skey(String key, String type){
		return type + KEY_CHAR + key;
	}
}
