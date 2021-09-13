package com.icutword.unknown.span;

import org.apache.log4j.Logger;

import com.icutword.model.contextstat.CContextStat;
import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dictionary.PWORD_RESULT;
import com.icutword.model.point.Pint;
import com.icutword.utility.CUtility;

/**
 * CSpan是一个利用现有人名，地名，翻译名辞典，对一个未知辞典进行动态匹配计算的功能类 首先在辞典上实现动态标注的功能。 CSpan是一个标准词性标注器
 * 
 * @author liujunsong
 * 
 */
public class CSpan extends CUtility {
	static Logger logger = Logger.getLogger("logger");

	static int MAX_WORDS_PER_SENTENCE = 120; // 每一句的最大词数
	static int MAX_UNKNOWN_PER_SENTENCE = 200; // 每一句最大未知词数
	static int MAX_POS_PER_WORD = 20; // 每一次最大位置
	static int LITTLE_FREQUENCY = 6; // 匹配的最小频度，不足这一频度的被废弃掉
	int WORD_MAXLENGTH = 100;

	TAG_TYPE m_tagType;// The type of tagging，标示器的类型

	// The number of unknown word，未知的词数
	public int m_nUnknownIndex;

	// 自动识别出来的词的存储点
	public int m_nUnknownWords[][] = new int[MAX_UNKNOWN_PER_SENTENCE][2];

	// The start and ending possition of unknown position
	// 自动识别出来的词的存储开始结束点
	public double m_dWordsPossibility[] = new double[MAX_UNKNOWN_PER_SENTENCE];

	// The possibility of unknown words
	// 未知词的可能性
	public CContextStat m_context = new CContextStat();// context
	// TAG_TYPE m_tagType= new TAG_TYPE();//The type of tagging

	int m_nStartPos;

	// Record the Best Tag
	int m_nBestTag[] = new int[MAX_WORDS_PER_SENTENCE];

	char m_sWords[][] = new char[MAX_WORDS_PER_SENTENCE][WORD_MAXLENGTH];
	int m_nWordPosition[] = new int[MAX_WORDS_PER_SENTENCE];
	int m_nTags[][] = new int[MAX_WORDS_PER_SENTENCE][MAX_POS_PER_WORD];
	char m_nBestPrev[][] = new char[MAX_WORDS_PER_SENTENCE][MAX_POS_PER_WORD];
	char m_nCurLength;
	double m_dFrequency[][] = new double[MAX_WORDS_PER_SENTENCE][MAX_POS_PER_WORD];

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////
	public CSpan() {
		if (m_tagType != TAG_TYPE.TT_NORMAL)
			m_nTags[0][0] = 100;// Begin tag
		else
			m_nTags[0][0] = 0;// Begin tag

		m_nTags[0][1] = -1;
		m_dFrequency[0][0] = 0;
		m_nCurLength = 1;
		m_nUnknownIndex = 0;
		m_nStartPos = 0;
		m_nWordPosition[1] = 0;
		m_sWords[0][0] = 0;

		m_tagType = TAG_TYPE.TT_NORMAL;// Default tagging type

		for (int i = 0; i < MAX_WORDS_PER_SENTENCE; i++)
			for (int j = 0; j < MAX_POS_PER_WORD; j++) {
				m_nBestPrev[i][j] = '\0';
			}
	}

	/**
	 * 计算一个路径矩阵出来
	 * 
	 * @return
	 */
	private boolean Disamb() {
		int i, j, k, nMinCandidate;
		double dMinFee = 0.0, dTmp;
		for (i = 1; i < m_nCurLength; i++)// For every word
		{
			for (j = 0; m_nTags[i][j] >= 0; j++)// For every word
			{
				nMinCandidate = MAX_POS_PER_WORD + 1;
				for (k = 0; m_nTags[i - 1][k] >= 0; k++) {
					// ConvertPOS(m_nTags[i-1][k],&nKey,&nPrevPOS);
					// ConvertPOS(m_nTags[i][j],&nKey,&nCurPOS);
					// dTmp=m_context.GetContextPossibility(nKey,nPrevPOS,nCurPOS);
					dTmp = -Math.log(m_context.GetContextPossibility(0,
							m_nTags[i - 1][k], m_nTags[i][j]));
					dTmp += m_dFrequency[i - 1][k];// Add the fees
					// logger.debug("dTmp="+dTmp);
					if (nMinCandidate > 10 || dTmp < dMinFee)// Get the minimum
																// fee
					{
//						logger.debug("set nMinCandidate:" + k);
						nMinCandidate = k;
						dMinFee = dTmp;
					}
				}
//				logger.debug("i,j,nMinCandidate=" + i + "," + j + ","
//						+ nMinCandidate);
				if (nMinCandidate < MAX_POS_PER_WORD + 1) {
					m_nBestPrev[i][j] = (char) nMinCandidate;// The best
																// previous
																// for j
				}
				m_dFrequency[i][j] = m_dFrequency[i][j] + dMinFee;
			}
		}

		return true;
	}

