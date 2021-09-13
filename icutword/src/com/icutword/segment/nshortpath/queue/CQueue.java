package com.icutword.segment.nshortpath.queue;

import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;

/**
 * 对CQueue的java代码实现
 * 
 * @author liujunsong
 * 
 */
public class CQueue {
	/**
	 * 本地变量定义
	 */
	private PQUEUE_ELEMENT m_pHead = null;// The chain sort according the weight
											// of shortest path
	private PQUEUE_ELEMENT m_pLastAccess = null;// The node last accessed

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 构造函数
	 */
	public CQueue() {
		m_pHead = null;
		m_pLastAccess = null;
	}

	/**
	 * 结构函数
	 */
	public void Destory() {
		PQUEUE_ELEMENT pCur = _copy(m_pHead), pTemp;// The pointer of queue
													// chain
		while (_isNotNullPoint(pCur)) {
			pTemp = _copy(pCur.next());
			// delete pCur;
			pCur.p.next = null;
			pCur = _copy(pTemp);
		}
		m_pHead = null;
		m_pLastAccess = null;
	}

	// ////////////////////////////////////////////////////////////////////
	// 本地方法
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 队列中压入一个元素
	 * 
	 * @param nValue
	 *            数值
	 * @param nIndex
	 *            索引
	 * @return
	 */
	public int Push(int nValue,// The value for parent node
			int nIndex// number of index in the parent node
	) {
		return Push(nValue, nIndex, 0);
	}

	/**
	 * 队列中压入一条记录,队列按照权重来排序？
	 * 
	 * @param nValue
	 *            父节点值
	 * @param nIndex
	 *            父节点指针
	 * @param eWeight
	 *            权重
	 * @return
	 */
	public int Push(int nValue,// The value for parent node
			int nIndex,// number of index in the parent node
			double eWeight// the weight of last path
	) {// Sort it
		PQUEUE_ELEMENT pAdd, pCur = _copy(m_pHead), pPre = null;
		// The pointer of queue chain

		while (_isNotNullPoint(pCur) && pCur.p.eWeight < eWeight) {
			pPre = _copy(pCur);
			pCur = _copy(pCur.next());
		}

		pAdd = new PQUEUE_ELEMENT(new QUEUE_ELEMENT());
		pAdd.p.nParent = nValue;
		pAdd.p.nIndex = nIndex;
		pAdd.p.eWeight = eWeight;
		pAdd.p.next = _copy(pCur);

		if (pPre == null)
			m_pHead = _copy(pAdd);
		else
			pPre.p.next = _copy(pAdd);
		return 1;
	}

	/**
	 * 队列中弹出一个元素
	 * 
	 * @param npValue
	 * @param npIndex
	 * @param epWeight
	 * @return
	 */
	public int Pop(Pint npValue,// The value for parent node
			Pint npIndex,// number of index in the parent node
			Pdouble epWeight) {
		return Pop(npValue, npIndex, epWeight, true, true);
	}

	/**
	 * 队列中弹出一个元素
	 * 
	 * @param npValue
	 * @param npIndex
	 * @param epWeight
	 * @param bModify
	 * @return
	 */
	public int Pop(Pint npValue,// The value for parent node
			Pint npIndex,// number of index in the parent node
			Pdouble epWeight, boolean bModify) {
		return Pop(npValue, npIndex, epWeight, bModify, true);
	}

	/**
	 * 从队列中弹出一个元素
	 * 
	 * @param npValue
	 * @param npIndex
	 * @param epWeight
	 * @param bModify
	 * @param bFirstGet
	 * @return
	 */
	public int Pop(Pint npValue,// The value for parent node
			Pint npIndex,// number of index in the parent node
			Pdouble epWeight,// the weight of last path
			boolean bModify,// not modify the data
			boolean bFirstGet// first get data,just for browse
	) {
		PQUEUE_ELEMENT pTemp;

		if (bModify)
			pTemp = _copy(m_pHead);// The temp buffer
		else {
			if (bFirstGet)// First get the data
				m_pLastAccess = _copy(m_pHead);// The temp buffer
			pTemp = _copy(m_pLastAccess);
		}

		if (_isNullPoint(pTemp))
			return -1;// fail get the value
		if (npValue != null)
			npValue.value = pTemp.p.nParent;
		if (npIndex != null)
			npIndex.value = pTemp.p.nIndex;
		if (epWeight != null)
			epWeight.value = pTemp.p.eWeight;

		if (bModify)// modify and get rid of the node
		{
			m_pHead = _copy(pTemp.next());
			pTemp.p = null;
		} else {
			m_pLastAccess = _copy(pTemp.next());
		}
		return 1;
	}

	/**
	 * 判断是否是一个空的队列,默认参数为false
	 * 
	 * @return
	 */
	public boolean IsEmpty() {
		return IsEmpty(false);
	}

	/**
	 * 判断是否是空队列，如果传入true，按照最后一个指针来判断，否则按照头来判断。
	 * 
	 * @param bBrowsed
	 * @return
	 */
	public boolean IsEmpty(boolean bBrowsed)// bBrowsed=true: judge whether the
											// browse
	// pointer got end.
	{
		if (bBrowsed == true)
			return _isNullPoint(m_pLastAccess);
		return _isNullPoint(m_pHead);
	}

	/**
	 * 判断队列是否有效。
	 * 
	 * @return
	 */
	public boolean IsSingle() {
		// return (m_pHead!=NULL&&m_pHead->next==NULL);
		return this._isNotNullPoint(m_pHead);
	}

	// ////////////////////////////////////////////////////////////////////
	// 增加的私有处理函数
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 判断PQUEUE_ELEMENT是否是一个无效的一级指针
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNullPoint(PQUEUE_ELEMENT pCur) {
		return !(pCur != null && pCur.p != null);
	}

	/**
	 * 判断pCur是否是一个有效的一级指针
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNotNullPoint(PQUEUE_ELEMENT pCur) {
		return (pCur != null && pCur.p != null);
	}

	/**
	 * 利用一个输入的PQUEUE_ELEMENT,复制生成另一个新的PQUEUE_ELEMENT
	 * 
	 * @param src
	 * @return
	 */
	private PQUEUE_ELEMENT _copy(PQUEUE_ELEMENT src) {
		if (src != null) {
			return new PQUEUE_ELEMENT(src.p);
		} else {
			return new PQUEUE_ELEMENT(null);
		}
	}

	/**
	 * 转换成一个字符串
	 */
	public String toString() {
		String sret = "";
		if (m_pHead != null) {
			sret += "m_pHead:" + m_pHead.toString();
		}
		if (m_pLastAccess != null) {
			sret += " m_pLastAccess:" + m_pLastAccess.toString();
		}
		if(sret.equals("")){
			sret="{EMP}";
		}
		return sret;
	}
	
	/**
	 * 转换成一个字符串
	 */
	public String AlltoString() {
		String sret = "";
		if (m_pHead != null) {
			sret += "m_pHead:" + m_pHead.AlltoString();
		}
		if (m_pLastAccess != null) {
			sret += " m_pLastAccess:" + m_pLastAccess.AlltoString();
		}
		if(sret.equals("")){
			sret="{E}";
		}
		return sret;
	}	
}
