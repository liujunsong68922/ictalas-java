package com.icutword.segment;

import org.apache.log4j.Logger;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dictionary.PWORD_RESULT;
import com.icutword.model.dictionary.WORD_RESULT;
import com.icutword.model.dynamicarray.CDynamicArray;
import com.icutword.model.dynamicarray.PARRAY_CHAIN;
import com.icutword.model.dynamicarray.PPARRAY_CHAIN;
import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;
import com.icutword.segment.nshortpath.CNShortPath;
import com.icutword.segment.seggraph.CSegGraph;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

/**
 * 在网状图的基础上进行裁剪，深入切词的功能类
 * 
 * @author liujunsong
 * 
 */
public class CSegment extends CUtility {
	static Logger logger = Logger.getLogger("logger");
	
	public int MAX_WORDS = 650;
	public int MAX_SEGMENT_NUM = 10;
	public int MAX_SENTENCE_LEN = 2000;
	public int WORD_MAXLENGTH = 100;
	public int MAX_FREQUENCE = 2079997;

	// The segmentation result
	// 分词结果的存储
	public PWORD_RESULT m_pWordSeg[][];
	public int m_nSegmentCount;

	// The optimumized segmentation graph
	// 优化以后的分词图谱存储,这个是存储用的类
	public CDynamicArray m_graphOptimum_DynamicArray = new CDynamicArray();

	// The segmentation graph
	// 分词图对象,这个是执行功能的类
	public CSegGraph m_graphSeg_CSegGraph = new CSegGraph();

	// Record the position map of possible words
	// 记录可能是词的出现位置
	int m_npWordPosMapTable[];

	// Record the End position of possible words
	// 记录可能性词的结束点
	int m_nWordCount;

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public CSegment() {
		// 初始化分词的存储结果,这个结果存储在m_pWordSeg
		m_pWordSeg = new PWORD_RESULT[MAX_SEGMENT_NUM][MAX_WORDS];
		for (int i = 0; i < MAX_SEGMENT_NUM; i++) {
			for (int j = 0; j < MAX_WORDS; j++) {
				m_pWordSeg[i][j] = new PWORD_RESULT(new WORD_RESULT());
			}
		}
		// Record the start position of possible words
		m_npWordPosMapTable = null;

		// Record the End position of possible words
		m_nWordCount = 0;

		// Set row first
		// 分词优化对象，按照行优先的规则进行判断查找
		m_graphOptimum_DynamicArray.SetRowFirst();
	}

