package com.icutword.test;

import java.util.ArrayList;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.utility.CUtility;

/**
 * 测试使用CDictionary来读取定义词典文件的时候，内存的使用占用情况
 * <li>读取结果令人吃惊，在读取一个7.5M的词典文件时，居然要使用40M的内存
 * <li>也就是说，占用的内存最少是原始文件的7-8倍，在词典变大，内存耗尽以后
 * <li>占用内存更加增加
 * <li>未来在优化分词程序的时候，需要考虑内存的分配和占用问题
 * <li>这个可能会成为系统的一个重要的瓶颈点
 * @author liujunsong
 * 
 */
public class CDictionaryMemUseTest extends CUtility {
	static char[] concatenate(char[] str1, char[] str2) {
		char str0[] = new char[500];
		char p[] = str0;
		int i = 0;
		while ((p[i] = str1[i]) != '\0')
			i++;
		// i--;
		int j = 0;
		while ((p[i] = str2[j]) != '\0') {
			i++;
			j++;
		}
		p[i] = '\0';
		return str0;
	}

	public static void main(String args[]) {
		long l1, l2;
		String DICT_FILE = "Data/coreDict.dct";
		String BIGRAM_FILE = "Data/BigramDict.dct";

		System.out.println("定义词典前使用内存:" + (l1 = getUsedMem()));
		CDictionary m_dictCore = new CDictionary();
		CDictionary m_dictBigram = new CDictionary();
		System.out.println("定义词典后使用内存:" + (l2 = getUsedMem()) + " 词典："
				+ DICT_FILE + "|" + BIGRAM_FILE);
		System.out.println("定义词典占用内存:" + (l2 - l1) / 1024 + "k");

		for (int i = 0; i < 3; i++) {
			System.out.println("-------------->第"+i+"次读取");

			System.out.println("读取词典前使用内存:" + (l1 = getUsedMem()));
//			m_dictCore
//					.Load(concatenate("\0".toCharArray(), toGB2312(DICT_FILE)));
			m_dictBigram.Load(concatenate("\0".toCharArray(),
					toGB2312(BIGRAM_FILE)));
			System.out.println("读取词典后使用内存:" + (l2 = getUsedMem()) + " 词典："
					+ DICT_FILE + "|" + BIGRAM_FILE);
			System.out.println("词典占用内存:" + (l2 - l1) / 1024 + "k");
			try{
			Thread.sleep(10000);
			}catch(Exception e){
				e.printStackTrace();
			}
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
