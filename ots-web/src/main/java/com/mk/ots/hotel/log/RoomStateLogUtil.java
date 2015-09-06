package com.mk.ots.hotel.log;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mk.ots.mapper.RoomStateLogMapper;
import com.mk.ots.room.bean.RoomStateLog;

/**
 * @author he
 * 房态log
 */
@Service
public class RoomStateLogUtil{
	
	@Autowired
	private RoomStateLogMapper roomStateLogMapper;
	
	private static ExecutorService pool = Executors.newFixedThreadPool(5);

    public void sendLog(final String hardwarecode,final String callmethod,final String callversion,final String ip,final String methodurl,final String methodparams,final String optuser){
    	pool.execute(new Runnable() {
			@Override
			public void run() {
				RoomStateLog log = new RoomStateLog();
				try {
					log.setCallmethod(callmethod);
					log.setCallversion(callversion);
					log.setIp(ip);
					log.setMethodurl(methodurl);
					log.setMethodparams(methodparams);
					log.setCreatetime(Calendar.getInstance().getTime());
					log.setOptuser(optuser);
					log.setOther1(hardwarecode);
					roomStateLogMapper.insertSelective(log);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    }
}
