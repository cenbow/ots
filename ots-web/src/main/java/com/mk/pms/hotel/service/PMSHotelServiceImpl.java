package com.mk.pms.hotel.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jodd.util.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.bean.Bean;
import com.mk.ots.annotation.HessianService;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.dao.HotelDAO;
import com.mk.ots.hotel.dao.RoomDAO;
import com.mk.ots.hotel.dao.RoomTypeDAO;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.manager.RedisCacheName;
import com.mk.ots.manager.SysConfigManager;
import com.mk.ots.mapper.THotelMapper;
import com.mk.pms.myenum.PmsErrorEnum;
import com.mk.pms.myenum.PmsStatusEnum;

/**
 * pmssoap交互 同步房间
 * 
 * @author LYN
 *
 */
@Service
@HessianService(value="/synRoom",implmentInterface=PMSHotelService.class)
public class PMSHotelServiceImpl implements PMSHotelService {

	private Logger logger = LoggerFactory.getLogger(PMSHotelServiceImpl.class);
	
	@Autowired
	private RoomTypeDAO roomtypeDAO = null;
	
	@Autowired
	private HotelDAO hotelDAO = null;
	@Autowired
	private RoomDAO roomDAO;
	@Autowired
	private THotelMapper tHotelMapper;
	@Autowired
	private RoomstateService roomstateService;

