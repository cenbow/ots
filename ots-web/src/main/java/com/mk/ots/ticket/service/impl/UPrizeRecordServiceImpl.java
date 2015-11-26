package com.mk.ots.ticket.service.impl;

import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.ticket.dao.IUPrizeRecordDao;
import com.mk.ots.ticket.model.UPrizeRecord;
import com.mk.ots.ticket.service.IUPrizeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UPrizeRecordServiceImpl implements IUPrizeRecordService {
	final Logger logger = LoggerFactory.getLogger(TicketService.class);

	@Autowired
	private IUPrizeRecordDao iuPrizeRecordDao;
	
	@Override
	public long selectCountByMidAndActiveIdAndOstypeAndTime(long mid,
			long activeid, List<String> ostypes, String time) {
		// TODO Auto-generated method stub
		return iuPrizeRecordDao.selectCountByMidAndActiveIdAndOstypeAndTime(mid, activeid, ostypes, time);
	}
	public List<UPrizeRecord> queryMyHistoryIsOrNotMiKePrize(long mid ,long activeid,int type,boolean isMiKe){
		return iuPrizeRecordDao.queryMyHistoryIsOrNotMiKePrize(mid, activeid,type,isMiKe);
	}
	@Override
	public boolean checkReceivePrizeByPhone(String phone, Long activeid,
											String ostype, String date) {
		// TODO Auto-generated method stub
		long count = iuPrizeRecordDao.findReceivePrizeCountByPhone(phone,activeid,ostype,date);
		return count > 0;
	}
	@Override
	public UPrizeRecord findUPrizeRecordById(Long id) {
		// TODO Auto-generated method stub
		return iuPrizeRecordDao.findUPrizeRecordById(id);
	}
	@Override
	public void updatePrizeRecordByRecordId(UPrizeRecord prizeRecord) {
		// TODO Auto-generated method stub
		iuPrizeRecordDao.saveOrUpdate(prizeRecord);
	}
	@Override
	public void updatePhoneByRecordId(Long prizerecordid, String phone,
									  Integer receivestate) {
		// TODO Auto-generated method stub
		iuPrizeRecordDao.updatePhoneByRecordId(prizerecordid, phone, receivestate);
	}
	@Override
	public List<UPrizeRecord> queryMyHistoryPrizeByUserMark(String usermark,
															long activeid, Integer state, String date) {
		// TODO Auto-generated method stub
		return iuPrizeRecordDao.queryMyHistoryPrizeByUserMark(usermark, activeid,state,date);
	}
	@Override
	public long  checkLuckChanceByUserMark(String usermark,
										   Long activeid) {
		// TODO Auto-generated method stub
		return iuPrizeRecordDao.queryRecodeByUserMark(usermark, activeid);
	}
	@Override
	public List<UPrizeRecord> findEffectivePrizeByPhone(String phone,
														Long activeid, String ostype, String date, Integer geted) {
		// TODO Auto-generated method stub
		return iuPrizeRecordDao.findEffectivePrizeByPhone(phone,activeid,ostype, DateUtils.getDate(),geted);
	}
}
