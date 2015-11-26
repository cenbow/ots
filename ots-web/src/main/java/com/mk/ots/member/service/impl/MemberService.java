package com.mk.ots.member.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.common.utils.CalculateMd5;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.member.dao.IMemberDao;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.order.common.PropertyConfigurer;
import com.mk.ots.order.model.FirstOrderModel;
import com.mk.ots.order.service.OrderUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class MemberService implements IMemberService{

	@Autowired
	private IMemberDao iMemberDao;
    @Autowired
    private OrderUtil orderUtil;
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
        return this.findMemberById(mid, "T");
    }

	@Override
	public Optional<UMember> findMemberById(long mid,String state) {
		return iMemberDao.findMemberById(mid, state);
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
		return iMemberDao.findMemberByLoginName(loginName, state);
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
		return iMemberDao.checkLoginPsdVerifyName(mid, name);
	}

	@Override
	public Optional<UMember> findMemberByUnionid(String unionid) {
		return iMemberDao.findMemberByUnionid(unionid, null);
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
	@Override
	public void checkPhoneIsBlack(String phone,String type,String name,String errmsg){
		if(StringUtils.isEmpty(phone)){
            return;
        }
		String blackSwitch = PropertyConfigurer.getProperty("blackSwitch");
		//1:开关关闭，说明不用查黑名单
		if ("1".equals(blackSwitch)) {
			return ;
		}
		Map params = new HashMap();
		params.put("phone", phone);
		JSONObject data = null;
		String url = UrlUtils.getUrl("blacklist.url");
		Transaction t = Cat.newTransaction(type, name);
		try {
			data = JSONObject.parseObject(orderUtil.doPost(url, params, 1000));// 超时1秒
			t.setStatus(Transaction.SUCCESS);
		} catch (Exception e) {
			t.setStatus(e);
		} finally {
			t.complete();
		}
		if (data != null && "T".equals(data.getString("check"))) {
			throw MyErrorEnum.customError.getMyException(errmsg);
		}
       }
	@Override
	public List<UMember> findUMemberByFirstOrder(FirstOrderModel fom) {
		return iMemberDao.findUMemberByFirstOrder(fom);
	}


	@Override
	public List<UMember> findUMemberByOpenId(String openId) {
		return this.iMemberDao.findUMemberByOpenId(openId);
	}
}