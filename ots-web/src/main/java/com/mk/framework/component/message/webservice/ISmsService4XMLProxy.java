package com.mk.framework.component.message.webservice;


public class ISmsService4XMLProxy implements ISmsService4XML {
  private String _endpoint = null;
  private ISmsService4XML iSmsService4XML = null;

  public ISmsService4XMLProxy() {
    _initISmsService4XMLProxy();
  }

  public ISmsService4XMLProxy(String endpoint) {
    _endpoint = endpoint;
    _initISmsService4XMLProxy();
  }

  private void _initISmsService4XMLProxy() {
    try {
      iSmsService4XML = (new SmsService4XMLImplServiceLocator()).getSmsService4XMLImplPort();
      if (iSmsService4XML != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)iSmsService4XML)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)iSmsService4XML)._getProperty("javax.xml.rpc.service.endpoint.address");
      }

    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }

  public String getEndpoint() {
    return _endpoint;
  }

  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (iSmsService4XML != null)
      ((javax.xml.rpc.Stub)iSmsService4XML)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);

  }

  public ISmsService4XML getISmsService4XML() {
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML;
  }

  public String balance(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.balance(message);
  }

  public String deliver(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.deliver(message);
  }

  public String submit(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.submit(message);
  }

  public String report(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.report(message);
  }

  public String keywordcheck(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.keywordcheck(message);
  }

  public String blackListCheck(String message) throws java.rmi.RemoteException{
    if (iSmsService4XML == null)
      _initISmsService4XMLProxy();
    return iSmsService4XML.blackListCheck(message);
  }
  
  
}