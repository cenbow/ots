package com.mk.ots.common.enums;


/**
 * 床型
 * 1-单人床；2-双人床;3-其他床-1不限
 * @author kirin.
 *
 */
public enum BedTypeEnum {
    SINGLEBED(1, "单人床"),
    DOUBLEBED(2, "双人床"),
    OTHER(3, "其他"),
    ALL(-1, "不限");

    private final Integer id;
    private final String name;

    /**
     *
     * @param id
     * @param name
     * @return
     */
    private BedTypeEnum(Integer id, String name) {
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
    public static BedTypeEnum getById(int id){
        for (BedTypeEnum temp : BedTypeEnum.values()) {
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
    public static BedTypeEnum getByName(String name){
        for (BedTypeEnum temp : BedTypeEnum.values()) {
            if(temp.getName().equals(name)){
                return temp;
            }
        }
        return ALL;
    }
}
