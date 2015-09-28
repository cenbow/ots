package com.mk.ots.system.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.NetUtils;
import com.mk.framework.util.ThreadUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CrossDomainFilter implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(CrossDomainFilter.class);

	private List<String> crossDomainPatterns;

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		writeLogInfo(request, response);

		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);

		String url = ((HttpServletRequest) request).getRequestURI();
		if (this.matchCrossDomainPatterns(request.getServletContext().getContextPath(), url)) {
			((HttpServletResponse)response).setHeader("Access-Control-Allow-Origin", "*");
			((HttpServletResponse)response).setHeader("Access-Control-Allow-Methods", "POST");
			((HttpServletResponse)response).setHeader("Access-Control-Max-Age", "1000");
			chain.doFilter(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		String initcrossDomainParameter = arg0.getInitParameter("crossDomainPatterns");
		this.crossDomainPatterns = Splitter.on(',').trimResults().omitEmptyStrings().splitToList(initcrossDomainParameter);
	}
	
	/**
	 * @param contextpath
	 * @param url
	 * @return
	 */
	public boolean matchCrossDomainPatterns(String contextpath, String url) {
		boolean isExist = false;
		for (String prefix : this.crossDomainPatterns) {
			if (url.startsWith(contextpath + prefix)) {
				isExist = true;
				break;
			}
		}
		return isExist;
	}

	private void writeLogInfo(ServletRequest request, ServletResponse response) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String requesturl = httpServletRequest.getRequestURI();
		String callmethod = httpServletRequest.getParameter("callmethod");
		String callversion = httpServletRequest.getParameter("callversion");
		String ip = "";
		String hardwarecode = httpServletRequest.getParameter("hardwarecode");
		String param = "";
		try {
			ip = NetUtils.getIpAddr(httpServletRequest);
			param = new ObjectMapper().writeValueAsString(httpServletRequest.getParameterMap());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info(">>>>>>>{}, callmethod: {}, callversion: {}, ip: {}, hardwarecode: {} \n param: {}", requesturl, callmethod, callversion, ip, hardwarecode, param);
	}
}
