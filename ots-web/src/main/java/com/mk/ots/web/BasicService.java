package com.mk.ots.web;

import com.mk.ots.web.model.User;

public interface BasicService {
    public void setServiceName(String serverName); 
    
    public String getServiceName(); 
       
    public User createUser(); 
}
