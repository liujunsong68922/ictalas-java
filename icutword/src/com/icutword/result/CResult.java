package com.icutword.result;

import org.apache.log4j.Logger;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dictionary.PWORD_RESULT;
import com.icutword.model.dictionary.WORD_RESULT;
import com.icutword.segment.CSegment;
//import com.icutword.unknown.CUnknowWord;
import com.icutword.unknown.CUnknowWord;
import com.icutword.unknown.span.CSpan;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;
import com.icutword.utility.LogUtil;

public class CResult extends CUtility {
	static Logger logger = Logger.getLogger("logger");

	int WORD_MAXLENGTH = 100;
	int MAX_WORDS = 650;
	int MAX_SEGMENT_NUM = 10;
	int MAX_SENTENCE_LEN = 2000;

	PWORD_RESULT m_pResult[][];
	// The buffer which store the segment and POS result
	// and They stored order by its possibility
	double m_dResultPossibility[] = new double[MAX_SEGMENT_NUM];
	double m_dSmoothingPara;

	int m_nResultCount;
	int m_nOperateType;// 0:Only Segment;1: First Tag; 2:Second Type
	int m_nOutputFormat;// 0:PKU criterion;1:973 criterion; 2: XML criterion

	CSegment m_Seg = new CSegment();// Seg class
	CDictionary m_dictCore = new CDictionary(),
			m_dictBigram = new CDictionary();// Core dictionary,bigram
												// dictionary
	CSpan m_POSTagger = new CSpan();// POS tagger
	// 暂时屏蔽掉CUnknowWord的处理逻辑
	CUnknowWord m_uPerson = new CUnknowWord(); //人名识别
	CUnknowWord m_uTransPerson = new CUnknowWord(); //翻译人名识别
	CUnknowWord m_uPlace = new CUnknowWord();// 地名识别

	String DICT_FILE = "Data/coreDict.dct";
	String LEXICAL_FILE = "Data/lexical.ctx";
	String BIGRAM_FILE = "Data/BigramDict.dct";
	String PERSON_FILE = "Data/nr"; // 人名
	String PLACE_FILE = "Data/ns"; // 地名
	String TRANSPERSON_FILE = "Data/tr"; // 翻译

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	static char[] concatenate(char[] str1, char[] str2) {
		char str0[] = new char[500];
		char p[] = str0;
		int i = 0;
		while ((p[i] = str1[i]) != '\0')
			i++;
		// i--;
		int j = 0;
		while ((p[i] = str2[j]) != '\0') {
			i++;
			j++;
		}
		p[i] = '\0';
		return str0;
	}

	CResult(char strDataPath[]) {
		// malloc buffer
		m_pResult = new PWORD_RESULT[MAX_SEGMENT_NUM][MAX_WORDS];
		for (int i = 0; i < MAX_SEGMENT_NUM; i++)
			for (int j = 0; j < MAX_WORDS; j++) {
				m_pResult[i][j] = new PWORD_RESULT(new WORD_RESULT());
			}

		m_dictCore.Load(concatenate(strDataPath, toGB2312(DICT_FILE)));
		m_POSTagger
				.LoadContext(concatenate(strDataPath, toGB2312(LEXICAL_FILE)));

		m_POSTagger.SetTagType();// 按照通常模式进行标注，标注词性

		// 未知的人名规则,暂时忽略
		// Set the person recognition configure
		m_uPerson.Configure(concatenate(strDataPath, toGB2312(PERSON_FILE)),
				TAG_TYPE.TT_PERSON);

		// 未知的地名规则,暂时忽略
		m_uPlace.Configure(concatenate(strDataPath, toGB2312(PLACE_FILE)),
				TAG_TYPE.TT_PLACE);
		// Set the place recognition configure

		// 未知的翻译人名规则,暂时忽略
		m_uTransPerson.Configure(
				concatenate(strDataPath, toGB2312(TRANSPERSON_FILE)),
				TAG_TYPE.TT_TRANS_PERSON);
		// Set the transliteration person recognition configure

		// 操作模式： 0 仅分词 1.先划词性 2 并行模式
		m_nOperateType = 2;// 0:Only Segment;1: First Tag; 2:Second Type

		// 输出格式定义
		// 0->PKU北京大学分词格式1->973 分词格式2->XML格式
		m_nOutputFormat = 0;// 0:PKU criterion;1:973 criterion; 2: XML criterion

		m_dSmoothingPara = 0.1;// Smoothing parameter
		// 读取分词辞典定义
		m_dictBigram.Load(concatenate(strDataPath, toGB2312(BIGRAM_FILE)));

	}

