package com.hc360.score.task;

import org.apache.log4j.Logger;

public class SumScore {

    private static Logger logger = Logger.getLogger(SumScore.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("SumScore start............");

            //1、分页查询商机质量星级表（busin_quality_star），
            //   获取HADOOP_STATE为1并且JOB_STATE为1、bcid尾号为args[0]的记录
            //2、汇总单项分到总分，计算星级
            //3、批量更新总分、星级与HADOOP_STATE、JOB_STATE状态，将HADOOP_STATE、JOB_STATE改为2

            logger.info("SumScore end...............");
            logger.info("SumScore use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
