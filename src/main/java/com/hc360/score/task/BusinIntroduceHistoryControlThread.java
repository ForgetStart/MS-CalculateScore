package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.BusinIntroduceHistoryControlTaskManage;
import org.apache.log4j.Logger;

public class BusinIntroduceHistoryControlThread implements Runnable {
	Logger logger = Logger.getLogger(AppContent.calculatescorelog);

    @Override
	/**
	 * �����̵߳���Ҫ����
	 */
	public void run() {
		try{
            long start = System.currentTimeMillis();
            logger.info("BusinIntroduceHistoryControlThread start............");

            BusinIntroduceHistoryControlTaskManage.getInstance().dealBusinIntroduceTask();

            logger.info("BusinIntroduceHistoryControlThread end...............");
            logger.info("BusinIntroduceHistoryControlThread use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e) {
            logger.error("�����̻�������Ϣ����״̬�߳��쳣"+e.getMessage());
            e.printStackTrace();
		}
	}

}