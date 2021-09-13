package com.icutword.result;

import org.apache.log4j.Logger;

import com.icutword.model.dictionary.CDictionary;
import com.icutword.model.dictionary.PWORD_RESULT;
import com.icutword.unknown.span.CSpan;
//import com.icutword.utility.CStringFunction;
import com.icutword.utility.CUtility;

/**
 * 从原来的CResult代码里面切分出来的相对独立的功能代码
 * @author liujunsong
 *
 */
public class CResultFunction  extends CUtility{
	static Logger logger = Logger.getLogger("logger");
	
	static int WORD_MAXLENGTH = 100;
	/**
	 * 文件处理功能
	 * @param sSourceFile 输入源文件
	 * @param sResultFile 输出结果文件
	 * @return
	 */
//	private boolean _FileProcessing(char sSourceFile[],char sResultFile[])
//	{
//		FILE *fpSource,*fpResult;//The file pointer of read and write
//		char *sParagraph,*sParagraphResult;
//		int nLineIndex=1;
//		sParagraph=new char[4*1024];
//		sParagraphResult=new char[8*1024];
//	    if((fpSource=fopen(sSourceFile,"rt"))==NULL)
//			return false;//Cannot open the source file to read
//	    if((fpResult=fopen(sResultFile,"wt"))==NULL) 
//			return false;//Cannot open the result  file to write
//		if(m_nOutputFormat==2)//XML format
//			fprintf(fpResult,"<?xml version=\042 1.0\042 encoding=\042gb2312\042?><result>");
//		while(!feof(fpSource))
//		{
//			if(fgets(sParagraph,4*1024,fpSource)==0)//Get a paragrah
//				continue;
//	#ifndef unix
//			TRACE("%d\n",nLineIndex++);
//	#endif
//			ParagraphProcessing(sParagraph,sParagraphResult);
//			fprintf(fpResult,"%s",sParagraphResult);
//		}
//		delete [] sParagraph;
//		delete [] sParagraphResult;
//		fclose(fpSource);	
//		if(m_nOutputFormat==2)//XML format
//			fprintf(fpResult,"</result>");
//		fclose(fpResult);
//		return true;
//	}	
	
//	private boolean _ChineseNameSplit(char sPersonName[], char sSurname[], char sSurname2[], char sGivenName[], CDictionary personDict)
//	{
//		int nSurNameLen=4,nLen=strlen(sPersonName),nFreq,i=0,nCharType,nFreqGiven;
//		char sTemp[]=new char[3];
//		if(nLen<3||nLen>8)//Not a traditional Chinese person name
//			return false;
//		while(i<nLen)//No Including non-CHinese char
//		{
//			nCharType=charType(sPersonName,i);
//			if(nCharType!=CT_CHINESE&&nCharType!=CT_OTHER)
//				return false;
//			i+=2;
//		}
//		sSurname2[0]=0;//init 
//		strncpy(sSurname,sPersonName,nSurNameLen);	
//		sSurname[nSurNameLen]=0;
//		if(!personDict.IsExist(sSurname,1))
//		{
//			nSurNameLen=2;
//			sSurname[nSurNameLen]=0;
//			if(!personDict.IsExist(sSurname,1))
//			{
//				nSurNameLen=0;
//				sSurname[nSurNameLen]=0;
//			}
//		}
//		strcpy(sGivenName,sPersonName,nSurNameLen);
//		if(nLen>6)
//		{
//			strncpy(sTemp,sPersonName,nSurNameLen,2);
//			sTemp[2]=0;//Get the second possible surname
//			if(personDict.IsExist(sTemp,1))
//			{//Hongkong women's name: Surname+surname+given name
//				strcpy(sSurname2,sTemp);
//				strcpy(sGivenName,sPersonName,nSurNameLen+2);
//			}
//		}
//		nFreq=personDict.GetFrequency(sSurname,1);
//		strncpy(sTemp,sGivenName,2);
//		sTemp[2]=0;
//		nFreqGiven=personDict.GetFrequency(sTemp,2);
//		if(nSurNameLen!=4&&((nSurNameLen==0&&nLen>4)||strlen(sGivenName)>4||(GetForeignCharCount(sPersonName)>=3&&nFreq<personDict.GetFrequency(toGB2312("张"),1)/40&&nFreqGiven<personDict.GetFrequency(toGB2312("华"),2)/20)||(nFreq<10&&GetForeignCharCount(sGivenName)==(nLen-nSurNameLen)/2)))
//			return false;
//		
//		return true;
//	}
	

