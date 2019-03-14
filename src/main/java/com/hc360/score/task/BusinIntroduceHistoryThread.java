package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.BusinIntroduceHistoryTaskManage;
import org.apache.log4j.Logger;

public class BusinIntroduceHistoryThread implements Runnable {
	Logger logger = Logger.getLogger(AppContent.calculatescorelog);

    private int threadno;

    public BusinIntroduceHistoryThread(int threadno) {
        this.threadno = threadno;
    }

    @Override
	/**
	 * 运行线程的主要方法
	 */
	public void run() {
		try{
            long start = System.currentTimeMillis();
            logger.info("BusinIntroduceHistoryThread threadno:"+threadno+" start............");

            BusinIntroduceHistoryTaskManage.getInstance().dealBusinIntroduceInfo(threadno);

            logger.info("BusinIntroduceHistoryThread threadno:"+threadno+" end...............");
            logger.info("BusinIntroduceHistoryThread threadno:"+threadno+" use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e) {
            logger.error("threadno:"+threadno+"处理商机详情信息线程异常"+e.getMessage());
            e.printStackTrace();
		}
	}

}