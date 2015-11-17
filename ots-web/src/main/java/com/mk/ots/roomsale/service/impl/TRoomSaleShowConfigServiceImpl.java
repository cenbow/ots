package com.mk.ots.roomsale.service.impl;

import com.mk.ots.mapper.RoomSaleShowConfigMapper;
import com.mk.ots.roomsale.model.RoomSaleShowConfigDto;
import com.mk.ots.roomsale.model.TRoomSaleCity;
import com.mk.ots.roomsale.model.TRoomSaleShowConfig;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class TRoomSaleShowConfigServiceImpl implements TRoomSaleShowConfigService {
	private Logger logger = org.slf4j.LoggerFactory.getLogger(TRoomSaleShowConfigServiceImpl.class);

	@Autowired
	private RoomSaleShowConfigMapper roomSaleShowConfigMapper;

	public List<RoomSaleShowConfigDto> queryRenderableHeaderShows(RoomSaleShowConfigDto bean) throws Exception {
		List<RoomSaleShowConfigDto> resultList = new ArrayList<>();

		try {
			List<TRoomSaleShowConfig> tRoomSaleShowConfigList = roomSaleShowConfigMapper
					.queryRenderableHeaderShows(bean);

			for (TRoomSaleShowConfig showConfig : tRoomSaleShowConfigList) {
				resultList.add(buildUMemberDto(showConfig));
			}
		} catch (Exception ex) {
			throw new Exception("failed to queryRenderableShows", ex);
		}

		return resultList;
	}

	public List<RoomSaleShowConfigDto> queryRenderableShows(RoomSaleShowConfigDto bean) throws Exception {
		List<RoomSaleShowConfigDto> resultList = new ArrayList<>();

		try {
			List<TRoomSaleShowConfig> tRoomSaleShowConfigList = roomSaleShowConfigMapper.queryRenderableShows(bean);

			for (TRoomSaleShowConfig showConfig : tRoomSaleShowConfigList) {
				resultList.add(buildUMemberDto(showConfig));
			}
		} catch (Exception ex) {
			throw new Exception("failed to queryRenderableShows", ex);
		}

		return resultList;
	}

	public List<RoomSaleShowConfigDto> queryRoomSaleShowConfigByParams(RoomSaleShowConfigDto bean) {
		List<TRoomSaleShowConfig> tRoomSaleShowConfigList = roomSaleShowConfigMapper
				.queryRoomSaleShowConfigByParams(bean);
		List<RoomSaleShowConfigDto> resultList = new ArrayList<>();
		for (TRoomSaleShowConfig showConfig : tRoomSaleShowConfigList) {

			resultList.add(buildUMemberDto(showConfig));
		}
		return resultList;
	}

	private RoomSaleShowConfigDto buildUMemberDto(TRoomSaleShowConfig bean) {
		if (bean == null) {
			return new RoomSaleShowConfigDto();
		}
		RoomSaleShowConfigDto showDto = new RoomSaleShowConfigDto();
		showDto.setId(bean.getId());
		showDto.setPromotext(bean.getSaleName());
		showDto.setPromoid(bean.getSaleTypeId());
		showDto.setPromoicon(bean.getPicUrl());
		showDto.setPromonote(bean.getDescription());
		showDto.setBackPics(bean.getBackPicUrl());
		showDto.setBackColor(bean.getBackColor());
		showDto.setFontColor(bean.getFontColor());
		showDto.setFontFamily(bean.getFontFamily());
		showDto.setShowBeginTime(bean.getShowBeginTime());
		showDto.setShowEndTime(bean.getShowBeginTime());
		showDto.setShowArea(bean.getShowArea());
		showDto.setNormalId(bean.getNormalId());
		showDto.setIsSpecial(bean.getIsSpecial());
		showDto.setOrd(bean.getOrd());
		return showDto;
	}

	public List<TRoomSaleCity> queryTRoomSaleCity(String cityid) {
		logger.info(" method queryTRoomSaleCity   parame  cityid   " + cityid);
		HashMap<String, Object> map = new HashMap<>();
		map.put("cityCode", cityid);
		return roomSaleShowConfigMapper.queryTRoomSaleCity(map);
	}
}
