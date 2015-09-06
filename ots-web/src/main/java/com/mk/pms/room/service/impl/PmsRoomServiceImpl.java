/**
 * 
 */
package com.mk.pms.room.service.impl;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.mapper.EHotelMapper;
import com.mk.ots.mapper.RoomLockPoMapper;
import com.mk.ots.mapper.RoomRepairPoMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.mapper.TRoomMapper;
import com.mk.pms.room.bean.RoomLockPo;
import com.mk.pms.room.bean.RoomLockPoExample;
import com.mk.pms.room.bean.RoomRepairPo;
import com.mk.pms.room.bean.RoomRepairPoExample;
import com.mk.pms.room.service.PmsRoomService;

/**
 * @author jianghe
 * p2o 2.0
 *
 */
@Service
public class PmsRoomServiceImpl implements PmsRoomService {

	@Autowired
	private RoomRepairPoMapper roomRepairPoMapper;
	
	@Autowired
	private RoomLockPoMapper roomLockPoMapper;
	
	@Autowired
	private EHotelMapper eHotelMapper;
	@Autowired
	private THotelMapper tHotelMapper;
	@Autowired
	private TRoomMapper tRoomMapper;
	/**
	 * @param roomPair
	 * 保存维修记录
	 */
	@Override
	public int saveRoomRepair(RoomRepairPo roomRepairPo) {
		return roomRepairPoMapper.insertSelective(roomRepairPo);
	}

	/**
	 * @param roomPairs
	 * 批量保存维修记录
	 */
	@Override
	public int batchSaveRoomRepairs(List<RoomRepairPo> roomRepairs) {
		int i=0;
		for (RoomRepairPo roomRepair:roomRepairs) {
			i+=saveRoomRepair(roomRepair);
		}
		return i;
	}

	/**
	 * @param roomPair
	 * 更新维修记录
	 */
	@Override
	public int updateRoomRepair(RoomRepairPo roomRepair) {
		RoomRepairPoExample roomRepairPoExample = new RoomRepairPoExample();
		roomRepairPoExample.createCriteria().andHotelidEqualTo(roomRepair.getHotelid()).andRepairidEqualTo(roomRepair.getRepairid());
		return roomRepairPoMapper.updateByExampleSelective(roomRepair, roomRepairPoExample);
	}

	/**
	 * @param pmsRepairId
	 * 通过pms维修id删除
	 */
	@Override
	public int deleteRoomRepairByConds(Long HotelId,String roomRepairId) {
		RoomRepairPoExample roomRepairPoExample = new RoomRepairPoExample();
		roomRepairPoExample.createCriteria().andHotelidEqualTo(HotelId).andRepairidEqualTo(roomRepairId);
		return roomRepairPoMapper.deleteByExample(roomRepairPoExample);
	}

	@Override
	public int saveRoomLock(RoomLockPo roomLockPo) {
		roomLockPo.setCreatetime(Calendar.getInstance().getTime());
		return roomLockPoMapper.insertSelective(roomLockPo);
	}

	@Override
	public int updateRoomLock(RoomLockPo roomLockPo) {
		roomLockPo.setUpdatetime(Calendar.getInstance().getTime());
		return roomLockPoMapper.updateByPrimaryKeySelective(roomLockPo);
	}

	@Override
	public RoomLockPo queryRoomLockByConds(long pmsorderid) {
		RoomLockPoExample roomLockPoExample = new RoomLockPoExample();
		roomLockPoExample.createCriteria().andPmsorderidEqualTo(pmsorderid);
		List<RoomLockPo> list = roomLockPoMapper.selectByExample(roomLockPoExample);
		if(list!=null && list.size()>0){
			return (RoomLockPo)list.get(0);
		}else{
			return null;
		}
	}


	@Override
	public int deleteRoomLock(Long id) {
		return roomLockPoMapper.deleteByPrimaryKey(id);
	}

	@Override
	public EHotelModel selectEhotelByPms(String pms) {
		return eHotelMapper.selectByPms(pms);
	}

	@Override
	public TRoomModel selectTroomByPms(Map<String, String> map) {
		return tRoomMapper.selectByPms(map);
	}

	@Override
	public THotelModel selectThotelByPms(String pms, Long time) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("pms", pms);
		map.put("time", time);
		return tHotelMapper.selectByPmsAndTime(map);
	}

}
