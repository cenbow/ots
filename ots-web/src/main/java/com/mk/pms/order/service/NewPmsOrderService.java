package com.mk.pms.order.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;

public interface NewPmsOrderService {

	public JSONObject saveCustomerNo(JSONObject param);

	public JSONObject synOrder(JSONObject param);

	public JSONObject orderCharge(JSONObject param);

	public JSONObject roomCharge(JSONObject param);
	
	public List<PmsOrder> findNeedPmsOrderSelect(Long hotelid);

	public void deletePmsOrder(List<PmsOrder> oldlist);

	public List<PmsRoomOrder> findNeedPmsRoomOrderSelect(Long hotelid);

	public JSONObject genJsonDataSaveCustomerNo(String json);

	JSONObject genJsonDataRoomCharge(String json);
	
	JSONObject genJsonDataOrderCharge(String json);
	
	JSONObject genJsonDataSynOrder(String json);
	
}
