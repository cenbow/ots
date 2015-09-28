//package com.mk.framework.component.message;
//
//import java.util.Date;
//
//import org.springframework.stereotype.Component;
//
//import com.mk.orm.DbTable;
//import com.mk.orm.plugin.bean.BizModel;
//
//@Component
//@DbTable(name="l_push_log")
//public class PushMessageLog extends BizModel<PushMessageLog> implements ILog {
//
//    /**
//     * 
//     */
//    private static final long serialVersionUID = 4543748128851441039L;
//    
////    private Long id;
////    private Date msgtime;
////    private String phone;
////    private String msgtype;
////    private String msgtitle;
////    private String msgcontent;
////    private String msgfrom;
////    private String msgip;
////    private String devicetype;
////    
////    public Long getId() {
////        return id;
////    }
////    public void setId(Long id) {
////        this.id = id;
////    }
////    public Date getMsgtime() {
////        return msgtime;
////    }
////    public void setMsgtime(Date msgtime) {
////        this.msgtime = msgtime;
////    }
////    public String getPhone() {
////        return phone;
////    }
////    public void setPhone(String phone) {
////        this.phone = phone;
////    }
////    public String getMsgtype() {
////        return msgtype;
////    }
////    public void setMsgtype(String msgtype) {
////        this.msgtype = msgtype;
////    }
////    public String getMsgtitle() {
////        return msgtitle;
////    }
////    public void setMsgtitle(String msgtitle) {
////        this.msgtitle = msgtitle;
////    }
////    public String getMsgcontent() {
////        return msgcontent;
////    }
////    public void setMsgcontent(String msgcontent) {
////        this.msgcontent = msgcontent;
////    }
////    public String getMsgfrom() {
////        return msgfrom;
////    }
////    public void setMsgfrom(String msgfrom) {
////        this.msgfrom = msgfrom;
////    }
////    public String getMsgip() {
////        return msgip;
////    }
////    public void setMsgip(String msgip) {
////        this.msgip = msgip;
////    }
////    public String getDevicetype() {
////        return devicetype;
////    }
////    public void setDevicetype(String devicetype) {
////        this.devicetype = devicetype;
////    }
//    
//    /**
//     * 
//     */
//    @Override
//    public void log() throws Exception {
//        this.save();
//    }
//
//}
