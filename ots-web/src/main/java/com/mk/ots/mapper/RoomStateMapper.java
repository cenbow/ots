package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.mk.ots.order.model.RoomStateModel;


/**
 * RoomState Mapper
 * @author chuaiqing.
 *
 */
public interface RoomStateMapper {
    
    /**
     * 获得指定酒店所有房间的房态
     * 从 b_pmsroomorder 表中得到指定酒店(hotelid)，指定房型(roomtypeid)，指定房间(roomid)， 指定日期(otsbegindate,otsenddate)的预定数据
     * 将结果数据作为锁房数据放到redis缓存中，在预定业务中，redis房态中的信息不能进行预定。
     * @param hotelid
     * 参数：酒店id
     * @param begindate
     * 参数：开始日期
     * @param enddate
     * 参数：结束日期
     * @return
     */
    @Select("select tp.thotelid, "
           + "    r.roomtypeid,r.id,r.name,r.pms, "
           + "    c.id as pmsroomorderid,c.status,c.roomid,c.begintime,c.endtime,c.otsbegindate,c.otsenddate,c.otsdays, "
           + "    case when c.roomid is null then 'vc' else 'nvc' end as roomstatus "
           + "  from t_roomtype tp "
           + "    left outer join t_room r on tp.id=r.roomtypeid "
           + "      left outer join ( "
           + "              select t.*,datediff(otsenddate, otsbegindate) as otsdays "
           + "                from (select a.id,a.status,a.roomid,a.begintime,a.endtime, "
           + "                          case when hour(begintime)=6 and minute(begintime)=0 and second(begintime)=0 then date(timestampadd(hour, -7, begintime)) else date(timestampadd(hour, -6, begintime)) end as otsbegindate, "
           + "                          case when hour(endtime)=12 and minute(endtime)=0 and second(endtime)=0 and endtime<now() then date(timestampadd(hour, 24, endtime)) else date(endtime) end otsenddate "
           + "                        from b_pmsroomorder a where 1=1 and ordertype = 2 and visible = 'T' and status IN ('IN' , 'RE') and hotelid=#{hotelid}"
//           + "                      union "
//           + "                        select rp.id,'WX' as status,rp.roomid,rp.begintime,rp.endtime, "
//           + "                            case when hour(begintime)=6 and minute(begintime)=0 and second(begintime)=0 then date(timestampadd(hour, -7, begintime)) else date(timestampadd(hour, -6, begintime)) end as otsbegindate,"
//           + "                            case when hour(endtime)=12 and minute(endtime)=0 and second(endtime)=0 and endtime<now() then date(timestampadd(hour, 24, endtime)) else date(endtime) end otsenddate"
//           + "                          from t_room_repair rp where 1=1 and rp.hotelid=#{hotelid} "
           + "                     ) t "
           + "                  where 1=1 "
           + "                    and (('#{begindate}' >= otsbegindate and '#{begindate}' <= otsenddate) or ('#{enddate}' >= otsbegindate and '#{enddate}' <= otsenddate)) "
           + "      ) c on r.id=c.roomid "
           + "    where 1=1 "
           + "      and tp.thotelid=#{hotelid}")
    public List<RoomStateModel> findRoomstateByHotel(@Param("hotelid") Long hotelid, 
            @Param("begindate") String begindate, @Param("enddate") String enddate);
    
    /**
     * 获得指定房型的房态
     * @param roomtypeid
     * 参数：房型id
     * @param begindate
     * 参数：开始日期
     * @param enddate
     * 参数：结束日期
     * @return
     */
    @Select("select tp.thotelid, "
            + "    r.roomtypeid,r.id,r.name,r.pms, "
            + "    c.id as pmsroomorderid,c.status,c.roomid,c.begintime,c.endtime,c.otsbegindate,c.otsenddate,c.otsdays, "
            + "    case when c.roomid is null then 'vc' else 'nvc' end as roomstatus "
            + "  from t_roomtype tp "
            + "    left outer join t_room r on tp.id=r.roomtypeid "
            + "      left outer join ( "
            + "              select t.*,datediff(otsenddate, otsbegindate) as otsdays "
            + "                from (select a.id,a.status,a.roomid,a.begintime,a.endtime, "
            + "                          case when hour(begintime)=6 and minute(begintime)=0 and second(begintime)=0 then date(timestampadd(hour, -7, begintime)) else date(timestampadd(hour, -6, begintime)) end as otsbegindate, "
            + "                          case when hour(endtime)=12 and minute(endtime)=0 and second(endtime)=0 and endtime<now() then date(timestampadd(hour, 24, endtime)) else date(endtime) end otsenddate "
            + "                        from b_pmsroomorder a where 1=1 and ordertype = 2 and visible = 'T' and status IN ('IN' , 'RE') and roomtypeid=#{roomtypeid}) t "
            + "                  where 1=1 "
            + "                    and (('#{begindate}' >= otsbegindate and '#{begindate}' <= otsenddate) or ('#{enddate}' >= otsbegindate and '#{enddate}' <= otsenddate)) "
            + "      ) c on r.id=c.roomid "
            + "    where 1=1 "
            + "      and tp.id=#{roomtypeid}")
    public List<RoomStateModel> findRoomstateByRoomtype(@Param("roomtypeid") Long roomtypeid,
            @Param("begindate") String begindate, @Param("enddate") String enddate);
    
    
    /**
     * 获得指定房间的房态
     * @param roomid
     * 参数：酒店房间id
     * @param begindate
     * 参数：开始日期
     * @param enddate
     * 参数：结束日期
     * @return
     */
    @Select("select tp.thotelid, "
            + "    r.roomtypeid,r.id,r.name,r.pms, "
            + "    c.id as pmsroomorderid,c.status,c.roomid,c.begintime,c.endtime,c.otsbegindate,c.otsenddate,c.otsdays, "
            + "    case when c.roomid is null then 'vc' else 'nvc' end as roomstatus "
            + "  from t_roomtype tp "
            + "    left outer join t_room r on tp.id=r.roomtypeid "
            + "      left outer join ( "
            + "              select t.*,datediff(otsenddate, otsbegindate) as otsdays "
            + "                from (select a.id,a.status,a.roomid,a.begintime,a.endtime, "
            + "                          case when hour(begintime)=6 and minute(begintime)=0 and second(begintime)=0 then date(timestampadd(hour, -7, begintime)) else date(timestampadd(hour, -6, begintime)) end as otsbegindate, "
            + "                          case when hour(endtime)=12 and minute(endtime)=0 and second(endtime)=0 and endtime<now() then date(timestampadd(hour, 24, endtime)) else date(endtime) end otsenddate "
            + "                        from b_pmsroomorder a where 1=1 and ordertype = 2 and visible = 'T' and status IN ('IN' , 'RE') and roomid=#{roomid}) t "
            + "                  where 1=1 "
            + "                    and (('#{begindate}' >= otsbegindate and '#{begindate}' <= otsenddate) or ('#{enddate}' >= otsbegindate and '#{enddate}' <= otsenddate)) "
            + "      ) c on r.id=c.roomid "
            + "    where 1=1 "
            + "      and r.id=#{roomid}")
    public List<RoomStateModel> findRoomstateByRoomid(@Param("roomid") Long roomid,
            @Param("begindate") String begindate, @Param("enddate") String enddate);
}
