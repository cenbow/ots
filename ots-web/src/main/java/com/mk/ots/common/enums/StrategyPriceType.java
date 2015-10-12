package com.mk.ots.common.enums;
/**
 * 
 * 价格策略表枚举类
 * @author zhangyajun
 *
 */
public enum StrategyPriceType {

	STANDARD(1),//标准价
    KNOCKOFF(2),//立减
    DISCOUNT(3);//折扣

    private int id;

    private StrategyPriceType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
