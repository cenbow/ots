package com.mk.ots.remote;

import com.mk.framework.util.NetUtils;
import com.mk.framework.util.UrlUtils;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.hessian.HessianHelper;
import com.mk.ots.common.utils.Constant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thinkpad on 2015/10/14.
 */
@Service
public class RoomRemoteService {
    private static Logger logger = LoggerFactory.getLogger(RoomRemoteService.class);

    private static String TASKFACTORY_URL = UrlUtils.getUrl("taskfactory.url");
    private static String QUERY_SALE_ROOM = "/taskfactory/roomSale/querySaleRoom";

    public JSONObject querySaleRoomByRoomId(Long roomId){
        logger.info(String.format("begin remote url %s params: roomid[%s]", QUERY_SALE_ROOM, roomId));
        String address = TASKFACTORY_URL + QUERY_SALE_ROOM;
        Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("roomId", roomId.toString());
        JSONObject obj = null;
        try {
            obj = NetUtils.dopost(address, paramsMap);
        } catch (IOException e) {
            logger.error("querySaleRoomByRoomId doPost error", e);
            e.printStackTrace();
        }
        logger.info(String.format("end remote url %s resule [%s]", QUERY_SALE_ROOM, roomId, JsonKit.toJson(obj)));
        return obj;
    }
}
