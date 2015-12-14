package com.mk.ots.remind.service;

import com.dianping.cat.Cat;
import com.mk.ots.common.enums.RemindStatusEnum;
import com.mk.ots.common.enums.RemindTypeEnum;
import com.mk.ots.mapper.RemindLogMapper;
import com.mk.ots.mapper.RemindMapper;
import com.mk.ots.mapper.RemindTypeMapper;
import com.mk.ots.remind.model.Remind;
import com.mk.ots.remind.model.RemindLog;
import com.mk.ots.remind.model.RemindType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RemindService {

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private RemindLogMapper remindLogMapper;

    @Autowired
    private RemindTypeMapper remindTypeMapper;

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

    public void createSpecialRoomRemind(Long mid, Long hotelId, Long roomTypeId) {

        Cat.logEvent("CreateSpecialRoomRemind", "mid:" + mid + " hotelId:" + hotelId);

        RemindType remindType = remindTypeMapper.queryByCode(RemindTypeEnum.SPECIAL_ROOM.getCode());

        //remind
        Remind remind = new Remind();
        remind.setMid(mid);
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
}

