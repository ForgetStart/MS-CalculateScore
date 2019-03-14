package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.threadpool.FixedThreadPool;
import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * �̻�������ʷ���ݴ���
 */
public class BusinIntroduceHistoryTask {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

    // �̵߳�name��ǰ׺
    static String threadName = "calculatescore_history";
    // �̳߳�core_Pool_Sizeֵ
    static int corePoolSize = 150;
    // �̳߳�maximumPoolSizeֵ
    static int maximumPoolSize = 200;
    // �̳߳ض��д�Сֵ
    static int queues = 150;
    // �߳̿���ʱ�䳬��keepAliveTime,�߳̽�����ֹ
    static int keepalive = 60 * 1000;


    public static ThreadPoolExecutor pool = null;

    static{
        pool = (ThreadPoolExecutor) new FixedThreadPool().getExecutor(threadName, corePoolSize, maximumPoolSize, queues, keepalive);
    }

    /*public BusinIntroduceHistoryTask() {
        pool = (ThreadPoolExecutor) new FixedThreadPool().getExecutor(threadName, corePoolSize, maximumPoolSize, queues, keepalive);
    }*/

	/**
     * �̻�������ʷ���ݴ���
     *
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("BusinIntroduceHistoryTask start............");

            /*for(int threadno=0;threadno<100;threadno++){
                Runnable runner = new BusinIntroduceHistoryThread(threadno);
                pool.execute(runner);
            }*/

            for(int threadno=100;threadno<200;threadno++){
                Runnable runner = new BusinIntroduceHistoryThread(threadno);
                pool.execute(runner);
            }

            logger.info("BusinIntroduceHistoryTask end...............");
            logger.info("BusinIntroduceHistoryTask use time:" + (System.currentTimeMillis() - start));

        }catch(Exception e){
			e.printStackTrace();
		}
		//System.exit(0);
	}

}
