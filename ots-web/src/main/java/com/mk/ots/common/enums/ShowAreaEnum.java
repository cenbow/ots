package com.mk.ots.common.enums;

/**
 * all promotion types used for queries. 
 * <p>
 * has equivalent value with t_room_sale_type.id
 * 
 * @author AaronG
 *
 */
public enum ShowAreaEnum {
    FrontPageHead("front_page_head","首页顶部"),
    FrontPageCentre("front_page_centre","首页中央"),
    FrontPageBottom("front_page_bottom", "首页底部"),
    HomePagePromoRecommend("home_page_promo_recommend", "首页特价推荐");
    private String code;
    private String text;

    ShowAreaEnum(String code, String text) {
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
