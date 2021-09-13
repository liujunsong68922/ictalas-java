package com.icutword.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * 数据文件读写方面功能的功能类定义，独立工具类，无依赖
 * @author liujunsong
 *
 */
public class FileIOUtility {
	/**
	 * 从指定的数据输入流中，读取指定长度的byte数据，存放到byte[]中返回
	 * 如果文件流的长度不足，不抛出异常，直接返回即可
	 * 如果输入流关闭，直接返回null
	 * @param in
	 * @param len
	 * @return
	 */
	private static byte[] readBytes(DataInputStream in, int len) throws IOException {
		if (in != null && len > 0) {
			byte[] b = new byte[len];
			try{
				for (int i = 0; i < len; i++)
					b[i] = in.readByte();
			} catch (IOException e) {
				e.printStackTrace();
				throw new IOException(e);
			}

			return b;
		}

		return null;
	}	
	
	/**
	 * 字节数组转成长整形
	 * 
	 * @param b
	 * @param isHighFirst 是否高位在前，目前的数据文件格式是高位在后的
	 *            
	 * @return
	 */
	private static long bytes2long(byte[] b, boolean isHighFirst) {
		long result = 0;

		if (b != null && b.length <= 8) {
			long value;

			if (isHighFirst) {
				//如果高位在前，从后往前读
				for (int i = b.length - 1, j = 0; i >= 0; i--, j++) {
					value = (long) (b[i] & 0xFF);
					result += value << (j <<3);
				}
			} else {
				//如果高位在后，从前往后读
				for (int i = 0, j = 0; i < b.length - 1; i++, j++) {
					value = (long) (b[i] & 0xFF);
					result += value << (j <<3);
				}
			}
		}

		return result;
	}
	
	/**
	 * 本地方法，将byte[]数组转换为int,
	 * @param b
	 * @param isHighFirst byte[0]是否是高位
	 * @return
	 */
	private static int bytes2int(byte[] b, boolean isHighFirst) {
		return (int) bytes2long(b, isHighFirst);
	}		
	/**
	 * 从输入流中读取一个int出来。一个int占用4个byte,
	 * 先低位，后高位。也就是说byte[0]代表最低位，byte[3]是最高位
	 * @param in 输入的数据流
	 * @return
	 * @throws Exception 
	 */
	public int readInt(DataInputStream in) throws IOException{
		return bytes2int(readBytes(in, 4), false);
	}
	
	/**
	 * 向输出流中写入一个int,按照原来的文件格式，byte[0]代表低位，byte[3]代表高位
	 * 每个int占用4个byte[]
	 * @param out 输出数据流
	 * @param v 要输出的int数值
	 */
	public void writeInt(DataOutputStream out,int v) throws IOException{
		byte[] b=new byte[4];
		b[0]=(byte)((v >>> 24) & 0xFF);
		b[1]=(byte)((v >>> 16) & 0xFF);
		b[2]=(byte)((v >>> 8) & 0xFF);
		b[3]=(byte)((v >>> 0) & 0xFF);	
		out.writeByte(b[3]);
		out.writeByte(b[2]);
		out.writeByte(b[1]);
		out.writeByte(b[0]);
		
	}
	

}
