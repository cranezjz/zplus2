package zplus2.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;

import zplus2.Log;

public class Helper {
	public static void main(String[] args) {
		// String a=
		// "/C:/work/ide/eclipse/../../java_workspace/eclipse_workspace/zplus/";
		System.out.println(getCurrentClassPath());

	}

	public static String getCurrentClassPath() {
		try {
			/**
			 * 下面的语句如果在“run as eclipse
			 * Application”中执行的结果是：/C:/work/ide/eclipse/../../java_workspace/
			 * eclipse_workspace/zplus/
			 * 如果在jar包中执行结果是：/C:/work/ide/eclipse/dropins/zplus_1.0.1.jar
			 * 故需要对"../"、"jar"人工处理
			 */
			String path = Helper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			path = changeRealPath(path);
			Log.debug("templateDir:"+path);
			return path;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 资源相对Helper.java类的路径
	 * @param resourceName
	 * @return 返回资源内容
	 */
	public static String readTemplateResource() {
		try {
			URL fileURL=Helper.class.getResource("/CodeMsg.ftl");     
	        Log.debug(fileURL.getFile());
	        InputStream is = Helper.class.getResourceAsStream("/CodeMsg.ftl");
	        return Helper.readStream(is,"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param path2
	 * @return
	 */

	private static String changeRealPath(String path2) {
		Stack<String> stack = new Stack<String>();
		String[] paths = path2.split("/");
		for (int i = 0; i < paths.length; i++) {
			if (paths[i].equals("..")) {
				stack.pop();
			} else {
				if ("".equals(paths[i])) {
					continue;
				}
				if (paths[i].endsWith(".jar")) {
					continue;
				}
				stack.push(paths[i]);
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("/");
		Iterator<String> it = stack.iterator();

		while (it.hasNext()) {
			sb.append(it.next()).append("/");
		}
		return sb.toString();
	}

	public static String readProperties(String key) {
		String path = Helper.getCurrentClassPath();
		File file = new File(path + "zplus.properties");
		if (!file.exists()) {
			createPropertiesFile(file);
		} else {
			try {
				Properties p = new Properties();
				FileInputStream in = new FileInputStream(file);
				p.load(in);
				String value = (String) p.get(key);
				in.close();
				return value;
			} catch (IOException e) {
			}
		}
		return "";
	}

	public static String writeProperties(String key, String value) {
		String path = Helper.getCurrentClassPath();
		File file = new File(path + "zplus.properties");
		if (!file.exists()) {
			createPropertiesFile(file);
		} else {
			OutputStreamWriter propertiesFile = null;
			try {
				String newLine = System.getProperty("line.separator");
				propertiesFile = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
				propertiesFile.write(key + "=" + value + newLine);
			} catch (IOException e) {
			}
		}
		return "";
	}

	private static void createPropertiesFile(File file) {
		OutputStreamWriter propertiesFile = null;
		try {
			String newLine = System.getProperty("line.separator");
			propertiesFile = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			propertiesFile.write("#The generation is "
					+ new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + newLine);
			propertiesFile.write("#0:debug" + newLine);
			propertiesFile.write("#1:trace" + newLine);
			propertiesFile.write("#2:info" + newLine);
			propertiesFile.write("#3:warning" + newLine);
			propertiesFile.write("#4:error" + newLine);
			propertiesFile.write("#5:fatal" + newLine);
			propertiesFile.write("logLevel=0" + newLine);
			propertiesFile.write("#appender:console;file" + newLine);
			propertiesFile.write("appender=file" + newLine);
			propertiesFile.write("#logFileName=c:/logs/zplus.log" + newLine);
			propertiesFile.write("logFileName=c:/logs/zplus.log" + newLine);
			propertiesFile.write("javaSrcDir=C:/work/java_workspace/eclipse_workspace/buildErrCode/conf/" + newLine);
			propertiesFile.write("packageName=com.zjz" + newLine);
			propertiesFile.write("className=CodeMsg" + newLine);
			propertiesFile.flush();
		} catch (IOException e) {
		} finally {
			try {
				propertiesFile.close();
			} catch (IOException e) {
			}
		}
	}

	public static void appendFile(File file, String content) {
		OutputStreamWriter logFile2 = null;
		try {
			logFile2 = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
			logFile2.write(content);
			logFile2.flush();
		} catch (IOException e) {
		} finally {
			try {
				logFile2.close();
			} catch (IOException e) {
			}
		}
	}
	public static void writeFile(File file, String content) {
		OutputStreamWriter logFile2 = null;
		try {
			logFile2 = new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8");
			logFile2.write(content);
			logFile2.flush();
		} catch (IOException e) {
		} finally {
			try {
				logFile2.close();
			} catch (IOException e) {
			}
		}
	}

	/** 
	 * 读取流 
	 *  
	 * @param inStream 
	 * @return 字节数组 
	 * @throws Exception 
	 */  
	public static String readStream(InputStream inStream,String encoding) throws Exception {  
	    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();  
	    byte[] buffer = new byte[1024];  
	    int len = -1;  
	    while ((len = inStream.read(buffer)) != -1) {  
	        outSteam.write(buffer, 0, len);  
	    }  
	    outSteam.close();  
	    inStream.close();
	    return new String(outSteam.toByteArray(),encoding);  
	}

	public static File createTemplate() {
		String fileContent = readTemplateResource();
		File file = new File(getCurrentClassPath()+"CodeMsg.ftl");
		if(file.exists()){
			return file;
		}
		writeFile(file,fileContent);
		return file;
	}
}