	private boolean Reset() {
		return Reset(true);
	}

	/**
	 * 重置
	 * 
	 * @param bContinue
	 * @return
	 */
	private boolean Reset(boolean bContinue) {
		if (!bContinue) {// ||CC_Find("。！”〕〉》」〗】",m_sWords[m_nCurLength-1])
			if (m_tagType != TAG_TYPE.TT_NORMAL)// Get the last POS in the last
												// sentence
				m_nTags[0][0] = 100;// Begin tag
			else
				m_nTags[0][0] = 0;// Begin tag
			m_nUnknownIndex = 0;
			m_dFrequency[0][0] = 0;
			m_nStartPos = 0;
		} else {
			m_nTags[0][0] = m_nTags[m_nCurLength - 1][0];// Get the last POS in
															// the last sentence
			m_dFrequency[0][0] = m_dFrequency[m_nCurLength - 1][0];
		}
		m_nTags[0][1] = -1;// Get the last POS in the last sentence,set the -1
							// as end flag
		m_nCurLength = 1;
		m_nWordPosition[1] = m_nStartPos;
		m_sWords[0][0] = 0;
		return true;
	}

	/**
	 * 读取上下文文件,文件后缀为.tcx,代表各个属性的关系强度
	 * 
	 * @param sFilename
	 * @return
	 */
	public boolean LoadContext(char[] sFilename) {
		return m_context.Load(sFilename);
	}

	/**
	 * 得到最佳位置
	 * 
	 * @return
	 */
	private boolean GetBestPOS() {
		Disamb();
		for (int i = m_nCurLength - 1, j = 0; i > 0; i--)// ,j>=0
		{
			if (m_sWords[i][0] != '\0') {// Not virtual ending
				//logger.debug("i,j=" + i + "," + j);
				//logger.debug(m_nTags[i][j]);
				m_nBestTag[i] = m_nTags[i][j];// Record the best POS and its
												// possibility
			}
//			logger.debug("--->m_nBestPrev[i,j]value=" + i + "," + j + ","
//					+ (int) m_nBestPrev[i][j]);
			j = m_nBestPrev[i][j];
		}
		int nEnd = m_nCurLength;// Set the end of POS tagging
		if (m_sWords[m_nCurLength - 1][0] == 0)
			nEnd = m_nCurLength - 1;
		m_nBestTag[nEnd] = -1;
		return true;
	}

