package com.mk.ots.rpc;

import java.io.Serializable;
import java.util.Map;

/**
 * OTS开放给pmssoap端的Hessian Rpc服务方法接口类
 * @author chuaiqing.
 *
 */
public interface IPmsSoapService extends Serializable {
    /**
     * PMS酒店上线处理接口
     * @param hotelid
     * 参数：PMS酒店id
     * @return
     */
    public Map<String, Object> pmsHotelOnline(String hotelid);
    
    /**
     * PMS酒店下线处理接口
     * @param hotelid
     * 参数：PMS酒店id
     * @return
     */
    public Map<String, Object> pmsHotelOffline(String hotelid);
}
