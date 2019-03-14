package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.threadpool.FixedThreadPool;
import org.apache.log4j.Logger;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 60w�̻����� ���̻�ͼƬ���̻����飩
 */
public class BusinHistory60wTmpTask {

    private static Logger logger = Logger.getLogger(AppContent.businhistory60w);

    // �̵߳�name��ǰ׺
    static String threadName = "BusinHistory60wTmpTask";
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
            logger.info("BusinHistory60wTmpTask start............");

            /*Exception in thread "main" java.lang.NoClassDefFoundError: org/apache/log4j/Logg
            er*/
            /*for(int threadno=0;threadno<100;threadno++){
                Runnable runner = new BusinIntroduceHistoryThread(threadno);
                pool.execute(runner);
            }*/

            Runnable runner = new BusinHistory60wThread(0);
            pool.execute(runner);

            logger.info("BusinHistory60wTmpTask end...............");
            logger.info("BusinHistory60wTmpTask use time:" + (System.currentTimeMillis() - start));

        }catch(Exception e){
			e.printStackTrace();
		}
		//System.exit(0);
	}

}