	/**
	 * 按照词性ID来检索词性表示
	 * @param nHandle
	 * @param sPOS973
	 * @return
	 */
	public static boolean PKU2973POS(int nHandle, char sPOS973[])
	{
		int nHandleSet[]={24832,24932,24935,24942,25088,25344,25600,25703,25856,26112,26368,26624,26880,27136,27392,27648,27904,28160,28263,28274,28275,28276,28280,28282,28416,28672,28928,29184,29440,29696,29799,29952,30052,30055,30058,30060,30070,30074,30208,30308,30311,30318,30464,30720,30976,31232};
							//   "a", "ad","ag","an","b", "c", "d", "dg","e", "f","g", "h", "i", "j", "k", "l", "m", "n", "ng","nr","ns","nt","nx","nz","o", "p", "q", "r", "s", "t", "tg","u", "ud","ug","uj","ul","uv","uz","v", "vd","vg","vn","w", "x", "y", "z"
		char sPOSRelated[][]=new char[46][3];
		String s1[]={"a", "ad","ga","an","f", "c", "d", "d", "e","nd","g", "h", "i", "j", "k", "l", "m", "n", "gn","nh","ns","ni","ws", "nz","o", "p", "q", "r", "nl","nt","gt","u", "ud","ug","uj","ul","uv","uz","v", "vd","gv","vn","w", "x", "u", "a"};
		for(int i=0;i<46;i++){
			strcpy(sPOSRelated[i],toGB2312(s1[i]));
		}
	/* 
	 "Bg","gf",
	 "Rg","gr",
	 "Mg","gm",
	 "Yg","u",
	 "Ug","u",
	 "Qg","q",
	*/

		int nIndex=BinarySearch(nHandle,nHandleSet,46);
		if(nIndex==-1)
			strcpy(sPOS973,toGB2312("@"));
		else
			strcpy(sPOS973,sPOSRelated[nIndex]);
		return true;
	}
	
//	public static boolean Output(PWORD_RESULT pItem[], char[] sResult,int m_nOutputFormat,int m_nOperateType) {
//		return Output(pItem, sResult,m_nOutputFormat,m_nOperateType, false);
//	}	
	
