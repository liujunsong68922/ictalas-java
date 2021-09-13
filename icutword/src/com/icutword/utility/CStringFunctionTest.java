package com.icutword.utility;

public class CStringFunctionTest {
	public static void main(String args[]){
		CStringFunction sf = new CStringFunction();
		char a[]=sf.toGB2312("中国人");
		char b[]=sf.toGB2312("中国");
		
		int i =sf.strcasecmp(a, b);
		System.out.println(i);
		
		char c[]=sf.strstr(a,b);
		System.out.println(sf.getCString(c));
		
	}
}
