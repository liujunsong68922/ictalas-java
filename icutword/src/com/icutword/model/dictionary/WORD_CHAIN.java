package com.icutword.model.dictionary;

/**
 * �ṹ��WORD_CHAIN��ģ��ʵ����
 * @author liujunsong
 *
 */
public class WORD_CHAIN {
    WORD_ITEM data;
    PWORD_CHAIN next;
    
    /**
     * ���캯��������WORD_CHAIN��ʱ���Զ�����������������
     */
    public WORD_CHAIN(){
    	data= new WORD_ITEM();
    }
    
    /**
     * �����ͷ�����
     */
    public void Destroy(){
    	if(data!=null){
    		data.sWord=null;
    	}
    	data=null;
    	next=null;
    }
}
