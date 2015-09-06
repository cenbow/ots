package com.mk.ots.system.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.base.Strings;
import com.mk.framework.exception.MyErrorEnum;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.ThreadUtil;

public class UserSecurityInterceptor implements HandlerInterceptor {
	private static Logger logger = LoggerFactory.getLogger(UserSecurityInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.info("Request URL:" + request.getRequestURL().toString() + ", Start Time=" + System.currentTimeMillis());
		request.setAttribute("startTime", startTime);
	
		String accessToken = request.getParameter("token");
		if (Strings.isNullOrEmpty(accessToken)) {
			accessToken = request.getParameter("accesstoken");
		}
		if (MyTokenUtils.getToken(accessToken) != null) {
			ThreadUtil.setThreadVar("token", accessToken);
			return true;
		} else {
			throw MyErrorEnum.accesstokenTimeOut.getMyException();
		}
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		logger.info("Request URL:" + request.getRequestURL().toString()+ " Sent to Handler : Current Time=" + System.currentTimeMillis());
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		long startTime = (Long) request.getAttribute("startTime");
		logger.info("Request URL::" + request.getRequestURL().toString() + ", ConsumeTime = " + (System.currentTimeMillis() - startTime));
	}

}
