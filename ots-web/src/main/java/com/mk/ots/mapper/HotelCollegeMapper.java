package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import com.mk.ots.search.model.HotelCollegeModel;

public interface HotelCollegeMapper {
    public List<HotelCollegeModel> queryHotelColleges(Map<String, Object> map);
}
