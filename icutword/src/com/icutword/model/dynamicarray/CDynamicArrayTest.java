package com.icutword.model.dynamicarray;

import java.util.Random;

import com.icutword.model.point.Pdouble;
import com.icutword.model.point.Pint;

public class CDynamicArrayTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CDynamicArrayTest t = new CDynamicArrayTest();
		t.test2();
	}

	public void test1() {
		CDynamicArray da = new CDynamicArray();
		int i, j;
		for (i = 0; i < 10; i++) {
			for (j = 0; j < 10; j++) {
				da.SetElement(i, j, i * j, 0,
						(">>>>oki=" + i + ":j=" + j + "\0").toCharArray());
			}
		}

		for (i = 0; i < 10; i++) {
			for (j = 0; j < 10; j++) {
				da.SetElement(i, j, i * j * 2, 0, (">>>>errori=" + i + ":j="
						+ j + "\0").toCharArray());
			}
		}

		for (i = 0; i < 10; i++) {
			for (j = 0; j < 10; j++) {
				PPARRAY_CHAIN ppRet = new PPARRAY_CHAIN(new PARRAY_CHAIN(
						new ARRAY_CHAIN()));
				System.out.print(">j:" + j + " "
						+ da.GetElement(i, j, null, ppRet));
				System.out.println(">>i=" + i + "j=" + j + "sword="
						+ new String(ppRet.p.p.sWord));
			}
			System.out.println();
		}

//		PPARRAY_CHAIN ppRet = new PPARRAY_CHAIN(new PARRAY_CHAIN(
//				new ARRAY_CHAIN()));
//		int inum = 0;
//		if ((inum = da.GetTail(ppRet)) > 0) {
//			System.out.println("inum:" + inum + "Tail:"
//					+ new String(ppRet.p.p.sWord));
//		}
//
//		ppRet.setValue(da.GetHead());
//		if (ppRet.p != null || ppRet.p.p != null) {
//			System.out.println("Head:" + new String(ppRet.p.p.sWord));
//		}
	}

	private void test2() {
		CDynamicArray da = new CDynamicArray();
		int i, j;
		for (i = 0; i < 10; i++) {
			for (j = 0; j < 10; j++) {
				da.SetElement(i, j, i * j, 0,
						(">oki=" + i + ":j=" + j + "\0").toCharArray());
			}
		}

		// da2=da2.Copy(da);
		Random r = new Random();
		for (i = 0; i < 10; i++) {
			for (j = 0; j < 10; j++) {
				da.SetElement(i, j, i * j* r.nextGaussian(), 0,
						(">oki=" + i + ":j=" + j + "\0").toCharArray());
			}
		}

		CDynamicArray da2= new CDynamicArray();
				da2.Copy(da);
				
				System.out.println(da.equals(da2));
				
				da.Destroy();
				for (i = 0; i < 10; i++) {
					for (j = 0; j < 10; j++) {
						PPARRAY_CHAIN ppRet = new PPARRAY_CHAIN(new PARRAY_CHAIN(
								new ARRAY_CHAIN()));
//						System.out.println(">i,j:"+i+"," + j + " "
//								+ da2.GetElement(i, j, null, ppRet));
						Pdouble p1=new Pdouble();
						Pint p2=new Pint();
						da2.GetElement2(i, j, p1, p2);
						System.out.println(">"+i+"*" + j + " = "+p1.value);
					}
				}	
				System.out.println("Tails:"+da2.GetTail(new PPARRAY_CHAIN(null)));
				da2.Destroy();
				System.out.println("Tails:"+da2.GetTail(new PPARRAY_CHAIN(null)));
				
//		System.out.println(da2.equals(da));
//		da2.SetElement(200, 200, 200, 0, "hello\0".toCharArray());
//		System.out.println(da2.equals(da));
//		
//		PPARRAY_CHAIN ppTailRet = new PPARRAY_CHAIN(new PARRAY_CHAIN(
//				new ARRAY_CHAIN()));		
//		System.out.println(da.GetTail(ppTailRet));
//		System.out.println(da2.GetTail(ppTailRet));
//		
//		da.Destroy();
//		da2.Destroy();
//		
//		System.out.println(da.GetTail(ppTailRet));
//		System.out.println(da2.GetTail(ppTailRet));
	}
}
