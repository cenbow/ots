package com.mk.ots.mapper;


import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;

import java.util.List;
import java.util.Map;

public interface RoomSaleConfigInfoMapper {

	public List<TRoomSaleConfigInfo> queryByPromoType(Map<String, Object> map);
	
    public List<TRoomSaleConfigInfo> queryRoomSaleConfigInfoList(Map<String, Object> map);

    public int saveRoomSaleConfigInfo(Map<String, Object> map );

    public TRoomSaleConfigInfo queryRoomSaleConfigById(Integer Id);

    public int updateTRoomSaleConfigInfo(Map<String, Object> map);
    
    public List<TRoomSaleConfigInfo> queryListBySaleTypeId(Map<String, Object> parameters);

    public TRoomSaleConfigInfo getRoomSaleConfigInfoByConfigId(TRoomSaleConfig tRoomSaleConfig);
}
