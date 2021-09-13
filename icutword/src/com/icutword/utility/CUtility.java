package com.icutword.utility;

import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.OutputStreamWriter;


public class CUtility extends CStringFunction {
	public static final int CT_SENTENCE_BEGIN = 1;// Sentence begin
	public static final int CT_SENTENCE_END = 4;// Sentence ending
	public static final int CT_SINGLE = 5;// SINGLE byte
	public static final int CT_DELIMITER = CT_SINGLE + 1;// delimiter
	public static final int CT_CHINESE = CT_SINGLE + 2;// Chinese Char
	public static final int CT_LETTER = CT_SINGLE + 3;// HanYu Pinyin
	public static final int CT_NUM = CT_SINGLE + 4;// HanYu Pinyin
	public static final int CT_INDEX = CT_SINGLE + 5;// HanYu Pinyin
	public static final int CT_OTHER = CT_SINGLE + 12;// Other
	public char[] POSTFIX_SINGLE = toGB2312("�Ӱ���ǳش嵥�����̵궴�ɶӷ��帮�Ը۸󹬹������źӺ������������ǽ־����ӿڿ�����¥·������Ū����������������Ȫ��ɽʡ��ˮ����̨̲̳����ͤ��������ϪϿ������������ҤӪ����԰ԷԺբկվ����ׯ�������");
	public char POSTFIX_MUTIPLE[][] = { toGB2312("�뵺"), toGB2312("��ԭ"),
			toGB2312("����"), toGB2312("���"), toGB2312("�󹫹�"), toGB2312("����"),
			toGB2312("����"), toGB2312("�۹�"), toGB2312("�ɲ�"), toGB2312("�ۿ�"),
			toGB2312("���ٹ�·"), toGB2312("��ԭ"), toGB2312("��·"), toGB2312("��԰"),
			toGB2312("���͹�"), toGB2312("�ȵ�"), toGB2312("�㳡"), toGB2312("����"),
			toGB2312("��Ͽ"), toGB2312("��ͬ"), toGB2312("����"), toGB2312("����"),
			toGB2312("����"), toGB2312("�ֵ�"), toGB2312("�ڰ�"), toGB2312("��ͷ"),
			toGB2312("ú��"), toGB2312("����"), toGB2312("ũ��"), toGB2312("���"),
			toGB2312("ƽԭ"), toGB2312("����"), toGB2312("Ⱥ��"), toGB2312("ɳĮ"),
			toGB2312("ɳ��"), toGB2312("ɽ��"), toGB2312("ɽ��"), toGB2312("ˮ��"),
			toGB2312("���"), toGB2312("����"), toGB2312("��·"), toGB2312("�´�"),
			toGB2312("ѩ��"), toGB2312("�γ�"), toGB2312("�κ�"), toGB2312("�泡"),
			toGB2312("ֱϽ��"), toGB2312("������"), toGB2312("������"), toGB2312("������"),
			toGB2312("") };

