package com.mk.ots.system.service.impl;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.ots.common.enums.VerifyEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.hotel.bean.VerifyRecord;
import com.mk.ots.manager.OtsCacheManager;

@Service
public class VerifyCodeService {

	public String generatePhoneVerifyCode(String phoneNum,VerifyEnum codeType) {
		StringBuilder sb = new StringBuilder();
		while(sb.length()<4){
			sb.append((int)(Math.random()*10));
		}
		String phoneVerifyCode = sb.toString();
		//验证码
		VerifyRecord verifyRecord = new VerifyRecord();
		verifyRecord.setGenerateTime(new Date());
		verifyRecord.setPhoneNum(phoneNum);
		verifyRecord.setPhoneVerifyCode(phoneVerifyCode);
		CacheVerifyCodeUtils.put(phoneNum, codeType, verifyRecord);
		return phoneVerifyCode;
	}
	
	public boolean checkPhoneVerifyCode(String phoneNum,String phoneVerifyCode,VerifyEnum codeType) {
		VerifyRecord verifyRecord =CacheVerifyCodeUtils.get(phoneNum, codeType);
		if(verifyRecord == null){
			return false;
		}
		String verifyCode = verifyRecord.getPhoneVerifyCode();
		if(verifyCode == null){
			verifyRecord.setCheck(false);
			return false;
		}
		Date generateTime = verifyRecord.getGenerateTime();
		int betweenMin = getBetweenMins(generateTime,new Date());
		String systime = SysConfig.getInstance().getSysValueByKey(Constant.messageCodeTime);
		int maxMins =  Strings.isNullOrEmpty(systime) ? 600000 : Integer.valueOf(systime);
		if(betweenMin >= maxMins){
			verifyRecord.setCheck(false);
			return false;
		}
		boolean check = phoneNum.equals(verifyRecord.getPhoneNum()) && phoneVerifyCode.equals(verifyCode)&&!verifyRecord.isCheck();
		//目前采用一次验证，所以验证之后就设置为false
		verifyRecord.setCheck(false);
		return check;
	}

	/**
	 * 计算相隔分钟数
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	private int getBetweenMins(Date beginDate, Date endDate) {
		long begin = beginDate.getTime();
		long end = endDate.getTime();
		int betweenMin = (int)(end-begin)/60000;
		return betweenMin;
	}
	
	public String generateMsgContent(String verifyCode, VerifyEnum codeEnum) {
		if(VerifyEnum.REG.equals(codeEnum)){
			return "您的眯客登录验证码为"+verifyCode+"，请于10分钟内填写。";
		}
		return "您的眯客验证码为"+verifyCode+"，请于10分钟内填写。";
	}

	static class CacheVerifyCodeUtils {
		private static Map<String,VerifyRecord> config = Maps.newConcurrentMap();
		
		private static OtsCacheManager getOTSCacheMgr(){
			return AppUtils.getBean(OtsCacheManager.class);
		}
		
		private static final String GLOBAL_VERIFY_TAG = "_GLOBAL_VERIFY_TAG_";
		
		public static void put(String phone, VerifyEnum codeType, VerifyRecord verifyRecord){
			config.put(phone+"_"+codeType.getName(), verifyRecord);
//			getOTSCacheMgr().setExpires(GLOBAL_VERIFY_TAG, phone+"_"+codeType.getName(), verifyRecord, (int)TimeUnit.MINUTES.toSeconds(10));
//			getOTSCacheMgr().expires(GLOBAL_VERIFY_TAG, , ,);
		} 
		
		public static VerifyRecord get(String phone, VerifyEnum codeType){
			VerifyRecord verifyRecord = (VerifyRecord) config.get(phone+"_"+codeType.getName());
			remove(phone, codeType);
			return verifyRecord;
//			return (VerifyRecord) getOTSCacheMgr().get(GLOBAL_VERIFY_TAG, phone+"_"+codeType.getName());
			//remove(phone, codeType)
		}
		
		public static void remove(String phone, VerifyEnum codeType){
			config.remove(phone+"_"+codeType.getName());
//			getOTSCacheMgr().remove(GLOBAL_VERIFY_TAG, phone+"_"+codeType.getName());
		}
	}
	
}
