package com.icutword.utility;

/**
 * 为了尽可能少改动代码，利用这个功能类来对C语言的一些函数进行同名的模拟
 * 
 * @author liujunsong
 * 
 */
public class CStringFunction {

	/**
	 * 封装原来的strlen方法。
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
	 * 封装原来的strcpy方法,拷贝结束以后target里面以'\0'来作为终止符
	 * 
	 * @param target
	 *            目标点
	 * @param src
	 *            原始点
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
	 * 封装原来的strcpy方法,拷贝结束以后target里面以'\0'来作为终止符
	 * 
	 * @param target
	 *            目标点
	 * @param src
	 *            原始点
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
	 * 执行strcat命令
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
	 * 从string里面找到strCharSet,将开始点部分作为一个char[]返回
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
			// 拷贝一个字符数组到temp,从i开始,拷贝长度是strlen(strCharSet)
			for (j = 0; j < strlen(strCharSet) && string[i + j] != '\0'; j++) {
				temp[j] = string[i + j];
			}
			temp[j] = '\0'; // C格式的char[],以\0来结束

			// 比较两者是否相同，如果相同，则计算剩余的字符串
			if (isSame(temp, strCharSet)) {
				ret = new char[ilen + 1];
				for (k = i; string[k] != '\0'; k++) {
					ret[k - i] = string[k];
				}
				ret[k - i] = '\0';
				break; // 终止循环
			} else {
				i++;
			}

		}
		return ret;
	}

	/**
	 * 输入一个字符串常量的参数
	 * 
	 * @param str
	 * @param strCharSet
	 * @return
	 */
	private char[] strstr(String str, char[] strCharSet) {
		return strstr(str.toCharArray(), strCharSet);
	}

	/**
	 * 判断两个char[]是否完全相同
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
				return false; // 比较以后，两者存在不同
			}
			i++;
		}

		return true; // 没有不同，两者相同

	}

	/**
	 * 判断在String里面c出现的位置,-1代表没有找到
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
	 * 查找char[] 中char是否存在
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
	 * 实现strncpy功能，执行字符串的复制功能
	 * 
	 * @param desc
	 *            目的点
	 * @param src
	 *            原始点
	 * @param size
	 *            长度
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
	 * 实现strncpy方法,descpos是目标点偏移量
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
	 * 实现strncpy方法,descpos是目标点偏移量,srcpos是原偏移量
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
	 * 实现strncpy方法,descpos是目标点偏移量,srcpos是原偏移量
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
	 * 实现strncmp功能
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
	 * 实现strncmp功能,字符串比较
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
	 * 实现strncmp功能
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
	 * 将一个gb2312编码的字符（16bit)，转换成一个8位unsignt int代表的值
	 * @param c
	 * @return
	 */
	private static int toUnsignByte(char c){
		return (byte)c>=0?(byte)c:(byte)c+256;
	}
	/**
	 * 实现strncmp功能,比较的时候不区分大小写
	 *  这里需要进行修改，因为原来的编码采用byte，改成char以后，高位为0，排序乱了
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
			// TODO:增加一个同样大小写的判断
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
	 * 实现strncmp功能
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
	 * debug 用
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
	 * 将一个原来给定的String，转换成对应的gb2312编码的char[];
	 * 
	 * @param instr
	 * @return
	 */
	public static char[] toGB2312(String instr) {
		try {
			byte temp[] = instr.getBytes("GB2312"); // 将instr转换成一个gb2312编码的byte[]
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
