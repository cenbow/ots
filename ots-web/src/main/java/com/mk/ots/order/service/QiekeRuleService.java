package com.mk.ots.order.service;

import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.SearchConst;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.mapper.OrderMapper;
import com.mk.ots.mapper.PmsCheckinUserMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.bean.TopPmsRoomOrderQuery;
import com.mk.ots.order.dao.CheckInUserDAO;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.order.dao.PmsRoomOrderDao;
import com.mk.ots.search.service.impl.SearchService;
import com.mk.ots.utils.DistanceUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Thinkpad on 2015/11/11.
 */
@Service
public class QiekeRuleService {
    private static Logger logger = LoggerFactory.getLogger(QiekeRuleService.class);

    @Autowired
    private SearchService searchService;
    @Autowired
    private CheckInUserDAO checkInUserDAO;
    @Autowired
    private PmsCheckinUserMapper pmsCheckinUserMapper;
    @Autowired
    private OrderDAO orderDAO;
    @Autowired
    private THotelMapper tHotelMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private PmsRoomOrderDao pmsRoomOrderDao;


    /**
     * 手机号必须是第一次，入住并离店的订单
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkMobile(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
    }

    /**
     * 手机唯一码必须是第一次，入住并离店的订单（若手机唯一码未取到，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkSysNo(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
    }

    /**
     * 身份证号必须是第一次，入住并离店的订单
     * （若pms_checkinuser对应的入住类型不是身份证或者没有身份证号，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkIdentityCard(OtaOrder otaOrder){
        List<String> cardIdList = pmsCheckinUserMapper.getCardId(otaOrder.getId());
        if(CollectionUtils.isEmpty(cardIdList)){
            return OtaFreqTrvEnum.CARD_ID_IS_NULL;
        }
        String cardId = "";
        for(String org : cardIdList){
            if(StringUtils.isNotEmpty(org)){
                cardId = org;
                break;
            }
        }
        if(StringUtils.isEmpty(cardId)){
            return OtaFreqTrvEnum.CARD_ID_NOT_FIRST;
        }
        Long cardCount = pmsCheckinUserMapper.getCardCountByCardId(otaOrder.getId(), cardId);
        if(cardCount > 1){
            return OtaFreqTrvEnum.CARD_ID_NOT_FIRST;
        }
        return OtaFreqTrvEnum.L1;
    }


    /**
     * 支付账号（如支付宝账号或者微信支付账号）必须是第一次（若订单为到付订单，则忽略此条）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkPayAccount(OtaOrder otaOrder) {
        return OtaFreqTrvEnum.IN_FREQUSER;
    }

    /**
     * 下单用户地址定位，其坐标必须在酒店坐标1公里内（若未获取用户坐标，则酒店无该笔拉新收益）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkUserAdders(OtaOrder otaOrder){
        THotelModel tHotelModel = tHotelMapper.selectById(otaOrder.getHotelId());
        Double userlongitude = otaOrder.getBigDecimal("userlongitude").doubleValue();
        Double userlatitude = otaOrder.getBigDecimal("userlatitude").doubleValue();
        if(userlatitude == null || userlongitude == null) {
            return OtaFreqTrvEnum.OUT_OF_RANG;
        }
        if (tHotelModel.getLatitude() == null || tHotelModel.getLongitude() == null){
            return OtaFreqTrvEnum.OUT_OF_RANG;
        }
        double distance = DistanceUtil.distance(tHotelModel.getLongitude().doubleValue(), tHotelModel.getLatitude().doubleValue(), userlongitude, userlatitude);
        if(distance < SearchConst.SEARCH_RANGE_1_KM){
            return OtaFreqTrvEnum.L1;
        }
        return OtaFreqTrvEnum.L1;
    }



    /**
     * 每个酒店每天前10个拉新订单有效，超过10个的，即使满足上述7条，酒店也不能获得更多的拉新收益（10个这个参数可以配置）
     * @param otaOrder
     * @return
     */
    public OtaFreqTrvEnum checkOrderNumberThreshold(OtaOrder otaOrder){
        return OtaFreqTrvEnum.IN_FREQUSER;
    }


    public void updateCheckInStatusInvalidReason(){
    }

    public void updateAccountAndCheckOutStatusInvalidReason(){

    }

    public void updateTopInvalidReason(){

    }

    public List<PmsRoomOrder> getTopPmsRoomOrder(TopPmsRoomOrderQuery topPmsRoomOrderQuery){
        Date yesterday = DateUtils.addDays(topPmsRoomOrderQuery.getNow(), -1);
        String yesterdayStr = DateUtils.formatDateTime(yesterday, DateUtils.FORMAT_DATE);
        String todayStr =  DateUtils.formatDateTime(yesterday, DateUtils.FORMAT_DATE);
        List<PmsRoomOrder> pmsRoomOrderList = pmsRoomOrderDao.getPmsRoomOrderByCheckInTime(yesterdayStr, todayStr,
                topPmsRoomOrderQuery.getLimitBegin(), topPmsRoomOrderQuery.getLimitEen());
        for(PmsRoomOrder pmsRoomOrder : pmsRoomOrderList){
            if(topPmsRoomOrderQuery.getPmsRoomOrderList().size() >= topPmsRoomOrderQuery.getCount()){
                return topPmsRoomOrderQuery.getPmsRoomOrderList();
            }
            BigDecimal orderId =  pmsRoomOrder.getBigDecimal("orderId");
            Integer invalidreason = pmsRoomOrder.getInt("Invalidreason");
            Date checkinTime = pmsRoomOrder.getDate("checkintime");
            Date checkouttime = pmsRoomOrder.getDate("checkouttime");
            if(invalidreason == 101){
                topPmsRoomOrderQuery.getPmsRoomOrderList().add(pmsRoomOrder);
                continue;
            }
            if(DateUtils.diffSecond(checkinTime, checkouttime) > 30 * 60){
                continue;
            }

        }
        if(topPmsRoomOrderQuery.getPmsRoomOrderList().size() >= topPmsRoomOrderQuery.getCount()){
            return topPmsRoomOrderQuery.getPmsRoomOrderList();
        }
        return getTopPmsRoomOrder(topPmsRoomOrderQuery);
    }


    public OtaFreqTrvEnum getQiekeRuleReason(OtaOrder otaOrder){
        OtaFreqTrvEnum otaFreqTrvEnum = checkMobile(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkSysNo(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkIdentityCard(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkPayAccount(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }

        otaFreqTrvEnum = checkUserAdders(otaOrder);
        if(!OtaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
            return otaFreqTrvEnum;
        }
        return OtaFreqTrvEnum.L1;
    }

    public boolean isOrderQiekeRuleCity(OtaOrder otaOrder){
        if(Constant.QIE_KE_CITY_MAP.containsKey(otaOrder.getCityCode())){
            return true;
        }else{
            return false;
        }
    }
}
