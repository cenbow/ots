package com.mk.ots.suggest.dao;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.suggest.model.USuggest;
 
public interface USuggestDao extends BaseDao<USuggest, String>{


	public void save(USuggest uSuggest);
	
}
