package com.mk.ots.hotel.dao;

import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Db;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.order.bean.PmsRoomOrder;

@Repository
public class EHotelDAO {

	private static final Logger logger = LoggerFactory.getLogger(EHotelDAO.class);

	public EHotel findEHotelByid(Long id) {
		return EHotel.dao.findFirst("select * from e_hotel t where t.id=?", id);
	}

	public Long findEHotelIdByPms(String pms) {
		return Db.queryLong("select id from e_hotel t where t.pms=?", pms);
	}

	public Long findRoomTypeIdByPms(Long hotelId, String pms) {
		return Db.queryLong("select id from t_roomtype t where t.ehotelid=? and pms=?", hotelId, pms);
	}

	public Long findRoomIdByPms(Long hotelId, String pms) {
		return Db.queryLong("select id from t_room t where t.roomtypeid=? and pms=?", hotelId, pms);
	}
	
	public String getCityIdByHotelId(Long hotelId){
		return Db.queryStr("select c.`Code` from t_city c, t_district d where d.CityID = c.cityid and d.id=?", hotelId);
	}

	public PmsRoomOrder findPmsRoomOrderByHotelIdAndCustomno(Long hotelId, String customerno) {
		// QueryCriteria query=QueryCriteria.newInstance();
		// query.addAllClassField(PmsRoomOrder.class);
		// FromClause
		// from=FromClause.newInstance(TableCriteria.newInstance(PmsRoomOrder.class));
		// Condition
		// cond1=Condition.equalTo(FieldCriteria.newInstance(PmsRoomOrder.class,
		// "hotelId"),hotelId);
		// Condition
		// cond2=Condition.equalTo(FieldCriteria.newInstance(PmsRoomOrder.class,
		// "pmsRoomOrderNo"),customerno);
		// AND and=AND.newInstance(cond1,cond2);
		// WhereClause where=WhereClause.newInstance(and);
		// QueryTemplate qt=QueryTemplate.newInstance(query,
		// from,where).setResultClass(PmsRoomOrder.class);

		List<PmsRoomOrder> list = PmsRoomOrder.dao.find("select * from b_pmsroomorder where hotelId = ? and pmsRoomOrderNo = ?", hotelId, customerno);
		if (list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			EHotelDAO.logger.warn("酒店id：" + hotelId + "中 酒店预订单号为：" + hotelId + "的客单号" + customerno + "已经存在" + list.size() + "条记录！！！！");
		}
		return list.get(0);
	}

	public TRoom findTRoomByHotelIdAndPmsno(Long hotelId, String roomPmsNo) {
		// QueryCriteria query=QueryCriteria.newInstance();
		// query.addAllClassField(TRoom.class);
		// query.addAllClassField(TRoomType.class);
		// FromClause
		// from=FromClause.newInstance(TableCriteria.newInstance(TRoom.class));
		// from.innerJoin(TableCriteria.newInstance(TRoomType.class),
		// Condition.equalTo(FieldCriteria.newInstance(TRoom.class, "roomType"),
		// FieldCriteria.newInstance(TRoomType.class, "id")));
		// Condition
		// cond1=Condition.equalTo(FieldCriteria.newInstance(TRoomType.class,
		// "ehotel"),hotelId);
		// Condition
		// cond2=Condition.equalTo(FieldCriteria.newInstance(TRoom.class,
		// "pms"),roomPmsNo);
		// AND and=AND.newInstance(cond1,cond2);
		// WhereClause where=WhereClause.newInstance(and);
		// QueryTemplate qt=QueryTemplate.newInstance(query,
		// from,where).setResultClass(TRoom.class);
		List<TRoom> list = TRoom.dao.find("");
		if (list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			EHotelDAO.logger.warn("酒店id：" + hotelId + "中 pms号为：" + roomPmsNo + "存在" + list.size() + "条房间记录！！！！");
		}
		return list.get(0);
	}

	/**
	 * @param o
	 * @return 影响的行数
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 */
	public Long deleteObjectByPojoKey(Object o) {
		// try {
		// List<FieldInfo> pfs =
		// ClassInfoCacheHandler.getInstance().getClassInfo(o.getClass()).getPrimaryKeyFields();
		// String sql = "";
		// List<Object> params = new ArrayList<Object>();
		// for (FieldInfo pf : pfs) {
		// if (StringUtils.isNotEmpty(sql))
		// sql += " and ";
		// sql += pf.getColName() + "=?";
		// params.add(pf.get(o));
		// }
		// sql = "delete from " + getTableName(o.getClass()) + " where " + sql;
		// return execute(sql, params).get(0).longValue();
		return Long.parseLong("0");
		// } catch (Exception e) {
		// throw new RuntimeException(e);
		// }
	}

	public TRoomType findTRoomTypeByPmsno(Long hotelId, String pmsno) {
		String sql = "select * from t_roomtype where ehotelid=? and pms=?";
		return TRoomType.dao.findFirst(sql, hotelId, pmsno);
	}

}
