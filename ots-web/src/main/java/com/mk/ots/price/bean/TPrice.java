package com.mk.ots.price.bean;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

@Component
@DbTable(name="t_price", pkey="id")
public class TPrice extends BizModel<TPrice> {
    
    public static final TPrice dao = new TPrice();
	private static final long serialVersionUID = 3835878355796255276L;

	public TPriceTime getTPriceTime(){
		return TPriceTime.dao.findFirst("select * from t_pricetime where id = ?", getLong("timeId"));
	}
	
	public TPriceTime getTpriceTime(String timeid){
		String sql="select * from t_pricetime where id=?";
		return TPriceTime.dao.findFirst(sql,timeid);
	}
}
