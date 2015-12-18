package com.mk.ots.remind.controller;

import com.mk.framework.util.MyTokenUtils;
import com.mk.ots.common.enums.RemindTypeEnum;
import com.mk.ots.member.model.UMember;
import com.mk.ots.remind.service.RemindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value = "/remind", produces = MediaType.APPLICATION_JSON_VALUE)
public class RemindController {

    final Logger logger = LoggerFactory.getLogger(RemindController.class);

    @Autowired
    private RemindService remindService;

    @RequestMapping("/create")
    public ResponseEntity<Map<String, Object>> createSpecialRoomRemind(String token, Long hotelId, Long roomTypeId) {
        UMember memberByToken = MyTokenUtils.getMemberByToken(token);

        //check
        boolean isRemind = this.remindService.checkRemind(memberByToken.getMid(), RemindTypeEnum.SPECIAL_ROOM);
        if (isRemind) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("success",false);
            result.put("errcode","");
            result.put("errmsg","已提醒");
            return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
        }

        //remind
        this.remindService.createSpecialRoomRemind(memberByToken.getMid(), hotelId, roomTypeId);

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("success",true);
        return new ResponseEntity<Map<String, Object>>(result, HttpStatus.OK);
    }


}
