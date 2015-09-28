package com.mk.pms.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name = "b_pms_checkinuser", pkey = "Id")
public class PmsCheckinUser extends BizModel<PmsCheckinUser> {

	private static final long serialVersionUID = -3977251084828431963L;
	
	public static final PmsCheckinUser dao = new PmsCheckinUser();
	
	public PmsCheckinUser saveOrUpdate(){
		if (get("Id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
}
