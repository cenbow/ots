package com.mk.ots.search.service;

import java.util.Map;

import com.mk.ots.restful.input.CollegeQueryEntity;

public interface CollegeSearchService {
	public Map<String, Object> search(CollegeQueryEntity params) throws Exception;
}
