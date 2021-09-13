package com.icutword.model.contextstat;

/**
 * PMYCONTEXT的定义是一个指向结构体MYCONTEXT的指针, 是一个一级指针
 * @author liujunsong
 * 
 */
public class PMYCONTEXT {
	public MYCONTEXT p=null;// The chain pointer to next Context
	/**
	 * 带参数的构造函数
	 * @param tag
	 */
	public PMYCONTEXT(MYCONTEXT tag){
		p=tag;
	}
	
	/**
	 * 得到下一个节点，通过封装p来实现。
	 * @return
	 */
	public PMYCONTEXT next(){
		if(p!=null){
			return p.next;
		}else{
			return null;
		}
	}
	
	/**
	 * 从当前节点开始，递归到各个下级节点，然后释放所有链表的内存
	 * 这个方法用来释放内存。
	 */
	public void DestroyAll(){
		PMYCONTEXT pCur=next(),pTemp;
		//递归清理所有下级的节点
		while(pCur!=null){
			pTemp=pCur.next(); //得到下一节点			
			pCur.p.Destroy(); //当前节点内存释放
			pCur=pTemp; //指向下一节点
		}
		//清理本级指针对应节点
		if(p!=null){
			p.Destroy();
			p=null;
		}
	}
	
}
