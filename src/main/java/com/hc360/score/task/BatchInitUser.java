package com.hc360.score.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import com.hc360.hbase.po.UserBusinScore;
import com.hc360.score.message.service.InitTaskManage;

public class BatchInitUser {
	public static AtomicInteger index = new  AtomicInteger();
	public static String[] uis = null;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BatchInitUser biu = new BatchInitUser();
		String userids = biu.getStrFromFile();
		System.out.println(userids);
		 uis = userids.split(",");
		
		try{
			ExecutorService executor = Executors.newFixedThreadPool(8) ;
			for(int i=0;i<8;i++){
				executor.execute(new InitUserTask());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private String getStrFromFile(){
		StringBuffer sb = new StringBuffer();
		try{
			File file = new File(("bin/userid.txt"));
			System.out.println(file.getCanonicalPath());
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String userid = br.readLine();
			while(userid != null){
				sb.append(userid);
				userid = br.readLine();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sb.toString();
	}
	public static void initOneUser(String userId) {
		try{
			UserBusinScore userBusinScore = new UserBusinScore();			
			InitTaskManage.getInstance().initUserBusinScore(Long.parseLong(userId));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
class InitUserTask implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		int i = BatchInitUser.index.getAndIncrement();
		int length = BatchInitUser.uis.length;
		
		while(i<length){

			int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
			System.out.print("zhanglf---i="+i+"---hour=-"+hour);
			if(hour>7 && hour<18){
				System.out.println("---sleeping....");
				try {
					Thread.sleep(60*60*1000);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
					continue;
				}
			}
			String uid = BatchInitUser.uis[i];
			System.out.println("zhanglf-----"+i+"="+uid);
			BatchInitUser.initOneUser(uid);
			System.out.println("zhanglf-----"+i+"="+uid+"--init over!!");
			i = BatchInitUser.index.getAndIncrement();
			
		}
	}
	
}