package com.mk.ots.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.Update;
import org.springframework.core.annotation.Order;

import com.mk.ots.common.enums.OtaOrderStatusEnum;
import com.mk.ots.order.bean.OtaOrder;

/**
 * OtaOrder 数据操作类
 * 
 * @author zzy
 *
 */
public interface OrderMapper {
	/**
	 * 插入
	 * 
	 * @param order
	 */
	@Insert("insert into b_otaorder values(#{id},#{PmsRoomOrderNo},#{Hotelid},#{HotelPms},"
			+ "#{PmsOrderId},#{Status},#{RoomTypePms},#{PmsOrderNo},#{RoomTypeId},#{RoomId},"
			+ "#{Roomno},#{RoomTypeName},#{RoomPms},#{Begintime},#{Endtime},#{Checkintime},"
			+ "#{Checkouttime},#{Ordertype},#{Roomcost},#{Othercost},#{Mikepay},#{Otherpay},#{Opuser},#{visible}")
	@Options(flushCache = true)
	public void saveOrder(Order order);

	/**
	 * update
	 * 
	 * @param order
	 */
	@Update("update b_otaorder set PmsRoomOrderNo=#{PmsRoomOrderNo}, Hotelid=#{Hotelid}, "
			+ "HotelPms=#{HotelPms}, PmsOrderId=#{PmsOrderId}, Status=#{Status}, RoomTypePms=#{RoomTypePms}, "
			+ "PmsOrderNo=#{PmsOrderNo}, RoomTypeId=#{RoomTypeId}, RoomId=#{RoomId}, Roomno=#{Roomno}, "
			+ "RoomTypeName=#{RoomTypeName}, RoomPms=#{RoomPms}, Begintime=#{Begintime}, "
			+ "Endtime=#{Endtime}, Checkintime=#{Checkintime}, Checkouttime=#{Checkouttime}, "
			+ "Ordertype=#{Ordertype}, Roomcost=#{Roomcost}, Othercost=#{Othercost}, Mikepay=#{Mikepay}, "
			+ "Otherpay=#{Otherpay}, Opuser=#{Opuser}, visible=#{visible} " + " where id=#{id}")
	@Options(flushCache = true)
	public void updateOrder(Order order);

	@Delete("delete from b_otaorder where id=#{id}")
	@Options(flushCache = true)
	public void deleteUser(String id);

	@Select("select * from b_otaorder where id = #{id}")
	@Options(useCache = true, flushCache = false, timeout = 10000)
	public OtaOrder findOtaOrderById(@Param("id") long id);

	@Select("select * from b_otaorder where otaOrderId = #{otaOrderId} and canshow = #{canshow}")
	@Options(useCache = true, flushCache = false, timeout = 10000)
	public OtaOrder findOtaOrderByIdCanShow(@Param("otaOrderId") long otaOrderId, @Param("canshow") boolean canshow);

	@SelectProvider(type = OrderSqlProvider.class, method = "findMyOtaOrderByMidSql")
	public List<OtaOrder> findMyOtaOrderByMid(@Param("otaOrderId") Long Mid, @Param("otaOrderId") Long hotelId,
			@Param("otaOrderId") List<OtaOrderStatusEnum> statusList, @Param("otaOrderId") Date begintime, @Param("otaOrderId") Date endtime,
			@Param("otaOrderId") Integer start, @Param("otaOrderId") Integer limit, @Param("otaOrderId") Boolean canshow);
}
