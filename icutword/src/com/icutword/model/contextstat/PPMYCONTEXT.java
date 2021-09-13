package com.icutword.model.contextstat;

/**
 * 一个指向MYCONTEXT的二级指针
 * @author liujunsong
 *
 */
public class PPMYCONTEXT {
	public PMYCONTEXT p=null;
	
	/**
	 * 唯一可用的构造函数，可以传入一个null
	 * @param point
	 */
	public PPMYCONTEXT(PMYCONTEXT point){
		p=point;
	}
	
	/**
	 * 重新设置指针的值,这一方法不会生成新的PPMYCONTEXT对象
	 * 也不会重新分配内存。
	 * @param point
	 */
	public void setValue(PMYCONTEXT point){
		p=point;
	}	
}