	/**
	 * 对未知的人名辞典，进行人名的识别
	 * 
	 * @param personDict
	 *            要进行人名识别的辞典，这个是输入 输出信息在
	 *            m_nUnknownWords[m_nUnknownIndex][0],[1],
	 *            m_nUnknownIndex,m_dWordsPossibility[m_nUnknownIndex]
	 * @return
	 */
	private boolean PersonRecognize(CDictionary personDict) {
		char sPOS[] = new char[MAX_WORDS_PER_SENTENCE];
		strcpy(sPOS, toGB2312("z"));
		char sPersonName[] = new char[100];
		// 0 1 2 3 4 5
		char sPatterns[][] = { toGB2312("BBCD"), toGB2312("BBC"),
				toGB2312("BBE"), toGB2312("BBZ"), toGB2312("BCD"),
				toGB2312("BEE"), toGB2312("BE"), toGB2312("BG"),
				toGB2312("BXD"), toGB2312("BZ"), toGB2312("CDCD"),
				toGB2312("CD"), toGB2312("EE"), toGB2312("FB"), toGB2312("Y"),
				toGB2312("XD"), toGB2312("") };
		// BBCD BBC BBE BBZ BCD BEE BE BG
		double dFactor[] = { 0.003606, 0.000021, 0.001314, 0.000315, 0.656624,
				0.000021, 0.146116, 0.009136,
				// BXD BZ CDCD CD EE FB Y XD
				0.000042, 0.038971, 0, 0.090367, 0.000273, 0.009157, 0.034324,
				0.009735, 0 };
		/*
		 * BBCD 343 0.003606 BBC 2 0.000021 BBE 125 0.001314 BBZ 30 0.000315 BCD
		 * 62460 0.656624 BEE 0 0.000000 BE 13899 0.146116 BG 869 0.009136 BXD 4
		 * 0.000042 BZ 3707 0.038971 CD 8596 0.090367 EE 26 0.000273 FB 871
		 * 0.009157 Y 3265 0.034324 XD 926 0.009735
		 */
		// The person recognition patterns set
		// BBCD:姓+姓+名1+名2;
		// BBE: 姓+姓+单名;
		// BBZ: 姓+姓+双名成词;
		// BCD: 姓+名1+名2;
		// BE: 姓+单名;
		// BEE: 姓+单名+单名;韩磊磊
		// BG: 姓+后缀
		// BXD: 姓+姓双名首字成词+双名末字
		// BZ: 姓+双名成词;
		// B: 姓
		// CD: 名1+名2;
		// EE: 单名+单名;
		// FB: 前缀+姓
		// XD: 姓双名首字成词+双名末字
		// Y: 姓单名成词
		int nPatternLen[] = { 4, 3, 3, 3, 3, 3, 2, 2, 3, 2, 4, 2, 2, 2, 1, 2, 0 };

		int i;
		for (i = 1; m_nBestTag[i] > -1; i++)
			// Convert to string from POS
			sPOS[i] = (char) (m_nBestTag[i] + 'A');
		sPOS[i] = 0;
		int j = 1, k, nPos;// Find the proper pattern from the first POS
		int nLittleFreqCount;// Counter for the person name role with little
								// frequecy
		boolean bMatched = false;
		while (j < i) {
			bMatched = false;
			for (k = 0; !bMatched && nPatternLen[k] > 0; k++) {
				if (strncmp(sPatterns[k], sPOS, j, nPatternLen[k]) == 0
						&& strcmp(m_sWords[j - 1], toGB2312("·")) != 0
						&& strcmp(m_sWords[j + nPatternLen[k]], toGB2312("·")) != 0) {// Find
					// the
					// proper
					// pattern
					// k
					if (strcmp(sPatterns[k], toGB2312("FB")) == 0
							&& (sPOS[j + 2] == 'E' || sPOS[j + 2] == 'C' || sPOS[j + 2] == 'G')) {// Rule
																									// 1
																									// for
																									// exclusion:前缀+姓+名1(名2):
																									// 规则(前缀+姓)失效；
						continue;
					}
					/*
					 * if((strcmp(sPatterns[k],"BEE")==0||strcmp(sPatterns[k],"EE"
					 * )==0)&&strcmp(m_sWords[j+nPatternLen[k]-1],m_sWords[j+
					 * nPatternLen[k]-2])!=0) {//Rule 2 for
					 * exclusion:姓+单名+单名:单名+单名 若EE对应的字不同，规则失效.如：韩磊磊 continue; }
					 * 
					 * if(strcmp(sPatterns[k],"B")==0&&m_nBestTag[j+1]!=12)
					 * {//Rule 3 for exclusion: 若姓后不是后缀，规则失效.如：江主席、刘大娘 continue;
					 * }
					 */// Get the possible name
					nPos = j;// Record the person position in the tag sequence
					sPersonName[0] = 0;
					nLittleFreqCount = 0;// Record the number of role with
											// little frequency
					while (nPos < j + nPatternLen[k]) {// Get the possible
														// person name
														//
						if (m_nBestTag[nPos] < 4
								&& personDict.GetFrequency(m_sWords[nPos],
										m_nBestTag[nPos]) < LITTLE_FREQUENCY)
							nLittleFreqCount++;// The counter increase
						strcat(sPersonName, m_sWords[nPos]);
						nPos += 1;
					}
					/*
					 * if(IsAllForeign(sPersonName)&&personDict.GetFrequency(
					 * m_sWords[j],1)<LITTLE_FREQUENCY) {//Exclusion foreign
					 * name //Rule 2 for exclusion:若均为外国人名用字 规则(名1+名2)失效
					 * j+=nPatternLen[k]-1; continue; }
					 */if (strcmp(sPatterns[k], toGB2312("CDCD")) == 0) {// Rule
																			// for
						// exclusion
						// 规则(名1+名2+名1+名2)本身是排除规则:女高音歌唱家迪里拜尔演唱
						// Rule 3 for
						// exclusion:含外国人名用字
						// 规则适用
						// 否则，排除规则失效:黑妞白妞姐俩拔了头筹。
						if (GetForeignCharCount(sPersonName) > 0)
							j += nPatternLen[k] - 1;
						continue;
					}
					/*
					 * if(strcmp(sPatterns[k],"CD")==0&&IsAllForeign(sPersonName)
					 * ) {// j+=nPatternLen[k]-1; continue; }
					 * if(nLittleFreqCount==nPatternLen[k]||nLittleFreqCount==3)
					 * //马哈蒂尔;小扎耶德与他的中国阿姨胡彩玲受华黎明大使之邀， //The all roles appear
					 * with two lower frequecy,we will ignore them continue;
					 */
					m_nUnknownWords[m_nUnknownIndex][0] = m_nWordPosition[j];
					m_nUnknownWords[m_nUnknownIndex][1] = m_nWordPosition[j
							+ nPatternLen[k]];
					m_dWordsPossibility[m_nUnknownIndex] = -Math
							.log(dFactor[k])
							+ ComputePossibility(j, nPatternLen[k], personDict);
					// Mutiply the factor
					m_nUnknownIndex += 1;
					j += nPatternLen[k];
					bMatched = true;
				}
			}
			if (!bMatched)// Not matched, add j by 1
				j += 1;
		}
		return true;
	}

