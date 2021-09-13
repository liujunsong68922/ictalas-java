package com.icutword.model.dictionary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
//import java.io.IOException;

//import com.icutword.CString;
import com.icutword.model.point.Pint;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;
import com.icutword.utility.FileIOUtility;

/**
 * �ʵ䶨����
 * 
 * @author liujunsong
 * 
 */
public class CDictionary {
	// ////////////////////////////////////////////////////////////////////
	// ϵͳ��������
	// ////////////////////////////////////////////////////////////////////
	// The number of Chinese Char,including 5 empty position between 3756-3761
	private int CC_NUM = 6768;

	private int WORD_MAXLENGTH = 100;
	private int WT_DELIMITER = 0;
	private int WT_CHINESE = 1;
	private int WT_OTHER = 2;

	// private int CT_SENTENCE_BEGIN = 1;// Sentence begin
	// private int CT_SENTENCE_END = 4;// Sentence ending
	private int CT_SINGLE = 5;// SINGLE byte
	private int CT_DELIMITER = CT_SINGLE + 1;// delimiter
	private int CT_CHINESE = CT_SINGLE + 2;// Chinese Char

	// private int CT_LETTER = CT_SINGLE + 3;// HanYu Pinyin
	// private int CT_NUM = CT_SINGLE + 4;// HanYu Pinyin
	// private int CT_INDEX = CT_SINGLE + 5;// HanYu Pinyin
	// private int CT_OTHER = CT_SINGLE + 12;// Other

	// ��һ��charת��Ϊ�޷��ŵ�byte����(0-255)
	// ��λ���������������ݱȽϣ���ԭ����C�߼�����
	private static int toUnsigByte(int c) {
		byte b = (byte) c;
		return (b >= 0 ? b : b + 256);
	}

	// The ID equation of Chinese Char
	private int CC_ID(int c1, int c2) {
		return (int) (toUnsigByte(c1) - 176) * 94 + (toUnsigByte(c2) - 161);
	}

	// The first char computed by the Chinese Char ID
	private int CC_CHAR1(int id) {
		return (id) / 94 + 176;
	}

	// The second char computed by the Chinese Char ID
	private int CC_CHAR2(int id) {
		return (id) % 94 + 161;
	}

	// �ֵ��������飬һ������һ��,����ԭʼ�ֵ�
	INDEX_TABLE m_IndexTable[] = null;
	// �ֵ���޸ļ�¼��
	PMODIFY_TABLE m_pModifyTable[] = null;

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction
	// ////////////////////////////////////////////////////////////////////

	/**
	 * ���캯��
	 */
	public CDictionary() {
		// ����GB2312���ֵ��������飬һ���Զ���6768��Ԫ��
		m_IndexTable = new INDEX_TABLE[CC_NUM];
		// ��ʼ�����������ָ�룬�ֱ�ָ��һ��INDEX_TABLE����
		for (int i = 0; i < CC_NUM; i++) {
			m_IndexTable[i] = new INDEX_TABLE();
		}
		// ���Ĺ����ֵ������Ϊ��
		m_pModifyTable = null;
	}

	/**
	 * �⹹�������ͷ��ڴ�
	 */
	private void Destroy() {
		for (int i = 0; i < CC_NUM; i++) {
			// delete the memory of word item array in the dictionary
			if (m_IndexTable[i].pWordItemHead != null) {
				for (int j = 0; j < m_IndexTable[i].nCount; j++) {
					if (m_IndexTable[i].pWordItemHead[j].p != null) {
						m_IndexTable[i].pWordItemHead[j].p.sWord = null;
						m_IndexTable[i].pWordItemHead[j].p = null;
					}
				}
			}

			// ɾ����pWordItemHead��Ӧ��ָ������
			m_IndexTable[i].nCount = 0;
			m_IndexTable[i].pWordItemHead = null;
		}

		// ɾ�����±�����ö���
		DelModified();
	}

	// ////////////////////////////////////////////////////////////////////
	// ���ع��ܷ���
	// ////////////////////////////////////////////////////////////////////

	/**
	 * �������ļ��ж�ȡ�ʵ��ļ���ˢ�µ��ڴ��е����ݽṹȥ
	 * 
	 * @param sFilename
	 * @return
	 */
	public boolean Load(char[] sFilename) {
		// System.out.println(new String(sFilename));
		return Load(sFilename, false);
	}

	/*********************************************************************
	 * 
	 * Func Name : Load
	 * 
	 * Description: Load the dictionary from the file .dct
	 * 
	 * 
	 * Parameters : sFilename: the file name
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-9
	 *********************************************************************/
	/**
	 * �������ļ��ж�ȡ������䵽���ص�������ȥ ����Ĺؼ��Ƕ�ԭʼ���ݸ�ʽ����⣬Ŀǰ�˷����Ѿ�����ͨ����
	 * 
	 * @param sFilename
	 * @param bReset
	 * @return
	 */
	private boolean Load(char[] sFilename, boolean bReset) {
		// FILE *fp;
		FileIOUtility fu = new FileIOUtility(); // �����ļ���д��IO������
		File fp;
		int i, j, nBuffer[] = new int[3];

		// if((fp=fopen(sFilename,"rb"))==NULL)
		// return false;//fail while opening the file
		try {
			fp = new File(new String(sFilename).trim());
			DataInputStream in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(fp)));

			// Release the memory for new files
			Destroy();

			for (i = 0; i < CC_NUM; i++) {
				// ����ÿ�����ֵĴʵ�����
				m_IndexTable[i].nCount = fu.readInt(in);
				// _log("\n nCount:" + m_IndexTable[i].nCount + " ");

				// ѭ����ȡÿһ����
				if (m_IndexTable[i].nCount > 0) {
					// ����ʵĴ�ſռ�,�ȷ���ָ������飬�ٸ�ÿ��ָ������ֵ
					m_IndexTable[i].pWordItemHead = new PWORD_ITEM[m_IndexTable[i].nCount];
					for (j = 0; j < m_IndexTable[i].nCount; j++) {
						m_IndexTable[i].pWordItemHead[j] = new PWORD_ITEM(
								new WORD_ITEM()); // �����ڴ棬����ָ��
					}
				} else {
					// ���nCount==0,����Ϊnull
					m_IndexTable[i].pWordItemHead = null;
					// ��������ѭ��
					continue;
				}

				j = 0;
				// ѭ����ȡÿһ���ʵĶ���
				while (j < m_IndexTable[i].nCount) {
					// ��ȡ3��intֵ���ֱ����Ƶ�ȣ����ȣ�����
					nBuffer[0] = fu.readInt(in); // Ƶ��
					nBuffer[1] = fu.readInt(in); // ����
					nBuffer[2] = fu.readInt(in); // ����

					// _log("Ƶ��,����,���ԣ�" + nBuffer[0] + "," + nBuffer[1] + ","
					// + nBuffer[2]);

					// ����洢�ֵ��������Ҫ�Ĵ洢�ռ�
					m_IndexTable[i].pWordItemHead[j].p.sWord = new char[nBuffer[1] + 1]; // ����洢�ռ�

					if (nBuffer[1] > 0) {
						byte[] buffer = new byte[nBuffer[1]];
						in.read(buffer);
						for (int k = 0; k < nBuffer[1]; k++) {
							m_IndexTable[i].pWordItemHead[j].p.sWord[k] = (char) buffer[k];
						}

						// _log("WORD��" + new String(buffer, "GB2312") + " \n");
					}
					// ��ֹ����
					m_IndexTable[i].pWordItemHead[j].p.sWord[nBuffer[1]] = '\0';

					if (bReset) {
						// �����Ҫ����Ƶ������
						m_IndexTable[i].pWordItemHead[j].p.nFrequency = 0;
					} else {
						m_IndexTable[i].pWordItemHead[j].p.nFrequency = nBuffer[0];
					}

					m_IndexTable[i].pWordItemHead[j].p.nWordLen = nBuffer[1];
					m_IndexTable[i].pWordItemHead[j].p.nHandle = nBuffer[2];

					j += 1;// Get next item in the original table.
				}
			}

