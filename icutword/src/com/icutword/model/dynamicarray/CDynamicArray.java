package com.icutword.model.dynamicarray;

import com.icutword.model.CONST;
import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;
import com.icutword.utility.CStringFunction;
//import com.icutword.utility.CUtility;

/**
 * 动态数组定义类，调用一个ARRATY_CHAIN来作实际的存储
 * 
 * @author liujunsong 2012-6-4 重新整理编写
 * 
 */
public class CDynamicArray {
	// 常量定义
//	private int _MIN_PROBLEM = 1;

	// 本地变量定义
	// The row and col of the array
	public int m_nCol; // 数组列数
	public int m_nRow;// 数组行数The row and col of the array
	public boolean m_bRowFirst;
	public PARRAY_CHAIN m_pHead = new PARRAY_CHAIN(null);

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 构造函数
	 * 
	 * @param bRowFirst
	 */
	public CDynamicArray(boolean bRowFirst) {
		m_pHead = null;
		m_nRow = 0;
		m_nCol = 0;
		m_bRowFirst = bRowFirst;
	}

	/**
	 * 无参数的构造函数,默认以列优先
	 */
	public CDynamicArray() {
		m_pHead = null;
		m_nRow = 0;
		m_nCol = 0;
		m_bRowFirst = false;
	}

	/**
	 * 解构函数
	 */
	public void Destroy() {
		// 调用PMYCONTEXT类的内存清理功能
		// 以释放链表内存
		if (m_pHead != null) {
			m_pHead.DestroyAll();
		}

		// 删除本地的变量
		m_pHead = null;
		m_nRow = 0;
		m_nCol = 0;
	}

	// ////////////////////////////////////////////////////////////////////
	// 本地功能方法定义
	// ////////////////////////////////////////////////////////////////////

	/*********************************************************************
	 * 
	 * Func Name : GetElement
	 * 
	 * Description: Get the element value
	 * 
	 * 
	 * Parameters : nRow: the row number nCol: the Column number
	 * 
	 * Returns : the element value if found,else Infinite value Author : Kevin
	 * Zhang History : 1.create 2001-11-7
	 *********************************************************************/
	/**
	 * 从链表中检索一个元素，顺序检索，返回，如果找不到，返回无限大。
	 * 
	 * @param nRow
	 * @param nCol
	 * @param pStart
	 *            开始点
	 * @param ppRet
	 *            如果设置了此参数，设置其返回节点数据
	 * @return
	 */
	public double GetElement(int nRow, int nCol, PARRAY_CHAIN pStart,
			PPARRAY_CHAIN ppRet) {
		PARRAY_CHAIN pCur = _copy(pStart);

		if (_isNullPoint(pStart)) {
			pCur = _copy(m_pHead);
		}

		// 二级指针对于地址的调用，采用setValue方法进行模拟
		// 初始化返回指针点
		if (ppRet != null) {
			ppRet.setValue(null);
		}

		// Judge if the row and col is overflow
		// 判断行号列号是否越界
		if (nRow > (int) m_nRow || nCol > (int) m_nCol) {
			return CONST.INFINITE_VALUE;
		}

		// 从开始点进行循环查找
		if (m_bRowFirst) {
			while (_isNotNullPoint(pCur)
					&& (nRow != -1 && (int) pCur.p.row < nRow || (nCol != -1
							&& (int) pCur.p.row == nRow && (int) pCur.p.col < nCol))) {

				if (ppRet != null) {
					ppRet.setValue(pCur); // ppRet指向最后一个有效点
				}

				pCur = _copy(pCur.next()); // 指向下一个节点
			}
		} else {
			while (_isNotNullPoint(pCur)
					&& (nCol != -1 && (int) pCur.p.col < nCol || ((int) pCur.p.col == nCol
							&& nRow != -1 && (int) pCur.p.row < nRow))) {
				if (ppRet != null) {
					ppRet.setValue(pCur);
				}

				pCur = _copy(pCur.next()); // 指向下一个节点
			}
		}

		// 查找工作结束,pCur是当前节点
		// Find the same position
		if (_isNotNullPoint(pCur) && ((int) pCur.p.row == nRow || nRow == -1)
				&& ((int) pCur.p.col == nCol || nCol == -1)) {
			if (ppRet != null) {
				ppRet.setValue(pCur); // 设置当前节点为返回点
			}
			return pCur.p.value;
		} else {
			return CONST.INFINITE_VALUE; // 返回一个代表无限的值，表示没有找到
		}
	}

	/**
	 * 设置动态数组的数据值，只设置数值，不修改内容
	 * 
	 * @param nRow
	 *            行号
	 * @param nCol
	 *            列号
	 * @param fValue
	 *            数值
	 * @param nPOS
	 *            位置
	 * @return
	 */
	public int SetElement(int nRow, int nCol, double fValue, int nPOS) {
		return SetElement(nRow, nCol, fValue, nPOS, null);
	}

