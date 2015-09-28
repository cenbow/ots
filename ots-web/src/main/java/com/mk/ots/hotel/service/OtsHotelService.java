package com.mk.ots.hotel.service;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.hotel.dao.OtsHotelDao;


/**
 * OtsHotelService: extends of HotelService
 * @author chuaiqing.
 *
 */
public class OtsHotelService extends HotelService {
    
    private Logger logger = org.slf4j.LoggerFactory.getLogger(OtsHotelService.class);
    
    @Autowired
    protected OtsHotelDao otsHotelDao;
    
}
