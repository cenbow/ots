package com.mk.ots.restful.input;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import com.mk.ots.common.bean.ParamBaseBean;

/**
 * hotel/querylist酒店综合查询接口入参实体类.
 * 
 * @author chuaiqing.
 *
 */
public class HotelQuerylistReqEntity extends ParamBaseBean implements Serializable {

	/**
	 * 序列化UID
	 */
	private static final long serialVersionUID = 8392856964959038410L;

	private String token;
	private String hotelid;
	private String hotelname;
	private String hoteladdr;
	private String keyword;

	/** 入参: 城市code,必填 */
	@NotNull(message = "缺少参数-城市编码: cityid.")
	@NotEmpty(message = "接口参数cityid不能为空.")
	private String cityid;

	private String disid;
	/** 用户地理位置坐标：计算酒店“距离我”的距离 */
	// 经度
	private Double userlongitude;
	// 纬度
	private Double userlatitude;

	/** 地图地理位置坐标：根据搜索范围查询周边酒店 */
	// 经度
	private Double pillowlongitude;
	// 纬度
	private Double pillowlatitude;

	/** 附近搜索距离范围：单位米 */
	// @Range(min=1000,message="搜索半径不能小于1000米")
	private Integer range;

	private String minprice;
	private String maxprice;
	// 床型
	private String bednum;
	// 查询开始日期
	private String startdateday;
	// 查询截止日期
	private String enddateday;
	// 入住时间：格式yyyyMMddHHmmss
	private String startdate;
	// 离店时间：格式yyyyMMddHHmmss
	private String enddate;

	/** 入参: 第几页,必填 */
	@NotNull(message = "缺少参数-第几页: page.")
	@Range(min = 1, message = "无效的参数值,page不能小于1.")
	private Integer page;

	/** 入参: 每页多少条,必填 */
	@NotNull(message = "缺少参数-每页多少条: limit.")
	@Range(min = 1, message = "无效的参数值,limit不能小于1.")
	private Integer limit;

	/**
	 * 排序项目: 非必填,
	 * 1:距离(distance),2:是否推荐(recommend),3:最低价格(minprice),4:酒店评分(score)
	 */
	private Integer orderby;

	// T 为true ,F或空 为false
	// 是否考虑优惠价格 T/F
	private String isdiscount;
	// 是否返回酒店图片 T/F
	private String ishotelpic;
	// 是否返回房型信息 T/F
	private String isroomtype;
	// 是否返回房型图片 T/F
	private String isroomtypepic;
	// 是否返回房型设施 T/F
	private String isroomtypefacility;
	// 是否返回酒店设施 T/F
	private String isfacility;
	// 是否返回商圈信息 T/F
	private String isbusinesszone;
	// 是否返回床型 T/F
	private String isbedtype;
	// 是否返回团购
	private String isteambuying;
	// 是否签约 T/F
	private String ispms;
	// 酒店类型: 不限/精品酒店/主题酒店/公寓/客栈/旅馆/招待所
	private String hoteltype;
	// 床型: -1不限/1单床房/2双床房
	private Integer bedtype;
	// 调用来源: 1-crs 客服；2-web；3-wechat 微信；4-app(ios) iOS App；5-app(Android)
	// android App
	private String callmethod;
	// app版本
	private String callversion;
	// 发起请求的ip
	private String ip;
	// 硬件编码
	private String hardwarecode;
	// OTS接口版本
	private String otsversion;
	// 排除酒店id: 用户酒店附近搜索时，过滤当前的酒店
	private String excludehotelid;
	// 搜索方式(0关键字；1商圈；2机场车站；3地铁路线；4行政区；5景点；6医院；7高校)
	private Integer searchtype;
	// 搜索位置区域id
	private String posid;
	// 搜索位置区域名称
	private String posname;
	// 坐标集合
	private String points;
	/**
	 * the entry identifier which represents the search entrance
	 * 
	 * 1-摇一摇 2-房态搜索入口
	 */
	private Integer callentry;

