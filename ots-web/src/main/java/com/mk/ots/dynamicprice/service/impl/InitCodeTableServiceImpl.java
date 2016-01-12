package com.mk.ots.dynamicprice.service.impl;

import com.mk.framework.MkJedisConnectionFactory;
import com.mk.ots.dynamicprice.bean.CriterionPriceCode;
import com.mk.ots.dynamicprice.service.InitCodeTableService;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.mapper.CriterionPriceCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * Created by kirinli on 16/1/12.
 */
@Service
public class InitCodeTableServiceImpl implements InitCodeTableService {
    @Autowired
    private CriterionPriceCodeMapper criterionPriceCodeMapper;
    @Autowired
    private MkJedisConnectionFactory jedisFactory = null;
    @Override
    public void initCriterionPriceCode2Redis() {
        List<CriterionPriceCode> criterionPriceCodes = criterionPriceCodeMapper.listAllCriterionPriceCode();

        for (CriterionPriceCode criterionPriceCode : criterionPriceCodes) {
            init2redis(RedisCacheName.DYNAMIC_PRICE_CRITERION_PRICE_CODE + criterionPriceCode.getoClock().toString(),
                    criterionPriceCode.getDropRatio().toString());
        }
    }

    @Override
    public void initCriterionPriceCheckInCode2Redis() {

    }

    final private void init2redis(String key, String value){
        Jedis jedis = null;
        try {
            jedis =  jedisFactory.getJedis();
            jedis.set(key,  value);
        } catch (Exception e) {
            throw e;
        } finally {
            if(null!=jedis){
                jedis.close();
            }
        }
    }

}
