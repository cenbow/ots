package com.mk.ots.bill.service;

import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.exception.HmsException;
import com.mk.ots.mapper.RoomSaleMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BillOrderService {

	private static final Logger logger = LoggerFactory.getLogger(BillOrderDAO.class);
	@Autowired
	private BillOrderDAO billOrderDAO;
	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private BillOrderLogicService billOrderLogicService;

	/**
	 * 账单明细表-每天跑 凌晨2点1分
	 * 
	 * @param request
	 * @return
	 */
	public void genBillOrders(Date begintime, String hotelid, String orderidId) {
		billOrderDAO.genBillOrders(begintime, hotelid, orderidId);
	}

	/**
	 * 特价账单-每天跑
	 * 
	 * @return
	 */
	public void createBillReport(Date beginTime, Date endTime) {
		List<Long> hotelIdList = billOrderDAO.findBillOrderHotelId(beginTime, endTime);
		if(CollectionUtils.isEmpty(hotelIdList)){
			logger.info("createBillReport hotelIdList is empty");
		}
		logger.info(String.format("createBillReport hotelIdList size is [%s]" , hotelIdList.size()+""));
		for(Long hotelId : hotelIdList){
			try {
				billOrderLogicService.createBillReportByHotelId(hotelId, beginTime, endTime);
			} catch (HmsException e) {
				logger.error("createBillReportByHotelId error", e);
				continue;
			}
		}

	}


	/**
	 * 账单汇总 月表
	 * 
	 * @param isThreshold
	 * @param request
	 * @return
	 */
	public void genBillConfirmChecks(Date begintime, String hotelid, String isThreshold) {
		billOrderDAO.genBillConfirmChecks(begintime, hotelid, isThreshold);
	}

	/**
	 * 周账单 到付贴现，预付贴现
	 * 
	 * @param request
	 * @return
	 */
	public void runtWeekClearing(Date nowTime, String hotelid) {
		List<Map<String, Object>> result = null;
		;
		if (nowTime == null) {
			result = billOrderDAO.getWeekClearing(new Date());
		} else {
			if (hotelid == null) {
				logger.info("周账单结算开始，执行日期为：{}", nowTime);
				result = billOrderDAO.getWeekClearing(nowTime);
			} else {
				logger.info("周账单结算开始，执行日期为：{}, hotelid:{}", nowTime, hotelid);
				result = billOrderDAO.getWeekClearingByHotelId(nowTime, Long.parseLong(hotelid));
			}
		}
		// 插入周结算明细
		this.billOrderDAO.insertWeekClearing(result.toArray(new Map[0]));

		if (nowTime == null || hotelid == null) {
			// 关联订单明细
			logger.info("关联明细表，执行时间：{}", nowTime);
			this.billOrderDAO.setWeekClearingRelevanceOrderDetail(nowTime, null);
			// 关联日账单明细
			logger.info("关联每天汇总表，执行时间：{}", nowTime);
			this.billOrderDAO.setWeekClearingRelevanceEveryDetail(nowTime, null);
		} else {
			// 关联订单明细
			logger.info("关联明细表，执行时间：{}, hotelid:{}", nowTime, hotelid);
			this.billOrderDAO.setWeekClearingRelevanceOrderDetail(nowTime, Long.parseLong(hotelid));
			// 关联日账单明细
			logger.info("关联每天汇总表，执行时间：{}, hotelid:{}", nowTime, hotelid);
			this.billOrderDAO.setWeekClearingRelevanceEveryDetail(nowTime, Long.parseLong(hotelid));
		}

	}

	/**
	 * 修改订单noshow 状态
	 * 
	 * @param request
	 * @return
	 */
	public void changeOrderStatusNoshow(Date nowTime) {
		if (nowTime == null) {
			billOrderDAO.changeOrderStatusNoshow(new Date());
		} else {
			billOrderDAO.changeOrderStatusNoshow(nowTime);
		}
	}

}
