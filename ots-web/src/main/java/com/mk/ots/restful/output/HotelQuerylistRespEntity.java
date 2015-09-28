package com.mk.ots.restful.output;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import com.mk.ots.common.utils.Constant;

/**
 * hotel/querylist酒店综合查询接口返回数据实体类.
 * @author chuaiqing.
 *
 */
public class HotelQuerylistRespEntity implements Serializable {

    /**
     * 序列化UID
     */
    private static final long serialVersionUID = -8180776117382019173L;
    
    //酒店id
    private Long hotelid = Long.MIN_VALUE;
    //酒店名称
    private String hotelname = "";
    //酒店切客规则类型码（1001规则A；1002规则B）
    private String hotelrulecode = "";
    //是否在线(T/F)
    private String online = Constant.STR_TRUE;
    //是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该酒店是否有可入住房间，并根据结果返回T、F
    private String hotelvc = Constant.STR_TRUE;
    //酒店地址
    private String detailaddr = "";
    //酒店所属区县
    private String hoteldis = "";
    //酒店所属城市
    private String hotelcity = "";
    //酒店所属省份
    private String hotelprovince = "";
    //联系电话
    private String hotelphone = "";
    //酒店简介
    private String hoteldisc = "";
    //酒店坐标(经度)
    private Double longitude = 0d;
    //酒店坐标(纬度)
    private Double latitude = 0d;
    //酒店距离（米），该距离表示用户坐标到酒店坐标的距离
    private Double distance = 0d;
    //是否最近酒店，distance值最小的酒店为T,其他为F
    private String isnear = Constant.STR_FALSE;
    //酒店评分
    private BigDecimal grade = BigDecimal.ZERO;
    //评价次数
    private Long scorecount = 0l;
    //最低价格
    private BigDecimal minprice = BigDecimal.ZERO;
    //最低价格对应房型的门市价
    private BigDecimal minpmsprice = BigDecimal.ZERO;
    //是否签约（T/F）
    private String ispms = Constant.STR_TRUE;
    //酒店图片总数
    private Long hotelpicnum = 0l;
    //可订房间数
    private Long avlblroomnum = 0l;
    //可订房描述
    private String avlblroomdes = "";
    //描述字体颜色:（状态：“>3间房间”绿色32ab18, 状态："<=仅剩3间"   红色   fb4b40,状态：满房   灰色    989898）
    private String descolor = "";
    //月订单数
    private Long ordernummon = 0l;
    //最近订单时间描述
    private String rcntordertimedes = "";
    //距离描述
    private String distancestr = "";
    //是否团购（T/F）
    private String isteambuying = Constant.STR_FALSE;
    //团购信息
    private List<TeambuyingBean> teambuying = new ArrayList<TeambuyingBean>();
    //是否推荐（T/F）
    private String isrecommend = Constant.STR_FALSE;
    //返回hotel集合总count数
    //private String count;
    //酒店图片信息
    private List<PicBean> hotelpic = new ArrayList<PicBean>();
    //酒店设施信息
    private List<HotelfacilityBean> hotelfacility = new ArrayList<HotelfacilityBean>();
    //酒店所属商圈信息
    private List<BusinesszoneBean> businesszone = new ArrayList<BusinesszoneBean>();
    //酒店房型信息
    private List<RoomtypeBean> roomtype = new ArrayList<RoomtypeBean>();
    private List<ServiceBean> service = new ArrayList<ServiceBean>();
    //是否返现（T/F）
    private String iscashback = Constant.STR_FALSE;
    
    public Long getHotelid() {
        return hotelid;
    }

