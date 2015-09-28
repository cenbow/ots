package com.mk.server.processer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;

import com.mk.sever.DirectiveSet;
import com.mk.sever.ServerChannel;
import com.mk.synserver.DirectiveData;

/**
 * 接受同步服务器身份认证处理.
 *
 * @author zhaoshb
 *
 */
public class RequestIdentiferProcessor extends AbstractProcessor {
	
	private Logger logger = LoggerFactory.getLogger(RequestIdentiferProcessor.class);


	private static final String INENTIFER = "identifer";

	private static final String SERVER = "server";

	@Override
	protected void handle(Channel channel, DirectiveData dd) {
		this.setGloablSessionId(dd);
		channel.writeAndFlush(this.createRequestIdentiferDD());
	}

	private void setGloablSessionId(DirectiveData dd) {
		logger.info("OTSMessage::dd::"+dd.toString());
		String sessionId = (String) this.getValue(dd, AbstractProcessor.SESSION_ID);
		logger.info("OTSMessage::setGloablSessionId::sessionId"+sessionId+",dd"+dd);
		AbstractProcessor.setSessionId(sessionId);
		logger.info("OTSMessage::setGloablSessionId::OK");
	}

	private DirectiveData createRequestIdentiferDD() {
		DirectiveData dd = this.createDirectiveData(DirectiveSet.DIRECTIVE_SEND_IDENTIFER);
		this.addData(dd, RequestIdentiferProcessor.INENTIFER, RequestIdentiferProcessor.SERVER);

		return dd;
	}

}
