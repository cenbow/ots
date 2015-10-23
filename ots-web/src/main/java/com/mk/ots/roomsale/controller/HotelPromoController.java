package com.mk.ots.roomsale.controller;

import com.alibaba.fastjson.JSONObject;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.roomsale.model.TRoomSaleConfigInfo;
import com.mk.ots.roomsale.service.RoomSaleConfigInfoService;
import com.mk.ots.web.ServiceOutput;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 特价类型接口
 */
@Controller
public class HotelPromoController {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private RoomSaleConfigInfoService roomSaleConfigInfoService;

	/**
	 * 活动查询
	 **/

	@RequestMapping(value = "/hotel/querytypelist", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> querytypelist(ParamBaseBean pbb, String cityid, String saletypeid,
			Integer page, Integer limit) {
		logger.info("HotelPromoController::querytypelist::params{}  begin",
				pbb + "," + saletypeid + "," + cityid + "," + page + "," + limit);
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (page == null || StringUtils.isBlank(page.toString())) {
				page = 1;
			}
			if (limit == null || StringUtils.isBlank(limit.toString())) {
				limit = 10;
			}

			int start = (page - 1) * limit;

			if (StringUtils.isEmpty(saletypeid)) {
				result.put(ServiceOutput.STR_MSG_SUCCESS, false);
				result.put(ServiceOutput.STR_MSG_ERRCODE, "404");
				result.put(ServiceOutput.STR_MSG_ERRMSG, "没有活动");
				return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
			}

			List<TRoomSaleConfigInfo> roomSaleConfigInfoList = roomSaleConfigInfoService.queryListBySaleTypeId(cityid,
					Integer.parseInt(saletypeid), start, limit);

			List<JSONObject> list = new ArrayList<JSONObject>();
			if (CollectionUtils.isNotEmpty(roomSaleConfigInfoList)) {
				for (TRoomSaleConfigInfo saleConfigInfo : roomSaleConfigInfoList) {
					long sec = DateUtils.calDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getStartTime());

					long endSec = DateUtils.calEndDiffTime(saleConfigInfo.getStartDate(), saleConfigInfo.getEndDate(),
							saleConfigInfo.getEndTime());
					if (sec < 0) {
						continue;
					}
					JSONObject ptype1 = new JSONObject();

					if (logger.isInfoEnabled()) {
						logger.info(String.format("promotypeid: %s; promotypetext: %s; promotypeprice:%s",
								saleConfigInfo.getId(), saleConfigInfo.getSaleLabel(), saleConfigInfo.getSaleValue()));
					}

					ptype1.put("promotypeid", saleConfigInfo.getId());
					ptype1.put("promotypetext", saleConfigInfo.getSaleLabel());
					ptype1.put("promotypeprice", saleConfigInfo.getSaleValue());
					ptype1.put("promosec", sec / 1000); // 秒
					ptype1.put("promosecend", endSec / 1000); //距离结束时间（s）
					list.add(ptype1);
				}
			}

			result.put("promotypes", list);

			result.put(ServiceOutput.STR_MSG_SUCCESS, true);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("HotelPromoController::querytypelist::error{}", e.getMessage());
			result.put(ServiceOutput.STR_MSG_SUCCESS, false);
			result.put(ServiceOutput.STR_MSG_ERRCODE, "-1");
			result.put(ServiceOutput.STR_MSG_ERRMSG, e.getMessage());
		} finally {
			logger.info("HotelCollectionController::querylist::end");
		}
		return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
	}

}
