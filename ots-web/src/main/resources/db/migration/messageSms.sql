-- 创建运营商表
CREATE TABLE `u_message_provider` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `providername` varchar(255) NOT NULL,
  `weight` bigint(20) NOT NULL,
  `providerclass` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 ;

-- 插入运营商
INSERT INTO `u_message_provider` (`providername`, `weight`, `providerclass`)
VALUES ('sms', '3', 'com.mk.framework.component.message.VoiceMessage,com.mk.framework.component.message.SmsMessage'),
  ('yuntongxun', '7',
   'com.mk.framework.component.message.YunSmsMessage,com.mk.framework.component.message.YunVoiceMessage')
