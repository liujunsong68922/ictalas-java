package com.icutword.model.dictionary;

/**
 * 索引表结构
 * @author liujunsong
 *
 */
public class INDEX_TABLE {
	int nCount;
	// The count number of words which initial letter is sInit
	PWORD_ITEM pWordItemHead[];

	/**
	 * 构造函数
	 */
	public INDEX_TABLE() {
		nCount = 0;
		pWordItemHead = null;
	}

}
