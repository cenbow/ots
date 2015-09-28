package com.mk.pms.hotel.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mk.ots.common.utils.DateUtils;
import com.mk.pms.bean.PmsCheckinUser;

@Service
public class PmsCheckinUserService {

	private static Logger logger = LoggerFactory.getLogger(PmsCheckinUserService.class);
	
	public PmsCheckinUser saveOrUpdatePmsInCheckUser(PmsCheckinUser pmsCheckinUser) {
		logger.info("PmsCheckinUserService::PmsCheckinUser::saveOrUpdatePmsInCheckUser"+pmsCheckinUser.getAttrs());
		PmsCheckinUser oldCheckinUser = findPmsUserIncheckSelect(pmsCheckinUser.getLong("Hotelid"), pmsCheckinUser.getStr("PmsRoomOrderNo"), pmsCheckinUser.getStr("cardid"));
		if (oldCheckinUser == null) {
			pmsCheckinUser.set("createtime", DateUtils.createDate());
			pmsCheckinUser.save();
		} else {
			pmsCheckinUser.set("id", oldCheckinUser.getLong("id"));
			pmsCheckinUser.set("updatetime", DateUtils.createDate());
			pmsCheckinUser.update();
		}
		logger.info("PmsCheckinUserService::PmsCheckinUser::saveOrUpdatePmsInCheckUser::end");
		return pmsCheckinUser;
	}
	
	public PmsCheckinUser findPmsUserIncheckSelect(Long hotelid,String pmsRoomNo,String cardid) {
		return PmsCheckinUser.dao.findFirst("select * from b_pms_checkinuser where hotelid=? and pmsroomorderno=? and cardid =?", hotelid, pmsRoomNo, cardid);
	}
}
