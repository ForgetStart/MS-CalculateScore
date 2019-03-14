package com.hc360.score.message;

import java.io.Serializable;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.log4j.Logger;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.jms.JMSConsumer;
import com.hc360.jms.activemq.ActiveMQ;
import com.hc360.jms.activemq.consumer.JMSMessageListener;
import com.hc360.score.common.AppContent;
import com.hc360.storm.hbase.JsonUtils;
/**
 * 从broker获取消息，放入队列，等待消费处理
 * @author hc360
 *
 */
public class MessageConsumer extends JMSMessageListener{
	private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);
	
	public static void start() {
		try {
			JMSConsumer jconsumer = new JMSConsumer("CalculateScoreConsumer","storm.business.queue",ActiveMQDestination.QUEUE_TYPE,new MessageConsumer());
			ActiveMQ.initConsumer(jconsumer);
			
			logger.info("Create JMSConsumer success,queue for 'storm.business.queue'");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("创建JMSConsumer时失败，异常信息：", e);
		}
	}	

	@Override
	public Serializable doMessage(Serializable msg) {
		// 只要队列未满，就将消息放入队列中；如果队列满了就阻塞，直到有空间为止。
		try{
			if(msg instanceof String){
				String message = (String)msg;
				Object object = JsonUtils.changeJson(message);
				BusinInfo busininfo = (BusinInfo)object;
				
				MessageHandler.blockQueue.put(busininfo);
			}else if(msg instanceof BusinInfo){
				BusinInfo busininfo = (BusinInfo)msg;
				
				MessageHandler.blockQueue.put(busininfo);
			}else{
				logger.error("The message type is not string: "+msg.toString());
			}
		}catch(Exception e){ 
			e.printStackTrace();
			logger.error("handler message exception:", e);
			throw new RuntimeException();
		}
		return null;
	}
}