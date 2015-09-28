package com.mk.ots.price.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_pricetime", pkey="id")
public class TPriceTime extends BizModel<TPriceTime> {
    
    public static final TPriceTime dao = new TPriceTime();

	private static final long serialVersionUID = 7817137376584750156L;
}
