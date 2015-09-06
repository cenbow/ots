package com.mk.ots.member.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
import com.mk.ots.common.utils.CalculateMd5;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.member.dao.IMemberDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
@Service
public class MemberService implements IMemberService{
	
	@Autowired
	private IMemberDao iMemberDao;
	
	@Override
	public void updateBaseInfo(long mid, String name, String sex, String birthday){
		iMemberDao.updateBaseInfo(mid, name, sex, birthday);
	}
	
	@Override
	public void updateBaseInfo(long mid, String name, String unionid){
		iMemberDao.updateBaseInfo(mid, name, unionid);
	}
	@Override
	public Optional<UMember> findMemberById(long mid) {
		return this.findMemberById(mid,null);
	}
	
	@Override
	public Optional<UMember> findMemberById(long mid,String state) {
		return iMemberDao.findMemberById(mid,state);
	}
	
	@Override
	public Optional<UMember> findMemberByLoginName(String loginName){
		Optional<UMember> optionalUMember = this.findMemberByLoginName(loginName,null);
		if(optionalUMember.isPresent()){
			return optionalUMember;
		}
		return Optional.absent();
	}
	
	@Override
	public Optional<UMember> findMemberByLoginName(String loginName,String state){
		return iMemberDao.findMemberByLoginName(loginName,state);
	}
	
	@Override
	public boolean checkExistByLoginName(String loginName,String state) {
		Optional<UMember> optionalMember = iMemberDao.findMemberByLoginName(loginName,state);
		if(optionalMember.isPresent()){
			return true;
		}
		return false;
	}

	@Override
	public Optional<UMember> resetPhoneNum(String phoneNum, String newPhoneNum) {
		// 1,检测用户是否存在:不存在时给出错误提示
		Optional<UMember> optionalMember = iMemberDao.findMemberByLoginName(phoneNum, UMember.NORMAL_STATE);
		if(!optionalMember.isPresent()){
			throw new MyException(MyErrorEnum.memberNotExist);
		}
		UMember member = optionalMember.get();
		member.setLoginname(newPhoneNum);
		member.setPhone(newPhoneNum);
		iMemberDao.updatePhoneNum(member.getMid(), newPhoneNum);
		return Optional.fromNullable(member);
	}
	
	@Override
	public boolean checkLoginPsdVerifyName(long mid, String name) {
		if(Strings.isNullOrEmpty(name)){
			return false;
		}
		return iMemberDao.checkLoginPsdVerifyName(mid,name);
	}
	
	@Override
	public Optional<UMember> findMemberByUnionid(String unionid) {
		return iMemberDao.findMemberByUnionid(unionid,null);
	}
	
	
	@Override
	public Optional<UMember> findMemberByUnionid(String unionid,String state) {
		return iMemberDao.findMemberByUnionid(unionid,state);
	}


	@Override
	public boolean checkPayPwd(String phoneNum, String payPsd) {
		// 1,检测用户是否存在:不存在时给出错误提示
		Optional<UMember> optionalMember = iMemberDao.findMemberByLoginName(phoneNum, UMember.NORMAL_STATE);
		if(!optionalMember.isPresent()){
			throw new MyException(MyErrorEnum.memberNotExist);
		}
		UMember member = optionalMember.get(); 
		String memberPayPsd = member.getPaypassword();
		String pagePsd = CalculateMd5.caculateCF(payPsd, Constant.defaulCharset);
		if (pagePsd.equals(memberPayPsd)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean resetPayPwd(String phoneNum, String payPsd) {
		Optional<UMember> optionalMember = iMemberDao.findMemberByLoginName(phoneNum, UMember.NORMAL_STATE); 
		// 1,检测用户是否存在:不存在时给出错误提示
		if(!optionalMember.isPresent()){
			throw new MyException(MyErrorEnum.memberNotExist);
		}
		
		UMember member = optionalMember.get();
		// 2,保存新密码
		String passwordMD5 = CalculateMd5.caculateCF(payPsd, Constant.defaulCharset);
		iMemberDao.updatePayPwd(member.getMid(), passwordMD5);
		// 3,保存修改密码记录
		//TODO 记录修改密码记录 saveMemberPsdLog(member,MemberPasswordLogEnum.payPassword, passwordMD5, updateDate);
		return true;
	}

	@Override
	public Optional<UMember> findMemberByPhone(String phoneNum) {
		return iMemberDao.findMemberByLoginName(phoneNum, null);
	}

	@Override
	public void save(UMember member) {
		this.iMemberDao.save(member);
	}
	
	@Override
	public void saveOrUpdate(UMember member) {
		if(member.getMid()!=null){
			this.iMemberDao.update(member);
		}else{
			this.iMemberDao.insert(member);
		}
	}

	@Override
	public UMember regNewMember(String phone, String appversion, String ostype) {
		UMember um = new UMember();
		um.setLoginname(phone);
		um.setName(phone);
		um.setPhone(phone);
		um.setCreatetime(new Date());
		um.setEnable(UMember.NORMAL_STATE);
		this.iMemberDao.save(um);
		if(um.getMid() != null){
			return um;
		}
		return null;
	}
	
	@Override
	public boolean isExistUnionid(String unionid){
		return this.iMemberDao.findMemberByUnionid(unionid, null).isPresent();
	}
	
	@Override
	public Optional<UMember> findMember(String phone, String unionid) {
		return this.iMemberDao.findMember(phone, unionid);
	}

	@Override
	public UMember findUMemberByMId(Long mid) {
		if(mid!=null){
			Optional<UMember> op =  this.findMemberById(mid);
			if(op.isPresent()){
				return op.get();
			}
		}
		return null;
	}
}
