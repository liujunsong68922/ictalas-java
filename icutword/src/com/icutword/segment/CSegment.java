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
 * ����״ͼ�Ļ����Ͻ��вü��������дʵĹ�����
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
	// �ִʽ���Ĵ洢
	public PWORD_RESULT m_pWordSeg[][];
	public int m_nSegmentCount;

	// The optimumized segmentation graph
	// �Ż��Ժ�ķִ�ͼ�״洢,����Ǵ洢�õ���
	public CDynamicArray m_graphOptimum_DynamicArray = new CDynamicArray();

	// The segmentation graph
	// �ִ�ͼ����,�����ִ�й��ܵ���
	public CSegGraph m_graphSeg_CSegGraph = new CSegGraph();

	// Record the position map of possible words
	// ��¼�����Ǵʵĳ���λ��
	int m_npWordPosMapTable[];

	// Record the End position of possible words
	// ��¼�����ԴʵĽ�����
	int m_nWordCount;

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	public CSegment() {
		// ��ʼ���ִʵĴ洢���,�������洢��m_pWordSeg
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
		// �ִ��Ż����󣬰��������ȵĹ�������жϲ���
		m_graphOptimum_DynamicArray.SetRowFirst();
	}

	// Generate Word according the segmentation route
	/**
	 * ���ɷִʴ��飬���ݷִʽ��,�����m_graphOptimum_DynamicArray
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
			
			// GetElement2�ķ����д���
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
			//TODO:IsAllNum,IsAllChineseNum�ļ��㹦���д���
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
					&& CC_Find(toGB2312("���ϳɡ������á�����"),
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
				if (strcmp(toGB2312("����"), m_pWordSeg[nIndex][k].p.sWord) == 0
						|| strcmp(toGB2312("��"), m_pWordSeg[nIndex][k].p.sWord) == 0
						|| m_pWordSeg[nIndex][k].p.sWord[0] == '-'
						&& m_pWordSeg[nIndex][k].p.sWord[1] == 0)// The
																	// delimiter
																	// "����"
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
							&& (strcmp(sInitChar, toGB2312("��")) == 0 || sInitChar[0] == '-')
							&& (strlen(m_pWordSeg[nIndex][k].p.sWord) > nCharIndex)) {// 3-4��
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
					if ((strlen(sAtom) == 2 && CC_Find(toGB2312("����ʱ����"), sAtom) != null)
							|| strcmp(sAtom, toGB2312("�·�")) == 0) {// 2001��
						strcat(m_pWordSeg[nIndex][k].p.sWord, sAtom);
						strcpy(sCurWord, toGB2312("δ##ʱ"));
						nPOS = -29696;// 't'*256;//Set the POS with 'm'
					} else if (strcmp(sAtom, toGB2312("��")) == 0) {
						if (CSegmentFunction
								.IsYearTime(m_pWordSeg[nIndex][k].p.sWord))// strncmp(sAtom,"��",2)==0&&
						{// 1998�꣬
							strcat(m_pWordSeg[nIndex][k].p.sWord, sAtom);
							strcpy(sCurWord, toGB2312("δ##ʱ"));
							nPOS = -29696;// Set the POS with 't'
						} else {
							strcpy(sCurWord, toGB2312("δ##��"));
							nPOS = -27904;// Set the POS with 'm'
							i--;// Can not be a time word
						}
					} else {
						// �糿/t ���/t
						if (strncmp(m_pWordSeg[nIndex][k].p.sWord,
								strlen(m_pWordSeg[nIndex][k].p.sWord) - 2,
								toGB2312("��"), 0) == 0) {
							strcpy(sCurWord, toGB2312("δ##ʱ"));
							nPOS = -29696;// Set the POS with 't'
						} else {
							// char[] temp = new char[3];
							// char[0]=
							if (CC_Find(toGB2312("�á�����"),
									m_pWordSeg[nIndex][k].p.sWord, nLen - 2) != null
									&& m_pWordSeg[nIndex][k].p.sWord[nLen - 1] != '.'
									&& m_pWordSeg[nIndex][k].p.sWord[nLen - 1] != '/') {
								strcpy(sCurWord, toGB2312("δ##��"));
								nPOS = -27904;// 'm'*256;Set the POS with 'm'
							} else if (nLen > strlen(sInitChar)) {// Get rid of
																	// . example
																	// 1.
								if (m_pWordSeg[nIndex][k].p.sWord[nLen - 1] == '.'
										|| m_pWordSeg[nIndex][k].p.sWord[nLen - 1] == '/')
									m_pWordSeg[nIndex][k].p.sWord[nLen - 1] = 0;
								else
									m_pWordSeg[nIndex][k].p.sWord[nLen - 2] = 0;
								strcpy(sCurWord, toGB2312("δ##��"));
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
	 * ����������
	 * 
	 * @param aWordIn
	 *            �����ԭʼ��̬����
	 * @param aBinaryWordNet
	 *            ����Ķ���������
	 * @param dSmoothingPara
	 *            ƽ������
	 * @param DictBinary
	 *            �ִ��ôʵ�
	 * @param DictCore
	 *            ���Ĵʵ�
	 * @return
	 */
	private boolean biGraphGenerate(CDynamicArray aWordIn,
			CDynamicArray aBinaryWordNet, double dSmoothingPara,
			CDictionary DictBinary, CDictionary DictCore) {
		// ����Ǩ�Ƴ�ȥ������һ��������ʵ��
		m_nWordCount = CSegmentFunction.biGraphGenerate(aWordIn,
				aBinaryWordNet, dSmoothingPara, DictBinary, DictCore,
				this);
		return true;
	}

	/**
	 * ����ִ�������,���룺sSentence,���m_graphSeg.m_segGraph
	 * 
	 * @param sSentence
	 *            ����ķִ��ַ���
	 * @param dSmoothingPara
	 *            ƽ��������������
	 * @param dictCore
	 *            ���Ĵǵ����
	 * @param dictBinary
	 *            �ִʴǵ����
	 * @param nResultCount
	 *            ��Ҫ����Ľ��·����
	 * @return
	 */
	public boolean BiSegment(char[] sSentence, double dSmoothingPara,
			CDictionary dictCore, CDictionary dictBinary, int nResultCount) {
		int nLen = strlen(sSentence) + 10;

		int nSegRoute[][];// The segmentation route
		// �ִ�·���������
		// ���������ִʼ�¼������������Ϊ10��������10�����
		// ���������ȵ�һ�룬���պ���������
		nSegRoute = new int[MAX_SEGMENT_NUM][nLen / 2];
		int i;

		// ��ʼ���ڴ�,��-1����ʼ��ȫ���ڴ�
		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			for (int k = 0; k < nLen / 2; k++) {
				nSegRoute[i][k] = -1;
			}
		}

		// Generate words array
		// ����sSentence,dictCore(���Ĵʵ�)
		// �����m_graphSeg.m_segGraph(һ����̬���������)
		logger.debug("BiSegment----->��ʼ������״ͼ:" + CStringFunction.getCString(sSentence));
		m_graphSeg_CSegGraph.GenerateWordNet(sSentence, dictCore, true);
		logger.debug("BiSegment----->������״ͼ����.");

		CDynamicArray aBiwordsNet = new CDynamicArray();
		// Generate the biword link net
		logger.debug("BiSegment----->��ʼ���ɶ�����:" + CStringFunction.getCString(sSentence));
		biGraphGenerate(m_graphSeg_CSegGraph.m_segGraph_CDynamicArray,
				aBiwordsNet, dSmoothingPara, dictBinary, dictCore);

		logger.debug("BiSegment-----�������ļ�����������");
		logger.debug(aBiwordsNet.toString());

		logger.debug("BiSegment----->���ɶ���������.");

		// �������·�����㷨
		logger.debug("BiSegment----->��ʼ�������·��");
		CNShortPath sp = new CNShortPath(aBiwordsNet, nResultCount);
		logger.debug(sp.toString());
		sp.ShortPath();
		logger.debug("BiSegment------>���·���������");
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
			logger.debug("��ʼ�������GenerateWord.");
			GenerateWord(nSegRoute, i);
			// Gernerate word according the Segmentation route
			logger.debug("�����������GenerateWord.");
			logger.debug(this.m_graphOptimum_DynamicArray.toString());
			i++;
		}

		return true;
	}

	/**
	 * ������ת��ΪΨһ·��
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
	 * ���������Ż�
	 * 
	 * @param nResultCount
	 * @param dSmoothingPara
	 *            ƽ������
	 * @param dictBinary
	 *            �����ƴʵ�
	 * @param dictCore
	 *            ���Ĵʵ�
	 * @return
	 */
	public boolean BiOptimumSegment(int nResultCount, double dSmoothingPara,
			CDictionary dictBinary, CDictionary dictCore) {
		int nSegRoute[][];// The segmentation route
		nSegRoute = new int[MAX_SEGMENT_NUM][MAX_SENTENCE_LEN / 2];
		int i, j;

		// nSegRoute�ĳ�ʼ��,ȫ����ʼ��Ϊ-1
		for (i = 0; i < MAX_SEGMENT_NUM; i++) {
			for (j = 0; j < MAX_SENTENCE_LEN / 2; j++) {
				nSegRoute[i][j] = -1;
			}
		}

		CDynamicArray aBiwordsNet_CDynamicArray = new CDynamicArray();
		// ����һ������Ķ�̬���飬����һ������Ķ�̬���飬����һ��������
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
			// ·��ת���ɱ�׼·�����˴����ܴ����
			biPath2UniPath(nSegRoute[i]);

			// Gernerate word according the Segmentation route
			// ���ݷִ�·���������ɷִʽ��,���
			GenerateWord(nSegRoute, i);
			logger.debug(m_graphOptimum_DynamicArray.toString());
			i++;
		}

		// free the memory
		// TODO:�ͷ��ڴ�
		nSegRoute = null;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// ��ʱ����ʹ�õķ���
	// ////////////////////////////////////////////////////////////////////
	/**
	 * �õ����һ���ִʽ��
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
	 * ���ݺ��Ĵʵ���зִ�
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
		logger.debug("-------------------���ɴʵ�����ͼ�ļ�������");
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
	 * �õ����еĽ������
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
			// ���ݷִ�·���������ɷִʽ��
			GenerateWord(nSegRoute, i);
			i++;
		}

		// free the memory
		// TODO:�ͷű��ε�����ʹ�õ��ڴ�
		nSegRoute = null;
		return true;
	}

}
