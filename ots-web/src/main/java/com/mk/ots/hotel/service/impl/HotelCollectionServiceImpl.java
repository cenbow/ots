package com.mk.ots.hotel.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.bean.HotelCollection;
import com.mk.ots.hotel.bean.HotelCollectionExample;
import com.mk.ots.hotel.comm.enums.HotelPictureEnum;
import com.mk.ots.hotel.model.THotel;
import com.mk.ots.hotel.service.CashBackService;
import com.mk.ots.hotel.service.HotelCollectionService;
import com.mk.ots.hotel.service.HotelPriceService;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.mapper.HotelCollectionMapper;

@Service
public class HotelCollectionServiceImpl implements HotelCollectionService {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMddHHmmss");
	
	@Autowired
	private HotelCollectionMapper hotelCollectionMapper;
	@Autowired
	private HotelPriceService hotelPriceService;
	@Autowired
	private HotelService hotelService;

	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private CashBackService cashBackService;

	/**
	 * 酒店收藏查询
	 */
	@Override
	public List<Map<String, Object>> querylist(String token, String code, int start, int limit) throws Exception {
		Calendar calendar = Calendar.getInstance();
		String today = sdf.format(calendar.getTime());
		String tomorrow = sdf.format(DateUtils.addDays(calendar.getTime(), 1));

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		Map<String, Object> param = Maps.newHashMap();
		param.put("token", token);
		param.put("code", code);
		param.put("start", start);
		param.put("limit", limit);
		List<HotelCollection> list = hotelCollectionMapper.querylist(param);
		for (HotelCollection hotelCollection : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			Long hotelid = hotelCollection.getHotelid();
			THotel thotel = new THotel();
			thotel.setHotelid(hotelid + "");
			thotel.setIsfacility("T");
			thotel.setIshotelpic("T");
			Map<String, Object> result0 = hotelService.readonlyOtsHotelFromEsStore(thotel);
			List<Map<String, Object>> hotels = (List<Map<String, Object>>) result0.get("hotel");
			if (hotels.size() > 0) {
				Map<String, Object> hotel = hotels.get(0);
				String citycode = hotel.get("citycode") == null ? "" : hotel.get("citycode").toString();
				map.put("visible", hotel.get("visible"));
				map.put("priority", hotel.get("priority"));
				map.put("hotelid", hotel.get("hotelid"));
				map.put("hoteldisc", hotel.get("hoteldisc"));
				map.put("hotelname", hotel.get("hotelname"));
				map.put("hotelprovince", hotel.get("hotelprovince"));
				map.put("hoteldis", hotel.get("hoteldis"));
				map.put("hotelrulecode", hotel.get("hotelrulecode"));
				map.put("scorecount", hotel.get("scorecount"));
				map.put("roomnum", hotel.get("roomnum"));
				map.put("online", hotel.get("online"));
				map.put("grade", hotel.get("grade"));
				map.put("detailaddr", hotel.get("detailaddr"));
				map.put("hotelpicnum", hotel.get("hotelpicnum"));
				map.put("hotelphone", hotel.get("hotelphone"));
				map.put("isrecommend", hotel.get("isrecommend"));
				map.put("hotelcity", hotel.get("hotelcity"));
				map.put("hotelfacility", hotel.get("hotelfacility"));
				map.put("ispms", hotel.get("ispms"));
				map.put("collecttime", sdf1.format(hotelCollection.getCollecttime()));
				map.put("isnewpms", hotel.get("isnewpms") == null ? "F" : hotel.get("isnewpms").toString());
				map.put("latitude", hotel.get("latitude"));
				map.put("longitude", hotel.get("longitude"));
				// 可订房间数
				Integer avlblroomnum = hotelService.getAvlblRoomNum(hotelid,
						hotel.get("isnewpms") == null ? "F" : hotel.get("isnewpms").toString(),
						hotel.get("visible").toString(), hotel.get("online").toString(), null, null);
				map.put("avlblroomnum", avlblroomnum);

				// 可定房间数描述
				Map<String, String> fullstate = hotelService.getFullState(avlblroomnum);
				map.putAll(fullstate);

				// 月销量
				String ordernummon = hotel.get("ordernummon") == null ? "" : String.valueOf(hotel.get("ordernummon"));

				if (Constant.STR_CITYID_SHANGHAI.equals(citycode)) {
					map.put("ordernummon", "");
				} else {
					map.put("ordernummon", ordernummon);
				}

				// 是否返现（T/F）
				boolean iscashback = cashBackService.isCashBackHotelId(Long.valueOf(hotelid), today, tomorrow);
				if (iscashback) {
					map.put("iscashback", Constant.STR_TRUE);
				} else {
					map.put("iscashback", Constant.STR_FALSE);
				}

				String[] prices = null;
				if (hotelPriceService.isUseNewPrice())
					prices = hotelPriceService.getHotelMikePrices(hotelid, today, tomorrow);
				else
					prices = roomstateService.getHotelMikePrices(hotelid, today, tomorrow); // 最低眯客价和最低门市价
				map.put("minprice", new BigDecimal(prices[0]));
				map.put("minpmsprice", new BigDecimal(prices[1]));

				// 酒店图片
				if (hotel.get("needtranspic") != null) {
					List<Map<String, Object>> beans = (List<Map<String, Object>>) hotel.get("needtranspic");
					// map.remove("needtranspic");
					List<Map<String, Object>> maplist = new ArrayList<Map<String, Object>>();
					boolean ismatch = false;
					for (Map<String, Object> hotelpic : beans) {
						// 遍历到主力房源
						Map<String, Object> picmap = new HashMap<String, Object>();
						String picName = String.valueOf(hotelpic.get("name"));
						List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
						if (HotelPictureEnum.PIC_MAINHOUSING.getTitle().equals(picName) && picurl != null
								&& picurl.size() > 0) {
							picmap.put("name", picName);
							picmap.put("url", picurl.get(0).get("url"));
							maplist.add(picmap);
							ismatch = true;
							map.put("pic", maplist);
							break;
						}
					}
					// 未匹配到取门头及招牌
					if (!ismatch) {
						for (Map<String, Object> hotelpic : beans) {
							Map<String, Object> picmap = new HashMap<String, Object>();
							String picName = String.valueOf(hotelpic.get("name"));
							List<Map<String, Object>> picurl = (List<Map<String, Object>>) hotelpic.get("pic");
							if (HotelPictureEnum.PIC_DEF.getTitle().equals(picName) && picurl != null
									&& picurl.size() > 0) {
								picmap.put("name", picName);
								picmap.put("url", picurl.get(0).get("url"));
								maplist.add(picmap);
								map.put("pic", maplist);
								break;
							}
						}
					}
				}
			}

			result.add(map);
		}
		return result;
	}

