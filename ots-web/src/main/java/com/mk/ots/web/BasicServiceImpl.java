package com.mk.ots.web;

import org.springframework.stereotype.Service;

import com.mk.ots.annotation.HessianService;
import com.mk.ots.web.model.User;

/**
 * 
 * @author chuaiqing.
 *
 */
@Service
@HessianService(value="/base", implmentInterface=BasicService.class)
public class BasicServiceImpl implements BasicService {
    
    private String serviceName; 
       
    @Override 
    public void setServiceName(String serverName) { 
        this.serviceName = serverName; 
    } 
       
    @Override 
    public String getServiceName() { 
        return this.serviceName; 
    } 
       
    @Override 
    public User createUser() { 
        return new User("zhangsan", "123456"); 
    }

}
