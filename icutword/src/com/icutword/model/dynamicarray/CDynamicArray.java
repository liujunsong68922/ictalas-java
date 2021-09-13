package com.icutword.model.dynamicarray;

import com.icutword.model.CONST;
import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;
import com.icutword.utility.CStringFunction;
//import com.icutword.utility.CUtility;

/**
 * ��̬���鶨���࣬����һ��ARRATY_CHAIN����ʵ�ʵĴ洢
 * 
 * @author liujunsong 2012-6-4 ���������д
 * 
 */
public class CDynamicArray {
	// ��������
//	private int _MIN_PROBLEM = 1;

	// ���ر�������
	// The row and col of the array
	public int m_nCol; // ��������
	public int m_nRow;// ��������The row and col of the array
	public boolean m_bRowFirst;
	public PARRAY_CHAIN m_pHead = new PARRAY_CHAIN(null);

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	/**
	 * ���캯��
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
	 * �޲����Ĺ��캯��,Ĭ����������
	 */
	public CDynamicArray() {
		m_pHead = null;
		m_nRow = 0;
		m_nCol = 0;
		m_bRowFirst = false;
	}

	/**
	 * �⹹����
	 */
	public void Destroy() {
		// ����PMYCONTEXT����ڴ�������
		// ���ͷ������ڴ�
		if (m_pHead != null) {
			m_pHead.DestroyAll();
		}

		// ɾ�����صı���
		m_pHead = null;
		m_nRow = 0;
		m_nCol = 0;
	}

	// ////////////////////////////////////////////////////////////////////
	// ���ع��ܷ�������
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
	 * �������м���һ��Ԫ�أ�˳����������أ�����Ҳ������������޴�
	 * 
	 * @param nRow
	 * @param nCol
	 * @param pStart
	 *            ��ʼ��
	 * @param ppRet
	 *            ��������˴˲����������䷵�ؽڵ�����
	 * @return
	 */
	public double GetElement(int nRow, int nCol, PARRAY_CHAIN pStart,
			PPARRAY_CHAIN ppRet) {
		PARRAY_CHAIN pCur = _copy(pStart);

		if (_isNullPoint(pStart)) {
			pCur = _copy(m_pHead);
		}

		// ����ָ����ڵ�ַ�ĵ��ã�����setValue��������ģ��
		// ��ʼ������ָ���
		if (ppRet != null) {
			ppRet.setValue(null);
		}

		// Judge if the row and col is overflow
		// �ж��к��к��Ƿ�Խ��
		if (nRow > (int) m_nRow || nCol > (int) m_nCol) {
			return CONST.INFINITE_VALUE;
		}

		// �ӿ�ʼ�����ѭ������
		if (m_bRowFirst) {
			while (_isNotNullPoint(pCur)
					&& (nRow != -1 && (int) pCur.p.row < nRow || (nCol != -1
							&& (int) pCur.p.row == nRow && (int) pCur.p.col < nCol))) {

				if (ppRet != null) {
					ppRet.setValue(pCur); // ppRetָ�����һ����Ч��
				}

				pCur = _copy(pCur.next()); // ָ����һ���ڵ�
			}
		} else {
			while (_isNotNullPoint(pCur)
					&& (nCol != -1 && (int) pCur.p.col < nCol || ((int) pCur.p.col == nCol
							&& nRow != -1 && (int) pCur.p.row < nRow))) {
				if (ppRet != null) {
					ppRet.setValue(pCur);
				}

				pCur = _copy(pCur.next()); // ָ����һ���ڵ�
			}
		}

		// ���ҹ�������,pCur�ǵ�ǰ�ڵ�
		// Find the same position
		if (_isNotNullPoint(pCur) && ((int) pCur.p.row == nRow || nRow == -1)
				&& ((int) pCur.p.col == nCol || nCol == -1)) {
			if (ppRet != null) {
				ppRet.setValue(pCur); // ���õ�ǰ�ڵ�Ϊ���ص�
			}
			return pCur.p.value;
		} else {
			return CONST.INFINITE_VALUE; // ����һ���������޵�ֵ����ʾû���ҵ�
		}
	}