	@Override
	public void addCollection(String token, Long hotelid) throws Exception {
		Calendar c = Calendar.getInstance();
		HotelCollection record = new HotelCollection();
		record.setHotelid(hotelid);
		record.setMid(MyTokenUtils.getMidByToken(token));
		record.setCollecttime(c.getTime());
		hotelCollectionMapper.insertSelective(record);
	}

	@Override
	public void deleteCollection(String token, Long hotelid) throws Exception {
		HotelCollectionExample hotelCollectionExample = new HotelCollectionExample();
		hotelCollectionExample.createCriteria().andMidEqualTo(MyTokenUtils.getMidByToken(token))
				.andHotelidEqualTo(hotelid);
		hotelCollectionMapper.deleteByExample(hotelCollectionExample);
	}

	@Override
	public HotelCollection queryinfo(String token, Long hotelid) throws Exception {
		HotelCollectionExample hotelCollectionExample = new HotelCollectionExample();
		Long midToken = MyTokenUtils.getMidByToken(token);
		if (midToken != null) {
			hotelCollectionExample.createCriteria().andMidEqualTo(midToken).andHotelidEqualTo(hotelid);
			List<HotelCollection> list = hotelCollectionMapper.selectByExample(hotelCollectionExample);
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		}

		return null;
	}

	@Override
	public Map<String, Object> readonlyHotelISCollected(String token, Long hotelid) throws Exception {
		HotelCollection hc = this.queryinfo(token, hotelid);
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		if (hc == null) {
			rtnMap.put("state ", Constant.STR_FALSE);
		} else {
			rtnMap.put("state", Constant.STR_TRUE);
		}
		return rtnMap;
	}

	@Override
	public int readonlyHotelCollectedCount(String token) {
		return hotelCollectionMapper.queryCount(token);
	}
}
