package com.hc360.score.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.score.common.AppContent;
import com.hc360.score.message.handler.BusinessScoreHandler;
import com.hc360.score.message.handler.Handler;
import com.hc360.score.message.handler.UserScoreHandler;
import com.hc360.score.statistics.BusinessRecord;
import com.hc360.score.statistics.StatisticsHandler;
import com.hc360.score.statistics.UserMessage;

public class MessageHandler{
	private static Logger calculatescoreLog = Logger.getLogger(AppContent.calculatescorelog);
	private static Logger businessLog = Logger.getLogger(AppContent.businesslog);
	
	//�����Ϣ�Ķ��У����ڶ��̵߳�2������ֹ������ʧ������Ϣ������Ϣ����ѹ��jms��broker��
	public static BlockingQueue<BusinInfo> blockQueue  = new ArrayBlockingQueue<BusinInfo>(AppContent.nThreads*2);
	
	//������д����е���Ϣ���ɹ�����Ƴ�����¼��ʧ�ܺ��������ԣ�����ʧ������Ҳ���Ƴ�����¼
	public static Map<Long, UserMessage> userMap = new ConcurrentHashMap<Long, UserMessage>();
	
	//�洢���������Ϣ��handler
	public static List<Handler> handlerList = new ArrayList<Handler>();
	static{
		handlerList.add(new BusinessScoreHandler());
		handlerList.add(new UserScoreHandler());
//		handlerList.add(new UserStateHandler());
		calculatescoreLog.info("�洢���������Ϣ��handler, size="+handlerList.size()+", "+handlerList.toString());
	}
	
	/**
	 * ����false��ζ�ţ���ֵĹ��̳��������⣬���������߼��ߣ��������쳣��
	 * ���������Ҫ����true���̻�idΪ0����Ϊ��ֹ��̲�û�г������⣬����Ҫ���¼��㣬ֻ��Ҫ��¼����
	 */
	public static boolean handler(Transinformation traninfo) {
		try{
			long startTime = System.currentTimeMillis();
			//BusinessRecord busin = new BusinessRecord(traninfo.getBusininfo().getBcid(), traninfo.getBusininfo().getOper(), startTime);
            BusinessRecord busin = new BusinessRecord(traninfo.getBusininfo().getBcid(), traninfo.getBusininfo().getOper(), traninfo.getBusininfo().getScoreIdentity(),startTime);
			traninfo.setBusinessRecord(busin);
			
			for (int i=0;i<handlerList.size();i++) {
				Handler handler = handlerList.get(i);
				//1.�������̻���Ϣ
				//2.�����û�ƽ��������Ϣ
				//3.�����û��ɳ�״̬
				boolean next = handler.handler(traninfo);
				
				//�������Ҫִ����һ��������ʧ�ܣ�������ѭ����������
				if(!next || !traninfo.isSuccess()) break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

		recordAndStatistics(traninfo);
		
		return traninfo.isSuccess();
	}
	
	private static void recordAndStatistics(Transinformation traninfo){
		BusinessRecord busin = traninfo.getBusinessRecord();
		
		busin.setDateTime(new Date().toLocaleString());
		busin.setUseTime((System.currentTimeMillis() - busin.getUseTime()));
		busin.setReason(traninfo.getReason());
		
		//��¼�û���Ϣ
		long userId = traninfo.getBusininfo().getUserid();
		UserMessage userMessage = userMap.get(userId);
		if(userMessage == null){
			userMessage = new UserMessage();
			userMessage.setUserId(userId);
			userMap.put(userId, userMessage);
		}

		if(!traninfo.isSuccess()){
			String error = busin.toString()+" ==�� "+traninfo.toString();
			
			//��¼ÿ����Ϣ����ϸ��Ϣ
			businessLog.info(error);
			
			calculatescoreLog.error(error);
			
			List<String> fail = userMessage.getFailMap().get(busin.getOperType());
			if(fail==null){
				 fail = new ArrayList<String>();
				 userMessage.getFailMap().put(busin.getOperType(), fail);
			}
			fail.add(busin.getBcids());
			//�����ͳ��
			StatisticsHandler.send("CalculateScore-Error-Business", error);
		}else{

			//��¼ÿ����Ϣ����ϸ��Ϣ
			businessLog.info(busin.toString());
			
			List<String> suc = userMessage.getSucMap().get(busin.getOperType());
			if(suc==null){
				 suc = new ArrayList<String>();
				 userMessage.getSucMap().put(busin.getOperType(), suc);
			}
			suc.add(busin.getBcids());
		}
	}
}