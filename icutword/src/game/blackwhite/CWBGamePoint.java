package game.blackwhite;

import java.util.Random;

public class CWBGamePoint extends Thread {
	int nRow, nCol;
	int row, col, value;

	/**
	 * ����һ���½ڵ�
	 * 
	 * @param irow
	 *            �к�
	 * @param icol
	 *            �к�
	 * @param ivalue
	 *            ֵ
	 */
	public CWBGamePoint(int inRow, int inCol, int irow, int icol, int ivalue) {
		nRow = inRow;
		nCol = inCol;

		row = irow;
		col = icol;
		value = ivalue;
	}

	// 5.���ӵļ������
	// 5.1 �����������Χ���������Ӷ���������ɫ�����ӣ���ô�������������߳���ֹ��
	// 5.2 �����������Χ���������Ӷ��Ѿ�ռ�ִֹ꣬ͣ�С�
	// 5.3 �����������Χ�пհ׵㣬��ô���ѡ������һ�������˴�����Ϊͬ����ɫ��������һ�������̡߳�
	/**
	 * �߳�����ִ��
	 */
	@SuppressWarnings("deprecation")
	public void run() {
		int stat[][] = CWBManager.state;
		int i1, i2, i3, i4;
		for (;;) {
			try {
				// System.out.println("Sleeping Thread row,col="+row+","+col);
				this.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("Running Thread row,col="+row+","+col);
			i1 = getUp();
			i2 = getDown();
			i3 = getLeft();
			i4 = getRight();

			// Step1:�ж���Χ���ӵ���ɫ���Ƿ������������
			if (i1 == -1 || (i1 != 0 && i1 != value))
				if (i2 == -1 || (i2 != 0 && i2 != value))
					if (i3 == -1 || (i3 != 0 && i3 != value))
						if (i4 == -1 || (i4 != 0 && i4 != value)) {
							// ������������
							value = 0;
							stat[row][col] = 0; // ����Ϊ��
							System.out.println("STOP Thread....I am Killed."+row+","+col);
							this.stop(); // �߳���ֹ
							break; // ����ѭ��
						}

			// Step2:�ж���Χ�Ƿ��п�λ
			Random rand = new Random();
			int i = (int) Math.abs(1000 * rand.nextGaussian());
			i = i % 4;

			if (i == 0 && i1 == 0) { // �����п�λ
				CWBGamePoint p1 = new CWBGamePoint(nRow, nCol, row - 1, col,
						value);
				stat[row - 1][col] = value;
				p1.start(); // �������߳�
				continue;
			}

			if (i == 1 && i2 == 0) { // �����п�λ
				CWBGamePoint p2 = new CWBGamePoint(nRow, nCol, row + 1, col,
						value);
				stat[row + 1][col] = value;
				p2.start(); // �������߳�
				continue;
			}

			if (i == 2 && i3 == 0) { // �����п�λ
				CWBGamePoint p3 = new CWBGamePoint(nRow, nCol, row, col - 1,
						value);
				stat[row][col - 1] = value;
				p3.start(); // �������߳�
				continue;
			}
			if (i == 3 && i4 == 0) { // �����п�λ
				CWBGamePoint p4 = new CWBGamePoint(nRow, nCol, row, col + 1,
						value);
				stat[row][col + 1] = value;
				p4.start(); // �������߳�
				continue;
			}
			// Step3:��Χ�Ѿ�����,�߳�����
			try {
				// System.out.println("Sleeping Thread row,col="+row+","+col);
				this.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * �Ϸ�λ�õ���ɫ��-1��������
	 * 
	 * @return
	 */
	private int getUp() {
		int i = row - 1;
		int j = col;
		if (i >= 0 && i < nRow && j >= 0 && j < nRow) {
			return CWBManager.state[i][j];
		} else {
			return -1;
		}
	}

	/**
	 * �·����ӵ���ɫ��-1��������
	 * 
	 * @return
	 */
	private int getDown() {
		int i = row + 1;
		int j = col;
		if (i >= 0 && i < nRow && j >= 0 && j < nRow) {
			return CWBManager.state[i][j];
		} else {
			return -1;
		}
	}

	/**
	 * �������ӵ���ɫ��-1��������
	 * 
	 * @return
	 */
	private int getLeft() {
		int i = row;
		int j = col - 1;
		if (i >= 0 && i < nRow && j >= 0 && j < nRow) {
			return CWBManager.state[i][j];
		} else {
			return -1;
		}
	}

	/**
	 * �������ӵ���ɫ��-1��������
	 * 
	 * @return
	 */
	private int getRight() {
		int i = row;
		int j = col + 1;
		if (i >= 0 && i < nRow && j >= 0 && j < nRow) {
			return CWBManager.state[i][j];
		} else {
			return -1;
		}
	}
}
