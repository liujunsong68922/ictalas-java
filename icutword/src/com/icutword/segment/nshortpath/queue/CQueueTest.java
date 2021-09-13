package com.icutword.segment.nshortpath.queue;

import java.util.Random;

import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;

/**
 * CQueue的测试功能类
 * @author liujunsong
 *
 */
public class CQueueTest {
	public static void main(String args[]){
		CQueue queue = new CQueue();
//		int i=0;
//		for(i=0;i<10;i++){
//			System.out.println("push return:"+i+" = "+queue.Push(i, i, i*i));
//		}
//		
//		for(i=0;i<10;i++){
//			System.out.println("push return:"+i+" = "+queue.Push(i, i, i*i));
//		}
//		
//		Pint a = new Pint();
//		Pint b = new Pint();
//		Pdouble d=new Pdouble();
//		while(queue.Pop(a, b, d, true, true)>0){
//			System.out.println("value:"+a.value+" index:"+b.value+" weight:"+d.value);
//		}
		
		int i=0;
		Random r = new Random();
		for(i=0;i<10;i++){
			System.out.println("push return:"+i+" = "+queue.Push(0,1, 1/(i+0.1)));
		}
		
		Pint a = new Pint();
		Pint b = new Pint();
		Pdouble d=new Pdouble();
		while(queue.Pop(a, b, d, true, true)>0){
			System.out.println("value:"+a.value+" index:"+b.value+" weight:"+d.value);
		}
	}
}
