package com.icutword.model.dynamicarray;

//import com.icutword.contextstat.PMYCONTEXT;

/**
 * tagArrayChainԭ����һ��C�����Structure, PARRAY_CHAIN�����������ָ�����
 * ת��ΪJava�Ժ�ֱ�Ӷ���һ��PARRAY_CHAIN��Ϳ����ˣ�������Ҫԭ����Structure������ģ��
 * 
 * @author liujunsong
 * 
 */
public class PARRAY_CHAIN {
	public ARRAY_CHAIN p;

	/**
	 * ����ʹ�ô��в����Ĺ��캯����ȷ��p�õ���ʼ��
	 * 
	 * @param point
	 */
	public PARRAY_CHAIN(ARRAY_CHAIN point) {
		p = point;
	}
	
	/**
	 * ���а�װ�Ժ󣬰�װһ��next�����������ª��.p.����
	 * @return
	 */
	public PARRAY_CHAIN next() {
		if (p != null) {
			return p.next;
		} else {
			return null;
		}
	}
	
	/**
	 * �ӵ�ǰ�ڵ㿪ʼ���ݹ鵽�����¼��ڵ㣬Ȼ���ͷ�����������ڴ�
	 * ������������ͷ��ڴ档
	 */
	public void DestroyAll(){
		PARRAY_CHAIN pCur=next(),pTemp;
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
	
	public String toString(){
		if(p!=null){
			return p.toString();
		}else{
			return "null";
		}
	}
	
	/**
	 * �����е�����ת����һ���ַ������
	 * @return
	 */
	public String AlltoString(){
		String ret="";
		PARRAY_CHAIN pCur=this;
		//�ݹ����������¼��Ľڵ�
		while(pCur!=null && pCur.p!=null){
			ret+= pCur.toString();
			ret+="\n";
			pCur=pCur.next(); //ָ����һ�ڵ�
		}
		return ret;
	}

}
