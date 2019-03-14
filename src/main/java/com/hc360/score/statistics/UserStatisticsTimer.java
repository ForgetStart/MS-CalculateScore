package com.hc360.score.statistics;

import java.util.Calendar;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.MessageHandler;

/**
 * 统计用户每天的操作，统计频率为每天一次，统计时间为每晚的23点
 * @author hc360
 *
 */
public class UserStatisticsTimer extends TimerTask{
	private static Logger calculatescoreLog = Logger.getLogger(AppContent.calculatescorelog);
	private static Logger userLog = Logger.getLogger(AppContent.userlog);

	@Override
	public void run() {
		try{
			Map<Long, UserMessage> userMap = MessageHandler.userMap;
			MessageHandler.userMap = new ConcurrentHashMap<Long, UserMessage>();
		
			PeriodStatistics ps = new PeriodStatistics();
			String userStatistics = ps.getUserStatistics(userMap);
			
			userLog.info(userStatistics);
			
//			for(Entry<Long, UserMessage> userMessage : userMap.entrySet()){
//				UserMessage um = userMessage.getValue();
//				userLog.info(um.toString());
//				sb.append(um.toString()+"\n");
//			}
			//发监控统计
			StatisticsHandler.send("CalculateScore-User", userStatistics);
		}catch(Exception e){
			e.printStackTrace();
			calculatescoreLog.error("统计用户每天的操作异常：",e);
		}
	}

	public static void start() {
		Calendar firstTime = Calendar.getInstance();
		firstTime.set(Calendar.HOUR_OF_DAY, 23);
		firstTime.set(Calendar.MINUTE, 0);

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new UserStatisticsTimer(), firstTime.getTime(), 24*60*60*1000);
//		timer.scheduleAtFixedRate(new UserStatisticsTimer(), firstTime.getTime(), 5*60*1000);
		calculatescoreLog.info("启动用户统计定时器成功，统计用户每天的操作，统计频率为每天一次，统计时间为每晚的23点 ");
	}

}
