package com.icutword.model.dynamicarray;

//import com.icutword.contextstat.PMYCONTEXT;

/**
 * tagArrayChain原来是一个C里面的Structure, PARRAY_CHAIN定义的是他的指针对象
 * 转换为Java以后，直接定义一个PARRAY_CHAIN类就可以了，不再需要原来的Structure对象来模拟
 * 
 * @author liujunsong
 * 
 */
public class PARRAY_CHAIN {
	public ARRAY_CHAIN p;

	/**
	 * 必须使用带有参数的构造函数，确保p得到初始化
	 * 
	 * @param point
	 */
	public PARRAY_CHAIN(ARRAY_CHAIN point) {
		p = point;
	}
	
	/**
	 * 进行包装以后，包装一个next方法，避免丑陋的.p.调用
	 * @return
	 */
	public PARRAY_CHAIN next() {
		if (p != null) {
			return p.next;
		} else {
			return null;
		}
	}
	
	/**
	 * 从当前节点开始，递归到各个下级节点，然后释放所有链表的内存
	 * 这个方法用来释放内存。
	 */
	public void DestroyAll(){
		PARRAY_CHAIN pCur=next(),pTemp;
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
	
	public String toString(){
		if(p!=null){
			return p.toString();
		}else{
			return "null";
		}
	}
	
	/**
	 * 将所有的数据转换成一个字符串输出
	 * @return
	 */
	public String AlltoString(){
		String ret="";
		PARRAY_CHAIN pCur=this;
		//递归清理所有下级的节点
		while(pCur!=null && pCur.p!=null){
			ret+= pCur.toString();
			ret+="\n";
			pCur=pCur.next(); //指向下一节点
		}
		return ret;
	}

}
