package com.hc360.score.message.handler;

import com.hc360.score.message.Transinformation;

public interface Handler {

	/**
	 * ����ֵ��ʶ���Ƿ���Ҫ������һ������
	 * traninfo.isSuccess ��ʶ�ò��Ƿ�ִ�гɹ�
	 */
	public boolean handler(Transinformation traninfo);
}
