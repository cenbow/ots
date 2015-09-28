package com.mk.ots.order.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="b_otaroomprice", pkey="id")
public class OtaRoomPrice extends BizModel<OtaRoomPrice> {

	public static final OtaRoomPrice dao = new OtaRoomPrice();
	private static final long serialVersionUID = 1891178016382375144L;
}