	public char[] TRANS_ENGLISH = toGB2312("�������������������°İʰŰͰװݰ������������ȱϱ˱𲨲��������������Ųɲֲ��񳹴��Ĵȴδ����������������µõĵǵϵҵٵ۶����Ŷض����������������Ʒҷѷ�򸣸������ǸɸԸ���������ŹϹ��������������ϺӺպ����������������Ӽּ��ܽ𾩾þӾ��������������¿ƿɿ˿Ͽ����������������������������������������������������������¡¬²³·��������������������éï÷����������������ĦĪīĬķľ������������������������ŦŬŵŷ��������������Ƥƽ��������ǡǿ��������Ȫ��������������������ɣɪɭɯɳɽ������ʥʩʫʯʲʷʿ��˹˾˿��������̩̹����������͡ͼ������������������Τάκ��������������������ϣϲ������Ъл������������ҢҶ��������������ӢӺ����Լ������ղ������������׿������٤��������üν�����������Ľ����������������ɺ����ѷ��������ܽ���������������");
	public char[] TRANS_RUSSIAN = toGB2312("�������°ͱȱ˲�����Ĵ�µö��Ŷ���������Ǹ�����Ӽ�ݽ𿨿ƿɿ˿���������������������¬³������÷����ķ������ŵ������������������ɫɽ��ʲ˹����̹������ά������ϣл��ҮҶ�������������ǵٸ�����ջ������������������������������ɣɳ��̩ͼ������׿��");
	public char[] TRANS_JAPANESE = toGB2312("���°˰װٰ�������ȱ��������ʲ˲ֲ������سന�����δ����������µص�ɶ������縣�Ը߹����Źȹع���úƺͺϺӺں���󻧻Ļ漪�ͼѼӼ�������������������þƾտ����ɿ˿�����������������������������¡¹������������ľ��������������Ƭƽ����ǧǰǳ����������������Ȫ������������ɭɴɼɽ��������ʥʯʵʸ������ˮ˳˾��̩��������������βδ����������ϸ������СТ����������������������ңҰҲҶһ����������ӣ��������������ԨԪԫԭԶ����������������լ����������ֲ֦֪֮��������������׵��������ܥݶ��޹������");
	
	// Translation type
	public int TT_ENGLISH = 0;
	public int TT_RUSSIAN = 1;
	public int TT_JAPANESE = 2;
	
	// Seperator type
	public String SEPERATOR_C_SENTENCE = "������������";
	public String SEPERATOR_C_SUB_SENTENCE = "����������������";
	public String SEPERATOR_E_SENTENCE = "!?:;";
	public String SEPERATOR_E_SUB_SENTENCE = ",()*'";
	public String SEPERATOR_LINK = "\n\r ��";
	
	// Sentence begin and ending string
	public String SENTENCE_BEGIN = "ʼ##ʼ";
	public String SENTENCE_END = "ĩ##ĩ";
	
	// Seperator between two words
	public static String WORD_SEGMENTER = "@";

