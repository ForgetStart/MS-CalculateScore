package com.hc360.score.threadpool;

import java.util.concurrent.Executor;


/**
 * ThreadPool
 * 
 */
public interface ThreadPool {
    
    /**
     * �̳߳�
     * @param threadName  �̵߳�name��ǰ׺
     * @param corePoolSize �̳߳�core_Pool_Sizeֵ
     * @param maximumPoolSize  �̳߳�maximumPoolSizeֵ
     * @param queues �̳߳ض��д�Сֵ
     * @param keepalive �߳̿���ʱ�䳬��keepAliveTime,�߳̽�����ֹ
     * @return
     */
    Executor getExecutor(String threadName, int corePoolSize, int maximumPoolSize, int queues, int keepalive);
}