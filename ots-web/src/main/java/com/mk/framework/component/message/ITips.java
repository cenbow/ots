package com.mk.framework.component.message;
 
public abstract interface ITips {
    public static final String PUSH_TYPE_SINGLE = "1";
    public static final String PUSH_TYPE_MULTY = "2";
    public static final String PUSH_TYPE_USERGROUP = "3";
    
    /**
     * 设置消息编码
     * @param msgid
     * @return
     */
    public abstract ITips setMsgId(Long msgid);
    
    /**
     * 设置消息标题
     * @param title
     * @return
     */
    public abstract ITips setTitle(String title);
    
    /**
     * 设置消息内容
     * @param content
     * @return
     */
    public abstract ITips setContent(String content);
    
    /**
     * 设置消息类型
     * @param msgtype
     * @return
     */
    public abstract ITips setMsgtype(String msgtype);
    
    /**
     * 设置接收人
     * @param receiver
     * @return
     */
    public abstract ITips setReceivers(String receiver);
    
    /**
     * 设置通知跳转地址
     * @param url
     * @return
     */
    public abstract ITips setUrl(String url);
    /**
     * 发送消息
     * @return
     * @throws Exception
     */
    public abstract boolean send() throws Exception;

}
