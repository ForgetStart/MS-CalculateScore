package com.hc360.score.task;

import java.util.List;

import org.apache.log4j.Logger;

import com.hc360.b2b.exception.MmtException;
import com.hc360.score.message.service.InitTaskManage;

/**
 * ��վ���ҵ��û���ɸѡ����0���̻����û�������ȫ�������������
 * 
    select count(1) from on_busin_chance a where a.whoinput='��ҹ���' and a.star='0' and a.pubdate >=sysdate-1
 * @author ����
 *
 */
public class InitUsers4SiteMove2 {

	private static Logger log = Logger.getLogger("initUsers4SiteMove") ;
	
	public static void main(String[] args) throws MmtException {
		//��ȡ���ɸ��û���id
		List<Long> provideridList ;
		if(args != null && args.length>0){
			provideridList = InitTaskManage.getInstance().getProviderIdByHour4SiteMove(args[0]) ;
		}else{
			provideridList = InitTaskManage.getInstance().getProviderIdByHour4SiteMove(null) ;
		}
		//��ȡ���ɸ��û���id
		
		List<Long> userIdList ;
		for(long  providerid : provideridList){
			try{
				userIdList = InitTaskManage.getInstance().getUsersByproviderId4SiteMove(providerid) ;

				//��ÿ���û��µ������̻���ʼ������
				for(long userId : userIdList){
					try{
						InitTaskManage.getInstance().initUserBusinScore(userId) ;
					}catch(Exception e){
						log.error(e) ;
					}
				}
			}catch(Exception e){
				log.error(e) ;
			}
		}

	}
}
