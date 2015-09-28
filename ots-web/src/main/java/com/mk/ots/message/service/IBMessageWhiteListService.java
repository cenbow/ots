package com.mk.ots.message.service;

import com.mk.ots.message.model.BMessageWhiteList;

/**
 * 
 * @author 张亚军
 *
 */
public interface IBMessageWhiteListService {

	/**
	 * 通过手机获取对象
	 * @param phone
	 * @return
	 */
	public BMessageWhiteList findByPhone(String phone);

}
