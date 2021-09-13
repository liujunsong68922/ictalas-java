package com.icutword.model.dictionary;

//import com.icutword.model.contextstat.PMYCONTEXT;

public class PWORD_CHAIN {
	WORD_CHAIN p = null;

	public PWORD_CHAIN(WORD_CHAIN point) {
		p = point;
	}
	
	/**
	 * ��һ��ָ���Ϸ�װһ��next()����
	 * @return
	 */
	public PWORD_CHAIN next(){
		if(p!=null){
			return p.next;
		}else{
			return null;
		}
	}
	
	/**
	 * �ӵ�ǰ�ڵ㿪ʼ���ݹ鵽�����¼��ڵ㣬Ȼ���ͷ�����������ڴ�
	 * ������������ͷ��ڴ档
	 */
	public void DestroyAll(){
		PWORD_CHAIN pCur=next(),pTemp;
		//�ݹ����������¼��Ľڵ�
		while(pCur!=null){
			pTemp=pCur.next(); //�õ���һ�ڵ�			
			pCur.p.Destroy(); //��ǰ�ڵ��ڴ��ͷ�
			pCur=pTemp; //ָ����һ�ڵ�
		}
		//������ָ���Ӧ�ڵ�
		if(p!=null){
			p.Destroy();
			p=null;
		}
	}
}
