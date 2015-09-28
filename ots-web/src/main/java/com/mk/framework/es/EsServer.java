package com.mk.framework.es;

import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.mk.sever.Server;

@Service
@PropertySource("classpath:synserver.properties")
public class EsServer implements InitializingBean {

	private Logger logger = org.slf4j.LoggerFactory.getLogger(EsServer.class);

	@Value("${synserver.ip}")
	private String ip = null;

	@Value("${synserver.port}")
	private String port = null;

	@Override
	public void afterPropertiesSet() throws Exception {
		new Thread(new Server(this.getIp(), Integer.valueOf(this.getPort()).intValue())).start();
		this.logger.info("connect synserver {}:{}", this.getIp(), this.getPort());
	}

	private String getIp() {
		return this.ip;
	}

	private String getPort() {
		return this.port;
	}

}
