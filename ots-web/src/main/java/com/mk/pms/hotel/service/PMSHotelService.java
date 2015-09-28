package com.mk.pms.hotel.service;

import java.util.Map;
/**
 * pmssoap交互 同步房间
 * 
 * @author LYN
 *
 */
public interface PMSHotelService {
    public Map syncHotelInfo(Map map);
    
    public void pmsOnline(Long hotelid,Long time);
    
    public void pmsOffline(Long hotelid,Long time);
}
