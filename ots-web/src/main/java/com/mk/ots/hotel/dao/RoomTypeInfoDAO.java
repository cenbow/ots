package com.mk.ots.hotel.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.mk.ots.hotel.bean.TRoomTypeInfo;

/**
 * 
 * @author LYN
 *
 */
@Repository
public class RoomTypeInfoDAO {

	/**
	 * 查询房间类型信息
	 * @param ids
	 * @return
	 */
	public List<TRoomTypeInfo> findTRoomTypeInfoByRoomTypeIds(Set<Long> ids) {
		if(ids==null ||ids.size()==0){
			return Collections.emptyList();
		}
		int count=ids.size();
		String whereStr="";
		for(int i=0;i<count;i++){
			whereStr+="?,";
		}
		whereStr=whereStr.substring(0, whereStr.length()-1);		
		String sql="select * from " +TRoomTypeInfo.TABLE_NAME;
		List<TRoomTypeInfo> result =new ArrayList();
		if(whereStr.length()!=0){
			sql+= " where roomtypeid in("+whereStr+")";
			result=TRoomTypeInfo.dao.find(sql, ids.toArray());
		}else{
			result= TRoomTypeInfo.dao.find(sql);
		}
		return result;
	}
	
	public TRoomTypeInfo findTRoomTypeInfoByRoomTypeId(Long roomtypeid){
		return TRoomTypeInfo.dao.findFirst("select * from t_roomtype_info where roomtypeid=?", roomtypeid);
	}
}
