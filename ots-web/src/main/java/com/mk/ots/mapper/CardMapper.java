/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.card.model.BCard;

import java.util.Map;


public interface CardMapper {
	public BCard findActivatedByPwd(String pwd);

	public void updateStatusById(Map<String,Object> param);
}
