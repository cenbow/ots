package com.mk.ots.activity.dao.impl;

import org.springframework.stereotype.Component;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.activity.dao.IBActivityDao;
import com.mk.ots.activity.model.BActivity;

@Component
public class BActivityDao extends MyBatisDaoImpl<BActivity, Long> implements IBActivityDao {

}
