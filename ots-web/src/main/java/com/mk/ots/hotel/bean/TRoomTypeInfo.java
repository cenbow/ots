package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;
@Component
@DbTable(name="t_roomtype_info", pkey="id")
public class TRoomTypeInfo extends BizModel<TRoomTypeInfo> {

	private static final long serialVersionUID = -6059032259732899304L;

	public static final String TABLE_NAME="t_roomtype_info";
	
	public static final TRoomTypeInfo dao = new TRoomTypeInfo();
}
