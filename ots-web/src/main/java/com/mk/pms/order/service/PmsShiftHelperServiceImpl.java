package com.mk.pms.order.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.mk.ots.mapper.TRoomMapper;

public class PmsShiftHelperServiceImpl implements PmsShiftHelperService {
	@Autowired
	private TRoomMapper roomMapper;
	
	private static final Logger logger = LoggerFactory.getLogger(PmsShiftServiceImpl.class);
	
	public boolean isOtaOrder(String pmsRoomOrderNo) throws Exception
	{
		if (StringUtils.isNotBlank(pmsRoomOrderNo)) {
			/**
			 * checks if this order comes from ota
			 */
			Map<String, Object> orderParameters = new HashMap<String, Object>();
			orderParameters.put("pmsroomorderno", pmsRoomOrderNo);

			List<Map<String, Object>> orderResponse = roomMapper.selectOtaRoomOrder(orderParameters);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("succeed in selectOtaRoomOrder with pmsroomorderno:%s; orderResponse:%s",
						pmsRoomOrderNo, orderResponse != null ? orderResponse.size() : 0));
			}

			if (orderResponse != null && orderResponse.size() > 0) {
				return false;
			}
		}
		
		return true;
	}
}
