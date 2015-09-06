package com.mk.framework;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.mk.orm.DbTable;
import com.mk.orm.plugin.bean.Bean;
import com.mk.orm.plugin.bean.CaseInsensitiveContainerFactory;
import com.mk.orm.plugin.bean.Db;
import com.mk.orm.plugin.bean.Model;
import com.mk.ots.manager.SysConfigManager;
import com.mk.sever.DirectiveProcessFactory;
import com.mk.sever.IDirectiveProcessor;
import com.mk.sever.ServerChannel;

public class AppUtils implements ApplicationContextAware {

	/** 是否开启开发模式 */
	public static final boolean DEBUG_MODE = true;
	/** 是否使用酒店缓存 */
	public static final boolean HOTEL_CACHE_USED = true;

	private Logger logger = LoggerFactory.getLogger(AppUtils.class);

	private static ApplicationContext applicationContext = null;

	public static ApplicationContext getApplicationContext() {
		return AppUtils.applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		AppUtils.applicationContext = applicationContext;
		
		String[] beanNames = AppUtils.applicationContext.getBeanNamesForType(IDirectiveProcessor.class);
		if (beanNames != null) {
			for (String beanName : beanNames) {
				IDirectiveProcessor idp = (IDirectiveProcessor) AppUtils.applicationContext.getBean(beanName);
				if (idp != null) {
					DirectiveProcessFactory.pushProcessor(idp);
				}
			}
		}

		// start Bean Plugin
		try {
			this.startBeanPlugin();
		} catch (Exception e) {
			this.logger.error("start Bean Plugin error:\n" + e.getMessage());
		}

		// init system config
		try {
			this.initSysConfig();
		} catch (Exception e) {
			this.logger.error("init system config error:\n" + e.getMessage());
		}

		// sync hotel keywords
		this.syncEsKeywords();
	}

	public static Map<String, Object> getBeanWithServiceAnnotaition() {
		return AppUtils.getBeansWithAnnotation(Service.class);
	}

	public static Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
		return AppUtils.applicationContext.getBeansWithAnnotation(annotationType);
	}

	public static <T> T getBean(Class<T> requiredType) {
		return AppUtils.applicationContext.getBean(requiredType);
	}

	public static Object getBean(String serviceid) {
		return AppUtils.getApplicationContext().getBean(serviceid);
	}

	/**
	 * start BeanPlugin
	 *
	 * @throws TTransportException
	 */
	private void startBeanPlugin() throws TTransportException {
		try {
			com.mk.orm.plugin.bean.BeanPlugin beanplugin = new com.mk.orm.plugin.bean.BeanPlugin((javax.sql.DataSource) AppUtils.getBean("dataSource"));
			// 配置大小写不敏感
			beanplugin.setContainerFactory(new CaseInsensitiveContainerFactory(true));
			Map<String, Object> map = AppUtils.getApplicationContext().getBeansWithAnnotation(DbTable.class);
			Iterator<String> ite = map.keySet().iterator();
			while (ite.hasNext()) {
				String key = ite.next();
				Object obj = map.get(key);
				DbTable dbTable = obj.getClass().getAnnotation(DbTable.class);
				String tableName = dbTable.name();
				String pkey = dbTable.pkey();
				Class<? extends Model<?>> modelClass = (Class<? extends Model<?>>) obj.getClass();
				beanplugin.addMapping(tableName, pkey, modelClass);
			}
			beanplugin.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * init system config.
	 */
	private void initSysConfig() {
		try {
			SysConfigManager.getInstance().loadSysConfig();
		} catch (Exception e) {
			this.logger.error("init system configuration cache data is error:\n {}", e.getMessage());
		}
	}

	/**
	 * sync es hotel keywords.
	 */
	private void syncEsKeywords() {
		// 同步酒店关键词库到es
		List<Bean> keywords = new ArrayList<Bean>();
		try {
			keywords = Db.find("select * from h_keyword");
			List<String> keywordList = new ArrayList<String>();
			for (Bean keyword : keywords) {
				keywordList.add(keyword.getStr("name"));
				ServerChannel.batchSendKeyword(keywordList);
			}
		} catch (Exception e) {
			this.logger.error("syncEsKeywords method error: {}", e.getMessage());
		}
	}
}
