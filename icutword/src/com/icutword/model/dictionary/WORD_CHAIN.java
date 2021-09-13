package com.icutword.model.dictionary;

/**
 * 结构体WORD_CHAIN的模拟实现类
 * @author liujunsong
 *
 */
public class WORD_CHAIN {
    WORD_ITEM data;
    PWORD_CHAIN next;
    
    /**
     * 构造函数，构造WORD_CHAIN的时候，自动生成下属的数据类
     */
    public WORD_CHAIN(){
    	data= new WORD_ITEM();
    }
    
    /**
     * 清理释放内容
     */
    public void Destroy(){
    	if(data!=null){
    		data.sWord=null;
    	}
    	data=null;
    	next=null;
    }
}
