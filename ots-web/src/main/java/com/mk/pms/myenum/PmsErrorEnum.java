package com.mk.pms.myenum;

import com.mk.pms.exception.PmsException;

/** 
 * 
 * @author shellingford
 * @version 2014年10月30日
 */

public enum PmsErrorEnum {
	loginError("100","接口账户登录错误"),
	needLogin("101","该接口需要先进行登录"),
	noCmdError("102","服务器端不支持该命令"),
	noData("103","空数据异常"),
	noPmsHotel("104","该酒店不存在pms标识"),
	noHotel("105","不存在该酒店"),
	offLine("106","该酒店目前不在线"),
	pramError("107","参数错误"),
	checkHotelPmsHotelNameError("108","匹配酒店pms号和名称失败"),
	noknowError("99998","未知错误"),
	serviceError("99999","其他服务器问题."),
	;
	
	public PmsException getException(){
		return new PmsException(errorCode, errorMessage);
	}
	
	public static PmsErrorEnum findPmsErrorEnumByCode(String code){
		for (PmsErrorEnum pee : PmsErrorEnum.values()) {
			if(pee.getErrorCode().equals(code)){
				return pee;
			}
		}
		return PmsErrorEnum.noknowError;
	} 
	
	private PmsErrorEnum(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	private final String errorCode;
	private final String errorMessage;
	public String getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	
//	public static void main(String[] args) throws Exception {
//		Element root=new Element("root");
//		root.setAttribute("aaa", PmsErrorEnum.serviceError.getException().getErrorMessage());
//		Document doc=new Document(root);
//		XMLUtils.XMLtoFile(doc, new File("d:/temp/001.xml"));
//		byte bb[]=XMLUtils.XMLtoShortString(doc).getBytes(PmsMinaManager.CHARSET);
//		File file=new File("d:/temp/002.xml");
//		FileOutputStream out=new FileOutputStream(file);
//		out.write(bb);
//		out.close();
//		System.out.println(XMLUtils.XMLtoShortString(doc));
//	}
}
