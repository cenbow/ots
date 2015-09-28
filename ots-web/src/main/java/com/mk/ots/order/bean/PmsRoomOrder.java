package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_pmsroomorder", pkey="id")
public class PmsRoomOrder extends BizModel<PmsRoomOrder> {
    
    private static final long serialVersionUID = -434027864719974886L;
    public static final PmsRoomOrder dao = new PmsRoomOrder();
    
    public PmsRoomOrder saveOrUpdate(){
		if (get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
}
