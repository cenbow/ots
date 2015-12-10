package com.mk.ots.view.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
import com.mk.framework.model.Page;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.service.IBCardService;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.model.THotelScore;
import com.mk.ots.score.service.ScoreService;
import com.mk.ots.view.service.ISyViewLogService;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;
import com.mk.ots.wallet.model.UWalletCashFlowExtend;
import com.mk.ots.wallet.service.IWalletCashflowService;
import com.mk.ots.wallet.service.IWalletService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 钱包统一入口
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Controller
@RequestMapping(value = "/sys", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class SyViewLogController {

    final Logger logger = LoggerFactory.getLogger(SyViewLogController.class);

    @Autowired
    private ISyViewLogService syViewLogService;


    @RequestMapping("/viewevent")
    public ResponseEntity<Map<String, Object>> addviewevent(HttpServletRequest request,String tourl, String actiontype) {
        logger.info("【sys/addviewevent】 params is : {tourl,actiontype}", tourl + " , " + actiontype );

        if (StringUtils.isEmpty(tourl)) {
            throw MyErrorEnum.errorParm.getMyException("获取目标url失败.");
        }

        if (StringUtils.isEmpty(actiontype)) {
            throw MyErrorEnum.errorParm.getMyException("获取事件类型失败.");
        }

        boolean result = false;
        HashMap<String, Object> dateMap = Maps.newHashMap();
        dateMap.put("toUrl",tourl);
        dateMap.put("actionType",actiontype);
        dateMap.put("fromUrl",request.getParameter("fromurl"));
        dateMap.put("params",request.getParameter("params"));
        dateMap.put("longitude",request.getParameter("longitude"));
        dateMap.put("latitude",request.getParameter("latitude"));
        dateMap.put("cityCode",request.getParameter("cityid"));
        dateMap.put("ip",request.getParameter("ip"));
        dateMap.put("callMethod",request.getParameter("callmethod"));
        dateMap.put("version",request.getParameter("version"));
        dateMap.put("wifiMacaddr",request.getParameter("wifimacaddr"));
        dateMap.put("biMacaddr",request.getParameter("bimacaddr"));
        dateMap.put("simsn",request.getParameter("simsn"));
        dateMap.put("mid",request.getParameter("mid"));
        dateMap.put("bussinessType",request.getParameter("bussinesstype"));
        dateMap.put("bussinessId",request.getParameter("bussinessid"));
        dateMap.put("hardwarecode",request.getParameter("hardwarecode"));
        dateMap.put("imei",request.getParameter("imei"));

        result = syViewLogService.saveSyViewLog(dateMap);
        //组织数据响应
        Map<String, Object> resultrtnMap = Maps.newHashMap();
        resultrtnMap.put("success", result);

        if (result) {
            return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
        } else {
            resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
            resultrtnMap.put("errmsg", "日志添加失败");
            return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
        }
    }
}
