package com.icutword.segment.nshortpath.queue;

/**
 * ���е�Ԫ�ض���,��Ӧ�ṹQUEUE
 * 
 * @author liujunsong
 * 
 */
public class QUEUE_ELEMENT {
	public int nParent;// the weight
	public int nIndex;// number of index in the parent node
	public double eWeight;// the weight of last path
	public PQUEUE_ELEMENT next;

	/**
	 * ���캯��
	 */
	public QUEUE_ELEMENT() {
		nParent = 0;
		nIndex = 0;
		eWeight = 0;
		next = null;
	}
	
	/**
	 * �ڴ����,��Ҫ���ж϶�������
	 */
	public void Destroy(){
		next = null;		
	}
	
	/**
	 * ת�����ַ�����ʽ��ʾ
	 */
	public String toString(){
		String sret="{nParent:"+nParent+",nIndex:"+nIndex+",eWeight:"+eWeight+"}";
		return sret;
	}
}
