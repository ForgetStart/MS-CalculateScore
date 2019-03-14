package com.hc360.score.task;

import org.apache.log4j.Logger;

public class ModifyStar {

    private static Logger logger = Logger.getLogger(ModifyStar.class);

	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("ModifyStar start............");

            //1、分页查询在线免费会员，尾号为args[0]的记录
            //2、分页查询免费会员的商机质量星级信息
            //2、批量更新商机的星级

            logger.info("ModifyStar end...............");
            logger.info("ModifyStar use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}