package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="pms_error", pkey="id")
public class PmsError extends BizModel<PmsError> {
	
	private static final long serialVersionUID = 1L;
    public static final PmsError dao = new PmsError();
    
}
