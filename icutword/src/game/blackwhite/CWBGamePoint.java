package game.blackwhite;

import java.util.Random;

public class CWBGamePoint extends Thread {
	int nRow, nCol;
	int row, col, value;

	/**
	 * 生成一个新节点
	 * 
	 * @param irow
	 *            行号
	 * @param icol
	 *            列号
	 * @param ivalue
	 *            值
	 */
	public CWBGamePoint(int inRow, int inCol, int irow, int icol, int ivalue) {
		nRow = inRow;
		nCol = inCol;

		row = irow;
		col = icol;
		value = ivalue;
	}

	// 5.棋子的计算规则：
	// 5.1 如果本棋子周围的所有棋子都是其他颜色的棋子，那么本棋子死亡，线程终止。
	// 5.2 如果本棋子周围的所有棋子都已经占完，停止执行。
	// 5.3 如果本棋子周围有空白点，那么随机选择其中一个，将此处设置为同种颜色，并增加一个并行线程。
	/**
	 * 线程启动执行
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

			// Step1:判断周围棋子的颜色，是否符合死亡条件
			if (i1 == -1 || (i1 != 0 && i1 != value))
				if (i2 == -1 || (i2 != 0 && i2 != value))
					if (i3 == -1 || (i3 != 0 && i3 != value))
						if (i4 == -1 || (i4 != 0 && i4 != value)) {
							// 符合死亡条件
							value = 0;
							stat[row][col] = 0; // 设置为空
							System.out.println("STOP Thread....I am Killed."+row+","+col);
							this.stop(); // 线程终止
							break; // 跳出循环
						}

			// Step2:判断周围是否有空位
			Random rand = new Random();
			int i = (int) Math.abs(1000 * rand.nextGaussian());
			i = i % 4;

			if (i == 0 && i1 == 0) { // 左面有空位
				CWBGamePoint p1 = new CWBGamePoint(nRow, nCol, row - 1, col,
						value);
				stat[row - 1][col] = value;
				p1.start(); // 启动新线程
				continue;
			}

			if (i == 1 && i2 == 0) { // 右面有空位
				CWBGamePoint p2 = new CWBGamePoint(nRow, nCol, row + 1, col,
						value);
				stat[row + 1][col] = value;
				p2.start(); // 启动新线程
				continue;
			}

			if (i == 2 && i3 == 0) { // 上面有空位
				CWBGamePoint p3 = new CWBGamePoint(nRow, nCol, row, col - 1,
						value);
				stat[row][col - 1] = value;
				p3.start(); // 启动新线程
				continue;
			}
			if (i == 3 && i4 == 0) { // 下面有空位
				CWBGamePoint p4 = new CWBGamePoint(nRow, nCol, row, col + 1,
						value);
				stat[row][col + 1] = value;
				p4.start(); // 启动新线程
				continue;
			}
			// Step3:周围已经填满,线程休眠
			try {
				// System.out.println("Sleeping Thread row,col="+row+","+col);
				this.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 上方位置的颜色，-1代表不存在
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
	 * 下方棋子的颜色，-1代表不存在
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
	 * 左面棋子的颜色，-1代表不存在
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
	 * 右面棋子的颜色，-1代表不存在
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
