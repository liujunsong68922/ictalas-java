package com.icutword.model.contextstat;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

//import java.io.IOException;

//import org.ictclas4j.utility.Utility;

import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;
import com.icutword.utility.FileIOUtility;


public class CContextStat {
	// ////////////////////////////////////////////////////////////////////
	// ������ݴ洢����
	// ////////////////////////////////////////////////////////////////////
	public int m_nTableLen; // ����
	public int[] m_pSymbolTable; // ��ʾ���ű���ͬ���Ŵ��ڲ�ͬ��
	public PMYCONTEXT m_pContext; // ���������Ŀ�ͷ
//	private int m_nCategory; // �ƺ���Ч,ע�͵�

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction ���캯��/�⹹��������
	// ////////////////////////////////////////////////////////////////////

	/**
	 * ���캯��
	 */
	public CContextStat() {
		m_pSymbolTable = null;// new buffer for symbol
		m_pContext = null;// init with empty
	}



	/**
	 * �⹹����������ͨ��ѭ������ÿ���ڵ��Destroy������ȷ���ڴ��ͷ�
	 */
	public void Destroy() {
		// ����PMYCONTEXT����ڴ�������
		// ���ͷ������ڴ�
		if (m_pContext != null) {
			m_pContext.DestroyAll();
		}

		// ���������ڴ����
		m_pContext = null;
//		m_nCategory = 0;
		m_nTableLen = 0;
		m_pSymbolTable = null;
	}

	// ////////////////////////////////////////////////////////////////////
	// ���ط�������
	// ////////////////////////////////////////////////////////////////////
	
	/**
	 * ����m_pSymbolTable������,�������洢�ռ䲢���ñ��� ��������������ò����������ݣ���������������һ������������ͬ���塣
	 * 
	 * @param nSymbol
	 * @return
	 */
	private boolean SetSymbol(int[] nSymbol) {
		// memcpy(m_pSymbolTable,nSymbol,sizeof(int)*m_nTableLen);
		m_pSymbolTable = new int[nSymbol.length];
		for (int i = 0; i < nSymbol.length; i++) {
			m_pSymbolTable[i] = nSymbol[i];
		}
		return true;
	}

	/**
	 * ����һ����¼��������ƥ���¼����ȥ ����Ѿ����ڣ��޸������Ƶ��
	 * ��һ�����ƺ�û��ʹ��
	 * @param nKey
	 *            �ؼ��ʣ������ֵ�����
	 * @param nPrevSymbol
	 *            ǰһ���͵Ĵ��룬��Ҫת��������ֵ
	 * @param nCurSymbol
	 *            ��ǰ���͵Ĵ��룬��Ҫת��������ֵ
	 * @param nFrequency
	 *            ����Ƶ��
	 * @return
	 */
	public boolean Add(int nKey, int nPrevSymbol, int nCurSymbol,
			int nFrequency) {
		// Add the context symbol to the array
		// �������ķ��ű����ӵ�array����ȥ
		PMYCONTEXT pNew;
		PPMYCONTEXT ppRetItem = new PPMYCONTEXT(null);
		int nPrevIndex, nCurIndex;

		// Not get it ���û���ҵ���Ӧ�ؼ��ʵĻ�������һ���µĴ洢�ڵ����
		if (!GetItem(nKey, ppRetItem)) {
			pNew = new PMYCONTEXT(new MYCONTEXT()); // ����ָ�룬ͬʱ����洢�ڴ�

			// �洢�ռ��ʼ��
			pNew.p.nKey = nKey;
			pNew.p.nTotalFreq = 0;
			pNew.p.next = null;
			pNew.p.aContextArray = new int[m_nTableLen][m_nTableLen]; // Ƶ�ȱ�
			pNew.p.aTagFreq = new int[m_nTableLen]; // ��Ƶ��

			// Empty, the new item is head
			if (ppRetItem.p == null) {
				m_pContext = _copy(pNew);
			} else// Link the new item between pRetItem and its next item
			{
				pNew.p.next = _copy(ppRetItem.p.next());
				ppRetItem.p.p.next = _copy(pNew);
			}
			ppRetItem.p = _copy(pNew);
		}

		// �õ�ǰһ��Ԫ�ش��Ե�����
		nPrevIndex = CUtility.BinarySearch(nPrevSymbol, m_pSymbolTable,
				m_nTableLen);
		if (nPrevSymbol > 256 && nPrevIndex == -1)// Not find, just for 'nx' and
													// other uncommon POS
			nPrevIndex = CUtility.BinarySearch(nPrevSymbol - nPrevSymbol % 256,
					m_pSymbolTable, m_nTableLen);

		// �õ���ǰԪ�ش��Ե�����
		nCurIndex = CUtility.BinarySearch(nCurSymbol, m_pSymbolTable,
				m_nTableLen);
		if (nCurSymbol > 256 && nCurIndex == -1)// Not find, just for 'nx' and
												// other uncommon POS
			nCurIndex = CUtility.BinarySearch(nCurSymbol - nCurSymbol % 256,
					m_pSymbolTable, m_nTableLen);

		// error finding the symbol
		if (nPrevIndex == -1 || nCurIndex == -1) {
			return false;
		} else {
			// ����Ѿ��ҵ������Ӷ�Ӧ�ķ���Ƶ��
			ppRetItem.p.p.aContextArray[nPrevIndex][nCurIndex] += nFrequency;
			ppRetItem.p.p.aTagFreq[nPrevIndex] += nFrequency;
			ppRetItem.p.p.nTotalFreq += nFrequency;
			return true;
		}
	}

