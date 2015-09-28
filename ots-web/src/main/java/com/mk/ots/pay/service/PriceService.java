package com.mk.ots.pay.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.OtaRoomPrice;
import com.mk.ots.price.dao.PriceDAO;

@Service
public class PriceService implements IPriceService {
	
	@Autowired
	PriceDAO priceDAO;

	@Override
	public void saveOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder,
			Map<String, BigDecimal> map) {
		if(roomOrder==null){
			MyErrorEnum.errorParm.getMyException();
		}
//		Map<String, BigDecimal> map=Services.getIOrderService().getCostByOtaRoomOrder(roomOrder);
		priceDAO.deletePriceByRoomOrder(roomOrder.getLong("id"));
		Date nowDate=new Date();
		for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {  
			OtaRoomPrice roomPrice=new OtaRoomPrice();
			roomPrice.set("CreateTime",nowDate);
			roomPrice.set("OtaRoomOrderId", roomOrder.get("id"));
			roomPrice.set("PriceType", roomOrder.getInt("pricetype"));
			roomPrice.set("ActionDate",entry.getKey());
			roomPrice.set("Price",entry.getValue());
			roomPrice.set("PmsPrice",entry.getValue());
			/*TODO 时租待完成
			roomPrice.setHourNum();
			roomPrice.setOutHourNum();
			roomPrice.setOutHour();
			*/
			priceDAO.saveOrUpdate(roomPrice);
        }
	}
	
	@Override
	public void saveOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder, List<RoomTypePriceBean> roomtypeList, boolean flag) {
		if(roomOrder==null){
			MyErrorEnum.errorParm.getMyException();
		}
		priceDAO.deletePriceByRoomOrder(roomOrder.getLong("id"));
		Date nowDate=new Date();
		for (int i = 0; i < roomtypeList.size(); i++) {
			RoomTypePriceBean rtpBean = roomtypeList.get(i);
			OtaRoomPrice roomPrice=new OtaRoomPrice();
			roomPrice.set("CreateTime",nowDate);
			roomPrice.set("OtaRoomOrderId", roomOrder.get("id"));
			roomPrice.set("PriceType", roomOrder.getInt("pricetype"));
			roomPrice.set("ActionDate",rtpBean.getDay());
			if(flag){
				roomPrice.set("Price",rtpBean.getPrice());
				roomPrice.set("PmsPrice",rtpBean.getPrice());
			} else {
				roomPrice.set("Price",rtpBean.getMikeprice());
				roomPrice.set("PmsPrice",rtpBean.getMikeprice());
			}
			priceDAO.saveOrUpdate(roomPrice);
		}
	}

	@Override
	public void updateOtaRoomPriceByOtaRoomOrder(OtaRoomOrder roomOrder,
			Map<String, BigDecimal> map) {
		if(roomOrder==null||roomOrder.get("Id")==null){
			throw MyErrorEnum.errorParm.getMyException();
		}
		// OtaRoomOrder只修改 删除原来的全部 重新建
		priceDAO.deletePriceByRoomOrder(roomOrder.getLong("Id"));
//		Map<String, BigDecimal> map=Services.getIOrderService().getCostByOtaRoomOrder(roomOrder);
		Date nowDate=new Date();
		for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {  
			OtaRoomPrice roomPrice=new OtaRoomPrice();
			roomPrice.set("CreateTime",nowDate);
			roomPrice.set("OtaRoomOrderId", roomOrder.get("id"));
			roomPrice.set("PriceType", roomOrder.getInt( "PriceType"));
			roomPrice.set("ActionDate",entry.getKey());
			roomPrice.set("Price",entry.getValue());
			roomPrice.set("PmsPrice",entry.getValue());
			/*TODO 时租待完成
			roomPrice.setHourNum();
			roomPrice.setOutHourNum();
			roomPrice.setOutHour();
			*/
			priceDAO.saveOrUpdate(roomPrice);
        } 

	}

	@Override
	public List<OtaRoomPrice> findOtaRoomPriceByOrder(OtaOrder order) {
		if(order==null||order.getId()==null){
			throw MyErrorEnum.errorParm.getMyException();
		}
		return priceDAO.findOtaRoomPriceByOrder(order.getId());
	}

}
