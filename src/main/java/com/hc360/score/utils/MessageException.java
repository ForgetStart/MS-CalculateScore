package com.hc360.score.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageException extends Exception {

	private static final long serialVersionUID = 1L;

	public MessageException(String message) {
		super(message);
	}

	/**
	 * ��ȡ�쳣�Ķ�ջ��Ϣ Java��ȡ�쳣�Ķ�ջ��Ϣ��String�ķ�ʽ��
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
