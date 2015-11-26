/**
 * SmsService4XMLImplServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mk.framework.component.message.webservice;

public class SmsService4XMLImplServiceLocator extends org.apache.axis.client.Service implements SmsService4XMLImplService {
	/**
	 *
	 */
	private static final long serialVersionUID = 3619907631862438826L;
	private int timeOut = 60000;
	
    public SmsService4XMLImplServiceLocator() {
    }


    public SmsService4XMLImplServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SmsService4XMLImplServiceLocator(String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SmsService4XMLImplPort
    private String SmsService4XMLImplPort_address = "http://172.18.1.107:8080/services/sms";

    public String getSmsService4XMLImplPortAddress() {
        return SmsService4XMLImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private String SmsService4XMLImplPortWSDDServiceName = "SmsService4XMLImplPort";

    public String getSmsService4XMLImplPortWSDDServiceName() {
        return SmsService4XMLImplPortWSDDServiceName;
    }

    public void setSmsService4XMLImplPortWSDDServiceName(String name) {
        SmsService4XMLImplPortWSDDServiceName = name;
    }

    public ISmsService4XML getSmsService4XMLImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SmsService4XMLImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSmsService4XMLImplPort(endpoint);
    }

    public ISmsService4XML getSmsService4XMLImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            SmsService4XMLImplPortSoapBindingStub _stub = new SmsService4XMLImplPortSoapBindingStub(portAddress, this);
            _stub.setPortName(getSmsService4XMLImplPortWSDDServiceName());
            _stub.setTimeout(this.timeOut);
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSmsService4XMLImplPortEndpointAddress(String address) {
        SmsService4XMLImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ISmsService4XML.class.isAssignableFrom(serviceEndpointInterface)) {
                SmsService4XMLImplPortSoapBindingStub _stub = new SmsService4XMLImplPortSoapBindingStub(new java.net.URL(SmsService4XMLImplPort_address), this);
                _stub.setPortName(getSmsService4XMLImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        String inputPortName = portName.getLocalPart();
        if ("SmsService4XMLImplPort".equals(inputPortName)) {
            return getSmsService4XMLImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.ws.dahantc.com", "SmsService4XMLImplService");
    }

    @SuppressWarnings("rawtypes")
	private java.util.HashSet ports = null;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.ws.dahantc.com", "SmsService4XMLImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(String portName, String address) throws javax.xml.rpc.ServiceException {

if ("SmsService4XMLImplPort".equals(portName)) {
            setSmsService4XMLImplPortEndpointAddress(address);
        }
        else
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }
    

	public int getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

}
