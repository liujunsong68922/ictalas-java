package dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * ͨ�������ҵ��ķִʽ���洢����Щ��δ�������Ǵʵ�
 * 
 * @author liujunsong
 * 
 */
public class FindDictionary {
	// ��һ��HashMap���洢���ֵĴʺͳ��ֵ�Ƶ��
	public HashMap<String, Integer> words = new HashMap<String, Integer>();
	public int totalfreq = 0;

	/**
	 * ���캯��
	 */
	public FindDictionary() {

	}

	/**
	 * ����һ���´�
	 * 
	 * @param word
	 */
	public void add(String word) {
		if (word.trim().length() == 0) {
			return;
		}

		// �ж��Ƿ��Ǻ���,������ǣ��˳�
		int i = 0;
		for (i = 0; i < word.length(); i++) {
			if ("������������ ".contains(word.substring(i, i + 1))) {
				return;
			}
			if (word.charAt(i) < 0x2f00) {
				return;
			}
		}

		Integer ival = (Integer) words.get(word);
		if (ival == null) {
			ival = new Integer(1);
		} else {
			ival = new Integer(ival.intValue() + 1);
		}
		words.put(word, ival);// ��д��hashMap����
		totalfreq++; // ��Ƶ�ȼ�1
	}

	/**
	 * ��ʾ���
	 */
	public void showAll(double freq) {
		Set<Map.Entry<String, Integer>> s = words.entrySet();
		for (Map.Entry<String, Integer> me : s) {
			if (me.getValue().intValue() > freq * totalfreq) {
				System.out.println(me.getKey() + ":" + me.getValue());
			}
		}
	}
}
