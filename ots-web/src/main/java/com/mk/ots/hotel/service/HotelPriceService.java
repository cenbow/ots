package com.mk.ots.hotel.service;

import java.util.List;
import java.util.Map;

import com.mk.ots.hotel.bean.EDailyRate;
import com.mk.ots.hotel.bean.ERackRate;
import com.mk.ots.hotel.bean.EWeekendRate;
import com.mk.ots.hotel.bean.RoomTypePriceBean;
import com.mk.ots.hotel.jsonbean.hotelprice.DailyRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.RackRateJsonBean;
import com.mk.ots.hotel.jsonbean.hotelprice.WeekendRateJsonBean;
import com.mk.ots.hotel.model.EHotelModel;


public interface HotelPriceService {
	/**
	 * @param hotelid
	 * @param roomtypeid
	 * 查询ERackRate 平日价
	 */
	public ERackRate findERackRateByConditions(Long hotelid,Long roomtypeid) throws Exception;
	/**
	 * @param eRackRate
	 * 保存ERackRate 平日价
	 */
	public int saveERackRate(ERackRate eRackRate);
	/**
	 * @param eRackRate
	 * 更新ERackRate 平日价
	 */
	public int updateERackRate(ERackRate eRackRate);
	
	
	/**
	 * 根据酒店ID查询酒店特殊价
	 * @param hotelid
	 * @param roomtypeid
	 * @return
	 */
	public List<EDailyRate> findEDailyRateByHotelid(Long hotelid);

	/**
	 * 批量保存EDailyRate 特殊价
	 * @param insertBatchList
	 * @return
	 */
	public Integer saveBatchEDailyRate(List<EDailyRate> insertBatchList);

	
	/**
	 * 批量更新EDailyRate 特殊价
	 * @param updateBatchList
	 * @return
	 */
	public Integer updateBatchEDailyRate(List<EDailyRate> updateBatchList);

	/**
	 * 同步特殊价
	 * @param dailyRateJsonBean
	 * @return
	 */
	public Integer syncDailyRate(DailyRateJsonBean dailyRateJsonBean, Long ehotelid);

	/**
	 * 同步门市价
	 * @return 是否有改动 
	 */
	public boolean syncrackrate(RackRateJsonBean rackRateJsonBean,Long ehotelid) throws Exception;
	
	/**
	 * @param eHotelModel 
	 * 修改酒店，暂不迁移，hotelservice内容太多，很容易出现冲突
	 */
	public int updateHotel(EHotelModel eHotelModel);
	
	/**
	 * @param hotelid
	 * @param roomtypeid
	 * 查询EWeekendRate 周末价
	 */
	public List<EWeekendRate> findEWeekendRatesByConditions(Long hotelid,Long roomtypeid) throws Exception;
	/**
	 * @param eWeekendRate
	 * 保存EWeekendRate 周末价
	 */
	public int saveEWeekendRate(EWeekendRate eWeekendRate);
	/**
	 * @param eWeekendRate
	 * 更新EWeekendRate 周末价
	 */
	public int updateEWeekendRate(EWeekendRate eWeekendRate);
	
	/**
	 * @return 是否有改动 
	 * 同步周末价
	 */
	public boolean syncweekend(WeekendRateJsonBean weekendRateJsonBean,Long ehotelid) throws Exception;
	
	/**
	 * 发送同步酒店价格消息到PMS
	 * @param hotelPMS
	 * @return
	 */
	public Map<String, Object> sendSyncPriceMessToPMS(String hotelPMS);
	
	/**
	 * @param hotelid 如果不传 刷新所有酒店 
	 * 刷新眯客价
	 */
	public void refreshMikePrices(Long hotelid);
	
	/**
	 * @param hotelid
	 * @param roomtypeid
	 * 获取缓存中价格相关数据
	 */
	public Map<String,Map<String,String>> getPriceConfigsFromCache(Long hotelid);
	
	/**
	 * 返回此酒店此房型在某一时间段内的眯客价及门市价列表(供订单组使用)
	 * @param hotelid
	 * @param roomtypeid
	 * @param startday
	 * @param endday
	 * 
	 */
	public List<RoomTypePriceBean> getRoomtypePrices(Long hotelid,Long roomtypeid,String startday,String endday);
	
	/**
	 * @return true 使用 false 不使用
	 * 动态门市价开关
	 */
	public boolean isUseNewPrice();
	

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
	public String[] getRoomtypeMikePrices(Long hotelid, Long roomtypeid, String startdateday, String enddateday);


	/**
	 * 得到酒店眯客价
	 * 返回值为字符串数组, 第1个为酒店的最低眯客价, 第2个为最低眯客价房型对应的门市价.
	 * @param hotelid
	 * @return String[]
	 * 返回值
	 */
	public String[] getHotelMikePrices(Long hotelid, String startdateday, String enddateday);
}
