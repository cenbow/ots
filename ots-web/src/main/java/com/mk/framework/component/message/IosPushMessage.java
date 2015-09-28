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
import com.baidu.yun.push.model.PushMsgToAllRequest;
import com.baidu.yun.push.model.PushMsgToAllResponse;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import com.baidu.yun.push.model.PushRequest;
import com.baidu.yun.push.model.PushResponse;
import com.mk.framework.util.Config;

/**
 * 
 * @author chuaiqing.
 *
 */
public class IosPushMessage extends AbstractMessage {
    private static final Logger logger = LoggerFactory.getLogger(IosPushMessage.class);
  
    private BaiduPushClient pushClient = null;
    
    /**
     * constructor
     */
    public IosPushMessage() {
        this("bbdnxkzQcU94eByfrGqeQQCQ", "8T9BkBBXuTnwGfeZ331fxMpxGQYH7FaK");
    }
    
    /**
     * constructor
     * @param apiKey
     * @param secretKey
     */
    public IosPushMessage(String apiKey, String secretKey) {
        try {
            // 1. get apiKey and secretKey from developer console
            PushKeyPair pair = new PushKeyPair(apiKey, secretKey);

            // 2. build a BaidupushClient object to access released interfaces
            pushClient = new BaiduPushClient(pair,  BaiduPushConstants.CHANNEL_REST_URL);

            // 3. register a YunLogHandler to get detail interacting information in this request.
            pushClient.setChannelLogHandler(new YunLogHandler() {
                @Override
                public void onHandle(YunLogEvent event) {
                    System.out.println(event.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("create error:\n" + e.getMessage());
        }
    }

    @Override
    public boolean send() throws Exception {
    	boolean isRtnFlag = false;
        try {
            // 4. specify request arguments
            // make IOS Notification
            JSONObject notification = new JSONObject();
            JSONObject jsonAPS = new JSONObject();
            jsonAPS.put("alert", super.getTitle());
            jsonAPS.put("sound", "ttt"); // 设置通知铃声样式，例如"ttt"，用户自定义。
            notification.put("aps", jsonAPS);
            notification.put("text", super.getContent());
            notification.put("url", super.getUrl());
            
            if(super.getReceivers()!=null && super.getReceivers().length>0){
            	PushRequest request = null;
            	PushResponse response = null;
            	if(super.getReceivers().length == 1){
            		 request = new PushMsgToSingleDeviceRequest()
                     	.addChannelId(super.getReceiver())
                     	.addMsgExpires(new Integer(3600)) // 设置message的有效时间
                     	.addMessageType(1) // 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                     	.addMessage(notification.toString())
                     	.addDeployStatus(Integer.valueOf(Config.getValue("baidumessage.DeployStatus"))) // IOS,DeployStatus => 1: Developer 2: Production.
                     	.addDeviceType(4); // deviceType => 3:android, 4:ios
            		 
            		 response = pushClient.pushMsgToSingleDevice((PushMsgToSingleDeviceRequest) request);
            	}else if(super.getReceivers().length > 1){
            		 request = new PushBatchUniMsgRequest()
                     	.addChannelIds(super.getReceivers())
                     	.addMsgExpires(new Integer(3600)) // 设置message的有效时间
                     	.addMessageType(1) // 1：通知,0:透传消息.默认为0 注：IOS只有通知.
                     	.addMessage(notification.toString())
                     .addDeviceType(4); // deviceType => 3:android, 4:ios
            		 
            		 response = pushClient.pushBatchUniMsg((PushBatchUniMsgRequest) request);
            	}
            	isRtnFlag = true;
            	logger.info("ostype: ios, msgid: {}, sendtime:{}>>>result:{}", ((PushMsgToSingleDeviceResponse)response).getMsgId(), ((PushMsgToSingleDeviceResponse)response).getSendTime());
            }
            /**
        	 * 去掉广播功能
            else{
            	// 未设置推送设备
            	//isRtnFlag = false;
                logger.error("未指定消息接收者,则为广播");
                PushMsgToAllRequest  request= new PushMsgToAllRequest()
             	.addMsgExpires(new Integer(3600)) // 设置message的有效时间
             	.addMessageType(1) // 1：通知,0:透传消息.默认为0 注：IOS只有通知.
             	.addMessage(notification.toString())
	             .addDeviceType(4); // deviceType => 3:android, 4:ios
	    		 
	    		 PushMsgToAllResponse  response = pushClient.pushMsgToAll(request);
	
		    	isRtnFlag = true;
		    	logger.info("ostype: ios, msgid: {}, sendtime:{}>>>result:{}", response.getMsgId(), response.getSendTime());
		    	
            }
            */
        } catch (PushClientException e) {
        	isRtnFlag = false;
            if (BaiduPushConstants.ERROROPTTYPE) {
                throw e;
            } else {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        } catch (PushServerException e) {
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
