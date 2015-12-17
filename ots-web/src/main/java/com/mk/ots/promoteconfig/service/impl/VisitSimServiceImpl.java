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
import org.springframework.stereotype.Service;

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
@Service
public class VisitSimServiceImpl implements VisitSimService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final static Integer defaultInitMin = 500;
	private final static Integer defaultInitMax = 1000;
	private final static Integer defaultGapMin = 5;
	private final static Integer defaultGapMax = 50;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private final Gson gsonParser = new Gson();

	private AtomicLong accessCounter;

	private AtomicLong currentVisitNumber;

	private AtomicInteger currentGapMin;

	private AtomicInteger currentGapMax;

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

	private void updateData(Data data) throws Exception {
		Jedis jedis = cacheManager.getNewJedis();

		if (data != null) {
			String dataJson = gsonParser.toJson(data, new TypeToken<Configuration>() {
			}.getType());
			jedis.set(getConfCacheKey(), dataJson);
		}
	}

	private Data queryData() throws Exception {
		Jedis jedis = cacheManager.getNewJedis();

		Data data = null;
		String cacheKey = getDataCacheKey();

		try {
			readLock.lock();

			String jedisVal = jedis.get(cacheKey);
			if (StringUtils.isNotBlank(jedisVal)) {
				data = gsonParser.fromJson(jedisVal, new TypeToken<Configuration>() {
				}.getType());
			}
		} catch (Exception ex) {
			logger.error(String.format("failed to querydata from cache with key %s", cacheKey), ex);
		} finally {
			readLock.unlock();
		}

		return data;
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
				Data data = null;
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
					if (conf.getInitMin() == null) {
						conf.setInitMin(defaultInitMin);
					}
					if (conf.getInitMax() == null) {
						conf.setInitMax(defaultInitMax);
					}
					if (conf.getGapMin() == null) {
						conf.setGapMin(defaultGapMin);
					}
					if (conf.getGapMax() == null) {
						conf.setGapMax(defaultGapMax);
					}

					currentVisitNumber = new AtomicLong(RandomUtils.nextInt(conf.getInitMin(), conf.getInitMax()));
					currentGapMin = new AtomicInteger(conf.getGapMin());
					currentGapMax = new AtomicInteger(conf.getGapMax());
					accessCounter = new AtomicLong(0);

					data = new Data();
					data.setAccessCounter(0L);
					data.setCurrentVisitNumber(currentVisitNumber.get());
					data.setLastVisit((new Date()).getTime());

					try {
						updateData(data);
					} catch (Exception ex) {
						logger.warn(String.format(
								"failed to updateData data.accessCounter:%s; data.currentVisitNumber:%s...",
								data.getAccessCounter(), data.getCurrentVisitNumber()), ex);
					}
				} else {
					currentVisitNumber = new AtomicLong(RandomUtils.nextInt(defaultInitMin, defaultInitMax));
					accessCounter = new AtomicLong(0);
					currentGapMin = new AtomicInteger(defaultGapMin);
					currentGapMax = new AtomicInteger(defaultGapMax);
				}
			}
		}
	}

	@Override
	public Long sim() {
		init();

		if (writeLock != null) {
			Data data = new Data();

			try {
				writeLock.lock();

				Integer gap = RandomUtils.nextInt(currentGapMin.get(), currentGapMax.get());

				long currVisitNumber = currentVisitNumber.addAndGet(gap);
				long accCounter = accessCounter.incrementAndGet();
				Date currentTime = new Date();

				if (logger.isInfoEnabled()) {
					logger.info(String.format("simulate currentVisitNumber:%s; accessCounter:%s; currentTime:%s",
							currVisitNumber, accCounter, currentTime));
				}

				data.setAccessCounter(accCounter);
				data.setCurrentVisitNumber(currVisitNumber);
				data.setLastVisit(currentTime.getTime());

				updateData(data);
			} catch (Exception ex) {
				logger.error(String.format(
						"failed to updateData with data.accessCounter:%s; data.currentVisitNumber:%s; data.lastVisit:%s",
						data.getAccessCounter(), data.getCurrentVisitNumber(), data.getLastVisit()), ex);
			} finally {
				writeLock.unlock();
			}
		}

		try {
			Data data = queryData();
			return data.getCurrentVisitNumber();
		} catch (Exception ex) {
			logger.error("failed to querydata from cache...", ex);
			return 0L;
		}
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
		private Long currentVisitNumber = 0L;
		private Long accessCounter = 0L;
		private Long lastVisit = 0L;

		public Long getLastVisit() {
			return lastVisit;
		}

		public void setLastVisit(Long lastVisit) {
			this.lastVisit = lastVisit;
		}

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
