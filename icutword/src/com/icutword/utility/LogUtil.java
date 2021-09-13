package com.icutword.utility;

import org.apache.log4j.*;

/**
 * Log�����࣬�򵥷�װ�������滻ԭ�����������System.out.println()������
 * @author liujunsong
 *
 */
public class LogUtil {
	static Logger logger = Logger.getLogger("logger");
	
	/**
	 * ���Debug��Ϣ
	 * @param s
	 */
	public static void debug(String s){
		logger.debug(s);
	}
	
	/**
	 * ���INFO��Ϣ
	 * @param s
	 */
	public static void info(String s){
		logger.info(s);
	}
	
	/**
	 * ���ERROR��Ϣ
	 * @param s
	 */
	public static void error(String s){
		logger.error(s);
	}
}
