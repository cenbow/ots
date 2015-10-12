package com.mk.ots.pricedrop.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.elasticsearch.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.common.enums.StrategyPriceType;
import com.mk.ots.pricedrop.model.BStrategyPrice;

/**
 * @author zhangyajun
 *
 */
@Controller
@RequestMapping(value="/salestrategy/rule", method=RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class BStrategyPriceController {
	final Logger logger = LoggerFactory.getLogger(BStrategyPriceController.class);

	@RequestMapping("/create")
	public ResponseEntity<Map<String, Object>> insertSalestrategyRule(HttpServletRequest request) {
		BStrategyPrice bStrategyPrice = new BStrategyPrice();
		//参数校验
		parameterCheck(request,bStrategyPrice);
		/*UMember manager = MyTokenUtils.getMemberByToken(authcode);
		if(manager==null || !"15801209201".equals(manager.getPhone())){
			throw MyErrorEnum.customError.getMyException("管理权限错误");
		}*/
		
		
		Map<String, Object> rtnMap = Maps.newHashMap();
		rtnMap.put("success", true);
		return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
	}
	private void  parameterCheck(HttpServletRequest request,BStrategyPrice bStrategyPrice){
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String value = request.getParameter("value");
		String stprice = request.getParameter("stprice");
		String rulearea = request.getParameter("rulearea");
		String rulehotel = request.getParameter("rulehotel");
		String ruleroomtype = request.getParameter("ruleroomtype");
		String rulebegintime = request.getParameter("rulebegintime");
		String ruleendtime = request.getParameter("ruleendtime");
		String ruleroom = request.getParameter("ruleroom");
		String enable = request.getParameter("enable");
		logger.info("参数打印：");
		logger.info("name:{},type:{},value:{},stprice:{},rulearea:{},rulehotel:{},ruleroomtype:{}",name,type,value,stprice,rulearea,rulehotel,ruleroomtype);
		logger.info("rulebegintime:{},ruleendtime:{},ruleroom:{},enable:{}",rulebegintime,ruleendtime,ruleroom,enable);
		if(Strings.isNullOrEmpty(name)){
			throw MyErrorEnum.customError.getMyException("策略名称参数不能为空");
		}
		if(Strings.isNullOrEmpty(type)){
			throw MyErrorEnum.customError.getMyException("类型参数不能为空");
		}else {
			try {
				long typeLong = Long.parseLong(type);
				if (StrategyPriceType.STANDARD.getId()>typeLong||StrategyPriceType.DISCOUNT.getId()<typeLong) {
					throw MyErrorEnum.customError.getMyException("类型参数数值不正确");
				}else {
					bStrategyPrice.setType(typeLong);
				}
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("类型参数须为整数");
			}
		}
		if(Strings.isNullOrEmpty(value)){
			throw MyErrorEnum.customError.getMyException("策略值参数不能为空");
		}else {
			try {
				long vlaueLong = Long.parseLong(value);
				BigDecimal vlaueBigDecimal = BigDecimal.valueOf(vlaueLong);
				bStrategyPrice.setValue(vlaueBigDecimal);
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("策略值参数转换为整数是异常");
			}
			
		}
		if(Strings.isNullOrEmpty(stprice)){
			throw MyErrorEnum.customError.getMyException("结算价参数不能为空");
		}else {
			try {
				long stpriceLong = Long.parseLong(stprice);
				BigDecimal stpriceBigDecimal = BigDecimal.valueOf(stpriceLong);
				bStrategyPrice.setStprice(stpriceBigDecimal);
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("结算价参数转换为整数是异常");
			}
		}
		
		if(Strings.isNullOrEmpty(rulearea)){
			throw MyErrorEnum.customError.getMyException("规则地域参数不能为空");
		}else {
			try {
				long ruleareaLong = Long.parseLong(rulearea);
				bStrategyPrice.setRulearea(ruleareaLong);
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("规则地域参数转换为整数是异常");
			}
		}
		if(Strings.isNullOrEmpty(rulehotel)){
			throw MyErrorEnum.customError.getMyException("规则酒店参数不能为空");
		}else {
			try {
				long rulehotelLong = Long.parseLong(rulehotel);
				bStrategyPrice.setRulehotel(rulehotelLong);
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("规则酒店参数转换为整数是异常");
			}
		}
		
		if(Strings.isNullOrEmpty(ruleroomtype)){
			throw MyErrorEnum.customError.getMyException("规则房型参数不能为空");
		}else {
			try {
				long ruleroomtypeLong = Long.parseLong(ruleroomtype);
				bStrategyPrice.setRuleroomtype(ruleroomtypeLong);
			} catch (Exception e) {
				// TODO: handle exception
				throw MyErrorEnum.customError.getMyException("规则房型参数转换为整数是异常");
			}
		}
		if(Strings.isNullOrEmpty(rulebegintime)){
			throw MyErrorEnum.customError.getMyException("起始时间参数不能为空");
		}
		
		if(Strings.isNullOrEmpty(ruleendtime)){
			throw MyErrorEnum.customError.getMyException("结束时间参数不能为空");
		}else {
			   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			   Date date = null;
			   try {
			    date = format.parse(ruleendtime);
			   } catch (ParseException e) {
			    e.printStackTrace();
			    throw MyErrorEnum.customError.getMyException("字符串结束时间转化为date类型错误");
			   }
			  
			
		}
		if(Strings.isNullOrEmpty(ruleroom)){
			throw MyErrorEnum.customError.getMyException("承包房间参数不能为空");
		}
		if(Strings.isNullOrEmpty(ruleroomtype)){
			throw MyErrorEnum.customError.getMyException("规则房型参数不能为空");
		}
		if(Strings.isNullOrEmpty(enable)){
			throw MyErrorEnum.customError.getMyException("是否启用参数不能为空");
		}
		
		bStrategyPrice.setName(name);
		
	} 
}
