package com.hc360.score.message;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.score.common.AppContent;
import com.hc360.score.statistics.ResendStatisticsHandler;
import com.hc360.score.statistics.Statistics;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * �������̣߳�ѭ��������Ϣ
 * @author hc360
 *
 */
public class ResendMessageExecutor implements Runnable{
	private static Logger logger = Logger.getLogger(AppContent.resendcalculatescorelog);
	
	//���̴߳�����Ϣ
	public static ExecutorService executorService  = Executors.newFixedThreadPool(AppContent.nThreads);
	
	public static void start(){
		
		for(int i=0;i<AppContent.nThreads;i++){
			executorService.submit(new ResendMessageExecutor());
		}
		logger.info("��������"+AppContent.nThreads+"���߳���������Ϣ����");
	}
	
	@Override
	public void run() {
		while(true){
			try{
                //TODO �˳��߳�  (���Ƕ�������)

				BusinInfo busininfo = ResendMessageHandler.blockQueue.take();
				boolean ret = false;
				int n = -1; //ʧ�ܺ�����3��
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