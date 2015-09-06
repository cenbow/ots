package com.mk.ots.hotel.comm.enums;


/**
 * 
 * @author chuaiqing.
 *
 */
public enum HotelPictureEnum {
    PIC_DEF("def", "门头及招牌"),
    PIC_LOBBY("lobby", "大堂"),
    PIC_MAINHOUSING("mainHousing", "主力房源"),
    PIC_UNKNOWN("unknown", "未知")
    ;
    
    private String name;
    private String title;
    
    /**
     * 
     * @param name
     * @param title
     */
    private HotelPictureEnum(String name, String title) {
        this.name = name;
        this.title = title;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public static HotelPictureEnum getByName(String name){
        for (HotelPictureEnum temp : HotelPictureEnum.values()) {
            if(temp.getName().equals(name)){
                return temp;
            }
        }
        return PIC_UNKNOWN;
    }
}
