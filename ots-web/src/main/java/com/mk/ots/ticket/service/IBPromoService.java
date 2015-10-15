package com.mk.ots.ticket.service;


import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.ticket.model.BPromo;

public interface IBPromoService {

	public BPromo getBPromoInfoByPwd(String promoPwd);

	public  boolean  checkCanUse(String  promoPwd,Long  hotelId);

	public  boolean  checkCanUseByCityId(String  promoPwd,Long  cityId);

	public   void  usePromo(OtaOrder otaOrder,BPromo  bpromo,int  newPromoStatus);
	
	public   boolean   checkCanUseByOrder(OtaOrder otaOrder);

	}
