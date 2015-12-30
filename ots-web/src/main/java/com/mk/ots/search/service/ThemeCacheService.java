package com.mk.ots.search.service;

import java.util.Map;

import com.mk.ots.search.model.ThemeRoomtypeModel;

public interface ThemeCacheService {
	public Map<Long, Map<Long, ThemeRoomtypeModel>> queryThemePricesWithLocalCache() throws Exception;
}
