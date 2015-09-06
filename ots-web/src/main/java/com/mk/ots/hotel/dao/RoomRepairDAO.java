package com.mk.ots.hotel.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.com.winhoo.mikeweb.util.DateTools;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.hotel.bean.RoomTemp;

@Repository
public class RoomRepairDAO   {

    
    public Map<String, Integer> getRepairNumByDay(Long roomTypeId,Long curDay,Long endDay) {
        
         SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        Map<String, Integer> map = new HashMap<>();
            StringBuilder sb = new StringBuilder();
            while(curDay<endDay){
                String curStr = sdf.format(new Date(curDay));
                sb.append("select "+curDay+" as 'date',COUNT(*) as 'num' from t_room_repair  where RoomTypeId='"+roomTypeId+"'");
                sb.append(" and Endtime>='"+curStr+"'");
                curDay = DateTools.getMilliseconds(curDay,1);
                curStr = sdf.format(new Date(curDay));
                sb.append(" and Begintime<='"+curStr+"' ");
                if(curDay<endDay){
                    sb.append("union all ");
                }
            }
            String sql =sb.toString();
            List<Bean> ll = Db.find(sql);
            Integer num = null;
            for(Bean b: ll){
                String numStr = b.get("num")==null?"0":String.valueOf(b.get("num"));
                num = Integer.parseInt(numStr);
                //key:"yyyyMMdd" value:Integer
                Long millis = b.getLong("date");
                Date date = new Date(millis);
                SimpleDateFormat sdfd=new SimpleDateFormat("yyyyMMdd");
                map.put(sdfd.format(date),num);
            }
        return map;
    }

    /**
     * 
     * @param hotelId
     * @param roomTypeId
     * @param curDay
     * @param endDay
     * @return
     */
    public List<RoomTemp> findRoomRepairNONUnion(Long hotelId,Long roomTypeId,Long curDay,Long endDay){
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMdd");
        String curStr = dateformat.format(new Date(curDay));
        String endStr= dateformat.format(new Date(endDay));
        StringBuffer sb = new StringBuffer();
        sb.append("select * from t_room_repair where hotelid =? and roomtypeid=? ");
        sb.append(" and endtime>=?");
        List<Bean> re= Db.find(sb.toString(),hotelId,roomTypeId,curStr);
        List<RoomTemp> result = new ArrayList();
        for(Bean b: re){
            Long repaireStart= b.getDate("begintime").getTime();
            if(curDay>repaireStart){
                repaireStart= curDay;
            }
            Long repaireEnd = b.getDate("endtime").getTime();
             while(repaireStart<=repaireEnd){
                 RoomTemp roomTemp  = new RoomTemp();
                 roomTemp.setHotelId(hotelId);
                 roomTemp.setRoomId(b.getLong("roomid"));
                 roomTemp.setRoomTypeId(roomTypeId);
                 roomTemp.setTime(dateformat.format(new Date(repaireStart)));
                 result.add(roomTemp);
                 repaireStart+= DateTools.DAY_MILLSECONDS;
             }
        }
        return result;
    }
    /**
     * 优化方法 到
     * @param hotelId
     * @param roomTypeId
     * @param curDay
     * @param endDay
     * @return
     */
    @Deprecated
    public List<RoomTemp> findRoomTemp(Long hotelId,Long roomTypeId, Long curDay, Long endDay) {
        List <RoomTemp> roomTempList = new ArrayList<RoomTemp>();
        SimpleDateFormat dateformat=new SimpleDateFormat("yyyyMMdd");
            StringBuilder sb = new StringBuilder();
            while(curDay<endDay){
                String curStr = dateformat.format(new Date(curDay));
                sb.append("select "+curDay+" as 'date',roomid from t_room_repair  where RoomTypeId='"+roomTypeId+"'");
                sb.append(" and Endtime>='"+curStr+"'");
                curDay += DateTools.DAY_MILLSECONDS;
                curStr = dateformat.format(new Date(curDay));
                sb.append(" and Begintime<="+curStr+" ");
                if(curDay<endDay){
                    sb.append("union all ");
                }
            }
            String sql =sb.toString();
            List<Bean> ll = Db.find(sql);
            RoomTemp roomTemp = null;
            for(Bean b: ll){
                roomTemp = new RoomTemp();
                roomTemp.setHotelId(hotelId);
                roomTemp.setRoomId(b.getLong("roomid"));
                roomTemp.setRoomTypeId(roomTypeId);
                roomTemp.setTime(dateformat.format(new Date(b.getLong("date"))));
                roomTempList.add(roomTemp);
            }
            
        return roomTempList;
    }
    public void deleteAllRoomRepairs(Long hotelid){
    	Db.update("delete from t_room_repair where hotelid=?", hotelid);
    }
}
