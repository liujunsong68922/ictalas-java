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
	// 类的数据存储定义
	// ////////////////////////////////////////////////////////////////////
	public int m_nTableLen; // 表长度
	public int[] m_pSymbolTable; // 表示符号表，不同符号存在不同表
	public PMYCONTEXT m_pContext; // 数组的链表的开头
//	private int m_nCategory; // 似乎无效,注释掉

	// ////////////////////////////////////////////////////////////////////
	// Construction/Destruction 构造函数/解构函数定义
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 构造函数
	 */
	public CContextStat() {
		m_pSymbolTable = null;// new buffer for symbol
		m_pContext = null;// init with empty
	}



	/**
	 * 解构函数，最终通过循环调用每个节点的Destroy方法，确保内存释放
	 */
	public void Destroy() {
		// 调用PMYCONTEXT类的内存清理功能
		// 以释放链表内存
		if (m_pContext != null) {
			m_pContext.DestroyAll();
		}

		// 清理本级的内存分配
		m_pContext = null;
//		m_nCategory = 0;
		m_nTableLen = 0;
		m_pSymbolTable = null;
	}

	// ////////////////////////////////////////////////////////////////////
	// 本地方法定义
	// ////////////////////////////////////////////////////////////////////
	
	/**
	 * 设置m_pSymbolTable的数据,定义分配存储空间并设置变量 这个方法用来设置参数基本数据，参数基本数据是一个整数，代表不同含义。
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
	 * 增加一条记录进入属性匹配记录表中去 如果已经存在，修改其计数频率
	 * 这一方法似乎没有使用
	 * @param nKey
	 *            关键词，代表字典类型
	 * @param nPrevSymbol
	 *            前一类型的代码，需要转换成索引值
	 * @param nCurSymbol
	 *            当前类型的代码，需要转换成索引值
	 * @param nFrequency
	 *            出现频率
	 * @return
	 */
	public boolean Add(int nKey, int nPrevSymbol, int nCurSymbol,
			int nFrequency) {
		// Add the context symbol to the array
		// 将上下文符号表增加到array里面去
		PMYCONTEXT pNew;
		PPMYCONTEXT ppRetItem = new PPMYCONTEXT(null);
		int nPrevIndex, nCurIndex;

		// Not get it 如果没有找到对应关键词的话，增加一个新的存储节点出来
		if (!GetItem(nKey, ppRetItem)) {
			pNew = new PMYCONTEXT(new MYCONTEXT()); // 创建指针，同时分配存储内存

			// 存储空间初始化
			pNew.p.nKey = nKey;
			pNew.p.nTotalFreq = 0;
			pNew.p.next = null;
			pNew.p.aContextArray = new int[m_nTableLen][m_nTableLen]; // 频度表
			pNew.p.aTagFreq = new int[m_nTableLen]; // 总频度

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

		// 得到前一个元素词性的索引
		nPrevIndex = CUtility.BinarySearch(nPrevSymbol, m_pSymbolTable,
				m_nTableLen);
		if (nPrevSymbol > 256 && nPrevIndex == -1)// Not find, just for 'nx' and
													// other uncommon POS
			nPrevIndex = CUtility.BinarySearch(nPrevSymbol - nPrevSymbol % 256,
					m_pSymbolTable, m_nTableLen);

		// 得到当前元素词性的索引
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
			// 如果已经找到，增加对应的发生频率
			ppRetItem.p.p.aContextArray[nPrevIndex][nCurIndex] += nFrequency;
			ppRetItem.p.p.aTagFreq[nPrevIndex] += nFrequency;
			ppRetItem.p.p.nTotalFreq += nFrequency;
			return true;
		}
	}

	/**
	 * 将ContextStat里面的内容，写入到文件里面去 这一功能可以用来进行数据的读写测试等操作。
	 * 同时将内容写入到两个文件里面去，一个是格式相同的文件，另一个输入日志记录文件
	 * 目前发现在写入日志记录文件的时候，数据内容丢失掉了，这是一个非常奇怪的问题 关于日志文件信息丢失的问题，以后再检查，现在先不管那么多了
	 * 
	 * @param sFilename
	 * @return
	 */
	public boolean Save(char[] sFilename) {
		// TODO:日志文件输出时，会出现信息丢失的问题，具体原因待查
		// 经过测试，写入到一个ctx文件时候也有错误。
		int i;
		File fp, fp1;
		PMYCONTEXT pCur;
		char sFileShow[] = new char[100];
		FileIOUtility fu = new FileIOUtility();

		try {
			CStringFunction.strcpy(sFileShow, sFilename);
			CStringFunction.strcat(sFileShow, ".shw\0".toCharArray());

			fp = new File(new String(sFilename)); // 输出文件1
			fp1 = new File(new String(sFileShow)); // 输出文件2

			DataOutputStream out = new DataOutputStream(
					new FileOutputStream(fp));
			DataOutputStream out1 = new DataOutputStream(new FileOutputStream(
					fp1));

			// 写入表长度
			// write the table length
			fu.writeInt(out, m_nTableLen);

			// 记录日志
			out1.writeUTF("Table Len=" + m_nTableLen);
			out1.writeUTF("Symbol:");

			// 写入符号表
			// write the symbol table
			for (i = 0; i < m_nTableLen; i++) {
				fu.writeInt(out, m_pSymbolTable[i]);
			}

			// 写入符号表的日志
			for (i = 0; i < m_nTableLen; i++) {
				out1.writeUTF(">" + m_pSymbolTable[i]);
			}
			out1.flush();
			out1.writeUTF("\n");

			// 指向数组的开始点
			pCur = m_pContext;
			while (_isNotNullPoint(pCur)) {
				// 写入nkey，nTotalFreq
				fu.writeInt(out, pCur.p.nKey);
				fu.writeInt(out, pCur.p.nTotalFreq);

				// 日志
				out1.writeUTF("nKey=" + pCur.p.nKey + ",Total frequency="
						+ pCur.p.nTotalFreq + ":");

				// fwrite(pCur->aTagFreq,sizeof(int),m_nTableLen,fp);
				// 写入每一个词的频率
				// the every POS frequency
				for (i = 0; i < m_nTableLen; i++) {
					fu.writeInt(out, pCur.p.aTagFreq[i]);
				}

				int j;
				for (i = 0; i < m_nTableLen; i++) {
					for (j = 0; j < m_nTableLen; j++) {
						// 数组内容
						out.writeInt(pCur.p.aContextArray[i][j]);

						// 写日志
						out1.writeUTF("No." + i + "," + j + "=:"
								+ pCur.p.aContextArray[i][j] + "");
					}
					// 写日志
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
	 * 从数据文件中读取ctx上下文频率文件，填充到内存中去 由于上面编写的Save方法目前存在问题， 因此目前写入的文件不能使用
	 * 这个方法已经测试通过，可以正常使用
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
			FileIOUtility fu = new FileIOUtility(); // 数据文件读写的IO功能类
			PMYCONTEXT pCur = m_pContext, pPre = null;

			// 内存初始化
			if (pCur != null) {// delete the context array
				pCur.DestroyAll();
			}

			// 读取表格长度
			// write the table length
			m_nTableLen = fu.readInt(in);
			_log(m_nTableLen);

			// 重新定义符号表大小
			m_pSymbolTable = new int[m_nTableLen];// new buffer for symbol

			// 读取符号表的数据
			// write the symbol table
			for (i = 0; i < m_nTableLen; i++) {
				m_pSymbolTable[i] = fu.readInt(in);
				_log(m_pSymbolTable[i]);
			}

			// 循环读取各个二维数组
			while (in.available() > 0) {// Read the context
				pCur = new PMYCONTEXT(new MYCONTEXT());
				pCur.p.next = null;

				// 读取nKey.
				pCur.p.nKey = fu.readInt(in);
				_log("key:" + pCur.p.nKey);

				// 读取总频度
				pCur.p.nTotalFreq = fu.readInt(in);
				_log("nTotalFreq:" + pCur.p.nTotalFreq);

				// 重新定义每个符号的频度数组
				// the every POS frequency
				pCur.p.aTagFreq = new int[m_nTableLen];
				// 循环读取每个元素的总频度
				for (i = 0; i < m_nTableLen; i++) {
					pCur.p.aTagFreq[i] = fu.readInt(in);
					_log(pCur.p.aTagFreq[i]);
				}

				// 重新定义上下文二维数组大小
				// 并循环顺序读取填充二维数组
				pCur.p.aContextArray = new int[m_nTableLen][m_nTableLen];
				for (i = 0; i < m_nTableLen; i++) {
					for (j = 0; j < m_nTableLen; j++) {
						pCur.p.aContextArray[i][j] = fu.readInt(in);
						_log(pCur.p.aContextArray[i][j]);
					}
				}

				// 将新增加的节点，插入到原来的链表中
				// pPre代表上一节点，可能为空
				if (pPre == null) {
					m_pContext = _copy(pCur); // 设置入口节点
				} else {
					pPre.p.next = _copy(pCur);
				}

				pPre = _copy(pCur);// 当前节点设置为上一节点
			}

			in.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 计算某一词性前后关系的可能性,这一方法可正常使用
	 * 
	 * @param nKey
	 *            词典代码
	 * @param nPrev
	 *            前一词词性代码
	 * @param nCur
	 *            后一词词性代码
	 * @return
	 */
	public double GetContextPossibility(int nKey, int nPrev, int nCur) {
		// 定义返回指针
		PPMYCONTEXT ppCur = new PPMYCONTEXT(null);
		// 当前元素的下标，从符号表中检索
		int nCurIndex = CUtility
				.BinarySearch(nCur, m_pSymbolTable, m_nTableLen);
		// 前一元素的下标，从符号表中检索得到
		int nPrevIndex = CUtility.BinarySearch(nPrev, m_pSymbolTable,
				m_nTableLen);

		// 如果找不到，或者判断以后不需要进行查找，直接返回一个极小值
		// 不返回0是为了避免以后计算的数据可能溢出问题
		if (!GetItem(nKey, ppCur) || nCurIndex == -1 || nPrevIndex == -1
				|| ppCur.p.p.aTagFreq[nPrevIndex] == 0
				|| ppCur.p.p.aContextArray[nPrevIndex][nCurIndex] == 0)
			return 0.000001;// return a lower value, not 0 to prevent data
							// sparse

		// 交叉点出现频度
		int nPrevCurConFreq = ppCur.p.p.aContextArray[nPrevIndex][nCurIndex];
		// 前一点总频度
		int nPrevFreq = ppCur.p.p.aTagFreq[nPrevIndex];

		// 根据交叉点频度，前一点频度，总频度三个数据
		// 构造一个经验公式，权重分别为0.9，0.1，返回一个计算值
		return 0.9 * (double) nPrevCurConFreq / (double) nPrevFreq + 0.1
				* (double) nPrevFreq / (double) ppCur.p.p.nTotalFreq;
		// 0.9 and 0.1 is a value based experience
	}

	/**
	 * 计算某个元素在指定目录的出现编码
	 * 
	 * @param nKey
	 *            对应词典类型
	 * @param nSymbol
	 *            对应词性代码
	 * @return
	 */
	public int GetFrequency(int nKey, int nSymbol) {
		// Get the frequency which nSymbol appears

		PPMYCONTEXT ppFound = new PPMYCONTEXT(null);
		int nIndex, nFrequency = 0;

		// 如果查找失败，直接返回0
		if (!GetItem(nKey, ppFound))
			return 0;

		// 如果没有找到，已经返回0
		// 下面一定是找到的
		// 查找符号的对应下标
		nIndex = CUtility.BinarySearch(nSymbol, m_pSymbolTable, m_nTableLen);
		// 如果找不到符号，直接返回0
		if (nIndex == -1)// error finding the symbol
			return 0;

		// 获取此元素的总频次
		nFrequency = ppFound.p.p.aTagFreq[nIndex];
		return nFrequency;
	}

	/**
	 * 利用给定的key值，来检索一个MYCONTEXT对象，返回一个ppItemRet,二级指针
	 * <P>
	 * 在二级指针中设置指针的数据，不新增加内存。
	 * 
	 * @param nKey
	 * @param pItemRet
	 * @return
	 */
	private boolean GetItem(int nKey, PPMYCONTEXT pItemRet) {
		// Get the item according the nKey

		PMYCONTEXT pCur = _copy(m_pContext), pPrev = null;

		if (nKey == 0 && !_isNullPoint(m_pContext)) {
			pItemRet.setValue(m_pContext); // 如果nkey==0,返回头元素
			return true;
		}

		// pCur有效的情况下，循环查找，来按照nKey来查找
		// 此处代码有一个bug,假设是按照nkey来排序的
		// 但在Add的时候并没有进行排序处理
		// 所以要修改一个判断条件为不相等
		while (_isNotNullPoint(pCur) && pCur.p.nKey != nKey) {
			pPrev = _copy(pCur);
			pCur = _copy(pCur.next());
		}

		// 循环结束，判断循环结束点
		if (_isNotNullPoint(pCur) && pCur.p.nKey == nKey) {
			// find it and return the current item
			pItemRet.setValue(pCur);
			return true;
		}

		pItemRet.setValue(pPrev); // 设置最后一个有效节点
		return false;
	}

	/**
	 * 设置列表的长度，也就是设置所有的属性字典的大小
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
	// 新增加的私有功能方法
	// ////////////////////////////////////////////////////////////////////

	/**
	 * 判断PMYCONTEXT是否是一个无效的一级指针
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNullPoint(PMYCONTEXT pCur) {
		return !(pCur != null && pCur.p != null);
	}

	/**
	 * 判断pCur是否是一个有效的一级指针
	 * 
	 * @param pCur
	 * @return
	 */
	private boolean _isNotNullPoint(PMYCONTEXT pCur) {
		return (pCur != null && pCur.p != null);
	}

	/**
	 * 测试功能，日志输出
	 * 
	 * @param s
	 */
	private void _log(String s) {
//		System.out.println(s);
	}

	/**
	 * 测试功能，日志输出
	 * 
	 * @param s
	 */
	private void _log(int s) {
//		System.out.println(s);
	}

	/**
	 * 利用一个输入的PMYCONTEXT,复制生成另一个新的PMYCONTEXT
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
