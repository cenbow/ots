package com.mk.ots.bill.service;

import com.mk.ots.bill.domain.BillOrder;
import com.mk.ots.bill.domain.BillOrderPayInfo;
import com.mk.ots.bill.model.BillOrderDetail;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.PPayInfoOtherTypeEnum;
import com.mk.ots.common.enums.PromoTypeEnum;
import com.mk.ots.common.enums.QieKeTypeEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.model.TCityModel;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.mapper.BillOrderDetailMapper;
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
        if(OrderTypeEnum.YF.getId() == billOrder.getOrderType().intValue()){
            billOrder.setPrepaymentDiscount(qiekeIncome);
            if(PromoTypeEnum.TJ.getCode().equals(billOrder.getPromoType())){
                billOrder.setSettlementPrice(billOrder.getTotalPrice());
            }else {
                billOrder.setSettlementPrice(billOrderPayInfo.getLezhu());
            }
        } else if(OrderTypeEnum.PT.getId() == billOrder.getOrderType().intValue()){
            billOrder.setToPayDiscount(qiekeIncome);
            billOrder.setSettlementPrice(BigDecimal.ZERO);
        }

        billOrder.setUserCost(billOrder.getUserCost());
        if(PPayInfoOtherTypeEnum.alipay.getId() ==  billOrderPayInfo.getOnlinePayType()){
            billOrder.setAliPayMoney(billOrderPayInfo.getUsercost());
        }else if(PPayInfoOtherTypeEnum.wechatpay.getId() ==  billOrderPayInfo.getOnlinePayType() || PPayInfoOtherTypeEnum.wxpay.getId() ==  billOrderPayInfo.getOnlinePayType()){
            billOrder.setWechatPayMoney(billOrderPayInfo.getUsercost());
        }
        billOrder.setTicketMoney(billOrder.getTicketMoney());
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
        if(spreadUser != null && spreadUser.intValue() == Constant.QIE_KE_SPREAD_USER){
            return  QieKeTypeEnum.LAXIN_RULE;
        }
        if(spreadUser!= null && invalidReason == null && ruleCode == 1002){
            return  QieKeTypeEnum.B_RULE;
        }
        return QieKeTypeEnum.OTHER;
    }

}
