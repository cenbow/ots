package com.mk.ots.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.mk.ots.order.model.PmsRoomOrderModel;


/**
 * PmsRoomOrder Mapper: b_pmsroomorder表映射类，pms客单表.
 * @author chuaiqing.
 *
 */
public interface PmsRoomOrderMapper {
    
    /**
     * 
     * @param id
     * @return
     */
    @Select("select id,pmsroomorderno,hotelid,hotelpms,pmsorderid,status,roomtypepms,pmsorderno,"
            + "roomtypeid,roomid,roomno,roomtypename,roompms,begintime,endtime,checkintime,checkouttime,"
            + "ordertype,roomcost,othercost,mikepay,otherpay,opuser,visible from b_pmsroomorder where id = #{id}")
    public PmsRoomOrderModel findById(@Param("id") Long id);
    
    /**
     * 
     * @param hotelid
     * @return
     */
    @Select("select a.id,a.pmsroomorderno,a.hotelid,a.hotelpms,a.pmsorderid,a.status,a.roomtypepms,a.pmsorderno,"
            + "a.roomtypeid,a.roomid,a.roomno,a.roomtypename,a.roompms,a.begintime,a.endtime,a.checkintime,a.checkouttime,"
            + "a.ordertype,a.roomcost,a.othercost,a.mikepay,a.otherpay,a.opuser,a.visible "
            + "  from b_pmsroomorder a where 1=1 and ordertype = 2 and (status='IN' or status='RE') and hotelid=#{hotelid}"
            + "    order by hotelid,roomtypeid,roomid,begintime")
    public List<PmsRoomOrderModel> findByHotelId(@Param("hotelid") Long hotelid);
    
    /**
     * 
     * @param roomtypeid
     * @return
     */
    @Select("select a.id,a.pmsroomorderno,a.hotelid,a.hotelpms,a.pmsorderid,a.status,a.roomtypepms,a.pmsorderno,"
            + "a.roomtypeid,a.roomid,a.roomno,a.roomtypename,a.roompms,a.begintime,a.endtime,a.checkintime,a.checkouttime,"
            + "a.ordertype,a.roomcost,a.othercost,a.mikepay,a.otherpay,a.opuser,a.visible "
            + "  from b_pmsroomorder a where 1=1 and ordertype = 2 and visible = 'T' and (status='IN' or status='RE') and roomtypeid=#{roomtypeid}"
            + "    order by hotelid,roomtypeid,roomid,begintime")
    public List<PmsRoomOrderModel> findByRoomtypeId(@Param("roomtypeid") Long roomtypeid);
    
    /**
     * 
     * @param roomid
     * @return
     */
    @Select("select a.id,a.pmsroomorderno,a.hotelid,a.hotelpms,a.pmsorderid,a.status,a.roomtypepms,a.pmsorderno,"
            + "a.roomtypeid,a.roomid,a.roomno,a.roomtypename,a.roompms,a.begintime,a.endtime,a.checkintime,a.checkouttime,"
            + "a.ordertype,a.roomcost,a.othercost,a.mikepay,a.otherpay,a.opuser,a.visible "
            + "  from b_pmsroomorder a where 1=1 and ordertype = 2 and visible = 'T' and (status='IN' or status='RE') and roomid=#{roomid}"
            + "    order by hotelid,roomtypeid,roomid,begintime")
    public List<PmsRoomOrderModel> findByRoomId(@Param("roomid") Long roomid);
    
}
