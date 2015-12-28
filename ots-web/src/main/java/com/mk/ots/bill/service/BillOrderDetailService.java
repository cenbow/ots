package com.mk.ots.bill.service;

import com.mk.ots.bill.domain.BillOrder;
import com.mk.ots.bill.domain.BillOrderPayInfo;
import com.mk.ots.bill.enums.BillOrderCheckStatusEnum;
import com.mk.ots.bill.enums.BillOrderFreezeEnum;
import com.mk.ots.bill.model.BillOrderDetail;
import com.mk.ots.bill.model.BillOrderWeek;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PromoTypeEnum;
import com.mk.ots.common.enums.QieKeTypeEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.TCityModel;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.mapper.BillOrderDetailMapper;
import com.mk.ots.mapper.BillOrderWeekMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Thinkpad on 2015/12/24.
 */
@Service
public class BillOrderDetailService {
    private static final Logger logger = LoggerFactory.getLogger(BillOrderDetailService.class);

    @Autowired
    private BillOrderDetailMapper billOrderDetailMapper;
    @Autowired
    private BillOrderWeekMapper billOrderWeekMapper;
    @Autowired
    private ServiceCostRuleService serviceCostRuleService;
    @Autowired
    private CityService cityService;


    public void genOrderDetail(Date billDate){
        if(billDate == null){
            logger.info("genOrderDetail billDate is null");
            return;
        }
        //查询订单信息
        Date billBeginDate = null;
        Date billEndDate = null;
        try {
            billBeginDate = DateUtils.parseDate(DateUtils.formatDateTime(billDate, DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
            billEndDate = DateUtils.parseDate(DateUtils.formatDateTime(DateUtils.addDays(billDate, 1), DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
        } catch (ParseException e) {
            logger.error("genOrderDetail get bill date exception" , e);
        }
        int pageSize = 1000;
        int pageIndex = 0;
        genOrderDetail(billBeginDate, billEndDate, pageSize, pageIndex);
    }


    public void genOrderDetailWeek(Date billDate){
        if(billDate == null){
            logger.info("genOrderDetail billDate is null");
            return;
        }
        //查询订单信息
        Date billBeginDate = null;
        Date billEndDate = null;
        try {
            billBeginDate = DateUtils.parseDate(DateUtils.formatDateTime(DateUtils.addDays(billDate, -7), DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
            billEndDate = DateUtils.parseDate(DateUtils.formatDateTime(billDate, DateUtils.FORMAT_DATE), DateUtils.FORMAT_DATE) ;
        } catch (ParseException e) {
            logger.error("genOrderDetail get bill date exception" , e);
        }
        genOrderDetailWeek(billBeginDate, billEndDate);
    }

    public void genOrderDetailWeek(Date billBeginDate, Date billEndDate){
        List<BillOrderWeek> billOrderWeekList = billOrderWeekMapper.sumBillOrderWeekList(billBeginDate, billEndDate);
        if(CollectionUtils.isEmpty(billOrderWeekList)){
            logger.info(String.format("genOrderDetailWeek billOrderWeekList is empty,params billBeginDate[%s],billEndDate[%s]",
                    billBeginDate, billEndDate));
            return;
        }
        createBillOrderWeekList(billOrderWeekList, billBeginDate, billEndDate);
        updateOrderWeekId(billOrderWeekList, billBeginDate, billEndDate);
    }

    private void updateOrderWeekId(List<BillOrderWeek> billOrderWeekList, Date billBeginDate, Date billEndDate) {
        for(BillOrderWeek billOrderWeek : billOrderWeekList){
            billOrderDetailMapper.updateOrderWeekId(billOrderWeek.getHotelId(), billOrderWeek.getCityCode(), billBeginDate, billEndDate);
        }
    }


    public void createBillOrderWeekList(List<BillOrderWeek> billOrderWeekList, Date billBeginDate, Date billEndDate){
        billOrderWeekList = buildOrderWeekList(billOrderWeekList, billBeginDate, billEndDate);
        if(CollectionUtils.isEmpty(billOrderWeekList)){
            logger.info(String.format("genOrderDetail createBillOrderWeekList is empty,params billBeginDate[%s],billEndDate[%s]",
                    billBeginDate, billEndDate));
            return;
        }
        billOrderWeekMapper.insertBatch(billOrderWeekList);
    }


    private List<BillOrderWeek> buildOrderWeekList(List<BillOrderWeek> billOrderWeekList, Date billBeginDate, Date billEndDate) {
        if(CollectionUtils.isEmpty(billOrderWeekList)){
            return null;
        }
        for(BillOrderWeek billOrderWeek : billOrderWeekList){
            buildOrderWeek(billOrderWeek, billBeginDate, billEndDate);
        }
        return billOrderWeekList;
    }

    private BillOrderWeek buildOrderWeek(BillOrderWeek billOrderWeek, Date billBeginDate, Date billEndDate) {
        billOrderWeek.setBeginTime(billBeginDate);
        billOrderWeek.setEndTime(billEndDate);
        Date now = new Date();
        billOrderWeek.setCreateTime(now);
        billOrderWeek.setUpdateTime(now);
        //账单金额=（如果是特价订单用酒店结算价格否则是用户实际支付金额+用户券+红包金额+预付贴现金额+到付贴现金额+补差金额-总服务费）
        BigDecimal billCost = BigDecimal.ZERO;
        billCost = billCost.add(billOrderWeek.getSettlementPrice() == null ? BigDecimal.ZERO : billOrderWeek.getSettlementPrice());
        billCost = billCost.add(billOrderWeek.getPrepaymentDiscount() == null ? BigDecimal.ZERO : billOrderWeek.getPrepaymentDiscount());
        billCost = billCost.add(billOrderWeek.getToPayDiscount() == null ? BigDecimal.ZERO : billOrderWeek.getToPayDiscount());
        billCost = billCost.subtract(billOrderWeek.getServiceCost() == null ? BigDecimal.ZERO : billOrderWeek.getServiceCost());
        billOrderWeek.setBillCost(billCost);
        //商家收款金额=账单金额+补差金额
        BigDecimal hotelCost = BigDecimal.ZERO;
        hotelCost = hotelCost.add(billCost);
        hotelCost = hotelCost.add(billOrderWeek.getChangeCost() == null ? BigDecimal.ZERO : billOrderWeek.getChangeCost());
        billOrderWeek.setHotelCost(hotelCost);
        billOrderWeek.setIsFreeze(BillOrderFreezeEnum.NO.getCode().toString());
        billOrderWeek.setCheckStatus(BillOrderCheckStatusEnum.INIT.getCode());
        return billOrderWeek;
    }

    public void genOrderDetail(Date billBeginDate, Date billEndDate, int pageSize, int pageIndex){
        List<BillOrder> billOrderList= billOrderDetailMapper.getBillOrderList(billBeginDate, billEndDate, pageSize, pageIndex);
        if(CollectionUtils.isEmpty(billOrderList)){
            logger.info(String.format("genOrderDetail otaOrderList is empty,params billBeginDate[%s],billEndDate[%s],pageSize[%d],pageIndex[%d]",
                    billBeginDate, billEndDate, pageSize, pageIndex));
            return;
        }
        createBillOrderDetailList(billOrderList, billBeginDate);
        if(CollectionUtils.isNotEmpty(billOrderList) && billOrderList.size() == pageSize){
            pageIndex = (pageIndex + 1) * pageSize;
            genOrderDetail(billBeginDate, billEndDate, pageSize, pageIndex);
        }
    }

    public void createBillOrderDetailList(List<BillOrder> billOrderList, Date billBeginDate){
        List<BillOrderDetail> buildBillOrderDetailList = buildOrderDetailList(billOrderList, billBeginDate);
        if(CollectionUtils.isEmpty(buildBillOrderDetailList)){
            logger.info(String.format("genOrderDetail buildBillOrderDetailList is empty,params otaOrderList size[%d]", billOrderList.size()));
            return;
        }
        insertBatch(buildBillOrderDetailList);
    }

    public void insertBatch(List<BillOrderDetail> buildBillOrderDetailList){
        billOrderDetailMapper.insertBatch(buildBillOrderDetailList);
    }


    private List<BillOrderDetail> buildOrderDetailList(List<BillOrder> billOrderList, Date billBeginDate) {
        List<BillOrderDetail> billOrderDetailList = new ArrayList<>();
        if(CollectionUtils.isEmpty(billOrderList)){
            return null;
        }
        for(BillOrder billOrder : billOrderList){
            BillOrderDetail billOrderDetail = buildOrderDetail(billOrder, billBeginDate);
            billOrderDetailList.add(billOrderDetail);
        }
        return billOrderDetailList;
    }

    public BillOrderDetail buildOrderDetail(BillOrder billOrder, Date billBeginDate){
        billOrder.setBillTime(billBeginDate);
        BillOrderPayInfo billOrderPayInfo = billOrderDetailMapper.getOrderPayInfo(billOrder.getOrderId());
        QieKeTypeEnum qieKeTypeEnum = getOrderCheckQiekeType(billOrder.getRuleCode(), billOrder.getSpreadUser(), billOrder.getInvalidReason());

        //商户金额减去商户补贴
        BigDecimal qiekeIncome = null;
        Boolean qikeFlag = false;
        if(qieKeTypeEnum.LAXIN_RULE.getCode().equals(qieKeTypeEnum.getCode())){
            qiekeIncome = billOrderPayInfo.getQiekeIncome();
            qikeFlag = true;
        }else if(qieKeTypeEnum.B_RULE.getCode().equals(qieKeTypeEnum.getCode())){
            qiekeIncome = billOrderPayInfo.getQiekeIncome();
            qikeFlag = true;
        }
        BigDecimal price = billOrder.getTotalPrice().subtract(billOrderPayInfo.getHotelgive() == null ? BigDecimal.ZERO : billOrderPayInfo.getHotelgive());
        BigDecimal serviceCost = serviceCostRuleService.getServiceCostByOrderType(billOrder.getOrderCreateTime(), qikeFlag, price, billOrder.getCityCode());
        billOrder.setServiceCost(serviceCost);


        billOrder.setUserCost(billOrderPayInfo.getUsercost());
        if(PPayInfoOtherTypeEnum.alipay.getId() ==  billOrderPayInfo.getOnlinePayType()){
            billOrder.setAliPayMoney(billOrderPayInfo.getUsercost());
        }else if(PPayInfoOtherTypeEnum.wechatpay.getId() ==  billOrderPayInfo.getOnlinePayType() || PPayInfoOtherTypeEnum.wxpay.getId() ==  billOrderPayInfo.getOnlinePayType()){
            billOrder.setWechatPayMoney(billOrderPayInfo.getUsercost());
        }
        //判断优惠券的金额是否大于房间价格 如果优惠券的比房间价格还要大则得到实际用优惠券金额
        if(billOrder.getTicketMoney()!= null && billOrder.getTicketMoney().compareTo(BigDecimal.ZERO) > 0){
            BigDecimal checkTicketPrice = billOrder.getTotalPrice() == null ? BigDecimal.ZERO : billOrder.getTotalPrice();
            checkTicketPrice = checkTicketPrice.subtract(billOrder.getUserCost() == null ? BigDecimal.ZERO : billOrder.getUserCost());
            checkTicketPrice = checkTicketPrice.subtract(billOrder.getTicketMoney() == null ? BigDecimal.ZERO : billOrder.getTicketMoney());
            if(checkTicketPrice.compareTo(BigDecimal.ZERO) < 0){
                BigDecimal ticketMoney = billOrder.getTicketMoney().add(checkTicketPrice);
                billOrder.setTicketMoney(ticketMoney);

            }else{
                billOrder.setTicketMoney(billOrder.getTicketMoney());
            }
        }

        if(OrderTypeEnum.YF.getId() == billOrder.getOrderType().intValue()){
            billOrder.setPrepaymentDiscount(qiekeIncome);
            if(PromoTypeEnum.TJ.getCode().equals(billOrder.getPromoType())){
                billOrder.setSettlementPrice(billOrderPayInfo.getLezhu());
            }else {
                billOrder.setSettlementPrice(billOrder.getTotalPrice());
            }
        } else if(OrderTypeEnum.PT.getId() == billOrder.getOrderType().intValue()){
            billOrder.setToPayDiscount(qiekeIncome);
            billOrder.setSettlementPrice(billOrder.getTicketMoney());
        }

        TCityModel tCityModel = cityService.findCityByCode(billOrder.getCityCode());
        if(tCityModel != null){
            billOrder.setCityName(tCityModel.getCityname());
        }
        Date now = new Date();
        billOrder.setUpdateTime(now);
        billOrder.setCreateTime(now);
        return billOrder;
    }

    private QieKeTypeEnum getOrderCheckQiekeType(Integer ruleCode,Long spreadUser, Integer invalidReason) {
        if(spreadUser != null && spreadUser.intValue() == Constant.QIE_KE_SPREAD_USER  && invalidReason == null){
            return  QieKeTypeEnum.LAXIN_RULE;
        }
        if(spreadUser!= null && invalidReason == null && ruleCode == 1002){
            return  QieKeTypeEnum.B_RULE;
        }
        return QieKeTypeEnum.OTHER;
    }

}
