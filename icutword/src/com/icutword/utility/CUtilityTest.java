package com.icutword.utility;

/**
 * CUtility的测试功能类
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
		char a[]=sf.toGB2312("中国人");
		char b[]=sf.toGB2312("中国");
		
		char c[]=sf.CC_Find(a,b);
		System.out.println(sf.getCString(c));
		
		
	}

}
