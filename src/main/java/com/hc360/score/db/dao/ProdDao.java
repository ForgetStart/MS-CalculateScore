/*
 * Copyright(c) 2000-2007 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.db.dao;

import java.sql.Timestamp;
import java.util.*;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.Convert;
import com.hc360.b2b.util.DateUtils;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.corpdb.CorTable;
import com.hc360.mmt.db.po.corpdb.OnCorTable;
import com.hc360.mmt.db.po.proddb.*;
import com.hc360.score.db.ProdDBSource;
import com.hc360.score.message.service.BusinessScoreManage;

public class ProdDao extends ProdDBSource {

	private static ProdDao masterInstance = new ProdDao(false);
	private static ProdDao slaveInstance = new ProdDao(true);

	private ProdDao(boolean isSlave) {
		super(isSlave);
	}

	public static ProdDao getInstance(boolean isSlave){
		return isSlave ? slaveInstance : masterInstance;
	}

	public OnBusinChance getOnBusinChanceById(long id) throws MmtException {
		return (OnBusinChance) super.find(OnBusinChance.class, Long.valueOf(id));
	}

	public BusinChance getBusinChanceById(long id) throws MmtException {
		return (BusinChance) super.find(BusinChance.class, Long.valueOf(id));
	}

	public boolean saveOrUpdateOnBusinChance(OnBusinChance onBusinChance) throws MmtException {
		super.saveOrUpdate(onBusinChance);
		return true;
	}

	public boolean saveOrUpdateBusinChance(BusinChance businChance) throws MmtException {
		super.saveOrUpdate(businChance);
		return true;
	}

	/**
	 * �õ��̻�
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public BusinChance getBusinChance(long bcid) throws MmtException{
		String sql = " from BusinChance a where a.id=:bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",bcid);

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			BusinChance  businchance = (BusinChance)rlist.get(0);
			return businchance;
		}else{
			return null;
		}
	}

	/**�õ��̻��������� ��Map
	 * @param userid �û���� 
	 * @return Map<Long,Double>  ��Ӧbc_id ��score
	 * */
	public Map<Long,Double> getBusinQualityStarMap(long userId) throws MmtException{
		Map<Long,Double> result =new HashMap<Long,Double>();
		String sql = " select bc_id,score from Busin_Quality_Star where providerid  = " +
				"(select providerid  from on_cor_table  where userid = " + userId + ")";
		List<PageRecordBean> list = super.queryByJDBC(sql);

		for(PageRecordBean page:list){
			Long bcid= Long.parseLong(page.getString(0) );
			Double mydouble = Double.parseDouble( page.getString(1));
			result.put(bcid, mydouble);
		}
		return result;
	}

	/**
	 * �õ��̻���������
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public BusinQualityStar getBusinQualityStar(long bcid) throws MmtException{
		String sql = " from BusinQualityStar a where a.bcid = :bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",bcid);

		List<BusinQualityStar> rlist = super.query(sql, map);
		if(rlist==null || rlist.size()==0){
			return null;
		}else{
			return rlist.get(0);
		}
	}


	/**
	 *ͨ��catid��ȡƷ����������(����������) 
	 */
	public List<PageRecordBean> getNortCategoryParam1(String catid) throws MmtException {
		List param = new ArrayList();
		param.add( Convert.getLong(catid));
		String sql = "select a.pi_id,i.name,a.is_required from Category_Param a, Param_Item i " +
				"where a.pi_id=i.id and a.category_id = ? and states='0' and sub_param_level='1'";
		List<PageRecordBean> list = this.queryByJDBC(sql, param);

		if(list != null && !list.isEmpty()){
			return list;
		}
		return null;
	}


	/**�����̻�id��ȡ�̻������Ǽ�
	 * @param long bcid
	 * @return CompassBusinComplete �̻������Ǽ�
	 * @throws MmtException
	 * */
	public CompassBusinComplete getCompassBusinComplete(long bcid) throws MmtException{
		String sql = " from CompassBusinComplete a where a.bcid=:bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",bcid);

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			CompassBusinComplete  compassbusincomplete = (CompassBusinComplete)rlist.get(0);
			return compassbusincomplete;
		}else{
			return null;
		}
	}

	/**
	 * ��д��ѻ�Ա�̻�����
	 * @return
	 * @throws MmtException
	 */
	public boolean resetCompassBusinComplete(CompassBusinComplete compassBusinComplete) throws MmtException{
		compassBusinComplete.setModifydate(DateUtils.getSysTimestamp()) ;
		super.update(compassBusinComplete);
		return true;
	}

	/**
	 * �����̻�
	 * @param onBusinChance
	 * @return
	 */
	public boolean saveBusinChance(BusinChance businChance)throws MmtException{
		super.update(businChance);
		return true;
	}

	/**
	 * �õ��û������̻�
	 */
	public Map<Long,OnBusinChance> getAllBusinChanceList(long userid)throws MmtException{
		if(userid<=0){
			return null;
		}
		long providerid = getProviderid(userid);
		if(providerid<=0){
			return null;
		}
		Map<Long,OnBusinChance> result = new Hashtable<Long,OnBusinChance>();
		//String sql = " from OnBusinChance a where a.sorttag = 0 and a.providerid=:providerid and a.states = '0' and a.checked = '1' and a.enddate > to_date('"+DateUtils.addTime(DateUtils.getString(new Date(),"yyyy-MM-dd"), "D", -1, "yyyy-MM-dd")+"','yyyy-MM-dd')";
		String sql = " from OnBusinChance a where a.sorttag = 0 and a.providerid=:providerid and a.states = '0' and a.checked = '1' and (a.isundershelf!=1 or a.isundershelf is null)";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("providerid",providerid);

		List<OnBusinChance> rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			for(OnBusinChance onBusinChance:rlist){
				result.put(onBusinChance.getId(), onBusinChance);
			}
		}

		sql = " from BusinChance a where a.sorttag = 0 and a.providerid=:providerid and a.states = '0' and a.checked = '1' and (a.isundershelf!=1 or a.isundershelf is null)";
		List<BusinChance> list = super.query(sql, map);

		if (list != null && list.size() > 0){
			for(BusinChance businChance:list){
				result.put(businChance.getId(),BusinessScoreManage.getInstance().chgOnBusinChance(businChance));
			}
		}
		return result;
	}

	/**
	 * ��ȡuserid ,����������̻������ͻ����̻�����
	 * @param userid �û����
	 * @return  �������
	 * @throws Exception
	 */
	public Map  findBusinStarSum(long userid)throws Exception{
		StringBuffer sql =  new StringBuffer();
		sql.append(" select  sum(star),count(bc_id)  from ( ");
		//sql.append("   select star, bc_id      from busin_chance a    where a.sorttag = 0      and a.states = '0'      and a.checked = '1'      and a.enddate > sysdate ");
		sql.append("   select star, bc_id      from busin_chance a    where a.sorttag = 0      and a.states = '0'      and a.checked = '1'      and (a.isundershelf!=1 or a.isundershelf is null) ");
		sql.append("   and a.providerid in (select providerid from on_cor_table where userid = " + userid + ") ");
		sql.append(" union   all ");
		//sql.append("  select  star, bc_id     from on_busin_chance a    where a.sorttag = 0         and a.states = '0'       and a.checked = '1'      and a.enddate > sysdate ");
		sql.append("  select  star, bc_id     from on_busin_chance a    where a.sorttag = 0         and a.states = '0'       and a.checked = '1'      and (a.isundershelf!=1 or a.isundershelf is null) ");
		sql.append(" and a.providerid in     (select providerid from on_cor_table where userid = "+userid+") ");
		sql.append(" and a.bc_id not in      (select bc_id    from busin_chance       where providerid in            (select providerid from on_cor_table where userid = " + userid + ")) ");
		sql.append(" )");

		List<PageRecordBean> list = super.queryByJDBC(sql.toString());

		Map map = new HashMap<String,Long>();
		for(PageRecordBean page:list){
			map.put("sumStar", page.getLong(0));
			map.put("sumBusin", page.getLong(1));
		}
		return map;
	}

	/**
	 * �����û����ܷ� 
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public long   findBusinScoreSum(long userid)throws Exception{

		/*StringBuffer sql = new StringBuffer("select sum(score)  from busin_quality_star where  bc_id in (select bc_id  from on_busin_chance a  where a.sorttag = 0  and a.states = '0'   and a.checked = '1'    and a.enddate > sysdate           and a.providerid in (select providerid from on_cor_table where userid = ?) ");
        sql.append("  union all      select bc_id  from busin_chance a  where a.sorttag = 0  and a.states = '0'   and a.checked = '1'    and a.enddate > sysdate           and a.providerid in (select providerid from on_cor_table where userid = ?) ");
        sql.append(")");*/
		StringBuffer sql = new StringBuffer("select sum(score_new)  from busin_quality_star where  bc_id in (select bc_id  from on_busin_chance a  where a.sorttag = 0  and a.states = '0'   and a.checked = '1'    and (a.isundershelf!=1 or a.isundershelf is null)           and a.providerid in (select providerid from on_cor_table where userid = ?) ");
		sql.append("  union all      select bc_id  from busin_chance a  where a.sorttag = 0  and a.states = '0'   and a.checked = '1'    and (a.isundershelf!=1 or a.isundershelf is null)           and a.providerid in (select providerid from on_cor_table where userid = ?) ");
		sql.append(")");
		List<Object> params = new ArrayList<Object>() ;
		params.add(userid) ;
		params.add(userid) ;

		List<PageRecordBean> list = super.queryByJDBC(sql.toString(),params);
		if(list.size()>0 ){//������
			PageRecordBean page = list.get(0);
			return  page.getLong(0);
		}else{
			return 0;
		}
	}

	/**
	 * ��ȡ��Ӧ�̻���ͼƬ��Ϣ
	 * @author Gao xingkun
	 * @version 1.0
	 * @date 2013-6-27 ����6:20:25
	 * @param bcid �̻�id
	 * @return  ͼƬ��Ϣ�����б�
	 * @throws MmtException List
	 */
	public List<BusinMultimedia> getBusinPicdetailList(long bcid) throws MmtException {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid", bcid);
		String sql = "from BusinMultimedia a where a.bcid = :bcid order by a.defaultflag ";

		List<BusinMultimedia> bmList = super.query(sql, map);
		return bmList;
	}

	/**
	 *��ȡ�û�����д�ķǱ�����������������Զ��������
	 * @author Gao xingkun
	 * @version 1.0
	 * @date 2014-5-9 ����3:28:07
	 * @param bcid
	 * @return List<CategoryParam>
	 */
	public List<BusinAttValue> getNorBusinAtt(long bcid) throws MmtException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid", bcid);
		String sql = "from BusinAttValue a where a.bcid = :bcid   and status='0'";
		List<BusinAttValue> baList = super.query(sql, map);
		return baList;
	}

	/**
	 * ��ȡ�̻�����д�����������������Ǳ���Զ��������
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public List<BusinAttValue> getCommonBusinAtt(long bcid) throws MmtException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid", bcid);
		List<BusinAttValue> baList = query(
				"from BusinAttValue a where a.bcid = :bcid and (a.isnormsparam!='1' or a.isnormsparam is null) and status='0'",
				map);
		return baList;
	}

	/**
	 * ��ȡ�̻�����д�������������Ǳ���Զ��������
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public List<BusinAttValue> getAllBusinAtt(long bcid) throws MmtException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid", bcid);
		List<BusinAttValue> baList = query(
				"from BusinAttValue a where a.bcid = :bcid",
				map);
		return baList;
	}

	public static void main(String[] args) {
		System.out.println(1);
		try {
			OnBusinChance obc = ProdDao.getInstance(false).getOnBusinChanceById(30448672);
			System.out.println(obc.getId()+"---"+obc.getTitle());

			obc.setTitle(obc.getTitle()+"123");
			boolean ret = ProdDao.getInstance(false).saveOrUpdateOnBusinChance(obc);
			System.out.println(ret);

			obc = ProdDao.getInstance(false).getOnBusinChanceById(30448672);
			System.out.println(obc.getId()+"---"+obc.getTitle());

		} catch (MmtException e) {
			e.printStackTrace();
		}
		System.out.println(2);
	}


	/**
	 * ��д�ƽ������̻�����
	 * @return
	 * @throws MmtException
	 */
	public boolean resetFreeBusinComplete(BusinQualityStar businQualityStar)throws MmtException{
		if(businQualityStar.getProviderid()<=0){
			businQualityStar.setProviderid(this.getProviderid(businQualityStar.getUserid()));
		}
		String sql = " from BusinQualityStar a where a.bcid=:bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",businQualityStar.getBcid());

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			BusinQualityStar businStar = (BusinQualityStar)rlist.get(0);
			businStar.setScore_new(businQualityStar.getScore_new());
			businStar.setStar_new(businQualityStar.getStar_new());
			businStar.setHassupcat_new(businQualityStar.getHassupcat_new());
			businStar.setHasphoto_new(businQualityStar.getHasphoto_new());
			businStar.setFirstphoto_new(businQualityStar.getFirstphoto_new());
			businStar.setHasprice_new(businQualityStar.getHasprice_new());
			businStar.setHasbrand_new(businQualityStar.getHasbrand_new());
			businStar.setHasminordernum_new(businQualityStar.getHasminordernum_new());
			businStar.setHaslongtitle_new(businQualityStar.getHaslongtitle_new());
			businStar.setHaslongintroduce_new(businQualityStar.getHaslongintroduce_new());
			businStar.setHastype_new(businQualityStar.getHastype_new());
			businStar.setPhotocount_new(businQualityStar.getPhotocount_new());
			businStar.setHasparam_new(businQualityStar.getHasparam_new());
			businStar.setModifydate_new(DateUtils.getSysTimestamp()) ;
			if(businStar.getProviderid()<=0){
				businStar.setProviderid(this.getProviderid(businQualityStar.getUserid()));
				businStar.setUserid(businQualityStar.getUserid());
			}
			super.update(businStar);
		}else{
			//���providerid
			super.save(businQualityStar);
		}

		return true;
	}
	/**
	 * �õ��û�providerid
	 * @param userid
	 * @return
	 * @throws MmtException
	 */
	public long getProviderid(long userid) throws MmtException{
		String sql = " from OnCorTable a where a.userid=:userid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid",userid);

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			OnCorTable  onCorTable = (OnCorTable)rlist.get(0);
			return onCorTable.getId();
		}else{
			sql = " from CorTable a where a.userid=:userid";
			rlist = super.query(sql, map);
			if (rlist != null && rlist.size() > 0){
				CorTable  corTable = (CorTable)rlist.get(0);
				return corTable.getId();
			}else{
				return 0;
			}
		}
	}

	/**
	 * �õ��̻�
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public OnBusinChance getOnBusinChance(long bcid)throws MmtException{
		String sql = " from OnBusinChance a where a.id=:bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",bcid);

		List rlist = super.query(sql, map);

		if (rlist != null && rlist.size() > 0){
			OnBusinChance  onbusinchance = (OnBusinChance)rlist.get(0);
			return onbusinchance;
		}else{
			return null;
		}
	}
	/**
	 * �����̻�
	 * @param onBusinChance
	 * @return
	 */
	public boolean saveOnBusinChance(OnBusinChance onBusinChance)throws MmtException{
		super.update(onBusinChance);
		return true;
	}

	public List<Long> getUsersToday4SiteMove(String startDate,String endDate) throws MmtException {
		String sql = "" ;
		List<PageRecordBean> pageList ;
		if(startDate != null && endDate != null){
			sql = "select t.userid  from on_cor_table t where exists (select 'x' from on_busin_chance a where a.whoinput='��ҹ���' " +
					"and a.states='0' and a.star='0' and a.pubdate >=to_date(?,'YYYY-MM-DD') AND " +
					"A.PUBDATE <= to_date(?,'YYYY-MM-DD') and a.providerid=t.providerid)  " ;
			List<Object> params = new ArrayList<Object>() ;
			params.add(startDate) ;
			params.add(endDate) ;
			pageList = super.queryByJDBC(sql, params) ;
		}else{
			sql = "select t.userid  from on_cor_table t where exists (select 'x' from on_busin_chance a where a.whoinput='��ҹ���' " +
					"and a.states='0' and a.star='0' and a.pubdate >= (sysdate-1) and a.providerid=t.providerid)" ;
			pageList = super.queryByJDBC(sql) ;
		}
		List<Long> result = new ArrayList<Long>() ;
		if(pageList != null){
			for(PageRecordBean record : pageList){
				result.add(record.getLong("userid")) ;
			}
		}
		return result ;
	}

	public List<Long> getUsersByProviderId4SiteMove(long providerid) throws MmtException {
		String sql = "" ;
		List<PageRecordBean> pageList ;
		sql = "select t.userid  from on_cor_table t where exists (" +
				"select 'x' from on_busin_chance a where a.whoinput='��ҹ���' and (a.star is null or a.star ='' or a.star ='0' or a.star ='1') and a.providerid=t.providerid and a.providerid=? )" ;
		List<Object> params = new ArrayList<Object>() ;
		params.add(providerid) ;
		pageList = super.queryByJDBC(sql, params) ;


		List<Long> result = new ArrayList<Long>() ;
		if(pageList != null){
			for(PageRecordBean record : pageList){
				result.add(record.getLong("userid")) ;
			}
		}
		return result ;
	}
	/**
	 * �������ڣ�ѡ����������� 
	 * @param starDate
	 * @param endDate
	 * @return
	 * @throws MmtException
	 */
	public List<String> getUserByDate(String starDate,String endDate ) throws MmtException {
		List<String> re = new ArrayList<String>();
		StringBuffer sql =  new StringBuffer();
		sql.append(" select distinct userid  from on_cor_table ");
		sql.append("     where providerid in(");
		sql.append("             select distinct providerid       from on_busin_chance  ");
		sql.append("             where checked = 1 ");
		sql.append("             and createdate >  to_date('" + starDate + "', 'yyyy-mm-dd_hh24:mi:ss') ");
		sql.append("             and createdate <  to_date('" + endDate +"', 'yyyy-mm-dd_hh24:mi:ss')  ");
//		sql.append("             and  username like 'a%' ");//ֻ����A��ͷ���û�
		sql.append("	)");

		List<PageRecordBean> list = super.queryByJDBC(sql.toString());
		for(PageRecordBean page:list){
			String userId = page.getString(0);
			re.add(userId);
		}
		return re;
	}

	public int getPriceItemsCount(long bcid) throws MmtException {
		StringBuilder sql = new StringBuilder();
		sql.append("select count(1) from product_price_item p where p.bc_id = ? and p.states = '0'");

		List<Object> params = new ArrayList<Object>() ;
		params.add(bcid) ;
		List<PageRecordBean> list = super.queryByJDBC(sql.toString(), params) ;
		if(list!=null&&list.size()>0){
			return Integer.valueOf(list.get(0).getString(0));
		}
		return 0;
	}

	/**
	 * ��ȡ����ͼ���
	 * @param bcid
	 * @return
	 * @throws MmtException
	 */
	public BusinIntroducePicWh getBusinIntroducePicWh(long bcid) throws MmtException{
		String sql = " from BusinIntroducePicWh p where p.bcid=:bcid";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bcid",bcid);

		List rlist = super.query(sql, map);
		if (rlist != null && rlist.size() > 0){
			BusinIntroducePicWh  bipWh = (BusinIntroducePicWh)rlist.get(0);
			return bipWh;
		}else{
			return null;
		}
	}

	/**
	 * ��������ͼ���
	 * @param bipWh
	 * @return
	 * @throws MmtException
	 */
	public boolean saveBusinIntroducePicWh(BusinIntroducePicWh bipWh) throws MmtException{
		super.save(bipWh);
		return true;
	}

	/**
	 * �޸�����ͼ���
	 * @param bipWh
	 * @return
	 * @throws MmtException
	 */
	public boolean updateBusinIntroducePicWh(BusinIntroducePicWh bipWh) throws MmtException{
		super.update(bipWh);
		return true;
	}
}