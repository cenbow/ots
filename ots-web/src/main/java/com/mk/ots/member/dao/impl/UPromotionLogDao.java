package com.mk.ots.member.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.member.dao.IUPromotionLogDao;
import com.mk.ots.member.model.UPromotionLog;

@Component
public class UPromotionLogDao extends MyBatisDaoImpl<UPromotionLog, Long> implements IUPromotionLogDao {
}
