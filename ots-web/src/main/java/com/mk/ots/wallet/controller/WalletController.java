package com.mk.ots.wallet.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.model.Page;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.service.ScoreService;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;
import com.mk.ots.wallet.service.IWalletCashflowService;
import com.mk.ots.wallet.service.IWalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 钱包统一入口
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Controller
@RequestMapping(value = "/wallet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class WalletController {
    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private IWalletCashflowService iWalletCashflowService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ScoreService scoreService;

    /**
     * 订单返现入帐
     *
     * @param sourceid 订单id
     * @return ResponseEntity
     */
    @RequestMapping("/entry")
    public ResponseEntity<Map<String, Object>> entry(String sourceid) {
        Long tokenMid = MyTokenUtils.getMidByToken("");
        //1. 基本数据校验
        if (sourceid == null) {
            throw MyErrorEnum.customError.getMyException("订单id不允许为空.");
        }
        Long tmpsourceid = Long.parseLong(sourceid);
        OtaOrder otaOrder = orderService.findOtaOrderById(tmpsourceid);
        if (otaOrder == null) {
            throw MyErrorEnum.customError.getMyException("返现订单不存在.");
        }
        if (otaOrder.getReceiveCashBack() == 0) {
            throw MyErrorEnum.customError.getMyException("此订单不允许返现.");
        }
        if (!tokenMid.equals(otaOrder.getMid())) {
            throw MyErrorEnum.customError.getMyException("非本人订单不可返现.");
        }


        //2. 服务调用
        BigDecimal price = iWalletCashflowService.entry(otaOrder.getMid(), CashflowTypeEnum.CASHBACK_ORDER_IN, tmpsourceid);

        //3. 组织数据响应
        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", true);
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    /**
     * 查询余额
     *
     * @param token 用户token
     * @return ResponseEntity
     */
    @RequestMapping("/query")
    public ResponseEntity<Map<String, Object>> query(String token) {
        Long mid = MyTokenUtils.getMidByToken("");

        BigDecimal balance = this.iWalletService.queryBalance(mid);

        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", true);
        rtnMap.put("balance", balance);
        rtnMap.put("isonline", true);
        rtnMap.put("isoffline", false);
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    /**
     * 查询交易明细
     *
     * @param orderid   不为空只查询该订单的消费记录
     * @param pageindex 第x页
     * @param datasize  每页条数
     * @return ResponseEntity
     */
    @RequestMapping("/detail/query")
    public ResponseEntity<Map<String, Object>> querydetail(Long orderid, String pageindex, String datasize) {
        //1. 请求参数处理
        Long mid = MyTokenUtils.getMidByToken("");
        int tmppageindex = 1;
        int tmpdatasize = 10;
        if (!Strings.isNullOrEmpty(pageindex)) {
            tmppageindex = Integer.parseInt(pageindex);
        }
        if (!Strings.isNullOrEmpty(datasize)) {
            tmpdatasize = Integer.parseInt(datasize);
        }

        //2. 服务调用
        Page<UWalletCashFlow> page = this.iWalletCashflowService.findPage(mid, orderid, tmppageindex, tmpdatasize);

        //3. 组织数据响应
        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", true);
        rtnMap.put("pageindex", tmppageindex);
        rtnMap.put("pagenum", page.getTotalPages());
        rtnMap.put("datasize", tmpdatasize);
        rtnMap.put("result", page.getResult());
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
}
