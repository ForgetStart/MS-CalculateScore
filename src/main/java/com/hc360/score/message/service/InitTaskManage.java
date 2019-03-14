package com.hc360.score.message.service;

import java.util.List;
import java.util.Map;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.po.BaseQueryObject;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.db.po.proddb.OnBusinChance;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.message.Transinformation;
import com.hc360.score.message.handler.UserScoreHandler;
import com.hc360.score.statistics.BusinessRecord;

public class InitTaskManage {
	
	private static InitTaskManage instance = new InitTaskManage();
	public static InitTaskManage getInstance(){
		return instance;
	}

	public List<Long> getUsersToday4SiteMove(String startDate, String endDate)
			throws MmtException {

		return ProdDao.getInstance(false).getUsersToday4SiteMove(startDate,
				endDate);
	}

	public List<Long> getProviderIdByHour4SiteMove(String startDate)
			throws MmtException {

		return CorpDao.getInstance(false).getProviderIdByHour4SiteMove(
				startDate);
	}

	public List<Long> getUsersByproviderId4SiteMove(long providerid)
			throws MmtException {

		return ProdDao.getInstance(false).getUsersByProviderId4SiteMove(
				providerid);
	}

	/**
	 * 初始化用户userBusinScore
	 * 如果从hbase中没有userBusinScore，为初始化后脏数据，为避免脏数据，从数据库中重新加载商机数据
	 * ，重新计算userBusinScore 包括有效商机
	 * 
	 * @param userBusinScore
	 * @throws Exception 
	 */
	public boolean initUserBusinScore(long userid){

		Transinformation traninfo = new Transinformation(new BusinInfo());
		traninfo.setBusinessRecord(new BusinessRecord("0",0,System.currentTimeMillis()));
		
		// 总分
		try {
			Map<Long, OnBusinChance> list = ProdDao.getInstance(false).getAllBusinChanceList(userid);
			if (list != null && list.size() > 0) {
				System.out.println("用户商机信息 userid="+userid+", 商机条数="+list.size());
				double newsumscore = 0.0;
				//循环保存
				
				for (OnBusinChance busin : list.values()) {
					BusinScore bs = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
					newsumscore += bs.getScore();
					System.out.print(busin.getId()+"-");
				}
				System.out.println();
				System.out.println("循环执行每条商机成功！");
				UserBusinScore userBusinScore = new UserBusinScore();
				userBusinScore.setUserid(userid);
				userBusinScore.setBusincount(list.size());
				userBusinScore.setBusinscore(newsumscore);
				if (userBusinScore.getBusincount() <= 0) {
					userBusinScore.setBusinarvgscore(0.0);
				} else {
					userBusinScore.setBusinarvgscore(userBusinScore.getBusinscore() / userBusinScore.getBusincount());
				}
				TableInfo tableInfo = new TableInfo();
				tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
				tableInfo.setRowKey(String.valueOf(userid));

				UserScoreManage.getInstance().setUserBusinScore(tableInfo, userBusinScore);
				System.out.println("保存用户信息成功");
				
				/**
				 * 更新成长状态
				 * 获取成长状态
				 */
				tableInfo = new TableInfo();
				tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); //由hrtc_rdsl_growstatus修改为hrtc_rdsl_growstatus_reverse
				tableInfo.setRowKey(String.valueOf(userid));
				tableInfo.setFamilyName("info");
				tableInfo.setColumnName("status");
				//该用户当前成长状态
				String status = HBaseUtilHelper.getData(tableInfo,true); //需要反转
				
				BaseQueryObject queryobject = new BaseQueryObject();
				queryobject.setKey(String.valueOf(userid));
				//如果没升过级，可以满足最低升级条件，就查询判断
				System.out.println("做升级判断 status="+status);
				UserScoreHandler ush = new UserScoreHandler();
				if(status==null){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,3);								
				}else if(String.valueOf(AppContent.USER_STATUS_INTRO).equals(status)){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,3);
				}else if(String.valueOf(AppContent.USER_STATUS_DEVE).equals(status)){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,4);
				}
				//所有都做降级判断
				System.out.println("所有都做降级判断 status="+status);
				UserStateManage.getInstance().downlevel(status,userBusinScore);
				
			}else{
				System.out.println("该用户没有商机信息 userid="+userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * 根据日期，选择这个日期下 
	 * @param starDate
	 * @param endDate
	 * @return
	 * @throws MmtException
	 */
	public List<String> getUserByDate(String starDate,String endDate) throws MmtException{

		List list =  ProdDao.getInstance(false).getUserByDate(starDate, endDate);
		List list2 = CorpDao.getInstance(false).findUserAveragequalityIsNull();
		
		list.addAll(list2);
		
		return list;
	}
	
}
