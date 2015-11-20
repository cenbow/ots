package com.mk.ots.search.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.GeoDistanceFilterBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryFilterBuilder;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.script.ScriptScoreFunctionBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Event;
import com.dianping.cat.message.Message;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mk.framework.AppUtils;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.ots.common.enums.FrontPageEnum;
import com.mk.ots.common.enums.HotelSearchEnum;
import com.mk.ots.common.enums.HotelSortEnum;
import com.mk.ots.common.enums.ShowAreaEnum;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.common.utils.SearchConst;
import com.mk.ots.hotel.comm.enums.HotelPictureEnum;
import com.mk.ots.hotel.comm.enums.HotelTypeEnum;
import com.mk.ots.hotel.model.TCityModel;
import com.mk.ots.hotel.model.TDistrictModel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.service.CashBackService;
import com.mk.ots.hotel.service.CityService;
import com.mk.ots.hotel.service.HotelCollectionService;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.inner.service.IOtsAdminService;
import com.mk.ots.mapper.PositionTypeMapper;
import com.mk.ots.mapper.RoomSaleConfigMapper;
import com.mk.ots.mapper.SAreaInfoMapper;
import com.mk.ots.mapper.SLandMarkMapper;
import com.mk.ots.mapper.SSubwayMapper;
import com.mk.ots.mapper.SSubwayStationMapper;
import com.mk.ots.mapper.TDistrictMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.mapper.THotelScoreMapper;
import com.mk.ots.restful.input.HotelQuerylistReqEntity;
import com.mk.ots.restful.output.SearchPositionsCoordinateRespEntity;
import com.mk.ots.restful.output.SearchPositionsCoordinateRespEntity.Child;
import com.mk.ots.restful.output.SearchPositionsDistanceRespEntity;
import com.mk.ots.restful.output.SearchPositiontypesRespEntity;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import com.mk.ots.search.enums.PositionTypeEnum;
import com.mk.ots.search.model.PositionTypeModel;
import com.mk.ots.search.model.SAreaInfo;
import com.mk.ots.search.model.SLandMark;
import com.mk.ots.search.model.SSubway;
import com.mk.ots.search.model.SSubwayStation;
import com.mk.ots.search.service.IPromoSearchService;
import com.mk.ots.utils.DistanceUtil;
import com.mk.ots.web.ServiceOutput;

