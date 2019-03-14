package com.hc360.score.utils;

import java.io.Serializable;

public class BaseResult<T> implements Serializable{
	
	//返回接口状态码
	private long errcode;
	//返回消息
	private String errmsg;
	//返回数据
	private T data;


	public long getErrcode() {
		return errcode;
	}

	public void setErrcode(long errcode) {
		this.errcode = errcode;
	}

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}
}
