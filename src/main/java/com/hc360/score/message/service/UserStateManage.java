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
 * 黄金罗盘manage
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
		/*** 判断是否升级 或有降级   */
		if (queryobject.getQueryMap().size() != 0) {
			Object status = queryobject.queryMap.get(AppContent.QUERYTYPE.READUSERSTATES.name());
			long userStatus = AppContent.USER_STATUS_INTRO;
			if (status != null) {
				userStatus = (Long) status;
			}
			// 新用户状态
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
			Timestamp updevdate = null;// 发展期时间
			Timestamp upposdate = null; // 扶持期时间

			UserAveragequalitylog useraverquality = CorpDao.getInstance(false).findUserAveragequality(userid);
			busincount = useraverquality.getBusincount();
			double d1 = busincount==0 ? 0 : useraverquality.getSumstar() / busincount;
			int star = (int) Math.floor(d1);
			// 是否升级
			boolean hasup = false;
			// 发展期
			if (busincount >= 5 && star >= 3 && compass >= 1) {
				// 升级
				if (userStatus <= AppContent.USER_STATUS_DEVE) {
					userStatus = AppContent.USER_STATUS_DEVE;
					updevdate = DateUtils.getSysTimestamp();
					hasup = true;
				}
			}
			// 升级判断
			if (busincount >= 50 && star >= 4 && compass == 3 && hasmatch && level && days >= 3) {
				// 升级扶持期
				if (userStatus <= AppContent.USER_STATUS_RECOMM) {
					userStatus = AppContent.USER_STATUS_RECOMM;
					upposdate = DateUtils.getSysTimestamp();
					hasup = true;
				}
			}
			// 判断用户每个字段状态
			if (userStatus == AppContent.USER_STATUS_DEVE) {// 发展期降级标准
				downLevel(
						busincount < 5 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						star < 3 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						(compass == 2 || compass == 0) ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!hasmatch ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						!level ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
						days < 3 ? AppContent.USER_STATUS_UNSATISFY : AppContent.USER_STATUS_SATISFY,
								userid,userStatus, updevdate, upposdate);
			} else if (userStatus == AppContent.USER_STATUS_RECOMM) {// 扶持期降级标准
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
			 * 如果升级，需要入库
			 */
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); // 由hrtc_rdsl_growstatus修改为hrtc_rdsl_growstatus_reverse
			tableInfo.setRowKey(userid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("status");
			tableInfo.setValue(String.valueOf(userStatus));
			HBaseUtilHelper.addRecord(tableInfo, true);// 入库 ,需要反转
			
			FreeUserRealtimeDataMO freeUserStatusMO = (FreeUserRealtimeDataMO) MemcachedHelper.get(userid, FreeUserRealtimeDataMO.class);
			if (freeUserStatusMO == null) {
				freeUserStatusMO = new FreeUserRealtimeDataMO();
			}
			freeUserStatusMO.setUserLevel((int) userStatus);
			MemcachedHelper.put(userid, freeUserStatusMO, true);

			// 回写入库
			if (hasup) {
				setUserGrowState(Long.parseLong(userid), userStatus);
			}
		}
	}
	
	/**
	 * 判断是否降级操作  
	 * @param status
	 * @param userBusinScore
	 * @param manage
	 * @throws Exception 
	 */
	public void downlevel(String status,UserBusinScore userBusinScore) throws Exception{
		if (status != null) {
			FreeUserState freeUserState = null;

			// 如果是发展期
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
			// 如果是推荐期
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
	 * 降级用户
	 * @param businCount    商机数
	 * @param businComple    商机分数是否达标
	 * @param companycomple    公司完整度是否达标
	 * @param havematch    匹配率是否达标
	 * @param readMatch    阅读率是否达标
	 * @param logintime    登录次数是否达标
	 * @param userid    用户Id
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
		fus.setUpdevdate(updevdate);// 发展期 更新时间
		fus.setUpfosdate(upfosdate);// 复制器 更新时间
		// 成功字段为1 降级字段为2，如果有一次降级字段，所有结果相乘，肯定大于等于2
		if (userstatus == AppContent.USER_STATUS_DEVE) {
			if ((businCount * businComple * companycomple) >= 2) { // 有降级字段，需要记录第一次降级时间
				fus.setDowndate(DateUtils.getSysTimestamp());
			}
		} else if (userstatus == AppContent.USER_STATUS_RECOMM) {
			if ((businCount * businComple * companycomple * havematch
					* readMatch * logintime) >= 2) { // 有降级字段，需要记录第一次降级时间
				fus.setDowndate(DateUtils.getSysTimestamp());
			}
		}

		CorpDao.getInstance(false).setFreeUserState(fus);
	}
	/**
	 * 回写用户成长数据库
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
		 * 设置升级统计表
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
