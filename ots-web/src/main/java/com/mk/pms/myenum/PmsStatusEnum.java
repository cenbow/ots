package com.mk.pms.myenum;
/**
 *
 * @author shellingford
 * @version 2015年1月16日
 */
public enum PmsStatusEnum {
	init(0,"初始化，未同步pms"),
	find(1,"已成功匹配到一次，正在进行同步工作"),
	syn(2,"酒店房型等数据已同步完成"),
	;
	
	private final Integer id;
	private final String name;
	
	private PmsStatusEnum(Integer id,String name){
		this.id=id;
		this.name=name;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return id.toString();
	}
}
