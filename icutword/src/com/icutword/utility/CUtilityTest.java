package com.icutword.utility;

/**
 * CUtility�Ĳ��Թ�����
 * @author liujunsong
 *
 */
public class CUtilityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CUtility sf = new CUtility();
		char a[]=sf.toGB2312("�й���");
		char b[]=sf.toGB2312("�й�");
		
		char c[]=sf.CC_Find(a,b);
		System.out.println(sf.getCString(c));
		
		
	}

}
