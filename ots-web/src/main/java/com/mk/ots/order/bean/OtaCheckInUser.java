package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_checkinuser", pkey="id")
public class OtaCheckInUser extends BizModel<OtaCheckInUser> {

	public static final OtaCheckInUser dao = new OtaCheckInUser();
	private static final long serialVersionUID = 1891178016382375144L;
	
	public OtaCheckInUser saveOrUpdate() {
		if (this.get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
	
	 private long  id;
	 private String  name;

	
	public void setId(Long id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public long getId(){
		return this.getLong("id");
	}
	
	public String getName() {
		return getStr("name");
	}

	
	
}
