package com.mk.ots.system.service;

import java.util.List;

import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.system.model.UToken;


/**
 * 系统token生成类
 * @author nolan
 *
 */
public interface ITokenService {

	/**
	 * 保存用户token
	 * @param token
	 * @return
	 */
	UToken savaOrUpdate(UToken token);

	/**
	 * 根据用户id获取token
	 * @param mid
	 * @return
	 */
	List<UToken> findTokenByMId(long mid);

	/**
	 * 根据用户id删除token
	 * @param mid
	 */
	void deleteTokenByMId(long mid);

	/**
	 * @param accesstoken
	 * @return
	 */
	UToken findTokenByAccessToken(String accesstoken);

	/**
	 * @param mid
	 * @param wx
	 * @return
	 */
	UToken findTokenByMId(long mid, TokenTypeEnum wx);

}
