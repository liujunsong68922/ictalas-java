package com.icutword.model.dynamicarray;

/**
 * 一个二级指针对象，指向一个PARRAY_CHAIN 用来模拟原代码里面的PARRAY_CHAIN *pRet对象 为 pPARRAY_CHAIN pRet
 * 
 * @author liujunsong
 * 
 */
public class PPARRAY_CHAIN {
	public PARRAY_CHAIN p;// p是一个指针，指向一个PARRAY_CHAIN对象

	/**
	 * 必须使用带有参数的构造函数
	 * 
	 * @param point
	 */
	public PPARRAY_CHAIN(PARRAY_CHAIN point) {
		p = point;
	}

	public void setValue(PARRAY_CHAIN point) {
		p = point;
	}
}
