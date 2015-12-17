
package com.mk.ots.hotel.service;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mk.es.entities.OtsHotel;
import com.mk.framework.AppUtils;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.utils.CSVFileUtil;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.EHotel;
import com.mk.ots.hotel.comm.enums.HotelPictureEnum;
import com.mk.ots.hotel.comm.enums.HotelTypeEnum;
import com.mk.ots.hotel.comm.enums.RoomTypePictureEnum;
import com.mk.ots.hotel.dao.*;
import com.mk.ots.hotel.jsonbean.HotelPicJsonBean;
import com.mk.ots.hotel.model.*;
import com.mk.ots.mapper.*;
import com.mk.ots.order.dao.OrderDAO;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.price.dao.BasePriceDAO;
import com.mk.ots.price.dao.PriceDAO;
import com.mk.ots.restful.output.RoomstateQuerylistRespEntity;
import com.mk.ots.roomsale.model.TRoomSale;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.score.dao.ScoreDAO;
import com.mk.ots.ticket.dao.BHotelStatDao;
import com.mk.ots.utils.DistanceUtil;
import com.mk.ots.web.ServiceOutput;
import com.mk.pms.order.dao.PmsOrderDAO;
import com.mk.pms.order.dao.PmsRoomOrderDAO;
import com.mk.sever.ServerChannel;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 酒店服务类
 *
 * @author LYN
 */
@Service
public class HotelService {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(HotelService.class);
	private Gson gson = new Gson();

	@Autowired
	private HotelDAO hotelDAO = null;
	@Autowired
	private CityDAO cityDAO = null;
	@Autowired
	private RoomTypeDAO roomTypeDAO = null;

	@Autowired
	private ScoreDAO scoreDAO = null;

	@Autowired
	private THotelMapper hotelMapper;

	@Autowired
	private OrderService orderService;

	@Autowired
	private TRoomMapper tRoomMapper;

	@Autowired
	private CostTempDAO costTempDAO = null;

	@Autowired
	private RoomDAO roomDAO = null;

	/**
	 * 注入新酒店价格服务
	 */
	@Autowired
	private HotelPriceService hotelPriceService;

	@Autowired
	private PmsRoomOrderDAO pmsRoomOrderDAO = null;

	@Autowired
	private PmsOrderDAO pmsOrderDAO = null;

	@Autowired
	private RoomRepairDAO roomRepairDAO = null;

	@Autowired
	private OrderDAO orderDAO;

	@Autowired
	private PriceDAO priceDAO = null;

	@Autowired
	private BasePriceDAO basePriceDAO = null;

	@Autowired
	protected ElasticsearchProxy esProxy;

	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private RoomSaleConfigInfoService roomSaleConfigInfoService;

	@Autowired
	private TFacilityMapper tFacilityMapper;
	@Autowired
	private TBusinesszoneMapper tBusinesszoneMapper;

	@Autowired
	private BHotelStatDao bHotelStatDao;

	@Autowired
	protected SqlSessionFactory sqlSessionFactory;

	@Autowired
	private CityService cityService;

	@Autowired
	private THotelMapper thotelMapper;

	@Autowired
	private TDistrictMapper tdistrictMapper;

	@Autowired
	private CashBackService cashBackService;

	@Autowired
	private BedTypeMapper bedTypeMapper;

	@Autowired
	private RoomSaleService roomSaleService;

	/**
	 * es filter builders
	 */
	private List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
	/**
	 * es keyword filter builders
	 */
	private List<FilterBuilder> keywordBuilders = new ArrayList<FilterBuilder>();
	@Autowired
	private TRoomtypeInfoMapper tRoomtypeInfoMapper;
	private final ExecutorService exService = Executors.newFixedThreadPool(2);

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");

	/**
	 * 房态标识：可用
	 */
	private final String ROOM_STATUS_VC = "vc";
	/**
	 * 房态标识：不可用
	 */
	private final String ROOM_STATUS_NVC = "nvc";

	private final AtomicInteger mkPriceCounter = new AtomicInteger(0);

	public THotel readonlyTHotel(long id) {
		return THotel.dao.findById(id);
	}

	public EHotel readonlyEHotel(Long id) {
		return EHotel.dao.findById(id);
	}

	/**
	 * 酒店综合查询默认分页
	 */
	public static final Integer HOTEL_PAGE_DEFAULT = 1;
	/**
	 * 酒店综合查询默认显示记录数
	 */
	public static final Integer HOTEL_LIMIT_DEFAULT = 10;
	/**
	 * 酒店综合查询默认查询范围: 单位米
	 */
	public static final Integer HOTEL_RANGE_DEFAULT = 3000000;

	/**
	 * 酒店眯客价保存天数
	 */
	public static final Integer MIKEPRICE_DAYS = 33;

	/**
	 * 眯客价属性前缀名
	 */
	public static final String MIKE_PRICE_PROP = "$mike_price_";
	/**
	 * 眯客价属性
	 */
	public static final String MIKE_PRICE_DATE = MIKE_PRICE_PROP.concat("yyyyMMdd");

