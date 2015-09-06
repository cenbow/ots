package com.mk.ots.hotel.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.stereotype.Component;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.BizModel;
import com.mk.orm.plugin.bean.Db;

/**
 * hotel Bean
 * @author LYN
 *
 */
@Component
@DbTable(name="t_hotel", pkey="id")
public class THotel extends BizModel<THotel> implements Serializable {
	
	/**
     * 
     */
    private static final long serialVersionUID = -2990942859851601495L;
	public static final THotel dao = new THotel();
	public static final String tableName = "t_hotel";
	
	private String hotelid;
	private String rulecode;
	private String hotelname;
	private String hoteladdr;
	private String keyword;
	private String cityid;//必填
	private String disid;
	/** 用户地理位置坐标：计算酒店“距离我”的距离 */
	private String userlongitude;  //经度
	private String userlatitude;  //纬度
	
	/** 地图地理位置坐标：根据搜索范围查询周边酒店 */
	private String pillowlongitude;  //经度
	private String pillowlatitude;  //纬度
	
	/** 附近搜索距离范围：单位米 */
	private String range;
	
	private String minprice;
	private String maxprice;
	private String bednum;
	private String startdateday;
	private String enddateday;
	private String startdate;//格式yyyyMMddHHmmss
	private String enddate;//格式yyyyMMddHHmmss
	private String page;//必填 页码
	private String limit;//必填 显示条数
	private String orderby; //1:距离 ,2:是否推荐 ,3:最低价格,4:酒店评分
	
	//T 为true ,F或空 为false
	private String isdiscount;//是否考虑优惠价格 T/F
	private String ishotelpic;//是否返回酒店图片 T/F
	private String isroomtype;//是否返回房型信息 T/F
	private String isroomtypepic;//是否返回房型图片 T/F
	private String isroomtypefacility;//是否返回房型设施 T/F
	private String isfacility;//是否返回酒店设施T/F
	private String isbusinesszone;//是否返回商圈信息 T/F
	private String isbedtype;//是否返回床型 T/F
	private String isteambuying; //是否返回团购
	private String ispms; //是否签约T/F
	private String isperipheral;//是否显示周边T/F
	private String istraffic;//是否显示交通
	
	
	private String grade;
	private String scorecount;
	
	private List roomTypeList;
	private List hotelfacilityList;
	private List bussinessZonList;
	
	/**
	 * 
	 * @return
	 */
	public List getRoomTypeList() {
		return roomTypeList;
	}
	public void setRoomTypeList(List roomTypeList) {
		this.roomTypeList = roomTypeList;
	}
	
	public List getHotelfacilityList() {
		return hotelfacilityList;
	}
	public void setHotelfacilityList(List hotelfacilityList) {
		this.hotelfacilityList = hotelfacilityList;
	}
	
	public List getBussinessZonList() {
		return bussinessZonList;
	}
	public void setBussinessZonList(List bussinessZonList) {
		this.bussinessZonList = bussinessZonList;
	}
	public String getHotelid() {
		return hotelid;
	}
	public void setHotelid(String hotelid) {
	    super.put("hotelid", hotelid);
		this.hotelid = hotelid;
	}
	
	public String getRulecode() {
		return rulecode;
	}
	public void setRulecode(String rulecode) {
		super.put("rulecode", rulecode);
		this.rulecode = rulecode;
	}
	public String getHotelname() {
		return hotelname;
	}
	public void setHotelname(String hotelname) {
	    super.put("hotelname", hotelname);
		this.hotelname = hotelname;
	}
	public String getHoteladdr() {
		return hoteladdr;
	}
	public void setHoteladdr(String hoteladdr) {
	    super.put("detailaddr", hoteladdr);
		this.hoteladdr = hoteladdr;
	}
	public String getCityid() {
		return cityid;
	}
	public void setCityid(String cityid) {
	    super.put("hotelcity", cityid);
		this.cityid = cityid;
	}
	public String getDisid() {
		return disid;
	}
	public void setDisid(String disid) {
	    super.put("hoteldis", disid);
		this.disid = disid;
	}
	public String getUserlongitude() {
		return userlongitude;
	}
	public void setUserlongitude(String userlongitude) {
		this.userlongitude = userlongitude;
	}
	public String getUserlatitude() {
		return userlatitude;
	}
	public void setUserlatitude(String userlatitude) {
		this.userlatitude = userlatitude;
	}
	public String getPillowlongitude() {
        return pillowlongitude;
    }
    public void setPillowlongitude(String pillowlongitude) {
        this.pillowlongitude = pillowlongitude;
    }
    public String getPillowlatitude() {
        return pillowlatitude;
    }
    public void setPillowlatitude(String pillowlatitude) {
        this.pillowlatitude = pillowlatitude;
    }
    public String getRange() {
		return range;
	}
	public void setRange(String range) {
		this.range = range;
	}
	public String getMinprice() {
		return minprice;
	}
	public void setMinprice(String minprice) {
		this.minprice = minprice;
	}
	public String getMaxprice() {
		return maxprice;
	}
	public void setMaxprice(String maxprice) {
		this.maxprice = maxprice;
	}
	
