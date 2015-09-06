package com.mk.ots.security.web.authentication;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.google.common.base.Strings;
import com.mk.framework.util.MyTokenUtils;
import com.mk.framework.util.ThreadUtil;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
		// response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
		// "Unauthorized");

		long startTime = System.currentTimeMillis();
		request.setAttribute("startTime", startTime);
		String accessToken = request.getParameter("token");
		if (Strings.isNullOrEmpty(accessToken)) {
			accessToken = request.getParameter("accesstoken");
		}
		if (Strings.isNullOrEmpty(accessToken)) {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		if (MyTokenUtils.getToken(accessToken) != null) {
			ThreadUtil.setThreadVar("token", accessToken);
		} else {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
	}

}
