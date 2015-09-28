package com.mk.ots.hotel.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.bean.TRoomTypeBed;
import com.mk.ots.hotel.bean.TRoomTypeBedLength;
import com.mk.ots.hotel.bean.TRoomTypeInfo;

/**
 * 
 * @author LYN
 *
 */
@Repository
public class RoomTypeDAO {

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
	
	/**
	 * 查询房间床
	 * @param roomRoomTypeids
	 * @return
	 */
	public List<TRoomTypeBed> findTRoomTypeBedByRoomTypeIds(Collection<Long> roomRoomTypeids){
		
		String whereStr="";
		int count=roomRoomTypeids.size();
		for(int i=0;i<count;i++){
			whereStr+="?,";
		}
		whereStr=whereStr.substring(0, whereStr.length()-1);	
		String sql="select a.id,a.roomtypeid,a.num,a.bedtype,a.otherinfo,b.name "
				+ "from "+TRoomTypeBed.TABLE_NAME+" a left join "+TRoomType.TABLE_NAME+" b on a.bedtype=b.id ";
		List<TRoomTypeBed> list= new ArrayList();
		if(whereStr.length()>0){
			sql=sql+"where roomtypeid in("+whereStr+")";
			list = TRoomTypeBed.dao.find(sql, roomRoomTypeids.toArray());
		}else{
			list = TRoomTypeBed.dao.find(sql);
		}
		
		if(list.size()<1){
			return list;
		}
		
		Set<Long> bedIds=new HashSet<>();
		for (TRoomTypeBed bean : list) {
			bedIds.add(bean.getLong("id"));
		}
		whereStr= whereStr.substring(0, whereStr.length()-1);
		//床长信息显示
		List<TRoomTypeBedLength> bedLengths=findTRoomTypeBedLengthByRoomIds(bedIds);
//		for (int i = 0; i < list.size(); i++) {
//			for (Bean bedBean : llist) {
//				if(bedLength.getRoomTypeBed().equals(list.get(i))){
//					if(list.get(i).getRoomTypeBedLengthList()==null){
//						list.get(i).setRoomTypeBedLengthList(new ArrayList<TRoomTypeBedLength>());
//					}
//					list.get(i).getRoomTypeBedLengthList().add(bedLength);
//				}
//			}
//		}	
		return list;
	}
	
	public List<TRoomTypeBedLength> findTRoomTypeBedLengthByRoomIds(Collection<Long> roomRoomTypeBedIds){
		String whereStr="";
		for (Long bean : roomRoomTypeBedIds) {
			whereStr+="?,";
		}
		List<Bean> llist= new ArrayList();
		String bsql="select a.id,a.bedlengthid,a.roomtypebedid,b.bedtypeid,b.name,b.visible "
				+ "from  t_roomtyle_length a left join t_bedlengthtype b on a.bedlengthid=b.id ";
		if(whereStr.length()>0){
			bsql=bsql+" where a.roomtypebedid in("+whereStr+")";
			llist = Db.find(bsql,roomRoomTypeBedIds.toArray());
		}else{
			llist = Db.find(bsql);
		}
		
//		QueryCriteria query=QueryCriteria.newInstance();
//		query.addAllClassField(TRoomTypeBedLength.class);
//		query.addAllClassField(TBedLengthType.class);
//		FromClause from=FromClause.newInstance(TableCriteria.newInstance(TRoomTypeBedLength.class));
//		from.leftJoin(TableCriteria.newInstance(TBedLengthType.class),Condition.equalTo(
//				FieldCriteria.newInstance(TRoomTypeBedLength.class, "bedLengType"), 
//				FieldCriteria.newInstance(TBedLengthType.class, "id")));
//		WhereClause where=WhereClause.newInstance(Condition.in(FieldCriteria.newInstance(TRoomTypeBedLength.class, "roomTypeBed"), roomRoomTypeBedIds));
//		QueryTemplate qt=QueryTemplate.newInstance(query, from,where).setResultClass(TRoomTypeBedLength.class);
//		List<TRoomTypeBedLength> list=this.queryObjects(qt);
		return null;
	}
	
	public TRoomType findTRoomType(Long roomTypeId){
		return TRoomType.dao.findById(roomTypeId);
	}
	
	public Bean findRoomTypeBean(Long roomTypeId){
		String sql="select id,thotelid,ehotelid,name,pms,bednum,roomnum,cost from t_roomtype where id=?";
		return Db.findFirst(sql, roomTypeId);
	}
	
	public List<TRoom> findTRoomTypeRooms(Long roomTypeId) {
		return TRoom.dao.find("select * from t_room r where r.roomtypeid =?", roomTypeId);
	}
	
	public TRoomType getTRoomTypeByEhotelidPMS(String ehotelid,String pms){
		String sql="select * from t_roomtype where ehotelid=? and pms=?";
		return TRoomType.dao.findFirst(sql, ehotelid,pms);
	}
}
