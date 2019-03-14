package com.hc360.score.task;

import org.apache.log4j.Logger;

public class ExportScore {

    private static Logger logger = Logger.getLogger(ExportScore.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("ExportScore start............");

            //1、分页查询商机质量星级表（busin_quality_star），
            //   获取HADOOP_STATE为2并且JOB_STATE为2、bcid尾号为args[0]的记录
            //2、导出到CVS文件中，包括（bc_id,score,star字段）

            logger.info("ExportScore end...............");
            logger.info("ExportScore use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
