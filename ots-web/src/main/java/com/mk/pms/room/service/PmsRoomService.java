/**
 * 
 */
package com.mk.pms.room.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.pms.room.bean.RoomLockPo;
import com.mk.pms.room.bean.RoomRepairPo;

/**
 * @author jianghe 
 * p2o 2.0
 */
public interface PmsRoomService {
	/**
	 * @param roomPair
	 * 保存维修记录
	 */
	public int saveRoomRepair(RoomRepairPo roomRepairPo);

	/**
	 * @param roomPairs
	 * 批量保存维修记录
	 */
	public int batchSaveRoomRepairs(List<RoomRepairPo> roomRepairs);

	/**
	 * @param roomPair
	 * 更新维修记录
	 */
	public int updateRoomRepair(RoomRepairPo roomRepairPo);

	/**
	 * @param pmsRepairId
	 * 通过pms维修id和hotelid删除
	 */
	public int deleteRoomRepairByConds(Long hotelId,String roomRepairId);
	
	/**
	 * @param roomLockPo
	 * 保存锁房记录
	 */
	public int saveRoomLock(RoomLockPo roomLockPo);
	/**
	 * @param roomLockPo
	 * 更新锁房记录
	 */
	public int updateRoomLock(RoomLockPo roomLockPo);
	/**
	 * @param id 主键
	 * 通过主键删除锁房记录
	 */
	public int deleteRoomLock(Long id);
	
	/**
	 * @param pmsorderid pmsRoomOrder 主键
	 * @return 查询 返回一条锁房记录
	 */
	public RoomLockPo queryRoomLockByConds(long pmsorderid);
	
	public EHotelModel selectEhotelByPms(String pms);
	public THotelModel selectThotelByPms(String pms,Long time);
	
	public TRoomModel selectTroomByPms(Map<String,String> map);
	
}
