package com.mk.ots.mapper;

import com.mk.ots.recommend.model.TRecommendposition;

public interface TRecommendpositionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TRecommendposition record);

    int insertSelective(TRecommendposition record);

    TRecommendposition selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TRecommendposition record);

    int updateByPrimaryKey(TRecommendposition record);
}