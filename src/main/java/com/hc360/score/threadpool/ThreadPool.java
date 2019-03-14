package com.hc360.score.threadpool;

import java.util.concurrent.Executor;


/**
 * ThreadPool
 * 
 */
public interface ThreadPool {
    
    /**
     * 线程池
     * @param threadName  线程的name的前缀
     * @param corePoolSize 线程池core_Pool_Size值
     * @param maximumPoolSize  线程池maximumPoolSize值
     * @param queues 线程池队列大小值
     * @param keepalive 线程空闲时间超过keepAliveTime,线程将被终止
     * @return
     */
    Executor getExecutor(String threadName, int corePoolSize, int maximumPoolSize, int queues, int keepalive);
}