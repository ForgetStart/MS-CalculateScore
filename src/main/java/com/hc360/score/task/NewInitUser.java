package com.hc360.score.task;

import com.hc360.score.message.service.InitTaskManage;

public class NewInitUser {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
			System.out.println("NewInitUser start............");
			System.out.println("user id ="+args[0]);
			
			String userId = args[0];
			InitTaskManage.getInstance().initUserBusinScore(Long.parseLong(userId));
			
			System.out.println("NewInitUser end...............");
			System.out.println("UserBusinScore use time:"+(System.currentTimeMillis()-start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
