package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

/**
 *
 * @author shellingford
 * @version 2015年1月6日
 */
@Component
@DbTable(name="u_level", pkey="mid")
public class ULevel extends BizModel<ULevel> {
	private static final long serialVersionUID = -7156569063162247649L;


	public static final ULevel dao = new ULevel();

}
