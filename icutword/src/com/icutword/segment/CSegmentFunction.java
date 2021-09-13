package com.icutword.segment;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dynamicarray.CDynamicArray;
import com.icutword.model.dynamicarray.PARRAY_CHAIN;
import com.icutword.model.dynamicarray.PPARRAY_CHAIN;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

public class CSegmentFunction extends CUtility {
	static int MAX_WORDS = 650;
	static int MAX_SEGMENT_NUM = 10;
	static int MAX_SENTENCE_LEN = 2000;
	static int WORD_MAXLENGTH = 100;
	static int MAX_FREQUENCE = 2079997;

	/**
	 * �������ַ����Ǵ�������
	 * @param sNum
	 * @return
	 */
	public static boolean IsYearTime(char[] sNum) {// Judge whether the sNum is
													// a num
		// genearating year
		int nLen = strlen(sNum);
		char sTemp[] = new char[3];
		strncpy(sTemp, sNum, 2);
		sTemp[2] = 0;
		if (IsAllSingleByte(sNum) && (nLen == 4 || nLen == 2 && sNum[0] > '4'))// 1992��,
			// 90��
			return true;
		if (IsAllNum(sNum)
				&& (nLen >= 6 || nLen == 4
						&& CC_Find(toGB2312("����������"), sTemp) != null))
			return true;
		if (GetCharCount(toGB2312("���һ�����������߰˾�Ҽ��������½��ƾ�"), sNum) == (int) nLen / 2
				&& nLen >= 3)
			return true;
		if (nLen == 8 && GetCharCount(toGB2312("ǧǪ���"), sNum) == 2)// ��Ǫ�����
			return true;
		if (nLen == 2 && GetCharCount(toGB2312("ǧǪ"), sNum) == 1)
			return true;

		sTemp = new char[nLen - 2 + 1];
		int i;
		for (i = 0; sNum[i + 2] != '\0'; i++)
			sTemp[i] = sNum[i + 2];
		sTemp[i] = '\0';

		if (nLen == 4 && GetCharCount(toGB2312("���ұ����켺�����ɹ�"), sNum) == 1
				&& GetCharCount(toGB2312("�ӳ���î������δ�����纥"), sTemp) == 1)
			return true;
		return false;
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
	public static int biGraphGenerate(CDynamicArray aWordIn,
			CDynamicArray aBinaryWordNet, double dSmoothingPara,
			CDictionary DictBinary, CDictionary DictCore,
			CSegment caller) {

		int m_nWordCount = 0;

		PARRAY_CHAIN pTail = null;
		PARRAY_CHAIN pCur, pNextWords;// Temp buffer
		int nWordIndex = 0, nTwoWordsFreq = 0, nCurWordIndex, nNextWordIndex;
		// nWordIndex: the index number of current word
		double dCurFreqency, dValue, dTemp;
		char sTwoWords[] = new char[WORD_MAXLENGTH];

		PPARRAY_CHAIN ppTail = new PPARRAY_CHAIN(null);

		// Get tail element and return the words count
		m_nWordCount = aWordIn.GetTail(ppTail);
		pTail = ppTail.p;

		// m_npWordPosMapTable���г�ʼ��
		// Word count is greater than 0
		if (m_nWordCount > 0) {
			// Record the position of possible words
			// ��¼�����ݽ�row,col�ϲ���һ��int�����д洢
			caller.m_npWordPosMapTable = new int[m_nWordCount];
		} else {
			return 0; // ���ݴ���ֱ�ӷ��ؼ���
		}

		// ѭ����ȡ����������Ķ�̬���飬����ʼ��һ��λ������
		pCur = aWordIn.GetHead();
		while (pCur != null && pCur.p != null)// Set the position map of words
		{
			caller.m_npWordPosMapTable[nWordIndex++] = pCur.p.row * MAX_SENTENCE_LEN
					+ pCur.p.col;
			pCur = pCur.p.next;
		}

		// �ٴζԶ�̬�������ѭ����ȡ
		pCur = aWordIn.GetHead();
		while (pCur != null && pCur.p != null)//
		{
			//���õ�ǰ�ڵ��Ƶ�ȣ��������֪���ԣ�ֱ��ʹ��Ƶ��
			//�����δ֪���ԣ��Ӻ��Ĵʵ��м������麺�ֶ�Ӧ2��Ƶ��
			if (pCur.p.nPOS >= 0) {
				// It's not an unknown words
				dCurFreqency = pCur.p.value;
			} else {
				// Unknown words
				dCurFreqency = DictCore.GetFrequency(pCur.p.sWord, 2);
			}

			PPARRAY_CHAIN pp = new PPARRAY_CHAIN(null);
			
			// Get next words which begin with pCur.p.col
			// ��������м����к�Ϊ��ǰ���кŵ���һ���ڵ�.��Ϊ��һ���ڵ�
			// �ӵ�ǰ�㿪ʼ����
			aWordIn.GetElement(pCur.p.col, -1, pCur, pp);
			pNextWords = pp.p;

			//����ɹ����ҵ�����һ���ڵ�,���ҿ��ܲ�ֹһ��
			while (pNextWords != null && pNextWords.p != null
					&& pNextWords.p.row == pCur.p.col)// Next words
			{
				// Current words frequency
				// ����ǰ�ʺ���һ��ƴװ���������дʴʿ��м�����Ƶ��
				strcpy(sTwoWords, pCur.p.sWord);
				strcat(sTwoWords, toGB2312(WORD_SEGMENTER));
				strcat(sTwoWords, pNextWords.p.sWord);
//				System.out.println(CStringFunction.getCString(sTwoWords));
				nTwoWordsFreq = DictBinary.GetFrequency(sTwoWords, 3);
				// Two linked Words frequency
				dTemp = (double) 1 / MAX_FREQUENCE;
				// Smoothing
				//����ƽ��ϵ��
				dValue = -Math
						.log(dSmoothingPara
								* (1 + dCurFreqency)
								/ (MAX_FREQUENCE + 80000)
								+ (1 - dSmoothingPara)
								* ((1 - dTemp) * nTwoWordsFreq
										/ (1 + dCurFreqency) + dTemp));
				// -log{a*P(Ci-1)+(1-a)P(Ci|Ci-1)} Note 0<a<1
				if (pCur.p.nPOS < 0){
					// Unknown words: P(Wi|Ci);while known words:1
					dValue += pCur.p.value;
				}
				// Get the position index of current word in the position map
				// table
				// ���õ�ǰ�ڵ���У��У������������������еĵ�һ���±������
				//nCurWordIndex����ǰ���±�ֵ
				//nNextWordIndex������һ���±�ֵ
				nCurWordIndex = BinarySearch(pCur.p.row * MAX_SENTENCE_LEN
						+ pCur.p.col, caller.m_npWordPosMapTable, m_nWordCount);
				nNextWordIndex = BinarySearch(pNextWords.p.row
						* MAX_SENTENCE_LEN + pNextWords.p.col,
						caller.m_npWordPosMapTable, m_nWordCount);
				
				//д�������̬���飬���ݷֱ�Ϊ:(��ǰ�ڵ㣬��һ�ڵ㣬Ƶ�ȣ�����)
				aBinaryWordNet.SetElement(nCurWordIndex, nNextWordIndex,
						dValue, pCur.p.nPOS);
				pNextWords = pNextWords.p.next;// Get next word
				
			}//whileѭ���������ж��Ƿ���Ȼ������һ���ڵ�pNextWords�Ƿ���Ч
			pCur = pCur.p.next;
		}//whileѭ���������ж����������Ƿ��ȡ���

		return m_nWordCount;
	}

}
