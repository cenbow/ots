package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
@Component
@DbTable(name="t_room", pkey="id")
public class TRoom extends BizModel<TRoom> {

	private static final long serialVersionUID = -4543979925223914056L;
	public static final TRoom dao = new TRoom();
	public static final String TABLE_NAME="t_room";
	
	public String getName() {
		return getStr("name");
	}
	
	public TRoomType getRoomType(){
		return TRoomType.dao.findById(get("roomtypeid"));
	}
}
