package com.mk.ots.bill.dao;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.bill.model.ServiceCostRule;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;
import com.mk.orm.plugin.bean.Bean;

import java.util.List;

/**
 * Created by Thinkpad on 2015/12/11.
 */
@Repository
public class ServiceCostRuleDao extends MyBatisDaoImpl<ServiceCostRule, Long>{

    public Bean getServiceCostRule(String hotelCityCode, Integer businessType){
        String sql = "select  *  from  t_service_cost_rule  where bussiness_type = ? and hotel_city_code =?  and  valid =1 order by id desc limit 1";
        List<Bean> bedList = Db.find(sql,businessType, hotelCityCode);
        if(CollectionUtils.isEmpty(bedList)){
            return null;
        }
        return  bedList.get(0);
    }

    public Bean getServiceCostRuleByDefault(Integer businessType){
        String sql = " select  * from  t_service_cost_rule  where bussiness_type = ? and is_default = 1 and  valid =1 order by id desc  limit 1 ";
        List<Bean> bedList = Db.find(sql, businessType);
        if(CollectionUtils.isEmpty(bedList)){
            return null;
        }
        return  bedList.get(0);
    }
}
