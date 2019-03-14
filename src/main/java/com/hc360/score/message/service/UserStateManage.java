package com.hc360.score.message.service;

import java.sql.Timestamp;

import com.hc360.b2b.util.DateUtils;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.po.BaseQueryObject;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.db.po.corpdb.FreeUserState;
import com.hc360.mmt.db.po.corpdb.UserAveragequalitylog;
import com.hc360.mmt.db.po.corpdb.UserGrowState;
import com.hc360.mmt.memcached.MemcachedHelper;
import com.hc360.mmt.memcached.mo.user.FreeUserRealtimeDataMO;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;

/**
 * �ƽ�����manage
 * 
 * @author saiwengang
 *
 */
public class UserStateManage {

	private static UserStateManage instance = new UserStateManage();
	public static UserStateManage getInstance(){
		return instance;
	}
	
	public void handleUserState(BaseQueryObject queryobject)throws Exception{
		String userid = queryobject.getKey();
		/*** �ж��Ƿ����� ���н���   */
		if (queryobject.getQueryMap().size() != 0) {
			Object status = queryobject.queryMap.get(AppContent.QUERYTYPE.READUSERSTATES.name());
			long userStatus = AppContent.USER_STATUS_INTRO;
			if (status != null) {
				userStatus = (Long) status;
			}
			// ���û�״̬
			Object userbusincount = queryobject.queryMap.get(AppContent.QUERYTYPE.READBUSINCOUNT.name());
			long busincount = 0;
			if (userbusincount != null) {
				busincount = (Long) userbusincount;
			}
			Object averagescore = queryobject.queryMap.get(AppContent.QUERYTYPE.READUSERAVERAGESCORE.name());
			double score = 0.0;
			if (averagescore != null) {
				score = (Double) averagescore;
			}
			Object companycompass = queryobject.queryMap.get(AppContent.QUERYTYPE.READCOMANYCOMPASS.name());
			int compass = 0;
			if (companycompass != null) {
				compass = (Integer) companycompass;
			}
			Object hasobject = queryobject.queryMap.get(AppContent.QUERYTYPE.READMATCHINFO.name());
			boolean hasmatch = false;
			if (hasobject != null) {
				hasmatch = (Boolean) hasobject;
			}
			Object levelobject = queryobject.queryMap.get(AppContent.QUERYTYPE.READMATCHREAD.name());
			boolean level = false;
			if (levelobject != null) {
					level = (Boolean) levelobject;
			}
			Object daysobject = queryobject.queryMap.get(AppContent.QUERYTYPE.READUSERLOGIN.name());
			long days = 0;
			if (daysobject != null) {
				days = (Long) daysobject;
			}
			Timestamp updevdate = null;// ��չ��ʱ��
			Timestamp upposdate = null; // ������ʱ��

			UserAveragequalitylog useraverquality = CorpDao.getInstance(false).findUserAveragequality(userid);
			busincount = useraverquality.getBusincount();
			double d1 = busincount==0 ? 0 : useraverquality.getSumstar() / busincount;
			int star = (int) Math.floor(d1);
			// �Ƿ�����
			boolean hasup = false;
			// ��չ��
			if (busincount >= 5 && star >= 3 && compass >= 1) {
				// ����
				if (userStatus <= AppContent.USER_STATUS_DEVE) {
					userStatus = AppContent.USER_STATUS_DEVE;
					updevdate = DateUtils.getSysTimestamp();
					hasup = true;
				}
			}
			// �����ж�
			if (busincount >= 50 && star >= 4 && compass == 3 && hasmatch && level && days >= 3) {
				// ����������
				if (userStatus <= AppContent.USER_STATUS_RECOMM) {
					userStatus = AppContent.USER_STATUS_RECOMM;
					upposdate = DateUtils.getSysTimestamp();
					hasup = true;
				}
			}
			// �ж��û�ÿ���ֶ�״̬
			if (userStatus == AppContent.USER_STATUS_DEVE) {// ��չ�ڽ�����׼
				downLevel(
						busincount < 5 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						star < 3 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						(compass == 2 || compass == 0) ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!hasmatch ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!level ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						days < 3 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
								userid,userStatus, updevdate, upposdate);
			} else if (userStatus == AppContent.USER_STATUS_RECOMM) {// �����ڽ�����׼
				downLevel(
						busincount < 50 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						star < 4 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						(compass < 3) ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!hasmatch ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!level ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						days < 3 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
								userid, userStatus, updevdate, upposdate);
			}

			/**
			 * �����������Ҫ���
			 */
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); // ��hrtc_rdsl_growstatus�޸�Ϊhrtc_rdsl_growstatus_reverse
			tableInfo.setRowKey(userid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("status");
			tableInfo.setValue(String.valueOf(userStatus));
			HBaseUtilHelper.addRecord(tableInfo, true);// ��� ,��Ҫ��ת
			
			FreeUserRealtimeDataMO freeUserStatusMO = (FreeUserRealtimeDataMO) MemcachedHelper.get(userid, FreeUserRealtimeDataMO.class);
			if (freeUserStatusMO == null) {
				freeUserStatusMO = new FreeUserRealtimeDataMO();
			}
			freeUserStatusMO.setUserLevel((int) userStatus);
			MemcachedHelper.put(userid, freeUserStatusMO, true);

			// ��д���
			if (hasup) {
				setUserGrowState(Long.parseLong(userid), userStatus);
			}
		}
	}
	
