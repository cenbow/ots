package com.mk.ots.ticket.controller;

import com.google.common.collect.Maps;
import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.common.enums.BPromoStatuEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.ticket.model.BPromo;
import com.mk.ots.ticket.service.IBPromoService;
import com.mk.ots.utils.PayLockKeyUtil;
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
 * 优惠券业务接口
 * 1. 查询订单及我的优惠券列表
 * 2. 领取优惠券
 * @author nolan.zhang
 *
 */
@Controller
@RequestMapping(value="/ticket", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class BPromoController {
	final Logger logger = LoggerFactory.getLogger(BPromoController.class);
	

	@Autowired
	private IBPromoService ibPromoService;

	@Autowired
	private OrderService orderService;


	/**
	 * 效验特价卷接口
	 * @param token
	 * @param roomticket  特价卷密码
	 * @param hotelid   酒店id
	 * @return
	 */
	@RequestMapping("/check")
	public ResponseEntity<Map<String,Object>> selectTicket(ParamBaseBean pbb,String token, String roomticket, String hotelid){
		String  result =  "F";
		UMember memberByToken = MyTokenUtils.getMemberByToken(token);
		if(null==memberByToken){
			throw   MyErrorEnum.customError.getMyException("token非法");
		}
		if(Strings.isNullOrEmpty(hotelid)){
			throw MyErrorEnum.customError.getMyException("酒店信息不正确");
		}
		roomticket = roomticket.toUpperCase();
		if(ibPromoService.checkCanUse(roomticket,Long.parseLong(hotelid))){
			result  = "T";
		}

		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", result);
 		return new ResponseEntity<Map<String,Object>>(rtnMap, HttpStatus.OK);
	}

	/**
	 * 效验特价卷接口

	 * @param roomticket  特价卷密码
	 * @param orderid 酒店id
	 * @return
	 */
	@RequestMapping("/use")
	public ResponseEntity<Map<String,Object>> userPromo(ParamBaseBean pbb,String roomticket, String orderid, String mid,String  price){
		String  result = "F";
		//1. 参数验证
		OtaOrder otaOrder = null;
		if(!Strings.isNullOrEmpty(orderid)){
			otaOrder = orderService.findOtaOrderById(Long.parseLong(orderid));
			if(otaOrder == null){
				throw MyErrorEnum.findOrder.getMyException();
			}
		}
		
//		Integer cityId = otaOrder.getTCity().getCityid();
		//验证
//		boolean  bl = ibPromoService.checkCanUseByCityId(roomticket, cityId.longValue());
		boolean  bl = ibPromoService.checkCanUseByOrder(otaOrder);

		if(bl){
//			String lockValue = DistributedLockUtil.tryLock(PayLockKeyUtil.genLockKeyPromo(roomticket), 40);
			try {
				BPromo  bpromo = ibPromoService.getBPromoInfoByPwd(roomticket);
				ibPromoService.usePromo(otaOrder,bpromo, BPromoStatuEnum.beuse.getType());
				result = "T";
			} catch (Throwable e) {
				logger.error("特价卷:" + roomticket +"出现异常!", e);
			}finally {
				logger.info("订单：" + orderid +"特价卷使用成功,释放分布锁.");
//				DistributedLockUtil.releaseLock(PayLockKeyUtil.genLockKey4Pay(orderid), lockValue);
			}
		}


		Map<String,Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", result);
		return new ResponseEntity<Map<String,Object>>(rtnMap, HttpStatus.OK);
	}
}
