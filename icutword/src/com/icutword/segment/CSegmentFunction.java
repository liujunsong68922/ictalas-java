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
	 * 给定的字符串是代表年吗？
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
		if (IsAllSingleByte(sNum) && (nLen == 4 || nLen == 2 && sNum[0] > '4'))// 1992年,
			// 90年
			return true;
		if (IsAllNum(sNum)
				&& (nLen >= 6 || nLen == 4
						&& CC_Find(toGB2312("５６７８９"), sTemp) != null))
			return true;
		if (GetCharCount(toGB2312("零○一二三四五六七八九壹贰叁肆伍陆柒捌玖"), sNum) == (int) nLen / 2
				&& nLen >= 3)
			return true;
		if (nLen == 8 && GetCharCount(toGB2312("千仟零○"), sNum) == 2)// 二仟零二年
			return true;
		if (nLen == 2 && GetCharCount(toGB2312("千仟"), sNum) == 1)
			return true;

		sTemp = new char[nLen - 2 + 1];
		int i;
		for (i = 0; sNum[i + 2] != '\0'; i++)
			sTemp[i] = sNum[i + 2];
		sTemp[i] = '\0';

		if (nLen == 4 && GetCharCount(toGB2312("甲乙丙丁戊己庚辛壬癸"), sNum) == 1
				&& GetCharCount(toGB2312("子丑寅卯辰巳午未申酉戌亥"), sTemp) == 1)
			return true;
		return false;
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

		// m_npWordPosMapTable进行初始化
		// Word count is greater than 0
		if (m_nWordCount > 0) {
			// Record the position of possible words
			// 记录的数据将row,col合并成一个int来进行存储
			caller.m_npWordPosMapTable = new int[m_nWordCount];
		} else {
			return 0; // 数据错误，直接返回即可
		}

		// 循环读取，利用输入的动态数组，来初始化一个位置数组
		pCur = aWordIn.GetHead();
		while (pCur != null && pCur.p != null)// Set the position map of words
		{
			caller.m_npWordPosMapTable[nWordIndex++] = pCur.p.row * MAX_SENTENCE_LEN
					+ pCur.p.col;
			pCur = pCur.p.next;
		}

		// 再次对动态数组进行循环读取
		pCur = aWordIn.GetHead();
		while (pCur != null && pCur.p != null)//
		{
			//设置当前节点的频度，如果是已知词性，直接使用频度
			//如果是未知词性，从核心词典中检索词组汉字对应2的频度
			if (pCur.p.nPOS >= 0) {
				// It's not an unknown words
				dCurFreqency = pCur.p.value;
			} else {
				// Unknown words
				dCurFreqency = DictCore.GetFrequency(pCur.p.sWord, 2);
			}

			PPARRAY_CHAIN pp = new PPARRAY_CHAIN(null);
			
			// Get next words which begin with pCur.p.col
			// 从输入点中检索行号为当前点列号的下一个节点.作为下一个节点
			// 从当前点开始查找
			aWordIn.GetElement(pCur.p.col, -1, pCur, pp);
			pNextWords = pp.p;

			//如果成功的找到了下一个节点,而且可能不止一个
			while (pNextWords != null && pNextWords.p != null
					&& pNextWords.p.row == pCur.p.col)// Next words
			{
				// Current words frequency
				// 将当前词和下一词拼装起来，从切词词库中检索其频度
				strcpy(sTwoWords, pCur.p.sWord);
				strcat(sTwoWords, toGB2312(WORD_SEGMENTER));
				strcat(sTwoWords, pNextWords.p.sWord);
//				System.out.println(CStringFunction.getCString(sTwoWords));
				nTwoWordsFreq = DictBinary.GetFrequency(sTwoWords, 3);
				// Two linked Words frequency
				dTemp = (double) 1 / MAX_FREQUENCE;
				// Smoothing
				//计算平滑系数
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
				// 利用当前节点的行，列，反向计算出在索引表中的第一个下标出来。
				//nCurWordIndex代表当前点下标值
				//nNextWordIndex代表下一点下标值
				nCurWordIndex = BinarySearch(pCur.p.row * MAX_SENTENCE_LEN
						+ pCur.p.col, caller.m_npWordPosMapTable, m_nWordCount);
				nNextWordIndex = BinarySearch(pNextWords.p.row
						* MAX_SENTENCE_LEN + pNextWords.p.col,
						caller.m_npWordPosMapTable, m_nWordCount);
				
				//写入输出动态数组，数据分别为:(当前节点，下一节点，频度，词性)
				aBinaryWordNet.SetElement(nCurWordIndex, nNextWordIndex,
						dValue, pCur.p.nPOS);
				pNextWords = pNextWords.p.next;// Get next word
				
			}//while循环结束，判断是否仍然存在下一个节点pNextWords是否有效
			pCur = pCur.p.next;
		}//while循环结束，判断输入数组是否读取完毕

		return m_nWordCount;
	}

}
