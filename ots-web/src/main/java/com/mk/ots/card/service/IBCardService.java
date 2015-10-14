package com.mk.ots.card.service;

import com.mk.ots.card.model.BCard;

public interface IBCardService {
    public BCard findActivatedByPwd(String pwd);

    public BCard recharge (Long mid,String pwd);
}
