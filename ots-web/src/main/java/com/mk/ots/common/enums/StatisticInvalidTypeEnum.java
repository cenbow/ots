package com.mk.ots.common.enums;

/**
 * Created by jjh on 15/7/30.
 */
public enum StatisticInvalidTypeEnum {

    statisticValid(1, "有效的统计基数"),
    statisticInvalid(2, "无效的统计基数"),
    ;

    private final Integer type;
    private final String desc;


    private StatisticInvalidTypeEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Integer getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
