package com.mk.ots.card.service.impl;

import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.card.dao.IBCardDAO;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.service.IBCardService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BCardService implements IBCardService {
    @Autowired
    private IBCardDAO iBCardDAO;

    @Override
    public BCard findActivatedByPwd(String pwd) {
        if (StringUtils.isEmpty(pwd)) {
            throw MyErrorEnum.customError.getMyException("密码不能为空.");
        }
        return this.iBCardDAO.findActivatedByPwd(pwd);
    }
}
