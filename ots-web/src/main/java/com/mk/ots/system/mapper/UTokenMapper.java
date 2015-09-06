package com.mk.ots.system.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.system.model.UToken;

public interface UTokenMapper {

	public UToken findTokenByAccessToken(String token);

	public List<UToken> findTokenByMId(@Param(value = "mid") long mid, @Param(value = "tokentype") TokenTypeEnum tokentype);

	public void deleteByAccessToken(String token);
	
	public void deleteByMid(long mid);

	public void save(UToken uToken);
	
	public void update(UToken uToken);
}
