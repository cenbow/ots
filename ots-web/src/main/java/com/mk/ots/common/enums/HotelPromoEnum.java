package com.mk.ots.common.enums;

/**
 * all promotion types used for queries. 
 * <p>
 * has equivalent value with t_room_sale_type.id
 * 
 * @author AaronG
 *
 */
public enum HotelPromoEnum {
    SAVING(0, "一省到底"), Night(1,"今夜特价"),OneDollar(6,"一元特价"), Day(2, "今日特价"), Theme(3, "主题酒店");
    private Integer code;
    private String text;

    HotelPromoEnum(Integer code, String text) {
        this.code = code;
        this.text = text;
    }

    public Integer getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
