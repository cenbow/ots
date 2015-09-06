package com.mk.ots.rpc.service;

import javax.servlet.annotation.WebServlet;


@WebServlet(name="RpcServlet", urlPatterns={"/hessian"})
public class RemoteServiceImpl {
    public String call(String name) {
    	return "remote service is " + name;
    }
}
