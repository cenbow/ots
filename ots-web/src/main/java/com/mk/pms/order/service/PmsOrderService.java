package com.mk.pms.order.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;

import cn.com.winhoo.pms.webout.service.bean.CustomerResult;

public interface PmsOrderService {

	public Map saveCustomerNo(Map param);

	public Map synOrder(Map param);

	public Map orderCharge(Map param);

	public Map roomCharge(Map param);
	
	public List<PmsOrder> findNeedPmsOrderSelect(Long hotelid);

	public List<PmsOrder> savePmsRoomTypeOrder(Long hotelId, List<Map> dataList);

	public void deletePmsOrder(List<PmsOrder> oldlist);

	public List<PmsRoomOrder> findNeedPmsRoomOrderSelect(Long hotelid,String roomNos);
	public List<PmsRoomOrder> findNeedPmsRoomOrderSelectBefore(Long hotelid,String roomNos);
	
	public Map cancelOrder(Map param);

	public Map savePmsRoomTypeOrder(Map param);
	
	public Map saveRepairs(Map param);
	
	public void synPmsOrder(Long hotelid,String roomNos);
	public String synPmsOrderBefore(Long hotelid,String roomNos);
	
	public void batchUpdateCustomerNo(Long hotelId);
	public List<PmsRoomOrder> findUnSynedPmsRoomOrder(Long hotelid,String customerNos);
	
	public String saveResult(Long hotelId,List<CustomerResult> result);
}
