package com.hc360.score.task;

import org.apache.log4j.Logger;

public class ExportScore {

    private static Logger logger = Logger.getLogger(ExportScore.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("ExportScore start............");

            //1����ҳ��ѯ�̻������Ǽ���busin_quality_star����
            //   ��ȡHADOOP_STATEΪ2����JOB_STATEΪ2��bcidβ��Ϊargs[0]�ļ�¼
            //2��������CVS�ļ��У�������bc_id,score,star�ֶΣ�

            logger.info("ExportScore end...............");
            logger.info("ExportScore use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