	/*********************************************************************
	 * 
	 * Func Name : SetElement
	 * 
	 * Description: Set the element value
	 * 
	 * 
	 * Parameters : nRow: the row number nCol: the Column number fValue: the
	 * value to be set Returns : the element value if found,else Infinite value
	 * Author : Kevin Zhang History : 1.create 2001-11-7
	 *********************************************************************/

	public int SetElement(int nRow, int nCol, double fValue, int nPOS,
			char[] sWord) {
		PARRAY_CHAIN pCur = _copy(m_pHead);
		PARRAY_CHAIN pPre = new PARRAY_CHAIN(null);
		PARRAY_CHAIN pAdd = new PARRAY_CHAIN(null);

		// The pointer of array chain
		if (nRow > m_nRow)// Set the array row
			m_nRow = nRow;
		if (nCol > m_nCol)// Set the array col
			m_nCol = nCol;

		// 循环找到相应的节点，或者是最近的节点
		if (m_bRowFirst) {
			while (_isNotNullPoint(pCur)
					&& (pCur.p.row < nRow || (pCur.p.row == nRow && pCur.p.col < nCol))) {

				pPre = _copy(pCur); // 前一节点
				pCur = _copy(pCur.next()); // 下一节点
			}
		} else {
			while (_isNotNullPoint(pCur)
					&& (pCur.p.col < nCol || (pCur.p.col == nCol && pCur.p.row < nRow))) {
				pPre = _copy(pCur);// 前一节点
				pCur = _copy(pCur.next());// 下一节点
			}
		}

		// 循环结束以后，判断是否找到了这样一个节点
		// Find the same position
		if (_isNotNullPoint(pCur) && pCur.p.row == nRow && pCur.p.col == nCol) {
			pCur.p.value = fValue;// Set the value
			pCur.p.nPOS = nPOS;// Set the possible POS
			// TODO:判断是否需要设置sWord[]
			// 这里的原始设计，在setitem的时候，sword是不更新的。是否符合要求，待讨论。
		} else {
			pAdd = new PARRAY_CHAIN(new ARRAY_CHAIN()); // 定义新对象
			pAdd.p.col = nCol;// get the value
			pAdd.p.row = nRow;
			pAdd.p.value = fValue;
			pAdd.p.nPOS = nPOS;

			if (sWord != null)// sWord is not empty
			{
				pAdd.p.nWordLen = CStringFunction.strlen(sWord);
				pAdd.p.sWord = new char[pAdd.p.nWordLen + 1];
				CStringFunction.strcpy(pAdd.p.sWord, sWord);
			} else {
				// sWord is Empty
				pAdd.p.nWordLen = 0;
				pAdd.p.sWord = null;
			}

			pAdd.p.next = pCur; // 在当前节点前面设置一个新节点

			if (_isNullPoint(pPre)) {
				m_pHead = _copy(pAdd); // 上一个节点为空，设置m_pHead为新增加节点
			} else {
				pPre.p.next = pAdd;
			}
		}
		return 0;
	}

	/*********************************************************************
	 * 
	 * Func Name : operator =
	 * 
	 * Description: operator =
	 * 
	 * 
	 * Parameters :
	 * 
	 * 
	 * Returns : Author : Kevin Chang History : 1.create 2001-11-7
	 *********************************************************************/

	/**
	 * 根据另外一个Array来复制出一个Array出来 实际上会修改当前的这个DynamicArray对象. 原来的方法是重载一个操作符号=
	 * 
	 * @return
	 */
	public CDynamicArray Copy(CDynamicArray array) {
		PARRAY_CHAIN pCur = null;// The pointer of array chain
		SetEmpty();
		pCur = array.m_pHead;
		while (_isNotNullPoint(pCur)) {
			this.SetElement(pCur.p.row, pCur.p.col, pCur.p.value, pCur.p.nPOS,
					pCur.p.sWord);
			pCur = pCur.next();
		}
		return this;
	}

	/**
	 * ==重载符号的等价方法，equals判断两个CDynamicArray数据是否相同 仅比较数组的数据值，不比较sWord[]
	 * 
	 * @param array
	 * @return
	 */
	public boolean equals(CDynamicArray array) {
		PARRAY_CHAIN pFirst, pSecond;// The pointer of array chain
		if (m_nCol != array.m_nCol || m_nRow != array.m_nRow) {
			// Row or Col not equal
			return false;
		}

		pFirst = array.m_pHead;
		pSecond = m_pHead;
		while (_isNotNullPoint(pFirst) && _isNotNullPoint(pSecond)
				&& pFirst.p.row == pSecond.p.row
				&& pFirst.p.col == pSecond.p.col
				&& pFirst.p.value == pSecond.p.value) {
			pFirst = _copy(pFirst.next());
			pSecond = _copy(pSecond.next());
		}
		// if(pFirst==NULL&&pSecond==NULL)
		if (_isNullPoint(pFirst) && _isNullPoint(pSecond))
			return true;
		return false;
	}

	/**
	 * 清空数据的方法,通过调用Destory方法实现
	 */
	public void SetEmpty() {
		Destroy();
	}