	/**
	 * �ж��Ƿ񽵼�����  
	 * @param status
	 * @param userBusinScore
	 * @param manage
	 * @throws Exception 
	 */
	public void downlevel(String status,UserBusinScore userBusinScore) throws Exception{
		if (status != null) {
			FreeUserState freeUserState = null;

			// ����Ƿ�չ��
			if (status.equals(String.valueOf(AppContent.USER_STATUS_DEVE))) {
				if (userBusinScore.getBusincount() < 5) {
					freeUserState = new FreeUserState();
					freeUserState.setDowndate(DateUtils.getSysTimestamp());
					freeUserState.setBusincount(AppContent.USER_STATUS_UNSATISFY);
				}
				if (userBusinScore.getFivestarcount() < 3) {
					if (freeUserState == null) {
						freeUserState = new FreeUserState();
						freeUserState.setDowndate(DateUtils.getSysTimestamp());
					}
					freeUserState.setBusincomple(AppContent.USER_STATUS_UNSATISFY);
				}

				if (freeUserState != null) {
					freeUserState.setUserid(userBusinScore.getUserid());
					CorpDao.getInstance(false).setFreeUserState(freeUserState);
				}
			}
			// ������Ƽ���
			if (status.equals(String.valueOf(AppContent.USER_STATUS_RECOMM))) {
				if (userBusinScore.getBusincount() < 50) {
					freeUserState = new FreeUserState();
					freeUserState.setDowndate(DateUtils.getSysTimestamp());
					freeUserState.setBusincount(AppContent.USER_STATUS_UNSATISFY);

				}
				if (userBusinScore.getFivestarcount() < 4) {
					if (freeUserState == null) {
						freeUserState = new FreeUserState();
						freeUserState.setDowndate(DateUtils.getSysTimestamp());
					}
					freeUserState.setBusincomple(AppContent.USER_STATUS_UNSATISFY);
				}
				if (freeUserState != null) {
					freeUserState.setUserid(userBusinScore.getUserid());
					CorpDao.getInstance(false).setFreeUserState(freeUserState);
				}
			}
		}
	}

