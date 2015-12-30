package com.mk.ots.search.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.search.model.ThemeRoomtypeModel;

public interface ThemeCacheService {
	public Map<Long, List<ThemeRoomtypeModel>> queryThemePricesWithLocalCache() throws Exception;
}
