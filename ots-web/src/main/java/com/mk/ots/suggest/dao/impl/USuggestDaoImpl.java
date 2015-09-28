package com.mk.ots.suggest.dao.impl;

import org.springframework.stereotype.Component;
import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.suggest.dao.USuggestDao;
import com.mk.ots.suggest.model.USuggest;

@Component
public class USuggestDaoImpl extends MyBatisDaoImpl<USuggest, String> implements USuggestDao{

	@Override
	public void save(USuggest uSuggest){
		insert(uSuggest);
	}

	 
}
