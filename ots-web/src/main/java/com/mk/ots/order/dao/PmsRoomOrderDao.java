package com.mk.ots.order.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.mk.ots.order.bean.PmsRoomOrder;

@Repository
public class PmsRoomOrderDao {
	
	public PmsRoomOrder getPmsRoomOrder(String pmsRoomOrderNo, Long hotelId){
		StringBuffer sql = new StringBuffer("select * from b_otaroomorder ox where ");
		List<Object> paras = new ArrayList<>();
		if(StringUtils.isNotBlank(pmsRoomOrderNo)){
			sql.append(" and ox.PmsRoomOrderNo = ?");
			paras.add(pmsRoomOrderNo);
		}
		if(!"".equals(hotelId) && hotelId != null){
			sql.append(" and ox.Hotelid = ?");
			paras.add(hotelId);
		}
		return PmsRoomOrder.dao.find(sql.toString(), paras).get(0);
		
	}
    
	public PmsRoomOrder getCheckInTime(Long otaorderid){
		return PmsRoomOrder.dao.findFirst("select bp.* from b_pmsroomorder bp, b_otaorder bo, b_otaroomorder bor where bo.id = ? and bo.id = bor.otaorderid and bor.hotelid = bp.hotelid and bor.pmsroomorderno = bp.pmsroomorderno", otaorderid);
	}
}
