package com.mk.ots.search.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.RoomSaleConfigMapper;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.search.model.ThemeRoomtypeModel;
import com.mk.ots.search.service.ThemeCacheService;

import redis.clients.jedis.Jedis;

@Service
public class ThemeCacheServiceImpl implements ThemeCacheService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private OtsCacheManager cacheManager;

	private final String lastUpdateName = "LASTUPT";

	@Autowired
	private RoomSaleConfigMapper saleConfigMapper;
	@Autowired
	private RoomstateService roomstateService;

	private final Gson gsonParser = new Gson();

	private Map<Long, Map<Long, ThemeRoomtypeModel>> themeRoomtypes;

	private static final ReadWriteLock lock = new ReentrantReadWriteLock();

	private Lock readLock;

	private Lock writeLock;

	private final Integer refreshSec = 1;

	private String getLastUpdateName() {
		return String.format("%s-%s", ThemeCacheServiceImpl.class.getCanonicalName(), lastUpdateName);
	}

	private String getHotelName(Long hotelId) {
		return String.format("%s-%s", ThemeCacheServiceImpl.class.getCanonicalName(), hotelId);
	}

	private List<TRoomSaleConfig> findThemeRoomtypes() throws Exception {
		List<TRoomSaleConfig> saleConfigs = null;

		try {
			saleConfigs = saleConfigMapper.queryAllThemeRoomtypes();
		} catch (Exception ex) {
			throw new Exception("failed to saleConfigMapper.queryAllThemeRoomtypes", ex);
		}

		return saleConfigs;
	}

	private Map<Long, Map<Long, ThemeRoomtypeModel>> updateAndQueryRoomtypes() throws Exception {
		Map<Long, Map<Long, ThemeRoomtypeModel>> roomtypes = new ConcurrentHashMap<Long, Map<Long, ThemeRoomtypeModel>>();

		Jedis jedis = cacheManager.getNewJedis();

		Date day = new Date();
		String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
		String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1), DateUtils.FORMATSHORTDATETIME);

		List<TRoomSaleConfig> saleConfigs = findThemeRoomtypes();
		if (saleConfigs == null) {
			logger.warn("no theme-roomtypes have been found...");
			return roomtypes;
		}

		for (TRoomSaleConfig saleConfig : saleConfigs) {
			Integer saleRoomtypeId = saleConfig.getSaleRoomTypeId();
			Integer hotelId = saleConfig.getHotelId();

			if (saleRoomtypeId != null && hotelId != null) {
				String[] prices = roomstateService.getRoomtypeMikePrices(Long.valueOf(hotelId),
						Long.valueOf(saleRoomtypeId), strCurDay, strNextDay);

				if (prices != null && prices.length > 0) {
					ThemeRoomtypeModel themeRoomtype = new ThemeRoomtypeModel();
					themeRoomtype.setHotelId(Long.valueOf(hotelId));
					themeRoomtype.setRoomtypeId(Long.valueOf(saleRoomtypeId));
					themeRoomtype.setPrice(prices[0]);

					if (!roomtypes.containsKey(Long.valueOf(hotelId))) {
						roomtypes.put(Long.valueOf(hotelId), new HashMap<Long, ThemeRoomtypeModel>());
					}
					roomtypes.get(Long.valueOf(hotelId)).put(themeRoomtype.getRoomtypeId(), themeRoomtype);
				}
			}
		}

		for (Long hotelId : roomtypes.keySet()) {
			Map<Long, ThemeRoomtypeModel> themes = roomtypes.get(hotelId);
			if (themes != null) {
				String themeJson = gsonParser.toJson(themes);

				jedis.set(getHotelName(hotelId), themeJson);
			}
		}

		return roomtypes;
	}

	@Override
	public Map<Long, Map<Long, ThemeRoomtypeModel>> queryThemePricesWithLocalCache() throws Exception {
		Jedis jedis = cacheManager.getNewJedis();

		if (readLock == null) {
			readLock = lock.readLock();
		}

		if (writeLock == null) {
			writeLock = lock.writeLock();
		}

		Map<Long, Map<Long, ThemeRoomtypeModel>> themeRoomtypes = null;

		String lastUpdate = "";

		try {
			lastUpdate = jedis.get(getLastUpdateName());
		} catch (Exception ex) {
			logger.warn("failed to get lastupdate from jedis...", ex);
		}

		final Date currentTime = new Date();

		if (StringUtils.isBlank(lastUpdate)) {
			jedis.set(getLastUpdateName(), String.valueOf(currentTime.getTime()));
			lastUpdate = String.valueOf(currentTime.getTime());
		}

		Date lastUpdateTime = new Date(Long.valueOf(lastUpdate));

		int seconds = DateUtils.diffSecond(lastUpdateTime, currentTime);
		if (seconds > refreshSec || this.themeRoomtypes == null) {
			writeLock.lock();

			try {
				themeRoomtypes = updateAndQueryRoomtypes();

				jedis.set(getLastUpdateName(), String.valueOf(currentTime.getTime()));

				this.themeRoomtypes = themeRoomtypes;
			} catch (Exception ex) {
				throw new Exception("failed to updateAndQueryRoomtypes...", ex);
			} finally {
				writeLock.unlock();
			}
		} else {
			readLock.lock();
			try {
				themeRoomtypes = this.themeRoomtypes;
			} finally {
				readLock.unlock();
			}
		}
		try {
			if (jedis != null) {
				jedis.close();
			}
		} catch (Exception ex) {
			logger.warn("failed to close jedis...", ex);
		}

		return themeRoomtypes;
	}

}
