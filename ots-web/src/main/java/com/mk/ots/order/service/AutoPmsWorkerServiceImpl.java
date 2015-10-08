package com.mk.ots.order.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableSet;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.SysConfig;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.dao.CheckInUserDAO;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.pms.order.service.NewPmsOrderService;
import com.mk.pms.order.service.PmsOrderService;
import com.mk.pms.order.service.PmsOrderUtilService;

/**
 * 模拟测试，自动pms入住、离店
 * @author zzy 
 * insert into sy_config (skey, svalue, stype) VALUES ('DEBUG_INSERT_PMS', 'true', 'mikeweb'); 
 * insert into sy_config (skey, svalue, stype) VALUES ('DEBUG_INSERT_PMS_HOTELIDS', '1939,1432', 'mikeweb');
 */
@Service
public class AutoPmsWorkerServiceImpl{

	private static Logger logger = LoggerFactory.getLogger(AutoPmsWorkerServiceImpl.class);

	@Autowired
	OrderServiceImpl orderService;
	@Autowired
	PmsOrderService pmsOrderService;
	@Autowired
	NewPmsOrderService newPmsOrderService;
	@Autowired
	CheckInUserDAO checkInUserDAO;
	@Autowired
	PmsOrderUtilService pmsOrderUtilService;
	@Autowired
	OrderDAO orderDao;

	/**
	 * 5分钟调度
	 * 
	 * @param orderId
	 */
	public void changePmsRoomOrderStatusJob() {
		String debugInserPms = SysConfig.getInstance().getSysValueByKey("DEBUG_INSERT_PMS");
		String debugHotelIds = "2476";//"2472,2409,2188";//SysConfig.getInstance().getSysValueByKey("DEBUG_INSERT_PMS_HOTELIDS");
		String debugSql = SysConfig.getInstance().getSysValueByKey("DEBUG_INSERT_PMS_SQL");

		this.logger.info("changePmsRoomOrderStatus:debugInserPms:{},debugHotelIds:{},debugSql:{}", debugInserPms, debugHotelIds, debugSql);

		if ("true".equals(debugInserPms) && StringUtils.isNotBlank(debugHotelIds)) {
			ImmutableSet<String> hotelidSet = ImmutableSet.copyOf(debugHotelIds.split(","));
			String sql = "SELECT \n" +
							"    a.id orderid, a.OrderStatus,a.Begintime,a.Endtime\n" +
							"FROM\n" +
							"    b_otaorder a,\n" +
							"    b_otaroomorder b,\n" +
							"    b_pmsroomorder c\n" +
							"WHERE\n" +
							"    	a.Createtime > '2015-09-24 21'\n" +
							"    	 and a.Createtime < '2015-09-27'\n" +
							"    	 and a.id = b.otaorderid\n" +
							"    	 and a.OrderStatus < 511 \n" +
							"        AND c.PMSRoomOrderNo = b.PmsRoomOrderNo\n" +
							"        AND c.Hotelid = b.Hotelid\n" +
							"        AND c.status IN ('RE', 'IN') " + 
							"		 AND a.hotelid =2476 " + 
							"		 AND a.hotelname='内测酒店'";
			debugSql = debugSql == null || debugSql.equals("null") ? sql : debugSql;
			List<Bean> beans = Db.find(debugSql);
			String[] statusArray = "140,180,200".split(",");
			for (Bean bean : beans) {
				this.logger.info("changePmsRoomOrderStatus:loop:bean:{}", bean);
				for (int i = 0; i < statusArray.length; i++) {
					if (statusArray[i].equals(String.valueOf(bean.getInt("OrderStatus")))) {
						try {
							String nextStatus = statusArray[i + 1];
							// 入住时间
							String dateTime = DateUtils.getDatetime(bean.getDate("Begintime"));
							if (nextStatus.equals("200")) {
								// 离店时间
								dateTime = DateUtils.getDatetime(bean.getDate("Endtime"));
							}

							this.logger.info("changePmsRoomOrderStatus:loop:nextStatus:{},dateTime:{}", nextStatus, dateTime);
							pmsOrderUtilService.changeRoomOrderStatus(bean.getLong("orderid"), nextStatus, dateTime, "", "调度入住,离店");
							if (nextStatus.equals("200")) {
								OtaOrder order = orderService.findOtaOrderById(bean.getLong("orderid"));
								this.logger.info("changePmsRoomOrderStatus:loop:call:cancelPmsOrder");
								orderService.cancelPmsOrder(order);
								// 重新置回订单的状态，删除b_orderbusiness_log里512，513
								Db.update("update b_otaorder set OrderStatus=200 where id=?", bean.getLong("orderid"));
								Db.update("update b_otaroomorder set OrderStatus=200 where otaorderid=?", bean.getLong("orderid"));
								Db.update("delete from b_orderbusiness_log where orderid=? and orderStatus in (512,513)", bean.getLong("orderid"));
							}
						} catch (Exception e) {
							this.logger.info("changePmsRoomOrderStatus:Exception:{}", e.getLocalizedMessage());
						}
					}
				}
			}
		}
		this.logger.info("changePmsRoomOrderStatus:end");
	}
}