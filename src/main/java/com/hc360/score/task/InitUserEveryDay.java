/**
 * 
 */
package com.hc360.score.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.mmt.memcached.MemcachedHelper;
import com.hc360.mmt.memcached.mo.user.FreeUserRealtimeDataMO;
import com.hc360.score.message.service.InitTaskManage;

/**
 *  ��ʼ�������û� 
 *
 */
public class InitUserEveryDay {
	public static void main(String[] args) {
		
		System.out.print("InitUserEveryDay start.............");

		try{
			String startDate ="2014-05-07_13:23:44"; 
			String endDate   ="2014-05-08_13:23:44";
			if(args!=null && args.length==2){//�ж��Ƿ���������� 
				startDate = args[0];
				endDate = args[1];
			}else{//û�д�ֵ�������³�ʼ�������
				SimpleDateFormat nowDay=new SimpleDateFormat("yyyy-MM-dd");
				startDate =  nowDay.format(new Date()) + "_00:01:01";
				endDate   =  nowDay.format(new Date()) + "_23:59:59";			
			}
			
			List<String> list = InitTaskManage.getInstance().getUserByDate(startDate, endDate) ;
			System.out.println(startDate+"  ��ȡ�û����� ="+list.size());
			
			for(int i=0;i<list.size();i++){
				try{
					String userId = list.get(i);
					System.out.println("�������û� UserId=" + userId );
					UserBusinScore userBusinScore = new UserBusinScore();
					userBusinScore.setUserid(Long.parseLong(userId));
					InitTaskManage.getInstance().initUserBusinScore(Long.parseLong(userId));
					
					//������� 
					FreeUserRealtimeDataMO fmo = new FreeUserRealtimeDataMO();
					MemcachedHelper.delete(userId,fmo,true );
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		System.out.print("InitUserEveryDay end.............");
		System.exit(0);
	}

}
