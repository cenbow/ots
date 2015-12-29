package com.mk.ots.search.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.common.utils.DateUtils;
import com.mk.ots.hotel.service.RoomstateService;
import com.mk.ots.mapper.HotelCollegeMapper;
import com.mk.ots.restful.input.CollegeQueryEntity;
import com.mk.ots.roomsale.service.RoomSaleService;
import com.mk.ots.search.model.HotelCollegeModel;
import com.mk.ots.search.service.CollegeSearchService;

@Service
public class CollegeSearchServiceImpl implements CollegeSearchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private HotelCollegeMapper hotelCollegeMapper;

	@Autowired
	private RoomstateService roomstateService;

	@Autowired
	private RoomSaleService roomSaleService;

	@Override
	public List<Map<String, Object>> search(CollegeQueryEntity queryEntity) throws Exception {
		List<HotelCollegeModel> collegeModels = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("cityId", queryEntity.getCityid());
			collegeModels = hotelCollegeMapper.queryHotelColleges(parameters);
		} catch (Exception ex) {
			logger.warn("failed to queryHotelColleges...", ex);

			throw new Exception("failed to queryHotelColleges...");
		}

		List<Map<String, Object>> collegeHotels = new ArrayList<>();

		if (collegeModels != null) {
			for (HotelCollegeModel collegeModel : collegeModels) {
				Map<String, Object> collegeHotel = new HashMap<>();
				Long hotelId = collegeModel.getHotelId();

				Date day = new Date();
				String strCurDay = DateUtils.getStringFromDate(day, DateUtils.FORMATSHORTDATETIME);
				String strNextDay = DateUtils.getStringFromDate(DateUtils.addDays(day, 1),
						DateUtils.FORMATSHORTDATETIME);

				if (hotelId != null) {
					collegeHotel.put("hotelid", String.valueOf(hotelId));

					String[] prices = null;

					try {
						prices = roomstateService.getHotelMikePrices(hotelId, strCurDay, strNextDay);
					} catch (Exception ex) {
						logger.warn(
								String.format("failed to roomstateService.getHotelMikePrices...hotelId:%s; ", hotelId),
								ex.getCause());
						continue;
					}

					String promopriceText = prices[0];
					String minpmspriceText = prices[1];

					BigDecimal promoPrice = new BigDecimal(promopriceText);
					BigDecimal minpmsPrice = new BigDecimal(minpmspriceText);

					Double tempMinPromoPrice = null;
					BigDecimal minPromoPrice = null;
					try {
						tempMinPromoPrice = roomSaleService.getHotelMinPromoPrice(hotelId.intValue());
						minPromoPrice = new BigDecimal(tempMinPromoPrice);
					} catch (Exception ex) {
						logger.warn(
								String.format("failed to roomSaleService.getHotelMinPromoPrice...hotelId:%s", hotelId),
								ex);
						continue;
					}

					if (tempMinPromoPrice != null) {
						if (promoPrice.compareTo(minPromoPrice) > 0) {
							promoPrice = minPromoPrice;
						}
					}

					collegeHotel.put("promoprice", promoPrice);
					collegeHotel.put("minpmsprice", minpmsPrice);
					collegeHotels.add(collegeHotel);
				} else {
					logger.warn("hotelId is empty...");
				}
			}
		} else {
			logger.warn("no college hotels have been found...");
		}

		return collegeHotels;
	}
}
