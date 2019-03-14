package com.hc360.score.message;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.score.common.AppContent;
import com.hc360.score.statistics.Statistics;
import com.hc360.score.statistics.StatisticsHandler;

/**
 * 启动多线程，循环处理消息
 * @author hc360
 *
 */
public class MessageExecutor implements Runnable{
	private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);
	
	//多线程处理消息
	public static ExecutorService executorService  = Executors.newFixedThreadPool(AppContent.nThreads);
	
	public static void start(){
		
		for(int i=0;i<AppContent.nThreads;i++){
			executorService.submit(new MessageExecutor());
		}
		logger.info("共启动了"+AppContent.nThreads+"个线程来处理消息！！");
	}
	
	@Override
	public void run() {
		while(true){
			try{
				BusinInfo busininfo = MessageHandler.blockQueue.take();
				boolean ret = false;
				int n = -1; //失败后重试3次
				while(!ret && n++<AppContent.retry){
					Statistics s = StatisticsHandler.createStatistics("MessageHandler");
					
					ret = MessageHandler.handler(new Transinformation(busininfo));
					
					s.end(ret);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Handler message exception:", e);
			}
		}
	}
}