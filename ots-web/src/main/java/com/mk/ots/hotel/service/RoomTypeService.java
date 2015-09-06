package com.mk.ots.hotel.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.dao.RoomTypeDAO;
import com.mk.ots.hotel.model.TRoomTypeModel;
import com.mk.ots.mapper.TRoomTypeMapper;

@Service
public class RoomTypeService {

	@Autowired 
	RoomTypeDAO roomtypeDao;
	
	@Autowired
	TRoomTypeMapper tRoomTypeMapper;
	
	public TRoomType getTRoomType(Long id){
		return TRoomType.dao.findById(id);
	}
	
	/**
	 * 新增troomtype
	 * @param tRoomType
	 * @return
	 */
	public int saveTRoomType(TRoomTypeModel tRoomType){
		return tRoomTypeMapper.saveTRoomType(tRoomType);
	}

	public int delTRoomType(Long id) {
		return tRoomTypeMapper.delTRoomTypeById(id);
	}

	public int updateTRoomType(TRoomTypeModel tRoomType) {
		return tRoomTypeMapper.updateTRoomType(tRoomType);
	}

	public List<TRoomTypeModel> findTRoomTypeByHotelid(Long hotelId) {		
		return tRoomTypeMapper.findTRoomTypeByHotelid(hotelId);
	}
}