	/**
	 * 同步房间 1.同步房间 2.同步房型 3.同步酒店
	 * 
	 * @param map
	 * @return
	 */
	public Map syncHotelInfo(Map map) {
	    logger.info("-==== OTS Info:: syncHotelInfo method begin...");
	    logger.info("-==== OTS Info:: parameters is: {}", JsonKit.toJson(map));
	    Map<String, Object> resultMap = new HashMap<String, Object>();
		Map<String, List> changemap = new HashMap<String, List>();
		changemap.put("updaterooms",new ArrayList());
		changemap.put("delrooms",new ArrayList());
		changemap.put("addrooms", new ArrayList());
		//changemap.put("addroomtype",new ArrayList());提前插入房型数据
		changemap.put("updateroomtype",new ArrayList());
		changemap.put("delroomtype",new ArrayList());
		changemap.put("adderoomtypefacility", new ArrayList());
		changemap.put("adderoomtypeinfo", new ArrayList());
		changemap.put("addtroomtypeinfo", new ArrayList());
		changemap.put("updatehotel", new ArrayList());
		changemap.put("updateehotel", new ArrayList());
		
		changemap.put("delRoomNoLog", new ArrayList());//记录删除的房间号
		changemap.put("addRoomNoLog", new ArrayList());//记录添加的房间号
		changemap.put("delRoomTypeLog", new ArrayList());//记录删除的房型
		
		String hotelid = map.get("hotelid").toString();
		
		String roomnum ="0";
		if(null != map.get("hotelnum")){
			roomnum = map.get("hotelnum").toString();
		}else{
			logger.info("PMSHotelServiceImpl::syncHotelInfo::"+hotelid+"酒店参数hotelnum为null，不做处理直接返回.");
			return resultMap;
		}
		if("0".equals(roomnum)){
			logger.info("PMSHotelServiceImpl::syncHotelInfo::"+hotelid+"酒店参数hotelnum为0,不做处理直接返回.");
			return resultMap;			
		}
		
		String synLockKey = null;
		String synLockValue=null;
		try{
			//加redis锁，防止重复请求
			synLockKey = RedisCacheName.IMIKE_OTS_SYNCHOTELINFO_KEY+hotelid;
			SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			
			logger.info("syncHotelInfo::synLockKey:"+synLockKey+"-----currentDate:"+sdf.format(new Date()));	
			if((synLockValue=DistributedLockUtil.tryLock(synLockKey, 60))==null){
				logger.info("PMSHotelServiceImpl::syncHotelInfo::"+hotelid+"-----正在进行中,重复请求");
				return resultMap;
			}
			logger.info("PMSHotelServiceImpl::syncHotelInfo::synLockValue:"+synLockValue);
			THotel thotel=hotelDAO.findHotelByHotelid(hotelid);
			if(thotel != null){
				//当酒店未审核上线之前是不存在T表的，审核上线之后要同时修改T表和E表的房间数
				List ll = new ArrayList();
				ll.add(roomnum);
				ll.add(thotel.get("id").toString());
				List l =(List)changemap.get("updatehotel");
				l.add(ll);
			}
			
			// 同步房型
			Bean eHotelBean = hotelDAO.findEHotelByHotelid(hotelid);
			List elist= new ArrayList();
			elist.add(roomnum);
			if(eHotelBean.getInt("pmsstatus")==null || eHotelBean.getInt("pmsstatus")==PmsStatusEnum.init.getId() || eHotelBean.getInt("pmsstatus")==PmsStatusEnum.find.getId()){
				elist.add(PmsStatusEnum.syn.getId());
			}else{
				elist.add(eHotelBean.getInt("pmsstatus"));
			}
			elist.add(eHotelBean.get("id"));
			List el =(List)changemap.get("updateehotel");
			el.add(elist);
			
			
			List<Map> inputroomtypelist = new ArrayList<Map>(); 
			if( map.get("roomtype") != null){
				inputroomtypelist = (List<Map>) map.get("roomtype");
			}
			List<Bean> roomtypelist = hotelDAO.findRoomTypeByEHotelid(hotelid);
			// 第一次遍历，修改/删除房型
			for (Bean troomtype : roomtypelist) {
				Map dest = null;
				for (Map roomtype : inputroomtypelist) {
					String pmsno = roomtype.get("pmsno").toString();
					String tpmsno = troomtype.get("pms").toString();
					logger.info("同步酒店{}房型，判断pmsno(pms):{}==pms(ots):{} , isequal:{}", eHotelBean.get("id"), pmsno, tpmsno, StringUtils.equals(pmsno, tpmsno));
					if (StringUtils.equals(pmsno, tpmsno)) {
						dest = roomtype;
						break;
					}
				}
				if (dest != null) {
					// 更新房型、房间号
					boolean canUpdate = updateRooms(eHotelBean, troomtype, dest, changemap);					
					if( !canUpdate ){
						resultMap.put("success", false);
						resultMap.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
						resultMap.put("errmsg", "门市价不能设置为0元!");
						return resultMap;
					}
				} else {
					// 删除房型所有内容
					deleteRoomType(eHotelBean, troomtype,changemap);
				}
			}
			// 第二次遍历 增加数据
			for (Map roomType : inputroomtypelist) {
				boolean find = false;
				for (Bean tRoomType : roomtypelist) {
					String pmsno = roomType.get("pmsno").toString();
					String tpmsno = tRoomType.get("pms").toString();
					if (StringUtils.equals(pmsno, tpmsno)) {
						find = true;
						break;
					}
				}
				if (find == false) {
					// 增加房型
					boolean canUpdate = addRoomType(eHotelBean, thotel, roomType,changemap);
					if( !canUpdate ){
						resultMap.put("success", false);
						resultMap.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
						resultMap.put("errmsg", "门市价不能设置为0元!");
						return resultMap;
					}
				}
			}
		
		    Boolean succeed = roomDAO.syncPmsRooms(changemap);
		    
		    logger.info("记录酒店基本信息轨迹日志--开始");
		    List delRoomNoLog = (List) changemap.get("delRoomNoLog");
		    List addRoomNoLog = (List) changemap.get("addRoomNoLog");
		    List delRoomTypeLog =(List) changemap.get("delRoomTypeLog");
		    roomDAO.logHotelTrack(hotelid, eHotelBean.get("hotelname").toString(), roomnum, delRoomNoLog, addRoomNoLog, delRoomTypeLog);
		    logger.info("记录酒店基本信息轨迹日志--结束");
		    //刷新价格
			roomstateService.updateHotelMikepricesCache(Long.parseLong(hotelid), null, true);
		    
			resultMap.put("success", true);
		}catch(Exception e){
		    logger.info("-==== OTS Info:: syncHotelInfo method error: {}", e.getStackTrace());
			resultMap.put("success", false);
			resultMap.put("errorcode", PmsErrorEnum.noknowError.getErrorCode());
			resultMap.put("errormsg", e.getLocalizedMessage());
		}finally{
			//消除锁
			if(StringUtil.isNotEmpty(synLockKey) && StringUtil.isNotEmpty(synLockValue)){
				DistributedLockUtil.releaseLock(synLockKey, synLockValue);
			}
		}
		logger.info("-==== OTS Info:: syncHotelInfo method end.");
		return resultMap;
	}

