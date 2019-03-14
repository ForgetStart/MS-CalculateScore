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
	
	//存放消息的队列，等于多线程的2倍，防止重启后丢失过多消息，让消息都挤压在jms的broker中
	public static BlockingQueue<BusinInfo> blockQueue  = new ArrayBlockingQueue<BusinInfo>(AppContent.nThreads*2);
	
	//存放所有处理中的消息，成功后会移出并记录；失败后会进行重试，重试失败最终也会移出并记录
	public static Map<Long, UserMessage> userMap = new ConcurrentHashMap<Long, UserMessage>();
	
	//存储多个处理消息的handler
	public static List<Handler> handlerList = new ArrayList<Handler>();
	static{
		handlerList.add(new BusinessScoreHandler());
		handlerList.add(new UserScoreHandler());
//		handlerList.add(new UserStateHandler());
		calculatescoreLog.info("存储多个处理消息的handler, size="+handlerList.size()+", "+handlerList.toString());
	}
	
	/**
	 * 返回false意味着，算分的过程出现了问题，即按正常逻辑走，出现了异常；
	 * 这种情况需要返回true：商机id为0，因为算分过程并没有出现问题，不需要重新计算，只需要记录即可
	 */
	public static boolean handler(Transinformation traninfo) {
		try{
			long startTime = System.currentTimeMillis();
			//BusinessRecord busin = new BusinessRecord(traninfo.getBusininfo().getBcid(), traninfo.getBusininfo().getOper(), startTime);
            BusinessRecord busin = new BusinessRecord(traninfo.getBusininfo().getBcid(), traninfo.getBusininfo().getOper(), traninfo.getBusininfo().getScoreIdentity(),startTime);
			traninfo.setBusinessRecord(busin);
			
			for (int i=0;i<handlerList.size();i++) {
				Handler handler = handlerList.get(i);
				//1.处理单条商机信息
				//2.处理用户平均质量信息
				//3.处理用户成长状态
				boolean next = handler.handler(traninfo);
				
				//如果不需要执行下一步，或者失败，则跳出循环结束处理
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
		
		//记录用户信息
		long userId = traninfo.getBusininfo().getUserid();
		UserMessage userMessage = userMap.get(userId);
		if(userMessage == null){
			userMessage = new UserMessage();
			userMessage.setUserId(userId);
			userMap.put(userId, userMessage);
		}

		if(!traninfo.isSuccess()){
			String error = busin.toString()+" ==》 "+traninfo.toString();
			
			//记录每条消息的详细信息
			businessLog.info(error);
			
			calculatescoreLog.error(error);
			
			List<String> fail = userMessage.getFailMap().get(busin.getOperType());
			if(fail==null){
				 fail = new ArrayList<String>();
				 userMessage.getFailMap().put(busin.getOperType(), fail);
			}
			fail.add(busin.getBcids());
			//发监控统计
			StatisticsHandler.send("CalculateScore-Error-Business", error);
		}else{

			//记录每条消息的详细信息
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