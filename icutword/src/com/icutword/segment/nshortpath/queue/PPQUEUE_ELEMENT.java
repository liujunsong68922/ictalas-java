package com.icutword.segment.nshortpath.queue;

/**
 * QUEUE_ELEMENT对应的二级指针
 * @author liujunsong
 *
 */
public class PPQUEUE_ELEMENT {
	public PQUEUE_ELEMENT p=null;
	
	/**
	 * 带参数的构造函数
	 * @param point
	 */
	public PPQUEUE_ELEMENT(PQUEUE_ELEMENT point){
		p=point;
	}
	
	/**
	 * 重新设置指针的值,这一方法不会生成新的PQUEUE_ELEMENT对象
	 * @param point
	 */
	public void setValue(PQUEUE_ELEMENT point){
		p=point;
	}	
}
