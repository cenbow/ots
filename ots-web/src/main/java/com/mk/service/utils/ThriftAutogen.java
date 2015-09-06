package com.mk.service.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ThriftAutogen {
	
	public static String appPath=Thread.currentThread().getContextClassLoader().getResource("").getFile().toString();
	public static String thriftName="thrift-0.9.2.exe";
	public static String thriftExeName=appPath+"thrift/"+thriftName;
	
	/*
	 *调用thriftExe 生成service文件
	 * 
	 */
	public static void gen(String thriftFile,String language){
		if(thriftFile==null || thriftFile.isEmpty()){
			return;
		}
		if(language==null || language.isEmpty()){
			language="java";
		}	
		String cmdStr= thriftExeName+" -r -out src\\main\\java --gen "+language+" "+ thriftFile;
		boolean isSuccess=exe(cmdStr);
	}

	public static boolean exe(String cmdStr) {
		Runtime rn = Runtime.getRuntime();
		try {
			Process	p = rn.exec(cmdStr);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
		
	public static boolean copy(String fileFrom, String fileTo) {  
        try {  
            FileInputStream in = new java.io.FileInputStream(fileFrom);  
            FileOutputStream out = new FileOutputStream(fileTo);  
            byte[] bt = new byte[1024];  
            int count;  
            while ((count = in.read(bt)) > 0) {  
                out.write(bt, 0, count);  
            }  
            in.close();  
            out.close();  
            return true;  
        } catch (IOException ex) {  
            return false;  
        }  
    }  
	
	public static void main(String[] args) {
		String thriftFileName="mk.thrift";
		String thriftFile=appPath.substring(1)+"thrift/"+thriftFileName;
		gen(thriftFile,null);
	}
}
