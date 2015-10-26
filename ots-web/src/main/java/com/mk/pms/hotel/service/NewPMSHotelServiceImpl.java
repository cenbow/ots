package com.mk.pms.hotel.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.mk.ots.mapper.*;
import com.mk.ots.roomsale.model.TRoomSaleConfig;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.model.TRoomSaleType;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.mortbay.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.UrlUtils;
import com.mk.orm.kit.JsonKit;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.hotel.model.EHotelModel;
import com.mk.ots.hotel.model.ERoomtypeFacilityModel;
import com.mk.ots.hotel.model.ERoomtypeInfoModel;
import com.mk.ots.hotel.model.THotelBaseTrackModel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.model.TRoomModel;
import com.mk.ots.hotel.model.TRoomTypeInfoModel;
import com.mk.ots.hotel.model.TRoomTypeModel;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomService;
import com.mk.ots.hotel.service.RoomTypeService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.hotel.service.THotelBaseTrackService;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.manager.SysConfigManager;
import com.mk.ots.pay.module.weixin.pay.common.PayTools;
import com.mk.ots.rpc.service.PmsSoapServiceImpl;
import com.mk.pms.hotel.bean.PMSHotelInfoJSONBean;
import com.mk.pms.hotel.bean.PMSRoomBean;
import com.mk.pms.hotel.bean.PMSRoomTypeBean;
import com.mk.pms.myenum.PmsErrorEnum;
import com.mk.pms.myenum.PmsStatusEnum;
import com.mk.pms.order.control.PmsUtilController;

import jodd.util.StringUtil;

/**
 * PMS2.0 同步房间
 * 
 * @author LYN
 *
 */
@Service
public class NewPMSHotelServiceImpl implements NewPMSHotelService {

	private Logger logger = LoggerFactory.getLogger(NewPMSHotelServiceImpl.class);

	@Autowired
	private RoomTypeService roomTypeService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private RoomSaleTypeMapper roomSaleTypeMapper;

	@Autowired
	private RoomSaleConfigMapper roomSaleConfigMapper;

	@Autowired
	private RoomSaleConfigInfoMapper roomSaleConfigInfoMapper;

	@Autowired
	private HotelService hotelService;
	@Autowired
	private RoomstateService roomstateService;
	
	@Autowired
	private THotelBaseTrackService tHotelBaseTrackService;
	
	@Autowired
	private THotelMapper hotelMapper;
	
	@Autowired
	private EHotelMapper eHotelmapper;
	
	@Autowired
	private TFacilityMapper tFacilityMapper;
	
	@Autowired
	private ERoomtypeFacilityMapper eRoomtypeFacilityMapper;
	
	@Autowired
	private ERoomtypeInfoMapper eRoomtypeInfoMapper;
	
	@Autowired
	private TRoomtypeInfoMapper tRoomtypeInfoMapper;

	@Autowired
	private PmsSoapServiceImpl pmsSoapServiceImpl;
	
	@Autowired
	private HotelPriceService hotelPriceService;
	
	/**
	 * 安装PMS
	 * @param hotelPMS
	 * @return
	 */
	public Map<String,Object> initPMS(String hotelPMS){
		Map<String, Object> resultMap = new HashMap<String, Object>();

		JSONObject hotel = new JSONObject();
		hotel.put("hotelid", hotelPMS);
		String resultJSONStr=doPostJson(UrlUtils.getUrl("newpms.url") + "/selecthotelinfo", hotel.toJSONString());
		JSONObject jsonOBJ = null;
		try{
			jsonOBJ = JSON.parseObject(resultJSONStr);
			
			if (jsonOBJ.getBooleanValue("success")) {
				resultMap.put("success", true);
			} else {
				logger.info("PMS2.0安装PMS,调用同步酒店失败:{}", jsonOBJ.getString("errmsg"));
				resultMap.put("success", false);
				resultMap.put("errcode", jsonOBJ.getString("errcode"));
				resultMap.put("errmsg", jsonOBJ.getString("errmsg"));
			}
		}catch(Exception e){
			logger.error("PMS2.0安装PMS失败,json转换错误:{}", e.fillInStackTrace());
			throw MyErrorEnum.errorParm.getMyException("PMS2.0安装PMS失败，json转换错误.");
		}
//		if(jsonOBJ != null ){
//			resultMap = this.syncHotelInfo(jsonOBJ);
//		}
		
		return resultMap;
	}
	
