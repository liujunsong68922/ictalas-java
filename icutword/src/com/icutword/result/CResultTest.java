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
 * <li>原来的C++程序，改造成同步的Java程序以后，存在的问题主要是无法扩展，难以理解 <li>
 * 可以考虑从几个方面来进行程序的改造，真正形成一个独立的中文分词工具产品 <li>
 * 1.将内存管理工作，从java代码中迁移出来，采用NoSQL数据库存储，这样可以动态扩展词库，并发访问提高效率 <li>
 * 2.将操作所需要用到的数据存储，和操作过程进行分离，采用责任链的处理模式 <li>3.将同一文档的上下文统计信息加入分解之中 <li>
 * 4.将中文语义的理解功能作为可配置插件加入系统设计中，可根据需要来扩展 <li>
 * 5.例如：XX笑道，XX说道，XX指出，这种情况下，可以根据后面分解得到的动词笑，说，指出，进而判断前面是一个人名。 <li>
 * 首先要做的事情，是统一各个模块输入输出的数据结构，使得各步骤的操作都集中在一个统一的基础上来进行执行。 <li>
 * 避免目前程序存在的各个阶段数据结构极为混乱的情况
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
		String a = "张进明和安娜斯基来到风景如画的铁山坪风景区";
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
	 * 执行解析方法，处理输入文件的方法
	 * 
	 * @param filename
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private static void runfile(String filename) throws Exception {
		CResult r = new CResult("\0".toCharArray()); // 初始化CResult类
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

			logger.info("结果:"+CUtility.getCString(ret));
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
	 * 得到已经使用的内存
	 * 
	 * @return
	 */
	private static long getUsedMem() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}

}
