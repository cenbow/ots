package com.mk.ots.promoteconfig.service.impl;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
	private final static Integer defaultInitMin = 500;
	private final static Integer defaultInitMax = 1000;
	private final static Integer defaultGapMin = 5;
	private final static Integer defaultGapMax = 50;

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	private AtomicLong lastVisit;

	private AtomicLong accessCounter;

	private AtomicLong currentVisitNumber;

	private AtomicInteger currentGap;

	private Lock readLock;

	private Lock writeLock;

	@Autowired
	private OtsCacheManager cacheManager;

	private String getCacheKey() {
		return String.format("%s_%s", VisitSimServiceImpl.class.getCanonicalName(),
				DateUtils.formatDateTime(new Date(), DateUtils.FORMATSHORTDATETIME));
	}

	private void queryConfs() throws Exception {
		Jedis jedis = cacheManager.getNewJedis();
		jedis.get(getCacheKey());
	}

	private void init() {
		/***
		 * not initialized yet
		 */
		if (currentVisitNumber == null || currentVisitNumber.get() <= 0) {
			synchronized (VisitSimServiceImpl.class) {
				readLock = lock.readLock();
				writeLock = lock.writeLock();

				/**
				 * giving random initial visit
				 */
				Integer randInitVisit = RandomUtils.nextInt(defaultInitMin, defaultInitMax);
				currentVisitNumber = new AtomicLong(randInitVisit);
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

}