	/**
	 * 初始化签约酒店数据到ES
	 *
	 * @param cityid
	 *            参数：城市编码
	 * @return
	 */
	public ServiceOutput readonlyInitPmsHotel(String cityid, String thotelid) {
		// TODO: 有则忽略，无则添加
		/*
		 * select a.*,c.code as cityid, c.cityid as
		 * city_id,c.cityname,f.maxprice,f.minprice from t_hotel a left outer
		 * join t_district b on a.disid = b.id left outer join t_city c on
		 * b.cityid=c.cityid left outer join (select hotelid,max(cost) as
		 * maxprice,min(cost) as minprice from b_costtemp_310000 group by
		 * hotelid) f on a.id=f.hotelid where a.visible = 'T' and a.online =
		 * 'T'; and a.id='555';
		 */
		long time = (new Date()).getTime();
		ServiceOutput output = new ServiceOutput();
		SqlSession session = null;
		try {
			String sql = "select a.*,c.code as cityid, c.cityid as city_id,c.cityname,"
					+ "     case when f.maxprice is null then 0 else f.maxprice end as maxprice,"
					+ "     case when f.minprice is null then 0 else f.minprice end as minprice " + " from t_hotel a "
					+ "     left outer join t_district b on a.disid = b.id "
					+ "       left outer join t_city c on b.cityid=c.cityid "
					+ "         left outer join (select hotelid,max(cost) as maxprice,min(cost) as minprice "
					+ "                            from b_costtemp_" + cityid + " group by hotelid) f on a.id=f.hotelid"
					+ "           where 1=1 ";
			// //+ " and a.visible = 'T' and a.online = 'T' ";
			if (!StringUtils.isBlank(thotelid)) {
				sql = sql + " and a.id = '" + thotelid + "'";
			}
			session = sqlSessionFactory.openSession();
			THotelMapper mapper = session.getMapper(THotelMapper.class);
			Collection<Object> coll = new ArrayList<Object>();
			try {
				List<THotelModel> hotels = mapper.findListInfo(thotelid == null ? null : Long.valueOf(thotelid));
				StringBuffer bfSql = new StringBuffer();
				/**
				 * add only for temporary use
				 */
				for (int j = 0; j < hotels.size(); j++) {
					THotelModel bean = hotels.get(j);
					// for (THotelModel bean : hotels) {
					String hotelid = bean.getId().toString();

					// 如果ES中已经有该酒店，则先删除再重新添加。
					SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);

					if (searchHits != null) {
						for (int i = 0; i < searchHits.length; i++) {
							logger.info("hotelid: {} has exists.", hotelid);
							esProxy.deleteDocument(searchHits[i].getId());
							logger.info("hotelid: {} has deleted.", hotelid);
						}
					}

					List<Map<String, Object>> businessZones = new ArrayList<Map<String, Object>>();
					bfSql.setLength(0);
					bfSql.append("select a.*,b.name from t_hotelbussinesszone a "
							+ " left outer join t_businesszone b on a.businesszoneid=b.id " + " where hotelid=?");
					List<Bean> list = Db.find(bfSql.toString(), hotelid);

					for (Bean item : list) {
						businessZones.add(item.getColumns());
					}

					// 图片显示更改为 显示酒店下的所有图片并且房型图片在前 by LYN 2015-08-26
					List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
					// 房型图片
					// TODO: 待确认，先注释掉
					/*
					 * List<TRoomTypeInfoModel> rtfs =
					 * tRoomtypeInfoMapper.findByHotelid(Long.valueOf(hotelid));
					 * for(TRoomTypeInfoModel roomtypeinfo : rtfs){ String rtPic
					 * = roomtypeinfo.getPics();
					 * if(StringUtils.isNotBlank(rtPic)){ List<Map<String,
					 * Object>> rtp = (new Gson()).fromJson(rtPic, List.class);
					 * pics.addAll(rtp); } }
					 */

					// 酒店图片
					String hotelpic = bean.getHotelpic();
					if (!StringUtils.isBlank(hotelpic)) {
						try {
							List<Map<String, Object>> hotelPics = (new Gson()).fromJson(hotelpic, List.class);
							pics.addAll(hotelPics);
						} catch (Exception e) {
							logger.error("解析酒店 {} 图片数据 {} 出错. {}", hotelid, hotelpic, e.getMessage());
						}
					}

					// 新增 交通 hotel traffic
					Map<String, String> traffic = new HashMap<String, String>();
					String trafficStr = bean.getTraffic();
					if (StringUtils.isNotBlank(trafficStr)) {
						try {
							traffic = (new Gson()).fromJson(trafficStr, HashMap.class);
						} catch (Exception e) {
							logger.error("解析酒店 {} 交通数据 {} 出错. {}", hotelid, trafficStr, e.getMessage());
						}
					}
					// hotel Peripheral
					Map<String, String> peripheral = new HashMap<String, String>();
					String peripheralStr = bean.getPeripheral();
					if (StringUtils.isNotBlank(peripheralStr)) {
						try {
							peripheral = (new Gson()).fromJson(peripheralStr, HashMap.class);
						} catch (Exception e) {
							logger.error("解析酒店 {} Peripheral数据 {} 出错. {}", hotelid, peripheralStr, e.getMessage());
						}
					}

					// hotel facilities
					List<Map<String, Object>> facies = new ArrayList<Map<String, Object>>();
					bfSql.setLength(0);
					bfSql.append("select a.facid,b.facname,b.factype from t_hotel_facility a "
							+ " left outer join t_facility b on a.facId=b.id "
							+ " where a.hotelid=? and b.visible='T' order by b.facsort");
					List<Bean> facilities = Db.find(bfSql.toString(), hotelid);
					for (Bean item : facilities) {
						facies.add(item.getColumns());
					}

					OtsHotel hotel = new OtsHotel();
					hotel.setHotelid(bean.getId().toString());
					hotel.setHotelname(bean.getHotelname() == null ? "" : bean.getHotelname());
					hotel.setIntroduction(bean.getIntroduction() == null ? "" : bean.getIntroduction());
					hotel.setMaxprice(BigDecimal.ZERO);
					hotel.setMinprice(BigDecimal.ZERO);
					hotel.setDetailaddr(bean.getDetailaddr() == null ? "" : bean.getDetailaddr());
					hotel.setHotelcity(bean.getCitycode() == null ? "" : bean.getCitycode());
					hotel.setHoteldis(bean.getDisid() == null ? "" : String.valueOf(bean.getDisid()));
					hotel.setBusinesszone(businessZones);
					hotel.setTraffic(traffic); // 新增交通
					hotel.setPeripheral(peripheral);// 新增周边
					hotel.setHotelpic(pics);
					hotel.setHotelfacility(facies);
					hotel.setGrade(BigDecimal.valueOf(5l));
					hotel.setPin(new GeoPoint(bean.getLatitude().doubleValue(), bean.getLongitude().doubleValue()));
					hotel.setIspms(1);
					hotel.setHoteldisc(bean.getIntroduction() == null ? "" : bean.getIntroduction());
					hotel.setFlag(1);
					hotel.setCreatetime(time);
					hotel.setModifytime(time);

					Date hotelRepairTime = bean.getRepairtime();

					String repairInfo = getRepairInfo(hotelRepairTime);
					if (StringUtils.isNotBlank(repairInfo)){
						hotel.setRepairinfo(repairInfo);
					}
					if (StringUtils.isNotBlank(hotelid)){

						List<Map<String, Object>> highLighs =  getHighlights(Long.valueOf(hotelid));

					/*
						if (StringUtils.isNotBlank(repairInfo)){
							Map<String, Object> repairTip = new HashMap<>();
							repairTip.put("id",-1);
							repairTip.put("name", repairInfo);
							highLighs.add(0,repairTip);
						}

						*/
						if (highLighs == null){
							highLighs = new ArrayList<Map<String, Object>>();
						}
						hotel.setHighlights(highLighs);
					}

					// 新增 最晚保留时间
					hotel.setRetentiontime(bean.getRetentiontime() == null ? "" : bean.getRetentiontime());
					// 新增 默认离店时间
					hotel.setDefaultlevaltime(bean.getDefaultleavetime() == null ? "" : bean.getDefaultleavetime());

					hotel.setVisible(bean.getVisible() == null ? Constant.STR_TRUE : bean.getVisible());
					hotel.setOnline(bean.getOnline() == null ? Constant.STR_TRUE : bean.getOnline());
					hotel.setNumroomtype1(1l);
					hotel.setNumroomtype2(1l);
					hotel.setNumroomtype3(1l);
					hotel.setRoomnum(bean.getRoomnum() == null ? 1l : bean.getRoomnum());
					hotel.setIsnewpms(Constant.STR_FALSE);
					// 酒店权重
					hotel.setPriority(bean.getPriority() == null ? 3 : bean.getPriority());

					hotel.setHotelrulecode(bean.getRulecode()); // 20150731 add

					// 添加酒店总图片数 by LYN at 2015-08-22 21:47
					int hotelpicnum = this.readonlyGetPicCount(Long.valueOf(hotelid));
					hotel.setHotelpicnum(hotelpicnum);

					// mike3.0 添加 酒店类型
					hotel.setHoteltype(bean.getHoteltype());

					// mike3.0 添加月销量
					hotel.setOrdernummon(getOrderNumMon(Long.valueOf(hotelid)));

					// mike3.2 添加受欢迎指数
					hotel.setGreetscore(getGreetScore(Long.valueOf(hotelid)));

					/**
					 * add bedtype
					 */
					List<Map<String, Object>> bedtypes = new ArrayList<Map<String, Object>>();
					try {
						List<Map<String, Object>> bedtypeList = readonlyRoomtypeList(
								bean.getId().toString(), "");
						for (Map<String, Object> bedtype : bedtypeList) {
							Map<String, Object> bed = new HashMap<String, Object>();
							bed.put("bedtype", bedtype.get("bedtype"));
							bed.put("bedtypename", bedtype.get("bedtypename"));
							bedtypes.add(bed);
						}
					} catch (Exception ex) {
						logger.warn(String.format("failed to add bedtype for hotelid:%s...", hotelid), ex);
					}

					if (bedtypes != null && bedtypes.size() > 0) {
						if (logger.isInfoEnabled()) {
							logger.info("bedtypes added for hotelid {}", hotelid);
						}
						hotel.setBedtypes(bedtypes);
					}
					// mike3.1 添加特价房

					TRoomSaleConfig tRoomSaleConfig = new TRoomSaleConfig();
					Integer hotelId = Integer.valueOf(bean.getId().toString());
					tRoomSaleConfig.setHotelId(hotelId);
					Boolean isPromo = roomSaleService.checkRoomSale(tRoomSaleConfig);
					if (isPromo != null && isPromo) {
						hotel.setIsonpromo("1");
					} else {
						hotel.setIsonpromo("0");
					}

					List<Map<String, Object>> promoinfo;

					promoinfo = roomSaleService.queryRoomPromoInfoByHotel(hotelid);
					if (promoinfo == null) {
						promoinfo = new ArrayList<>();
					}

					hotel.setPromoinfo(promoinfo);

					List<Integer> promoIds = new ArrayList<Integer>();
					hotel.setPromoids(promoIds);

					// 先把新的酒店放到集合中，后面做批量添加
					coll.add(hotel);
					logger.info("hotelid: {} added in collections and will be add in elasticsearch document.", hotelid);
				}

				if (coll.size() > 0) {
					esProxy.batchAddDocument(coll);

					if (logger.isInfoEnabled()) {
						logger.info("total pms hotel added: {}", coll.size());
					}

					output.setSuccess(true);
					output.setMsgAttr("count", coll.size());

					if (logger.isInfoEnabled()) {
						logger.info("fire the task which updates bedtypes...");
					}

					// asyncBatchUpdateHotelBedtypes(cityid);

				} else {
					output.setSuccess(true);
				}
			} catch (Exception e) {
				output.setSuccess(false);
				output.setErrcode("-1");
				output.setErrmsg(e.getMessage());
				logger.error("post es error: {} " + e.getMessage());
			}
		} catch (Exception e) {
			output.setSuccess(false);
			output.setErrcode("-1");
			output.setErrmsg(e.getMessage());
			logger.error("post es error: {} " + e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return output;
	}

	public static String getRepairInfo(Date hotelRepairTime ){
		Date now = new Date();
		int diffYears = DateUtils.diffYears(hotelRepairTime,now);

		if (diffYears <= Constant.SHOW_HOTEL_REPAIRINFO_YEARS_LIMIT){
			String repairInfo = DateUtils.getDateYear(hotelRepairTime) + "年装修";
			return  repairInfo;
		}else {
			return null;
		}
	}

	public  List<Map<String, Object>> getHighlights(Long hotelid){
		List<TFacilityModel> facilitys = tFacilityMapper.findByHotelid(hotelid);
		String[] showIds = Constant.HOTEL_HIGHLIGHT_SHOWS_IDS.split(",");
		List<Map<String, Object>> highLights = new ArrayList();

		for (TFacilityModel fac : facilitys) {

			for (String showId: showIds) {
				if (fac.getId() == Long.valueOf(showId)){
					Map<String, Object> highLight = new HashMap<>();
					highLight.put("name",fac.getFacname());
					highLight.put("id",fac.getId());
					if (StringUtils.isNotBlank(fac.getIconurl())){
						highLight.put("icon", fac.getIconurl());
					}
					highLights.add(highLight);
				}

			}
		}

		return highLights;
	}
	/**
	 * query promo data
	 * 
	 * @param hotelId
	 * @return
	 */
	public TRoomSale queryPromoData(Integer hotelId) {
		if (hotelId == null || hotelId == 0) {
			return null;
		}

		TRoomSale roomSale = new TRoomSale();
		roomSale.setHotelId(hotelId);

		TRoomSale result = roomSaleService.getOneRoomSale(roomSale);
		return result;
	}

	/**
	 * 初始化非签约酒店数据到ES
	 *
	 * @param cityid
	 *            参数：城市编码
	 * @return
	 */
	public ServiceOutput readonlyInitNotPmsHotel(String cityid, String thotelid) {
		Date day = new Date();
		// 有则忽略，无则添加
		ServiceOutput output = new ServiceOutput();
		try {
			String sql = "select * from t_hotel_nopms where 1=1 ";
			if (!StringUtils.isBlank(thotelid)) {
				sql = sql + " and hotelid = '" + thotelid + "'";
			}
			List<Bean> hotels = Db.find(sql);
			Collection<Object> addList = new ArrayList<Object>();
			for (Bean bean : hotels) {
				String hotelid = String.valueOf(bean.get("hotelid"));
				// 如果ES中已经有该酒店，则先删除再重新添加。
				SearchHit[] searchHits = esProxy.searchHotelByHotelId(hotelid);
				for (int i = 0; i < searchHits.length; i++) {
					logger.error("not pms hotelid: {} has exists.", hotelid);
					esProxy.deleteDocument(searchHits[i].getId());
					logger.error("not pms hotelid: {} has deleted.", hotelid);
				}

				OtsHotel hotel = new OtsHotel();
				hotel.setHotelid(hotelid);
				hotel.setBusinesszone(null);
				// 创建时间
				hotel.setCreatetime(day.getTime());
				hotel.setDetailaddr(bean.getStr("addr"));
				hotel.setFlag(1);
				hotel.setGrade(BigDecimal.ZERO);

				// 酒店所属城市
				if (bean.get("hotelcity") == null) {
					hotel.setHotelcity(Constant.STR_CITYID_SHANGHAI);
				} else {
					hotel.setHotelcity(String.valueOf(bean.get("hotelcity")));
				}

				hotel.setHoteldis(null);
				hotel.setHoteldisc(bean.getStr("descr"));

				// hotel facility
				String facility = bean.getStr("facility");
				if (!StringUtils.isBlank(facility)) {
					String[] arrFaci = facility.split(",");
					List<Map<String, Object>> faciList = new ArrayList<Map<String, Object>>();
					for (int i = 0; i < arrFaci.length; i++) {
						Map<String, Object> faci = new HashMap<String, Object>();
						// fixed bug - OTS-147 : 酒店综合信息查询非签约酒店设施id与设施对应关系不正确.
						// 由于非签约酒店导入数据时，没有提供酒店设施编码和名称的对应关系，因此facid确定不了正确的值，暂定为：999
						faci.put("facid", 999);
						faci.put("facname", arrFaci[i]);
						faciList.add(faci);
					}
					hotel.setHotelfacility(faciList);
				}
				hotel.setHotelname(bean.getStr("hotelname"));

				// hotel pics
				Map<String, Object> pic = new HashMap<String, Object>();
				pic.put("url", bean.get("pics"));
				List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
				pics.add(pic);

				Map<String, Object> hotelpic = new HashMap<String, Object>();
				hotelpic.put("name", "def");
				hotelpic.put("pic", pics);
				List<Map<String, Object>> hotelpics = new ArrayList<Map<String, Object>>();
				hotelpics.add(hotelpic);

				hotel.setHotelpic(hotelpics);

				// 设置为非签约酒店
				hotel.setIspms(2);

				// 酒店最高价和最低价
				hotel.setMaxprice(BigDecimal.ZERO);
				hotel.setMinprice(BigDecimal.ZERO);

				// 修改时间
				hotel.setModifytime(day.getTime());

				double lat = bean.getBigDecimal("latitude").doubleValue();
				double lon = bean.getBigDecimal("longitude").doubleValue();
				hotel.setPin(new GeoPoint(lat, lon));

				// 新增字段
				hotel.setVisible(Constant.STR_TRUE);
				hotel.setOnline(Constant.STR_TRUE);
				hotel.setNumroomtype1(1l);
				hotel.setNumroomtype2(1l);
				hotel.setNumroomtype3(1l);
				hotel.setRoomnum(1l);
				hotel.setIsnewpms(Constant.STR_FALSE);
				hotel.setPriority(4);
				/**
				 * added in mike3.1
				 */
				hotel.setIsonpromo("0");

				// 先把新的酒店放到集合中，后面做批量添加
				addList.add(hotel);
				logger.info("not pms hotelid: {} added in collections and will be add in elasticsearch document.",
						hotelid);
			}
			// 批量添加
			if (addList.size() > 0) {
				esProxy.batchAddDocument(addList);
				output.setSuccess(true);
				output.setMsgAttr("count", addList.size());
				logger.info("total not pms hotel added: {}条.", addList.size());
			} else {
				output.setSuccess(true);
			}
		} catch (Exception e) {
			output.setSuccess(false);
			output.setErrcode("-1");
			output.setErrmsg(e.getMessage());
			logger.error("initNotPmsHotel method error: {} ", e.getMessage());
		}
		return output;
	}

	/**
	 * @param hotelID
	 * @return
	 */
	public Bean readonlyHotelScoreInfo(String hotelID) {
		return scoreDAO.findScoreSByHotelid(hotelID);
	}

	/**
	 * @param hotelID
	 * @return
	 */
	public Bean readonlyHotelBaseInfo(String hotelID) {
		Bean info = null;
		StringBuffer bfSql = new StringBuffer();
		try {
			bfSql.append(
					"select a.id,a.hotelphone,b.cityid,c.cityname as hotelcity,c.proid,d.proname as hotelprovince,e.disname as hoteldis"
							+ " from t_hotel a " + " left outer join t_hotel_city b on a.id=b.hotelid"
							+ "   left outer join t_city c on b.cityid = c.code"
							+ "     left outer join t_province d on c.proid=d.proid "
							+ "       left outer join t_district e on e.id=a.disid" + "         where a.id=?");
			// // 上面的语句查询有问题，t_hotel_city表中如果没有配置酒店所属城市，得不到酒店所属城市，省份信息
			bfSql.setLength(0);
			bfSql.append("select b.id as hotelid,b.disid,b.hotelphone,a.disname as hoteldis,a.cityid ");
			bfSql.append("        ,c.cityname as hotelcity,d.proid,d.proname as hotelprovince ");
			bfSql.append(" from t_hotel b ");
			bfSql.append("    left outer join  t_district a on a.id = b.disid ");
			bfSql.append("      left outer join t_city c on c.cityid=a.cityid ");
			bfSql.append("        left outer join t_province d on d.proid = c.proid ");
			bfSql.append("          where b.id = ? ");
			////

			info = Db.findFirst(bfSql.toString(), hotelID);
		} catch (Exception e) {
			logger.error("getHotelBaseInfo is error:\n" + e.getMessage());
			logger.error("sql is: \n" + bfSql.toString());
		}
		return info;
	}

	/**
	 * @return
	 */
	public Map<String, Object> readonlyFromEsStore(THotel hotel) throws Exception {
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		if (StringUtils.isBlank(hotel.getHotelid())) {

			// // 必填参数默认值处理：开始
			if (StringUtils.isBlank(hotel.getCityid())) {
				hotel.setCityid(Constant.STR_CITYID_SHANGHAI);
			}
			// 用户坐标经纬度值没有,先判断屏幕坐标经纬度值，有的话用屏幕坐标经纬度，没有默认上海市中心位置
			if (StringUtils.isBlank(hotel.getUserlongitude())) {
				if (StringUtils.isBlank(hotel.getPillowlongitude())) {
					hotel.setUserlongitude(String.valueOf(Constant.LON_SHANGHAI));
				} else {
					hotel.setUserlongitude(hotel.getPillowlongitude());
				}
			}
			if (StringUtils.isBlank(hotel.getUserlatitude())) {
				if (StringUtils.isBlank(hotel.getPillowlatitude())) {
					hotel.setUserlatitude(String.valueOf(Constant.LAT_SHANGHAI));
				} else {
					hotel.setUserlatitude(hotel.getPillowlatitude());
				}
			}
			if (StringUtils.isBlank(hotel.getPage())) {
				hotel.setPage(String.valueOf(HOTEL_PAGE_DEFAULT));
			}
			if (StringUtils.isBlank(hotel.getLimit())) {
				hotel.setLimit(String.valueOf(HOTEL_LIMIT_DEFAULT));
			}
			if (StringUtils.isBlank(hotel.getRange())) {
				hotel.setRange(String.valueOf(HOTEL_RANGE_DEFAULT));
			}
			// // 必填参数默认值处理：结束

			if (hotel.getCityid() == null || hotel.getCityid().isEmpty()) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：cityid.");
				return rtnMap;
			}

			if (hotel.getPage() == null || hotel.getPage().isEmpty()) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：page.");
				return rtnMap;
			}
			// page参数校验：如果page小于等于0，默认为1.
			int page = Integer.parseInt(hotel.getPage());
			if (page <= 0) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "参数page必须大于0.");
				return rtnMap;
			}

			if (hotel.getLimit() == null || hotel.getLimit().isEmpty()) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：limit.");
				return rtnMap;
			}
			// limit参数校验：如果limit小于等于0，默认为10.
			int limit = Integer.parseInt(hotel.getLimit());
			if (limit <= 0) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "参数limit必须大于0.");
				return rtnMap;
			}

			if (StringUtils.isBlank(hotel.getUserlatitude())) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：userlatitude.");
				return rtnMap;
			}

			if (StringUtils.isBlank(hotel.getUserlongitude())) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：userlongitude.");
				return rtnMap;
			}
			if (StringUtils.isBlank(hotel.getRange())) {
				rtnMap.put("success", false);
				rtnMap.put("errcode", "-1");
				rtnMap.put("errmsg", "缺少参数：range.");
				return rtnMap;
			}
		}
		try {
			// search ots hotel data form es
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

			if (StringUtils.isBlank(hotel.getHotelid())) {
				logger.info("未指定参数hotelid，搜索酒店数据开始...");
				rtnMap = this.readonlyOtsHotelListFromEsStore(hotel);
				logger.info("未指定参数hotelid，搜索酒店数据结束.");
			} else {
				logger.info("指定hotelid，返回酒店数据开始...");
				rtnMap = this.readonlyOtsHotelFromEsStore(hotel);
				logger.info("指定hotelid，返回酒店数据开始...");
			}
			return rtnMap;
		} catch (Exception e) {
			logger.error("search hotel error: {}\n", e.getMessage());
			rtnMap.put("success", false);
			rtnMap.put("errcode", "-1");
			rtnMap.put("errmsg", e.getMessage());
		}
		return rtnMap;
	}

	/**
	 * @param hotel
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> readonlyOtsHotelFromEsStore(THotel hotel) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			List<FilterBuilder> keywordBuilders = new ArrayList<FilterBuilder>();

			ObjectMapper objectMapper = new ObjectMapper();
			logger.info("getOtsHotel method params: {}\n", objectMapper.writeValueAsString(hotel));

			String hotelid = hotel.getHotelid();
			// added by chuaiqing
			THotelModel thotelModel = thotelMapper.selectById(Long.valueOf(hotelid));
			if (thotelModel == null) {
				rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
				rtnMap.put("count", 0);
				rtnMap.put("hotel", new ArrayList<Map<String, Object>>());
				return rtnMap;
			}

			double cityLat_default = Constant.LAT_SHANGHAI;
			double cityLon_default = Constant.LON_SHANGHAI;

			TCityModel tcity = null;
			Integer hotelDisid = thotelModel.getDisid();
			if (hotelDisid != null) {
				TDistrictModel tdistrictModel = tdistrictMapper
						.selectByPrimaryKey(Long.valueOf(hotelDisid.longValue()));
				if (tdistrictModel != null) {
					Integer hotelCityid = tdistrictModel.getCityid();
					if (hotelCityid != null) {
						tcity = cityService.findCityById(Long.valueOf(hotelCityid.longValue()));
					}
				}
			}

			if (tcity != null) {
				BigDecimal cityLat = tcity.getLatitude();
				if (cityLat != null) {
					cityLat_default = cityLat.doubleValue();
				}

				BigDecimal cityLon = tcity.getLongitude();
				if (cityLon != null) {
					cityLon_default = cityLon.doubleValue();
				}

				Double cityRange = tcity.getRange();
				if (cityRange != null) {
					hotel.setRange(cityRange.toString());
				}
			}

			// 用户经纬度坐标
			double userlat = StringUtils.isBlank(hotel.getUserlatitude()) ? cityLat_default
					: Double.valueOf(hotel.getUserlatitude());
			double userlon = StringUtils.isBlank(hotel.getUserlongitude()) ? cityLon_default
					: Double.valueOf(hotel.getUserlongitude());

			// 屏幕地图经纬度坐标
			double lat = StringUtils.isBlank(hotel.getPillowlatitude()) ? cityLat_default
					: Double.valueOf(hotel.getPillowlatitude());
			double lon = StringUtils.isBlank(hotel.getPillowlongitude()) ? cityLon_default
					: Double.valueOf(hotel.getPillowlongitude());

			List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			// 只显示上线酒店
			QueryStringQueryBuilder querystringQueryBuilder = QueryBuilders.queryStringQuery("T").field("visible");
			searchBuilder.setQuery(querystringQueryBuilder);
			// 显示在线酒店
			QueryStringQueryBuilder querystringQueryBuilder1 = QueryBuilders.queryStringQuery("T").field("online");
			searchBuilder.setQuery(querystringQueryBuilder1);
			// 如果输入参数中有hotelid，忽略其它过滤条件
			filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));

			// 是否签约 值为T，则只返回签约酒店，值为F或空，返回所有酒店
			// 从眯客2.1开始，只返回签约酒店
			filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));

			if (AppUtils.DEBUG_MODE) {
				logger.info("boolFilter is : \n{}", boolFilter.toString());
			}
			searchBuilder.setPostFilter(boolFilter);
			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.getHits();
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				// 计算两个经纬度坐标距离（单位：米）
				Map<String, Object> pin = (Map<String, Object>) result.get("pin");
				// hotel latitude and longitude
				double hotelLongitude = Double.valueOf(String.valueOf(pin.get("lon")));
				double hotelLatitude = Double.valueOf(String.valueOf(pin.get("lat")));
				result.put("latitude", hotelLatitude);
				result.put("longitude", hotelLongitude);
				// 眯客2.2.1, 根据屏幕坐标计算距离.
				double hotelDistance = DistanceUtil.distance(lon, lat, hotelLongitude, hotelLatitude);
				result.put("distance", hotelDistance);
				if (result.get("ordernummon") != null) {
					Long sales = Long.valueOf(String.valueOf(result.get("ordernummon")));
					result.put("ordernummon", (sales >= 10 ? "月销" + sales + "单" : ""));
				} else {
					result.put("ordernummon", "");
				}
				// 从ES中查到酒店数据后，根据接口参数做进一步处理
				try {
					queryTransferData(result, hotel);
				} catch (Exception e) {
					logger.error("酒店处理出错: {}", e.getMessage());
				}

				// 添加到酒店列表
				hotels.add(result);

				// 记录离线埋点
				if ("F".equals(result.get("online"))) {
					logger.info("记录离线埋点:{}", hotel.toString());
					if ("T".equals(result.get("isnewpms"))) {
						Cat.logEvent("ROOMSTATE", "pmsOffLine-2.0", Event.SUCCESS, hotelid);
					} else {
						Cat.logEvent("ROOMSTATE", "pmsOffLine-1.0", Event.SUCCESS, hotelid);
					}
				}
			}
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("count", hotels.size());
			rtnMap.put("hotel", hotels);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return rtnMap;
	}

	/**
	 * 酒店综合查询返回酒店列表数据
	 *
	 * @param hotel
	 *            参数: 酒店搜索入参Bean对象
	 * @return
	 */
	public Map<String, Object> readonlyOtsHotelListFromEsStore(THotel hotel) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			List<FilterBuilder> keywordBuilders = new ArrayList<FilterBuilder>();

			//
			List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
			// 如果城市id 为空则默认设置为上海
			String cityid = hotel.getCityid();

			String hotelid = hotel.getHotelid();

			// page参数校验：如果page小于等于0，默认为1.
			int page = Integer.parseInt(hotel.getPage());
			if (page <= 0) {
				page = HOTEL_PAGE_DEFAULT;
			}
			// limit参数校验：如果limit小于等于0，默认为10.
			int limit = Integer.parseInt(hotel.getLimit());
			if (limit <= 0) {
				limit = HOTEL_LIMIT_DEFAULT;
			}

			// added by chuaiqing: 城市搜索市中心坐标和搜索半径通过B端配置
			// 酒店搜索城市编码参数说明：最初接口文档中是cityid，后来接口文档改为citycode。但是各端还是沿用cityid参数名称。
			double cityLat_default = Constant.LAT_SHANGHAI;
			double cityLon_default = Constant.LON_SHANGHAI;

			logger.info("find city geopoint begin...");
			TCityModel tcity = null;
			String citycode = cityid;
			if (citycode != null) {
				tcity = cityService.findCityByCode(citycode);
			}
			if (tcity != null) {
				BigDecimal cityLat = tcity.getLatitude();
				if (cityLat != null) {
					cityLat_default = cityLat.doubleValue();
					logger.info("city {} lat is {}.", cityid, cityLat_default);
				}

				BigDecimal cityLon = tcity.getLongitude();
				if (cityLon != null) {
					cityLon_default = cityLon.doubleValue();
					logger.info("city {} lon is {}.", cityid, cityLon_default);
				}

				Double cityRange = tcity.getRange();
				if (cityRange != null) {
					hotel.setRange(cityRange.toString());
					logger.info("set city {} search range is {}", cityid, cityRange);
				}
			}

			// 用户经纬度，根据它来计算酒店“距离我”的距离
			double userlat = StringUtils.isBlank(hotel.getUserlatitude()) ? cityLat_default
					: Double.valueOf(hotel.getUserlatitude());
			double userlon = StringUtils.isBlank(hotel.getUserlongitude()) ? cityLon_default
					: Double.valueOf(hotel.getUserlongitude());

			// 屏幕地图经纬度，根据它按照范围来搜索酒店
			double lat = StringUtils.isBlank(hotel.getPillowlatitude()) ? cityLat_default
					: Double.valueOf(hotel.getPillowlatitude());
			double lon = StringUtils.isBlank(hotel.getPillowlongitude()) ? cityLon_default
					: Double.valueOf(hotel.getPillowlongitude());

			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			// 只显示上线酒店和在线酒店
			searchBuilder.setQuery(QueryBuilders.matchQuery("visible", Constant.STR_TRUE));
			searchBuilder.setQuery(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
			if (StringUtils.isBlank(hotelid)) {
				// 设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
				// 2.SearchType.SCAN = 扫描查询,无序
				searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

				// make term filter builder
				this.makeTermFilter(hotel, filterBuilders);

				// 酒店搜索范围
				// added by chuaiqing at 2015-09-08 19:39:35
				// 有关键字或者有酒店名称或者有酒店地址搜索，按照默认3000公里半径搜索
				if (StringUtils.isNotBlank(hotel.getKeyword()) || StringUtils.isNotBlank(hotel.getHotelname())
						|| StringUtils.isNotBlank(hotel.getHoteladdr())) {
					hotel.setRange(String.valueOf(HOTEL_RANGE_DEFAULT));
					logger.info("keyword or hotelname or hoteladdress search, set search range {}",
							HOTEL_RANGE_DEFAULT);
				}
				double distance = Double.valueOf(hotel.getRange());
				GeoDistanceFilterBuilder geoFilter = FilterBuilders.geoDistanceFilter("pin");
				// 按照屏幕地图经纬度来搜索酒店范围默认单位：米
				geoFilter.point(lat, lon).distance(distance, DistanceUnit.METERS).optimizeBbox("memory")
						.geoDistance(GeoDistance.ARC);
				filterBuilders.add(geoFilter);

				// hotelname,hoteladdr模糊查询
				this.makeQueryFilter(hotel, filterBuilders);

				// keyword查询
				// 如果没有指定酒店名称和酒店地址，按照keyword来搜索
				if (StringUtils.isNotBlank(hotel.getKeyword())) {
					makeKeywordFilter(hotel, keywordBuilders);
					Cat.logEvent("HotKeywords", hotel.getKeyword(), Message.SUCCESS, "");
				}

				FilterBuilder[] builders = new FilterBuilder[] {};
				BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
				// modified by chuaiqing at 2015-08-18 14:37:09
				if (keywordBuilders.size() > 0) {
					FilterBuilder[] arrKeywordBuilders = new FilterBuilder[] {};
					boolFilter.should(keywordBuilders.toArray(arrKeywordBuilders));
				}
				// make range filter builder
				List<FilterBuilder> mikePriceBuilders = this.makeMikePriceRangeFilter(hotel);

				if (mikePriceBuilders.size() > 0) {
					BoolFilterBuilder mikePriceBoolFilter = FilterBuilders.boolFilter();
					mikePriceBoolFilter.should(mikePriceBuilders.toArray(builders));
					boolFilter.must(mikePriceBoolFilter);
				}
				if (AppUtils.DEBUG_MODE) {
					logger.info("boolFilter is : \n{}", boolFilter.toString());
				}

				searchBuilder.setPostFilter(boolFilter);

				/**
				 * @version 2015-07-15 眯客2.1排序规则1: 是否在线, 酒店权重, 距离(距离我),
				 *          是否推荐(签约和非签约酒店), 最低价格, 酒店评分
				 * @version 2015-07-24 排序规则2(废弃排序规则1)
				 *          排序规则排序规则：是否在线，权重，是否签约酒店，距离（由近及远）
				 */
				searchBuilder.addSort("online", SortOrder.DESC).addSort("priority", SortOrder.ASC)
						.addSort("ispms", SortOrder.ASC)
						.addSort(SortBuilders.geoDistanceSort("pin").point(lat, lon).order(SortOrder.ASC)); // 根据屏幕经纬度
																											// yub
																											// edit
																											// 20150724
			} else {
				// 如果输入参数中有hotelid，忽略其它过滤条件
				filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));
			}
			// 分页应用
			searchBuilder.setFrom((page - 1) * limit).setSize(limit).setExplain(true);

			logger.info(searchBuilder.toString());
			SearchResponse searchResponse = searchBuilder.execute().actionGet();

			SearchHits searchHits = searchResponse.getHits();
			long totalHits = searchResponse.getHits().totalHits();

			if (StringUtils.isNotBlank(hotel.getKeyword()) && (totalHits == 0D)) {
				Cat.logEvent("MismatchKeywords", hotel.getKeyword(), Message.SUCCESS, "");
			}

			SearchHit[] hits = searchHits.getHits();
			logger.info("search hotel success: total {} found. current pagesize:{}", totalHits, hits.length);
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				// 根据用户经纬度来计算两个经纬度坐标距离（单位：米）
				Map<String, Object> pin = (Map<String, Object>) result.get("pin");
				// hotel latitude and longitude
				double hotelLongitude = Double.valueOf(String.valueOf(pin.get("lon")));
				double hotelLatitude = Double.valueOf(String.valueOf(pin.get("lat")));
				double hotelDistance = DistanceUtil.distance(lon, lat, hotelLongitude, hotelLatitude); // 根据屏幕经纬度
																										// yub
																										// 20150724
				result.put("distance", hotelDistance);

				// 接口新增属性isnear: 是否最近酒店, distance值最小的酒店为T,其他为F.
				if (page <= 1 && i == 0) {
					result.put("isnear", Constant.STR_TRUE);
				} else {
					result.put("isnear", Constant.STR_FALSE);
				}

				// TODO: 酒店首屏列表页面数据，只返回基本信息。
				// 从ES中查到酒店数据后，根据接口参数做进一步处理
				int num = i + 1;
				logger.info("--================================== " + num
						+ ". 处理ES酒店数据开始： ==================================--");
				try {
					// hotel ispms: 是否签约酒店
					if ("1".equals(String.valueOf(result.get("ispms")))) {
						result.put("ispms", Constant.STR_TRUE);
					} else {
						result.put("ispms", Constant.STR_FALSE);
					}
					// hotel latitude and longitude
					result.put("latitude", pin.get("lat"));
					result.put("longitude", pin.get("lon"));

					// TODO: 是否推荐，无数据来源，暂定为F
					result.put("isrecommend", Constant.STR_FALSE);

					// 不返回酒店图片信息，从结果集中删除
					// 是否返回酒店图片: 非必填(T/F)，空等同于F，值为T，则返回图片信息，空或F则不返回图片信息
					boolean isHotelPic = Constant.STR_TRUE.equals(hotel.getIshotelpic());
					if (!isHotelPic) {
						result.remove("hotelpic");
					} else {
						// 需要返回酒店图片信息
						/*
						 * List<Map<String, Object>> hotelpics = new
						 * ArrayList<Map<String, Object>>(); if
						 * (result.get("hotelpic") != null) { hotelpics =
						 * (List<Map<String, Object>>) result.get("hotelpic"); }
						 * 
						 * for (Map<String, Object> hotelpic : hotelpics) {
						 * String picName =
						 * String.valueOf(hotelpic.get("name"));
						 * hotelpic.put("name",
						 * HotelPictureEnum.getByName(picName).getTitle()); }
						 * result.put("hotelpic", hotelpics);
						 */

						/*
						 * 排序 List<Map<String, String>> thebeans
						 * =(List<Map<String, String>>) result.get("hotelpic");
						 * 
						 * Collections.sort(thebeans,new Comparator<Map<String,
						 * String>>(){
						 * 
						 * @Override public int compare(Map<String, String> b1,
						 * Map<String, String> b2) { return
						 * b2.get("name").compareTo(b1.get("name")); } });
						 */

						// 酒店图片
						if (result.get("hotelpic") != null) {
							List<Map<String, Object>> hotelpics = new ArrayList<Map<String, Object>>();
							hotelpics = (List<Map<String, Object>>) result.get("hotelpic");
							List<Map<String, Object>> hotelpiclist = new ArrayList<Map<String, Object>>();
							boolean ismatch = false;
							for (Map<String, Object> hotelpic : hotelpics) {
								String picName = String.valueOf(hotelpic.get("name"));
								List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
								// 遍历到主力房源
								if (HotelPictureEnum.PIC_MAINHOUSING.getName().equals(picName) && picurl != null
										&& picurl.size() > 0) {
									hotelpic.put("name", HotelPictureEnum.getByName(picName).getTitle());
									hotelpiclist.add(hotelpic);
									result.put("hotelpic", hotelpiclist);
									ismatch = true;
									break;
								}
							}
							// 未匹配到取门头及招牌
							if (!ismatch) {
								for (Map<String, Object> hotelpic : hotelpics) {
									String picName = String.valueOf(hotelpic.get("name"));
									List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
									// 门头及招牌
									if (HotelPictureEnum.PIC_DEF.getName().equals(picName) && picurl != null
											&& picurl.size() > 0) {
										hotelpic.put("name", HotelPictureEnum.getByName(picName).getTitle());
										hotelpiclist.add(hotelpic);
										result.put("hotelpic", hotelpiclist);
										break;
									}
								}
							}
						}
					}

					// TODO: hotel score
					logger.info(
							"--================================== 查询酒店评价信息开始： ==================================-- ");
					Bean scoreBean = this.readonlyHotelScoreInfo(String.valueOf(result.get("hotelid")));
					if (scoreBean != null) {
						result.put("scorecount", scoreBean.get("scorecount") == null ? 0 : scoreBean.get("scorecount"));
						result.put("grade", scoreBean.get("grade") == null ? 0 : scoreBean.get("grade"));
					} else {
						result.put("scorecount", 0);
						result.put("grade", 0);
					}
					logger.info(
							"--================================== 查询酒店评价信息结束： ==================================-- ");

					// TODO: hotel base info
					Bean hotelInfo = this.readonlyHotelBaseInfo(String.valueOf(result.get("hotelid")));
					if (hotelInfo != null) {
						result.put("hoteldis", hotelInfo.get("hoteldis") == null ? "" : hotelInfo.get("hoteldis"));
						result.put("hotelcity", hotelInfo.get("hotelcity") == null ? "" : hotelInfo.get("hotelcity"));
						result.put("hotelprovince",
								hotelInfo.get("hotelprovince") == null ? "" : hotelInfo.get("hotelprovince"));
						result.put("hotelphone",
								hotelInfo.get("hotelphone") == null ? "" : hotelInfo.get("hotelphone"));
					}

					// //queryTransferData(result, hotel);
					logger.info("Hotelid: {} queryTransferData success. ", result.get("hotelid"));
				} catch (Exception e) {
					logger.error("Hotelid: {} queryTransferData error: {} ", result.get("hotelid"), e.getMessage());
				}
				logger.info("--================================== " + num
						+ ". 处理ES酒店数据结束： ==================================--");

				// 添加到酒店列表
				result.remove("pin");
				result.remove("flag");
				// TODO: 酒店最低眯客价对应的房型的门市价,暂时取maxprice.
				String[] prices = null;
				if (hotelPriceService.isUseNewPrice())
					prices = hotelPriceService.getHotelMikePrices(Long.valueOf(String.valueOf(result.get("hotelid"))),
							hotel.getStartdateday(), hotel.getEnddateday());
				else
					prices = roomstateService.getHotelMikePrices(Long.valueOf(String.valueOf(result.get("hotelid"))),
							hotel.getStartdateday(), hotel.getEnddateday());
				BigDecimal minPrice = new BigDecimal(prices[0]);
				result.put("minprice", minPrice);
				result.put("minpmsprice", new BigDecimal(prices[1]));

				logger.info("--================================== 查询酒店图片总数开始： ==================================-- ");
				if (result.get("hotelpicnum") == null)
					result.put("hotelpicnum", 0);

				logger.info("--================================== 查询可订房间数开始： ==================================-- ");
				Long p_hotelid = Long.valueOf(String.valueOf(result.get("hotelid")));
				String p_isnewpms = Constant.STR_TRUE.equals(result.get("isnewpms")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;
				String p_visible = Constant.STR_TRUE.equals(result.get("visible")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;
				String p_online = Constant.STR_TRUE.equals(result.get("online")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;
				Integer avlblroomnum = getAvlblRoomNum(p_hotelid, p_isnewpms, p_visible, p_online,
						hotel.getStartdateday(), hotel.getEnddateday());
				logger.info("readonlyOtsHotelListFromEsStore 可订房间数: {}", avlblroomnum);
				result.put("avlblroomnum", avlblroomnum);
				if (avlblroomnum <= 0)
					result.put("isfull", Constant.STR_TRUE);
				else
					result.put("isfull", Constant.STR_FALSE);

				Map<String, String> fullstate = getFullState(avlblroomnum);
				result.putAll(fullstate);

				logger.info("--================================== 查询可订房间数结束： ==================================-- ");

				logger.info("--================================== 月销量查询开始: ==================================-- ");

				// added by chuaiqing at 2015-09-10 13:33:07
				// 搜索上海的酒店时，不显示近30天订单销量
				// 眯客2.5业务逻辑：如果近30天订单销量小于10，则C端不显示“月销xxx单”
				// 所以如果搜索城市为上海的时候，接口返回月销售量数据为0
				if (Constant.STR_CITYID_SHANGHAI.equals(cityid)) {
					result.put("ordernummon", "");
				} else {
					if (result.get("ordernummon") != null) {
						Long sales = Long.valueOf(String.valueOf(result.get("ordernummon")));
						result.put("ordernummon", (sales >= 10 ? "月销" + sales + "单" : ""));
					} else {
						result.put("ordernummon", "");
					}
				}

				logger.info("--================================== 月销量查询结束: ==================================-- ");

				logger.info("--================================== 最近预订时间查询开始: ==================================-- ");

				String createTime = orderDAO.findLatestTime(Long.valueOf(String.valueOf(result.get("hotelid"))));
				String rcntordertime = getRcntOrderTimeDes(createTime);
				result.put("rcntordertimedes", rcntordertime);

				logger.info("--================================== 最近预订时间查询结束: ==================================-- ");
				hotels.add(result);
			}

			Collections.sort(hotels, new Comparator<Map<String, Object>>() {
				@Override
				public int compare(Map<String, Object> hotel1, Map<String, Object> hotel2) {
					return hotel1.get("isfull").toString().compareTo(hotel2.get("isfull").toString());
				}
			});
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("count", totalHits);
			rtnMap.put("hotel", hotels);
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return rtnMap;
	}

	/**
	 * 最近预订时间查询
	 *
	 * @param createTime
	 *            yyyyMMddHHmmss
	 * @return
	 */
	private String getRcntOrderTimeDes(String createTime) {
		String latestTime = "";
		try {
			// 最近预订时间 如 间隔<24h 显示小时, 如 间隔>=24h 显示天数
			if (createTime == null) {
				latestTime = "";
			} else {
				SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
				long diff = new Date().getTime() - sdf14.parse(createTime).getTime();
				long nh = 1000 * 60 * 60; // 一小时的毫秒数
				long hour = diff / nh; // 计算差多少小时
				if (hour > 0 && hour < 24)
					latestTime = "最近预订" + hour + "小时前";
				else if (hour <= 0)
					latestTime = "最近预订1小时内";
				else
					latestTime = "最近预订1天前";
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return latestTime;
	}

	/**
	 * 历史酒店最近入住时间查询
	 *
	 * @param createTime
	 *            yyyyMMddHHmmss
	 * @return
	 */
	private String getRcntHotelLiveTimeDes(String createTime) {
		String latestTime = "";
		try {
			// 最近预订时间 如 间隔<24h 显示小时, 如 间隔>=24h 显示天数
			if (createTime == null) {
				latestTime = "";
			} else {
				SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");
				long diff = new Date().getTime() - sdf14.parse(createTime).getTime();
				long nh = 1000 * 60 * 60 * 24; // 一天的毫秒数
				long day = diff / nh; // 计算差多少天
				if (day >= 0 && day < 1) {
					latestTime = "1天内预订";
				} else if (day >= 1 && day <= 30) {
					latestTime = day + "天前预订";
				} else if (day > 30) {
					long month = day / 30;
					latestTime = month + "个月前预订";
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return latestTime;
	}

	/**
	 * 房满状态查询
	 *
	 * @param freeRoomCount
	 * @return
	 */
	public Map<String, String> getFullState(Integer freeRoomCount) {
		// avlblroomdes:可订房描述 若 可订总房间数<=3 显示 “仅剩x间”, 若 可订总房间数> 3 显示 “xx 间可订”
		// 数据可以按房态实时更新； 若房间满房，显示“ 已满房”
		// descolor:描述字体颜色 状态: “>3间房间” 绿色 32ab18 状态："<=仅剩3间" 红色 fb4b40 状态：满房 灰色
		// 989898
		Map<String, String> fullState = new HashMap<String, String>();
		if (freeRoomCount > 3) {
			// 大于3间不显示
			//// fullState.put("avlblroomdes", freeRoomCount + "间可订");
			fullState.put("avlblroomdes", "");
			fullState.put("descolor", "32ab18");
		} else if (freeRoomCount <= 3 && freeRoomCount > 0) {
			fullState.put("avlblroomdes", "仅剩" + freeRoomCount + "间");
			fullState.put("descolor", "fb4b40");
		} else {
			fullState.put("avlblroomdes", "满房");
			fullState.put("descolor", "989898");
		}
		return fullState;
	}

	/**
	 * 特价房满状态查询
	 *
	 * @param freeRoomCount
	 * @return
	 */
	public Map<String, String> getPromoFullState(Integer freeRoomCount) {
		// avlblroomdes:可订房描述 若 可订总房间数<=3 显示 “仅剩x间”, 若 可订总房间数> 3 显示 “xx 间可订”
		// 数据可以按房态实时更新； 若房间满房，显示“ 已满房”
		// descolor:描述字体颜色 状态: “>3间房间” 绿色 32ab18 状态："<=仅剩3间" 红色 fb4b40 状态：满房 灰色
		// 989898
		Map<String, String> fullState = new HashMap<String, String>();
		if (freeRoomCount > 3) {
			// 大于3间不显示
			//// fullState.put("avlblroomdes", freeRoomCount + "间可订");
			fullState.put("avlblroomdes", "");
			fullState.put("descolor", "32ab18");
		} else if (freeRoomCount <= 3 && freeRoomCount > 0) {
			fullState.put("avlblroomdes", "仅剩" + freeRoomCount + "间");
			fullState.put("descolor", "fb4b40");
		} else {
			fullState.put("avlblroomdes", "");
			fullState.put("descolor", "32ab18");
		}
		return fullState;
	}

	/**
	 * 月销量查询
	 *
	 * @param hotelId
	 * @return
	 */
	public Long getOrderNumMon(long hotelId) {
		// 月销量记录 显示近30天内的销量数据，数据每日更新。若 销量<10 不显示该数据信息若 销量>= 10 显示 “月销xxx单 ”
		Long sales = orderService.findMonthlySales(hotelId);
		logger.info("getOrderNumMon hotelId: {}, get月销量: {}", hotelId, sales);
		return sales;
	}

	/**
	 * PMS 月销量查询
	 *
	 * @param hotelId
	 * @return
	 */
	public Long getGreetScore(long hotelId) {
		//最受欢迎指数  pms 月销* 1000 / hotelromnums”
		Long pmsSales = orderService.findPMSMonthlySales(hotelId);
		//Long otaSales = orderService.findMonthlySales(hotelId);
		logger.info("getPMSOrderNumMon hotelId: {}, getPMS月销量：{}", hotelId,pmsSales);
		return pmsSales;
	}

	/**
	 * @param roomid
	 * @param lockDate
	 * @return
	 */
	private String getRoomLockedKey(Long roomid, String lockDate) {
		StringBuffer bfKey = new StringBuffer();
		bfKey.setLength(0);
		bfKey.append(roomid).append(Constant.CACHENAME_SEPARATOR).append(lockDate);
		return bfKey.toString();
	}

	/**
	 * @param room
	 * @param hotelid
	 * @param begindate
	 * @param enddate
	 */
	private void processRoomState(RoomstateQuerylistRespEntity.Room room, Long hotelid, String begindate,
			String enddate, Map<String, String> lockRoomsCache) throws Exception {
		Long roomid = room.getRoomid();
		String bdate = DateUtils.formatDate(DateUtils.getDateFromString(begindate));
		String edate = DateUtils.formatDate(DateUtils.getDateFromString(enddate));
		int lockDays = DateUtils.selectDateDiff(edate, bdate);
		if (lockRoomsCache == null) {
			room.setRoomstatus(this.ROOM_STATUS_VC);
			return;
		}
		// 先设置为vc
		room.setRoomstatus(this.ROOM_STATUS_VC);
		for (int i = 0; i < lockDays; i++) {
			String lockDate = DateUtils.formatDate(DateUtils.addDays(DateUtils.getDateFromString(begindate), i));
			String lockFlag = lockRoomsCache.get(this.getRoomLockedKey(roomid, lockDate));
			if (lockFlag != null) {
				// 只要房态锁房缓存中存在key，设置该房间不可用nvc
				this.logger.info("processRoomState::hotelid:{}, roomid:{}, Date: {}, status is nvc.", hotelid, roomid,
						lockDate);
				room.setRoomstatus(this.ROOM_STATUS_NVC);
				return;
			}
		}
	}

	/**
	 * 可定房间数查询(加查询时间)
	 *
	 * @param hotelid
	 * @return
	 */
	public Integer getAvlblRoomNum(Long hotelid, String isnewpms, String isvisible, String isonline, String starttime,
			String endtime) {
		// 初始化查询参数
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Calendar calendar = Calendar.getInstance();
		String today = sdf.format(calendar.getTime());
		String tomorrow = sdf.format(DateUtils.addDays(calendar.getTime(), 1));
		if (starttime == null) {
			starttime = today;
		}
		if (endtime == null) {
			endtime = tomorrow;
		}
		// 空闲房间数
		Integer freeRoomCount = 0;
		try {
			if (!Constant.STR_TRUE.equals(isvisible) || !Constant.STR_TRUE.equals(isonline)) {
				freeRoomCount = 0;
			} else {
				// 从redis缓存中查询已锁的房态
				Map<String, String> lockRoomsCache = new HashMap<String, String>();

				if (Constant.STR_TRUE.equals(isnewpms)) {// 新pms
					lockRoomsCache = roomstateService.findBookedRoomsByHotelIdNewPms(hotelid, starttime, endtime);
				} else {
					lockRoomsCache = roomstateService.findBookedRoomsByHotelId(hotelid, starttime, endtime);
				}

				List<TRoomModel> trooms = tRoomMapper.findRoomsByHotelId(hotelid);
				for (TRoomModel troom : trooms) {
					RoomstateQuerylistRespEntity.Room room = new RoomstateQuerylistRespEntity().new Room();
					room.setRoomid(troom.getId());
					room.setRoomno(troom.getName());
					// 与redis房态缓存比较：vc可用，nvc不可用
					this.processRoomState(room, hotelid, starttime, endtime, lockRoomsCache);

					if (room.getRoomstatus().equals(roomstateService.ROOM_STATUS_VC)) {
						freeRoomCount++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("getAvlblRoomNum hotelid: {} exception {}", hotelid, e);
		}
		return freeRoomCount; // 可订房间数
	}

	/**
	 * calculate room vacancy for promo rooms
	 * 
	 * @param roomTypeId
	 * @param roomModels
	 * @param hotelid
	 * @param isonline
	 * @param starttime
	 * @param endtime
	 * @param lockRoomsCache
	 * @return
	 */
	public Integer calPromoVacants(Integer promoType, Long hotelid, String starttime, String endtime, String isnewpms)
			throws Exception {
		Integer vacants = 0;

		List<TRoomModel> roomModels = tRoomMapper.findRoomsByHotelId(hotelid);
		Map<String, String> lockRoomsCache = null;
		if (Constant.STR_TRUE.equals(isnewpms)) {// 新pms
			try {
				lockRoomsCache = roomstateService.findBookedRoomsByHotelIdNewPms(hotelid, starttime, endtime);
			} catch (Exception ex) {
				throw new Exception(String.format(
						"failed to load cache from findBookedRoomsByHotelIdNewPms for hotelId %s", hotelid), ex);
			}
		} else {
			try {
				lockRoomsCache = roomstateService.findBookedRoomsByHotelId(hotelid, starttime, endtime);
			} catch (Exception ex) {
				throw new Exception(
						String.format("failed to load cache from findBookedRoomsByHotelId for hotelId %s", hotelid),
						ex);
			}
		}

		for (TRoomModel roomModel : roomModels) {
			Long curRoomTypeId = roomModel.getRoomtypeid();
			Long roomid = roomModel.getId();

			Integer curPromoType = 0;
			try {
				List<Map<String, Object>> rooms = roomSaleService.queryRoomByHotelAndRoomType(String.valueOf(hotelid),
						String.valueOf(curRoomTypeId));

				if (rooms.size() > 0) {
					curPromoType = (Integer) rooms.get(0).get("promotype");
				} else {
					logger.warn(String.format("no roomtype have been found for hotelid:%s; roomtypeid:%s", hotelid,
							curRoomTypeId));
				}
			} catch (Exception ex) {
				logger.warn(String.format("failed to queryRoomByHotelAndRoomType, hotelid:%s; roomid:%s; roomtypeid:%s",
						hotelid, roomid, curRoomTypeId), ex);
			}

			if (logger.isInfoEnabled()) {
				logger.info(String.format("queried for roomid:%s->curPromoType:%s; promoType:%s; roomtype:%s", roomid,
						curPromoType, promoType, curRoomTypeId));
			}

			if (curPromoType != null && promoType == curPromoType) {
				RoomstateQuerylistRespEntity.Room room = new RoomstateQuerylistRespEntity().new Room();
				room.setRoomid(roomModel.getId());
				room.setRoomno(roomModel.getName());

				try {
					this.processRoomState(room, hotelid, starttime, endtime, lockRoomsCache);
				} catch (Exception ex) {
					logger.error(String.format("failed to calculate room vacancy for room %s", roomModel.getId()), ex);
					continue;
				}

				if (room.getRoomstatus().equals(roomstateService.ROOM_STATUS_VC)) {
					vacants++;
				}
			}

		}

		return vacants;
	}

	/**
	 * make es term filter
	 *
	 * @param filterBuilder
	 * @return
	 */
	private void makeTermFilter(THotel hotel, List<FilterBuilder> filterBuilders) {
		String hotelid = hotel.getHotelid();
		if (!Strings.isNullOrEmpty(hotelid)) {
			filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));
		}

		String cityid = hotel.getCityid();
		if (!Strings.isNullOrEmpty(cityid)) {
			filterBuilders.add(FilterBuilders.termFilter("hotelcity", cityid));
		}

		String disid = hotel.getDisid();
		if (!Strings.isNullOrEmpty(disid)) {
			filterBuilders.add(FilterBuilders.termFilter("hoteldis", disid));
		}

		// 是否签约 值为T，则只返回签约酒店，值为F或空，返回所有酒店
		filterBuilders.add(FilterBuilders.termFilter("ispms", 1));

	}

	/**
	 * @param hotel
	 * @return
	 */
	private void makeQueryFilter(THotel hotel, List<FilterBuilder> filterBuilders) {
		// hotelname模糊查询
		if (!StringUtils.isBlank(hotel.getHotelname())) {
			QueryFilterBuilder hotelnameFilter = FilterBuilders
					.queryFilter(QueryBuilders.matchQuery("hotelname", hotel.getHotelname()).operator(Operator.AND));
			filterBuilders.add(hotelnameFilter);
		}
		// hoteladdr模糊查询
		if (hotel.getHoteladdr() != null) {
			QueryFilterBuilder hoteladdrFilter = FilterBuilders
					.queryFilter(QueryBuilders.matchQuery("detailaddr", hotel.getHoteladdr()).operator(Operator.AND));
			filterBuilders.add(hoteladdrFilter);
		}
	}

	/**
	 * @param hotel
	 * @return
	 */
	private void makeKeywordFilter(THotel hotel, List<FilterBuilder> keywordBuilders) {
		try {
			// keyword搜索酒店名称，酒店地址
			if (!StringUtils.isBlank(hotel.getKeyword())) {
				QueryFilterBuilder hotelNameFilter = FilterBuilders
						.queryFilter(QueryBuilders.matchQuery("hotelname", hotel.getKeyword()).operator(Operator.AND));
				keywordBuilders.add(hotelNameFilter);

				QueryFilterBuilder hotelAddrFilter = FilterBuilders
						.queryFilter(QueryBuilders.matchQuery("detailaddr", hotel.getKeyword()).operator(Operator.AND));
				keywordBuilders.add(hotelAddrFilter);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * @return
	 */
	private List<FilterBuilder> makeMikePriceRangeFilter(THotel hotel) throws ParseException {
		List<FilterBuilder> mikePriceBuilders = new ArrayList<FilterBuilder>();
		if (StringUtils.isNotBlank(hotel.getMinprice()) || StringUtils.isNotBlank(hotel.getMaxprice())) {
			Double minpriceParam = 0D;
			if (StringUtils.isNotBlank(hotel.getMinprice())) {
				minpriceParam = Double.valueOf(hotel.getMinprice());
			}
			Double maxpriceParam = Double.MAX_VALUE;
			if (StringUtils.isNotBlank(hotel.getMaxprice())) {
				maxpriceParam = Double.valueOf(hotel.getMaxprice());
			}

			// hotel.getStartdateday(), hotel.getEnddateday() yyyyMMdd
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date startDate = sdf.parse(hotel.getStartdateday());
			Date endDate = sdf.parse(hotel.getEnddateday());
			Calendar startDateCal = Calendar.getInstance();
			startDateCal.setTime(startDate);
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(endDate);
			while (startDateCal.compareTo(endDateCal) <= 0) {
				mikePriceBuilders.add(FilterBuilders.rangeFilter(MIKE_PRICE_PROP + sdf.format(startDateCal.getTime()))
						.gte(Double.valueOf(minpriceParam)).lte(Double.valueOf(maxpriceParam)));
				startDateCal.add(Calendar.DATE, 1);
			}
		}
		return mikePriceBuilders;
	}

	/**
	 * 返回酒店是否有可售房间
	 *
	 * @param eshotel
	 * @param hotel
	 * @return
	 */
	private String readonlyHotelvc(Map<String, Object> eshotel, THotel hotel) {
		String result = Constant.STR_FALSE;
		String hotelid = String.valueOf(eshotel.get("hotelid"));
		if (hotel == null || StringUtils.isBlank(hotelid)) {
			return Constant.STR_FALSE;
		}
		String cityid = Constant.STR_CITYID_SHANGHAI;
		try {
			String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
			String whereStarttime = "";
			if (!StringUtils.isBlank(hotel.getStartdateday())) {
				whereStarttime = " time >= '" + hotel.getStartdateday() + "'";
			} else {
				whereStarttime = " time >= '" + strCurDay + "'";
			}

			String whereEndtime = "";
			if (!StringUtils.isBlank(hotel.getEnddateday())) {
				whereEndtime = " time <= '" + hotel.getEnddateday() + "'";
			} else {
				whereEndtime = " time <= '" + strCurDay + "'";
			}

			if (!StringUtils.isBlank(hotel.getCityid())) {
				cityid = hotel.getCityid();
			}

			String sql = "select count(id) as counts from b_roomtemp_" + cityid + " where hotelid='" + hotelid + "'";

			String whereTime = "";
			if (!StringUtils.isBlank(whereStarttime)) {
				whereTime = whereStarttime;
			}
			if (!StringUtils.isBlank(whereEndtime)) {
				if (StringUtils.isBlank(whereTime)) {
					whereTime = whereEndtime;
				} else {
					whereTime += " and " + whereEndtime;
				}
			}
			if (!StringUtils.isBlank(whereTime)) {
				sql += " and (" + whereTime + ")";
			}
			logger.info("getHotelvc method sql is: \n {} ", sql);
			long counts = Db.findFirst(sql).getLong("counts");
			logger.info("酒店占用记录数：{} ", counts);
			// counts > 0，说明已被占用，不可用
			result = counts > 0 ? Constant.STR_FALSE : Constant.STR_TRUE;
			logger.info("--================================== 酒店是否有可售房查询结果：{} ==================================-- ",
					result);
		} catch (Exception e) {
			result = Constant.STR_FALSE;
			logger.error("getHotelvc method error:\n" + e.getMessage());
		}
		return result;
	}

	/**
	 * @param cityid
	 * @param hotelid
	 * @param beginDate
	 * @param endDate
	 * @return
	 */
	public Bean readonlyHotelPrice(String cityid, String hotelid, String beginDate, String endDate) {
		logger.info("!!!--== OTS Info ==-- !!! getHotelMinPrice method begin...");
		Bean bean = null;
		try {
			String sql = "select max(cost) as maxprice, min(cost) as minprice from b_costtemp_" + cityid
					+ " where hotelid='" + hotelid + "' " + " and time>='" + beginDate + "' and time<='" + endDate
					+ "'";
			bean = Db.findFirst(sql);
			if (bean == null || (bean.get("minprice") == null && bean.get("maxprice") == null)) {
				logger.info(
						"!!!--== OTS Warning ==-- !!! cityid: {}, hotelid: {}, beginDate: {}, endDate: {} price not find.",
						cityid, hotelid, beginDate, endDate);
			} else {
				logger.info("!!!--== OTS Info ==-- !!! get Hotel: {} price success, maxprice = {}, minprice = {} .",
						hotelid, bean.get("maxprice"), bean.get("minprice"));
			}
		} catch (Exception e) {
			logger.error("!!!--== OTS Error ==-- !!! get Hotel: {} price error: {} ", hotelid, e.getMessage());
		}
		logger.info("!!!--== OTS Info ==-- !!! getHotelMinPrice method end...");
		return bean;
	}

	/**
	 * @param roomtypeItem
	 * @param hotel
	 * @return
	 */
	private String readonlyRoomtypevc(Map<String, Object> roomtypeItem, THotel hotel) {
		String result = Constant.STR_FALSE;
		if (hotel == null) {
			return Constant.STR_FALSE;
		}
		String cityid = Constant.STR_CITYID_SHANGHAI;
		try {
			String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
			String whereStarttime = "";
			if (!StringUtils.isBlank(hotel.getStartdate())) {
				whereStarttime = " time >= '" + hotel.getStartdate() + "'";
			} else {
				whereStarttime = " time >= '" + strCurDay + "'";
			}

			String whereEndtime = "";
			if (!StringUtils.isBlank(hotel.getEnddate())) {
				whereEndtime = " time <= '" + hotel.getEnddate() + "'";
			} else {
				whereEndtime = " time <= '" + strCurDay + "'";
			}

			if (!StringUtils.isBlank(hotel.getCityid())) {
				cityid = hotel.getCityid();
			}

			String sql = "select count(id) as counts from b_roomtemp_" + cityid + " where roomtypeid=?";
			String whereTime = "";
			if (!StringUtils.isBlank(whereStarttime)) {
				whereTime = whereStarttime;
			}
			if (!StringUtils.isBlank(whereEndtime)) {
				if (StringUtils.isBlank(whereTime)) {
					whereTime = whereEndtime;
				} else {
					whereTime += " and " + whereEndtime;
				}
			}
			if (!StringUtils.isBlank(whereTime)) {
				sql += " and (" + whereTime + ")";
			}
			logger.info("getRoomtypevc method sql is: \n {} ", sql);
			long counts = Db.findFirst(sql, roomtypeItem.get("roomtypeid")).getLong("counts");
			// counts > 0，说明已被占用，不可用
			result = counts > 0 ? Constant.STR_FALSE : Constant.STR_TRUE;
			logger.info("--================================== 房型是否可用查询结果：{} ==================================-- ",
					result);
		} catch (Exception e) {
			result = Constant.STR_FALSE;
			logger.error("getRoomtypevc method error:\n" + e.getMessage());
		}
		return result;
	}

	/**
	 * @param eshotel
	 * @return
	 */
	private List<Map<String, Object>> readonlyRoomtypeList(String hotelid, String bedtype) {
		List<Map<String, Object>> roomtypelist = new ArrayList<Map<String, Object>>();
		if (hotelid == null || StringUtils.isBlank(hotelid)) {
			return roomtypelist;
		}
		try {
			StringBuffer bfSql = new StringBuffer();
			bfSql.append(
					"select a.id as roomtypeid, a.name as roomtypename, a.cost as roomtypepmsprice, a.bednum,a.roomnum, "
							+ "a.cost as roomtypeprice,b.maxarea,b.minarea,b.pics,b.bedtype,b.bedsize as bedlength, d.name as bedtypename")
					.append("  from t_roomtype a ")
					.append("    left outer join t_roomtype_info b on a.id = b.roomtypeid")
					.append("      left outer join t_bedtype d on b.bedtype = d.id")
					.append("        where a.thotelid='" + hotelid + "'");
			if (!StringUtils.isBlank(bedtype)) {
				bfSql.append(" and b.bedtype='" + bedtype + "'");
			}
			List<Bean> list = Db.find(bfSql.toString());
			logger.info("getRoomtypeList method sql: {}\n", bfSql.toString());
			for (Bean bean : list) {
				roomtypelist.add(bean.getColumns());
			}
		} catch (Exception e) {
			logger.error("getRoomtypeList method error:\n" + e.getMessage());
			return roomtypelist;
		}
		return roomtypelist;
	}

	/**
	 * @param roomtypeItem
	 * @return
	 */
	private List<Map<String, Object>> readonlyRoomtypeFaciList(Map<String, Object> roomtypeItem) {
		List<Map<String, Object>> roomtypefacilist = new ArrayList<Map<String, Object>>();
		try {
			String sql = "select a.facid as roomtypefacid,b.facname as roomtypefacname "
					+ " from t_roomtype_facility a " + " left outer join t_facility b " + " on a.facid = b.id "
					+ " where a.roomtypeid=? " + " order by b.facsort asc";
			logger.info("getRoomtypeFaciList method sql is:\n {} ", sql);
			List<Bean> list = Db.find(sql, roomtypeItem.get("roomtypeid"));
			logger.info("getRoomtypeFaciList method return {} records.", list.size());
			for (Bean bean : list) {
				roomtypefacilist.add(bean.getColumns());
			}
		} catch (Exception e) {
			logger.error("getRoomtypeFaciList method error:\n" + e.getMessage());
		}
		return roomtypefacilist;
	}

	/**
	 * @param roomtypeItem
	 * @return
	 */
	private List<Bean> readonlyRoomtypeFac(Long roomtypeid) {
		String sql = "select a.facid as roomtypefacid,b.facname as roomtypefacname,b.facType "
				+ " from t_roomtype_facility a " + " left outer join t_facility b " + " on a.facid = b.id "
				+ " where a.roomtypeid=? " + " order by b.facsort asc";
		logger.info("getRoomtypeFaciList method sql is:\n {} ", sql);
		return Db.find(sql, roomtypeid);
	}

	/**
	 * 转换酒店数据
	 *
	 * @param data
	 *            参数：es酒店信息
	 * @param hotel
	 *            参数：参数数据
	 * @return
	 */
	private Object queryTransferData(Map<String, Object> data, THotel hotel) {
		// 是否考虑优惠价格: 非必填(T/F)，值为T，则最低价取优惠活动最低价，空或F则最低价取ota最低门市价
		boolean isDiscount = Constant.STR_TRUE.equals(hotel.getIsdiscount());

		// 是否返回酒店图片: 非必填(T/F)，空等同于F，值为T，则返回图片信息，空或F则不返回图片信息
		boolean isHotelPic = Constant.STR_TRUE.equals(hotel.getIshotelpic());

		// 是否返回酒店设施: 非必填(T/F)，值为T，则返回酒店设施信息，空或F则不返回酒店设施信息
		boolean isFacility = Constant.STR_TRUE.equals(hotel.getIsfacility());

		// 是否返回商圈: 非必填(T/F)，值为T，则返回酒店商圈信息，空或F则不返回酒店设施信息
		boolean isBussinessZone = Constant.STR_TRUE.equals(hotel.getIsbusinesszone());

		// 是否返回房型: 非必填(T/F)，值为T，则返回房型信息，空或F则不返回房型信息
		boolean isRoomType = Constant.STR_TRUE.equals(hotel.getIsroomtype());

		// 是否返回房型图片: 非必填(T/F)，值为T，则返回房型图片信息，空或F则不返回房型图片信息
		boolean isRoomTypePic = Constant.STR_TRUE.equals(hotel.getIsroomtypepic());

		// 是否返回房型设施: 非必填(T/F)，值为T，则返回房型设施信息，空或F则不返回房型设施信息
		boolean isRoomTypeFacility = Constant.STR_TRUE.equals(hotel.getIsroomtypefacility());

		// 是否返回床型: 非必填(T/F)，值为T，则返回床型信息，空或F则不返回床型信息
		boolean isBedType = Constant.STR_TRUE.equals(hotel.getIsbedtype());

		// 是否返回团购信息: 非必填(T/F)，值为T，则返回团购信息，空或F则不返回团购信息
		boolean isTeambuying = Constant.STR_TRUE.equals(hotel.getIsteambuying());

		// 是否返回交通信息: 非必填(T/F)，值为T，则返回交通信息，空或F则不返回交通信息
		boolean isTraffic = Constant.STR_TRUE.equals(hotel.getIstraffic());

		// 是否返回周边信息: 非必填(T/F)，值为T，则返回周边信息，空或F则不返回周边信息
		boolean isPeripheral = Constant.STR_TRUE.equals(hotel.getIsperipheral());

		// 床型：1单床房，2双床房，3其它房，空不限制
		String bedtype = hotel.getBednum();

		// 不返回酒店图片信息，从结果集中删除
		if (!isHotelPic) {
			data.remove("hotelpic");
		} else {
			/*
			 * // 需要返回酒店图片信息 List<Map<String, Object>> hotelpics = new
			 * ArrayList<Map<String, Object>>(); if (data.get("hotelpic") !=
			 * null) { hotelpics = (List<Map<String, Object>>)
			 * data.get("hotelpic"); } for (Map<String, Object> hotelpic :
			 * hotelpics) { String picName =
			 * String.valueOf(hotelpic.get("name")); hotelpic.put("name",
			 * HotelPictureEnum.getByName(picName).getTitle()); }
			 */
			// 酒店图片
			if (data.get("hotelpic") != null) {
				List<Map<String, Object>> needtranspic = (List<Map<String, Object>>) data.get("hotelpic");
				data.put("needtranspic", needtranspic);
				List<Map<String, Object>> hotelpics = new ArrayList<Map<String, Object>>();
				hotelpics = (List<Map<String, Object>>) data.get("hotelpic");
				List<Map<String, Object>> hotelpiclist = new ArrayList<Map<String, Object>>();
				boolean ismatch = false;
				for (Map<String, Object> hotelpic : hotelpics) {
					String picName = String.valueOf(hotelpic.get("name"));
					List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
					// 遍历到主力房源
					if (HotelPictureEnum.PIC_MAINHOUSING.getName().equals(picName) && picurl != null
							&& picurl.size() > 0) {
						hotelpic.put("name", HotelPictureEnum.getByName(picName).getTitle());
						hotelpiclist.add(hotelpic);
						data.put("hotelpic", hotelpiclist);
						ismatch = true;
						break;
					}
				}
				// 未匹配到取门头及招牌
				if (!ismatch) {
					for (Map<String, Object> hotelpic : hotelpics) {
						String picName = String.valueOf(hotelpic.get("name"));
						List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
						// 门头及招牌
						if (HotelPictureEnum.PIC_DEF.getName().equals(picName) && picurl != null && picurl.size() > 0) {
							hotelpic.put("name", HotelPictureEnum.getByName(picName).getTitle());
							hotelpiclist.add(hotelpic);
							data.put("hotelpic", hotelpiclist);
							break;
						}
					}
				}
			}

		}
		// 不返回酒店设施信息，从结果集中删除
		if (!isFacility) {
			data.remove("hotelfacility");
		}
		// 不返回酒店商圈信息，从结果集中删除
		if (!isBussinessZone) {
			data.remove("businesszone");
		}

		// 不返回酒店交通信息，从结果集中删除
		if (!isTraffic) {
			data.remove("traffic");
		}
		// 不返回酒店周边信息，从结果集中删除
		if (!isPeripheral) {
			data.remove("peripheral");
		}
		// 先判断该酒店是否有可售房，如果没有的话，不再继续处理后面的业务逻辑
		// TODO: ???? ---- 是否有可售房（T/F）该字段根据入参入住时间和离店时间获取该房型是否有可入住房间，并根据结果返回T、F
		// ---- ????
		// //logger.info("--================================== 查询酒店是否有可售房开始：
		// ==================================-- ");
		String hotelvc = Constant.STR_TRUE; // //this.getHotelvc(data, hotel);
		// //logger.info("--================================== 查询酒店是否有可售房结束：
		// ==================================-- ");
		data.put("hotelvc", hotelvc);
		// 酒店没有可售房间也要返回接口数据
		// if (Constant.STR_FALSE.equals(hotelvc) ||
		// !Constant.STR_TRUE.equals(hotelvc)) {
		// return data;
		// }

		logger.info("--================================== 查询酒店最低价格开始： ==================================-- ");
		// TODO: 如果是非签约酒店，不需要查询房价.(因为C端有业务逻辑：如果按照房型和价格搜索，不显示非签约酒店)
		String cityid = String.valueOf(data.get("hotelcity"));
		String hotelid = String.valueOf(data.get("hotelid"));
		String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
		String beginDate = strCurDay;
		String endDate = strCurDay;
		if (!StringUtils.isBlank(hotel.getStartdateday())) {
			beginDate = hotel.getStartdateday();
		}

		if (!StringUtils.isBlank(hotel.getEnddateday())) {
			endDate = hotel.getEnddateday();
		}
		Bean priceBean = this.readonlyHotelPrice(cityid, hotelid, beginDate, endDate);
		if (priceBean != null) {
			data.put("maxprice", priceBean.get("maxprice") == null ? 0 : priceBean.get("maxprice"));
			data.put("minprice", priceBean.get("minprice") == null ? 0 : priceBean.get("minprice"));
		} else {
			data.put("maxprice", 0);
			data.put("minprice", 0);
		}
		logger.info("--================================== 查询酒店最低价格结束： ==================================-- ");

		Map<String, Object> point = (Map<String, Object>) data.get("pin");
		// hotel latitude and longitude
		data.put("latitude", point.get("lat"));
		data.put("longitude", point.get("lon"));
		// hotel distance
		// data.put("distance", "3.5");

		// hotel ispms：是否签约酒店，移到listOtsHotel和getOtsHotel
		if ("1".equals(String.valueOf(data.get("ispms")))) {
			data.put("ispms", Constant.STR_TRUE);
		} else {
			data.put("ispms", Constant.STR_FALSE);
		}

		// 如果返回团购信息，查询团购信息放到data结果集中
		if (isTeambuying) {
			data.put("isteambuying", "T");
			List<Map<String, Object>> teambuyinfos = new ArrayList<Map<String, Object>>();
			Map<String, Object> info = new HashMap<String, Object>();
			info.put("teambuyingname", "眯客团购");
			info.put("url", "http://www.tuangou.com");
			teambuyinfos.add(info);
			data.put("teambuying", teambuyinfos);
		}

		// TODO: 是否推荐，无数据来源，暂定为F
		data.put("isrecommend", Constant.STR_FALSE);

		// TODO: hotel score: 移到listOtsHotel和getOtsHotel方法里.
		logger.info("--================================== 查询酒店评价信息开始： ==================================-- ");
		Bean scoreBean = this.readonlyHotelScoreInfo(String.valueOf(data.get("hotelid")));
		if (scoreBean != null) {
			data.put("scorecount", scoreBean.get("scorecount") == null ? 0 : scoreBean.get("scorecount"));
			data.put("grade", scoreBean.get("grade") == null ? 0 : scoreBean.get("grade"));
		} else {
			data.put("scorecount", 0);
			data.put("grade", 0);
		}
		logger.info("--================================== 查询酒店评价信息结束： ==================================-- ");

		data.put("citycode", data.get("hotelcity") == null ? "" : data.get("hotelcity"));
		// hotel base info: 移到listOtsHotel和getOtsHotel方法里.
		Bean hotelInfo = this.readonlyHotelBaseInfo(String.valueOf(data.get("hotelid")));
		if (hotelInfo != null) {
			data.put("hoteldis", hotelInfo.get("hoteldis") == null ? "" : hotelInfo.get("hoteldis"));
			data.put("hotelcity", hotelInfo.get("hotelcity") == null ? "" : hotelInfo.get("hotelcity"));
			data.put("hotelprovince", hotelInfo.get("hotelprovince") == null ? "" : hotelInfo.get("hotelprovince"));
			data.put("hotelphone", hotelInfo.get("hotelphone") == null ? "" : hotelInfo.get("hotelphone"));
		}

		// room type
		// 如果返回房型信息，查询房型信息放到data结果集中
		if (isRoomType) {
			List<Map<String, Object>> roomtypeList = this.readonlyRoomtypeList(String.valueOf(data.get("hotelid")),
					bedtype);
			for (Map<String, Object> roomtypeItem : roomtypeList) {
				logger.info("--================================== 查询房型是否可用信息开始： ==================================-- ");
				// roomtypevc
				// TODO: 获取房态信息:
				// 该房间是否有可售.(由于现在房态信息缓存到redis，不再存储到mysql中间表，所以这里从缓存取.)
				String roomtypevc = this.readonlyRoomtypevc(roomtypeItem, hotel);
				logger.info("--================================== 查询房型是否可用信息结束： ==================================-- ");
				roomtypeItem.put("roomtypevc", roomtypevc);
				// 床型
				// 如果返回床型信息，查询床型信息放到data结果集中
				if (isBedType) {
					// 2015-05-14: 业务逻辑修改，床型返回数据做了修改。
					List<Map<String, Object>> bedList = new ArrayList<Map<String, Object>>();
					String bedlength = String.valueOf(roomtypeItem.get("bedlength"));
					String[] bedsizes = bedlength.split(",");
					for (int i = 0; i < bedsizes.length; i++) {
						Map<String, Object> bedItem = new HashMap<String, Object>();
						bedItem.put("bedtypename", roomtypeItem.get("bedtypename")); // 床型(双人床、单人床)
						bedItem.put("bedlength", bedsizes[i]); // 尺寸(1.5米、1.8米)
						bedList.add(bedItem);
					}

					Map<String, Object> bedMap = new HashMap<String, Object>();
					bedMap.put("count", bedList.size());
					bedMap.put("beds", bedList);

					roomtypeItem.put("bed", bedMap);
				}

				// 房型图片信息
				// 如果返回房型图片信息，查询房型图片数据放到data结果集中
				if (isRoomTypePic) {
					//// JSONArray pics =
					//// JSONArray.fromObject(roomtypeItem.get("pics"));
					//// List<Map<String, Object>> picsList = (List<Map<String,
					//// Object>>)JSONArray.toCollection(pics, Map.class);
					ObjectMapper objectMapper = new ObjectMapper();
					String pics = "";
					try {
						pics = (String) roomtypeItem.get("pics");
						List picsList = objectMapper.readValue(pics, List.class);
						roomtypeItem.put("roomtypepic", picsList);
						roomtypeItem.remove("pics");
					} catch (Exception e) {
						logger.error("解析房型图片数据 {} 出错: {}", pics, e.getMessage());
					}
				} else {
					roomtypeItem.remove("pics");
					roomtypeItem.remove("roomtypepic");
				}

				// 房间设施
				// 如果返回房型设施，查询房型设施信息放到data结果集中
				if (isRoomTypeFacility) {
					logger.info("--================================== 查询房型设施开始： ==================================-- ");
					List<Map<String, Object>> facilityList = this.readonlyRoomtypeFaciList(roomtypeItem);
					logger.info("--================================== 查询房型设施结束： ==================================-- ");
					roomtypeItem.put("roomtypefacility", facilityList);
				}

				// 处理房型眯客价roomtypeprice
				String strRoomtypeid = String.valueOf(roomtypeItem.get("roomtypeid"));
				BigDecimal roomtypeprice = roomstateService.getRoomPrice(Long.valueOf(hotelid),
						Long.valueOf(strRoomtypeid), beginDate, endDate);
				roomtypeItem.put("roomtypeprice", roomtypeprice);
			}
			data.put("roomtype", roomtypeList);
		}

		// 返回数据
		// 删除接口数据不需要的属性
		data.remove("pin");
		data.remove("flag");
		return data;
	}

	// 添加非签约酒店
	public boolean syncNoPMSHotel(String name) throws Exception {
		if (StringUtils.isBlank(name)) {
			name = "nopmshotels.csv";
		}
		//// String filename = this.getClass().getResource("/").getPath() +
		//// name;
		String filename = Thread.currentThread().getContextClassLoader().getResource(name).getFile();
		List tableList = CSVFileUtil.readLine(filename);
		tableList.remove(0);// 移除第一行的标题行
		/*
		 * for(int i=0;i<tableList.size();i++){ List row =
		 * (List)tableList.get(i);
		 * 
		 * System.out.println(row.get(0)+" "+ row.size()); }
		 */
		return hotelDAO.syncNoPMSHotel(tableList);
	}

	/**
	 * 同步酒店关键词库
	 *
	 * @param hotelid
	 * @param keywords
	 * @return
	 */
	public Map<String, Object> saveKeywords(Long hotelid, String keywords) {
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			// 首先检查hotelid的关键字keywords是否存在，如果存在，则不再同步关键字
			Bean keyword = Db.findById("h_keyword", hotelid);
			if (keyword == null) {
				ServerChannel.sendKeyword(hotelid, keywords);
				rtnMap.put("success", true);
			} else {
				rtnMap.put("success", true);
			}
		} catch (Exception e) {
			rtnMap.put("success", false);
			rtnMap.put("errcode", "-1");
			rtnMap.put("errmsg", e.getMessage());
		}
		return rtnMap;
	}

	/**
	 * 清空ES中的酒店数据
	 *
	 * @return
	 */
	public ServiceOutput clearEsHotel() {
		ServiceOutput output = new ServiceOutput();
		try {
			try {
				esProxy.deleteAllDocument();
				output.setSuccess(true);
			} catch (Exception e) {
				output.setFault("clear es hotel data error: " + e.getMessage());
				logger.error("clear es hotel data error: {} ", e.getMessage());
			}
		} catch (Exception e) {
			output.setFault("清空ES中的酒店数据出错: " + e.getMessage());
		}
		return output;
	}

	public List<Map<String, Object>> queryHistoryHotels(String token, String code, int start, int limit)
			throws Exception {
		Calendar calendar = Calendar.getInstance();
		String today = sdf.format(calendar.getTime());
		String tomorrow = sdf.format(DateUtils.addDays(calendar.getTime(), 1));

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		List<THotelModel> list = bHotelStatDao.queryHistoryHotels(token, code, start, limit);
		for (THotelModel tHotelModel : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			Long hotelid = tHotelModel.getId();
			THotel thotel = new THotel();
			thotel.setHotelid(hotelid + "");
			thotel.setIsfacility("T");
			Map<String, Object> result0 = readonlyOtsHotelFromEsStore(thotel);
			List<Map<String, Object>> hotels = (List<Map<String, Object>>) result0.get("hotel");
			if (hotels.size() > 0) {
				Map<String, Object> hotel = hotels.get(0);
				map.putAll(hotel);
			}

			// 酒店id
			map.put("hotelid", hotelid);
			// 酒店名称
			map.put("hotelname", tHotelModel.getHotelname());

			// 酒店图片
			String picJson = tHotelModel.getHotelpic();
			if (StringUtils.isNotEmpty(picJson)) {
				List<HotelPicJsonBean> beans = gson.fromJson(picJson, new TypeToken<ArrayList<HotelPicJsonBean>>() {
				}.getType());
				List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
				boolean ismatch = false;
				for (HotelPicJsonBean bean : beans) {
					// 遍历到主力房源
					Map<String, Object> picmap = new HashMap<String, Object>();
					if (HotelPictureEnum.PIC_MAINHOUSING.getName().equals(bean.getName()) && bean.getPic().size() > 0) {
						picmap.put("name", HotelPictureEnum.getByName(bean.getName()).getTitle());
						picmap.put("url", bean.getPic().get(0).getUrl());
						maplist.add(picmap);
						ismatch = true;
						map.put("pic", maplist);
						break;
					}
				}
				// 未匹配到取门头及招牌
				if (!ismatch) {
					for (HotelPicJsonBean bean : beans) {
						Map<String, Object> picmap = new HashMap<String, Object>();
						if (HotelPictureEnum.PIC_DEF.getName().equals(bean.getName()) && bean.getPic().size() > 0) {
							picmap.put("name", HotelPictureEnum.getByName(bean.getName()).getTitle());
							picmap.put("url", bean.getPic().get(0).getUrl());
							maplist.add(picmap);
							map.put("pic", maplist);
							break;
						}
					}
				}
			}
			// 房型id
			map.put("roomtypeid", tHotelModel.getRoomTypeId());
			// 房间号
			map.put("roomno", tHotelModel.getRoomNo());
			// 房型名字
			map.put("roomtypename", tHotelModel.getRoomTypeName());

			// 可订房间数
			Integer avlblroomnum = getAvlblRoomNum(tHotelModel.getId(), tHotelModel.getIsnewpms(),
					tHotelModel.getVisible(), tHotelModel.getOnline(), null, null);
			map.put("avlblroomnum", avlblroomnum);

			// 可定房间数描述
			Map<String, String> fullstate = getFullState(avlblroomnum);
			map.putAll(fullstate);

			// 月销量
			// added by chuaiqing at 2015-09-10 14:03:07
			// 搜索上海的酒店时，不显示近30天订单销量
			// 眯客2.5业务逻辑：如果近30天订单销量小于10，则C端不显示“月销xxx单”
			// 所以如果返回城市为上海的时候，接口返回月销售量数据为0
			if (Constant.STR_CITYID_SHANGHAI.equals(tHotelModel.getCitycode())) {
				map.put("ordernummon", "");
			} else {
				Long sales = getOrderNumMon(tHotelModel.getId());
				map.put("ordernummon", (sales >= 10 ? "月销" + sales + "单" : ""));
			}

			// 是否返现（T/F）
			boolean iscashback = cashBackService.isCashBackHotelId(Long.valueOf(hotelid), today, tomorrow);
			if (iscashback) {
				map.put("iscashback", Constant.STR_TRUE);
			} else {
				map.put("iscashback", Constant.STR_FALSE);
			}

			// 最近预订时间描述信息
			Date createTimeDate = tHotelModel.getCreateTime();
			String rcntordertimedes = getRcntHotelLiveTimeDes(sdf1.format(createTimeDate));
			map.put("rcntordertimedes", rcntordertimedes);

			// 最低眯客价和最低门市价
			String[] prices = null;
			if (hotelPriceService.isUseNewPrice())
				prices = hotelPriceService.getHotelMikePrices(tHotelModel.getId(), today, tomorrow);
			else
				prices = roomstateService.getHotelMikePrices(tHotelModel.getId(), today, tomorrow);
			map.put("minprice", new BigDecimal(prices[0]));
			map.put("minpmsprice", new BigDecimal(prices[1]));

			result.add(map);
		}
		return result;
	}

	public long queryHistoryHotelsCount(String token, String code) {
		return bHotelStatDao.queryHistoryHotelsCount(token, code);
	}

	public void deleteHotelStats(String token, Long hotelid) {
		bHotelStatDao.deleteHotelStats(token, hotelid);
	}

	/**
	 * 查询酒店下的所有房型信息
	 *
	 * @param hotelid
	 * @param isRoomTypeFacility
	 * @param isBedType
	 * @return
	 */
	public Map<String, Object> readonlyRoomTypeInfo(Long roomtypeid) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		TRoomTypeInfoModel roomtypeInfo = tRoomtypeInfoMapper.findByRoomtypeid(roomtypeid);
		if (roomtypeInfo == null) {
			resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			resultMap.put(ServiceOutput.STR_MSG_ERRMSG, "无此房型:" + roomtypeid);
			return resultMap;
		}
		// 房型图片信息
		ObjectMapper objectMapper = new ObjectMapper();
		String pics = "";
		try {
			pics = roomtypeInfo.getPics();
			List<Map<String, Object>> resultPics = new ArrayList<Map<String, Object>>();
			if (StringUtils.isNotBlank(pics)) {
				List<Map<String, Object>> picsList = objectMapper.readValue(pics, List.class);
				for (Map<String, Object> pp : picsList) {
					String name = pp.get("name").toString();
					List<Map<String, Object>> urls = (List<Map<String, Object>>) pp.get("pic");
					if (urls == null)
						continue;
					for (Map<String, Object> u : urls) {
						String url = u.get("url").toString();
						if (StringUtils.isNotBlank(url)) {
							Map<String, Object> picMap = new HashMap<String, Object>();
							String nameTitle = RoomTypePictureEnum.getByName(name).getTitle();
							if (StringUtils.isBlank(nameTitle)) {
								nameTitle = "未知";
							}
							picMap.put("name", nameTitle);
							picMap.put("url", url);
							resultPics.add(picMap);
						}
					}
				}
			}
			resultMap.put("pic", resultPics);
		} catch (Exception e) {
			logger.error("解析房型图片数据 {} 出错: {}", pics, e.getStackTrace());
		}
		BigDecimal area = roomtypeInfo.getMaxarea();
		if (area == null) {
			area = roomtypeInfo.getMinarea();
		}
		if (area == null) {
			area = BigDecimal.ZERO;
		}
		resultMap.put("area", area);
		String bedLength = roomtypeInfo.getBedsize();
		if (StringUtils.isNotBlank(bedLength)) {
			if (bedLength.contains(",")) {
				bedLength = bedLength.replaceAll(",", "米,");
			}
			bedLength += "米";
		} else {
			bedLength = "";
		}
		resultMap.put("bedlength", bedLength);
		resultMap.put("bedtypename", roomtypeInfo.getBedtypename() == null ? "" : roomtypeInfo.getBedtypename());

		String bathroomtype = "";// 卫浴类型

		// 房间设施 不需要加 binding hotel设施和 roomtype设施存的关联表不同
		// List<TFacilityModel> tFacs =
		// tFacilityMapper.findByRoomtypeId(roomtypeid,
		// TFacilityMapper.ROOM_TYPE_BINDING);
		List<TFacilityModel> tFacs = tFacilityMapper.findByRoomtypeId(roomtypeid);
		List<Map<String, Object>> facList = new ArrayList();
		List<Map<String, Object>> serviceList = new ArrayList();
		for (TFacilityModel fac : tFacs) {
			Map<String, Object> val = new HashMap<String, Object>();
			int facType = fac.getFactype();
			Long facId = fac.getId();
			String facName = fac.getFacname();
			switch (facType) {
			case 1:
				val.put("infrastructureid", facId);
				val.put("infrastructurename", facName);
				facList.add(val);
				break;
			case 2:
				val.put("valueaddedfaid", facId);
				val.put("valueaddedfaname", facName);
				serviceList.add(val);
				break;
			case 3:
				bathroomtype = facName;
				break;
			}
		}
		resultMap.put("infrastructure", facList);// 基础设施
		resultMap.put("valueaddedfa", serviceList);// 增值设施
		resultMap.put("bathroomtype", bathroomtype);// 卫浴类型

		// mike3.0 add
		// 是否返现
		Map<String, Object> cashMap = cashBackService.getCashBackByRoomtypeId(roomtypeid, null, null);
		String iscashback = "F";
		BigDecimal cash = BigDecimal.ZERO;
		if (cashMap != null && cashMap.containsKey("iscashback") && cashMap.containsKey("cashbackcost")) {
			iscashback = cashMap.get("iscashback").toString();
			cash = new BigDecimal(cashMap.get("cashbackcost").toString());
		}
		resultMap.put("iscashback", iscashback);// 是否返现（T/F）
		resultMap.put("cashbackcost", cash);// 返现金额

		/*
		 * roomtypeItem.put("roomtypefacility", facilityList); //
		 * 处理房型眯客价roomtypeprice String strRoomtypeid =
		 * String.valueOf(roomtypeItem.get("roomtypeid")); BigDecimal
		 * roomtypeprice = roomstateService.getRoomPrice(Long.valueOf(hotelid),
		 * Long.valueOf(strRoomtypeid), beginDate, endDate);
		 * roomtypeItem.put("roomtypeprice", roomtypeprice);
		 */
		return resultMap;
	}

	/**
	 * 获取酒店详细信息
	 *
	 * @param hotel
	 * @return
	 */
	public Map<String, Object> readonlyHotelDetail(Long hotelId) {
		THotelModel hotelModel = hotelMapper.findHotelInfoById(hotelId);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		if (hotelModel == null) {
			resultMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			resultMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			resultMap.put(ServiceOutput.STR_MSG_ERRMSG, "无此酒店:" + hotelId);
			return resultMap;
		}
		// 添加设施
		List<TFacilityModel> facilitys = tFacilityMapper.findByHotelid(hotelId);
		List<Map<String, Object>> facList = new ArrayList();
		List<Map<String, Object>> serviceList = new ArrayList();
		for (TFacilityModel fac : facilitys) {
			Map<String, Object> val = new HashMap<String, Object>();
			if (fac.getFactype() == 2) {
				val.put("serviceid", fac.getId());
				val.put("servicename", fac.getFacname());
				serviceList.add(val);
			} else {
				val.put("facid", fac.getId());
				val.put("facname", fac.getFacname());
				facList.add(val);
			}
		}
		resultMap.put("hotelfacility", facList);
		resultMap.put("service", serviceList);

		resultMap.put("hotelid", hotelId);
		resultMap.put("hotelname", hotelModel.getHotelname());
		resultMap.put("detailaddr", hotelModel.getDetailaddr() == null ? "" : hotelModel.getDetailaddr());

		// 图片处理
		List<Map<String, Object>> picResult = new ArrayList<Map<String, Object>>();
		String hotelpic = hotelModel.getHotelpic();
		if (!StringUtils.isBlank(hotelpic)) {
			try {
				List<Map<String, Object>> pics = (new Gson()).fromJson(hotelpic, List.class);
				for (Map<String, Object> pp : pics) {
					String name = pp.get("name").toString();
					List<Map<String, Object>> urls = (List<Map<String, Object>>) pp.get("pic");
					if (urls == null)
						continue;
					for (Map<String, Object> u : urls) {
						String url = u.get("url").toString();
						if (StringUtils.isNotBlank(url)) {
							Map<String, Object> picMap = new HashMap<String, Object>();
							String nameTitle = HotelPictureEnum.getByName(name).getTitle();
							if (StringUtils.isBlank(nameTitle)) {
								nameTitle = "未知";
							}
							picMap.put("name", nameTitle);
							picMap.put("url", url);
							picResult.add(picMap);
						}
					}
				}
			} catch (Exception e) {
				logger.error("解析酒店 {} 图片数据 {} 出错. {}", hotelId, hotelpic, e.getMessage());
			}
		}
		resultMap.put("hotelpic", picResult);

		resultMap.put("hoteldis", hotelModel.getDisname());
		resultMap.put("hotelcity", hotelModel.getCityname());
		resultMap.put("hotelprovince", hotelModel.getProvince());
		resultMap.put("hotelphone", hotelModel.getHotelphone() == null ? "" : hotelModel.getHotelphone());
		resultMap.put("hoteldisc", hotelModel.getIntroduction() == null ? "" : hotelModel.getIntroduction());
		resultMap.put("retentiontime", hotelModel.getRetentiontime() == null ? "18:00" : hotelModel.getRetentiontime());
		resultMap.put("defaultleavetime",
				hotelModel.getDefaultleavetime() == null ? "12:00" : hotelModel.getDefaultleavetime());

		// 添加是否返现
		String iscashback = "F";
		boolean iscash = cashBackService.isCashBackHotelId(hotelId, null, null);
		if (iscash) {
			iscashback = "T";
		}
		resultMap.put("iscashback", iscashback);

		// 开店时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date opentime = hotelModel.getOpentime();
		if (opentime != null) {
			resultMap.put("opentime", sdf.format(opentime));
		}
		// resultMap.put("traffic", hotelModel.getTraffic());

		// 添加businesszone
		List<TBusinesszoneModel> businesses = tBusinesszoneMapper.selectByHotelid(hotelId);
		List<Map<String, Object>> bList = new ArrayList<Map<String, Object>>();
		for (TBusinesszoneModel bm : businesses) {
			Map<String, Object> b = new HashMap<String, Object>();
			b.put("businesszonename", bm.getName());
			bList.add(b);
		}
		resultMap.put("businesszone", bList);

		// 交通
		Map<String, Object> traffics = new HashMap<String, Object>();
		String trafficStr = hotelModel.getTraffic();
		if (!StringUtils.isBlank(trafficStr)) {
			try {
				traffics = (new Gson()).fromJson(trafficStr, HashMap.class);
			} catch (Exception e) {
				logger.error("解析酒店 {} 交通数据 {} 出错. {}", hotelId, trafficStr, e.getMessage());
			}
		}
		resultMap.put("trafficdec", traffics);

		// 周边
		Map<String, Object> peripheralMap = new HashMap<String, Object>();
		String peripheralsStr = hotelModel.getPeripheral();
		if (!StringUtils.isBlank(peripheralsStr)) {
			try {
				peripheralMap = (new Gson()).fromJson(peripheralsStr, HashMap.class);
			} catch (Exception e) {
				logger.error("解析酒店 {} 交通数据 {} 出错. {}", hotelId, peripheralsStr, e.getMessage());
			}
		}
		// 周边景点
		if (peripheralMap != null && peripheralMap.containsKey("scenicSpot")) {
			resultMap.put("viewspotdec", peripheralMap.get("scenicSpot"));
		} else {
			resultMap.put("viewspotdec", "");
		}

		// 周边生活描述
		if (peripheralMap != null && peripheralMap.containsKey("restaurant")) {
			resultMap.put("ambituslifedec", peripheralMap.get("restaurant"));
		} else {
			resultMap.put("ambituslifedec", "");
		}

		return resultMap;
	}

	/**
	 * 查询酒店下所有图片信息
	 *
	 * @param hotel
	 * @return
	 */
	public Map<String, Object> readonlyHotelPics(Long hotelId) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> hotels = hotelMapper.selectPicsByHotelId(hotelId);
		List<Map<String, Object>> resultPics = new ArrayList<Map<String, Object>>();
		int count = 0;
		for (Map<String, String> hm : hotels) {
			if (hm == null)
				continue;
			List<Map<String, Object>> pics = new ArrayList<Map<String, Object>>();
			String hotelpic = hm.get("hotelpic");
			String tname = hm.get("name") + "-";
			if (StringUtils.isBlank(tname))
				tname = "";
			if (!StringUtils.isBlank(hotelpic)) {
				try {
					pics = (new Gson()).fromJson(hotelpic, List.class);
					for (Map<String, Object> pp : pics) {
						String pname = pp.get("name").toString();
						List<Map<String, Object>> urls = (List<Map<String, Object>>) pp.get("pic");
						if (urls == null)
							continue;
						for (Map<String, Object> u : urls) {
							String url = u.get("url").toString();
							if (StringUtils.isNotBlank(url)) {
								Map<String, Object> picMap = new HashMap<String, Object>();
								String nameTitle = "";
								if (count == 0) {
									nameTitle = HotelPictureEnum.getByName(pname).getTitle();
								} else {
									nameTitle = tname + RoomTypePictureEnum.getByName(pname).getTitle();
								}
								if (StringUtils.isBlank(nameTitle)) {
									nameTitle = "未知";
								}
								picMap.put("name", nameTitle);
								picMap.put("url", url);
								resultPics.add(picMap);
							}
						}
					}
				} catch (Exception e) {
					logger.error("解析酒店 {} 图片数据 {} 出错. {}", hotelId, hotelpic, e.getMessage());
				}
			}
			count++;
		}
		resultMap.put("pic", resultPics);
		return resultMap;
	}

	public int readonlyGetPicCount(Long hotelid) {
		Map<String, Object> hotelPics = this.readonlyHotelPics(hotelid);
		if (hotelPics != null) {
			List<Map<String, Object>> plist = (List<Map<String, Object>>) hotelPics.get("pic");
			return plist.size();
		}
		return 0;
	}

	/**
	 * 查询所有酒店数据，刷新redis眯客价缓存
	 */
	public void batchUpdateRedisMikePrice(final String citycode) {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				SqlSession session = sqlSessionFactory.openSession();
				THotelMapper mapper = session.getMapper(THotelMapper.class);
				List<Long> hotelIdArr = mapper.findCityHotelIds(citycode);
				for (Long hotelId : hotelIdArr) {
					updateRedisMikePrice(hotelId);
				}
			}
		});
		try {
			t.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}



	/**
	 * 更新ES中酒店眯客价。
	 *
	 * @param hotelid
	 *            参数: 酒店id
	 */
	public void updateRedisMikePrice(Long hotelid) {
		roomstateService.updateHotelMikepricesCache(hotelid, null, true);
	}

	/**
	 * 查询所有酒店数据，刷新全量眯客价到ES
	 */
	public void batchUpdateEsMikePrice() {
		final int coreNum = 10;
		ExecutorService exService = Executors.newFixedThreadPool(10);
		SqlSession session = sqlSessionFactory.openSession();
		THotelMapper mapper = session.getMapper(THotelMapper.class);

		final List<Long> hotelIdArr = mapper.findAllHotelIds();
		final int hotelCounter = hotelIdArr.size() / coreNum;
		final List<Future<?>> mkPriceFutures = new ArrayList<Future<?>>();

		if (logger.isInfoEnabled()) {
			logger.info("using multi-threading to process mkPrice indexes");
		}

		for (int i = 0; i < coreNum; i++) {
			final int index = i;
			final int currentMKIndex = i * hotelCounter;

			Future<?> mkPriceFuture = exService.submit(new Runnable() {
				private int beginIndex = currentMKIndex;

				@Override
				public void run() {
					for (int j = beginIndex; j < ((index == coreNum - 1) ? hotelIdArr.size()
							: beginIndex + hotelCounter); j++) {
						if (hotelIdArr.get(j) != null) {
							updateEsMikePrice(hotelIdArr.get(j));
							int mkPriceCounterTmp = mkPriceCounter.incrementAndGet();
							if (mkPriceCounterTmp % 100 == 0 && logger.isInfoEnabled()) {
								logger.info("{} mkPrice has been updated", mkPriceCounterTmp);
							}
						}

						if (logger.isInfoEnabled()) {
							logger.info("updating mkPrice with index {}, hotelId {}", j, hotelIdArr.get(j));
						}
					}
				}
			});
			mkPriceFutures.add(mkPriceFuture);
		}

		Thread waitThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if (logger.isInfoEnabled()) {
						logger.info("waiting for mkPrice update to complete...");
					}

					for (Future<?> mkFuture : mkPriceFutures) {
						mkFuture.get();

						if (logger.isInfoEnabled()) {
							logger.info("1 mkPrice update worker finished...");
						}
					}

					if (logger.isInfoEnabled()) {
						logger.info("all mkPrices {} have been updated...", hotelIdArr.size());
					}
				} catch (Exception e) {
					logger.error("failed to wait for mkFuture to complete...", e);
				}
			}
		});
		waitThread.start();
	}

	/**
	 * 更新ES中酒店眯客价。
	 *
	 * @param hotelid
	 *            参数: 酒店id
	 */
	public void updateEsMikePrice(Long hotelid) {
		// 由于C端在凌晨2点之前预定选房时可以选择前一天的房间，所以这里从前一天开始更新眯客价。
		Date curDate = DateUtils.addDays(new Date(), -1);
		// 将当期日期格式化为yyyyMMdd格式
		String startdate = DateUtils.getStringFromDate(curDate, DateUtils.FORMATSHORTDATETIME);
		// 默认保存多少天的眯客价
		Integer days = MIKEPRICE_DAYS;
		updateEsMikePrice(hotelid, startdate, days);
	}

	/**
	 * 更新ES中酒店眯客价: 从startdate开始, 保存days天的眯客价。
	 *
	 * @param hotelid
	 *            参数: 酒店id
	 * @param startdate
	 *            参数: 开始日期
	 * @param days
	 *            参数: 天数
	 */
	public void updateEsMikePrice(Long hotelid, String startdate, Integer days) {
		logger.info("updateEsMikePrice method begin...");
		logger.info("updateEsMikePrice method parameters hotelid:{}, startdate:{}, days:{}", hotelid, startdate, days);
		String hid = hotelid.toString();
		try {
			SearchHit[] searchHits = esProxy.searchHotelByHotelIdWithRetry(hid, 2);
			logger.info("眯客价查询到酒店个数: {}", searchHits.length);
			for (int i = 0; i < searchHits.length; i++) {
				SearchHit searchHit = searchHits[i];
				String _id = searchHit.getId();
				Map<String, Object> doc = searchHit.getSource();
				Iterator<Map.Entry<String, Object>> iter = doc.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry<String, Object> entry = iter.next();
					String key = entry.getKey();
					if (key.indexOf(MIKE_PRICE_PROP) == 0 && key.length() == MIKE_PRICE_DATE.length()) {
						// 先删除眯客价属性
						logger.info("删除眯客价属性: {}", key);
						iter.remove();
					}
				}
				for (int d = 0; d < MIKEPRICE_DAYS; d++) {
					Date mikepriceDate = DateUtils.addDays(DateUtils.getDateFromString(startdate), d);
					String startdateday = DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
					String enddateday = startdateday;
					// 取眯客价
					String[] prices = null;
					if (hotelPriceService.isUseNewPrice())
						prices = hotelPriceService.getHotelMikePrices(hotelid, startdateday, enddateday);
					else
						prices = roomstateService.getHotelMikePrices(hotelid, startdateday, enddateday);
					BigDecimal mikePriceValue = new BigDecimal(prices[0]);
					String mikePriceKey = MIKE_PRICE_PROP
							+ DateUtils.getStringFromDate(mikepriceDate, DateUtils.FORMATSHORTDATETIME);
					doc.put(mikePriceKey, mikePriceValue);
					logger.info("更新眯客价属性: {}, 值: {}", mikePriceKey, mikePriceValue);
				}
				// mike3.0增加月销量
				doc.put("ordernummon", getOrderNumMon(hotelid));
				doc.put("greetscore", getGreetScore(hotelid));
				esProxy.updateDocument(_id, doc);
				logger.info("更新酒店眯客价成功.");
			}
			logger.info("updateEsMikePrice method success.");
		} catch (Exception e) {
			logger.error("更新ES眯客价出错: " + e.getLocalizedMessage(), e);
		}
	}

	/**
	 * 获取酒店信息
	 *
	 * @param hotelid
	 * @return
	 */
	public THotelModel readonlyHotelModel(Long hotelid) {
		return thotelMapper.selectById(hotelid);
	}

	/**
	 * @param orderId
	 * @return
	 */
	public String selectCityCodeByOrderId(Long orderId) {
		return thotelMapper.selectCityCodeByOrderId(orderId);
	}

	/**
	 * 返回酒店类型
	 *
	 * @return
	 */
	public List<Map<String, String>> readonlyFindHotelTypes() {
		List<Map<String, String>> datas = new ArrayList<Map<String, String>>();
		for (HotelTypeEnum htype : HotelTypeEnum.values()) {
			Map<String, String> rm = new HashMap<String, String>();
			rm.put("id", "" + htype.getId());
			rm.put("name", htype.getTitle());
			datas.add(rm);
		}
		return datas;
	}

	private void removeObsoleteBedtypes() {

	}

	public void asyncBatchUpdateHotelBedtypes(final String citycode) {
		this.exService.submit(new Runnable() {
			@Override
			public void run() {
				Integer hitCounter = 0;

				try {
					LocalDateTime startTime = LocalDateTime.now();
					// 更新酒店床型。
					SearchRequestBuilder searchBuilder = esProxy.prepareSearch(ElasticsearchProxy.OTS_INDEX_DEFAULT,
							ElasticsearchProxy.HOTEL_TYPE_DEFAULT);

					List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
					if (!Strings.isNullOrEmpty(citycode)) {
						filterBuilders.add(FilterBuilders.termFilter("hotelcity", citycode));
					}
					filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
					FilterBuilder[] builders = new FilterBuilder[] {};

					BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
					searchBuilder.setFrom(0).setSize(10000).setExplain(true);
					searchBuilder.setPostFilter(boolFilter);

					SearchResponse searchResponse = searchBuilder.execute().actionGet();
					SearchHits searchHits = searchResponse.getHits();
					SearchHit[] hits = searchHits.getHits();
					logger.info("开始更新酒店床型...");
					for (int i = 0; i < hits.length; i++) {
						SearchHit hit = hits[i];
						Map<String, Object> result = hit.getSource();
						String hotelid = result.get("hotelid") == null ? "" : String.valueOf(result.get("hotelid"));
						logger.info("更新酒店{}床型...", hotelid);
						if (StringUtils.isBlank(hotelid)) {
							continue;
						}
						List<Map<String, Object>> bedtypes = bedTypeMapper.selectBedtypesByHotelId(hotelid);
						for (Map<String, Object> bedtype : bedtypes) {
							if (bedtype.get("bedtype") == null) {
								continue;
							}
							String field = "bedtype" + bedtype.get("bedtype");

							esProxy.updateDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT,
									ElasticsearchProxy.HOTEL_TYPE_DEFAULT, hit.getId(), field, 1);
							logger.info("酒店{}有床型{}", hotelid, bedtype.get("bedtype"));
						}
					}
					hitCounter = hits.length;
					LocalDateTime endTime = LocalDateTime.now();

					logger.info("更新酒店床型完成.{}", org.joda.time.Seconds.secondsBetween(startTime, endTime));
				} catch (Exception e) {
					logger.error("failed to update bedtypes in batch", e);
				}

				if (logger.isInfoEnabled()) {
					logger.info(String.format("counts:%s", (Integer) hitCounter));
				}
			}
		});
	}

	/**
	 * 批量更新ES酒店床型
	 *
	 * @param citycode
	 *            参数：城市编码
	 * @param token
	 *            参数：token
	 * @return
	 */
	public Map<String, Object> readonlyBatchUpdateHotelBedtype(String citycode, String token) {
		Map<String, Object> datas = Maps.newHashMap();
		try {
			long startTime = new Date().getTime();
			// 更新酒店床型。
			SearchRequestBuilder searchBuilder = esProxy.prepareSearch(ElasticsearchProxy.OTS_INDEX_DEFAULT,
					ElasticsearchProxy.HOTEL_TYPE_DEFAULT);
			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			if (!Strings.isNullOrEmpty(citycode)) {
				filterBuilders.add(FilterBuilders.termFilter("hotelcity", citycode));
			}
			filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
			searchBuilder.setFrom(0).setSize(10000).setExplain(true);
			searchBuilder.setPostFilter(boolFilter);
			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.getHits();
			logger.info("开始更新酒店床型...");
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				String hotelid = result.get("hotelid") == null ? "" : String.valueOf(result.get("hotelid"));
				logger.info("更新酒店{}床型...", hotelid);
				if (StringUtils.isBlank(hotelid)) {
					continue;
				}
				List<Map<String, Object>> bedtypes = bedTypeMapper.selectBedtypesByHotelId(hotelid);
				for (Map<String, Object> bedtype : bedtypes) {
					if (bedtype.get("bedtype") == null) {
						continue;
					}
					String field = "bedtype" + bedtype.get("bedtype");
					esProxy.updateDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.HOTEL_TYPE_DEFAULT,
							hit.getId(), field, 1);
					logger.info("酒店{}有床型{}", hotelid, bedtype.get("bedtype"));
				}
			}
			datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
			if (AppUtils.DEBUG_MODE) {
				long endTime = new Date().getTime();
				datas.put(ServiceOutput.STR_MSG_TIMES, endTime - startTime);
			}
			datas.put("counts", hits.length);
			logger.info("更新酒店床型完成.");
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		//
		return datas;
	}

	/**
	 * 批量更新ES酒店类型
	 *
	 * @param citycode
	 *            参数：城市编码
	 * @param token
	 *            参数：token
	 * @return
	 */
	public Map<String, Object> readonlyBatchUpdateHoteltype(String citycode, String token) {
		Map<String, Object> datas = Maps.newHashMap();
		try {
			long startTime = new Date().getTime();
			// 更新酒店床型。
			SearchRequestBuilder searchBuilder = esProxy.prepareSearch(ElasticsearchProxy.OTS_INDEX_DEFAULT,
					ElasticsearchProxy.HOTEL_TYPE_DEFAULT);
			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			if (!Strings.isNullOrEmpty(citycode)) {
				filterBuilders.add(FilterBuilders.termFilter("hotelcity", citycode));
			}
			filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
			searchBuilder.setFrom(0).setSize(10000).setExplain(true);
			searchBuilder.setPostFilter(boolFilter);
			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			SearchHit[] hits = searchHits.getHits();
			logger.info("开始更新酒店类型...");
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				String hotelid = result.get("hotelid") == null ? "" : String.valueOf(result.get("hotelid"));
				logger.info("更新酒店{}类型...", hotelid);
				if (StringUtils.isBlank(hotelid)) {
					continue;
				}
				THotelModel hotel = thotelMapper.selectById(Long.valueOf(hotelid));
				if (hotel == null) {
					continue;
				}
				result.put("hoteltype", hotel.getHoteltype());
				result.put("discode", hotel.getDiscode());
				result.put("hoteldis", hotel.getDisname());
				result.put("hotelprovince", hotel.getProvince());
				result.put("hotelphone", hotel.getHotelphone());
				esProxy.updateDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.HOTEL_TYPE_DEFAULT,
						hit.getId(), result);
				// esProxy.updateDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT,
				// ElasticsearchProxy.HOTEL_TYPE_DEFAULT,
				// hit.getId(), "hoteltype", hotel.getHoteltype());
				logger.info("酒店{}类型更新成功。", hotelid);
			}
			datas.put(ServiceOutput.STR_MSG_SUCCESS, true);
			if (AppUtils.DEBUG_MODE) {
				long endTime = new Date().getTime();
				datas.put(ServiceOutput.STR_MSG_TIMES, endTime - startTime);
			}
			datas.put("counts", hits.length);
			logger.info("更新酒店类型完成.");
		} catch (Exception e) {
			datas.put(ServiceOutput.STR_MSG_SUCCESS, false);
			datas.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			datas.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		//
		return datas;
	}

	/**
	 * @param citycode
	 * @return
	 */
	public Map<String, Object> readonlyClearEsHotelNotInTHotel(String citycode) {
		logger.info("clearEsHotelNotInTHotel method begin...");
		long startTime = new Date().getTime();
		Map<String, Object> rtnMap = Maps.newHashMap();
		try {
			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().mustNot(FilterBuilders.missingFilter("hotelid"));
			if (StringUtils.isNotBlank(citycode)) {
				boolFilter.must(FilterBuilders.termFilter("hotelcity", citycode));
			}
			searchBuilder.setFrom(0).setSize(10000).setExplain(true);
			searchBuilder.setPostFilter(boolFilter);
			SearchResponse searchResponse = searchBuilder.execute().actionGet();
			SearchHits searchHits = searchResponse.getHits();
			long totalHits = searchResponse.getHits().totalHits();
			logger.info("search hotel success: total {} found.", totalHits);
			SearchHit[] hits = searchHits.getHits();
			int delCount = 0;
			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				if (result.get("hotelid") == null || "".equals(result.get("hotelid"))) {
					continue;
				}
				logger.info("es doc hotelid is: {}", result.get("hotelid"));
				Long id = Long.valueOf(String.valueOf(result.get("hotelid")));
				if (id == null) {
					continue;
				}
				logger.info("HMS酒店数据搜索id: " + id);
				THotelModel hotel = thotelMapper.selectById(id);
				if (hotel == null) {
					if (String.valueOf(id).equals(String.valueOf(result.get("hotelid")))) {
						logger.error("id为{}的酒店在HMS已经不存在了.", result.get("hotelid"));
						esProxy.deleteDocument(hit.getId());
						delCount++;
						logger.error("删除了ES中的酒店: {}。", result.get("hotelid"));
					}
				}
			}
			logger.info("总共删除了: {}条无效酒店数据.", delCount);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("deletecount", delCount);
			rtnMap.put("count", hits.length);
			rtnMap.put("totalcount", totalHits);
			rtnMap.put("citycode", citycode);
			if (AppUtils.DEBUG_MODE) {
				long finishTime = new Date().getTime();
				rtnMap.put(ServiceOutput.STR_MSG_TIMES, (finishTime - startTime) + "ms");
			}
		} catch (Exception e) {
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			e.printStackTrace();
			logger.error("clearEsHotelNotInTHotel:: method error: {}", e.getLocalizedMessage());
		}
		return rtnMap;
	}

}