    public void setHotelid(Long hotelid) {
        this.hotelid = hotelid;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    public String getHotelrulecode() {
        return hotelrulecode;
    }

    public void setHotelrulecode(String hotelrulecode) {
        this.hotelrulecode = hotelrulecode;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getHotelvc() {
        return hotelvc;
    }

    public void setHotelvc(String hotelvc) {
        this.hotelvc = hotelvc;
    }

    public String getDetailaddr() {
        return detailaddr;
    }

    public void setDetailaddr(String detailaddr) {
        this.detailaddr = detailaddr;
    }

    public String getHoteldis() {
        return hoteldis;
    }

    public void setHoteldis(String hoteldis) {
        this.hoteldis = hoteldis;
    }

    public String getHotelcity() {
        return hotelcity;
    }

    public void setHotelcity(String hotelcity) {
        this.hotelcity = hotelcity;
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

    public String getHoteldisc() {
        return hoteldisc;
    }

    public void setHoteldisc(String hoteldisc) {
        this.hoteldisc = hoteldisc;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getIsnear() {
        return isnear;
    }

    public void setIsnear(String isnear) {
        this.isnear = isnear;
    }

    public BigDecimal getGrade() {
        return grade;
    }

    public void setGrade(BigDecimal grade) {
        this.grade = grade;
    }

    public Long getScorecount() {
        return scorecount;
    }

    public void setScorecount(Long scorecount) {
        this.scorecount = scorecount;
    }

    public BigDecimal getMinprice() {
        return minprice;
    }

    public void setMinprice(BigDecimal minprice) {
        this.minprice = minprice;
    }

    public BigDecimal getMinpmsprice() {
        return minpmsprice;
    }

    public void setMinpmsprice(BigDecimal minpmsprice) {
        this.minpmsprice = minpmsprice;
    }

    public String getIspms() {
        return ispms;
    }

    public void setIspms(String ispms) {
        this.ispms = ispms;
    }

    public Long getHotelpicnum() {
        return hotelpicnum;
    }

    public void setHotelpicnum(Long hotelpicnum) {
        this.hotelpicnum = hotelpicnum;
    }

    public Long getAvlblroomnum() {
        return avlblroomnum;
    }

    public void setAvlblroomnum(Long avlblroomnum) {
        this.avlblroomnum = avlblroomnum;
    }

    public String getAvlblroomdes() {
        return avlblroomdes;
    }

    public void setAvlblroomdes(String avlblroomdes) {
        this.avlblroomdes = avlblroomdes;
    }

    public String getDescolor() {
        return descolor;
    }

    public void setDescolor(String descolor) {
        this.descolor = descolor;
    }

    public Long getOrdernummon() {
        return ordernummon;
    }

    public void setOrdernummon(Long ordernummon) {
        this.ordernummon = ordernummon;
    }

    public String getRcntordertimedes() {
        return rcntordertimedes;
    }

    public void setRcntordertimedes(String rcntordertimedes) {
        this.rcntordertimedes = rcntordertimedes;
    }

    public String getDistancestr() {
        return distancestr;
    }

    public void setDistancestr(String distancestr) {
        this.distancestr = distancestr;
    }

    public String getIsteambuying() {
        return isteambuying;
    }

    public void setIsteambuying(String isteambuying) {
        this.isteambuying = isteambuying;
    }

    public List<TeambuyingBean> getTeambuying() {
        return teambuying;
    }

    public void setTeambuying(List<TeambuyingBean> teambuying) {
        this.teambuying = teambuying;
    }

    public String getIsrecommend() {
        return isrecommend;
    }

    public void setIsrecommend(String isrecommend) {
        this.isrecommend = isrecommend;
    }

    public List<PicBean> getHotelpic() {
        return hotelpic;
    }

    public void setHotelpic(List<PicBean> hotelpic) {
        this.hotelpic = hotelpic;
    }

    public List<HotelfacilityBean> getHotelfacility() {
        return hotelfacility;
    }

    public void setHotelfacility(List<HotelfacilityBean> hotelfacility) {
        this.hotelfacility = hotelfacility;
    }

    public List<BusinesszoneBean> getBusinesszone() {
        return businesszone;
    }

    public void setBusinesszone(List<BusinesszoneBean> businesszone) {
        this.businesszone = businesszone;
    }

    public List<RoomtypeBean> getRoomtype() {
        return roomtype;
    }

    public void setRoomtype(List<RoomtypeBean> roomtype) {
        this.roomtype = roomtype;
    }

    public List<ServiceBean> getService() {
        return service;
    }

    public void setService(List<ServiceBean> service) {
        this.service = service;
    }

    public String getIscashback() {
        return iscashback;
    }

    public void setIscashback(String iscashback) {
        this.iscashback = iscashback;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * 团购信息
     * @author chuaiqing.
     *
     */
    public class TeambuyingBean {
        //团购名称
        private String teambuyingname;
        //团购地址
        private String url;
        
        public String getTeambuyingname() {
            return teambuyingname;
        }
        public void setTeambuyingname(String teambuyingname) {
            this.teambuyingname = teambuyingname;
        }
        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }
    
    /**
     * 酒店图片信息
     * @author chuaiqing.
     *
     */
    public class PicBean {
        //酒店图片名称
        private String name;
        //图片列表
        private List<Pic> pic = new ArrayList<Pic>();
        
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<Pic> getPic() {
            return pic;
        }
        public void setPic(List<Pic> pic) {
            this.pic = pic;
        }
    }
    /**
     * 图片Bean
     * @author chuaiqing.
     *
     */
    public class Pic {
        //图片url
        private String url;

        public String getUrl() {
            return url;
        }
        public void setUrl(String url) {
            this.url = url;
        }
    }
    
    /**
     * 酒店设施信息
     * @author chuaiqing.
     *
     */
    public class HotelfacilityBean {
        //设施id
        private Long facid;
        //设施名称
        private String facname;
        
        public Long getFacid() {
            return facid;
        }
        public void setFacid(Long facid) {
            this.facid = facid;
        }
        public String getFacname() {
            return facname;
        }
        public void setFacname(String facname) {
            this.facname = facname;
        }
    }
    
    /**
     * 酒店所属商圈信息
     * @author chuaiqing.
     *
     */
    public class BusinesszoneBean {
        //商圈名称
        private String businesszonename;

        public String getBusinesszonename() {
            return businesszonename;
        }
        public void setBusinesszonename(String businesszonename) {
            this.businesszonename = businesszonename;
        }
    }
    
    /**
     * 酒店房型信息
     * @author chuaiqing.
     *
     */
    public class RoomtypeBean {
        //房型id
        private Long roomtypeid;
        //房型名称
        private String roomtypename;
        //房型眯客价格
        private BigDecimal roomtypeprice;
        //房型门市价格
        private BigDecimal roomtypepmsprice;
        //是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
        private String roomtypevc;
        //最大面积
        private BigDecimal maxarea;
        //最小面积
        private BigDecimal minarea;
        //床数量（1：单床房，2：双床房，大于2：其他房）
        private Integer bednum;
        //房间数量
        private Integer roomnum;
        //房间内床数
        private Integer count;
        //床型信息
        private List<BedBean> beds = new ArrayList<BedBean>();
        //房型图片信息
        private List<PicBean> roomtypepic = new ArrayList<PicBean>();
        //房型设施信息
        private List<RoomtypefacilityBean> roomtypefacility = new ArrayList<RoomtypefacilityBean>();
        //private String floor;
        
        public Long getRoomtypeid() {
            return roomtypeid;
        }
        public void setRoomtypeid(Long roomtypeid) {
            this.roomtypeid = roomtypeid;
        }
        public String getRoomtypename() {
            return roomtypename;
        }
        public void setRoomtypename(String roomtypename) {
            this.roomtypename = roomtypename;
        }
        public BigDecimal getRoomtypeprice() {
            return roomtypeprice;
        }
        public void setRoomtypeprice(BigDecimal roomtypeprice) {
            this.roomtypeprice = roomtypeprice;
        }
        public BigDecimal getRoomtypepmsprice() {
            return roomtypepmsprice;
        }
        public void setRoomtypepmsprice(BigDecimal roomtypepmsprice) {
            this.roomtypepmsprice = roomtypepmsprice;
        }
        public String getRoomtypevc() {
            return roomtypevc;
        }
        public void setRoomtypevc(String roomtypevc) {
            this.roomtypevc = roomtypevc;
        }
        public BigDecimal getMaxarea() {
            return maxarea;
        }
        public void setMaxarea(BigDecimal maxarea) {
            this.maxarea = maxarea;
        }
        public BigDecimal getMinarea() {
            return minarea;
        }
        public void setMinarea(BigDecimal minarea) {
            this.minarea = minarea;
        }
        public Integer getBednum() {
            return bednum;
        }
        public void setBednum(Integer bednum) {
            this.bednum = bednum;
        }
        public Integer getRoomnum() {
            return roomnum;
        }
        public void setRoomnum(Integer roomnum) {
            this.roomnum = roomnum;
        }
        public Integer getCount() {
            return count;
        }
        public void setCount(Integer count) {
            this.count = count;
        }
        public List<BedBean> getBeds() {
            return beds;
        }
        public void setBeds(List<BedBean> beds) {
            this.beds = beds;
        }
        public List<PicBean> getRoomtypepic() {
            return roomtypepic;
        }
        public void setRoomtypepic(List<PicBean> roomtypepic) {
            this.roomtypepic = roomtypepic;
        }
        public List<RoomtypefacilityBean> getRoomtypefacility() {
            return roomtypefacility;
        }
        public void setRoomtypefacility(List<RoomtypefacilityBean> roomtypefacility) {
            this.roomtypefacility = roomtypefacility;
        }
    }
    /**
     * 房型下床信息
     * @author chuaiqing.
     *
     */
    public class BedBean {
        //床型(双人床，单人床)
        private String bedtypename;
        //尺寸(1.5米，1.8米)
        private String bedlength;
        
        public String getBedtypename() {
            return bedtypename;
        }
        public void setBedtypename(String bedtypename) {
            this.bedtypename = bedtypename;
        }
        public String getBedlength() {
            return bedlength;
        }
        public void setBedlength(String bedlength) {
            this.bedlength = bedlength;
        }
    }
    /**
     * 房型设施Bean
     * @author chuaiqing.
     *
     */
    public class RoomtypefacilityBean {
        //设施id
        private Long roomtypefacid;
        //设施名称
        private String roomtypefacname;
        
        public Long getRoomtypefacid() {
            return roomtypefacid;
        }
        public void setRoomtypefacid(Long roomtypefacid) {
            this.roomtypefacid = roomtypefacid;
        }
        public String getRoomtypefacname() {
            return roomtypefacname;
        }
        public void setRoomtypefacname(String roomtypefacname) {
            this.roomtypefacname = roomtypefacname;
        }
    }
    
    /**
     * 酒店服务
     * @author chuaiqing.
     *
     */
    public class ServiceBean {
        //服务id
        private Long serviceid;
        //服务名称
        private String servicename;
        
        public Long getServiceid() {
            return serviceid;
        }
        public void setServiceid(Long serviceid) {
            this.serviceid = serviceid;
        }
        public String getServicename() {
            return servicename;
        }
        public void setServicename(String servicename) {
            this.servicename = servicename;
        }
    }

    public static void main (String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            HotelQuerylistRespEntity resp = new HotelQuerylistRespEntity();
            System.out.println(objectMapper.writeValueAsString(resp));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
