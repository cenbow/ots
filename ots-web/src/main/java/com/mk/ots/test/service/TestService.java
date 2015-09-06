package com.mk.ots.test.service;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.mk.ots.mapper.OtaOrderMacMapper;

@Service
public class TestService {

	@Autowired
	private OtaOrderMacMapper dao = null;

	//
	// @Autowired
	// private ReadWriteSplittingDataSource dataSource = null;
	//
	// @Autowired
	// private TestFindService findService = null;
	//
	// private Logger logger = LoggerFactory.getLogger(TestService.class);
	//
	// public void getTest() {
	// try {
	// this.getDataSource().getConnection();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// this.logger.info("测试消息");
	// }
	//
	// public void findTest() {
	// try {
	// this.getDataSource().getConnection();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// this.logger.info("测试消息");
	// }
	//
	// public void addTest() {
	// try {
	// this.getFindService().findData();
	// this.getDataSource().getConnection();
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// this.logger.info("测试消息");
	//
	// }
	//
	// public ReadWriteSplittingDataSource getDataSource() {
	// return this.dataSource;
	// }
	//
	// public TestFindService getFindService() {
	// return findService;
	// }
	//

	@Async
	public Future<String> test() {
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new AsyncResult<String>("test");
	}

	public OtaOrderMacMapper getDao() {
		return this.dao;
	}

}
