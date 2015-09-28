package com.mk.ots.ticket.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.ticket.dao.IUPrizeRecordDao;
import com.mk.ots.ticket.model.UPrizeRecord;
import com.mk.ots.ticket.service.IUPrizeRecordService;

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
}