	/**
	 * 同步房间 1.同步房间 2.同步房型 3.同步酒店
	 * 
	 * @param map
	 * @return
	 */
	public Map<String, Object> syncHotelInfo(JSONObject jsonOBJ) {
		logger.info("PMS2.0同步房间 开始params:{}", jsonOBJ);

		// 转换json 为Bean
		PMSHotelInfoJSONBean pmsHotelInfoJsonBean = JSONObject.toJavaObject(jsonOBJ, PMSHotelInfoJSONBean.class);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String hotelpms = pmsHotelInfoJsonBean.getHotelid();
		EHotelModel eHotel = eHotelmapper.selectByPms(hotelpms);
		if( eHotel == null ){
			logger.info("PMS2.0同步房间失败:未找到酒店pms:{}", hotelpms);
			resultMap.put("success", false);
			resultMap.put("errcode", -1);
			resultMap.put("errmsg", "PMS2.0 同步房间，未找到酒店pms: "+hotelpms);
			return resultMap;
		}
		Long hotelId = eHotel.getId();
		
		String synLockKey = null;
		String synLockValue=null;
		try{
			//加redis锁，防止重复请求
			synLockKey = RedisCacheName.IMIKE_OTS_SYNCHOTELINFO_PMS20_KEY+hotelId;
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			
			logger.info("PMS2.0同步房间 ::synLockKey:"+synLockKey+"-----currentDate:"+sdf.format(new Date()));	
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("PMS2.0同步房间 ::syncHotelInfo::{}-----正在进行中,重复请求", hotelId);
				return resultMap;
			}
			logger.info("PMS2.0同步房间 ::syncHotelInfo::synLockValue:"+synLockValue);
		
			THotelModel tHotel = hotelMapper.selectById(hotelId); //hotelService.readonlyTHotel(hotelId);
			
			
			int roomNum = this.countRoomNum(hotelId, pmsHotelInfoJsonBean.getRoomtype());
			String hotelPhone = pmsHotelInfoJsonBean.getPhone(); 
			
			
			this.updateHotelRoomNum(tHotel, eHotel, roomNum, hotelPhone);

			boolean isSucced = this.syncRoomTypes(hotelId, eHotel.getHotelname(), roomNum, pmsHotelInfoJsonBean.getRoomtype());
			if(isSucced){
				//需要废弃
				roomstateService.updateHotelMikepricesCache(hotelId, null, true);
				
				hotelPriceService.refreshMikePrices(hotelId);
				
				resultMap.put("success", true);
			}else{
				resultMap.put("success", false);
				resultMap.put("errcode", -1);
				resultMap.put("errmsg", "同步房型失败.");
			}
			return resultMap;
		}catch(Exception e){
		    logger.error("PMS2.0同步房间:: syncHotelInfo method error:{},{}",hotelId, e.getMessage());
			resultMap.put("success", false);
			resultMap.put("errcode", PmsErrorEnum.noknowError.getErrorCode());
			resultMap.put("errmsg", e.getMessage());
		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockKey) && StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		logger.info("PMS2.0同步房间:: syncHotelInfo method end.");
		return resultMap;
	}
	
	
	/**
	 * 判断是否需要做同步操作
	 * 1.房间数为0不做同步操作
	 * 2.房型门市价为0 不做同步操作
	 */
	
	private int countRoomNum(Long hotelId, List<PMSRoomTypeBean> roomTypeList){
		int hotelNum = 0;
		for (PMSRoomTypeBean pmsRoomTypeBean: roomTypeList){
			BigDecimal cost = pmsRoomTypeBean.getPrice();
			if(BigDecimal.ZERO.equals(cost)){
				logger.info("PMS2.0同步房间酒店:{} 门市价不能为0元", hotelId);
				throw MyErrorEnum.errorParm.getMyException("门市价不能设置为0元!");
			}
			hotelNum += pmsRoomTypeBean.getRoom().size();
		}
		if( hotelNum == 0){
			logger.info("PMS2.0同步房间酒店{}:房间数为0,不做处理直接返回.", hotelId);
			throw MyErrorEnum.errorParm.getMyException("酒店 "+ hotelId +" 无房间不做同步.");			
		}
		return hotelNum;
	}

	
	/**
	 * 更新房型，及房型下的所有房间
	 * 
	 * @param roomtype
	 * @param tRoomTypeList
	 * @param pmsRoomTypeList
	 */
	private boolean syncRoomTypes(Long hotelId, String hotelName, int roomCount, List<PMSRoomTypeBean> pmsRoomTypeList) {
		logger.info("PMS2.0 同步房间数据：参数{} ", JsonKit.toJson(pmsRoomTypeList));

		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 获取OTS的roomtypeId 下的房型
		List<TRoomTypeModel> tRoomTypeList = roomTypeService.findTRoomTypeByHotelid(hotelId);
		List<TRoomModel> tRoomList = roomService.findRoomsByHotelId(hotelId);
		logger.info("PMS2.0 同步房间获取酒店:{}下的所有房型:{}", hotelId,	JsonKit.toJson(tRoomTypeList));
		
		Map<String, List> changemap = new HashMap<String, List>();
		changemap.put("delRoomNoLog", new ArrayList());//记录删除的房间号
		changemap.put("addRoomNoLog", new ArrayList());//记录添加的房间号
		changemap.put("delRoomTypeLog", new ArrayList());//记录删除的房型
		
		List<TRoomTypeModel> tRoomTypeListExits = new ArrayList<TRoomTypeModel>();
		Map<Integer, TRoomTypeModel> roomTypeMap = new HashMap<Integer, TRoomTypeModel>();

		// 遍历tRoomType数据 如果传入参数中不存在则删除OTS中的房型数据
		for (TRoomTypeModel tRoomType : tRoomTypeList) {
			String otsPms = tRoomType.getPms();
			Long roomTypeId = tRoomType.getId();
			roomTypeMap.put(roomTypeId.intValue(), tRoomType);

			boolean isExits = false;
			for (PMSRoomTypeBean pmsRoomType : pmsRoomTypeList) {
				String pms = String.valueOf(pmsRoomType.getId());
				if (StringUtil.equals(otsPms, pms)) {
					isExits = true;
					break;
				}
			}
			if (!isExits) {
				this.delRoomType(tRoomType,changemap);				
			}else{				
				tRoomTypeListExits.add(tRoomType);
			}
		}

		// 遍历参数roomType 添加或更新 roomType
		// 获取OTS的所有房间
		// 房间list 转换为Map<roomtypeid, roomBean>
		Map<Long, List<TRoomModel>> roomsMap = this.roomListTOMap(tRoomTypeList, tRoomList);
		/*
			为重庆特价房，增加。若是特价房，特殊处理
		 */
		Map<String, Long> onSaleRoomTypePms = getOnSaleRoomType(roomTypeMap);
		/*
			end
		 */
		// tRoomtypeList 转为 map<pms,TRoomType>,直接使用参数pms号取map key 如果取到就是更新，如果没有就新增
		Map<String, List<TRoomTypeModel>> tRoomTypesMap = this.roomTypeListTOMap(tRoomTypeListExits);
		for (PMSRoomTypeBean pmsRoomTypeBean : pmsRoomTypeList) {
			// PMS2.0中的ID与OTS中的pms字段对应
			String pms = String.valueOf(pmsRoomTypeBean.getId());
//			/*
//				为重庆特价房，增加。若是特价房，不更新
//			 */
//			if (onSaleRoomTypePms.contains(pms)) {
//				logger.info("PMS2.0 不同步房间数据：参数{} ", pms);
//				continue;
//			} else {
//				logger.info("PMS2.0 同步房间数据：参数{} ", pms);
//			}
//			/*
//
//			 */

			List<TRoomTypeModel> tRoomTypes = tRoomTypesMap.get(pms);
			//活动 特价和一般房型的所有房间
			List<TRoomModel> roomList = new ArrayList<>();
			for (TRoomTypeModel tRoomType : tRoomTypes) {
				Long roomId = tRoomType.getId();
				roomList.addAll(roomsMap.get(roomId));
			}

			for (TRoomTypeModel tRoomType : tRoomTypes) {
				if (tRoomType == null) {
					logger.info("PMS2.0 同步房型数据-->开始添加房型：参数roomtype:{} ", JsonKit.toJson(pmsRoomTypeBean));
					roomCount = roomCount + pmsRoomTypeBean.getRoom().size();
					if (pmsRoomTypeBean.getRoom() == null || pmsRoomTypeBean.getRoom().size() == 0) {
						logger.info("PMS2.0 同步房型数据-->开始添加房型：参数roomtype:{} 房间数为0 ,此房型不做同步", JsonKit.toJson(pmsRoomTypeBean.getId()));
					} else {
						this.addRoomType(hotelId, pmsRoomTypeBean, hotelName, changemap);
					}
				} else {
					logger.info("PMS2.0 同步房型数据-->开始更新房型：参数roomtype:{},tRoomType:{}", JsonKit.toJson(pmsRoomTypeBean), JsonKit.toJson(tRoomType));
					roomCount = roomCount + pmsRoomTypeBean.getRoom().size();
					if (pmsRoomTypeBean.getRoom() == null || pmsRoomTypeBean.getRoom().size() == 0) {
						logger.info("PMS2.0 同步房型数据-->开始添加房型：参数roomtype:{} 房间数为0 ,此房型不做同步", JsonKit.toJson(pmsRoomTypeBean.getId()));
					} else {
					/*
						先判断是否是特价房型
					 */
						Long roomTypeId = onSaleRoomTypePms.get(pms);
						if (tRoomType.getId() == roomTypeId){
							//原房型
							this.updateRoomType(tRoomType, pmsRoomTypeBean, roomList, changemap, false);
						} else {
							//特价房型
							this.updateRoomType(tRoomType, pmsRoomTypeBean, roomList, changemap, true);
						}
					/*
						end
					 */
					}
				}
			}
		}



		List delRoomNoLog = (List) changemap.get("delRoomNoLog");
	    List addRoomNoLog = (List) changemap.get("addRoomNoLog");
	    List delRoomTypeLog =(List) changemap.get("delRoomTypeLog");
	    String content = logHotelTrack(delRoomNoLog, addRoomNoLog, delRoomTypeLog);
	    logger.info("delRoomNoLog{},addRoomNoLog{},delRoomTypeLog",delRoomNoLog.size(),addRoomNoLog.size(),delRoomTypeLog.size());
		THotelBaseTrackModel tTrack = new THotelBaseTrackModel();
		tTrack.setHotelid(hotelId);
		tTrack.setHotelname(hotelName);
		tTrack.setRoomcnt(roomCount);
		tTrack.setContent(content);
		tTrack.setCreatetime(new Date());
		tHotelBaseTrackService.saveTHotelBaseTrack(tTrack);
		logger.info("添加日志 完毕");
		logger.info("PMS2.0 同步房间完成 ");
		return true;
	}

	private Map<String, Long> getOnSaleRoomType(Map<Integer, TRoomTypeModel> roomTypeMap) {
		Map<String, Long> onSaleRoomTypePms = new HashMap<>();
		//
		Map<String,Object> saleTypeParam = new HashMap<>();
		saleTypeParam.put("valid","T");
		List<TRoomSaleType> roomSaleTypeList = roomSaleTypeMapper.queryRoomSaleType(saleTypeParam);

		for (TRoomSaleType saleType : roomSaleTypeList) {
			Integer typeId = saleType.getId();
			//先取可用configInfo
			Map<String, Object> saleConfigParam = new HashMap<>();
			saleConfigParam.put("valid", "T");
			saleConfigParam.put("saleTypeId",typeId);

			List<TRoomSaleConfigInfo> infoList = roomSaleConfigInfoMapper.queryRoomSaleConfigInfoList(saleConfigParam);
			for (TRoomSaleConfigInfo info : infoList) {
				String infoValid = info.getValid();
				Integer infoId = info.getId();

				if ("T".equals(infoValid)) {
					//再取可用 TRoomSaleConfig
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("saleConfigInfoId", infoId);
					List<TRoomSaleConfig> configList = roomSaleConfigMapper.queryRoomSaleConfigByParams(map);
					for (TRoomSaleConfig config : configList) {
						String configValid = config.getValid();
						if ("T".equals(configValid)) {
							Integer roomTypeId = config.getRoomTypeId();

							//获取房型相关pms
							TRoomTypeModel roomTypeModel = roomTypeMap.get(roomTypeId);
							if (null != roomTypeModel) {
								String pmsId = roomTypeModel.getPms();
								onSaleRoomTypePms.put(pmsId,roomTypeModel.getId());
							}
						}
					}
				}
			}
		}
		return onSaleRoomTypePms;
	}

	//更新房间数
	private void updateHotelRoomNum(THotelModel tHotel, EHotelModel eHotel, int roomNum, String hotelPhone){
		//修改t表t_hotel房间数;
		if(tHotel!=null){
			tHotel.setRoomnum(roomNum);
			tHotel.setIsnewpms("T");
			if ( StringUtils.isNotEmpty(hotelPhone)){
				tHotel.setHotelphone(hotelPhone);
			}
			hotelMapper.updateTHotel(tHotel);
		}
		
		//修改e表 e_hotel 房间数
		if(eHotel != null ){
			eHotel.setRoomnum(roomNum);
			eHotel.setIsnewpms("T");
			if(eHotel.getPmsstatus() ==null || eHotel.getPmsstatus()==PmsStatusEnum.init.getId() || eHotel.getPmsstatus()==PmsStatusEnum.find.getId()){
				eHotel.setPmsstatus(PmsStatusEnum.syn.getId());
				if ((eHotel.getState() != null) && (eHotel.getState().intValue() == -1)) {
					eHotel.setState(0);
				}
			}
			if(StringUtils.isNotEmpty(hotelPhone)){
				eHotel.setHotelphone(hotelPhone);
			}
			eHotelmapper.updateByPrimaryKeySelective(eHotel);
		}
		
	}

	/**
	 * 删除房型
	 * @param tRoomType
	 * @return
	 */
	private int delRoomType(TRoomTypeModel tRoomType,Map changemap) {
		//删除房型下的房间
		int count= this.delRoomByRoomTypeId(tRoomType.getId());
		String pms= tRoomType.getPms();
		logDelRoomType(tRoomType.getId().toString(), pms, changemap);
		//TODO:删除房型附加信息
		
		return roomTypeService.delTRoomType(tRoomType.getId());
	}

	/**
	 * 删除房型下的所有房间
	 * @param id
	 * @return
	 */
	private int delRoomByRoomTypeId(Long roomTypeId) {
		return roomService.delRoomByRoomTypeId(roomTypeId);
	}

	private void updateRoomType(TRoomTypeModel tRoomType, PMSRoomTypeBean pmsRoomTypeBean, List<TRoomModel> tRoomList,Map changemap, boolean isOnSaleRoomType) {
		//非特价房不更新房型名称
		if (!isOnSaleRoomType) {
			tRoomType.setName(pmsRoomTypeBean.getName());
		}
		tRoomType.setRoomnum(pmsRoomTypeBean.getRoom().size());
		tRoomType.setCost(pmsRoomTypeBean.getPrice());
		int updateCount = roomTypeService.updateTRoomType(tRoomType);

		//
		this.updateRooms(tRoomType, pmsRoomTypeBean.getRoom(), tRoomList,changemap, isOnSaleRoomType);
	}

	/**
	 * 更新房型下的房间
	 * @param room
	 * @param tRoomType
	 */
	private void updateRooms(TRoomTypeModel tRoomType, List<PMSRoomBean> pmsRoomList, List<TRoomModel> tRoomList,Map changemap, boolean isOnSaleRoomType) {
		//特价房型不更新房型
		if (isOnSaleRoomType) {
			return;
		}
		// 第一次遍历，修改和删除
		for(TRoomModel tRoom : tRoomList){
			PMSRoomBean temp = null;
			for(PMSRoomBean pmsRoom : pmsRoomList){
				String otspms = tRoom.getPms();
				String pms  = pmsRoom.getId().toString();
				if(StringUtils.equals(otspms, pms)){
					temp = pmsRoom;
					break;
				}
			}
			if( null != temp ){
				this.updateRoom(tRoom, temp);
			}else{
				this.delRoom(tRoom.getId(),tRoom.getName(),changemap);
			}
		}
		// 第二次循环 增加
		for(PMSRoomBean pmsRoom : pmsRoomList){
			boolean isFind = false;
			for(TRoomModel tRoom : tRoomList){
				String otspms = tRoom.getPms();
				String pms  = pmsRoom.getId().toString();
				if (StringUtils.equals(otspms,pms)) {
					isFind = true;
					break;
				}
			}
			if (!isFind) {
				this.addRoom(tRoomType, pmsRoom,changemap);
			}
		}
	}

	/**
	 * 删除房间
	 * @param id
	 */
	private void delRoom(Long id,String name,Map changemap) {
		roomService.delRoomById(id);
		logDelRoomNo(name, changemap);//记录删除的房间号
	}

	/**
	 * 更新room
	 * @param tRoom
	 * @param temp
	 */
	private void updateRoom(TRoomModel tRoom, PMSRoomBean temp) {
		tRoom.setName(temp.getRoomno());
		roomService.updateRoom(tRoom);
	}

	/**
	 * 添加房型
	 * 
	 * @param pmsRoomTypeBean
	 */
	private void addRoomType(Long hotelId, PMSRoomTypeBean pmsRoomTypeBean, String hotelName, Map changemap) {
		logger.info("PMS2.0同步房型,参数{}", pmsRoomTypeBean.toString());
		if (pmsRoomTypeBean == null) {
			return;
		}
		TRoomTypeModel tRoomType = new TRoomTypeModel();
		tRoomType.setPms(pmsRoomTypeBean.getId().toString());
		tRoomType.setCost(pmsRoomTypeBean.getPrice());
		tRoomType.setEhotelid(hotelId);
		tRoomType.setThotelid(hotelId);
		tRoomType.setName(pmsRoomTypeBean.getName());
		tRoomType.setRoomnum(pmsRoomTypeBean.getRoom().size());
		tRoomType.setBednum(2);
		int count = roomTypeService.saveTRoomType(tRoomType);
		if (count == 0) {
			throw MyErrorEnum.errorParm.getMyException("保存房型失败!");
		}
		this.addRoomTypeInfo(tRoomType);
		this.addRooms(pmsRoomTypeBean.getRoom(), tRoomType,changemap);
	}

	/**
	 * 添加房型附加信息
	 *  e_roomtype_facility
	 *  e_roomtype_info
	 *  t_roomtype_info
	 * @param tRoomType
	 */
	private void addRoomTypeInfo(TRoomTypeModel tRoomType) {
		//添加默认酒店设施
		Long roomTypeId = tRoomType.getId();
		String initids=SysConfigManager.getInstance().readOne(Constant.mikeweb, Constant.initTFacility);
		if(StringUtils.isNotBlank(initids)){
			String ids[]=initids.split(",");
			for(String facilityId : ids){
				ERoomtypeFacilityModel eRoomtypeFacility = new ERoomtypeFacilityModel();
				eRoomtypeFacility.setRoomtypeid(roomTypeId);
				eRoomtypeFacility.setFacid(Long.parseLong(facilityId));
				eRoomtypeFacilityMapper.insertSelective(eRoomtypeFacility);
			}
//			List<TFacilityModel> tFacilityModelList = tFacilityMapper.findByIds(ids);
//			for(TFacilityModel tFacilityModel : tFacilityModelList){
//				Long facilityId = tFacilityModel.getId();
//			}
		}
		
		//添加e表  e_roomtype_info
		ERoomtypeInfoModel eRoomtypeInfoModel = new ERoomtypeInfoModel();
		eRoomtypeInfoModel.setRoomtypeid(roomTypeId);
		eRoomtypeInfoModel.setBedtype(Long.parseLong(tRoomType.getBednum().toString()));
		eRoomtypeInfoMapper.insertSelective(eRoomtypeInfoModel);
		
		//添加t表  t_roomtype_info 
		TRoomTypeInfoModel tRoomTypeInfoModel = new TRoomTypeInfoModel();
		tRoomTypeInfoModel.setRoomtypeid(roomTypeId);
		tRoomTypeInfoModel.setBedtype(Long.parseLong(tRoomType.getBednum().toString()));
		tRoomtypeInfoMapper.insertSelective(tRoomTypeInfoModel);
	}

	/**
	 * 添加房间
	 * 
	 * @param room
	 * @param tRoomType
	 */
	private void addRooms(List<PMSRoomBean> pmsRoomList, TRoomTypeModel tRoomType,Map changemap) {
		for (PMSRoomBean pmsRoom : pmsRoomList) {			
			this.addRoom(tRoomType,pmsRoom,changemap);
		}
	}
	

	/**
	 * 新增房间
	 * @param pmsRoom
	 */
	private void addRoom(TRoomTypeModel tRoomType, PMSRoomBean pmsRoom,Map changemap) {
		TRoomModel tRoom = new TRoomModel();
		tRoom.setPms(pmsRoom.getId().toString());
		tRoom.setName(pmsRoom.getRoomno());
		tRoom.setRoomtypeid(tRoomType.getId());		
		roomService.saveTRoom(tRoom);
		logAddRoomNo(pmsRoom.getRoomno(), changemap);
		logger.info("getRoomno{},",pmsRoom.getRoomno());
	}
	//记录添加的房间号
		private void logAddRoomNo(String tRoomNo, Map changeMap) {
			List addList = (List) changeMap.get("addRoomNoLog");
			addList.add(tRoomNo);
		}
	/**
	 * 转换roomTypeList 到 Map<String, roomTypeList<Bean> key为pms字段
	 * 
	 * @param roomTypeList
	 * @return
	 */
	private Map<String, List<TRoomTypeModel>> roomTypeListTOMap( List<TRoomTypeModel> tRoomTypeList) {
		Map<String, List<TRoomTypeModel>> roomTypeMap = new HashMap<>();
		for (TRoomTypeModel tRoomType : tRoomTypeList) {
			String tpms = tRoomType.getPms();

			if (!roomTypeMap.containsKey(tpms)) {
				roomTypeMap.put(tpms, new ArrayList<TRoomTypeModel>());
			}
			roomTypeMap.get(tpms).add(tRoomType);
		}
		return roomTypeMap;
	}

	/**
	 * 转换roomList 为Map<roomtypeid, roomBean>
	 */
	private Map<Long, List<TRoomModel>> roomListTOMap(List<TRoomTypeModel> roomTypeList, List<TRoomModel> roomList) {
		Map<Long, List<TRoomModel>> roomMap = new HashMap<Long, List<TRoomModel>>();
		for (TRoomTypeModel roomType : roomTypeList) {
			Long roomtypeid = roomType.getId();
			List<TRoomModel> rList = new ArrayList<TRoomModel>();
			for (TRoomModel room : roomList) {
				Long rroomtypeid = room.getRoomtypeid();
				if ( roomtypeid.equals(rroomtypeid) ) {
					rList.add(room);
				}
			}
			roomMap.put(roomtypeid, rList);
		}
		return roomMap;
	}
	//删除房型日志
	private void logDelRoomType(String roomtypeId, String tpms, Map changeMap){
		List delRoomRypeLog = (List) changeMap.get("delRoomTypeLog");
		String rt=roomtypeId+"("+tpms+")";
		delRoomRypeLog.add(rt);
	}
	//记录删除的房间号
	private void logDelRoomNo(String tRoomNo, Map changeMap) {
		List delRoomNoLog = (List) changeMap.get("delRoomNoLog");
		delRoomNoLog.add(tRoomNo);
	}
		
	/**
	 * 
	 *2015年6月26日下午4:54:23
	 * 返回增减的log
	 * 
	 * @param delRooms
	 * @param addRooms
	 * @param delRoomTypes
	 * @return content
	 */
	 private String logHotelTrack (List delRooms, List addRooms , List delRoomTypes) {
	    	if(delRooms.size()==0 && addRooms.size() ==0 && delRoomTypes.size()==0){
	    		return "";
	    	}
	    	//记录删除房间到酒店 信息轨迹日志
	        StringBuffer delRoom = new StringBuffer();
	        int delSize = delRooms.size();
	        if(delSize > 0){
	        	delRoom.append("del:");
	        	for(int i=0; i<delSize; i++) {
	        		if(delRooms.get(i) != null){
	        			delRoom.append(delRooms.get(i).toString()).append(",");
	        		}
	        	}
	        }
	        String delRoomStr ="";
	        if(delRoom.length() >0 ){
	        	delRoomStr = delRoom.toString().substring(0, delRoom.length()-1);
	        }
	         
	        StringBuffer addRoom = new StringBuffer();//添加房间日志内容
	        int addSize = addRooms.size();
	        if(addSize > 0){
	        	addRoom.append("add:");
	        	for(int i=0; i < addSize; i++) {
	        		if(addRooms.get(i) != null){
	        			addRoom.append(addRooms.get(i).toString()).append(",");
	        			logger.info("room{}",addRooms.get(i).toString());
	        		}
	        	}
	        }
	        String addRoomStr = "";
	        if(addRoom.length() >0 ){
	        	addRoomStr = addRoom.toString().substring(0, addRoom.length()-1);
	        }
	        logger.info("room{}",addRoomStr);
	        
	        StringBuffer delRoomType = new StringBuffer();//添加房间日志内容
	        int delRoomTypeSize = delRoomTypes.size();
	        if( delRoomTypeSize >0 ){
	        	delRoomType.append("dRT:");
	        	for(int i=0; i<delRoomTypeSize; i++){
	        		if(delRoomTypes.get(i)!=null){
	        			delRoomType.append(delRoomTypes.get(i).toString()).append(",");
	        		}
	        	}
	        }
	        String delRoomTypeStr = "";
	        if(delRoomType.length() >0 ){
	        	delRoomTypeStr = delRoomType.toString().substring(0, delRoomType.length()-1);
	        }
	        
	        String content = "" ;
	        if(delRoomStr.trim().length()>0){
	        	content=delRoomStr;
	        }
	        
	        if(addRoomStr.length()>0 ){
	        	if(content.trim().length()>0){	        		
	        		content = content + ";" + addRoomStr;
	        	}else{
	        		content =  addRoomStr;
	        	}
	        }
	        if(delRoomType.length() > 0){
	        	if(content.trim().length()>0){	
	        		content = content +";" + delRoomTypeStr; 
	        	}else{
	        		content = delRoomTypeStr; 
	        	}
	        }
	        logger.info("content{}",content);
	        return content;
	 }
	 
	 private String doPostJson(String url, String json) {
			JSONObject back = new JSONObject();
			try {
				return PayTools.dopostjson(url, json);
			} catch (Exception e) {
				logger.info("doPostJson参数:{},{},异常:{}", url, json, e.getLocalizedMessage());
				e.printStackTrace();
				back.put("success", false);
				back.put("errorcode", "-1");
				back.put("errormsg", e.getLocalizedMessage());
			}
			return back.toJSONString();
		}

	/**
	 * PMS 登录
	 */
	public Map<String, Object> doLogin(String hotelPMS, String hotelName) {
		EHotelModel eHotel = eHotelmapper.selectByPms(hotelPMS);
		if(eHotel == null ){
			logger.info("PMS2.0 Hotel: " + hotelPMS + " 登录失败, 未找到pms酒店: \n");
			throw MyErrorEnum.errorParm.getMyException("PMS2.0 Hotel: " + hotelPMS + " 登录失败, 未找到pms酒店: \n");
		}
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		String isVisible = eHotel.getVisible();
		//判断酒店是否下线
		if( isVisible.equals("T")){
			resultMap = pmsSoapServiceImpl.pmsHotelOnline(eHotel.getId().toString());
			logger.info("PMS2.0 酒店hotelPMS:{} 已经上线.", hotelPMS);
		}else{
			logger.info("PMS2.0 酒店hotelPMS:{} 已经下线.", hotelPMS);
			resultMap.put("success", false);
			resultMap.put("errorcode", "-1");
			resultMap.put("errormsg", "酒店已经下线，不能登录");
		}
		return resultMap;
	}
	/**
	 * 酒店离线
	 */
	public Map<String, Object> doOffLine(String hotelPMS){
		try{
		EHotelModel eHotel = eHotelmapper.selectByPms(hotelPMS);
		if(eHotel == null ){
			logger.info("PMS2.0 Hotel: " + hotelPMS + " 登录失败, 未找到pms酒店: \n");
			throw MyErrorEnum.errorParm.getMyException("PMS2.0 Hotel: " + hotelPMS + " 登录失败, 未找到pms酒店: \n");
		}
		Long hotelid = eHotel.getId();
		return pmsSoapServiceImpl.pmsHotelOnline(hotelid.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * PMS2.0 酒店信息查询
	 * 设置pms安装状态
	 * @param hotelPMS 酒店PMS号
	 * @return
	 */
	public Map<String, Object> changeHotelInfo(String hotelPMS) {
		EHotelModel eHotel = eHotelmapper.selectByPms(hotelPMS);
		if(eHotel == null ){
			logger.info("PMS2.0  酒店信息查询失败，未找到pms{}酒店", hotelPMS);
			throw MyErrorEnum.errorParm.getMyException("PMS2.0 酒店信息查询失败，未找到pms:"+ hotelPMS +"酒店");
		}
		if(eHotel.getPmsstatus() ==null || eHotel.getPmsstatus()==PmsStatusEnum.init.getId() || eHotel.getPmsstatus()==PmsStatusEnum.find.getId()){
			eHotel.setPmsstatus(PmsStatusEnum.syn.getId());
			if ((eHotel.getState() != null) && (eHotel.getState().intValue() == -1)) {
				eHotel.setState(0);
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success",true);
		return resultMap;
	}

	@Override
	public Map<String, Object> installPms(String hotelPMS,String hotelname) {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		JSONObject hotel = new JSONObject();
		hotel.put("hotelid", hotelPMS);
		hotel.put("hotelname", hotelname);
		String resultJSONStr=doPostJson(UrlUtils.getUrl("newpms.url") + "/online", hotel.toJSONString());
		JSONObject jsonOBJ = null;
		jsonOBJ = JSON.parseObject(resultJSONStr);
		
		if (jsonOBJ.getBooleanValue("success")) {
			EHotelModel eHotel = eHotelmapper.selectByPms(hotelPMS);
			if(eHotel==null){
				logger.error("PMS2.0安装PMS，更新安装状态失败，PMS为 {} 的酒店不存在",hotelPMS);
				resultMap.put("success", false);
				resultMap.put("errcode", "-1");
				resultMap.put("errmsg", "PMS2.0安装PMS，更新安装状态失败，PMS为 "+hotelPMS+" 的酒店不存在");
			}else{
				//更新酒店安装状态为-1，(安装完成，同步数据过程中)
				eHotel.setState(-1);
				eHotelmapper.updateByPrimaryKeySelective(eHotel);
				resultMap.put("success", true);
			}
			
		} else {
			logger.error("PMS2.0安装PMS,调用酒店上线接口失败:{}", jsonOBJ.getString("errmsg"));
			resultMap.put("success", false);
			resultMap.put("errcode", jsonOBJ.getString("errcode"));
			resultMap.put("errmsg", jsonOBJ.getString("errmsg"));
		}
		return resultMap;
	}

	@Override
	public void sendOfflineMsg(String hotelPMS) {
		JSONObject hotel = new JSONObject();
		hotel.put("hotelid", hotelPMS);
		String resultJSONStr=doPostJson(UrlUtils.getUrl("newpms.url") + "/offline", hotel.toJSONString());
		JSONObject jsonOBJ = null;
		jsonOBJ = JSON.parseObject(resultJSONStr);
		
		if (jsonOBJ.getBooleanValue("success")) {
			logger.info("调用pms下线成功,hotelPMS{}:return{}",hotelPMS,resultJSONStr);
		    
		} else {
			logger.info("调用pms下线失败,hotelPMS{}:return{}",hotelPMS,resultJSONStr);
		}
	}
}
