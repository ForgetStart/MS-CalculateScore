package com.hc360.score.threadpool;

import com.hc360.rsf.common.utils.NamedThreadFactory;
import com.hc360.score.common.AppContent;
import org.apache.log4j.Logger;

import java.util.concurrent.*;

/**
 * FixedThreadPool
 * 
 * ͨ�����·�ʽ�����̳߳�:
 * AbstractExecutorService executor=new ThreadPoolExecutor(3,10,30L,TimeUnit.SECONDS,new SynchronousQueue(),new ExecutorThreadFactory("ThrowableThreadPoolExecutor"),new  AbortPolicy());
 * new ExecutorThreadFactory("ThrowableThreadPoolExecutor")�򵥵ķ�װ��ThreadFactory
 * ����SynchronousQueue������һ�������Ķ���,����һ�ֹ���ֱ�����̼߳��ƽ���Ϣ�Ļ���,Ϊ�˰�һ��Ԫ�ط��뵽SynchronousQueue��,��������һ���߳����ڵȴ������ƽ�������.���û������һ���߳�,ֻҪ��ǰ�صĴ�С��С�����ֵ,ThreadPoolExcutor�ͻᴴ��һ���µ��̣߳�������ݱ��Ͳ���,����ᱻ�ܾ�.�����õı��Ͳ���ǡǡ��new  AbortPolicy(),���̳߳����˺�,execute�׳�δ����RejectedExecutionException,�̶߳�ʧ.����ͨ��������쳣����Ӧ�Ĳ��ȴ���.
 *  
 * ����Ĵ���ʽ�������̳߳صĶ��еı��Ͳ���,�̳߳ش�������:
 * AbstractExecutorService executor = new ThrowableThreadPoolExecutor(10,40, 30L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
 *             new ExecutorThreadFactory("ThrowableThreadPoolExecutor"), new ThreadPoolExecutor.CallerRunsPolicy());
 *  
 * ���������̳߳�����Ҫ�����߳�ʱ,�̳߳ش�������:
 *  
 * 1�������ʱ�̳߳��е�����С��corePoolSize,��ʹ�̳߳��е��̶߳����ڿ���״̬,ҲҪ�����µ��߳���������ӵ�����
 * 2�������ʱ�̳߳��е��������� corePoolSize,���ǻ������ workQueueδ��,��ô���񱻷��뻺�����
 * 
 * 3�� �����ʱ�̳߳��е���������corePoolSize,�������workQueue��,�����̳߳��е�����С��maximumPoolSize,���µ��߳���������ӵ�����
 * 4�������ʱ�̳߳��е���������corePoolSize,�������workQueue��,�����̳߳��е���������maximumPoolSize,��ôͨ�� handler��ָ���Ĳ��������������.Ҳ����:������������ȼ�Ϊ:�����߳�corePoolSize���������workQueue������߳� maximumPoolSize,������߶�����,ʹ��handler�����ܾ�������
 * 5�����̳߳��е��߳��������� corePoolSizeʱ,���ĳ�߳̿���ʱ�䳬��keepAliveTime,�߳̽�����ֹ.����,�̳߳ؿ��Զ�̬�ĵ������е��߳���
 * 
 * 1��    ��Ĭ�ϵ� ThreadPoolExecutor.AbortPolicy ��,��������⵽�ܾ����׳�����ʱ RejectedExecutionException.
 * 
 * 2��      �� ThreadPoolExecutor.CallerRunsPolicy ��,�̵߳������и������ execute ����.�˲����ṩ�򵥵ķ������ƻ���,�ܹ�������������ύ�ٶ�.
 * 
 * 3��      �� ThreadPoolExecutor.DiscardPolicy ��,����ִ�е����񽫱�ɾ��.
 * 
 * 4��      �� ThreadPoolExecutor.DiscardOldestPolicy ��,���ִ�г�����δ�ر�,��λ�ڹ�������ͷ�������񽫱�ɾ��,Ȼ������ִ�г���(����ٴ�ʧ��,���ظ��˹���)
 */
public class FixedThreadPool implements ThreadPool {
	private static Logger log = Logger.getLogger(AppContent.calculatescorelog);

    /**
     * �̳߳�
     * @param threadName  �̵߳�name��ǰ׺
     * @param corePoolSize �̳߳�core_Pool_Sizeֵ
     * @param maximumPoolSize  �̳߳�maximumPoolSizeֵ
     * @param queues �̳߳ض��д�Сֵ
     * @param keepalive �߳̿���ʱ�䳬��keepAliveTime,�߳̽�����ֹ
     * @return
     */
    public Executor getExecutor(String threadName, int corePoolSize,int maximumPoolSize ,int queues,int keepalive) {
    	//�̵߳�name��ǰ׺
    	if(threadName==null || "".equals(threadName)){
    		threadName="calculatescore_history";
    	}
    	
    	//�̳߳�maximumPoolSizeֵ
    	if(corePoolSize<=0){
    		corePoolSize= 20;
    	}
    	
    	 //�̳߳ض��д�Сֵ 
    	if(maximumPoolSize<=0){
    		maximumPoolSize= 20;
    	}
    	
    	//�߳̿���ʱ�䳬��keepAliveTime,�߳̽�����ֹ
    	if(keepalive<=0){
    		keepalive= 60 * 1000;
    	}
        
        log.info("�̳߳�����=fixed"+"corePoolSize="+corePoolSize+",maximumPoolSize="+maximumPoolSize+",queueSize="+queues+",keepAliveTime(ms)="+keepalive);
        
        // �� ThreadPoolExecutor.CallerRunsPolicy ��,�̵߳������и������ execute ����.�˲����ṩ�򵥵ķ������ƻ���,�ܹ�������������ύ�ٶ�.
        //RejectedExecutionHandler r=new ThreadPoolExecutor.CallerRunsPolicy();
        //��������⵽�ܾ����׳�����ʱ RejectedExecutionException.
        RejectedExecutionHandler r=new AbortPolicyWithReport(threadName);
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepalive, TimeUnit.MILLISECONDS, 
                                      queues <= 0 ? new SynchronousQueue<Runnable>() : new LinkedBlockingQueue<Runnable>(queues),
                               new NamedThreadFactory(threadName, false),  r);
    }
       
}