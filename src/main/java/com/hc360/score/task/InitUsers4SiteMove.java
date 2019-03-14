package com.hc360.score.task;

import java.util.List;

import org.apache.log4j.Logger;

import com.hc360.b2b.exception.MmtException;
import com.hc360.score.message.service.InitTaskManage;

/**
 * 对站外搬家的用户，筛选出有0星商机的用户，对其全部进行重新算分
 * 
    select count(1) from on_busin_chance a where a.whoinput='搬家工具' and a.star='0' and a.pubdate >=sysdate-1
 * @author 李萌
 *
 */
public class InitUsers4SiteMove {

	private static Logger log = Logger.getLogger("initUsers4SiteMove") ;
	
	public static void main(String[] args) throws MmtException {
		//获取若干个用户的id
		List<Long> userIdList ;
		if(args != null && args.length > 1){
			userIdList = InitTaskManage.getInstance().getUsersToday4SiteMove(args[0],args[1]) ;
		}else{
			userIdList = InitTaskManage.getInstance().getUsersToday4SiteMove(null ,null) ;
		}
		log.info("共取出" + userIdList.size() + "个用户，开始对这些用户进行重新算分") ;
		//对每个用户下的所有商机初始化分数
		for(long userId : userIdList){
			try{
				InitTaskManage.getInstance().initUserBusinScore(userId) ;
			}catch(Exception e){
				log.error(e) ;
			}
		}
		log.info("本次" + userIdList.size() + "个用户重新算分完成") ;
	}

}
