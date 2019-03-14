package com.hc360.score.statistics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ŀ�꣺
	1. ͳ��ĳһ�죬��Щ�û���������Щ�̻�������ʲô����
	2. ��¼ÿ����Ϣ���Լ���ÿ�����������ɹ�����ʱ������ϸ��¼ʧ��ԭ��
	3. ÿ��������Ϣ��һ̨�����ϲ�����������û��棬���ٶ��洢
	4. ���ٶ����ݿ�Ĳ���������������
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
