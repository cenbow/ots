package com.mk.pms.order.event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.com.winhoo.mikeweb.ssh.Services;
import cn.com.winhoo.pms.webout.service.bean.CustomerResult;
import cn.com.winhoo.pms.webout.service.bean.OrderResult;
import cn.com.winhoo.pms.webout.service.bean.ReturnObject;
import cn.com.winhoo.pms.webout.service.bean.RoomTypeOrderResult;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.eventbus.Subscribe;
import com.mk.framework.AppUtils;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.manager.HotelPMSManager;
import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.pay.module.weixin.pay.common.Tools;
import com.mk.pms.bean.PmsCheckinUser;
import com.mk.pms.hotel.service.PmsCheckinUserService;
import com.mk.pms.order.service.NewPmsOrderService;
import com.mk.pms.order.service.PmsOrderService;

/**
 * @author zzy
 * com.mk.ots.order.event.EventListener
 */
@Component
public class PmsQueryAgainEventListener {
	
	private Logger logger = LoggerFactory.getLogger(PmsQueryAgainEventListener.class);
	public Long hotelid = null;

	public Long getLastMessage() {
		return hotelid;
	}
	
	@Autowired
	private RoomService roomService;
	
	
	@Subscribe
	public void listen(PmsQueryAgainEvent event) {
		hotelid = event.getMessage();
		logger.info("OTSMessage::resetRoomStatusByHotelidFromPMS::synOrder::全量更新::同步消息:{}", hotelid);

		if (hotelid == null) {
			logger.info("PmsQueryAgainEventListener::没有hotel信息，返回了。hotelid::"+hotelid);
			return;
		}
		HotelService hotelService = AppUtils.getBean(HotelService.class);
		THotel hotel = hotelService.readonlyTHotel(hotelid);
		PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
		List<PmsOrder> oldlist = pmsOrderService.findNeedPmsOrderSelect(hotelid);
		if(oldlist.size()>0){
			List<String> pmsOrderIds=new ArrayList<>();
			for (PmsOrder temp : oldlist) {
				if(pmsOrderIds.contains(temp.get("PmsOrderNo"))==false){
					pmsOrderIds.add(String.valueOf(temp.get("PmsOrderNo")));
				}
			}
			logger.info("PmsQueryAgainEventListener::call pms selectOrder::pmsOrderIds::"+pmsOrderIds);
			ReturnObject<List<OrderResult>> returnobject = HotelPMSManager.getInstance().getService().selectOrder(hotelid, pmsOrderIds);
			logger.info("PmsQueryAgainEventListener::call pms selectOrder::pmsOrderIds::ok");
//			PmsOutService pmsOurService=new PmsOutService();
//			pmsOurService.setTimeout(60*3);
//			ReturnObject<List<OrderResult>> returnobject=pmsOurService.selectOrder(hotelid, pmsOrderIds);
			if(returnobject!=null && returnobject.getIsError()==false){
				if(returnobject.getValue()!=null){
					for (OrderResult or : returnobject.getValue()) {
						saveResult(hotelid, or,oldlist);
					}
				}
			} else {
				logger.info("PmsQueryAgainEventListener::call pms selectOrder::哎呦 pms selectOrder 异常::err::" + returnobject.getErrorMessage());
			}
		}
		if(oldlist.size()>0){
			logger.info("PmsQueryAgainEventListener::deletePmsOrder");
			pmsOrderService.deletePmsOrder(oldlist);
			logger.info("PmsQueryAgainEventListener::deletePmsOrder::ok");
		}
		List<PmsRoomOrder> oldRoomOrderlist = pmsOrderService.findNeedPmsRoomOrderSelect(hotelid,null);
		if(oldRoomOrderlist.size()>0){
			List<String> pmsCustomNos=new ArrayList<>();
			for (PmsRoomOrder temp : oldRoomOrderlist) {
				if(pmsCustomNos.contains(temp.get("PmsRoomOrderNo"))==false){
					pmsCustomNos.add(String.valueOf(temp.get("PmsRoomOrderNo")));
				}
			}
			// pms2.0
			if ("T".equals(hotel.getStr("isNewPms"))) {
				JSONObject pmsCustomNosJson = new JSONObject();
				pmsCustomNosJson.put("hotelid", hotel.getStr("pms"));
				StringBuffer ids = new StringBuffer();
				for (String id : pmsCustomNos) {
					ids.append(id).append(",");
				}
				ids.setLength(ids.length() - 1);
				pmsCustomNosJson.put("customerid", ids.toString());
				
				JSONObject returnObject = JSONObject.parseObject(doPostJson(UrlUtils.getUrl("newpms.url") + "/selectcustomerno", pmsCustomNosJson.toJSONString()));
				if (returnObject.getBooleanValue("success")) {
					logger.info("PmsQueryAgainEventListener::call pms ::selectCustomerno::ok::saveResult");
					JSONArray customerno = returnObject.getJSONArray("customerno");
					saveResultPms2(hotelid, customerno);
				} else {
					logger.info("PmsQueryAgainEventListener::call pms ::selectCustomerno::err::"+returnObject.getString("errormsg"));
					throw MyErrorEnum.customError.getMyException("NEWPMSselectCustomerno:" + returnObject.getString("errormsg") + "；如需帮助请联系客服人员");
				}
			} else {
				// pms1.0
				logger.info("PmsQueryAgainEventListener::call pms ::selectCustomerno::");
				ReturnObject<List<CustomerResult>> ro = HotelPMSManager.getInstance().getService().selectCustomerno(hotelid, pmsCustomNos);
				if (ro != null && ro.getIsError() == false) {
					logger.info("PmsQueryAgainEventListener::call pms ::selectCustomerno::ok::saveResult");
					saveResult(hotelid, ro.getValue());
				} else {
					logger.info("PmsQueryAgainEventListener::call pms ::selectCustomerno::err::"+ro.getErrorMessage());
				}
			}
		}
	}

