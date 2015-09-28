package com.mk.ots.order.model;

/**
 * 订单联系人
 * @author zhiwei
 *
 */
public class OrderContacts {
	//联系人
	private String Contacts;
	//联系电话
	private String ContactsPhone;
	//联系邮箱
	private String ContactsEmail;
	//联系人微信
	private String ContactsWeixin;
	
	public String getContacts() {
		return Contacts;
	}
	public void setContacts(String contacts) {
		Contacts = contacts;
	}
	public String getContactsPhone() {
		return ContactsPhone;
	}
	public void setContactsPhone(String contactsPhone) {
		ContactsPhone = contactsPhone;
	}
	public String getContactsEmail() {
		return ContactsEmail;
	}
	public void setContactsEmail(String contactsEmail) {
		ContactsEmail = contactsEmail;
	}
	public String getContactsWeixin() {
		return ContactsWeixin;
	}
	public void setContactsWeixin(String contactsWeixin) {
		ContactsWeixin = contactsWeixin;
	}
}
