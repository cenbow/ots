package com.mk.pms.order.service;

import org.springframework.stereotype.Service;

import com.mk.pms.bean.PmsCost;

@Service
public class PmsCostService {

	public PmsCost findPmsCost(Long otaOrderId){
		PmsCost cost = PmsCost.dao.findFirst("SELECT \n" +
									"    pc.*\n" +
									"FROM\n" +
									"    b_pmscost pc,\n" +
									"    b_otaorder o,\n" +
									"    b_otaroomorder r\n" +
									"WHERE\n" +
									"    o.id = r.otaorderid\n" +
									"        AND pc.customerno = r.PMSRoomOrderNo\n" +
									"        AND pc.Hotelid = o.HotelId\n" +
									"		and o.id=?", otaOrderId);
		return cost;
	}

}
