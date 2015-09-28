package com.mk.ots.hotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.bean.TCity;
import com.mk.ots.hotel.dao.CityDAO;
import com.mk.ots.hotel.model.TBusinesszoneModel;
import com.mk.ots.hotel.model.TCityModel;
import com.mk.ots.mapper.CityMapper;
import com.mk.ots.mapper.TBusinesszoneMapper;
import com.mk.ots.mapper.TCityMapper;

/**
 * 城市，地区类
 * @author LYN
 *
 */
@Service
public class CityService {

	@Autowired
	private CityDAO cityDAO=null;
	
	@Autowired
	private CityMapper cityMapper;
	
	@Autowired
	private TCityMapper tcityMapper;
	
	@Autowired
	private TBusinesszoneMapper tBusinesszoneMapper;
	
	/**
	 * 获取省
	 * @return
	 */
	public List findProvince(){
		return cityDAO.getProvince();
	}
	
	/**
	 * 获取市
	 * @param procode
	 * @return
	 */
	public List findCity(String procode){
		return cityDAO.getCity(procode);
	}
	
	/**
	 * 获取区县
	 * @param citycode
	 * @return
	 */
	public List findDistrict(String citycode){
		return cityDAO.getDis(citycode);
	}
	
	
	/**
	 * 获取可查询城市信息
	 * @return
	 */
	public List<TCity> findSelectCity() {
		return cityMapper.getSelectCity();
	}
	
	/**
	 * 查询城市信息
	 * @param citycode
	 * 参数：城市code码
	 * @return TCityModel
	 */
	public TCityModel findCityByCode(String citycode) {
	    return tcityMapper.selectByCode(citycode);
	}
	
	public TCityModel findCityById(Long cityid) {
	    return tcityMapper.selectByPrimaryKey(cityid);
	}
	
	
	/**
	 * 根据城市code 查询城市商圈
	 * @param citycode
	 * @return
	 */
	public List<TBusinesszoneModel> findBusinessZoneByCityCode(String citycode) {
		return tBusinesszoneMapper.getBusinessZoneByCityCode(citycode);
	}
	
}
