package com.hc360.score.task;

import org.apache.log4j.Logger;

public class SumScore {

    private static Logger logger = Logger.getLogger(SumScore.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("SumScore start............");

            //1����ҳ��ѯ�̻������Ǽ���busin_quality_star����
            //   ��ȡHADOOP_STATEΪ1����JOB_STATEΪ1��bcidβ��Ϊargs[0]�ļ�¼
            //2�����ܵ���ֵ��ܷ֣������Ǽ�
            //3�����������ܷ֡��Ǽ���HADOOP_STATE��JOB_STATE״̬����HADOOP_STATE��JOB_STATE��Ϊ2

            logger.info("SumScore end...............");
            logger.info("SumScore use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