	public enum TAG_TYPE {
		TT_NORMAL, TT_PERSON, TT_PLACE, TT_TRANS_PERSON
	};	
	/*********************************************************************
	 * 
	 * Func Name : GB2312_Generate
	 * 
	 * Description: Generate the GB2312 List file
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output GB2312 List
	 * 
	 * Returns : boolean Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	boolean GB2312_Generate(char[] sFileName) {
		File fp=null;
		int i, j;

		try {
			fp = new File(new String(sFileName));
			OutputStreamWriter fw = new OutputStreamWriter(
					new FileOutputStream(new String(sFileName)), "GB2312");
			for (i = 161; i < 255; i++) {
				for (j = 161; j < 255; j++) {
					byte b[] = new byte[2];
					b[0] = (byte) i;
					b[1] = (byte) j;
					String s = new String(b, "GB2312");
					System.out.print(s);
					fw.write(s);
					fw.write(i);
					fw.write(j);
				}
				System.out.println();
				fw.write("\n\r");
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********************************************************************
	 * 
	 * Func Name : CC_Generate
	 * 
	 * Description: Generate the Chinese Char List file
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output CC List
	 * 
	 * Returns : boolean Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	/**
	 * ����GB2312���������֣�д���ļ��У��ļ����뷽ʽGB2312
	 * 
	 * @param sFileName
	 * @return
	 */
	boolean CC_Generate(char[] sFileName) {
		File fp;
		int i, j;

		try {
			fp = new File(new String(sFileName));
			OutputStreamWriter fw = new OutputStreamWriter(
					new FileOutputStream(new String(sFileName)), "GB2312");
			for (i = 176; i < 255; i++) {
				for (j = 161; j < 255; j++) {
					byte b[] = new byte[2];
					b[0] = (byte) i;
					b[1] = (byte) j;
					String s = new String(b, "GB2312");
					System.out.print(s);
					fw.write(s);
					fw.write(i);
					fw.write(j);
				}
				System.out.println();
				fw.write("\n\r");
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********************************************************************
	 * 
	 * Func Name : CC_Find
	 * 
	 * Description: Find a Chinese sub-string in the Chinese String
	 * 
	 * 
	 * Parameters : string:Null-terminated string to search
	 * 
	 * strCharSet:Null-terminated string to search for
	 * 
	 * Returns : char * Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	
	/**
	 * �ӵ�һ���ַ������棬�ҵ��ڶ����ַ���������ʣ�ಿ�֣���Ϊһ��char[]
	 * 
	 * @param string
	 * @param strCharSet
	 * @return
	 */
	public static char[] CC_Find(char[] string, char[] strCharSet,int srcpos) {
		char[] src1=new char[strlen(strCharSet)-srcpos+1];
		int i=srcpos;
		while(strCharSet[i]!='\0'){
			src1[i-srcpos]=strCharSet[i];
			i++;
		}
		src1[i-srcpos]='\0';
		
		char[] cp = CStringFunction.strstr(string, src1); // ����ʣ����ַ���
		// �����������з֣����뿼���ڲ��ҵĹ����г��ְ�����ִ�������
		if (cp != null && ((strlen(string) - strlen(cp)) % 2 == 1)) {
			return null;
		}
		return cp;
	}
	/**
	 * �ӵ�һ���ַ������棬�ҵ��ڶ����ַ���������ʣ�ಿ�֣���Ϊһ��char[]
	 * 
	 * @param string
	 * @param strCharSet
	 * @return
	 */
	public static char[] CC_Find(char[] string, char[] strCharSet) {
		if(strlen(strCharSet)==0){
			char c1[] = new char[1];
			c1[0]=0;
			return c1;
		}
		
		char[] cp = CStringFunction.strstr(string, strCharSet); // ����ʣ����ַ���
		// �����������з֣����뿼���ڲ��ҵĹ����г��ְ�����ִ�������
		if (cp != null && ((strlen(string) - strlen(cp)) % 2 == 1)) {
			return null;
		}
		return cp;
	}

	/**
	 * ��һ��Ѱ��ʣ��ķ�������һ������������String
	 * 
	 * @param str
	 * @param strCharSet
	 * @return
	 */
	private static char[] CC_Find(String str, char[] strCharSet) {
		return CC_Find(str.toCharArray(), strCharSet);
	}

	/*********************************************************************
	 * 
	 * Func Name : charType
	 * 
	 * Description: Judge the type of sChar or (sChar,sChar+1)
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output CC List
	 * 
	 * Returns : int : the type of char Author : Kevin Zhang History : 1.create
	 * 2002-1-8
		 *********************************************************************/
	public static int charType(char[] sChar,int ipos) {
		char c[]=new char[strlen(sChar)-ipos+1];
		int i;
		for(i=0;sChar[i+ipos]!='\0';i++){
			c[i]=sChar[i+ipos];
		}
		c[i]='\0';
		return charType(c);
	}
	
	//��һ��charת��Ϊ�޷��ŵ�byte����(0-255)
	//��λ���������������ݱȽϣ���ԭ����C�߼�����
	private static int toUnsigByte(char c){
		byte b = (byte)c;
		return (b>=0?b:b+256);
	}
	/**
	 * �ж�һ�������ַ����Ĵ���
	 * �����жϵ�ʱ������char��16λ��ռ����2��byte,�Ƚ�ʱ��Ҫ����ѹ����byte���Ƚ�
	 * ��ΪJava����û���޷��������ĸ������������Ҫ���¿������߼�
	 * @param sChar
	 * @return
	 */
	public static int charType(char[] sChar) {
		if (toUnsigByte(sChar[0]) < 128) {
			if (strchr("*!,.?()[]{}+=", (int) sChar[0]) >= 0)
				return CT_DELIMITER; //�ָ����
			return CT_SINGLE; //���ֽڴ�
		} else if (toUnsigByte(sChar[0]) == 162)
			return CT_INDEX;
		else if (toUnsigByte(sChar[0]) == 163 && (toUnsigByte(sChar[1])) > 175 && (toUnsigByte(sChar[1])) < 186)
			return CT_NUM;
		else if (toUnsigByte(sChar[0]) == 163
				&& (toUnsigByte(sChar[1]) >= 193 && toUnsigByte(sChar[1]) <= 218 || toUnsigByte(sChar[1]) >= 225
						&& toUnsigByte(sChar[1]) <= 250))
			return CT_LETTER;
		else if (toUnsigByte(sChar[0]) == 161 || toUnsigByte(sChar[0]) == 163)
			return CT_DELIMITER;
		else if (toUnsigByte(sChar[0]) >= 176 && toUnsigByte(sChar[0]) <= 247)
			return CT_CHINESE;
		else
			return CT_OTHER;
	}


	/*********************************************************************
	 * 
	 * Func Name : GetCCPrefix
	 * 
	 * Description: Get the max Prefix string made up of Chinese Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-8
	 *********************************************************************/
	/**
	 * �õ�һ���ַ�������������������ǰ׺����
	 * 
	 * @param sSentence
	 * @return
	 */
	int GetCCPrefix(char[] sSentence) {
		int nLen = strlen(sSentence), nCurPos = 0;
		while (nCurPos < nLen && sSentence[nCurPos] > 175
				&& sSentence[nCurPos] < 248) {
			nCurPos += 2;// Get next Chinese Char
		}
		return nCurPos;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllSingleByte
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * �ж��ַ����Ƿ�ȫ������
	 * 
	 * @param sString
	 * @return
	 */
	public static boolean IsAllChinese(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && sString[i] < 248 && sString[i] > 175) {
			i += 2;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllNonChinese
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * �Ƿ�ȫ����������
	 * 
	 * @param sString
	 * @return
	 */
	boolean IsAllNonChinese(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen) {
			if (sString[i] < 248 && sString[i] > 175)
				return false;
			if (sString[i] > 128)
				i += 2;
			else
				i += 1;
		}
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllSingleByte
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * ���ǵ��ֽڵ�byte��
	 * @param sString
	 * @return
	 */
	public static boolean IsAllSingleByte(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen && sString[i] < 128) {
			i++;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllNum
	 * 
	 * Description: Judge the string is all made up of Num Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * ���еĶ���������
	 * @param sString
	 * @return
	 */
	public static boolean IsAllNum(char[] sString) {

		int nLen = strlen(sString), i = 0;
		char[] sChar = new char[3];
		sChar[2] = 0;
		if (i < nLen)// Get prefix such as + -
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (strstr(toGB2312("��+��-��"), sChar) != null) {
				i = 0;
			}
		}
		while (i < nLen - 1 && sString[i] == 163 && sString[i + 1] > 175
				&& sString[i + 1] < 186) {
			i += 2;
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find("�á�����\0", sChar) != null || sChar[0] == '.'
					|| sChar[0] == '/') {// 98��1��
				while (i < nLen - 1 && sString[i] == 163
						&& sString[i + 1] > 175 && sString[i + 1] < 186) {
					i += 2;
				}
			} else {
				i -= strlen(sChar);
			}
		}

		if (i >= nLen)
			return true;
		while (i < nLen && sString[i] > '0' - 1 && sString[i] < '9' + 1) {// single
																			// byte
																			// number
																			// char
			i += 1;
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find("�á�����\0", sChar) != null || sChar[0] == '.'
					|| sChar[0] == '/') {// 98��1��
				while (i < nLen && sString[i] > '0' - 1 && sString[i] < '9' + 1) {
					i += 1;
				}
			} else {
				i -= strlen(sChar);
			}
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find(toGB2312("��ǧ���ڰ�Ǫ����"), sChar) == null && sChar[0] != '%')
				i -= strlen(sChar);
		}
		if (i >= nLen)
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllIndex
	 * 
	 * Description: Judge the string is all made up of Index Num Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	boolean IsAllIndex(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && sString[i] == 162) {
			i += 2;
		}
		if (i >= nLen)
			return true;
		while (i < nLen && (sString[i] > 'A' - 1 && sString[i] < 'Z' + 1)
				|| (sString[i] > 'a' - 1 && sString[i] < 'z' + 1)) {// single
																	// byte
																	// number
																	// char
			i += 1;
		}

		if (i < nLen)
			return false;
		return true;

	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllLetter
	 * 
	 * Description: Judge the string is all made up of Letter Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	public boolean IsAllLetter(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1
				&& sString[i] == 163
				&& ((sString[i + 1] >= 193 && sString[i + 1] <= 218) || (sString[i + 1] >= 225 && sString[i + 1] <= 250))) {
			i += 2;
		}
		if (i < nLen)
			return false;

		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllDelimiter
	 * 
	 * Description: Judge the string is all made up of Delimiter
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	boolean IsAllDelimiter(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && (sString[i] == 161 || sString[i] == 163)) {
			i += 2;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : BinarySearch
	 * 
	 * Description: Lookup the index of nVal in the table nTable which length is
	 * nTableLen
	 * 
	 * Parameters : nPOS: the POS value
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-25
	 *********************************************************************/
	/**
	 * ��һ�������������棬���ö��ַ�����������һ���������±�
	 * @param nVal Ҫ���ҵ�ֵ
	 * @param nTable ��������
	 * @param nTableLen ������󳤶�
	 * @return
	 */
	public static int BinarySearch(int nVal, int[] nTable, int nTableLen) {
		int nStart = 0, nEnd = nTableLen - 1, nMid = (nStart + nEnd) / 2;
		while (nStart <= nEnd)// Binary search
		{
			if (nTable[nMid] == nVal) {
				return nMid;// find it
			} else if (nTable[nMid] < nVal) {
				nStart = nMid + 1;
			} else {
				nEnd = nMid - 1;
			}
			nMid = (nStart + nEnd) / 2;
		}
		return -1;// Can not find it;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsForeign
	 * 
	 * Description: Decide whether the word is not a Non-fereign word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-26
	 *********************************************************************/
	/**
	 * ���������������
	 * @param sWord
	 * @return
	 */
	boolean IsForeign(char[] sWord) {
		int nForeignCount = GetForeignCharCount(sWord), nCharCount = strlen(sWord);
		if (nCharCount > 2 || nForeignCount >= 1 * nCharCount / 2)
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllForeign
	 * 
	 * Description: Decide whether the word is not a Non-fereign word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-3-25
	 *********************************************************************/
	boolean IsAllForeign(char[] sWord) {
		int nForeignCount = (int) GetForeignCharCount(sWord);
		if (2 * nForeignCount == strlen(sWord))
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsForeign
	 * 
	 * Description: Decide whether the word is Chinese Num word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-26
	 *********************************************************************/
	public boolean IsAllChineseNum(char[] sWord) {// �ٷ�֮������������ϰ˵�ʮ�˷���
		int k;
		char tchar[] = new char[3];
		char ChineseNum[] = toGB2312("���һ�������������߰˾�ʮإ��ǧ����Ҽ��������½��ƾ�ʰ��Ǫ�á�������");//
		char sPrefix[] = toGB2312("�������ϳ�");
		for (k = 0; k < strlen(sWord); k += 2) {
			strncpy(tchar, sWord, k, 2);
			tchar[2] = '\0';
			if (strncmp(sWord, k, toGB2312("��֮"), 4) == 0)// �ٷ�֮��
			{
				k += 2;
				continue;
			}

			if (CC_Find(ChineseNum, tchar) == null
					&& !(k == 0 && CC_Find(sPrefix, tchar) != null))
				return false;
		}
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetForeignCharCount
	 * 
	 * Description:
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-4-4 2.Modify 2002-5-21
	 *********************************************************************/
	public int GetForeignCharCount(char[] sWord) {
		int nForeignCount, nCount;
		nForeignCount = GetCharCount(TRANS_ENGLISH, sWord);// English char
															// counnts
		nCount = GetCharCount(TRANS_JAPANESE, sWord);// Japan char counnts
		if (nForeignCount <= nCount)
			nForeignCount = nCount;
		nCount = GetCharCount(TRANS_RUSSIAN, sWord);// Russian char counnts
		if (nForeignCount <= nCount)
			nForeignCount = nCount;
		return nForeignCount;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetCharCount
	 * 
	 * Description: Get the count of char which is in sWord and in sCharSet
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : COUNT Author : Kevin Zhang History : 1.create 2002-5-21
	 *********************************************************************/
	public static int GetCharCount(char[] sCharSet, char[] sWord) {
		int k = 0;
		char tchar[] = new char[3];
		int nCount = 0;
		tchar[2] = 0;
		while (k < strlen(sWord)) {
			tchar[0] = sWord[k];
			tchar[1] = 0;
			if (sWord[k] < 0) {
				tchar[1] = sWord[k + 1];
				k += 1;
			}
			k += 1;
			if ((tchar[0] < 0 && CC_Find(sCharSet, tchar) != null)
					|| strchr(sCharSet, tchar[0]) >= 0)
				nCount++;
		}
		return nCount;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetForeignCharCount
	 * 
	 * Description: Return the foreign type
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-4-4 2.Modify 2002-5-21
	 *********************************************************************/
	int GetForeignType(char[] sWord) {
		int nForeignCount, nCount, nType = TT_ENGLISH;
		nForeignCount = GetCharCount(TRANS_ENGLISH, sWord);// English char
															// counnts
		nCount = GetCharCount(TRANS_RUSSIAN, sWord);// Russian char counnts
		if (nForeignCount < nCount) {
			nForeignCount = nCount;
			nType = TT_RUSSIAN;
		}
		nCount = GetCharCount(TRANS_JAPANESE, sWord);// Japan char counnts
		if (nForeignCount < nCount) {
			nForeignCount = nCount;
			nType = TT_JAPANESE;
		}
		return nType;
	}

	/**
	 * 
	 * @param sWord
	 * @param sWordRet
	 * @param sPostfix
	 * @return
	 */
	boolean PostfixSplit(char[] sWord, char[] sWordRet, char[] sPostfix) {
		char sSinglePostfix[] = POSTFIX_SINGLE;
		char sMultiPostfix[][] = POSTFIX_MUTIPLE;
		int nPostfixLen = 0, nWordLen = strlen(sWord);
		int i = 0;

		while (sMultiPostfix[i][0] != 0
				&& strncmp(sWord, nWordLen - strlen(sMultiPostfix[i]),
						sMultiPostfix[i], strlen(sMultiPostfix[i])) != 0) {
			// Try to get the postfix of an address
			i++;
		}
		strcpy(sPostfix, sMultiPostfix[i]);
		nPostfixLen = strlen(sMultiPostfix[i]);// Get the length of place
												// postfix

		if (nPostfixLen == 0) {
			sPostfix[2] = 0;
			strncpy(sPostfix, sWord, nWordLen - 2, 2);
			if (CC_Find(sSinglePostfix, sPostfix) != null)
				nPostfixLen = 2;
		}

		strncpy(sWordRet, sWord, nWordLen - nPostfixLen);
		sWordRet[nWordLen - nPostfixLen] = 0;// Get the place name which have
												// erasing the postfix
		sPostfix[nPostfixLen] = 0;
		return true;

	}


	
	public static int atoi(char[] c){
		String s=new String(c);
		return Integer.parseInt(s);
	}
	
	public static double log(double d){
		return Math.log(d);
	}

}
