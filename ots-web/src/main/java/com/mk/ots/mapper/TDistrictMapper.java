package com.mk.ots.mapper;

import org.springframework.stereotype.Repository;

import com.mk.ots.hotel.model.TDistrictModel;

/**
 * TDistrictMapper.
 * @author chuaiqing.
 *
 */
@Repository
public interface TDistrictMapper {
    TDistrictModel selectByPrimaryKey(Long id);
}