package com.icutword.result;

//import com.icutword.utility.CStringFunction;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.log4j.Logger;

import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

/**
 * <li>ԭ����C++���򣬸����ͬ����Java�����Ժ󣬴��ڵ�������Ҫ���޷���չ��������� <li>
 * ���Կ��ǴӼ������������г���ĸ��죬�����γ�һ�����������ķִʹ��߲�Ʒ <li>
 * 1.���ڴ����������java������Ǩ�Ƴ���������NoSQL���ݿ�洢���������Զ�̬��չ�ʿ⣬�����������Ч�� <li>
 * 2.����������Ҫ�õ������ݴ洢���Ͳ������̽��з��룬�����������Ĵ���ģʽ <li>3.��ͬһ�ĵ���������ͳ����Ϣ����ֽ�֮�� <li>
 * 4.�������������⹦����Ϊ�����ò������ϵͳ����У��ɸ�����Ҫ����չ <li>
 * 5.���磺XXЦ����XX˵����XXָ������������£����Ը��ݺ���ֽ�õ��Ķ���Ц��˵��ָ���������ж�ǰ����һ�������� <li>
 * ����Ҫ�������飬��ͳһ����ģ��������������ݽṹ��ʹ�ø�����Ĳ�����������һ��ͳһ�Ļ�����������ִ�С� <li>
 * ����Ŀǰ������ڵĸ����׶����ݽṹ��Ϊ���ҵ����
 * 
 * @author liujunsong
 * 
 */
public class CResultTest {
	static Logger logger = Logger.getLogger("logger");

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		logger.info("Total,free,Used Memory:"
				+ Runtime.getRuntime().totalMemory() + ","
				+ Runtime.getRuntime().freeMemory() + "," + getUsedMem());
		
		CResult r = new CResult("\0".toCharArray());
		String a = "�Ž����Ͱ���˹�������羰�续����ɽƺ�羰��";
		char ret[] = new char[1000];
		ret[100] = '\0';

		long begin = System.currentTimeMillis();
		long end;

		r.ParagraphProcessing(CUtility.toGB2312(a), ret);
		logger.info("Total,free,Used Memory:"
				+ Runtime.getRuntime().totalMemory() + ","
				+ Runtime.getRuntime().freeMemory() + "," + getUsedMem());
		end = System.currentTimeMillis();
		logger.info("User Time:" + (end - begin));
		System.out.println("size:" + CStringFunction.strlen(ret));
		System.out.println("Result:" + CUtility.getCString(ret));

		//runfile("/home/liujunsong/reddream.txt");
	}

	/**
	 * ִ�н������������������ļ��ķ���
	 * 
	 * @param filename
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private static void runfile(String filename) throws Exception {
		CResult r = new CResult("\0".toCharArray()); // ��ʼ��CResult��
		logger.info("Total,free,Used Memory:"
				+ Runtime.getRuntime().totalMemory() + ","
				+ Runtime.getRuntime().freeMemory() + "," + getUsedMem());
		MyFileReader myfile = new MyFileReader(filename);
		byte line[];
		line = myfile.readLine();
		int i = 0;
		long begin = System.currentTimeMillis();
		long end;
		while (line != null) {
			char buffer[] = new char[line.length + 1];
			for (i = 0; i < line.length; i++)
				buffer[i] = (char) line[i];
			buffer[i] = 0;

			char ret[] = new char[1024 * 50];
			ret[100] = '\0';
			r.ParagraphProcessing(buffer, ret);

			logger.info("���:"+CUtility.getCString(ret));
			logger.info("Total,free,Used Memory:"
					+ Runtime.getRuntime().totalMemory() + ","
					+ Runtime.getRuntime().freeMemory() + "," + getUsedMem());
			end = System.currentTimeMillis();
			logger.info("User Time:" + (end - begin));
			begin = end;
			line = myfile.readLine();
		}
	}

	/**
	 * �õ��Ѿ�ʹ�õ��ڴ�
	 * 
	 * @return
	 */
	private static long getUsedMem() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

}