	/**
	 * 
	 * @param pWordItems
	 * @param nIndex
	 * @param dictCore
	 * @param dictUnknown
	 * @return
	 */
	private int GetFrom(PWORD_RESULT pWordItems[], int nIndex,
			CDictionary dictCore, CDictionary dictUnknown) {
		int nCount, aPOS[] = new int[MAX_POS_PER_WORD], aFreq[] = new int[MAX_POS_PER_WORD];
		int nFreq = 0, j, nRetPos = 0, nWordsIndex = 0;
		boolean bSplit = false;// Need to split in Transliteration recognition
		int i = 1, nPOSCount;
		char sCurWord[] = new char[WORD_MAXLENGTH];// Current word
		nWordsIndex = i + nIndex - 1;
		for (; i < MAX_WORDS_PER_SENTENCE
				&& pWordItems[nWordsIndex].p.sWord[0] != 0; i++) {
			if (m_tagType == TAG_TYPE.TT_NORMAL
					|| !dictUnknown
							.IsExist(pWordItems[nWordsIndex].p.sWord, 44)) {
				strcpy(m_sWords[i], pWordItems[nWordsIndex].p.sWord);// store
																		// current
																		// word
				m_nWordPosition[i + 1] = m_nWordPosition[i]
						+ strlen(m_sWords[i]);
			} else {
				if (!bSplit) {
					strncpy(m_sWords[i], pWordItems[nWordsIndex].p.sWord, 2);// store
																				// current
																				// word
					m_sWords[i][2] = 0;
					bSplit = true;
				} else {
					int nLen = strlen(pWordItems[nWordsIndex].p.sWord) - 2;
					strncpy(m_sWords[i], pWordItems[nWordsIndex].p.sWord, 2,
							nLen);// store current word
					m_sWords[i][nLen] = 0;
					bSplit = false;
				}
				m_nWordPosition[i + 1] = m_nWordPosition[i]
						+ strlen(m_sWords[i]);
			}
			// Record the position of current word
			m_nStartPos = m_nWordPosition[i + 1];
			// Move the Start POS to the ending
			if (m_tagType != TAG_TYPE.TT_NORMAL) {
				// Get the POSs from the unknown recognition dictionary
				strcpy(sCurWord, m_sWords[i]);
				if (m_tagType == TAG_TYPE.TT_TRANS_PERSON && i > 0
						&& charType(m_sWords[i - 1]) == CT_CHINESE) {
					if (m_sWords[i][0] == '.' && m_sWords[i][1] == 0)
						strcpy(sCurWord, toGB2312("．"));
					else if (m_sWords[i][0] == '-' && m_sWords[i][1] == 0)
						strcpy(sCurWord, toGB2312("－"));
				}
				Pint pnCount = new Pint();
				dictUnknown.GetHandle(sCurWord, pnCount, aPOS, aFreq);
				nCount = pnCount.value;

				nPOSCount = nCount + 1;
				for (j = 0; j < nCount; j++) {// Get the POS set of sCurWord in
												// the unknown dictionary
					m_nTags[i][j] = aPOS[j];
					m_dFrequency[i][j] = -Math.log((double) (1 + aFreq[j]))
							+ Math.log((double) (m_context.GetFrequency(0,
									aPOS[j]) + nPOSCount));
				}
				// Get the POS set of sCurWord in the core dictionary
				// We ignore the POS in the core dictionary and recognize them
				// as other (0).
				// We add their frequency to get the possibility as POS 0
				if (strcmp(m_sWords[i], toGB2312("始##始")) == 0) {
					m_nTags[i][j] = 100;
					m_dFrequency[i][j] = 0;
					j++;
				} else if (strcmp(m_sWords[i], toGB2312("末##末")) == 0) {
					m_nTags[i][j] = 101;
					m_dFrequency[i][j] = 0;
					j++;
				} else {
					Pint pp = new Pint();
					dictCore.GetHandle(m_sWords[i], pp, aPOS, aFreq);
					nCount = pp.value;
					nFreq = 0;
					for (int k = 0; k < nCount; k++) {
						nFreq += aFreq[k];
					}
					if (nCount > 0) {
						m_nTags[i][j] = 0;
						// m_dFrequency[i][j]=(double)(1+nFreq)/(double)(m_context.GetFrequency(0,0)+1);
						m_dFrequency[i][j] = -Math.log((double) (1 + nFreq))
								+ Math.log((double) (m_context.GetFrequency(0,
										0) + nPOSCount));
						j++;
					}
				}
			} else// For normal POS tagging
			{
				j = 0;
				// Get the POSs from the unknown recognition dictionary
				if (pWordItems[nWordsIndex].p.nHandle > 0) {// The word has is
															// only one POS
															// value
															// We have record
															// its POS and
															// nFrequncy in the
															// items.
					m_nTags[i][j] = pWordItems[nWordsIndex].p.nHandle;
					m_dFrequency[i][j] = -Math
							.log(pWordItems[nWordsIndex].p.dValue)
							+ Math.log((double) (m_context.GetFrequency(0,
									m_nTags[i][j]) + 1));
					if (m_dFrequency[i][j] < 0)// Not permit the value less than
												// 0
						m_dFrequency[i][j] = 0;
					j++;
				} else {// The word has multiple POSs, we should retrieve the
						// information from Core Dictionary

					if (pWordItems[nWordsIndex].p.nHandle < 0) {// The word has
																// is only one
																// POS value
																// We have
																// record its
																// POS and
																// nFrequncy in
																// the items.
						/*
						 * if(pWordItems[nWordsIndex].p.nHandle==-'t'*256-'t')//tt
						 * { char sWordOrg[100],sPostfix[10]; double
						 * dRatio=0.6925;//The ratio which transliteration as a
						 * person name
						 * PostfixSplit(pWordItems[nWordsIndex].p.sWord
						 * ,sWordOrg,sPostfix); if(sPostfix[0]!=0) dRatio=0.01;
						 * m_nTags[i][j]='n'*256+'r';
						 * m_dFrequency[i][j]=-Math.log
						 * (dRatio)+pWordItems[nWordsIndex].p.dValue;
						 * //m_dFrequency
						 * [i][j]=log(dRatio)+pWordItems[nWordsIndex
						 * ].p.dValue-Math
						 * .log(m_context.GetFrequency(0,m_nTags[i
						 * ][j]))+Math.log(MAX_FREQUENCE);
						 * //P(W|R)=P(WRT)/P(RT)=P(R)*P(W|T)/P(R|T) j++;
						 * m_nTags[i][j]='n'*256+'s';
						 * m_dFrequency[i][j]=-Math.log
						 * (1-dRatio)+pWordItems[nWordsIndex].p.dValue;
						 * //m_dFrequency
						 * [i][j]=log(1-dRatio)+pWordItems[nWordsIndex
						 * ].p.dValue-
						 * Math.log(m_context.GetFrequency(0,m_nTags[i
						 * ][j]))+Math.log(MAX_FREQUENCE); j++; } else//Unknown
						 * words such as Chinese person name or place name {
						 */
						m_nTags[i][j] = -pWordItems[nWordsIndex].p.nHandle;
						m_dFrequency[i][j++] = pWordItems[nWordsIndex].p.dValue;
						// }
					}
					Pint pcount = new Pint();
					dictCore.GetHandle(m_sWords[i], pcount, aPOS, aFreq);
					nCount = pcount.value;
					nPOSCount = nCount;
					for (; j < nCount; j++) {// Get the POS set of sCurWord in
												// the unknown dictionary
						m_nTags[i][j] = aPOS[j];
						m_dFrequency[i][j] = -Math.log(1 + aFreq[j])
								+ Math.log(m_context.GetFrequency(0,
										m_nTags[i][j]) + nPOSCount);
					}
				}
			}
			if (j == 0) {// We donot know the POS, so we have to guess them
							// according lexical knowledge
				Pint pj = new Pint();
				GuessPOS(i, pj);// Guess the POS of current word
				j = pj.value;
			}
			m_nTags[i][j] = -1;// Set the ending POS
			if (j == 1 && m_nTags[i][j] != CT_SENTENCE_BEGIN)// No ambuguity
			{// No ambuguity, so we can break from the loop
				i++;
				m_sWords[i][0] = 0;
				break;
			}
			if (!bSplit)
				nWordsIndex++;
		}
		if (pWordItems[nWordsIndex].p.sWord[0] == 0)
			nRetPos = -1;// Reaching ending

		if (m_nTags[i - 1][1] != -1)// ||m_sWords[i][0]==0
		{// Set end for words like "张/华/平"
			if (m_tagType != TAG_TYPE.TT_NORMAL)
				m_nTags[i][0] = 101;
			else
				m_nTags[i][0] = 1;

			m_dFrequency[i][0] = 0;
			m_sWords[i][0] = 0;// Set virtual ending
			m_nTags[i++][1] = -1;
		}
		m_nCurLength = (char) i;// The current word count
		if (nRetPos != -1)
			return nWordsIndex + 1;// Next start position
		return -1;// Reaching ending
	}

