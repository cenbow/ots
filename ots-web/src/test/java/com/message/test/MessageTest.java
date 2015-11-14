package com.message.test;

import com.mk.framework.component.message.TongSmsMessage;

public class MessageTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//sms message test
//		YunSmsMessage sms=new YunSmsMessage("sandboxapp.cloopen.com","8883","8a48b5514e5298b9014e53712ba601fe","b52279b85f6c4f2b940311d91e6e30e6","aaf98f894e52805a014e5372a6220221","1");
//		sms.setMobile("18701286940");
//		sms.setData(new String[]{});
//		sms.send();

        TongSmsMessage smsMessage=new TongSmsMessage();
        smsMessage.setMobiles("18616886963") ;
        smsMessage.setContent("test");
        smsMessage.setMsgId(1L);
        smsMessage.send();

		/*YunVoiceMessage yvm=new YunVoiceMessage("sandboxapp.cloopen.com","8883","8a48b5514e5298b9014e53712ba601fe","b52279b85f6c4f2b940311d91e6e30e6","aaf98f894e52805a014e5372a6220221");
		yvm.setMobile("18701286940");
		yvm.setVerifyCode("34567890");
		yvm.setPlayTimes("2");
		yvm.send();*/
		
		/*IMessageProviderDao ipd=new MessageProviderDao();
		List<MessageProvider> tempList= ipd.queryAllProviders();
		for (MessageProvider messageProvider : tempList) {
			System.out.println(messageProvider.getProvidename()+" : "+messageProvider.getWeight());
		}*/
	}

}
