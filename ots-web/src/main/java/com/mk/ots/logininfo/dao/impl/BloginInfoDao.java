package com.mk.ots.logininfo.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.logininfo.dao.IBloginInfoDao;
import com.mk.ots.logininfo.model.BLoginInfo;
import org.springframework.stereotype.Component;
@Component
public class BloginInfoDao  extends MyBatisDaoImpl<BLoginInfo, String> implements IBloginInfoDao{

	@Override
	public void save(BLoginInfo bLoginInfo) {
		String primarykey = this.insert(bLoginInfo);
		System.out.println("gen key: "+ primarykey);
	}

}