	public boolean GetElement2(int nRow, int nCol, Pdouble pRetValue) {
		return GetElement2(nRow, nCol, pRetValue, null, null);
	}
	
	public boolean GetElement2(int nRow, int nCol, Pdouble pRetValue,
			Pint pRetPOS) {
		return GetElement2(nRow, nCol, pRetValue, pRetPOS, null);
	}

	/*********************************************************************
	 * 
	 * Func Name : GetElement
	 * 
	 * Description: Get the element value
	 * 
	 * 
	 * Parameters : nRow: the row number nCol: the Column number
	 * 
	 * Returns : the element value if found,else Infinite value Author : Kevin
	 * Zhang History : 1.create 2002-4-22
	 *********************************************************************/

	public boolean GetElement2(int nRow, int nCol, Pdouble pRetValue,
			Pint pRetPOS, char[] sRetWord) {
		PARRAY_CHAIN pCur = m_pHead;
		pRetValue.value = CONST.INFINITE_VALUE;

		if (pRetPOS != null) {
			pRetPOS.value = 0;
		}

		if (nRow > (int) m_nRow || nCol > (int) m_nCol) {
			// Judge if the row and col is overflow
			return false;
		}

		if (m_bRowFirst) {
			while (_isNotNullPoint(pCur)
					&& (nRow != -1 && (int) pCur.p.row < nRow || (nCol != -1
							&& (int) pCur.p.row == nRow && (int) pCur.p.col < nCol))) {
				pCur = _copy(pCur.next());
			}
		} else {
			while (_isNotNullPoint(pCur)
					&& (nCol != -1 && (int) pCur.p.col < nCol || ((int) pCur.p.col == nCol
							&& nRow != -1 && (int) pCur.p.row < nRow))) {
				pCur = _copy(pCur.next());
			}
		}

		// Find the same position
		// Find it and return the value
		// 在查找成功的情况下，设置返回值
		if (_isNotNullPoint(pCur) && ((int) pCur.p.row == nRow || nRow == -1)
				&& ((int) pCur.p.col == nCol || nCol == -1)) {
			pRetValue.value = pCur.p.value;

			if (pRetPOS != null) {
				pRetPOS.value = pCur.p.nPOS;
			}

			if (sRetWord != null)// sWord is not empty
			{
				CStringFunction.strcpy(sRetWord, pCur.p.sWord);
			}
		}
		return true;
	}

	/**
	 * 得到列表的开头
	 * 
	 * @return
	 */
	public PARRAY_CHAIN GetHead() {
		return m_pHead;
	}

	// Get the tail Element buffer and return the count of elements
	/**
	 * 得到链表的最后一个节点，并返回链表的总长度。
	 * 链表的最后一个节点，通过指针的方式来返回。
	 * @param pTailRet
	 * @return
	 */
	public int GetTail(PPARRAY_CHAIN pTailRet) {
		PARRAY_CHAIN pCur = m_pHead, pPrev = null;
		// pCur: current node;pPrev:previous node
		int nCount = 0;
		while (pCur != null && pCur.p != null) {
			nCount += 1;
			pPrev = _copy(pCur);
			pCur = _copy(pCur.next());
		}
		pTailRet.setValue(pPrev);
		return nCount;
	}

	/**
	 * 设置链表的排序规则，是否行优先，否则就是列优先
	 * @param RowFirst
	 * @return
	 */
	public boolean SetRowFirst(boolean RowFirst) {
		m_bRowFirst = RowFirst;
		return true;
	}

	/**
	 * 设置按照行号优先的规则来进行链表的排序
	 * @return
	 */
	public boolean SetRowFirst() {
		m_bRowFirst = true;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// 新增加的私有功能方法
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 自定义的拷贝两个指针的方法，因为对于Java来说，需要模拟原来的 pCur=pTemp,这样两个指针地址的拷贝动作。
	 * 
	 * @param src
	 * @return
	 */
	private PARRAY_CHAIN _copy(PARRAY_CHAIN src) {
		if (src != null) {
			PARRAY_CHAIN ret = new PARRAY_CHAIN(src.p);
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * 判断给定的一级指针是否是一个空指针
	 * 
	 * @param point
	 * @return
	 */
	private boolean _isNullPoint(PARRAY_CHAIN point) {
		return (point == null || point.p == null);
	}

	/**
	 * 判断给定的一级指针是否是一个有效指针
	 * 
	 * @param point
	 * @return
	 */
	private boolean _isNotNullPoint(PARRAY_CHAIN point) {
		return !(point == null || point.p == null);
	}
	
	/**
	 * 将这个动态数组的内容，转换成一个字符串来输出
	 */
	public String toString(){
		String sret="";
		sret="{m_nCol="+m_nCol+";m_nRow="+m_nRow+";m_bRowFirst="+m_bRowFirst;
		sret+="m_pHead=\n"+(m_pHead==null?"null":m_pHead.AlltoString())+"}";
		return sret;
	}
}
