package com.icutword.segment.nshortpath;

import org.apache.log4j.Logger;

import com.icutword.model.CONST;
import com.icutword.model.dynamicarray.CDynamicArray;
import com.icutword.model.dynamicarray.PARRAY_CHAIN;
import com.icutword.model.dynamicarray.PPARRAY_CHAIN;
import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;
import com.icutword.segment.nshortpath.queue.CQueue;

/**
 * ��״ͼ�����·���㷨������Ҫʹ��Queue��Ϊ�м���ת����
 * 
 * @author liujunsong
 * 
 */
public class CNShortPath {
	static Logger logger = Logger.getLogger("logger");
	
	public int MAX_SEGMENT_NUM = 10; // ����з�·��

	int m_nResultCount; // �зֽ��·��
	CDynamicArray m_apCost; // һ����̬���飬������¼ʵ�ʵĴ洢��·,���������
	int m_nValueKind;// The number of value kinds
	int m_nVertex;// The number of vertex in the graph
	CQueue m_pParent[][];// The 2-dimension array for the nodes//��������
	double m_pWeight[][];// The weight of node

	/**
	 * ���캯��
	 * 
	 * @param apCost
	 *            һ����̬������,��̬����,���������������
	 * @param nValueKind
	 *            ����ֵ������
	 */
	public CNShortPath(CDynamicArray apCost_CDynamicArray, int nValueKind) {
		m_apCost = new CDynamicArray().Copy(apCost_CDynamicArray);// Set the
																	// cost
		m_nValueKind = nValueKind;// Set the value kind
		m_nVertex = apCost_CDynamicArray.m_nCol + 1; // ���������
		if (m_nVertex < apCost_CDynamicArray.m_nRow + 1)
			m_nVertex = apCost_CDynamicArray.m_nRow + 1;// Get the vertex
														// numbers ȡ���е����ֵ+1

		// m_pWeight����洢��������ڵ��Ȩ��
		m_pWeight = new double[m_nVertex - 1][m_nVertex];

		// m_pParent�Ǵ���Ӵ˵㿪ʼ��һ������������������ͬ
		m_pParent = new CQueue[m_nVertex - 1][m_nVertex];
		for (int i = 0; i < m_nVertex - 1; i++) {
			for (int j = 0; j < m_nVertex; j++) {
				m_pParent[i][j] = new CQueue();
				m_pWeight[i][j] = 0;
			}
		}

	}

