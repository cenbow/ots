package com.mk.ots.search.enums;


public enum PositionTypeEnum {
	NEAR(0,"附近"),
	BUSINESSZONE(1,"商圈"),
    AIRPORTSTATION(2,"机场车站"),
    METRO(3,"地铁线路"),
    DISTRICT(4,"行政区"),
    SCENIC(5,"景点"),
    HOSPITAL(6,"医院"),
    HIGHTSTCHOOL(7,"高校"),
    HOTEL(8,"酒店"),
    ADDRESS(9,"地址"),
    ALL(-1,"不限");
   ;
   
   private int id;
   private String typeName;
   
   
   
   public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public String getTypeName() {
	return typeName;
}
public void setTypeName(String typeName) {
	this.typeName = typeName;
}
/**
    * 
    * @param id
    * @param typeName
    */
   private PositionTypeEnum(int id, String typeName) {
       this.id = id;
       this.typeName = typeName;
   }
   public static PositionTypeEnum getById(int id){
       for (PositionTypeEnum temp : PositionTypeEnum.values()) {
           if(temp.getId() == id){
               return temp;
           }
       }
       return ALL;
   }
}
