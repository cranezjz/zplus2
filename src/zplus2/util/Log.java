package zplus2.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Date;

import zplus2.MyConsole;

public class Log {
	/**
	 * 0:debug
	 * 1:trace
	 * 2:info
	 * 3:warning
	 * 4:error
	 */
	private static int level = 0;
	private static String appender = "console";//console;file
	private static String logFileName = "c:/logs/zplus.log";
	
	public static final int debug = 0;
	public static final int trace = 1;
	public static final int info = 2;
	public static final int warning = 3;
	public static final int error = 4;
	
	private  static void log(String msg,int curLevel){
		if(curLevel>=level){
			msg = appendLevel(msg,curLevel);
			String timeStr = new java.text.SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
			if("file".equals(appender)){
				File file = new File(logFileName);
				try {
					OutputStreamWriter logFile = new OutputStreamWriter(new FileOutputStream(file,true),"UTF-8");
					logFile.write(timeStr+" " +msg+System.getProperty("line.separator"));
					logFile.flush();
					logFile.close();
				}catch (Exception e) {
				}
			}else{
				if(level>Log.debug){
					MyConsole.printToConsole(timeStr+" " +msg,true);		
				}else{
					MyConsole.printToConsole(timeStr+" " +msg,false);		
				}
			}
		}
	}

	private static String appendLevel(String msg, int curLevel) {
		if(curLevel==0)
			return " debug "+msg;
		if(curLevel==1)
			return " trace "+msg;
		if(curLevel==2)
			return " info "+msg;
		if(curLevel==3)
			return " warning "+msg;
		if(curLevel==4)
			return " error "+msg;
		if(curLevel==5)
			return " fatal "+msg;
		return msg;
	}

	public static void debug(String msg){
		log(msg, 0);
	}
	public static void trace(String msg){
		log(msg, 1);
	}
	public static void info(String msg){
		log(msg, 2);
	}
	public static void warning(String msg){
		log(msg, 3);
	}
	public static void error(String msg,Throwable e){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(out));
		log(msg+System.getProperty("line.separator")+out.toString(), 4);
	}
	public static void fatal(String msg,Throwable e){
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(out));
		log(msg+System.getProperty("line.separator")+out.toString(), 5);
	}
	public static void main(String[] args) {
/*		Log.debug("11111");
		Log.trace("11111");
		Log.info("11111");
		Log.warning("11111");
		Log.error("11111");*/
	}
}
