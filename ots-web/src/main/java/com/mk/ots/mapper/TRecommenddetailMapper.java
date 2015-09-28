package com.mk.ots.mapper;

import com.mk.ots.recommend.model.TRecommenddetail;

public interface TRecommenddetailMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TRecommenddetail record);

    int insertSelective(TRecommenddetail record);

    TRecommenddetail selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TRecommenddetail record);

    int updateByPrimaryKeyWithBLOBs(TRecommenddetail record);

    int updateByPrimaryKey(TRecommenddetail record);
    
    TRecommenddetail selectByRecommendid(Long recommendid);
    
}