	/**
	 * 将一个PWORD_RESULT[]里面的数据，输出到一个sResult[]里面去
	 * @param pItem 输入，要输出的原始数据
	 * @param sResult 输出字符串数组
	 * @param m_nOutputFormat 输出格式
	 * @param m_nOperateType 操作类型
	 * @param bFirstWordIgnore 首字是否忽略true忽略false不忽略
	 * @return
	 */
	public static boolean Output(PWORD_RESULT pItem[], char[] sResult,
			int m_nOutputFormat,int m_nOperateType,
			boolean bFirstWordIgnore) {
		//TODO:此函数存在严重bug，没有正确返回
		int i = 0;
		int j=0;
		char sTempBuffer[] = new char[WORD_MAXLENGTH], sPOS[] = new char[3];
		sPOS[2] = 0;
		sResult[0] = 0;

		if (bFirstWordIgnore)// Ignore first valid
			i = 1;
		while (pItem[i].p.sWord[0] != 0
				&& pItem[i].p.nHandle != CT_SENTENCE_END)// Not sentence ending
															// flag
		{
			// Get the POS string
			if (m_nOutputFormat != 0)// Not PKU format
				CResultFunction.PKU2973POS(pItem[i].p.nHandle, sPOS);
			else// PKU format
			{
				sPOS[0] = (char) (pItem[i].p.nHandle / 256);
				sPOS[1] = (char) (pItem[i].p.nHandle % 256);
			}
			sPOS[m_nOperateType] = 0;// Set the sPOS with operate type

			if (m_nOutputFormat == 0)// PKU format
			{
				//sprintf(sTempBuffer, "%s", pItem[i].p.sWord);
				for(j=0;pItem[i].p.sWord[j]!=0;j++){
					sTempBuffer[j]=pItem[i].p.sWord[j];
				}
				sTempBuffer[j]=0;
				
				strcat(sResult, sTempBuffer);
				if (sPOS[0] != 0)// need POS
				{
//					sprintf(sTempBuffer, "/%s", sPOS);
					sTempBuffer[0]='/';
					sTempBuffer[1]=0;
					strcat(sTempBuffer,sPOS);
					
					strcat(sResult, sTempBuffer);
				}
				strcat(sResult, toGB2312("  "));
			} else if (m_nOutputFormat == 1)// 973 format
			{
				//sprintf(sTempBuffer, "%s\\", pItem[i].p.sWord);
				for(j=0;pItem[i].p.sWord[j]!=0;j++){
					sTempBuffer[j]=pItem[i].p.sWord[j];
				}
				sTempBuffer[j]='\\';;
				j++;
				sTempBuffer[j]=0;
				strcat(sResult, sTempBuffer);
				if (sPOS[0] != 0)// need POS
				{
					//sprintf(sTempBuffer, "[%s]", sPOS);
					sTempBuffer[0]='[';
					for(j=0;pItem[i].p.sWord[j]!=0;j++){
						sTempBuffer[j+1]=pItem[j].p.sWord[i];
					}
					sTempBuffer[j+1]=']';;
					j++;
					sTempBuffer[j+1]=0;
					strcat(sResult, sTempBuffer);
				}
			} else if (m_nOutputFormat == 2)// XML format
			{
				if (sPOS[0] != 0)// POS
				{
					//sprintf(sTempBuffer, "<any type=\042%s\042>", sPOS);
					strcat(sTempBuffer,"<any type=*\0".toCharArray());
					for(j=0;sPOS[j]!=0;j++){
						sTempBuffer[j+11]=sPOS[j];
					}
					sTempBuffer[j+11]='*';
					sTempBuffer[j+12]='>';
					sTempBuffer[j+13]='"';
					sTempBuffer[j+14]=0;
					strcat(sResult, sTempBuffer);
				}
				//sprintf(sTempBuffer, "<src>%s</src>", pItem[i].p.sWord);
				strcat(sTempBuffer,"<src>\0".toCharArray());
				for(j=0; pItem[i].p.sWord[j]!=0;j++){
					sTempBuffer[j+11]= pItem[i].p.sWord[j];
				}
				sTempBuffer[j+11]='<';
				sTempBuffer[j+12]='s';
				sTempBuffer[j+13]='r';
				sTempBuffer[j+14]='c';
				sTempBuffer[j+15]='>';
				sTempBuffer[j+16]=0;
				
				strcat(sResult, sTempBuffer);
				if (sPOS[0] != 0) {
					strcat(sResult, toGB2312("</any>"));
				}
			}
			i++;
		}
		return true;
	}	
	

	/**
	 * 计算可能性大小
	 * @param pItem 代表分词结果的数组，本身是一个二维数组的一部分
	 * @param m_POSTagger 分词计算器，通过加载词性字典来实现
	 * @return
	 */
	//Compute the possibility of current segmentation and POS result
	public static double ComputePossibility(PWORD_RESULT pItem[],CSpan m_POSTagger)
	{
		int i=0;
		double dResultPossibility=0;
		while(pItem[i].p.sWord[0]!=0)
		{
			dResultPossibility+=pItem[i].p.dValue;
			//Compute the possibility of logP(Wi|Ti)
			if(pItem[i+1].p.sWord[0]!=0)//Not the last one
			{//Compute the possibility of logP(Ti|Ti-1)
				dResultPossibility+=log((double)(m_POSTagger.m_context.GetContextPossibility(0,pItem[i].p.nHandle,pItem[i+1].p.nHandle)+1));
				dResultPossibility-=log((double)(m_POSTagger.m_context.GetFrequency(0,pItem[i].p.nHandle)+1));
			}
			i++;
		}
		return dResultPossibility;
	}	
	
