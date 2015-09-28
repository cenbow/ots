package com.mk.ots.message.dao;

import java.util.List;

import com.mk.framework.datasource.dao.BaseDao;
import com.mk.ots.message.model.MessageProvider;

public interface IMessageProviderDao extends BaseDao<MessageProvider, Long> {
public List<MessageProvider> queryAllProviders(String ExceptProvider);
}
