package com.hc360.score.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageException(String message) {
		super(message);
	}

	/**
	 * 获取异常的堆栈信息 Java获取异常的堆栈信息到String的方式：
	 */
	public static String getStackTrace(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		try {
			t.printStackTrace(pw);
			return sw.toString();
		} finally {
			pw.close();
		}
	}
}
