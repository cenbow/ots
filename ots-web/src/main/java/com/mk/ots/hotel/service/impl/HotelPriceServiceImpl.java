package com.mk.ots.hotel.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mk.framework.es.ElasticsearchProxy;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.common.utils.DateTools;
import com.mk.ots.hotel.bean.EDailyRate;
import com.mk.ots.hotel.bean.ERackRate;
import com.mk.ots.hotel.bean.ERackRateExample;
import com.mk.ots.hotel.bean.EWeekendRate;
import com.mk.ots.hotel.bean.EWeekendRateExample;
import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.hotel.bean.TDailyRate;
import com.mk.ots.hotel.bean.TDailyRateExample;
import com.mk.ots.hotel.bean.TRackRate;
import com.mk.ots.hotel.bean.TRackRateExample;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.bean.TSettingMikeRate;
import com.mk.ots.hotel.bean.TSettingMikeRateExample;
import com.mk.ots.hotel.bean.TWeekendRate;
import com.mk.ots.hotel.bean.TWeekendRateExample;
import com.mk.ots.hotel.jsonbean.hotelprice.DailyRateInfoJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.DailyRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.RackRateInfoJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.RackRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.WeekendRateInfoDataJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.WeekendRateInfoJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.WeekendRateJsonBean;
import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.model.TRoomTypeModel;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.manager.OtsCacheManager;
import com.mk.ots.mapper.EDailyRateMapper;
import com.mk.ots.mapper.EHotelMapper;
import com.mk.ots.mapper.ERackRateMapper;
import com.mk.ots.mapper.EWeekendRateMapper;
import com.mk.ots.mapper.TDailyRateMapper;
import com.mk.ots.mapper.THotelMapper;
import com.mk.ots.mapper.TRackRateMapper;
import com.mk.ots.mapper.TRoomTypeMapper;
import com.mk.ots.mapper.TSettingMikeRateMapper;
import com.mk.ots.mapper.TWeekendRateMapper;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.pricedrop.model.BStrategyPrice;
import com.mk.ots.pricedrop.service.IBStrategyPriceService;
import com.mk.pms.room.service.PmsRoomService;

@Service
public class HotelPriceServiceImpl implements HotelPriceService {
	
	/**
	 * 平日价redis key
	 */
	public static String PRICE_RACK_KEY = "PRICE_RACK";
	/**
	 * 周末价redis key
	 */
	public static String PRICE_WEEKEND_KEY = "PRICE_WEEKEND";
	/**
	 * 特殊价redis key
	 */
	public static String PRICE_DAILY_KEY = "PRICE_DAILY";
	/**
	 * hms设置价格下浮redis key(全局设置)
	 */
	public static String PRICE_SETTING_WHOLE_KEY = "PRICE_SETTING_WHOLE";
	/**
	 * hms设置价格下浮redis key(按天设置)
	 */
	public static String PRICE_SETTING_KEY = "PRICE_SETTING";
	

	// private Gson gson = new Gson();

	private static final Logger log = LoggerFactory.getLogger(HotelPriceServiceImpl.class);

	@Autowired
	private TRackRateMapper tRackRateMapper;
	@Autowired
	private ERackRateMapper eRackRateMapper;
	@Autowired
	private TWeekendRateMapper tWeekendRateMapper;
	@Autowired
	private EWeekendRateMapper eWeekendRateMapper;
	@Autowired
	private TDailyRateMapper tDailyRateMapper;
	@Autowired
	private EDailyRateMapper eDailyRateMapper;
	@Autowired
	private PmsRoomService pmsRoomService;
	@Autowired
	private EHotelMapper eHotelMapper;
	@Autowired
	private THotelMapper tHotelMapper;
	@Autowired
	private TRoomTypeMapper tRoomTypeMapper;
	@Autowired
	private TSettingMikeRateMapper tSettingMikeRateMapper;
	@Autowired
	private OtsCacheManager cacheManager;
	@Autowired
	private IBStrategyPriceService strategryPriceService;
	@Autowired
	protected ElasticsearchProxy esProxy;

	/**
	 * @param hotelPMS
	 * 通知酒店同步动态门市价
	 */
	@Override
	public Map<String, Object> sendSyncPriceMessToPMS(String hotelPMS) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		JSONObject hotel = new JSONObject();
		hotel.put("hotelid", hotelPMS);

