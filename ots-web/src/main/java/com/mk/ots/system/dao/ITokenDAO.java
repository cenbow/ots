package com.mk.ots.system.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.system.model.UToken;

public interface ITokenDAO extends BaseDao<UToken, Long>{

	UToken savaOrUpdate(UToken token);
	
	List<UToken> findTokenByMId(long mid);

	void deleteTokenByMId(long mid);

	UToken findTokenByAccessToken(String accesstoken);

	UToken findTokenByMId(long mid, TokenTypeEnum token);
}