	// Generate Word according the segmentation route
	/**
	 * 生成分词词组，根据分词结果,输出在m_graphOptimum_DynamicArray
	 * 
	 * @param nSegRoute
	 * @param nIndex
	 * @return
	 */
	private boolean GenerateWord(int nSegRoute[][], int nIndex) {
		int i = 0, k = 0;
		int j, nStartVertex, nEndVertex, nPOS;
		char sAtom[] = new char[WORD_MAXLENGTH], sNumCandidate[] = new char[100], sCurWord[] = new char[100];
		double fValue;
		//logger.debug(m_graphSeg_CSegGraph.m_segGraph_CDynamicArray.toString());
		
		while (nSegRoute[nIndex][i] != -1 && nSegRoute[nIndex][i + 1] != -1
				&& nSegRoute[nIndex][i] < nSegRoute[nIndex][i + 1]) {
			nStartVertex = nSegRoute[nIndex][i];
			j = nStartVertex;// Set the start vertex
			nEndVertex = nSegRoute[nIndex][i + 1];// Set the end vertex
			nPOS = 0;

			Pdouble pfValue = new Pdouble();
			Pint pnPos = new Pint();
			
			// GetElement2的方法有错误。
			m_graphSeg_CSegGraph.m_segGraph_CDynamicArray.GetElement2(
					nStartVertex, nEndVertex, pfValue, pnPos);
			fValue = pfValue.value;
			nPOS = pnPos.value;

			sAtom[0] = 0;
			while (j < nEndVertex) {// Generate the word according the
									// segmentation route
				strcat(sAtom, m_graphSeg_CSegGraph.m_sAtom[j]);
				j++;
			}
			m_pWordSeg[nIndex][k].p.sWord[0] = 0;// Init the result ending
			strcpy(sNumCandidate, sAtom);
			//TODO:IsAllNum,IsAllChineseNum的计算功能有错误
			while (sAtom[0] != 0
					&& (IsAllNum(sNumCandidate) || IsAllChineseNum(sNumCandidate))) {
				// Merge all seperate continue num into one number sAtom[0]!=0:
				// add in 2002-5-9
				strcpy(m_pWordSeg[nIndex][k].p.sWord, sNumCandidate);
				// Save them in the result segmentation
				i++;// Skip to next atom now
				sAtom[0] = 0;

				while (j < nSegRoute[nIndex][i + 1]) {
					// Generate the word according the segmentation route
					strcat(sAtom, m_graphSeg_CSegGraph.m_sAtom[j]);
					j++;
				}
				strcat(sNumCandidate, sAtom);
			}
			int nLen = strlen(m_pWordSeg[nIndex][k].p.sWord);
			if (nLen == 4
					&& CC_Find(toGB2312("第上成±—＋∶·．／"),
							m_pWordSeg[nIndex][k].p.sWord) != null || nLen == 1
					&& strchr("+-./", m_pWordSeg[nIndex][k].p.sWord[0]) >= 0) {// Only
																				// one
																				// word
				strcpy(sCurWord, m_pWordSeg[nIndex][k].p.sWord);// Record
																// current word
				i--;
			} else if (m_pWordSeg[nIndex][k].p.sWord[0] == 0)// Have never
																// entering the
																// while loop
			{
				strcpy(m_pWordSeg[nIndex][k].p.sWord, sAtom);
				// Save them in the result segmentation
				strcpy(sCurWord, sAtom);// Record current word
			} else {// It is a num
				if (strcmp(toGB2312("－－"), m_pWordSeg[nIndex][k].p.sWord) == 0
						|| strcmp(toGB2312("—"), m_pWordSeg[nIndex][k].p.sWord) == 0
						|| m_pWordSeg[nIndex][k].p.sWord[0] == '-'
						&& m_pWordSeg[nIndex][k].p.sWord[1] == 0)// The
																	// delimiter
																	// "－－"
				{
					nPOS = 30464;// 'w'*256;Set the POS with 'w'
					i--;// Not num, back to previous word
				} else {// Adding time suffix

					char sInitChar[] = new char[3];
					int nCharIndex = 0;// Get first char
					sInitChar[nCharIndex] = m_pWordSeg[nIndex][k].p.sWord[nCharIndex];
					if ((byte) sInitChar[nCharIndex] < 0) {
						nCharIndex += 1;
						sInitChar[nCharIndex] = m_pWordSeg[nIndex][k].p.sWord[nCharIndex];
					}
					nCharIndex += 1;
					sInitChar[nCharIndex] = '\0';
					if (k > 0
							&& (Math.abs(m_pWordSeg[nIndex][k - 1].p.nHandle) == 27904 || Math
									.abs(m_pWordSeg[nIndex][k - 1].p.nHandle) == 29696)
							&& (strcmp(sInitChar, toGB2312("—")) == 0 || sInitChar[0] == '-')
							&& (strlen(m_pWordSeg[nIndex][k].p.sWord) > nCharIndex)) {// 3-4月
																						// //27904='m'*256
																						// Split
																						// the
																						// sInitChar
																						// from
																						// the
																						// original
																						// word
						strcpy(m_pWordSeg[nIndex][k + 1].p.sWord,
								m_pWordSeg[nIndex][k].p.sWord, nCharIndex);
						m_pWordSeg[nIndex][k + 1].p.nHandle = (int) m_pWordSeg[nIndex][k].p.dValue;
						m_pWordSeg[nIndex][k + 1].p.nHandle = 27904;
						m_pWordSeg[nIndex][k].p.sWord[nCharIndex] = 0;
						m_pWordSeg[nIndex][k].p.dValue = 0;
						m_pWordSeg[nIndex][k].p.nHandle = 30464;// 'w'*256;
						m_graphOptimum_DynamicArray.SetElement(nStartVertex,
								nStartVertex + 1,
								m_pWordSeg[nIndex][k].p.dValue,
								m_pWordSeg[nIndex][k].p.nHandle,
								m_pWordSeg[nIndex][k].p.sWord);
						nStartVertex += 1;
						k += 1;
					}
					nLen = strlen(m_pWordSeg[nIndex][k].p.sWord);
					if ((strlen(sAtom) == 2 && CC_Find(toGB2312("月日时分秒"), sAtom) != null)
							|| strcmp(sAtom, toGB2312("月份")) == 0) {// 2001年
						strcat(m_pWordSeg[nIndex][k].p.sWord, sAtom);
						strcpy(sCurWord, toGB2312("未##时"));
						nPOS = -29696;// 't'*256;//Set the POS with 'm'
					} else if (strcmp(sAtom, toGB2312("年")) == 0) {
						if (CSegmentFunction
								.IsYearTime(m_pWordSeg[nIndex][k].p.sWord))// strncmp(sAtom,"年",2)==0&&
						{// 1998年，
							strcat(m_pWordSeg[nIndex][k].p.sWord, sAtom);
							strcpy(sCurWord, toGB2312("未##时"));
							nPOS = -29696;// Set the POS with 't'
						} else {
							strcpy(sCurWord, toGB2312("未##数"));
							nPOS = -27904;// Set the POS with 'm'
							i--;// Can not be a time word
						}
					} else {
						// 早晨/t 五点/t
						if (strncmp(m_pWordSeg[nIndex][k].p.sWord,
								strlen(m_pWordSeg[nIndex][k].p.sWord) - 2,
								toGB2312("点"), 0) == 0) {
							strcpy(sCurWord, toGB2312("未##时"));
							nPOS = -29696;// Set the POS with 't'
						} else {
							// char[] temp = new char[3];
							// char[0]=
							if (CC_Find(toGB2312("∶·．／"),
									m_pWordSeg[nIndex][k].p.sWord, nLen - 2) != null
									&& m_pWordSeg[nIndex][k].p.sWord[nLen - 1] != '.'
									&& m_pWordSeg[nIndex][k].p.sWord[nLen - 1] != '/') {
								strcpy(sCurWord, toGB2312("未##数"));
								nPOS = -27904;// 'm'*256;Set the POS with 'm'
							} else if (nLen > strlen(sInitChar)) {// Get rid of
																	// . example
																	// 1.
								if (m_pWordSeg[nIndex][k].p.sWord[nLen - 1] == '.'
										|| m_pWordSeg[nIndex][k].p.sWord[nLen - 1] == '/')
									m_pWordSeg[nIndex][k].p.sWord[nLen - 1] = 0;
								else
									m_pWordSeg[nIndex][k].p.sWord[nLen - 2] = 0;
								strcpy(sCurWord, toGB2312("未##数"));
								nPOS = -27904;// 'm'*256;Set the POS with 'm'
								i--;
							}
						}
						i--;// Not num, back to previous word
					}
				}
				fValue = 0;
				nEndVertex = nSegRoute[nIndex][i + 1];// Ending POS changed to
														// latter
			}
			m_pWordSeg[nIndex][k].p.nHandle = nPOS;// Get the POS of current
													// word
			m_pWordSeg[nIndex][k].p.dValue = fValue;// (int)(MAX_FREQUENCE*exp(-fValue));//Return
													// the frequency of current
													// word
			m_graphOptimum_DynamicArray.SetElement(nStartVertex, nEndVertex,
					fValue, nPOS, sCurWord);
			// Generate optimum segmentation graph according the segmentation
			// result
			i++;// Skip to next atom
			k++;// Accept next word
		}
		m_pWordSeg[nIndex][k].p.sWord[0] = 0;
		m_pWordSeg[nIndex][k].p.nHandle = -1;// Set ending
		return true;
	}