	public String getStartdate() {
		return startdate;
	}
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public String getLimit() {
		return limit;
	}
	public void setLimit(String limit) {
		this.limit = limit;
	}
	public String getOrderby() {
		return orderby;
	}
	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}
	public String getIsdiscount() {
		return isdiscount;
	}
	public void setIsdiscount(String isdiscount) {
		this.isdiscount = isdiscount;
	}
	public String getIshotelpic() {
		return ishotelpic;
	}
	public void setIshotelpic(String ishotelpic) {
		this.ishotelpic = ishotelpic;
	}
	public String getIsroomtype() {
		return isroomtype;
	}
	public void setIsroomtype(String isroomtype) {
		this.isroomtype = isroomtype;
	}
	public String getIsroomtypepic() {
		return isroomtypepic;
	}
	public void setIsroomtypepic(String isroomtypepic) {
		this.isroomtypepic = isroomtypepic;
	}
	public String getIsroomtypefacility() {
		return isroomtypefacility;
	}
	public void setIsroomtypefacility(String isroomtypefacility) {
		this.isroomtypefacility = isroomtypefacility;
	}
	public String getIsfacility() {
		return isfacility;
	}
	public void setIsfacility(String isfacility) {
		this.isfacility = isfacility;
	}
	public String getIsbusinesszone() {
		return isbusinesszone;
	}
	public void setIsbusinesszone(String isbusinesszone) {
		this.isbusinesszone = isbusinesszone;
	}
	public String getIsbedtype() {
		return isbedtype;
	}
	public void setIsbedtype(String isbedtype) {
		this.isbedtype = isbedtype;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public String getBednum() {
		return bednum;
	}
	public void setBednum(String bednum) {
		this.bednum = bednum;
	}
	public String getStartdateday() {
		return startdateday;
	}
	public void setStartdateday(String startdateday) {
		this.startdateday = startdateday;
	}
	public String getEnddateday() {
		return enddateday;
	}
	public void setEnddateday(String enddateday) {
		this.enddateday = enddateday;
	}
	public String getIsteambuying() {
		return isteambuying;
	}
	public void setIsteambuying(String isteambuying) {
		this.isteambuying = isteambuying;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getScorecount() {
		return scorecount;
	}
	public void setScorecount(String scorecount) {
		this.scorecount = scorecount;
	}
	public String getIspms() {
		return ispms;
	}
	public void setIspms(String ispms) {
		this.ispms = ispms;
	}

    public String getIsperipheral() {
		return isperipheral;
	}
	public void setIsperipheral(String isperipheral) {
		this.isperipheral = isperipheral;
	}
	public String getIstraffic() {
		return istraffic;
	}
	public void setIstraffic(String istraffic) {
		this.istraffic = istraffic;
	}



	Bean city;
	Bean dis;
	public Bean getTCityByDisId(){
		if (city != null) {
			return city;
		}
		city = Db.findFirst("select c.* from t_city c, t_district d where c.cityid = d.CityID and d.id=?", getLong("disId"));
		return city;
	}
	
	public Bean getTDistrictByDisId(){
		if (dis != null) {
			return dis;
		}
		dis = Db.findFirst("select * from t_district where id=?", getLong("disId"));
		return dis;
	}
	
	
}
