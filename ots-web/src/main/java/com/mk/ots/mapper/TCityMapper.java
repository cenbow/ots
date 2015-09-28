package com.mk.ots.mapper;

import org.springframework.stereotype.Repository;

import com.mk.ots.hotel.model.TCityModel;

/**
 * TCityMapper
 * @author chuaiqing.
 *
 */
@Repository
public interface TCityMapper {
    TCityModel selectByPrimaryKey(Long cityid);
    
    TCityModel selectByCode(String citycode);
}