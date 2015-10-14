package com.mk.ots.card.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.card.dao.IBCardDAO;
import com.mk.ots.card.model.BCard;
import com.mk.ots.mapper.CardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BCardDAO extends MyBatisDaoImpl<BCard, Long> implements IBCardDAO {

    @Autowired
    private CardMapper cardMapper;

    @Override
    public BCard findActivatedByPwd(String pwd) {
        BCard card = this.cardMapper.findActivatedByPwd(pwd);
        return card;
    }
    public void updateStatusById(Map<String,Object> param) {
        this.cardMapper.updateStatusById(param);
    }
}
