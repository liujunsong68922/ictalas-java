package com.icutword.segment.nshortpath.queue;

/**
 * QUEUE_ELEMENT��Ӧ�Ķ���ָ��
 * @author liujunsong
 *
 */
public class PPQUEUE_ELEMENT {
	public PQUEUE_ELEMENT p=null;
	
	/**
	 * �������Ĺ��캯��
	 * @param point
	 */
	public PPQUEUE_ELEMENT(PQUEUE_ELEMENT point){
		p=point;
	}
	
	/**
	 * ��������ָ���ֵ,��һ�������������µ�PQUEUE_ELEMENT����
	 * @param point
	 */
	public void setValue(PQUEUE_ELEMENT point){
		p=point;
	}	
}
