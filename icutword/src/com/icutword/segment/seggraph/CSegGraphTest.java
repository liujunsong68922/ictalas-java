package com.icutword.segment.seggraph;

import com.icutword.utility.CUtility;

/**
 * CSegGraph�Ĳ�����
 * 
 * @author liujunsong
 * 
 */
public class CSegGraphTest {

	/**
	 * ����������
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
			System.out.println("���� Atom length[" + i + "]="
					+ sg.m_nAtomLength[i]);
		}
		
		System.out.println("�ִʽ�� Cut Result:");
		for (i = 0; i < sg.m_nAtomCount; i++) {
			System.out.print("POS:"+sg.m_nAtomPOS[i]+"   -->");
				System.out.println(CUtility.getCString(sg.m_sAtom[i]));
		}
	}

}
