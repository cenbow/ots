package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

//h_qrcode
@Component
@DbTable(name="h_qrcode", pkey="id")
public class HQrCode  extends BizModel<HQrCode> {

	private static final long serialVersionUID = 3697903537080047541L;
	public static final HQrCode dao = new HQrCode();
}
