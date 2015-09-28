/**
 * 
 */
package com.mk.ots.hotel.jsonbean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author he
 * 房间json转化bean
 */
public class HotelPicJsonBean {
	private String name;
	private List<HotelPicUrlJsonBean> pic = new ArrayList<HotelPicUrlJsonBean>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<HotelPicUrlJsonBean> getPic() {
		return pic;
	}
	public void setPic(List<HotelPicUrlJsonBean> pic) {
		this.pic = pic;
	}
	
}
