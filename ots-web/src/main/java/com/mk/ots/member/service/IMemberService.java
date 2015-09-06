package com.mk.ots.member.service;

import java.util.Date;

import com.google.common.base.Optional;
import com.mk.ots.member.model.UMember;



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
}
