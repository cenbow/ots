//package com.mk.framework.util;
//
//import com.warrenstrange.googleauth.CredentialRepositoryMock;
//import com.warrenstrange.googleauth.GoogleAuthenticator;
//import com.warrenstrange.googleauth.GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder;
//import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
//import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
//import com.warrenstrange.googleauth.KeyRepresentation;
//
//public class OTPUtils {
//
//	public static void main(String[] args) {
//		String secret = "I3BJKHW6HO77JRG3";
////		String url = genQRCode(secret, "www.imike.com", "nolan");
////		System.out.println(url);
////		
//		System.out.println(validate(secret, 150809));
////		System.out.println(validate(secret, 765090));
//	}
//	
//	
//	public static boolean validate(String secret, int verificationCode){
//		return instance().authorize(secret, verificationCode);
//	}
//	
//	private static GoogleAuthenticator instance(){
//		GoogleAuthenticatorConfigBuilder gacb = new GoogleAuthenticatorConfigBuilder()
//        .setKeyRepresentation(KeyRepresentation.BASE32);
//		return new GoogleAuthenticator(gacb.build());
//	}
//}
