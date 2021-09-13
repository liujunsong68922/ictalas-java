package com.icutword.segment.seggraph;

import org.apache.log4j.Logger;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dynamicarray.CDynamicArray;
import com.icutword.model.point.Pint;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

public class CSegGraph extends CUtility {
	static Logger logger = Logger.getLogger("logger");	
	
	int MAX_FREQUENCE = 2079997;
	int MAX_SENTENCE_LEN = 2000;
	int WORD_MAXLENGTH = 100;
	// pAtoms: the buffer for returned segmented atoms
	// Such as a Chinese Char, digit, single byte, or delimiters
	public char m_sAtom[][] = new char[MAX_SENTENCE_LEN][WORD_MAXLENGTH];
	// Save the individual length of atom in the array
	public int m_nAtomLength[] = new int[MAX_SENTENCE_LEN];
	int m_nAtomPOS[] = new int[MAX_SENTENCE_LEN];// pAtoms: the POS property
	public int m_nAtomCount;// The count of atoms

	// 用一个动态数组，来表示分词的路径结果
	public CDynamicArray m_segGraph_CDynamicArray = new CDynamicArray();

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 构造函数，设置行优先
	 */
	public CSegGraph() {
		// segGraph: The segmentation word graph Row first array
		// 设置m_segGraph为行优先，默认的情况下是列优先
		m_segGraph_CDynamicArray.SetRowFirst();
	}

	/**
	 * 利用核心词库生成网状图,输出在动态数组中
	 * 
	 * @param sSentence
	 * @param dictCore
	 * @return
	 */
	public boolean GenerateWordNet(char[] sSentence, CDictionary dictCore) {
		return GenerateWordNet(sSentence, dictCore, false);

	}

