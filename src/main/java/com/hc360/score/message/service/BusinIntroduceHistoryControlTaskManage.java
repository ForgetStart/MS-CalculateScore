package com.hc360.score.message.service;

import com.hc360.mmt.db.po.statdb.BusinDataTask;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.StatDao;
import org.apache.log4j.Logger;

import java.sql.Timestamp;
import java.util.Date;

public class BusinIntroduceHistoryControlTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	private static BusinIntroduceHistoryControlTaskManage instance = new BusinIntroduceHistoryControlTaskManage();
	public static BusinIntroduceHistoryControlTaskManage getInstance(){
		return instance;
	}


	public boolean dealBusinIntroduceTask(){

		// 总分
		try {
            long startTask = System.currentTimeMillis();

            while(true){
                Thread.sleep(5*60*1000);
                if((System.currentTimeMillis() - startTask)>=10*3600*1000){
                    logger.info("BusinIntroduceHistoryControlThread states=1 start............");
                    BusinDataTask bdt=StatDao.getInstance(false).getBusinIntroduceTask("BusinIntroduceTask");
                    if(bdt!=null&&"0".equals(bdt.getStates())){
                        //BusinIntroduceHistoryTaskManage.TASK_STATES=false;
                        bdt.setStates("1");
                        bdt.setModifydate(new Timestamp(new Date().getTime()));
                        StatDao.getInstance(false).updateBusinIntroduceTask(bdt);
                    }else{
                        logger.error("查不到商机详情信息任务状态");
                    }
                    logger.info("BusinIntroduceHistoryControlThread states=1 end............");
                    startTask=System.currentTimeMillis();
                }else{
                    BusinDataTask bdt=StatDao.getInstance(false).getBusinIntroduceTask("BusinIntroduceTask");
                    if(bdt!=null&&"0".equals(bdt.getStates())){
                        //BusinIntroduceHistoryTaskManage.TASK_STATES=true;
                    }
                }
            }
		} catch (Exception e) {
            logger.error("处理商机详情信息任务状态异常"+e.getMessage());
			e.printStackTrace();
            return false;
		}
	}
	
}
