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

	// ��һ����̬���飬����ʾ�ִʵ�·�����
	public CDynamicArray m_segGraph_CDynamicArray = new CDynamicArray();

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////
	/**
	 * ���캯��������������
	 */
	public CSegGraph() {
		// segGraph: The segmentation word graph Row first array
		// ����m_segGraphΪ�����ȣ�Ĭ�ϵ��������������
		m_segGraph_CDynamicArray.SetRowFirst();
	}

	/**
	 * ���ú��Ĵʿ�������״ͼ,����ڶ�̬������
	 * 
	 * @param sSentence
	 * @param dictCore
	 * @return
	 */
	public boolean GenerateWordNet(char[] sSentence, CDictionary dictCore) {
		return GenerateWordNet(sSentence, dictCore, false);

	}

	/**
	 * �����д���״ͼ,���룺Ҫ�зֵ��ַ��������Ĵǵ� ���m_segGraph,����һ����̬����Ķ���
	 * 
	 * @param sSentence
	 * @param dictCore
	 *            ���Ĵʿ�
	 * @param bOriginalFreq
	 *            �Ƿ����ԭʼ�ִʵ�Ƶ�� true,��ʼ��Ϊ���logֵ,false,����¼����ʼ��Ϊ0
	 * @return
	 */
	public boolean GenerateWordNet(char[] sSentence, CDictionary dictCore,
			boolean bOriginalFreq) {
		// Gernerate the word net from the sLine, that's list all the possible
		// word
		// Step1:��������ͱ�����ʼ��
		int i = 0, j, nLen = strlen(sSentence);
		char sWord[] = new char[WORD_MAXLENGTH], sTempWord[] = new char[WORD_MAXLENGTH], sWordMatch[] = new char[WORD_MAXLENGTH];
		// int nWordIndex = 0,
		int nHandleTemp = 0;
		int k = 0, nPOS = 0;
		int nMatchFreq[] = new int[20], nMatchHandle[] = new int[20], nTotalFreq, nMatchCount;
		double dValue = 0;

		// ������ʼ��
		m_nAtomCount = 0;
		m_segGraph_CDynamicArray.SetEmpty();// Set segmentation graph empty

		sWord[0] = 0;
		sTempWord[0] = 0;
		sWordMatch[0] = 0;

		// Step2:����ԭ�ӷִʣ������е�Ԫ�ؽ��зֽ�
		// Atomic Segmentation
		// ��sSentence����ԭ���д�
		AtomSegment(sSentence);

		// Step3:��ʼ��m_nAtom[][]�з���ϵĽڵ���м��㣬�õ�����ͼ
		// ����m_segGraph�ϸ���·���ڵ��ֵ
		// �����ǰ��Ԫ�ڵ��Ǻ��֣�����ʹ��ԭ��·������������·��Ϊ���ֵ��
		// ������Ǻ��֣�����Ϊ����
		// ��һ���ֲ������������Ƕ�̬����m_segGraph_CDynamicArray
		// i����ָ��m_nAtom[][]����ָ��
		for (i = 0; i < m_nAtomCount; i++)// Init the cost array
		{
			if (m_nAtomPOS[i] == CT_CHINESE) {
				// The atom is a Chinese Char
				// ������ԭ����һ�����֣�ͨ�����Խ����ж�, �򽫽ڵ���뵽���Ԫ����

				if (!bOriginalFreq) {
					// Not original frequency
					// init the link with the maximum value
					// �������Ϊ����ҪԭʼƵ�ʣ����������Ƶ��
					m_segGraph_CDynamicArray.SetElement(i, i + 1,
							Math.log(MAX_FREQUENCE), 0);
				} else {
					// init the link with the maximum value
					// �������Ϊ��ҪԭʼƵ�ʣ�����Ϊ0��ͬʱд��ִ�����
					m_segGraph_CDynamicArray.SetElement(i, i + 1, 0, 0,
							m_sAtom[i]);
				}
			} else// Other atom, ������Բ��Ǻ��֣�������ԭ������
			{
				strcpy(sWord, m_sAtom[i]);// init the word
				dValue = MAX_FREQUENCE;

				// ����ض����Եĸ��Ǵ���
				switch (m_nAtomPOS[i]) { // ���ݴ��Խ��й���
				case CT_INDEX:
				case CT_NUM:
					nPOS = -27904;// 'm'*256,����
					strcpy(sWord, CUtility.toGB2312("δ##��"));
					dValue = 0;
					break;
				case CT_DELIMITER:
					nPOS = 30464;// 'w'*256;
					break;
				case CT_LETTER:
					nPOS = -'n' * 256 - 'x';//
					dValue = 0;
					strcpy(sWord, toGB2312("δ##��"));
					break;
				case CT_SINGLE:// 12021-2129-3121
					if (GetCharCount("+-1234567890\0".toCharArray(), m_sAtom[i]) == (int) strlen(m_sAtom[i])) {
						nPOS = -27904;// 'm'*256
						strcpy(sWord, toGB2312("δ##��"));
					} else {
						nPOS = -'n' * 256 - 'x';//
						strcpy(sWord, toGB2312("δ##��"));
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
			}// if�жϽ���
		}// forѭ������

		logger.debug("��ʼ��m_segGraph������m_segGraph_CDynamicArray:\n");
		logger.debug(m_segGraph_CDynamicArray.toString()+"\n");

		i = 0;
		while (i < m_nAtomCount)// All the word
		{
			// i����m_sAtom[][]�Ŀ�ʼ��ָ��
			// j����m_sAtom[]�Ľ�����ָ��
			strcpy(sWord, m_sAtom[i]);// Get the current atom
			j = i + 1;

			// �˴�������һ���·ݵ��жϣ����˸о�����֣��ȱ������Ժ�����������δ���
			if (strcmp(sWord, toGB2312("��")) == 0
					&& strcmp(m_sAtom[i + 1], toGB2312("��")) == 0) {
				// Don't split �·�
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
					// ����ҵ��˵�ǰ�ʣ�����ʵ��е���Ƶ�ȣ��洢��nTotalFreq����
					nTotalFreq = 0;
					Pint p1 = new Pint();
					// �õ�����
					dictCore.GetHandle(sWord, p1, nMatchHandle, nMatchFreq);
					nMatchCount = p1.value;
					for (k = 0; k < nMatchCount; k++)// Add the frequency
					{
						nTotalFreq += nMatchFreq[k];
					}
					// Adding a rule to exclude some words to be formed.
					// ����һЩ����������һЩ�ʵ���ʽ����֤�����Ĵʲ����ж�
					// ������������������µ�
					if (strlen(sWord) == 4
							&& i >= 1
							&& (IsAllNum(m_sAtom[i - 1]) || IsAllChineseNum(m_sAtom[i - 1]))
							&& (strncmp(sWord, toGB2312("��"), 2) == 0 || strncmp(
									sWord, toGB2312("��"), 2) == 0)) {// 1���ڡ�1999��ĩ
						if (CC_Find(toGB2312("ĩ���е�ǰ���"), sWord, 2) != null)
							break;
					}

					// The possible word has only one POS,store it
					// �����ǰ������ֻ��һ�����ԣ��������ظ�������д洢
					if (nMatchCount == 1) {
						if (!bOriginalFreq) {// Not original frequency
							//��Ҫԭʼ��Ƶ�ȣ��洢���ֵ��
							m_segGraph_CDynamicArray.SetElement(i, j,
									-log(nTotalFreq + 1) + log(MAX_FREQUENCE),
									nMatchHandle[0]);
						} else {
							//�洢ԭʼ��Ƶ�Ⱥϼƣ����ԣ��ִʽ��
							m_segGraph_CDynamicArray.SetElement(i, j,
									nTotalFreq, nMatchHandle[0], sWord);
						}
					} else {//���ж�����ܵĴ���,�洢ʱ����Ϊ0
						if (!bOriginalFreq){// Not original frequency
							m_segGraph_CDynamicArray.SetElement(i, j,
									-log(nTotalFreq + 1) + log(MAX_FREQUENCE),
									0);
						}else{
							m_segGraph_CDynamicArray.SetElement(i, j,
									nTotalFreq, 0, sWord);}
					}
				}
				//j��ָ���ƶ�����һ��,�����ݸ��ӵ�sWord����
				strcat(sWord, m_sAtom[j++]);
				logger.debug(CStringFunction.getCString(sWord));
			}// GetMaxMatchѭ����������ʱ�Ѿ��Ҳ���ƥ���������
			//i��ָ������һ��
			i += 1;// Start from i++;
		}// while ѭ������

		logger.debug("m_segGraph��״ͼ���������m_segGraph_CDynamicArray:\n");
		logger.debug(m_segGraph_CDynamicArray.toString()+"\n");		
		return true;
	}

	private double log(int i) {
		return Math.log(i + 0.0);
	}

	/**
	 * ԭ���зֹ���,�������char[]����ԭ���з�
	 * 
	 * @param sSentence
	 *            ���룺Ҫԭ���зֵ��ַ���,�����m_sAtom[][],m_nAtomLength[],m_nAtomCount
	 * @return true �ɹ� false ʧ��
	 * 
	 */
	public boolean AtomSegment(char[] sSentence) {

		// step1:���屾�ر��������ҳ�ʼ��������ʼ��������
		// i is the pointer of sentence string
		// j is the pointer of pAtoms
		// i�������ַ�����ָ�룬j������ַ�����ָ��
		int i = 0, j = 0, nCurType, nNextType;

		char sChar[] = new char[3];// sChar����ȡһ������,ռ2��byte
		sChar[2] = 0;// Set the char ending

		// ��ʼ��������.j��ָ�룬�����ô˱���
		m_nAtomCount = 0;
		for (int k = 0; k < MAX_SENTENCE_LEN; k++) {
			m_sAtom[k][0] = 0; // ����
			m_nAtomLength[k] = 0; // ����
			m_nAtomPOS[k] = 0; // ����
		}

		// step2.�������������ԡ�ʼ##ʼ����ʼ����ô����ƶ�ָ��,
		// ��һ���������������ͷ����
		if (strncmp(sSentence, toGB2312(SENTENCE_BEGIN),
				strlen(toGB2312(SENTENCE_BEGIN))) == 0) {
			// Set the first word as sentence begining
			// ���õ�һ��Ԫ��Ϊ��俪ʼ
			strcpy(m_sAtom[j], toGB2312(SENTENCE_BEGIN)); // ����
			m_nAtomLength[j] = strlen(toGB2312(SENTENCE_BEGIN)); // ����
			m_nAtomPOS[j] = CT_SENTENCE_BEGIN;// ���ô���

			// ָ����ƣ�i,j������ƶ�
			i += m_nAtomLength[j];
			j += 1;

		}

		// step3: ��ͷ���������Ժ�ѭ������ʼ
		while (i < strlen(sSentence)) {
			// ����Ѿ����˽�����
			if (strncmp(sSentence, i, toGB2312(SENTENCE_END),
					strlen(toGB2312(SENTENCE_END))) == 0) {
				// Set the first word as null
				strcpy(m_sAtom[j], toGB2312(SENTENCE_END)); // ����
				m_nAtomLength[j] = strlen(toGB2312(SENTENCE_END)); // ����
				m_nAtomPOS[j] = CT_SENTENCE_END;// ����

				// �ƶ�ָ��
				i += m_nAtomLength[j];
				j += 1;

				continue;
			}

			// Get the char with first byte,ȡһ��byte
			sChar[0] = sSentence[i];
			sChar[1] = 0;//
			i += 1;
			// ��Java���棬һ��char��16λ����GB2312�õ���byte����λ��Զ��0��
			// �����޷��ж��Ƿ��Ǻ��֣�������Ҫת����byte�ٽ��бȽϽ������׼ȷ��
			if ((byte) sChar[0] < 0)// Two byte char,��˫�ֽڵ�����
			{
				sChar[1] = sSentence[i];// Get the char with second byte
				sChar[2] = 0;
				i += 1;// i increased by 1
			}
			strcat(m_sAtom[j], sChar); // д��m_sAtom[j]
			nCurType = charType(sChar);// �ж�������,nCurType�������

			// �����Ǵ����жϵ������������
			if (sChar[0] == '.'
					&& (charType(sSentence, i) == CT_NUM || (sSentence[i] >= '0' && sSentence[i] <= '9'))) {
				// Digit after . indicate . as a point in the numeric
				// ��.��ͷ����һ�������ֵģ��������ִ���
				nCurType = CT_NUM;
			}
			m_nAtomPOS[j] = nCurType; // ���ô���
			// Record its property, just convience for continuous processing

			if (nCurType == CT_CHINESE || nCurType == CT_INDEX
					|| nCurType == CT_DELIMITER || nCurType == CT_OTHER) {
				// Chinese char, index number,delimiter and other is treated as
				// atom
				// ���֣��������ָ���ţ���������Ϊԭ�����ݽ��д���
				m_nAtomLength[j] = strlen(m_sAtom[j]);// Save its length
				j += 1;// Skip to next atom
			} else {// Number,single char, letter
				// ���ȼ���nNextType����ʾ��һ��byte����������
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
			}// ��ǰ�ַ��������жϵ�if����
		}// whileѭ������
		m_nAtomCount = j;// The count of segmentation atoms
		return true;
	}

}