	/**
	 * 生成切词网状图,输入：要切分的字符串，核心辞典 输出m_segGraph,这是一个动态数组的定义
	 * 
	 * @param sSentence
	 * @param dictCore
	 *            核心词库
	 * @param bOriginalFreq
	 *            是否采用原始分词的频率 true,初始化为最大log值,false,不记录，初始化为0
	 * @return
	 */
	public boolean GenerateWordNet(char[] sSentence, CDictionary dictCore,
			boolean bOriginalFreq) {
		// Gernerate the word net from the sLine, that's list all the possible
		// word
		// Step1:变量定义和变量初始化
		int i = 0, j, nLen = strlen(sSentence);
		char sWord[] = new char[WORD_MAXLENGTH], sTempWord[] = new char[WORD_MAXLENGTH], sWordMatch[] = new char[WORD_MAXLENGTH];
		// int nWordIndex = 0,
		int nHandleTemp = 0;
		int k = 0, nPOS = 0;
		int nMatchFreq[] = new int[20], nMatchHandle[] = new int[20], nTotalFreq, nMatchCount;
		double dValue = 0;

		// 变量初始化
		m_nAtomCount = 0;
		m_segGraph_CDynamicArray.SetEmpty();// Set segmentation graph empty

		sWord[0] = 0;
		sTempWord[0] = 0;
		sWordMatch[0] = 0;

		// Step2:进行原子分词，将所有的元素进行分解
		// Atomic Segmentation
		// 对sSentence进行原子切词
		AtomSegment(sSentence);

		// Step3:开始对m_nAtom[][]切分完毕的节点进行计算，得到网络图
		// 设置m_segGraph上各个路径节点的值
		// 如果当前单元节点是汉字，而且使用原生路径，则设置其路径为最大值。
		// 如果不是汉字，设置为其他
		// 这一部分操作的输出结果是动态数组m_segGraph_CDynamicArray
		// i代表指向m_nAtom[][]的行指针
		for (i = 0; i < m_nAtomCount; i++)// Init the cost array
		{
			if (m_nAtomPOS[i] == CT_CHINESE) {
				// The atom is a Chinese Char
				// 如果这个原子是一个汉字，通过词性进行判断, 则将节点加入到输出元素中

				if (!bOriginalFreq) {
					// Not original frequency
					// init the link with the maximum value
					// 如果设置为不需要原始频率，则设置最大频率
					m_segGraph_CDynamicArray.SetElement(i, i + 1,
							Math.log(MAX_FREQUENCE), 0);
				} else {
					// init the link with the maximum value
					// 如果设置为需要原始频率，设置为0，同时写入分词内容
					m_segGraph_CDynamicArray.SetElement(i, i + 1, 0, 0,
							m_sAtom[i]);
				}
			} else// Other atom, 如果词性不是汉字，是其他原子类型
			{
				strcpy(sWord, m_sAtom[i]);// init the word
				dValue = MAX_FREQUENCE;

				// 针对特定词性的覆盖处理
				switch (m_nAtomPOS[i]) { // 根据词性进行过滤
				case CT_INDEX:
				case CT_NUM:
					nPOS = -27904;// 'm'*256,词性
					strcpy(sWord, CUtility.toGB2312("未##数"));
					dValue = 0;
					break;
				case CT_DELIMITER:
					nPOS = 30464;// 'w'*256;
					break;
				case CT_LETTER:
					nPOS = -'n' * 256 - 'x';//
					dValue = 0;
					strcpy(sWord, toGB2312("未##串"));
					break;
				case CT_SINGLE:// 12021-2129-3121
					if (GetCharCount("+-1234567890\0".toCharArray(), m_sAtom[i]) == (int) strlen(m_sAtom[i])) {
						nPOS = -27904;// 'm'*256
						strcpy(sWord, toGB2312("未##数"));
					} else {
						nPOS = -'n' * 256 - 'x';//
						strcpy(sWord, toGB2312("未##串"));
					}
					dValue = 0;
					break;
				default:
					nPOS = m_nAtomPOS[i];// '?'*256;
					break;
				}
				if (!bOriginalFreq) {// Not original frequency
					// init the link with minimum
					m_segGraph_CDynamicArray.SetElement(i, i + 1, 0, nPOS);
				} else {
					// init the link with minimum
					m_segGraph_CDynamicArray.SetElement(i, i + 1, dValue, nPOS,
							sWord);
				}
			}// if判断结束
		}// for循环结束

		logger.debug("初始化m_segGraph结束。m_segGraph_CDynamicArray:\n");
		logger.debug(m_segGraph_CDynamicArray.toString()+"\n");

		i = 0;
		while (i < m_nAtomCount)// All the word
		{
			// i代表m_sAtom[][]的开始点指针
			// j代表m_sAtom[]的结束点指针
			strcpy(sWord, m_sAtom[i]);// Get the current atom
			j = i + 1;

			// 此处有这样一个月份的判断，令人感觉很奇怪，先保留，以后理解了再深层次处理
			if (strcmp(sWord, toGB2312("月")) == 0
					&& strcmp(m_sAtom[i + 1], toGB2312("份")) == 0) {
				// Don't split 月份
				j += 1;
			}
			Pint pnHandleTemp = new Pint();
			while (j <= m_nAtomCount
					&& dictCore.GetMaxMatch(sWord, sWordMatch, pnHandleTemp)) {
				// Add a condition to control the end of string retrieve the
				// dictionary with the word
				nHandleTemp = pnHandleTemp.value;
				// System.out.println("sWordMatch:");
				// logger.debug(CStringFunction.getCString(sWordMatch));

				if (strcmp(sWordMatch, sWord) == 0) {
					// find the current word
					// 如果找到了当前词，计算词典中的总频度，存储在nTotalFreq里面
					nTotalFreq = 0;
					Pint p1 = new Pint();
					// 得到词性
					dictCore.GetHandle(sWord, p1, nMatchHandle, nMatchFreq);
					nMatchCount = p1.value;
					for (k = 0; k < nMatchCount; k++)// Add the frequency
					{
						nTotalFreq += nMatchFreq[k];
					}
					// Adding a rule to exclude some words to be formed.
					// 增加一些规则，来定义一些词的形式，保证连续的词不被切断
					// 这个规则用来处理年月等
					if (strlen(sWord) == 4
							&& i >= 1
							&& (IsAllNum(m_sAtom[i - 1]) || IsAllChineseNum(m_sAtom[i - 1]))
							&& (strncmp(sWord, toGB2312("年"), 2) == 0 || strncmp(
									sWord, toGB2312("月"), 2) == 0)) {// 1年内、1999年末
						if (CC_Find(toGB2312("末内中底前间初"), sWord, 2) != null)
							break;
					}

					// The possible word has only one POS,store it
					// 如果当前的数据只有一个词性，不可能重复，则进行存储
					if (nMatchCount == 1) {
						if (!bOriginalFreq) {// Not original frequency
							//不要原始的频度，存储最大值。
							m_segGraph_CDynamicArray.SetElement(i, j,
									-log(nTotalFreq + 1) + log(MAX_FREQUENCE),
									nMatchHandle[0]);
						} else {
							//存储原始的频度合计，词性，分词结果
							m_segGraph_CDynamicArray.SetElement(i, j,
									nTotalFreq, nMatchHandle[0], sWord);
						}
					} else {//具有多个可能的词性,存储时词性为0
						if (!bOriginalFreq){// Not original frequency
							m_segGraph_CDynamicArray.SetElement(i, j,
									-log(nTotalFreq + 1) + log(MAX_FREQUENCE),
									0);
						}else{
							m_segGraph_CDynamicArray.SetElement(i, j,
									nTotalFreq, 0, sWord);}
					}
				}
				//j的指针移动到下一个,将内容附加到sWord里面
				strcat(sWord, m_sAtom[j++]);
				logger.debug(CStringFunction.getCString(sWord));
			}// GetMaxMatch循环结束，这时已经找不到匹配的数据了
			//i的指针下移一步
			i += 1;// Start from i++;
		}// while 循环结束

		logger.debug("m_segGraph网状图计算结束。m_segGraph_CDynamicArray:\n");
		logger.debug(m_segGraph_CDynamicArray.toString()+"\n");		
		return true;
	}

