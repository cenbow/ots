package com.mk.ots.log.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

public interface LogMapper {
	
	public void save(@Param("table") String table, @Param("mid") long mid, @Param("accesstoken") String accesstoken, @Param("type") int type, @Param("time") Date time, @Param("ostype") String ostype, @Param("ip") String ip, @Param("appversion") int appversion);

}