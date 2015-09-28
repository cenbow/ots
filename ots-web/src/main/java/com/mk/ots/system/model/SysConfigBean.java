package com.mk.ots.system.model;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;


@Component
@DbTable(name="sy_config", pkey="skey")
public class SysConfigBean extends BizModel<SysConfigBean> {

	
	private static final long serialVersionUID = -5850639709504989654L;
	public static final SysConfigBean dao = new SysConfigBean();
	
	/**
	 * stype getter
	 * @return
	 */
	public String getStype() {
	    return get("stype", "");
	}
	/**
	 * stype setter
	 * @param stype
	 * @return
	 */
	public SysConfigBean setStype(String stype) {
	    return set("stype", stype);
	}
	
	/**
	 * skey getter
	 * @return
	 */
	public String getSkey() {
	    return get("skey", "");
	}
	/**
	 * skey setter
	 * @param skey
	 * @return
	 */
	public SysConfigBean setSkey(String skey) {
	    return set("skey", skey);
	}
	
	/**
	 * svalue getter
	 * @return
	 */
	public String getSvalue() {
	    return get("svalue", "");
	}
	/**
	 * svalue setter
	 * @param svalue
	 * @return
	 */
	public SysConfigBean setSvalue(String svalue) {
	    return set("svalue", svalue);
	}
	
}
