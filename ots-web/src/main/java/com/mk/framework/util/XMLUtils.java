package com.mk.framework.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLUtils {
	
	public static final String DEFAULTENCODING = "utf-8";
	
	public static Document readXMLFromRequestBody(HttpServletRequest request) throws JDOMException, IOException{
		String str=readXMLStringFromRequestBody(request);
		if(str==null){
			return null;
		}else{
			return StringtoXML(str);
		}
	}
	
	public static String doString(String str){
		return str.replaceAll("\\\"", "\\\\\"");
	}
	
	public static void XMLtoFile(Document doc,File file) throws Exception{
		FileOutputStream out=null;
		try {
			out=new FileOutputStream(file);
			XMLOutputter outp = new XMLOutputter();//用于输出jdom 文档
			Format format=Format.getPrettyFormat(); //格式化文档
			format.setEncoding(DEFAULTENCODING); //由于默认的编码是utf-8,中文将显示为乱码，所以设为gbk
			outp.setFormat(format);
			outp.output(doc,out); //输出文档
		} catch (Exception e) {
			throw e;
		} finally{
			IOUtils.closeQuietly(out);
		}
		
	}

	public static String readXMLStringFromRequestBody(HttpServletRequest request) {
		StringBuffer xml = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = request.getReader();
			while ((line = reader.readLine()) != null) {
				xml.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return xml.toString();
	}

	public static Document StringtoXML(String xmlstring) throws JDOMException,
			IOException {
		SAXBuilder builder = new SAXBuilder(false);
		StringReader read = new StringReader(xmlstring);
		Document doc = builder.build(read);
		return doc;
	}

	public static String XMLtoString(Document doc) {
		return XMLtoString(doc, DEFAULTENCODING);
	}
	
	public static String XMLtoShortString(Document doc){
		return XMLtoShortString(doc, DEFAULTENCODING);
	}
	
	public static String XMLtoShortString(Document doc, String encoding) {
		if (encoding == null || encoding.trim().equals("")) {
			encoding = DEFAULTENCODING;
		}
		Format f = Format.getPrettyFormat();
		f.setEncoding(encoding);
		f.setIndent("");
		f.setLineSeparator("");
		XMLOutputter output = new XMLOutputter();
		output.setFormat(f);
		return output.outputString(doc);
	}

	public static String XMLtoString(Document doc, String encoding) {
		if (encoding == null || encoding.trim().equals("")) {
			encoding = DEFAULTENCODING;
		}
		Format f = Format.getPrettyFormat();
		f.setEncoding(encoding);
		f.setIndent("  ");
		f.setLineSeparator("\r\n");
		XMLOutputter output = new XMLOutputter();
		output.setFormat(f);
		return output.outputString(doc);
	}
	
	public static Document FileToXML(File file) throws JDOMException, IOException{
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(file);
		return doc;
	}

	public static Document InputStreamToXML(InputStream in)
			throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder(false);
		Document doc = builder.build(in);
		return doc;
	}
}