	// Adjust the result with some rules
	/**
	 * 使用一些特定规则来调整输出,这部分规则是固化的，而不是动态的
	 * @param pItemIn 输入的PWORD_RESULT[]
	 * @param pItemRetOut 输出的PWORD_RESULT[]
	 * @return
	 */
	public static boolean Adjust(PWORD_RESULT pItemIn[], PWORD_RESULT pItemRetOut[]) {
		int i = 0, j = 0;
		int nLen;
		char sSurName[] = new char[10], sSurName2[] = new char[10], sGivenName[] = new char[10];
		boolean bProcessed = false;// Have been processed
		while (pItemIn[i].p.sWord[0] != 0) {
			nLen = strlen(pItemIn[i].p.sWord);
			bProcessed = false;

			// Rule1: adjust person name
			// if(pItem[i].p.nHandle==28274&&ChineseNameSplit(pItem[i].p.sWord,sSurName,sSurName2,sGivenName,m_uPerson.m_dict)&&strcmp(pItem[i].p.sWord,toGB2312("叶利钦"))!=0)//'nr'
			// TODO:暂时停止使用人名的处理,全部改成false.以后再改回来
			if (false) {// Divide name into surname and given name

				if (sSurName[0] != 0) {
					strcpy(pItemRetOut[j].p.sWord, sSurName);
					pItemRetOut[j++].p.nHandle = 28274;
				}
				if (sSurName2[0] != 0) {
					strcpy(pItemRetOut[j].p.sWord, sSurName2);
					pItemRetOut[j++].p.nHandle = 28274;
				}
				if (sGivenName[0] != 0) {
					strcpy(pItemRetOut[j].p.sWord, sGivenName);
					pItemRetOut[j++].p.nHandle = 28274;
				}
				bProcessed = true;
			}
			// Rule2 for overlap words ABB 一段段、一片片
			else if (pItemIn[i].p.nHandle == 27904
					&& strlen(pItemIn[i + 1].p.sWord) == 2
					&& strcmp(pItemIn[i + 1].p.sWord, pItemIn[i + 2].p.sWord) == 0) {// (pItem[i+1].p.nHandle/256=='q'||pItem[i+1].p.nHandle/256=='a')&&
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 2].p.sWord);
				pItemRetOut[j].p.nHandle = 27904;
				j += 1;
				i += 2;
				bProcessed = true;
			}
			// Rule3 for overlap words AA
			else if (nLen == 2
					&& strcmp(pItemIn[i].p.sWord, pItemIn[i + 1].p.sWord) == 0) {
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
				// 24832=='a'*256
				pItemRetOut[j].p.nHandle = 24832;// a
				if (pItemIn[i].p.nHandle / 256 == 'v'
						|| pItemIn[i + 1].p.nHandle / 256 == 'v')// 30208='v'8256
				{
					pItemRetOut[j].p.nHandle = 30208;
				}
				if (pItemIn[i].p.nHandle / 256 == 'n'
						|| pItemIn[i + 1].p.nHandle / 256 == 'n')// 30208='v'8256
				{
					pItemRetOut[j].p.nHandle = 'n' * 256;
				}
				i += 1;
				if (strlen(pItemIn[i + 1].p.sWord) == 2) {// AAB:洗/洗/脸、蒙蒙亮
					if ((pItemRetOut[j].p.nHandle == 30208 && pItemIn[i + 1].p.nHandle / 256 == 'n')
							|| (pItemRetOut[j].p.nHandle == 24832 && pItemIn[i + 1].p.nHandle / 256 == 'a')) {
						strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
						i += 1;
					}
				}
				j += 1;
				bProcessed = true;
			}

