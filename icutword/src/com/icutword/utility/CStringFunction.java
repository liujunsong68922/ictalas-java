package com.icutword.utility;

/**
 * Ϊ�˾������ٸĶ����룬�����������������C���Ե�һЩ��������ͬ����ģ��
 * 
 * @author liujunsong
 * 
 */
public class CStringFunction {

	/**
	 * ��װԭ����strlen������
	 * 
	 * @param data
	 * @return
	 */
	public static int strlen(char[] data) {

		int i = 0;
		while (data[i] != '\0') {
			i++;
		}
		return i;
	}

	/**
	 * ��װԭ����strcpy����,���������Ժ�target������'\0'����Ϊ��ֹ��
	 * 
	 * @param target
	 *            Ŀ���
	 * @param src
	 *            ԭʼ��
	 */
	public static void strcpy(char[] target, char[] src) {
		int i = 0;
		while (src[i] != '\0') {
			target[i] = src[i];
			i++;
		}
		target[i] = '\0';
	}

	/**
	 * ��װԭ����strcpy����,���������Ժ�target������'\0'����Ϊ��ֹ��
	 * 
	 * @param target
	 *            Ŀ���
	 * @param src
	 *            ԭʼ��
	 */
	public static void strcpy(char[] target, char[] src, int ipos) {
		int i = 0;
		while (src[i + ipos] != '\0') {
			target[i] = src[i + ipos];
			i++;
		}
		target[i + ipos] = '\0';
	}

	/**
	 * ִ��strcat����
	 * 
	 * @param target
	 * @param src
	 */

	public static void strcat(char[] target, char[] src) {
		int i = strlen(target);
		int j = 0;
		while (src[j] != '\0') {
			target[i + j] = src[j];
			j++;
		}
		target[i + j] = '\0';
	}

	/**
	 * ��string�����ҵ�strCharSet,����ʼ�㲿����Ϊһ��char[]����
	 * 
	 * @param string
	 * @param strCharSet
	 * @return
	 */
	public static char[] strstr(char[] string, char[] strCharSet) {
		// System.out.println(getCString(string));
		// System.out.println(getCString(strCharSet));

		char[] ret = null;
		int i = 0, j, k;
		int ilen = strlen(string);

		while (i < string.length && string[i] != '\0') {
			char[] temp = new char[ilen + 1];
			// ����һ���ַ����鵽temp,��i��ʼ,����������strlen(strCharSet)
			for (j = 0; j < strlen(strCharSet) && string[i + j] != '\0'; j++) {
				temp[j] = string[i + j];
			}
			temp[j] = '\0'; // C��ʽ��char[],��\0������

			// �Ƚ������Ƿ���ͬ�������ͬ�������ʣ����ַ���
			if (isSame(temp, strCharSet)) {
				ret = new char[ilen + 1];
				for (k = i; string[k] != '\0'; k++) {
					ret[k - i] = string[k];
				}
				ret[k - i] = '\0';
				break; // ��ֹѭ��
			} else {
				i++;
			}

		}
		return ret;
	}

	/**
	 * ����һ���ַ��������Ĳ���
	 * 
	 * @param str
	 * @param strCharSet
	 * @return
	 */
	private char[] strstr(String str, char[] strCharSet) {
		return strstr(str.toCharArray(), strCharSet);
	}

	/**
	 * �ж�����char[]�Ƿ���ȫ��ͬ
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private static boolean isSame(char[] s1, char[] s2) {
		int i = 0;

		if (strlen(s1) != strlen(s2)) {
			return false;
		}

		while (i < strlen(s1)) {
			if (s1[i] != s2[i]) {
				return false; // �Ƚ��Ժ����ߴ��ڲ�ͬ
			}
			i++;
		}

		return true; // û�в�ͬ��������ͬ

	}

	/**
	 * �ж���String����c���ֵ�λ��,-1����û���ҵ�
	 * 
	 * @param str1
	 * @param c
	 * @return
	 */
	public static int strchr(String str1, int c) {
		return str1.indexOf(c);
	}

	public int strchr(String str1, char c) {
		return str1.indexOf(c);
	}

	/**
	 * ����char[] ��char�Ƿ����
	 * 
	 * @param str1
	 * @param c
	 * @return
	 */
	public static int strchr(char[] str1, char c) {
		int iret = -1;
		int i = 0;
		while (i < strlen(str1)) {
			if (str1[i] == c) {
				return i;
			}
			i++;
		}
		return iret;
	}

	/**
	 * ʵ��strncpy���ܣ�ִ���ַ����ĸ��ƹ���
	 * 
	 * @param desc
	 *            Ŀ�ĵ�
	 * @param src
	 *            ԭʼ��
	 * @param size
	 *            ����
	 * @return
	 */
	public static int strncpy(char[] desc, char[] src, int size) {
		int i = 0;
		for (i = 0; i < size; i++) {
			desc[i] = src[i];
		}
		return 0;
	}

	/**
	 * ʵ��strncpy����,descpos��Ŀ���ƫ����
	 * 
	 * @param desc
	 * @param pos
	 * @param src
	 * @param size
	 * @return
	 */
	public int strncpy(char[] desc, int descpos, char[] src, int size) {
		int i = 0;
		for (i = 0; i < size; i++) {
			desc[i + descpos] = src[i];
		}
		return 0;
	}

