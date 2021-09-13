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
 * 网状图的最短路径算法程序，需要使用Queue作为中间结果转换类
 * 
 * @author liujunsong
 * 
 */
public class CNShortPath {
	static Logger logger = Logger.getLogger("logger");
	
	public int MAX_SEGMENT_NUM = 10; // 最大切分路径

	int m_nResultCount; // 切分结果路径
	CDynamicArray m_apCost; // 一个动态数组，用来记录实际的存储链路,这个是输入
	int m_nValueKind;// The number of value kinds
	int m_nVertex;// The number of vertex in the graph
	CQueue m_pParent[][];// The 2-dimension array for the nodes//这个是输出
	double m_pWeight[][];// The weight of node

	/**
	 * 构造函数
	 * 
	 * @param apCost
	 *            一个动态数组类,动态数组,这个代表输入数据
	 * @param nValueKind
	 *            数据值的类型
	 */
	public CNShortPath(CDynamicArray apCost_CDynamicArray, int nValueKind) {
		m_apCost = new CDynamicArray().Copy(apCost_CDynamicArray);// Set the
																	// cost
		m_nValueKind = nValueKind;// Set the value kind
		m_nVertex = apCost_CDynamicArray.m_nCol + 1; // 顶点的数量
		if (m_nVertex < apCost_CDynamicArray.m_nRow + 1)
			m_nVertex = apCost_CDynamicArray.m_nRow + 1;// Get the vertex
														// numbers 取行列的最大值+1

		// m_pWeight里面存储的是这个节点的权重
		m_pWeight = new double[m_nVertex - 1][m_nVertex];

		// m_pParent是代表从此点开始的一个子树，行列数据相同
		m_pParent = new CQueue[m_nVertex - 1][m_nVertex];
		for (int i = 0; i < m_nVertex - 1; i++) {
			for (int j = 0; j < m_nVertex; j++) {
				m_pParent[i][j] = new CQueue();
				m_pWeight[i][j] = 0;
			}
		}

	}

	/**
	 * 计算最短路径，输入：m_apCost,
	 * 
	 * @return
	 */
	public int ShortPath() {
		int nCurNode = 1, nPreNode, i, nIndex;
		double eWeight;
		PARRAY_CHAIN pEdgeList;
		PPARRAY_CHAIN ppEdgeList = new PPARRAY_CHAIN(null);

		// 循环开始点,按行进行循环
		for (; nCurNode < m_nVertex; nCurNode++) {
			// 生成一个新的CQueue对象
			CQueue queWork = new CQueue();

			// Get all the edges
			// 得到所有节点的开始指针，行为-1,列为nCurNode
			eWeight = m_apCost.GetElement(-1, nCurNode, null, ppEdgeList);
			pEdgeList = ppEdgeList.p;

			// Step1:从动态数组中检索数据，并将数据压入队列之中。
			// 输入：动态数组
			// 输出：queWork
			// 对动态数组的节点进行便利循环,pEdgeList是一个对应的指针
			// 只要列名等于当前行的
			while (pEdgeList != null && pEdgeList.p != null
					&& pEdgeList.p.col == nCurNode) {
				nPreNode = pEdgeList.p.row;
				eWeight = pEdgeList.p.value;// Get the value of edges
				for (i = 0; i < m_nValueKind; i++) {
					// Push the weight and the pre node infomation
					// 压入权重和前一节点的信息
					if (nPreNode > 0) {
						// 如果这两个节点之间的距离为无穷大，跳出
						if (m_pWeight[nPreNode - 1][i] == CONST.INFINITE_VALUE) {
							break;
						}

						// 负责，将路径压入队列Queue数据中:前一节点，当前节点，权重
						queWork.Push(nPreNode, i, eWeight
								+ m_pWeight[nPreNode - 1][i]);
					} else {
						// 如果行数等于0，代表第一次访问到，压入堆栈的数据
						// 前一节点，当前节点，权重
						queWork.Push(nPreNode, i, eWeight);
						break;
					}
				}// end for
				pEdgeList = pEdgeList.next();

			}// while循环结束
 
			logger.debug("nCurNode:" + nCurNode + " queWork:" + queWork.AlltoString());

			// Now get the result queue which sort as weight.
			// Set the current node information

			// Step2:初始化输出用的m_pWeight,先全部初始化为无穷大
			// 先初始化权重数组
			for (i = 0; i < m_nValueKind; i++) {
				m_pWeight[nCurNode - 1][i] = CONST.INFINITE_VALUE;
			}

			// Step3:利用Queue进行中转
			// 将原来队列里面的数据，全部筛选以后转到数组里面去
			// 自动进行排序，设置最大的权重点作为结果
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
					// 如果没有权重，则设置权重
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

				// 在当前节点上，压入一个节点数据进行,相当于
				// 从queWork里面弹出，然后再压入到m_pParent[][]里面去
				// 一次循环，只压入一个节点进去
				// 这次权重没有了。
				m_pParent[nCurNode - 1][i].Push(nPreNode, nIndex);
			}
		}// for 循环结束

		return 1;
	}

	/**
	 * 计算得到最短路径
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
	 * 输出按照最短路径分词的结果,结果的存储点要在调用前定义
	 * 
	 * @param nResult
	 *            分词结果路径
	 * @param bBest
	 *            是否要最优解
	 * @param npCount
	 *            结果总数，代表返回的数组有效行号
	 * @return 永远返回1
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
	 * 将当前的CNShortPath转换成一个字符串表示，测试程序使用
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
