package com.icutword.utility;

import java.io.File;
import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.OutputStreamWriter;


public class CUtility extends CStringFunction {
	public static final int CT_SENTENCE_BEGIN = 1;// Sentence begin
	public static final int CT_SENTENCE_END = 4;// Sentence ending
	public static final int CT_SINGLE = 5;// SINGLE byte
	public static final int CT_DELIMITER = CT_SINGLE + 1;// delimiter
	public static final int CT_CHINESE = CT_SINGLE + 2;// Chinese Char
	public static final int CT_LETTER = CT_SINGLE + 3;// HanYu Pinyin
	public static final int CT_NUM = CT_SINGLE + 4;// HanYu Pinyin
	public static final int CT_INDEX = CT_SINGLE + 5;// HanYu Pinyin
	public static final int CT_OTHER = CT_SINGLE + 12;// Other
	public char[] POSTFIX_SINGLE = toGB2312("坝邦堡杯城池村单岛道堤店洞渡队法峰府冈港阁宫沟国海号河湖环集江奖礁角街井郡坑口矿里岭楼路门盟庙弄牌派坡铺旗桥区渠泉人山省市水寺塔台滩坛堂厅亭屯湾文屋溪峡县线乡巷型洋窑营屿语园苑院闸寨站镇州庄族陂庵町");
	public char POSTFIX_MUTIPLE[][] = { toGB2312("半岛"), toGB2312("草原"),
			toGB2312("城市"), toGB2312("大堤"), toGB2312("大公国"), toGB2312("大桥"),
			toGB2312("地区"), toGB2312("帝国"), toGB2312("渡槽"), toGB2312("港口"),
			toGB2312("高速公路"), toGB2312("高原"), toGB2312("公路"), toGB2312("公园"),
			toGB2312("共和国"), toGB2312("谷地"), toGB2312("广场"), toGB2312("国道"),
			toGB2312("海峡"), toGB2312("胡同"), toGB2312("机场"), toGB2312("集镇"),
			toGB2312("教区"), toGB2312("街道"), toGB2312("口岸"), toGB2312("码头"),
			toGB2312("煤矿"), toGB2312("牧场"), toGB2312("农场"), toGB2312("盆地"),
			toGB2312("平原"), toGB2312("丘陵"), toGB2312("群岛"), toGB2312("沙漠"),
			toGB2312("沙洲"), toGB2312("山脉"), toGB2312("山丘"), toGB2312("水库"),
			toGB2312("隧道"), toGB2312("特区"), toGB2312("铁路"), toGB2312("新村"),
			toGB2312("雪峰"), toGB2312("盐场"), toGB2312("盐湖"), toGB2312("渔场"),
			toGB2312("直辖市"), toGB2312("自治区"), toGB2312("自治县"), toGB2312("自治州"),
			toGB2312("") };

