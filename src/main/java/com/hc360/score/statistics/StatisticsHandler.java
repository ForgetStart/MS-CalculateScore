package com.hc360.score.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.log4j.Logger;

import com.hc360.jms.JMSProducer;
import com.hc360.jms.activemq.ActiveMQ;
import com.hc360.jms.activemq.utils.Constants;
import com.hc360.statistics.StatisticsConsumer;
import com.hc360.statistics.StatisticsProducer;

public class StatisticsHandler {
    private static Logger logger;
	private static int interval;
	private static long[] points;
	public static ConcurrentLinkedQueue<Statistics> stQueue = new ConcurrentLinkedQueue<Statistics>();
	
	/**
	 * 自增，单纯的计数
	 * @param name
	 */
	public static void increment(String name){
		Statistics st = new Statistics(name,1);
		stQueue.add(st);
	}
	/**
	 * 自增，计数并区分成功和失败
	 * @param name
	 * @param result
	 */
	public static void increment(String name,boolean result){
		Statistics st = new Statistics(name,result,2);
		stQueue.add(st);
	}
	/**
	 * 计数，带结果和响应时间
	 * @param name
	 * @return
	 */
	public static Statistics createStatistics(String name){
		Statistics st = new Statistics(name,3);
		st.setTime(System.currentTimeMillis());
		return st;
	}
	
	public static void start(String logName, int statisticsInterval, long[] points){
		 logger = Logger.getLogger(logName);
		 interval = statisticsInterval;
		 if(points == null) points = new long[0];
		 StatisticsHandler.points = points;
		 start();
	}
	private static void start(){
		//统计用户的操作
		UserStatisticsTimer.start();
		//启动统计线程
		ExecutorService stexecutor = Executors.newSingleThreadExecutor();
		logger.info("创建独立后台线程，进行统计工作。。。。");
		stexecutor.execute(new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(interval);
						Object[] items = stQueue.toArray();
						stQueue.clear(); //清空重新统计
						
						StringBuffer sb = new StringBuffer();
						sb.append("totalCount").append("=").append(items.length).append(", ");
						
						String str = getEach(statistics(items));
						sb.append(str);
						
						logger.info(sb.toString());
					}catch(Exception e){
						logger.error("统计异常：",e);
					}
				}
			}

			private Map<String,Total> statistics(Object[] items){
				Map<String,Total> totals = new HashMap<String,Total>();
				for(Object obj : items){
					Statistics s = (Statistics)obj;
					//不记录统计信息
					//if(s.getName().contains("jms-statistcs")) continue;
					
					Total t = totals.get(s.getName());
					
					if(t==null) t = new Total();
					
					t.type = s.getType();
					if(s.getType()==1){
						t.total++;
					}else{
						if(s.isResult()) t.success++;
						else t.fail++;
						
						if(s.getType()==3) t.times.add(s.getTime());
					}
					totals.put(s.getName(), t);
				}
				return totals;
			}
			private String getEach(Map<String,Total> totals){
				
				StringBuffer sb = new StringBuffer();
				for(Map.Entry<String,Total> total: totals.entrySet()){
					String name = total.getKey();
					Total t = total.getValue();
//					sb.append(System.getProperty("line.separator"));
					if(t.type!=1){
						sb.append(name).append("_success_count=").append(t.success).append(",  ");
						sb.append(name).append("_fail_count=").append(t.fail).append(",  ");
						if(t.type==3){
							sb.append(getTimes(name,t.times));
						}
					}else{
						sb.append(name).append("_count=").append(t.total).append(",  ");
					}
				}
				return sb.toString();
			}
			private String getTimes(String name, List<Long> times){
				StringBuffer sb = new StringBuffer();
				if(times!=null && times.size()>0){
					Object[] restimes = times.toArray();
					int length = restimes.length;
					Arrays.sort(restimes);
					sb.append(name).append("_time_min=").append(restimes[0]).append(",  ");
					sb.append(name).append("_time_max=").append(restimes[length-1]).append(",  ");
					sb.append(name).append("_time_%90=").append(restimes[length*90/100]).append(",  ");
					
					for(long point : points){
						int index = Arrays.binarySearch(restimes, point); //查找大于point的位置
				        if(index<0){
				        	index = Math.abs(index)-1;
				        }
				        sb.append(name).append("_time_gt").append(point).append("=").append(length-index).append(",  ");
					}
				}else{
					sb.append(name).append("_time_min=").append("0,  ");
					sb.append(name).append("_time_max=").append("0,  ");
					sb.append(name).append("_time_%90=").append("0,  ");
				}
				return sb.toString();
			}
		});
	}
	
	public static void send(String name, String context){
		try{
			JMSProducer jpq = ActiveMQ.createProducer("CalculateScore",Constants.JMS_STATISTCS, ActiveMQDestination.QUEUE_TYPE);
			String statistics = (new Date().toLocaleString())+" "+StatisticsProducer.ips+" "+ context;
			StatisticsConsumer sc = new StatisticsConsumer(null, name, statistics);
			jpq.asynchSend(sc, null);
		}catch(Exception e){
			e.printStackTrace();
			
		}
	}
	public static void sendEmail(Object obj){
//		try{
//			JMSProducer jpq = ActiveMQ.createProducer("CalculateScore",Constants.JMS_STATISTCS, ActiveMQDestination.QUEUE_TYPE);
//			String statistics = (new Date().toLocaleString())+" "+StatisticsProducer.ips+" "+obj.toString();
//			StatisticsConsumer sc = new StatisticsConsumer(null, name, statistics);
//			jpq.asynchSend(sc, null);
//		}catch(Exception e){
//			e.printStackTrace();
//			
//		}
	}
}
/*class Total{
    int type = 0;
    int total = 0;
    int success = 0;
    int fail = 0;
    List<Long> times = new ArrayList<Long>();
}*/
