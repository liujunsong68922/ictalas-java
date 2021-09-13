package com.icutword.utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class FileIOUtilityTest {
	public static void main(String args[]) throws Exception{
		FileIOUtility fu = new FileIOUtility();
		String sFilename="test";
		File outfile = new File(sFilename);
		DataOutputStream out = new DataOutputStream(new FileOutputStream(
				outfile));
		fu.writeInt(out, 10257);
		out.flush();
		out.close();
		
		DataInputStream in = new DataInputStream(new FileInputStream(
				outfile));
		int i = fu.readInt(in);
		System.out.println(i);
	}
}
