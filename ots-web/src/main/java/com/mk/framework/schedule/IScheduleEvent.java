package com.mk.framework.schedule;

public interface IScheduleEvent extends java.io.Serializable{
	
    public String getName();

    public String description();
    
    public void execute() ;
}