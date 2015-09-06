package com.mk.ots.order.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.OSTypeEnum;
import com.mk.ots.common.enums.OrderTypeEnum;
import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.common.enums.PromotionTypeEnum;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.HQrCode;
import com.mk.ots.order.bean.BHotelPromotionPrice;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.bean.OtaRoomOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.ots.order.bean.UUseTicketRecord;
import com.mk.ots.order.service.OrderUtil;
import com.mk.ots.promo.model.BPromotion;
import com.mk.ots.promo.model.BPromotionPrice;
import com.mk.ots.ticket.model.UTicket;

/**
 * otaorder dao
 *
 * @author zzy
 *
 */
@Repository
public class OrderDAO extends BaseDAO {

	public static final String table = "b_otaorder";

	/**
	 * 根据订单id查
	 *
	 * @param otaOrderId
	 * @return
	 */
	public OtaOrder findOtaOrderById(Long otaOrderId) {
		return OtaOrder.dao.findById(otaOrderId);
	}

	/**
	 * 根据订单id，canshow 查
	 *
	 * @param otaOrderId
	 * @param canshow
	 * @return
	 */
	public OtaOrder findOtaOrderByIdCanShow(Long otaOrderId, Boolean canshow) {
		return Db.queryFirst("select * from b_otaorder where otaOrderId = ? and canshow = ?", otaOrderId, canshow);
	}

	public BPromotion findPromotion(long promotionId) {
		return BPromotion.dao.findById(promotionId);
	}

	/**
	 * 查otaorder
	 *
	 * @param Mid
	 * @param hotelId
	 * @param statusList
	 * @param begintime
	 * @param endtime
	 * @param start
	 * @param limit
	 * @param canshow
	 * @return
	 */
	public List<OtaOrder> findMyOtaOrderByMid(Long Mid, Long hotelId, List<OtaOrderStatusEnum> statusList, String begintime, String endtime, Integer start, Integer limit, Boolean canshow,
			String isscore, List<OtaOrderStatusEnum> notStatusList) {
		StringBuffer sql = new StringBuffer();
		List<Object> paras = this.getMyOtaOrderByMidSql(Mid, hotelId, statusList, begintime, endtime, start, limit, canshow, isscore, sql, notStatusList);
		// List<Long> ids = Db.query(sql.toString(), paras.toArray());
		return OtaOrder.dao.find(sql.toString(), paras.toArray());
	}

	/**
	 * @param Mid
	 * @param hotelId
	 * @param statusList
	 * @param begintime
	 * @param endtime
	 * @param start
	 * @param limit
	 * @param canshow
	 * @return
	 */
	public Long countMyOtaOrderByMid(Long Mid, Long hotelId, List<OtaOrderStatusEnum> statusList, String begintime, String endtime, Integer start, Integer limit, Boolean canshow, String isscore,
			List<OtaOrderStatusEnum> notStatusList) {
		StringBuffer sql = new StringBuffer();
		List<Object> paras = this.getMyOtaOrderByMidSql(Mid, hotelId, statusList, begintime, endtime, start, limit, canshow, isscore, sql, notStatusList);
		return Db.queryLong("select count(*) from (" + sql.toString() + ") t", paras.toArray());
	}