	private boolean updateRooms(Bean ehotel, Bean tRoomType, Map roomType, Map changemap) {
        if(roomType ==null){
            return true;
        }
        
        BigDecimal cost = BigDecimal.ZERO;
		if( roomType.get("price") != null ) {
			cost = new BigDecimal(roomType.get("price").toString());
		}
		if(BigDecimal.ZERO.equals( cost )){
			logger.info("同步hotelid:{},房型:{}价格为0，直接返回，不做处理.", ehotel.get("id").toString(), roomType.get("name").toString());
			return false;
		}
		
		String roomnum = roomType.get("roomnum").toString();
		String name = roomType.get("name").toString();
		String roomtypeid=tRoomType.get("roomtypeid").toString();
		List ll=(List)changemap.get("updateroomtype");
		List rtlist = new ArrayList();
		rtlist.add(roomnum);
		rtlist.add(cost);
		rtlist.add(name);
		rtlist.add(roomtypeid);
		ll.add(rtlist);

		List<Map> roomlist = new ArrayList<Map>();
		if (roomType.get("room") != null) {
		    roomlist = (List<Map>)roomType.get("room");
		}
		List<Bean> tRoomList = hotelDAO.findRoomByRoomtypeid(roomtypeid);
		logger.info("同步酒店房型 {},params:{},ots:{}", roomtypeid, JsonKit.toJson(roomlist), JsonKit.toJson(tRoomList));
		// 第一次遍历，修改和删除
		for (Bean tRoom : tRoomList) {
			Map dest = null;
			for (Map room : roomlist) {
				String tpms = tRoom.get("pms").toString();
				String pms = room.get("pmsno").toString();
				logger.info("同步酒店房型{} updateRooms，判断pmsno(pms):{}==pms(ots):{} , isequal:{}", roomtypeid, pms, tpms, StringUtils.equals(tpms , pms));
				if (StringUtils.equals(tpms, pms)) {
					dest = room;
					break;
				}
			}
			if (dest != null) {
				// 更新
				updateRoom(roomtypeid, dest,tRoom.get("id").toString() ,changemap);
			} else {
				// 删除
				logger.info("同步房间:更新房型{}:删除房间:{}", roomtypeid, JsonKit.toJson(tRoomList));
				String tRoomid = tRoom.get("id").toString();
				deleteRoom(tRoomid,changemap);

				String tRoomName = tRoom.get("name").toString();
				logDelRoomNo(tRoomName, changemap);//记录删除的房间号
			}
		}
		// 第二次循环 增加
		for (Map room : roomlist) {
			boolean find = false;
			for (Bean tRoom : tRoomList) {
				String tpms = tRoom.get("pms").toString();
				String pms = room.get("pmsno").toString();
				if (StringUtils.equals(tpms,pms)) {
					find = true;
					break;
				}
			}
			if (find == false) {
				addRoom(ehotel, roomtypeid, room,changemap);
			}
		}
		return true;
	}

	private void updateRoom(String roomtypeid, Map room,String roomid,
			Map changemap) {
        if(room == null ) {
            return ;
        }
		List params = new ArrayList();
		params.add(room.get("floor").toString());
		params.add(room.get("memo").toString());
		params.add(room.get("pmsno").toString());
		params.add(room.get("name").toString());//TODO roomno 存放到 了name字段
		params.add( roomtypeid);
		params.add( room.get("property").toString());
		params.add(room.get("lockid").toString());
		params.add(roomid);
		List roomList = (List) changemap.get("updaterooms");
		roomList.add(params);
	}
	
	private void deleteRoom(String tRoomid, Map changemap){
        if(changemap == null ){
            return ;
        }
		List params = new ArrayList();
		params.add(tRoomid);
		List roomList = (List) changemap.get("delrooms");
		roomList.add(params);
		//暂不删除足迹、评论
	}
	
	private void addRoom(Bean ehotel,String tRoomTypeid, Map room,Map changemap){
        if(room == null){
            return ;
        }
		List params = new ArrayList();		
		params.add(room.get("floor"));
		params.add(room.get("memo"));
		params.add(room.get("pmsno"));
		String name ="";
		if(room.get("name") !=null ){
			name = room.get("name").toString(); 
		}
		params.add( name );
		params.add(tRoomTypeid);
		params.add(new BigDecimal("0"));
		params.add(false);
		params.add(room.get("tel"));
		params.add(room.get("property"));
		params.add(room.get("lockid"));
		List l = (List)changemap.get("addrooms");		
		l.add(params);
		
		logAddRoomNo(name, changemap);
	}
	
	//记录添加的房间号
	private void logAddRoomNo(String tRoomNo, Map changeMap) {
		List addList = (List) changeMap.get("addRoomNoLog");
		addList.add(tRoomNo);
	}
	
