package com.mk.ots.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.CacheNamespace;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.core.annotation.Order;

public interface PmsOrderMapper {

	@Select("select * from b_pmsorder where id = #{id}")
	Order getOrder(@Param("id") long id);

	@Insert("insert into b_pmsorder values (default, #{typeName}, "
			+ "#{title}, #{titlePic}, #{content}, #{source}, #{author}, "
			+ "default, default, default, #{state})")
	@Options(flushCache = true)
	public void saveOrder(Order order);

	@Select("select * from b_pmsorder where id = #{id}")
	@Options(flushCache = true)
	Map getPmsRoomOrder(@Param("id") long id);

}
