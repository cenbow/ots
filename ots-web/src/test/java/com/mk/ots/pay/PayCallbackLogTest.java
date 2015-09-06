package com.mk.ots.pay;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mk.ots.common.enums.PayCallbackEnum;
import com.mk.ots.pay.dao.impl.PayCallbackLogDao;
import com.mk.ots.pay.model.PayCallbackLog;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-db.xml" })
public class PayCallbackLogTest {

	private PayCallbackLogDao payCallbackLogDao = new PayCallbackLogDao();
	
	@Resource
	private SqlSessionFactory sqlSessionFactory;
	
	@Test
	@Rollback(false) 
	public void testInsert() {
		
		String orderId = "2015081202";
		String onlinepaytype = "2";
		String payno = "2015081202alicheck";
		String price = "10.01";
		
		PayCallbackLog log = new PayCallbackLog();
		
		log.setOrderId(Long.parseLong(orderId));
		log.setCallbackFrom(PayCallbackEnum.getById(Integer.parseInt(onlinepaytype)).name());
		log.setPayNo(payno);
		log.setPayResult("Y");
		log.setPrice(new BigDecimal(price).setScale(2, BigDecimal.ROUND_HALF_UP));
		log.setErrorMsg("测试一下能放几个中文");

		payCallbackLogDao.setSqlSessionFactory(sqlSessionFactory);
		System.out.println(new Date() +"insert result:" + payCallbackLogDao.insertPayCallbackLog(log));
	}
	
}
