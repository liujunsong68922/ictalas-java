package dict;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 通过计算找到的分词结果存储，这些字未来可能是词典
 * 
 * @author liujunsong
 * 
 */
public class FindDictionary {
	// 用一个HashMap来存储发现的词和出现的频率
	public HashMap<String, Integer> words = new HashMap<String, Integer>();
	public int totalfreq = 0;

	/**
	 * 构造函数
	 */
	public FindDictionary() {

	}

	/**
	 * 增加一个新词
	 * 
	 * @param word
	 */
	public void add(String word) {
		if (word.trim().length() == 0) {
			return;
		}

		// 判断是否都是汉字,如果不是，退出
		int i = 0;
		for (i = 0; i < word.length(); i++) {
			if ("　，。：“． ".contains(word.substring(i, i + 1))) {
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
		words.put(word, ival);// 回写到hashMap里面
		totalfreq++; // 总频度加1
	}

	/**
	 * 显示输出
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
