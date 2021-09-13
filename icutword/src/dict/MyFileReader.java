package dict;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class MyFileReader {
	String filename;
	File file;
	byte[] buffer = new byte[1024 * 1024]; // byte����
	int pos = 0;
	int size = 0;

	//FileReader fr;
	//InputStreamReader reader;
	DataInputStream in ;
	
	public MyFileReader(String filename1) throws Exception {
		filename = filename1;
		file = new File(filename);
		 in = new DataInputStream(new BufferedInputStream(
					new FileInputStream(file)));
		 
		buffer = new byte[1024 * 1024];
		pos = 0;
		size = 0;
	}

	public byte[] readLine() throws Exception {
		// ��buffer��Ѱ��һ��\r, ����\r\n����
		int ipos = findLineBreak(buffer, pos, size-1);
		//System.out.println("ipos:"+ipos);
		byte[] ret = new byte[0];

		if (ipos > 0) {
			ret = new byte[ipos - pos + 1];
			for (int j = pos; j < ipos; j++) {
				ret[j - pos] = buffer[j];
			}
			if (buffer[ipos + 1] == '\n') {
				pos = ipos + 2;
			} else {
				pos = ipos + 1;
			}
		} else {
			// ���û���ҵ�һ���س����ţ���������
			byte[] buffer2 = new byte[1024 * 1024];
			int l = 0;
			for (l = 0; l < size - pos; l++) {
				buffer2[l] = buffer[l + pos];
			}
			int size2 = in.read(buffer2, l, 1024 * 1024 - l);
			buffer = buffer2;
			System.out.println("read file size2:"+size2);
			if (size2 > 0) {
				size = size2 + (size - pos);
				pos = 0;
				return readLine();
			} else {
				in.close();
				return null;
			}
		}

		return ret;
	}

	/**
	 * ��һ��char[] ����һ���س����ų�����������\r,Ҳ������\n
	 * 
	 * @param data
	 * @param begin
	 * @param end
	 * @return
	 */
	private int findLineBreak(byte[] data, int begin, int end) {
		int i = 0;
		for (i = begin; i <= end; i++) {
			if (data[i] == '\r') {
				return i;
			}
		}
		return -1;
		
	}
}
