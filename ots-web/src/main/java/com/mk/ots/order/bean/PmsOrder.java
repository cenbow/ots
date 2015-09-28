package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_pmsorder", pkey="id")
public class PmsOrder extends BizModel<PmsOrder> {
    
    private static final long serialVersionUID = -434027864719974886L;
    public static final PmsOrder dao = new PmsOrder();
    
    public PmsOrder saveOrUpdate(){
		if (get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
}
