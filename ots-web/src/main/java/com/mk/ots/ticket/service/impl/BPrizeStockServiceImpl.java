package com.mk.ots.ticket.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.ticket.dao.BPrizeStockDao;
import com.mk.ots.ticket.model.BPrizeInfo;
import com.mk.ots.ticket.model.BPrizeStock;
import com.mk.ots.ticket.service.IBPrizeStockService;

@Service
public class BPrizeStockServiceImpl implements IBPrizeStockService{

	
	 @Autowired
	 private  BPrizeStockDao bPrizeStockDao ;
	 
	@Override
	public List<BPrizeInfo> queryMyThirdpartyPrize(BPrizeInfo prizeInfo) {
		// TODO Auto-generated method stub
		
		BPrizeStock bPrizeStock = bPrizeStockDao.findBPrizeStockById(prizeInfo.getId());
		 List<BPrizeInfo> bPrizeInfoList = new ArrayList<BPrizeInfo>();
	        
			if (bPrizeStock!=null) {
				BPrizeInfo bPrizeInfo =new BPrizeInfo();
				bPrizeInfo.setId(prizeInfo.getPrizeRecordId());
				bPrizeInfo.setType(bPrizeStock.getbPrize().getType());
				bPrizeInfo.setName(bPrizeStock.getbPrize().getName());
				bPrizeInfo.setPrice(bPrizeStock.getbPrize().getPrice());
				bPrizeInfo.setUrl(bPrizeStock.getbPrize().getUrl());
				bPrizeInfo.setCode(bPrizeStock.getCode());
				bPrizeInfo.setBegintime(DateUtils.formatDateTime(bPrizeStock.getBegintime()));
				bPrizeInfo.setEndtime(DateUtils.formatDateTime(bPrizeStock.getEndtime()));
				bPrizeInfo.setCreatetime(prizeInfo.getCreatetime());
				bPrizeInfoList.add(bPrizeInfo);
			}else {
				throw MyErrorEnum.customError.getMyException("根据奖品库存id未能查到数据");
			}
			return bPrizeInfoList;
		
	}

	
}
