package com.mk.ots.wallet.service.impl;

import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.model.Page;
import com.mk.ots.city.service.ITCityCommentConfigService;
import com.mk.ots.common.enums.OtaOrderFlagEnum;
import com.mk.ots.hotel.model.THotelModel;
import com.mk.ots.hotel.service.HotelService;
import com.mk.ots.order.bean.OtaOrder;
import com.mk.ots.order.service.OrderBusinessLogService;
import com.mk.ots.order.service.OrderService;
import com.mk.ots.score.model.THotelScore;
import com.mk.ots.score.service.ScoreService;
import com.mk.ots.wallet.dao.IUWalletCashFlowDAO;
import com.mk.ots.wallet.model.CashflowTypeEnum;
import com.mk.ots.wallet.model.UWalletCashFlow;
import com.mk.ots.wallet.service.IWalletCashflowService;
import com.mk.ots.wallet.service.IWalletService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 钱包现金流业务处理类
 * <p/>
 * Created by nolan on 15/9/9.
 */
@Service
public class WalletCashflowService implements IWalletCashflowService {
    private static Logger logger = LoggerFactory.getLogger(WalletCashflowService.class);

    @Autowired
    private IUWalletCashFlowDAO iuWalletCashFlowDAO;

    @Autowired
    private IWalletService iWalletService;

    @Autowired
    private OrderBusinessLogService orderBusinessLogService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private HotelService hotelService;

    @Autowired
    private ITCityCommentConfigService itCityCommentConfigService;

    @Override
    public Page<UWalletCashFlow> findPage(Long mid, Long sourceid, int pageindex, int datesize) {
        Page<UWalletCashFlow> pageEntity = new Page<UWalletCashFlow>(datesize);
        pageEntity.setPageNo(pageindex);

        UWalletCashFlow entity = new UWalletCashFlow();
        entity.setMid(mid);
        entity.setSourceid(sourceid);
        return iuWalletCashFlowDAO.find(pageEntity, entity);
    }

    @Override
    public BigDecimal entry(Long mid, CashflowTypeEnum cashflowtype, Long sourceid) {
        logger.info(">>>点评返现: mid:{}, cashflowtype:{}, sourceid:{}", mid, cashflowtype, sourceid);
        BigDecimal price = BigDecimal.ZERO;
        if (mid == null) {
            throw MyErrorEnum.customError.getMyException("用户编码不允许为空.");
        }
        if (cashflowtype == null) {
            throw MyErrorEnum.customError.getMyException("返现类型不允许为空.");
        }
        if (sourceid == null) {
            throw MyErrorEnum.customError.getMyException("业务sourceid不允许为空.");
        }
        if (!CashflowTypeEnum.CASHBACK_ORDER_IN.equals(cashflowtype) && !CashflowTypeEnum.CASHBACK_HOTEL_IN.equals(cashflowtype)) {
            throw MyErrorEnum.customError.getMyException("此返现类别不存在.");
        }


        String tryLockKey = "WalletCashEntry_" + cashflowtype.getId() + "_" + sourceid;
        String tryLockValue = DistributedLockUtil.tryLock(tryLockKey, 60);
        if (tryLockValue == null) {
            throw MyErrorEnum.customError.getMyException("系统正在返现, 请匆重复操作.");
        }

        //1. 基础数据校验(判断订单或酒店点评是否已返现)
        if (this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, cashflowtype, sourceid) != null) {
            String tip = "";
            if (CashflowTypeEnum.CASHBACK_ORDER_IN.equals(cashflowtype)) {
                tip = "订单";
            } else if (CashflowTypeEnum.CASHBACK_HOTEL_IN.equals(cashflowtype)) {
                tip = "酒店点评";
            }
            throw MyErrorEnum.customError.getMyException("此" + tip + "[" + sourceid + "]已返现");
        }

