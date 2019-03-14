package com.hc360.score.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class OperateFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static String getContent(String name){
		try {
			File file = new File("src/main/resources/businmo.txt");
			System.out.println(file.getAbsolutePath());
			FileInputStream ifs = new FileInputStream(file);
			InputStreamReader inr = new InputStreamReader(ifs,"GBK");
			BufferedReader r = new BufferedReader(inr);
			StringBuilder sb = new StringBuilder();
			String s = r.readLine();
			while(s!=null && s.length()>=1){
				sb.append(s);
				s = r.readLine();
			}
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
