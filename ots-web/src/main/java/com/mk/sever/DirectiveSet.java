package com.mk.sever;

/**
 *
 * 指令集合.
 *
 * @author zhaoshb
 *
 */
public class DirectiveSet {

	/**
	 * 空指令.
	 */
	public static int DIRECTIVE_NULL = -1;

	/**
	 * 请求连接指令.
	 */
	public static int DIRECTIVE_CONNECT = 0;

	/**
	 * 请求身份指令.
	 */
	public static int DIRECTIVE_REQUEST_IDENTIER = 1;

	/**
	 * 发送身份指令.
	 */
	public static int DIRECTIVE_SEND_IDENTIFER = 2;

	/**
	 * 发送关键字指令.
	 */
	public static int DIRECTIVE_SEND_KEYWORD = 3;

	/**
	 *  接受关键字指令.
	 */
	public static int DIRECTIVE_RECEIVED_KEYWORD = 4;

	/**
	 * 预定酒店指令.
	 */
	public static int DIRECTIVE_BOOK_HOTEL = 5;

	/**
	 * 酒店注册指令.
	 */
	public static int DIRECTIVE_REGISTER_HOTEL = 6;

	/**
	 * 执行失败指令.
	 */
	public static int DIRECTIVE_BOOK_HOTEL_FAILED = 7;

	/**
	 * 批量发送关键字指令.
	 */
	public static int DIRECTIVE_BATCH_SEND_KEYWORD = 8;
	
	/**
	 * 创建订单通知指令.
	 */
	public static int DIRECTIVE_RECEIVED_ORDER = 9;

}
