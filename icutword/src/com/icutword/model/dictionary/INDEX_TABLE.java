package com.icutword.model.dictionary;

/**
 * ������ṹ
 * @author liujunsong
 *
 */
public class INDEX_TABLE {
	int nCount;
	// The count number of words which initial letter is sInit
	PWORD_ITEM pWordItemHead[];

	/**
	 * ���캯��
	 */
	public INDEX_TABLE() {
		nCount = 0;
		pWordItemHead = null;
	}

}