	public char[] TRANS_ENGLISH = toGB2312("·—阿埃艾爱安昂敖奥澳笆芭巴白拜班邦保堡鲍北贝本比毕彼别波玻博勃伯泊卜布才采仓查差柴彻川茨慈次达大戴代丹旦但当道德得的登迪狄蒂帝丁东杜敦多额俄厄鄂恩尔伐法范菲芬费佛夫福弗甫噶盖干冈哥戈革葛格各根古瓜哈海罕翰汗汉豪合河赫亨侯呼胡华霍基吉及加贾坚简杰金京久居君喀卡凯坎康考柯科可克肯库奎拉喇莱来兰郎朗劳勒雷累楞黎理李里莉丽历利立力连廉良列烈林隆卢虏鲁路伦仑罗洛玛马买麦迈曼茅茂梅门蒙盟米蜜密敏明摩莫墨默姆木穆那娜纳乃奈南内尼年涅宁纽努诺欧帕潘畔庞培佩彭皮平泼普其契恰强乔切钦沁泉让热荣肉儒瑞若萨塞赛桑瑟森莎沙山善绍舍圣施诗石什史士守斯司丝苏素索塔泰坦汤唐陶特提汀图土吐托陀瓦万王旺威韦维魏温文翁沃乌吾武伍西锡希喜夏相香歇谢辛新牙雅亚彦尧叶依伊衣宜义因音英雍尤于约宰泽增詹珍治中仲朱诸卓孜祖佐伽娅尕腓滕济嘉津赖莲琳律略慕妮聂裴浦奇齐琴茹珊卫欣逊札哲智兹芙汶迦珀琪梵斐胥黛");
	public char[] TRANS_RUSSIAN = toGB2312("·阿安奥巴比彼波布察茨大德得丁杜尔法夫伏甫盖格哈基加坚捷金卡科可克库拉莱兰勒雷里历利连列卢鲁罗洛马梅蒙米姆娜涅宁诺帕泼普奇齐乔切日萨色山申什斯索塔坦特托娃维文乌西希谢亚耶叶依伊以扎佐柴达登蒂戈果海赫华霍吉季津柯理琳玛曼穆纳尼契钦丘桑沙舍泰图瓦万雅卓兹");
	public char[] TRANS_JAPANESE = toGB2312("安奥八白百邦保北倍本比滨博步部彩菜仓昌长朝池赤川船淳次村大代岛稻道德地典渡尔繁饭风福冈高工宫古谷关广桂贵好浩和合河黑横恒宏后户荒绘吉纪佳加见健江介金今进井静敬靖久酒菊俊康可克口梨理里礼栗丽利立凉良林玲铃柳隆鹿麻玛美萌弥敏木纳南男内鸟宁朋片平崎齐千前浅桥琴青清庆秋丘曲泉仁忍日荣若三森纱杉山善上伸神圣石实矢世市室水顺司松泰桃藤天田土万望尾未文武五舞西细夏宪相小孝新星行雄秀雅亚岩杨洋阳遥野也叶一伊衣逸义益樱永由有佑宇羽郁渊元垣原远月悦早造则泽增扎宅章昭沼真政枝知之植智治中忠仲竹助椎子佐阪坂堀荻菅薰浜濑鸠筱");
	
	// Translation type
	public int TT_ENGLISH = 0;
	public int TT_RUSSIAN = 1;
	public int TT_JAPANESE = 2;
	
	// Seperator type
	public String SEPERATOR_C_SENTENCE = "。！？：；…";
	public String SEPERATOR_C_SUB_SENTENCE = "、，（）“”‘’";
	public String SEPERATOR_E_SENTENCE = "!?:;";
	public String SEPERATOR_E_SUB_SENTENCE = ",()*'";
	public String SEPERATOR_LINK = "\n\r 　";
	
	// Sentence begin and ending string
	public String SENTENCE_BEGIN = "始##始";
	public String SENTENCE_END = "末##末";
	
	// Seperator between two words
	public static String WORD_SEGMENTER = "@";

