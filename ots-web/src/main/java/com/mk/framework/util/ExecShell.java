package com.mk.framework.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

public class ExecShell {

	/**
	 * Return the result of the command stdout.
	 * 
	 * <br/>
	 * 
	 * @param string
	 * @return)
	 * @throws Exception
	 */
	public static String getExecCmdString(String string) throws Exception {
		StringBuffer result = new StringBuffer();
		BufferedReader reader = null;
		try {
			String tmp = "";
			reader = getExecCmdReader(string);
			while ((tmp = reader.readLine())!=null) {
				result.append(tmp);
				result.append(System.getProperty("line.separator"));
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
				reader = null;
			}
		}
		return result.toString();
	}

	/**
	 * Always remember to release the resource --- close the reader in a finally
	 * block.
	 * 
	 * <br/>
	 * 
	 * @param string
	 * @return
	 */
	public static BufferedReader getExecCmdReader(String string) {
		BufferedReader reader = null;
		try {
			String[] exeStrings = getExeStrings();
			exeStrings[2] = string;
			Process process = Runtime.getRuntime().exec(exeStrings);
			reader = new BufferedReader(new InputStreamReader(
					process.getErrorStream()));
			String line = "";
			reader = new BufferedReader(new InputStreamReader(
					process.getInputStream()));
			process.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reader;
	}

	/**
	 * Always remember to release the resource --- close the reader in a finally
	 * block.
	 * 
	 * <br/>
	 * 
	 * @param string
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static int execCmd(String string) throws InterruptedException,
			IOException {
		String[] exeStrings = getExeStrings();
		exeStrings[2] = string;
		Process process = Runtime.getRuntime().exec(exeStrings);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		String line = "";
		while ((line = reader.readLine()) != null) {
			System.out.print(line);
		}
		reader.close();
		int waitFor = process.waitFor();
		return waitFor;
	}

	/**
	 * Prepare for the executions.
	 * 
	 * <br/>
	 * 
	 * @return
	 */
	public static String[] getExeStrings() {
		String ostype = System.getProperty("os.name").toLowerCase();
		if (ostype.indexOf("window")!=-1) {
			return new String[] { "cmd", "/c", "" };
		} else {
			return new String[] { "sh", "-c", "" };
		}
	}
	
	public static void main(String[] args) throws Exception {
		String project_dir = System.getProperty("user.dir") + File.separator;
		String outpath = project_dir+"src/main/java";
		String language = "java";
		String tpl_define = project_dir+"src/main/resource/thrift/mk.thrift";
		String cmd = MessageFormat.format("thrift -r -out {0} --gen {1} {2}", new String[]{outpath, language, tpl_define});
		System.out.println(cmd);
		System.out.println(ExecShell.getExecCmdString(cmd));;
	}
}