package com.icutword.model.dynamicarray;

/**
 * һ������ָ�����ָ��һ��PARRAY_CHAIN ����ģ��ԭ���������PARRAY_CHAIN *pRet���� Ϊ pPARRAY_CHAIN pRet
 * 
 * @author liujunsong
 * 
 */
public class PPARRAY_CHAIN {
	public PARRAY_CHAIN p;// p��һ��ָ�룬ָ��һ��PARRAY_CHAIN����

	/**
	 * ����ʹ�ô��в����Ĺ��캯��
	 * 
	 * @param point
	 */
	public PPARRAY_CHAIN(PARRAY_CHAIN point) {
		p = point;
	}

	public void setValue(PARRAY_CHAIN point) {
		p = point;
	}
}
