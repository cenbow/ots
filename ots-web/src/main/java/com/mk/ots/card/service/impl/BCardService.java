package com.mk.ots.card.service.impl;

import com.mk.framework.DistributedLockUtil;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.ots.card.model.BCard;
import com.mk.ots.card.model.CardTypeEnum;
import com.mk.ots.card.model.UCardUseLog;
import com.mk.ots.card.service.IBCardService;
import com.mk.ots.card.service.IUCardUseLogService;
import com.mk.ots.mapper.CardMapper;
import com.mk.ots.order.controller.OrderController;
import com.mk.ots.wallet.service.IWalletCashflowService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BCardService implements IBCardService {

    private static Logger logger = LoggerFactory.getLogger(BCardService.class);

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private IUCardUseLogService uCardUseLogService;

    @Autowired
    private IWalletCashflowService iWalletCashflowService;

    @Override
    public BCard findActivatedByPwd(String pwd) {
        if (StringUtils.isEmpty(pwd)) {
            throw MyErrorEnum.customError.getMyException("密码不能为空.");
        }
        BCard card = this.cardMapper.findActivatedByPwd(pwd);

        if (null == card) {
            throw MyErrorEnum.customError.getMyException("密码错误.");
        }

        //验证状态
        int status = card.getStatus();
        if (CardTypeEnum.TYPE_ACTIVE.getId() < status) {
            throw MyErrorEnum.customError.getMyException("未开始.");
        }
        if (CardTypeEnum.TYPE_ACTIVE.getId() > status) {
            throw MyErrorEnum.customError.getMyException("已充值.");
        }

        //验证是否在有效期内期
        Date beginDate = card.getBeginDate();
        Date endDate = card.getEndDate();
        Date now = new Date();

        if (now.before(beginDate) || now.after(endDate)) {
            throw MyErrorEnum.customError.getMyException("已过期.");
        }
        return card;
    }

    @Override
    public BCard recharge(Long mid, String pwd) {

        logger.info("充值卡：" + pwd + " 开始");

        if (null == mid) {
            throw MyErrorEnum.customError.getMyException("参数无效.");
        }
        //验证
        BCard card = this.findActivatedByPwd(pwd);

        String lockKey = "rechargeCard_" + pwd;
        //加锁
        logger.info("充值卡：" + pwd + "加分布锁");
        String lockValue = DistributedLockUtil.tryLock(lockKey, 40);
        if (lockValue == null) {
            logger.error("充值卡：" + pwd + " 重复充值");
            throw MyErrorEnum.customError.getMyException("不能重复充值");
        }

        try {
            //充值
            Long cardId = card.getId();
            this.iWalletCashflowService.accountCharge(mid, card.getPrice(), cardId);

            //log
            UCardUseLog log = new UCardUseLog();
            log.setCardId(cardId);
            log.setMid(mid);
            log.setCardPrice(card.getPrice());
            log.setCreateTime(new Date());
            this.uCardUseLogService.insert(log);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            logger.info("充值卡：" + pwd + "释放分布锁");
            DistributedLockUtil.releaseLock(lockKey, lockValue);
        }

        logger.info("充值卡：" + pwd + " 结束");
        return card;
    }
}
