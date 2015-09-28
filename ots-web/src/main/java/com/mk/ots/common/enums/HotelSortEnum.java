package com.mk.ots.common.enums;

import cn.com.winhoo.mikeweb.exception.MyErrorEnum;

/**
 * 酒店搜索排序类型枚举类.
 * 
 * @author chuaiqing.
 *
 */
public enum HotelSortEnum {
    DISTANCE(1, "距离"),
    RECOMMEND(2, "推荐"),
    PRICE(3, "价格"),
    SCORE(4, "权重"),
    ORDERNUMS(5, "人气");
    
    private final Integer id;
    private final String name;
    
    /**
     * 
     * @param id
     * @param name
     * @return
     */
    private HotelSortEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    public static HotelSortEnum getByName(String name){
        for (HotelSortEnum temp : HotelSortEnum.values()) {
            if(temp.getName().equals(name)){
                return temp;
            }
        }
        throw MyErrorEnum.errorParm.getMyException("枚举ID错误");
    }
}
