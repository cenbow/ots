package com.mk.ots.view.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.MkJedisConnectionFactory;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
import com.mk.framework.jedis.MkJedis;
import com.mk.framework.jedis.MkJedisFactory;
import com.mk.framework.model.Page;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.UrlUtils;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.service.IBCardService;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.model.THotelScore;
import com.mk.ots.score.service.ScoreService;
import com.mk.ots.utils.SpringContextUtil;
import com.mk.ots.view.dao.ISyViewLogDao;
import com.mk.ots.view.dao.impl.SyViewLogDaoImpl;
import com.mk.ots.view.model.SyViewLog;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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


    @RequestMapping(value = "/viewevent", method = {RequestMethod.POST })
    public ResponseEntity<Map<String, Object>> addviewevent(String data) {
        logger.info("【sys/addviewevent】 data is : {}", data);
        List<SyViewLog> logList = JSONObject.parseArray(data,SyViewLog.class);
        Map<String, Object> resultrtnMap = Maps.newHashMap();
        if (StringUtils.isEmpty(data)) {
            logger.error("获取目标data失败.");
            resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
            resultrtnMap.put("errmsg", "获取目标data失败!");
            return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
        }

        boolean result = false;
        try{
            //组织数据响应
            syViewLogService.pushSyViewLog(logList);
            result = true;
        }catch(Exception   e){
            result =false;
        }finally{
            resultrtnMap.put("success", result);
            if (result) {
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            } else {
                resultrtnMap.put("errcode", HttpStatus.BAD_REQUEST.value());
                resultrtnMap.put("errmsg", "添加日志失败");
                return new ResponseEntity<Map<String, Object>>(resultrtnMap, HttpStatus.OK);
            }
        }

    }
}