	/**
	 * 二叉树生成
	 * 
	 * @param aWordIn
	 *            输入的原始动态数组
	 * @param aBinaryWordNet
	 *            输出的二叉树数组
	 * @param dSmoothingPara
	 *            平滑参数
	 * @param DictBinary
	 *            分词用词典
	 * @param DictCore
	 *            核心词典
	 * @return
	 */
	private boolean biGraphGenerate(CDynamicArray aWordIn,
			CDynamicArray aBinaryWordNet, double dSmoothingPara,
			CDictionary DictBinary, CDictionary DictCore) {
		// 代码迁移出去，在另一个类里面实现
		m_nWordCount = CSegmentFunction.biGraphGenerate(aWordIn,
				aBinaryWordNet, dSmoothingPara, DictBinary, DictCore,
				this);
		return true;
	}

	/**
	 * 二叉分词树生成,输入：sSentence,输出m_graphSeg.m_segGraph
	 * 
	 * @param sSentence
	 *            输入的分词字符串
	 * @param dSmoothingPara
	 *            平滑参数，计算用
	 * @param dictCore
	 *            核心辞典对象
	 * @param dictBinary
	 *            分词辞典对象
	 * @param nResultCount
	 *            需要计算的结果路径数
	 * @return
	 */
	public boolean BiSegment(char[] sSentence, double dSmoothingPara,
			CDictionary dictCore, CDictionary dictBinary, int nResultCount) {
		int nLen = strlen(sSentence) + 10;

		int nSegRoute[][];// The segmentation route
		// 分词路径结果数组
		// 行数：最大分词记录数：常数定义为10，最多计算10个结果
		// 列数：长度的一半，按照汉字来计算
		nSegRoute = new int[MAX_SEGMENT_NUM][nLen / 2];
		int i;

		// 初始化内存,用-1来初始化全部内存
		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			for (int k = 0; k < nLen / 2; k++) {
				nSegRoute[i][k] = -1;
			}
		}

