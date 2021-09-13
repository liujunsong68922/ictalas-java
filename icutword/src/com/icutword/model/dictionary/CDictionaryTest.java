package com.icutword.model.dictionary;

import com.icutword.model.point.Pint;
import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

public class CDictionaryTest {

	public static void main(String args[]) throws Exception {
		CDictionary dict[] = new CDictionary[5];
		String test[] = { "张敏","月份" };
//		try {
//			String dname = "Data/coreDict.dct\0";
//			for (int i = 0; i < 1; i++) {
//				dict[i] = new CDictionary();
//				// System.in.read();
//				dict[i].Load(dname.toCharArray());
//				// System.in.read();
//				System.out.println("load finish.");
//
//				Pint pnCount = new Pint();
//				int pnHandle[] = new int[100];
//				int pnFrequency[] = new int[100];
//
//				for (int l = 0; l < test.length; l++) {
//					System.in.read();
//					char ret[] = new char[100];
//					boolean b =  dict[i].GetMaxMatch(CUtility.toGB2312(test[l]),
//							ret, pnCount);
//					System.out.println(">find result:"+b);
//					System.out.println(">词性=" + pnCount.value);
//					System.out.println(">返回结果:"
//							+ CStringFunction.getCString(ret));
//				}
//				System.in.read();
//				System.gc();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		String dname = "Data/coreDict.dct\0";
		dict[0]=new CDictionary();
		dict[0].Load(dname.toCharArray());
		String s1="张敏";
		boolean bb = dict[0].AddItem(CStringFunction.toGB2312(s1), 0, 100);
		System.out.println(bb);
		
		dict[0].Save("test.dct");
		System.out.println("save ok");
		
		dict[0]=new CDictionary();
		dict[0].Load("test.dct\0".toCharArray());
		char ret[] = new char[100];
		Pint pnCount = new Pint();
		boolean b =  dict[0].GetMaxMatch(CUtility.toGB2312(s1),
				ret, pnCount);
		System.out.println(">find result:"+b);
		System.out.println(">词性=" + pnCount.value);
		System.out.println(">返回结果:"
				+ CStringFunction.getCString(ret));		
		
	}
}
