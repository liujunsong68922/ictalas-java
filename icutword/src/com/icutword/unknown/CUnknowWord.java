package com.icutword.unknown;

import org.apache.log4j.Logger;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dictionary.PWORD_RESULT;
import com.icutword.model.dynamicarray.CDynamicArray;
import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;
import com.icutword.segment.seggraph.CSegGraph;
import com.icutword.unknown.span.CSpan;
import com.icutword.utility.CUtility;

/**
 * CUnknowWord��һ������CSpan�����벻ͬ�Ĵǵ��ļ����Ӷ�ʵ��δ֪�ʵ��д�
 * 
 * @author liujunsong
 * 
 */
public class CUnknowWord extends CUtility {
	//��־�ඨ��
	static Logger logger = Logger.getLogger("logger");
	
	// �����Ǿֲ���������
	// Unknown dictionary�ִʴǵ䶨��
	private CDictionary m_dict = new CDictionary();
	// Role tagging��ɫ���ֶ���
	CSpan m_roleTag = new CSpan();
	// The POS of such a category
	int m_nPOS;
	char m_sUnknownFlags[] = new char[10];

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////
	/**
	 * ���캯��
	 */
	public CUnknowWord() {
		m_sUnknownFlags[0] = 0;
	}

	// Unknown word recognition
	// pWordSegResult:word Segmentation result;graphOptimum: The optimized
	// segmentation graph
	// graphSeg: The original segmentation graph
	/**
	 * δ֪�ʵ�ʶ����
	 * 
	 * @param pWordSegResult
	 *            �ִ��Ժ�ʶ������ĴʵĴ洢������
	 * @param graphOptimum
	 *            Ŀǰ�Ѿ��ִʵĽ�����������룬ͬʱҲ��Ψһ�����
	 * @param graphSeg
	 *            �ִ��࣬���������Ѿ��ִʵĽ��
	 * @param dictCore
	 *            ���Ĵǵ䣬ֻ��
	 * @return
	 */
	public boolean Recognition(PWORD_RESULT pWordSegResult[],
			CDynamicArray graphOptimum, CSegGraph graphSeg, CDictionary dictCore) {
		int nStartPos = 0, j = 0, nAtomStart, nAtomEnd, nPOSOriginal;
		double dValue;
		
		//�Ƚ��б�׼���Ա�ע
		logger.debug("���Ա�ע��ʼ");
		m_roleTag.POSTagging(pWordSegResult, dictCore, m_dict);
		logger.debug("���Ա�ע������δ֪��������"+m_roleTag.m_nUnknownIndex);
		// Tag the segmentation with unknown recognition roles according the
		// core dictionary and unknown recognition dictionary
		
		for (int i = 0; i < m_roleTag.m_nUnknownIndex; i++) {
			while (j < graphSeg.m_nAtomCount
					&& nStartPos < m_roleTag.m_nUnknownWords[i][0]) {
				nStartPos += graphSeg.m_nAtomLength[j++];
			}
			nAtomStart = j;
			while (j < graphSeg.m_nAtomCount
					&& nStartPos < m_roleTag.m_nUnknownWords[i][1]) {
				nStartPos += graphSeg.m_nAtomLength[j++];
			}
			nAtomEnd = j;
			if (nAtomStart < nAtomEnd) {
				Pdouble pd = new Pdouble();
				Pint pi = new Pint();
				graphOptimum.GetElement2(nAtomStart, nAtomEnd, pd, pi);
				dValue = pd.value;
				nPOSOriginal = pi.value;
				if (dValue > m_roleTag.m_dWordsPossibility[i])// Set the element
																// with less
																// frequency
					graphOptimum.SetElement(nAtomStart, nAtomEnd,
							m_roleTag.m_dWordsPossibility[i], m_nPOS,
							m_sUnknownFlags);
			}
		}
		return true;
	}

	// Load unknown recognition dictionary
	// Load context
	// type: Unknown words type (including person,place,transliterion and so on)
	/**
	 * �ִ��������ù���,������������ļ����뵽CDictionary����ȥ �ļ��Ĺ�����ϵ��ȡ��CSpan����ȥ
	 * 
	 * @param sConfigFile
	 *            �����ļ����ַ�����ʾ
	 * @param type
	 *            �ִ�������
	 * @return
	 */
	public boolean Configure(char sConfigFile[], TAG_TYPE type) {
		char sFilename[] = new char[100];

		// Load the unknown recognition dictionary
		//��ȡδ֪��ʶ��ʵ�
		strcpy(sFilename, sConfigFile);
		strcat(sFilename, toGB2312(".dct"));
		m_dict.Load(sFilename);

		// Load the unknown recognition context
		//��ȡδ֪�εĶ�ȡ������
		strcpy(sFilename, sConfigFile);
		strcat(sFilename, ".ctx\0".toCharArray());
		m_roleTag.LoadContext(sFilename);

		// Set the tagging type
		//���÷ִ�����
		m_roleTag.SetTagType(type);
		switch (type) {
		case TT_PERSON:
		case TT_TRANS_PERSON:// Set the special flag for transliterations
			m_nPOS = -28274;// -'n'*256-'r';
			strcpy(m_sUnknownFlags, toGB2312("δ##��"));
			break;
		case TT_PLACE:
			m_nPOS = -28275;// -'n'*256-'s';
			strcpy(m_sUnknownFlags, toGB2312("δ##��"));
			break;
		default:
			m_nPOS = 0;
			break;
		}
		return true;
	}

	// Judge whether the name is a given name
	private boolean IsGivenName(char sName[]) {
		char sFirstChar[] = new char[3], sSecondChar[] = new char[3];
		double dGivenNamePossibility = 0, dSingleNamePossibility = 0;
		if (strlen(sName) != 4)
			return false;

		strncpy(sFirstChar, sName, 2);
		sFirstChar[2] = 0;
		strncpy(sSecondChar, sName, 2, 2);
		sSecondChar[2] = 0;

		// The possibility of P(Wi|Ti)
		dGivenNamePossibility += log((double) m_dict
				.GetFrequency(sFirstChar, 2) + 1.0)
				- log(m_roleTag.m_context.GetFrequency(0, 2) + 1.0);
		dGivenNamePossibility += log((double) m_dict.GetFrequency(sSecondChar,
				3) + 1.0) - log(m_roleTag.m_context.GetFrequency(0, 3) + 1.0);
		// The possibility of conversion from 2 to 3
		dGivenNamePossibility += log(m_roleTag.m_context.GetContextPossibility(
				0, 2, 3) + 1.0)
				- log(m_roleTag.m_context.GetFrequency(0, 2) + 1.0);

		// The possibility of P(Wi|Ti)
		dSingleNamePossibility += log((double) m_dict.GetFrequency(sFirstChar,
				1) + 1.0) - log(m_roleTag.m_context.GetFrequency(0, 1) + 1.0);
		dSingleNamePossibility += log((double) m_dict.GetFrequency(sSecondChar,
				4) + 1.0) - log(m_roleTag.m_context.GetFrequency(0, 4) + 1.0);
		// The possibility of conversion from 1 to 4
		dSingleNamePossibility += log(m_roleTag.m_context
				.GetContextPossibility(0, 1, 4) + 1.0)
				- log(m_roleTag.m_context.GetFrequency(0, 1) + 1.0);

		if (dSingleNamePossibility >= dGivenNamePossibility)// ����||m_dict.GetFrequency(sFirstChar,1)/m_dict.GetFrequency(sFirstChar,2)>=10
			// The possibility being a single given name is more than being a
			// 2-char
			// given name
			return false;
		return true;
	}
}
