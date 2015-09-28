package com.mk.ots.hotel.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.order.bean.PmsRoomOrder;

/**
 * 房间DAO数据操作
 */
@Repository
public class RoomDAO {
    final Logger logger = LoggerFactory.getLogger(this.getClass());


    public List<Bean> findRoom(Long roomTypeId) {
        String sql = "select * from t_room where roomtypeid=?";
        return Db.find(sql, roomTypeId);
    }
    
    
    public List<Bean> findRoomsByRoomtypeid(String roomtypeid) {
        String sql = "select * from t_room where roomtypeid=?";
        return Db.find(sql, roomtypeid);
    }
    
    /**
     * 同步pms酒店房间信息.
     * 在新方法中使用spring来管理数据库事务.
     * @param map
     * @return
     * @throws Exception
     */
    public boolean syncPmsRooms(Map map) throws Exception {
        boolean succeed = true;
        try {
            logger.info("syncPmsRooms method begin...");
            logger.info("parameters is : {}", map.toString());
            final List updaterooms = (List) map.get("updaterooms");
            final List delrooms = (List) map.get("delrooms");
            final List addrooms = (List) map.get("addrooms");
            final List updateroomtype = (List) map.get("updateroomtype");
            final List delroomtype = (List) map.get("delroomtype");
            final List adderoomtypefacility = (List) map.get("adderoomtypefacility");
            final List adderoomtypeinfo = (List) map.get("adderoomtypeinfo");
            final List addtroomtypeinfo = (List) map.get("addtroomtypeinfo");
            final List updatehotel = (List) map.get("updatehotel");
            final List updateehotel = (List) map.get("updateehotel");            
            
            // 1. 更新房间sql
            int usize = updaterooms.size();
            if (usize > 0) {
                String updateroomSql = "update t_room set floor=?,memo=?,pms=?,name=?,roomtypeid=?,property=?,lockid=? where id=?";
                logger.info("开始批量更新房间...\n{}", updateroomSql);
                Object uparas[][] = new Object[usize][];
                for (int i = 0; i < usize; i++) {
                    List l = (List) updaterooms.get(i);
                    Object p[] = l.toArray();
                    uparas[i] = p;
                    logger.info("update sql params: {}", JsonKit.toJson(p));
                }
                // 批量更新操作
                Db.batch(updateroomSql, uparas, 1000);
                logger.info("批量更新房间结束, 共更新{}条.", usize);
            }
    
            // 2. 删除房间
            int dsize = delrooms.size();
            if (dsize > 0) {
                String delroomsSql = "delete from t_room where id=?";
                logger.info("开始批量删除房间...\n{}", delroomsSql);
                Object dparas[][] = new Object[dsize][];
                for (int i = 0; i < dsize; i++) {
                    List l = (List) delrooms.get(i);
                    Object p[] = l.toArray();
                    dparas[i] = p;
                    logger.info("delete sql params: {}", JsonKit.toJson(p));
                }
                // 批量删除操作
                Db.batch(delroomsSql, dparas, 1000);
                logger.info("批量删除房间结束, 共删除{}条.", dsize);
            }
                
            // 3. 添加房间
            int isize = addrooms.size();
            if (isize > 0) {
                String addroomSql = "insert into t_room "
                        + " (floor,memo,pms,name,roomtypeid,score,islock,tel,property,lockid) "
                        + "   values "
                        + " (?    ,?   ,?  ,?   ,?         ,?    ,?     ,?  ,?       ,?)";
                logger.info("开始批量添加房间...\n{}", addroomSql);
                Object iparas[][] = new Object[isize][];
                for (int i = 0; i < isize; i++) {
                    List l = (List) addrooms.get(i);
                    Object p[] = l.toArray();
                    iparas[i] = p;
                    logger.info("insert sql params: {}", JsonKit.toJson(p));
                }
                // 批量添加操作
                Db.batch(addroomSql, iparas, 1000);
                logger.info("批量添加房间结束, 共添加{}条.", isize);
            }
            
            // 4. 更新房型
            int uroomtypeSize = updateroomtype.size();
            if (uroomtypeSize > 0) {
                String updateroomtypeSql = "update t_roomtype set roomnum=?,cost=?,name=? where id=?";
                logger.info("开始批量更新房型...\n{}", updateroomtypeSql);
                Object paras[][] = new Object[uroomtypeSize][];
                for (int i = 0; i < uroomtypeSize; i++) {
                    List l = (List) updateroomtype.get(i);
                    Object p[] = l.toArray();
                    paras[i] = p;
                    logger.info("update sql params: {}", JsonKit.toJson(p));
                }
                // 批量更新房型操作
                Db.batch(updateroomtypeSql, paras, 1000);
                logger.info("批量更新房型结束, 共更新{}条.", uroomtypeSize);
            }
            
            // 删除房型关联表
            /*
             * 暂时先不删除附加信息，
            // 删除t 表房型设施
            logger.info("删除t 表房型设施", new Date().getTime());
            String deltroomtypefacility = "delete from t_roomtype_facility where roomtypeid=?";
            // 删除e 表房型设施
            logger.info("删除e 表房型设施", new Date().getTime());
            String deleroomtypefacility = "delete from e_roomtype_facility where roomtypeid=?";
            // 删除e 表房型床长关联表
            logger.info("删除e 表房型床长关联表", new Date().getTime());
            String deleroomtypebedlength = "delete from e_roomtyle_length where roomtypebedid "
                    + " in (select id from e_roomtype_bed where roomtypeid=?)";
            // 删除e 表房型床
            logger.info("删除e 表房型床", new Date().getTime());
            String deleroomtypebed = "delete from e_roomtype_bed where roomtypeid=?";
            // 删除t 表房型床长关联表
            logger.info("删除t 表房型床长关联表", new Date().getTime());
            String deltroomtypebedlength = "delete from t_roomtyle_length where roomtypebedid "
                    + " in (select id from t_roomtype_bed where roomtypeid=?)";
            // 删除t 表房型床
            logger.info("删除t 表房型床", new Date().getTime());
            String deltroomtypebed = "delete from t_roomtype_bed where roomtypeid=?";
            // 删除e 表房型附加信息
            logger.info("删除e 表房型附加信息", new Date().getTime());
            String deleroomtypeinfo = "delete from e_roomtype_info where roomtypeid=?";
            // 删除t 表房型附加信息
            logger.info("删除t 表房型附加信息", new Date().getTime());
            String deltroomtypeinfo = "delete from t_roomtype_info where roomtypeid=?";
            // 删除e 表基本房价信息
            logger.info("删除e 表基本房价信息", new Date().getTime());
            String delebaseprice = "delete from e_baseprice where roomtypeid=?";
            // 删除e 表房价信息
            logger.info("删除e 表房价信息", new Date().getTime());
            String deleprice = "delete from e_price where roomtypeid=?";
            // 删除t 表基本房价信息
            logger.info("删除t 表基本房价信息", new Date().getTime());
            String deltbaseprice = "delete from t_baseprice where roomtypeid=?";
            // 删除t 表房价信息
            logger.info("删除t 表房价信息", new Date().getTime());
            String deltprice = "delete from t_price where roomtypeid=?";
            */
            // 删除房型
            logger.info("删除房型", new Date().getTime());
            String delroomtypeSql = "delete from t_roomtype where id=?";
            
            // 5. 多个批量操作
            int multDelSize = delroomtype.size();
            if (multDelSize > 0) {
                logger.info("多个批量删除操作: 开始...");
                
                //修改数组初始化长度 uroomtypeSize --> multDelSize
                Object multParas[][] = new Object[multDelSize][];
                for (int i = 0; i < delroomtype.size(); i++) {
                    List l = (List) delroomtype.get(i);
                    Object p[] = l.toArray();
                    multParas[i] = p;
                    logger.info(" *********** sql params: {}", JsonKit.toJson(p));
                }
                /*
                 * 暂时不删除附加信息，冗余数据。
                // 多次批量操作
                logger.info("deltroomtypefacility: 开始...\n{}", deltroomtypefacility);
                Db.batch(deltroomtypefacility, multParas, 1000);
                logger.info("deltroomtypefacility: 删除t表房型设施数据成功.");
                
                logger.info("deleroomtypefacility: 开始...\n{}", deleroomtypefacility);
                Db.batch(deleroomtypefacility, multParas, 1000);
                logger.info("deleroomtypefacility: 删除e表房型设施数据成功.");
                
                logger.info("deleroomtypebedlength: 开始...\n{}", deleroomtypebedlength);
                Db.batch(deleroomtypebedlength, multParas, 1000);
                logger.info("deleroomtypebedlength: 删除e表房型尺寸数据成功.");
                
                logger.info("deleroomtypebed: 开始...\n{}", deleroomtypebed);
                Db.batch(deleroomtypebed, multParas, 1000);
                logger.info("deleroomtypebed: 删除e表床型数据成功.");
                
                logger.info("deltroomtypebedlength: 开始...\n{}", deltroomtypebedlength);
                Db.batch(deltroomtypebedlength, multParas, 1000);
                logger.info("deltroomtypebedlength: 删除t表床型尺寸数据成功.");
                
                logger.info("deltroomtypebed: 开始...\n{}", deltroomtypebed);
                Db.batch(deltroomtypebed, multParas, 1000);
                logger.info("deltroomtypebed: 删除t表床型数据成功.");
                
                logger.info("deleroomtypeinfo: 开始...\n{}", deleroomtypeinfo);
                Db.batch(deleroomtypeinfo, multParas, 1000);
                logger.info("deleroomtypeinfo: 删除e表房型信息数据成功.");
                
                logger.info("deltroomtypeinfo: 开始...\n{}", deltroomtypeinfo);
                Db.batch(deltroomtypeinfo, multParas, 1000);
                logger.info("deltroomtypeinfo: 删除t表房型信息数据成功.");
                
                logger.info("delebaseprice: 开始...\n{}", delebaseprice);
                Db.batch(delebaseprice, multParas, 1000);
                logger.info("delebaseprice: 删除e表基本价格数据成功.");
                
                logger.info("deleprice: 开始...\n{}", deleprice);
                Db.batch(deleprice, multParas, 1000);
                logger.info("deleprice: 删除e表策略价格数据成功.");
                
                logger.info("deltbaseprice: 开始...\n{}", deltbaseprice);
                Db.batch(deltbaseprice, multParas, 1000);
                logger.info("deltbaseprice: 删除t表基本价格数据成功.");
                
                logger.info("deltprice: 开始...\n{}", deltprice);
                Db.batch(deltprice, multParas, 1000);
                logger.info("deltprice: 删除t表策略价格数据成功.");
                */
                logger.info("delroomtypeSql: 开始...\n{}", delroomtypeSql);
                Db.batch(delroomtypeSql, multParas, 1000);
                logger.info("delroomtypeSql: 删除房型数据成功.");
                
                logger.info("多个批量删除操作: 结束.");
            }
    
            // 6. 添加房型设施e_roomtype_facility
            if (adderoomtypefacility.size() > 0) {
                String addERoomtypeFacilitySql = "insert into e_roomtype_facility (roomtypeid,facid) values (?,?)";
                logger.info("添加e表房型设施e_roomtype_facility开始...\n", addERoomtypeFacilitySql);
                Object paras[][] = new Object[adderoomtypefacility.size()][];
                for (int i = 0; i < adderoomtypefacility.size(); i++) {
                    List l = (List) adderoomtypefacility.get(i);
                    Object p[] = l.toArray();
                    paras[i] = p;
                    logger.info("insert sql params: {}", JsonKit.toJson(p));
                }
                // 批量添加操作
                Db.batch(addERoomtypeFacilitySql, paras, 1000);
                logger.info("添加e表房型设施e_roomtype_facility成功, 共添加{}条.", adderoomtypefacility.size());
            }
    
            // 7. 添加房型附加信息e_roomtype_info
            if (adderoomtypeinfo.size() > 0) {
                String adderoomtypeinfoSql = "insert into e_roomtype_info(roomtypeid) values(?)";
                logger.info("添加e表房型附加信息e_roomtype_info开始...\n", adderoomtypeinfoSql);
                Object paras[][] = new Object[adderoomtypeinfo.size()][];
                for (int i = 0; i < adderoomtypeinfo.size(); i++) {
                    List l = (List) adderoomtypeinfo.get(i);
                    Object p[] = l.toArray();
                    paras[i] = p;
                    logger.info("insert sql params: {}", JsonKit.toJson(p));
                }
                //批量操作
                Db.batch(adderoomtypeinfoSql, paras, 1000);
                logger.info("添加e表房型附加信息e_roomtype_info成功, 共添加{}条.", adderoomtypeinfo.size());
            }
            
            // 8. 添加房型附加信息t_roomtype_info
            if (addtroomtypeinfo.size() > 0) {
                String addtroomtypeinfoSql = "insert into t_roomtype_info(roomtypeid,bedtype,bedsize) values(?,0,0)";
                logger.info("添加t表房型附加信息t_roomtype_info开始...\n", addtroomtypeinfoSql);
                Object paras[][] = new Object[addtroomtypeinfo.size()][];
                for (int i = 0; i < addtroomtypeinfo.size(); i++) {
                    List l = (List) addtroomtypeinfo.get(i);
                    Object[] p = l.toArray();
                    paras[i] = p;
                    logger.info("insert sql params: {}", JsonKit.toJson(p));
                }
                // 批量操作
                Db.batch(addtroomtypeinfoSql, paras, 1000);
                logger.info("添加t表房型附加信息t_roomtype_info成功, 共添加{}条.", addtroomtypeinfo.size());
            }
            
            // 9. 更新updatehotel
            if (updateehotel.size() > 0) {
                String updateEhotelSql = "update e_hotel set roomnum=?, isnewpms='F' ,pmsstatus=? where id=?";
                logger.info("更新updatehotel开始...\n{}", updateEhotelSql);
                Object paras[][] = new Object[updateehotel.size()][];
                for (int i = 0; i < updateehotel.size(); i++) {
                    List l = (List) updateehotel.get(i);
                    Object[] p = l.toArray();
                    paras[i] = p;
                    logger.info("update sql params: {}", JsonKit.toJson(p));
                }
                // 批量操作
                Db.batch(updateEhotelSql, paras, 1000);
                logger.info("更新updatehotel成功, 共更新{}条.", updateehotel.size());
            }
            
            // 10. 更新t 表 hotel
            if (updatehotel.size() > 0) {
                String updateThotelSql = "update t_hotel set roomnum=?,isnewpms='F' where id=?";
                logger.info("更新t 表 hotel 开始...\n", updateThotelSql);
                Object paras[][] = new Object[updatehotel.size()][];
                for (int i = 0; i < updatehotel.size(); i++) {
                    List l = (List) updatehotel.get(i);
                    Object[] p = l.toArray();
                    paras[i] = p;
                    logger.info("update sql params: {}", JsonKit.toJson(p));
                }
                // 批量操作
                Db.batch(updateThotelSql, paras, 1000);
                logger.info("更新t 表 hotel 成功,  共更新{}条.", updatehotel.size());
            }
            
            logger.info("同步pms酒店房间成功.");
           
        } catch(Exception e) {
            succeed = false;
            logger.error("同步pms酒店房间出错: {}", e.getMessage());
            throw e;
        }
        return succeed;
    }

