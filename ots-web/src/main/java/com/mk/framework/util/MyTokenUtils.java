package com.mk.framework.util;

import java.util.UUID;

import org.elasticsearch.common.base.Strings;

import com.google.common.base.Optional;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.TokenTypeEnum;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.system.model.UToken;
import com.mk.ots.system.service.ITokenService;

/**
 * 用户token获取工具类
 * @author nolan
 *
 */
public class MyTokenUtils {

	
	private static String TOKEN_GLOBAL = "_GLOBAL_TOKEN_";
	
	private static OtsCacheManager getManager() {
		return AppUtils.getBean(OtsCacheManager.class);
	}

	private static ITokenService getiTokenService() {
		return AppUtils.getBean(ITokenService.class);
	}

	private static IMemberService getiMemberService() {
		return  AppUtils.getBean(IMemberService.class);
	}
	
	/**
	 * 根据token获取具体明细
	 * @param accessToken
	 * @return
	 */
	public static UToken getToken(String accessToken){
		if (Strings.isNullOrEmpty(accessToken)) {
			accessToken = (String) ThreadUtil.threadVar("token");
		}
		UToken uToken = get(accessToken);
		if(uToken == null){
			uToken = getiTokenService().findTokenByAccessToken(accessToken);
			if(uToken != null){
				put(accessToken, uToken);
			}
		}
		return uToken;
	}
	
	/**
	 * 根据token获取用户信息
	 * @param accessToken
	 * @return
	 */
	public static UMember getMemberByToken(String accessToken){
		Long mid = getMidByToken(accessToken);
		if(mid != null){
			Optional<UMember> op =  getiMemberService().findMemberById(mid);
			if(op.isPresent()){
				return op.get();
			}
		}
		return null;
	}
	
	/**
	 * 根据token获取mid
	 * @param accessToken
	 * @return
	 */
	public static Long getMidByToken(String accessToken){
		UToken ut = getToken(accessToken);
		if(ut != null){
			return ut.getMid();
		}
		return null;
	}
	
	
	/**
	 * 根据用户信息生成token
	 * @param member
	 * @return
	 */
	public static UToken genAndCacheUToken(long mid, TokenTypeEnum tokenTypeEnum, OSTypeEnum osType){
		UToken ut  = new UToken();
		ut.setMid(mid);
		ut.setAccesstoken(UUID.randomUUID().toString());
		ut.setType(tokenTypeEnum);
		ut.setOstype(osType);
		getiTokenService().savaOrUpdate(ut);
		if(ut.getId() != null){
			put(ut.getAccesstoken(), ut);
			return ut;
		}
		return null;
	}
	
	/**
	 * @param accessToken
	 * @return
	 */
	public static UToken removeToken(String accessToken){
		UToken ut = getToken(accessToken);
		if(ut != null){
			getiTokenService().deleteTokenByMId(ut.getMid()); //清除数据库缓存
		}
		remove(accessToken); //清除redis缓存
		return ut;
	}
	
	

	/**
	 * 存储token到缓存中
	 * @param accessToken
	 * @param myToken
	 */
	protected static void put(String accessToken, UToken myToken){
		getManager().put(TOKEN_GLOBAL, accessToken, myToken);
	}
	
	/**
	 * @param accessToken
	 */
	protected static void remove(String accessToken){
		getManager().remove(TOKEN_GLOBAL, accessToken);
	}
	
	/**
	 * @param accessToken
	 * @param myToken
	 * @return
	 */
	protected static UToken get(String accessToken){
		return (UToken) getManager().get(TOKEN_GLOBAL, accessToken);
	}
}
