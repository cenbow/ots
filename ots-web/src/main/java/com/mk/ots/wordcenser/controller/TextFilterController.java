package com.mk.ots.wordcenser.controller;

import com.google.common.collect.Maps;
import com.mk.ots.wordcenser.job.TextFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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

    @RequestMapping("/test")
    public ResponseEntity<Map<String, Object>> test(HttpServletRequest request, String token) {
        String text = request.getParameter("text");
        System.out.printf("校验结果 " + textFilterService.checkSensitiveWord(text));
        Map<String, Object> rtnMap = Maps.newHashMap();
        rtnMap.put("success", true);
        rtnMap.put("校验结果", textFilterService.checkSensitiveWord(text));
        rtnMap.put("message", "重新加载敏感词成功");
        return new ResponseEntity<Map<String, Object>>(rtnMap, HttpStatus.OK);
    }
}
