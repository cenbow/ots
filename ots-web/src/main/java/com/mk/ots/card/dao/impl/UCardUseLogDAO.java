package com.mk.ots.card.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.card.dao.IBCardDAO;
import com.mk.ots.card.dao.IUCardUseLogDAO;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.model.UCardUseLog;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UCardUseLogDAO extends MyBatisDaoImpl<UCardUseLog, Long> implements IUCardUseLogDAO {

    public UCardUseLog findByCardId (Long cardId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("cardId", cardId);
        return super.findOne("findByCardId",param);
    }
}
