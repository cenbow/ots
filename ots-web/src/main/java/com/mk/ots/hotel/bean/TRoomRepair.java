package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
@Component
@DbTable(name="t_room_repair", pkey="id")
public class TRoomRepair extends BizModel<TRoomRepair> {

	private static final long serialVersionUID = -1607555463510135683L;
	public static final TRoomRepair dao = new TRoomRepair();
	public static final String TABLE_NAME="t_room_repair";
	
	
	public TRoomRepair saveOrUpdate(){
		if (get("id") == null) {
			this.save();
		} else {
			this.update();
		}
		return this;
	}
}
