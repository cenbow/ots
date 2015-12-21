package com.mk.ots.remind.service;

import com.dianping.cat.Cat;
import com.google.common.base.Optional;
import com.mk.care.kafka.common.PushMessageTypeEnum;
import com.mk.care.kafka.common.SmsMessageTypeEnum;
import com.mk.care.kafka.model.AppMessage;
import com.mk.care.kafka.model.Message;
import com.mk.care.kafka.model.SmsMessage;
import com.mk.care.kafka.model.WeixinMessage;
import com.mk.ots.common.enums.RemindStatusEnum;
import com.mk.ots.common.enums.RemindTypeEnum;
import com.mk.ots.common.enums.ValidEnum;
import com.mk.ots.kafka.message.CareProducer;
import com.mk.ots.kafka.message.MessageProducer;
import com.mk.ots.mapper.RemindLogMapper;
import com.mk.ots.mapper.RemindMapper;
import com.mk.ots.mapper.RemindTypeMapper;
import com.mk.ots.member.model.UMember;
import com.mk.ots.member.service.impl.MemberService;
import com.mk.ots.remind.model.Remind;
import com.mk.ots.remind.model.RemindLog;
import com.mk.ots.remind.model.RemindType;
import com.mk.ots.remind.runnable.PushMessageRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class RemindService {

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private RemindLogMapper remindLogMapper;

    @Autowired
    private RemindTypeMapper remindTypeMapper;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private MemberService memberService;

    @Autowired
    private CareProducer careProducer;
    /**
     * 检查用户当日是否已设置提醒
     * @param mid
     * @param typeEnum
     * @return 遇错 和 已提醒都返回true
     */
    public boolean checkRemind(Long mid, RemindTypeEnum typeEnum) {
        //
        if (null == mid || null == typeEnum) {
            return true;
        }

        //
        RemindType remindType = remindTypeMapper.queryByCode(typeEnum.getCode());

        if (null == remindType) {
            return true;
        }

        //判断当日是否已提醒
        Remind paramRemind = new Remind();
        paramRemind.setMid(mid);
        paramRemind.setTypeId(remindType.getId());
        remindType.setRemindTime(this.getDate(null));

        List<Remind> remindList = this.remindMapper.queryByMid(paramRemind);
        if (remindList.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void createSpecialRoomRemind(UMember member, Long hotelId, Long roomTypeId) {
        if (null == member || null == hotelId) {
            return;
        }

        Cat.logEvent("CreateSpecialRoomRemind", "mid:" + member.getId() + " hotelId:" + hotelId);

        RemindType remindType = remindTypeMapper.queryByCode(RemindTypeEnum.SPECIAL_ROOM.getCode());

        //remind
        Remind remind = new Remind();
        remind.setMid(member.getMid());
        remind.setPhone(member.getPhone());
        remind.setContent(remindType.getContent());
        remind.setTitle(remindType.getTitle());
        remind.setUrl(remindType.getUrl() + hotelId);
        remind.setTypeId(remindType.getId());
        remind.setRemindTime(this.buildTodayRemindTime(remindType.getRemindTime()));
        remind.setCount(0);
        remind.setStatusCode(RemindStatusEnum.INITIALIZE.getCode());
        remind.setCreateTime(new Date());
        remind.setUpdateTime(new Date());
        this.remindMapper.save(remind);

        //log
        RemindLog log = new RemindLog();
        log.setRemindId(remind.getId());
        log.setContent(remind.getContent());
        log.setStatusCode(RemindStatusEnum.INITIALIZE.getCode());
        log.setCreateTime(new Date());
        remindLogMapper.save(log);

    }

    private Date buildTodayRemindTime(Date remindTime) {
        if (null == remindTime) {
            return new Date();
        }

        //
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat remindFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String strDate = dateFormat.format(new Date());
        String strTime = timeFormat.format(remindTime);

        try {
            Date remindDate = remindFormat.parse(strDate + " " + strTime);
            return remindDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    private Date getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            if (null == date) {
                String strDate = dateFormat.format(new Date());
                return dateFormat.parse(strDate);
            } else {
                String strDate = dateFormat.format(date);
                return dateFormat.parse(strDate);
            }
        }catch (Exception e) {
            return new Date();
        }
    }

    public void pushMessage() {
        //查询类型
        List<RemindType> remindTypeList = remindTypeMapper.queryByValid();

        //
        ExecutorService pool = Executors.newFixedThreadPool(8);
        for(RemindType type : remindTypeList) {
            Long typeId = type.getId();

            //查询要发送的消息
            List<Remind> remindList = this.remindMapper.findPushRemind(typeId);
            for (Remind remind : remindList) {

                remind.setCount(remind.getCount() + 1);
                remind.setExecuteTime(new Date());
                remind.setUpdateTime(new Date());
                this.remindMapper.update(remind);
                //发送消息
                PushMessageRunnable pushMessageRunnable = new PushMessageRunnable(type,remind);
                pool.submit(pushMessageRunnable);
            }
        }

        //超45秒
        final long awaitTime = 45 * 1000;
        try {

            if(!pool.awaitTermination(awaitTime, TimeUnit.MILLISECONDS)){
                pool.shutdown();
            }
        } catch (InterruptedException e) {
            System.out.println("awaitTermination interrupted: " + e);
            pool.shutdownNow();
        }

    }

    public void pushMessage(RemindType type,Remind remind){
        //
        int isPush = type.getPush();
        if (isPush == 1) {
            AppMessage message = new AppMessage();
            message.setMid(remind.getMid());
            message.setPhone(remind.getPhone());
            message.setTitle(remind.getTitle());
            message.setMsgContent(remind.getContent());
            message.setMsgtype(PushMessageTypeEnum.USER);
            message.setUrl(remind.getUrl());
            this.messageProducer.sendAppMsg(message);

        }

        //
        int isSms = type.getSms();
        if (isSms == 1) {

            SmsMessage message = new SmsMessage();
            message.setPhone(remind.getPhone());
            message.setMessage(remind.getContent());
            message.setSmsMessageTypeEnum(SmsMessageTypeEnum.normal);
            this.messageProducer.sendSmsMsg(message);
        }

        //
        int isWeixin = type.getWeixin();
        if (isWeixin == 1) {
            String openId = "";
            Long mid = remind.getMid();
            Optional<UMember> op = this.memberService.findMemberById(mid);
            if (op.isPresent()) {
                UMember uMember = op.get();
                openId = uMember.getUnionid();
            } else {
                return;
            }

            WeixinMessage message = new WeixinMessage();

            message.setContent(remind.getContent());
            message.setUnionid(openId);
            message.setUrl(remind.getUrl());
            message.setMid(remind.getMid());
            this.messageProducer.sendWeixinMsg(message);
        }

        remind.setStatusCode(RemindStatusEnum.DONE.getCode());
        remind.setUpdateTime(new Date());
        this.remindMapper.update(remind);

        //log
        RemindLog log = new RemindLog();
        log.setRemindId(remind.getId());
        log.setContent(remind.getContent());
        log.setStatusCode(RemindStatusEnum.DONE.getCode());
        log.setCreateTime(new Date());
        this.remindLogMapper.save(log);
    }
}