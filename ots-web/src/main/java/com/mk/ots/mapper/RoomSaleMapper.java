package com.mk.ots.mapper;

import com.mk.ots.room.sale.model.TRoomSale;

import java.util.List;
/**
 * RoomSaleMapper.
 * @author kangxiaolong.
 *
 */
public interface RoomSaleMapper{
	public  List<TRoomSale> getSaleRoomListByHotel();
	public  TRoomSale getOneRoomSale(TRoomSale bean);
	public  List<TRoomSale> queryRoomSale(TRoomSale bean);
}