	/**
	 * �����û�
	 * @param businCount    �̻���
	 * @param businComple    �̻������Ƿ���
	 * @param companycomple    ��˾�������Ƿ���
	 * @param havematch    ƥ�����Ƿ���
	 * @param readMatch    �Ķ����Ƿ���
	 * @param logintime    ��¼�����Ƿ���
	 * @param userid    �û�Id
	 * @throws Exception
	 */
	public void downLevel(int businCount, int businComple, int companycomple,
			int havematch, int readMatch, int logintime, String userid,
			long userstatus, Timestamp updevdate, Timestamp upfosdate)
			throws Exception {
		FreeUserState fus = new FreeUserState();
		fus.setBusincount(businCount);
		fus.setBusincomple(businComple);
		fus.setCompanycomple(companycomple);
		fus.setHavematch(havematch);
		fus.setReadmatch(readMatch);
		fus.setLogintime(logintime);
		fus.setUserid(Long.parseLong(userid));
		fus.setGrowstate(userstatus);
		fus.setUpdevdate(updevdate);// ��չ�� ����ʱ��
		fus.setUpfosdate(upfosdate);// ������ ����ʱ��
		// �ɹ��ֶ�Ϊ1 �����ֶ�Ϊ2�������һ�ν����ֶΣ����н����ˣ��϶����ڵ���2
		if (userstatus == AppContent.USER_STATUS_DEVE) {
			if ((businCount * businComple * companycomple) >= 2) { // �н����ֶΣ���Ҫ��¼��һ�ν���ʱ��
				fus.setDowndate(DateUtils.getSysTimestamp());
			}
		} else if (userstatus == AppContent.USER_STATUS_RECOMM) {
			if ((businCount * businComple * companycomple * havematch
					* readMatch * logintime) >= 2) { // �н����ֶΣ���Ҫ��¼��һ�ν���ʱ��
				fus.setDowndate(DateUtils.getSysTimestamp());
			}
		}

		CorpDao.getInstance(false).setFreeUserState(fus);
	}
	/**
	 * ��д�û��ɳ����ݿ�
	 * @param userGrowState
	 * @throws Exception 
	 */
	public boolean setUserGrowState(long userid,long growstatus) throws Exception{
		UserGrowState userGrowState = new UserGrowState();
		userGrowState.setGrowstate(growstatus);
		userGrowState.setUpdatedate(DateUtils.getSysTimestamp());
		userGrowState.setUserid(userid);
		boolean result = CorpDao.getInstance(false).setUserGrowState(userGrowState);
		/**
		 * ��������ͳ�Ʊ�
		 */
		FreeUserState freeUserState = new FreeUserState();
		freeUserState.setUserid(userGrowState.getUserid());
		freeUserState.setGrowstate(userGrowState.getGrowstate());
		if(userGrowState.getGrowstate()==AppContent.USER_STATUS_DEVE){
			freeUserState.setUpdevdate(DateUtils.getSysTimestamp());
			freeUserState.setHavematch(AppContent.USER_STATUS_UNSATISFY);
			freeUserState.setReadmatch(AppContent.USER_STATUS_UNSATISFY);
			freeUserState.setLogintime(AppContent.USER_STATUS_UNSATISFY);
		}else if(userGrowState.getGrowstate()==AppContent.USER_STATUS_RECOMM){
			freeUserState.setUpfosdate(DateUtils.getSysTimestamp());
			freeUserState.setHavematch(AppContent.USER_STATUS_SATISFY);
			freeUserState.setReadmatch(AppContent.USER_STATUS_SATISFY);
			freeUserState.setLogintime(AppContent.USER_STATUS_SATISFY);
		}
		freeUserState.setBusincount(AppContent.USER_STATUS_SATISFY);
		freeUserState.setBusincomple(AppContent.USER_STATUS_SATISFY);
		freeUserState.setCompanycomple(AppContent.USER_STATUS_SATISFY);
		freeUserState.setUpdatetime(DateUtils.getSysTimestamp());
		
		CorpDao.getInstance(false).setFreeUserState(freeUserState);
		return result;
	}

}
