package com.mk.ots.system.dao.impl;
import java.util.List;

import org.springframework.stereotype.Component;
import com.mk.ots.system.model.SysConfigBean;

/**
 * 
 * @author chuaiqing.
 *
 */
@Component
public class SysConfigDAO {
	/**
	 * 
	 * @return
	 */
	public List<SysConfigBean> findAll(){
		return SysConfigBean.dao.find("select * from sy_config");
	}
	
	/**
	 * 
	 * @param stype
	 * @param skey
	 * @return
	 */
	public SysConfigBean findByKey(String stype, String skey) {
		return SysConfigBean.dao.findFirst("select * from sy_config where stype=? and skey=?", stype, skey);
	}
	
}