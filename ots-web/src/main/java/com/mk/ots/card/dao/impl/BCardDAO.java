package com.mk.ots.card.dao.impl;

import com.mk.framework.datasource.dao.mybatis.MyBatisDaoImpl;
import com.mk.ots.card.dao.IBCardDAO;
import com.mk.ots.card.model.BCard;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BCardDAO extends MyBatisDaoImpl<BCard, Long> implements IBCardDAO {

    @Override
    public BCard findActivatedByPwd(String pwd) {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("password", pwd);
        BCard card = super.findOne("findActivatedByPwd", param);
        return card;
    }
    public void updateStatusById(Map<String,Object> param) {
        super.update("updateStatusById",param);
    }
}
