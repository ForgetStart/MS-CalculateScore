package com.hc360.score.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
	2015.8.25 共有n个用户对商机进行了操作！
	共进行了n个操作，成功n个，失败n个！
	其中新发待审：成功n个、失败n个，修改：成功n个、失败n个，删除：成功n个、失败n个...
 */
public class PeriodStatistics {
	
	private int totalNum;
	private int sucNum;
	private int failNum;
	
	private Map<Integer,Integer> sucMap = new HashMap<Integer,Integer>();
	private Map<Integer,Integer> failMap = new HashMap<Integer,Integer>();
	
	public String getUserStatistics(Map<Long, UserMessage> userMap){

		StringBuffer sb = new StringBuffer();
		StringBuffer users = new StringBuffer();
		sb.append("\n ").append(new Date().toLocaleString());
		if(userMap==null || userMap.size()<=0){
			sb.append(" 共有0个用户对商机进行了操作！");
			return sb.toString();
		}
		sb.append(" 共有").append(userMap.size()).append("个用户对商机进行了操作！").append(" \n");
		
		for(Entry<Long, UserMessage> userMessage : userMap.entrySet()){
			UserMessage um = userMessage.getValue();
			
			totalNum += um.getTotalSum();
			sucNum += um.getSucSum();
			failNum += um.getFailSum();
			
			addNumForType(um);
			
			users.append(um.toString()).append("\n");
		}
		
		sb.append(" 共进行了").append(totalNum).append("个操作，成功").append(sucNum).append("个，失败").append(failNum).append("个！").append(" \n");

		sb.append(" 操作类型:0新发待审，1新发免审，2修改待审，3修改免审，4未过期重发，5过期重发，6转过期，7删除，8审核通过，9拒审").append(" \n");
		sb.append(" 成功：").append(sucMap.toString()).append(" \n");
		sb.append(" 失败：").append(failMap.toString()).append("\n ");
		sb.append(users.toString());
		
		return sb.toString();
	}
	private void addNumForType(UserMessage um){
		
		for(Map.Entry<Integer, List<String>> kv: um.getSucMap().entrySet()){
			int k = kv.getKey();
			int size = kv.getValue().size();
			if(!sucMap.containsKey(k)){
				sucMap.put(k, size);
			}else{
				sucMap.put(k, sucMap.get(k)+size);
			}
		}
		for(Map.Entry<Integer, List<String>> kv: um.getFailMap().entrySet()){
			int k = kv.getKey();
			int size = kv.getValue().size();
			if(!failMap.containsKey(k)){
				failMap.put(k, size);
			}else{
				failMap.put(k, failMap.get(k)+size);
			}
		}
	}
}