	/**
	 * ʵ��strncpy����,descpos��Ŀ���ƫ����,srcpos��ԭƫ����
	 * 
	 * @param desc
	 * @param pos
	 * @param src
	 * @param size
	 * @return
	 */
	public static int strncpy(char[] desc, int descpos, char[] src, int srcpos,
			int size) {
		int i = 0;
		for (i = 0; i < size; i++) {
			desc[i + descpos] = src[i + srcpos];
		}
		return 0;
	}

	/**
	 * ʵ��strncpy����,descpos��Ŀ���ƫ����,srcpos��ԭƫ����
	 * 
	 * @param desc
	 * @param pos
	 * @param src
	 * @param size
	 * @return
	 */
	public static int strncpy(char[] desc, char[] src, int srcpos, int size) {
		int i = 0;
		for (i = 0; i < size; i++) {
			desc[i] = src[i + srcpos];
		}
		return 0;
	}

	/**
	 * ʵ��strncmp����
	 * 
	 * @param str1
	 * @param str2
	 * @param maxlen
	 * @return
	 */
	public static int strncmp(char[] str1, char[] str2, int ipos2, int maxlen) {
		int i = 0;
		int iret = 0;
		for (i = 0; i < maxlen; i++) {
			if ((iret = toUnsignByte(str1[i]) - toUnsignByte(str2[i + ipos2])) != 0) {
				return iret;
			}
		}
		return 0;
	}

	/**
	 * ʵ��strncmp����,�ַ����Ƚ�
	 * 
	 * @param str1
	 * @param str2
	 * @param maxlen
	 * @return
	 */
	public static int strncmp(char[] str1, char[] str2, int maxlen) {
		int i = 0;
		int iret = 0;
		for (i = 0; i < maxlen; i++) {
			if ((iret = toUnsignByte(str1[i]) - toUnsignByte(str2[i])) != 0) {
				return iret;
			}
		}
		return 0;
	}

	/**
	 * ʵ��strncmp����
	 * 
	 * @param str1
	 * @param str2
	 * @param maxlen
	 * @return
	 */
	public static int strcmp(char[] str1, char[] str2) {
		int i = 0;
		int iret = 0;
		int maxlen = strlen(str1);
		if (maxlen > strlen(str2)) {
			maxlen = strlen(str2);
		}
		for (i = 0; i < maxlen; i++) {
			if ((iret = toUnsignByte(str1[i]) - toUnsignByte(str2[i])) != 0) {
				return iret;
			}
		}
		if (strlen(str1) == strlen(str2)) {
			return 0;
		} else {
			return strlen(str1) > strlen(str2) ? 1 : -1;
		}
	}

	public static int _stricmp(char[] str1, char[] str2) {
		return CStringFunction.strcasecmp(str1, str2);
	}
	
	/**
	 * ��һ��gb2312������ַ���16bit)��ת����һ��8λunsignt int�����ֵ
	 * @param c
	 * @return
	 */
	private static int toUnsignByte(char c){
		return (byte)c>=0?(byte)c:(byte)c+256;
	}
	/**
	 * ʵ��strncmp����,�Ƚϵ�ʱ�����ִ�Сд
	 *  ������Ҫ�����޸ģ���Ϊԭ���ı������byte���ĳ�char�Ժ󣬸�λΪ0����������
	 * @param str1
	 * @param str2
	 * @param maxlen
	 * @return
	 */
	public static int strcasecmp(char[] str1, char[] str2) {
		int i = 0;
		int iret = 0;
		int maxlen = strlen(str1);
		if (maxlen > strlen(str2)) {
			maxlen = strlen(str2);
		}
		for (i = 0; i < maxlen; i++) {
			// TODO:����һ��ͬ����Сд���ж�
			if ((iret =toUnsignByte(str1[i]) - toUnsignByte(str2[i])) != 0) {
				return iret;
			}
		}
		if (strlen(str1) == strlen(str2)) {
			return 0;
		} else {
			return strlen(str1) > strlen(str2) ? 1 : -1;
		}
	}

	/**
	 * ʵ��strncmp����
	 * 
	 * @param str1
	 * @param str2
	 * @param maxlen
	 * @return
	 */
	public static int strncmp(char[] str1, int pos1, char[] str2, int maxlen) {
		int i = 0;
		int iret = 0;
		for (i = 0; i < maxlen; i++) {
			if ((iret = toUnsignByte(str1[i + pos1]) - toUnsignByte(str2[i])) != 0) {
				return iret;
			}
		}
		return 0;
	}

	/**
	 * debug ��
	 * 
	 * @param in
	 * @return
	 */
	public static String getCString(char in[]) {
		if (in == null) {
			return "";
		}
		byte t[] = new byte[strlen(in)];
		int k;
		for (k = 0; in[k] != 0; k++) {
			t[k] = (byte) in[k];
		}
		try {
			return new String(t,"GB2312");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * ��һ��ԭ��������String��ת���ɶ�Ӧ��gb2312�����char[];
	 * 
	 * @param instr
	 * @return
	 */
	public static char[] toGB2312(String instr) {
		try {
			byte temp[] = instr.getBytes("GB2312"); // ��instrת����һ��gb2312�����byte[]
			char ret[] = new char[temp.length + 1];
			int i = 0;
			for (i = 0; i < temp.length; i++) {
				ret[i] = (char) temp[i];
			}
			ret[i] = '\0';
			return ret;
		} catch (Exception e) {
			return new char[0];
		}
	}

}
