package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.BusinHistory60wTaskManage;
import org.apache.log4j.Logger;

public class BusinHistory60wThread implements Runnable {
	Logger logger = Logger.getLogger(AppContent.businhistory60w);

    private int threadno;

    public BusinHistory60wThread(int threadno) {
        this.threadno = threadno;
    }

    @Override
	/**
	 * 运行线程的主要方法
	 */
	public void run() {
		try{
            long start = System.currentTimeMillis();
            logger.info("BusinHistory60wThread threadno:"+threadno+" start............");

            BusinHistory60wTaskManage.getInstance().dealBusinInfo(threadno);

            logger.info("BusinHistory60wThread threadno:"+threadno+" end...............");
            logger.info("BusinHistory60wThread threadno:"+threadno+" use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e) {
            logger.error("threadno:"+threadno+"处理60w商机线程异常"+e.getMessage());
            e.printStackTrace();
		}
	}

}