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
    Night("1","今夜特价"),OneDollar("6","一元特价"), Day("11", "今日特价"), Theme("16", "主题酒店");
    private String code;
    private String text;

    HotelPromoEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }
}
