package com.mk.ots.wallet.service.impl;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.common.enums.HotelPromoEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.wallet.dao.ITBackMoneyRuleDao;
import com.mk.ots.wallet.enums.BackMoneyTypeEnum;
import com.mk.ots.wallet.service.ITBackMoneyRuleService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by jeashi on 2015/12/10.
 */
@Service
public class TBackMoneyRuleServiceImpl implements ITBackMoneyRuleService {

    private static Logger logger = LoggerFactory.getLogger(WalletCashflowService.class);

    @Autowired
    private ITBackMoneyRuleDao tBackMoneyRuleDao;

    @Autowired
    private HotelDAO hotelDAO = null;


    public BigDecimal getBackMoneyByOrder(OtaOrder order) {
        if (null == order) {
            throw MyErrorEnum.errorParm.getMyException("订单不存在.");
        }
        if(OrderTypeEnum.PT.getId() == order.getOrderType()){
            return new BigDecimal(0);
        }
        if(order.getTotalPrice().compareTo(order.getAvailableMoney()) == 0){
            return new BigDecimal(0);
        }
        Long thotelId = order.getHotelId();
        Bean beanHotel = hotelDAO.findThotelByHotelid(thotelId);
        if (null == beanHotel) {
            throw MyErrorEnum.errorParm.getMyException("酒店不存在.");
        }
        Integer cityCode = beanHotel.getInt("citycode");
        if (cityCode == null) {
            logger.info("酒店对应的城市为空");
            return new BigDecimal(0);
        }
        Integer bussinessType = getBusinessType(order);
        List<Bean> lsit =  tBackMoneyRuleDao.getBackMoneyByHotelCityCode(cityCode.toString(), BackMoneyTypeEnum.type_pay.getId(),bussinessType);
        if(null==lsit||lsit.size()==0){
            logger.info("酒店对应的城市未配置返现");
            return new BigDecimal(0);
        }
        BigDecimal backMoneyPer = lsit.get(0).getBigDecimal("per_money");
        if(backMoneyPer == null){
            return new BigDecimal(0);
        }
       return  backMoneyPer;
    }

    public Integer getBusinessType(OtaOrder order) {
        Integer bussinessType = 0;
        Long promoid = null;
        for (OtaRoomOrder room : order.getRoomOrderList()) {
            promoid = room.getLong("promoid");
            break;
        }
        if(promoid == null){
            bussinessType = 0;
            return bussinessType;
        }
        //一元特价房价
        if(HotelPromoEnum.OneDollar.getCode() == promoid.intValue()){
            bussinessType = HotelPromoEnum.OneDollar.getCode();
            return bussinessType;
        }

        String promotyStr = order.getPromoType();
        if(!StringUtils.isEmpty(promotyStr)){
            bussinessType = Integer.parseInt(promotyStr);
            return bussinessType;
        }
        return bussinessType;
    }
}
