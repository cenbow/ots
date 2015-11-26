/**
 * 
 */
package com.mk.ots.mapper;

import com.mk.ots.member.model.UUnionidLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnionidLogMapper {

	public List<UUnionidLog> queryByUnionid(String unionid);

	public int saveLog(UUnionidLog log);
}
