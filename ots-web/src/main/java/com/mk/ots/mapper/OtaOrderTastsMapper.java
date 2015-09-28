package com.mk.ots.mapper;

import java.util.List;

import com.mk.ots.order.model.OtaOrderTasts;

public interface OtaOrderTastsMapper {
	int deleteByPrimaryKey(Long id);

	int insert(OtaOrderTasts record);

	int insertSelective(OtaOrderTasts record);

	OtaOrderTasts selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(OtaOrderTasts record);

	int updateByPrimaryKeyWithBLOBs(OtaOrderTasts record);

    int updateByPrimaryKey(OtaOrderTasts record);
    
    OtaOrderTasts selectByOtaorderid(Long id);
    
    List<OtaOrderTasts> selectall();
    
    int updateCountById(OtaOrderTasts record);

	/**
	 * 查询推送信息 ，根据(tasktype，status); count 小于3次 ，执行时间小于现在的
	 * 
	 * @param otaOrderTasts
	 * @return
	 */
	List<OtaOrderTasts> findPushMessage(OtaOrderTasts otaOrderTasts);

	/**
	 * 根据订单ID,状态，类型查找
	 * 
	 * @param record
	 * @return
	 */
	List<OtaOrderTasts> findByOrderId_status_tasktype(OtaOrderTasts record);

	/**
	 * 查询出要取消的消息，根据订单ID,类型,状态，错误次数<3的
	 * 
	 * @param record
	 * @return
	 */
	List<OtaOrderTasts> findCancelMsg(OtaOrderTasts record);

	List<OtaOrderTasts> selectOrderTasksList(Long otaOrderId);
}
