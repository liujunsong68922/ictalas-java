package com.icutword.model.contextstat;

/**
 * MYCONTEXT�Ķ�����һ���ṹ��,���е�next��������ָ����һ���ڵ��ָ��
 * 
 * @author liujunsong
 * 
 */
public class MYCONTEXT {
	public int nKey;// The key word �ؼ���
	public int aContextArray[][];// The context array ����������
	public int aTagFreq[];// The total number a tag appears �ʵķֲ�Ƶ��
	public int nTotalFreq;// The total number of all the tags �ܵĳ���Ƶ��
	public PMYCONTEXT next;// The chain pointer to next Context ��һ���ڵ�
	
	/**
	 * �ڴ����
	 */
	public void Destroy(){
		aContextArray=null;
		aTagFreq=null;
		next=null;
	}
}
