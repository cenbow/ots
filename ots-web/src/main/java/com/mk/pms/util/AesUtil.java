package com.mk.pms.util;
/*
定义加密算法AES、算法模式ECB、补码方式PKCS5Padding加密结果编码十六进制
身份证加密前要求身份证号去掉了3，6，9位
*/
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
public class AesUtil {
	
	public final static String password="imike";
	
	public static String encrypt(String content){
		byte[] encryptResult = encrypt(content, password,16); 
		return parseByte2HexStr(encryptResult);
	}
	
	public static String decrypt(String content) throws UnsupportedEncodingException{
		byte[] encryptResult=parseHexStr2Byte(content);
		byte[] decryptResult = decrypt(encryptResult,password,16);
		return  new String(decryptResult,"UTF-8");
	}
	
	public static void main(String args[]) throws UnsupportedEncodingException{

//        //加密内容
//        String content = "asdfghjklqwertyuioopasdfghjklp";
//        //密钥
//        String password = "imike";
//        //加密
//        System.out.println("加密前：" + content);
//        byte[] encryptResult = encrypt(content, password,16); //密钥长度128位
//        System.out.println("加密后：" + parseByte2HexStr(encryptResult));
//        //解密
//        byte[] decryptResult = decrypt(encryptResult,password,16);
//        System.out.println("解密后：" + new String(decryptResult));
//        System.out.println("KeyGenerator 结果：");
        System.out.println(decrypt("FCD6112CE852645C24572F65862A7D2A35FA0492F9A08AA905EDFAFE0DA249B90868256BCDB875259B9FF53FB9F26EC7E37B2C96E83184014490BA5A7247D9F23DE423711FD07CCAD5F3C11B2BE6442E586A007D5D3931D063469FF544128275DEB0EE9554E3958077CA34457AA7B004712395161C91B6A46AD682AD8687483B94AA6CF532D0F3A5DAF20CF01F5965164BA7E09A15C60462A00DCD12327A4AB54FEC54237F453100CC4E732192B3E1FB7EBE92B010341BC4CE436121D2A87E245E4223FF96A05FD7023575E79FF60651C774B8130533FEA52B5588B0B41E23CE0898563B447D9CADF8C15F30F0783DDEB5F8E94208D49D14595707DCBC6BD61FE80F4968D8504C7F2F82DC71469030899CEC4611AFF0C80474E5BDCF22027871ABD1C5DEF3DDB1FB79276C9502F3C755BB3C065483E0B59D34A7C2B6F774B58013442F4A616ED786B219F5C860ABBC4DC31F215F4B3CD2786EBBE3DACBA7C6C3889D54EC7C309E2297B53BC111D37B2ABA9829E4F0EA50F212F747110CD6A6D6C8F2DDD6D4319A6D20906691A21DD3DFFA6F4BA014D658A1B71F3FA7B4D61B9732628AF07A35429AC5F484B0197316DC0E0C27FFD7163C5AC23A6CF76292942DA51938967C8F1BEEAA5124FF3E029647FD80F21088E3EDD082246D0DCAF084FEAD64A9C08487D064F8F64ADE12B99A86021F5D9907AFDD907B5056819B4EB34509AB1633260E00112DB65DD6070B887B955F8CD50E2CCAD6569594429BFA65F6B444F698D6CFF9AD9BC6849E2FF9B79C144CE2C0EC21AD8075C67B9C3B505375E0A6181A86DC319A3BEFAF096CADF646361AC07DA58EAC86A7B929212FB939C383C414CA6CAA33640FADD66F249D25951E365E4A7C49B8DA1410C478BF28119A0CFD1C35DF02B17A25C434D56CA7F42C"));
    
        
        
	}
	
    /**
     * 加密
     * 
     * @param content 需要加密的内容
     * @param password  加密密码
     * @param keySize 密钥长度16,24,32
     * @return
     */
    public static byte[] encrypt(String content, String password, int keySize) {
            try {                              
        		SecretKeySpec key = new SecretKeySpec(ZeroPadding(password.getBytes(), keySize), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 定义加密算法AES、算法模式ECB、补码方式PKCS5Padding
                byte[] byteContent = content.getBytes();
                cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
                byte[] result = cipher.doFinal(byteContent);
                return result; // 加密
            } catch (Exception e) {
            	throw new RuntimeException(e);
            }
    }
    
    /**解密
     * @param content  待解密内容
     * @param password 解密密钥
     * @param keySize 密钥长度16,24,32
     * @return
     */
    public static byte[] decrypt(byte[] content, String password, int keySize) {
            try { 
        		SecretKeySpec key = new SecretKeySpec(ZeroPadding(password.getBytes(), keySize), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 定义加密算法AES、算法模式ECB、补码方式PKCS5Padding
                cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
                byte[] result = cipher.doFinal(content);
                return result; // 加密
            } catch (Exception e) {
            	throw new RuntimeException(e);
            }
    }
    
    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
                String hex = Integer.toHexString(buf[i] & 0xFF);
                if (hex.length() == 1) {
                        hex = '0' + hex;
                }
                sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }
    
    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
                return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
                int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
                int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
                result[i] = (byte) (high * 16 + low);
        }
        return result;
    }
    
    public static byte[] ZeroPadding(byte[] in,Integer blockSize){
    	Integer copyLen = in.length;
    	if (copyLen > blockSize) {
			copyLen = blockSize;
		}
    	byte[] out = new byte[blockSize];
    	System.arraycopy(in, 0, out, 0, copyLen);
    	return out;
    }
}
