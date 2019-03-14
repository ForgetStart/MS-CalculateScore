package com.hc360.score;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.ResendMessageConsumer;
import com.hc360.score.message.ResendMessageExecutor;
import com.hc360.score.statistics.ResendStatisticsHandler;
import org.apache.log4j.Logger;

/**
 * ����ǰ��stormʵʱ�̻���ֳ���������
 * ��Ҫ�ṩ��̨�����̻������Ǽ�
 * @author hc360
 *
 */
public class ResendCalculateScore {
	private static Logger logger = Logger.getLogger(AppContent.resendcalculatescorelog);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger.info("Resend Calculate Score start...............");
		
		//����������Ѷˣ���ʼ������Ϣ
		ResendMessageConsumer.start();
		logger.info("ResendMessageConsumer start success");
		
		//�������̣߳���ʼ������Ϣ
		ResendMessageExecutor.start();
		logger.info("ResendMessageExecutor start success");
		
		//����ͳ�ƹ��ܣ���¼��ͳ����Ϣ
		ResendStatisticsHandler.start(AppContent.resendcstatisticslog, 60 * 1000, new long[]{1000l, 3000l});
		logger.info("ResendStatisticsHandler start success");
		
		logger.info("Resend Calculate Score start success");
	}

}
