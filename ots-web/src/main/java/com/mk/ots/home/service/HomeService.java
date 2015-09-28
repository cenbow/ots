package com.mk.ots.home.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.home.dao.HomeDAO;

@Service
public class HomeService {

	@Autowired
	HomeDAO homeDAO;
	
	public void genHomeDatas(Long hotelId, String date, int addDay){
		homeDAO.genHomeDatas(hotelId, date, addDay);
	}

	public void genBillDetails(Long hotelId, String beginDate, String endDate) {
		homeDAO.genBillDetails(hotelId, beginDate, endDate);
		
	}

	public void genCheckerBill(String beginDate, String endDate) {
		homeDAO.genCheckerBill(beginDate, endDate);
	}

}
