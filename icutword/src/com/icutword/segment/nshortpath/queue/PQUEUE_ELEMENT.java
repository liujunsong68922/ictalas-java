package com.icutword.segment.nshortpath.queue;

//import com.icutword.model.dynamicarray.PARRAY_CHAIN;

//import com.icutword.model.contextstat.PMYCONTEXT;

/**
 * ָ��QUEUE_ELEMENT��ָ�����ģ��,һ��ָ��
 * 
 * @author liujunsong
 * 
 */
public class PQUEUE_ELEMENT {
	public QUEUE_ELEMENT p = null;

	/**
	 * ͨ�ù��캯��
	 * 
	 * @param point
	 */
	public PQUEUE_ELEMENT(QUEUE_ELEMENT point) {
		p = point;
	}

	/**
	 * ��װnext()���������Է���null
	 * 
	 * @return
	 */
	public PQUEUE_ELEMENT next() {
		if (p != null) {
			return p.next;
		} else {
			return null;
		}
	}

	/**
	 * �ӵ�ǰ�ڵ㿪ʼ���ݹ鵽�����¼��ڵ㣬Ȼ���ͷ�����������ڴ� ������������ͷ��ڴ档
	 */
	public void DestroyAll() {
		PQUEUE_ELEMENT pCur = next(), pTemp;
		// �ݹ����������¼��Ľڵ�
		while (pCur != null) {
			pTemp = pCur.next(); // �õ���һ�ڵ�
			pCur.p.Destroy(); // ��ǰ�ڵ��ڴ��ͷ�
			pCur = pTemp; // ָ����һ�ڵ�
		}
		// ������ָ���Ӧ�ڵ�
		if (p != null) {
			p.Destroy();
			p = null;
		}
	}

	public String toString() {
		if (p != null) {
			return p.toString();
		} else {
			return "NULL";
		}
	}

	/**
	 * �����е�����ת����һ���ַ������
	 * 
	 * @return
	 */
	public String AlltoString() {
		String ret = "";
		PQUEUE_ELEMENT pCur = this;
		// �ݹ����������¼��Ľڵ�
		while (pCur != null) {
			ret += pCur.toString();
				ret += "->";
			pCur = pCur.next(); // ָ����һ�ڵ�
		}
		return ret;
	}
}
