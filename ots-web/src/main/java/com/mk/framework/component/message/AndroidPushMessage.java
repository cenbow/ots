package com.mk.framework.component.message;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.baidu.yun.core.log.YunLogEvent;
import com.baidu.yun.core.log.YunLogHandler;
import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.constants.BaiduPushConstants;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.PushBatchUniMsgRequest;
import com.baidu.yun.push.model.PushBatchUniMsgResponse;
import com.baidu.yun.push.model.PushMsgToAllRequest;
import com.baidu.yun.push.model.PushMsgToAllResponse;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import com.baidu.yun.push.model.PushRequest;
import com.baidu.yun.push.model.PushResponse;

/**
 * 
 * @author chuaiqing.
 *
 */
public class AndroidPushMessage extends AbstractMessage {
    
    private static final Logger logger = LoggerFactory.getLogger(AndroidPushMessage.class);
    
    private BaiduPushClient pushClient = null;
    
    /**
     * constructor
     */
    public AndroidPushMessage() {
        this("PSxPV12Nv0xgjwV87Eg3I7Ev", "VFvkrxB0EMwqTKGslmExoIohu4wDxIcb");
    }
    
    /**
     * constructor
     */
    public AndroidPushMessage(String apiKey, String secretKey) {
        try {
            // 1. get apiKey and secretKey from developer console
            PushKeyPair pair = new PushKeyPair(apiKey, secretKey);

            // 2. build a BaidupushClient object to access released interfaces
            pushClient = new BaiduPushClient(pair, BaiduPushConstants.CHANNEL_REST_URL);

            // 3. register a YunLogHandler to get detail interacting information in this request.
            pushClient.setChannelLogHandler(new YunLogHandler() {
                @Override
                public void onHandle(YunLogEvent event) {
                    System.out.println(event.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("构建AndroidPushMessage出错:\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 
     */
    @Override
    public boolean send() throws Exception {
    	boolean isRtnFlag = false;
        try {
            JSONObject notification = new JSONObject();
            notification.put("title", super.getTitle());
            notification.put("description", super.getContent());
            notification.put("notification_builder_id", 0);
            notification.put("notification_basic_style", 4);
            notification.put("open_type", 3);
            //notification.put("url", "http://www.imike.com");
            JSONObject custom_content = new JSONObject();
            custom_content.put("url", super.getUrl());
            notification.put("custom_content", custom_content);
            
            
            if(super.getReceivers()!=null && super.getReceivers().length>0){
            	PushRequest request;
                PushResponse response;
            	if(super.getReceivers().length == 1){
            		request = new PushMsgToSingleDeviceRequest().addChannelId(super.getReceiver())
                            .addMsgExpires(new Integer(3600)) // message有效时间
                            .addMessageType(1) // 1：通知,0:透传消息. 默认为0 注：IOS只有通知.
                            .addMessage(notification.toString())
                            .addDeviceType(3); // deviceType => 3:android, 4:ios
                    response = pushClient.pushMsgToSingleDevice((PushMsgToSingleDeviceRequest) request);
                    logger.info("msgId: " + ((PushMsgToSingleDeviceResponse) response).getMsgId() + ",sendTime: " + ((PushMsgToSingleDeviceResponse) response).getSendTime());
            	}else if(super.getReceivers().length > 1){
            		request = new PushBatchUniMsgRequest().addChannelIds(super.getReceivers())
                            .addMsgExpires(new Integer(3600))
                            .addMessageType(1)
                            .addMessage(notification.toString())
                            .addDeviceType(3)
                            .addTopicId("OtsPush"); // 设置类别主题
                    response = pushClient.pushBatchUniMsg((PushBatchUniMsgRequest) request);
                	logger.info("msgId: " + ((PushBatchUniMsgResponse) response).getMsgId() + ",sendTime: " + ((PushBatchUniMsgResponse) response).getSendTime());
            	}
            	isRtnFlag = true;
			} else {
				// 未设置推送设备
				isRtnFlag = false;
				logger.error("未指定消息接收者.则为广播消息");
				PushMsgToAllRequest request = new PushMsgToAllRequest()
						.addMsgExpires(new Integer(3600)).addMessageType(1)
						.addMessage(notification.toString()).addDeviceType(3);
				PushMsgToAllResponse response = pushClient.pushMsgToAll(request);
				logger.info("msgId: " + response.getMsgId() + ",sendTime: "
						+ response.getSendTime());
				isRtnFlag = true;
			}
        } catch (PushClientException e) {
        	logger.error("send msg error.", e);
        	isRtnFlag = false;
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                e.printStackTrace();
            }
        } catch (PushServerException e) {
        	logger.error("send msg error.", e);
        	isRtnFlag = false;
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                logger.error(String.format("requestId: %d, errorCode: %d, errorMessage: %s",
                        e.getRequestId(), e.getErrorCode(), e.getErrorMsg()));
            }
        }
        return isRtnFlag;
    }

}
