package com.mk.ots.price.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.order.bean.OtaRoomPrice;
import com.mk.ots.order.dao.BaseDAO;
import com.mk.ots.price.bean.TPrice;

@Repository
public class PriceDAO extends BaseDAO{

	public List<TPrice> findPrice(Long roomTypeId){
		String sql="select p.id,p.roomtypeid,p.timeid, p.price,p.subprice,p.subper,p.orderindex,"
				+ "t.name,t.cron,t.addcron,t.subcron,t.hotelid,t.updatetime,t.begintime,t.endtime from t_price p "
				+ " left join t_pricetime t on p.timeid=t.id where p.roomtypeid=?";
		return TPrice.dao.find(sql, roomTypeId);
	}
	
	public List<OtaRoomPrice> findOtaRoomPriceByOrder(Long otaOrderId){
		String sql = "select op.* from b_OtaRoomPrice op, b_otaroomorder orr where op.OtaRoomOrderId = orr.id and orr.OtaOrderId=?";
		return OtaRoomPrice.dao.find(sql, otaOrderId);
	}
	
	public void deletePriceByRoomOrder(Long roomOrderId) {
		List<OtaRoomPrice> roomPrices = OtaRoomPrice.dao.find("select * from  b_OtaRoomPrice where OtaRoomOrderId=?", roomOrderId);
		for (OtaRoomPrice otaRoomPrice : roomPrices) {
			otaRoomPrice.delete();
		}
	}
	
	public void saveOrUpdate(OtaRoomPrice roomPrice){
		if (roomPrice.get("id") != null) {
			 roomPrice.update();
		} else {
			 roomPrice.save();
		}
	}

	public List<Bean> findCostTempPrice(Long hotelId, String start, String end) {
		String sql="select roomtypeid,min(cost) as cost from b_costtemp_310000 where hotelid=? and time between ? and ? group by roomtypeid";
		return Db.find(sql, hotelId, start, end);
	}
	
}
