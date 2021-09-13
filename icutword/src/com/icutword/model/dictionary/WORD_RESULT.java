package com.icutword.model.dictionary;

import com.icutword.utility.CUtility;

/**
 *  分词以后的分词节点结果,一个WORD_RESULT对应一个具体的单词
 * @author liujunsong
 *
 */
public class WORD_RESULT {
	int WORD_MAXLENGTH = 100;
	
	// The word
	// 切分出来的词
	public char sWord[] = new char[WORD_MAXLENGTH];
	
	// the POS of the word	
	//词对应的词性
	public int nHandle;

	// The -log(frequency/MAX)
	// 发生频率比最大值的log值，代表可能性
	public double dValue;

	/**
	 * 构造函数
	 */
	public WORD_RESULT(){
		sWord[0]=0;
	}
	
	/**
	 * 转成字符串形式显示
	 */
	public String toString(){
		return ("dvalue:"+dValue+" nHandle:"+nHandle+" sword:"+(sWord==null?"null":CUtility.getCString(sWord)));
	}
}
