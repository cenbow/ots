package com.mk.ots.bill.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.bill.dao.BillOrderDAO;

@Service
public class BillOrderService {

	@Autowired
	BillOrderDAO billOrderDAO;
	
	/**
	 * 账单明细表-每天跑 凌晨2点1分
	 * @param request
	 * @return
	 */
	public void genBillOrders(Date begintime, String hotelid, String orderidId){
		billOrderDAO.genBillOrders(begintime, hotelid, orderidId);
	}
	
	/**
	 * 账单汇总 月表
	 * @param request
	 * @return
	 */
	public void genBillConfirmChecks(Date begintime, String hotelid){
		billOrderDAO.genBillConfirmChecks(begintime, hotelid);
	}
}
