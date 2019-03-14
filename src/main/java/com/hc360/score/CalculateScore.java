package com.hc360.score;

import org.apache.log4j.Logger;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.MessageConsumer;
import com.hc360.score.message.MessageExecutor;
import com.hc360.score.statistics.StatisticsHandler;
/**
 * 由以前的storm实时商机算分程序改造而成
 * 主要提供后台计算商机分数星级
 * @author hc360
 *
 */
public class CalculateScore {
	private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("Calculate Score start...............");
		
		//启动算分消费端，开始接收消息
		MessageConsumer.start();
		logger.info("MessageConsumer start success");
		
		//启动多线程，开始处理消息
		MessageExecutor.start();
		logger.info("MessageExecutor start success");

		//开启统计功能，记录下统计信息
		StatisticsHandler.start(AppContent.cstatisticslog, 60*1000, new long[]{1000l,3000l});
		logger.info("StatisticsHandler start success");
		
		logger.info("Calculate Score start success");
	}

}