	private List<Object> getMyOtaOrderByMidSql(Long Mid, Long hotelId, List<OtaOrderStatusEnum> statusList, String begintime, String endtime, Integer start, Integer limit, Boolean canshow,
			String isscore, StringBuffer sql, List<OtaOrderStatusEnum> notStatusList) {
		List<Object> paras = new ArrayList<>();
		sql.append("select * from b_otaorder where 1=1 ");
		if (Mid != null) {
			sql.append(" and mid = ?");
			paras.add(Mid);
		}
		if (hotelId != null) {
			sql.append(" and hotelId = ?");
			paras.add(hotelId);
		}
		if (begintime != null) {
			sql.append(" and createTime >= ?");
			paras.add(begintime);
		}
		if (endtime != null) {
			sql.append(" and createTime <= ?");
			paras.add(endtime);
		}
		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") ");
		} else {
			// 查询不包含的东西
			if ((notStatusList != null) && (notStatusList.size() > 0)) {
				sql.append(" and orderStatus not in (");
				for (Object s : notStatusList) {
					sql.append("?,");
					paras.add(s.toString());
				}
				sql.setLength(sql.length() - 1);
				sql.append(") ");
			}
		}

		if ((canshow != null) && canshow) {
			sql.append(" and canshow = '" + (canshow ? "T" : "F") + "'");
		} else {
			sql.append(" and canshow = 'T'");
		}
		if (isscore != null) {
			if (isscore.equals("T")) {
				sql.append(" and isscore = ? ");
				paras.add(isscore);
			} else {
				sql.append(" and (isscore = ? or isscore is null) ");
				paras.add(isscore);
			}
		}

		sql.append(" order by createTime desc ");
		if ((start != null) && (limit != null)) {
			sql.append(" limit ").append(start).append(", ").append(limit);
		}
		return paras;
	}

	/**
	 * 根据订单状态查询
	 *
	 * @param mid
	 * @param statusList
	 * @return
	 */
	public List<OtaOrder> findOtaOrderByMid(Long mid, List<OtaOrderStatusEnum> statusList) {
		StringBuffer sql = new StringBuffer("select * from b_otaorder where 1=1 ");
		List<Object> paras = new ArrayList<>();
		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") ");
		}
		return Db.query(sql.toString(), paras.toArray());
	}

	/**
	 *
	 * @param statusList
	 * @param time
	 * @return
	 */
	public List<Long> findOtaOrderByStatusAndTime(List<OtaOrderStatusEnum> statusList, int time) {
		StringBuffer sql = new StringBuffer("select id from b_otaorder where 1=1 ");
		List<Object> paras = new ArrayList<>();
		sql.append(" and orderType = ?");
		paras.add(OrderTypeEnum.YF.getId());

		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") ");
		}
		sql.append(" and createTime <= ?");
		paras.add(DateUtils.getDatetime(DateUtils.addMinutes(DateUtils.createDate(), (time * -1))));
		return Db.query(sql.toString(), paras.toArray());
	}

	/**
	 * @param statusList
	 * @param time
	 * @param count
	 * @param id
	 * @return
	 */
	public List<Long> findOtaOrderByStatusAndTime_TOP(List<OtaOrderStatusEnum> statusList, int time, int count, Long id) {
		List<Object> paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select id from b_otaorder where 1=1 ");
		sql.append(" and orderType = ?");
		paras.add(OrderTypeEnum.YF.getId());
		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") ");
		}
		sql.append(" and createTime <= ?");
		paras.add(DateUtils.getDatetime(DateUtils.addMinutes(DateUtils.createDate(), (time * -1))));
		if (id != null) {
			sql.append(" and id > ?");
			paras.add(id);
		}
		sql.append(" order by id asc limit 0, ?");
		paras.add(count);
		return Db.query(sql.toString(), paras.toArray());
	}

	/**
	 * @param statusList
	 * @return
	 */
	public List<Long> findOtaOrderByStatus(List<OtaOrderStatusEnum> statusList) {
		List paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select id from b_otaorder where 1=1 ");
		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
				paras.add(s.toString());
			}
			sql.setLength(sql.length() - 1);
			sql.append(") ");
		}
		return Db.query(sql.toString(), paras.toArray());
	}

	public List<UTicket> findTicketsByMemberId(Long mid) {
		List<Object> paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select t.* from b_promotion p , u_ticket t where p.id = t.promotionid ");
		sql.append(" and mid = ?");
		paras.add(mid);
		return Db.query(sql.toString(), paras.toArray());
	}

	public Long changeOrderPriceType(Long orderId) {
		String sql = "update b_otaorder set orderType = ? where id=?";
		return (long) Db.update(sql, OrderTypeEnum.PT.getId(), orderId);
	}

	/**
	 * @param roomOrder
	 */
	public void updateOtaRoomOrderToPMS(OtaRoomOrder roomOrder) {
		roomOrder.save();
	}

	/**
	 * @param orderId
	 * @param promoId
	 * @return
	 */
	public BPromotionPrice getPromotionPriceByOrderId(Long orderId, Long promoId) {
		String sql = "select * from b_promotion_price where otaorderid = ? and promotion = ?";
		return BPromotionPrice.dao.findFirst(sql, orderId, promoId);
	}

	/**
	 * @param orderId
	 * @return
	 */
	public List<BPromotionPrice> findPromotionPricesByOrderId(Long orderId) {
		String sql = "select t.* from b_promotion p , b_promotion_price t where p.id = t.promotion and t.otaorderid = ?";
		return BPromotionPrice.dao.find(sql, orderId);
	}

	/**
	 * @param mid
	 * @param hotelId
	 * @return
	 */
	public Long checkQiekeWebPromoUsed(Long mid, Long hotelId) {
		List<Object> paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select * from b_promotion p, b_promotion_price pp, b_otaorder o where o.id = pp.otaorderid and p.id = pp.promotion");
		sql.append(" and p.type=?");
		paras.add(PromotionTypeEnum.qieke.getId());
		sql.append(" and o.mid=?");
		paras.add(mid);
		sql.append(" and o.ostype=?");
		paras.add(OSTypeEnum.H.getId());
		sql.append(" and o.orderStatus < ?");
		paras.add(OtaOrderStatusEnum.CancelByU_WaitRefund.getId());
		sql.append(" and o.orderStatus >= ?");
		paras.add(OtaOrderStatusEnum.NotIn.getId());
		sql.append(" and o.createTime > ?");
		paras.add(OrderUtil.getLimitDateTime());
		sql.append(" and o.hotelId = ?");
		paras.add(hotelId);
		return Db.queryLong("select count(*) from (" + sql.toString() + ")", paras.toArray());
	}

	/**
	 * @param otaOrder
	 * @param orderType
	 * @return
	 */
	public Long countQiekeBySts(OtaOrder otaOrder, OrderTypeEnum orderType) {
		List<Object> paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select p.id from b_promotion p, b_promotion_price pp, b_otaorder o where o.id = pp.otaorderid and p.id = pp.promotion");
		sql.append(" and p.type=?");
		paras.add(PromotionTypeEnum.qieke.getId());
		sql.append(" and o.mid=?");
		paras.add(otaOrder.getLong("Mid"));
		sql.append(" and o.orderType=?");
		paras.add(orderType.getId());
		sql.append(" and o.orderStatus < ?");
		paras.add(OtaOrderStatusEnum.CancelByU_WaitRefund.getId());
		sql.append(" and o.orderStatus >= ?");
		paras.add(OtaOrderStatusEnum.NotIn.getId());
		sql.append(" and o.createTime > ?");
		paras.add(OrderUtil.getLimitDateTime());
		sql.append(" and o.hotelId = ?");
		paras.add(otaOrder.getLong("id"));
		return Db.queryLong("select count(*) c from (" + sql.toString() + ") t", paras.toArray());
	}

	public void updateOrderSts(OtaOrder order) {
		order.update();
	}

	/**
	 * @param otaOrderId
	 * @return
	 */
	public BHotelPromotionPrice findHPromoPriceByOrderId(Long otaOrderId) {
		StringBuffer sql = new StringBuffer("select hp.* from b_hotel_promotionprice hp, b_otaorder o where hp.hotelId = o.id ");
		sql.append(" and o.id=?");
		return BHotelPromotionPrice.dao.findFirst(sql.toString(), otaOrderId);
	}

	public void delectOrderSpreadUser(OtaOrder order) {
		order.set("spreadUser", null);
		order.update();
	}

	/**
	 * @param otaOrder
	 * @param qiekeStartTime
	 * @return
	 */
	public List<PmsRoomOrder> getOrderIdListBySameRoom(OtaOrder otaOrder, String qiekeStartTime) {
		OtaRoomOrder otaRoomOrder = otaOrder.getRoomOrderList().get(0);
		StringBuffer sql = new StringBuffer("select * from b_pmsroomorder t where 1=1 ");
		List<Object> paras = new ArrayList<>();
		sql.append(" and hotelId = ?");
		paras.add(otaRoomOrder.getLong("hotelId"));
		sql.append(" and roomNo = ?");
		paras.add(otaRoomOrder.getLong("roomNo"));
		sql.append(" and checkInTime >= ?");
		paras.add(qiekeStartTime);
		sql.append(" and checkInTime < ?");
		paras.add(DateUtils.getDatetime());
		return PmsRoomOrder.dao.find(sql.toString(), paras.toArray());
	}

	/**
	 * @param pmsRoomOrder
	 * @return
	 */
	public Long getOrderIdListByPmsOrder(PmsRoomOrder pmsRoomOrder) {
		List<Object> paras = new ArrayList<>();
		StringBuffer sql = new StringBuffer("select id from b_otaroomorder where 1=1 ");
		sql.append(" and hotelId = ?");
		paras.add(pmsRoomOrder.getLong("hotelId"));
		sql.append(" and roomNo = ?");
		paras.add(pmsRoomOrder.getLong("roomNo"));
		sql.append(" and pmsRoomOrderNo = ?");
		paras.add(pmsRoomOrder.getStr("PmsRoomOrderNo"));
		return Db.queryLong(sql.toString(), paras.toArray());
	}

	public List<UUseTicketRecord> getTicketRecordsByOrderId(List<Long> orderIdList, Long qiekePromoId) {
		List<Object> paras = new ArrayList<>();
		StringBuffer s = new StringBuffer();
		for (Long id : orderIdList) {
			paras.add(id);
			s.append("?,");
		}
		if (s.length() > 0) {
			s.setLength(s.length() - 1);
		}
		paras.add(qiekePromoId);
		String sql = "select u.* from p_pay p , u_useticket_record u where p.id = u.payid and p.orderid in(" + s.toString() + ") and u.promotionid = ?";
		return UUseTicketRecord.dao.find(sql, paras.toArray());
	}

	public HQrCode findQrcode(long hotelid, long spreadUser) {
		return HQrCode.dao.findFirst("select * from h_qrcode where hotelid = ? and id=?", hotelid, spreadUser);
	}

	public int countOrderByOrderSts(Long orderId, OtaOrderStatusEnum sts) {
		return Db.queryInt("select count(1) from b_otaorder where id=? and orderStatus=?", orderId, sts.getId());
	}

	public boolean updateOtaOrderOrderStatus(Long orderid, int orderStatus) {
		if (Db.update(" update b_otaorder set orderStatus= " + orderStatus + "   where id=" + orderid) > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean updateOtaOrderPaystatus(Long orderid, int paystatus) {
		if (Db.update(" update b_otaorder set paystatus= " + paystatus + "   where id=" + orderid) > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * update 退款成功后修改 otaOrder 状态
	 *
	 * @return int
	 */
	public int updateSql(String sql) {
		return Db.update(sql);
	}

	/**
	 * 根据订单id查询下看是否支付成功
	 *
	 * @param otaOrderId
	 * @return
	 */
	public OtaOrder findOtaOrderById(String otaOrderId) {
		return OtaOrder.dao.findFirst("select * from b_otaorder where  Id = ?   ", otaOrderId);

		// return
		// OtaOrder.dao.findFirst("select * from b_otaorder where  Id = ? and OrderStatus = ?  and Paystatus = ?",
		// otaOrderId,OtaOrderStatusEnum.Confirm.getId().intValue(),
		// PayStatusEnum.alreadyPay.getId().intValue());
	}

	/**
	 * 查询等待支付的订单
	 *
	 * @param otaOrderId
	 * @return
	 */
	public OtaOrder getWaitPay(String otaOrderId) {
		return OtaOrder.dao.findFirst("select * from b_otaorder where  Id = ? and OrderStatus = ?  ", otaOrderId, OtaOrderStatusEnum.WaitPay.getId().intValue());
	}

	/**
	 * 判断用户是否已经有了未完成的订单
	 *
	 * @param mid
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Long findOrderCountByMid(Long mid, Date startTime, Date endTime) {
		String sql = "select count(id) c from b_otaorder t where orderStatus in (120,140,160,180,190,200,520) " + "and mid=? and t.Begintime < ? and t.Endtime > ?";
		return Db.queryLong(sql, mid, endTime, startTime);
	}

	/**
	 * 订单数量统计
	 *
	 * @param token
	 * @param orderStatus
	 */
	public Long selectCountByOrderStatus(List<String> statusList, String token) {
		List<String> paraList = new ArrayList<String>();
		paraList.add(token);
		paraList.addAll(statusList);
		StringBuffer sql = new StringBuffer();
		sql.append("select count(1) from b_otaorder where 1=1 ");
		sql.append(" and mid = " + " ( select mid from u_token where accesstoken = ? )");
		sql.append(" and canshow = 'T' ");
		if ((statusList != null) && (statusList.size() > 0)) {
			sql.append(" and orderStatus in (");
			for (Object s : statusList) {
				sql.append("?,");
			}
			sql.setLength(sql.length() - 1);
			sql.append(" ) ");
		}
		return Db.queryLong(sql.toString(), paraList.toArray());
	}

	public List<Long> getUserIds(Long hotelId) {

		String sql = "SELECT userid from h_user_role u join  h_role r on u.roleid = r.id  where r.hotelid = ? ";

		return Db.query(sql, hotelId);
	}
	
	
	/**
	 * 月销量记录(根据ID) 显示近30天内的销量数据(不包含current date)
	 * @param hotelId
	 * @return
	 */
	public Long findMonthlySaleByHotelId(Long hotelId, String beforetime, String yestertime) {
		String sql = "SELECT count(1) FROM b_otaorder WHERE HotelId = ? AND OrderStatus IN (180, 190, 200) AND Createtime BETWEEN ? AND ?";
		return Db.queryLong(sql, hotelId, beforetime, yestertime);
	}
	
	/**
	 * 月销量记录   显示近30天内的销量数据(不包含current date)
	 * @return
	 */
	public List<Bean> findAllMonthlySales(String beforetime, String yestertime) {
		String sql = "SELECT HotelId as hid, count(HotelId) cnt FROM b_otaorder WHERE OrderStatus IN (180, 190, 200) AND Createtime BETWEEN ? AND ? GROUP BY HotelId";
		return Db.find(sql, beforetime, yestertime);
	}
	
	/**
	 * 查询最近预订时间 (根据ID)
	 * @param hotelId
	 * @return
	 */
	public String findLatestTime(Long hotelId) {
		String sql = "select Createtime from b_otaorder WHERE HotelId = ? ORDER BY Createtime desc LIMIT 1";
		Date date = Db.queryDate(sql, hotelId);
		if(date==null)
			return null;
		return new SimpleDateFormat("yyyyMMddHHmmss").format(Db.queryDate(sql, hotelId));
	}
	 
	public List<OtaOrder> findOrderListByHotelIdAndChangeDate(String hotelid, String date) {
		Date datetime = DateUtils.getDateFromString(date.trim());
		if (datetime.before(DateUtils.getDateFromString("20150801"))) {
			datetime = DateUtils.getDateFromString("20150801");
		}
		StringBuffer sql = new StringBuffer();
		List<Object> paras = new ArrayList<>();
		sql.append("select * from b_otaorder where 1=1 ");
		if (hotelid != null) {
			sql.append(" and hotelid = ?");
			paras.add(Long.parseLong(hotelid.trim()));
		}
		if (hotelid != null) {
			sql.append(" and Createtime >= ?");
			paras.add(datetime);
		}
		sql.append(" and OrderStatus < 500").append(" and rulecode = 1001 ");

		return OtaOrder.dao.find(sql.toString(), paras.toArray());
	}
	
	public List<OtaOrder> findOrderListByHotelIdAndChangeDate2() {
		StringBuffer sql = new StringBuffer();
		List<Object> paras = new ArrayList<>();
		sql.append("select t.* from b_otaorder t, b_promotion_price p, b_otaroomorder rr, b_pmsroomorder pr ");
		sql.append("where t.rulecode = 1002 ");
		sql.append("and t.spreadUser is null ");
		sql.append("and t.id = p.otaorderid ");
		sql.append("and t.id = rr.OtaOrderId ");
		sql.append("and rr.Hotelid = pr.Hotelid ");
		sql.append("and rr.PMSRoomOrderNo = pr.PmsRoomOrderNo ");
		sql.append("and t.OrderStatus < 500 ");
		sql.append("and TIMESTAMPDIFF(SECOND,t.createtime,pr.checkintime) < 900 ");
		sql.append("and t.Createtime > '2015-08-01' ");
		sql.append("order by t.id ");

		return OtaOrder.dao.find(sql.toString(), paras.toArray());
	}
}
