package com.mk.ots.suggest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mk.ots.suggest.dao.USuggestDao;
import com.mk.ots.suggest.model.USuggest;

@Service
public class USuggestService implements ISuggestService{
	
	@Autowired
	USuggestDao usuggestDAO;

	@Override
	public void save(USuggest   uSuggest) {
		usuggestDAO.save(uSuggest);
	}

	 
}
