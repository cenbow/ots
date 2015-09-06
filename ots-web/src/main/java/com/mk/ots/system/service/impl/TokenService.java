package com.mk.ots.system.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.system.dao.ITokenDAO;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.ITokenService;

@Service
public class TokenService implements ITokenService {
	
	@Autowired
	private ITokenDAO tokenDAO;
	
	@Override
	public UToken savaOrUpdate(UToken token) {
		return tokenDAO.savaOrUpdate(token);
	}
	
	@Override
	public List<UToken> findTokenByMId(long mid) {
		return tokenDAO.findTokenByMId(mid);
	}
	
	public UToken findTokenByMId(long mid,TokenTypeEnum token ){
		return tokenDAO.findTokenByMId( mid,token );
	}
	
	@Override
	public void deleteTokenByMId(long mid) {
		tokenDAO.deleteTokenByMId(mid);
	}

	@Override
	public UToken findTokenByAccessToken(String accesstoken) {
		return tokenDAO.findTokenByAccessToken(accesstoken);
	}
}
