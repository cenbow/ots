package com.mk.ots.hotel.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.hotel.bean.TCity;

/**
 * 城市地区数据库操作类
 * @author LYN
 *
 */
@Repository
public class CityDAO {

	/**
	 * 获取省份
	 * @return
	 */
	public List getProvince(){
		String sql="select proid,code,proname,prosort,proremark,latitude,longitude from t_province order by prosort";
		List<Bean> provinceList= Db.find(sql);
		return provinceList;
	}

	/**
	 * 获取市
	 * @param procode
	 * @return
	 */
	public List getCity(String procode) {
		String sql="select cityid,code,cityname,proid,citysort,latitude,longitude from t_city where proid=? order by citysort";
		List<Bean> cityList= Db.find(sql,procode);
		return cityList;
	}
	
	/**
	 * 获取区县
	 * @param citycode
	 * @return
	 */
	public List getDis(String citycode){
		String sql="select id,code,disname,cityid,dissort,latitude,longitude from t_district where cityid=? order by dissort";
		List<Bean> disList = Db.find(sql,citycode);
		return disList;
	}
	
	/**
	 * 根据酒店id 查城市
	 * @param hotelId
	 * @return
	 */
	public TCity findCityByHotelId(Long hotelId) {
		String sql = "select c.* from t_city c ,t_district d, t_hotel h where c.CityID = d.CityID and d.id = h.disId and h.id = ?";
		return Db.queryFirst(sql, hotelId);
	}
	
	public Bean getCityByDisId(String disid){
		String sql="select a.id as disid,a.code as discode,a.disname,a.latitude as dislatitude,a.longitude as dislongitude,b.cityid,b.cityname,b.code as citycode,b.proid,b.latitude as citylatitude,b.longitude as citylongitude from t_district a left join t_city b on a.CityID =b.cityid where a.id=?";
		return Db.findFirst(sql,disid);
	}
}
