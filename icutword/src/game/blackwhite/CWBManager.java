package game.blackwhite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ���̳���Ĺ���������̣߳�����ִ��
 * 
 * @author liujunsong
 * 
 */
public class CWBManager {
	public static int nRow; // ������,������
	public static int nCol; // ������,������
	public static int state[][]; // ���̵�����״̬ 0 �հף�1 ��ɫ 2 ��ɫ
	public static List<CWBGamePoint> pointList; // �������ӵĴ洢�б�

	/**
	 * ��ʼ�����̵�����
	 * 
	 * @param row
	 *            �к�
	 * @param col
	 *            �к�
	 * @param initnum
	 */
	public CWBManager(int row, int col, int initnum) {
		nRow = row;
		nCol = col;
		state = new int[nRow][nCol];
		pointList = new ArrayList<CWBGamePoint>();

		init(initnum);
	}

	/**
	 * ��ʼ���ӽ��������ȥ
	 * 
	 * @param num
	 */
	private void init(int num) {
		int i, j, k;
		Random rand = new Random();

		for (int m = 0; m < num; m++) {
			i = (int) Math.abs(nRow * rand.nextGaussian() / 2); // �к�
			j = (int) Math.abs(nCol * rand.nextGaussian() / 2); // �к�
			k = (m % 2 == 1) ? 1 : 2; // ��ɫ

			if (i >= nRow || j >= nCol || state[i][j] > 0) {
				m--;
				continue;
			}
			System.out.println("" + i + "," + j + "," + k);

			if (state[i][j] == 0) {
				CWBGamePoint point = new CWBGamePoint(nRow, nCol, i, j, k);
				state[i][j] = k; // �趨���
				pointList.add(point);
			}
		}
	}

	private void showInfo() {
		String flag[] = { " ", "*", "#" };
		int i, j;
		for (i = 0; i < nRow; i++) {
			System.out.println();
			System.out.print("|");
			for (j = 0; j < nCol; j++) {

				System.out.print( " "+flag[state[i][j]] + " |");
			}
			
		}
		System.out.println();
	}

	/**
	 * ����ִ��������
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		CWBManager wb = new CWBManager(20, 20, 4);
		System.out.println("��ʼ״̬��");
		wb.showInfo();

		System.out.println("��ʼ����");
		for (int i = 0; i < pointList.size(); i++) {
			CWBGamePoint p = pointList.get(i);
			p.start();
		}

		boolean flag = true;
		for (; flag;) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			flag = false;
			for (int i = 0; i < wb.nRow; i++)
				for (int j = 0; j < wb.nCol; j++) {
					if (wb.state[i][j] == 0) {
						flag = true;
						i = wb.nRow;
						j = wb.nCol;
						break;
					}
				}
			System.out.println("���н����");
			wb.showInfo();

		}
		System.out.println("----------->���н�����");
		System.exit(0);
	}
}
