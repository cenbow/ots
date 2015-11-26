/**
 * ISmsService4XML.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mk.framework.component.message.webservice;

public interface ISmsService4XML extends java.rmi.Remote {
    public String balance(String message) throws java.rmi.RemoteException;
    public String deliver(String message) throws java.rmi.RemoteException;
    public String submit(String message) throws java.rmi.RemoteException;
    public String report(String message) throws java.rmi.RemoteException;
    public String keywordcheck(String message) throws java.rmi.RemoteException;
    public String blackListCheck(String message) throws java.rmi.RemoteException;
}
