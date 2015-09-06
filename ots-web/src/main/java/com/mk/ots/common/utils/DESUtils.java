package com.mk.ots.common.utils;

import java.security.Key;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESUtils {
	// static String nouse="5q8W2ra1SD6f79E";
	static String nouse = "5q8W2ra1";
	public static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";

	/**
	 * DES算法，加密 不使用
	 *
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	private static String encode(String key, String data) throws Exception {
		return DESUtils.encode(key, data.getBytes());
	}

	/**
	 * DES算法，加密，不使用
	 *
	 * @param data
	 *            待加密字符串
	 * @param key
	 *            加密私钥，长度不能够小于8位
	 * @return 加密后的字节数组，一般结合Base64编码使用
	 * @throws CryptException
	 *             异常
	 */
	private static String encode(String key2, byte[] data) throws Exception {
		try {
			// DESKeySpec dks = new DESKeySpec(key.getBytes());
			//
			// SecretKeyFactory keyFactory =
			// SecretKeyFactory.getInstance("DES");
			// //key的长度不能够小于8位字节
			// Key secretKey = keyFactory.generateSecret(dks);
			// Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			// IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			// AlgorithmParameterSpec paramSpec = iv;
			// cipher.init(Cipher.ENCRYPT_MODE, secretKey,paramSpec);
			//
			// byte[] bytes = cipher.doFinal(data);

			// return Base64.encodeToString(bytes, Base64.DEFAULT);

			IvParameterSpec zeroIv = new IvParameterSpec(DESUtils.iv);
			SecretKeySpec key = new SecretKeySpec(key2.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
			byte[] encryptedData = cipher.doFinal(data);

			return Base64.encodeToString(encryptedData, Base64.DEFAULT);
			// return Base64.encode(encryptedData);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	/**
	 * DES算法，解密
	 *
	 * @param data
	 *            待解密字符串
	 * @param key
	 *            解密私钥，长度不能够小于8位
	 * @return 解密后的字节数组
	 * @throws Exception
	 *             异常
	 */
	private static byte[] decode(String key, byte[] data) throws Exception {
		try {
			SecureRandom sr = new SecureRandom();
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			// key的长度不能够小于8位字节
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(DESUtils.ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return cipher.doFinal(data);
		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/***
	 * 新的解密方法，使用这个
	 *
	 * @param decryptString
	 * @param decryptKey
	 * @return
	 * @throws Exception
	 */
	public static String decryptDES(String encodedString) {
		// byte[] byteMi = Base64.decode(decryptString);
		try {
			byte[] byteMi = Base64.decode(encodedString, Base64.DEFAULT);
			IvParameterSpec zeroIv = new IvParameterSpec(DESUtils.iv);
			SecretKeySpec key = new SecretKeySpec(DESUtils.nouse.getBytes(), "DES");
			Cipher cipher;

			cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
			byte decryptedData[] = cipher.doFinal(byteMi);

			return new String(decryptedData, "UTF-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("encodedString:" + encodedString);
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * 获取编码后的值
	 *
	 * @param key
	 * @param data
	 * @return
	 * @throws Exception
	 */
	private static String decodeValue(String key, String data) {
		// byte[] datas;
		String value = null;
		try {
			// if(System.getProperty("os.name") != null &&
			// (System.getProperty("os.name").equalsIgnoreCase("sunos") ||
			// System.getProperty("os.name").equalsIgnoreCase("linux")))
			// {
			// datas = decode(key, Base64.decode(data, Base64.DEFAULT));
			//
			// }
			// else
			// {
			// datas = decode(key, Base64.decode(data, Base64.DEFAULT));
			// }

			// value = new String(datas);
			value = DESUtils.decryptDES(data);
		} catch (Exception e) {
			value = "";
		}
		return value;
	}

	public static String encode(String originalText) {
		try {
			return DESUtils.encode(DESUtils.nouse, originalText);
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}

	}

	private static String decode(String encodedText) {

		try {
			return DESUtils.decryptDES(encodedText);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// try
		// {
		// return decodeValue(nouse, encodedText);
		// } catch (Exception e)
		// {
		//
		// e.printStackTrace();
		// return null;
		// }

	}
}
