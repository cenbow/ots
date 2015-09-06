package com.mk.ots.hotel.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.bean.TRoomTypeInfo;
import com.mk.ots.hotel.dao.RoomTypeInfoDAO;

@Service
public class RoomTypeInfoService {

	@Autowired 
	RoomTypeInfoDAO roomtypeinfoDao;
	
	public TRoomTypeInfo getTRoomTypeInfo(Long id){
		return TRoomTypeInfo.dao.findFirst("select * from t_roomtype_info where id= ?",id);
	}
	public TRoomTypeInfo findTRoomTypeInfoByRoomTypeId(Long roomtypeid){
		return roomtypeinfoDao.findTRoomTypeInfoByRoomTypeId(roomtypeid);
	}
	
	
}
