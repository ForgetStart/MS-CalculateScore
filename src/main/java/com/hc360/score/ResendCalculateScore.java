package com.hc360.score;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.ResendMessageConsumer;
import com.hc360.score.message.ResendMessageExecutor;
import com.hc360.score.statistics.ResendStatisticsHandler;
import org.apache.log4j.Logger;

/**
 * 由以前的storm实时商机算分程序改造而成
 * 主要提供后台计算商机分数星级
 * @author hc360
 *
 */
public class ResendCalculateScore {
	private static Logger logger = Logger.getLogger(AppContent.resendcalculatescorelog);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("Resend Calculate Score start...............");
		
		//启动算分消费端，开始接收消息
		ResendMessageConsumer.start();
		logger.info("ResendMessageConsumer start success");
		
		//启动多线程，开始处理消息
		ResendMessageExecutor.start();
		logger.info("ResendMessageExecutor start success");
		
		//开启统计功能，记录下统计信息
		ResendStatisticsHandler.start(AppContent.resendcstatisticslog, 60 * 1000, new long[]{1000l, 3000l});
		logger.info("ResendStatisticsHandler start success");
		
		logger.info("Resend Calculate Score start success");
	}

}
