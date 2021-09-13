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
	// ��ʱ���ε�CUnknowWord�Ĵ����߼�
	CUnknowWord m_uPerson = new CUnknowWord(); //����ʶ��
	CUnknowWord m_uTransPerson = new CUnknowWord(); //��������ʶ��
	CUnknowWord m_uPlace = new CUnknowWord();// ����ʶ��

	String DICT_FILE = "Data/coreDict.dct";
	String LEXICAL_FILE = "Data/lexical.ctx";
	String BIGRAM_FILE = "Data/BigramDict.dct";
	String PERSON_FILE = "Data/nr"; // ����
	String PLACE_FILE = "Data/ns"; // ����
	String TRANSPERSON_FILE = "Data/tr"; // ����

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

		m_POSTagger.SetTagType();// ����ͨ��ģʽ���б�ע����ע����

		// δ֪����������,��ʱ����
		// Set the person recognition configure
		m_uPerson.Configure(concatenate(strDataPath, toGB2312(PERSON_FILE)),
				TAG_TYPE.TT_PERSON);

		// δ֪�ĵ�������,��ʱ����
		m_uPlace.Configure(concatenate(strDataPath, toGB2312(PLACE_FILE)),
				TAG_TYPE.TT_PLACE);
		// Set the place recognition configure

		// δ֪�ķ�����������,��ʱ����
		m_uTransPerson.Configure(
				concatenate(strDataPath, toGB2312(TRANSPERSON_FILE)),
				TAG_TYPE.TT_TRANS_PERSON);
		// Set the transliteration person recognition configure

		// ����ģʽ�� 0 ���ִ� 1.�Ȼ����� 2 ����ģʽ
		m_nOperateType = 2;// 0:Only Segment;1: First Tag; 2:Second Type

		// �����ʽ����
		// 0->PKU������ѧ�ִʸ�ʽ1->973 �ִʸ�ʽ2->XML��ʽ
		m_nOutputFormat = 0;// 0:PKU criterion;1:973 criterion; 2: XML criterion

		m_dSmoothingPara = 0.1;// Smoothing parameter
		// ��ȡ�ִʴǵ䶨��
		m_dictBigram.Load(concatenate(strDataPath, toGB2312(BIGRAM_FILE)));

	}

	/**
	 * �����������һ���ַ�����������ȥ
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
	 * �����������һ���ַ�����������ȥ
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
	 * ��һ���ַ������д���,����sSentence[],���m_Seg.
	 * 
	 * @param sSentence
	 * @param nCount
	 *            ������Ҫ�����ļ�����
	 * @return
	 */
	private boolean _Processing(char sSentence[], int nCount) {
		int nIndex;
		boolean _ICT_DEBUG = true; // ���Ա���

		char sSegment[];
		sSegment = new char[MAX_SENTENCE_LEN * 2]; // ��Ϊ��Ҫ�������Ӵ��Ե�����

		// Unigram segment
		// m_Seg.Segment(sSentence,m_dictCore,nCount);
		// Bigram segment
		// ���ɶ������зֽ��
		// ���룺sSentence,m_dSmoothingPara,m_dictCore,m_dictBigram,ncount
		// ���: m_Seg.m_graphSeg.m_segGraph
		logger.debug("-----��ʼ���ɶ�����");
		m_Seg.BiSegment(sSentence, m_dSmoothingPara, m_dictCore, m_dictBigram,
				nCount);
		logger.debug("-----���������ɽ���.�������������ܳ���:" + m_Seg.m_nSegmentCount);

		m_nResultCount = m_Seg.m_nSegmentCount;
		// Record the number of result
		for (nIndex = 0; nIndex < m_Seg.m_nSegmentCount; nIndex++) {
			if (_ICT_DEBUG) {
				logger.debug("���Ա�ʶ��ʼ.����CSpan.");
				m_POSTagger.POSTagging(m_Seg.m_pWordSeg[nIndex], m_dictCore,
						m_dictCore);
				_Output(m_Seg.m_pWordSeg[nIndex], sSegment);
				logger.debug("���Ա�ʶ���.");
				logger.debug(CStringFunction.getCString(sSegment));

				// printf("POS Tag%d:%s\n", nIndex + 1, sSegment);
			}
			logger.debug("��ʼ���е�һ������ʶ��");
			m_uPerson.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("��һ������ʶ�����");

			char sSegment2[];
			sSegment2 = new char[MAX_SENTENCE_LEN * 2]; // ��Ϊ��Ҫ�������Ӵ��Ե�����
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment2);
			logger.debug(CStringFunction.getCString(sSegment2));

			logger.debug("��ʼ���е�һ�η�������ʶ��");
			m_uTransPerson.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("��һ��������������ʶ�����");

			char sSegment3[];
			sSegment3 = new char[MAX_SENTENCE_LEN * 2]; // ��Ϊ��Ҫ�������Ӵ��Ե�����
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment3);
			logger.debug(CStringFunction.getCString(sSegment3));

			logger.debug("��ʼ���е�һ�ε���ʶ��");
			m_uPlace.Recognition(m_Seg.m_pWordSeg[nIndex],
					m_Seg.m_graphOptimum_DynamicArray,
					m_Seg.m_graphSeg_CSegGraph, m_dictCore);
			logger.debug("��һ�ε���ʶ�����");

			char sSegment4[];
			sSegment4 = new char[MAX_SENTENCE_LEN * 2]; // ��Ϊ��Ҫ�������Ӵ��Ե�����
			_Output(m_Seg.m_pWordSeg[nIndex], sSegment4);
			logger.debug(CStringFunction.getCString(sSegment4));
		}

		// Unigram
		// m_Seg.OptimumSegmet(nCount);
		// Bigram
		// �Զ����������Ż�
		logger.debug("-----�������ĸ���δ֪��Ĵ��Ժ;����Ѿ���־��ϣ���ʼ�Ż�������");
		m_Seg.BiOptimumSegment(nCount, m_dSmoothingPara, m_dictBigram,
				m_dictCore);
		logger.debug("-----�Ż����������");

		char sSegment5[];
		sSegment5 = new char[MAX_SENTENCE_LEN * 2]; // ��Ϊ��Ҫ�������Ӵ��Ե�����
		_Output(m_Seg.m_pWordSeg[0], sSegment5);
		logger.debug(CStringFunction.getCString(sSegment5));

		for (nIndex = 0; nIndex < m_Seg.m_nSegmentCount; nIndex++) {
			logger.debug("-----�ڶ��ο�ʼ��Ǵ���");
			// TODO:�˴�������bug����Ҫ���ǽ��е���,�ڶ����Ż���ʱ�����ִ���
			m_POSTagger.POSTagging(m_Seg.m_pWordSeg[nIndex], m_dictCore,
					m_dictCore);
			logger.debug("-----�ڶ��α�Ǵ������");
			if (_ICT_DEBUG) {
				_Output(m_Seg.m_pWordSeg[nIndex], sSegment);
				logger.debug(CStringFunction.getCString(sSegment));
			}
		}

		logger.debug("�Զ����������������������ʼ��");
		_Sort();// Sort the ending
		logger.debug("�Զ��������������������ɡ�");

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
	 * ����POS�Ŀ����Դ�С��������,�ȼ���һ�� ���룺m_Seg �����m_Seg.m_pWordSeg��m_pResult
	 * 
	 * @return
	 */
	private boolean _Sort() {
		double dPossibility[] = new double[MAX_SEGMENT_NUM], dTemp;
		int nIndex[] = new int[MAX_SEGMENT_NUM], nTemp;// Index

		int i;
		for (i = 0; i < m_Seg.m_nSegmentCount; i++) {
			// Computing the possibility
			// �ȼ���ÿһ���ִʼ�¼�Ŀ�����
			// dPossibility[i] = CResultFunction.ComputePossibility(
			// m_Seg.m_pWordSeg[i], m_POSTagger);
			nIndex[i] = i;// Record the index
		}

		// Sort with Bubble sort algorithm
		// ����ð�ݷ�����dPossibility�������������������nIndex[]����
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
			// �����зֽ������ǽ���Ĵ��ԣ��洢�����Ľ�����顣���ݿ������������С�
			CResultFunction.Adjust(m_Seg.m_pWordSeg[nIndex[i]], m_pResult[i]);
			m_dResultPossibility[i] = dPossibility[i];
		}
		return true;
	}

	// Paragraph Segment and POS Tagging
	/**
	 * <li>ͼ�ηִ��Լ�λ�ñ�ע 
	 * <li>�ⲿ�ִַʵ��ص㣬�ǰ����Ե�����������ѧ��ʽ�����������Ա�����ڲ������߼�
	 * <li>�����������ĵ����������зִʣ�ʵ�����Ǻ������������Ա�����﷨�ص�
	 * <li>��������90%����Ϣ����ֱ�ӽ����ļ򵥵�ͬ������������ʹ��
	 * <li>���������棬�����ĵ�����ﾳ�����൱��Ҫ�Ļ�����Ϣ��Ӧ��������һ��Ϣ�����о�ȷ�з�
	 * <li>�����ĳ������һ���������л�������Ӧ�Եĳ���
	 * 
	 * @param sParagraph
	 * @param sResult
	 * @return
	 */
	public boolean ParagraphProcessing(char sParagraph[], char sResult[]) {
		// logger.info("ParagraphProcessing ���ÿ�ʼ:"
		// + CStringFunction.getCString(sParagraph));
		// step1:�������弰��ʼ��
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

		// step2:����������ַ������зֶϾ䣬Ȼ�����Processing����
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
			 * #define SEPERATOR_C_SENTENCE "������������" #define
			 * SEPERATOR_C_SUB_SENTENCE "����������������" #define SEPERATOR_E_SENTENCE
			 * "!?:;" #define SEPERATOR_E_SUB_SENTENCE ",()\042'" #define
			 * SEPERATOR_LINK "\n\r ��"
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

					logger.debug(">>>>>>>>>>>enter Process...�������жϷ���");
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
		}// while ѭ������

		if (sSentence[0] != 0
				&& strcmp(sSentence, toGB2312(SENTENCE_BEGIN)) != 0) {
			strcat(sSentence, toGB2312(SENTENCE_END));// Add sentence ending
														// flag
			logger.debug(">ѭ���������з�ʣ�ಿ�֡�");
			logger.debug(CStringFunction.getCString(sSentence));
			_Processing(sSentence, 1);// Processing and output the result of
										// current sentence.
			// logger.info(">Processing ���ý���.");
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
