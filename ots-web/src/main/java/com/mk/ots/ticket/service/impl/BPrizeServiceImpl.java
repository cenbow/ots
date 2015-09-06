package com.mk.ots.ticket.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.ticket.dao.BPrizeDao;
import com.mk.ots.ticket.model.BPrize;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.service.IBPrizeService;

@Service
public class BPrizeServiceImpl implements IBPrizeService{

	
	 @Autowired
	 private  BPrizeDao bPrizeDao ;

	@Override
	public List<BPrizeInfo> queryMyMaterialPrize(BPrizeInfo prizeInfo) {
		// TODO Auto-generated method stub
		
		BPrize bPrize = bPrizeDao.findPrizeById(prizeInfo.getId());
		 List<BPrizeInfo> bPrizeInfoList = new ArrayList<BPrizeInfo>();
			if (bPrize!=null) {
				BPrizeInfo bPrizeInfo =new BPrizeInfo();
				bPrizeInfo.setId(prizeInfo.getPrizeRecordId());
				bPrizeInfo.setType(bPrize.getType());
				bPrizeInfo.setName(bPrize.getName());
				bPrizeInfo.setPrice(0L);
				bPrizeInfo.setUrl("");
				bPrizeInfo.setCode("");
				bPrizeInfo.setBegintime("");
				bPrizeInfo.setEndtime("");
				bPrizeInfo.setCreatetime(prizeInfo.getCreatetime());
				bPrizeInfoList.add(bPrizeInfo);
			}else {
				throw MyErrorEnum.customError.getMyException("根据实物id未能查到数据");
			}
			return bPrizeInfoList;
		
	}
	 
	
	
}
