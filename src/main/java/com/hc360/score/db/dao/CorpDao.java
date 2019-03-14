/*
 * Copyright(c) 2000-2007 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.db.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.Convert;
import com.hc360.b2b.util.DateUtils;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.corpdb.CorTable;
import com.hc360.mmt.db.po.corpdb.CorTableCommon;
import com.hc360.mmt.db.po.corpdb.FreeUserState;
import com.hc360.mmt.db.po.corpdb.OnCorTable;
import com.hc360.mmt.db.po.corpdb.OnCorTableCommon;
import com.hc360.mmt.db.po.corpdb.UserAveragequalitylog;
import com.hc360.mmt.db.po.corpdb.UserGrowState;
import com.hc360.score.db.CorpDBSource;

public class CorpDao extends CorpDBSource {

	private static CorpDao masterInstance = new CorpDao(false);
	private static CorpDao slaveInstance = new CorpDao(true);

	private CorpDao(boolean isSlave) {
		super(isSlave);
	}

	public static CorpDao getInstance(boolean isSlave) {
		return isSlave ? slaveInstance : masterInstance;
	}

	// ----------------OnCorTable--------------------------------------------
	public OnCorTable getOnCorTable(long userid) throws MmtException {
		String queryString = "from  OnCorTable a where a.userid=:userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);

		List<OnCorTable> list = super.query(queryString, map);

		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public boolean saveOrUpdateOnCorTable(OnCorTable onCorTable)
			throws MmtException {
		super.saveOrUpdate(onCorTable);
		return true;
	}

	// ----------------UserAveragequalitylog--------------------------------------------
	public UserAveragequalitylog getUserAveragequalitylog(long userid)
			throws MmtException {
		String queryString = "from  UserAveragequalitylog a where a.userid=:userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);

		List<UserAveragequalitylog> list = super.query(queryString, map);

		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public boolean saveOrUpdateUserAveragequalitylog(
			UserAveragequalitylog userAveragequalitylog) throws MmtException {
		super.saveOrUpdate(userAveragequalitylog);
		return true;
	}

	/**
	 * ͨ�����б��idȡ�÷���catid
	 *
	 * @author zhangpeng
	 * @throws MmtException
	 * @date 2014-4-16 ����02:28:33
	 * @see com.hc360.bcs.dao.BusinchanceCorpDAO#getCatidBySupcatid(java.lang.String)
	 */
	public String getCatidBySupcatid(String supcatid) throws MmtException{
		String resultstr = "";
		String sql = "select a.catid from SupermarketCat a where  a.supcatid= :supcatid ";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("supcatid", supcatid);

		List alist = super.query(sql, params);

		if (alist.size() > 0) {
			Object obj = (Object) alist.get(0);
			resultstr = Convert.getString(obj);
		}
		return resultstr;
	}
	/**
	 * �����û�ƽ���̻�����
	 * @param userBusinScore
	 * @param sumBusin  �̻�����
	 * @param sumstar   �Ǽ�����
	 * @return
	 * @throws MmtException
	 */
	public UserAveragequalitylog saveUpdateUserAveragequality(UserBusinScore userBusinScore,long sumBusin,long sumstar,OnCorTable oct) throws MmtException{
		if(userBusinScore==null || userBusinScore.getUserid()<=0){
			return null;
		}
		UserAveragequalitylog userAveragequalitylog = null;
		String sql = " from UserAveragequalitylog a where a.userid = :userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid",userBusinScore.getUserid());

		List<UserAveragequalitylog> rlist = super.query(sql, map);

		if(rlist!=null && rlist.size()>0){
			userAveragequalitylog = rlist.get(0);
		}
		//����
		if(userAveragequalitylog!=null){
			userAveragequalitylog.setAveragescore(BigDecimal.valueOf(userBusinScore.getBusinarvgscore()));
			userAveragequalitylog.setSumscore(BigDecimal.valueOf(userBusinScore.getBusinscore()));
			userAveragequalitylog.setUpdatedate(DateUtils.getSysTimestamp());
			userAveragequalitylog.setBusincount(sumBusin); // ��ʱע��
			//userAveragequalitylog.setBusincount(userBusinScore.getBusincount());
			userAveragequalitylog.setSumstar(sumstar);

			// my���������user_averagequalitylog�����͹��߿���,�¼�
			userAveragequalitylog.setOperstate("0");
			int states = Integer.valueOf(userAveragequalitylog.getStates());
			if(sumBusin>=5){
				userAveragequalitylog.setStates("1");
			}
			if(states==1 && sumBusin<5){
				userAveragequalitylog.setStates("2");
			}
			//ͬ��������ҵ��providerid
			if(oct!=null){
				userAveragequalitylog.setAreaid(oct.getAreaid());
				userAveragequalitylog.setProviderid(oct.getId());
			}
			super.update(userAveragequalitylog);
		}
		//����
		else{
			userAveragequalitylog = new UserAveragequalitylog();
			userAveragequalitylog.setUserid(userBusinScore.getUserid());
			userAveragequalitylog.setAveragescore(BigDecimal.valueOf(userBusinScore.getBusinarvgscore()));
			userAveragequalitylog.setSumscore(BigDecimal.valueOf(userBusinScore.getBusinscore()));
			userAveragequalitylog.setUpdatedate(DateUtils.getSysTimestamp());
			userAveragequalitylog.setBusincount(sumBusin); // ��ʱע��
			//userAveragequalitylog.setBusincount(userBusinScore.getBusincount());
			userAveragequalitylog.setSumstar(sumstar);

			// my���������user_averagequalitylog�����͹��߿���,�¼�
			userAveragequalitylog.setOperstate("0");
			userAveragequalitylog.setStates("0");
			userAveragequalitylog.setRanking(0l);
			if(oct!=null){
				userAveragequalitylog.setAreaid(oct.getAreaid());
				userAveragequalitylog.setProviderid(oct.getId());
			}

			super.save(userAveragequalitylog);
		}
		return userAveragequalitylog;
	}

	/**
	 * �õ��û�userid
	 * @param providerid
	 * @return
	 * @throws MmtException
	 */
	public long getUserid(long providerid) throws MmtException{
		OnCorTable oncortable = (OnCorTable)super.find(OnCorTable.class, providerid);
		if(oncortable==null){
			CorTable cortable = (CorTable)super.find(CorTable.class,providerid);
			if(cortable!=null){
				return cortable.getUserid();
			}
		}else{
			return oncortable.getUserid();
		}
		return 0;
	}

	public static void main(String[] args) {
		System.out.println(1);
		try {
			UserAveragequalitylog ua = CorpDao.getInstance(false).getUserAveragequalitylog(1211);
			if (ua != null) {
				System.out.println(ua.getBusincount());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(2);
	}

	/**
	 * �����û�ƽ�������� ����userid
	 *
	 * @param userId
	 * @return
	 * @throws MmtException
	 */
	public UserAveragequalitylog findUserAveragequality(String userId) throws MmtException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", Long.parseLong(userId));
		String sql = " from UserAveragequalitylog a where a.userid = :userid";

		List<UserAveragequalitylog> rlist = super.query(sql, map);
		if (rlist != null && rlist.size() > 0) {
			return rlist.get(0);
		}

		return null;
	}

	/**
	 * ��д�û��ɳ����ݿ�
	 *
	 * @param userGrowState
	 * @throws Exception
	 */
	public boolean setUserGrowState(UserGrowState userGrowState) throws Exception {
		if (userGrowState == null || userGrowState.getUserid() <= 0) {
			return false;
		}
		String sql = " from UserGrowState a where a.userid=:userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userGrowState.getUserid());

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0) {
			UserGrowState userState = (UserGrowState) rlist.get(0);
			userState.setGrowstate(userGrowState.getGrowstate());
			userState.setUpdatedate(userGrowState.getUpdatedate());
			super.update(userState);
		} else {
			super.save(userGrowState);
		}
		return true;
	}

	/**
	 * ������ѻ�Ա�ɳ�ͳ�Ʊ�
	 *
	 * @param freeUserState
	 * @return
	 * @throws Exception
	 */
	public boolean setFreeUserState(FreeUserState freeUserState) throws Exception {
		if (freeUserState == null) {
			return false;
		}
		String sql = "from FreeUserState a where a.userid = :userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", freeUserState.getUserid());

		List rlist = super.query(sql, map);
		if (rlist != null && rlist.size() > 0) {
			FreeUserState userState = (FreeUserState) rlist.get(0);
			// �������������
			if (freeUserState.getGrowstate() > 0) {
				userState.setGrowstate(freeUserState.getGrowstate());
			}
			// �����������չ��ʱ��
			if (userState.getUpdevdate() == null
					&& freeUserState.getUpdevdate() != null) {
				userState.setUpdevdate(freeUserState.getUpdevdate());
			}
			// ���������������ʱ��
			if (userState.getUpfosdate() == null
					&& freeUserState.getUpfosdate() != null) {
				userState.setUpfosdate(freeUserState.getUpfosdate());

			}
			// ����е���ʱ��
			if (userState.getDowndate() == null
					&& freeUserState.getDowndate() != null) {
				userState.setDowndate(freeUserState.getDowndate());
			}

			/**
			 * �������������
			 */
			if (freeUserState.getBusincount() > 0) {
				userState.setBusincount(freeUserState.getBusincount());
			}
			if (freeUserState.getBusincomple() > 0) {
				userState.setBusincomple(freeUserState.getBusincomple());
			}
			if (freeUserState.getCompanycomple() > 0) {
				userState.setCompanycomple(freeUserState.getCompanycomple());
			}
			if (freeUserState.getHavematch() > 0) {
				userState.setHavematch(freeUserState.getHavematch());
			}
			if (freeUserState.getReadmatch() > 0) {
				userState.setReadmatch(freeUserState.getReadmatch());
			}
			if (freeUserState.getLogintime() > 0) {
				userState.setLogintime(freeUserState.getLogintime());
			}

			userState.setUpdatetime(DateUtils.getSysTimestamp());
			super.update(userState);
//			super.commit();
		} else {
			freeUserState.setUpdatetime(DateUtils.getSysTimestamp());
			super.save(freeUserState);
		}
		return true;
	}

	/**
	 * �õ��û����������Ķ�
	 */
	public boolean getMatchInfoCount(long providerid) throws MmtException {
		String sql = "from MatchBusinInfo where providerid = :providerid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("providerid", providerid);

		List list = super.query(sql, params);
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	public long getUserLoginCount(long userid) throws MmtException {
		List<Object> param = new ArrayList<Object>();
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append(" select sum(log_count) as num from user_logon_stat a ");
		sbSQL.append(" where a.userid= ? ");
		sbSQL.append(" and a.pubdate > sysdate - 30");
		param.add(userid);

		List<PageRecordBean> list = queryByJDBC(sbSQL.toString(), param);
		long num = 0;
		if (list != null && list.size() > 0) {
			PageRecordBean record = list.get(0);
			if (record.getValue("num") == null) {
				num = 0l;
			} else {
				num = Long.parseLong(String.valueOf(record.getValue("num")));
			}
		}
		return num;
	}

	public List<Long> getProviderIdByHour4SiteMove(String startDate) throws MmtException {
		String sql = "" ;
		List<PageRecordBean> pageList ;
		if(startDate != null){
			sql = "select providerid  from newhc.cor_website_move t1 where t1.states='3' and t1.tooledate > to_date(?,'yyyy-MM-dd HH24:mi:ss')" ;
			List<Object> params = new ArrayList<Object>() ;
			params.add(startDate) ;
			pageList = super.queryByJDBC(sql, params) ;
		}else{
			sql = "select providerid  from cor_website_move t1 where t1.states='3' and t1.tooledate > (sysdate - interval '1' hour)" ;
			pageList = super.queryByJDBC(sql) ;
		}
		List<Long> result = new ArrayList<Long>() ;
		if(pageList != null){
			for(PageRecordBean record : pageList){
				result.add(record.getLong("providerid")) ;
			}
		}
		return result ;
	}

	public List<String> findUserAveragequalityIsNull() throws MmtException {

		List<String> re = new ArrayList<String>();
		StringBuffer sql =  new StringBuffer();

		sql.append(" select userid  from newhc.user_averagequalitylog where busincount =0 and sumstar >0  ");
		sql.append(" union all  ");
		sql.append(" select userid from newhc.user_averagequalitylog where busincount >0 and sumstar =0  ");

		List<PageRecordBean> list;
		list = super.queryByJDBC(sql.toString());
		for(PageRecordBean page:list){
			String userId = page.getString(0);
			re.add(userId);
		}
		return re;
	}

	// ----------------CorTableCommon--------------------------------------------
	public CorTableCommon getCorTableCommon(long providerid) throws MmtException {
		String queryString = "from  CorTableCommon a where a.id=:providerid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("providerid", providerid);

		List<CorTableCommon> list = super.query(queryString, map);

		if (list != null && list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}


	public void saveOrUpdateCorTableCommon(CorTableCommon corTableCommon) throws MmtException {
		super.saveOrUpdate(corTableCommon);
	}

}