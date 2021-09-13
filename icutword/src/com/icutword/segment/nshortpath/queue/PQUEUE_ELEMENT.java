package com.icutword.segment.nshortpath.queue;

//import com.icutword.model.dynamicarray.PARRAY_CHAIN;

//import com.icutword.model.contextstat.PMYCONTEXT;

/**
 * 指向QUEUE_ELEMENT的指针对象模拟,一级指针
 * 
 * @author liujunsong
 * 
 */
public class PQUEUE_ELEMENT {
	public QUEUE_ELEMENT p = null;

	/**
	 * 通用构造函数
	 * 
	 * @param point
	 */
	public PQUEUE_ELEMENT(QUEUE_ELEMENT point) {
		p = point;
	}

	/**
	 * 封装next()方法，可以返回null
	 * 
	 * @return
	 */
	public PQUEUE_ELEMENT next() {
		if (p != null) {
			return p.next;
		} else {
			return null;
		}
	}

	/**
	 * 从当前节点开始，递归到各个下级节点，然后释放所有链表的内存 这个方法用来释放内存。
	 */
	public void DestroyAll() {
		PQUEUE_ELEMENT pCur = next(), pTemp;
		// 递归清理所有下级的节点
		while (pCur != null) {
			pTemp = pCur.next(); // 得到下一节点
			pCur.p.Destroy(); // 当前节点内存释放
			pCur = pTemp; // 指向下一节点
		}
		// 清理本级指针对应节点
		if (p != null) {
			p.Destroy();
			p = null;
		}
	}

	public String toString() {
		if (p != null) {
			return p.toString();
		} else {
			return "NULL";
		}
	}

	/**
	 * 将所有的数据转换成一个字符串输出
	 * 
	 * @return
	 */
	public String AlltoString() {
		String ret = "";
		PQUEUE_ELEMENT pCur = this;
		// 递归清理所有下级的节点
		while (pCur != null) {
			ret += pCur.toString();
				ret += "->";
			pCur = pCur.next(); // 指向下一节点
		}
		return ret;
	}
}
