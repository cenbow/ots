package com.mk.pms.room.bean;

import java.util.ArrayList;
import java.util.List;

public class RoomRepairJsonBean {
	/*{
		hotelid:’220’,  //酒店id
		lock:[{
			roomid:’330’,  //房间id
			begintime:’yyyyMMddHHmmss’, //开始时间
			endtime:’ yyyyMMddHHmmss’,  //结束时间
			action:’’,  //  add增加/delete取消/update修改
			id:’’  //锁房id
	    }]
	  }*/
	/*{
	    "hotelid": "222",
	    "lock": [
	        {
	            "roomid": "333",
	            "begintime": "20150617041255",
	            "endtime": "20150618051433",
	            "action": "add",
	            "id": "1"
	        },
	        {
	            "roomid": "334",
	            "begintime": "20150617041255",
	            "endtime": "20150618051433",
	            "action": "add",
	            "id": "2"
	        }
	    ]
	}*/
	String hotelid;
	List<RoomRepairLockJsonBean> lock=new ArrayList<RoomRepairLockJsonBean>();
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}
	public List<RoomRepairLockJsonBean> getLock() {
		return lock;
	}
	public void setLock(List<RoomRepairLockJsonBean> lock) {
		this.lock = lock;
	}
	
}
