package com.icutword.utility;

import org.apache.log4j.*;

/**
 * Log功能类，简单封装，用来替换原来程序里面的System.out.println()方法。
 * @author liujunsong
 *
 */
public class LogUtil {
	static Logger logger = Logger.getLogger("logger");
	
	/**
	 * 输出Debug消息
	 * @param s
	 */
	public static void debug(String s){
		logger.debug(s);
	}
	
	/**
	 * 输出INFO消息
	 * @param s
	 */
	public static void info(String s){
		logger.info(s);
	}
	
	/**
	 * 输出ERROR消息
	 * @param s
	 */
	public static void error(String s){
		logger.error(s);
	}
}