		// Generate words array
		// 输入sSentence,dictCore(核心词典)
		// 输出到m_graphSeg.m_segGraph(一个动态定义的数组)
		logger.debug("BiSegment----->开始生成网状图:" + CStringFunction.getCString(sSentence));
		m_graphSeg_CSegGraph.GenerateWordNet(sSentence, dictCore, true);
		logger.debug("BiSegment----->生成网状图结束.");

		CDynamicArray aBiwordsNet = new CDynamicArray();
		// Generate the biword link net
		logger.debug("BiSegment----->开始生成二叉树:" + CStringFunction.getCString(sSentence));
		biGraphGenerate(m_graphSeg_CSegGraph.m_segGraph_CDynamicArray,
				aBiwordsNet, dSmoothingPara, dictBinary, dictCore);

		logger.debug("BiSegment-----二叉树的计算输出结果：");
		logger.debug(aBiwordsNet.toString());

		logger.debug("BiSegment----->生成二叉树结束.");

		// 计算最短路径的算法
		logger.debug("BiSegment----->开始计算最短路径");
		CNShortPath sp = new CNShortPath(aBiwordsNet, nResultCount);
		logger.debug(sp.toString());
		sp.ShortPath();
		logger.debug("BiSegment------>最短路径计算完毕");
		logger.debug(sp.toString());