	// Set the tag type
	public void SetTagType() {
		m_tagType = TAG_TYPE.TT_NORMAL;
	}

	public void SetTagType(TAG_TYPE nType) {
		m_tagType = nType;
	}

	// POS tagging with Hidden Markov Model
	/**
	 * 对给定的分词结果，按照辞典和未知辞典，联合起来进行未知数据的词性标注 输出的结果，在m_nBestTag[]里面
	 * 
	 * @param pWordItems
	 *            输入的分词结果
	 * @param dictCore
	 *            核心辞典
	 * @param dictUnknown
	 *            未知的辞典
	 * @return
	 */
	public boolean POSTagging(PWORD_RESULT pWordItems[], CDictionary dictCore,
			CDictionary dictUnknown) {
		// pWordItems: Items; nItemCount: the count of items;core dictionary and
		// unknown recognition dictionary
		int i = 0, j, nStartPos;
		Reset(false);
		while (i > -1 && pWordItems[i].p.sWord[0] != 0) {
			nStartPos = i;// Start Position
			i = GetFrom(pWordItems, nStartPos, dictCore, dictUnknown);
			// logger.debug("i="+i);
			GetBestPOS();
			// logger.debug("call getbestpos finish.");
			switch (m_tagType) {
			case TT_NORMAL:// normal POS tagging
				j = 1;
				while (m_nBestTag[j] != -1 && j < m_nCurLength) {
					// Store the best POS tagging
					pWordItems[j + nStartPos - 1].p.nHandle = m_nBestTag[j];
					// Let 。be 0
					if (pWordItems[j + nStartPos - 1].p.dValue > 0
							&& dictCore.IsExist(
									pWordItems[j + nStartPos - 1].p.sWord, -1))
						// Exist and update its frequncy as a POS value
						pWordItems[j + nStartPos - 1].p.dValue = dictCore
								.GetFrequency(
										pWordItems[j + nStartPos - 1].p.sWord,
										m_nBestTag[j]);
					j += 1;
				}
				break;
			case TT_PERSON:// Person recognition
				PersonRecognize(dictUnknown);
				break;
			case TT_PLACE:// Place name recognition
			case TT_TRANS_PERSON:// Transliteration Person
				PlaceRecognize(dictCore, dictUnknown);
				break;
			default:
				break;
			}
			Reset();
		}
		return true;
	}