	private double log(int i) {
		return Math.log(i + 0.0);
	}

	/**
	 * 原子切分规则,对输入的char[]进行原子切分
	 * 
	 * @param sSentence
	 *            输入：要原子切分的字符串,输出：m_sAtom[][],m_nAtomLength[],m_nAtomCount
	 * @return true 成功 false 失败
	 * 
	 */
	public boolean AtomSegment(char[] sSentence) {

		// step1:定义本地变量，并且初始化，并初始化输出结果
		// i is the pointer of sentence string
		// j is the pointer of pAtoms
		// i是输入字符串的指针，j是输出字符串的指针
		int i = 0, j = 0, nCurType, nNextType;

		char sChar[] = new char[3];// sChar代表取一个汉字,占2个byte
		sChar[2] = 0;// Set the char ending

		// 初始化输出结果.j是指针，不重用此变量
		m_nAtomCount = 0;
		for (int k = 0; k < MAX_SENTENCE_LEN; k++) {
			m_sAtom[k][0] = 0; // 内容
			m_nAtomLength[k] = 0; // 长度
			m_nAtomPOS[k] = 0; // 词性
		}

		// step2.如果输入的内容以【始##始】开始，那么向后移动指针,
		// 这一步用来处理输入的头部。
		if (strncmp(sSentence, toGB2312(SENTENCE_BEGIN),
				strlen(toGB2312(SENTENCE_BEGIN))) == 0) {
			// Set the first word as sentence begining
			// 设置第一个元素为语句开始
			strcpy(m_sAtom[j], toGB2312(SENTENCE_BEGIN)); // 内容
			m_nAtomLength[j] = strlen(toGB2312(SENTENCE_BEGIN)); // 长度
			m_nAtomPOS[j] = CT_SENTENCE_BEGIN;// 设置词性

			// 指针后移，i,j都向后移动
			i += m_nAtomLength[j];
			j += 1;

		}

		// step3: 开头部分跳过以后，循环处理开始
		while (i < strlen(sSentence)) {
			// 如果已经到了结束点
			if (strncmp(sSentence, i, toGB2312(SENTENCE_END),
					strlen(toGB2312(SENTENCE_END))) == 0) {
				// Set the first word as null
				strcpy(m_sAtom[j], toGB2312(SENTENCE_END)); // 内容
				m_nAtomLength[j] = strlen(toGB2312(SENTENCE_END)); // 长度
				m_nAtomPOS[j] = CT_SENTENCE_END;// 词性

				// 移动指针
				i += m_nAtomLength[j];
				j += 1;

				continue;
			}

			// Get the char with first byte,取一个byte
			sChar[0] = sSentence[i];
			sChar[1] = 0;//
			i += 1;
			// 在Java里面，一个char是16位，从GB2312得到的byte，高位永远是0，
			// 所以无法判断是否是汉字，所以需要转换成byte再进行比较结果才是准确的
			if ((byte) sChar[0] < 0)// Two byte char,是双字节的数据
			{
				sChar[1] = sSentence[i];// Get the char with second byte
				sChar[2] = 0;
				i += 1;// i increased by 1
			}
			strcat(m_sAtom[j], sChar); // 写入m_sAtom[j]
			nCurType = charType(sChar);// 判断其类型,nCurType代表词性

			// 下面是词性判断的例外情况处理
			if (sChar[0] == '.'
					&& (charType(sSentence, i) == CT_NUM || (sSentence[i] >= '0' && sSentence[i] <= '9'))) {
				// Digit after . indicate . as a point in the numeric
				// 以.开头，下一个是数字的，按照数字处理
				nCurType = CT_NUM;
			}
			m_nAtomPOS[j] = nCurType; // 设置词性
			// Record its property, just convience for continuous processing

			if (nCurType == CT_CHINESE || nCurType == CT_INDEX
					|| nCurType == CT_DELIMITER || nCurType == CT_OTHER) {
				// Chinese char, index number,delimiter and other is treated as
				// atom
				// 汉字，索引，分割符号，其他都作为原子数据进行处理
				m_nAtomLength[j] = strlen(m_sAtom[j]);// Save its length
				j += 1;// Skip to next atom
			} else {// Number,single char, letter
				// 首先计算nNextType，表示下一个byte的数据类型
				nNextType = 255;
				if (i < strlen(sSentence)) {
					nNextType = charType(sSentence, i);
				}

				if (nNextType != nCurType || i == strlen(sSentence)) {
					// Reaching end or next char type is
					// different from current char
					m_nAtomLength[j] = strlen(m_sAtom[j]);// Save its length
					j += 1;
				}
			}// 当前字符的类型判断的if结束
		}// while循环结束
		m_nAtomCount = j;// The count of segmentation atoms
		return true;
	}

}