		String resultJSONStr = doPostJson(UrlUtils.getUrl("newpms.url") + "/synchotelprice", hotel.toJSONString());
		JSONObject jsonOBJ = null;
		try {
			jsonOBJ = JSON.parseObject(resultJSONStr);
			
			if (jsonOBJ.getBooleanValue("success")) {
				resultMap.put("success", true);
			} else {
				log.info("PMS2.0安装PMS,同步酒店价格失败:{}", jsonOBJ.getString("errmsg"));
				resultMap.put("success", false);
				resultMap.put("errcode", jsonOBJ.getString("errcode"));
				resultMap.put("errmsg", jsonOBJ.getString("errmsg"));
			}
		} catch (Exception e) {
			log.error("PMS2.0安装PMS 同步酒店价格失败,json转换错误:{}", e.fillInStackTrace());
			throw MyErrorEnum.errorParm.getMyException("PMS2.0安装PMS同步酒店价格失败，json转换错误.");
		}
		return resultMap;
	}

	private String doPostJson(String url, String json) {
		JSONObject back = new JSONObject();
		try {
			return PayTools.dopostjson(url, json);
		} catch (Exception e) {
			log.info("doPostJson参数:{},{},异常:{}", url, json,
					e.getLocalizedMessage());
			e.printStackTrace();
			back.put("success", false);
			back.put("errorcode", "-1");
			back.put("errormsg", e.getLocalizedMessage());
		}
		return back.toJSONString();
	}

	/**
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 * 查询e表平日价
	 */
	@Override
	public ERackRate findERackRateByConditions(Long hotelid, Long roomtypeid)
			throws Exception {
		ERackRateExample eRackRateExample = new ERackRateExample();
		eRackRateExample.createCriteria().andEhotelidEqualTo(hotelid)
				.andRoomtypeidEqualTo(roomtypeid);
		List<ERackRate> list = eRackRateMapper
				.selectByExample(eRackRateExample);
		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * @param hotelid
	 * 查询t表平日价列表
	 */
	private List<TRackRate> findTRackRates(Long hotelid){
		TRackRateExample tRackRateExample = new TRackRateExample();
		tRackRateExample.createCriteria().andEhotelidEqualTo(hotelid);
		List<TRackRate> list = tRackRateMapper
				.selectByExample(tRackRateExample);
		return list;
	}
	/**
	 * @param hotelid
	 * 查询t表特殊价列表
	 */
	private List<TDailyRate> findTDailyRates(Long hotelid){
		TDailyRateExample tDailyRateExample = new TDailyRateExample();
		tDailyRateExample.createCriteria().andEhotelidEqualTo(hotelid);
		List<TDailyRate> list = tDailyRateMapper
				.selectByExample(tDailyRateExample);
		return list;
	}
	/**
	 * @param hotelid
	 * 查询t表h端设置的下浮
	 */
	private List<TSettingMikeRate> findSettingMikeRates(Long hotelid){
		TSettingMikeRateExample tSettingMikeRateExample = new TSettingMikeRateExample();
		tSettingMikeRateExample.createCriteria().andEhotelidEqualTo(hotelid);
		List<TSettingMikeRate> list = tSettingMikeRateMapper
				.selectByExample(tSettingMikeRateExample);
		return list;
	}
	

	/**
	 * @param eRackRate
	 * 保存e表平日价
	 */
	@Override
	public int saveERackRate(ERackRate eRackRate) {
		return eRackRateMapper.insertSelective(eRackRate);
	}

	/**
	 * @param eRackRate
	 * 更新e表平日价
	 */
	@Override
	public int updateERackRate(ERackRate eRackRate) {
		return eRackRateMapper.updateByPrimaryKeySelective(eRackRate);
	}

	/**
	 * @param hotelid
	 * 查询e表特殊价列表
	 */
	@Override
	public List<EDailyRate> findEDailyRateByHotelid(Long hotelid) {
		return eDailyRateMapper.selectByHotelid(hotelid);
	}

	/**
	 * @param insertBatchList
	 * 批量保存e表特殊价
	 */
	@Override
	public Integer saveBatchEDailyRate(List<EDailyRate> insertBatchList) {
		return eDailyRateMapper.saveBatch(insertBatchList);
	}

	/**
	 * @param updateBatchList
	 * 批量更新e表特殊价
	 */
	@Override
	public Integer updateBatchEDailyRate(List<EDailyRate> updateBatchList) {
		return eDailyRateMapper.updateBatch(updateBatchList);
	}

	/**
	 * @param dailyRateJsonBean
	 * @param ehotelid
	 * 同步e表特殊价
	 */
	@Override
	public Integer syncDailyRate(DailyRateJsonBean dailyRateJsonBean, Long ehotelid) {
		try {
			List<EDailyRate> updateBatchList = new ArrayList<EDailyRate>();
			List<EDailyRate> insertBatchList = new ArrayList<EDailyRate>();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

			List<EDailyRate> edailyrates = this.findEDailyRateByHotelid(ehotelid);

			for (DailyRateInfoJsonBean info : dailyRateJsonBean.getRoomtype()) {
				String roomtypepms = info.getId();
				if (StringUtils.isEmpty(roomtypepms)) {
					throw MyErrorEnum.errorParm.getMyException("参数错误!roomtype中id为空");
				}
				List<DailyRateInfoJsonBean.RoomTypeData> data = info.getData();
				if (data.size() == 0) {
					throw MyErrorEnum.errorParm.getMyException("参数错误!roomtype中data为空");
				}

				// t_roomtype 根据pms查询
				TRoomType roomType = pmsRoomService.findTRoomTypeByPmsno(ehotelid, roomtypepms);
				if (roomType == null) {
					log.error("未找到hotelid为"+ ehotelid + ",pms为 " + roomtypepms + " 的房型信息");
					continue;
				}
				Long roomtypeid = roomType.getLong("id");

				// 构建数据集 update list
				for (EDailyRate eDailyRate : edailyrates) {
					Iterator<DailyRateInfoJsonBean.RoomTypeData> iterator = data.iterator();
					while (iterator.hasNext()) {
						DailyRateInfoJsonBean.RoomTypeData roomTypeData = iterator.next();
						//如果存在相同的特殊价就更新
						if (eDailyRate.getRoomtypeid().longValue() == roomtypeid.longValue() && eDailyRate.getDay().longValue() == (StringUtils.isBlank(roomTypeData.getDay()) ? null : Long.valueOf(roomTypeData.getDay())).longValue()) {
							eDailyRate.setPrice(StringUtils.isBlank(roomTypeData.getPrice()) ? null : new BigDecimal(roomTypeData.getPrice()));
							eDailyRate.setUpdatetime(sdf.format(new Date()));
							eDailyRate.setUpdateuser("pms");
							//如果相等就不更新
							if((roomTypeData.getPrice() == null ? null : new BigDecimal(roomTypeData.getPrice())) != eDailyRate.getPrice())
								updateBatchList.add(eDailyRate);
							iterator.remove();
						}
					}
				}

				// 构建数据集 insert list
				for (DailyRateInfoJsonBean.RoomTypeData rtdata : data) {
					EDailyRate eDailyRate = new EDailyRate();
					eDailyRate.setEhotelid(ehotelid);
					eDailyRate.setRoomtypeid(roomtypeid);
					eDailyRate.setDay(StringUtils.isBlank(rtdata.getDay()) ? null : Long.valueOf(rtdata.getDay()));
					eDailyRate.setPrice(StringUtils.isBlank(rtdata.getPrice()) ? null : new BigDecimal(rtdata.getPrice()));
					eDailyRate.setCreatetime(sdf.format(new Date()));
					eDailyRate.setCreateuser("pms");
					eDailyRate.setUpdatetime(sdf.format(new Date()));
					eDailyRate.setUpdateuser("pms");
					insertBatchList.add(eDailyRate);
				}
			}
			Integer addres = 0;
			if(insertBatchList.size() > 0)
				addres = this.saveBatchEDailyRate(insertBatchList);
			Integer updateres = 0;
			if(updateBatchList.size() > 0)
				updateres= this.updateBatchEDailyRate(updateBatchList);
			return addres + updateres;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 * 查询e表周末价列表
	 */
	@Override
	public List<EWeekendRate> findEWeekendRatesByConditions(Long hotelid,
			Long roomtypeid) throws Exception {
		EWeekendRateExample eWeekendRateExample = new EWeekendRateExample();
		eWeekendRateExample.createCriteria().andEhotelidEqualTo(hotelid)
				.andRoomtypeidEqualTo(roomtypeid);
		return eWeekendRateMapper.selectByExample(eWeekendRateExample);
	}
	/**
	 * @param hotelid
	 * 查询t表周末价列表
	 */
	public List<TWeekendRate> findTWeekendRates(Long hotelid){
		TWeekendRateExample tWeekendRateExample = new TWeekendRateExample();
		tWeekendRateExample.createCriteria().andEhotelidEqualTo(hotelid);
		return tWeekendRateMapper.selectByExample(tWeekendRateExample);
	}

	/**
	 * @param eWeekendRate
	 * 保存e表周末价
	 */
	@Override
	public int saveEWeekendRate(EWeekendRate eWeekendRate) {
		return eWeekendRateMapper.insertSelective(eWeekendRate);
	}

	/**
	 * @param eWeekendRate
	 * 更新e表周末价
	 */
	@Override
	public int updateEWeekendRate(EWeekendRate eWeekendRate) {
		return eWeekendRateMapper.updateByPrimaryKeySelective(eWeekendRate);
	}

	/**
	 * @param rackRateJsonBean
	 * @param ehotelid
	 * @return
	 * 同步e表平日价
	 */
	@Override
	public boolean syncrackrate(RackRateJsonBean rackRateJsonBean, Long ehotelid)
			throws Exception {
		boolean ischange = false;
		for (RackRateInfoJsonBean info : rackRateJsonBean.getRoomtype()) {
			String roomtypepms = info.getId();
			if (StringUtils.isEmpty(roomtypepms)) {
				throw MyErrorEnum.errorParm
						.getMyException("参数错误!roomtype中id为空");
			}
			String price = info.getPrice();
			if (StringUtils.isEmpty(price)) {
				throw MyErrorEnum.errorParm
						.getMyException("参数错误!roomtype中price为空");
			}
			BigDecimal newprice = new BigDecimal(price);
			// t_roomtype 根据pms查询
			TRoomType roomType = pmsRoomService.findTRoomTypeByPmsno(ehotelid,
					roomtypepms);
			if (roomType == null) {
				log.error("未找到hotelid为"+ ehotelid + ",pms为 " + roomtypepms + " 的房型信息");
				continue;
			}
			Long roomtypeid = roomType.getLong("id");
			// 判断t_rack_rate表中是否存在记录
			ERackRate erackrate = findERackRateByConditions(ehotelid,
					roomtypeid);
			Calendar c = Calendar.getInstance();
			if (erackrate == null) {
				// 添加
				erackrate = new ERackRate();
				erackrate.setCreatetime(c.getTime());
				erackrate.setCreateuser("PMS");
				erackrate.setUpdatetime(c.getTime());
				erackrate.setUpdateuser("PMS");
				erackrate.setEhotelid(ehotelid);
				erackrate.setPrice(newprice);
				erackrate.setRoomtypeid(roomtypeid);
				saveERackRate(erackrate);
				ischange = true;
			} else {
				// 更新
				// 判断价格是否更改
				BigDecimal oldprice = erackrate.getPrice();

				BigDecimal a = oldprice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
				BigDecimal b = newprice.setScale(2, BigDecimal.ROUND_HALF_DOWN);
				if (!a.equals(b)) {
					erackrate.setUpdatetime(c.getTime());
					erackrate.setUpdateuser("PMS");
					erackrate.setPrice(newprice);
					updateERackRate(erackrate);
					ischange = true;
				}
			}
		}
		return ischange;

	}

	/**
	 * @param weekendRateJsonBean
	 * @param ehotelid
	 * @return
	 * 同步e表周末价
	 */
	@Override
	public boolean syncweekend(WeekendRateJsonBean weekendRateJsonBean,
			Long ehotelid) throws Exception {
		boolean ischange = false;
		for (WeekendRateInfoJsonBean info : weekendRateJsonBean.getRoomtype()) {
			String roomtypepms = info.getId();
			if (StringUtils.isEmpty(roomtypepms)) {
				throw MyErrorEnum.errorParm
						.getMyException("参数错误!roomtype中id为空");
			}
			List<WeekendRateInfoDataJsonBean> data = info.getData();
			if (data.size() == 0) {
				throw MyErrorEnum.errorParm.getMyException("参数错误!data为空");
			}
			// t_roomtype 根据pms查询
			TRoomType roomType = pmsRoomService.findTRoomTypeByPmsno(ehotelid,
					roomtypepms);
			if (roomType == null) {
				log.error("未找到hotelid为"+ ehotelid + ",pms为 " + roomtypepms + " 的房型信息");
				continue;
			}
			Long roomtypeid = roomType.getLong("id");
			// 判断t_rack_rate表中是否存在记录
			List<EWeekendRate> eweekendrates = findEWeekendRatesByConditions(
					ehotelid, roomtypeid);
			Calendar c = Calendar.getInstance();
			// 更新
			// List<EWeekendRate> insertlist = new ArrayList<EWeekendRate>();
			// List<EWeekendRate> updatelist = new ArrayList<EWeekendRate>();
			// List<EWeekendRate> deletelist = new ArrayList<EWeekendRate>();

			// 循环已有数据集
			Map<String, EWeekendRate> existsMap = new HashMap<String, EWeekendRate>();
			for (EWeekendRate eWeekendRate : eweekendrates) {
				existsMap.put(eWeekendRate.getWeek() + "", eWeekendRate);
			}

			// 循环传入数据并找出需要插入的记录
			Map<String, WeekendRateInfoDataJsonBean> dataMap = new HashMap<String, WeekendRateInfoDataJsonBean>();
			for (WeekendRateInfoDataJsonBean weekendRateInfoDataJsonBean : data) {
				String week = weekendRateInfoDataJsonBean.getWeek();
				if (existsMap.containsKey(week)) {
					dataMap.put(week, weekendRateInfoDataJsonBean);
				} else {
					EWeekendRate eweekendrate = new EWeekendRate();
					eweekendrate.setCreatetime(c.getTime());
					eweekendrate.setCreateuser("PMS");
					eweekendrate.setUpdatetime(c.getTime());
					eweekendrate.setUpdateuser("PMS");
					eweekendrate.setEhotelid(ehotelid);
					eweekendrate.setWeek(Integer
							.parseInt(weekendRateInfoDataJsonBean.getWeek()));
					eweekendrate.setPrice(new BigDecimal(
							weekendRateInfoDataJsonBean.getPrice()));
					eweekendrate.setRoomtypeid(roomtypeid);
					saveEWeekendRate(eweekendrate);
					ischange = true;
				}

			}

			// 循环数据库结果集
			for (EWeekendRate eWeekendRate : eweekendrates) {
				String week = eWeekendRate.getWeek() + "";
				if (dataMap.containsKey(week)) {
					WeekendRateInfoDataJsonBean weekendRateInfoDataJsonBean = dataMap
							.get(week);
					BigDecimal newprice = new BigDecimal(
							weekendRateInfoDataJsonBean.getPrice());
					BigDecimal oldprice = eWeekendRate.getPrice();
					BigDecimal a = oldprice.setScale(2,
							BigDecimal.ROUND_HALF_DOWN);
					BigDecimal b = newprice.setScale(2,
							BigDecimal.ROUND_HALF_DOWN);
					if (!a.equals(b)) {
						// 更新
						eWeekendRate.setUpdatetime(c.getTime());
						eWeekendRate.setUpdateuser("PMS");
						eWeekendRate.setPrice(newprice);
						updateEWeekendRate(eWeekendRate);
						ischange = true;
					}
				} else {
					// 删除
					eWeekendRateMapper.deleteByPrimaryKey(eWeekendRate.getId());
					ischange = true;
				}
			}
		}
		return ischange;

	}

	/**
	 * @param eHotelModel
	 * 更新e表hotel
	 */
	@Override
	public int updateHotel(EHotelModel eHotelModel) {
		return eHotelMapper.updateByPrimaryKeySelective(eHotelModel);
	}

	/**
	 * 刷新缓存特殊价、周末价、门市价、h端下浮比例（按日期和房型）
	 */
	@Override
	public void refreshMikePrices(Long hotelidparam) {
		
		//不传hotelid 查所有
		List<THotelModel> hotellist = new ArrayList<THotelModel>();
		if(hotelidparam==null){
			hotellist =  tHotelMapper.findList();
		}else{
			THotelModel tHotelModel = tHotelMapper.selectById(hotelidparam);
			if(tHotelModel!=null){
				hotellist.add(tHotelModel);
			}
		}
		for (THotelModel tHotelModel:hotellist) {
			String isnewpms = tHotelModel.getIsnewpms();
			Long hotelid = tHotelModel.getId();
			if("T".equals(isnewpms)){
				//存平日价开始
					Map<String,String> rackRateMap = new HashMap<String,String>();
					List<TRackRate> rackRateList= findTRackRates(hotelid);
					for (TRackRate tRackRate:rackRateList) {
						rackRateMap.put(tRackRate.getRoomtypeid().toString(), tRackRate.getPrice().toString());
					}
					saveRackRateToCache(hotelid,rackRateMap);
				//存平日价结束
				
				//存周末价开始
					Map<String,String> weekendRateMap = new HashMap<String,String>();
					List<TWeekendRate> weekendRateList= findTWeekendRates(hotelid);
					for(TWeekendRate tWeekendRate:weekendRateList){
						weekendRateMap.put(tWeekendRate.getRoomtypeid()+"@"+tWeekendRate.getWeek(), tWeekendRate.getPrice().toString());
					}
					saveWeekendRateToCache(hotelid,weekendRateMap);
				//存周末价结束
				
				//存特殊价开始
					Map<String,String> dailyRateMap = new HashMap<String,String>();
					List<TDailyRate> dailyRateList= findTDailyRates(hotelid);
					for(TDailyRate tDailyRate:dailyRateList){
						dailyRateMap.put(tDailyRate.getRoomtypeid()+"@"+tDailyRate.getDay(), tDailyRate.getPrice().toString());
					}
					saveDailyRateToCache(hotelid,dailyRateMap);
				//存特殊价结束
					
				//存hms设置价格下浮开始
					Map<String,String> settingMikeRateWholeMap = new HashMap<String,String>();
					Map<String,String> settingMikeRateDayMap = new HashMap<String,String>();
					List<TSettingMikeRate> settingMikeRateList= findSettingMikeRates(hotelid);
					for(TSettingMikeRate tSettingMikeRate:settingMikeRateList){
						String subprice = tSettingMikeRate.getSubprice()==null?"-":tSettingMikeRate.getSubprice().toString();
						String subper = tSettingMikeRate.getSubper()==null?"-":tSettingMikeRate.getSubper().toString();
						if(tSettingMikeRate.getSettingtype()==1){
							//全局设置
							settingMikeRateWholeMap.put(tSettingMikeRate.getRoomtypeid().toString(), subprice+"@"+subper);
						}else if(tSettingMikeRate.getSettingtype()==2){
							//指定日期设置
							settingMikeRateDayMap.put(tSettingMikeRate.getRoomtypeid().toString()+"@"+tSettingMikeRate.getSettingtime(), subprice+"@"+subper);
						}
					}
					saveWholePriceSettingToCache(hotelid,settingMikeRateWholeMap);
					savePriceSettingToCache(hotelid,settingMikeRateDayMap);
				//存hms设置价格下浮结束
			}else{
				//1.0平日价取roomtype表cost字段
				//存平日价开始
					Map<String,String> rackRateMap = new HashMap<String,String>();
					List<TRoomTypeModel> roomtypeList = tRoomTypeMapper.findTRoomTypeByHotelid(hotelid);
					for (TRoomTypeModel tRoomTypeModel:roomtypeList) {
						rackRateMap.put(tRoomTypeModel.getId().toString(), tRoomTypeModel.getCost().toString());
					}
					saveRackRateToCache(hotelid,rackRateMap);
				//存平日价结束
					//存hms设置价格下浮开始
					Map<String,String> settingMikeRateWholeMap = new HashMap<String,String>();
					Map<String,String> settingMikeRateDayMap = new HashMap<String,String>();
					List<TSettingMikeRate> settingMikeRateList= findSettingMikeRates(hotelid);
					for(TSettingMikeRate tSettingMikeRate:settingMikeRateList){
						String subprice = tSettingMikeRate.getSubprice()==null?"-":tSettingMikeRate.getSubprice().toString();
						String subper = tSettingMikeRate.getSubper()==null?"-":tSettingMikeRate.getSubper().toString();
						if(tSettingMikeRate.getSettingtype()==1){
							//全局设置
							settingMikeRateWholeMap.put(tSettingMikeRate.getRoomtypeid().toString(), subprice+"@"+subper);
						}else if(tSettingMikeRate.getSettingtype()==2){
							//指定日期设置
							settingMikeRateDayMap.put(tSettingMikeRate.getRoomtypeid().toString()+"@"+tSettingMikeRate.getSettingtime(), subprice+"@"+subper);
						}
					}
					saveWholePriceSettingToCache(hotelid,settingMikeRateWholeMap);
					savePriceSettingToCache(hotelid,settingMikeRateDayMap);
				//存hms设置价格下浮结束
			}
		}
	}
	/**
	 * 缓存门市价
	 */
	private void saveRackRateToCache(Long hotelid,Map<String,String> map){
		String key = PRICE_RACK_KEY+":"+hotelid;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		if(map!=null && !map.isEmpty()){
			cacheManager.hmset(key, map);
		}
	}
	/**
	 * 缓存周末价
	 */
	private void saveWeekendRateToCache(Long hotelid,Map<String,String> map){
		String key = PRICE_WEEKEND_KEY+":"+hotelid;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		if(map!=null && !map.isEmpty()){
			cacheManager.hmset(key, map);
		}
	}
	/**
	 * 缓存特殊价
	 */
	private void saveDailyRateToCache(Long hotelid,Map<String,String> map){
		String key = PRICE_DAILY_KEY+":"+hotelid;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		if(map!=null && !map.isEmpty()){
			cacheManager.hmset(key, map);
		}
	}
	/**
	 * 缓存h端下浮设置(全局设置)
	 */
	private void saveWholePriceSettingToCache(Long hotelid,Map<String,String> map){
		String key = PRICE_SETTING_WHOLE_KEY+":"+hotelid;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		if(map!=null && !map.isEmpty()){
			cacheManager.hmset(key, map);
		}
	}
	/**
	 * 缓存h端下浮设置(按天设置)
	 */
	private void savePriceSettingToCache(Long hotelid,Map<String,String> map){
		String key = PRICE_SETTING_KEY+":"+hotelid;
		if(cacheManager.isExistKey(key)){
			cacheManager.del(key);
		}
		if(map!=null && !map.isEmpty()){
			cacheManager.hmset(key, map);
		}
	}
	

	/**
	 * @param hotelid
	 * 从缓存中查询所有动态门市价相关信息
	 */
	@Override
	public Map<String,Map<String,String>> getPriceConfigsFromCache(Long hotelid) {
		Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
		Map<String,String> amap= cacheManager.hgetAll(PRICE_RACK_KEY+":"+hotelid);
		Map<String,String> bmap= cacheManager.hgetAll(PRICE_WEEKEND_KEY+":"+hotelid);
		Map<String,String> cmap= cacheManager.hgetAll(PRICE_DAILY_KEY+":"+hotelid);
		Map<String,String> dmap= cacheManager.hgetAll(PRICE_SETTING_WHOLE_KEY+":"+hotelid);
		Map<String,String> emap= cacheManager.hgetAll(PRICE_SETTING_KEY+":"+hotelid);
		map.put(PRICE_RACK_KEY, amap);
		map.put(PRICE_WEEKEND_KEY, bmap);
		map.put(PRICE_DAILY_KEY, cmap);
		map.put(PRICE_SETTING_WHOLE_KEY, dmap);
		map.put(PRICE_SETTING_KEY, emap);
		return map;
	}

	
	/**
	 * @param hotelid
	 * @param roomtypeid
	 * @param startday
	 * @param endday
	 * 查询房型在时间段内每天的眯客价及门市价（供订单组调用）
	 */
	@Override
	public List<RoomTypePriceBean> getRoomtypePrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday) {
	    try {
	        Map<String, Map<String,String>> rtnMap = getPriceConfigsFromCache(hotelid);
	        if (rtnMap == null || rtnMap.size() == 0) {
	            return null;
	        }
	       
	        // 酒店眯客特殊价格map
	        Map<String, String> mikeDailyMap = (Map<String, String>) rtnMap.get(PRICE_DAILY_KEY);
	        // 酒店眯客周末价格map
	        Map<String, String> mikeWeekendMap = (Map<String, String>) rtnMap.get(PRICE_WEEKEND_KEY);
	        // 酒店眯客门市价格map
	        Map<String, String> mikeRackMap = (Map<String, String>) rtnMap.get(PRICE_RACK_KEY);
	        // 酒店当日减幅
	        Map<String, String> settingMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_KEY);
	        // 酒店总减幅
	        Map<String, String> settingWholeMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_WHOLE_KEY);
	       
	        List<RoomTypePriceBean> roomTypePriceBeans = new ArrayList<RoomTypePriceBean>();
	        
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        
	        //如果开始时间等于结束时间 认为当天住当天离,当天价格计算至mike价内, 为完成改日价格计算,故结束时间+1天
            if(startdateday.equals(enddateday)) {
            	Calendar endCal = Calendar.getInstance();
            	endCal.setTime(sdf.parse(enddateday));
            	endCal.add(Calendar.DAY_OF_MONTH, 1);
            	enddateday = sdf.format(endCal.getTime());
            }
	        
        	Calendar cal = Calendar.getInstance();
        	cal.setTime(sdf.parse(startdateday));
        	
        	//从开始时间 开始轮循+1天,计算mike价,结束日期当天认为顾客离店所以不计算眯客价格
        	while(!sdf.format(cal.getTime()).equals(enddateday)) {
		        //有特殊价
		        if(mikeDailyMap != null && mikeDailyMap.size() > 0) {                
                	String key = "" + roomtypeid + "@" + sdf.format(cal.getTime());
                	String val = mikeDailyMap.get(key);
                	if(StringUtils.isNotBlank(val)) {
                		RoomTypePriceBean roomTypePriceBean = new RoomTypePriceBean();
                		val = getDampPrice(key, String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
                		roomTypePriceBean.setDay(sdf.format(cal.getTime()));
                		roomTypePriceBean.setRoomtypeid(roomtypeid);
                		roomTypePriceBean.setMikeprice(new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP));
                		String strRoomprice = mikeRackMap.get(roomtypeid + "");
                        if (StringUtils.isNotBlank(strRoomprice))
                        	roomTypePriceBean.setPrice(new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP));
                        roomTypePriceBeans.add(roomTypePriceBean);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        continue;
                	}
                }
		        //无特殊价,有周末价
		        if(mikeWeekendMap != null && mikeWeekendMap.size() > 0) {
		        	int weekDays = cal.get(Calendar.DAY_OF_WEEK) - 1;
		        	String key = "" + roomtypeid + "@" + weekDays;
	                String val = mikeWeekendMap.get(key);
	                if(StringUtils.isNotBlank(val)) {
	                	RoomTypePriceBean roomTypePriceBean = new RoomTypePriceBean();
                		val = getDampPrice(roomtypeid + "@" + sdf.format(cal.getTime()), String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
                		roomTypePriceBean.setDay(sdf.format(cal.getTime()));
                		roomTypePriceBean.setRoomtypeid(roomtypeid);
                		roomTypePriceBean.setMikeprice(new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP));
                		String strRoomprice = mikeRackMap.get(roomtypeid + "");
                        if (StringUtils.isNotBlank(strRoomprice))
                        	roomTypePriceBean.setPrice(new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP));
                        roomTypePriceBeans.add(roomTypePriceBean);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        continue;
	                }
			    }
		        //无特殊价，无周末价
		        {
		        	String val = mikeRackMap.get(roomtypeid + "");
		        	// 如果酒店也没有设置基本价和门市价
		        	if (StringUtils.isBlank(val)) {
		        		log.error("hotel ---- {},roomtype---{}, this day ----{} not have price", hotelid, roomtypeid, sdf.format(cal.getTime()));
		        		cal.add(Calendar.DAY_OF_MONTH, 1);
		        		continue;
		        	}
		        	
	                RoomTypePriceBean roomTypePriceBean = new RoomTypePriceBean();

            		val = getDampPrice(roomtypeid + "@" + sdf.format(cal.getTime()), String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
            		roomTypePriceBean.setDay(sdf.format(cal.getTime()));
            		roomTypePriceBean.setRoomtypeid(roomtypeid);
            		roomTypePriceBean.setMikeprice(new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP));
            		String strRoomprice = mikeRackMap.get(roomtypeid + "");
                    if (StringUtils.isNotBlank(strRoomprice))
                    	roomTypePriceBean.setPrice(new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP));
                    roomTypePriceBeans.add(roomTypePriceBean);
		        }
		        cal.add(Calendar.DAY_OF_MONTH, 1);
		        continue;
        	}
        	return roomTypePriceBeans;
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return null;
	}
	
	
	/**
	 * 得到酒店眯客价
	 * 返回值为字符串数组, 第1个为酒店的最低眯客价, 第2个为最低眯客价房型对应的门市价.
	 * @param hotelid
	 * @return String[]
	 * 返回值
	 */
	
	@Override
	public String[] getHotelMikePrices(Long hotelid, String startdateday, String enddateday) {
	    String[] resultVal = new String[2];
	    try {
	        Map<String, Map<String,String>> rtnMap = getPriceConfigsFromCache(hotelid);
	        if (rtnMap == null || rtnMap.size() == 0) {
	        	resultVal = new String[]{"111", "111"};
	            return resultVal;
	        }
	        // 酒店眯客特殊价格map
	        Map<String, String> mikeDailyMap = (Map<String, String>) rtnMap.get(PRICE_DAILY_KEY);
	        // 酒店眯客周末价格map
	        Map<String, String> mikeWeekendMap = (Map<String, String>) rtnMap.get(PRICE_WEEKEND_KEY);
	        // 酒店眯客门市价格map
	        Map<String, String> mikeRackMap = (Map<String, String>) rtnMap.get(PRICE_RACK_KEY);
	        // 酒店当日减幅
	        Map<String, String> settingMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_KEY);
	        // 酒店总减幅
	        Map<String, String> settingWholeMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_WHOLE_KEY);

	        //有特殊价
	        if(resultVal[0] == null && mikeDailyMap != null && mikeDailyMap.size() > 0) {
                Map<String, String> minDailyPriceMap = this.getMinDailyPrice(mikeDailyMap, startdateday, enddateday, settingMap, settingWholeMap);
                Iterator<Entry<String, String>> iter = minDailyPriceMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String val = entry.getValue();
                    if (StringUtils.isNotBlank(val)) {
                        resultVal[0] = new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP).toString(); 
                        // key的格式roomtypeid@date, 从key中解析roomtypeid
                        String key = entry.getKey();
                        String keyroomtypeid = key.split("@")[0];
                        String strRoomprice = mikeRackMap.get(keyroomtypeid);
                        if (StringUtils.isNotBlank(strRoomprice))
                            resultVal[1] = new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP).toString();  // 获得房型门市价.
                        break;
                    }
                }
	        }
	        //无特殊价,有周末价
	        if(resultVal[0] == null && mikeWeekendMap != null && mikeWeekendMap.size() > 0) {
                Map<String, String> minWeekendPriceMap = this.getMinWeekendPrice(mikeWeekendMap, startdateday, enddateday, settingMap, settingWholeMap);
                Iterator<Entry<String, String>> iter = minWeekendPriceMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String val = entry.getValue();
                    if (StringUtils.isNotBlank(val)) {
                        resultVal[0] = new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
                        // key的格式roomtypeid@date, 从key中解析roomtypeid
                        String key = entry.getKey();
                        String keyroomtypeid = key.split("@")[0];
                        String strRoomprice = mikeRackMap.get(keyroomtypeid);
                        if (StringUtils.isNotBlank(strRoomprice))
                            resultVal[1] = new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP).toString(); // 获得房型门市价.
                        break;
                    }
                }
	        }
	        //无特殊价，无周末价
	        if (resultVal[0] == null) {
	        	Map<String, String> minRoompriceMap = this.getMinRoomprice(mikeRackMap, startdateday, enddateday, settingMap, settingWholeMap);
	        	
	        	// 如果酒店也没有设置基本价和门市价
	        	if (minRoompriceMap == null || minRoompriceMap.size() == 0) {
	        		resultVal = new String[]{"111", "111"};
	        		return resultVal;
	        	}
	        	Iterator<Entry<String, String>> iter = minRoompriceMap.entrySet().iterator();
	        	while (iter.hasNext()) {
	        		Map.Entry<String, String> entry = iter.next();
	        		String val = entry.getValue();
	        		
	        		if (!StringUtils.isBlank(val)) {
	        			resultVal[0] = new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
	        			String key = entry.getKey();
                        String strRoomprice = mikeRackMap.get(key);
                        if (StringUtils.isNotBlank(strRoomprice))
                            resultVal[1] = new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP).toString(); // 获得房型门市价.
                        break;
	        		}
	            }
	        } 
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return resultVal;
	}
	
	/**
	 * 得到酒店房型眯客价
	 * @param hotelid
	 * 参数：酒店id
	 * @param roomtypeid
	 * 参数：房型id
	 * @param startdateday
	 * 参数：查询起始日期
	 * @param enddateday
	 * 参数：查询截止日期
	 * @return String[]
	 * 返回值
	 */
	@Override
	public String[] getRoomtypeMikePrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday) {
	    String[] resultVal = new String[2];
	    try {
	        Map<String, Map<String,String>> rtnMap = getPriceConfigsFromCache(hotelid);
	        if (rtnMap == null || rtnMap.size() == 0) {
	        	resultVal = new String[]{"111", "111"};
	            return resultVal;
	        }
	        // 酒店眯客特殊价格map
	        Map<String, String> mikeDailyMap = (Map<String, String>) rtnMap.get(PRICE_DAILY_KEY);
	        // 酒店眯客周末价格map
	        Map<String, String> mikeWeekendMap = (Map<String, String>) rtnMap.get(PRICE_WEEKEND_KEY);
	        // 酒店眯客门市价格map
	        Map<String, String> mikeRackMap = (Map<String, String>) rtnMap.get(PRICE_RACK_KEY);
	        // 酒店当日减幅
	        Map<String, String> settingMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_KEY);
	        // 酒店总减幅
	        Map<String, String> settingWholeMap = (Map<String, String>) rtnMap.get(PRICE_SETTING_WHOLE_KEY);
	    	
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        
	        
	        //如果开始时间等于结束时间 认为当天住当天离,当天价格计算至mike价内, 为完成改日价格计算,故结束时间+1天
            if(startdateday.equals(enddateday)) {
            	Calendar endCal = Calendar.getInstance();
            	endCal.setTime(sdf.parse(enddateday));
            	endCal.add(Calendar.DAY_OF_MONTH, 1);
            	enddateday = sdf.format(endCal.getTime());
            }
	        
	        //有特殊价
	        if(resultVal[0] == null && mikeDailyMap != null && mikeDailyMap.size() > 0) {
            	Calendar cal = Calendar.getInstance();
            	cal.setTime(sdf.parse(startdateday));
            	String minval = null;
            	
            	//从开始时间 开始轮循+1天,计算mike价,结束日期当天认为顾客离店所以不计算眯客价格
                while(!sdf.format(cal.getTime()).equals(enddateday)) {
                	String key = "" + roomtypeid + "@" + sdf.format(cal.getTime());
                	String val = mikeDailyMap.get(key);
                	if(StringUtils.isBlank(val)) {
                		cal.add(Calendar.DAY_OF_MONTH, 1);
                		continue;
                	}
                	val = getDampPrice(key, String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
    		        if (minval == null) {
    		        	minval = val;
    		        } else {
    		        	BigDecimal minprice = new BigDecimal(minval);
    		        	BigDecimal price = new BigDecimal(val);
    		        	if (price.compareTo(minprice) == -1) {
    		        		minval = val;
    		        	}
    		        }
    		        cal.add(Calendar.DAY_OF_MONTH, 1);
                }
                resultVal = getResultVal(minval, mikeRackMap, roomtypeid + "");
	        }

	        //无特殊价,有周末价
	        if(resultVal[0] == null && mikeWeekendMap != null && mikeWeekendMap.size() > 0) {
		    	Calendar cal = Calendar.getInstance();
		    	cal.setTime(sdf.parse(startdateday));
		    	String minval = null;
		    	while(!sdf.format(cal.getTime()).equals(enddateday)) {
		    		int weekDays = cal.get(Calendar.DAY_OF_WEEK) - 1;
                	String key = roomtypeid + "@" + weekDays;
                	String val = mikeWeekendMap.get(key);
                	if(StringUtils.isBlank(val)) {
                		cal.add(Calendar.DAY_OF_MONTH, 1);
                		continue;
                	}
                	val = getDampPrice(roomtypeid + "@" + sdf.format(cal.getTime()), String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
    		        if (minval == null) {
    		        	minval = val;
    		        } else {
    		        	BigDecimal minprice = new BigDecimal(minval);
    		        	BigDecimal price = new BigDecimal(val);
    		        	if (price.compareTo(minprice) == -1) {
    		        		minval = val;
    		        	}
    		        }
    		        cal.add(Calendar.DAY_OF_MONTH, 1);
		    	}
		    	resultVal = getResultVal(minval, mikeRackMap, roomtypeid + "");
	        }
	        
	        //无特殊价，无周末价
	        if (resultVal[0] == null) {
	    		// 如果酒店也没有设置基本价和门市价
	    		if (StringUtils.isBlank(mikeRackMap.get(roomtypeid + ""))) {
	    			resultVal = new String[]{"111", "111"};
	    			return resultVal;
	    		}
		    	Calendar cal = Calendar.getInstance();
		    	cal.setTime(sdf.parse(startdateday));
		    	String minval = null;
		    	while(!sdf.format(cal.getTime()).equals(enddateday)) {	
		    		String val = mikeRackMap.get(roomtypeid + "");

                	val = getDampPrice(roomtypeid + "@" + sdf.format(cal.getTime()), String.valueOf(roomtypeid), val, settingMap, settingWholeMap);
    		        if (minval == null) {
    		        	minval = val;
    		        } else {
    		        	BigDecimal minprice = new BigDecimal(minval);
    		        	BigDecimal price = new BigDecimal(val);
    		        	if (price.compareTo(minprice) == -1) {
    		        		minval = val;
    		        	}
    		        }
    		        cal.add(Calendar.DAY_OF_MONTH, 1);
		    	}
		    	resultVal = getResultVal(minval, mikeRackMap, roomtypeid + "");
	        } 
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return resultVal;
	}
	
	
	/**
	 * 
	 * @param val
	 * @param mikeRackMap
	 * @param keyroomtypeid
	 * @return
	 */
	private String[] getResultVal(String val, Map<String, String> mikeRackMap, String keyroomtypeid) {
		String[] resultVal = new String[2];
		if (StringUtils.isNotBlank(val)) {
			resultVal[0] = new BigDecimal(val).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
            String strRoomprice = mikeRackMap.get(keyroomtypeid);
            if (StringUtils.isNotBlank(strRoomprice))
                resultVal[1] = new BigDecimal(strRoomprice).setScale(0, BigDecimal.ROUND_HALF_UP).toString(); // 获得房型门市价.
        }
		return resultVal;
	}
	
	/**
	 * 从酒店特殊价格中计算得到最低特殊价.
	 * @param mikeTimepriceMap
	 * 参数：眯客特殊价格缓存map
	 * @return String
	 * 返回值
	 */
	private Map<String, String> getMinDailyPrice(Map<String, String> mikeDailyMap, String startdateday, String enddateday, Map<String, String> settingMap, Map<String, String> settingWholeMap) {
	    if (mikeDailyMap == null || mikeDailyMap.size() == 0) {
	        return null;
	    }
	    Map<String, String> minDailyPrices = Maps.newHashMap();
	    try {
	        long startday = Long.valueOf(startdateday);
	        long endday = Long.valueOf(enddateday);
	        Iterator<Entry<String, String>> iterator = mikeDailyMap.entrySet().iterator();
	        String minkey = null;
	        String minval = null;
	        while (iterator.hasNext()) {
	            Map.Entry<String, String> entry = iterator.next();
	            String key = entry.getKey();
	            String val = entry.getValue();
	            String[] keyArr = key.split("@");
	            String keydate = null;
	            String keyroomtypeid = null;
	            if (keyArr.length > 1) {
	            	keyroomtypeid = keyArr[0];
	                keydate = keyArr[1];
	            }
	            if (StringUtils.isBlank(keydate))
	                continue;
	            if(StringUtils.isBlank(keyroomtypeid))
	            	continue;
	            long timeday = Long.valueOf(keydate);
	            // 特殊价设置的日期不在查询区间
	            if (timeday < startday || timeday >= endday)
	                continue;	            
	            
	           val = getDampPrice(key, keyroomtypeid, val, settingMap, settingWholeMap);

	            if (minkey == null && minval == null) {
	                minkey = key;
	                minval = val;
	            } else {
	                BigDecimal minprice = new BigDecimal(minval);
	                BigDecimal price = new BigDecimal(val);
	                if (price.compareTo(minprice) == -1) {
	                    minkey = key;
	                    minval = val;
	                }
	            }
	        }
	        if (minkey != null && minval != null) {
	        	minDailyPrices.put(minkey, minval);
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return minDailyPrices;
	}
	
	
	/**
	 * 从酒店周末价格中计算得到最低周末价.
	 * @param mikeTimepriceMap
	 * 参数：眯客周末价格缓存map
	 * @return String
	 * 返回值
	 */
	private Map<String, String> getMinWeekendPrice(Map<String, String> mikeWeekendMap, String startdateday, String enddateday, Map<String, String> settingMap, Map<String, String> settingWholeMap) {
	    if (mikeWeekendMap == null || mikeWeekendMap.size() == 0) {
	        return null;
	    }
	    Map<String, String> minWeekendPrices = Maps.newHashMap();
	    try {
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	        Iterator<Entry<String, String>> iterator = mikeWeekendMap.entrySet().iterator();
	        String minkey = null;
	        String minval = null;
	        while (iterator.hasNext()) {
		    	Calendar cal = Calendar.getInstance();
		    	cal.setTime(sdf.parse(startdateday));
		    	int weekDays = cal.get(Calendar.DAY_OF_WEEK) - 1;
		    	int diff = DateTools.getBetweenDays(startdateday, enddateday);
	            Map.Entry<String, String> entry = iterator.next();
	            String key = entry.getKey();
	            String val = entry.getValue();
	            String keyroomtypeid = null;
	            String keyweek = null;
	            String[] keyArr = key.split("@");
	            if (keyArr.length > 1) {
	            	keyweek = keyArr[1];
	            	keyroomtypeid = keyArr[0];
	            }
	            if (StringUtils.isBlank(keyweek)) {
	                continue;
	            }
	            // 周末价设置的时间不在查询区间
	            if(diff < 7) {
	            	int week = -1;
			    	for (int i = weekDays; i <= weekDays + diff; i++) {
			    		if(String.valueOf(weekDays).equals(keyweek)) {
			    			week = Integer.valueOf(keyweek);
			    			cal.add(Calendar.DAY_OF_MONTH, i - weekDays);
			    			break;
			    		}
					}
			    	if(week < 0)
			    		continue;
	            }
	            
	            val = getDampPrice(keyroomtypeid + "@" + sdf.format(cal.getTime()), keyroomtypeid, val, settingMap, settingWholeMap);
	            
	            if (minkey == null && minval == null) {
	                minkey = key;
	                minval = val;
	            } else {
	                BigDecimal minprice = new BigDecimal(minval);
	                BigDecimal price = new BigDecimal(val);
	                if (price.compareTo(minprice) == -1) {
	                    minkey = key;
	                    minval = val;
	                }
	            }
	        }
	        if (minkey != null && minval != null) {
	        	minWeekendPrices.put(minkey, minval);
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return minWeekendPrices;
	}
	
	
	
	/**
	 * 从酒店门市价格缓存中计算得到酒店房型门市价.
	 * @param mikeRoompriceMap
	 * 参数：眯客门市价格缓存map
	 * @param settingWholeMap 
	 * @param settingMap 
	 * @param enddateday 
	 * @param startdateday 
	 * @return String
	 * 返回值
	 */
	private Map<String, String> getMinRoomprice(Map<String, String> mikeRoompriceMap, String startdateday, String enddateday, Map<String, String> settingMap, Map<String, String> settingWholeMap) {
	    if (mikeRoompriceMap == null || mikeRoompriceMap.size() == 0) {
	        return null;
	    }
	    Map<String, String> minRoomprice = Maps.newHashMap();
	    try {
            Iterator<Entry<String, String>> iter = mikeRoompriceMap.entrySet().iterator();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String minkey = null;
            String minval = null;
            
            //如果开始时间等于结束时间 认为当天住当天离,当天价格计算至mike价内, 为完成改日价格计算,故结束时间+1天
            if(startdateday.equals(enddateday)) {
            	Calendar endCal = Calendar.getInstance();
            	endCal.setTime(sdf.parse(enddateday));
            	endCal.add(Calendar.DAY_OF_MONTH, 1);
            	enddateday = sdf.format(endCal.getTime());
            }
            
            
            while (iter.hasNext()) {
                Map.Entry<String, String> entry = iter.next();
                String key = entry.getKey();
                
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(startdateday));
                
                //从开始时间 开始轮循+1天,计算mike价,结束日期当天认为顾客离店所以不计算眯客价格
                while(!sdf.format(cal.getTime()).equals(enddateday)) {
    	            
                	String val = entry.getValue();
    	            val = getDampPrice(key + "@" + sdf.format(cal.getTime()), key, val, settingMap, settingWholeMap);
    		        
    		        if (minkey == null && minval == null) {
    		        	minkey = key;
    		        	minval = val;
    		        } else {
    		        	BigDecimal minprice = new BigDecimal(minval);
    		        	BigDecimal price = new BigDecimal(val);
    		        	if (price.compareTo(minprice) == -1) {
    		        		minkey = key;
    		        		minval = val;
    		        	}
    		        }
    		        cal.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
            if (minkey != null && minval != null) {
                minRoomprice.put(minkey, minval);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	    return minRoomprice;
	}
	

	/**
	 * 获得减幅价格
	 * @param key
	 * @param keyroomtypeid
	 * @param price
	 * @param settingMap
	 * @param settingWholeMap
	 * @return
	 */
	private String getDampPrice(String key, String keyroomtypeid, String price, Map<String, String> settingMap, Map<String, String> settingWholeMap) {
        String setting = settingMap.get(key);
        if(StringUtils.isBlank(setting))
        	setting = settingWholeMap.get(keyroomtypeid);
        if(StringUtils.isNotBlank(setting)) {
        	String[] setArr = setting.split("@");
        	if(!setArr[0].equals("-"))
         		price = new BigDecimal(price).subtract(new BigDecimal(setArr[0])).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
        	else if(!setArr[1].equals("-"))
         		price = new BigDecimal(price).subtract(new BigDecimal(price).multiply(new BigDecimal(setArr[1]))).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
         }
         return price;
	}


	/**
	 * @return
	 * 动态门市价开关
	 */
	public boolean isUseNewPrice(){
		//测试在用
		boolean flag = false;
		if(cacheManager.isExistKey("usenewprice")){
			flag = true;
		}
		return flag;
	}
	
	
	/**
	 * 初始化直减价格到ES
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 */
	public Map initReducePriceToES(Long hotelid, Long roomtypeid){
		List<BStrategyPrice> hotels = strategryPriceService.findAllBStrategyPrices();
		List<Long> hotelids = new ArrayList<Long>();
		for(BStrategyPrice stPrice : hotels){
			Map<String, Object> reducePrice = new HashMap<String, Object>();
			reducePrice.put("begintime", stPrice.getRulebegintime());
			reducePrice.put("endtime", stPrice.getRuleendtime());
			String reduceType = "S";
			reducePrice.put("reduceValue", stPrice.getStprice());

			SearchHit[] searchHits = esProxy.searchHotelByHotelId(""+hotelid);
			 for (int i = 0; i < searchHits.length; i++) {
	                SearchHit searchHit = searchHits[i];
	                String _id = searchHit.getId();
	                Map<String, Object> doc = searchHit.getSource();
	                doc.remove("reducePrice");
					doc.put("reducePrice", reducePrice);
		             esProxy.updateDocument(_id, doc);
		             log.info("更新酒店:{}直减价格成功.", hotelid);
			 }
		}
		
		return null;
	}
}
