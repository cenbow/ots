package com.mk.ots.member.service;

import java.util.List;

import com.google.common.base.Optional;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.model.FirstOrderModel;



/**
 * @author nolan
 *
 */
public interface IMemberService {
	
	public boolean checkPayPwd(String phoneNum, String payPsd);

	public boolean resetPayPwd(String phoneNum, String payPsd);

	public Optional<UMember> resetPhoneNum(String phoneNum, String newPhoneNum);//重置手机号 
	
	public Optional<UMember> findMember(String phone, String unionid);
	
	public Optional<UMember> findMemberById(long mid);
	
	public Optional<UMember> findMemberById(long mid, String state);
	
	public Optional<UMember> findMemberByPhone(String phoneNum);

	public Optional<UMember> findMemberByUnionid(String unionid);

	public Optional<UMember> findMemberByUnionid(String unionid, String state);

	public Optional<UMember> findMemberByLoginName(String loginName, String state);
	
	public boolean checkExistByLoginName(String loginName, String state);
	
	public boolean checkLoginPsdVerifyName(long mid, String name);

	public Optional<UMember> findMemberByLoginName(String loginName);

	public void updateBaseInfo(long mid, String name,String sex, String birthday);

	public abstract void updateBaseInfo(long mid, String name, String unionid);
	
	public void saveOrUpdate(UMember member);
	
	public void save(UMember member);
	
	public UMember regNewMember(String phone, String appversion, String ostype);

	public abstract boolean isExistUnionid(String unionid);
	
	public abstract UMember findUMemberByMId(Long mid);

	/**
	 * 判断该手机号是不是存在于黑名单中
	 * @param phone
	 * @param type
	 * @param name
	 * @param errmsg
	 */
	public void checkPhoneIsBlack(String phone,String type,String name,String errmsg);
	

	
	public abstract List<UMember> findUMemberByFirstOrder(FirstOrderModel fom);

}
