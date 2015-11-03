package com.mk.ots.bill.service;

import com.mk.ots.bill.dao.BillOrderDAO;
import com.mk.ots.bill.model.BillSpecial;
import com.mk.ots.bill.model.BillSpecialDay;
import com.mk.ots.bill.model.BillSpecialDetail;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PromoTypeEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.exception.HmsException;
import com.mk.ots.mapper.BillSpecialDayMapper;
import com.mk.ots.mapper.BillSpecialDetailMapper;
import com.mk.ots.mapper.BillSpecialMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.roomsale.model.TRoomSale;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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

    @Transactional
    public void createBillReportByHotelId(Long hotelId, Date beginTime, Date endTime) throws HmsException {
        logger.info(String.format("createBillReportByHotelId by hotelId[%s]",hotelId));
        int billSpecialId = insertHotelId(hotelId);
        // 查询订单数据
        List<Map> billOrderList = billOrderDAO.getBillOrderList(hotelId, beginTime, endTime);
        if (CollectionUtils.isEmpty(billOrderList)) {
            logger.info(String.format("createBillReportByHotelId billOrderList is empty. params hotelId[%s],beginTime[%s],endTime[%s]",
                    hotelId, beginTime, endTime));
            return;
        }
        createBillSpecialDetailList(billOrderList, billSpecialId);
        //执行update b_bill_special
        String billTime = DateUtils.formatDateTime(beginTime, DateUtils.FORMATSHORTDATETIME);
        billSpecialMapper.updateBillSpecial(hotelId, beginTime, endTime, billTime);
    }

    public int insertHotelId(Long hotelId){
        BillSpecial record = new BillSpecial();
        record.setHotelid(hotelId);
        return billSpecialMapper.insertHotelId(record);
    }

    private void createBillSpecialDetailList(List<Map> billOrderList, int billSpecialId) {
        int batchSize = 50;
        int listIndex = 0;
        List<BillSpecialDetail> billSpecialDetailsList = new ArrayList<>();
        for (Map billOrderMap : billOrderList) {
            listIndex++;
            Long orderId = (Long) billOrderMap.get("orderId");
            // 根据订单查询订单金额
            Map financeOrder = billOrderDAO.getFinanceOrder(orderId);
            if (financeOrder.isEmpty()) {
                logger.info(String.format("createBillReportByHotelId financeOrder is empty. params orderId[%s]", orderId));
                continue;
            }
            // 判断在b_bill_special_day中是否存在
            try {
                BillSpecialDetail billSpecialDetail = convertPromoDetails(Long.valueOf(billSpecialId), billOrderMap, financeOrder);
                billSpecialDetailsList.add(billSpecialDetail);
            } catch (Exception e) {
                logger.warn("failed to convertPromoDetails...", e);
                continue;
            }
            // 将数据insert到b_bill_special_day
            if (listIndex % batchSize == 0 || billOrderList.size() == listIndex) {
                Map<String, Object> params = new HashMap<>();
                params.put("billSpecialDetailsList", billSpecialDetailsList);
                billSpecialDetailMapper.insertBatch(params);
                logger.info(String.format("createBillReportByHotelId createBillReportByHotelId. params listIndex[%s]", listIndex));
                billSpecialDetailsList.clear();
            }
        }
        Map<String, Object> params = new HashMap<>();
        params.put("billSpecialDetailsList", billSpecialDetailsList);
        billSpecialDetailMapper.insertBatch(params);
    }

    /**
     * 特价账单-每天跑
     *
     * @return
     */
    @Transactional
    public void genBillOrdersV2(Date beginTime, Date endTime) {
        // 查询订单数据
        List<Map> billOrderList = null;//billOrderDAO.getBillOrderList(beginTime, endTime);
        if (CollectionUtils.isEmpty(billOrderList)) {
            logger.info(String.format("genBillOrdersV2 billOrderList is empty. params beginTime[%s],endTime[%s]",
                    beginTime, endTime));
            return;
        }
        int batchSize = 50;
        int listIndex = 0;
        List<BillSpecialDay> billSpecialDayList = new ArrayList<>();
        for (Map billOrderMap : billOrderList) {
            listIndex++;
            Long orderId = (Long) billOrderMap.get("orderId");
            // 根据订单查询订单金额
            Map financeOrder = billOrderDAO.getFinanceOrder(orderId);
            if (financeOrder.isEmpty()) {
                logger.info(String.format("genBillOrdersV2 financeOrder is empty. params orderId[%s]", orderId));
                continue;
            }
            // 判断在b_bill_special_day中是否存在
            try {
                BillSpecialDay billSpecialDay = convertBillSpecialDay(beginTime, endTime, billOrderMap, financeOrder);
                billSpecialDayList.add(billSpecialDay);
            } catch (Exception e) {
                logger.info("convertBillSpecialDay Exception", e);
                e.printStackTrace();
                continue;
            }
            // 将数据insert到b_bill_special_day
            if (listIndex % batchSize == 0 || billOrderList.size() == listIndex) {
                Map params = new HashMap();
                params.put("billSpecialDayList", billSpecialDayList);
                billSpecialDayMapper.insertBillSpecialDayBatch(params);
                logger.info(
                        String.format("genBillOrdersV2 insertBillSpecialDayBatch. params listIndex[%s]", listIndex));
                billSpecialDayList.clear();
            }
        }
    }

    private BillSpecialDetail convertPromoDetails(Long billId, Map<String, Object> billOrderMap, Map<String, Object> financeOrder) {
        Long hotelId = (Long) billOrderMap.get("hotelId");
        Long roomTypeId = getMapValueToLong(billOrderMap.get("roomTypeId"));
        BillSpecialDetail specialDetail = new BillSpecialDetail();
        specialDetail.setBillid(billId);
        specialDetail.setOrderid(getMapValueToLong(billOrderMap.get("orderId")));
        specialDetail.setHotelid(getMapValueToLong(billOrderMap.get("orderId")));
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
            throw new RuntimeException(
                    String.format("TRoomSale is null params hotelId[%s],roomTypeId[%s]", hotelId, roomTypeId));
        }
        BigDecimal mikePrice = new BigDecimal(tRooSmale.getCostPrice());
        BigDecimal lezhuCoins = (BigDecimal) billOrderMap.get("lezhuCoins");
        BigDecimal discount = mikePrice.divide(new BigDecimal(tRooSmale.getSalePrice().toString()), 2, BigDecimal.ROUND_UP);

        specialDetail.setMikeprice(mikePrice);
        specialDetail.setDiscount(discount);
        specialDetail.setLezhucoins(lezhuCoins);
        specialDetail.setOrderprice(getMapValueToBigDecimal(billOrderMap.get("totalPrice")));
        specialDetail.setOrdertype(getMapValueToInteger(billOrderMap.get("orderType")));
        specialDetail.setRoomtypename((String) billOrderMap.get("roomTypeName"));
        specialDetail.setRoomno((String) billOrderMap.get("roomNo"));
        specialDetail.setPaymethod((String) billOrderMap.get("payMethod"));
        specialDetail.setUsercost(getMapValueToBigDecimal(financeOrder.get("usercost")));
        specialDetail.setAvailablemoney(getMapValueToBigDecimal(financeOrder.get("availablemoney")));
        specialDetail.setOrderupdatetime((Date) billOrderMap.get("orderUpdateTime"));
        specialDetail.setCreatetime(new Date());
        return specialDetail;
    }

    private Long getMapValueToLong(Object value){
        if(value == null){
            return 0L;
        }
        return (Long)value;
    }

    private Integer getMapValueToInteger(Object value){
        if(value == null){
            return 0;
        }
        return (Integer)value;
    }

    private BigDecimal getMapValueToBigDecimal(Object value){
        if(value == null){
            return new BigDecimal(0);
        }
        return (BigDecimal)value;
    }



    private BillSpecialDay convertBillSpecialDay(Date beginTime, Date endTime, Map billOrderMap, Map financeOrder) {
        BillSpecialDay billSpecialDay = new BillSpecialDay();
        billSpecialDay.setBeginTime(beginTime);
        billSpecialDay.setEndTime(endTime);
        billSpecialDay.setPromoType(Long.valueOf(PromoTypeEnum.TJ.getCode()));
        Long hotelId = (Long) billOrderMap.get("hotelId");
        billSpecialDay.setHotelId(hotelId);
        Long orderId = (Long) billOrderMap.get("orderId");
        billSpecialDay.setOrderId(orderId);
        BigDecimal onlinePaied = (BigDecimal) financeOrder.get("onlinePaied");
        billSpecialDay.setOnlinePaied(onlinePaied);
        Integer payType = (Integer) financeOrder.get("payType");
        BigDecimal aliPaied = new BigDecimal(0);
        BigDecimal wechatPaied = new BigDecimal(0);
        if (PPayInfoOtherTypeEnum.alipay.getId() == payType) {
            aliPaied = onlinePaied;
        } else {
            wechatPaied = onlinePaied;
        }
        billSpecialDay.setAliPaied(aliPaied);
        billSpecialDay.setWechatPaied(wechatPaied);
        BigDecimal lezhuCoins = (BigDecimal) billOrderMap.get("lezhuCoins");
        billSpecialDay.setBillCost(lezhuCoins);
        billSpecialDay.setChangeCost(new BigDecimal(0));
        billSpecialDay.setFinalCost(lezhuCoins);
        TRoomSale queryBean = new TRoomSale();
        queryBean.setHotelId(hotelId.intValue());
        String roomNo = (String) billOrderMap.get("roomNo");
        queryBean.setRoomNo(roomNo);
        TRoomSale tRooSmale = roomSaleMapper.queryRoomSaleByOriginal(queryBean);
        if (tRooSmale == null || tRooSmale.getId() == null) {
            throw new RuntimeException(
                    String.format("TRoomSale is null params hotelId[%s],roomNo[%s]", hotelId, roomNo));
        }
        BigDecimal mikePrice = new BigDecimal(tRooSmale.getCostPrice());
        BigDecimal income = mikePrice.subtract(lezhuCoins);
        billSpecialDay.setIncome(income);
        BigDecimal availableMoney = (BigDecimal) financeOrder.get("availablemoney");
        billSpecialDay.setAvailableMoney(availableMoney);
        billSpecialDay.setFinanceStatus(1L);
        billSpecialDay.setCreateTime(new Date());
        return billSpecialDay;
    }
}
