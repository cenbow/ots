package com.mk.ots.member.dao;

import java.util.List;

import com.google.common.base.Optional;
import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.model.FirstOrderModel;

public interface IMemberDao extends BaseDao<UMember, String> {

	public abstract void updatePhoneNum(long mid, String phoneNum);

	public abstract void updatePayPwd(long mid, String newPwd);

	public abstract void updateBaseInfo(long mid, String name, String unionid);

	public abstract void updateBaseInfo(long mid, String name, String sex,
			String birthday);

	public abstract Optional<UMember> findMemberByLoginName(String loginName,
			String state);

	public abstract Optional<UMember> findMemberById(long mid, String state);
	
	public abstract Optional<UMember> findMember(String phone, String unionid);

	public abstract boolean checkLoginPsdVerifyName(long mid, String name);

	public abstract Optional<UMember> findMemberByUnionid(String unionid,
			String state);

	public abstract void save(UMember uMember);

	public abstract Optional<List<UMember>> findPushMember(String phone);
	
	public abstract Optional<List<UMember>> findBindMemberByGroupid(String groupid);
	
	public abstract List<UMember> findUMemberByFirstOrder(FirstOrderModel fom);

}