	// Guess the POS of No. nIndex word item

	private boolean GuessPOS(int nIndex, Pint pSubIndex) {
		int j = 0, i = nIndex, nCharType;
		int nLen;
		switch (m_tagType) {
		case TT_NORMAL:
			break;
		case TT_PERSON:
			j = 0;
			if (CC_Find(toGB2312("××"), m_sWords[nIndex]) != null) {
				m_nTags[i][j] = 6;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 6) + 1);
			} else {
				m_nTags[i][j] = 0;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 0) + 1);
				nLen = strlen(m_sWords[nIndex]);
				if (nLen >= 4) {
					m_nTags[i][j] = 0;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 0) + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 11) * 8);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 12) * 8);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 13) * 8);
				} else if (nLen == 2) {
					m_nTags[i][j] = 0;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 0) + 1);
					nCharType = charType(m_sWords[nIndex]);
					if (nCharType == CT_OTHER || nCharType == CT_CHINESE) {
						m_nTags[i][j] = 1;
						m_dFrequency[i][j++] = (double) 1
								/ (double) (m_context.GetFrequency(0, 1) + 1);
						m_nTags[i][j] = 2;
						m_dFrequency[i][j++] = (double) 1
								/ (double) (m_context.GetFrequency(0, 2) + 1);
						m_nTags[i][j] = 3;
						m_dFrequency[i][j++] = (double) 1
								/ (double) (m_context.GetFrequency(0, 3) + 1);
						m_nTags[i][j] = 4;
						m_dFrequency[i][j++] = (double) 1
								/ (double) (m_context.GetFrequency(0, 4) + 1);
					}
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 11) * 8);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 12) * 8);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 13) * 8);
				}
			}
			break;
		case TT_PLACE:
			j = 0;
			m_nTags[i][j] = 0;
			m_dFrequency[i][j++] = (double) 1
					/ (double) (m_context.GetFrequency(0, 0) + 1);
			nLen = strlen(m_sWords[nIndex]);
			if (nLen >= 4) {
				m_nTags[i][j] = 11;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 11) * 8);
				m_nTags[i][j] = 12;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 12) * 8);
				m_nTags[i][j] = 13;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 13) * 8);
			} else if (nLen == 2) {
				m_nTags[i][j] = 0;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 0) + 1);
				nCharType = charType(m_sWords[nIndex]);
				if (nCharType == CT_OTHER || nCharType == CT_CHINESE) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 1) + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 2) + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 3) + 1);
					m_nTags[i][j] = 4;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 4) + 1);
				}
				m_nTags[i][j] = 11;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 11) * 8);
				m_nTags[i][j] = 12;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 12) * 8);
				m_nTags[i][j] = 13;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 13) * 8);
			}
			break;
		case TT_TRANS_PERSON:
			j = 0;
			nLen = strlen(m_sWords[nIndex]);

			m_nTags[i][j] = 0;
			m_dFrequency[i][j++] = (double) 1
					/ (double) (m_context.GetFrequency(0, 0) + 1);

			if (!IsAllChinese(m_sWords[nIndex])) {
				if (IsAllLetter(m_sWords[nIndex])) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 1) + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 11) + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 2) * 2 + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 3) * 2 + 1);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 12) * 2 + 1);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 13) * 2 + 1);
				}
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 43) * 8);
			} else if (nLen >= 4) {
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 43) * 8);
			} else if (nLen == 2) {
				nCharType = charType(m_sWords[nIndex]);
				if (nCharType == CT_OTHER || nCharType == CT_CHINESE) {
					m_nTags[i][j] = 1;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 1) * 2 + 1);
					m_nTags[i][j] = 2;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 2) * 2 + 1);
					m_nTags[i][j] = 3;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 3) * 2 + 1);
					m_nTags[i][j] = 30;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 30) * 8 + 1);
					m_nTags[i][j] = 11;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 11) * 4 + 1);
					m_nTags[i][j] = 12;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 12) * 4 + 1);
					m_nTags[i][j] = 13;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 13) * 4 + 1);
					m_nTags[i][j] = 21;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 21) * 2 + 1);
					m_nTags[i][j] = 22;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 22) * 2 + 1);
					m_nTags[i][j] = 23;
					m_dFrequency[i][j++] = (double) 1
							/ (double) (m_context.GetFrequency(0, 23) * 2 + 1);
				}
				m_nTags[i][j] = 41;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 41) * 8);
				m_nTags[i][j] = 42;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 42) * 8);
				m_nTags[i][j] = 43;
				m_dFrequency[i][j++] = (double) 1
						/ (double) (m_context.GetFrequency(0, 43) * 8);
			}
			break;
		default:
			break;
		}
		pSubIndex.value = j;
		return true;
	}

	/**
	 * 利用词典计算可能性
	 * 
	 * @param nStartPos
	 * @param nLength
	 * @return
	 */
	private double ComputePossibility(int nStartPos, int nLength,
			CDictionary dict) {
		double dRetValue = 0, dPOSPoss;
		// dPOSPoss: the possibility of a POS appears
		// dContextPoss: The possibility of context POS appears
		int nFreq;
		for (int i = nStartPos; i < nStartPos + nLength; i++) {
			nFreq = dict.GetFrequency(m_sWords[i], m_nBestTag[i]);
			// nFreq is word being the POS
			dPOSPoss = Math.log((double) (m_context.GetFrequency(0,
					m_nBestTag[i]) + 1)) - Math.log((double) (nFreq + 1));
			dRetValue += dPOSPoss;
		}
		return dRetValue;
	}

	/**
	 * 地点识别算法
	 * 
	 * @param dictCore
	 * @param placeDict
	 * @return
	 */
	private boolean PlaceRecognize(CDictionary dictCore, CDictionary placeDict) {
		int nStart = 1, nEnd = 1, i = 1, nTemp;
		double dPanelty = 1.0;// Panelty value
		while (m_nBestTag[i] > -1) {
			if (m_nBestTag[i] == 1)// 1 Trigger the recognition procession
			{
				nStart = i;
				nEnd = nStart + 1;
				while (m_nBestTag[nEnd] == 1)//
				{
					if (nEnd > nStart + 1)
						dPanelty += 1.0;
					nEnd++;
				}
				while (m_nBestTag[nEnd] == 2)
					// 2,12,22
					nEnd++;
				nTemp = nEnd;
				while (m_nBestTag[nEnd] == 3) {
					if (nEnd > nTemp)
						dPanelty += 1.0;
					nEnd++;
				}
			} else if (m_nBestTag[i] == 2)// 1,11,21 Trigger the recognition
			{
				dPanelty += 1.0;
				nStart = i;
				nEnd = nStart + 1;
				while (m_nBestTag[nEnd] == 2)
					// 2
					nEnd++;
				nTemp = nEnd;
				while (m_nBestTag[nEnd] == 3)// 2
				{
					if (nEnd > nTemp)
						dPanelty += 1.0;
					nEnd++;
				}
			}
			if (nEnd > nStart) {
				m_nUnknownWords[m_nUnknownIndex][0] = m_nWordPosition[nStart];
				m_nUnknownWords[m_nUnknownIndex][1] = m_nWordPosition[nEnd];
				m_dWordsPossibility[m_nUnknownIndex++] = ComputePossibility(
						nStart, nEnd - nStart + 1, placeDict)
						+ Math.log(dPanelty);
				nStart = nEnd;
			}
			if (i < nEnd)
				i = nEnd;
			else
				i = i + 1;
		}
		return true;
	}

}
