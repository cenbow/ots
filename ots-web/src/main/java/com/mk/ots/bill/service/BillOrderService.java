package com.mk.ots.bill.service;

import java.math.BigDecimal;
import java.util.*;

import com.mk.ots.bill.model.BillSpecialDay;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PromoTypeEnum;
import com.mk.ots.exception.BusinessException;
import com.mk.ots.mapper.BillSpecialDayMapper;
import com.mk.ots.mapper.RoomSaleMapper;
import com.mk.ots.roomsale.model.TRoomSale;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.bill.dao.BillOrderDAO;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillOrderService {

    private static final Logger logger = LoggerFactory.getLogger(BillOrderDAO.class);
    @Autowired
    private BillOrderDAO billOrderDAO;
    @Autowired
    private BillSpecialDayMapper billSpecialDayMapper;
    @Autowired
    private RoomSaleMapper roomSaleMapper;
    /**
     * 账单明细表-每天跑 凌晨2点1分
     * @param request
     * @return
     */
    public void genBillOrders(Date begintime, String hotelid, String orderidId){
        billOrderDAO.genBillOrders(begintime, hotelid, orderidId);
    }

    /**
     * 特价账单-每天跑
     * @return
     */
    @Transactional
    public void genBillOrdersV2(Date beginTime, Date endTime){
        //查询订单数据
        List<Map> billOrderList = billOrderDAO.getBillOrderList(beginTime, endTime);
        if(CollectionUtils.isEmpty(billOrderList)){
            logger.info(String.format("genBillOrdersV2 billOrderList is empty. params beginTime[%s],endTime[%s]", beginTime, endTime));
            return;
        }
        int batchSize = 50;
        int listIndex = 0;
        List<BillSpecialDay> billSpecialDayList = new ArrayList<>();
        for (Map billOrderMap : billOrderList){
            listIndex++;
            Long orderId = (Long) billOrderMap.get("orderId");
            //根据订单查询订单金额
            Map financeOrder =  billOrderDAO.getFinanceOrder(orderId);
            if(financeOrder.isEmpty()){
                logger.info(String.format("genBillOrdersV2 financeOrder is empty. params orderId[%s]", orderId));
                continue;
            }
            //判断在b_bill_special_day中是否存在
            try {
                BillSpecialDay billSpecialDay = convertBillSpecialDay(beginTime,endTime,billOrderMap, financeOrder);
                billSpecialDayList.add(billSpecialDay);
            }catch (Exception e){
                logger.info("convertBillSpecialDay Exception",e);
                e.printStackTrace();
                continue;
            }
            //将数据insert到b_bill_special_day
            if(listIndex % batchSize == 0 || billOrderList.size() == listIndex){
                Map params = new HashMap();
                params.put("billSpecialDayList", billSpecialDayList);
                billSpecialDayMapper.insertBillSpecialDayBatch(params);
                logger.info(String.format("genBillOrdersV2 insertBillSpecialDayBatch. params listIndex[%s]", listIndex));
                billSpecialDayList.clear();
            }
        }
    }

    private BillSpecialDay convertBillSpecialDay(Date beginTime, Date endTime,Map billOrderMap, Map financeOrder) {
        BillSpecialDay billSpecialDay = new BillSpecialDay();
        billSpecialDay.setBeginTime(beginTime);
        billSpecialDay.setEndTime(endTime);
        billSpecialDay.setPromoType(Long.valueOf(PromoTypeEnum.TJ.getCode()));
        Long hotelId = (Long)billOrderMap.get("hotelId");
        billSpecialDay.setHotelId(hotelId);
        Long orderId = (Long)billOrderMap.get("orderId");
        billSpecialDay.setOrderId(orderId);
        BigDecimal onlinePaied = (BigDecimal)financeOrder.get("onlinePaied");
        billSpecialDay.setOnlinePaied(onlinePaied);
        Integer payType = (Integer)financeOrder.get("payType");
        BigDecimal aliPaied = new BigDecimal(0);
        BigDecimal wechatPaied = new BigDecimal(0);
        if(PPayInfoOtherTypeEnum.alipay.getId() == payType){
            aliPaied = onlinePaied;
        }else{
            wechatPaied = onlinePaied;
        }
        billSpecialDay.setAliPaied(aliPaied);
        billSpecialDay.setWechatPaied(wechatPaied);
        BigDecimal lezhuCoins = (BigDecimal)billOrderMap.get("lezhuCoins");
        billSpecialDay.setBillCost(lezhuCoins);
        billSpecialDay.setChangeCost(new BigDecimal(0));
        billSpecialDay.setFinalCost(lezhuCoins);
        TRoomSale queryBean = new TRoomSale();
        queryBean.setHotelId(hotelId.intValue());
        Long roomTypeId = (Long)billOrderMap.get("roomTypeId");
        queryBean.setRoomTypeId(roomTypeId.intValue());
        TRoomSale tRooSmale = roomSaleMapper.queryRoomSaleByOriginal(queryBean);
        if(tRooSmale == null ||tRooSmale.getId() == null){
            throw new RuntimeException(String.format("TRoomSale is null params hotelId[%s],roomTypeId[%s]", hotelId, roomTypeId));
        }
        BigDecimal mikePrice = new BigDecimal(tRooSmale.getCostPrice());
        BigDecimal income = mikePrice.subtract(lezhuCoins);
        billSpecialDay.setIncome(income);
        BigDecimal availableMoney = (BigDecimal)financeOrder.get("availablemoney");
        billSpecialDay.setAvailableMoney(availableMoney);
        billSpecialDay.setFinanceStatus(1L);
        billSpecialDay.setCreateTime(new Date());
        return billSpecialDay;
    }

    /**
     * 账单汇总 月表
     * @param isThreshold
     * @param request
     * @return
     */
    public void genBillConfirmChecks(Date begintime, String hotelid, String isThreshold){
        billOrderDAO.genBillConfirmChecks(begintime, hotelid, isThreshold);
    }

    /**
     * 周账单 到付贴现，预付贴现
     * @param request
     * @return
     */
    public void runtWeekClearing(Date nowTime,String hotelid){
        List<Map<String, Object>> result=null;;
        if(nowTime==null){
            result=billOrderDAO.getWeekClearing(new Date());
        }else{
            if(hotelid==null){
                logger.info("周账单结算开始，执行日期为：{}", nowTime);
                result=billOrderDAO.getWeekClearing(nowTime);
            }else{
                logger.info("周账单结算开始，执行日期为：{}, hotelid:{}", nowTime, hotelid);
                result=billOrderDAO.getWeekClearingByHotelId(nowTime,Long.parseLong(hotelid));
            }
        }
        //插入周结算明细
        this.billOrderDAO.insertWeekClearing(result.toArray(new Map[0]));

        if(nowTime==null||hotelid==null){
            //关联订单明细
            logger.info("关联明细表，执行时间：{}", nowTime);
            this.billOrderDAO.setWeekClearingRelevanceOrderDetail(nowTime,null);
            //关联日账单明细
            logger.info("关联每天汇总表，执行时间：{}", nowTime);
            this.billOrderDAO.setWeekClearingRelevanceEveryDetail(nowTime,null);
        }else{
            //关联订单明细
            logger.info("关联明细表，执行时间：{}, hotelid:{}", nowTime, hotelid);
            this.billOrderDAO.setWeekClearingRelevanceOrderDetail(nowTime,Long.parseLong(hotelid));
            //关联日账单明细
            logger.info("关联每天汇总表，执行时间：{}, hotelid:{}", nowTime, hotelid);
            this.billOrderDAO.setWeekClearingRelevanceEveryDetail(nowTime,Long.parseLong(hotelid));
        }

    }

    /**
     * 修改订单noshow 状态
     * @param request
     * @return
     */
    public void changeOrderStatusNoshow(Date nowTime) {
        if(nowTime==null){
            billOrderDAO.changeOrderStatusNoshow(new Date());
        }else{
            billOrderDAO.changeOrderStatusNoshow(nowTime);
        }
    }

}
