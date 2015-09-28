package com.mk.ots.common.enums;


/**
 * 酒店搜索方式
 * 0附近；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校；8酒店；9地址；-1不限
 * @author chuaiqing.
 *
 */
public enum HotelSearchEnum {
    NEAR(0, "附近"),
    BZONE(1, "商圈"),
    AIRPORT(2, "机场车站"),
    SUBWAY(3, "地铁线路"),
    AREA(4, "行政区"),
    SAREA(5, "景点"),
    HOSPITAL(6, "医院"),
    COLLEGE(7, "高校"),
    HNAME(8, "酒店名"),
    HADDR(9, "酒店地址"),
    ALL(-1, "不限");
    
    private final Integer id;
    private final String name;
    
    /**
     * 
     * @param id
     * @param name
     * @return
     */
    private HotelSearchEnum(Integer id, String name) {
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
    
    /**
     * 
     * @param id
     * @return
     */
    public static HotelSearchEnum getById(int id){
        for (HotelSearchEnum temp : HotelSearchEnum.values()) {
            if(temp.getId() == id){
                return temp;
            }
        }
        return ALL;
    }
    
    /**
     * 
     * @param name
     * @return
     */
    public static HotelSearchEnum getByName(String name){
        for (HotelSearchEnum temp : HotelSearchEnum.values()) {
            if(temp.getName().equals(name)){
                return temp;
            }
        }
        return ALL;
    }
}