	/**
	 * 删除房型上的所有数据
	 */
	private void deleteRoomType(Bean ehotel,Bean tRoomType,Map changemap){
        if(tRoomType == null){
            return ;
        }
		//删除所有房型下的房间数据
		String roomtypeid= String.valueOf(tRoomType.get("roomtypeid"));
		if(roomtypeid==null || StringUtils.isBlank(roomtypeid)){
			return ;
		}
		List<Bean> tRoomList = roomDAO.findRoomsByRoomtypeid(roomtypeid);
		logger.info("同步房间:删除房型{}:删除房间:{}", roomtypeid, JsonKit.toJson(tRoomList));
		//if(tRoomList == null) return;
		for (Bean tRoom : tRoomList) {
			String troomid = tRoom.get("id").toString();
			deleteRoom(troomid, changemap);
			
			String tRoomName = tRoom.get("name").toString();
			logDelRoomNo(tRoomName, changemap);//记录删除的房间号
		}
		
		String troomtypeid = tRoomType.get("roomtypeid").toString();
		List params = new ArrayList();
		params.add(troomtypeid);
		List delrtList= (List)changemap.get("delroomtype");
		delrtList.add(params);
		
		String pms= tRoomType.get("pms");
		logDelRoomType(troomtypeid, pms, changemap);
		
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
	 * 增加房型
	 * @param ehotel
	 * @param roomType
	 */
	private boolean addRoomType(Bean ehotel,THotel thotel,Map roomtype,Map changemap){
		if(roomtype == null ) {
			return true;
		}
		TRoomType troomType = new TRoomType();
		
		BigDecimal cost = BigDecimal.ZERO;
		if( roomtype.get("price") != null ) {
			cost = new BigDecimal(roomtype.get("price").toString());
		}
		if(BigDecimal.ZERO.equals( cost )){
			logger.info("同步hotelid:{},房型:{}价格为0，直接返回，不做处理.", ehotel.get("id").toString(), roomtype.get("name").toString());
			return false;
		}
		
		troomType.set("cost", roomtype.get("price"));
		troomType.set("ehotelid",ehotel.get("id").toString());
		if(thotel!=null && thotel.get("id")!=null){
			troomType.set("thotelid",thotel.get("id").toString());
		}else{
			troomType.set("thotelid",ehotel.get("id").toString());
		}
		troomType.set("name",roomtype.get("name").toString());
		troomType.set("pms",roomtype.get("pmsno"));
		troomType.set("roomnum", roomtype.get("roomnum"));
		troomType.set("bednum",0L);
		
		
		TRoomType rte=roomtypeDAO.getTRoomTypeByEhotelidPMS(ehotel.get("id").toString(),roomtype.get("pmsno").toString());
		boolean succeed=true;
		if(rte==null){
			succeed=troomType.save();
		}else{
			rte.set("cost", roomtype.get("price"));			
			rte.set("name",roomtype.get("name").toString());			
			rte.set("roomnum", roomtype.get("roomnum"));
			succeed= rte.update();
		}		
		
		if(!succeed){
			throw MyErrorEnum.errorParm.getMyException("保存房型失败");
		}
		String roomtypeid=troomType.get("id").toString();
		
		//添加默认酒店设施
		String initids=SysConfigManager.getInstance().readOne(Constant.mikeweb, Constant.initTFacility);
		if(StringUtils.isNotBlank(initids)){
//			List<Bean> facilityList = hotelDAO.findFacilityByIds(initids);
//			for(Bean b: facilityList){
//				
//			}
			String ids[]=initids.split(",");
			if(ids!=null){
				for (String id : ids) {
					if(StringUtils.isNotBlank(id)){
						List fparam= new ArrayList();
						fparam.add(roomtypeid);
						fparam.add(id);
						List fl = (List)changemap.get("adderoomtypefacility");
						fl.add(fparam);
					}
				}
			}
		}
		
		List eroomtype = new ArrayList();
		eroomtype.add(roomtypeid);
		List eroomtypelist =(List)changemap.get("adderoomtypeinfo");
		eroomtypelist.add(eroomtype);
		
		if(thotel!=null){
			List troomtype = new ArrayList();
			troomtype.add(roomtypeid);
			List troomtypelist = (List)changemap.get("addtroomtypeinfo");
			troomtypelist.add(troomtype);
		}
        
        if(roomtype.get("room") == null ) {
            return true;
        }
        		
		List<Map> tRoomList = new ArrayList<Map>();
		if( roomtype.get("room") !=null ){
			tRoomList = (List)roomtype.get("room");
		}
		for (Map room : tRoomList) {
			addRoom(ehotel, roomtypeid, room,changemap);
		}
		return true;
	}

	@Override
	public void pmsOnline(Long hotelid, Long time) {
		THotelModel tHotelModel = new THotelModel();
		tHotelModel.setId(hotelid);
		tHotelModel.setPmsopttime(time);
		tHotelModel.setOnline("T");
		tHotelMapper.updateTHotel(tHotelModel);
	}

	@Override
	public void pmsOffline(Long hotelid, Long time) {
		THotelModel tHotelModel = new THotelModel();
		tHotelModel.setId(hotelid);
		tHotelModel.setPmsopttime(time);
		tHotelModel.setOnline("F");
		tHotelMapper.updateTHotel(tHotelModel);
	}
}