	public enum TAG_TYPE {
		TT_NORMAL, TT_PERSON, TT_PLACE, TT_TRANS_PERSON
	};	
	/*********************************************************************
	 * 
	 * Func Name : GB2312_Generate
	 * 
	 * Description: Generate the GB2312 List file
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output GB2312 List
	 * 
	 * Returns : boolean Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	boolean GB2312_Generate(char[] sFileName) {
		File fp=null;
		int i, j;

		try {
			fp = new File(new String(sFileName));
			OutputStreamWriter fw = new OutputStreamWriter(
					new FileOutputStream(new String(sFileName)), "GB2312");
			for (i = 161; i < 255; i++) {
				for (j = 161; j < 255; j++) {
					byte b[] = new byte[2];
					b[0] = (byte) i;
					b[1] = (byte) j;
					String s = new String(b, "GB2312");
					System.out.print(s);
					fw.write(s);
					fw.write(i);
					fw.write(j);
				}
				System.out.println();
				fw.write("\n\r");
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********************************************************************
	 * 
	 * Func Name : CC_Generate
	 * 
	 * Description: Generate the Chinese Char List file
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output CC List
	 * 
	 * Returns : boolean Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	/**
	 * 生成GB2312的所有文字，写入文件中，文件编码方式GB2312
	 * 
	 * @param sFileName
	 * @return
	 */
	boolean CC_Generate(char[] sFileName) {
		File fp;
		int i, j;

		try {
			fp = new File(new String(sFileName));
			OutputStreamWriter fw = new OutputStreamWriter(
					new FileOutputStream(new String(sFileName)), "GB2312");
			for (i = 176; i < 255; i++) {
				for (j = 161; j < 255; j++) {
					byte b[] = new byte[2];
					b[0] = (byte) i;
					b[1] = (byte) j;
					String s = new String(b, "GB2312");
					System.out.print(s);
					fw.write(s);
					fw.write(i);
					fw.write(j);
				}
				System.out.println();
				fw.write("\n\r");
			}
			fw.flush();
			fw.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/*********************************************************************
	 * 
	 * Func Name : CC_Find
	 * 
	 * Description: Find a Chinese sub-string in the Chinese String
	 * 
	 * 
	 * Parameters : string:Null-terminated string to search
	 * 
	 * strCharSet:Null-terminated string to search for
	 * 
	 * Returns : char * Author : Kevin Zhang History : 1.create 2002-1-8
	 *********************************************************************/
	
	/**
	 * 从第一个字符串里面，找到第二个字符串，返回剩余部分，作为一个char[]
	 * 
	 * @param string
	 * @param strCharSet
	 * @return
	 */
	public static char[] CC_Find(char[] string, char[] strCharSet,int srcpos) {
		char[] src1=new char[strlen(strCharSet)-srcpos+1];
		int i=srcpos;
		while(strCharSet[i]!='\0'){
			src1[i-srcpos]=strCharSet[i];
			i++;
		}
		src1[i-srcpos]='\0';
		
		char[] cp = CStringFunction.strstr(string, src1); // 计算剩余的字符串
		// 由于是中文切分，必须考虑在查找的过程中出现半个汉字错误的情况
		if (cp != null && ((strlen(string) - strlen(cp)) % 2 == 1)) {
			return null;
		}
		return cp;
	}
	/**
	 * 从第一个字符串里面，找到第二个字符串，返回剩余部分，作为一个char[]
	 * 
	 * @param string
	 * @param strCharSet
	 * @return
	 */
	public static char[] CC_Find(char[] string, char[] strCharSet) {
		if(strlen(strCharSet)==0){
			char c1[] = new char[1];
			c1[0]=0;
			return c1;
		}
		
		char[] cp = CStringFunction.strstr(string, strCharSet); // 计算剩余的字符串
		// 由于是中文切分，必须考虑在查找的过程中出现半个汉字错误的情况
		if (cp != null && ((strlen(string) - strlen(cp)) % 2 == 1)) {
			return null;
		}
		return cp;
	}

	/**
	 * 另一个寻找剩余的方法，第一个参数换成了String
	 * 
	 * @param str
	 * @param strCharSet
	 * @return
	 */
	private static char[] CC_Find(String str, char[] strCharSet) {
		return CC_Find(str.toCharArray(), strCharSet);
	}

	/*********************************************************************
	 * 
	 * Func Name : charType
	 * 
	 * Description: Judge the type of sChar or (sChar,sChar+1)
	 * 
	 * 
	 * Parameters : sFilename: the file name for the output CC List
	 * 
	 * Returns : int : the type of char Author : Kevin Zhang History : 1.create
	 * 2002-1-8
		 *********************************************************************/
	public static int charType(char[] sChar,int ipos) {
		char c[]=new char[strlen(sChar)-ipos+1];
		int i;
		for(i=0;sChar[i+ipos]!='\0';i++){
			c[i]=sChar[i+ipos];
		}
		c[i]='\0';
		return charType(c);
	}
	
	//将一个char转换为无符号的byte类型(0-255)
	//高位丢弃掉，用于数据比较，和原来的C逻辑兼容
	private static int toUnsigByte(char c){
		byte b = (byte)c;
		return (b>=0?b:b+256);
	}
	/**
	 * 判断一个给定字符串的词性
	 * 这里判断的时候，由于char是16位，占据了2个byte,比较时需要将其压缩到byte来比较
	 * 因为Java里面没有无符号整数的概念，整个计算需要重新考虑其逻辑
	 * @param sChar
	 * @return
	 */
	public static int charType(char[] sChar) {
		if (toUnsigByte(sChar[0]) < 128) {
			if (strchr("*!,.?()[]{}+=", (int) sChar[0]) >= 0)
				return CT_DELIMITER; //分割符号
			return CT_SINGLE; //单字节词
		} else if (toUnsigByte(sChar[0]) == 162)
			return CT_INDEX;
		else if (toUnsigByte(sChar[0]) == 163 && (toUnsigByte(sChar[1])) > 175 && (toUnsigByte(sChar[1])) < 186)
			return CT_NUM;
		else if (toUnsigByte(sChar[0]) == 163
				&& (toUnsigByte(sChar[1]) >= 193 && toUnsigByte(sChar[1]) <= 218 || toUnsigByte(sChar[1]) >= 225
						&& toUnsigByte(sChar[1]) <= 250))
			return CT_LETTER;
		else if (toUnsigByte(sChar[0]) == 161 || toUnsigByte(sChar[0]) == 163)
			return CT_DELIMITER;
		else if (toUnsigByte(sChar[0]) >= 176 && toUnsigByte(sChar[0]) <= 247)
			return CT_CHINESE;
		else
			return CT_OTHER;
	}


	/*********************************************************************
	 * 
	 * Func Name : GetCCPrefix
	 * 
	 * Description: Get the max Prefix string made up of Chinese Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-8
	 *********************************************************************/
	/**
	 * 得到一个字符串数组里面最大的中文前缀所在
	 * 
	 * @param sSentence
	 * @return
	 */
	int GetCCPrefix(char[] sSentence) {
		int nLen = strlen(sSentence), nCurPos = 0;
		while (nCurPos < nLen && sSentence[nCurPos] > 175
				&& sSentence[nCurPos] < 248) {
			nCurPos += 2;// Get next Chinese Char
		}
		return nCurPos;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllSingleByte
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * 判断字符串是否全是中文
	 * 
	 * @param sString
	 * @return
	 */
	public static boolean IsAllChinese(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && sString[i] < 248 && sString[i] > 175) {
			i += 2;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllNonChinese
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * 是否全部不是中文
	 * 
	 * @param sString
	 * @return
	 */
	boolean IsAllNonChinese(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen) {
			if (sString[i] < 248 && sString[i] > 175)
				return false;
			if (sString[i] > 128)
				i += 2;
			else
				i += 1;
		}
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllSingleByte
	 * 
	 * Description: Judge the string is all made up of Single Byte Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * 都是单字节的byte吗？
	 * @param sString
	 * @return
	 */
	public static boolean IsAllSingleByte(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen && sString[i] < 128) {
			i++;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllNum
	 * 
	 * Description: Judge the string is all made up of Num Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	/**
	 * 所有的都是数字吗？
	 * @param sString
	 * @return
	 */
	public static boolean IsAllNum(char[] sString) {

		int nLen = strlen(sString), i = 0;
		char[] sChar = new char[3];
		sChar[2] = 0;
		if (i < nLen)// Get prefix such as + -
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (strstr(toGB2312("±+—-＋"), sChar) != null) {
				i = 0;
			}
		}
		while (i < nLen - 1 && sString[i] == 163 && sString[i + 1] > 175
				&& sString[i + 1] < 186) {
			i += 2;
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find("∶·．／\0", sChar) != null || sChar[0] == '.'
					|| sChar[0] == '/') {// 98．1％
				while (i < nLen - 1 && sString[i] == 163
						&& sString[i + 1] > 175 && sString[i + 1] < 186) {
					i += 2;
				}
			} else {
				i -= strlen(sChar);
			}
		}

		if (i >= nLen)
			return true;
		while (i < nLen && sString[i] > '0' - 1 && sString[i] < '9' + 1) {// single
																			// byte
																			// number
																			// char
			i += 1;
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find("∶·．／\0", sChar) != null || sChar[0] == '.'
					|| sChar[0] == '/') {// 98．1％
				while (i < nLen && sString[i] > '0' - 1 && sString[i] < '9' + 1) {
					i += 1;
				}
			} else {
				i -= strlen(sChar);
			}
		}
		if (i < nLen)// Get middle delimiter such as .
		{
			sChar[0] = sString[i++];
			if (sChar[0] < 0)// Get first char
				sChar[1] = sString[i++];
			else
				sChar[1] = 0;
			if (CC_Find(toGB2312("百千万亿佰仟％‰"), sChar) == null && sChar[0] != '%')
				i -= strlen(sChar);
		}
		if (i >= nLen)
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllIndex
	 * 
	 * Description: Judge the string is all made up of Index Num Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	boolean IsAllIndex(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && sString[i] == 162) {
			i += 2;
		}
		if (i >= nLen)
			return true;
		while (i < nLen && (sString[i] > 'A' - 1 && sString[i] < 'Z' + 1)
				|| (sString[i] > 'a' - 1 && sString[i] < 'z' + 1)) {// single
																	// byte
																	// number
																	// char
			i += 1;
		}

		if (i < nLen)
			return false;
		return true;

	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllLetter
	 * 
	 * Description: Judge the string is all made up of Letter Char
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	public boolean IsAllLetter(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1
				&& sString[i] == 163
				&& ((sString[i + 1] >= 193 && sString[i + 1] <= 218) || (sString[i + 1] >= 225 && sString[i + 1] <= 250))) {
			i += 2;
		}
		if (i < nLen)
			return false;

		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllDelimiter
	 * 
	 * Description: Judge the string is all made up of Delimiter
	 * 
	 * 
	 * Parameters : sSentence: the original sentence which includes Chinese or
	 * Non-Chinese char
	 * 
	 * Returns : the end of the sub-sentence Author : Kevin Zhang History :
	 * 1.create 2002-1-24
	 *********************************************************************/
	boolean IsAllDelimiter(char[] sString) {
		int nLen = strlen(sString), i = 0;
		while (i < nLen - 1 && (sString[i] == 161 || sString[i] == 163)) {
			i += 2;
		}
		if (i < nLen)
			return false;
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : BinarySearch
	 * 
	 * Description: Lookup the index of nVal in the table nTable which length is
	 * nTableLen
	 * 
	 * Parameters : nPOS: the POS value
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-25
	 *********************************************************************/
	/**
	 * 在一个整数数组里面，采用二分法来查找其中一个变量的下标
	 * @param nVal 要查找的值
	 * @param nTable 整数数组
	 * @param nTableLen 数组最大长度
	 * @return
	 */
	public static int BinarySearch(int nVal, int[] nTable, int nTableLen) {
		int nStart = 0, nEnd = nTableLen - 1, nMid = (nStart + nEnd) / 2;
		while (nStart <= nEnd)// Binary search
		{
			if (nTable[nMid] == nVal) {
				return nMid;// find it
			} else if (nTable[nMid] < nVal) {
				nStart = nMid + 1;
			} else {
				nEnd = nMid - 1;
			}
			nMid = (nStart + nEnd) / 2;
		}
		return -1;// Can not find it;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsForeign
	 * 
	 * Description: Decide whether the word is not a Non-fereign word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-26
	 *********************************************************************/
	/**
	 * 是外国人名翻译吗？
	 * @param sWord
	 * @return
	 */
	boolean IsForeign(char[] sWord) {
		int nForeignCount = GetForeignCharCount(sWord), nCharCount = strlen(sWord);
		if (nCharCount > 2 || nForeignCount >= 1 * nCharCount / 2)
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsAllForeign
	 * 
	 * Description: Decide whether the word is not a Non-fereign word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-3-25
	 *********************************************************************/
	boolean IsAllForeign(char[] sWord) {
		int nForeignCount = (int) GetForeignCharCount(sWord);
		if (2 * nForeignCount == strlen(sWord))
			return true;
		return false;
	}

	/*********************************************************************
	 * 
	 * Func Name : IsForeign
	 * 
	 * Description: Decide whether the word is Chinese Num word
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-1-26
	 *********************************************************************/
	public boolean IsAllChineseNum(char[] sWord) {// 百分之五点六的人早上八点十八分起床
		int k;
		char tchar[] = new char[3];
		char ChineseNum[] = toGB2312("零○一二两三四五六七八九十廿百千万亿壹贰叁肆伍陆柒捌玖拾佰仟∶·．／点");//
		char sPrefix[] = toGB2312("几数第上成");
		for (k = 0; k < strlen(sWord); k += 2) {
			strncpy(tchar, sWord, k, 2);
			tchar[2] = '\0';
			if (strncmp(sWord, k, toGB2312("分之"), 4) == 0)// 百分之五
			{
				k += 2;
				continue;
			}

			if (CC_Find(ChineseNum, tchar) == null
					&& !(k == 0 && CC_Find(sPrefix, tchar) != null))
				return false;
		}
		return true;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetForeignCharCount
	 * 
	 * Description:
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-4-4 2.Modify 2002-5-21
	 *********************************************************************/
	public int GetForeignCharCount(char[] sWord) {
		int nForeignCount, nCount;
		nForeignCount = GetCharCount(TRANS_ENGLISH, sWord);// English char
															// counnts
		nCount = GetCharCount(TRANS_JAPANESE, sWord);// Japan char counnts
		if (nForeignCount <= nCount)
			nForeignCount = nCount;
		nCount = GetCharCount(TRANS_RUSSIAN, sWord);// Russian char counnts
		if (nForeignCount <= nCount)
			nForeignCount = nCount;
		return nForeignCount;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetCharCount
	 * 
	 * Description: Get the count of char which is in sWord and in sCharSet
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : COUNT Author : Kevin Zhang History : 1.create 2002-5-21
	 *********************************************************************/
	public static int GetCharCount(char[] sCharSet, char[] sWord) {
		int k = 0;
		char tchar[] = new char[3];
		int nCount = 0;
		tchar[2] = 0;
		while (k < strlen(sWord)) {
			tchar[0] = sWord[k];
			tchar[1] = 0;
			if (sWord[k] < 0) {
				tchar[1] = sWord[k + 1];
				k += 1;
			}
			k += 1;
			if ((tchar[0] < 0 && CC_Find(sCharSet, tchar) != null)
					|| strchr(sCharSet, tchar[0]) >= 0)
				nCount++;
		}
		return nCount;
	}

	/*********************************************************************
	 * 
	 * Func Name : GetForeignCharCount
	 * 
	 * Description: Return the foreign type
	 * 
	 * Parameters : sWord: the word
	 * 
	 * Returns : the index value Author : Kevin Zhang History : 1.create
	 * 2002-4-4 2.Modify 2002-5-21
	 *********************************************************************/
	int GetForeignType(char[] sWord) {
		int nForeignCount, nCount, nType = TT_ENGLISH;
		nForeignCount = GetCharCount(TRANS_ENGLISH, sWord);// English char
															// counnts
		nCount = GetCharCount(TRANS_RUSSIAN, sWord);// Russian char counnts
		if (nForeignCount < nCount) {
			nForeignCount = nCount;
			nType = TT_RUSSIAN;
		}
		nCount = GetCharCount(TRANS_JAPANESE, sWord);// Japan char counnts
		if (nForeignCount < nCount) {
			nForeignCount = nCount;
			nType = TT_JAPANESE;
		}
		return nType;
	}

	/**
	 * 
	 * @param sWord
	 * @param sWordRet
	 * @param sPostfix
	 * @return
	 */
	boolean PostfixSplit(char[] sWord, char[] sWordRet, char[] sPostfix) {
		char sSinglePostfix[] = POSTFIX_SINGLE;
		char sMultiPostfix[][] = POSTFIX_MUTIPLE;
		int nPostfixLen = 0, nWordLen = strlen(sWord);
		int i = 0;

		while (sMultiPostfix[i][0] != 0
				&& strncmp(sWord, nWordLen - strlen(sMultiPostfix[i]),
						sMultiPostfix[i], strlen(sMultiPostfix[i])) != 0) {
			// Try to get the postfix of an address
			i++;
		}
		strcpy(sPostfix, sMultiPostfix[i]);
		nPostfixLen = strlen(sMultiPostfix[i]);// Get the length of place
												// postfix

		if (nPostfixLen == 0) {
			sPostfix[2] = 0;
			strncpy(sPostfix, sWord, nWordLen - 2, 2);
			if (CC_Find(sSinglePostfix, sPostfix) != null)
				nPostfixLen = 2;
		}

		strncpy(sWordRet, sWord, nWordLen - nPostfixLen);
		sWordRet[nWordLen - nPostfixLen] = 0;// Get the place name which have
												// erasing the postfix
		sPostfix[nPostfixLen] = 0;
		return true;

	}


	
	public static int atoi(char[] c){
		String s=new String(c);
		return Integer.parseInt(s);
	}
	
	public static double log(double d){
		return Math.log(d);
	}

}
