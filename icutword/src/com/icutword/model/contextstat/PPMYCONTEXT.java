package com.icutword.model.contextstat;

/**
 * һ��ָ��MYCONTEXT�Ķ���ָ��
 * @author liujunsong
 *
 */
public class PPMYCONTEXT {
	public PMYCONTEXT p=null;
	
	/**
	 * Ψһ���õĹ��캯�������Դ���һ��null
	 * @param point
	 */
	public PPMYCONTEXT(PMYCONTEXT point){
		p=point;
	}
	
	/**
	 * ��������ָ���ֵ,��һ�������������µ�PPMYCONTEXT����
	 * Ҳ�������·����ڴ档
	 * @param point
	 */
	public void setValue(PMYCONTEXT point){
		p=point;
	}	
}