        //2. 根据不同类别保存返现记录
        try {
            if (CashflowTypeEnum.CASHBACK_ORDER_IN.equals(cashflowtype)) {
                //2.1 订单返现
                //2.1.1 从订单获取返现金额
                price = queryCashBackOrder(sourceid);
                //2.1.2 保存返现记录并同步用户帐户
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    boolean result = saveCashflowAndSynWallet(mid, price, CashflowTypeEnum.CASHBACK_ORDER_IN, sourceid);
                    if (!result) {
                        throw MyErrorEnum.customError.getMyException("订单返现失败.");
                    }
                    logger.info("设置订单已返现. orderid: {}", sourceid);
                    orderService.receiveCashBack(sourceid);
                } else {
                    throw MyErrorEnum.customError.getMyException("订单返现不允许为零或负值.");
                }
                //2.1.3 记录返现日志
                OtaOrder order = orderService.findOtaOrderById(sourceid);
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.ORDER_CASHBACK.getId(), "", "¥" + price + "红包已放入您的账户", "");
            } else if (CashflowTypeEnum.CASHBACK_HOTEL_IN.equals(cashflowtype)) {
                //2.2 酒店点评返现
                //2.2.1 从配置表查询城市返现金额
                price = queryCashBackHotel(sourceid);
                //2.2.2 保存返现记录并同步用户帐户
                if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
                    boolean result = saveCashflowAndSynWallet(mid, price, CashflowTypeEnum.CASHBACK_HOTEL_IN, sourceid);
                    if (!result) {
                        throw MyErrorEnum.customError.getMyException("酒店点评返现失败.");
                    }
                } else {
                    throw MyErrorEnum.customError.getMyException("酒店点评返现不允许为零或负值.");
                }

                //2.1.3 记录返现日志
                OtaOrder order = orderService.findOtaOrderByScoreId(sourceid);
                orderBusinessLogService.saveLog(order, OtaOrderFlagEnum.SCORE_OK.getId(), "", "点评完成", "");
            }
        } finally {
            DistributedLockUtil.releaseLock(tryLockKey, tryLockValue);
        }
        return price;
    }


    @Override
    public boolean refund(Long mid, Long orderid) {
        logger.info(">>>钱包消费退回: mid:{}, cashflowtype:{}, orderid:{}", mid, CashflowTypeEnum.CONSUME_ORDER_REFUND, orderid);

        UWalletCashFlow uWalletCashFlow = this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, CashflowTypeEnum.CONSUME_ORDER_OUT_CONFIRM, orderid);
        if (uWalletCashFlow == null) {
            logger.info(">>>钱包消费退回(失败): 此用户订单不存在消费流水,无需退回.");
            return false;
        }
        //TODO 需考虑重复退回的问题(根据最后一条记录排重).
        if (CashflowTypeEnum.CONSUME_ORDER_REFUND.equals(uWalletCashFlow.getCashflowtype())) {
            logger.info(">>>钱包消费退回(成功): 请匆重复调用退回接口.");
            return true;
        } else if (CashflowTypeEnum.CONSUME_ORDER_OUT_CONFIRM.equals(uWalletCashFlow.getCashflowtype())) {
            logger.info(">>>钱包消费退回: 正常处理退回逻辑//开始.");
            boolean result = saveCashflowAndSynWallet(mid, uWalletCashFlow.getPrice().abs(), CashflowTypeEnum.CONSUME_ORDER_REFUND, orderid);
            logger.info(">>>钱包消费退回: 正常处理退回逻辑//结束. 结果:{}", result);
            return result;
        } else {
            logger.info(">>>钱包消费退回(失败): 未知业务类别操作.");
            throw MyErrorEnum.customError.getMyException("未知业务类别操作.");
        }
    }

    @Override
    public boolean pay(Long mid, Long orderid, BigDecimal price) {
        logger.info(">>>钱包消费: mid:{}, cashflowtype:{}, orderid:{}, price:{}", mid, CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK, orderid, price);

        if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            UWalletCashFlow uWalletCashFlow = this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, CashflowTypeEnum.CONSUME_ORDER_OUT_CONFIRM, orderid);
            if (uWalletCashFlow == null) {
                uWalletCashFlow = this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK, orderid);
                if (uWalletCashFlow == null) {
                    //1. 此订单以前没有消费流水
                    logger.info(">>>钱包消费[情景一]: 此订单之前未产生过消费流水.");
                    logger.info(">>>钱包消费[情景一]: 正常消费逻辑处理//开始.");
                    boolean result = saveCashflowAndSynWallet(mid, price, CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK, orderid);
                    logger.info(">>>钱包消费[情景一]: 正常消费逻辑处理//结束. 结果: {}.", result);
                    return result;
                } else {
                    //2. 此订单产生过消费流水
                    logger.info(">>>钱包消费[情景二]: 此订单消费流水[已冻结],请勿重复支付");
                    return true;
                }
            } else {
                //支付已确认
                logger.info(">>>钱包消费[情景三]: 此订单消费流水[已确认],请勿重复支付");
                return true;
            }
        } else {
            logger.warn(">>>钱包消费: 消费金额为零");
            return true;
        }
    }

    /**
     * 保存钱包乐住币流水
     *
     * @param mid              用户id
     * @param price            金额
     * @param cashflowTypeEnum 业务类别
     * @param sourceid         业务对应记录id
     * @return T/F
     */
    private boolean saveCashflowAndSynWallet(Long mid, BigDecimal price, CashflowTypeEnum cashflowTypeEnum, Long sourceid) {
        logger.info(">>>记录钱包流水并同步钱包总额: mid:{}, cashflowtype:{}, orderid:{}, price:{}", mid, cashflowTypeEnum, sourceid, price);

        BigDecimal realprice = BigDecimal.ZERO;
        if (CashflowTypeEnum.CASHBACK_HOTEL_IN.equals(cashflowTypeEnum) || CashflowTypeEnum.CASHBACK_ORDER_IN.equals(cashflowTypeEnum)) {
            realprice = price.abs();
        } else if (CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK.equals(cashflowTypeEnum)) {
            realprice = price.multiply(new BigDecimal(-1));
        } else if (CashflowTypeEnum.CONSUME_ORDER_REFUND.equals(cashflowTypeEnum)) {
            realprice = price.abs();
        }
        logger.info(">>>记录钱包流水并同步钱包总额: 记录钱包流水//开始");
        UWalletCashFlow item = new UWalletCashFlow();
        item.setMid(mid);
        item.setCashflowtype(cashflowTypeEnum);
        item.setPrice(realprice);
        item.setSourceid(sourceid);
        item.setCreatetime(new Date());
        iuWalletCashFlowDAO.insert(item);
        boolean success = item.getId() != null;
        logger.info(">>>记录钱包流水并同步钱包总额: 记录钱包流水//结束. 结果: {}.", success);

        if (success) {
            logger.info(">>>记录钱包流水并同步钱包总额: 同步钱包总额//开始");
            boolean isupdate = syncWalletBalance(mid);
            logger.info(">>>记录钱包流水并同步钱包总额: 同步钱包总额//结束. 结果: {}.", isupdate);
        }
        return success;
    }

    @Override
    public boolean confirmCashFlow(Long mid, Long orderid) {
        logger.info(">>>确认订单红包消费: mid:{}, orderid:{}", mid, orderid);
        UWalletCashFlow uWalletCashFlow = this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK, orderid);
        logger.info(">>>确认订单红包消费: 修改消费状态.");
        boolean status = false;
        if (uWalletCashFlow != null) {
            uWalletCashFlow.setCashflowtype(CashflowTypeEnum.CONSUME_ORDER_OUT_CONFIRM);
            status = this.iuWalletCashFlowDAO.update(uWalletCashFlow) > 0;
        }
        logger.info(">>>确认订单红包消费: 修改消费状态由[冻结]为[确认].//结束. 结果: {}", status);
        return status;
    }

    @Override
    public boolean unLockCashFlow(Long mid, Long orderid) {
        logger.info(">>>解冻订单红包消费: mid:{}, orderid:{}", mid, orderid);
        UWalletCashFlow uWalletCashFlow = this.iuWalletCashFlowDAO.findByTypeAndSourceid(mid, CashflowTypeEnum.CONSUME_ORDER_OUT_LOCK, orderid);
        logger.info(">>>解冻订单红包消费: 删除冻结记录.");
        boolean status = false;
        if (uWalletCashFlow != null) {
            status = this.iuWalletCashFlowDAO.delete(uWalletCashFlow.getId()) > 0;
            if (status) {
                logger.info(">>>记录钱包流水并同步钱包总额: 同步钱包总额//开始");
                boolean isupdate = syncWalletBalance(mid);
                logger.info(">>>记录钱包流水并同步钱包总额: 同步钱包总额//结束. 结果: {}.", isupdate);
            }
        }
        logger.info(">>>解冻订单红包消费: 删除冻结记录.//结束. 结果: {}", status);
        return false;
    }

    /**
     * 同步钱包金额
     *
     * @param mid 用户mid
     */
    private boolean syncWalletBalance(Long mid) {
        List<UWalletCashFlow> itemList = this.iuWalletCashFlowDAO.findCashflowsByMid(mid);
        if (!CollectionUtils.isEmpty(itemList)) {
            BigDecimal total = BigDecimal.ZERO;
            for (UWalletCashFlow tmp : itemList) {
                total = total.add(tmp.getPrice());
            }
            return this.iWalletService.updateBalance(mid, total);
        }
        return true;
    }

    /**
     * 查询订单返现金额
     *
     * @param orderid 订单id
     * @return 返现金额
     */
    private BigDecimal queryCashBackOrder(Long orderid) {
        OtaOrder otaorder = this.orderService.findOtaOrderById(orderid);
        if (otaorder != null) {
            return otaorder.getCashBack();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 酒店点评返现
     *
     * @param scoreid 点评id
     * @return 点评返现金额
     */
    private BigDecimal queryCashBackHotel(Long scoreid) {
        THotelScore hotelScore = scoreService.findScoreByScoreid(scoreid);
        if (hotelScore != null) {
            THotelModel tHotelModel = hotelService.readonlyHotelModel(hotelScore.getHotelid());
            Long citycode = Long.parseLong(tHotelModel.getCitycode());
            return itCityCommentConfigService.findCashbackByCitycode(citycode);
        }
        return BigDecimal.ZERO;
    }
}
