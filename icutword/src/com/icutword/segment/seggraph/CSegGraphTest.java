package com.icutword.segment.seggraph;

import com.icutword.utility.CUtility;

/**
 * CSegGraph的测试类
 * 
 * @author liujunsong
 * 
 */
public class CSegGraphTest {

	/**
	 * 测试主程序
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CSegGraph sg = new CSegGraph();
		char input[] = CUtility.toGB2312("abcd 1234");
		sg.AtomSegment(input);
		System.out.println("Atom ok.");
		System.out.println("Atom length:" + sg.m_nAtomCount);
		int i;
		for (i = 0; i < sg.m_nAtomCount; i++) {
			System.out.println("长度 Atom length[" + i + "]="
					+ sg.m_nAtomLength[i]);
		}
		
		System.out.println("分词结果 Cut Result:");
		for (i = 0; i < sg.m_nAtomCount; i++) {
			System.out.print("POS:"+sg.m_nAtomPOS[i]+"   -->");
				System.out.println(CUtility.getCString(sg.m_sAtom[i]));
		}
	}

}
