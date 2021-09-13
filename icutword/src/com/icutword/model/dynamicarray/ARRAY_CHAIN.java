package com.icutword.model.dynamicarray;

import com.icutword.utility.CStringFunction;

/**
 * ARRAY_CHAIN�Ƕ��дʽ���Ķ�Ӧ�洢������һ������ṹ
 * @author liujunsong
 *
 */
public class ARRAY_CHAIN {
	//���ر����洢��ʼ
	public int col, row;// row and column
	public double value;// The value of the array
	public int nPOS;
	public int nWordLen;
	// ֮���Բ�ʹ��String���ԭ������Ϊ��ת����ʱ����ܻ����ת�������
	// Ϊ��֤��ԭ�����빦��һ�£���ʹ��String��
	public char[] sWord; // ��һ��char[]������ģ��char *sWord,Ч����ͬ
	// The possible POS of the word related to the segmentation graph
	// nextָ����һ���ڵ�
	public PARRAY_CHAIN next;
	
	/**
	 * ���캯��
	 */
	public ARRAY_CHAIN(){
		next=null;
		sWord=null;
	}
	
	/**
	 * �⹹�������_���ͷ��ڴ�
	 */
	public void Destroy(){
		next=null;
		sWord=null;		
	}
	
	/**
	 * ����toString��������ARRAY_CHAIN���������ת�����ַ���
	 */
	public String toString(){
		String sout="";
		sout+="{row:"+row;
		sout+=",col:"+col;
		sout+=",value:"+value;
		sout+=",nPOS:"+nPOS;
		sout+=",nWordLen:"+nWordLen;
		sout+=",sWord:"+(sWord==null?"null":CStringFunction.getCString(sWord));
		sout+="}";
		
		return sout;
	}
}
