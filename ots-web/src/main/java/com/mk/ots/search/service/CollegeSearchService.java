package com.mk.ots.search.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.restful.input.CollegeQueryEntity;

public interface CollegeSearchService {
	public List<Map<String, Object>> search(CollegeQueryEntity params) throws Exception;
}
