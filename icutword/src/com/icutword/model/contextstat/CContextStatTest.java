package com.icutword.model.contextstat;

/**
 * CContextStat的测试程序，通过这个程序可以测试CContextStat类的正确性
 * 要确保程序的准确性，需要一级一级进行严格测试，避免错误的产生
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
