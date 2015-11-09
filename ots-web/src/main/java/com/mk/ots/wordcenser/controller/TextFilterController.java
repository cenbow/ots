package com.mk.ots.wordcenser.controller;

import com.google.common.collect.Maps;
import com.mk.ots.wordcenser.job.TextFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping(value = "/textfilter", produces = MediaType.APPLICATION_JSON_VALUE)

public class TextFilterController {

	@Autowired
	TextFilterService textFilterService;
	
	@RequestMapping("/reload")
    public ResponseEntity<Map<String, Object>> reload(String token) {
        textFilterService.reload();
        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", true);
        rtnMap.put("message", "重新加载敏感词成功");
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }

}
