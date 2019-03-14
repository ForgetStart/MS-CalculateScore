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
	 * ��ʼ���û�userBusinScore
	 * �����hbase��û��userBusinScore��Ϊ��ʼ���������ݣ�Ϊ���������ݣ������ݿ������¼����̻�����
	 * �����¼���userBusinScore ������Ч�̻�
	 * 
	 * @param userBusinScore
	 * @throws Exception 
	 */
	public boolean initUserBusinScore(long userid){

		Transinformation traninfo = new Transinformation(new BusinInfo());
		traninfo.setBusinessRecord(new BusinessRecord("0",0,System.currentTimeMillis()));
		
		// �ܷ�
		try {
			Map<Long, OnBusinChance> list = ProdDao.getInstance(false).getAllBusinChanceList(userid);
			if (list != null && list.size() > 0) {
				System.out.println("�û��̻���Ϣ userid="+userid+", �̻�����="+list.size());
				double newsumscore = 0.0;
				//ѭ������
				
				for (OnBusinChance busin : list.values()) {
					BusinScore bs = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
					newsumscore += bs.getScore();
					System.out.print(busin.getId()+"-");
				}
				System.out.println();
				System.out.println("ѭ��ִ��ÿ���̻��ɹ���");
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
				System.out.println("�����û���Ϣ�ɹ�");
				
				/**
				 * ���³ɳ�״̬
				 * ��ȡ�ɳ�״̬
				 */
				tableInfo = new TableInfo();
				tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); //��hrtc_rdsl_growstatus�޸�Ϊhrtc_rdsl_growstatus_reverse
				tableInfo.setRowKey(String.valueOf(userid));
				tableInfo.setFamilyName("info");
				tableInfo.setColumnName("status");
				//���û���ǰ�ɳ�״̬
				String status = HBaseUtilHelper.getData(tableInfo,true); //��Ҫ��ת
				
				BaseQueryObject queryobject = new BaseQueryObject();
				queryobject.setKey(String.valueOf(userid));
				//���û������������������������������Ͳ�ѯ�ж�
				System.out.println("�������ж� status="+status);
				UserScoreHandler ush = new UserScoreHandler();
				if(status==null){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,3);								
				}else if(String.valueOf(AppContent.USER_STATUS_INTRO).equals(status)){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,3);
				}else if(String.valueOf(AppContent.USER_STATUS_DEVE).equals(status)){
					ush.buildBolot(traninfo, queryobject,userBusinScore,5,4);
				}
				//���ж��������ж�
				System.out.println("���ж��������ж� status="+status);
				UserStateManage.getInstance().downlevel(status,userBusinScore);
				
			}else{
				System.out.println("���û�û���̻���Ϣ userid="+userid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	/**
	 * �������ڣ�ѡ����������� 
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
