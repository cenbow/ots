package com.mk.ots.rpc;

import com.mk.es.entities.OtsHotel;

/**
 * IHotelService
 * OTS端酒店服务方法接口类,提供给内部系统通过hessian进行RPC调用。
 * @author chuaiqing.
 *
 */
public interface IHotelService {
    public abstract boolean save(OtsHotel hotel);
    public abstract boolean update(OtsHotel hotel);
    /** H端酒店上线处理 */
    public abstract boolean online(String hotelid);
    /** H端酒店下线处理 */
    public abstract boolean offline(String hotelid);
    
}
