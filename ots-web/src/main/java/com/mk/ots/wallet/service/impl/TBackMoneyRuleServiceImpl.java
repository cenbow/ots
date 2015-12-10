package com.mk.ots.wallet.service.impl;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.wallet.dao.ITBackMoneyRuleDao;
import com.mk.ots.wallet.dao.IUWalletCashFlowDAO;
import com.mk.ots.wallet.enums.BackMoneyBussinessTypeEnum;
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


    public BigDecimal   getBackMoneyByOrder(OtaOrder order) {
        if (null == order) {
            throw MyErrorEnum.errorParm.getMyException("订单不存在.");
        }

        Long thotelId = order.getHotelId();
        Bean beanHotel = hotelDAO.findThotelByHotelid(thotelId);
        if (null == beanHotel) {
            throw MyErrorEnum.errorParm.getMyException("酒店不存在.");
        }

        String cityCode = beanHotel.getStr("citycode");
        if (StringUtils.isEmpty(cityCode)) {
            logger.info("酒店对应的城市为空");
            return new BigDecimal(0);
        }

        List<Bean> lsit =    tBackMoneyRuleDao.getBackMoneyByHotelCityCode(cityCode, BackMoneyTypeEnum.type_pay.getId(), BackMoneyBussinessTypeEnum.type_TJ.getId());
        if(null==lsit||lsit.size()==0){
            logger.info("酒店对应的城市未配置返现");
            return new BigDecimal(0);
        }
        String  backMoneyPerStr = lsit.get(0).getStr("per_money");
        if(StringUtils.isEmpty(backMoneyPerStr)){
            return new BigDecimal(0);
        }

        BigDecimal  backMoenyBigD = BigDecimal.valueOf(Double.parseDouble(backMoneyPerStr));
       return  backMoenyBigD;
    }
}