	private void saveResult(Long hotelId,OrderResult or,List<PmsOrder> oldlist){
		if(or==null){
			return;
		}
		if(or.getRoomTypeOrderList()!=null){
			List<Map> dataList=new ArrayList<Map>();
			for (RoomTypeOrderResult result : or.getRoomTypeOrderList()) {
				Map rto=new HashMap();
				rto.put("orderid",or.getPmsorderid());
				rto.put("optype","delete");
				rto.put("roomtypepms",result.getRoomtypePms());
				rto.put("bookingcnt",result.getBookingcnt());
				rto.put("roomingcnt",result.getRoomingcnt());
				rto.put("checkincnt",result.getCheckincnt());
				rto.put("price",result.getPrice());
				rto.put("begintime",result.getArrivaldate());
				rto.put("endtime",result.getExcheckoutdate());
				rto.put("opuser",result.getOpuser());
				rto.put("batchno",result.getBatchno());
				dataList.add(rto);
				removeOldList(oldlist,rto);
			}
			PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
			List<PmsOrder> sendlist=pmsOrderService.savePmsRoomTypeOrder(hotelId,dataList);
			
			/*for (PmsOrder pmsOrder : sendlist) {
				HotelService hotelService = AppUtils.getBean(HotelService.class);
				roomService.calCacheByHotelIdAndRoomType(pmsOrder.getLong("Hotelid"), pmsOrder.getLong("roomtypepms"));
			}*/
//			MikeWebHessianTask task=new MikeWebHessianTask(MikeWebHessianCmdEnum.changePmsOrder, sendlist);
//			SysConfig.getInstance().getThreadPool().submit(task);
		}
		
		if(or.getCustomerList()!=null ){
			List<Map> datalist=new ArrayList<Map>();
			for (CustomerResult result : or.getCustomerList()) {
				Map bean=new HashMap();
				bean.put("roomtypepms",result.getRoomTypePms());
				bean.put("roompms",result.getRoompms());
				bean.put("delete",false);
				bean.put("orderid",or.getPmsorderid());
				bean.put("customerno",result.getCustomerpms());
//				PmsRoomOrderStatusEnum recstatus=PmsRoomOrderStatusEnum.findPmsRoomOrderStatusEnumById(result.getRecstatus());
				bean.put("recstatus",result.getRecstatus());
				bean.put("rectype","");
				bean.put("roompms",result.getRoompms());
				if(result.getPrice()!=null){
					bean.put("price",result.getPrice());
				}
				if(result.getPayment()!=null){
					bean.put("payment",result.getPayment());
				}
				if(result.getCost()!=null){
					bean.put("cost",result.getCost());
				}
				if(result.getBalance()!=null){
					bean.put("balance",result.getBalance());
				}
				bean.put("arrivetime",result.getArrivaltime());
				bean.put("excheckouttime",result.getExcheckouttime());
				bean.put("roominguser",result.getRoominguser());
				String priceType=result.getPaytype();
				if(!"H".equalsIgnoreCase(priceType)){
					bean.put("priceType",2);
				}else{
					bean.put("priceType",1);
				}
				datalist.add(bean);
			}
			Map tempMap = new HashMap();
			tempMap.put("hotelid", hotelId);
			tempMap.put("customerno", datalist);
			PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
			Map map = pmsOrderService.saveCustomerNo(tempMap);
			HotelService hotelService = AppUtils.getBean(HotelService.class);
			// 计算缓存
			/*for (Object _hotelId : map.keySet()) {
				Set<Long> roomTypeIds = (Set<Long>) map.get(_hotelId);
				for (Long roomTypeId : roomTypeIds) {
					roomService.calCacheByHotelIdAndRoomType((Long) _hotelId, roomTypeId);
				}
			}*/
		}
	}
	
