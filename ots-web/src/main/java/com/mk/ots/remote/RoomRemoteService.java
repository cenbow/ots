package com.mk.ots.remote;

import com.mk.framework.util.NetUtils;
import com.mk.framework.util.UrlUtils;
import com.mk.orm.kit.JsonKit;
import com.mk.orm.plugin.hessian.HessianHelper;
import com.mk.ots.common.utils.Constant;
import com.mk.ots.remote.json.RoomSale;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thinkpad on 2015/10/14.
 */
@Service
public class RoomRemoteService {
    private static Logger logger = LoggerFactory.getLogger(RoomRemoteService.class);

    private static String TASKFACTORY_URL = UrlUtils.getUrl("taskfactory.url");
    private static String QUERY_SALE_ROOM = "/roomsale/querysaleroom";

    public List<RoomSale> querySaleRoomByRoomId(Long roomId){
        logger.info(String.format("begin remote url %s params: roomid[%s]", QUERY_SALE_ROOM, roomId));
        String address = TASKFACTORY_URL + QUERY_SALE_ROOM;
        List<RoomSale> roomSalesList = new ArrayList<RoomSale>();
        try {
            String jsonStr = "[\n" +
                    "  {\n" +
                    "    \"isOnPromo\": \"T\",\n" +
                    "    \"promoText\": \"今夜特价\",\n" +
                    "    \"promoTextColor\": \"#256887\",\n" +
                    "    \"promoStartTime\": \"10:32:00\",\n" +
                    "    \"promoEndTime\": \"2015-10-15 00:00:00.0\",\n" +
                    "    \"saleType\": 1,\n" +
                    "    \"saleName\": \"特价房\",\n" +
                    "    \"salePrice\": 50.0,\n" +
                    "    \"roomNo\": \"106\",\n" +
                    "    \"roomtypeid\": 626,\n" +
                    "    \"useDescribe\": null\n" +
                    "  }\n" +
                    "]";//NetUtils.dopost(address, String.format("roomId=%s",roomId.toString()));
            JSONArray jsonArray1 = JSONArray.fromObject(jsonStr);
            for (int i = 0; i < jsonArray1.size(); i++) {
                JSONObject jsonObject2 = jsonArray1.getJSONObject(i);
                RoomSale roomSale = (RoomSale) JSONObject.toBean(jsonObject2,
                        RoomSale.class);
                roomSalesList.add(roomSale);
            }
        } catch (Exception e) {
            logger.error("querySaleRoomByRoomId doPost error", e);
            e.printStackTrace();
        }
        logger.info(String.format("end remote url %s resule [%s]", QUERY_SALE_ROOM, roomId, JsonKit.toJson(jsonStr)));
        return roomSalesList;
    }
}
