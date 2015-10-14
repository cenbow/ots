/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.card.model.BCard;


public interface CardMapper {
	public BCard findActivatedByPwd(String pwd);
}
