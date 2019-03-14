package com.hc360.score.message.handler;

import com.hc360.score.message.Transinformation;

public interface Handler {

	/**
	 * 返回值标识：是否需要进行下一步计算
	 * traninfo.isSuccess 标识该步是否执行成功
	 */
	public boolean handler(Transinformation traninfo);
}