	private void saveResultPms2(Long hotelId,JSONArray result){
		if(result==null || result.size()==0){
			return;
		}
		JSONObject param = new JSONObject();
		param.put("hotelid", String.valueOf(hotelId));
		param.put("customerno", result);
		NewPmsOrderService pmsOrderService = AppUtils.getBean(NewPmsOrderService.class);
		Map map = pmsOrderService.saveCustomerNo(param);
	}
	
	private void saveResult(Long hotelId,List<CustomerResult> result){
		if(result==null || result.size()==0){
			return;
		}
		List<Map> datalist=new ArrayList<Map>();
		for (CustomerResult customerResult : result) {
			Map bean=new HashMap();
			bean.put("roomtypepms",customerResult.getRoomTypePms());
			bean.put("roompms",customerResult.getRoompms());
			bean.put("delete",false);
			bean.put("customerno",customerResult.getCustomerpms());
//			PmsRoomOrderStatusEnum recstatus=PmsRoomOrderStatusEnum.findPmsRoomOrderStatusEnumById(customerResult.getRecstatus());
			bean.put("recstatus",customerResult.getRecstatus());
			bean.put("rectype","");
			if(customerResult.getPrice()!=null){
				bean.put("price",customerResult.getPrice());
			}
			if(customerResult.getPayment()!=null){
				bean.put("payment",customerResult.getPayment());
			}
			if(customerResult.getCost()!=null){
				bean.put("cost",customerResult.getCost());
			}
			if(customerResult.getBalance()!=null){
				bean.put("balance",customerResult.getBalance());
			}
			bean.put("arrivetime",customerResult.getArrivaltime());
			bean.put("excheckouttime",customerResult.getExcheckouttime());
			bean.put("Roominguser",customerResult.getRoominguser());
			String priceType=customerResult.getPaytype();
			if(!"H".equalsIgnoreCase(priceType)){
				bean.put("priceType",2);
			}else{
				bean.put("priceType",1);
			}
			// 记录身份证、卡号、民族 数据
			if (StringUtils.isNotBlank(customerResult.getCardid()) && StringUtils.isNotBlank(customerResult.getCardType())
					&& StringUtils.isNotBlank(customerResult.getName())) {
				PmsCheckinUserService checkinService = AppUtils.getBean(PmsCheckinUserService.class);
				PmsCheckinUser checkinUser =new PmsCheckinUser();
				checkinUser.set("Hotelid", hotelId);
				checkinUser.set("Name", customerResult.getName());
				checkinUser.set("PmsRoomOrderNo", customerResult.getCustomerpms());
				checkinUser.set("Cardid", customerResult.getCardid());
				checkinUser.set("Ethnic", customerResult.getEthnic());
				checkinUser.set("CardType", customerResult.getCardType());
				checkinUser.set("FromAddress", customerResult.getFromAddress());
				checkinUser.set("Address", customerResult.getAddress());
				checkinUser.set("Updatetime", new Date());
				checkinUser.set("Createtime", new Date());
				checkinService.saveOrUpdatePmsInCheckUser(checkinUser);
			}
			customerResult.getCardType();
			datalist.add(bean);
		}
		Map tempMap = new HashMap();
		tempMap.put("hotelid", hotelId);
		tempMap.put("customerno", datalist);
		PmsOrderService pmsOrderService = AppUtils.getBean(PmsOrderService.class);
		Map map = pmsOrderService.saveCustomerNo(tempMap);
	}
	
	private void removeOldList(List<PmsOrder> oldlist, Map rto) {
		if(oldlist==null || oldlist.size()==0){
			return;
		}
		Iterator<PmsOrder> iter=oldlist.iterator();
		while(iter.hasNext()){
			PmsOrder old=iter.next();
			if(StringUtils.equals(String.valueOf(old.get("PmsOrderNo")),String.valueOf(rto.get("orderid"))) && StringUtils.equals(String.valueOf(old.get("PmsRoomTypeOrderNo")),String.valueOf(rto.get("batchno")))){
				iter.remove();
			}
		}
	}
	private String doPostJson(String url, String json) {
		JSONObject back = new JSONObject();
		try {
			return Tools.dopostjson(url, json);
		} catch (Exception e) {
			logger.info("doPostJson参数:{},{},异常:{}", url, json, e.getLocalizedMessage());
			e.printStackTrace();
			back.put("success", false);
			back.put("errorcode", "-1");
			back.put("errormsg", e.getLocalizedMessage());
		}
		return back.toJSONString();
	}

}