	/**
	 * �������·�������룺m_apCost,
	 * 
	 * @return
	 */
	public int ShortPath() {
		int nCurNode = 1, nPreNode, i, nIndex;
		double eWeight;
		PARRAY_CHAIN pEdgeList;
		PPARRAY_CHAIN ppEdgeList = new PPARRAY_CHAIN(null);

		// ѭ����ʼ��,���н���ѭ��
		for (; nCurNode < m_nVertex; nCurNode++) {
			// ����һ���µ�CQueue����
			CQueue queWork = new CQueue();

			// Get all the edges
			// �õ����нڵ�Ŀ�ʼָ�룬��Ϊ-1,��ΪnCurNode
			eWeight = m_apCost.GetElement(-1, nCurNode, null, ppEdgeList);
			pEdgeList = ppEdgeList.p;

			// Step1:�Ӷ�̬�����м������ݣ���������ѹ�����֮�С�
			// ���룺��̬����
			// �����queWork
			// �Զ�̬����Ľڵ���б���ѭ��,pEdgeList��һ����Ӧ��ָ��
			// ֻҪ�������ڵ�ǰ�е�
			while (pEdgeList != null && pEdgeList.p != null
					&& pEdgeList.p.col == nCurNode) {
				nPreNode = pEdgeList.p.row;
				eWeight = pEdgeList.p.value;// Get the value of edges
				for (i = 0; i < m_nValueKind; i++) {
					// Push the weight and the pre node infomation
					// ѹ��Ȩ�غ�ǰһ�ڵ����Ϣ
					if (nPreNode > 0) {
						// ����������ڵ�֮��ľ���Ϊ���������
						if (m_pWeight[nPreNode - 1][i] == CONST.INFINITE_VALUE) {
							break;
						}

						// ���𣬽�·��ѹ�����Queue������:ǰһ�ڵ㣬��ǰ�ڵ㣬Ȩ��
						queWork.Push(nPreNode, i, eWeight
								+ m_pWeight[nPreNode - 1][i]);
					} else {
						// �����������0�������һ�η��ʵ���ѹ���ջ������
						// ǰһ�ڵ㣬��ǰ�ڵ㣬Ȩ��
						queWork.Push(nPreNode, i, eWeight);
						break;
					}
				}// end for
				pEdgeList = pEdgeList.next();

			}// whileѭ������
 
			logger.debug("nCurNode:" + nCurNode + " queWork:" + queWork.AlltoString());

			// Now get the result queue which sort as weight.
			// Set the current node information

			// Step2:��ʼ������õ�m_pWeight,��ȫ����ʼ��Ϊ�����
			// �ȳ�ʼ��Ȩ������
			for (i = 0; i < m_nValueKind; i++) {
				m_pWeight[nCurNode - 1][i] = CONST.INFINITE_VALUE;
			}

			// Step3:����Queue������ת
			// ��ԭ��������������ݣ�ȫ��ɸѡ�Ժ�ת����������ȥ
			// �Զ�����������������Ȩ�ص���Ϊ���
			i = 0;
			Pint pnPreNode = new Pint();
			Pint pnIndex = new Pint();
			Pdouble peWeight = new Pdouble();
			while (i < m_nValueKind
					&& queWork.Pop(pnPreNode, pnIndex, peWeight) != -1) {
				// Set the current node weight and parent
				nPreNode = pnPreNode.value;
				nIndex = pnIndex.value;
				eWeight = peWeight.value;

				if (m_pWeight[nCurNode - 1][i] == CONST.INFINITE_VALUE) {
					// ���û��Ȩ�أ�������Ȩ��
					m_pWeight[nCurNode - 1][i] = eWeight;
				} else if (m_pWeight[nCurNode - 1][i] < eWeight) {
					// Next queue
					i++;// Go next queue and record next weight
					if (i == m_nValueKind) {
						// Get the last position
						break;
					}
					m_pWeight[nCurNode - 1][i] = eWeight;
				}

				// �ڵ�ǰ�ڵ��ϣ�ѹ��һ���ڵ����ݽ���,�൱��
				// ��queWork���浯����Ȼ����ѹ�뵽m_pParent[][]����ȥ
				// һ��ѭ����ֻѹ��һ���ڵ��ȥ
				// ���Ȩ��û���ˡ�
				m_pParent[nCurNode - 1][i].Push(nPreNode, nIndex);
			}
		}// for ѭ������

		return 1;
	}