	/**
	 * 输出计算结果到一个字符串数组里面去
	 * 
	 * @param pItem
	 * @param sResult
	 * @return
	 */
	private boolean _Output(PWORD_RESULT pItem[], char[] sResult) {
		return CResultFunction.Output(pItem, sResult, m_nOutputFormat,
				m_nOperateType, false);
	}

	/**
	 * 输出计算结果到一个字符串数组里面去
	 * 
	 * @param pItem
	 * @param sResult
	 * @return
	 */
	private boolean _Output(PWORD_RESULT pItem[], char[] sResult, boolean flag) {
		return CResultFunction.Output(pItem, sResult, m_nOutputFormat,
				m_nOperateType, flag);
	}

	/**
	 * 对一个字符串进行处理,输入sSentence[],输出m_Seg.
	 * 
	 * @param sSentence
	 * @param nCount
	 *            定义需要产生的计算结果
	 * @return
	 */
	private boolean _Processing(char sSentence[], int nCount) {
		int nIndex;
		boolean _ICT_DEBUG = true; // 调试变量

		char sSegment[];
		sSegment = new char[MAX_SENTENCE_LEN * 2]; // 因为需要考虑增加词性的内容

		// Unigram segment
		// m_Seg.Segment(sSentence,m_dictCore,nCount);
		// Bigram segment
		// 生成二叉树切分结果
		// 输入：sSentence,m_dSmoothingPara,m_dictCore,m_dictBigram,ncount
		// 输出: m_Seg.m_graphSeg.m_segGraph
		logger.debug("-----开始生成二叉树");
		m_Seg.BiSegment(sSentence, m_dSmoothingPara, m_dictCore, m_dictBigram,
				nCount);
		logger.debug("-----二叉树生成结束.计算结果二叉树总长度:" + m_Seg.m_nSegmentCount);

		m_nResultCount = m_Seg.m_nSegmentCount;
		// Record the number of result
		for (nIndex = 0; nIndex < m_Seg.m_nSegmentCount; nIndex++) {
			if (_ICT_DEBUG) {
				logger.debug("属性标识开始.调用CSpan.");
				m_POSTagger.POSTagging(m_Seg.m_pWordSeg[nIndex], m_dictCore,
						m_dictCore);
				_Output(m_Seg.m_pWordSeg[nIndex], sSegment);
				logger.debug("属性标识完毕.");
				logger.debug(CStringFunction.getCString(sSegment));

				// printf("POS Tag%d:%s\n", nIndex + 1, sSegment);
			}
			logger.debug("开始进行第一次人名识别");
			m_uPerson.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("第一次人名识别完成");

			char sSegment2[];
			sSegment2 = new char[MAX_SENTENCE_LEN * 2]; // 因为需要考虑增加词性的内容
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment2);
			logger.debug(CStringFunction.getCString(sSegment2));

			logger.debug("开始进行第一次翻译人名识别");
			m_uTransPerson.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("第一次人名翻译人名识别完成");

			char sSegment3[];
			sSegment3 = new char[MAX_SENTENCE_LEN * 2]; // 因为需要考虑增加词性的内容
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment3);
			logger.debug(CStringFunction.getCString(sSegment3));

			logger.debug("开始进行第一次地名识别");
			m_uPlace.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("第一次地名识别完成");