    public void logHotelTrack (String hotelId, String hotelName, String roomNum, List delRooms, List addRooms , List delRoomTypes) {
    	if(delRooms.size()==0 && addRooms.size() ==0 && delRoomTypes.size()==0){
    		return;
    	}
    	//记录删除房间到酒店 信息轨迹日志
        StringBuffer delRoom = new StringBuffer();
        int delSize = delRooms.size();
        if(delSize > 0){
        	delRoom.append("del:");
        	for(int i=0; i<delSize; i++) {
        		if(delRooms.get(i) != null){
        			delRoom.append(delRooms.get(i).toString()).append(",");
        		}
        	}
        }
        String delRoomStr ="";
        if(delRoom.length() >0 ){
        	delRoomStr = delRoom.toString().substring(0, delRoom.length()-1);
        }
         
        StringBuffer addRoom = new StringBuffer();//添加房间日志内容
        int addSize = addRooms.size();
        if(addSize > 0){
        	addRoom.append("add:");
        	for(int i=0; i < addSize; i++) {
        		if(addRooms.get(i) != null){
        			addRoom.append(addRooms.get(i).toString()).append(",");
        		}
        	}
        }
        String addRoomStr = "";
        if(addRoom.length() >0 ){
        	addRoomStr = addRoom.toString().substring(0, addRoom.length()-1);
        }
        
        StringBuffer delRoomType = new StringBuffer();//添加房间日志内容
        int delRoomTypeSize = delRoomTypes.size();
        if( delRoomTypeSize >0 ){
        	delRoomType.append("dRT:");
        	for(int i=0; i<delRoomTypeSize; i++){
        		if(delRoomTypes.get(i)!=null){
        			delRoomType.append(delRoomTypes.get(i).toString()).append(",");
        		}
        	}
        }
        String delRoomTypeStr = "";
        if(delRoomType.length() >0 ){
        	delRoomTypeStr = delRoomType.toString().substring(0, delRoomType.length()-1);
        }
        
        String content = "" ;
        if(delRoomStr.trim().length()>0){
        	content=delRoomStr;
        }
        
        if(addRoomStr.length()>0 ){
        	if(content.trim().length()>0){	        		
        		content = content + ";" + addRoomStr;
        	}else{
        		content =  addRoomStr;
        	}
        }
        if(delRoomType.length() > 0){
        	if(content.trim().length()>0){	
        		content = content +";" + delRoomTypeStr; 
        	}else{
        		content = delRoomTypeStr; 
        	}
        }
        String sql="insert into t_hotel_base_track ( hotelid, hotelname, roomcnt, content, createtime ) values (?, ?, ?, ?, now())";
        logger.info("记录到酒店信息轨迹日志中：hotelId:{}, hotelName:{}, roomNum:{},content:{}",hotelId, hotelName, roomNum, content);
        Db.update(sql, hotelId, hotelName, roomNum, content);
    }

    public PmsRoomOrder findPmsRoomOrder(String pmsRoomOrderNo, Long hotelId) {
        return PmsRoomOrder.dao.findFirst("select * from b_pmsroomorder where hotelId = ? and pmsRoomOrderNo = ?", hotelId, pmsRoomOrderNo);
    }


    public Bean findRoomStatus(String hotelid, String roomtypeid, String roomid, String date) {
        String sql = "select * from b_roomtemp_310000 where hotelid=? and roomtypeid=? and roomid=? and time=?";
        return Db.findFirst(sql, hotelid, roomtypeid, roomid, date);

    }
}
