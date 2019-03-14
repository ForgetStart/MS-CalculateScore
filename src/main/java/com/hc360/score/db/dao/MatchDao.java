/*
 * Copyright(c) 2000-2007 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.db.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc360.b2b.exception.MmtException;
import com.hc360.score.db.MatchDBSource;

public class MatchDao extends MatchDBSource {

	private static MatchDao masterInstance = new MatchDao(false);
	private static MatchDao slaveInstance = new MatchDao(true);
	
	private MatchDao(boolean isSlave) {
		super(isSlave);
	}
	
	public static MatchDao getInstance(boolean isSlave){
		return isSlave ? slaveInstance : masterInstance;
	}
	
	
	public static void main(String[] args) {
		System.out.println(1);
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(2);
	}

	/**
	 * 得到用户买卖速配阅读
	 */
	public boolean getMatchInfoCount(long providerid)throws MmtException{
		String sql = "from MatchBusinInfo where providerid = :providerid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("providerid", providerid);
		
		List list = super.query(sql, params);
		
		if(list!=null && list.size()>0){
			return true;
		}
		return false;
	}
	
}