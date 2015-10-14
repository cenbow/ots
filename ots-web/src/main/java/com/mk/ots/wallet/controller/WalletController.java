package com.mk.ots.wallet.controller;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.exception.MyException;
import com.mk.framework.model.Page;
import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.service.IBCardService;
import com.mk.ots.common.bean.ParamBaseBean;
import com.mk.ots.member.model.UMember;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.service.ScoreService;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 钱包统一入口
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Controller
@RequestMapping(value = "/wallet", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
public class WalletController {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private IWalletCashflowService iWalletCashflowService;

    @Autowired
    private IBCardService iBCardService;
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
    public ResponseEntity<Map<String, Object>> querydetail(ParamBaseBean pbb,Long orderid, String pageindex, String datasize) {
        logger.info("【entry/detail/query】 params is : {}", pbb.toString());
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
        List<UWalletCashFlowExtend> result = getuWalletCashFlowExtends(page);
        rtnMap.put("result", result);
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

    private List<UWalletCashFlowExtend> getuWalletCashFlowExtends(Page<UWalletCashFlow> page) {
        List<UWalletCashFlowExtend> result=new ArrayList<>();
        if (CollectionUtils.isNotEmpty(page.getResult())){
            for(UWalletCashFlow uWalletCashFlow:page.getResult()){
                UWalletCashFlowExtend extend=new UWalletCashFlowExtend();
                try{
                    BeanUtils.copyProperties(extend, uWalletCashFlow);
                }catch (Exception e){
                    logger.error("uWalletCashFlow copy error:", e);
                }
                extend.setCashflowtypestr(uWalletCashFlow.getCashflowtype().getDesc());
                extend.setIsgetin(getIsgetin(uWalletCashFlow.getPrice()));
                result.add(extend);
            }
        }
        return result;
    }

    private int getIsgetin(BigDecimal price){                //1支出 2收入
        return price.compareTo(BigDecimal.ZERO) < 0 ? 1 : 2;
    }

    @RequestMapping("/balance/charge")
    public ResponseEntity<Map<String, Object>> saveCharge(ParamBaseBean bean,String token, String chargeno, String phone) {
        logger.info("【wallet/balance/charge】 params is : {}", bean.toString());
        logger.info("【wallet/balance/charge】 params is : {token,chargeno,Phone}", token + " , " + chargeno + " , " + phone);

        if (StringUtils.isEmpty(token)) {
            throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
        }
        UMember memberByToken = MyTokenUtils.getMemberByToken(token);
        if(memberByToken == memberByToken){
            throw MyErrorEnum.errorParm.getMyException("获取用户信息失败.");
        }
        if (StringUtils.isEmpty(chargeno)) {
            throw MyErrorEnum.errorParm.getMyException("充值密码为空.");
        }

        Long mid = memberByToken.getMid();
        String result = "F";
        String errMsg = "";
        BigDecimal price = null;
        try {
            BCard card = this.iBCardService.recharge(mid, chargeno);
            result = "T";
            price = card.getPrice();
        } catch (MyException e) {
            result = "F";
            errMsg = e.getMessage();
        }
        //组织数据响应
        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", result);
        rtnMap.put("errmsg", errMsg);
        rtnMap.put("chargeprice", price);

        if ("T".equals(result)) {
            rtnMap.put("errcode", HttpStatus.OK);
            return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
        } else {
            rtnMap.put("errcode", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.BAD_REQUEST);
        }
    }
}
