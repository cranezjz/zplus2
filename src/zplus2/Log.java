package zplus2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

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
				MyConsole.printToConsole(timeStr+" " +msg);		
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
	public static void error(String msg){
		log(msg, 4);
	}
	public static void fatal(String msg){
		log(msg, 5);
	}
	public static void main(String[] args) {
/*		Log.debug("11111");
		Log.trace("11111");
		Log.info("11111");
		Log.warning("11111");
		Log.error("11111");*/
	}
}