		Pint a = new Pint();
		sp.Output(nSegRoute, false, a);
		m_nSegmentCount = a.value;

		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			String sline = "";
			for (int k = 0; k < nLen / 2; k++) {
				sline += ("|" + nSegRoute[i][k]);
			}
			logger.debug("sline="+sline);
		}

		// Set graph optimum empty
		m_graphOptimum_DynamicArray.SetEmpty();
		i = 0;
		while (i < m_nSegmentCount) {
			biPath2UniPath(nSegRoute[i]);
			String sline = "line:" + i + ": ";
			for (int k = 0; k < nLen / 2; k++) {
				sline += ("|" + nSegRoute[i][k]);
			}
			logger.debug(sline);
			sline = "m_npWordPosMapTable=";
			if (m_npWordPosMapTable != null) {
				for (int k = 0; k < m_npWordPosMapTable.length; k++) {
					sline += "|" + m_npWordPosMapTable[k];
				}
			}
			logger.debug(sline);

			// Path convert to unipath
			logger.debug("开始生成输出GenerateWord.");
			GenerateWord(nSegRoute, i);
			// Gernerate word according the Segmentation route
			logger.debug("生成输出结束GenerateWord.");
			logger.debug(this.m_graphOptimum_DynamicArray.toString());
			i++;
		}

		return true;
	}

	/**
	 * 二叉数转换为唯一路径
	 * 
	 * @param npPath
	 * @return
	 */
	private boolean biPath2UniPath(int npPath[]) {// BiPath convert to unipath
		int i = 0, nTemp = -1;
		if (m_npWordPosMapTable == null)
			return false;
		while (npPath[i] != -1 && npPath[i] < m_nWordCount) {
			nTemp = m_npWordPosMapTable[npPath[i]];
			npPath[i] = nTemp / MAX_SENTENCE_LEN;
			i++;
		}
		if (nTemp > 0)
			npPath[i++] = nTemp % MAX_SENTENCE_LEN;
		npPath[i] = -1;
		return true;
	}

	/**
	 * 二叉树的优化
	 * 
	 * @param nResultCount
	 * @param dSmoothingPara
	 *            平滑参数
	 * @param dictBinary
	 *            二进制词典
	 * @param dictCore
	 *            核心词典
	 * @return
	 */
	public boolean BiOptimumSegment(int nResultCount, double dSmoothingPara,
			CDictionary dictBinary, CDictionary dictCore) {
		int nSegRoute[][];// The segmentation route
		nSegRoute = new int[MAX_SEGMENT_NUM][MAX_SENTENCE_LEN / 2];
		int i, j;

		// nSegRoute的初始化,全部初始化为-1
		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			for (j = 0; j < MAX_SENTENCE_LEN / 2; j++) {
				nSegRoute[i][j] = -1;
			}
		}

		CDynamicArray aBiwordsNet_CDynamicArray = new CDynamicArray();
		// 根据一个输入的动态数组，生成一个输出的动态数组，代表一个二叉树
		biGraphGenerate(m_graphOptimum_DynamicArray, aBiwordsNet_CDynamicArray,
				dSmoothingPara, dictBinary, dictCore);
		// Generate the biword link net

		CNShortPath sp = new CNShortPath(aBiwordsNet_CDynamicArray,
				nResultCount);
		sp.ShortPath();

		Pint t = new Pint();
		sp.Output(nSegRoute, false, t);
		m_nSegmentCount = t.value;

		i = 0;
		m_graphSeg_CSegGraph.m_segGraph_CDynamicArray = m_graphOptimum_DynamicArray;
		m_graphOptimum_DynamicArray.SetEmpty();// Set graph optimum empty

		while (i < m_nSegmentCount) {
			// Path convert to unipath
			// 路径转换成标准路径？此处功能待理解
			biPath2UniPath(nSegRoute[i]);

			// Gernerate word according the Segmentation route
			// 根据分词路径，来生成分词结果,输出
			GenerateWord(nSegRoute, i);
			logger.debug(m_graphOptimum_DynamicArray.toString());
			i++;
		}

		// free the memory
		// TODO:释放内存
		nSegRoute = null;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// 暂时不再使用的方法
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 得到最后一个分词结果
	 * 
	 * @param pItem
	 * @param sWordRet
	 * @return
	 */
	private boolean _GetLastWord(PWORD_RESULT pItem[], char[] sWordRet) {
		int nCount = 0;
		sWordRet[0] = 0;
		while (pItem[nCount].p.sWord != null && pItem[nCount].p.sWord[0] != 0) {
			strcpy(sWordRet, pItem[nCount].p.sWord);
			nCount += 1;
		}
		return sWordRet[0] != 0;
	}

	/**
	 * 根据核心词典进行分词
	 * 
	 * @param sSentence
	 * @param dictCore
	 * @param nResultCount
	 * @return
	 */
	private boolean _Segment(char[] sSentence, CDictionary dictCore,
			int nResultCount) {
		int nSegRoute[][];// The segmentation route
		nSegRoute = new int[MAX_SEGMENT_NUM][MAX_SENTENCE_LEN / 2];
		int i, j;
		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			// nSegRoute[i]=new int[MAX_SENTENCE_LEN/2];
			// memset(nSegRoute[i],0,MAX_SENTENCE_LEN/2*sizeof(int));
			for (j = 0; j < MAX_SENTENCE_LEN / 2; j++)
				nSegRoute[i][j] = 0;
		}
		m_graphSeg_CSegGraph.m_segGraph_CDynamicArray.SetRowFirst(false);
		m_graphOptimum_DynamicArray.SetRowFirst(false);
		m_graphSeg_CSegGraph.GenerateWordNet(sSentence, dictCore);
		logger.debug("-------------------生成词典网络图的计算结果：");
		logger.debug(m_graphSeg_CSegGraph.m_segGraph_CDynamicArray
				.toString());

		CNShortPath sp = new CNShortPath(
				m_graphSeg_CSegGraph.m_segGraph_CDynamicArray, nResultCount);
		sp.ShortPath();
		Pint pm_nSegmentCount = new Pint();
		sp.Output(nSegRoute, false, pm_nSegmentCount);
		m_nSegmentCount = pm_nSegmentCount.value;

		m_graphOptimum_DynamicArray.SetEmpty();// Set graph optimum empty
		i = 0;
		while (i < m_nSegmentCount) {
			GenerateWord(nSegRoute, i);
			// Gernerate word according the Segmentation route
			i++;
		}

		// free the memory
		// for(i=0;i<MAX_SEGMENT_NUM;i++)
		// {
		// delete [] nSegRoute[i];//free the pointer memory
		// }
		// delete [] nSegRoute;//free the pointer array
		nSegRoute = null;

		return true;
	}

	/**
	 * 得到所有的结果计数
	 * 
	 * @param pItem
	 * @return
	 */
	private int _GetResultCount(PWORD_RESULT pItem[]) {
		int nCount = 0;
		while (pItem[nCount].p.sWord != null && pItem[nCount].p.sWord[0] != 0) {
			nCount += 1;
		}
		return nCount;
	}

	// After unknown word recognition
	private boolean _OptimumSegmet(int nResultCount) {
		int nSegRoute[][];// The segmentation route
		nSegRoute = new int[MAX_SEGMENT_NUM][MAX_SENTENCE_LEN / 2];
		int i;
		// for(i=0;i<MAX_SEGMENT_NUM;i++)
		// {
		// nSegRoute[i]=new int[MAX_SENTENCE_LEN/2];
		// }

		CNShortPath sp = new CNShortPath(m_graphOptimum_DynamicArray,
				nResultCount);
		sp.ShortPath();

		Pint pm_nSegmentCount = new Pint();
		sp.Output(nSegRoute, false, pm_nSegmentCount);
		m_nSegmentCount = pm_nSegmentCount.value;

		i = 0;
		m_graphSeg_CSegGraph.m_segGraph_CDynamicArray = m_graphOptimum_DynamicArray;
		m_graphOptimum_DynamicArray.SetEmpty();// Set graph optimum empty
		while (i < m_nSegmentCount) {
			// Gernerate word according the Segmentation route
			// 根据分词路径，来生成分词结果
			GenerateWord(nSegRoute, i);
			i++;
		}

		// free the memory
		// TODO:释放本次调用所使用的内存
		nSegRoute = null;
		return true;
	}

}
