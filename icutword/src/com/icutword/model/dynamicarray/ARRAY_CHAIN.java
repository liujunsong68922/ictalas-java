package com.icutword.model.dynamicarray;

import com.icutword.utility.CStringFunction;

/**
 * ARRAY_CHAIN是对切词结果的对应存储，构成一个链表结构
 * @author liujunsong
 *
 */
public class ARRAY_CHAIN {
	//本地变量存储开始
	public int col, row;// row and column
	public double value;// The value of the array
	public int nPOS;
	public int nWordLen;
	// 之所以不使用String类的原因，是因为在转换的时候可能会出现转码的问题
	// 为保证和原来代码功能一致，不使用String类
	public char[] sWord; // 用一个char[]数组来模拟char *sWord,效果相同
	// The possible POS of the word related to the segmentation graph
	// next指向下一个节点
	public PARRAY_CHAIN next;
	
	/**
	 * 构造函数
	 */
	public ARRAY_CHAIN(){
		next=null;
		sWord=null;
	}
	
	/**
	 * 解构函数，確保释放内存
	 */
	public void Destroy(){
		next=null;
		sWord=null;		
	}
	
	/**
	 * 重载toString方法，将ARRAY_CHAIN里面的数据转换成字符串
	 */
	public String toString(){
		String sout="";
		sout+="{row:"+row;
		sout+=",col:"+col;
		sout+=",value:"+value;
		sout+=",nPOS:"+nPOS;
		sout+=",nWordLen:"+nWordLen;
		sout+=",sWord:"+(sWord==null?"null":CStringFunction.getCString(sWord));
		sout+="}";
		
		return sout;
	}
}
