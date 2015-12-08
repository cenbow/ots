package com.mk.ots.score.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Repository;

import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.Db;
import com.mk.orm.plugin.bean.IAtom;
/**
 * 评分
 *
 * @author LYN
 *
 */
@Repository
public class ScoreDAO {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(ScoreDAO.class);

	/**
	 * 插入评价信息,
	 *
	 * @param score
	 * @return
	 */
	public boolean insert(final List cList, final List sList,final String orderid) {
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String cSql = "insert into t_hotel_score (roomid,roomtypeid,hotelid,score,pics,grade,isdefault,createtime,orderid,mid) values(?,?,?,?,?,?,?,NOW(),?,?)";
				int count = Db.update(cSql, cList.toArray());
				int cc = 0;
				int size = sList.size();
				for (int i = 0; i < size; i++) {
					List ss = (List) sList.get(i);
					String sql = "insert into t_hotel_subject_mx (roomid,roomtypeid,hotelid,grade,createtime,subjectid,orderid)values(?,?,?,?,now(),?,?)";
					int c = Db.update(sql, ss.toArray());
					cc += c;
				}
				//return count == 1 && cc == size;
				return true;
			}
		});
		return succeed;
	}

	public   boolean  deleteScoreMarkMember(final Long mid,final String orderId){
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String cSql = "delete from t_hotel_score where orderid=?  and  mid = ?";
				int c = Db.update(cSql, orderId,mid);
				return true;
			}
		});
		return  succeed;
	}



		public   boolean  insertScoreMarkMember(final List cList,final String scoreMarkIdS){
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String[] markId = scoreMarkIdS.split(",");
				for(int i=0;i<markId.length;i++){
					String cSql = "insert into t_hotel_score_mark_member (mid,hotel_id,room_id,order_id,create_time,mark_id) values(?,?,?,?,NOW()," + markId[i] + ")";
					int count = Db.update(cSql, cList.toArray());
				}
				return true;
			}
		});
		return succeed;
	}

	/**
	 * 插入评价信息,
	 *
	 * @param score
	 * @return
	 */
	public boolean update(final List cList, final List sList,final String orderid) {
		final boolean isExists = isExistsGrade(orderid);
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String cSql = "update t_hotel_score set roomid=? , roomtypeid=? , hotelid=? , score=? ,pics=?,grade=?, isdefault=?, createtime=now() where orderid=?";
				int count = Db.update(cSql, cList.toArray());
				int cc = 0;
				int size = sList.size();
				String sql = "update t_hotel_subject_mx set roomid=?, roomtypeid=?, hotelid=?,grade=?,createtime=now() where subjectid=? and orderid=?";
				if (!isExists) {
					sql = "insert into t_hotel_subject_mx (roomid,roomtypeid,hotelid,grade,createtime,subjectid,orderid)values(?,?,?,?,now(),?,?)";
				}
				for (int i = 0; i < size; i++) {
					List ss = (List) sList.get(i);
					int c = Db.update(sql, ss.toArray());
					cc += c;
				}
				//return count == 1 && cc == size;
				return true;
			}
		});
		return true;
	}

	public boolean isExistsGrade(String orderid){
		String sql="select count(*) from t_hotel_subject_mx where orderid=?";
		long count=Db.queryLong(sql, orderid);
		return count>0?true:false;
	}
	/**
	 * 删除 评价信息
	 * @param orderid
	 * @return
	 */
	public boolean del(final String orderid) {
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String cSql = "delete from t_hotel_score where orderid=?";
				int c = Db.update(cSql, orderid);
				String sql = "delete from t_hotel_subject_mx where orderid=?";
				int ccc = Db.update(sql, orderid);
				return true;
			}
		});
		return succeed;
	}
	/**
	 * 查询所有评分项
	 *
	 * @return
	 */
	public List<Bean> findSubject(String subjectid) {
		String sql = "select * from t_subject";
		List<Bean> list = new ArrayList();
		if(StringUtils.isBlank(subjectid)){
			list = Db.find(sql);
		}else{
			sql+=" where subjectid=?";
			list = Db.find(sql,subjectid);
		}
		return list;
	}


	/**
	 * 查询所有评价标签
	 * @return
	 */
	public List<Bean> findScoreMark() {
		String sql = "select * from t_hotel_score_mark where  isdelete = 'F' order  by  ord";
		List<Bean> list = new ArrayList();
		list = Db.find(sql);
		return list;
	}

	/**
	 * 查询酒店总评分
	 * @param hotelid
	 * @return
	 */
	public Bean findScoreSByHotelid(String hotelid){
		Long mid=null;
		return this.findScoreSByHotelid(hotelid,  mid);
	}

	/**
	 * 查询酒店总评分
	 * @param hotelid
	 * @return
	 */
	public Bean findScoreSByHotelid(String hotelid, Long mid){
		Bean scoreS = null;
		try {
			StringBuffer sql= new StringBuffer();
			List<Object> params = new ArrayList<Object>();
			params.add(hotelid);
			sql.append("select a.hotelid,count(*) as scorecount, b.s as grade from  t_hotel_score a left join t_hotel_subject b on a.hotelid=b.hotelid where a.hotelid=? and a.type=1 ");
			//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
			sql.append(" and ((a.status=4 or a.status=7) or a.mid=?)");
			params.add(mid);
			//20150923 产品讨论，只展示状态为4的记录
			//sql.append(" and a.status=4 ");//4:待回复
			//4:待回复，5:已经回复
			//sql.append(" and ( a.status=4 or a.status=5 ) ");

			logger.info("findScoreSByHotelid method sql is：\n {}", sql.toString());
			logger.info("sql parameter hotelid value is: {} ", params.toString());
			scoreS = Db.findFirst(sql.toString(), params.toArray());
			if (scoreS != null) {
				logger.info("酒店评分数据信息：\n {}", scoreS.toJson());
			} else {
				logger.info("酒店hotelid: {} 没有评分数据.", hotelid);
			}
		} catch (Exception e) {
			logger.error("查询酒店评分信息出错：");
			logger.error("findScoreSByHoetlid method is error:\n {}", e.getMessage());
		}
		return scoreS;
	}
	/**
	 * 查询酒店房型评分
	 * @param hotelid
	 * @param roomtpyeid
	 * @return
	 */
	public Bean findScoreByHotelidRoomtypeid(String hotelid,String roomtypeid, Long mid){
		List<Object> params= new ArrayList<Object>();
		params.add(hotelid);
		params.add(roomtypeid);
		StringBuffer sql = new StringBuffer();
		sql.append(" select hotelid,roomtypeid,sum(grade)/count(*) as grades,count(*) as itemc from t_hotel_score where hotelid=? and roomtypeid=? and type=1 ");
		//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
		sql.append(" and ((status=4 or status=7) or mid=?)");
		params.add(mid);
		//20150923 产品讨论，只展示状态为4的记录
		//sql.append(" and status=4 ");//4:待回复
		//2015-08-21修改为只显示已审核，已经回复的评价
		//sql.append(" and ( status=4 or status=5 )");//4:待回复，5:已经回复

		sql.append(" group by hotelid,roomtypeid");
		Bean score= Db.findFirst(sql.toString(), params.toArray());
		return score;
	}
	/**
	 * 查询酒店单项请分
	 * @param hotelid
	 * @return
	 */
	public List<Bean> findScoreRByHotelid(String hotelid){
		String sql="select th.subjectid,s.subjectname,th.pr from "
				+ "t_hotel_subject_his th "
				+ "left join t_subject s on th.subjectid=s.subjectid  "
				+ "where hotelid=? and createtime=(select max(createtime ) from t_hotel_subject_his) ";
		List<Bean> list= Db.find(sql,hotelid);
		return list;
	}
	/**
	 * 全部酒店的各评分项的平均值计算公式为：单项评分总数/单项评分投票数。
	 * 例如：所有卫生的评分分值总和除以卫生类评分的投票次数，结果就是卫生类的平均分。
	 * 该分数用于在跑批计算中，作为c数值计算。
	 * 数据保存在OTA单项评分历史表中，具体包含以下字段：ID、评分项ID、项目总平均分、创建日期、创建时间。
	 * @return
	 */
	public int scoreAverage() {
//		String sql = "insert into t_subject_c_his(subjectid,c,createdate) "
//				+ "select subjectid,sum(grade)/count(*) as av,now() from "
//				+ "t_hotel_subject_mx group by subjectid";
		String sql = "insert into t_subject_c_his(subjectid,c,createdate) "
				+ "select a.subjectid,sum(a.grade)/count(*) as av,now() from t_hotel_score b "
				+ " inner join  t_hotel_subject_mx a  on a.orderid=b.orderid "
				//20150923 产品讨论，只展示状态为4的记录
				//+ " where b.status=4  and b.type=1 "

				//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
				+ " where (b.status=4 or b.status=7) and b.type=1 "
				+	" group by a.subjectid ";
		int count = Db.update(sql);
		logger.info("评分-->计算平均分:sql{}", sql);
		return count;
	}

	/**
	 * 酒店评分统计历史表
	 * R：项目计算分值，分数计算公式为：R = n/(n+m) ×r + m/(n+m) ×c
	 * @return
	 */
	public int scoreR() {
		String sql = "insert into t_hotel_subject_his (hotelid,subjectid,r,n,m,cid,pr,weightfunction,createtime) "
				+ "select hotelid,subjectid,r,n,m,cid,n/(n+m)*r+m/(n+m)*c as pr,weightfunction,now() from "
				+ "( select s.subjectid,s.subjectname,s.mno as m,s.weightfunction,a.hotelid,h.id as cid,h.c,a.av as r,a.citem as n from t_subject s "
				+ "left join (select * from t_subject_c_his where createdate=(select max(createdate) from t_subject_c_his)) h on s.subjectid= h.subjectid "
				+ "left join (select hotelid,subjectid,sum(grade)/count(id) as av,count(id) as citem from t_hotel_subject_mx "
				+ " where  orderid in ( select b.orderid from t_hotel_score b where "
				//20150923 产品讨论，只展示状态为4的记录
				//+ " b.status=4 and type=1 ) "
				//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
				+ " (b.status=4 or b.status=7) and type=1 ) "
				+ " group by hotelid,subjectid) a on s.subjectid=a.subjectid) pr "
				+ "order by hotelid,subjectid";
		int count =Db.update(sql);
		logger.info("评分-->计算评分统计历史表:sql{}", sql);
		return count;
	}

	/**
	 * 计算总分
	 * 1.清空表
	 * 2.S=(R1*a+R2*b+R3*c)/a+b+c
	 * @return
	 */
	public boolean scoreS() {
		boolean succeed = Db.tx(new IAtom() {
			public boolean run() throws SQLException {
				String delSql="delete from t_hotel_subject";
				int count = Db.update(delSql);
				logger.info("评分-->del:sql{}:",delSql);
				String inSql="insert into t_hotel_subject (hotelid,s) select hotelid,sum(pr*weightfunction)/sum(weightfunction) as s from t_hotel_subject_his where createtime=(select max(createtime) from t_hotel_subject_his) group by hotelid";
				int inCount= Db.update(inSql);
				logger.info("评分-->insert:sql{}:",inSql);
				return inCount==0?false:true;
			}
		});
		return succeed;
	}

	/**
	 * 查询房型单项分数
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 */
	public List<Bean> findScoreSubjectByHotelidRoomtypeid(String hotelid,
														  String roomtypeid) {
		String sql=" select a.subjectid,sum(grade)/count(*) as grades from  t_hotel_subject_mx a where a.hotelid=? and a.roomtypeid=? group by a.hotelid,a.roomtypeid,a.subjectid";
		List<Bean> list= Db.find(sql,hotelid,roomtypeid);
		return list;
	}

	/**
	 * 查询房间评价
	 * @param roomtypeid
	 * @param roomid
	 * @return
	 */
	public Bean findScoreByRoomid(String roomid) {
		String sql="select roomtypeid,roomid,sum(grade)/count(*) as grades,count(*) as itemc from "
				+ "t_hotel_subject_mx where  roomid=? group by roomtypeid,roomid";
		return Db.findFirst(sql,roomid);
	}

	/**
	 * 查询房间评价
	 * @param roomtypeid
	 * @param roomid
	 * @return
	 */
	public List<Bean> findScoreSubjectByRoomid(	String roomid) {
		String sql="select subjectid, sum(grade) as grades from    t_hotel_subject_mx a    where  a.roomid=?   group by a.roomtypeid,a.subjectid,a.roomid";
		return Db.find(sql,roomid);
	}

	/**
	 * 查询明细(优化去掉left join)
	 * @param maxgrade
	 * @param mingrade
	 * @param subjectid
	 * @param orderby
	 * @param startdateday
	 * @param enddateday
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public List<Bean> findScoreMx2(String hotelid,String roomtypeid,
								   String roomid,String maxgrade,String mingrade,
								   String subjectid,String orderby,String startdateday,
								   String enddateday,String starttime,String endtime,int page,int limit, int type, Long mid, String gradetype) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select a.id,a.hotelid,a.roomtypeid,a.roomid,a.score,a.createtime,a.pics,a.orderid,a.grade,a.hotelscore,a.hotelscoretime,a.servicescore,a.servicescoretime,a.isreply,a.isaduit,a.isvisible,a.status,a.type,a.mid ");
		sqlBuffer.append(" from t_hotel_score a where ");

		List<Object> paramsList= new ArrayList<Object>();
		sqlBuffer.append(" isvisible='T' ");
		sqlBuffer.append(" and type=? ");
		paramsList.add(type);

		String statusWhere ="";
		switch( type){
			case 1:
				//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
				sqlBuffer.append(" and ((a.status=4 or a.status=7) or a.mid=?)");
				paramsList.add(mid);
				//20150923 产品讨论，只展示状态为4的记录
				//sqlBuffer.append(" and a.status=4 ");//4:待回复
				//2015-08-21修改为只显示已审核，已经回复的评价
				//statusWhere = " and ( a.status=4 or a.status=5 )";//4:待回复，5:已经回复
				break;
			case 2:
				statusWhere = " and a.status=6 ";
				break;
		}
		sqlBuffer.append( statusWhere );

		if(StringUtils.isNotBlank(roomid)){
			sqlBuffer.append( " and a.roomid=? ");
			paramsList.add(roomid);
		}else if( StringUtils.isNotBlank(roomtypeid)){
			sqlBuffer.append( " and a.roomtypeid=? ");
			paramsList.add(roomtypeid);
		}else{
			sqlBuffer.append( " and a.hotelid=? ");
			paramsList.add(hotelid);
		}
		//眯客3.0
		//好：G，中：M,  差：B， 全部：A
		//B:<2分，M:>=2且<3，G:>=3
		if(StringUtils.isNotBlank(gradetype)){
			this.settingGradeType(gradetype, sqlBuffer, paramsList);
		}else{
			if(StringUtils.isNotBlank(maxgrade)){
				sqlBuffer.append( " and a.grade<=? ");
				paramsList.add(maxgrade);
			}
			if(StringUtils.isNotBlank(mingrade)){
				sqlBuffer.append( " and a.grade>=?");
				paramsList.add(mingrade);
			}
		}
		if(StringUtils.isNotBlank(startdateday)){
			sqlBuffer.append( " and a.createtime >=? ");
			paramsList.add(startdateday);
		}
		if(StringUtils.isNotBlank(enddateday)){
			sqlBuffer.append( " and a.createtime<=? ");
			paramsList.add(enddateday);
		}
//		sqlBuffer.append(" and a.isaduit=2 or( (a.isaduit=1 or a.isaduit=3)  and o.mid=?) and isvisible='T' ");

		sqlBuffer.append(" order by a.createtime desc ");
		sqlBuffer.append( " limit " + (page - 1) * limit + "," + limit);
		logger.info("查询评价详细信息sql{}, params:{}:", sqlBuffer.toString(), paramsList.toString());
		List<Bean> list=Db.find(sqlBuffer.toString(),paramsList.toArray());
		return list;
	}

	public List<Bean> findSubjectByOrderid(String orderid){
		String sql="select a.hotelid,a.subjectid,b.subjectname,a.grade from t_hotel_subject_mx a  left join t_subject b on a.subjectid= b.subjectid where a.orderid=?";
		List<Bean> list= Db.find(sql,orderid);
		return list;

	}

	/**
	 * 查询分数范围
	 * @param hotelid
	 * @param group 1-2
	 * @param startdate
	 * @param enddate
	 * @return
	 */
	public List<Bean> findScoreGroup(String hotelid, String scoregroups, String startdate,
									 String enddate, Long mid) {
		String[] groups= scoregroups.split(",");
		StringBuffer sqlBuffer = new StringBuffer();
		List params = new ArrayList();
		for(int i=0;i<groups.length;i++){
			String group= groups[i];
			String[] grades= group.split("-");
			Double min= Double.parseDouble(grades[0]);
			Double max= Double.parseDouble(grades[1]);

			params.add(hotelid);
			params.add(min);
			params.add(max);
			if(i>0){
				sqlBuffer.append(" union ");
			}
			sqlBuffer.append("select '");
			sqlBuffer.append(group);
			sqlBuffer.append("' as scoregroup,count(*) as scorecount from t_hotel_score where  hotelid=? and grade>=? and grade<=? and type=1 ");
			if(StringUtils.isNotBlank(startdate)){
				sqlBuffer.append(" and createtime>= ? ");
				params.add(startdate);
			}
			if( StringUtils.isNotBlank(enddate)){
				sqlBuffer.append(" and createtime<= ? ");
				params.add(enddate);
			}
			//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
			sqlBuffer.append(" and ((a.status=4 or a.status=7) or a.mid=?)");
			params.add(mid);
			//20150923 产品讨论，只展示状态为4的记录
			//sqlBuffer.append(" and status=4 ");//4:待回复
			/*
			if(mid != null){
				//2015-08-21修改为只显示已审核，已经回复的评价
				//sqlBuffer.append(" and ( status=4 or status=5 )");//4:待回复，5:已经回复
				//sqlBuffer.append(" and (( status=4 or status=5 ) or mid=? )");//4:待回复，5:已经回复
				//params.add(mid);
			}else{
				sqlBuffer.append(" and ( status=4 or status=5 )");//4:待回复，5:已经回复
			}*/
		}
		return  Db.find(sqlBuffer.toString(),params.toArray());
	}

	public long findScoreMxCount(String hotelid, String roomtypeid,
								 String roomid, String maxgrade, String mingrade, String subjectid,
								 String orderby, String startdateday, String enddateday,
								 String starttime, String endtime, Long mid, String gradetype) {
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select count(*)   from t_hotel_score a  where ");
		//sqlBuffer.append( " left join t_hotel_subject s on a.hotelid=s.hotelid where");
		List paramsList= new ArrayList();

		//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
		sqlBuffer.append(" type=1 and ((a.status=4 or a.status=7) or a.mid=?)");
		paramsList.add(mid);
		//20150923 产品讨论，只展示状态为4的记录
		//sqlBuffer.append(" type=1 and a.status=4 ");//4:待回复
		//2015-08-21修改为只显示已审核，已经回复的评价
		//sqlBuffer.append(" type=1 and (a.status=4 or a.status=5)");

		if(StringUtils.isNotBlank(roomid)){
			sqlBuffer.append( " and a.roomid=? ");
			paramsList.add(roomid);
		}else if( StringUtils.isNotBlank(roomtypeid)){
			sqlBuffer.append( " and a.roomtypeid=? ");
			paramsList.add(roomtypeid);
		}else{
			sqlBuffer.append( " and a.hotelid=? ");
			paramsList.add(hotelid);
		}
		//眯客3.0
		//好：G，中：M,  差：B， 全部：A
		//B:<2分，M:>=2且<3，G:>=3
		if(StringUtils.isNotBlank(gradetype)){
			this.settingGradeType(gradetype, sqlBuffer, paramsList);
		}else{
			if(StringUtils.isNotBlank(maxgrade)){
				sqlBuffer.append( " and a.grade<=? ");
				paramsList.add(maxgrade);
			}
			if(StringUtils.isNotBlank(mingrade)){
				sqlBuffer.append( " and a.grade>=?");
				paramsList.add(mingrade);
			}
		}
		if(StringUtils.isNotBlank(startdateday)){
			sqlBuffer.append( " and a.createtime >=? ");
			paramsList.add(startdateday);
		}
		if(StringUtils.isNotBlank(enddateday)){
			sqlBuffer.append( " and a.createtime<=? ");
			paramsList.add(enddateday);
		}

		return Db.queryLong(sqlBuffer.toString(),paramsList.toArray());
	}

	//取单项评分
	public List<Bean> findSubjectScoreByHotel(String hotelid) {
		String sql="select * from t_hotel_subject_his where hotelid=? and createTime=(select max(createtime) from t_hotel_subject_his) ";
		return Db.find(sql,hotelid);
	}

	public Bean findScorebyOrderId(String orderid) {
		String sql=" select * from t_hotel_score where orderid= ? and type=1";
		return Db.findFirst(sql, orderid);
	}

	public List<Bean> findScoreReplyByParentIds(String replyIds, List<Long> idList) {
		String sql=" select * from t_hotel_score where parentid in(" + replyIds + ") order by createtime desc";
		return Db.find(sql, idList.toArray());
	}

	public List<Bean> findMemberByIds(String ids, List<Long> midList){
		String sql="select mid,loginname,phone from u_member where mid in(" + ids + ")";
		return Db.find(sql, midList.toArray());
	}

	/**
	 * 组装评分分类
	 * 眯客3.0
	 * 好：G，中：M,  差：B， 全部：A
	 * B:<2分，M:>=2且<3，G:>=3
	 * @param gradetype
	 * @param sqlBuffer
	 * @param paramsList
	 */
	private void settingGradeType(String gradetype, StringBuffer sqlBuffer, List paramsList){
		if(gradetype.equals("G")){
			sqlBuffer.append( " and a.grade>=? ");
			paramsList.add("3");
		}
		if(gradetype.equals("M")){
			sqlBuffer.append( " and a.grade>=? ");
			paramsList.add("2");
			sqlBuffer.append( " and a.grade<? ");
			paramsList.add("3");
		}
		if( gradetype.equals("B")){
			sqlBuffer.append( " and a.grade<? ");
			paramsList.add("2");
		}
	}

	public List<Bean> findScoreGroupByGrade(String hotelid, String gradetype, String startdate, String enddate, Long mid) {
		String[] gradetypes= gradetype.split(",");
		StringBuffer sqlBuffer = new StringBuffer();
		List params = new ArrayList();
		for(int i=0;i<gradetypes.length;i++){
			String gt= gradetypes[i];

			params.add(hotelid);
			if(i>0){
				sqlBuffer.append(" union ");
			}
			sqlBuffer.append("select '");
			sqlBuffer.append(gt);
			sqlBuffer.append("' as scoregroup,count(*) as scorecount from t_hotel_score a where  hotelid=? and type=1 ");
			this.settingGradeType(gt, sqlBuffer, params);

			if(StringUtils.isNotBlank(startdate)){
				sqlBuffer.append(" and createtime>= ? ");
				params.add(startdate);
			}
			if( StringUtils.isNotBlank(enddate)){
				sqlBuffer.append(" and createtime<= ? ");
				params.add(enddate);
			}
			//20151012  4:通过审核,未回复，5:未通过审核,已回复，7:通过审核,已回复
			sqlBuffer.append(" and ((status=4 or status=7) or mid=?)");//4:待回复
			params.add(mid);

			//20150923 产品讨论，只展示状态为4的记录
			//sqlBuffer.append(" and status=4 ");//4:待回复
			//2015-08-21修改为只显示已审核，已经回复的评价
			//sqlBuffer.append(" and ( status=4 or status=5 )");//4:待回复，5:已经回复
		}
		return  Db.find(sqlBuffer.toString(),params.toArray());
	}
}
