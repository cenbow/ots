package com.mk.ots.pay.dao.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mk.ots.common.enums.PayTaskStatusEnum;
import com.mk.ots.common.enums.PayTaskTypeEnum;
import com.mk.ots.pay.dao.impl.PayTaskDao;
import com.mk.ots.pay.model.PPayTask;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/test-db.xml" })
public class PayTaskTest {
	
//	private Logger logger = LoggerFactory.getLogger(PayTaskTest.class);
	
//	@Resource
//	private PayTaskDao payTaskDao;
	
	private PayTaskDao payTaskDao = new PayTaskDao();
	
	@Resource
	private SqlSessionFactory sqlSessionFactory;
	
	@Test
	@Rollback(false) 
	public void testInsert() {
		
		PPayTask task = new PPayTask();
		
		task.setContent("test2");
		task.setOrderId(11111l);
		task.setStatus(PayTaskStatusEnum.INIT);
		task.setTaskType(PayTaskTypeEnum.SENDMSG2LANDLORD);
		
//		logger.info("插入PayTask...");
		System.out.println("插入PayTask...");
		payTaskDao.setSqlSessionFactory(sqlSessionFactory);
		Long result = payTaskDao.insert(task);
		System.out.println("插入结果:" + result);
//		logger.info("插入结果:" + result);
	}
	
	@Test
	public void testSelect() {
		
		
		System.out.println("查询初始状态的任务...");
		payTaskDao.setSqlSessionFactory(sqlSessionFactory);
		List<PPayTask> tasks = payTaskDao.selectInitTask(PayTaskTypeEnum.SENDMSG2LANDLORD, PayTaskStatusEnum.INIT);
		
		for(PPayTask task : tasks) {
			
			System.out.println("id:" + task.getId() + ",内容:" + task.getContent());
		}
		System.out.println("查询初始状态的任务结束.");
	}
	
	@Test
	public void testUpdate() {
		
		
		System.out.println("批量更新任务状态...");
		payTaskDao.setSqlSessionFactory(sqlSessionFactory);
		
		List<PPayTask> tasks = new ArrayList<PPayTask>();
		
		PPayTask task = new PPayTask();
		
		task.setId(3l);
		
		tasks.add(task);
		
		PPayTask task1 = new PPayTask();
		
		task1.setId(4l);
		
		tasks.add(task1);
		
		int c = payTaskDao.updatePayTask(PayTaskStatusEnum.INIT, tasks);
		
		System.out.println("批量更新任务状态结束,修改记录" + c + "条.");
	}

}
