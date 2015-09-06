package com.mk.ots.hotel.comm.enums;


/**
 * 
 * @author chuaiqing.
 *
 */
public enum RoomTypePictureEnum {
    PIC_DEF("def", "主展图"),
    PIC_LOBBY("bed", "床"),
    PIC_MAINHOUSING("toilet", "卫生间"),
    PIC_UNKNOWN("unknown", "未知")
    ;
    
    private String name;
    private String title;
    
    /**
     * 
     * @param name
     * @param title
     */
    private RoomTypePictureEnum(String name, String title) {
        this.name = name;
        this.title = title;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public static RoomTypePictureEnum getByName(String name){
        for (RoomTypePictureEnum temp : RoomTypePictureEnum.values()) {
            if(temp.getName().equals(name)){
                return temp;
            }
        }
        return PIC_UNKNOWN;
    }
}
