package com.mk.ots.message.dao;

import com.mk.ots.message.model.BMessageWhiteList;

/**
 * 
 * @author 张亚军
 *
 */
public interface IBMessageWhiteListDao {

	/**
	 * 通过手机获取对象
	 * @param phone
	 * @return
	 */
	public BMessageWhiteList findByPhone(String phone);

}
