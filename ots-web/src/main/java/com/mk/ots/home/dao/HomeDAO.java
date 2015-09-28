package com.mk.ots.home.dao;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import redis.clients.jedis.Jedis;

import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.util.HomeConst;
import com.mk.ots.home.util.ValueUtil;
import com.mk.ots.manager.OtsCacheManager;

@Repository
public class HomeDAO {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeDAO.class);
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private OtsCacheManager cacheManager;
	
	public List<Map<String, Object>> getHomeDatas(Long hotelId, String date, int addDays) {
		Date begintime = DateUtils.addDays(DateUtils.getDateFromString(date + " 00:00:00"), addDays);
		Date endtime = DateUtils.addDays(DateUtils.getDateFromString(DateUtils.getDateAdded(1, date) + " 00:00:00"), addDays);
		
		String sql = "SELECT \n" +
				"    a.Hotelid,\n" +
				"    a.hotelRoomNum,\n" +
				"    a.pmsOrderNum,		--  PMS订单间夜数\n" +
				"    a.pmsCheckinNum,	--  PMS入住间夜数\n" +
				"    a.pmsRevenues,		--  PMS营业额\n" +
				"    b.otaOrderNum,		--  OTA订单间夜数\n" +
				"    b.otaCheckinNum,	--  OTA入住间夜数\n" +
				"    b.otaRevenues		--  OTA营业额\n" +
				"FROM\n" +
				"    (SELECT \n" +
				"        t.Hotelid,\n" +
				"            MAX(h.roomNum) 'hotelRoomNum',\n" +
				"            SUM(CASE\n" +
				"                WHEN\n" +
				"                    Status IN ('RE' , 'IN', 'OK')\n" +
				"                        AND t.Begintime >= '2015-05-25'\n" +
				"                        AND t.Begintime < '2015-05-26'\n" +
				"                THEN\n" +
				"                    1\n" +
				"                ELSE 0\n" +
				"            END) 'pmsOrderNum',\n" +
				"            SUM(CASE\n" +
				"                WHEN\n" +
				"                    Status IN ('IN' , 'OK')\n" +
				"                        AND t.Checkintime >= '2015-05-25'\n" +
				"                        AND t.Checkintime < '2015-05-26'\n" +
				"                THEN\n" +
				"                    1\n" +
				"                ELSE 0\n" +
				"            END) 'pmsCheckinNum',\n" +
				"            SUM(CASE\n" +
				"                WHEN\n" +
				"                    Status IN ('IN' , 'OK')\n" +
				"                        AND c.Roomcost > 0\n" +
				"                        AND t.Checkintime >= '2015-05-25'\n" +
				"                        AND t.Checkintime < '2015-05-26'\n" +
				"                THEN\n" +
				"                    c.Roomcost\n" +
				"                ELSE 0\n" +
				"            END) 'pmsRevenues'\n" +
				"    FROM\n" +
				"        b_pmsroomorder t\n" +
				"    LEFT JOIN t_hotel h ON t.hotelid = h.id\n" +
				"    LEFT JOIN b_pmscost c ON t.PmsRoomOrderNo = c.customerno\n" +
				"        AND t.Hotelid = c.Hotelid\n" +
				"    WHERE\n" +
				"        " + (hotelId != null ? " t.Hotelid = " + hotelId +" and " : "") + "\n" +
				"        ((t.Begintime >= '2015-05-25'\n" +
				"            AND t.Begintime < '2015-05-26')\n" +
				"            OR (t.Checkintime >= '2015-05-25'\n" +
				"            AND t.Checkintime < '2015-05-26'))\n" +
				"    GROUP BY t.hotelid) a,\n" +
				"    (SELECT \n" +
				"        t.hotelid,\n" +
				"            COUNT(t.hotelid) 'otaOrderNum',\n" +
				"            COUNT(c.hotelid) 'otaCheckinNum',\n" +
				"            SUM(c.Roomcost) 'otaRevenues'\n" +
				"    FROM\n" +
				"        b_otaroomorder t\n" +
				"    LEFT OUTER JOIN b_pmsroomorder pro ON pro.Hotelid = t.hotelid\n" +
				"        AND pro.PmsRoomOrderNo = t.PMSRoomOrderNo\n" +
				"        AND pro.Status IN ('IN' , 'OK')\n" +
				"    LEFT JOIN b_PmsCost c ON pro.PmsRoomOrderNo = c.customerno\n" +
				"        AND pro.Hotelid = c.hotelid\n" +
				"        AND c.Roomcost > 0\n" +
				"    WHERE\n" +
				"        " + (hotelId != null ? " t.Hotelid = " + hotelId +" and " : "") + "\n" +
				"        ((t.Begintime >= '2015-05-25'\n" +
				"            AND t.Begintime < '2015-05-26')\n" +
				"            OR (pro.Checkintime >= '2015-05-25'\n" +
				"            AND pro.Checkintime < '2015-05-26'))\n" +
				"    GROUP BY t.Hotelid) b\n" +
				"WHERE\n" +
				"    a.hotelid = b.hotelid";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("begintime", begintime);
		paramMap.put("endtime", endtime);
		return namedParameterJdbcTemplate.queryForList(sql, paramMap);
	}

	public void genHomeDatas(Long hotelId, final String date, int addDay) {
		final List<Map<String, Object>> datas = getHomeDatas(hotelId, date, addDay);
		logger.info("genHomeDatas,hotelId:{},date:{}", hotelId, date);
		// 遍历每日的营业额，设置最高营业额、最高入住夜间数
		compareAndSetMaxPrice(datas);
		String sql = "delete from h_hotel_orders_daily where date=?";
		// 先删掉今天的数据，避免重复
		jdbcTemplate.update(sql, new Object[] { DateUtils.getDateFromString(date + " 00:00:00") });
		sql = "insert into h_hotel_orders_daily "
				+ "(hotelId, date, pmsOrderNum, otaOrderNum, pmsCheckinNum, otaCheckinNum, pmsRevenues, otaRevenues, hotelRoomNum) values "
				+ "(:Hotelid, :date, :pmsOrderNum, :otaOrderNum, :pmsCheckinNum, :otaCheckinNum, :pmsRevenues, :otaRevenues, :hotelRoomNum)";
		final Set<Long> hotelids = new HashSet<>();
		for (Map<String, Object> map : datas) {
			map.put("date", DateUtils.getDateFromString(date + " 00:00:00"));
		}

		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		namedParameterJdbcTemplate.batchUpdate(sql, datas.toArray(new Map[0]));

		for (Long id : hotelids) {
			cacheManager.setExpires(HomeConst.HOTEL_STATISTICS_CACHE, HomeConst.CACHE_HOTEL_ORDER_DAILY_LIST_60 + id, new ArrayList(), 1);
		}
		logger.info("genHomeDatas{},begintidateme{},ok", hotelId, date);
	}
	
	/**
	 * 调度时比较并写入maxprice、和缓存
	 * @param datas
	 */
	private void compareAndSetMaxPrice(final List<Map<String, Object>> datas) {
		for (Map map : datas) {
			long _hotelId = ValueUtil.getLong(map, "Hotelid");
			Map maxPrice = getHotelMaxPrice(_hotelId);
			StringBuffer s = new StringBuffer();
			// OTA入住间夜数
			if (maxPrice.get("otaCheckinNum") != null
					&& ValueUtil.getInt(map, "otaCheckinNum") > ValueUtil.getInt(maxPrice, "otaCheckinNum")) {
				// 更新数据库，更新缓存，异步执行处理
				maxPrice.put("otaCheckinNum", ValueUtil.getInt(map, "otaCheckinNum"));
				s.append(" otaCheckinNum=").append(ValueUtil.getInt(map, "otaCheckinNum")).append(",");
			}
			// PMS最高营业额
			if (maxPrice.get("pmsRevenues") != null
					&& ValueUtil.getBigDecimal(map, "pmsRevenues").doubleValue() > ValueUtil.getBigDecimal(maxPrice,
							"pmsRevenues").doubleValue()) {
				maxPrice.put("pmsRevenues", ValueUtil.getBigDecimal(map, "pmsRevenues"));
				s.append(" pmsRevenues=").append(ValueUtil.getBigDecimal(map, "pmsRevenues").doubleValue()).append(",");
			}
			// OTA最高营业额
			if (maxPrice.get("otaRevenues") != null
					&& ValueUtil.getBigDecimal(map, "otaRevenues").doubleValue() > ValueUtil.getBigDecimal(maxPrice,
							"otaRevenues").doubleValue()) {
				maxPrice.put("otaRevenues", ValueUtil.getBigDecimal(map, "otaRevenues"));
				s.append(" otaRevenues=").append(ValueUtil.getBigDecimal(map, "otaRevenues").doubleValue()).append(",");
			}
			if (s.length() > 0) {
				s.setLength(s.length() - 1);
				jdbcTemplate.update(
						"update t_hotel_maxPrice set " + s.toString() + " where hotelId=?",
						new Object[] { _hotelId });
			}
		}
	}
	
	/**
	 * 获取酒店的maxPrice缓存
	 * @return
	 */
	public Map getHotelMaxPrice(Long hotelId){
		Map maxPrice = (Map) cacheManager.get(HomeConst.HOTEL_STATISTICS_CACHE, HomeConst.CACHE_HOTEL_MAX_PRICE_PREFIX + hotelId);
		if (maxPrice == null) {
			try {
				maxPrice = jdbcTemplate.queryForMap("select * from t_hotel_maxPrice t where t.hotelId = ?", new Object[] { hotelId });
			} catch (Exception e) {
				maxPrice = null;
			}
			if (maxPrice == null) {
				jdbcTemplate.update("insert into t_hotel_maxPrice (hotelId, otaCheckinNum, pmsRevenues, otaRevenues) values(?,?,?,?)", new Object[]{hotelId,0,0,0});
				maxPrice = new HashMap();
				maxPrice.put("hotelId", hotelId);
				maxPrice.put("otaCheckinNum", 0);
				maxPrice.put("pmsRevenues", 0);
				maxPrice.put("otaRevenues", 0);
			}
			// 缓存一天的时间
			cacheManager.setExpires(HomeConst.HOTEL_STATISTICS_CACHE, HomeConst.CACHE_HOTEL_MAX_PRICE_PREFIX + hotelId, maxPrice, (int)TimeUnit.DAYS.toMillis(1));
		}
		return maxPrice;
	}
	
	/**
	 * 构建单日流水数据，注意这里是大于beginDate 小于等于 endDate
	 * @param hotelId
	 * @param beginDate
	 * @param endDate
	 */
	public void genBillDetails(Long hotelId, String beginDate, String endDate){
		logger.info("genBillDetails{},begintidateme{},endDate{}",hotelId, beginDate, endDate);
		String sql = "SELECT\n" +
				"	'0' daydetailtype,\n" +
				"	o.id as otaorderid,\n" +
				"	ro.id as otaroomorderid,\n" +
				"	o.hotelid,\n" +
				"	CURDATE() + 0 as orderdate,\n" +
				"	ro.RoomId,\n" +
				"	ro.RoomNo,\n" +
				"	rp.Price roomPrice,\n" +
				"	0.1 commissionRate,\n" +
				"	rp.Price commission,\n" +
				"	0.01 commissionDRate,\n" +
				"	case when (hotelDiscuntOnly.price + pays.SHTX) is null then 0 else (hotelDiscuntOnly.price + pays.SHTX) end hotelDiscuntOnly, -- 商户贴现\n" +
				"	case when pays.PMS_TXFT is null then 0 else pays.PMS_TXFT end hotelDiscount,-- 商户贴现分摊\n" +
				"	case when pays.OAT_TXFT is null then 0 else pays.OAT_TXFT end otaDiscount, -- OTA贴现分摊\n" +
				"	0 conectHotelId,\n" +
				"	0 conectHotelprice,\n" +
				"	o.Mid,\n" +
				"	0 payBack,\n" +
				"	o.spreadUser as conectpersonid, --  切客id\n" +
				"	5.00 conectPersonPrice, -- 切客返佣\n" +
				"	0 isConfirm,\n" +
				"	CURRENT_DATE createTime,\n" +
				"	'' updTime\n" +
				"FROM\n" +
				"	b_otaroomprice rp \n" +
				"INNER JOIN b_otaroomorder ro ON rp.OtaRoomOrderId = ro.id \n" +
				"INNER JOIN b_otaorder o ON o.OrderStatus IN (180, 190, 200, 520)\n" +
				"AND o.id = ro.OtaOrderId \n" +
				"INNER JOIN t_hotel h ON h.id = o.HotelId\n" +
				"left join (select t.id, sum( case p.isota when 'T' then (-ut.cost * p.otapre) else 0 end) OAT_TXFT,-- OTA贴现分摊\n" +
				"						sum( case p.isota when 'T' then -ut.cost * (1 -p.otapre) else 0 end ) PMS_TXFT,-- 店铺贴现分摊\n" +
				"						sum(case p.isota when 'F' then -ut.cost else 0 end ) SHTX  -- 商户贴现 剩余部分\n" +
				"		from p_pay t, u_useticket_record ut, b_promotion p  where \n" +
				"		p.id = ut.promotionid  \n" +
				"		and ut.return = 'F'\n" +
				"		and t.id = ut.payid group by t.id) pays -- 分摊 补贴剩余部分\n" +
				"	on pays.id = o.id\n" +
				"left join (select p.otaorderid, sum(p.offlineprice) price \n" +
				"			from b_promotion_price p where \n" +
				"				p.promotion=16 group by p.otaorderid) hotelDiscuntOnly -- 商户贴现\n" +
				"on hotelDiscuntOnly.otaorderid = o.id\n" +
				"WHERE\n" +
				"	rp.ActionDate > :beginTime and rp.ActionDate <= :endTime";
		if (hotelId != null) {
			sql += " and h.id = :hotelId";
		}
		final NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("createTime", DateUtils.getDateFromString(DateUtils.getDate()));
		namedParameterJdbcTemplate.update("delete from t_bill_detail where createTime = :createTime", paramMap);
		final String insertSql = "insert into t_bill_detail"
				+ "(  dayDetailType, otaorderId, otaroomorderId, hotelId, orderDate, roomId, roomNo, roomPrice, commissionRate, commission, commissionDRate, hotelDiscuntOnly, hotelDiscount, otaDiscount, conectHotelId, conectHotelprice, mId, payBack, conectPersonId, conectPersonPrice, isConfirm, createTime, updTime) values "
				+ "( :daydetailtype, :otaorderid, :otaroomorderid, :hotelid, :orderdate, :roomid, :roomno, :roomprice, :commissionrate, :commission, :commissiondrate, :hoteldiscuntonly, :hoteldiscount, :otadiscount, :conecthotelid, :conecthotelprice, :mid, :payback, :conectpersonid, :conectpersonprice, :isconfirm, :createtime, :updtime)";
		
		paramMap.clear();
		paramMap.put("hotelId", hotelId);
		paramMap.put("beginTime", beginDate.replace("-", ""));
		paramMap.put("endTime", endDate.replace("-", ""));
		logger.info("genBillDetails,paramMap{}",paramMap);
		final List<Map> result = new ArrayList<Map>();
		namedParameterJdbcTemplate.query(sql, paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				Map<String, Object> paramMap = new HashMap<String, Object>();
				// ResultSet转到Map里
				ResultSetMetaData meta = (ResultSetMetaData) rs.getMetaData();
				for (int i = 0; i < meta.getColumnCount(); i++) {
					paramMap.put(meta.getColumnLabel(i+1).toLowerCase(), rs.getObject(i+1));
				}
				paramMap.put("createtime", DateUtils.getDateFromString(DateUtils.getDate()));
				result.add(paramMap);
				// 添加、更新到缓存
				if (result.size() >= 100) {
					logger.info("genBillDetails,size{}",result.size());
					namedParameterJdbcTemplate.batchUpdate(insertSql, result.toArray(new Map[0]));
					result.clear();
				}
			}
		});
		if (result.size() > 0) {
			logger.info("genBillDetails,size{}",result.size());
			namedParameterJdbcTemplate.batchUpdate(insertSql, result.toArray(new Map[0]));
		}
		saveJobHistory(HomeConst.BILL_DETAIL_JOB_HIS, endDate, 1);
		logger.info("genBillDetails{},begintidateme{},endDate{},ok",hotelId, beginDate, endDate);
	}
	
	/**
	 * 查job日志
	 * 
	 */
	public Map getJobHistory(String job){
		try {
			Map his = jdbcTemplate.queryForMap("select * from t_job_history where job = ?", new Object[]{job});
			return his;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 构建checkerbill的数据【6.8.	酒店前台切客收益表】
	 * 每30分钟执行此跑批任务
	 * @param beginDate
	 * @param endDate
	 */
	public void genCheckerBill(String beginDate, String endDate) {
		Jedis jedis = cacheManager.getNewJedis();
		try {
			logger.info("job::genCheckerBill::清空::{}", "*" + HomeConst.CACHE_CHECKER_BILL_2WEEKS_PREFIX + "*");
			jedis.del("*" + HomeConst.CACHE_CHECKER_BILL_2WEEKS_PREFIX + "*");
		} finally {
			jedis.close();
		}
	}
	
	public void saveJobHistory(String job, String runTime, int status){
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("job", job);
		paramMap.put("last_run_time", runTime);
		paramMap.put("status", status);
		int r = namedParameterJdbcTemplate.update("update t_job_history set last_run_time = :last_run_time, status = :status where job = :job", paramMap);
		if (r == 0) {
			namedParameterJdbcTemplate.update("insert into t_job_history (job, last_run_time, status) values (:job, :last_run_time, :status)", paramMap);
		}
	}

}
