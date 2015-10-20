package com.mk.ots.mapper;

import com.mk.ots.recommend.model.TRecommendItemArea;

import java.util.List;
import java.util.Map;

public interface TRecommendItemAreaMapper {
    List<TRecommendItemArea> selectByCityId(Map<String, Object> map);
}