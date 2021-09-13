package com.icutword.test;

import java.util.ArrayList;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.utility.CUtility;

/**
 * ����ʹ��CDictionary����ȡ����ʵ��ļ���ʱ���ڴ��ʹ��ռ�����
 * <li>��ȡ������˳Ծ����ڶ�ȡһ��7.5M�Ĵʵ��ļ�ʱ����ȻҪʹ��40M���ڴ�
 * <li>Ҳ����˵��ռ�õ��ڴ�������ԭʼ�ļ���7-8�����ڴʵ����ڴ�ľ��Ժ�
 * <li>ռ���ڴ��������
 * <li>δ�����Ż��ִʳ����ʱ����Ҫ�����ڴ�ķ����ռ������
 * <li>������ܻ��Ϊϵͳ��һ����Ҫ��ƿ����
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

		System.out.println("����ʵ�ǰʹ���ڴ�:" + (l1 = getUsedMem()));
		CDictionary m_dictCore = new CDictionary();
		CDictionary m_dictBigram = new CDictionary();
		System.out.println("����ʵ��ʹ���ڴ�:" + (l2 = getUsedMem()) + " �ʵ䣺"
				+ DICT_FILE + "|" + BIGRAM_FILE);
		System.out.println("����ʵ�ռ���ڴ�:" + (l2 - l1) / 1024 + "k");

		for (int i = 0; i < 3; i++) {
			System.out.println("-------------->��"+i+"�ζ�ȡ");

			System.out.println("��ȡ�ʵ�ǰʹ���ڴ�:" + (l1 = getUsedMem()));
//			m_dictCore
//					.Load(concatenate("\0".toCharArray(), toGB2312(DICT_FILE)));
			m_dictBigram.Load(concatenate("\0".toCharArray(),
					toGB2312(BIGRAM_FILE)));
			System.out.println("��ȡ�ʵ��ʹ���ڴ�:" + (l2 = getUsedMem()) + " �ʵ䣺"
					+ DICT_FILE + "|" + BIGRAM_FILE);
			System.out.println("�ʵ�ռ���ڴ�:" + (l2 - l1) / 1024 + "k");
			try{
			Thread.sleep(10000);
			}catch(Exception e){
				e.printStackTrace();
			}
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
