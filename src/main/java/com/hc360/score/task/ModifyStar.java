package com.hc360.score.task;

import org.apache.log4j.Logger;

public class ModifyStar {

    private static Logger logger = Logger.getLogger(ModifyStar.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("ModifyStar start............");

            //1����ҳ��ѯ������ѻ�Ա��β��Ϊargs[0]�ļ�¼
            //2����ҳ��ѯ��ѻ�Ա���̻������Ǽ���Ϣ
            //2�����������̻����Ǽ�

            logger.info("ModifyStar end...............");
            logger.info("ModifyStar use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}