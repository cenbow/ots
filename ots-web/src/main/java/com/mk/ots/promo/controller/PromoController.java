package com.mk.ots.promo.controller;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.IMemberService;
import com.mk.ots.promo.service.IPromoService;
import com.mk.ots.roomsale.service.TRoomSaleShowConfigService;
import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author nolan
 *
 */
@Controller
@RequestMapping(value="/promo", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class PromoController {
	final Logger logger = LoggerFactory.getLogger(PromoController.class);

	@Autowired
	private IPromoService iPromoService;
	
	@Autowired
	private IMemberService iMemberService;

	@Autowired
	private TRoomSaleShowConfigService tRoomSaleShowConfigService;
	
	@RequestMapping("/handgennewticket")
	public ResponseEntity<Map<String, Object>> handGenTicket(String authcode, String regmid) {
		if(Strings.isNullOrEmpty(authcode)){
			throw MyErrorEnum.customError.getMyException("参数不正确");
		}
		if(Strings.isNullOrEmpty(regmid)){
			throw MyErrorEnum.customError.getMyException("注册用户编码为空");
		}
		UMember manager = MyTokenUtils.getMemberByToken(authcode);
		if(manager==null || !"15801209201".equals(manager.getPhone())){
			throw MyErrorEnum.customError.getMyException("管理权限错误");
		}
		
		Optional<UMember> regMemOpl = iMemberService.findMemberById(Long.parseLong(regmid));
		if(!regMemOpl.isPresent()){
			throw MyErrorEnum.customError.getMyException("注册用户不存在.");
		} else {
			UMember regMem = regMemOpl.get();
			iPromoService.genTicketByAllRegNewMember(regMem.getMid());
			logger.info("手动发放新用户礼包. mid:{}", regMem.getMid());
		}
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
}
