package com.hc360.score.threadpool;

import com.hc360.rsf.common.utils.NamedThreadFactory;
import com.hc360.score.common.AppContent;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * FixedThreadPool
 * 
 * 通过如下方式创建线程池:
 * AbstractExecutorService executor=new ThreadPoolExecutor(3,10,30L,TimeUnit.SECONDS,new SynchronousQueue(),new ExecutorThreadFactory("ThrowableThreadPoolExecutor"),new  AbortPolicy());
 * new ExecutorThreadFactory("ThrowableThreadPoolExecutor")简单的封装了ThreadFactory
 * 由于SynchronousQueue并不是一个真正的队列,而是一种管理直接在线程间移交信息的机制,为了把一个元素放入到SynchronousQueue中,必须有另一个线程正在等待接受移交的任务.如果没有这样一个线程,只要当前池的大小还小于最大值,ThreadPoolExcutor就会创建一个新的线程；否则根据饱和策略,任务会被拒绝.而设置的饱和策略恰恰是new  AbortPolicy(),当线程池满了后,execute抛出未检查的RejectedExecutionException,线程丢失.可以通过捕获该异常做相应的补救处理.
 *  
 * 另外的处理方式是设置线程池的队列的饱和策略,线程池创建如下:
 * AbstractExecutorService executor = new ThrowableThreadPoolExecutor(10,40, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
 *             new ExecutorThreadFactory("ThrowableThreadPoolExecutor"), new ThreadPoolExecutor.CallerRunsPolicy());
 *  
 * 当任务向线程池请求要分配线程时,线程池处理如下:
 *  
 * 1、如果此时线程池中的数量小于corePoolSize,即使线程池中的线程都处于空闲状态,也要创建新的线程来处理被添加的任务
 * 2、如果此时线程池中的数量等于 corePoolSize,但是缓冲队列 workQueue未满,那么任务被放入缓冲队列
 * 
 * 3、 如果此时线程池中的数量大于corePoolSize,缓冲队列workQueue满,并且线程池中的数量小于maximumPoolSize,建新的线程来处理被添加的任务
 * 4、如果此时线程池中的数量大于corePoolSize,缓冲队列workQueue满,并且线程池中的数量等于maximumPoolSize,那么通过 handler所指定的策略来处理此任务.也就是:处理任务的优先级为:核心线程corePoolSize、任务队列workQueue、最大线程 maximumPoolSize,如果三者都满了,使用handler处理被拒绝的任务
 * 5、当线程池中的线程数量大于 corePoolSize时,如果某线程空闲时间超过keepAliveTime,线程将被终止.这样,线程池可以动态的调整池中的线程数
 * 
 * 1、    在默认的 ThreadPoolExecutor.AbortPolicy 中,处理程序遭到拒绝将抛出运行时 RejectedExecutionException.
 * 
 * 2、      在 ThreadPoolExecutor.CallerRunsPolicy 中,线程调用运行该任务的 execute 本身.此策略提供简单的反馈控制机制,能够减缓新任务的提交速度.
 * 
 * 3、      在 ThreadPoolExecutor.DiscardPolicy 中,不能执行的任务将被删除.
 * 
 * 4、      在 ThreadPoolExecutor.DiscardOldestPolicy 中,如果执行程序尚未关闭,则位于工作队列头部的任务将被删除,然后重试执行程序(如果再次失败,则重复此过程)
 */
public class FixedThreadPool implements ThreadPool {
	private static Logger log = Logger.getLogger(AppContent.calculatescorelog);

    /**
     * 线程池
     * @param threadName  线程的name的前缀
     * @param corePoolSize 线程池core_Pool_Size值
     * @param maximumPoolSize  线程池maximumPoolSize值
     * @param queues 线程池队列大小值
     * @param keepalive 线程空闲时间超过keepAliveTime,线程将被终止
     * @return
     */
    public Executor getExecutor(String threadName, int corePoolSize,int maximumPoolSize ,int queues,int keepalive) {
    	//线程的name的前缀
    	if(threadName==null || "".equals(threadName)){
    		threadName="calculatescore_history";
    	}
    	
    	//线程池maximumPoolSize值
    	if(corePoolSize<=0){
    		corePoolSize= 20;
    	}
    	
    	 //线程池队列大小值 
    	if(maximumPoolSize<=0){
    		maximumPoolSize= 20;
    	}
    	
    	//线程空闲时间超过keepAliveTime,线程将被终止
    	if(keepalive<=0){
    		keepalive= 60 * 1000;
    	}
        
        log.info("线程池类型=fixed"+"corePoolSize="+corePoolSize+",maximumPoolSize="+maximumPoolSize+",queueSize="+queues+",keepAliveTime(ms)="+keepalive);
        
        // 在 ThreadPoolExecutor.CallerRunsPolicy 中,线程调用运行该任务的 execute 本身.此策略提供简单的反馈控制机制,能够减缓新任务的提交速度.
        //RejectedExecutionHandler r=new ThreadPoolExecutor.CallerRunsPolicy();
        //处理程序遭到拒绝将抛出运行时 RejectedExecutionException.
        RejectedExecutionHandler r=new AbortPolicyWithReport(threadName);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepalive, TimeUnit.MILLISECONDS, 
                                      queues <= 0 ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues),
                               new NamedThreadFactory(threadName, false),  r);
    }
       
}