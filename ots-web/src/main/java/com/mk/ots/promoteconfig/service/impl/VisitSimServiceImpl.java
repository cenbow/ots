package com.mk.ots.promoteconfig.service.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.RandomUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.promoteconfig.service.VisitSimService;

import redis.clients.jedis.Jedis;

/**
 * simulator class that simulates site visit number according to each access
 * 
 * @author AaronG
 *
 */
public class VisitSimServiceImpl implements VisitSimService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final static Integer defaultInitMin = 500;
	private final static Integer defaultInitMax = 1000;
	private final static Integer defaultGapMin = 5;
	private final static Integer defaultGapMax = 50;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final Gson gsonParser = new Gson();

	private AtomicLong lastVisit;

	private AtomicLong accessCounter;

	private AtomicLong currentVisitNumber;

	private AtomicInteger currentGap;

	private Lock readLock;

	private Lock writeLock;

	@Autowired
	private OtsCacheManager cacheManager;

	private String getConfCacheKey() {
		return String.format("conf_%s_%s", VisitSimServiceImpl.class.getCanonicalName(),
				DateUtils.formatDateTime(new Date(), DateUtils.FORMATSHORTDATETIME));
	}

	private String getDataCacheKey() {
		return String.format("data_%s_%s", VisitSimServiceImpl.class.getCanonicalName(),
				DateUtils.formatDateTime(new Date(), DateUtils.FORMATSHORTDATETIME));
	}

	private Configuration queryConfs() throws Exception {
		Jedis jedis = cacheManager.getNewJedis();
		String jedisVal = jedis.get(getConfCacheKey());
		Configuration conf = null;
		if (StringUtils.isNotBlank(jedisVal)) {
			conf = gsonParser.fromJson(jedisVal, new TypeToken<Configuration>() {
			}.getType());
		}

		return conf;
	}

	private void init() {
		/***
		 * not initialized yet
		 */
		if (currentVisitNumber == null || currentVisitNumber.get() <= 0) {
			synchronized (VisitSimServiceImpl.class) {
				readLock = lock.readLock();
				writeLock = lock.writeLock();

				boolean isConfLoaded = false;
				Configuration conf = null;
				try {
					conf = queryConfs();
					isConfLoaded = true;
				} catch (Exception ex) {
					logger.warn("failed to queryConfs...", ex);
				}

				/**
				 * giving random initial visit
				 */
				if (isConfLoaded && conf != null) {
					currentVisitNumber = new AtomicLong(RandomUtils.nextInt(conf.getInitMin(), conf.getInitMax()));
					currentGap = new AtomicInteger(RandomUtils.nextInt(conf.getGapMin(), conf.getGapMax()));
				} else {
					currentVisitNumber = new AtomicLong(RandomUtils.nextInt(defaultInitMin, defaultInitMax));
					currentGap = new AtomicInteger(RandomUtils.nextInt(defaultGapMin, defaultGapMax));
				}
			}
		}
	}

	@Override
	public Long sim() {
		init();

		if (writeLock != null) {
			writeLock.lock();
		}

		return null;
	}

	private class Configuration {
		private Integer initMin;
		private Integer initMax;
		private Integer gapMin;
		private Integer gapMax;

		public Integer getInitMin() {
			return initMin;
		}

		public void setInitMin(Integer initMin) {
			this.initMin = initMin;
		}

		public Integer getInitMax() {
			return initMax;
		}

		public void setInitMax(Integer initMax) {
			this.initMax = initMax;
		}

		public Integer getGapMin() {
			return gapMin;
		}

		public void setGapMin(Integer gapMin) {
			this.gapMin = gapMin;
		}

		public Integer getGapMax() {
			return gapMax;
		}

		public void setGapMax(Integer gapMax) {
			this.gapMax = gapMax;
		}
	}

	private class Data {
		private Long currentVisitNumber;
		private Long accessCounter;

		public Long getCurrentVisitNumber() {
			return currentVisitNumber;
		}

		public void setCurrentVisitNumber(Long currentVisitNumber) {
			this.currentVisitNumber = currentVisitNumber;
		}

		public Long getAccessCounter() {
			return accessCounter;
		}

		public void setAccessCounter(Long accessCounter) {
			this.accessCounter = accessCounter;
		}
	}
}