			// Rule 4: AAB 洗/洗澡
			else if (nLen == 2
					&& strncmp(pItemIn[i].p.sWord, pItemIn[i + 1].p.sWord, 2) == 0
					&& strlen(pItemIn[i + 1].p.sWord) == 4
					&& (pItemIn[i].p.nHandle / 256 == 'v' || pItemIn[i].p.nHandle == 24832))// v,a
			{
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
				// 24832=='a'*256
				pItemRetOut[j].p.nHandle = 24832;// 'a'
				if (pItemIn[i].p.nHandle / 256 == 'v'
						|| pItemIn[i + 1].p.nHandle / 256 == 'v')// 30208='v'8256
				{
					pItemRetOut[j].p.nHandle = 30208;
				}

				i += 1;
				j += 1;
				bProcessed = true;
			} else if (pItemIn[i].p.nHandle / 256 == 'u'
					&& pItemIn[i].p.nHandle % 256 != 0)// uj,ud,uv,uz,ul,ug->u
				pItemIn[i].p.nHandle = 'u' * 256;
			else if (nLen == 2
					&& strncmp(pItemIn[i].p.sWord, pItemIn[i + 1].p.sWord, 2) == 0
					&& strlen(pItemIn[i + 1].p.sWord) == 4
					&& strncmp(pItemIn[i + 1].p.sWord, 2, pItemIn[i + 2].p.sWord, 2) == 0) {// AABB
																						// 朴朴素素
																						// 枝枝叶叶
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
				strcat(pItemRetOut[j].p.sWord, pItemIn[i + 2].p.sWord);
				pItemRetOut[j].p.nHandle = pItemIn[i + 1].p.nHandle;
				i += 2;
				j += 1;
				bProcessed = true;
			} else if (pItemIn[i].p.nHandle == 28275)// PostFix
			{
				// if(m_uPlace.m_dict.IsExist(pItem[i+1].p.sWord,4))
				if (false) {
					strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					pItemRetOut[j].p.nHandle = 28275;
					i += 1;
					j += 1;
					bProcessed = true;
				} else if (strlen(pItemIn[i + 1].p.sWord) == 2
						&& CC_Find(toGB2312("队"), pItemIn[i + 1].p.sWord) != null) {
					strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					pItemRetOut[j].p.nHandle = 28276;
					i += 1;
					j += 1;
					bProcessed = true;
				} else if (strlen(pItemIn[i + 1].p.sWord) == 2
						&& CC_Find(toGB2312("语文字杯"), pItemIn[i + 1].p.sWord) != null) {
					strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					pItemRetOut[j].p.nHandle = 28282;
					i += 1;
					j += 1;
					bProcessed = true;
				} else if (strlen(pItemIn[i + 1].p.sWord) == 2
						&& CC_Find(toGB2312("裔"), pItemIn[i + 1].p.sWord) != null) {
					strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					pItemRetOut[j].p.nHandle = 28160;
					i += 1;
					j += 1;
					bProcessed = true;
				}
			} else if (pItemIn[i].p.nHandle == 30208
					|| pItemIn[i].p.nHandle == 28160)// v
			{
				if (strlen(pItemIn[i + 1].p.sWord) == 2
						&& CC_Find(toGB2312("员"), pItemIn[i + 1].p.sWord) != null) {
					strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					pItemRetOut[j].p.nHandle = 28160;
					i += 1;
					j += 1;
					bProcessed = true;
				}
			} else if (pItemIn[i].p.nHandle == 28280) {// www/nx ./w sina/nx;
														// ＥＩＭ/nx -６０１/m
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				pItemRetOut[j].p.nHandle = 28280;
				while (pItemIn[i + 1].p.nHandle == 28280
						|| strstr(toGB2312(".．"), pItemIn[i + 1].p.sWord) != null
						|| (pItemIn[i + 1].p.nHandle == 27904 && IsAllNum(pItemIn[i + 1].p.sWord))) {
					strcat(pItemRetOut[j].p.sWord, pItemIn[i + 1].p.sWord);
					i += 1;
				}
				j += 1;
				bProcessed = true;
			}

			if (!bProcessed) {// If not processed,that's mean: not need to
								// adjust;
								// just copy to the final result
				strcpy(pItemRetOut[j].p.sWord, pItemIn[i].p.sWord);
				pItemRetOut[j++].p.nHandle = pItemIn[i].p.nHandle;
			}
			i++;
		}
		pItemRetOut[j].p.sWord[0] = 0;// Set ending
		return true;
	}
}