	/**
	 * ����õ����·��
	 * 
	 * @param nNode
	 * @param nIndex
	 * @param nResult
	 * @param bBest
	 */
	private void _GetPaths(int nNode, int nIndex, int nResult[][], boolean bBest) {
		CQueue queResult = new CQueue();
		int nCurNode, nCurIndex, nParentNode, nParentIndex, nResultIndex = 0;

		if (m_nResultCount >= MAX_SEGMENT_NUM)// Only need 10 result
			return;
		nResult[m_nResultCount][nResultIndex] = -1;// Init the result
		queResult.Push(nNode, nIndex);
		nCurNode = nNode;
		nCurIndex = nIndex;
		Pint pnCurNode = new Pint();
		Pint pnCurIndex = new Pint();

		Pint pnParentNode = new Pint();
		Pint pnParentIndex = new Pint();

		boolean bFirstGet;
		while (!queResult.IsEmpty()) {
			while (nCurNode > 0)//
			{// Get its parent and store them in nParentNode,nParentIndex

				if (m_pParent[nCurNode - 1][nCurIndex].Pop(pnParentNode,
						pnParentIndex, null, false, true) != -1) {
					nParentNode = pnParentNode.value;
					nParentIndex = pnParentIndex.value;
					nCurNode = nParentNode;
					nCurIndex = nParentIndex;
				}
				nParentNode = pnParentNode.value;
				nParentIndex = pnParentIndex.value;

				if (nCurNode > 0)
					queResult.Push(nCurNode, nCurIndex);
			}

			if (nCurNode == 0) { // Get a path and output
				nResult[m_nResultCount][nResultIndex++] = nCurNode;// Get the
																	// first
																	// node
				bFirstGet = true;
				nParentNode = nCurNode;

				while (queResult.Pop(pnCurNode, pnCurIndex, null, false,
						bFirstGet) != -1) {
					nCurNode = pnCurNode.value;
					nCurIndex = pnCurIndex.value;

					nResult[m_nResultCount][nResultIndex++] = nCurNode;
					bFirstGet = false;
					nParentNode = nCurNode;
				}
				nCurNode = pnCurNode.value;
				nCurIndex = pnCurIndex.value;

				nResult[m_nResultCount][nResultIndex] = -1;// Set the end
				m_nResultCount += 1;// The number of result add by 1
				if (m_nResultCount >= MAX_SEGMENT_NUM)// Only need 10 result
					return;
				nResultIndex = 0;
				nResult[m_nResultCount][nResultIndex] = -1;// Init the result

				if (bBest)// Return the best result, ignore others
					return;
			}
			queResult.Pop(pnCurNode, pnCurIndex, null, false, true);// Read the
																	// top node
			while (queResult.IsEmpty() == false
					&& (m_pParent[nCurNode - 1][nCurIndex].IsSingle() || m_pParent[nCurNode - 1][nCurIndex]
							.IsEmpty(true))) {
				queResult.Pop(pnCurNode, pnCurIndex, null);// Get rid of it
				queResult.Pop(pnCurNode, pnCurIndex, null, false, true);// Read
																		// the
																		// top
																		// node
			}
			if (queResult.IsEmpty() == false
					&& m_pParent[nCurNode - 1][nCurIndex].IsEmpty(true) == false) {
				m_pParent[nCurNode - 1][nCurIndex].Pop(pnParentNode,
						pnParentIndex, null, false, false);

				nParentNode = pnParentNode.value;
				nParentIndex = pnParentIndex.value;

				nCurNode = nParentNode;
				nCurIndex = nParentIndex;
				if (nCurNode > 0)
					queResult.Push(nCurNode, nCurIndex);
			}
		}
	}

	/**
	 * ����������·���ִʵĽ��,����Ĵ洢��Ҫ�ڵ���ǰ����
	 * 
	 * @param nResult
	 *            �ִʽ��·��
	 * @param bBest
	 *            �Ƿ�Ҫ���Ž�
	 * @param npCount
	 *            ��������������ص�������Ч�к�
	 * @return ��Զ����1
	 */
	public int Output(int nResult[][], boolean bBest, Pint npCount) {
		// sResult is a
		int i;

		m_nResultCount = 0;// The
		if (m_nVertex < 2) {
			nResult[0][0] = 0;
			nResult[0][1] = 1;
			npCount.value = 1;
			return 1;
		}
		for (i = 0; i < m_nValueKind
				&& m_pWeight[m_nVertex - 2][i] < CONST.INFINITE_VALUE; i++) {
			_GetPaths(m_nVertex - 1, i, nResult, bBest);
			npCount.value = m_nResultCount;
			if (nResult[i][0] != -1 && bBest)// Get the best answer
				return 1;
			if (m_nResultCount >= MAX_SEGMENT_NUM)// Only need 10 result
				return 1;
		}
		return 1;
	}

	/**
	 * ����ǰ��CNShortPathת����һ���ַ�����ʾ�����Գ���ʹ��
	 */
	public String toString() {
		String sret = "{m_nResultCount=" + m_nResultCount + ",m_nValueKind="
				+ m_nValueKind;
		sret += ",m_nVertex=" + m_nVertex + "}\n";
		// sret+=",m_apCost="+m_apCost.toString();
		sret += ",m_pParent=\n";
		for (int i = 0; i < m_nVertex - 1; i++) {
			for (int j = 0; j < m_nVertex; j++) {
				sret += m_pParent[i][j].AlltoString() + "|";
			}
			sret += "\n";
		}
		sret += "m_pweight=\n";
		for (int i = 0; i < m_nVertex - 1; i++) {
			for (int j = 0; j < m_nVertex; j++) {
				sret += m_pWeight[i][j] + "|";
			}
			sret += "\n";
		}
		return sret;
	}

//	private void _l(String s) {
//		System.out.print("CNShortPath...       ----->");
//		System.out.println(s);
//	}
}
