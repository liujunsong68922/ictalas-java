package com.icutword.model.dictionary;

/**
 * 指向WORD_RESULT的指针对象
 * @author liujunsong
 *
 */
public class PWORD_RESULT {
	public WORD_RESULT p=null;
	
	/**
	 * 带参数的构造函数
	 * @param point
	 */
	public PWORD_RESULT(WORD_RESULT point){
		p=point;
	}


}