	/**
	 * ��ContextStat��������ݣ�д�뵽�ļ�����ȥ ��һ���ܿ��������������ݵĶ�д���ԵȲ�����
	 * ͬʱ������д�뵽�����ļ�����ȥ��һ���Ǹ�ʽ��ͬ���ļ�����һ��������־��¼�ļ�
	 * Ŀǰ������д����־��¼�ļ���ʱ���������ݶ�ʧ���ˣ�����һ���ǳ���ֵ����� ������־�ļ���Ϣ��ʧ�����⣬�Ժ��ټ�飬�����Ȳ�����ô����
	 * 
	 * @param sFilename
	 * @return
	 */
	public boolean Save(char[] sFilename) {
		// TODO:��־�ļ����ʱ���������Ϣ��ʧ�����⣬����ԭ�����
		// �������ԣ�д�뵽һ��ctx�ļ�ʱ��Ҳ�д���
		int i;
		File fp, fp1;
		PMYCONTEXT pCur;
		char sFileShow[] = new char[100];
		FileIOUtility fu = new FileIOUtility();

		try {
			CStringFunction.strcpy(sFileShow, sFilename);
			CStringFunction.strcat(sFileShow, ".shw\0".toCharArray());

			fp = new File(new String(sFilename)); // ����ļ�1
			fp1 = new File(new String(sFileShow)); // ����ļ�2

			DataOutputStream out = new DataOutputStream(
					new FileOutputStream(fp));
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(
					fp1));

			// д�����
			// write the table length
			fu.writeInt(out, m_nTableLen);

			// ��¼��־
			out1.writeUTF("Table Len=" + m_nTableLen);
			out1.writeUTF("Symbol:");

			// д����ű�
			// write the symbol table
			for (i = 0; i < m_nTableLen; i++) {
				fu.writeInt(out, m_pSymbolTable[i]);
			}

			// д����ű����־
			for (i = 0; i < m_nTableLen; i++) {
				out1.writeUTF(">" + m_pSymbolTable[i]);
			}
			out1.flush();
			out1.writeUTF("\n");

			// ָ������Ŀ�ʼ��
			pCur = m_pContext;
			while (_isNotNullPoint(pCur)) {
				// д��nkey��nTotalFreq
				fu.writeInt(out, pCur.p.nKey);
				fu.writeInt(out, pCur.p.nTotalFreq);

				// ��־
				out1.writeUTF("nKey=" + pCur.p.nKey + ",Total frequency="
						+ pCur.p.nTotalFreq + ":");

				// fwrite(pCur->aTagFreq,sizeof(int),m_nTableLen,fp);
				// д��ÿһ���ʵ�Ƶ��
				// the every POS frequency
				for (i = 0; i < m_nTableLen; i++) {
					fu.writeInt(out, pCur.p.aTagFreq[i]);
				}

				int j;
				for (i = 0; i < m_nTableLen; i++) {
					for (j = 0; j < m_nTableLen; j++) {
						// ��������
						out.writeInt(pCur.p.aContextArray[i][j]);

						// д��־
						out1.writeUTF("No." + i + "," + j + "=:"
								+ pCur.p.aContextArray[i][j] + "");
					}
					// д��־
					out1.writeUTF("total=  " + pCur.p.aTagFreq[i] + ":");
				}
				pCur = _copy(pCur.next());
			}
			out.flush();
			out.close();

			out1.flush();
			out1.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * �������ļ��ж�ȡctx������Ƶ���ļ�����䵽�ڴ���ȥ ���������д��Save����Ŀǰ�������⣬ ���Ŀǰд����ļ�����ʹ��
	 * ��������Ѿ�����ͨ������������ʹ��
	 * 
	 * @param sFilename
	 * @return
	 */
	public boolean Load(char[] sFilename) {
		int i, j;
		File fp;

		try {
			fp = new File(new String(sFilename,0,CUtility.strlen(sFilename)));
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(fp)));
			FileIOUtility fu = new FileIOUtility(); // �����ļ���д��IO������
			PMYCONTEXT pCur = m_pContext, pPre = null;

			// �ڴ��ʼ��
			if (pCur != null) {// delete the context array
				pCur.DestroyAll();
			}

			// ��ȡ��񳤶�
			// write the table length
			m_nTableLen = fu.readInt(in);
			_log(m_nTableLen);

			// ���¶�����ű��С
			m_pSymbolTable = new int[m_nTableLen];// new buffer for symbol

			// ��ȡ���ű������
			// write the symbol table
			for (i = 0; i < m_nTableLen; i++) {
				m_pSymbolTable[i] = fu.readInt(in);
				_log(m_pSymbolTable[i]);
			}

			// ѭ����ȡ������ά����
			while (in.available() > 0) {// Read the context
				pCur = new PMYCONTEXT(new MYCONTEXT());
				pCur.p.next = null;

				// ��ȡnKey.
				pCur.p.nKey = fu.readInt(in);
				_log("key:" + pCur.p.nKey);

				// ��ȡ��Ƶ��
				pCur.p.nTotalFreq = fu.readInt(in);
				_log("nTotalFreq:" + pCur.p.nTotalFreq);

				// ���¶���ÿ�����ŵ�Ƶ������
				// the every POS frequency
				pCur.p.aTagFreq = new int[m_nTableLen];
				// ѭ����ȡÿ��Ԫ�ص���Ƶ��
				for (i = 0; i < m_nTableLen; i++) {
					pCur.p.aTagFreq[i] = fu.readInt(in);
					_log(pCur.p.aTagFreq[i]);
				}

				// ���¶��������Ķ�ά�����С
				// ��ѭ��˳���ȡ����ά����
				pCur.p.aContextArray = new int[m_nTableLen][m_nTableLen];
				for (i = 0; i < m_nTableLen; i++) {
					for (j = 0; j < m_nTableLen; j++) {
						pCur.p.aContextArray[i][j] = fu.readInt(in);
						_log(pCur.p.aContextArray[i][j]);
					}
				}

				// �������ӵĽڵ㣬���뵽ԭ����������
				// pPre������һ�ڵ㣬����Ϊ��
				if (pPre == null) {
					m_pContext = _copy(pCur); // ������ڽڵ�
				} else {
					pPre.p.next = _copy(pCur);
				}

				pPre = _copy(pCur);// ��ǰ�ڵ�����Ϊ��һ�ڵ�
			}

			in.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ����ĳһ����ǰ���ϵ�Ŀ�����,��һ����������ʹ��
	 * 
	 * @param nKey
	 *            �ʵ����
	 * @param nPrev
	 *            ǰһ�ʴ��Դ���
	 * @param nCur
	 *            ��һ�ʴ��Դ���
	 * @return
	 */
	public double GetContextPossibility(int nKey, int nPrev, int nCur) {
		// ���巵��ָ��
		PPMYCONTEXT ppCur = new PPMYCONTEXT(null);
		// ��ǰԪ�ص��±꣬�ӷ��ű��м���
		int nCurIndex = CUtility
				.BinarySearch(nCur, m_pSymbolTable, m_nTableLen);
		// ǰһԪ�ص��±꣬�ӷ��ű��м����õ�
		int nPrevIndex = CUtility.BinarySearch(nPrev, m_pSymbolTable,
				m_nTableLen);

		// ����Ҳ����������ж��Ժ���Ҫ���в��ң�ֱ�ӷ���һ����Сֵ
		// ������0��Ϊ�˱����Ժ��������ݿ����������
		if (!GetItem(nKey, ppCur) || nCurIndex == -1 || nPrevIndex == -1
				|| ppCur.p.p.aTagFreq[nPrevIndex] == 0
				|| ppCur.p.p.aContextArray[nPrevIndex][nCurIndex] == 0)
			return 0.000001;// return a lower value, not 0 to prevent data
							// sparse

		// ��������Ƶ��
		int nPrevCurConFreq = ppCur.p.p.aContextArray[nPrevIndex][nCurIndex];
		// ǰһ����Ƶ��
		int nPrevFreq = ppCur.p.p.aTagFreq[nPrevIndex];

		// ���ݽ����Ƶ�ȣ�ǰһ��Ƶ�ȣ���Ƶ����������
		// ����һ�����鹫ʽ��Ȩ�طֱ�Ϊ0.9��0.1������һ������ֵ
		return 0.9 * (double) nPrevCurConFreq / (double) nPrevFreq + 0.1
				* (double) nPrevFreq / (double) ppCur.p.p.nTotalFreq;
		// 0.9 and 0.1 is a value based experience
	}

	/**
	 * ����ĳ��Ԫ����ָ��Ŀ¼�ĳ��ֱ���
	 * 
	 * @param nKey
	 *            ��Ӧ�ʵ�����
	 * @param nSymbol
	 *            ��Ӧ���Դ���
	 * @return
	 */
	public int GetFrequency(int nKey, int nSymbol) {
		// Get the frequency which nSymbol appears

		PPMYCONTEXT ppFound = new PPMYCONTEXT(null);
		int nIndex, nFrequency = 0;

		// �������ʧ�ܣ�ֱ�ӷ���0
		if (!GetItem(nKey, ppFound))
			return 0;

		// ���û���ҵ����Ѿ�����0
		// ����һ�����ҵ���
		// ���ҷ��ŵĶ�Ӧ�±�
		nIndex = CUtility.BinarySearch(nSymbol, m_pSymbolTable, m_nTableLen);
		// ����Ҳ������ţ�ֱ�ӷ���0
		if (nIndex == -1)// error finding the symbol
			return 0;

		// ��ȡ��Ԫ�ص���Ƶ��
		nFrequency = ppFound.p.p.aTagFreq[nIndex];
		return nFrequency;
	}

	/**
	 * ���ø�����keyֵ��������һ��MYCONTEXT���󣬷���һ��ppItemRet,����ָ��
	 * <P>
	 * �ڶ���ָ��������ָ������ݣ����������ڴ档
	 * 
	 * @param nKey
	 * @param pItemRet
	 * @return
	 */
	private boolean GetItem(int nKey, PPMYCONTEXT pItemRet) {
		// Get the item according the nKey

		PMYCONTEXT pCur = _copy(m_pContext), pPrev = null;

		if (nKey == 0 && !_isNullPoint(m_pContext)) {
			pItemRet.setValue(m_pContext); // ���nkey==0,����ͷԪ��
			return true;
		}

		// pCur��Ч������£�ѭ�����ң�������nKey������
		// �˴�������һ��bug,�����ǰ���nkey�������
		// ����Add��ʱ��û�н���������
		// ����Ҫ�޸�һ���ж�����Ϊ�����
		while (_isNotNullPoint(pCur) && pCur.p.nKey != nKey) {
			pPrev = _copy(pCur);
			pCur = _copy(pCur.next());
		}

		// ѭ���������ж�ѭ��������
		if (_isNotNullPoint(pCur) && pCur.p.nKey == nKey) {
			// find it and return the current item
			pItemRet.setValue(pCur);
			return true;
		}

		pItemRet.setValue(pPrev); // �������һ����Ч�ڵ�
		return false;
	}

	/**
	 * �����б�ĳ��ȣ�Ҳ�����������е������ֵ�Ĵ�С
	 * 
	 * @param nTableLen
	 * @return
	 */
	private boolean SetTableLen(int nTableLen) {
		m_nTableLen = nTableLen;// Set the table len
		m_pSymbolTable = new int[nTableLen];// new buffer for symbol
		m_pContext = null;
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// �����ӵ�˽�й��ܷ���
	// ////////////////////////////////////////////////////////////////////

	/**
	 * �ж�PMYCONTEXT�Ƿ���һ����Ч��һ��ָ��
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNullPoint(PMYCONTEXT pCur) {
		return !(pCur != null && pCur.p != null);
	}

	/**
	 * �ж�pCur�Ƿ���һ����Ч��һ��ָ��
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNotNullPoint(PMYCONTEXT pCur) {
		return (pCur != null && pCur.p != null);
	}

	/**
	 * ���Թ��ܣ���־���
	 * 
	 * @param s
	 */
	private void _log(String s) {
//		System.out.println(s);
	}

	/**
	 * ���Թ��ܣ���־���
	 * 
	 * @param s
	 */
	private void _log(int s) {
//		System.out.println(s);
	}

	/**
	 * ����һ�������PMYCONTEXT,����������һ���µ�PMYCONTEXT
	 * 
	 * @param src
	 * @return
	 */
	private PMYCONTEXT _copy(PMYCONTEXT src) {
		if (src != null) {
			return new PMYCONTEXT(src.p);
		} else {
			return new PMYCONTEXT(null);
		}
	}
}
