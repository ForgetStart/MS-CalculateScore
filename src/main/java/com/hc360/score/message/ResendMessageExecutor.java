package com.hc360.score.message;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.score.common.AppContent;
import com.hc360.score.statistics.ResendStatisticsHandler;
import com.hc360.score.statistics.Statistics;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动多线程，循环处理消息
 * @author hc360
 *
 */
public class ResendMessageExecutor implements Runnable{
	private static Logger logger = Logger.getLogger(AppContent.resendcalculatescorelog);
	
	//多线程处理消息
	public static ExecutorService executorService  = Executors.newFixedThreadPool(AppContent.nThreads);
	
	public static void start(){
		
		for(int i=0;i<AppContent.nThreads;i++){
			executorService.submit(new ResendMessageExecutor());
		}
		logger.info("共启动了"+AppContent.nThreads+"个线程来处理消息！！");
	}
	
	@Override
	public void run() {
		while(true){
			try{
                //TODO 退出线程  (考虑队列阻塞)

				BusinInfo busininfo = ResendMessageHandler.blockQueue.take();
				boolean ret = false;
				int n = -1; //失败后重试3次
				while(!ret && n++<AppContent.retry){
					Statistics s = ResendStatisticsHandler.createStatistics("ResendMessageHandler");
					
					ret = ResendMessageHandler.handler(new Transinformation(busininfo));
					
					s.end(ret);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error("Resend Handler message exception:", e);
			}
		}
	}
}