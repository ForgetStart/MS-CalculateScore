package com.hc360.score.statistics;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
	2015.8.25 ����n���û����̻������˲�����
	��������n���������ɹ�n����ʧ��n����
	�����·����󣺳ɹ�n����ʧ��n�����޸ģ��ɹ�n����ʧ��n����ɾ�����ɹ�n����ʧ��n��...
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
			sb.append(" ����0���û����̻������˲�����");
			return sb.toString();
		}
		sb.append(" ����").append(userMap.size()).append("���û����̻������˲�����").append(" \n");
		
		for(Entry<Long, UserMessage> userMessage : userMap.entrySet()){
			UserMessage um = userMessage.getValue();
			
			totalNum += um.getTotalSum();
			sucNum += um.getSucSum();
			failNum += um.getFailSum();
			
			addNumForType(um);
			
			users.append(um.toString()).append("\n");
		}
		
		sb.append(" ��������").append(totalNum).append("���������ɹ�").append(sucNum).append("����ʧ��").append(failNum).append("����").append(" \n");

		sb.append(" ��������:0�·�����1�·�����2�޸Ĵ���3�޸�����4δ�����ط���5�����ط���6ת���ڣ�7ɾ����8���ͨ����9����").append(" \n");
		sb.append(" �ɹ���").append(sucMap.toString()).append(" \n");
		sb.append(" ʧ�ܣ�").append(failMap.toString()).append("\n ");
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
