package com.mk.ots.price.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_baseprice", pkey="id")
public class TBasePrice extends BizModel<TBasePrice> {
    
    public static final TBasePrice dao = new TBasePrice();
    
	private static final long serialVersionUID = -4749270876982721041L;
}
