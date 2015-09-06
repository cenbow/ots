package com.mk.ots.system.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.system.dao.ITokenDAO;
import com.mk.ots.system.model.UToken;
 
@Component
public class TokenDAO extends MyBatisDaoImpl<UToken, Long> implements ITokenDAO{

	@Override
	public List<UToken> findTokenByMId(long mid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid",mid);
		return this.find("findTokenByMId",param);
	}
	
	@Override
	public UToken findTokenByMId(long mid, TokenTypeEnum token) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid",mid);
		param.put("tokentype",token);
		return this.findOne("findTokenByMid",param);
	}

	@Override
	public void deleteTokenByMId(long mid) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("mid",mid);
		this.delete("deleteByMid",param);
	}

	@Override
	public UToken findTokenByAccessToken(String accesstoken) {
		Map<String, Object> param = Maps.newHashMap();
		param.put("accesstoken",accesstoken);
		return this.findOne("findTokenByAccessToken",param);
	}

	@Override
	public UToken savaOrUpdate(UToken uToken) {
		if(uToken.getId()!=null){
			this.update(uToken);
		}else{
			this.insert(uToken);
		}
		return uToken;
	}
}
