/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.card.model.BCard;
import com.mk.ots.hotel.bean.TCity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardMapper {
	public BCard findActivatedByPwd(String pwd);
}
