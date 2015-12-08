package com.mk.ots.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.mk.ots.hotel.model.TRoomModel;


/**
 * TRoomMapper.
 * @author chuaiqing.
 *
 */
public interface TRoomMapper {
	public List<TRoomModel> findList(@Param("roomtypeid") Long roomtypeid) throws Exception;
    
    public List<TRoomModel> findRoomsByHotelId(Long hotelId);
    
    public TRoomModel selectByPms(Map<String,String> map);
    
    public int saveTRoom(TRoomModel tRoom);

	public int delRoomByRoomTypeId(Long roomTypeId);

	public int updateRoom(TRoomModel tRoom);

	public int delRoomById(Long id);
	
	public void updateRoomtypeByRoom(Map<String, Object> parameters);
	
}
