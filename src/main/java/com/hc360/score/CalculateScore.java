package com.hc360.score;

import org.apache.log4j.Logger;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.MessageConsumer;
import com.hc360.score.message.MessageExecutor;
import com.hc360.score.statistics.StatisticsHandler;
/**
 * ����ǰ��stormʵʱ�̻���ֳ���������
 * ��Ҫ�ṩ��̨�����̻������Ǽ�
 * @author hc360
 *
 */
public class CalculateScore {
	private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("Calculate Score start...............");
		
		//����������Ѷˣ���ʼ������Ϣ
		MessageConsumer.start();
		logger.info("MessageConsumer start success");
		
		//�������̣߳���ʼ������Ϣ
		MessageExecutor.start();
		logger.info("MessageExecutor start success");

		//����ͳ�ƹ��ܣ���¼��ͳ����Ϣ
		StatisticsHandler.start(AppContent.cstatisticslog, 60*1000, new long[]{1000l,3000l});
		logger.info("StatisticsHandler start success");
		
		logger.info("Calculate Score start success");
	}

}
