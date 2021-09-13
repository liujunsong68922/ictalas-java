package dict;

/**
 * CWordFinder��Ŀ�ģ��ǴӸ����������ļ��У����ж�ȡ���� <li>�ж���Щ����������һ�𣬲���Ƶ�����ֵģ��������ǵĳ���Ƶ�� <li>
 * �����������Ϊ���ɴʿ������ <li>��������Ƕ����Ĺ���
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
		System.out.println("��ȡ��ϣ�");
		System.out.println("--------------------------");
		System.out.println("���ָ�Ƶ,�ܴ�����" + dict1.words.size());
		dict1.showAll(0.001);
		System.out.println("--------------------------");
		System.out.println("˫�ָ�Ƶ,�ܴ���:" + dict2.words.size());
		dict2.showAll(0.00001);
		System.out.println("--------------------------");
		System.out.println("���ָ�Ƶ,�ܴ���:" + dict3.words.size());
		dict3.showAll(0.0004);
		System.out.println("--------------------------");
		System.out.println("���ָ�Ƶ,�ܴ���:" + dict4.words.size());
		dict4.showAll(0.00005);

		// ��ʼ���������Ĳ��ң�����ǰ���Ѿ��ҵ��ĵ���Ƶ����˫��Ƶ��������Ƶ��������Ƶ����Ϊԭʼ���롣
		MyFileReader fr = new MyFileReader(filename);
		byte[] buffer;
		int i;
		int ipos1, ipos2, ipos3;
		buffer = fr.readLine();
		while (buffer != null) {
			String sline = new String(buffer, "GB2312"); // ����GB2312ת�����ַ���

			// �ж��������ֵĸ��ʣ��������µ��жϹ���
			// step1:�ж�ͬһ���У�ͬʱ�����գ������֣�������
			if (sline.contains("��") && sline.contains("��")
					&& sline.contains("��")) {
				ipos1 = sline.indexOf("��");
				ipos2 = sline.indexOf("��", ipos1);
				ipos3 = sline.indexOf("��", ipos2);
				if (ipos1 > 0 && ipos2 > 0 && ipos3 > 0 && ipos2 < ipos1 + 5
						&& ipos3 < ipos2 + 6)
					System.out.println(sline.substring(ipos1, ipos3 + 4));
			}

			// ȴ˵XXX
			if (sline.contains("ȴ˵")) {
				ipos1 = sline.indexOf("ȴ˵");
				System.out.println(sline.substring(ipos1, ipos1 + 8));
			}
			
			//�͹���Ĵ���
			if (sline.contains("��") && !sline.contains("����")) {
				ipos1 = sline.indexOf("��");
				if (ipos1 < sline.length() - 4) {
					System.out.println(sline.substring(ipos1 - 2, ipos1 + 4));
				}
			}
			
			//�޵Ĵ���
			if(sline.contains("��")){
				ipos1=sline.indexOf("��");
				if(ipos1>3){
					System.out.println(sline.substring(ipos1-3,ipos1+1));
				}
			}
			
			//˵���Ĵ���
			if(sline.contains("˵��")){
				ipos1=sline.indexOf("˵��");
				if(ipos1>3){
					System.out.println(sline.substring(ipos1-3,ipos1+2));
				}
			}
			// ��ȡ��һ��
			buffer = fr.readLine();
		}
		
	}

	/**
	 * ��������ָ�����ȵĳ���Ƶ��
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
			String sline = new String(buffer, "GB2312"); // ����GB2312ת�����ַ���

			// step1:��ÿһ�����ֶ�ѹ�������
			for (i = 0; i < sline.length() - len + 1; i++) {
				dict.add(sline.substring(i, i + len));
			}

			buffer = fr.readLine();
		}
	}

}
