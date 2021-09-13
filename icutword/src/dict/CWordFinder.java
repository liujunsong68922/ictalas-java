package dict;

/**
 * CWordFinder的目的，是从给定的输入文件中，进行读取计数 <li>判断那些字是连接在一起，并且频繁出现的，计算他们的出现频率 <li>
 * 并输出出来作为生成词库的依据 <li>这个程序是独立的功能
 * 
 * @author liujunsong
 * 
 */
public class CWordFinder {

	public static void main(String args[]) throws Exception {
		String filename = "/home/liujunsong/reddream.txt";
		FindDictionary dict1 = new FindDictionary();
		FindDictionary dict2 = new FindDictionary();
		FindDictionary dict3 = new FindDictionary();
		FindDictionary dict4 = new FindDictionary();

		// run1(dict1,filename,1);
		// run1(dict2,filename,2);
		// run1(dict3,filename,3);
		// run1(dict4,filename,4);
		System.out.println("读取完毕！");
		System.out.println("--------------------------");
		System.out.println("单字高频,总词数：" + dict1.words.size());
		dict1.showAll(0.001);
		System.out.println("--------------------------");
		System.out.println("双字高频,总词数:" + dict2.words.size());
		dict2.showAll(0.00001);
		System.out.println("--------------------------");
		System.out.println("三字高频,总词数:" + dict3.words.size());
		dict3.showAll(0.0004);
		System.out.println("--------------------------");
		System.out.println("四字高频,总词数:" + dict4.words.size());
		dict4.showAll(0.00005);

		// 开始进行人名的查找，根据前面已经找到的单字频数，双字频数，三字频数，四字频数作为原始输入。
		MyFileReader fr = new MyFileReader(filename);
		byte[] buffer;
		int i;
		int ipos1, ipos2, ipos3;
		buffer = fr.readLine();
		while (buffer != null) {
			String sline = new String(buffer, "GB2312"); // 按照GB2312转换成字符串

			// 判断人名出现的概率，按照如下的判断规则：
			// step1:判断同一行中，同时出现姓，名，字，的行数
			if (sline.contains("姓") && sline.contains("名")
					&& sline.contains("字")) {
				ipos1 = sline.indexOf("姓");
				ipos2 = sline.indexOf("名", ipos1);
				ipos3 = sline.indexOf("字", ipos2);
				if (ipos1 > 0 && ipos2 > 0 && ipos3 > 0 && ipos2 < ipos1 + 5
						&& ipos3 < ipos2 + 6)
					System.out.println(sline.substring(ipos1, ipos3 + 4));
			}

			// 却说XXX
			if (sline.contains("却说")) {
				ipos1 = sline.indexOf("却说");
				System.out.println(sline.substring(ipos1, ipos1 + 8));
			}
			
			//和规则的处理
			if (sline.contains("和") && !sline.contains("和尚")) {
				ipos1 = sline.indexOf("和");
				if (ipos1 < sline.length() - 4) {
					System.out.println(sline.substring(ipos1 - 2, ipos1 + 4));
				}
			}
			
			//哭的处理
			if(sline.contains("哭")){
				ipos1=sline.indexOf("哭");
				if(ipos1>3){
					System.out.println(sline.substring(ipos1-3,ipos1+1));
				}
			}
			
			//说道的处理
			if(sline.contains("说道")){
				ipos1=sline.indexOf("说道");
				if(ipos1>3){
					System.out.println(sline.substring(ipos1-3,ipos1+2));
				}
			}
			// 读取下一行
			buffer = fr.readLine();
		}
		
	}

	/**
	 * 计算其中指定长度的出现频率
	 * 
	 * @param filename
	 * @param len
	 * @throws Exception
	 */
	private static void run1(FindDictionary dict, String filename, int len)
			throws Exception {
		if (len <= 0) {
			return;
		}

		MyFileReader fr = new MyFileReader(filename);
		byte[] buffer;
		int i;
		buffer = fr.readLine();
		while (buffer != null) {
			String sline = new String(buffer, "GB2312"); // 按照GB2312转换成字符串

			// step1:将每一个汉字都压入队列中
			for (i = 0; i < sline.length() - len + 1; i++) {
				dict.add(sline.substring(i, i + len));
			}

			buffer = fr.readLine();
		}
	}

}
