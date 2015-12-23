package com.mk.ots.view.service;

import com.alibaba.fastjson.JSONArray;
import com.mk.ots.view.model.SyViewLog;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jeashi on 2015/12/9.
 */
public interface ISyViewLogService {

    public  Boolean saveSyViewLog(HashMap<String, Object> map);

    public void pushSyViewLog(JSONArray ja);
}
