package com.icutword.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * �����ļ���д���湦�ܵĹ����ඨ�壬���������࣬������
 * @author liujunsong
 *
 */
public class FileIOUtility {
	/**
	 * ��ָ���������������У���ȡָ�����ȵ�byte���ݣ���ŵ�byte[]�з���
	 * ����ļ����ĳ��Ȳ��㣬���׳��쳣��ֱ�ӷ��ؼ���
	 * ����������رգ�ֱ�ӷ���null
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
	 * �ֽ�����ת�ɳ�����
	 * 
	 * @param b
	 * @param isHighFirst �Ƿ��λ��ǰ��Ŀǰ�������ļ���ʽ�Ǹ�λ�ں��
	 *            
	 * @return
	 */
	private static long bytes2long(byte[] b, boolean isHighFirst) {
		long result = 0;

		if (b != null && b.length <= 8) {
			long value;

			if (isHighFirst) {
				//�����λ��ǰ���Ӻ���ǰ��
				for (int i = b.length - 1, j = 0; i >= 0; i--, j++) {
					value = (long) (b[i] & 0xFF);
					result += value << (j <<3);
				}
			} else {
				//�����λ�ں󣬴�ǰ�����
				for (int i = 0, j = 0; i < b.length - 1; i++, j++) {
					value = (long) (b[i] & 0xFF);
					result += value << (j <<3);
				}
			}
		}

		return result;
	}
	
	/**
	 * ���ط�������byte[]����ת��Ϊint,
	 * @param b
	 * @param isHighFirst byte[0]�Ƿ��Ǹ�λ
	 * @return
	 */
	private static int bytes2int(byte[] b, boolean isHighFirst) {
		return (int) bytes2long(b, isHighFirst);
	}		
	/**
	 * ���������ж�ȡһ��int������һ��intռ��4��byte,
	 * �ȵ�λ�����λ��Ҳ����˵byte[0]�������λ��byte[3]�����λ
	 * @param in �����������
	 * @return
	 * @throws Exception 
	 */
	public int readInt(DataInputStream in) throws IOException{
		return bytes2int(readBytes(in, 4), false);
	}
	
	/**
	 * ���������д��һ��int,����ԭ�����ļ���ʽ��byte[0]�����λ��byte[3]�����λ
	 * ÿ��intռ��4��byte[]
	 * @param out ���������
	 * @param v Ҫ�����int��ֵ
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
