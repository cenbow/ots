package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlBuilder;

import com.mk.ots.common.enums.OtaOrderStatusEnum;

/**
 * 
 * @author zzy
 *
 */
public class OrderSqlProvider extends SqlBuilder {

	private static final String TABLE_NAME = "b_otaorder";

	public String findMyOtaOrderByMidSql(Map<String, Object> parameters) {
		StringBuffer sql = new StringBuffer();
		sql.append("select * from b_otaorder where 1=1 ");
		String mid = (String) parameters.get("mid");
		if (mid != null) {
			sql.append(" and mid = " + mid);
		}
		String hotelId = (String) parameters.get("hotelId");
		if (hotelId != null) {
			sql.append(" and hotelId = " + hotelId);
		}
		String createTime = (String) parameters.get("createTime");
		if (createTime != null) {
			sql.append(" and createTime >= " + createTime);
		}
		String endtime = (String) parameters.get("endtime");
		if (endtime != null) {
			sql.append(" and createTime <= " + endtime);
		}
		List<OtaOrderStatusEnum> statusList = (List<OtaOrderStatusEnum>) parameters.get("orderStatus");
		if (statusList != null) {
			sql.append(" and orderStatus in (" + getIn(statusList) + ")");
		}
		Boolean canshow = (Boolean) parameters.get("canshow");
		if (canshow != null && canshow) {
			sql.append(" and canshow = '" + (canshow ? "T" : "F") + "'");
		} else {
			sql.append(" and canshow = 'T'");
		}
		Integer start = (Integer) parameters.get("start");
		Integer limit = (Integer) parameters.get("limit");
		sql.append("order by createTime desc limit ").append(start).append(", ").append(limit);
		return sql.toString();
	}

	public String getIn(List lists) {
		StringBuffer in = new StringBuffer();
		for (Object e : lists) {
			in.append("'").append(e.toString()).append("',");
		}
		if (in.length() > 0) {
			in.setLength(in.length() - 1);
		}
		return in.toString();
	}

}
