package com.icutword.model.dictionary;

//import com.icutword.model.contextstat.PMYCONTEXT;

public class PWORD_CHAIN {
	WORD_CHAIN p = null;

	public PWORD_CHAIN(WORD_CHAIN point) {
		p = point;
	}
	
	/**
	 * 在一级指针上封装一个next()方法
	 * @return
	 */
	public PWORD_CHAIN next(){
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
		PWORD_CHAIN pCur=next(),pTemp;
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
