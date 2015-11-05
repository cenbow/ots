package com.mk.ots.bill.service;

import java.math.BigDecimal;
import java.util.*;

import com.mk.ots.common.enums.PayCallbackEnum;
import com.mk.ots.hotel.model.TRoomTypeModel;
import com.mk.ots.mapper.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.bill.model.BillSpecial;
import com.mk.ots.bill.model.BillSpecialDay;
import com.mk.ots.bill.model.BillSpecialDetail;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PromoTypeEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.exception.HmsException;
import com.mk.ots.roomsale.model.TRoomSale;

/**
 * Created by Thinkpad on 2015/11/3.
 */
@Service
public class BillOrderLogicService {
	private static final Logger logger = LoggerFactory.getLogger(BillOrderLogicService.class);
	@Autowired
	private BillOrderDAO billOrderDAO;
	@Autowired
	private BillSpecialDayMapper billSpecialDayMapper;
	@Autowired
	private BillSpecialMapper billSpecialMapper;
	@Autowired
	private BillSpecialDetailMapper billSpecialDetailMapper;
	@Autowired
	private RoomSaleMapper roomSaleMapper;
	@Autowired
	private TRoomTypeMapper tRoomTypeMapper;

	public void createBillReportByHotelId(Long hotelId, Date beginTime, Date endTime) throws HmsException {
		logger.info(String.format("createBillReportByHotelId by hotelId[%s]", hotelId));
		int billSpecialId = insertHotelId(hotelId);
		// 查询订单数据
		List<Map> billOrderList = billOrderDAO.getBillOrderList(hotelId, beginTime, endTime);
		if (CollectionUtils.isEmpty(billOrderList)) {
			logger.info(String.format(
					"createBillReportByHotelId billOrderList is empty. params hotelId[%s],beginTime[%s],endTime[%s]",
					hotelId, beginTime, endTime));
			return;
		}

		try {
			createBillSpecialDetailList(billOrderList, billSpecialId);
		} catch (Exception ex) {
			throw new HmsException(-1, ex);
		}

		logger.info("about to updateBillSpecial with hotelid {}", hotelId);

		// 执行update b_bill_special
		Calendar beginTimeCalendar = Calendar.getInstance();
		beginTimeCalendar.setTime(beginTime);
		Date[] d = DateUtils.getWeekStartAndEndDate(beginTimeCalendar);
		Date firstDateOfWeek = d[0], lastDateOfWeek = d[1];
		String billTime = DateUtils.getStringFromDate(firstDateOfWeek, DateUtils.FORMAT_DATE) + "-" + DateUtils.getStringFromDate(lastDateOfWeek, DateUtils.FORMAT_DATE);
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("hotelId", hotelId);
			parameters.put("beginTime", beginTime);
			parameters.put("endTime", endTime);
			parameters.put("billTime", billTime);
			
			billSpecialMapper.updateBillSpecial(parameters);
		} catch (Exception ex) {
			throw new HmsException(-1, ex);
		}
	}

	public int insertHotelId(Long hotelId) {
		BillSpecial record = new BillSpecial();
		record.setHotelid(hotelId);
		return billSpecialMapper.insertHotelId(record);
	}

	private void createBillSpecialDetailList(List<Map> billOrderList, int billSpecialId) throws Exception {
		int batchSize = 50;
		int listIndex = 0;
		List<BillSpecialDetail> billSpecialDetailsList = new ArrayList<>();
		for (Map billOrderMap : billOrderList) {
			listIndex++;
			Long orderId = (Long) billOrderMap.get("orderId");
			// 根据订单查询订单金额
			Map financeOrder = billOrderDAO.getFinanceOrder(orderId);
			if (financeOrder.isEmpty()) {
				logger.info(
						String.format("createBillReportByHotelId financeOrder is empty. params orderId[%s]", orderId));
				continue;
			}
			// 判断在b_bill_special_day中是否存在
			try {
				BillSpecialDetail billSpecialDetail = convertPromoDetails(Long.valueOf(billSpecialId), billOrderMap,
						financeOrder);
				billSpecialDetailsList.add(billSpecialDetail);
			} catch (Exception e) {
				logger.warn("failed to convertPromoDetails...", e);
				continue;
			}
			// 将数据insert到b_bill_special_day
			if (listIndex % batchSize == 0 || billOrderList.size() == listIndex) {
				Map<String, Object> params = new HashMap<>();
				params.put("billSpecialDetailsList", billSpecialDetailsList);
				try {
					billSpecialDetailMapper.insertBatch(params);
				} catch (Exception ex) {
					logger.warn("failed to insertBatch...", ex);
					continue;
				}

				logger.info(String.format("createBillReportByHotelId createBillReportByHotelId. params listIndex[%s]",
						listIndex));
				billSpecialDetailsList.clear();
			}
		}
		if (CollectionUtils.isNotEmpty(billSpecialDetailsList)) {
			Map<String, Object> params = new HashMap<>();
			params.put("billSpecialDetailsList", billSpecialDetailsList);
			billSpecialDetailMapper.insertBatch(params);
		}
	}


	private BillSpecialDetail convertPromoDetails(Long billId, Map<String, Object> billOrderMap,
			Map<String, Object> financeOrder) throws Exception {
		Long hotelId = (Long) billOrderMap.get("hotelId");
		Long roomTypeId = getMapValueToLong(billOrderMap.get("roomTypeId"));
		BillSpecialDetail specialDetail = new BillSpecialDetail();
		specialDetail.setBillid(billId);
		specialDetail.setOrderid(getMapValueToLong(billOrderMap.get("orderId")));
		specialDetail.setHotelid(hotelId);
		specialDetail.setCheckintime((Date) billOrderMap.get("checkinTime"));
		specialDetail.setCheckouttime((Date) billOrderMap.get("checkoutTime"));
		specialDetail.setBegintime((Date) billOrderMap.get("beginTime"));
		specialDetail.setEndtime((Date) billOrderMap.get("endTime"));
		specialDetail.setOrdertime((Date) billOrderMap.get("orderTime"));
		specialDetail.setDaynumber(getMapValueToInteger(billOrderMap.get("dayNumber")));

		TRoomSale queryBean = new TRoomSale();
		queryBean.setHotelId(hotelId.intValue());
		queryBean.setRoomTypeId(roomTypeId.intValue());
		TRoomSale tRooSmale = roomSaleMapper.queryRoomSaleByOriginal(queryBean);
		if (tRooSmale == null || tRooSmale.getId() == null) {
			logger.info(String.format("TRoomSale is null params hotelId[%s],roomTypeId[%s]", hotelId, roomTypeId));
			BigDecimal mikePrice = new BigDecimal("-1");
			BigDecimal discount = new BigDecimal("0");
			specialDetail.setMikeprice(mikePrice);
			specialDetail.setDiscount(discount);
		}else{
			BigDecimal mikePrice = new BigDecimal(tRooSmale.getCostPrice());
			//如果Mike价格为0的话则直接取对应T_base_price的数据
			if(mikePrice.compareTo(new BigDecimal(0)) == 0){
				TRoomTypeModel tRoomTypeModel = tRoomTypeMapper.selectByPrimaryKey(tRooSmale.getOldRoomTypeId().longValue());
				mikePrice = tRoomTypeModel.getCost();
			}
			BigDecimal lezhuCoins = (BigDecimal) billOrderMap.get("lezhuCoins");
			BigDecimal discount =lezhuCoins.divide(mikePrice, 2,
					BigDecimal.ROUND_UP);
			specialDetail.setMikeprice(mikePrice);
			specialDetail.setDiscount(discount);
		}
		BigDecimal lezhuCoins = (BigDecimal) billOrderMap.get("lezhuCoins");
		specialDetail.setLezhucoins(lezhuCoins);
		specialDetail.setOrderprice(getMapValueToBigDecimal(billOrderMap.get("totalPrice")));
		specialDetail.setOrdertype(getMapValueToInteger(billOrderMap.get("orderType")));
		specialDetail.setRoomtypename((String) billOrderMap.get("roomTypeName"));
		specialDetail.setRoomno((String) billOrderMap.get("roomNo"));

		Integer payType = (Integer) financeOrder.get("payType");
		String payMethod = "";
		if (PPayInfoOtherTypeEnum.alipay.getId() == payType) {
			payMethod = PayCallbackEnum.Ali_Callback.name();
		} else {
			payMethod = PayCallbackEnum.WeChat_Callback.name();
		}
		specialDetail.setPaymethod(payMethod);
		specialDetail.setUsercost(getMapValueToBigDecimal(financeOrder.get("usercost")));
		specialDetail.setAvailablemoney(getMapValueToBigDecimal(financeOrder.get("availablemoney")));
		specialDetail.setOrderupdatetime((Date) billOrderMap.get("orderUpdateTime"));
		specialDetail.setCreatetime(new Date());
		return specialDetail;
	}

	private Long getMapValueToLong(Object value) {
		if (value == null) {
			return 0L;
		}
		return (Long) value;
	}

	private Integer getMapValueToInteger(Object value) {
		if (value == null) {
			return 0;
		}
		return (Integer) value;
	}

	private BigDecimal getMapValueToBigDecimal(Object value) {
		if (value == null) {
			return new BigDecimal(0);
		}
		return (BigDecimal) value;
	}

}
