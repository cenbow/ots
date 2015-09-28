package com.mk.ots.comp;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SynOrderBootstrap implements InitializingBean {

	@Autowired
	private SynOrderConf synOrderConf = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = new ObjectName("MeanSynOrder:name=SynOrderConf");
		if (mBeanServer.isRegistered(name)) {
			mBeanServer.unregisterMBean(name);
		}
		mBeanServer.registerMBean(this.getSynOrderConf(), name);
	}

	public SynOrderConf getSynOrderConf() {
		return this.synOrderConf;
	}

}
