package com.mk.ots.hotel.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import cn.com.winhoo.mikeweb.hotelpojo.THotel;
import cn.com.winhoo.mikeweb.orderpojo.CostTemp;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;

/**
 * 实时房型房价、数量计算缓存
 * 
 * @author LYN
 *
 */
@Repository
public class CostTempDAO {

	public void deleteDateBefore(String citycode, String thotelid,
			Long roomTypeId, String date) {
		if (StringUtils.isBlank(citycode)) {
			throw new RuntimeException("找不到酒店对应的城市");
		}

		List params = new ArrayList();
		params.add(roomTypeId);
		String tableName = "b_costtemp_" + citycode;
		String sql = "delete from " + tableName + " where roomtypeid=? ";
		if (StringUtils.isNotBlank(date)) {
			sql += " and time<=?";
			params.add(date);
		}
		Db.update(sql, params.toArray());
	}

	public boolean updateCostTemp(String cityid, String citycode,
			Long roomTypeId, String dateStr, Integer num, BigDecimal cost) {
		if (StringUtils.isBlank(citycode)) {
			throw new RuntimeException("找不到酒店对应的城市");
		}
		String tableName = "b_costtemp_" + citycode;
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE " + tableName + " SET Num = '" + num + "',Cost = '"
				+ cost + "' ");
		sb.append("WHERE hotelid = '" + cityid + "' and Roomtypeid = '"
				+ roomTypeId + "' and Time = '" + dateStr + "'");
		String sql = sb.toString();
		int flag = Db.update(sql);
		if (flag > 0) {
			return true;
		}
		return false;
	}

	public void saveOrUpdate(String citycode, CostTemp costTemp) {
		if (StringUtils.isBlank(citycode)) {
			throw new RuntimeException("找不到酒店对应的城市");
		}
		String tableName = "b_costtemp_" + citycode;
		// 插入数据
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO " + tableName
				+ "(hotelid,Roomtypeid,Num,Cost,Time) ");
		sb.append("VALUES(");
		sb.append(costTemp.getHotelId() + "," + costTemp.getRoomTypeId() + ","
				+ costTemp.getNum() + "," + costTemp.getCost() + ","
				+ costTemp.getTime());
		sb.append(")");
		String sql = sb.toString();
		Db.update(sql);
	}
	
	public List<Bean> findRoomTypeIdList(String citycode,String thotelid) {
		if(StringUtils.isBlank(citycode)){
			throw new RuntimeException("找不到酒店对应的城市");
		}
		
			String tableName = "b_costtemp_"+citycode;
	        //插入数据
	        StringBuilder sb = new StringBuilder();
	        sb.append("SELECT distinct roomtypeid FROM "+tableName);
	        sb.append(" WHERE hotelid = ?");
	        return Db.find(sb.toString(),thotelid);
	}
}
