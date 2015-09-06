package com.mk.es.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.elasticsearch.common.geo.GeoPoint;

import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.common.utils.Constant;

/**
 * OTS Hotel Entity.
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
    
//    /** hotel introduction */
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
    
    /** 酒店规则值  默认 1001   */
    private int hotelrulecode;
    
    private Long pmsopttime;
    
    /**存放酒店总图片数**/
    private int hotelpicnum;
    
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
        setBusinesszone((List<Map<String, Object>>)bean.get("businesszone"));
        setHotelpic((List<Map<String, Object>>)bean.get("hotelpic"));
        setTraffic((Map<String, String>)bean.get("traffic"));
        setPeripheral((Map<String, String>)bean.get("peripheral"));
        setHotelfacility((List<Map<String, Object>>)bean.get("hotelfacility"));
        setGrade(bean.getBigDecimal("grade"));
        setPin(new GeoPoint(bean.getBigDecimal("latitude").doubleValue(), bean.getBigDecimal("longitude").doubleValue()));
        setIspms(1);
        setHotelrulecode(bean.getInt("rulecode"));
        setHoteldisc((String)bean.get("introduction", ""));
        setFlag(1);
        setCratetime(time);
        setModifytime(time);
        setRetentiontime((String)bean.get("retentiontime",""));
        setDefaultlevaltime((String)bean.get("defaultlevaltime",""));
        setHotelpicnum(bean.getInt("hotelpicnum"));
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

    public Long getCratetime() {
        return createtime;
    }

    public void setCratetime(Long createtime) {
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
	 * @param hotelrulecode the hotelrulecode to set
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
    
    
}
