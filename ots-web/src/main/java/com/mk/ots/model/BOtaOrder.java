package com.mk.ots.model;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.BizModel;

/**
 * OtaOrder
 * @author chuaiqing.
 *
 */
@Component
@DbTable(name="b_otaorder", pkey="id")
public class BOtaOrder extends BizModel<BOtaOrder> {

    /**
     * 
     */
    private static final long serialVersionUID = 2380359762476968135L;
    public static final BOtaOrder dao = new BOtaOrder();

}
