package com.icutword.model.dictionary;

import com.icutword.utility.CUtility;

/**
 *  �ִ��Ժ�ķִʽڵ���,һ��WORD_RESULT��Ӧһ������ĵ���
 * @author liujunsong
 *
 */
public class WORD_RESULT {
	int WORD_MAXLENGTH = 100;
	
	// The word
	// �зֳ����Ĵ�
	public char sWord[] = new char[WORD_MAXLENGTH];
	
	// the POS of the word	
	//�ʶ�Ӧ�Ĵ���
	public int nHandle;

	// The -log(frequency/MAX)
	// ����Ƶ�ʱ����ֵ��logֵ�����������
	public double dValue;

	/**
	 * ���캯��
	 */
	public WORD_RESULT(){
		sWord[0]=0;
	}
	
	/**
	 * ת���ַ�����ʽ��ʾ
	 */
	public String toString(){
		return ("dvalue:"+dValue+" nHandle:"+nHandle+" sword:"+(sWord==null?"null":CUtility.getCString(sWord)));
	}
}
