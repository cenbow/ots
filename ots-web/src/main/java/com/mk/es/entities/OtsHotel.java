package com.mk.es.entities;

import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.common.utils.Constant;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.common.geo.GeoPoint;

import java.math.BigDecimal;
import java.util.*;

/**
 * OTS Hotel Entity.
 * 
 * @author chuaiqing.
 *
 */
public class OtsHotel {
	/** hotel id */
	private String hotelid;

	/** hotel name */
	private String hotelname;

	/** hotel introduction */
	private String introduction;

	/** hotel address */
	private String detailaddr;

	/** hotel min price */
	private BigDecimal minprice;

	/** hotel max price */
	private BigDecimal maxprice;

	/** hotel GeoPoint */
	private GeoPoint pin = null;

	/** hotel city id */
	private String hotelcity;

	/** hotel dis id */
	private String hoteldis;

	/** hotel business zone */
	private List<Map<String, Object>> businesszone;

	/** hotel traffic */
	private Map<String, String> traffic;

	/** hotel peripheral */
	private Map<String, String> peripheral;

	/** hotel pictures */
	private List<Map<String, Object>> hotelpic;

	private List<Map<String, Object>> hotelfacility;

	/** 最晚保留时间 */
	private String retentiontime;

	/** 默认离店时间 */
	private String defaultlevaltime;

	/** hotel grade */
	private BigDecimal grade;

	/** hotel is pms */
	private int ispms;

	// /** hotel introduction */
	private String hoteldisc;

	/** hotel data record create time */
	private Long createtime;

	/** hotel data record modify time */
	private Long modifytime;

	/** hote data record flag */
	private int flag;

	/** 酒店是否可用: T可用，F不可用 */
	private String visible = Constant.STR_TRUE;
	/** 酒店是否在线: T在线，F不在线 */
	private String online = Constant.STR_TRUE;

	/** 单床房数量 */
	private long numroomtype1 = 1;
	/** 双床房数量 */
	private long numroomtype2 = 1;
	/** 其它类型房数量 */
	private long numroomtype3 = 1;
	/** 酒店房间总数 */
	private long roomnum = 0l;
	/** 是否新pms */
	private String isnewpms = Constant.STR_FALSE;
	/** 酒店权重: 1特推; 2主推; 3一般推荐; 4不推荐 */
	private int priority = 3;

	/** 酒店规则值 默认 1001 */
	private int hotelrulecode;

	private Long pmsopttime;

	/** 存放酒店总图片数 **/
	private int hotelpicnum;

	private Integer hoteltype;

	/** mike 3.0 月销量 **/
	private Long ordernummon;

	/** 酒店省份编码 */
	private String provcode;
	/** 酒店城市编码 */
	private String citycode;
	/** 酒店区县编码 */
	private String discode;
	/** 酒店区域编码 */
	private String areacode;
	/** 酒店区域名 */
	private String areaname;

	/** 酒店区县名称 */
	private String hoteldisname;
	/** 酒店城市名称 */
	private String hotelcityname;
	/** 酒店省份名称 */
	private String hotelprovince;
	/** 酒店联系电话 */
	private String hotelphone;

	// mike3.1 添加今夜特价房信息
	private String isonpromo;

	private List<Map<String, Object>> promoinfo;
	
	private List<Integer> promoids;

	private List<Map<String, Object>> bedtypes;

	public OtsHotel() {

	}

