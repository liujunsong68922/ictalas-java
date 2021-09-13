package com.icutword.model.contextstat;

/**
 * PMYCONTEXT�Ķ�����һ��ָ��ṹ��MYCONTEXT��ָ��, ��һ��һ��ָ��
 * @author liujunsong
 * 
 */
public class PMYCONTEXT {
	public MYCONTEXT p=null;// The chain pointer to next Context
	/**
	 * �������Ĺ��캯��
	 * @param tag
	 */
	public PMYCONTEXT(MYCONTEXT tag){
		p=tag;
	}
	
	/**
	 * �õ���һ���ڵ㣬ͨ����װp��ʵ�֡�
	 * @return
	 */
	public PMYCONTEXT next(){
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
		PMYCONTEXT pCur=next(),pTemp;
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
