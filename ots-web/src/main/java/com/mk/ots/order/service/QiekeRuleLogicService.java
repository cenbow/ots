package com.mk.ots.order.service;

import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.OtaFreqTrvEnum;
import com.mk.ots.order.bean.OtaOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thinkpad on 2015/12/30.
 */
@Service
public class QiekeRuleLogicService {

    private static Logger logger = LoggerFactory.getLogger(QiekeRuleLogicService.class);

    public void getQiekeReason(OtaOrder otaOrder){
        Map<String,String> resultMap = new HashMap<>();
        OtaFreqTrvEnum qiekeTicktReason = getQiekeTicktReason(otaOrder, resultMap);

        OtaFreqTrvEnum qiekeRuleReason = getQiekeRuleReason(otaOrder, resultMap);
    }

    public OtaFreqTrvEnum getQiekeTicktReason(OtaOrder otaOrder, Map<String,String> resultMap){
        List<String> promoRuleList = getPromoList(otaOrder);
        OtaFreqTrvEnum otaFreqTrvEnum = null;
        QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
        for(String promoRule : promoRuleList){
            otaFreqTrvEnum = OtaFreqTrvEnum.getEnumById(promoRule);
            if(StringUtils.isBlank(otaFreqTrvEnum.getInvokeMethod())){
                continue;
            }
            otaFreqTrvEnum = executeQiekeRuleReason(otaOrder, qiekeRuleService, otaFreqTrvEnum.getInvokeMethod());
            resultMap.put(promoRule, otaFreqTrvEnum.getId());
            if(!otaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
                return otaFreqTrvEnum;
            }
        }
        return otaFreqTrvEnum;
    }

    public OtaFreqTrvEnum getQiekeRuleReason(OtaOrder otaOrder, Map<String,String> resultCacheMap) {
        List<String> promoRuleList = getPromoList(otaOrder);
        OtaFreqTrvEnum otaFreqTrvEnum = null;
        QiekeRuleService qiekeRuleService = AppUtils.getBean(QiekeRuleService.class);
        for(String promoRule : promoRuleList){
            otaFreqTrvEnum = OtaFreqTrvEnum.getEnumById(promoRule);
            if(StringUtils.isNotBlank(otaFreqTrvEnum.getInvokeMethod())){
              continue;
            }
            if(resultCacheMap.containsKey(resultCacheMap.get(promoRule))){
                otaFreqTrvEnum = OtaFreqTrvEnum.getEnumById(resultCacheMap.get(promoRule));
            }
            if(!otaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
                return otaFreqTrvEnum;
            }else {
                otaFreqTrvEnum = executeQiekeRuleReason(otaOrder, qiekeRuleService, otaFreqTrvEnum.getInvokeMethod());
                if(!otaFreqTrvEnum.L1.getId().equals(otaFreqTrvEnum.getId())){
                    return otaFreqTrvEnum;
                }
            }
        }
        return otaFreqTrvEnum;
    }

    public OtaFreqTrvEnum executeQiekeRuleReason(OtaOrder otaOrder, QiekeRuleService qiekeRuleService, String methodName){
        OtaFreqTrvEnum otaFreqTrvEnum = OtaFreqTrvEnum.L1;
        Class cls = qiekeRuleService.getClass();
        Method declaredMethod = null;
        try {
            declaredMethod = cls.getDeclaredMethod(methodName, OtaOrder.class);
        } catch (NoSuchMethodException e) {
            logger.error("QiekeRuleLogicService NoSuchMethodException", e);
            return otaFreqTrvEnum.L1;
        }
        try {
            otaFreqTrvEnum = (OtaFreqTrvEnum) declaredMethod.invoke(qiekeRuleService, otaOrder);
        } catch (IllegalAccessException e) {
            logger.error("QiekeRuleLogicService IllegalAccessException", e);
            return otaFreqTrvEnum.L1;
        } catch (InvocationTargetException e) {
            logger.error("QiekeRuleLogicService InvocationTargetException", e);
            return otaFreqTrvEnum.L1;
        }
        return otaFreqTrvEnum;
    }

    public List<String> getPromoList(OtaOrder otaOrder){
        ArrayList promoList = new ArrayList();
        promoList.add(OtaFreqTrvEnum.CARD_ID_IS_NOT_PMS_SCAN.getId());
        return promoList;
    }
}