	/**
	 * 
	 * @param bean
	 */
	public OtsHotel(Bean bean) {
		if (bean == null) {
			return;
		}
		long time = (new Date()).getTime();
		setHotelid(String.valueOf(bean.get("id")));
		setHotelname(bean.getStr("hotelname"));
		setMaxprice(bean.getBigDecimal("maxprice"));
		setMinprice(bean.getBigDecimal("minprice"));
		setDetailaddr(bean.getStr("detailaddr"));
		setHotelcity(String.valueOf(bean.get("cityid")));
		setHoteldis(String.valueOf(bean.get("disid")));
		setBusinesszone((List<Map<String, Object>>) bean.get("businesszone"));
		setHotelpic((List<Map<String, Object>>) bean.get("hotelpic"));
		setTraffic((Map<String, String>) bean.get("traffic"));
		setPeripheral((Map<String, String>) bean.get("peripheral"));
		setHotelfacility((List<Map<String, Object>>) bean.get("hotelfacility"));
		setGrade(bean.getBigDecimal("grade"));
		setPin(new GeoPoint(bean.getBigDecimal("latitude").doubleValue(),
				bean.getBigDecimal("longitude").doubleValue()));
		setIspms(1);
		setHotelrulecode(bean.getInt("rulecode"));
		setHoteldisc((String) bean.get("introduction", ""));
		setFlag(1);
		setCreatetime(time);
		setModifytime(time);
		setRetentiontime((String) bean.get("retentiontime", ""));
		setDefaultlevaltime((String) bean.get("defaultlevaltime", ""));
		setHotelpicnum(bean.getInt("hotelpicnum"));
		if (bean.get("hoteltype") != null) {
			if (StringUtils.isNotBlank(String.valueOf(bean.get("hoteltype")))) {
				setHoteltype(Integer.valueOf(String.valueOf(bean.get("hoteltype"))));
			}
		}
		// 订单初始化ES时 月销量为0
		setOrdernummon(0l);
	}

	public List<Integer> getPromoids() {
		return promoids;
	}

	public void setPromoids(List<Integer> promoids) {
		this.promoids = promoids;
	}

	public List<Map<String, Object>> getBedtypes() {
		return bedtypes;
	}

	public void setBedtypes(List<Map<String, Object>> bedtypes) {
		this.bedtypes = bedtypes;
	}

	public String getIsonpromo() {
		return isonpromo;
	}

	public void setIsonpromo(String isonpromo) {
		this.isonpromo = isonpromo;
	}

	/** getters and setters */
	public String getHotelid() {
		return hotelid;
	}

	public void setHotelid(String id) {
		this.hotelid = id;
	}

	public String getHotelname() {
		return hotelname;
	}

	public void setHotelname(String name) {
		this.hotelname = name;
	}

	public BigDecimal getMinprice() {
		return minprice;
	}

	public void setMinprice(BigDecimal minprice) {
		this.minprice = minprice;
	}

	public BigDecimal getMaxprice() {
		return maxprice;
	}

	public void setMaxprice(BigDecimal maxprice) {
		this.maxprice = maxprice;
	}

	public GeoPoint getPin() {
		return this.pin;
	}

	public void setPin(GeoPoint pin) {
		this.pin = pin;
	}

	public String getDetailaddr() {
		return this.detailaddr;
	}

	public void setDetailaddr(String addr) {
		this.detailaddr = addr;
	}

	public String getHotelcity() {
		return this.hotelcity;
	}

	public void setHotelcity(String cityid) {
		this.hotelcity = cityid;
	}

	public String getHoteldis() {
		return this.hoteldis;
	}

	public void setHoteldis(String disid) {
		this.hoteldis = disid;
	}

	public List<Map<String, Object>> getBusinesszone() {
		return businesszone;
	}

	public void setBusinesszone(List<Map<String, Object>> bizzone) {
		this.businesszone = bizzone;
	}

	public Map<String, String> getTraffic() {
		return traffic;
	}

	public void setTraffic(Map<String, String> traffic) {
		this.traffic = traffic;
	}

	public List<Map<String, Object>> getHotelpic() {
		return this.hotelpic;
	}

	public void setHotelpic(List<Map<String, Object>> pics) {
		this.hotelpic = pics;
	}

	public List<Map<String, Object>> getHotelfacility() {
		return hotelfacility;
	}

	public void setHotelfacility(List<Map<String, Object>> hotelfacility) {
		this.hotelfacility = hotelfacility;
	}

	public BigDecimal getGrade() {
		return grade;
	}

	public void setGrade(BigDecimal grade) {
		this.grade = grade;
	}

	public int getIspms() {
		return ispms;
	}

	public void setIspms(int ispms) {
		this.ispms = ispms;
	}

	public String getHoteldisc() {
		return hoteldisc;
	}

	public void setHoteldisc(String hoteldisc) {
		this.hoteldisc = hoteldisc;
	}

