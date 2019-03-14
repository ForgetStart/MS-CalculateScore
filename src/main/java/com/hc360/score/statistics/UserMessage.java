package com.hc360.score.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 目标：
	1. 统计某一天，哪些用户处理了哪些商机，做的什么操作
	2. 记录每条消息，以及其每步处理结果（成功、耗时），详细记录失败原因
	3. 每个任务消息在一台机器上操作，充分利用缓存，减少读存储
	4. 减少对数据库的操作，尽量读备库
 *
 */
public class UserMessage {
	private long userId;
	private Map<Integer,List<String>> sucMap = new HashMap<Integer,List<String>>();
	private Map<Integer,List<String>> failMap = new HashMap<Integer,List<String>>();
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Map<Integer, List<String>> getSucMap() {
		return sucMap;
	}
	public Map<Integer, List<String>> getFailMap() {
		return failMap;
	}

	public int getTotalSum(){
		return getSucSum() + getFailSum();
	}
	public int getSucSum(){
		return getSum(sucMap);
	}
	
	public int getFailSum(){
		return getSum(failMap);
	}
	private int getSum(Map<Integer,List<String>> map){
		int sum = 0;
		for(Map.Entry<Integer, List<String>> kv: map.entrySet()){
			sum += kv.getValue().size();
		}
		return sum;
	}
	@Override
	public String toString() {
		return "UserMessage [userId=" + userId +", totalSize=" + getTotalSum() +", sucSize=" + getSucSum() +", failSize=" + getFailSum()
				+ ", \n failMap=" + failMap
				+ ", \n sucMap=" + sucMap + "]";
	}
}
