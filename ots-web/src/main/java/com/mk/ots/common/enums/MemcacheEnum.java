//package com.mk.ots.common.enums;
//
//
//public enum MemcacheEnum {
//	hotel("hotel",THotel.class), 
//	member("member",UMember.class),
//	district("district",TDistrict.class),
//	roomType("roomType",TRoomType.class),
//	roomTypeInfo("roomTypeInfo",TRoomTypeInfo.class),
//	;
//	
//	private final static String baseName="mikeweb_";
//	private final String key;
//	private final Class<? extends ICacheable> enumclass;
//	private MemcacheEnum(String key,Class<? extends ICacheable> c){
//		this.key=key;
//		this.enumclass=c;
//	}
//	
//	public static String toMemcacheBaseKey(Class<? extends ICacheable> cla){
//		String key="";
//		for (MemcacheEnum e : MemcacheEnum.values()) {
//			if(cla.equals(e.getEnumclass())){
//				key=e.getKey();
//				break;
//			}
//		}
//		return baseName+key+"_";
//	}
//
//	public String getKey() {
//		return key;
//	}
//
//	public Class<? extends ICacheable> getEnumclass() {
//		return enumclass;
//	}
//	
//}
