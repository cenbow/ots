package com.mk.ots.order.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.mapper.BOrderBusinessLogMapper;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.model.BOrderBusinessLog;

@Service
public class OrderBusinessLogService {

	@Autowired
	private BOrderBusinessLogMapper bOrderBusinessLogMapper;

	@Autowired
	private OrderService orderService;
	/**
	 * 
	 * @param order
	 * @param businessCode
	 * @param bussinessParams
	 * @param desc
	 * @param exception
	 */
	public void saveLog(Long orderid, String orderFlag,  String desc) {
        OtaOrder order = orderService.findOtaOrderById(orderid);
        this.saveLog(order, orderFlag, "", desc, "");
	}
	
	/**
	 * 
	 * @param order
	 * @param businessCode
	 * @param bussinessParams
	 * @param desc
	 * @param exception
	 */
	public void saveLog(OtaOrder order, String orderFlag, String busParms, String desc, String exception) {

		BOrderBusinessLog log = new BOrderBusinessLog();
		log.setOrderid(order.getId());
		log.setOrderstatus(order.getOrderStatus());
		Long mid = MyTokenUtils.getMidByToken("");
		if (mid != null) {
			log.setOperateuser(String.valueOf(mid));
		}
		log.setBussinesscode(orderFlag);
		log.setBusinessparams(busParms);
		log.setBusinessdesc(desc);
		log.setBusinessexception(exception);
		log.setCreatetime(new Date());
		this.bOrderBusinessLogMapper.insert(log);
	}

	/**
	 * 
	 * 
	 * @param order
	 * @param orderFlag
	 * @param busParms
	 * @param desc
	 * @param exception
	 * @param operateName
	 */
	public void saveLog1(OtaOrder order, String orderFlag, String busParms, String desc, String exception, String operateName) {

		BOrderBusinessLog log = new BOrderBusinessLog();
		log.setOrderid(order.getId());
		log.setOrderstatus(order.getOrderStatus());
		Long mid = MyTokenUtils.getMidByToken("");
		if (mid != null) {
			log.setOperateuser(String.valueOf(mid));
		}
		log.setBussinesscode(orderFlag);
		log.setBusinessparams(busParms);
		log.setBusinessdesc(desc);
		log.setBusinessexception(exception);
		log.setCreatetime(new Date());
		log.setOperatename(operateName);

		this.bOrderBusinessLogMapper.insert(log);
	}
	
	public List<BOrderBusinessLog> selectByOrderId(Long orderId){
		return this.bOrderBusinessLogMapper.selectByOrderId(orderId);
	}

}
