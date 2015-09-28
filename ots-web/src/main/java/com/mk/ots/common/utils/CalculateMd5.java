package com.mk.ots.common.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CalculateMd5 {
	
	public static String caculateCF(String value,String charset) {
		try {
			ByteArrayInputStream bis=new ByteArrayInputStream(value.getBytes(charset));
			String str=caculateCf(bis);
			bis.close();
			return str;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
//	public static String caculateCf(File file) throws NoSuchAlgorithmException, IOException{
//		String result = "";
//		FileInputStream in = new FileInputStream(file);
//		try {
//			byte[] readBytes = new byte[1024 * 500];
//			byte[] md5Bytes = null;
//			int readCount;
//			MessageDigest md5 = MessageDigest.getInstance("MD5");
//			while ((readCount = in.read(readBytes)) != -1) {
//				md5.update(readBytes, 0, readCount);
//			}
//			md5Bytes = md5.digest();
//			result = toHexString(md5Bytes);
//		} finally {
//			IOUtils.closeQuietly(in);
//		}
//		return result;
//	}
	
	public static String caculateCf(InputStream in) throws NoSuchAlgorithmException, IOException{
		byte[] readBytes = new byte[1024 * 500];
		byte[] md5Bytes = null;
		int readCount;
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		while ((readCount = in.read(readBytes)) != -1) {
			md5.update(readBytes, 0, readCount);
		}
		md5Bytes = md5.digest();
		String result = toHexString(md5Bytes);
		return result;
	}
	
	public final static String toHexString(byte[] res) {
		StringBuffer sb = new StringBuffer(res.length << 1);
		for (int i = 0; i < res.length; i++) {
			String digit = Integer.toHexString(0xFF & res[i]);
			if (digit.length() == 1) {
				digit = '0' + digit;
			}
			sb.append(digit);
		}
		return sb.toString().toUpperCase();
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		System.out.println(caculateCF("test","UTF-8"));
	}
}

