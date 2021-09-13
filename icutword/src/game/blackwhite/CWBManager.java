package game.blackwhite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 棋盘程序的管理调度主线程，单个执行
 * 
 * @author liujunsong
 * 
 */
public class CWBManager {
	public static int nRow; // 总行数,棋盘行
	public static int nCol; // 总列数,棋盘列
	public static int state[][]; // 棋盘的棋子状态 0 空白，1 黑色 2 白色
	public static List<CWBGamePoint> pointList; // 所有棋子的存储列表

	/**
	 * 初始化棋盘的数据
	 * 
	 * @param row
	 *            行号
	 * @param col
	 *            列号
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
	 * 初始化扔进几个点进去
	 * 
	 * @param num
	 */
	private void init(int num) {
		int i, j, k;
		Random rand = new Random();

		for (int m = 0; m < num; m++) {
			i = (int) Math.abs(nRow * rand.nextGaussian() / 2); // 行号
			j = (int) Math.abs(nCol * rand.nextGaussian() / 2); // 列号
			k = (m % 2 == 1) ? 1 : 2; // 颜色

			if (i >= nRow || j >= nCol || state[i][j] > 0) {
				m--;
				continue;
			}
			System.out.println("" + i + "," + j + "," + k);

			if (state[i][j] == 0) {
				CWBGamePoint point = new CWBGamePoint(nRow, nCol, i, j, k);
				state[i][j] = k; // 设定结果
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
	 * 程序执行主方法
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		CWBManager wb = new CWBManager(20, 20, 4);
		System.out.println("开始状态：");
		wb.showInfo();

		System.out.println("开始运行");
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
			System.out.println("运行结果：");
			wb.showInfo();

		}
		System.out.println("----------->运行结束：");
		System.exit(0);
	}
}