@Service
public class PromoSearchServiceImpl implements IPromoSearchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 注入ES代理服务类实例
	 */
	@Autowired
	private ElasticsearchProxy esProxy;

	/**
	 * 注入位置区域类型mapper
	 */
	@Autowired
	private PositionTypeMapper positionTypeMapper;

	/**
	 * 注入城市服务类实例
	 */
	@Autowired
	private CityService cityService;

	/**
	 * 注入酒店评分mapper
	 */
	@Autowired
	private THotelScoreMapper thotelscoreMapper;

	/**
	 * 注入酒店服务
	 */
	@Autowired
	private HotelService hotelService;

	/**
	 * 注入房态服务
	 */
	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private HotelCollectionService hotelCollectionService;

	@Autowired
	private RoomSaleConfigInfoService roomSaleConfigInfoService;

	@Autowired
	private RoomSaleService roomSaleService;

	/**
	 * 注入酒店信息mapper
	 */
	@Autowired
	private THotelMapper thotelMapper;

	/**
	 * 注入区县mapper
	 */
	@Autowired
	private TDistrictMapper tdistrictMapper;

	/**
	 * 注入返现服务
	 */
	@Autowired
	private CashBackService cashBackService;

	@Autowired
	private SearchService searchService;

	/**
	 * 注入新酒店价格服务
	 */
	@Autowired
	private HotelPriceService hotelPriceService;

	@Autowired
	private SAreaInfoMapper sareaInfoMapper;

	@Autowired
	private SLandMarkMapper slandMarkMapper;

	@Autowired
	private IOtsAdminService otsAdminService;

	@Autowired
	private SSubwayMapper subwayMapper;

	@Autowired
	private SSubwayStationMapper subwayStationMapper;

	@Autowired
	private TRoomSaleShowConfigService roomSaleShowConfigService;

	private LocalDateTime promoStartTime;
	private LocalDateTime promoEndTime;

	private final SimpleDateFormat defaultFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");

	private final int minItemCount = 5;

	@Autowired
	private RoomSaleConfigMapper roomsaleConfigMapper;

	/*
	 * 获取 区域位置类型
	 * 
	 * @param citycode
	 * 
	 * @param positiontypeid
	 */
	@Override
	public List<SearchPositiontypesRespEntity> readonlyPositionTypes(String citycode, Long positiontypeid) {
		List<SearchPositiontypesRespEntity> result = new ArrayList<SearchPositiontypesRespEntity>();
		List<PositionTypeModel> positionTypes = positionTypeMapper.findByCitycode(citycode);
		for (PositionTypeModel pm : positionTypes) {
			SearchPositiontypesRespEntity psr = new SearchPositiontypesRespEntity();
			psr.setId(pm.getId());
			psr.setTypename(pm.getTypename());
			result.add(psr);
		}
		return result;
	}

	/**
	 * 查询位置区域
	 * 
	 * @param citycode
	 * @param ptype
	 */
	@Override
	public Map<String, Object> readonlyPositions(String citycode, String ptype) {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		// 从ES中读取区域位置信息
		List<SearchPositionsCoordinateRespEntity> datas = readonlyPositionsFromES(citycode, ptype);
		rtnMap.put("datas", datas);

		List<SearchPositionsDistanceRespEntity> distances = Lists.newArrayList();
		SearchPositionsDistanceRespEntity ds = new SearchPositionsDistanceRespEntity();
		ds.setName("附近1km");
		ds.setValue(1000L);
		distances.add(ds);

		ds = new SearchPositionsDistanceRespEntity();
		ds.setName("附近3km");
		ds.setValue(3000L);
		distances.add(ds);

		ds = new SearchPositionsDistanceRespEntity();
		ds.setName("附近5km");
		ds.setValue(5000L);
		distances.add(ds);

		ds = new SearchPositionsDistanceRespEntity();
		ds.setName("附近10km");
		ds.setValue(10000L);
		distances.add(ds);

		ds = new SearchPositionsDistanceRespEntity();
		ds.setName("全城");
		ds.setValue(0L);
		distances.add(ds);

		rtnMap.put("distance", distances);

		return rtnMap;
	}

	/**
	 * 
	 * @param cityId
	 * @param promoId
	 * @return
	 */
	public Integer queryByPromoId(Integer promoId) throws Exception {
		try {
			List<TRoomSaleConfigInfo> promos = roomSaleConfigInfoService.queryListBySaleTypeId("", promoId, 0, 10);

			if (promos != null && promos.size() > 0) {
				return promos.get(0).getId();
			} else {
				return -1;
			}
		} catch (Exception ex) {
			throw new Exception(String.format("failed to query promotype by promoId %s", promoId), ex);
		}
	}

	/**
	 * 校验酒店搜索日期
	 * 
	 * @param startDate
	 *            参数：查询开始日期
	 * @param endDate
	 *            参数：查询截止日期
	 * @return String 返回值
	 */
	private String validateSearchDate(Date startDate, Date endDate) {
		// 说明：凌晨2点之前预定，可以预定前1天的酒店
		String validateStr = "";
		try {
			Date curDate = new Date();
			// 校验入住日期
			int diffDays = DateUtils.diffDay(startDate, curDate);
			if (diffDays > 1) {
				// 入住日期不能早于当前日期超过1天。
				validateStr = "入住日期不正确.";
				return validateStr;
			}
			diffDays = DateUtils.diffDay(DateUtils.addDays(curDate, SearchConst.SEARCH_DAYS_MAX), startDate);
			if (diffDays > 0) {
				// 入住日期不能晚于当前日期+最大搜索天数
				validateStr = "入住日期不正确.";
				return validateStr;
			}
			// 校验离店日期
			// 入住日期最早只能在凌晨2点的情况下，可以预定前1天的，所以离店日期最早只能是当天。
			diffDays = DateUtils.diffDay(endDate, curDate);
			if (diffDays > 0) {
				validateStr = "离店日期不正确.";
				return validateStr;
			}
			diffDays = DateUtils.diffDay(startDate, endDate);
			if (diffDays > SearchConst.SEARCH_DAYS_MAX) {
				validateStr = "最多只能搜索" + SearchConst.SEARCH_DAYS_MAX + "天的酒店.";
				return validateStr;
			}
		} catch (Exception e) {
			logger.error("failed to validateSearchDate", e);
			validateStr = e.getLocalizedMessage();
		}

		return validateStr;
	}

	private String validateSearchHome(HotelQuerylistReqEntity params) {
		String validateStr = "";
		try {
			String startdateday = params.getStartdateday();
			if (StringUtils.isBlank(startdateday)) {
				validateStr = "无效的入住日期.";
				return validateStr;
			}
			String enddateday = params.getEnddateday();
			if (StringUtils.isBlank(enddateday)) {
				validateStr = "无效的离店日期.";
				return validateStr;
			}
			if (startdateday.equals(enddateday)) {
				validateStr = "入住和离店不能是同一天.";
				return validateStr;
			}
			// 用户坐标经纬度值没有,先判断屏幕坐标经纬度值，有的话用屏幕坐标经纬度，没有默认上海市中心位置
			if (params.getUserlongitude() == null) {
				return "用户userlongitude必须传入";
			}
			if (params.getUserlatitude() == null) {
				return "用户userlatitude必须传入";
			}
			if (StringUtils.isBlank(params.getCityid())) {
				return "用户cityid必须传入";
			}
			if (StringUtils.isBlank(params.getCallversion())) {
				return "callversion必须传入";
			}
			if (StringUtils.isNotBlank(params.getCallversion()) && ("3.1".compareTo(params.getCallversion()) >= 0)) {
				return "callversion版本不正确";
			}
			if (StringUtils.isNotEmpty(params.getCallmethod()) && "3".equalsIgnoreCase(params.getCallmethod())) {
				return "callmethod为wechat";
			}
			if ((params.getCallentry() != null) && (params.getCallentry() == 1 || params.getCallentry() == 2)) {
				return "callentry为摇一摇或切客";
			}

			Date startDate = DateUtils.getDateFromString(params.getStartdateday());
			Date endDate = DateUtils.getDateFromString(params.getEnddateday());
			validateStr = this.validateSearchDate(startDate, endDate);
			return validateStr;
		} catch (Exception e) {
			logger.error("search hotel validation failed", e);
			validateStr = e.getLocalizedMessage();
		}

		return validateStr;
	}

	/**
	 * 酒店搜索校验
	 * 
	 * @param params
	 *            参数：接口请求入参对象
	 * @return String 返回值
	 */
	private String getValidateSearchHotels(HotelQuerylistReqEntity params) {
		String validateStr = "";
		try {
			String startdateday = params.getStartdateday();
			if (StringUtils.isBlank(startdateday)) {
				validateStr = "无效的入住日期.";
				return validateStr;
			}
			String enddateday = params.getEnddateday();
			if (StringUtils.isBlank(enddateday)) {
				validateStr = "无效的离店日期.";
				return validateStr;
			}
			if (startdateday.equals(enddateday)) {
				validateStr = "入住和离店不能是同一天.";
				return validateStr;
			}
			// 用户坐标经纬度值没有,先判断屏幕坐标经纬度值，有的话用屏幕坐标经纬度，没有默认上海市中心位置
			if (params.getUserlongitude() == null) {
				return "用户userlongitude必须传入";
			}
			if (params.getUserlatitude() == null) {
				return "用户userlatitude必须传入";
			}
			if (StringUtils.isBlank(params.getCityid())) {
				return "用户cityid必须传入";
			}

			Date startDate = DateUtils.getDateFromString(params.getStartdateday());
			Date endDate = DateUtils.getDateFromString(params.getEnddateday());
			validateStr = this.validateSearchDate(startDate, endDate);
			return validateStr;
		} catch (Exception e) {
			logger.error("search hotel validation failed", e);
			validateStr = e.getLocalizedMessage();
		}

		return validateStr;
	}

	@Override
	public Map<String, Object> searchThemes(HotelQuerylistReqEntity reqentity) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();

		try {
			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			List<FilterBuilder> keywordBuilders = new ArrayList<FilterBuilder>();

			// C端搜索分类
			Integer searchType = reqentity.getSearchtype();
			if (searchType == null) {
				searchType = HotelSearchEnum.ALL.getId();
			}

			// 如果城市id 为空则默认设置为上海
			String cityid = reqentity.getCityid();

			String hotelid = reqentity.getHotelid();

			if (logger.isInfoEnabled()) {
				logger.info(String.format("about to search for cityid: %s; hotelid: %s", cityid, hotelid));
			}

			int page = reqentity.getPage().intValue();
			int limit = reqentity.getLimit().intValue();

			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

			// make term filter builder
			this.makeTermFilter(reqentity, filterBuilders);

			if (StringUtils.isNotBlank(reqentity.getPromotype())) {
				filterBuilders.add(FilterBuilders
						.queryFilter(QueryBuilders.matchQuery("promoinfo.promotype", reqentity.getPromotype())));
			}

			filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("isonpromo", "1")));

			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));

			// make range filter builder
			List<FilterBuilder> mikePriceBuilders = this.makeMikePriceRangeFilter(reqentity);

			if (mikePriceBuilders.size() > 0) {
				BoolFilterBuilder mikePriceBoolFilter = FilterBuilders.boolFilter();
				mikePriceBoolFilter.should(mikePriceBuilders.toArray(builders));
				boolFilter.must(mikePriceBoolFilter);
			}
			if (AppUtils.DEBUG_MODE) {
				logger.info("boolFilter is : \n{}", boolFilter.toString());
			}

			if (StringUtils.isNotBlank(reqentity.getKeyword())) {
				makeKeywordFilter(reqentity, keywordBuilders);
				Cat.logEvent("HotKeywords", reqentity.getKeyword(), Message.SUCCESS, "");
			}

			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("visible", Constant.STR_TRUE))
					.must(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
			boolFilter.must(FilterBuilders.queryFilter(boolQueryBuilder));
			searchBuilder.setPostFilter(boolFilter);

			if (keywordBuilders.size() > 0) {
				FilterBuilder[] arrKeywordBuilders = new FilterBuilder[] {};
				boolFilter.should(keywordBuilders.toArray(arrKeywordBuilders));
			}

			Integer paramOrderby = reqentity.getOrderby();
			if (paramOrderby == null) {
				paramOrderby = 0;
			}

			String startdateday = reqentity.getStartdateday();
			String enddateday = reqentity.getEnddateday();
			List<String> mkPriceDateList = this.getMikepriceDateList(startdateday, enddateday);
			this.setScoreScriptSort(searchBuilder, boolFilter,
					new GeoPoint(reqentity.getUserlatitude(), reqentity.getUserlongitude()), mkPriceDateList);

			searchBuilder.setFrom((page - 1) * limit).setSize(limit).setExplain(true);

			logger.info(searchBuilder.toString());

			SearchResponse searchResponse = searchBuilder.execute().actionGet();

			SearchHits searchHits = searchResponse.getHits();
			long totalHits = searchHits.totalHits();

			if (StringUtils.isNotBlank(reqentity.getKeyword()) && (totalHits == 0D)) {
				Cat.logEvent("MismatchKeywords", reqentity.getKeyword(), Message.SUCCESS, "");
			}

			if (logger.isInfoEnabled()) {
				logger.info("about to reorderSearchResults");
			}

			List<Map<String, Object>> searchResults = this.reorderSearchResults(searchHits.getHits(), reqentity);

			logger.info("search hotel success: total {} found. current pagesize:{}", totalHits,
					searchResults != null ? searchResults.size() : 0);

			rtnMap.put("hotel", searchResults);
			rtnMap.put("count", searchResults.size());
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, "true");
		} catch (Exception e) {
			logger.error("failed to readonlyOtsHotelListFromEsStore...", e);

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, "false");
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}

		rtnMap.put("supplementhotel", new ArrayList<Map<String, Object>>());

		return rtnMap;
	}

	private boolean isThemed(Integer hotelId, Map<String, Object> roomtype) {
		try {
			TRoomSaleConfig config = roomsaleConfigMapper
					.queryRoomSaleConfigByType(((Long) roomtype.get("roomtypeid")).intValue());
			if (config != null) {
				return true;
			}
		} catch (Exception ex) {
			logger.warn("failed to queryRoomSaleConfigByType with hotelId", ex);
			return false;
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> groupThemes(List<Map<String, Object>> searchResults) {
		Map<Integer, Queue<Map<String, Object>>> hotelRoomTypes = new HashMap<Integer, Queue<Map<String, Object>>>();
		List<Map<String, Object>> themeGrouped = new ArrayList<Map<String, Object>>();

		Integer counter = 0;
		for (Map<String, Object> hotel : searchResults) {
			List<Map<String, Object>> roomtypes = (List<Map<String, Object>>) hotel.get("roomtype");
			Integer hotelId = Integer.parseInt((String) hotel.get("hotelid"));
			String hotelname = (String) hotel.get("hotelname");

			if (!hotelRoomTypes.containsKey(hotelId)) {
				hotelRoomTypes.put(hotelId, new ArrayBlockingQueue<Map<String, Object>>(10));
			}

			for (Map<String, Object> roomtype : roomtypes) {
				if (!roomtype.containsKey("hotelname")) {
					roomtype.put("hotelname", hotelname);
					roomtype.put("hotelId", hotelId);
				}

				if (isThemed(hotelId, roomtype)) {
					if (!hotelRoomTypes.get(hotelId).contains(roomtype)) {
						hotelRoomTypes.get(hotelId).offer(roomtype);
						counter++;
					}
				}
			}
		}

		Iterator<Queue<Map<String, Object>>> roomTypeQueues = hotelRoomTypes.values().iterator();
		List<Queue<Map<String, Object>>> roomTypeQueueList = new ArrayList<Queue<Map<String, Object>>>();
		while (roomTypeQueues.hasNext()) {
			Queue<Map<String, Object>> roomTypeQueue = roomTypeQueues.next();
			if (roomTypeQueue != null) {
				roomTypeQueueList.add(roomTypeQueue);
			}
		}

		AtomicInteger curPos = new AtomicInteger(0);

		for (int i = 0; i < counter; i++) {
			Map<String, Object> roomtype = pollRoomtype(roomTypeQueueList, curPos);

			if (roomtype != null) {
				themeGrouped.add(roomtype);
			}
			/**
			 * no roomtypes existed any more
			 */
			else {
				if (i < counter) {
					logger.warn("roomtype is leaked somehow...");
				}
				break;
			}
		}

		return themeGrouped;
	}

	private Map<String, Object> pollRoomtype(List<Queue<Map<String, Object>>> roomTypeQueue, AtomicInteger curPos) {
		Map<String, Object> roomtype = null;

		for (int i = curPos.get(); i < roomTypeQueue.size(); i++) {
			if (!roomTypeQueue.get(i).isEmpty()) {
				roomtype = roomTypeQueue.get(i).poll();
			}

			if (roomtype != null) {
				curPos.set(i);
				break;
			}
		}

		if (roomtype == null) {
			for (int i = 0; i < roomTypeQueue.size(); i++) {
				if (!roomTypeQueue.get(i).isEmpty()) {
					roomtype = roomTypeQueue.get(i).poll();
				}

				if (roomtype != null) {
					curPos.set(i);
					break;
				}
			}
		}

		return roomtype;
	}

	private Map<String, Object> distanceQueryMap(HotelQuerylistReqEntity hotelEntity) {
		// 最近酒店

		hotelEntity.setOrderby(HotelSortEnum.DISTANCE.getId());

		if (hotelEntity.getPillowlatitude() != null || hotelEntity.getPillowlongitude() != null) {
			hotelEntity.setPillowlatitude(hotelEntity.getUserlatitude());
			hotelEntity.setPillowlongitude(hotelEntity.getUserlongitude());
		}

		hotelEntity.setIspromoonly(false);

		Map<String, Object> distanceResultMap = searchService.readonlySearchHotels(hotelEntity);

		Integer normalid = HotelSortEnum.DISTANCE.getId();
		RoomSaleShowConfigDto roomSaleShowConfigDto = new RoomSaleShowConfigDto();
		roomSaleShowConfigDto.setCityid(hotelEntity.getCityid());
		roomSaleShowConfigDto.setIsSpecial(Constant.STR_FALSE);
		roomSaleShowConfigDto.setShowArea(ShowAreaEnum.FrontPageCentre.getCode());
		roomSaleShowConfigDto.setNormalId(normalid);

		RoomSaleShowConfigDto defaultShowConfig = new RoomSaleShowConfigDto();
		defaultShowConfig.setPromotext(Constant.DEFAULT_NORMAL_CHEAPEST_TEXT);
		defaultShowConfig.setPromonote(Constant.DEFAULT_NORMAL_CHEAPEST_NOTE);
		defaultShowConfig.setPromoicon(Constant.DEFAULT_NORMAL_CHEAPEST_ICON);

		distanceResultMap.put("normalid", normalid);

		Map<String, Object> resultMap = renderNormalItem(distanceResultMap, roomSaleShowConfigDto, defaultShowConfig);

		return resultMap;
	}

	private Map<String, Object> cheapestQueryMap(HotelQuerylistReqEntity hotelEntity) {
		// 最便宜
		hotelEntity.setOrderby(HotelSortEnum.PRICE.getId());
		hotelEntity.setIspromoonly(false);

		Map<String, Object> priceResultMap = searchService.readonlySearchHotels(hotelEntity);

		Integer normalid = HotelSortEnum.PRICE.getId();
		RoomSaleShowConfigDto roomSaleShowConfigDto = new RoomSaleShowConfigDto();
		roomSaleShowConfigDto.setCityid(hotelEntity.getCityid());
		roomSaleShowConfigDto.setIsSpecial(Constant.STR_FALSE);
		roomSaleShowConfigDto.setShowArea(ShowAreaEnum.FrontPageCentre.getCode());
		roomSaleShowConfigDto.setNormalId(normalid);

		RoomSaleShowConfigDto defaultShowConfig = new RoomSaleShowConfigDto();
		defaultShowConfig.setPromotext(Constant.DEFAULT_NORMAL_CHEAPEST_TEXT);
		defaultShowConfig.setPromonote(Constant.DEFAULT_NORMAL_CHEAPEST_NOTE);
		defaultShowConfig.setPromoicon(Constant.DEFAULT_NORMAL_CHEAPEST_ICON);

		priceResultMap.put("normalid", normalid);

		Map<String, Object> resultMap = renderNormalItem(priceResultMap, roomSaleShowConfigDto, defaultShowConfig);

		return resultMap;
	}

	private Map<String, Object> popularityQueryMap(HotelQuerylistReqEntity hotelEntity) {
		// 最高人气

		hotelEntity.setOrderby(HotelSortEnum.ORDERNUMS.getId());
		hotelEntity.setIspromoonly(false);

		Map<String, Object> orderNumResultMap = searchService.readonlySearchHotels(hotelEntity);

		Integer normalid = HotelSortEnum.ORDERNUMS.getId();
		RoomSaleShowConfigDto roomSaleShowConfigDto = new RoomSaleShowConfigDto();
		roomSaleShowConfigDto.setCityid(hotelEntity.getCityid());
		roomSaleShowConfigDto.setIsSpecial(Constant.STR_FALSE);
		roomSaleShowConfigDto.setShowArea(ShowAreaEnum.FrontPageCentre.getCode());
		roomSaleShowConfigDto.setNormalId(normalid);

		orderNumResultMap.put("normalid", normalid);

		RoomSaleShowConfigDto defaultShowConfig = new RoomSaleShowConfigDto();
		defaultShowConfig.setPromotext(Constant.DEFAULT_NORMAL_POPULARITY_TEXT);
		defaultShowConfig.setPromonote(Constant.DEFAULT_NORMAL_POPULARITY_NOTE);
		defaultShowConfig.setPromoicon(Constant.DEFAULT_NORMAL_POPULARITY_ICON);

		Map<String, Object> resultMap = renderNormalItem(orderNumResultMap, roomSaleShowConfigDto, defaultShowConfig);

		return resultMap;
	}

	private Map<String, Object> renderNormalItem(Map<String, Object> resultMap,
			RoomSaleShowConfigDto roomSaleShowConfigDto, RoomSaleShowConfigDto defaultShowConfig) {

		List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService
				.queryRoomSaleShowConfigByParams(roomSaleShowConfigDto);

		resultMap.put("promoid", -1);

		if (showConfigs != null && showConfigs.size() > 0) {
			RoomSaleShowConfigDto normalShowConfig = showConfigs.get(0);
			resultMap.put("promotext", normalShowConfig.getPromotext());
			resultMap.put("promonote", normalShowConfig.getPromonote());
			resultMap.put("promoicon", normalShowConfig.getPromoicon());

		} else if (defaultShowConfig != null) {

			resultMap.put("promonote", defaultShowConfig.getPromonote());
			resultMap.put("promoicon", defaultShowConfig.getPromoicon());
			resultMap.put("promotext", defaultShowConfig.getPromotext());
		}
		return resultMap;

	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchHomeNormals(HotelQuerylistReqEntity params) throws Exception {
		// 酒店搜索校验: 开始
		String validateStr = this.validateSearchHome(params);
		if (StringUtils.isNotBlank(validateStr)) {
			logger.error("invalid parameters {}", validateStr);
			throw new Exception(String.format("invalid paramters %s", validateStr));
		}
		// 酒店搜索校验: 结束

		List<Map<String, Object>> normallist = new ArrayList<Map<String, Object>>();
		try {
			RoomSaleShowConfigDto showConfig = new RoomSaleShowConfigDto();
			showConfig.setPromoid(-1);
			showConfig.setIsSpecial(Constant.STR_FALSE);
			showConfig.setShowArea(ShowAreaEnum.FrontPageCentre.getCode());

			List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService
					.queryRoomSaleShowConfigByParams(showConfig);
			for (RoomSaleShowConfigDto showConfigDto : showConfigs) {
				Map<String, Object> normalItem = createNormalItem(params, showConfigDto.getNormalId());
				if (normalItem != null && normalItem.get("hotel") != null
						&& ((List<Map<String, Object>>) normalItem.get("hotel")).size() > 0) {
					normallist.add(normalItem);
				}
			}

			return normallist;
		} catch (Exception e) {
			throw new Exception("failed to searchHomeNormals", e);
		}
	}

	private Map<String, Object> createNormalItem(HotelQuerylistReqEntity params, Integer normalId) throws Exception {

		Map<String, Object> resultMap = new HashMap<>();

		if (HotelSortEnum.DISTANCE.getId() == normalId) {
			resultMap = distanceQueryMap(params);
		} else if (HotelSortEnum.PRICE.getId() == normalId) {
			resultMap = cheapestQueryMap(params);
		} else if (HotelSortEnum.ORDERNUMS.getId() == normalId) {
			resultMap = popularityQueryMap(params);
		}

		return resultMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> createPromoItem(HotelQuerylistReqEntity params, RoomSaleShowConfigDto showConfig)
			throws Exception {
		Map<String, Object> promoItem = new HashMap<String, Object>();

		params.setIspromoonly(Boolean.TRUE);
		params.setLimit(FrontPageEnum.limit.getId());
		params.setCallentry(null);
		params.setPromoid(String.valueOf(showConfig.getPromoid()));

		Integer promoId = showConfig.getPromoid();
		Integer normalId = showConfig.getNormalId();

		Map<String, Object> rtnMap = null;
		if (promoId > 0) {
			rtnMap = this.readonlyOtsHotelListFromEsStore(params);
		} else if (normalId > 0) {
			rtnMap = this.createNormalItem(params, normalId);
		}

		List<Map<String, Object>> hotels = (List<Map<String, Object>>) rtnMap.get("hotel");
		if (hotels != null && hotels.size() >= FrontPageEnum.limit.getId()) {
			promoItem.put("hotel", hotels);
		} else if (hotels != null && hotels.size() < FrontPageEnum.limit.getId()) {
			Map<String, Object> sups = new HashMap<String, Object>();
			searchAround(sups, params, FrontPageEnum.limit.getId() - hotels.size());
			List<Map<String, Object>> supplementHotels = (List<Map<String, Object>>) sups.get("supplementhotel");
			promoItem.put("hotel", new ArrayList<Map<String, Object>>());
			((List<Map<String, Object>>) promoItem.get("hotel"))
					.addAll(hotels == null ? new ArrayList<Map<String, Object>>() : hotels);
			((List<Map<String, Object>>) promoItem.get("hotel"))
					.addAll(supplementHotels == null ? new ArrayList<Map<String, Object>>() : supplementHotels);
		}

		promoItem.put("promoicon", showConfig.getPromoicon());
		promoItem.put("promotext", showConfig.getPromotext());
		promoItem.put("promonote", showConfig.getPromonote());
		promoItem.put("promoid", showConfig.getPromoid());
		promoItem.put("normalid", showConfig.getNormalId());

		return promoItem;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> searchHomePromos(HotelQuerylistReqEntity params) throws Exception {
		// 酒店搜索校验: 开始
		String validateStr = this.validateSearchHome(params);
		if (StringUtils.isNotBlank(validateStr)) {
			logger.error("invalid parameters {}", validateStr);
			throw new Exception(String.format("invalid paramters %s", validateStr));
		}
		// 酒店搜索校验: 结束

		List<Map<String, Object>> promolist = new ArrayList<Map<String, Object>>();
		try {
			RoomSaleShowConfigDto showConfig = new RoomSaleShowConfigDto();
			showConfig.setIsSpecial("T");

			List<RoomSaleShowConfigDto> showConfigs = roomSaleShowConfigService.queryRenderableShows(showConfig);
			for (RoomSaleShowConfigDto showConfigDto : showConfigs) {
				Map<String, Object> promoItem = createPromoItem(params, showConfigDto);
				if (promoItem != null && promoItem.get("hotel") != null
						&& ((List<Map<String, Object>>) promoItem.get("hotel")).size() > 0) {
					promolist.add(promoItem);
				}
			}

			return promolist;
		} catch (Exception e) {
			throw new Exception("failed to searchHomePromos", e);
		}
	}

	/**
	 * 酒店搜索
	 */
	@Override
	public Map<String, Object> readonlySearchHotels(HotelQuerylistReqEntity params) {
		Map<String, Object> rtnMap = Maps.newHashMap();

		// 酒店搜索校验: 开始
		String validateStr = this.getValidateSearchHotels(params);
		if (StringUtils.isNotBlank(validateStr)) {
			logger.error("readonlySearchHotels:: method error: {}", validateStr);
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, validateStr);
			return rtnMap;
		}
		// 酒店搜索校验: 结束

		if (StringUtils.isBlank(params.getHotelid())) {
			// 必填参数默认值处理：开始
			if (StringUtils.isBlank(params.getCityid())) {
				params.setCityid(Constant.STR_CITYID_SHANGHAI);
			}
			// 用户坐标经纬度值没有,先判断屏幕坐标经纬度值，有的话用屏幕坐标经纬度，没有默认上海市中心位置
			if (params.getUserlongitude() == null) {
				if (params.getPillowlongitude() == null) {
					params.setUserlongitude(Constant.LON_SHANGHAI);
				} else {
					params.setUserlongitude(params.getPillowlongitude());
				}
			}
			if (params.getUserlatitude() == null) {
				if (params.getPillowlatitude() == null) {
					params.setUserlatitude(Constant.LAT_SHANGHAI);
				} else {
					params.setUserlatitude(params.getPillowlatitude());
				}
			}
			if (params.getPage() == null || params.getPage() <= 0) {
				params.setPage(SearchConst.SEARCH_PAGE_DEFAULT);
			}
			if (params.getLimit() == null || params.getLimit() <= 0) {
				params.setLimit(SearchConst.SEARCH_LIMIT_DEFAULT);
			}

			// 眯客3.0：搜索酒店周边的酒店
			if (StringUtils.isNotBlank(params.getExcludehotelid())) {
				// 如果是酒店周边搜索，默认搜索半径为5000米
				if (params.getRange() == null || params.getRange() <= 0) {
					params.setRange(SearchConst.SEARCH_RANGE_DEFAULT);
				}
			} else {
				if (params.getRange() == null || params.getRange() <= 0) {
					params.setRange(SearchConst.SEARCH_RANGE_MAX);
				}
			}
			// 必填参数默认值处理：结束
		}
		try {
			// search ots hotel data form es
			if (StringUtils.isBlank(params.getHotelid())) {
				logger.info("未指定参数hotelid，搜索酒店数据开始...");
				// 眯客3.0：联想搜索列表中选择酒店名称进行搜索
				if (StringUtils.isBlank(params.getKeyword())) {
					// 如果C端没有传keyword参数，判断搜索类型是否按照酒店名或酒店地址
					this.searchTypeFilter(params);
				}
				rtnMap = this.readonlyOtsHotelListFromEsStore(params);
				logger.info(String.format("未指定参数hotelid，搜索酒店数据结束. size:%s", rtnMap != null ? rtnMap.size() : 0));
			} else {
				logger.info("指定hotelid，返回酒店数据开始...");
				rtnMap = this.readonlyOtsHotelFromEsStore(params);
				logger.info("指定hotelid，返回酒店数据开始...");
			}
			rtnMap.put("success", true);
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
	 * 
	 * @param points
	 * @return
	 */
	private GeoPoint getPoint(String points) {
		if (StringUtils.isBlank(points) || "[[]]".equals(points.replaceAll(" ", ""))) {
			return null;
		}
		GeoPoint point = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			ArrayList<ArrayList<Double>> list = objectMapper.readValue(points, ArrayList.class);
			if (list.size() > 0) {
				ArrayList<Double> data = list.get(0);
				if (data.size() == 2) {
					point = new GeoPoint(data.get(1), data.get(0));
				}
			}
		} catch (Exception e) {
			logger.error("错误的坐标数据：{}", points);
		}
		return point;
	}

	/**
	 * 
	 * @param params
	 */
	private void resetSearchPoint(HotelQuerylistReqEntity params) {
		GeoPoint searchPoint = this.getPoint(params.getPoints());
		if (searchPoint != null) {
			double slat = searchPoint.getLat();
			double slon = searchPoint.getLon();
			params.setPillowlatitude(slat);
			params.setPillowlongitude(slon);
		}
	}

	/**
	 * 
	 * @param params
	 */
	private void searchTypeFilter(HotelQuerylistReqEntity params) {
		Integer searchType = params.getSearchtype();
		// 如果C端没有传keyword参数，判断搜索类型是否按照酒店名或酒店地址
		if (searchType != null) {
			//
			if (HotelSearchEnum.HNAME.getId().equals(searchType) || HotelSearchEnum.HADDR.getId().equals(searchType)) {
				String posname = params.getPosname();
				if (StringUtils.isNotBlank(posname)) {
					params.setKeyword(posname);
				}
			} else if (HotelSearchEnum.NEAR.getId().equals(searchType)) {
				// 搜索附近xxxkm的酒店
			} else if (HotelSearchEnum.BZONE.getId().equals(searchType)) {
				// 按商圈搜索酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.AIRPORT.getId().equals(searchType)) {
				// 搜索机场车站附近的酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.SUBWAY.getId().equals(searchType)) {
				// 搜索地铁线路附近的酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.AREA.getId().equals(searchType)) {
				// 搜索行政区的酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.SAREA.getId().equals(searchType)) {
				// 搜索景点附近的酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.HOSPITAL.getId().equals(searchType)) {
				// 搜索医院附近的酒店
				resetSearchPoint(params);
			} else if (HotelSearchEnum.COLLEGE.getId().equals(searchType)) {
				// 搜索高校附近的酒店
				resetSearchPoint(params);
			} else {
				// 不限
			}
		}
	}

	/**
	 * 查询指定酒店
	 * 
	 * @param reqentity
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> readonlyOtsHotelFromEsStore(HotelQuerylistReqEntity reqentity) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {
			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();

			ObjectMapper objectMapper = new ObjectMapper();
			logger.info("getOtsHotel method params: {}\n", objectMapper.writeValueAsString(reqentity));

			String hotelid = reqentity.getHotelid();
			String promotype = reqentity.getPromotype();

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
					reqentity.setRange(cityRange.intValue());
				}
			}

			// 用户经纬度坐标
			double userlat = reqentity.getUserlatitude() == null ? cityLat_default : reqentity.getUserlatitude();
			double userlon = reqentity.getUserlongitude() == null ? cityLon_default : reqentity.getUserlongitude();

			// 屏幕地图经纬度坐标
			double lat = reqentity.getPillowlatitude() == null ? cityLat_default : reqentity.getPillowlatitude();
			double lon = reqentity.getPillowlongitude() == null ? cityLon_default : reqentity.getPillowlongitude();

			List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			// 如果输入参数中有hotelid，忽略其它过滤条件
			filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));

			// 是否签约 值为T，则只返回签约酒店，值为F或空，返回所有酒店
			// 从眯客2.1开始，只返回签约酒店
			filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
			FilterBuilder[] builders = new FilterBuilder[] {};
			BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
			// 只显示上线酒店和在线酒店
			BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
					.must(QueryBuilders.matchQuery("visible", Constant.STR_TRUE))
					.must(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
			boolFilter.must(FilterBuilders.queryFilter(boolQueryBuilder));

			/**
			 * added in mike3.1
			 */
			this.makePromoFilter(reqentity, filterBuilders);

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
				// 眯客2.2.1, 根据屏幕坐标计算距离.
				double hotelDistance = DistanceUtil.distance(lon, lat, hotelLongitude, hotelLatitude);
				result.put("distance", hotelDistance);
				Long sales = Long
						.valueOf(String.valueOf(result.get("ordernummon") == null ? "0" : result.get("ordernummon")));
				result.put("ordernummon", (sales >= 10 ? "月销" + sales + "单" : ""));

				// 眯客3.0增加userdistance属性：用户坐标与酒店坐标的距离
				double userDistance = DistanceUtil.distance(hotelLongitude, hotelLatitude, userlon, userlat);
				// 选择地标搜索(机场车站、地铁线路、景点、医院、高校)，按照用户坐标和地标坐标计算距离
				// C端搜索分类
				Integer searchType = reqentity.getSearchtype();
				if (searchType == null) {
					searchType = HotelSearchEnum.ALL.getId();
				}
				if (HotelSearchEnum.AIRPORT.getId().equals(searchType)
						|| HotelSearchEnum.SUBWAY.getId().equals(searchType)
						|| HotelSearchEnum.SAREA.getId().equals(searchType)
						|| HotelSearchEnum.HOSPITAL.getId().equals(searchType)
						|| HotelSearchEnum.COLLEGE.getId().equals(searchType)) {
					String points = reqentity.getPoints();
					if (StringUtils.isEmpty(points)) {
						logger.error("按照{}搜索时points参数错误{}", HotelSearchEnum.getById(searchType).getName(), points);
					}
					GeoPoint point = this.getPoint(points);
					if (point != null) {
						// 地标经纬度坐标
						double marklat = point.getLat();
						double marklon = point.getLon();
						userDistance = DistanceUtil.distance(hotelLongitude, hotelLatitude, marklon, marklat);
					} else {
						userDistance = 0;
						logger.error("按照{}搜索是没有获取到经纬度, points: {}。", HotelSearchEnum.getById(searchType).getName(),
								points);
					}
				}
				result.put("userdistance", userDistance);

				// 从ES中查到酒店数据后，根据接口参数做进一步处理
				try {
					queryTransferData(result, reqentity);
				} catch (Exception e) {
					logger.error(String.format("failed to queryTransferData with hotelid %s..., ignore and continue...",
							reqentity.getHotelid()), e);
				}
				String p_isnewpms = Constant.STR_TRUE.equals(result.get("isnewpms")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;

				if (StringUtils.isNotBlank(promotype)) {
					Integer vacants = hotelService.calPromoVacants(Integer.parseInt(promotype), Long.parseLong(hotelid),
							reqentity.getStartdateday(), reqentity.getEnddateday(), p_isnewpms);
					result.put("roomvacancy", vacants);
				} else {
					logger.warn(String.format("promotype is blank on hotelid %s", hotelid));
				}

				// 添加到酒店列表
				hotels.add(result);

				// 记录离线埋点
				if (!Constant.STR_TRUE.equals(result.get("online"))) {
					logger.info("记录离线埋点:{}", reqentity.toString());
					if (Constant.STR_TRUE.equals(result.get("isnewpms"))) {
						Cat.logEvent("ROOMSTATE", "pmsOffLine-2.0", Event.SUCCESS, "");
					} else {
						Cat.logEvent("ROOMSTATE", "pmsOffLine-1.0", Event.SUCCESS, "");
					}
				}
			}

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("count", hotels.size());
			rtnMap.put("hotel", hotels);
		} catch (Exception e) {
			e.printStackTrace();
			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return rtnMap;
	}

	private void resortPromo(List<Map<String, Object>> hotels) {
		List<Map<String, Object>> datasVC = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> datasNVC = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> data : hotels) {
			Integer roomVacancy = (Integer) data.get("roomvacancy");
			if (roomVacancy != null && roomVacancy == 0) {
				datasNVC.add(data);
			} else {
				datasVC.add(data);
			}
		}

		hotels.clear();
		hotels.addAll(datasVC);
		hotels.addAll(datasNVC);
	}

	/**
	 * 
	 * @param searchBuilder
	 * @param boolFilter
	 * @param mkPriceDateList
	 */
	private void setMikepriceScriptSort(SearchRequestBuilder searchBuilder, BoolFilterBuilder boolFilter,
			List<String> mkPriceDateList) {
		// 脚本排序功能开始
		ScriptScoreFunctionBuilder scriptSortBuilder = new ScriptScoreFunctionBuilder();
		scriptSortBuilder.script("mikeprice-sort");
		// 眯客价属性列表
		scriptSortBuilder.param("mkPriceDates", mkPriceDateList);
		logger.info("mike price scriptSortBuilder is :\n" + scriptSortBuilder.toString());
		FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(boolFilter, scriptSortBuilder);
		searchBuilder.setQuery(queryBuilder);
		// 脚本排序功能结束
	}

	/**
	 * 
	 * @param searchBuilder
	 * @param sortType
	 */
	private void setScoreScriptSort(SearchRequestBuilder searchBuilder, BoolFilterBuilder boolFilter, GeoPoint geopoint,
			List<String> mkPriceDateList) {
		// 脚本排序功能开始
		ScriptScoreFunctionBuilder scriptSortBuilder = new ScriptScoreFunctionBuilder();
		scriptSortBuilder.script("calculate-score");
		scriptSortBuilder.param("lat", geopoint.getLat());
		scriptSortBuilder.param("lon", geopoint.getLon());
		// dt1: 距离1000米
		scriptSortBuilder.param("dt1", 1);
		// dt2: 距离3000米
		scriptSortBuilder.param("dt2", 3);

		// pt1: 价格200元
		scriptSortBuilder.param("pt1", 200);
		// pt2: 价格300元
		scriptSortBuilder.param("pt2", 300);

		// 眯客价属性列表
		scriptSortBuilder.param("mkPriceDates", mkPriceDateList);
		logger.info("score sort scriptSortBuilder is :\n" + scriptSortBuilder.toString());
		FunctionScoreQueryBuilder queryBuilder = QueryBuilders.functionScoreQuery(boolFilter, scriptSortBuilder);
		searchBuilder.setQuery(queryBuilder);
		// 脚本排序功能结束
	}

	/**
	 * 距离排序
	 * 
	 * @param searchBuilder
	 * @param geopoint
	 */
	private void sortByDistance(SearchRequestBuilder searchBuilder, GeoPoint geopoint) {
		searchBuilder.addSort(
				SortBuilders.geoDistanceSort("pin").point(geopoint.getLat(), geopoint.getLon()).order(SortOrder.ASC));
	}

	/**
	 * 是否推荐排序: 是否签约(升序), 推荐值(降序)
	 * 
	 * @param searchBuilder
	 */
	private void sortByRecommend(SearchRequestBuilder searchBuilder) {
		searchBuilder.addSort("ispms", SortOrder.ASC).addSort("priority", SortOrder.DESC);
	}

	/**
	 * 价格排序
	 * 
	 * @param searchBuilder
	 */
	// private void sortByPrice(List<Map<String, Object>> hotels) {
	// //
	// Collections.sort(hotels, new Comparator<Map<String, Object>>(){
	// @Override
	// public int compare(Map<String, Object> hotel1, Map<String, Object>
	// hotel2) {
	// int val = 0;
	// try {
	// BigDecimal price1 =
	// BigDecimal.valueOf(Double.valueOf(String.valueOf(hotel1.get("minprice"))));
	// BigDecimal price2 =
	// BigDecimal.valueOf(Double.valueOf(String.valueOf(hotel2.get("minprice"))));
	// val = price1.compareTo(price2);
	// } catch (Exception e) {
	// val = 0;
	// }
	// return val;
	// }
	// });
	// }
	/**
	 * 人气排序（月销量由高到低）
	 * 
	 * @param searchBuilder
	 */
	private void sortByOrders(SearchRequestBuilder searchBuilder) {
		searchBuilder.addSort("ordernummon", SortOrder.DESC);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean isInPromoPeriod() {
		/**
		 * hasn't been initialized yet
		 */
		if (promoStartTime == null) {
			List<String> promoTimes = roomSaleService.queryPromoTime();
			String startTime = promoTimes.get(0);
			String endTime = promoTimes.get(1);

			try {
				promoStartTime = LocalDateTime.fromDateFields(defaultFormatter.parse(startTime));
				promoEndTime = LocalDateTime.fromDateFields(defaultFormatter.parse(endTime));
			} catch (Exception ex) {
				logger.error(String.format("failed to parse startTime %s/endTime %s", startTime, endTime), ex);
				return false;
			}
		}

		boolean isAfter = LocalDateTime.now().isAfter(promoStartTime);
		boolean isBefore = LocalDateTime.now().isBefore(promoEndTime);

		return isAfter && isBefore;
	}

	/**
	 * 
	 * @param searchBuilder
	 */
	private void sortByPromo(SearchRequestBuilder searchBuilder, String version) {
		if (StringUtils.isNotEmpty(version) && ("3.1".compareTo(version) <= 0)) {
			searchBuilder.addSort("isonpromo", SortOrder.DESC);
		}
	}

	/**
	 * 
	 * @param hotels
	 */
	private void sortByVcState(List<Map<String, Object>> hotels) {
		List<Map<String, Object>> datasVC = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> datasNVC = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> data : hotels) {
			if (Constant.STR_TRUE.equals(data.get("isfull"))) {
				datasNVC.add(data);
			} else {
				datasVC.add(data);
			}
		}

		//
		hotels.clear();
		hotels.addAll(datasVC);
		hotels.addAll(datasNVC);
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> reorderSearchResults(SearchHit[] hits, HotelQuerylistReqEntity reqEntity)
			throws Exception {
		List<Map<String, Object>> searchResults = new ArrayList<Map<String, Object>>();

		Integer promoType = 0;

		try {
			promoType = Integer.parseInt(reqEntity.getPromotype());
		} catch (Exception ex) {
			throw new Exception(String.format("invalid promotype %s", reqEntity.getPromotype()), ex);
		}

		Map<String, Object> hotelIdMap = new HashMap<String, Object>();

		for (int i = 0; i < hits.length; i++) {
			SearchHit hit = hits[i];
			Map<String, Object> result = hit.getSource();
			String es_hotelid = String.valueOf(result.get("hotelid"));

			if (StringUtils.isBlank(es_hotelid)) {
				logger.warn("hotelid is empty, skip");
				continue;
			}

			hotelIdMap.put(es_hotelid, result);

			String isonpromo = (String) result.get("isonpromo");
			if (StringUtils.isBlank(isonpromo) || "0".equals(isonpromo)) {
				logger.warn(String.format("hotelid %s doesn't belong to promo", es_hotelid));
				continue;
			}

			result.put("$sortScore", hit.getScore());
			result.put("promoprice", 0);

			Map<String, Object> pin = (Map<String, Object>) result.get("pin");
			// hotel latitude and longitude
			double hotelLongitude = Double.valueOf(String.valueOf(pin.get("lon")));
			double hotelLatitude = Double.valueOf(String.valueOf(pin.get("lat")));
			double userDistance = DistanceUtil.distance(reqEntity.getUserlongitude(), reqEntity.getUserlatitude(),
					hotelLongitude, hotelLatitude);
			result.put("distance", userDistance);
			result.put("userdistance", userDistance);
			result.put("isnear", Constant.STR_FALSE);

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

				result.put("isrecommend", Constant.STR_FALSE);

				Long startTime = new Date().getTime();
				Long endTime = new Date().getTime();
				Long times = endTime - startTime;

				logger.info("--================================== 查询酒店省份区县信息开始： ==================================-- ");
				startTime = new Date().getTime();
				THotelModel hotelInfo = thotelMapper.findHotelInfoById(Long.valueOf(es_hotelid));
				if (hotelInfo != null) {
					result.put("hoteldis", hotelInfo.getDisname() == null ? "" : hotelInfo.getDisname());
					result.put("hotelcity", hotelInfo.getCitycode() == null ? "" : hotelInfo.getCitycode());
					result.put("hotelprovince", hotelInfo.getProvince() == null ? "" : hotelInfo.getProvince());
					result.put("hotelphone", hotelInfo.getHotelphone() == null ? "" : hotelInfo.getHotelphone());
				}
				endTime = new Date().getTime();
				times = endTime - startTime;
				logger.info("查询酒店: {}省份区县信息耗时: {}ms.", es_hotelid, times);
				logger.info("--================================== 查询酒店省份区县信息结束： ==================================-- ");

				logger.info("Hotelid: {} queryTransferData success. ", es_hotelid);
			} catch (Exception e) {
				logger.error("Hotelid: {} queryTransferData error: {} ", es_hotelid, e.getMessage());
			}

			result.remove("pin");
			result.remove("flag");

			logger.info("--================================== 查询酒店眯客价开始： ==================================-- ");

			Long startTime = new Date().getTime();
			String[] prices = null;
			if (hotelPriceService.isUseNewPrice())
				prices = hotelPriceService.getHotelMikePrices(Long.valueOf(es_hotelid), reqEntity.getStartdateday(),
						reqEntity.getEnddateday());
			else
				prices = roomstateService.getHotelMikePrices(Long.valueOf(es_hotelid), reqEntity.getStartdateday(),
						reqEntity.getEnddateday());
			Long endTime = new Date().getTime();
			Long times = endTime - startTime;
			logger.info("查询酒店: {}眯客价耗时: {}ms.", es_hotelid, times);
			BigDecimal minPrice = new BigDecimal(prices[0]);
			result.put("minprice", minPrice);

			Long maxPrice = roomstateService.findHotelMaxPrice(Long.parseLong(es_hotelid));
			result.put("minpmsprice", new BigDecimal(maxPrice));

			logger.info("酒店: {}门市价: {} maxprice{}", es_hotelid, prices[1], maxPrice);
			logger.info("--================================== 查询酒店眯客价结束： ==================================-- ");

			if (result.get("hotelpicnum") == null) {
				result.put("hotelpicnum", 0);
			}

			logger.info("--================================== 查询可订房间数开始： ==================================-- ");
			startTime = new Date().getTime();
			Long p_hotelid = Long.valueOf(es_hotelid);
			String p_isnewpms = Constant.STR_TRUE.equals(result.get("isnewpms")) ? Constant.STR_TRUE
					: Constant.STR_FALSE;
			String p_visible = Constant.STR_TRUE.equals(result.get("visible")) ? Constant.STR_TRUE : Constant.STR_FALSE;
			String p_online = Constant.STR_TRUE.equals(result.get("online")) ? Constant.STR_TRUE : Constant.STR_FALSE;

			Integer avlblroomnum = hotelService.getAvlblRoomNum(p_hotelid, p_isnewpms, p_visible, p_online,
					reqEntity.getStartdateday(), reqEntity.getEnddateday());

			Integer vacants = hotelService.calPromoVacants(promoType, p_hotelid, reqEntity.getStartdateday(),
					reqEntity.getEnddateday(), p_isnewpms);
			result.put("roomvacancy", vacants);

			endTime = new Date().getTime();
			times = endTime - startTime;
			logger.info("查询酒店: {}可订房间数耗时: {}ms.", es_hotelid, times);
			logger.info("酒店: {}可订房间数: {}", es_hotelid, avlblroomnum);
			result.put("avlblroomnum", avlblroomnum);
			if (avlblroomnum <= 0) {
				result.put("isfull", Constant.STR_TRUE);
			} else {
				result.put("isfull", Constant.STR_FALSE);
			}

			Map<String, String> fullstate = hotelService.getPromoFullState(vacants);
			result.putAll(fullstate);
			logger.info("--================================== 查询可订房间数结束： ==================================-- ");

			logger.info("--================================== 月销量查询开始: ==================================-- ");
			startTime = new Date().getTime();
			endTime = new Date().getTime();
			times = endTime - startTime;
			logger.info("查询酒店: {}月销量耗时: {}ms.", es_hotelid, times);
			logger.info("--================================== 月销量查询结束: ==================================-- ");

			logger.info("--================================== 最近预订时间查询开始: ==================================-- ");
			startTime = new Date().getTime();
			String createTime = thotelMapper.getLatestOrderTime(Long.valueOf(es_hotelid));
			logger.info("酒店: {}最近预定时间为: {}", es_hotelid, createTime);
			String rcntordertime = getRcntOrderTimeDes(createTime);
			logger.info("酒店: {}, {}", es_hotelid, rcntordertime);
			result.put("rcntordertimedes", rcntordertime);
			endTime = new Date().getTime();
			times = endTime - startTime;
			logger.info("查询酒店: {}最近预定时间耗时: {}ms.", es_hotelid, times);
			logger.info("--================================== 最近预订时间查询结束: ==================================-- ");

			logger.info("--================================== 查询酒店服务信息开始: ==================================-- ");
			startTime = new Date().getTime();
			Map<String, Object> hotelMap = hotelService.readonlyHotelDetail(Long.valueOf(es_hotelid));
			if (hotelMap != null) {
				if (Boolean.valueOf(String.valueOf(ServiceOutput.STR_MSG_SUCCESS))) {
					result.put("service", hotelMap.get("service") == null ? new ArrayList<Map<String, Object>>()
							: hotelMap.get("service"));
				} else {
					result.put("service", new ArrayList<Map<String, Object>>());
				}
			} else {
				result.put("service", new ArrayList<Map<String, Object>>());
			}
			endTime = new Date().getTime();
			times = endTime - startTime;
			logger.info("查询酒店: {}服务信息耗时: {}ms.", es_hotelid, times);
			logger.info("--================================== 查询酒店服务信息结束: ==================================-- ");

			logger.info("--================================== 查询酒店是否有返现开始: ==================================-- ");
			// TODO: 暂时没有数据来源，待添加
			// 是否返现（T/F）
			boolean iscashback = cashBackService.isCashBackHotelId(Long.valueOf(es_hotelid),
					reqEntity.getStartdateday(), reqEntity.getEnddateday());
			if (iscashback) {
				result.put("iscashback", Constant.STR_TRUE);
			} else {
				result.put("iscashback", Constant.STR_FALSE);
			}
			logger.info("--================================== 查询酒店是否有返现结束: ==================================-- ");

			String hotelvc = Constant.STR_TRUE;
			result.put("hotelvc", hotelvc);

			List<Map<String, Object>> roomtypeList = this.readonlyRoomtypeList(result, "");
			result.put("roomtype", roomtypeList);

			result.put("collectionstate", "");

			if (StringUtils.isNotBlank(reqEntity.getToken())) {
				try {
					String collectionState = findCollection(reqEntity.getToken(), Long.valueOf(es_hotelid));
					if (StringUtils.isNotBlank(collectionState)) {
						result.put("collectionstate", collectionState);
					}
				} catch (Exception ex) {
					result.put("collectionstate", "");

					logger.warn(String.format("invalid collectionstate for hotelid:%s", es_hotelid), ex);
				}
			}

			// 添加接口返回数据到结果集
			searchResults.add(result);
		}

		if (logger.isInfoEnabled()) {
			logger.info("about to groupThemes");
		}

		List<Map<String, Object>> roomtypeGrouped = groupThemes(searchResults);
		List<Map<String, Object>> hotelIds = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> roomtype : roomtypeGrouped) {
			Integer hotelId = (Integer) roomtype.get("hotelId");

			Map<String, Object> hotel = (Map<String, Object>) hotelIdMap.get(String.valueOf(hotelId));
			List<Map<String, Object>> singleRoomType = new ArrayList<Map<String, Object>>();
			singleRoomType.add(roomtype);

			hotel.put("roomtype", singleRoomType);

			hotelIds.add(hotel);
		}

		return hotelIds;
	}

	private String findCollection(String token, Long hotelid) throws Exception {
		String isCollected = "F";

		try {
			Map<String, Object> result = hotelCollectionService.readonlyHotelISCollected(token, hotelid);

			if (result != null && result.containsKey("state")) {
				isCollected = (String) result.get("state");
			}
		} catch (Exception ex) {
			throw new Exception("failed to collection user collecitonstate", ex);
		}

		return isCollected;
	}

	/**
	 * 酒店综合查询返回酒店列表数据
	 * 
	 * @param reqentity
	 *            参数: 酒店搜索入参Bean对象
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> readonlyOtsHotelListFromEsStore(HotelQuerylistReqEntity reqentity) throws Exception {
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		try {

			List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
			List<FilterBuilder> keywordBuilders = new ArrayList<FilterBuilder>();

			// C端搜索分类
			Integer searchType = reqentity.getSearchtype();
			if (searchType == null) {
				searchType = HotelSearchEnum.ALL.getId();
			}

			//
			List<Map<String, Object>> hotels = new ArrayList<Map<String, Object>>();
			// 如果城市id 为空则默认设置为上海
			String cityid = reqentity.getCityid();

			String hotelid = reqentity.getHotelid();

			if (logger.isInfoEnabled()) {
				logger.info(String.format("about to search for cityid: %s; hotelid: %s", cityid, hotelid));
			}

			// page参数校验：如果page小于等于0，默认为1.
			int page = reqentity.getPage().intValue();
			if (page <= 0) {
				page = SearchConst.SEARCH_PAGE_DEFAULT;
			}
			// limit参数校验：如果limit小于等于0，默认为10.
			int limit = reqentity.getLimit().intValue();
			if (limit <= 0) {
				limit = SearchConst.SEARCH_LIMIT_DEFAULT;
			}

			// added by chuaiqing: 城市搜索市中心坐标和搜索半径通过B端配置
			// 酒店搜索城市编码参数说明：最初接口文档中是cityid，后来接口文档改为citycode。但是各端还是沿用cityid参数名称。
			double cityLat_default = Constant.LAT_SHANGHAI;
			double cityLon_default = Constant.LON_SHANGHAI;

			// 眯客3.0：是否是当前酒店周边酒店搜索
			boolean isZhoubian = StringUtils.isNotBlank(reqentity.getExcludehotelid());
			if (isZhoubian) {
				// 周边默认搜素半径为5000米。
				if (reqentity.getRange() == null || reqentity.getRange() <= 0) {
					reqentity.setRange(SearchConst.SEARCH_RANGE_DEFAULT);
				}
			} else {
				if (!HotelSearchEnum.NEAR.getId().equals(searchType)) {
					// 不是搜索酒店周边，也不是搜索附近酒店，使用B端配置的搜索半径
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
							reqentity.setRange(cityRange.intValue());
							logger.info("set city {} search range is {}", cityid, cityRange);
						}
					}
					logger.info("find city geopoint end...");
				} else {
					// 如果搜索附近酒店，并且C端没有传range，默认5000米。
					if (reqentity.getRange() == null) {
						reqentity.setRange(SearchConst.SEARCH_RANGE_DEFAULT);
					}
				}
			}

			// 用户经纬度，根据它来计算酒店“距离我”的距离
			double userlat = reqentity.getUserlatitude() == null ? cityLat_default : reqentity.getUserlatitude();
			double userlon = reqentity.getUserlongitude() == null ? cityLon_default : reqentity.getUserlongitude();

			// 屏幕地图经纬度，根据它按照范围来搜索酒店
			double lat = reqentity.getPillowlatitude() == null ? cityLat_default : reqentity.getPillowlatitude();
			double lon = reqentity.getPillowlongitude() == null ? cityLon_default : reqentity.getPillowlongitude();

			SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
			if (StringUtils.isBlank(hotelid)) {
				// 设置查询类型 1.SearchType.DFS_QUERY_THEN_FETCH = 精确查询
				// 2.SearchType.SCAN = 扫描查询,无序
				searchBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);

				// make term filter builder
				this.makeTermFilter(reqentity, filterBuilders);

				// 酒店搜索范围
				// added by chuaiqing at 2015-09-08 19:39:35
				// 有关键字或者有酒店名称或者有酒店地址搜索，按照默认3000公里半径搜索
				if (StringUtils.isNotBlank(reqentity.getKeyword()) || StringUtils.isNotBlank(reqentity.getHotelname())
						|| StringUtils.isNotBlank(reqentity.getHoteladdr())) {
					reqentity.setRange(SearchConst.SEARCH_RANGE_MAX);
					logger.info("keyword or hotelname or hoteladdress search, set search range {}",
							SearchConst.SEARCH_RANGE_MAX);
				}

				// 眯客3.0：
				// 按照行政区域搜索开始
				if (HotelSearchEnum.AREA.getId().equals(searchType)) {
					makeAreaFilter(reqentity, filterBuilders);
				}
				// 按照行政区域搜索结束

				// 自定义筛选：按酒店类型
				makeHotelTypeFilter(reqentity, filterBuilders);
				// 自定义筛选：按床型
				makeBedTypeFilter(reqentity, filterBuilders);
				// 眯客3.0：
				/**
				 * added in Mike3.1
				 */
				makePromoFilter(reqentity, filterBuilders);

				double distance = Double.valueOf(reqentity.getRange());
				GeoDistanceFilterBuilder geoFilter = FilterBuilders.geoDistanceFilter("pin");
				// 按照屏幕地图经纬度来搜索酒店范围默认单位：米

				// 眯客3.0位置区域搜索：开始
				if (HotelSearchEnum.BZONE.getId().equals(searchType)
						|| HotelSearchEnum.AIRPORT.getId().equals(searchType)
						|| HotelSearchEnum.SUBWAY.getId().equals(searchType)
						|| HotelSearchEnum.SAREA.getId().equals(searchType)
						|| HotelSearchEnum.HOSPITAL.getId().equals(searchType)
						|| HotelSearchEnum.COLLEGE.getId().equals(searchType)) {
					// 坐标取所选位置的坐标，搜索半径取5000米
					String points = reqentity.getPoints();
					if (StringUtils.isEmpty(points)) {
						logger.error("按照{}搜索时points参数错误{}", HotelSearchEnum.getById(searchType).getName(), points);
					}
					GeoPoint point = this.getPoint(points);
					if (point != null) {
						// 屏幕坐标使用所选位置的经纬度坐标
						lat = point.getLat();
						lon = point.getLon();
						// 指定位置区域搜索的话，搜索半径按照默认5000米
						distance = SearchConst.SEARCH_RANGE_DEFAULT;
						logger.info("按照{}搜索，经纬度坐标：[{},{}], 搜索范围:{}米", HotelSearchEnum.getById(searchType).getName(),
								lon, lat, distance);
					} else {
						logger.error("按照{}搜索是没有获取到经纬度, points: {}。", HotelSearchEnum.getById(searchType).getName(),
								points);
					}
				}
				// 眯客3.0位置区域搜索：结束

				geoFilter.point(lat, lon).distance(distance, DistanceUnit.METERS).optimizeBbox("memory")
						.geoDistance(GeoDistance.ARC);
				filterBuilders.add(geoFilter);

				// hotelname,hoteladdr模糊查询
				this.makeQueryFilter(reqentity, filterBuilders);

				// keyword查询
				// 如果没有指定酒店名称和酒店地址，按照keyword来搜索
				if (StringUtils.isNotBlank(reqentity.getKeyword())) {
					makeKeywordFilter(reqentity, keywordBuilders);
					Cat.logEvent("HotKeywords", reqentity.getKeyword(), Message.SUCCESS, "");
				}

				FilterBuilder[] builders = new FilterBuilder[] {};
				BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
				// modified by chuaiqing at 2015-08-18 14:37:09
				if (keywordBuilders.size() > 0) {
					FilterBuilder[] arrKeywordBuilders = new FilterBuilder[] {};
					boolFilter.should(keywordBuilders.toArray(arrKeywordBuilders));
				}
				// make range filter builder
				List<FilterBuilder> mikePriceBuilders = this.makeMikePriceRangeFilter(reqentity);

				if (mikePriceBuilders.size() > 0) {
					BoolFilterBuilder mikePriceBoolFilter = FilterBuilders.boolFilter();
					mikePriceBoolFilter.should(mikePriceBuilders.toArray(builders));
					boolFilter.must(mikePriceBoolFilter);
				}
				if (AppUtils.DEBUG_MODE) {
					logger.info("boolFilter is : \n{}", boolFilter.toString());
				}

				// 如果搜酒店周边，当前酒店排除
				if (isZhoubian) {
					boolFilter.mustNot(FilterBuilders.termFilter("hotelid", reqentity.getExcludehotelid()));
				}

				// 提交搜索过滤filter
				// 只显示上线酒店和在线酒店
				BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
						.must(QueryBuilders.matchQuery("visible", Constant.STR_TRUE))
						.must(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
				boolFilter.must(FilterBuilders.queryFilter(boolQueryBuilder));
				searchBuilder.setPostFilter(boolFilter);

				// 眯客3.0排序：前置规则：可售房排序靠前，不可售排序靠后。
				// (1)按距离排序：按照返回的距离升序
				// (2)按价格排序：按照价格从低到高排序
				// (3)按人气排序：按照OTS上近30天的订单量从高到低排序
				// (4)默认排序（酒店权重分数）：按照酒店权重估值从高到低排序
				Integer paramOrderby = reqentity.getOrderby();
				if (paramOrderby == null) {
					paramOrderby = 0;
				}

				/**
				 * added in mike3.1, lift up promo as the top search variable
				 */
				sortByPromo(searchBuilder, reqentity.getCallversion());

				if (HotelSortEnum.DISTANCE.getId() == paramOrderby) {
					// 距离排序
					this.sortByDistance(searchBuilder, new GeoPoint(lat, lon));
				} else if (HotelSortEnum.PRICE.getId() == paramOrderby) {
					// 眯客价属性列表
					String startdateday = reqentity.getStartdateday();
					String enddateday = reqentity.getEnddateday();
					List<String> mkPriceDateList = this.getMikepriceDateList(startdateday, enddateday);
					setMikepriceScriptSort(searchBuilder, boolFilter, mkPriceDateList);
				} else if (HotelSortEnum.RECOMMEND.getId() == paramOrderby) {
					// 推荐排序(暂未使用)
					this.sortByRecommend(searchBuilder);
				} else if (HotelSortEnum.ORDERNUMS.getId() == paramOrderby) {
					// 人气（月订单量）排序
					this.sortByOrders(searchBuilder);
				} else {
					// 默认排序：开始
					// 眯客价属性列表
					String startdateday = reqentity.getStartdateday();
					String enddateday = reqentity.getEnddateday();
					List<String> mkPriceDateList = this.getMikepriceDateList(startdateday, enddateday);
					this.setScoreScriptSort(searchBuilder, boolFilter, new GeoPoint(lat, lon), mkPriceDateList);
					// 默认排序：结束
				}
			} else {
				// 如果输入参数中有hotelid，忽略其它过滤条件
				filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));
			}

			// 分页应用
			searchBuilder.setFrom((page - 1) * limit).setSize(limit).setExplain(true);

			logger.info(searchBuilder.toString());
			SearchResponse searchResponse = searchBuilder.execute().actionGet();

			SearchHits searchHits = searchResponse.getHits();
			long totalHits = searchHits.totalHits();

			if (StringUtils.isNotBlank(reqentity.getKeyword()) && (totalHits == 0D)) {
				Cat.logEvent("MismatchKeywords", reqentity.getKeyword(), Message.SUCCESS, "");
			}

			SearchHit[] hits = searchHits.getHits();
			logger.info("search hotel success: total {} found. current pagesize:{}", totalHits, hits.length);

			for (int i = 0; i < hits.length; i++) {
				SearchHit hit = hits[i];
				Map<String, Object> result = hit.getSource();
				String es_hotelid = String.valueOf(result.get("hotelid"));

				String isonpromo = (String) result.get("isonpromo");
				if (StringUtils.isBlank(isonpromo) || "0".equals(isonpromo)) {
					logger.warn(String.format("hotelid %s doesn't belong to promo", es_hotelid));
					continue;
				}

				result.put("$sortScore", hit.getScore());

				Integer promoType = StringUtils.isNotBlank(reqentity.getPromotype())
						? Integer.valueOf(reqentity.getPromotype()) : null;

				if (promoType == null) {
					if (result.get("promoinfo") != null
							&& ((List<Map<String, Object>>) result.get("promoinfo")).size() > 0) {
						promoType = findMinPromoType((List<Map<String, Object>>) result.get("promoinfo"));
					} else {
						promoType = 0;
					}
				}

				if (promoType != null) {
					List<Map<String, Integer>> promoList = (List<Map<String, Integer>>) result.get("promoinfo");
					if (promoList != null) {
						for (Map<String, Integer> promoinfo : promoList) {
							Integer hotelPromoType = promoinfo.get("promotype");
							if (hotelPromoType == promoType) {
								result.put("promoprice", promoinfo.get("promoprice"));
							}
						}
					}

					if (!result.containsKey("promoids")) {
						List<Integer> promoIds = new ArrayList<Integer>();
						for (Map<String, Integer> promo : promoList) {
							Integer tmppromoType = promo.get("promotype");

							List<TRoomSaleConfigInfo> configInfos = roomSaleConfigInfoService
									.querybyPromoType(tmppromoType);

							if (configInfos != null && configInfos.size() > 0) {
								Integer promoId = configInfos.get(0).getId();
								if (!promoIds.contains(promoId)) {
									promoIds.add(promoId);
								}
							}
						}

						result.put("promoids", promoIds);
					}
				}

				// 根据用户经纬度来计算两个经纬度坐标距离（单位：米）
				Map<String, Object> pin = (Map<String, Object>) result.get("pin");
				// hotel latitude and longitude
				double hotelLongitude = Double.valueOf(String.valueOf(pin.get("lon")));
				double hotelLatitude = Double.valueOf(String.valueOf(pin.get("lat")));
				double hotelDistance = DistanceUtil.distance(lon, lat, hotelLongitude, hotelLatitude); // 根据屏幕经纬度
																										// yub
																										// 20150724
				result.put("distance", hotelDistance);

				// 眯客3.0增加userdistance属性：用户坐标与酒店坐标的距离
				double userDistance = DistanceUtil.distance(userlon, userlat, hotelLongitude, hotelLatitude);
				// 选择地标搜索(机场车站、地铁线路、景点、医院、高校)，按照用户坐标和地标坐标计算距离
				if (HotelSearchEnum.AIRPORT.getId().equals(searchType)
						|| HotelSearchEnum.SUBWAY.getId().equals(searchType)
						|| HotelSearchEnum.SAREA.getId().equals(searchType)
						|| HotelSearchEnum.HOSPITAL.getId().equals(searchType)
						|| HotelSearchEnum.COLLEGE.getId().equals(searchType)) {
					String points = reqentity.getPoints();
					if (StringUtils.isEmpty(points)) {
						logger.error("按照{}搜索时points参数错误{}", HotelSearchEnum.getById(searchType).getName(), points);
					}
					GeoPoint point = this.getPoint(points);
					if (point != null) {
						// 地标经纬度坐标
						double marklat = point.getLat();
						double marklon = point.getLon();
						userDistance = DistanceUtil.distance(hotelLongitude, hotelLatitude, marklon, marklat);
					} else {
						userDistance = 0;
						logger.error("按照{}搜索是没有获取到经纬度, points: {}。", HotelSearchEnum.getById(searchType).getName(),
								points);
					}
				}
				result.put("userdistance", userDistance);

				// 眯客3.0: 产品中去掉酒店列表显示最近酒店特性.
				// 接口新增属性isnear: 是否最近酒店, distance值最小的酒店为T,其他为F.
				if (page <= 1 && i == 0) {
					// 第一条记录不一定是最近的，因为眯客3.0默认不是按照距离排序的。
					result.put("isnear", Constant.STR_FALSE);
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
					boolean isHotelPic = Constant.STR_TRUE.equals(reqentity.getIshotelpic());
					if (!isHotelPic) {
						result.remove("hotelpic");
					} else {
						// 需要返回酒店图片信息
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
					Long startTime = new Date().getTime();
					List<Map<String, String>> scores = thotelscoreMapper
							.findHotelScoresByHotelid(Long.valueOf(es_hotelid));
					Map<String, String> scoreMap = null;
					if (scores.size() > 0) {
						scoreMap = scores.get(0);
					}
					if (scoreMap != null) {
						result.put("scorecount", scoreMap.get("scorecount") == null ? 0 : scoreMap.get("scorecount"));
						result.put("grade", scoreMap.get("grade") == null ? 0 : scoreMap.get("grade"));
					} else {
						result.put("scorecount", 0);
						result.put("grade", 0);
					}
					Long endTime = new Date().getTime();
					Long times = endTime - startTime;
					logger.info("查询酒店: {}评价信息耗时: {}ms.", es_hotelid, times);
					logger.info(
							"--================================== 查询酒店评价信息结束： ==================================-- ");

					// TODO: hotel base info
					logger.info(
							"--================================== 查询酒店省份区县信息开始： ==================================-- ");
					startTime = new Date().getTime();
					THotelModel hotelInfo = thotelMapper.findHotelInfoById(Long.valueOf(es_hotelid));
					if (hotelInfo != null) {
						result.put("hoteldis", hotelInfo.getDisname() == null ? "" : hotelInfo.getDisname());
						result.put("hotelcity", hotelInfo.getCitycode() == null ? "" : hotelInfo.getCitycode());
						result.put("hotelprovince", hotelInfo.getProvince() == null ? "" : hotelInfo.getProvince());
						result.put("hotelphone", hotelInfo.getHotelphone() == null ? "" : hotelInfo.getHotelphone());
					}
					endTime = new Date().getTime();
					times = endTime - startTime;
					logger.info("查询酒店: {}省份区县信息耗时: {}ms.", es_hotelid, times);
					logger.info(
							"--================================== 查询酒店省份区县信息结束： ==================================-- ");

					// //queryTransferData(result, hotel);
					logger.info("Hotelid: {} queryTransferData success. ", es_hotelid);
				} catch (Exception e) {
					logger.error("Hotelid: {} queryTransferData error: {} ", es_hotelid, e.getMessage());
				}
				logger.info("--================================== " + num
						+ ". 处理ES酒店数据结束： ==================================--");

				// 添加到酒店列表
				result.remove("pin");
				result.remove("flag");

				logger.info("--================================== 查询酒店眯客价开始： ==================================-- ");
				// TODO: 酒店最低眯客价对应的房型的门市价,暂时取maxprice.
				Long startTime = new Date().getTime();
				String[] prices = null;
				if (hotelPriceService.isUseNewPrice())
					prices = hotelPriceService.getHotelMikePrices(Long.valueOf(es_hotelid), reqentity.getStartdateday(),
							reqentity.getEnddateday());
				else
					prices = roomstateService.getHotelMikePrices(Long.valueOf(es_hotelid), reqentity.getStartdateday(),
							reqentity.getEnddateday());
				Long endTime = new Date().getTime();
				Long times = endTime - startTime;
				logger.info("查询酒店: {}眯客价耗时: {}ms.", es_hotelid, times);

				BigDecimal minPrice = new BigDecimal(prices[0]);

				Integer hotelId = Integer.valueOf(es_hotelid);
				Double tempMinPromoPrice = roomSaleService.getHotelMinPromoPrice(hotelId);

				if (tempMinPromoPrice != null) {
					BigDecimal minPromoPrice = new BigDecimal(tempMinPromoPrice);
					if (minPrice.compareTo(minPromoPrice) > 0) {
						minPrice = minPromoPrice;
					}
				}
				result.put("minprice", minPrice);

				Long maxPrice = roomstateService.findHotelMaxPrice(Long.parseLong(es_hotelid));
				result.put("minpmsprice", new BigDecimal(maxPrice));

				logger.info("酒店: {}门市价: {} maxprice{}", es_hotelid, prices[1], maxPrice);
				logger.info("--================================== 查询酒店眯客价结束： ==================================-- ");

				if (result.get("hotelpicnum") == null) {
					result.put("hotelpicnum", 0);
				}

				logger.info("--================================== 查询可订房间数开始： ==================================-- ");
				startTime = new Date().getTime();
				Long p_hotelid = Long.valueOf(es_hotelid);
				String p_isnewpms = Constant.STR_TRUE.equals(result.get("isnewpms")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;
				String p_visible = Constant.STR_TRUE.equals(result.get("visible")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;
				String p_online = Constant.STR_TRUE.equals(result.get("online")) ? Constant.STR_TRUE
						: Constant.STR_FALSE;

				Integer avlblroomnum = hotelService.getAvlblRoomNum(p_hotelid, p_isnewpms, p_visible, p_online,
						reqentity.getStartdateday(), reqentity.getEnddateday());

				Integer vacants = hotelService.calPromoVacants(promoType, p_hotelid, reqentity.getStartdateday(),
						reqentity.getEnddateday(), p_isnewpms);
				result.put("roomvacancy", vacants);

				endTime = new Date().getTime();
				times = endTime - startTime;
				logger.info("查询酒店: {}可订房间数耗时: {}ms.", es_hotelid, times);
				logger.info("酒店: {}可订房间数: {}", es_hotelid, avlblroomnum);
				result.put("avlblroomnum", avlblroomnum);
				if (avlblroomnum <= 0) {
					result.put("isfull", Constant.STR_TRUE);
				} else {
					result.put("isfull", Constant.STR_FALSE);
				}

				Map<String, String> fullstate = hotelService.getPromoFullState(vacants);
				result.putAll(fullstate);
				logger.info("--================================== 查询可订房间数结束： ==================================-- ");

				logger.info("--================================== 月销量查询开始: ==================================-- ");
				startTime = new Date().getTime();
				// added by chuaiqing at 2015-09-10 13:33:07
				// 搜索上海的酒店时，不显示近30天订单销量
				// 眯客2.5业务逻辑：如果近30天订单销量小于10，则C端不显示“月销xxx单”
				// 所以如果搜索城市为上海的时候，接口返回月销售量数据为0
				if (Constant.STR_CITYID_SHANGHAI.equals(cityid)) {
					result.put("ordernummon", "");
				} else {
					Long sales = result.get("ordernummon") == null ? 0l
							: Long.valueOf(String.valueOf(result.get("ordernummon")));
					result.put("ordernummon", (sales >= 10 ? "月销" + sales + "单" : ""));
					logger.info("酒店: {}月销: {}单", es_hotelid, sales);
				}
				endTime = new Date().getTime();
				times = endTime - startTime;
				logger.info("查询酒店: {}月销量耗时: {}ms.", es_hotelid, times);
				logger.info("--================================== 月销量查询结束: ==================================-- ");

				logger.info("--================================== 最近预订时间查询开始: ==================================-- ");
				startTime = new Date().getTime();
				String createTime = thotelMapper.getLatestOrderTime(Long.valueOf(es_hotelid));
				logger.info("酒店: {}最近预定时间为: {}", es_hotelid, createTime);
				String rcntordertime = getRcntOrderTimeDes(createTime);
				logger.info("酒店: {}, {}", es_hotelid, rcntordertime);
				result.put("rcntordertimedes", rcntordertime);
				endTime = new Date().getTime();
				times = endTime - startTime;
				logger.info("查询酒店: {}最近预定时间耗时: {}ms.", es_hotelid, times);
				logger.info("--================================== 最近预订时间查询结束: ==================================-- ");

				logger.info("--================================== 查询酒店服务信息开始: ==================================-- ");
				startTime = new Date().getTime();
				Map<String, Object> hotelMap = hotelService.readonlyHotelDetail(Long.valueOf(es_hotelid));
				if (hotelMap != null) {
					if (Boolean.valueOf(String.valueOf(ServiceOutput.STR_MSG_SUCCESS))) {
						result.put("service", hotelMap.get("service") == null ? new ArrayList<Map<String, Object>>()
								: hotelMap.get("service"));
					} else {
						result.put("service", new ArrayList<Map<String, Object>>());
					}
				} else {
					result.put("service", new ArrayList<Map<String, Object>>());
				}
				endTime = new Date().getTime();
				times = endTime - startTime;
				logger.info("查询酒店: {}服务信息耗时: {}ms.", es_hotelid, times);
				logger.info("--================================== 查询酒店服务信息结束: ==================================-- ");

				logger.info("--================================== 查询酒店是否有返现开始: ==================================-- ");
				// TODO: 暂时没有数据来源，待添加
				// 是否返现（T/F）
				boolean iscashback = cashBackService.isCashBackHotelId(Long.valueOf(es_hotelid),
						reqentity.getStartdateday(), reqentity.getEnddateday());
				if (iscashback) {
					result.put("iscashback", Constant.STR_TRUE);
				} else {
					result.put("iscashback", Constant.STR_FALSE);
				}
				logger.info("--================================== 查询酒店是否有返现结束: ==================================-- ");

				String hotelvc = Constant.STR_TRUE;
				result.put("hotelvc", hotelvc);

				result.put("collectionstate", "");

				if (StringUtils.isNotBlank(reqentity.getToken())) {
					try {
						String collectionState = findCollection(reqentity.getToken(), Long.valueOf(hotelid));
						if (StringUtils.isNotBlank(collectionState)) {
							result.put("collectionstate", collectionState);
						}
					} catch (Exception ex) {
						result.put("collectionstate", "");

						logger.warn(String.format("invalid collectionstate for hotelid:%s", hotelid), ex);
					}
				}

				// 添加接口返回数据到结果集
				hotels.add(result);
			}

			// 重新按照是否可售分组排序
			this.sortByVcState(hotels);

			/**
			 * adjust the order by suppress all no vacancy hotels
			 */
			this.resortPromo(hotels);

			rtnMap.put("supplementhotel", new ArrayList<Map<String, Object>>());
			/**
			 * add hotel supplement to be bottom
			 */
			if (hotels.size() < this.minItemCount) {
				if (logger.isInfoEnabled()) {
					logger.info("about to add supplement hotels");
				}

				Integer supplementcount = searchAround(rtnMap, reqentity, this.minItemCount - hotels.size());
				rtnMap.put("supplementcount", supplementcount);
			}

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, true);
			rtnMap.put("count", totalHits);
			rtnMap.put("hotel", hotels);
		} catch (Exception e) {
			logger.error("failed to readonlyOtsHotelListFromEsStore...", e);

			rtnMap.put(ServiceOutput.STR_MSG_SUCCESS, false);
			rtnMap.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			rtnMap.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		}
		return rtnMap;
	}

	private Integer findMinPromoType(List<Map<String, Object>> promoInfoList) {
		Integer minTypeId = 0;
		Integer minPrice = 0;
		for (int i = 0; (promoInfoList != null && i < promoInfoList.size()); i++) {
			Map<String, Object> promoInfo = promoInfoList.get(i);

			Integer promoType = (Integer) promoInfo.get("promotype");
			String promoPriceTxt = (String) promoInfo.get("promoprice");

			Integer promoPrice = 0;
			try {
				promoPrice = Integer.valueOf(promoPriceTxt);
			} catch (Exception ex) {
				logger.warn(String.format("promotype is invalid %s", promoPriceTxt), ex);
			}

			if (minPrice == 0 || (promoPrice < minPrice)) {
				minPrice = promoPrice;
				minTypeId = promoType;
			}
		}

		if (minTypeId == 0) {
			logger.warn("default promotype not found right after...");
		}

		return minTypeId;
	}

	/**
	 * search hotels by around when supplement is required
	 * 
	 * @param hotels
	 * @param params
	 * @param hotelAroundCounter
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Integer searchAround(Map<String, Object> response, HotelQuerylistReqEntity params,
			Integer hotelAroundCounter) throws Exception {
		params.setCallentry(1);
		params.setIspromoonly(null);
		params.setHotelid("");
		params.setPromotype("");

		Map<String, Object> hotelsAround = searchService.readonlySearchHotels(params);

		if (hotelsAround != null & hotelsAround.get("hotel") != null) {
			List<Map<String, Object>> aroundHotels = (List<Map<String, Object>>) hotelsAround.get("hotel");
			List<Map<String, Object>> supplementhotel = new ArrayList<Map<String, Object>>();

			for (int i = 0; i < (aroundHotels.size() < hotelAroundCounter ? aroundHotels.size()
					: hotelAroundCounter); i++) {
				supplementhotel.add(aroundHotels.get(i));
			}
			response.put("supplementhotel", supplementhotel);

			return aroundHotels.size() < hotelAroundCounter ? aroundHotels.size() : hotelAroundCounter;
		} else {
			return 0;
		}
	}

	/**
	 * make es term filter
	 * 
	 * @param reqentity
	 * @param filterBuilder
	 * @return
	 */
	private void makeTermFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		String hotelid = reqentity.getHotelid();
		if (!Strings.isNullOrEmpty(hotelid)) {
			filterBuilders.add(FilterBuilders.termFilter("hotelid", hotelid));
		}

		String cityid = reqentity.getCityid();
		if (!Strings.isNullOrEmpty(cityid)) {
			filterBuilders.add(FilterBuilders.termFilter("hotelcity", cityid));
		}

		String disid = reqentity.getDisid();
		if (!Strings.isNullOrEmpty(disid)) {
			filterBuilders.add(FilterBuilders.termFilter("hoteldis", disid));
		}

		// 是否签约 值为T，则只返回签约酒店，值为F或空，返回所有酒店
		filterBuilders.add(FilterBuilders.termFilter("ispms", 1));
	}

	/**
	 * 
	 * @param reqentity
	 * @return
	 */
	private void makeQueryFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		// hotelname模糊查询
		if (!StringUtils.isBlank(reqentity.getHotelname())) {
			QueryFilterBuilder hotelnameFilter = FilterBuilders.queryFilter(
					QueryBuilders.matchQuery("hotelname", reqentity.getHotelname()).operator(Operator.AND));
			filterBuilders.add(hotelnameFilter);
		}
		// hoteladdr模糊查询
		if (reqentity.getHoteladdr() != null) {
			QueryFilterBuilder hoteladdrFilter = FilterBuilders.queryFilter(
					QueryBuilders.matchQuery("detailaddr", reqentity.getHoteladdr()).operator(Operator.AND));
			filterBuilders.add(hoteladdrFilter);
		}
	}

	/**
	 * 
	 * @param reqentity
	 * @return
	 */
	private void makeKeywordFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> keywordBuilders) {
		try {
			// keyword搜索酒店名称，酒店地址
			if (!StringUtils.isBlank(reqentity.getKeyword())) {
				QueryFilterBuilder hotelNameFilter = FilterBuilders.queryFilter(
						QueryBuilders.matchQuery("hotelname", reqentity.getKeyword()).operator(Operator.AND));
				keywordBuilders.add(hotelNameFilter);

				QueryFilterBuilder hotelAddrFilter = FilterBuilders.queryFilter(
						QueryBuilders.matchQuery("detailaddr", reqentity.getKeyword()).operator(Operator.AND));
				keywordBuilders.add(hotelAddrFilter);
			}
		} catch (Exception e) {
		}
	}

	/**
	 * 
	 * @param reqentity
	 * @return
	 */
	private List<FilterBuilder> makeMikePriceRangeFilter(HotelQuerylistReqEntity reqentity) throws ParseException {
		List<FilterBuilder> mikePriceBuilders = new ArrayList<FilterBuilder>();
		if (StringUtils.isNotBlank(reqentity.getMinprice()) || StringUtils.isNotBlank(reqentity.getMaxprice())) {
			Double minpriceParam = 0D;
			if (StringUtils.isNotBlank(reqentity.getMinprice())) {
				minpriceParam = Double.valueOf(reqentity.getMinprice());
			}
			Double maxpriceParam = Double.MAX_VALUE;
			if (StringUtils.isNotBlank(reqentity.getMaxprice())) {
				maxpriceParam = Double.valueOf(reqentity.getMaxprice());
			}

			// hotel.getStartdateday(), hotel.getEnddateday() yyyyMMdd
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			Date startDate = sdf.parse(reqentity.getStartdateday());
			Date endDate = sdf.parse(reqentity.getEnddateday());
			Calendar startDateCal = Calendar.getInstance();
			startDateCal.setTime(startDate);
			Calendar endDateCal = Calendar.getInstance();
			endDateCal.setTime(endDate);
			while (startDateCal.compareTo(endDateCal) <= 0) {
				mikePriceBuilders.add(
						FilterBuilders.rangeFilter(SearchConst.MIKE_PRICE_PROP + sdf.format(startDateCal.getTime()))
								.gte(Double.valueOf(minpriceParam)).lte(Double.valueOf(maxpriceParam)));
				startDateCal.add(Calendar.DATE, 1);
			}
		}
		return mikePriceBuilders;
	}

	/**
	 * 按行政区搜索
	 * 
	 * @param reqentity
	 * @return
	 */
	private void makeAreaFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		String posid = reqentity.getPosid();
		try {
			Long id = posid == null ? 0l : Long.valueOf(posid);
			SAreaInfo areainfo = sareaInfoMapper.selectByPrimaryKey(id);
			String discode = areainfo.getDiscode();
			if (discode == null) {
				discode = "";
			}
			filterBuilders.add(FilterBuilders.termFilter("discode", discode));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 按行政区搜索
	 * 
	 * @param reqentity
	 * @return
	 */
	private void makeHotelTypeFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		String hoteltype = reqentity.getHoteltype();
		if (StringUtils.isBlank(hoteltype)) {
			return;
		}
		if (hoteltype.equals(String.valueOf(HotelTypeEnum.HMSHOTEL.getId()))
				|| hoteltype.equals(String.valueOf(HotelTypeEnum.THEMEDHOTEL.getId()))
				|| hoteltype.equals(String.valueOf(HotelTypeEnum.PLAZAHOTEL.getId()))
				|| hoteltype.equals(String.valueOf(HotelTypeEnum.APARTMENTHOTEL.getId()))
				|| hoteltype.equals(String.valueOf(HotelTypeEnum.HOSTELS.getId()))
				|| hoteltype.equals(String.valueOf(HotelTypeEnum.INNER.getId()))) {
			// 按酒店类型过滤：1旅馆，2主题酒店，3精品酒店，4公寓，5招待所，6客栈
			filterBuilders.add(FilterBuilders.termFilter("hoteltype", hoteltype));
		}
	}

	/**
	 * 按指定床型搜搜
	 * 
	 * @param reqentity
	 * @param filterBuilders
	 */
	private void makeBedTypeFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		// 入参bedtype
		Integer bedtype = reqentity.getBedtype();
		if (bedtype == null) {
			return;
		}
		if (1 == bedtype || 2 == bedtype) {
			String field = "bedtype" + bedtype;
			filterBuilders.add(FilterBuilders.termFilter(field, 1));
		}
	}

	/**
	 * added in mike3.1, promo filter will be added in version 3.1
	 * 
	 * 
	 * @param reqentity
	 * @param filterBuilders
	 */
	private void makePromoFilter(HotelQuerylistReqEntity reqentity, List<FilterBuilder> filterBuilders) {
		Boolean isPromoOnly = reqentity.getIspromoonly();
		String callVersion = reqentity.getCallversion() == null ? "" : reqentity.getCallversion().trim();
		Integer callEntry = reqentity.getCallentry();
		String callMethod = reqentity.getCallmethod() == null ? "" : reqentity.getCallmethod().trim();
		String promoType = reqentity.getPromotype() == null ? "" : reqentity.getPromotype().trim();
		String promoId = reqentity.getPromoid();

		if (logger.isDebugEnabled()) {
			logger.debug(String.format("callEntry:%s; callMethod:%s; callVersion:%s; isPromoOnly:%s", callEntry,
					callMethod, callVersion, isPromoOnly));
		}

		if (isPromoOnly == null) {
			/**
			 * old version compatible, promo types won't show
			 */
			if (StringUtils.isBlank(callVersion) || "3.1".compareTo(callVersion.trim()) > 0) {
				filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("isonpromo", "0")));
			}

			return;
		} else if (isPromoOnly) {
			if ("3.1".compareTo(callVersion) > 0) {
				logger.warn("version before 3.1 shouldn't access this attribute isonpromo...");
			}

			if (callEntry != null && callEntry != 2) {
				if (callEntry == 1) {
					Cat.logEvent("摇一摇", Event.SUCCESS);
				} else if (callEntry == 3) {
					Cat.logEvent("切客", Event.SUCCESS);
				}

				filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("isonpromo", "0")));
			} else if (StringUtils.isNotEmpty(callMethod) && "3".equalsIgnoreCase(callMethod)) {
				Cat.logEvent("wechat", Event.SUCCESS);

				filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("isonpromo", "0")));
			} else {
				filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("isonpromo", "1")));
			}
		}

		if (StringUtils.isNotBlank(promoId)) {
			List<TRoomSaleConfigInfo> promotypes = roomSaleConfigInfoService.queryListBySaleTypeId("",
					Integer.parseInt(promoId), 0, 10);
			for (TRoomSaleConfigInfo config : promotypes) {
				Integer promotype = config.getId();

				filterBuilders
						.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("promoinfo.promotype", promotype)));
			}

		} else if (StringUtils.isNotBlank(promoType)) {
			filterBuilders.add(FilterBuilders.queryFilter(QueryBuilders.matchQuery("promoinfo.promotype", promoType)));
		}
	}

	/**
	 * 
	 * @param startdateday
	 * @param enddateday
	 * @return
	 */
	private List<String> getMikepriceDateList(String startdateday, String enddateday) {
		// 眯客价属性列表
		List<String> mkPriceDateList = new ArrayList<String>();
		try {
			int days = SearchConst.MIKEPRICE_DAYS;
			Date sdate = DateUtils.getDateFromString(startdateday);
			String mkDate = startdateday;
			if (startdateday.equals(enddateday)) {
				mkPriceDateList.add(SearchConst.MIKE_PRICE_PROP.concat(mkDate));
			} else {
				for (int i = 0; i < days; i++) {
					if (enddateday.equals(mkDate)) {
						break;
					}
					Date date = DateUtils.addDays(sdate, i);
					mkDate = DateUtils.formatDateTime(date, DateUtils.FORMATSHORTDATETIME);
					String mkPricekey = SearchConst.MIKE_PRICE_PROP.concat(mkDate);
					mkPriceDateList.add(mkPricekey);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mkPriceDateList;
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
	 * 转换酒店数据
	 * 
	 * @param data
	 *            参数：es酒店信息
	 * @param reqentity
	 *            参数：参数数据
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object queryTransferData(Map<String, Object> data, HotelQuerylistReqEntity reqentity) throws Exception {
		// 是否考虑优惠价格: 非必填(T/F)，值为T，则最低价取优惠活动最低价，空或F则最低价取ota最低门市价
		boolean isDiscount = Constant.STR_TRUE.equals(reqentity.getIsdiscount());

		// 是否返回酒店图片: 非必填(T/F)，空等同于F，值为T，则返回图片信息，空或F则不返回图片信息
		boolean isHotelPic = Constant.STR_TRUE.equals(reqentity.getIshotelpic());

		// 是否返回酒店设施: 非必填(T/F)，值为T，则返回酒店设施信息，空或F则不返回酒店设施信息
		boolean isFacility = Constant.STR_TRUE.equals(reqentity.getIsfacility());

		// 是否返回商圈: 非必填(T/F)，值为T，则返回酒店商圈信息，空或F则不返回酒店设施信息
		boolean isBussinessZone = Constant.STR_TRUE.equals(reqentity.getIsbusinesszone());

		// 是否返回房型: 非必填(T/F)，值为T，则返回房型信息，空或F则不返回房型信息
		boolean isRoomType = Constant.STR_TRUE.equals(reqentity.getIsroomtype());

		// 是否返回房型图片: 非必填(T/F)，值为T，则返回房型图片信息，空或F则不返回房型图片信息
		boolean isRoomTypePic = Constant.STR_TRUE.equals(reqentity.getIsroomtypepic());

		// 是否返回房型设施: 非必填(T/F)，值为T，则返回房型设施信息，空或F则不返回房型设施信息
		boolean isRoomTypeFacility = Constant.STR_TRUE.equals(reqentity.getIsroomtypefacility());

		// 是否返回床型: 非必填(T/F)，值为T，则返回床型信息，空或F则不返回床型信息
		boolean isBedType = Constant.STR_TRUE.equals(reqentity.getIsbedtype());

		// 是否返回团购信息: 非必填(T/F)，值为T，则返回团购信息，空或F则不返回团购信息
		boolean isTeambuying = Constant.STR_TRUE.equals(reqentity.getIsteambuying());

		// 是否返回交通信息: 非必填(T/F)，值为T，则返回交通信息，空或F则不返回交通信息
		boolean isTraffic = false;

		// 是否返回周边信息: 非必填(T/F)，值为T，则返回周边信息，空或F则不返回周边信息
		boolean isPeripheral = false;

		// 床型：1单床房，2双床房，3其它房，空不限制
		String bedtype = reqentity.getBednum();

		if (logger.isInfoEnabled()) {
			logger.info(String.format("queryTransferData with parameters, isRoomType:%s", isRoomType));
		}

		// 不返回酒店图片信息，从结果集中删除
		if (!isHotelPic) {
			data.remove("hotelpic");
		} else {
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
		String hotelvc = Constant.STR_TRUE;
		data.put("hotelvc", hotelvc);

		String cityid = String.valueOf(data.get("hotelcity"));
		String hotelid = String.valueOf(data.get("hotelid"));
		String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
		String beginDate = strCurDay;
		String endDate = strCurDay;
		if (!StringUtils.isBlank(reqentity.getStartdateday())) {
			beginDate = reqentity.getStartdateday();
		}

		if (!StringUtils.isBlank(reqentity.getEnddateday())) {
			endDate = reqentity.getEnddateday();
		}

		Map<String, Object> point = (Map<String, Object>) data.get("pin");
		// hotel latitude and longitude
		data.put("latitude", point.get("lat"));
		data.put("longitude", point.get("lon"));

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

		// 是否推荐，无数据来源，暂定为F
		data.put("isrecommend", Constant.STR_FALSE);

		// hotel score: 移到listOtsHotel和getOtsHotel方法里.
		logger.info("--================================== 查询酒店评价信息开始： ==================================-- ");
		Long startTime = new Date().getTime();
		List<Map<String, String>> scores = thotelscoreMapper.findHotelScoresByHotelid(Long.valueOf(hotelid));
		Map<String, String> scoreMap = null;
		if (scores.size() > 0) {
			scoreMap = scores.get(0);
		}
		if (scoreMap != null) {
			data.put("scorecount", scoreMap.get("scorecount") == null ? 0 : scoreMap.get("scorecount"));
			data.put("grade", scoreMap.get("grade") == null ? 0 : scoreMap.get("grade"));
		} else {
			data.put("scorecount", 0);
			data.put("grade", 0);
		}
		Long endTime = new Date().getTime();
		Long times = endTime - startTime;
		logger.info("查询酒店: {}评价信息耗时: {}ms.", hotelid, times);
		logger.info("--================================== 查询酒店评价信息结束： ==================================-- ");

		// hotel base info
		logger.info("--================================== 查询酒店省份区县信息开始： ==================================-- ");
		startTime = new Date().getTime();
		THotelModel hotelInfo = thotelMapper.findHotelInfoById(Long.valueOf(hotelid));
		if (hotelInfo != null) {
			data.put("hoteldis", hotelInfo.getDisname() == null ? "" : hotelInfo.getDisname());
			data.put("hotelcity", hotelInfo.getCitycode() == null ? "" : hotelInfo.getCitycode());
			data.put("hotelprovince", hotelInfo.getProvince() == null ? "" : hotelInfo.getProvince());
			data.put("hotelphone", hotelInfo.getHotelphone() == null ? "" : hotelInfo.getHotelphone());
		}
		endTime = new Date().getTime();
		times = endTime - startTime;
		logger.info("查询酒店: {}省份区县信息耗时: {}ms.", hotelid, times);
		logger.info("--================================== 查询酒店省份区县信息结束： ==================================-- ");

		// room type
		// 如果返回房型信息，查询房型信息放到data结果集中

		if (isRoomType) {
			logger.info(String.format("promoinfo:%s", data == null ? "" : data.get("promoinfo").toString()));

			List<Map<String, Object>> promoInfoList = (List<Map<String, Object>>) data.get("promoinfo");
			final Map<Integer, String> promoMap = new HashMap<Integer, String>();
			for (int i = 0; promoInfoList != null && i < promoInfoList.size(); i++) {
				Integer promotype = 0;
				String promoprice = "";

				try {
					if (promoInfoList.get(i) != null && promoInfoList.get(i).containsKey("promotype")) {
						promotype = promoInfoList.get(i).get("promotype") == null ? 0
								: (Integer) promoInfoList.get(i).get("promotype");
						promoprice = promoInfoList.get(i).get("promoprice") == null ? "0"
								: (String) promoInfoList.get(i).get("promoprice");

						promoMap.put(promotype == null ? 0 : promotype, promoprice);
					}
				} catch (Exception ex) {
					logger.warn("invalid dateformat for promotype and promoprice", ex);
					continue;
				}
			}

			List<Map<String, Object>> roomtypeList = this.readonlyRoomtypeList(data, bedtype);
			for (Map<String, Object> roomtypeItem : roomtypeList) {
				logger.info("--================================== 查询房型是否可用信息开始： ==================================-- ");
				// roomtypevc
				// 获取房态信息:
				// 该房间是否有可售.(由于现在房态信息缓存到redis，不再存储到mysql中间表，所以这里从缓存取.)
				String roomtypevc = this.readonlyRoomtypevc(roomtypeItem, reqentity);
				String roomtypevacancy;

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

				roomtypeItem.put("promoprice", 0);
				Integer roomPromotype = (Integer) roomtypeItem.get("promotype");
				if (roomPromotype != null) {
					String promoPrice = promoMap.get(roomPromotype);
					if (StringUtils.isNotBlank(promoPrice)) {
						roomtypeItem.put("promoprice", promoPrice);
					}
				}

				if (roomtypeItem.get("promotype") == null) {
					roomtypeItem.put("promotype", "");
				}
			}

			data.put("roomtype", roomtypeList);

			Integer promoType = StringUtils.isNotBlank(reqentity.getPromotype())
					? Integer.valueOf(reqentity.getPromotype()) : null;
			if (promoType != null) {
				List<Map<String, Integer>> promoList = (List<Map<String, Integer>>) data.get("promoinfo");
				if (promoList != null) {
					for (Map<String, Integer> promoinfo : promoList) {
						Integer hotelPromoType = promoinfo.get("promotype");
						if (hotelPromoType == promoType) {
							data.put("promoprice", promoinfo.get("promoprice"));
						}
					}
				}
			}
		}

		// 是否返现（T/F）
		boolean iscashback = cashBackService.isCashBackHotelId(Long.valueOf(hotelid), beginDate, endDate);
		if (iscashback) {
			data.put("iscashback", Constant.STR_TRUE);
		} else {
			data.put("iscashback", Constant.STR_FALSE);
		}

		// 返回数据
		// 删除接口数据不需要的属性
		data.remove("pin");
		data.remove("flag");
		return data;
	}

	/**
	 * 
	 * @param eshotel
	 * @return
	 */
	private List<Map<String, Object>> readonlyRoomtypeList(Map<String, Object> eshotel, String bedtype) {
		List<Map<String, Object>> roomtypelist = new ArrayList<Map<String, Object>>();
		String hotelid = String.valueOf(eshotel.get("hotelid"));
		if (eshotel == null || StringUtils.isBlank(hotelid)) {
			return roomtypelist;
		}
		try {
			StringBuffer bfSql = new StringBuffer();
			bfSql.append(
					"select a.id as roomtypeid, a.name as roomtypename, a.cost as roomtypepmsprice, a.bednum,a.roomnum, "
							+ "a.cost as roomtypeprice,b.maxarea,b.minarea,b.pics,b.bedtype,b.bedsize as bedlength, d.name as bedtypename")
					.append(", info.id as promotype, info.saleTypeId as promoid  ").append("  from t_roomtype a ")
					.append(" join t_roomtype_info b on a.id = b.roomtypeid")
					.append(" join t_bedtype d on b.bedtype = d.id")
					.append(" left join t_room_sale_config config on b.roomtypeid = config.saleroomtypeid ")
					.append(" join t_room_sale_config_info info on config.saleConfigInfoId = info.id ")
					.append(" where a.thotelid='" + hotelid + "'")
					.append(" and config.valid = 'T' and info.valid = 'T' ");
			if (!StringUtils.isBlank(bedtype)) {
				bfSql.append(" and b.bedtype='" + bedtype + "'");
			}
			List<Bean> list = Db.find(bfSql.toString());
			logger.info("getRoomtypeList method sql: {}\n", bfSql.toString());
			for (Bean bean : list) {
				roomtypelist.add(bean.getColumns());
			}
		} catch (Exception e) {
			logger.error("getRoomtypeList method error...", e);
			return roomtypelist;
		}
		return roomtypelist;
	}

	/**
	 * 
	 * @param roomtypeItem
	 * @param reqentity
	 * @return
	 */
	private String readonlyRoomtypevc(Map<String, Object> roomtypeItem, HotelQuerylistReqEntity reqentity) {
		String result = Constant.STR_FALSE;
		if (reqentity == null) {
			return Constant.STR_FALSE;
		}
		String cityid = Constant.STR_CITYID_SHANGHAI;
		try {
			String strCurDay = DateUtils.getStringFromDate(new Date(), DateUtils.FORMATSHORTDATETIME);
			String whereStarttime = "";
			if (!StringUtils.isBlank(reqentity.getStartdate())) {
				whereStarttime = " time >= '" + reqentity.getStartdate() + "'";
			} else {
				whereStarttime = " time >= '" + strCurDay + "'";
			}

			String whereEndtime = "";
			if (!StringUtils.isBlank(reqentity.getEnddate())) {
				whereEndtime = " time <= '" + reqentity.getEnddate() + "'";
			} else {
				whereEndtime = " time <= '" + strCurDay + "'";
			}

			if (!StringUtils.isBlank(reqentity.getCityid())) {
				cityid = reqentity.getCityid();
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
	 * 
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

	/*
	 * 模糊查询位置区域
	 * 
	 * @param citycode
	 * 
	 * @param keyword
	 */
	@Override
	public Map<String, Object> readonlyPositionsFuzzy(String citycode, String keyword) {
		SearchRequestBuilder searchBuilder = esProxy.prepareSearch(esProxy.OTS_INDEX_DEFAULT,
				esProxy.POSITION_TYPE_DEFAULT);
		List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
		QueryFilterBuilder citycodeFilter = FilterBuilders.queryFilter(QueryBuilders.termQuery("citycode", citycode));
		filterBuilders.add(citycodeFilter);
		QueryFilterBuilder keywordFilter = FilterBuilders.queryFilter(
				QueryBuilders.multiMatchQuery(keyword, "name", "stations.stationname").operator(Operator.AND));
		filterBuilders.add(keywordFilter);

		FilterBuilder[] builders = new FilterBuilder[] {};
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
		searchBuilder.setFrom(0).setSize(10000).setExplain(true);// ES默认10条记录，设置10000，能返回citycode下所有。
		searchBuilder.setPostFilter(boolFilter);
		searchBuilder.addSort("ptype", SortOrder.ASC);
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		SearchHits searchHits = searchResponse.getHits();
		long totalHits = searchResponse.getHits().totalHits();
		logger.info("search positions by citycode:{}, success: total {} found.", citycode, totalHits);
		SearchHit[] hits = searchHits.getHits();
		List<SearchPositionsCoordinateRespEntity> datas = Lists.newArrayList();
		for (int i = 0; i < hits.length; i++) {
			SearchHit hit = hits[i];
			Map<String, Object> result = hit.getSource();
			if (HotelSearchEnum.SUBWAY.getId().equals(Integer.valueOf(String.valueOf(result.get("ptype"))))) {
				// 地铁线路
				String linename = String.valueOf(result.get("name"));
				List<Map<String, Object>> stations = Lists.newArrayList();
				if (result.containsKey("stations")) {
					stations = (List<Map<String, Object>>) result.get("stations");
					for (Map<String, Object> station : stations) {
						String staname = String.valueOf(station.get("stationname"));
						if (staname.indexOf(keyword) > -1) {
							SearchPositionsCoordinateRespEntity ds = new SearchPositionsCoordinateRespEntity();
							ds.setId(Long.valueOf(String.valueOf(result.get("id"))));
							ds.setName(linename + staname);
							ds.setType(String.valueOf(HotelSearchEnum.SUBWAY.getId()));
							ds.setTname(HotelSearchEnum.SUBWAY.getName());
							if (station.get("lat") == null || station.get("lng") == null) {
								ds.setCoordinates("[[]]");
							} else {
								Double lat = Double.valueOf(String.valueOf(station.get("lat")));
								Double lng = Double.valueOf(String.valueOf(station.get("lng")));
								ds.setCoordinates("[[" + lng + "," + lat + "]]");
							}
							//
							datas.add(ds);
						}
					}
				}
			} else {
				// 非地铁线路
				SearchPositionsCoordinateRespEntity ds = new SearchPositionsCoordinateRespEntity();
				ds.setId(Long.valueOf(result.get("id").toString()));
				ds.setName(result.get("name").toString());
				String typeID = result.get("ptype").toString();
				ds.setType(typeID);
				int tid = Integer.parseInt(typeID);
				ds.setTname(PositionTypeEnum.getById(tid).getTypeName());
				if (result.get("lat") == null || result.get("lng") == null) {
					ds.setCoordinates("[[]]");
				} else {
					Double lat = Double.valueOf(String.valueOf(result.get("lat")));
					Double lng = Double.valueOf(String.valueOf(result.get("lng")));
					ds.setCoordinates("[[" + lng + "," + lat + "]]");
				}
				//
				datas.add(ds);
			}
		}
		List<SearchPositionsCoordinateRespEntity> hotelList = this.readonlyHotelListFromES(citycode, keyword);
		datas.addAll(hotelList);
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		rtnMap.put("datas", datas);
		rtnMap.put("count", datas.size());
		return rtnMap;
	}

	/**
	 * 从ES中读取位置信息
	 * 
	 * @param citycode
	 * @param ptype
	 * @return
	 */
	public List<SearchPositionsCoordinateRespEntity> readonlyPositionsFromES(String citycode, String ptype) {
		SearchRequestBuilder searchBuilder = esProxy.prepareSearch(esProxy.OTS_INDEX_DEFAULT,
				esProxy.POSITION_TYPE_DEFAULT);
		searchBuilder.setQuery(QueryBuilders.matchQuery("citycode", citycode));
		if (StringUtils.isNotBlank(ptype)) {
			searchBuilder.setQuery(QueryBuilders.matchQuery("ptype", ptype));
		}
		searchBuilder.addSort("ptype", SortOrder.ASC).addSort("id", SortOrder.ASC);
		searchBuilder.setFrom(0).setSize(10000).setExplain(true);// ES默认10条记录，设置10000，能返回citycode下所有。
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		SearchHits searchHits = searchResponse.getHits();
		long totalHits = searchResponse.getHits().totalHits();
		logger.info("search positions by citycode:{}, success: total {} found.", citycode, totalHits);
		SearchHit[] hits = searchHits.getHits();
		List<SearchPositionsCoordinateRespEntity> datas = Lists.newArrayList();
		for (int i = 0; i < hits.length; i++) {
			SearchHit hit = hits[i];
			Map<String, Object> result = hit.getSource();
			SearchPositionsCoordinateRespEntity ds = new SearchPositionsCoordinateRespEntity();
			ds.setId(Long.valueOf(result.get("id").toString()));
			ds.setName(result.get("name").toString());
			String typeid = result.get("ptype").toString();
			ds.setType(typeid);
			int tid = Integer.parseInt(typeid);
			ds.setTname(PositionTypeEnum.getById(tid).getTypeName());
			if (result.get("lat") == null || result.get("lng") == null) {
				ds.setCoordinates("[[]]");
			} else {
				Double lat = (double) result.get("lat");
				Double lng = (double) result.get("lng");
				ds.setCoordinates("[[" + lng + "," + lat + "]]");
			}
			if (tid == PositionTypeEnum.METRO.getId()) {
				List stResult = new ArrayList();
				List stations = (List) result.get("stations");
				for (int j = 0; j < stations.size(); j++) {
					Map<String, Object> cm = (Map) stations.get(j);
					Child dsc = ds.new Child();
					dsc.setId(Long.parseLong(cm.get("id").toString()));
					dsc.setPid(Long.parseLong(cm.get("lineid").toString()));
					dsc.setCid(Long.parseLong(cm.get("stationid").toString()));
					dsc.setcName(cm.get("stationname").toString());
					if (cm.get("lat") == null || cm.get("lng") == null) {
						dsc.setcCoordinates("[[]]");
					} else {
						Double lat = (double) cm.get("lat");
						Double lng = (double) cm.get("lng");
						dsc.setcCoordinates("[[" + lng + "," + lat + "]]");
					}
					stResult.add(dsc);
				}
				// 地铁站排序
				Collections.sort(stResult, new Comparator<Child>() {
					@Override
					public int compare(Child b1, Child b2) {
						return b1.getCid().compareTo(b2.getCid());
					}
				});
				ds.setChild(stResult);
			}
			datas.add(ds);
		}
		return datas;
	}

	/**
	 * 
	 * @param citycode
	 * @param keyword
	 * @return
	 */
	private List<SearchPositionsCoordinateRespEntity> readonlyHotelListFromES(String citycode, String keyword) {
		int limit = 5;
		SearchRequestBuilder searchBuilder = esProxy.prepareSearch();
		List<FilterBuilder> filterBuilders = new ArrayList<FilterBuilder>();
		QueryFilterBuilder citycodeFilter = FilterBuilders.queryFilter(QueryBuilders.termQuery("hotelcity", citycode));
		filterBuilders.add(citycodeFilter);

		QueryFilterBuilder keywordFilter = FilterBuilders
				.queryFilter(QueryBuilders.matchQuery("hotelname", keyword).operator(Operator.AND));
		filterBuilders.add(keywordFilter);
		// 只搜索签约酒店
		filterBuilders.add(FilterBuilders.termFilter("ispms", 1));

		FilterBuilder[] builders = new FilterBuilder[] {};
		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().must(filterBuilders.toArray(builders));
		// 只显示上线酒店和在线酒店
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
				.must(QueryBuilders.matchQuery("visible", Constant.STR_TRUE))
				.must(QueryBuilders.matchQuery("online", Constant.STR_TRUE));
		boolFilter.must(FilterBuilders.queryFilter(boolQueryBuilder));
		searchBuilder.setFrom(0).setSize(limit).setExplain(true);
		searchBuilder.setPostFilter(boolFilter);
		SearchResponse searchResponse = searchBuilder.execute().actionGet();
		SearchHits searchHits = searchResponse.getHits();
		long totalHits = searchResponse.getHits().totalHits();
		logger.info("search hotel by citycode:{},keywork{}, success: total {} found.", citycode, keyword, totalHits);
		SearchHit[] hits = searchHits.getHits();
		List<SearchPositionsCoordinateRespEntity> datas = Lists.newArrayList();
		for (int i = 0; i < hits.length; i++) {
			SearchHit hit = hits[i];
			Map<String, Object> result = hit.getSource();
			SearchPositionsCoordinateRespEntity ds = new SearchPositionsCoordinateRespEntity();
			ds.setId(Long.valueOf(result.get("hotelid").toString()));
			ds.setName(result.get("hotelname").toString());
			ds.setType("8");// 8: 酒店
			int tid = Integer.parseInt("8");
			ds.setTname(PositionTypeEnum.getById(tid).getTypeName());
			String coor = "";
			if (result.get("pin") != null) {
				Map<String, Object> pin = (Map<String, Object>) result.get("pin");
				String lon = pin.get("lon").toString();
				String lat = pin.get("lat").toString();
				coor = "[[" + lon + ", " + lat + "]]";
			}
			ds.setCoordinates(coor);
			datas.add(ds);
		}
		return datas;
	}

	/**
	 * 同步城市位置区域数据到es。
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @param typeid
	 *            参数：数据分类id
	 * @param forceUpdate
	 *            参数：是否强制更新
	 */
	@Override
	public Map<String, Object> readonlySyncCityPOI(String citycode, String typeid, boolean forceUpdate) {
		Map<String, Object> data = Maps.newHashMap();
		try {
			Map<String, Object> result = Maps.newHashMap();
			List<Map<String, Object>> messages = Lists.newArrayList();
			// 行政区
			result = Maps.newHashMap();
			result.putAll(this.readonlySyncCityAreas(citycode, forceUpdate));
			messages.add(result);

			// 地标
			result = Maps.newHashMap();
			result.putAll(this.readonlySyncCityLandmarks(citycode, forceUpdate));
			messages.add(result);

			// 地铁线路
			result = Maps.newHashMap();
			result.putAll(this.readonlySyncCitySubways(citycode, forceUpdate));
			messages.add(result);

			//
			data.put(ServiceOutput.STR_MSG_SUCCESS, true);
			data.put("message", messages);
		} catch (Exception e) {
			data.put(ServiceOutput.STR_MSG_SUCCESS, false);
			data.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			data.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
		}
		return data;
	}

	/**
	 * 添加es行政区文档数据
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @return
	 */
	private Map<String, Object> addAreaDocs(String citycode) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			List<SAreaInfo> areainfos = sareaInfoMapper.findAll(citycode);
			if (areainfos == null || areainfos.size() == 0) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "未找到城市：" + citycode + "的行政区数据。");
				return result;
			}
			Collection<Object> esdatas = new ArrayList<Object>();
			for (SAreaInfo areainfo : areainfos) {
				Map<String, Object> data = Maps.newHashMap();
				data.put("id", areainfo.getId());
				data.put("areaid", areainfo.getAreaid());
				data.put("name", areainfo.getAreaname());
				data.put("pinyin", areainfo.getPinyin());
				data.put("lat", areainfo.getLat());
				data.put("lng", areainfo.getLng());
				data.put("ptype", areainfo.getLtype());
				data.put("citycode", areainfo.getCitycode());
				data.put("discode", areainfo.getDiscode());
				data.put("status", areainfo.getStatus());
				esdatas.add(data);
			}
			if (esdatas.size() > 0) {
				esProxy.batchAddDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT,
						esdatas);
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "城市: " + citycode + "添加" + esdatas.size() + "条行政区。");
			}
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG,
					"citycode: " + citycode + ", addAreaDocs:: method error: " + e.getLocalizedMessage());
			logger.error("citycode: {}, addAreaDocs:: method error: {}", citycode, e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 同步城市行政区数据到es。
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @param forceUpdate
	 *            参数：是否强制更新
	 */
	private Map<String, Object> readonlySyncCityAreas(String citycode, boolean forceUpdate) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			// 先删除行政区数据，然后再添加
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.AREA.getId()));
			if (Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				// 删除成功后添加行政区数据
				result.clear();
				result.putAll(this.addAreaDocs(citycode));
			} else {
				logger.info("删除城市:{}行政区数据失败.", citycode);
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "删除城市:" + citycode + "行政区数据失败.");
			}
			//
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			logger.error("readonlySyncCityAreas:: error: {}", e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 添加es地标文档数据
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @return
	 */
	private Map<String, Object> addLandmarkDocs(String citycode) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			List<SLandMark> landmarks = slandMarkMapper.findAll(citycode);
			if (landmarks == null || landmarks.size() == 0) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "未找到城市：" + citycode + "的地标数据。");
				return result;
			}
			Collection<Object> esdatas = new ArrayList<Object>();
			for (SLandMark landmark : landmarks) {
				Map<String, Object> data = Maps.newHashMap();
				data.put("id", landmark.getId());
				data.put("areaid", landmark.getLandmarkid());
				data.put("name", landmark.getLandmarkname());
				data.put("pinyin", landmark.getPinyin());
				data.put("lat", landmark.getLat());
				data.put("lng", landmark.getLng());
				data.put("ptype", landmark.getLtype());
				data.put("citycode", landmark.getCitycode());
				data.put("discode", landmark.getDiscode());
				data.put("status", landmark.getStatus());
				esdatas.add(data);
			}
			if (esdatas.size() > 0) {
				esProxy.batchAddDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT,
						esdatas);
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "城市: " + citycode + "添加" + esdatas.size() + "条地标。");
			}
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG,
					"citycode: " + citycode + ", addLandmarkDocs:: method error: " + e.getLocalizedMessage());
			logger.error("citycode: {}, addLandmarkDocs:: method error: {}", citycode, e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 
	 * @param citycode
	 * @param forceUpdate
	 * @return
	 */
	private Map<String, Object> readonlySyncCityLandmarks(String citycode, boolean forceUpdate) {
		Map<String, Object> result = Maps.newHashMap();
		List<String> errors = Lists.newArrayList();
		try {
			// 先删除landmark数据，然后再添加
			// 删除1商圈
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.BZONE.getId()));
			if (!Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				errors.add(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)));
			}

			// 删除2机场车站
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.AIRPORT.getId()));
			if (!Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				errors.add(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)));
			}

			// 删除5景点
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.SAREA.getId()));
			if (!Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				errors.add(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)));
			}

			// 删除6医院
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.HOSPITAL.getId()));
			if (!Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				errors.add(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)));
			}

			// 删除7高校
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.COLLEGE.getId()));
			if (!Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				errors.add(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)));
			}

			if (errors.size() == 0) {
				result.clear();
				result.putAll(this.addLandmarkDocs(citycode));
			} else {
				result.clear();
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "同步城市: " + citycode + "地标数据出错.");
				result.put("message", errors);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG,
					"citycode: " + citycode + ", readonlySyncCityLandmarks:: method error: " + e.getLocalizedMessage());
			logger.error("citycode: {}, readonlySyncCityLandmarks:: method error: {}", citycode,
					e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 
	 * @param citycode
	 * @param forceUpdate
	 * @return
	 */
	private Map<String, Object> addSubwayDocs(String citycode) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			List<SSubway> subways = subwayMapper.findAll(citycode);
			if (subways == null || subways.size() == 0) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "未找到城市：" + citycode + "的地铁线路数据。");
				return result;
			}
			Collection<Object> esdatas = new ArrayList<Object>();
			for (SSubway subway : subways) {
				Map<String, Object> data = Maps.newHashMap();
				data.put("id", subway.getId());
				data.put("areaid", subway.getLineid());
				data.put("name", subway.getLinename());
				data.put("pinyin", "");
				data.put("ptype", subway.getLtype());
				data.put("citycode", subway.getCitycode());
				data.put("discode", "");
				data.put("status", subway.getStatus());

				// 查询地铁站点
				String lineid = subway.getLineid() == null ? "" : String.valueOf(subway.getLineid());
				List<SSubwayStation> stations = subwayStationMapper.findStations(citycode, lineid);
				data.put("stations", stations);

				//
				esdatas.add(data);
			}
			if (esdatas.size() > 0) {
				esProxy.batchAddDocument(ElasticsearchProxy.OTS_INDEX_DEFAULT, ElasticsearchProxy.POSITION_TYPE_DEFAULT,
						esdatas);
				result.put(ServiceOutput.STR_MSG_SUCCESS, true);
				result.put("message", "城市: " + citycode + "添加" + esdatas.size() + "条地铁线路。");
			}
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG,
					"citycode: " + citycode + ", addAreaDocs:: method error: " + e.getLocalizedMessage());
			logger.error("citycode: {}, addAreaDocs:: method error: {}", citycode, e.getLocalizedMessage());
		}
		return result;
	}

	/**
	 * 同步城市地铁线路数据到es。
	 * 
	 * @param citycode
	 *            参数：城市编码
	 * @param forceUpdate
	 *            参数：是否强制更新
	 */
	private Map<String, Object> readonlySyncCitySubways(String citycode, boolean forceUpdate) {
		Map<String, Object> result = Maps.newHashMap();
		try {
			// 先删除地铁线路数据，然后再添加
			result.clear();
			result.putAll(otsAdminService.readonlyDeletePoiDatas(citycode, HotelSearchEnum.SUBWAY.getId()));
			if (Boolean.valueOf(String.valueOf(result.get(ServiceOutput.STR_MSG_SUCCESS)))) {
				// 删除成功后添加地铁线路数据
				result.clear();
				result.putAll(this.addSubwayDocs(citycode));
			} else {
				logger.info("删除城市:{}地铁线路数据失败.", citycode);
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "删除城市:" + citycode + "地铁线路数据失败.");
			}
			//
		} catch (Exception e) {
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getLocalizedMessage());
			logger.error("readonlySyncCitySubways:: error: {}", e.getLocalizedMessage());
		}
		return result;
	}

}
