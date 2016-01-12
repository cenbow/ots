package com.mk.ots.dynamicprice.service.impl;

import com.mk.framework.MkJedisConnectionFactory;
import com.mk.ots.dynamicprice.service.CodeTableService;
import com.mk.ots.manager.RedisCacheName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;

/**
 * Created by kirinli on 16/1/12.
 */
@Service
public class CodeTableServiceImpl implements CodeTableService{
    @Autowired
    private MkJedisConnectionFactory jedisFactory = null;

    @Override
    public BigDecimal getCriterionPriceCode(Integer oClock) {
        String redisKey = RedisCacheName.DYNAMIC_PRICE_CRITERION_PRICE_CODE + oClock;
        String value = getKey(redisKey);

        return new BigDecimal(value);
    }

    @Override
    public BigDecimal getCriterionPriceCheckInCode(Integer currentOClock, Integer oClock) {
        return null;
    }
    final private String getKey(String key){
        Jedis jedis = null;
        try {
            jedis =  jedisFactory.getJedis();
           return jedis.get(key);
        } catch (Exception e) {
            throw e;
        } finally {
            if(null!=jedis){
                jedis.close();
            }
        }
    }

}