	private Boolean ispromoonly;

	private String promoid;
	
	private String promotype;
	
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPromoid() {
		return promoid;
	}

	public void setPromoid(String promoid) {
		this.promoid = promoid;
	}

	public String getPromotype() {
		return promotype;
	}

	public void setPromotype(String promotype) {
		this.promotype = promotype;
	}

	public Boolean getIspromoonly() {
		return ispromoonly;
	}

	public void setIspromoonly(Boolean ispromoonly) {
		this.ispromoonly = ispromoonly;
	}

	public Integer getCallentry() {
		return callentry;
	}

	public void setCallentry(Integer callentry) {
		this.callentry = callentry;
	}

	public String getHotelid() {
		return hotelid;
	}

	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}

	public String getHotelname() {
		return hotelname;
	}

	public void setHotelname(String hotelname) {
		this.hotelname = hotelname;
	}

	public String getHoteladdr() {
		return hoteladdr;
	}

	public void setHoteladdr(String hoteladdr) {
		this.hoteladdr = hoteladdr;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getCityid() {
		return cityid;
	}

	public void setCityid(String cityid) {
		this.cityid = cityid;
	}

	public String getDisid() {
		return disid;
	}

	public void setDisid(String disid) {
		this.disid = disid;
	}

	public Double getUserlongitude() {
		return userlongitude;
	}

	public void setUserlongitude(Double userlongitude) {
		this.userlongitude = userlongitude;
	}

	public Double getUserlatitude() {
		return userlatitude;
	}

	public void setUserlatitude(Double userlatitude) {
		this.userlatitude = userlatitude;
	}

	public Double getPillowlongitude() {
		return pillowlongitude;
	}

	public void setPillowlongitude(Double pillowlongitude) {
		this.pillowlongitude = pillowlongitude;
	}

	public Double getPillowlatitude() {
		return pillowlatitude;
	}

	public void setPillowlatitude(Double pillowlatitude) {
		this.pillowlatitude = pillowlatitude;
	}

	public Integer getRange() {
		return range;
	}

	public void setRange(Integer range) {
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

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOrderby() {
		return orderby;
	}

	public void setOrderby(Integer orderby) {
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

	public String getIsteambuying() {
		return isteambuying;
	}

	public void setIsteambuying(String isteambuying) {
		this.isteambuying = isteambuying;
	}

	public String getIspms() {
		return ispms;
	}

	public void setIspms(String ispms) {
		this.ispms = ispms;
	}

	public String getHoteltype() {
		return hoteltype;
	}

	public void setHoteltype(String hoteltype) {
		this.hoteltype = hoteltype;
	}

	public Integer getBedtype() {
		return bedtype;
	}

	public void setBedtype(Integer bedtype) {
		this.bedtype = bedtype;
	}

	public String getCallmethod() {
		return callmethod;
	}

	public void setCallmethod(String callmethod) {
		this.callmethod = callmethod;
	}

	public String getCallversion() {
		return callversion;
	}

	public void setCallversion(String callversion) {
		this.callversion = callversion;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHardwarecode() {
		return hardwarecode;
	}

	public void setHardwarecode(String hardwarecode) {
		this.hardwarecode = hardwarecode;
	}

	public String getOtsversion() {
		return otsversion;
	}

	public void setOtsversion(String otsversion) {
		this.otsversion = otsversion;
	}

	public String getExcludehotelid() {
		return excludehotelid;
	}

	public void setExcludehotelid(String excludehotelid) {
		this.excludehotelid = excludehotelid;
	}

	public Integer getSearchtype() {
		return searchtype;
	}

	public void setSearchtype(Integer searchtype) {
		this.searchtype = searchtype;
	}

	public String getPosid() {
		return posid;
	}

	public void setPosid(String posid) {
		this.posid = posid;
	}

	public String getPosname() {
		return posname;
	}

	public void setPosname(String posname) {
		this.posname = posname;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

}
