package com.mk.ots.hotel.bean;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

/**
 *
 * @author LYN
 * @version 2015年4月13日
 */
@Component
@DbTable(name="t_defselect", pkey="id")
public class TDefselect extends BizModel<TDefselect>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4910474000747054170L;
	
}
