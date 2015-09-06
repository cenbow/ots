package com.mk.ots.bill.dao;
/**
 * p_orderlog allcost	//总价
				hotelgive //商户补贴 
				otagive //ota补贴
				usercost //用户应支付
				realcost //用户实际支付
				realotagive //在ota贴现中实际ota平台贴现
				refund //资金偿还 偿还数额
				qiekeIncome //切客收益
 * b_bill_orders allCost //订单金额--allcost	//总价
				userCost //预付金额--realcost //用户实际支付
				cutCost //切客优惠金额(0L 10L)
				hotelGive //商户优惠金额--hotelgive //商户补贴 
				otherGive //其他优惠金额
				serviceCost //产生服务费
				prepaymentDiscount //预付贴现金额--qiekeIncome //切客收益20
				toPayDiscount //到付贴现金额--qiekeIncome //切客收益10
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.google.common.collect.ImmutableMap;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.home.util.HomeConst;

@Repository
public class BillOrderDAO {

	private static final Logger logger = LoggerFactory.getLogger(BillOrderDAO.class);
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private static final long TIME_FOR_FIFTEEN = 15 * 60 * 1000L;
	
	/**
	 * 生成每日账单管理的订单数据
	 * 每天查询本月内的没有插入到billorder里的数据
	 * @param beginTime
	 */
	public void genBillOrders(Date beginTime, String hotelid, String orderidId){
		logger.info("BillOrderDAO::genBillOrders{}::start", beginTime);
//		String startTime = DateUtils.getMonthFirstDay(DateUtils.getDatetime(beginTime)); //每月1号，当执行一个月的时候可以使用
//		String endTime = DateUtils.getMonthLastDay(DateUtils.getDatetime(beginTime)); //每月月底
//		endTime = DateUtils.getDateAdded(1, endTime); //下个月1号的时间
		//设置每天订单执行范围 当天00:00  小于第二天的时间
		String startTime = DateUtils.getStringFromDate(beginTime, DateUtils.FORMAT_DATE);
		String endTime = DateUtils.getDateAdded(1, startTime);
		
		//每天查询订单信息 sql
		StringBuffer sql = new StringBuffer();
		String sql0 = "SELECT "
				+ "o.id orderid, "
				+ "o.HotelId hotelid, "
				+ "o.OrderStatus orderstatus, "
				+ "o.Ordertype ordertype, "
				+ "o.Paystatus paystatus, "
				+ "o.Begintime begintime, "
				+ "o.Endtime endtime, "
				+ "o.Createtime orderCreatetime, "
				+ "o.daynumber daynumber, "
				+ "( "
					+ "CASE "
					+ "WHEN isnull(o.spreadUser) THEN "
					 + "1 " //非切客
					+ "ELSE "
					 + "2 " //切客 有值
					+ "END "
				+ ") AS spreaduser, "
				+ "bpp.promotion isPromotion, " //判断切客是否绑定优惠券 A规则 = 4 则绑定优惠券
				+ "o.Invalidreason invalidreason, "
				+ "ro.RoomTypeName roomtypename, "
				+ "ro.RoomNo roomno, "
				+ "ox.checkintime checkintime, "
				+ "o.rulecode rulecode "
				+ "FROM "
				+ "b_otaorder o "
					+ "LEFT JOIN b_otaroomorder ro ON o.id = ro.OtaOrderId "
					+ "LEFT JOIN b_pmsroomorder ox ON ro.PMSRoomOrderNo = ox.PmsRoomOrderNo "
					+ "AND ro.Hotelid = ox.Hotelid "
					+ "LEFT JOIN b_promotion_price bpp ON o.id = bpp.otaorderid "
				+ "WHERE "
				+ "ox.checkintime >= :startTime "
				+ "AND ox.checkintime < :endTime "
				+ "AND ((o.orderstatus IN (180, 190, 200) AND o.Ordertype = 2) "
				+ "OR (o.Ordertype = 1 AND o.Paystatus = 120)) ";
		sql.append(sql0);
		if(null != hotelid){
			sql.append("AND o.hotelid = " + hotelid);
		}
				
		//每天查询 订单各种金额 sql
		String sql2 = "SELECT "
				+ "o.id AS orderid, "
				+ "ol.payid AS payid, "
				+ "ol.allcost AS allcost, "
				+ "ol.hotelgive AS hotelgive,  "
				+ "ol.otagive AS otagive, "
				+ "( "
					+ "CASE " 
					+ "WHEN o.ordertype = 1 AND o.paystatus = 120 THEN ol.usercost " 
					+ "WHEN o.ordertype = 2 THEN 0 " 
					+ "ELSE 0 " 
					+ "END "
				+ ") AS usercost, " 
				+ "ol.realcost AS realcost, "
				+ "ol.realotagive AS realotagive, "
				+ "ol.qiekeIncome AS qiekeIncome "
				+ "FROM "
					+ "b_otaorder o "
					+ "LEFT JOIN p_pay p ON o.id = p.orderid "
					+ "LEFT JOIN p_orderlog ol ON p.id = ol.payid "
				+ "WHERE "
				+ "o.id = :orderid ";
		
		//插入到数据表中 b_bill_orders
		final String insertSql = "insert into b_bill_orders " 
				+ "(hotelid, orderid, ordertype, roomtypename, roomno, begintime, endtime, daynumber, allcost, usercost, cutcost, hotelgive, othergive, servicecost, createtime, isPromotion, Invalidreason, payStatus, spreadUser, checkintime, prepaymentDiscount, toPayDiscount, rulecode, orderCreatetime) values "
				+ "(:hotelid, :orderid, :ordertype, :roomtypename, :roomno, :begintime, :endtime, :daynumber, :allcost, :usercost, :cutcost, :hotelgive, :othergive, :servicecost, :createtime, :isPromotion, :invalidreason, :paystatus, :spreaduser, :checkintime, :prepaymentDiscount, :toPayDiscount, :rulecode, :orderCreatetime)";
		
		//查询每天订单条件 时间
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("startTime", startTime);
		paramMap.put("endTime", endTime);
		
		//执行查询 
		final List<Map> result = new ArrayList<Map>();
		List<Map<String,Object>> datas;
		int insertCount = 0;
		
		datas = namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap);
		logger.info("BillOrderDAO::genBillOrders, 查询订单完成,共计:{}条",datas.size());
		for (int i = 0; i < datas.size(); i++) {
			Map<String, Object> map = datas.get(i);
			Long orderid = (Long) map.get("orderId");
			
			//查询每条订单的各种金额条件 订单id
			Map<String, Object> paramMapSql2 = new HashMap<String, Object>();
			paramMapSql2.put("orderid", orderid);
			
			List<Map<String,Object>> datasCost = namedParameterJdbcTemplate.queryForList(sql2, paramMapSql2);
			Map<String, Object> datas2 = datasCost.get(0);
			if(orderid.longValue() == ((Long)datas2.get("orderid")).longValue()){
				Long spreaduser = (Long)map.get("spreaduser");//1=非切克 2=切客
				Integer invalidreason = (Integer)map.get("invalidreason");//有值=非切客 空=切客
				if(invalidreason == null){
					invalidreason = 0;
				}
				Long isPromotion = (Long)map.get("isPromotion");//4=旧的切客 其他则无关
				BigDecimal allcost = (BigDecimal)datas2.get("allcost");
				BigDecimal hotelgive = (BigDecimal)datas2.get("hotelgive");
				BigDecimal otagive = (BigDecimal) datas2.get("realotagive");
				Integer rulecode = (Integer)map.get("rulecode");
				BigDecimal qiekeIncome = (BigDecimal) datas2.get("qiekeIncome");
				
				//判断1.0 2.0 切客条件 
//				System.out.println("orderid="+orderid+"; spreaduser="+spreaduser + "; invalidreason="+invalidreason + "; isPromotion="+isPromotion + "; rulecode="+rulecode);
				//B规则切客
				boolean bSpreadFlag = (spreaduser == 2 && rulecode == 1002 &&  invalidreason == 0);
				//A规则切客
				boolean aSpreadFlag = false;
				if(null != isPromotion){
					aSpreadFlag = (spreaduser == 2 && isPromotion == 4 && rulecode == 1001 && invalidreason == 0);
				}
				
				//allcost订单总金额
				map.put("allcost", datas2.get("allcost"));
				//usercost用户实际支付的钱 realCost为0 //TODO
				map.put("usercost", datas2.get("usercost"));
				//cutCost切客金额 A规则
				BigDecimal cutcost = new BigDecimal(0);
				if(aSpreadFlag){
					cutcost = new BigDecimal(10);
				} 
				map.put("cutcost", cutcost);
				//hotelGive 酒店优惠金额
				map.put("hotelgive", datas2.get("hotelgive"));
				//otherGive 其他优惠金额
				BigDecimal othergive = otagive.subtract(cutcost); //otagive未统计切客的钱
				if(othergive.compareTo(new BigDecimal(0)) == -1){
					othergive = new BigDecimal(0);
				}
				map.put("othergive", othergive);
				//prepaymentDiscount toPayDiscount
				BigDecimal prepaymentDiscount = null;
				BigDecimal toPayDiscount = null;
				if((int)map.get("ordertype") == 1){
					prepaymentDiscount = qiekeIncome;
				} else if((int)map.get("ordertype") == 2){
					toPayDiscount = qiekeIncome;
				}
				map.put("prepaymentDiscount", prepaymentDiscount);
				map.put("toPayDiscount", toPayDiscount);
				//serviceCost = （预付订单金额 - 酒店优惠金额） * 酒店佣金比例	createtime
				/*计算规则
				"    CASE\n" +
				"        WHEN isPromotion = 2 THEN (o.allcost - o.hotelgive) * 0.1\n" +
				"        ELSE 0\n" +
				"    END servicecost,\n" +*/
				BigDecimal servicecost = new BigDecimal(0);
				Date createTime = (Date) map.get("orderCreatetime");
				Date checkintime = (Date) map.get("checkintime");
				long temp = checkintime.getTime() - createTime.getTime(); // 相差毫秒数 > 15分钟，直单到付预付收取服务费
				if(spreaduser == 1L){//判断不是切客
					if(temp > BillOrderDAO.TIME_FOR_FIFTEEN){//判断下单时间大于15分钟的 //new BigDecimal(0) == qiekeIncome && 
						servicecost = allcost.subtract(hotelgive).multiply(new BigDecimal(0.1));
					} else{
						servicecost = new BigDecimal(0);
					}
				}
				map.put("servicecost", servicecost);
				
				//明细表中创建时间
				map.put("createtime", new Date());
				result.add(map);
				
				insertCount += result.size();
//				logger.info("genBillOrders,size{}",result.size());

				if(orderidId == null){
					namedParameterJdbcTemplate.batchUpdate(insertSql, result.toArray(new Map[0]));					
				}else{
					String updateSqlExit = "UPDATE b_bill_orders "
							+ "SET "
							+ "hotelId = :hotelid, "
							+ "orderType = :ordertype, "
							+ "roomTypeName = :roomtypename, "
							+ "roomNo = :roomno, "
							+ "beginTime = :begintime, "
							+ "endTime = :endtime, "
							+ "dayNumber = :daynumber, "
							+ "allCost = :allcost, "
							+ "userCost = :usercost, "
							+ "cutCost = :cutcost, "
							+ "hotelGive = :hotelgive, "
							+ "otherGive = :othergive,"
							+ "serviceCost = :servicecost,"
							+ "createTime = :createtime, "
							+ "isPromotion = :isPromotion, "
							+ "Invalidreason = :invalidreason, "
							+ "payStatus = :paystatus, "
							+ "spreadUser = :spreaduser, "
							+ "checkinTime = :checkintime, "
							+ "prepaymentDiscount = :prepaymentDiscount, "
							+ "toPayDiscount = :toPayDiscount "
							+ "WHERE "
							+ "orderId = " + orderidId;
					namedParameterJdbcTemplate.batchUpdate(updateSqlExit, result.toArray(new Map[0]));
					//删除生成的每天的一个酒店的表的数据
					paramMap.put("hotelid", hotelid);
					namedParameterJdbcTemplate.update("DELETE FROM b_bill_confirm_everyday WHERE hotelid = :hotelid AND begintime >= :startTime AND endtime < :endTime", paramMap);
				}
				result.clear();
				if(i % 100 == 0){
				    logger.info("已经执行了{}条",i);
				}
			}
		}
		logger.info("BillOrderDAO::genBillOrders,插入条数共计:{}",insertCount);
		
		saveJobHistory(HomeConst.BILL_ORDERS_JOB_HIS, DateUtils.getDatetime(), 1);
		logger.info("BillOrderDAO::genBillOrders.ok");
		
		logger.info("BillOrderDAO::genBillOrdersDay::start");
		//生成每个酒店每天的账单
		StringBuffer selectHotelBillSql = new StringBuffer();
		String selectHotelBillSql0 = "SELECT " +
				"    bo.hotelid hotelid, " +
				"    COUNT(bo.orderid) ordernum, " +
				"    SUM(CASE " +
				"        WHEN bo.ordertype = 2 THEN 1 " +
				"        ELSE 0 " +
				"    END) topaynum, " +
				"    SUM(CASE " +
				"        WHEN bo.ordertype = 2 THEN bo.allCost " +
				"        ELSE 0 " +
				"    END) topaymon, " +
				"    SUM(hotelGive) hoteldiscountcost, " +
				"    SUM(serviceCost) servicecost, " +
				"    SUM(otherGive) otherdiscountcost, " +
				"    SUM(CASE " +
				"        WHEN bo.cutCost > 0 THEN cutCost " +
				"        ELSE 0 " +
				"    END) cutoffcost, " +
				"    SUM(CASE " +
				"        WHEN bo.ordertype = 1 THEN 1 " +
				"        ELSE 0 " +
				"    END) prepaymentnum, " +
				"    SUM(CASE " +
				"        WHEN bo.ordertype = 1 THEN bo.usercost " +
				"        ELSE 0 " +
				"    END) prepaymentcost, " +
				"    SUM(CASE " +
				"        WHEN bo.Invalidreason IS NOT null THEN 1 " +
				"        ELSE 0 " +
				"    END) invaildcutofforders, " +
				"    SUM(prepaymentDiscount) prepaymentDiscount, " +
				"    SUM(toPayDiscount) toPayDiscount " +
				"FROM " +
				"    b_bill_orders bo " +
				"    WHERE bo.checkintime >= :startTime " +
				"    AND bo.checkintime < :endTime ";
		selectHotelBillSql.append(selectHotelBillSql0);
		if(null != hotelid){
			selectHotelBillSql.append(" AND bo.hotelid = " + hotelid);
		}else{
			selectHotelBillSql.append(" GROUP BY bo.hotelId");
		}
		
		String insertHotelBillSql = "INSERT INTO b_bill_confirm_everyday "
				+ "(hotelid, ordernum, topaynum, topaymon, hoteldiscountcost, servicecost, otherdiscountcost, cutofforders, cutoffcost, prepaymentnum, prepaymentcost, invaildcutofforders, createtime, begintime, endtime, prepaymentDiscount, toPayDiscount, billcost) VALUES "
				+ "(:hotelid, :ordernum, :topaynum, :topaymon, :hoteldiscountcost, :servicecost, :otherdiscountcost, :cutofforders, :cutoffcost, :prepaymentnum, :prepaymentcost, :invaildcutofforders, :createtime, :begintime, :endtime, :prepaymentDiscount, :toPayDiscount, :billcost)";

		final List<Map> resultDay = new ArrayList<Map>();
		List<Map<String,Object>> datasDay;
		int insertCountDay = 0;
		datasDay = namedParameterJdbcTemplate.queryForList(selectHotelBillSql.toString(), paramMap);
		for (int i = 0; i < datasDay.size(); i++) {
			Map<String, Object> mapEv = datasDay.get(i);
			// “总服务费”-“其他优惠金额”- "切客结算总费用"  -  "预付总金额"  -“调整金额”
			// servicecost - otherdiscountcost - cutoffcost -prepaymentcost
			BigDecimal servicecost = (BigDecimal)mapEv.get("servicecost");
			BigDecimal otherdiscountcost = (BigDecimal)mapEv.get("otherdiscountcost");
			BigDecimal cutoffcost = (BigDecimal)mapEv.get("cutoffcost");
			BigDecimal prepaymentcost = (BigDecimal)mapEv.get("prepaymentcost");
			BigDecimal billcost = servicecost.subtract(otherdiscountcost).subtract(cutoffcost).subtract(prepaymentcost);
			mapEv.put("billcost", billcost);
			//切客订单数cutofforders
			mapEv.put("cutofforders", cutoffcost.divide(new BigDecimal(10)));//切客钱/10,A规则
			
			mapEv.put("createtime", new Date());
			mapEv.put("begintime", startTime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(DateUtils.getDateFromString(endTime));
			calendar.add(Calendar.SECOND, -1);
			Date time = calendar.getTime();
			mapEv.put("endtime", DateUtils.getStringFromDate(time, DateUtils.FORMATDATETIME));
			
			resultDay.add(mapEv);
			
			insertCountDay += resultDay.size();
//			logger.info("genBillConfirmChecksEveryDay,size{}",resultDay.size());
			if(i % 50 == 0){
			    logger.info("BillOrderDAO::genBillOrdersDay,已经执行了{}个酒店",i);
			}
			
			String uHotelBillSql = "INSERT INTO b_bill_confirm_everyday "
					+ "(hotelid, ordernum, topaynum, topaymon, hoteldiscountcost, servicecost, otherdiscountcost, cutofforders, cutoffcost, prepaymentnum, prepaymentcost, invaildcutofforders, createtime, begintime, endtime, prepaymentDiscount, toPayDiscount, billcost) VALUES "
					+ "(:hotelid, :ordernum, :topaynum, :topaymon, :hoteldiscountcost, :servicecost, :otherdiscountcost, :cutofforders, :cutoffcost, :prepaymentnum, :prepaymentcost, :invaildcutofforders, :createtime, :begintime, :endtime, :prepaymentDiscount, :toPayDiscount, :billcost)";

			
			namedParameterJdbcTemplate.batchUpdate(insertHotelBillSql, resultDay.toArray(new Map[0]));
			resultDay.clear();
			
		}
		logger.info("BillOrderDAO::genBillOrdersDay,插入条数共计:{}",insertCountDay);
		logger.info("BillOrderDAO::genBillOrdersDay.ok");
		
	}
	
	/**
	 * 每月1号对账单数据每个酒店生成一个审核数据
	 *    默认调度 传时间参数 8月1号（下个月1号）
	 *    
	 * @param theMonth
	 */
	public void genBillConfirmChecks(Date begintime, String hotelid){//默认传过来下个月第一天 //如果是用户传过来则为一个用户日期+hotelid
		
		logger.info("BillOrderDAO::genBillConfirmChecks::执行月报表开始::hotelid:" + hotelid);
		
		//获取当前月份
		Date date = new Date();
		String nowMonth = DateUtils.getStringFromDate(date, DateUtils.FORMATDATETIME).subSequence(0, 6).toString();
		//获取begintime月份 如果相等 则 为用户传过来的时间 如果begintime > date 1个月 则为默认时间
		String theMonth = DateUtils.getStringFromDate(begintime, DateUtils.FORMATDATETIME).subSequence(0, 6).toString();
		String fstarttime = null;
		String fendtime = null;
		String sql0 = "SELECT "
				+ "id, "
				+ "hotelid hotelid, "
				+ "SUM(ordernum) ordernum, "
				+ "SUM(topaynum) topaynum, "
				+ "SUM(topaymon) topaymon, "
				+ "SUM(hoteldiscountcost) hoteldiscountcost, "
				+ "SUM(servicecost) servicecost, "
				+ "SUM(otherdiscountcost) otherdiscountcost, "
				+ "SUM(cutofforders) cutofforders, "
				+ "SUM(cutoffcost) cutoffcost, "
				+ "SUM(prepaymentnum) prepaymentnum, "
				+ "SUM(prepaymentcost) prepaymentcost, "
				+ "SUM(invaildcutofforders) invaildcutofforders, "
				+ "begintime, "
				+ "endtime, "
				+ "SUM(prepaymentDiscount) prepaymentdiscount, "
				+ "SUM(toPayDiscount) topaydiscount, "
				+ "SUM(billcost) billcost "
				+ " FROM b_bill_confirm_everyday"
				+ " WHERE"
				+ " begintime >= :fstarttime " //开始时间>=20150701
				+ " AND endtime < :fendtime "; //结束时间<20150801
		Map<String, Object> paramMap = new HashMap<String, Object>();
		//查询账期表中是否存在这个月的记录
		String sqlPriod = "SELECT "
				+ "hotelId hotelid, beginTime begintime, endTime endtime, theMonth themonth "
				+ "FROM "
				+ "b_bill_period "
				+ "WHERE "
				+ "theMonth = :theMonth "
				+ "AND hotelid = :hotelid "
				+ "ORDER BY id DESC";
		final String insertSql = "INSERT INTO b_bill_confirm_check "
				+ "(invaildcutofforders, hotelid, billtime, begintime, endtime, ordernum, prepaymentnum, prepaymentcost, topaynum, topaymon, cutofforders, cutoffcost, hoteldiscountcost, otherdiscountcost, servicecost, createtime, changecost, billcost, checkstatus, prepaymentdiscount, topaydiscount) "
				+ "values "
				+ "(:invaildcutofforders, :hotelid, :billtime, :begintime, :endtime, :ordernum, :prepaymentnum, :prepaymentcost, :topaynum, :topaymon, :cutofforders, :cutoffcost, :hoteldiscountcost, :otherdiscountcost, :servicecost, :createtime, :changecost, :billcost, :checkstatus, :prepaymentdiscount, :topaydiscount)"; 
		
		if(null != hotelid){//执行一个酒店的
			if(nowMonth.equals(theMonth)){//证明是当月的 值 不是默认的下个月
				List<Map<String,Object>> datasPriod;
				Map<String, Object> paramMapPriod = new HashMap<String, Object>();
				paramMapPriod.put("theMonth", theMonth);
				paramMapPriod.put("hotelid", hotelid);
				//查询账期表中是否存在这个月的记录
				datasPriod = namedParameterJdbcTemplate.queryForList(sqlPriod, paramMapPriod);
				if(datasPriod.size() == 0){
					//日期从1号到 begintime - 1天  == 并且插入到账期表中
					fstarttime = DateUtils.getMonthFirstDay(DateUtils.getDatetime(begintime));
					fendtime = DateUtils.getDateAdded(0, DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE));
				} else {//得到账期表中本月的最后一条记录的一天 endtime 从 >=endtime+1 到传进来的begintime-1的一天
					Map<String, Object> mapPriod = datasPriod.get(0);
					Date endtime = (Date)mapPriod.get("endtime");
					fstarttime = DateUtils.getDateAdded(1, DateUtils.getStringFromDate(endtime, DateUtils.FORMAT_DATE));
					fendtime = DateUtils.getDateAdded(0, DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE));
				}
				if(fstarttime.compareTo(fendtime) < 0){
					paramMap.put("fstarttime", fstarttime);
					paramMap.put("fendtime", fendtime);
				}
			}else{
				//获取begintime的月份
				//如果是下个月日期 则获取前一天并且获取前一个月份
				String time0 = DateUtils.getDateAdded(-1, DateUtils.getStringFromDate(begintime, DateUtils.FORMATDATETIME));
				theMonth = time0.split("-")[0]+time0.split("-")[1];
				List<Map<String,Object>> datasPriod;
				Map<String, Object> paramMapPriod = new HashMap<String, Object>();
				paramMapPriod.put("theMonth", theMonth);
				paramMapPriod.put("hotelid", hotelid);
				//查询账期表中是否存在这个月的记录
				datasPriod = namedParameterJdbcTemplate.queryForList(sqlPriod, paramMapPriod);
				if(datasPriod.size() == 0){
					fstarttime = DateUtils.getMonthFirstDay(DateUtils.getDateAdded(-1, DateUtils.getDate()));
//					fendtime = DateUtils.getMonthLastDay(DateUtils.getDateAdded(0, DateUtils.getDate()));
					fendtime = DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE);
				} else {
					Map<String, Object> mapPriod = datasPriod.get(0);
					Date endtime = (Date)mapPriod.get("endtime");
					fstarttime = DateUtils.getDateAdded(1, DateUtils.getStringFromDate(endtime, DateUtils.FORMAT_DATE));
					fendtime = DateUtils.getDateAdded(0, DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE));
				}
				paramMap.put("fstarttime", fstarttime);
				paramMap.put("fendtime", fendtime);
			}
			
			if(fstarttime.compareTo(fendtime) < 0){
				/**
				 * ordernum 总订单数, topaynum 到付订单数, topaymon 到付总金额, prepaymentnum 预付订单数, prepaymentcost 预付总金额, 
				 * cutofforders 切客订单数, cutoffcost 切客结算总费用, hoteldiscountcost 商家优惠金额, otherdiscountcost 其他优惠金额, servicecost 总服务费
				 */
				StringBuffer sql = new StringBuffer();
				sql.append(sql0);
				sql.append(" AND hotelid = " + hotelid);
				
				final List<Map> result = new ArrayList<Map>();
				List<Map<String,Object>> datas;
				datas = namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap);
				for (int j = 0; j < datas.size(); j++) {
					Map<String, Object> map = datas.get(j);
					
					BigDecimal ordernum = (BigDecimal)map.get("ordernum");
					if(ordernum != null){					
						map.put("billtime", theMonth);
						map.put("begintime", fstarttime);
						Calendar calendar2 = Calendar.getInstance();
						calendar2.setTime(DateUtils.getDateFromString(fendtime));
						calendar2.add(Calendar.SECOND, -1);
						Date time2 = calendar2.getTime();
						map.put("endtime", DateUtils.getStringFromDate(time2, DateUtils.FORMATDATETIME));
						map.put("createtime", new Date());
						map.put("changecost", BigDecimal.ZERO);
						map.put("checkstatus", 0);
						
						result.add(map);
						// 添加、更新到缓存
						logger.info("BillOrderDAO::genBillConfirmChecks,size{}",result.size());
						namedParameterJdbcTemplate.batchUpdate(insertSql, result.toArray(new Map[0]));
						result.clear();
						
						//将每个酒店的账期插入到账期表中
						String insertSqlPeiod = "INSERT INTO b_bill_period "
								+ "(hotelId, beginTime, endTime, theMonth ) "
								+ "VALUES "
								+ "(:hotelid, :begintime, :endtime, :themonth )";
						final List<Map> resultInsertPriod = new ArrayList<Map>();
						Map<String, Object> paramMapInsertPriod = new HashMap<String, Object>();
						paramMapInsertPriod.put("hotelid", hotelid);
						paramMapInsertPriod.put("themonth", theMonth);
						paramMapInsertPriod.put("begintime", fstarttime);
	
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(DateUtils.getDateFromString(fendtime));
						calendar.add(Calendar.SECOND, -1);
						Date time = calendar.getTime();
	//					map.put("endtime", DateUtils.getStringFromDate(time2, DateUtils.FORMATDATETIME));
	//					Calendar calendar = Calendar.getInstance();
	//					calendar.setTime(DateUtils.getDateFromString(DateUtils.getDateAdded(-1, DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE))));
	//					calendar.add(Calendar.SECOND, -1);
	//					Date time = calendar.getTime();
						paramMapInsertPriod.put("endtime", DateUtils.getStringFromDate(time, DateUtils.FORMATDATETIME));
						resultInsertPriod.add(paramMapInsertPriod);
						namedParameterJdbcTemplate.batchUpdate(insertSqlPeiod, resultInsertPriod.toArray(new Map[0]));
						
						//更新明细表 天表中 pId字段
						String sqlPid = "UPDATE b_bill_orders t SET t.pId = ( "
									+ "SELECT id FROM b_bill_confirm_check c "
									+ "WHERE c.hotelid = t.hotelid AND c.billtime = :themonth ORDER BY c.id DESC LIMIT 1 "
								+ ") "
								+ "WHERE t.checkintime >= :begintime AND t.checkintime <= :endtime AND t.hotelid = :hotelid AND t.id > 0 ";
	//					Map<String, Object> paramMapPid = new HashMap<String, Object>();
	//					paramMapPid.put("theMonth", theMonth);
	//					paramMapPid.put("hotelid", hotelidAll);
	//					paramMapPid.put("paramMapPid", fstarttime);
	//					paramMapPid.put("fendtime", fendtime);
						namedParameterJdbcTemplate.update(sqlPid, paramMapInsertPriod);
						String sqlPidEv = "UPDATE b_bill_confirm_everyday t SET t.pId = ( "
								+ "SELECT id FROM b_bill_confirm_check c "
								+ "WHERE c.hotelid = t.hotelid AND c.billtime = :themonth ORDER BY c.id DESC LIMIT 1 "
							+ ") "
							+ "WHERE t.begintime >= :begintime AND t.endtime <= :endtime AND t.hotelid = :hotelid AND t.id > 0 ";
						namedParameterJdbcTemplate.update(sqlPidEv, paramMapInsertPriod);
					}
				}
			}
			
		} else {//执行所有酒店的
			//查询出所有酒店 循环 查询每个酒店的账期 执行查询 插入表中 并且写入每个酒店的账期表 更新 明细表和天表 中的PID字段
			//从每天表中查询所有酒店 1号-31号的
			fstarttime = DateUtils.getMonthFirstDay(DateUtils.getDateAdded(-1, DateUtils.getStringFromDate(begintime, DateUtils.FORMATDATETIME)));
			fendtime = DateUtils.getDateAdded(0, DateUtils.getStringFromDate(begintime, DateUtils.FORMAT_DATE));
			theMonth = fstarttime.split("-")[0]+fstarttime.split("-")[1];
			List<Map<String,Object>> datasAllHotel;
			Map<String, Object> paramMapAllHotel = new HashMap<String, Object>();
			paramMapAllHotel.put("fstarttime", fstarttime);
			paramMapAllHotel.put("fendtime", fendtime);
			String sqlAllHotel = "SELECT "
					+ "DISTINCT hotelId hotelid "
					+ "FROM "
					+ "b_bill_confirm_everyday "
					+ "WHERE "
					+ "begintime >= :fstarttime "
					+ "AND endtime < :fendtime";
			datasAllHotel = namedParameterJdbcTemplate.queryForList(sqlAllHotel, paramMapAllHotel);
			//本月一共多少个酒店 日志
			logger.info("BillOrderDAO::genBillConfirmChecks,本月一共有  {} 个酒店需要结算",datasAllHotel.size());

			for (int i = 0; i < datasAllHotel.size(); i++) {
				Map<String, Object> mapHotel = datasAllHotel.get(i);
				Long hotelidAll = (Long)mapHotel.get("hotelid");
				
				List<Map<String,Object>> datasPriod;
				Map<String, Object> paramMapPriod = new HashMap<String, Object>();
				paramMapPriod.put("theMonth", theMonth);
				paramMapPriod.put("hotelid", hotelidAll);
				datasPriod = namedParameterJdbcTemplate.queryForList(sqlPriod, paramMapPriod);
				if(datasPriod.size() > 0){
					Map<String, Object> mapPriod = datasPriod.get(0);
					Date endtime = (Date)mapPriod.get("endtime");
					fstarttime = DateUtils.getDateAdded(1, DateUtils.getStringFromDate(endtime, DateUtils.FORMAT_DATE));
					paramMap.put("fstarttime", fstarttime);
					paramMap.put("fendtime", fendtime);
				} else {
					fstarttime = DateUtils.getMonthFirstDay(theMonth);
					paramMap.put("fstarttime", fstarttime);
					paramMap.put("fendtime", fendtime);
				}
				
				if(fstarttime != null && fstarttime.compareTo(fendtime) > 0){
					logger.info("BillOrderDAO::genBillConfirmChecks:: 该酒店已经结算, hotelid:{}", hotelidAll);
					fstarttime = DateUtils.getMonthFirstDay(DateUtils.getDateAdded(-1, DateUtils.getStringFromDate(begintime, DateUtils.FORMATDATETIME)));
					continue;
				} else {
					/**
					 * ordernum 总订单数, topaynum 到付订单数, topaymon 到付总金额, prepaymentnum 预付订单数, prepaymentcost 预付总金额, 
					 * cutofforders 切客订单数, cutoffcost 切客结算总费用, hoteldiscountcost 商家优惠金额, otherdiscountcost 其他优惠金额, servicecost 总服务费
					 */
					StringBuffer sql = new StringBuffer();
					sql.append(sql0);
					sql.append(" AND hotelid = " + hotelidAll);
					
					final List<Map> result = new ArrayList<Map>();
					List<Map<String,Object>> datas;
					datas = namedParameterJdbcTemplate.queryForList(sql.toString(), paramMap);
					for (int j = 0; j < datas.size(); j++) {
						Map<String, Object> map = datas.get(j);
						
						BigDecimal ordernum = (BigDecimal)map.get("ordernum");
						if(ordernum != null){
							map.put("billtime", theMonth);
							map.put("begintime", fstarttime);
							Calendar calendar2 = Calendar.getInstance();
							calendar2.setTime(DateUtils.getDateFromString(fendtime));
							calendar2.add(Calendar.SECOND, -1);
							Date time2 = calendar2.getTime();
							map.put("endtime", DateUtils.getStringFromDate(time2, DateUtils.FORMATDATETIME));
							map.put("createtime", new Date());
							map.put("changecost", BigDecimal.ZERO);
							map.put("checkstatus", 0);
							
							result.add(map);
							// 添加、更新到缓存
							logger.info("BillOrderDAO::genBillConfirmChecks,size{}",result.size());
							namedParameterJdbcTemplate.batchUpdate(insertSql, result.toArray(new Map[0]));
							result.clear();
							
							logger.info("BillOrderDAO::genBillConfirmChecks,hotelid:{},酒店结算完成。开始日期:{},结束日期:{}",hotelidAll,fstarttime,fendtime);
							
							//将每个酒店的账期插入到账期表中
							String insertSqlPeiod = "INSERT INTO b_bill_period "
									+ "(hotelId, beginTime, endTime, theMonth ) "
									+ "VALUES "
									+ "(:hotelid, :begintime, :endtime, :themonth )";
							final List<Map> resultInsertPriod = new ArrayList<Map>();
							Map<String, Object> paramMapInsertPriod = new HashMap<String, Object>();
							paramMapInsertPriod.put("hotelid", hotelidAll);
							paramMapInsertPriod.put("themonth", theMonth);
							paramMapInsertPriod.put("begintime", fstarttime);
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(DateUtils.getDateFromString(fendtime));
							calendar.add(Calendar.SECOND, -1);
							Date time = calendar.getTime();
							paramMapInsertPriod.put("endtime", DateUtils.getStringFromDate(time, DateUtils.FORMATDATETIME));
							paramMapInsertPriod.put("endtime", time);
							resultInsertPriod.add(paramMapInsertPriod);
							namedParameterJdbcTemplate.batchUpdate(insertSqlPeiod, resultInsertPriod.toArray(new Map[0]));
							
							//更新明细表 天表中 pId字段
							String sqlPid = "UPDATE b_bill_orders t SET t.pId = ( "
										+ "SELECT id FROM b_bill_confirm_check c "
										+ "WHERE c.hotelid = t.hotelid AND c.billtime = :themonth ORDER BY c.id DESC LIMIT 1 "
									+ ") "
									+ "WHERE t.checkintime >= :begintime AND t.checkintime <= :endtime AND t.hotelid = :hotelid AND t.id > 0 ";
	//						Map<String, Object> paramMapPid = new HashMap<String, Object>();
	//						paramMapPid.put("theMonth", theMonth);
	//						paramMapPid.put("hotelid", hotelidAll);
	//						paramMapPid.put("paramMapPid", fstarttime);
	//						paramMapPid.put("fendtime", fendtime);
							namedParameterJdbcTemplate.update(sqlPid, paramMapInsertPriod);
							String sqlPidEv = "UPDATE b_bill_confirm_everyday t SET t.pId = ( "
									+ "SELECT id FROM b_bill_confirm_check c "
									+ "WHERE c.hotelid = t.hotelid AND c.billtime = :themonth ORDER BY c.id DESC LIMIT 1 "
								+ ") "
								+ "WHERE t.begintime >= :begintime AND t.endtime <= :endtime AND t.hotelid = :hotelid AND t.id > 0 ";
							namedParameterJdbcTemplate.update(sqlPidEv, paramMapInsertPriod);
						}
					}
				}
			}
		}
		saveJobHistory(HomeConst.BILL_CONFIRM_JOB_HIS, DateUtils.getDatetime(), 1);
		logger.info("BillOrderDAO::genBillConfirmChecks.ok");
		
	}
	
	public void saveJobHistory(String job, String runTime, int status){
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("job", job);
		paramMap.put("last_run_time", runTime);
		paramMap.put("status", status);
		int r = namedParameterJdbcTemplate.update("update t_job_history set last_run_time = :last_run_time, status = :status where job = :job", paramMap);
		if (r == 0) {
			namedParameterJdbcTemplate.update("insert into t_job_history (job, last_run_time, status) values (:job, :last_run_time, :status)", paramMap);
		}
	}
	
	public Map getJobHistory(String job){
		try {
			Map his = namedParameterJdbcTemplate.queryForMap("select * from t_job_history where job = :job", ImmutableMap.of("job", job));
			return his;
		} catch (Exception e) {
			return null;
		}
	}
	
}
