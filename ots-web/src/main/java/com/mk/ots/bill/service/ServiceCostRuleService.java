package com.mk.ots.bill.service;

import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.bill.dao.ServiceCostRuleDao;
import com.mk.ots.bill.enums.ServiceCostRuleTypeEnum;
import com.mk.ots.bill.enums.ServiceCostTypeEnum;
import com.mk.ots.bill.enums.ServiceQiekeTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Thinkpad on 2015/12/11.
 */
@Service
public class ServiceCostRuleService {
    @Autowired
    private ServiceCostRuleDao serviceCostRuleDao;

    public BigDecimal getServiceCostByOrderType(Date orderCreateTime, Boolean qiekeFlag, BigDecimal price, String hotelCityCode){
        return  getServiceCost(orderCreateTime, qiekeFlag, price, hotelCityCode, ServiceCostTypeEnum.ORDER_SERVICE_COST);
    }

    public BigDecimal getServiceCost(Date orderCreateTime, Boolean qiekeFlag, BigDecimal price, String hotelCityCode, ServiceCostTypeEnum businessType){
        if(StringUtils.isBlank(hotelCityCode)){
            return new BigDecimal("0");
        }
        if(businessType == null){
            return new BigDecimal("0");
        }
        Bean queryServiceCostRule = serviceCostRuleDao.getServiceCostRule(hotelCityCode, businessType.getType());
        if(queryServiceCostRule != null){
            queryServiceCostRule.getDate("");
            return new BigDecimal("0");
        }
        queryServiceCostRule = serviceCostRuleDao.getServiceCostRuleByDefault(businessType.getType());
        if(!checkTime(orderCreateTime, queryServiceCostRule)){
            return new BigDecimal("0");
        }
        Integer resultQiekeFlag = queryServiceCostRule.getInt("qieke_flag");
        if(ServiceQiekeTypeEnum.NO.getType().equals(resultQiekeFlag) && qiekeFlag){
            return new BigDecimal("0");
        }
        if(ServiceCostRuleTypeEnum.RATIO.getType() == queryServiceCostRule.getInt("rule_type")){
            if(price == null){
                return new BigDecimal("0");
            }
            BigDecimal ratio = queryServiceCostRule.getBigDecimal("ratio");
            if(ratio != null){
                return price.multiply(ratio);
            }

        }else if(ServiceCostRuleTypeEnum.FIX.getType() == queryServiceCostRule.getInt("rule_type")){
            BigDecimal serviceCost = queryServiceCostRule.getBigDecimal("service_cost");
            BigDecimal maxServiceCost = queryServiceCostRule.getBigDecimal("max_service_cost");
            if(serviceCost != null){
                if(maxServiceCost != null && serviceCost.compareTo(maxServiceCost) >0){
                    return maxServiceCost;
                }else{
                    return serviceCost;
                }
            }
        }
        return new BigDecimal("0");
    }

    private boolean checkTime(Date orderCreateTime, Bean queryServiceCostRule){
        Date beginTime = queryServiceCostRule.getDate("begin_time");
        Date endTime = queryServiceCostRule.getDate("end_time");
        if(beginTime == null){
            if(endTime != null){
                if(endTime.compareTo(orderCreateTime) > 0){
                    return true;
                }
            }else{
                return true;
            }
        }else{
            if(endTime != null){
                if(endTime.compareTo(orderCreateTime) > 0 && beginTime.compareTo(orderCreateTime) < 1){
                    return true;
                }
            }else{
                if(beginTime.compareTo(orderCreateTime) < 1){
                    return true;
                }
            }
        }
        return false;
    }



}
