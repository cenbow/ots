package com.mk.orm.plugin.hessian;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.support.AopUtils;

import com.caucho.hessian.server.HessianServlet;
import com.mk.framework.AppUtils;
import com.mk.ots.annotation.HessianService;

public class HessianDispatcherServlet extends HessianServlet {

	/**
     *
     */
	private static final long serialVersionUID = 7278887184914959024L;

	/**
	 * 定义一个 Hessian Servlet Map，用于管理 Hessian URL 与 Hessian Servlet 之间的映射关系
	 *
	 */
	private final static Map<String, HessianServlet> hessianServletMap = new HashMap<String, HessianServlet>();

	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			super.init(config);
			// 获取所有标注了 @HessianService 注解的类（接口）
			Map<String, Object> hessianInterfaceMaps = AppUtils.getApplicationContext().getBeansWithAnnotation(HessianService.class);
			if (hessianInterfaceMaps == null) {
				return;
			}
			Iterator<String> ite = hessianInterfaceMaps.keySet().iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				Object hessianObj = hessianInterfaceMaps.get(key);
				Class<?> serviceClass = AopUtils.getTargetClass(hessianObj);
				HessianService servceAnnotation = serviceClass.getAnnotation(HessianService.class);
				if (servceAnnotation == null) {
					continue;
				}
				String url = servceAnnotation.value();
				Class<?> hessianInterface = servceAnnotation.implmentInterface();

				// 创建 Hessian Servlet
				HessianServlet hessianServlet = new HessianServlet();
				hessianServlet.setHomeAPI(hessianInterface); // 设置接口
				hessianServlet.setHome(hessianObj); // 设置实现类实例
				hessianServlet.init(config); // 初始化 Servlet
				// 将 Hessian URL 与 Hessian Servlet 放入 Hessian Servlet Map 中
				HessianDispatcherServlet.hessianServletMap.put("/hessian" + url, hessianServlet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void service(ServletRequest request, ServletResponse response) throws IOException, ServletException {
		// 获取请求 URL
		HttpServletRequest req = (HttpServletRequest) request;
		String url = HessianDispatcherServlet.getRequestPath(req);
		// 从 Hessian Servlet Map 中获取 Hessian Servlet
		HessianServlet hessianServlet = HessianDispatcherServlet.hessianServletMap.get(url);
		if (hessianServlet != null) {
			// 执行 Servlet
			hessianServlet.service(request, response);
		}
	}

	public static HessianServlet getHessianServlet(String service) {
		if (HessianDispatcherServlet.hessianServletMap.containsKey("/hessian" + service)) {
			return HessianDispatcherServlet.hessianServletMap.get("/hessian" + service);
		}
		return null;
	}

	/**
	 * 获取请求路径
	 */
	private static String getRequestPath(HttpServletRequest request) {
		String servletPath = request.getServletPath();
		String pathInfo = StringUtils.defaultIfEmpty(request.getPathInfo(), "");
		return servletPath + pathInfo;
	}
}