	public Long getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Long createtime) {
		this.createtime = createtime;
	}

	public Long getModifytime() {
		return modifytime;
	}

	public void setModifytime(Long modifytime) {
		this.modifytime = modifytime;
	}

	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getVisible() {
		return visible;
	}

	public void setVisible(String visible) {
		this.visible = visible;
	}

	public String getOnline() {
		return online;
	}

	public void setOnline(String online) {
		this.online = online;
	}

	public long getNumroomtype1() {
		return numroomtype1;
	}

	public void setNumroomtype1(long numroomtype1) {
		this.numroomtype1 = numroomtype1;
	}

	public long getNumroomtype2() {
		return numroomtype2;
	}

	public void setNumroomtype2(long numroomtype2) {
		this.numroomtype2 = numroomtype2;
	}

	public long getNumroomtype3() {
		return numroomtype3;
	}

	public void setNumroomtype3(long numroomtype3) {
		this.numroomtype3 = numroomtype3;
	}

	public long getRoomnum() {
		return roomnum;
	}

	public void setRoomnum(long roomnum) {
		this.roomnum = roomnum;
	}

	public String getIsnewpms() {
		return isnewpms;
	}

	public void setIsnewpms(String isnewpms) {
		this.isnewpms = isnewpms;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	/**
	 * @return the hotelrulecode
	 */
	public int getHotelrulecode() {
		return hotelrulecode;
	}

	/**
	 * @param hotelrulecode
	 *            the hotelrulecode to set
	 */
	public void setHotelrulecode(int hotelrulecode) {
		this.hotelrulecode = hotelrulecode;
	}

	public Map<String, String> getPeripheral() {
		return peripheral;
	}

	public void setPeripheral(Map<String, String> peripheral) {
		this.peripheral = peripheral;
	}

	public String getRetentiontime() {
		return retentiontime;
	}

	public void setRetentiontime(String retentiontime) {
		this.retentiontime = retentiontime;
	}

	public String getDefaultlevaltime() {
		return defaultlevaltime;
	}

	public void setDefaultlevaltime(String defaultlevaltime) {
		this.defaultlevaltime = defaultlevaltime;
	}

	public Long getPmsopttime() {
		return pmsopttime;
	}

	public void setPmsopttime(Long pmsopttime) {
		this.pmsopttime = pmsopttime;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getHotelpicnum() {
		return hotelpicnum;
	}

	public void setHotelpicnum(int hotelpicnum) {
		this.hotelpicnum = hotelpicnum;
	}

	public Integer getHoteltype() {
		return hoteltype;
	}

	public void setHoteltype(Integer hoteltype) {
		this.hoteltype = hoteltype;
	}

	public Long getOrdernummon() {
		return ordernummon;
	}

	public void setOrdernummon(Long ordernummon) {
		this.ordernummon = ordernummon;
	}

	public String getProvcode() {
		return provcode;
	}

	public void setProvcode(String provcode) {
		this.provcode = provcode;
	}

	public String getCitycode() {
		return citycode;
	}

	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}

	public String getDiscode() {
		return discode;
	}

	public void setDiscode(String discode) {
		this.discode = discode;
	}

	public String getAreacode() {
		return areacode;
	}

	public void setAreacode(String areacode) {
		this.areacode = areacode;
	}

	public String getAreaname() {
		return areaname;
	}

	public void setAreaname(String areaname) {
		this.areaname = areaname;
	}

	public String getHoteldisname() {
		return hoteldisname;
	}

	public void setHoteldisname(String hoteldisname) {
		this.hoteldisname = hoteldisname;
	}

	public String getHotelcityname() {
		return hotelcityname;
	}

	public void setHotelcityname(String hotelcityname) {
		this.hotelcityname = hotelcityname;
	}

	public String getHotelprovince() {
		return hotelprovince;
	}

	public void setHotelprovince(String hotelprovince) {
		this.hotelprovince = hotelprovince;
	}

	public String getHotelphone() {
		return hotelphone;
	}

	public void setHotelphone(String hotelphone) {
		this.hotelphone = hotelphone;
	}

	public List<Map<String, Object>> getPromoinfo() {
		return promoinfo;
	}

	public void setPromoinfo(List<Map<String, Object>> promoinfo) {
		this.promoinfo = promoinfo;
	}

}
