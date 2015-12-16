package com.mk.ots.hotel.dao;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.orm.plugin.bean.IAtom;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.HotelRoomTempInfo;
import com.mk.ots.hotel.bean.SelectRoomDto;
import com.mk.ots.hotel.bean.TRoom;
import com.mk.ots.hotel.bean.TRoomRepair;
import com.mk.ots.hotel.bean.TRoomType;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.order.bean.PmsOrder;
import com.mk.ots.order.bean.PmsRoomOrder;
import com.mk.pms.bean.PmsCost;

/**
 * 酒店DAO操作
 *
 * @author LYN
 *
 */
@Repository
public class HotelDAO {

	private Logger logger = LoggerFactory.getLogger(HotelDAO.class);

	private Date splitTime(Date begintime) {
		String splitTime = Constant.splitTime;
		Calendar beginCal = Calendar.getInstance();
		beginCal.setTime(begintime);
		if (beginCal.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(splitTime)) {
			beginCal.add(Calendar.DATE, -1);
			begintime = beginCal.getTime();
		}
		return begintime;
	}

	/**
	 * 查询城市酒店
	 *
	 * @param cityId
	 * @return
	 */
	public List<THotel> findByCityId(String cityId, int page, int limit, boolean isdiscount, boolean ishotelpic, boolean isroomtype, boolean isroomtypepic, boolean isroomtypefacility,
			boolean isfacility, boolean isbusinesszone, boolean isbedtype) {
		String sql = "select * from t_hotel h " + "where exists (" + "select a.hotelid from t_hotel_city a " + "left join t_city b on b.Code=a.cityid " + "where h.id=a.hotelid and a.cityid=?)";
		List<THotel> hotelList = new ArrayList();
		try {
			hotelList = THotel.dao.find(sql, cityId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hotelList;
	}

	/**
	 * 查询房型详细信息数据
	 *
	 * @param hotelid
	 */
	public List findRoomTypeInfoByHotelID(String hotelid) {
		String sql = "select * from t_roomtype r left join t_roomtype_info b on r.id=b.roomtypeid where exists(select id from t_hotel where r.thotelid=id and id=?)";
		List<Bean> roomTypeList = Db.find(sql, hotelid);
		return roomTypeList;
	}

	/**
	 * 返回房型信息
	 *
	 * @param ehotelid
	 * @return
	 */
	public List<Bean> findRoomTypeByEHotelid(String hotelid) {
		String sql = "select a.hotelname, b.id as roomtypeid,b.name as roomtypename,b.pms,b.bednum,b.roomnum,b.cost from t_roomtype b left join e_hotel a on a.id=b.thotelid where b.thotelid=?";
		List<Bean> roomTypeList = Db.find(sql, hotelid);
		return roomTypeList;
	}

	/**
	 * 好像有误，
	 *
	 * @param hotelid
	 * @param bednum
	 * @return
	 */
	public List<Bean> findRoomTypeByHotelid(String hotelid, String bednum) {
		String sql = "select a.hotelname, b.id as roomtypeid,b.name as roomtypename,b.pms,b.bednum,b.roomnum,b.cost,c.minarea,c.maxarea,c.pics,c.bedtype,d.name as bedname,c.bedsize from t_roomtype b left join t_hotel a on a.id=b.thotelid  left join e_roomtype_info c on b.id=c.roomtypeid left join t_bedtype d on c.bedtype=d.id where b.thotelid=? ";
		List<Bean> roomTypeList = new ArrayList();
		if (StringUtils.isNotBlank(bednum)) {
			sql += " and c.bedtype=?";
			roomTypeList = Db.find(sql, hotelid, bednum);
		} else {
			roomTypeList = Db.find(sql, hotelid);
		}
		return roomTypeList;
	}

	public List<Bean> findTRoomTypeByHotelid(String citycode, String hotelid, String roomtypeid, String bednum, String startdateday, String enddateday) {
		this.logger.info("从t_roomtype_info 取数据，如果未审核，无对应数据,hotelid:{},roomtypeid:{},bednum:{},startdateday:{},enddateday:{}", hotelid, roomtypeid, bednum, startdateday, enddateday);
		List<Bean> roomTypeList = new ArrayList<Bean>();
		try {
			String sql = "select a.id as roomtypeid,thotelid,ehotelid,name as roomtypename,pms,c.bedtype as bednum,roomnum,cost,b.mincost " + " from t_roomtype a left join ( "
					+ " select roomtypeid, min(cost) as mincost from b_costtemp_" + citycode + " where hotelid=?  and time >=? and time<=? group by roomtypeid) b "
					+ " on a.id=b.roomtypeid left join t_roomtype_info c on c.roomtypeid=a.id where a.thotelid=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(hotelid);
			params.add(startdateday);
			params.add(enddateday);
			params.add(hotelid);

			if (StringUtils.isNotBlank(roomtypeid)) {
				sql += "and a.id=?";
				params.add(roomtypeid);
			}
			if (StringUtils.isNotBlank(bednum)) {
				sql += " and c.bedtype=?";
				params.add(bednum);
			}
			roomTypeList = Db.find(sql, params.toArray());
		} catch (Exception e) {
			this.logger.error("HotelDAO findTRoomTypeByHotelid method error: {} ", e.getMessage());
		}
		return roomTypeList;
	}

	/**
	 * 查询酒店设施
	 *
	 * @param hotelID
	 * @return
	 */
	public List findFacilityByHotelID(String hotelid) {
		String sql = "select id,facname,factype,binding,facsort,visible from t_facility f where exists( select facid from t_hotel_facility where facid= f.id and hotelid=? )";
		List facilityList = Db.find(sql, hotelid);
		return facilityList;
	}

	/**
	 * 查询商圈
	 *
	 * @param hotelid
	 * @return
	 */
	public List findBussinessByHotelID(String hotelid) {
		String sql = "select  h.hotelid,z.id,z.businesszonetype,t.name as typename,z.name,z.dis,z.fatherid,z.cityid " + "from t_hotelbussinesszone h  "
				+ "left join t_businesszone z on h.businesszoneid= z.id " + "left join t_businesszonetype t on z.businesszonetype=t.id " + "where h.hotelid=?";
		List bussinessList = Db.find(sql, hotelid);
		return bussinessList;
	}

	/**
	 * 查询房型设施
	 *
	 * @param typeid
	 * @return
	 */
	public List findRoomTypeFacilityByTypeid(String typeid) {
		String sql = "select r.id,r.roomtypeid,r.facid,f.facname,f.factype,f.binding,f.facsort,f.visible from t_roomtype_facility r left join t_facility f on r.facid= f.id where roomtypeid=?";
		List<Bean> roomTypeFacilityList = Db.find(sql, typeid);
		return roomTypeFacilityList;
	}

	/**
	 * 查询床
	 *
	 * @param typeid
	 * @return
	 */
	public List findRoomBedByTypeid(String typeid) {
		String sql = "select a.id,a.roomtypeid,a.num,a.bedtype,a.otherinfo,bt.name,bt.visible,bl.name as lengths,bl.visible " + "from t_roomtype_bed a    "
				+ "left join t_bedtype bt on a.bedtype=bt.id    " + "left join t_roomtyle_length l  on l.roomtypebedid= a.id    " + "left join t_bedlengthtype bl on l.bedlengthid=bl.id "
				+ "where roomtypeid=?";
		List<Bean> bedList = Db.find(sql, typeid);
		return bedList;
	}

	/**
	 * 查询酒店信息
	 *
	 * @param hotelid
	 * @return
	 */
	public THotel findHotelByHotelid(String hotelid) {
		String sql = "select id,hotelname,hotelcontactname,regtime,disid,detailaddr,longitude,latitude,opentime,repairtime,roomnum,introduction,traffic,hotelpic,peripheral,businesslicensefront,businesslicenseback,pms,visible,online,idcardfront,idcardback,retentiontime,defaultleavetime,needwait from t_hotel a where id=?";
		THotel th = THotel.dao.findFirst(sql, hotelid);
		return th;
	}


	public List<PmsOrder> findPmsOrderByHotelIdAndPmsOrderNo(Long hotelId, Long orderidPms) {
		return PmsOrder.dao.find("select * from b_pmsorder where hotelid=? and Pmsorderno=?", hotelId, orderidPms);
	}

	public PmsOrder findPmsOrderByHotelIdAndPmsOrderNo(Long hotelId, String orderid, String batchno) {
		String sql = "select * from b_pmsorder t where t.Hotelid=? and t.Pmsorderno=? and t.PmsRoomTypeOrderNo=?";
		return PmsOrder.dao.findFirst(sql, hotelId, orderid, batchno);
	}

	public List<PmsCost> findPmsCostByHotelIdAndCostId(Long hotelId, List<String> ids) {
		List<Object> paras = new ArrayList<>();
		paras.add(hotelId);
		StringBuffer wenhao = new StringBuffer();
		for (String id : ids) {
			wenhao.append("?,");
			paras.add(id);
		}
		wenhao.setLength(wenhao.length() - 1);
		return PmsCost.dao.find("select * from b_PmsCost where hotelid=? and roomcostno in(" + wenhao.toString() + ")", paras.toArray());
	}

	public PmsRoomOrder findPmsRoomOrderByHotelIdAndCustomno(Long hotelId, String customerno) {
		String sql = "select * from b_pmsroomorder where hotelId=? and pmsRoomOrderNo=?";
		return PmsRoomOrder.dao.findFirst(sql, hotelId, customerno);
	}

	public TRoom findTRoomByHotelIdAndPmsno(Long hotelId, String roomPmsNo) {
		String sql = "select r.* from t_room r, t_roomtype rt where rt.id = r.roomtypeid and rt.ehotelid =? and r.pms=?";
		return TRoom.dao.findFirst(sql, hotelId, roomPmsNo);
	}

	public void resetAllOrder(Long hotelId) {
		try {
			// String sql1 =
			// "update b_pmsroomorder set visible='F' where hotelid=? and status = 'IN' and visible = 'T'";
			String sql2 = "update b_pmsroomorder set visible='F' where hotelid=? and status = 'RE' and visible = 'T'";
			String sql3 = "update b_pmsroomorder set visible='F' where hotelid=? and status = 'PM' and visible = 'T'";
			String sql4 = "update b_pmsorder set visible='F' where Ordernum<>Plannum and hotelid=?";
			// Db.update(sql1, hotelId);
			this.updateHotelInStatusVisableF(hotelId);
			Db.update(sql2, hotelId);
			Db.update(sql3, hotelId);
			Db.update(sql4, hotelId);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void updateHotelInStatusVisableF(Long hotelId) {
		List<Long> idList = this.selectHotelInStatusId(hotelId);
		int length = idList.size();
		int step = 20;
		for (int i = 0; i < length; i += step) {
			this.updateHotelInStatusVisableF(i, step, idList);
		}
	}

	private void updateHotelInStatusVisableF(int start, int step, List<Long> idList) {
		String inSql = this.getInSql(start, start + step, idList);
		String updateSql = "update b_pmsroomorder set visible='F' where id in (" + inSql + ")";
		Db.update(updateSql);
	}

	private String getInSql(int start, int end, List<Long> idList) {
		StringBuilder inSql = new StringBuilder();
		for (int i = start; (i < end) && (i < idList.size()); i++) {
			inSql.append(idList.get(i) + ",");
		}
		return inSql.substring(0, inSql.length() - 1);
	}

	private List<Long> selectHotelInStatusId(Long hotelId) {
		String sql = "select id from b_pmsroomorder where hotelid = ? and status = 'IN' and visible = 'T'";
		List<Bean> beanList = Db.find(sql, hotelId);
		List<Long> idList = new ArrayList<Long>();
		for (Bean bean : beanList) {
			idList.add(bean.getLong("id"));
		}
		return idList;
	}

	public List<TRoomRepair> findAllTRoomRepairByHotelId(Long hotelId) {
		return TRoomRepair.dao.find("select * from t_room_repair where hotelid=?", hotelId);
	}

	public TRoomRepair findTRoomRepairByHotelIdAndRepairId(Long hotelId, String repairId) {
		return TRoomRepair.dao.findFirst("select * from t_room_repair where hotelid=? and repairId=?", hotelId, repairId);
	}

	// 查询eHotel
	public Bean findEHotelByHotelid(String hotelid) {
		String sql = "select * from e_hotel where id=?";
		return Db.findFirst(sql, hotelid);
	}

	// 查询tHotel
	public Bean findThotelByHotelid(Long hotelid) {
		String sql = "select * from t_hotel where id=?";
		return Db.findFirst(sql, hotelid);
	}

	/**
	 * 查询房间
	 *
	 * @param roomtypeid
	 * @return
	 */
	public List<Bean> findRoomByRoomtypeid(String roomtypeid) {
		String sql = "select * from t_room where roomtypeid=?";
		return Db.find(sql, roomtypeid);
	}

	/**
	 * 查询酒店设施
	 *
	 * @param facilityid
	 * @return
	 */
	public Bean findFacilityByfacid(String facilityid) {
		String sql = " select * from t_facility where id=?";
		return Db.findFirst(sql, facilityid);
	}

	/**
	 * 查询房型
	 *
	 * @param string
	 */
	public Bean findRoomTypeByRoomtypeid(String roomtypeid) {
		String sql = "select * from t_roomtype where id=?";
		return Db.findFirst(sql, roomtypeid);
	}

	public List<PmsOrder> findNeedPmsOrderSelect(Long hotelid) {
		String sql = "select * from b_pmsorder t where t.Hotelid=? and t.visible = 'F'";
		return PmsOrder.dao.find(sql, hotelid);

	}

	public List<PmsRoomOrder> findNeedPmsRoomOrderSelect(Long hotelid,String roomNos) {
		String sql = "select * from b_pmsroomorder t where t.Hotelid=? and status in ('IN','RE','PM') and t.Endtime > ?";
		if(StringUtils.isNotEmpty(roomNos)){
			StringBuffer sb = new StringBuffer();
			for (String roomNo:roomNos.split(",")) {
				sb.append("'").append(roomNo).append("'").append(",");
			}
			if(sb.length()>0){
				sb.setLength(sb.length()-1);
			}
			sql+=" and t.Roomno in ("+sb.toString()+")";
		}
		return PmsRoomOrder.dao.find(sql, hotelid, DateUtils.getTodayBeginTime());
		
	}
	public List<PmsRoomOrder> findNeedPmsRoomOrderSelectBefore(Long hotelid,String roomNos) {
		String sql = "select * from b_pmsroomorder t where t.Hotelid=? and status in ('IN','RE','PM') and t.Endtime < ?";
		if(StringUtils.isNotEmpty(roomNos)){
			sql+=" and t.Roomno in ?";
			return PmsRoomOrder.dao.find(sql, hotelid, DateUtils.getTodayBeginTime(),roomNos);
		}
		return PmsRoomOrder.dao.find(sql, hotelid, DateUtils.getTodayBeginTime());
	}
	
	public static void main(String[]args){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(sdf.format(c.getTime()));
	}

	/**
	 * 同步非签约酒店
	 *
	 * @param tableList
	 */
	public boolean syncNoPMSHotel(final List tableList) {
		final String sql = "insert into t_hotel_nopms (hotelid,hoteltype,hotelname,addr,tel,pics,facility,descr) values(?,?,?,?,?,?,?,?)";
		boolean succeed = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				int count = 0;
				for (int i = 0; i < tableList.size(); i++) {
					List row = (List) tableList.get(i);
					count += Db.update(sql, row.toArray());
				}
				return count == tableList.size() ? true : false;
			}
		});
		return succeed;
	}

	public TRoomType findTRoomTypeByPmsno(Long hotelId, String pmsno) {
		List<TRoomType> list = TRoomType.dao.find("select * from t_roomtype t where ehotelid=? and pms=?", hotelId, pmsno);
		if (list.size() == 0) {
			return null;
		} else if (list.size() > 1) {
			this.logger.warn("酒店id：" + hotelId + "中 pms号为：" + pmsno + "存在" + list.size() + "条房型记录！！！！");
		}
		return list.get(0);
	}
	public List<PmsRoomOrder> findUnSynedPmsRoomOrder(Long hotelid,String customerNos) {
		String sql = "select id,PmsRoomOrderNo,Hotelid,HotelPms,PmsOrderId,Status,RoomTypePms,PmsOrderNo,RoomTypeId,RoomId,Roomno,RoomTypeName,RoomPms,Begintime,Endtime,checkintime,checkouttime,Ordertype,Roomcost,Othercost,Mikepay,Otherpay,Opuser,visible from b_pmsroomorder where Hotelid=? and status in ('IN','RE','PM') and Endtime > ?";
		if(StringUtils.isNotEmpty(customerNos)){
			StringBuffer sb = new StringBuffer();
			for (String customerNo:customerNos.split(",")) {
				sb.append("'").append(customerNo).append("'").append(",");
			}
			if(sb.length()>0){
				sb.setLength(sb.length()-1);
			}
			sql+=" and PmsRoomOrderNo not in ("+sb.toString()+")";
		}
		return PmsRoomOrder.dao.find(sql, hotelid, DateUtils.getTodayBeginTime());
		//return PmsRoomOrder.dao.find(sql, hotelid, "2015-06-09 12:12:12");
	}
}
