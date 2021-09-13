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
 * CUnknowWord是一个利用CSpan，传入不同的辞典文件，从而实现未知词的切词
 * 
 * @author liujunsong
 * 
 */
public class CUnknowWord extends CUtility {
	//日志类定义
	static Logger logger = Logger.getLogger("logger");
	
	// 下面是局部变量定义
	// Unknown dictionary分词辞典定义
	private CDictionary m_dict = new CDictionary();
	// Role tagging角色划分定义
	CSpan m_roleTag = new CSpan();
	// The POS of such a category
	int m_nPOS;
	char m_sUnknownFlags[] = new char[10];

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////
	/**
	 * 构造函数
	 */
	public CUnknowWord() {
		m_sUnknownFlags[0] = 0;
	}

	// Unknown word recognition
	// pWordSegResult:word Segmentation result;graphOptimum: The optimized
	// segmentation graph
	// graphSeg: The original segmentation graph
	/**
	 * 未知词的识别功能
	 * 
	 * @param pWordSegResult
	 *            分词以后识别出来的词的存储索引？
	 * @param graphOptimum
	 *            目前已经分词的结果，既是输入，同时也是唯一的输出
	 * @param graphSeg
	 *            分词类，用来传入已经分词的结果
	 * @param dictCore
	 *            核心辞典，只读
	 * @return
	 */
	public boolean Recognition(PWORD_RESULT pWordSegResult[],
			CDynamicArray graphOptimum, CSegGraph graphSeg, CDictionary dictCore) {
		int nStartPos = 0, j = 0, nAtomStart, nAtomEnd, nPOSOriginal;
		double dValue;
		
		//先进行标准词性标注
		logger.debug("词性标注开始");
		m_roleTag.POSTagging(pWordSegResult, dictCore, m_dict);
		logger.debug("词性标注结束，未知词数量："+m_roleTag.m_nUnknownIndex);
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
	 * 分词器的配置功能,将输入的配置文件读入到CDictionary里面去 文件的关联关系读取到CSpan里面去
	 * 
	 * @param sConfigFile
	 *            配置文件的字符串表示
	 * @param type
	 *            分词器类型
	 * @return
	 */
	public boolean Configure(char sConfigFile[], TAG_TYPE type) {
		char sFilename[] = new char[100];

		// Load the unknown recognition dictionary
		//读取未知的识别词典
		strcpy(sFilename, sConfigFile);
		strcat(sFilename, toGB2312(".dct"));
		m_dict.Load(sFilename);

		// Load the unknown recognition context
		//读取未知次的读取上下文
		strcpy(sFilename, sConfigFile);
		strcat(sFilename, ".ctx\0".toCharArray());
		m_roleTag.LoadContext(sFilename);

		// Set the tagging type
		//设置分词类型
		m_roleTag.SetTagType(type);
		switch (type) {
		case TT_PERSON:
		case TT_TRANS_PERSON:// Set the special flag for transliterations
			m_nPOS = -28274;// -'n'*256-'r';
			strcpy(m_sUnknownFlags, toGB2312("未##人"));
			break;
		case TT_PLACE:
			m_nPOS = -28275;// -'n'*256-'s';
			strcpy(m_sUnknownFlags, toGB2312("未##地"));
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

		if (dSingleNamePossibility >= dGivenNamePossibility)// 张震||m_dict.GetFrequency(sFirstChar,1)/m_dict.GetFrequency(sFirstChar,2)>=10
			// The possibility being a single given name is more than being a
			// 2-char
			// given name
			return false;
		return true;
	}
}
