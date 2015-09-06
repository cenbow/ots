package com.mk.ots.order.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.order.bean.OtaRoomOrder;
@Repository
public class RoomOrderDAO extends BaseDAO {
	public static final String table = "b_otaroomorder";

	/**
	 * 
	 * @param orderId
	 * @param statusList
	 * @return
	 */
	public List<OtaRoomOrder> findOtaRoomOrderByOrderId(Long orderId, List<OtaOrderStatusEnum> statusList) {
		StringBuffer sql = new StringBuffer("select * from  b_otaroomorder t where 1=1 and OtaOrderId = ?");
		List<Object> paras = new ArrayList<Object>();
		paras.add(orderId);
		if (statusList != null && statusList.size() > 0) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") "); 
		}
		return OtaRoomOrder.dao.find(sql.toString(), paras.toArray());
	}

	public int changeRoomOrderPriceType(Long orderId) {
		return Db.update("update b_otaroomorder t set t.OrderType=? WHERE t.OtaOrderId=?", OrderTypeEnum.PT.getId(),
				orderId);
	}
	
	/**
	 * 根据orderid删除roomorder数据
	 * @param orderId
	 * @return
	 */
	public int delectRoomOrderByOrderId(Long orderId) {
		return Db.update("delete from b_otaroomorder where OtaOrderId = ?", orderId);
	}
	
	/**
	 * @param id
	 * @param status
	 */
	public void updateCancelByOrderID(Long id,OtaOrderStatusEnum status) {
		Db.update("update b_otaroomorder set orderStatus=? where id = ?", status.toString(), id);
	}
	
	/**
	 * @param roomId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public List<OtaRoomOrder> findRoomOrderByRoomIdAndTime(Long roomId,	Long beginTime, Long endTime) {
		String sql = "select * from  b_otaroomorder t where 1=1 and RoomId = ? and beginTime > ? and endTime <= ?";
		return OtaRoomOrder.dao.find(sql, roomId, beginTime, endTime);
	}

	/**
	 * 根据pmsroomorderno、hotelid查询客单信息
	 * @param roomId
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	public OtaRoomOrder findRoomOrderByPmsRoomOrderNoAndHotelId(String pmsRoomOrderNo, Long hotelId) {
		// 通过pms房间订单号和酒店id查询房间订单
		return OtaRoomOrder.dao.findFirst("select * from b_otaroomorder where hotelId = ? and pmsRoomOrderNo = ? order by id desc", hotelId, pmsRoomOrderNo);
	}
	
	/**
	 * 根据id获取客单信息
	 * @param roomId
	 * @return
	 */
	public OtaRoomOrder findOtadRoomOrderById(long roomId){
		return OtaRoomOrder.dao.findById(roomId);
	}
	
	/**
	 * 查询订单的客单信息
	 * @param orderId
	 * @return
	 */
	public OtaRoomOrder findOtadRoomOrderByOtaOrderId(long orderId){
		return OtaRoomOrder.dao.findFirst("select * from b_otaroomorder where otaorderid=?", orderId);
	}
}
