package com.mk.ots.user.service.impl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.TestUserMapper;
import com.mk.ots.user.model.TestUser;
import com.mk.ots.user.service.WhiteUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class WhiteUserServiceImpl implements WhiteUserService {

	@Autowired
	private TestUserMapper testUserMapper;
	
	@Autowired
	private OtsCacheManager cacheManager;
	
	private Gson gson = new Gson();
	
	private static String TEST_USER_KEY="TESTUSERS";
	
	@Override
	public int saveTestUser(TestUser record) {
		int i = 0;
		i = testUserMapper.insertSelective(record);
		refreshTestUserToRedis();
		return i;
	}

	@Override
	public int deleteTestUser(Long id) {
		int i = 0;
		i = testUserMapper.deleteByPrimaryKey(id);
		refreshTestUserToRedis();
		return i;
	}

	@Override
	public int updateTestUser(TestUser record) {
		int i = 0;
		i = testUserMapper.updateByPrimaryKeySelective(record);
		refreshTestUserToRedis();
		return i;
	}

	@Override
	public List<TestUser> queryTestUsers() {
		List<TestUser> list = new ArrayList<TestUser>();
		String key = TEST_USER_KEY;
		if(!cacheManager.isExistKey(key)){
			refreshTestUserToRedis();
		}
		String str = cacheManager.getbykey(key);
		if(str==null || "".equals(str)){
			return list;
		}
		list  = gson.fromJson(str, new TypeToken<List<TestUser>>(){}.getType());
		return list == null ? new ArrayList<TestUser>() : list;
	}
	
	/**
	 * 从db查白名单
	 * @return
	 */
	private List<TestUser> queryTestUsersFromDB(){
		List<TestUser> list = new ArrayList<TestUser>();
		list = testUserMapper.selectByExample(null);
		return list;
	}
	/**
	 * 刷新白名单到redis缓存
	 */
	private void refreshTestUserToRedis(){
		String key = TEST_USER_KEY;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		List<TestUser> list = queryTestUsersFromDB();
		if(list!=null && list.size()>0){
			String value = gson.toJson(list);
			cacheManager.set(key, value);
		}
	}

	@Override
	public boolean containPhone(String phone) {
		List<TestUser> list = queryTestUsers();
		for (TestUser testUser : list) {
			if (testUser.getPhone().equals(phone)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public TestUser findByPhone(String phone) {
		List<TestUser> list = queryTestUsers();
		for (TestUser testUser : list) {
			if (testUser.getPhone().equals(phone)) {
				return testUser;
			}
		}
		return null;
	}

	@Override
	public boolean containHardware(String hardware) {
		List<TestUser> list = queryTestUsers();
		for (TestUser testUser : list) {
			if (hardware.equals(testUser.getBackupfield1())) {
				return true;
			}
		}
		return false;
	}

}
