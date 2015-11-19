package com.mk.ots.member.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.member.dao.IMemberDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.model.FirstOrderModel;

@Component
public class MemberDao extends MyBatisDaoImpl<UMember, String> implements IMemberDao{
	
	@Override
	public void updatePhoneNum(long mid, String phoneNum){
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("phonenum", phoneNum);
		this.update("updatePhoneNum", param);
	}
	
	@Override
	public void updatePayPwd(long mid, String newPwd){
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("newpwd", newPwd);
		this.update("updatePayPwd", param);
	}
	
	@Override
	public void updateBaseInfo(long mid, String name, String unionid){
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("name", name);
		param.put("unionid", unionid);
		this.update("updateUnionidAndName", param);
	}
	
	@Override
	public void updateBaseInfo(long mid, String name, String sex, String birthday){
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("name", name);
		param.put("sex", sex);
		param.put("birthday", birthday);
		this.update("updateBaseInfo", param);
	}
	 
	@Override
	public Optional<UMember> findMemberByLoginName(String loginName,String state) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("loginname", loginName);
		param.put("enable", state);
		return Optional.fromNullable(this.findOne("findMemberByLoginName", param));
	}

	@Override
	public Optional<UMember> findMemberById(long mid, String state) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("enable", state);
		return Optional.fromNullable(this.findOne("findMemberById", param));
	}
	
	@Override
	public boolean checkLoginPsdVerifyName(long mid, String name) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", mid);
		param.put("name", name);
		return this.count("countLoginPsdVerifyName", param)>0;
	}
	
	@Override
	public Optional<UMember> findMemberByUnionid(String unionid,String state) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("unionid", unionid);
		param.put("enable", state);
		return Optional.fromNullable(this.findOne("findMemberByUnionid", param));
	}
	
	@Override
	public void save(UMember uMember){
		String primarykey = this.insert(uMember);
		System.out.println("gen key: " + primarykey);
	}
	
	@Override
	public Optional<UMember> findMember(String phone, String unionid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("phone", phone);
		param.put("unionid", unionid);
		return Optional.fromNullable(this.findOne("findMemberByPhoneAndUnionid", param));
	}
	
	@Override
	public Optional<List<UMember>> findPushMember(String phone) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("phone", phone);
		return Optional.fromNullable(this.find("findPushMember", param));
	}

	@Override
	public Optional<List<UMember>> findBindMemberByGroupid(String groupid) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("groupid", groupid);
		return Optional.fromNullable(this.find("findBindMemberByGroupid", param));
	}
	
	@Override
	public List<UMember> findUMemberByFirstOrder(FirstOrderModel fom) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("mid", fom.getMid());
		param.put("appid", fom.getAppid());
		param.put("idcard", fom.getIdcard());
		param.put("phone", fom.getPhone());
		param.put("deviceid", fom.getDeviceid());
		return this.find("findUMemberByFirstOrder", param);
	}

	@Override
	public List<UMember> findUMemberByOpenId(String openId) {
		Map<String,Object> param = Maps.newHashMap();
		param.put("findMemberByOpenId", openId);
		return this.find("findMemberByOpenId", param);
	}
}
