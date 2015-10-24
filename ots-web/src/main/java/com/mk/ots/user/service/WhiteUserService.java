package com.mk.ots.user.service;

import com.mk.ots.user.model.TestUser;

import java.util.List;

/**
 * @author he
 *
 */
public interface WhiteUserService {
	/**
	 * 向数据库和redis存储
	 */
	public int saveTestUser(TestUser record);
	/**
	 * 向数据库和redis删除
	 */
	public int deleteTestUser(Long id);
	/**
	 * 向数据库和redis更新
	 */
	public int updateTestUser(TestUser record);
	/**
	 * 从redis查询
	 */
	public List<TestUser> queryTestUsers();
	
	/**
	 * 白名单校验:检查是否包含当前phone
	 */
	public boolean containPhone(String phone);
	/**
	 * 查找手机号对应bean
	 */
	public TestUser findByPhone(String phone);
	
	/**
	 * 白名单校验：检查是否包含当前硬件编码
	 */
	public boolean containHardware(String hardware);
	
}
