package com.icutword.model.contextstat;

/**
 * CContextStat�Ĳ��Գ���ͨ�����������Բ���CContextStat�����ȷ��
 * Ҫȷ�������׼ȷ�ԣ���Ҫһ��һ�������ϸ���ԣ��������Ĳ���
 * @author liujunsong
 *
 */
public class CContextStatTest {

	public static void main(String args[]) throws Exception{
		CContextStat stat = new CContextStat();
		stat.Load("Data/nr.ctx\0".toCharArray());
		System.out.println("Load finished...");
		System.out.println(stat.m_nTableLen);

		
		System.out.println("i="+stat.m_pContext.p.aContextArray[14][14]);
		System.out.println("poss:"+stat.GetContextPossibility(0, 1, 2));
		//stat.Save("nr1.ctx\0".toCharArray());
		
		int i,j;
		for(i=0;i<stat.m_nTableLen/2;i++){
			for(j=0;j<stat.m_nTableLen/2;j++){
				System.out.println("poss:"+stat.GetContextPossibility(
						0, stat.m_pSymbolTable[i] , stat.m_pSymbolTable[j]));
			}
			System.out.println("---------");
		}

		for(i=0;i<stat.m_nTableLen;i++){
			System.out.println("frequency["+i+"]:"+stat.GetFrequency(0,stat.m_pSymbolTable[i]));
		}
		
	}
}
