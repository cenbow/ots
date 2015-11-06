package com.mk.ots.mapper;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository
public interface BRPPromoDetailsMapper {
    
    public void insertBillRPPromoDetailsBatch(Map<String, Object> params);
}
