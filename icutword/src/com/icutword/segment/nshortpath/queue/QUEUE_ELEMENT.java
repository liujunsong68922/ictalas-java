package com.icutword.segment.nshortpath.queue;

/**
 * 队列的元素对象,对应结构QUEUE
 * 
 * @author liujunsong
 * 
 */
public class QUEUE_ELEMENT {
	public int nParent;// the weight
	public int nIndex;// number of index in the parent node
	public double eWeight;// the weight of last path
	public PQUEUE_ELEMENT next;

	/**
	 * 构造函数
	 */
	public QUEUE_ELEMENT() {
		nParent = 0;
		nIndex = 0;
		eWeight = 0;
		next = null;
	}
	
	/**
	 * 内存回收,主要是切断对象引用
	 */
	public void Destroy(){
		next = null;		
	}
	
	/**
	 * 转换成字符串格式表示
	 */
	public String toString(){
		String sret="{nParent:"+nParent+",nIndex:"+nIndex+",eWeight:"+eWeight+"}";
		return sret;
	}
}
