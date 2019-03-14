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
public class InitUsers4SiteMove {

	private static Logger log = Logger.getLogger("initUsers4SiteMove") ;
	
	public static void main(String[] args) throws MmtException {
		//��ȡ���ɸ��û���id
		List<Long> userIdList ;
		if(args != null && args.length > 1){
			userIdList = InitTaskManage.getInstance().getUsersToday4SiteMove(args[0],args[1]) ;
		}else{
			userIdList = InitTaskManage.getInstance().getUsersToday4SiteMove(null ,null) ;
		}
		log.info("��ȡ��" + userIdList.size() + "���û�����ʼ����Щ�û������������") ;
		//��ÿ���û��µ������̻���ʼ������
		for(long userId : userIdList){
			try{
				InitTaskManage.getInstance().initUserBusinScore(userId) ;
			}catch(Exception e){
				log.error(e) ;
			}
		}
		log.info("����" + userIdList.size() + "���û�����������") ;
	}

}
