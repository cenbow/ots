package com.mk.ots.rpc.processor;

import io.netty.channel.Channel;

import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.mk.framework.AppUtils;
import com.mk.ots.rpc.service.HmsHotelService;
import com.mk.server.processer.AbstractProcessor;
import com.mk.synserver.DirectiveData;

@Component(value = "hmsHotelProcessor")
public class ReceivedKeywordProcessor extends AbstractProcessor {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(ReceivedKeywordProcessor.class);

	@Override
	protected void handle(Channel channel, DirectiveData dd) {
		// TODO: 目前只有酒店的关键字，所以返回的keywordid即是hotelid
		Long hotelid = (Long) this.getValue(dd, AbstractProcessor.KEYWORD_ID);
		logger.info("同步酒店：{} 关键字成功.", hotelid);
		try {
			// 根据hotelid查询酒店信息，并同步到ES
			HmsHotelService hmsHotelService = AppUtils.getBean(HmsHotelService.class);
			hmsHotelService.addHmsHotelById(String.valueOf(hotelid));
		} catch (Exception e) {
			logger.error("save hms hotel to ES error: {} ", e.getMessage());
		}

	}
}