	/**
	 * ���ö�̬���������ֵ��ֻ������ֵ�����޸�����
	 * 
	 * @param nRow
	 *            �к�
	 * @param nCol
	 *            �к�
	 * @param fValue
	 *            ��ֵ
	 * @param nPOS
	 *            λ��
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

		// ѭ���ҵ���Ӧ�Ľڵ㣬����������Ľڵ�
		if (m_bRowFirst) {
			while (_isNotNullPoint(pCur)
					&& (pCur.p.row < nRow || (pCur.p.row == nRow && pCur.p.col < nCol))) {

				pPre = _copy(pCur); // ǰһ�ڵ�
				pCur = _copy(pCur.next()); // ��һ�ڵ�
			}
		} else {
			while (_isNotNullPoint(pCur)
					&& (pCur.p.col < nCol || (pCur.p.col == nCol && pCur.p.row < nRow))) {
				pPre = _copy(pCur);// ǰһ�ڵ�
				pCur = _copy(pCur.next());// ��һ�ڵ�
			}
		}

		// ѭ�������Ժ��ж��Ƿ��ҵ�������һ���ڵ�
		// Find the same position
		if (_isNotNullPoint(pCur) && pCur.p.row == nRow && pCur.p.col == nCol) {
			pCur.p.value = fValue;// Set the value
			pCur.p.nPOS = nPOS;// Set the possible POS
			// TODO:�ж��Ƿ���Ҫ����sWord[]
			// �����ԭʼ��ƣ���setitem��ʱ��sword�ǲ����µġ��Ƿ����Ҫ�󣬴����ۡ�
		} else {
			pAdd = new PARRAY_CHAIN(new ARRAY_CHAIN()); // �����¶���
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

			pAdd.p.next = pCur; // �ڵ�ǰ�ڵ�ǰ������һ���½ڵ�

			if (_isNullPoint(pPre)) {
				m_pHead = _copy(pAdd); // ��һ���ڵ�Ϊ�գ�����m_pHeadΪ�����ӽڵ�
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
	 * ��������һ��Array�����Ƴ�һ��Array���� ʵ���ϻ��޸ĵ�ǰ�����DynamicArray����. ԭ���ķ���������һ����������=
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
	 * ==���ط��ŵĵȼ۷�����equals�ж�����CDynamicArray�����Ƿ���ͬ ���Ƚ����������ֵ�����Ƚ�sWord[]
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
	 * ������ݵķ���,ͨ������Destory����ʵ��
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
		// �ڲ��ҳɹ�������£����÷���ֵ
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
	 * �õ��б�Ŀ�ͷ
	 * 
	 * @return
	 */
	public PARRAY_CHAIN GetHead() {
		return m_pHead;
	}

	// Get the tail Element buffer and return the count of elements
	/**
	 * �õ���������һ���ڵ㣬������������ܳ��ȡ�
	 * ��������һ���ڵ㣬ͨ��ָ��ķ�ʽ�����ء�
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
	 * �����������������Ƿ������ȣ��������������
	 * @param RowFirst
	 * @return
	 */
	public boolean SetRowFirst(boolean RowFirst) {
		m_bRowFirst = RowFirst;
		return true;
	}

	/**
	 * ���ð����к����ȵĹ������������������
	 * @return
	 */
	public boolean SetRowFirst() {
		m_bRowFirst = true;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// �����ӵ�˽�й��ܷ���
	// ////////////////////////////////////////////////////////////////////
	/**
	 * �Զ���Ŀ�������ָ��ķ�������Ϊ����Java��˵����Ҫģ��ԭ���� pCur=pTemp,��������ָ���ַ�Ŀ���������
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
	 * �жϸ�����һ��ָ���Ƿ���һ����ָ��
	 * 
	 * @param point
	 * @return
	 */
	private boolean _isNullPoint(PARRAY_CHAIN point) {
		return (point == null || point.p == null);
	}

	/**
	 * �жϸ�����һ��ָ���Ƿ���һ����Чָ��
	 * 
	 * @param point
	 * @return
	 */
	private boolean _isNotNullPoint(PARRAY_CHAIN point) {
		return !(point == null || point.p == null);
	}
	
	/**
	 * �������̬��������ݣ�ת����һ���ַ��������
	 */
	public String toString(){
		String sret="";
		sret="{m_nCol="+m_nCol+";m_nRow="+m_nRow+";m_bRowFirst="+m_bRowFirst;
		sret+="m_pHead=\n"+(m_pHead==null?"null":m_pHead.AlltoString())+"}";
		return sret;
	}
}