			in.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********************************************************************
	 * 
	 * Func Name : Save
	 * 
	 * Description: Save the dictionary as the file .dct
	 * 
	 * 
	 * Parameters : sFilename: the file name
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-9
	 *********************************************************************/

	public boolean Save(String sFilename) throws Exception {
		// FILE *fp;

		FileIOUtility fu = new FileIOUtility(); // �����ļ���д��IO������

		File outfile = new File(sFilename);
		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				outfile));
		int i, j, nCount, nBuffer[] = new int[3];
		PWORD_CHAIN pCur;
		// if((fp=fopen(sFilename,"wb"))==NULL)
		// return false;//fail while opening the file
		for (i = 0; i < CC_NUM; i++) {
			pCur = null;
			if (m_pModifyTable != null) {// Modification made
				nCount = m_IndexTable[i].nCount + m_pModifyTable[i].p.nCount
						- m_pModifyTable[i].p.nDelete;
				// fwrite(&nCount,sizeof(int),1,fp);
				fu.writeInt(out, nCount); // ����ܼ�¼��,һ��int

				pCur = m_pModifyTable[i].p.pWordItemHead;
				j = 0;
				while (pCur != null && j < m_IndexTable[i].nCount) {
					// Output to the file after comparision
					if (CStringFunction.strcmp(pCur.p.data.sWord,
							m_IndexTable[i].pWordItemHead[j].p.sWord) < 0
							|| (CStringFunction.strcmp(pCur.p.data.sWord,
									m_IndexTable[i].pWordItemHead[j].p.sWord) == 0 
									&& pCur.p.data.nHandle < m_IndexTable[i].pWordItemHead[j].p.nHandle)) {
						// Output the modified data to the file
						nBuffer[0] = pCur.p.data.nFrequency;
						nBuffer[1] = pCur.p.data.nWordLen;
						nBuffer[2] = pCur.p.data.nHandle;
						// fwrite(nBuffer,sizeof(int),3,fp);
						fu.writeInt(out, nBuffer[0]);
						fu.writeInt(out, nBuffer[1]);
						fu.writeInt(out, nBuffer[2]);

						if (nBuffer[1] > 0) {// String length is more than 0
							// fwrite(pCur.p.data.sWord,sizeof(char),nBuffer[1],fp);
							byte[] buffer = new byte[nBuffer[1]];
							for (int k = 0; k < nBuffer[1]; k++) {
								buffer[k] = (byte) pCur.p.data.sWord[k];
							}
							out.write(buffer);
						}
						pCur = pCur.next();// Get next item in the modify
											// table.
					} else if (m_IndexTable[i].pWordItemHead[j].p.nFrequency == -1) {
						// The item has been removed,so skip it
						j += 1;
					} else if (CStringFunction.strcmp(pCur.p.data.sWord,
							m_IndexTable[i].pWordItemHead[j].p.sWord) > 0
							|| (CStringFunction.strcmp(pCur.p.data.sWord,
									m_IndexTable[i].pWordItemHead[j].p.sWord) == 0
									&& pCur.p.data.nHandle > m_IndexTable[i].pWordItemHead[j].p.nHandle)) {
						// Output the index table data to the file
						nBuffer[0] = m_IndexTable[i].pWordItemHead[j].p.nFrequency;
						nBuffer[1] = m_IndexTable[i].pWordItemHead[j].p.nWordLen;
						nBuffer[2] = m_IndexTable[i].pWordItemHead[j].p.nHandle;

						// fwrite(nBuffer,sizeof(int),3,fp);
						fu.writeInt(out, nBuffer[0]);
						fu.writeInt(out, nBuffer[1]);
						fu.writeInt(out, nBuffer[2]);

						if (nBuffer[1] > 0) {// String length is more than 0
							// fwrite(m_IndexTable[i].pWordItemHead[j].sWord,sizeof(char),nBuffer[1],fp);
							byte[] buffer = new byte[nBuffer[1]];
							for (int k = 0; k < nBuffer[1]; k++) {
								buffer[k] = (byte) m_IndexTable[i].pWordItemHead[j].p.sWord[k];
							}
							out.write(buffer);
						}
						j += 1;// Get next item in the original table.
					}
				}
				if (j < m_IndexTable[i].nCount) {
					while (j < m_IndexTable[i].nCount) {
						if (m_IndexTable[i].pWordItemHead[j].p.nFrequency != -1) {// Has
																					// been
																					// deleted
							nBuffer[0] = m_IndexTable[i].pWordItemHead[j].p.nFrequency;
							nBuffer[1] = m_IndexTable[i].pWordItemHead[j].p.nWordLen;
							nBuffer[2] = m_IndexTable[i].pWordItemHead[j].p.nHandle;
							// fwrite(nBuffer,sizeof(int),3,fp);
							fu.writeInt(out, nBuffer[0]);
							fu.writeInt(out, nBuffer[1]);
							fu.writeInt(out, nBuffer[2]);

							if (nBuffer[1] > 0) {
								// String length is more than 0
								// fwrite(m_IndexTable[i].pWordItemHead[j].sWord,sizeof(char),nBuffer[1],fp);
								byte[] buffer = new byte[nBuffer[1]];
								for (int k = 0; k < nBuffer[1]; k++) {
									buffer[k] = (byte) m_IndexTable[i].pWordItemHead[j].p.sWord[k];
								}
								out.write(buffer);
							}
						}
						j += 1;// Get next item in the original table.
					}
				} else
					// //No Modification
					while (pCur != null)// Add the rest data to the file.
					{
						nBuffer[0] = pCur.p.data.nFrequency;
						nBuffer[1] = pCur.p.data.nWordLen;
						nBuffer[2] = pCur.p.data.nHandle;
						// fwrite(nBuffer,sizeof(int),3,fp);
						fu.writeInt(out, nBuffer[0]);
						fu.writeInt(out, nBuffer[1]);
						fu.writeInt(out, nBuffer[2]);

						if (nBuffer[1] > 0) {// String length is more than 0
							// fwrite(pCur->data.sWord,sizeof(char),nBuffer[1],fp);
							byte[] buffer = new byte[nBuffer[1]];
							for (int k = 0; k < nBuffer[1]; k++) {
								buffer[k] = (byte) pCur.p.data.sWord[k];
							}
							out.write(buffer);
						}
						pCur = pCur.next();// Get next item in the modify table.
					}
			} else {
				// fwrite(&m_IndexTable[i].nCount,sizeof(int),1,fp);
				fu.writeInt(out, m_IndexTable[i].nCount);
				// write to the file
				j = 0;
				while (j < m_IndexTable[i].nCount) {
					nBuffer[0] = m_IndexTable[i].pWordItemHead[j].p.nFrequency;
					nBuffer[1] = m_IndexTable[i].pWordItemHead[j].p.nWordLen;
					nBuffer[2] = m_IndexTable[i].pWordItemHead[j].p.nHandle;
					// fwrite(nBuffer,sizeof(int),3,fp);
					fu.writeInt(out, nBuffer[0]);
					fu.writeInt(out, nBuffer[1]);
					fu.writeInt(out, nBuffer[2]);

					if (nBuffer[1] > 0) {// String length is more than 0
						// fwrite(m_IndexTable[i].pWordItemHead[j].sWord,sizeof(char),nBuffer[1],fp);
						byte[] buffer = new byte[nBuffer[1]];
						for (int k = 0; k < nBuffer[1]; k++) {
							buffer[k] = (byte) m_IndexTable[i].pWordItemHead[j].p.sWord[k];
						}
						out.write(buffer);
					}
					j += 1;// Get next item in the original table.
				}
			}
		}
		// fclose(fp);
		out.flush();
		out.close();
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : AddItem
	 * 
	 * Description: Add a word item to the dictionary
	 * 
	 * 
	 * Parameters : sWord: the word nHandle:the handle number nFrequency: the
	 * frequency Returns : success or fail Author : Kevin Zhang History :
	 * 1.create 2002-1-9
	 *********************************************************************/

	public boolean AddItem(char[] sWord, int nHandle, int nFrequency) {
		char sWordAdd[] = new char[WORD_MAXLENGTH - 2];
		int nPos, nFoundPos;
		PWORD_CHAIN pRet, pTemp, pNext;
		int i = 0;
		Pint pnPos = new Pint();
		if (!PreProcessing(sWord, pnPos, sWordAdd, true))
			return false;

		nPos = pnPos.value;

		Pint pnFoundPos = new Pint();
		if (FindInOriginalTable(nPos, sWordAdd, nHandle, pnFoundPos)) {
			// The word exists in the originaltable,
			// so add the frequency Operation in the index table and its items
			nFoundPos = pnFoundPos.value;
			if (m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nFrequency == -1) {
				// The word item has been removed
				m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nFrequency = nFrequency;
				if (m_pModifyTable == null)// Not prepare the buffer
				{
					m_pModifyTable = new PMODIFY_TABLE[CC_NUM];
					// memset(m_pModifyTable,0,CC_NUM*sizeof(MODIFY_TABLE));
					for (int ii = 0; ii < CC_NUM; ii++) {
						m_pModifyTable[ii] = new PMODIFY_TABLE(
								new MODIFY_TABLE());
					}
				}
				m_pModifyTable[nPos].p.nDelete -= 1;
			} else
				m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nFrequency += nFrequency;
			return true;
		}

		// The items not exists in the index table.
		// As following, we have to find the item whether exists in the modify
		// data region
		// If exists, change the frequency .or else add a item
		if (m_pModifyTable == null)// Not prepare the buffer
		{
			m_pModifyTable = new PMODIFY_TABLE[CC_NUM];
			// memset(m_pModifyTable,0,CC_NUM*sizeof(MODIFY_TABLE));
			for (int ii = 0; ii < CC_NUM; ii++) {
				m_pModifyTable[ii] = new PMODIFY_TABLE(new MODIFY_TABLE());
			}
		}
		PPWORD_CHAIN ppRet = new PPWORD_CHAIN(new PWORD_CHAIN(new WORD_CHAIN()));
		if (FindInModifyTable(nPos, sWordAdd, nHandle, ppRet)) {
			pRet = ppRet.p;
			if (pRet != null && pRet.p != null) {
				pRet = pRet.next();

			} else {
				pRet = m_pModifyTable[nPos].p.pWordItemHead;
				pRet.p.data.nFrequency += nFrequency;
				return true;
			}
		}
		pRet = ppRet.p;
		// find the proper position to add the word to the modify data table
		// and link
		pTemp = new PWORD_CHAIN(new WORD_CHAIN());// Allocate the word chain
													// node
		if (pTemp == null) // Allocate memory failure
			return false;
		// memset(pTemp,0,sizeof(WORD_CHAIN));//init it with 0
		pTemp.p.data.nHandle = nHandle;// store the handle
		pTemp.p.data.nWordLen = CStringFunction.strlen(sWordAdd);
		pTemp.p.data.sWord = new char[1 + pTemp.p.data.nWordLen];
		CStringFunction.strcpy(pTemp.p.data.sWord, sWordAdd);
		pTemp.p.data.nFrequency = nFrequency;
		pTemp.p.next = null;
		if (pRet != null && pRet.p != null) {
			pNext = pRet.next();// Get the next item before the current item
			pRet.p.next = pTemp;// link the node to the chain
		} else {
			pNext = m_pModifyTable[nPos].p.pWordItemHead;
			m_pModifyTable[nPos].p.pWordItemHead = pTemp;
			// Set the pAdd as the head node
		}
		pTemp.p.next = pNext;// Very important!!!! or else it will lose some
								// node
		// Modify in 2001-10-29
		m_pModifyTable[nPos].p.nCount++;// the number increase by one
		return true;
	}

	/**
	 * �Ӵʵ��������棬ɾ��һ���ʵĶ���,ɾ���Ĺ��̣�ʵ�����ǽ�Ƶ������Ϊ-1�Ĺ��̣� ͬʱ������һ�ű��м�¼�˺��ֿ�ͷ�ʵ�ɾ������
	 * 
	 * @param sWord
	 *            Ҫɾ���Ĵ�����
	 * @param nHandle
	 *            Ҫɾ���Ĵ���
	 * @return
	 */
	private boolean DelItem(char[] sWord, int nHandle) {
		char sWordDel[] = new char[WORD_MAXLENGTH - 2];
		int nPos, nFoundPos, nTemp;
		PWORD_CHAIN pPre, pTemp, pCur;
		PPWORD_CHAIN ppPre = new PPWORD_CHAIN(null);

		Pint pnPos = new Pint();
		if (!PreProcessing(sWord, pnPos, sWordDel))
			return false;

		nPos = pnPos.value;

		Pint pnFoundPos = new Pint();
		if (FindInOriginalTable(nPos, sWordDel, nHandle, pnFoundPos)) {
			nFoundPos = pnFoundPos.value;

			if (m_pModifyTable == null) {
				// Not prepare the buffer,
				// ���������δ׼�����������ݴ洢�������ڴ�ռ�
				m_pModifyTable = new PMODIFY_TABLE[CC_NUM];
				for (int i = 0; i < CC_NUM; i++) {
					m_pModifyTable[i] = new PMODIFY_TABLE(new MODIFY_TABLE());
				}
			}
			m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nFrequency = -1; // ԭ����Ƶ������Ϊ-1,�Ѿ�ʧЧ
			m_pModifyTable[nPos].p.nDelete += 1; // ɾ����Ƶ�ȣ�����1����������ֿ�ͷ����ɾ����һ����

			if (nHandle == -1)// Remove all items which word is
								// sWordDel,ignoring the handle
			{
				nTemp = nFoundPos + 1;// Check its previous position
				while (nTemp < m_IndexTable[nPos].nCount
						&& CStringFunction
								.strcmp(m_IndexTable[nPos].pWordItemHead[nFoundPos].p.sWord,
										sWordDel) == 0) {// �����ͬһ���ʣ������Ǵ��ԣ�ͨͨ����Ϊ������״̬

					m_IndexTable[nPos].pWordItemHead[nTemp].p.nFrequency = -1; // �˴ʲ�����
					m_pModifyTable[nPos].p.nDelete += 1; // ɾ��������һ��
					nTemp += 1;
				}
			}
			return true;
		}

		// Operation in the modify table and its items
		// �����޸ı��е���Ϣ��¼,��������¼���޸ı�֮�еĻ�
		// ��������еĲ���������ɾ��

		if (FindInModifyTable(nPos, sWordDel, nHandle, ppPre)) {
			pPre = ppPre.p;
			pCur = m_pModifyTable[nPos].p.pWordItemHead;
			if (pPre != null && pPre.p != null)
				pCur = _copy(pPre.next());

			while (pCur != null
					&& pCur.p != null
					&& CStringFunction._stricmp(pCur.p.data.sWord, sWordDel) == 0
					&& (pCur.p.data.nHandle == nHandle || nHandle < 0)) {
				pTemp = _copy(pCur);
				if (pPre != null && pPre.p != null) {
					// pCur is the first item

					pPre.p.next = pCur.next();
				} else {
					m_pModifyTable[nPos].p.pWordItemHead = _copy(pCur.next());
				}

				pCur = _copy(pCur.next());
				pTemp.p.data.sWord = null;
				pTemp.p = null;
				pTemp = null;
				// delete pTemp->data.sWord;//Delete the word
				// delete pTemp;
			}
			return true;
		}
		return false;

	}

	/**
	 * ɾ�����и��¹�������
	 * 
	 * @return
	 */
	private boolean DelModified() {
		PWORD_CHAIN pTemp, pCur;
		if (m_pModifyTable == null)
			return true;

		for (int i = 0; i < m_pModifyTable.length; i++) {
			if (m_pModifyTable[i] != null && m_pModifyTable[i].p != null) {
				pCur = _copy(m_pModifyTable[i].p.pWordItemHead);
				if (pCur != null) {
					pCur.DestroyAll();
				}
			}
		}

		for (int i = 0; i < m_pModifyTable.length; i++) {
			m_pModifyTable[i] = null;
		}
		m_pModifyTable = null;
		return true;
	}

	/**
	 * �жϸ������ַ������ڴʿ����Ƿ����
	 * 
	 * @param sWord
	 * @param nHandle
	 * @return
	 */
	public boolean IsExist(char[] sWord, int nHandle) {
		char sWordFind[] = new char[WORD_MAXLENGTH - 2];
		int nPos;
		Pint pnPos = new Pint();
		if (!PreProcessing(sWord, pnPos, sWordFind))
			return false;
		nPos = pnPos.value;
		return (FindInOriginalTable(nPos, sWordFind, nHandle) || FindInModifyTable(
				nPos, sWordFind, nHandle));
	}

	/**
	 * �õ������ʾ�Ĵ���
	 * 
	 * @param sWord
	 *            Ҫ����Ĵ���
	 * @param pnCount
	 *            ��������
	 * @param pnHandle
	 *            ��������
	 * @param pnFrequency
	 *            Ƶ������
	 * @return
	 */
	public boolean GetHandle(char[] sWord, Pint pnCount, int[] pnHandle,
			int[] pnFrequency) {
		char sWordGet[] = new char[WORD_MAXLENGTH - 2];
		int nPos, nFoundPos, nTemp;
		Pint pnFoundPos = new Pint();
		PWORD_CHAIN pPre, pCur;
		// *pnCount=0;
		pnCount.value = 0;

		Pint pnPos = new Pint();
		if (!PreProcessing(sWord, pnPos, sWordGet))
			return false;

		nPos = pnPos.value; // ����npos����ֵ

		// step1:��ԭʼ���ݱ��н��в���
		if (FindInOriginalTable(nPos, sWordGet, -1, pnFoundPos)) {
			nFoundPos = pnFoundPos.value;
			pnHandle[pnCount.value] = m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nHandle;
			pnFrequency[pnCount.value] = m_IndexTable[nPos].pWordItemHead[nFoundPos].p.nFrequency;

			pnCount.value += 1;
			nTemp = nFoundPos + 1;// Check its previous position

			while (nTemp < m_IndexTable[nPos].nCount
					&& CStringFunction.strcmp(
							m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord,
							sWordGet) == 0) {

				pnHandle[pnCount.value] = m_IndexTable[nPos].pWordItemHead[nTemp].p.nHandle;
				pnFrequency[pnCount.value] = m_IndexTable[nPos].pWordItemHead[nTemp].p.nFrequency;
				pnCount.value += 1;

				nTemp += 1;
			}
			return true;
		}

		// step2:�ڸ�����Ϣ���н��в���
		// �������ֵ�����ƴ�������������еĲ��ҽ��
		PPWORD_CHAIN ppPre = new PPWORD_CHAIN(new PWORD_CHAIN(new WORD_CHAIN()));
		if (FindInModifyTable(nPos, sWordGet, -1, ppPre)) {
			pPre = ppPre.p;
			pCur = _copy(m_pModifyTable[nPos].p.pWordItemHead);

			if (pPre != null) {
				pCur = _copy(pPre.next());
			}
			while (pCur != null
					&& pCur.p != null
					&& CStringFunction.strcasecmp(pCur.p.data.sWord, sWordGet) == 0) {

				pnHandle[pnCount.value] = pCur.p.data.nHandle;
				pnFrequency[pnCount.value] = pCur.p.data.nFrequency;

				pnCount.value += 1;
				pCur = _copy(pCur.next());

			}
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : FindInOriginalTable
	 * 
	 * Description: judge the word and handle exist in the inner table and its
	 * items
	 * 
	 * 
	 * Parameters : nInnerCode: the inner code of the first CHines char sWord:
	 * the word nHandle:the handle number *nPosRet:the position which node is
	 * matched
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-9
	 *********************************************************************/
	/**
	 * ��ԭʼ�ֵ���н��в���
	 * 
	 * @param nInnerCode
	 *            ���ֵ��ڲ����ֱ���GB2312
	 * @param sWord
	 *            Ҫ���ҵĴ���
	 * @param nHandle
	 *            Ҫ���ҵĴ���
	 * @return
	 */
	private boolean FindInOriginalTable(int nInnerCode, char[] sWord,
			int nHandle) {
		return FindInOriginalTable(nInnerCode, sWord, nHandle, null);
	}

	/**
	 * ��ԭʼ�ֵ���н��в���(��һ��������������)
	 * 
	 * @param nInnerCode
	 *            ���ֵ��ڲ����ֱ���GB2312
	 * @param sWord
	 *            Ҫ���ҵĴ���
	 * @param nHandle
	 *            Ҫ���ҵĴ���
	 * @param nPosRet
	 *            nPos����ֵ
	 * @return
	 */
	public boolean FindInOriginalTable(int nInnerCode, char[] sWord,
			int nHandle, Pint nPosRet) {
		PWORD_ITEM pItems[] = m_IndexTable[nInnerCode].pWordItemHead;
		int nStart = 0, nEnd = m_IndexTable[nInnerCode].nCount - 1, nMid = (nStart + nEnd) / 2, nCount = 0, nCmpValue;

		while (nStart <= nEnd)// Binary search
		{
			// System.out
			// .println(CStringFunction.getCString(pItems[nMid].p.sWord));
			// System.out.println(CStringFunction.getCString(sWord));
			nCmpValue = CStringFunction.strcmp(pItems[nMid].p.sWord, sWord);
			if (nCmpValue == 0
					&& (pItems[nMid].p.nHandle == nHandle || nHandle == -1)) {
				if (nPosRet != null) {
					if (nHandle == -1)// Not very strict match
					{// Add in 2002-1-28
						nMid -= 1;
						while (nMid >= 0
								&& CStringFunction.strcasecmp(
										pItems[nMid].p.sWord, sWord) == 0)
							// Get the first item which match the current word
							nMid--;
						if (nMid < 0
								|| CStringFunction.strcasecmp(
										pItems[nMid].p.sWord, sWord) != 0)
							nMid++;
					}
					// *nPosRet=nMid;
					nPosRet.value = nMid;
					return true;
				}
				// if(nPosRet)
				// *nPosRet=nMid;
				if (nPosRet != null)
					nPosRet.value = nMid;
				return true;// find it
			} else if (nCmpValue < 0
					|| (nCmpValue == 0 && pItems[nMid].p.nHandle < nHandle && nHandle != -1)) {
				nStart = nMid + 1;
			} else if (nCmpValue > 0
					|| (nCmpValue == 0 && pItems[nMid].p.nHandle > nHandle && nHandle != -1)) {
				nEnd = nMid - 1;
			}
			nMid = (nStart + nEnd) / 2;
		}// while ѭ��

		if (nPosRet != null) {
			// Get the previous position
			nPosRet.value = nMid - 1;
		}
		return false;
	}

	/**
	 * �ڸ��¼�¼�в��ң������һ������
	 * 
	 * @param nInnerCode
	 * @param sWord
	 * @param nHandle
	 * @return
	 */
	private boolean FindInModifyTable(int nInnerCode, char[] sWord, int nHandle) {
		return FindInModifyTable(nInnerCode, sWord, nHandle, null);
	}

	/*********************************************************************
	 * 
	 * Func Name : FindInModifyTable
	 * 
	 * Description: judge the word and handle exist in the modified table and
	 * its items
	 * 
	 * 
	 * Parameters : nInnerCode: the inner code of the first CHines char sWord:
	 * the word nHandle:the handle number *pFindRet: the node found
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-9
	 *********************************************************************/
	private boolean FindInModifyTable(int nInnerCode, char[] sWord,
			int nHandle, PPWORD_CHAIN pFindRet) {
		PWORD_CHAIN pCur, pPre;
		if (m_pModifyTable == null)// empty
			return false;

		pCur = m_pModifyTable[nInnerCode].p.pWordItemHead;
		pPre = null;

		while (pCur != null
				&& pCur.p != null
				&& (CStringFunction.strcasecmp(pCur.p.data.sWord, sWord) < 0 || (CStringFunction
						.strcasecmp(pCur.p.data.sWord, sWord) == 0 && pCur.p.data.nHandle < nHandle)))
		// sort the link chain as alphabet
		{
			pPre = _copy(pCur);
			pCur = _copy(pCur.next());
		}
		if (pFindRet != null)
			pFindRet.p = _copy(pPre);
		if (pCur != null && pCur.p != null
				&& CStringFunction.strcasecmp(pCur.p.data.sWord, sWord) == 0
				&& (pCur.p.data.nHandle == nHandle || nHandle < 0)) {// The node
																		// exists,
																		// delete
																		// the
																		// node
																		// and
																		// return
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetWordType
	 * 
	 * Description: Get the type of word
	 * 
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the type Author : Kevin Zhang History : 1.create 2002-1-9
	 *********************************************************************/
	/**
	 * ��������ַ�������������
	 * 
	 * @param sWord
	 * @return
	 */
	private int GetWordType(char[] sWord) {
		int nType = CUtility.charType(sWord), nLen = CStringFunction
				.strlen(sWord);
		if (nLen > 0 && nType == CT_CHINESE && CUtility.IsAllChinese(sWord))
			return WT_CHINESE;// Chinese word
		else if (nLen > 0 && nType == CT_DELIMITER)
			return WT_DELIMITER;// Delimiter
		else
			return WT_OTHER;// other invalid
	}

	/*********************************************************************
	 * 
	 * Func Name : PreProcessing
	 * 
	 * Description: Get the type of word
	 * 
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the type Author : Kevin Zhang History : 1.create 2002-1-9
	 *********************************************************************/
	/**
	 * Ҫ���ҵ�����Ԥ��������һ�����֣�����char��ȥ����������������ֵ����
	 * 
	 * @param sWord
	 * @param nId
	 * @param sWordRet
	 * @param bAdd
	 * @return
	 */
	private boolean PreProcessing(char[] sWord, Pint nId, char[] sWordRet,
			boolean bAdd) {
		// Position for the delimeters
		int nType = CUtility.charType(sWord), nLen = CUtility.strlen(sWord);
		int nEnd = nLen - 1, nBegin = 0;
		if (nLen == 0)
			return false;
		while (nEnd >= 0 && sWord[nEnd] == ' ')
			nEnd -= 1;
		while (nBegin <= nEnd && sWord[nBegin] == ' ')
			nBegin += 1;
		if (nBegin > nEnd)
			return false;
		if (nEnd != nLen - 1 || nBegin != 0) {
			CStringFunction.strncpy(sWord, sWord, nBegin, nEnd - nBegin + 1);
			sWord[nEnd - nBegin + 1] = 0;
		}
		/*
		 * if((bAdd||strlen(sWord)>4)&&IsAllChineseNum(sWord)) { //Only convert
		 * the Chinese Num to 3755 while //Get the inner code of the first
		 * Chinese Char strcpy(sWord,"��ʮ��"); }
		 */
		if (nType == CT_CHINESE)// &&IsAllChinese((unsigned char *)sWord)
		{// Chinese word
			nId.value = CC_ID(sWord[0], sWord[1]);
			// Get the inner code of the first Chinese Char
			CStringFunction.strcpy(sWordRet, sWord, 2);// store the word,not
														// store the
			// first Chinese Char
			return true;
		}
		/*
		 * if(nType==CT_NUM&&IsAllNum((unsigned char *)sWord)) {nId=3756; //Get
		 * the inner code of the first Chinese Char sWordRet[0]=0;//store the
		 * word,not store the first Chinese Char return true; }
		 */if (nType == CT_DELIMITER) {// Delimiter
			nId.value = 3755;
			// Get the inner code of the first Chinese Char
			CStringFunction.strcpy(sWordRet, sWord);// store the word,not store
													// the
			// first Chinese Char
			return true;
		}
		/*
		 * if(nType==CT_LETTER&&IsAllLetter((unsigned char *)sWord)) {nId=3757;
		 * //Get the inner code of the first Chinese Char sWordRet[0]=0;//store
		 * the word,not store the first Chinese Char return true; }
		 * if(nType==CT_SINGLE&&IsAllSingleByte((unsigned char *)sWord)) {
		 * nId=3758; //Get the inner code of the first Chinese Char
		 * sWordRet[0]=0;//store the word,not store the first Chinese Char
		 * return true; } if(nType==CT_INDEX&&IsAllIndex((unsigned char
		 * *)sWord)) {nId=3759; //Get the inner code of the first Chinese Char
		 * sWordRet[0]=0;//store the word,not store the first Chinese Char
		 * return true; }
		 */
		return false;// other invalid
	}

	/**
	 * �����Ժ��PreProcessing���������һ������Ϊfalse��ʵ�������á�
	 * 
	 * @param sWord
	 * @param nId
	 * @param sWordRet
	 * @return
	 */
	private boolean PreProcessing(char[] sWord, Pint nId, char[] sWordRet) {
		return PreProcessing(sWord, nId, sWordRet, false);
	}

	/*********************************************************************
	 * 
	 * Func Name : MergePOS
	 * 
	 * Description: Merge all the POS into nHandle, just get the word in the
	 * dictionary and set its Handle as nHandle
	 * 
	 * 
	 * Parameters : nHandle: the only handle which will be attached to the word
	 * 
	 * Returns : the type Author : Kevin Zhang History : 1.create 2002-1-21
	 *********************************************************************/
	private boolean MergePOS(int nHandle) {
		int i, j, nCompare;
		char sWordPrev[] = new char[WORD_MAXLENGTH];
		PWORD_CHAIN pPre, pCur, pTemp;
		if (m_pModifyTable != null)// Not prepare the buffer
		{
			// TODO:�˴��߼��ƺ��е����⣬��Ҫ��ϸ����
			// m_pModifyTable=new MODIFY_TABLE[CC_NUM];
			// memset(m_pModifyTable,0,CC_NUM*sizeof(MODIFY_TABLE));
			m_pModifyTable = new PMODIFY_TABLE[CC_NUM];
			for (i = 0; i < CC_NUM; i++) {
				m_pModifyTable[i] = new PMODIFY_TABLE(new MODIFY_TABLE());
			}
		}
		for (i = 0; i < CC_NUM; i++)// Operation in the index table
		{// delete the memory of word item array in the dictionary
			sWordPrev[0] = 0;// Set empty
			for (j = 0; j < m_IndexTable[i].nCount; j++) {
				nCompare = CStringFunction._stricmp(sWordPrev,
						m_IndexTable[i].pWordItemHead[j].p.sWord);
				if ((j == 0 || nCompare < 0)
						&& m_IndexTable[i].pWordItemHead[j].p.nFrequency != -1) {// Need
																					// to
																					// modify
																					// its
																					// handle
					m_IndexTable[i].pWordItemHead[j].p.nHandle = nHandle;// Change
																			// its
																			// handle
					CStringFunction.strcpy(sWordPrev,
							m_IndexTable[i].pWordItemHead[j].p.sWord);// Refresh
																		// previous
																		// Word
				} else if (nCompare == 0
						&& m_IndexTable[i].pWordItemHead[j].p.nFrequency != -1) {// Need
																					// to
																					// delete
																					// when
																					// not
																					// delete
																					// and
																					// same
																					// as
																					// previous
																					// word
					m_IndexTable[i].pWordItemHead[j].p.nFrequency = -1;// Set
																		// delete
																		// flag
					m_pModifyTable[i].p.nDelete += 1;// Add the number of being
														// deleted
				}
			}
		}
		for (i = 0; i < CC_NUM; i++)// Operation in the modify table
		{
			pPre = null;
			pCur = m_pModifyTable[i].p.pWordItemHead;
			sWordPrev[0] = 0;// Set empty
			while (pCur != null && pCur.p != null) {
				if (CStringFunction._stricmp(pCur.p.data.sWord, sWordPrev) > 0) {// The
					// new
					// word
					pCur.p.data.nHandle = nHandle;// Chang its handle
					CStringFunction.strcpy(sWordPrev, pCur.p.data.sWord);// Set
																			// new
					// previous
					// word
					pPre = _copy(pCur);// New previous pointer
					pCur = _copy(pCur.next());
				} else {// The same word as previous,delete it.
					pTemp = _copy(pCur);
					if (pPre != null && pPre.p != null)// pCur is the first item
						pPre.p.next = _copy(pCur.next());
					else
						m_pModifyTable[i].p.pWordItemHead = _copy(pCur.next());
					pCur = _copy(pCur.next());
					// delete pTemp->data.sWord;//Delete the word
					// delete pTemp;//Delete the item
					pTemp.p.data.sWord = null;
					pTemp.p.next = null;
					pTemp = null;
				}
			}
		}
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetMaxMatch
	 * 
	 * Description: Get the max match to the word
	 * 
	 * 
	 * Parameters : nHandle: the only handle which will be attached to the word
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-21
	 *********************************************************************/
	/**
	 * �������ƥ�������
	 * 
	 * @param sWord
	 *            �����ַ���
	 * @param sWordRet
	 *            ����ַ���
	 * @param npHandleRet
	 *            �������
	 * @return true/false��ȡ�ɹ�����ʧ��
	 */
	public boolean GetMaxMatch(char[] sWord, char[] sWordRet, Pint npHandleRet) {
		char sWordGet[] = new char[WORD_MAXLENGTH - 2], sFirstChar[] = new char[3];
		int nPos, nFoundPos, nTemp;
		PWORD_CHAIN pCur;
		// *npHandleRet=-1;
		npHandleRet.value = -1;
		Pint pnPos = new Pint();

		// sWordGet�Ǵ���ȥ����һ�������Ժ��ʣ�ಿ�֡�
		if (!PreProcessing(sWord, pnPos, sWordGet))
			return false;

		// System.out.println(CStringFunction.getCString(sWord));
		// System.out.println(CStringFunction.getCString(sWordGet));

		// nPos����������Table���е���������һ������ֵ
		nPos = pnPos.value;
		sWordRet[0] = 0;
		// Get the first char
		// SFirstChar������char���������һ�����ֵ�ֵ������GB2312����
		CStringFunction.strncpy(
				sFirstChar,
				sWord,
				CStringFunction.strlen(sWord)
						- CStringFunction.strlen(sWordGet));

		// System.out.println(CStringFunction.strlen(sWord));
		// System.out.println(" >  len,sWordGet="
		// + CStringFunction.strlen(sWordGet) + ":"
		// + CStringFunction.getCString(sWordGet));

		// Set the end flag
		sFirstChar[CStringFunction.strlen(sWord)
				- CStringFunction.strlen(sWordGet)] = 0;
		// System.out.println(CStringFunction.getCString(sFirstChar));
		// System.out.println("nPos=" + nPos);
		Pint pnFoundPos = new Pint();
		// ��ԭʼ�ʵ���н��в���
		FindInOriginalTable(nPos, sWordGet, -1, pnFoundPos);
		nFoundPos = pnFoundPos.value;
		nTemp = nFoundPos;// Check its previous position
		if (nFoundPos == -1)
			nTemp = 0;

		// _log(" nTemp=" + nTemp + "\n");
		// _log(" sWordGet.len2=" + CStringFunction.strlen(sWordGet) + "\n");
		// _log(" sWordGet:" + this.getCString(sWordGet) + "\n");

		while (nTemp < m_IndexTable[nPos].nCount) {
			char[] find1 = CUtility.CC_Find(
					m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord, sWordGet);
			// _log(" 1064sWord:"
			// + CStringFunction
			// .getCString(m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord));
			// _log(" 1064sWord:" + CStringFunction.getCString(sWordGet));
			//
			// _log(CStringFunction.getCString(find1));

			if (find1 != null
					&& !_isSame(find1,
							m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord)) {
				// Get the next
				nTemp += 1;
				_log("not found. nTemp=" + nTemp + "\n");
			} else {
				break;
			}
		}

		if (nTemp < m_IndexTable[nPos].nCount) {
			char find2[] = CUtility.CC_Find(
					m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord, sWordGet);
			_log("find2:" + CStringFunction.getCString(find2));
			_log("find2:"
					+ CStringFunction
							.getCString(m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord));
			if (find2 != null
					&& _isSame(find2,
							m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord)) {
				CStringFunction.strcpy(sWordRet, sFirstChar);
				CStringFunction.strcat(sWordRet,
						m_IndexTable[nPos].pWordItemHead[nTemp].p.sWord);
				npHandleRet.value = m_IndexTable[nPos].pWordItemHead[nTemp].p.nHandle;
				return true;
			}
		}// Cannot get the item and retrieve the modified data if exists
			// Operation in the index table and its items
		if (m_pModifyTable != null
				&& m_pModifyTable[nPos].p.pWordItemHead != null)// Exists
			pCur = _copy(m_pModifyTable[nPos].p.pWordItemHead);
		else
			pCur = null;

		
		while (pCur != null
				&& pCur.p != null
				&& CStringFunction.strcmp(pCur.p.data.sWord, sWordGet) <= 0
				&& !_isSame(CUtility.CC_Find(pCur.p.data.sWord, sWordGet),
						pCur.p.data.sWord))//
		{
			pCur = _copy(pCur.next());
		}

		if (pCur != null
				&& pCur.p != null
				&& _isSame(CUtility.CC_Find(pCur.p.data.sWord, sWordGet),
						pCur.p.data.sWord)) {
			// Get it
			// Warning:ԭ����C++����˴��ж���һ���߼�����ѡ����ǣ�isSame�ķ���
			// �Ѿ�������
			CStringFunction.strcpy(sWordRet, sFirstChar);
			CStringFunction.strcat(sWordRet, pCur.p.data.sWord);
			npHandleRet.value = pCur.p.data.nHandle;
			return true;
		}
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetPOSValue
	 * 
	 * Description: Get the POS value according the POS string
	 * 
	 * 
	 * Parameters :
	 * 
	 * Returns : the value Author : Kevin Zhang History : 1.create 2002-1-29
	 *********************************************************************/
	/**
	 * �����ַ�����ƫ����
	 * 
	 * @param sPOS
	 * @return
	 */
	private int GetPOSValue(char[] sPOS) {
		int nPOS;
		char[] ssPlusPos, sTemp = new char[4];
		int sPlusPos;
		if (CStringFunction.strlen(sPOS) < 3) {
			nPOS = sPOS[0] * 256 + sPOS[1];
		} else {
			sPlusPos = CStringFunction.strchr(sPOS, '+');
			CStringFunction.strncpy(sTemp, sPOS, sPlusPos);
			sTemp[sPlusPos] = 0;
			nPOS = 100 * GetPOSValue(sTemp);
			CStringFunction.strncpy(sTemp, 0, sPOS, sPlusPos + 1, 4);
			nPOS += CUtility.atoi(sTemp);
		}
		return nPOS;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetPOSString
	 * 
	 * Description: Get the POS string according the POS value
	 * 
	 * 
	 * Parameters :
	 * 
	 * Returns : success or fail Author : Kevin Zhang History : 1.create
	 * 2002-1-29
	 *********************************************************************/
	private boolean GetPOSString(int nPOS, char[] sPOSRet) {
		if (nPOS > 'a' * 25600) {
			if ((nPOS / 100) % 256 != 0) {
				// sprintf(sPOSRet,"%c%c+%d",nPOS/25600,(nPOS/100)%256,nPOS%100);
				sPOSRet[0] = (char) (nPOS / 25600);
				sPOSRet[1] = (char) ((nPOS / 100) % 256);
				sPOSRet[2] = '+';
				sPOSRet[3] = (char) (nPOS % 100);
			} else {
				// sprintf(sPOSRet,"%c+%d",nPOS/25600,nPOS%100);
				sPOSRet[0] = (char) ((nPOS / 100) % 256);
				sPOSRet[1] = '+';
				sPOSRet[2] = (char) (nPOS % 100);
			}
		} else {
			if (nPOS > 256) {
				// sprintf(sPOSRet,"%c%c",nPOS/256,nPOS%256);
				sPOSRet[0] = (char) (nPOS / 256);
				sPOSRet[1] = (char) (nPOS % 256);
			} else {
				// sprintf(sPOSRet,"%c",nPOS%256);
				sPOSRet[0] = (char) (nPOS % 256);
			}
		}
		return true;
	}

	/**
	 * �õ�����ʺʹ��Ե�Ƶ������
	 * 
	 * @param sWord
	 *            ����
	 * @param nHandle
	 *            ����
	 * @return Ƶ��
	 */
	public int GetFrequency(char[] sWord, int nHandle) {
		char sWordFind[] = new char[WORD_MAXLENGTH - 2];
		int nPos, nIndex;
		PWORD_CHAIN pFound;
		Pint pnPos = new Pint();
		if (!PreProcessing(sWord, pnPos, sWordFind))
			return 0;
		nPos = pnPos.value;

		Pint pnIndex = new Pint();
		if (FindInOriginalTable(nPos, sWordFind, nHandle, pnIndex)) {
			nIndex = pnIndex.value;
			return m_IndexTable[nPos].pWordItemHead[nIndex].p.nFrequency;
		}
		nIndex = pnIndex.value;

		PPWORD_CHAIN ppFound = new PPWORD_CHAIN(new PWORD_CHAIN(
				new WORD_CHAIN()));
		if (FindInModifyTable(nPos, sWordFind, nHandle, ppFound)) {
			return ppFound.p.p.data.nFrequency;
		}
		return 0;
	}

	// bool CDictionary::Output(char *sFilename)
	// {
	// FILE *fp;
	// int i,j;
	// PWORD_CHAIN pCur;
	// char sPrevWord[WORD_MAXLENGTH]="", sCurWord[WORD_MAXLENGTH],sPOS[10];
	// if((fp=fopen(sFilename,"wb"))==NULL)
	// return false;//fail while opening the file
	// if(m_pModifyTable)
	// {//Modification made, not to output when modify table exists.
	// return false;
	// }
	// for(i=0;i<CC_NUM;i++)
	// {
	// pCur=NULL;
	// j=0;
	// while(j<m_IndexTable[i].nCount)
	// {
	// GetPOSString(m_IndexTable[i].pWordItemHead[j].nHandle,sPOS);
	// //Get the POS string
	// sprintf(sCurWord,"%c%c%s",CC_CHAR1(i),CC_CHAR2(i),m_IndexTable[i].pWordItemHead[j].sWord);
	// if(strcmp(sPrevWord,sCurWord)!=0)
	// fprintf(fp,"\n%s %s",sCurWord,sPOS);
	// else
	// fprintf(fp," %s",sPOS);
	// strcpy(sPrevWord,sCurWord);
	// j+=1;//Get next item in the original table.
	// }
	// }
	// fclose(fp);
	// return true;
	// }

	// bool CDictionary::OutputChars(char *sFilename)
	// {
	// FILE *fp;
	// int i,j;
	// char sPrevWord[WORD_MAXLENGTH]="", sCurWord[WORD_MAXLENGTH];
	// if((fp=fopen(sFilename,"wb"))==NULL)
	// return false;//fail while opening the file
	// if(m_pModifyTable)
	// {//Modification made, not to output when modify table exists.
	// return false;
	// }
	// for(i=0;i<CC_NUM;i++)
	// {
	// j=0;
	// while(j<m_IndexTable[i].nCount)
	// {
	// sprintf(sCurWord,"%c%c%s",CC_CHAR1(i),CC_CHAR2(i),m_IndexTable[i].pWordItemHead[j].sWord);
	// if(strcmp(sPrevWord,sCurWord)!=0&&m_IndexTable[i].pWordItemHead[j].nFrequency>50)//
	// fprintf(fp,"%s",sCurWord);
	// strcpy(sPrevWord,sCurWord);
	// j+=1;//Get next item in the original table.
	// }
	// }
	// fclose(fp);
	// return true;
	//
	// }

	/**
	 * �ʵ�ϲ�����
	 * 
	 * @param dict2
	 * @param nRatio
	 * @return
	 */
	private boolean Merge(CDictionary dict2, int nRatio)
	// Merge dict2 into current dictionary and the frequency ratio from dict2
	// and current dict is nRatio
	{
		int i, j, k, nCmpValue;
		char sWord[] = new char[WORD_MAXLENGTH];
		if (m_pModifyTable != null || dict2.m_pModifyTable != null) {// Modification
																		// made,
																		// not
																		// to
																		// output
																		// when
																		// modify
																		// table
																		// exists.
			return false;
		}
		for (i = 0; i < CC_NUM; i++) {
			j = 0;
			k = 0;
			while (j < m_IndexTable[i].nCount
					&& k < dict2.m_IndexTable[i].nCount) {
				nCmpValue = CStringFunction.strcmp(
						m_IndexTable[i].pWordItemHead[j].p.sWord,
						dict2.m_IndexTable[i].pWordItemHead[k].p.sWord);
				if (nCmpValue == 0)// Same Words and determine the different
									// handle
				{
					if (m_IndexTable[i].pWordItemHead[j].p.nHandle < dict2.m_IndexTable[i].pWordItemHead[k].p.nHandle)
						nCmpValue = -1;
					else if (m_IndexTable[i].pWordItemHead[j].p.nHandle > dict2.m_IndexTable[i].pWordItemHead[k].p.nHandle)
						nCmpValue = 1;
				}

				if (nCmpValue == 0) {
					m_IndexTable[i].pWordItemHead[j].p.nFrequency = (nRatio
							* m_IndexTable[i].pWordItemHead[j].p.nFrequency + dict2.m_IndexTable[i].pWordItemHead[k].p.nFrequency)
							/ (nRatio + 1);
					j += 1;
					k += 1;
				} else if (nCmpValue < 0)// Get next word in the current
											// dictionary
				{
					m_IndexTable[i].pWordItemHead[j].p.nFrequency = (nRatio * m_IndexTable[i].pWordItemHead[j].p.nFrequency)
							/ (nRatio + 1);
					j += 1;
				} else// Get next word in the second dictionary
				{
					if (dict2.m_IndexTable[i].pWordItemHead[k].p.nFrequency > (nRatio + 1) / 10) {
						// TODO:write it
						// sprintf(sWord,"%c%c%s",CC_CHAR1(i),CC_CHAR2(i),dict2.m_IndexTable[i].pWordItemHead[k].p.sWord);
						AddItem(sWord,
								dict2.m_IndexTable[i].pWordItemHead[k].p.nHandle,
								dict2.m_IndexTable[i].pWordItemHead[k].p.nFrequency
										/ (nRatio + 1));
					}
					k += 1;
				}
			}
			while (j < m_IndexTable[i].nCount)// words in current dictionary are
												// left
			{
				m_IndexTable[i].pWordItemHead[j].p.nFrequency = (nRatio * m_IndexTable[i].pWordItemHead[j].p.nFrequency)
						/ (nRatio + 1);
				j += 1;
			}
			while (k < dict2.m_IndexTable[i].nCount)// words in Dict2 are left
			{
				if (dict2.m_IndexTable[i].pWordItemHead[k].p.nFrequency > (nRatio + 1) / 10) {
					// TODO:change it.
					// sprintf(sWord,"%c%c%s",CC_CHAR1(i),CC_CHAR2(i),dict2.m_IndexTable[i].pWordItemHead[k].p.sWord);
					AddItem(sWord,
							dict2.m_IndexTable[i].pWordItemHead[k].p.nHandle,
							dict2.m_IndexTable[i].pWordItemHead[k].p.nFrequency
									/ (nRatio + 1));
				}
				k += 1;
			}
		}
		return true;
	}

	// Delete word item which
	// (1)frequency is 0
	// (2)word is same as following but the POS value is parent set of the
	// following
	// for example "������/n/0" will deleted, because "������/nr/0" is more detail and
	// correct
	/**
	 * �ʵ��Ż�
	 * 
	 * @return
	 */
	private boolean Optimum() {
		int nPrevPOS, i, j, nPrevFreq;
		char sPrevWord[] = new char[WORD_MAXLENGTH], sCurWord[] = new char[WORD_MAXLENGTH];
		for (i = 0; i < CC_NUM; i++) {
			j = 0;
			sPrevWord[0] = 0;
			nPrevPOS = 0;
			nPrevFreq = -1;
			while (j < m_IndexTable[i].nCount) {
				// TODO it.
				// sprintf(sCurWord,"%c%c%s",CC_CHAR1(i),CC_CHAR2(i),m_IndexTable[i].pWordItemHead[j].sWord);
				if (nPrevPOS == 30720
						|| nPrevPOS == 26368
						|| nPrevPOS == 29031
						|| (CStringFunction.strcmp(sPrevWord, sCurWord) == 0
								&& nPrevFreq == 0 && m_IndexTable[i].pWordItemHead[j].p.nHandle / 256 * 256 == nPrevPOS)) {
					// Delete Previous word item
					// Delete word with POS 'x','g' 'qg'
					// TODO:delItem
					// DelItem(sPrevWord,nPrevPOS);
				}
				CStringFunction.strcpy(sPrevWord, sCurWord);
				nPrevPOS = m_IndexTable[i].pWordItemHead[j].p.nHandle;
				nPrevFreq = m_IndexTable[i].pWordItemHead[j].p.nFrequency;
				j += 1;// Get next item in the original table.
			}
		}
		return true;
	}

	// ////////////////////////////////////////////////////////////////////
	// Ӧ�õ��������ӵı��ط���
	// ////////////////////////////////////////////////////////////////////

	private void _log(String s) {
		// System.out.print(s);
	}

	// private void _log(char s[]) {
	// String s1 = CUtility.getCString(s);
	// System.out.print(s1);
	// }

	/**
	 * ����һ�������PWORD_CHAIN,����������һ���µ�PWORD_CHAIN
	 * 
	 * @param src
	 * @return
	 */
	private PWORD_CHAIN _copy(PWORD_CHAIN src) {
		if (src != null) {
			return new PWORD_CHAIN(src.p);
		} else {
			return new PWORD_CHAIN(null);
		}
	}

	/**
	 * �ж�����char[]�Ƿ���ȫ��ͬ
	 * 
	 * @param s1
	 * @param s2
	 * @return
	 */
	private boolean _isSame(char[] s1, char[] s2) {
		int i = 0;

		if (CStringFunction.strlen(s1) != CStringFunction.strlen(s2)) {
			return false;
		}

		while (i < CStringFunction.strlen(s1)) {
			if (s1[i] != s2[i]) {
				return false; // �Ƚ��Ժ����ߴ��ڲ�ͬ
			}
			i++;
		}

		return true; // û�в�ͬ��������ͬ

	}

	/**
	 * debug ��
	 * 
	 * @param in
	 * @return
	 */
	// private String getCString(char in[]) {
	// if (in == null) {
	// return "";
	// }
	// byte t[] = new byte[in.length];
	// for (int k = 0; in[k] != 0; k++) {
	// if (in[k] > 128) {
	// t[k] = (byte) (in[k] - 256);
	// } else {
	// t[k] = (byte) in[k];
	// }
	// }
	// try {
	// return new String(t, "GB2312");
	// } catch (Exception e) {
	// e.printStackTrace();
	// return "";
	// }
	// }
}
