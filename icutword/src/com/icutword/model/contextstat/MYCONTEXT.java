package com.icutword.model.contextstat;

/**
 * MYCONTEXT的定义是一个结构体,其中的next变量代表指向下一个节点的指针
 * 
 * @author liujunsong
 * 
 */
public class MYCONTEXT {
	public int nKey;// The key word 关键词
	public int aContextArray[][];// The context array 上下文数组
	public int aTagFreq[];// The total number a tag appears 词的分布频率
	public int nTotalFreq;// The total number of all the tags 总的出现频率
	public PMYCONTEXT next;// The chain pointer to next Context 下一个节点
	
	/**
	 * 内存回收
	 */
	public void Destroy(){
		aContextArray=null;
		aTagFreq=null;
		next=null;
	}
}
