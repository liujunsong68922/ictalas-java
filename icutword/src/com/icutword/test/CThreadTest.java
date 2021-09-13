package com.icutword.test;

/**
 * �̲߳������Գ���
 * ���Խ��ۣ����������������࣬�����й���ֲ��������������̲߳���ȫ�ġ�
 * @author liujunsong
 * 
 */
public class CThreadTest {

	public static void main(String args[]) {
		Testobj aa = new Testobj();
		int i = 0;

		for (i = 0; i < 500; i++) {
			MyThread t = new MyThread(aa, i);
			try {
				Thread.currentThread().sleep(100);
			} catch (Exception e) {

			}
			t.start();
		}
	}

}

class Testobj {

	public int test(int i) {
		int j = i;
		int k;
		for (k = 0; k < 500; k++) {
			j++;
			try {
				Thread.currentThread().sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (k = 0; k < 500; k++) {
			j--;
			try {
				Thread.currentThread().sleep(1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return j;
	}

}

class MyThread extends Thread {
	Testobj t = null;
	int ivalue = 0;

	public MyThread(Testobj a, int i) {
		t = a;
		ivalue = i;
	}

	public void run() {

		for (;;) {
			try {
				System.out.println("Used Mem:" + getUsedMem() / 1024
						+ "       " + ivalue + ":           "
						+ (t.test(ivalue) - ivalue));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �õ��Ѿ�ʹ�õ��ڴ�
	 * 
	 * @return
	 */
	private static long getUsedMem() {
		return Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
	}
}