			char sSegment4[];
			sSegment4 = new char[MAX_SENTENCE_LEN * 2]; // 因为需要考虑增加词性的内容
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment4);
			logger.debug(CStringFunction.getCString(sSegment4));
		}

		// Unigram
		// m_Seg.OptimumSegmet(nCount);
		// Bigram
		// 对二叉树进行优化
		logger.debug("-----二叉树的各个未知点的词性和距离已经标志完毕，开始优化二叉树");
		m_Seg.BiOptimumSegment(nCount, m_dSmoothingPara, m_dictBigram,
				m_dictCore);
		logger.debug("-----优化二叉树完成");

		char sSegment5[];
		sSegment5 = new char[MAX_SENTENCE_LEN * 2]; // 因为需要考虑增加词性的内容
		_Output(m_Seg.m_pWordSeg[0], sSegment5);
		logger.debug(CStringFunction.getCString(sSegment5));

		for (nIndex = 0; nIndex < m_Seg.m_nSegmentCount; nIndex++) {
			logger.debug("-----第二次开始标记词性");
			// TODO:此处程序有bug，需要考虑进行调整,第二次优化的时候会出现错误
			m_POSTagger.POSTagging(m_Seg.m_pWordSeg[nIndex], m_dictCore,
					m_dictCore);
			logger.debug("-----第二次标记词性完成");
			if (_ICT_DEBUG) {
				_Output(m_Seg.m_pWordSeg[nIndex], sSegment);
				logger.debug(CStringFunction.getCString(sSegment));
			}
		}

		logger.debug("对多个二叉树计算结果进行排序开始。");
		_Sort();// Sort the ending
		logger.debug("对多个二叉树计算结果排序完成。");

		if (_ICT_DEBUG) {
			for (nIndex = 0; nIndex < m_Seg.m_nSegmentCount; nIndex++) {
				_Output(m_pResult[nIndex], sSegment);
				// printf("POS Tag%d(P=Exp(%f)):%s\n",nIndex+1,m_dResultPossibility[nIndex],sSegment);
			}
			// delete [] sSegment;
			sSegment = null;
		}
		return true;
	}

	// Sort the segmentation and POS result according its possibility
	/**
	 * 按照POS的可能性大小进行排序,先计算一次 输入：m_Seg 输出：m_Seg.m_pWordSeg，m_pResult
	 * 
	 * @return
	 */
	private boolean _Sort() {
		double dPossibility[] = new double[MAX_SEGMENT_NUM], dTemp;
		int nIndex[] = new int[MAX_SEGMENT_NUM], nTemp;// Index

		int i;
		for (i = 0; i < m_Seg.m_nSegmentCount; i++) {
			// Computing the possibility
			// 先计算每一个分词记录的可能性
			// dPossibility[i] = CResultFunction.ComputePossibility(
			// m_Seg.m_pWordSeg[i], m_POSTagger);
			nIndex[i] = i;// Record the index
		}

		// Sort with Bubble sort algorithm
		// 采用冒泡法，对dPossibility进行排序，排序结果存放在nIndex[]里面
		for (i = 0; i < m_Seg.m_nSegmentCount; i++)
			for (int j = i + 1; j < m_Seg.m_nSegmentCount; j++) {
				if (dPossibility[i] < dPossibility[j]) {
					// Swap the possition and value
					nTemp = nIndex[i];
					dTemp = dPossibility[i];
					nIndex[i] = nIndex[j];
					dPossibility[i] = dPossibility[j];
					nIndex[j] = nTemp;
					dPossibility[j] = dTemp;
				}
			}

		for (i = 0; i < m_Seg.m_nSegmentCount; i++) {
			// Adjust the segmentation and POS result and store them in the
			// final result array Store them according their possibility
			// ascendly
			// 调整切分结果，标记结果的词性，存储入最后的结果数组。根据可能性升序排列。
			CResultFunction.Adjust(m_Seg.m_pWordSeg[nIndex[i]], m_pResult[i]);
			m_dResultPossibility[i] = dPossibility[i];
		}
		return true;
	}

	// Paragraph Segment and POS Tagging
	/**
	 * <li>图形分词以及位置标注 
	 * <li>这部分分词的特点，是把语言单纯当成了数学公式，忽略了语言本身的内部表现逻辑
	 * <li>单纯按照中文的字面来进行分词，实际上是忽略了中文语言本身的语法特点
	 * <li>抛弃其中90%的信息，而直接将中文简单等同于其他语言来使用
	 * <li>在中文里面，上下文的相关语境，是相当重要的基础信息，应当利用这一信息来进行精确切分
	 * <li>这样的程序才是一个真正具有活力和适应性的程序
	 * 
	 * @param sParagraph
	 * @param sResult
	 * @return
	 */
	public boolean ParagraphProcessing(char sParagraph[], char sResult[]) {
		// logger.info("ParagraphProcessing 调用开始:"
		// + CStringFunction.getCString(sParagraph));
		// step1:变量定义及初始化
		char sSentence[], sChar[] = new char[3];
		char sSentenceResult[];
		int nLen = strlen(sParagraph) + 13;
		sSentence = new char[nLen];// malloc buffer
		sSentenceResult = new char[nLen * 3];// malloc buffer
		sSentence[0] = 0;
		int nPosIndex = 0, nParagraphLen = strlen(sParagraph), nSentenceIndex = 0;
		sChar[2] = 0;
		sResult[0] = 0;// Init the result
		boolean bFirstIgnore = true;

		// step2:根据输入的字符进行切分断句，然后调用Processing方法
		strcpy(sSentence, toGB2312(SENTENCE_BEGIN));// Add a sentence begin flag
		while (nPosIndex < nParagraphLen) {// Find a whole sentence which
											// separated by ! . \n \r
			sChar[0] = sParagraph[nPosIndex];// Get a char
			sChar[1] = 0;
			if ((byte) sParagraph[nPosIndex] < 0) {// double byte char
				nPosIndex += 1;
				sChar[1] = sParagraph[nPosIndex];
			}
			nPosIndex += 1;
			/*
			 * #define SEPERATOR_C_SENTENCE "。！？：；…" #define
			 * SEPERATOR_C_SUB_SENTENCE "、，（）“”‘’" #define SEPERATOR_E_SENTENCE
			 * "!?:;" #define SEPERATOR_E_SUB_SENTENCE ",()\042'" #define
			 * SEPERATOR_LINK "\n\r 　"
			 */
			if (CC_Find(toGB2312(SEPERATOR_C_SENTENCE), sChar) != null
					|| CC_Find(toGB2312(SEPERATOR_C_SUB_SENTENCE), sChar) != null
					|| strstr(toGB2312(SEPERATOR_E_SENTENCE), sChar) != null
					|| strstr(toGB2312(SEPERATOR_E_SUB_SENTENCE), sChar) != null
					|| strstr(toGB2312(SEPERATOR_LINK), sChar) != null) {
				// Reach end of a sentence.Get a whole sentence
				if (strstr(toGB2312(SEPERATOR_LINK), sChar) == null)

				{// Not link seperator
					strcat(sSentence, sChar);
					logger.debug(CStringFunction.getCString(sSentence));
				}
				if (sSentence[0] != 0
						&& strcmp(sSentence, toGB2312(SENTENCE_BEGIN)) != 0) {
					if (strstr(toGB2312(SEPERATOR_C_SUB_SENTENCE), sChar) != null
							&& strstr(toGB2312(SEPERATOR_E_SUB_SENTENCE), sChar) != null)
						strcat(sSentence, toGB2312(SENTENCE_END));
					// Add sentence ending flag

					logger.debug(">>>>>>>>>>>enter Process...发现了中断符号");
					logger.debug(CStringFunction.getCString(sSentence));
					_Processing(sSentence, 1);// Processing and output the
												// result
												// of current sentence.
					_Output(m_pResult[0], sSentenceResult, bFirstIgnore);
					// Output to the imediate result
					// bFirstIgnore=true;
					strcat(sResult, sSentenceResult);// Store in the result
														// buffer
					logger.debug(sResult);
					logger.debug(CStringFunction.getCString(sSentenceResult));
				}
				if (strstr(toGB2312(SEPERATOR_LINK), sChar) != null)// Link the
																	// result
																	// with the
																	// SEPERATOR_LINK
				{
					strcat(sResult, sChar);
					strcpy(sSentence, toGB2312(SENTENCE_BEGIN));// Add a
																// sentence
																// begin flag
					logger.debug(sResult);
					logger.debug(CStringFunction.getCString(sSentenceResult));
					// sSentence[0]=0;//New sentence, and begin new segmentation
					// bFirstIgnore=false;
				} else if (strstr(toGB2312(SEPERATOR_C_SENTENCE), sChar) != null
						|| strstr(toGB2312(SEPERATOR_E_SENTENCE), sChar) != null) {
					strcpy(sSentence, toGB2312(SENTENCE_BEGIN));// Add a
																// sentence
																// begin flag
					logger.debug(CStringFunction.getCString(sSentence));
					// sSentence[0]=0;//New sentence, and begin new segmentation
					// bFirstIgnore=false;
				} else {
					strcpy(sSentence, sChar);// reset current sentence, and add
												// the previous end at begin
												// position
					logger.debug(CStringFunction.getCString(sSentence));
				}
			} else { // Other chars and store in the sentence buffer
				strcat(sSentence, sChar);
				logger.debug(CStringFunction.getCString(sSentence));
			}
		}// while 循环结束

		if (sSentence[0] != 0
				&& strcmp(sSentence, toGB2312(SENTENCE_BEGIN)) != 0) {
			strcat(sSentence, toGB2312(SENTENCE_END));// Add sentence ending
														// flag
			logger.debug(">循环结束，切分剩余部分。");
			logger.debug(CStringFunction.getCString(sSentence));
			_Processing(sSentence, 1);// Processing and output the result of
										// current sentence.
			// logger.info(">Processing 调用结束.");
			// Output to the imediate result
			_Output(m_pResult[0], sSentenceResult, bFirstIgnore);

			strcat(sResult, sSentenceResult);// Store in the result buffer
			// logger.info(CStringFunction.getCString(sSentenceResult));
		}
		// delete [] sSentence;//FREE sentence buffer
		// delete [] sSentenceResult;//free buffer
		return true;
	}

}
