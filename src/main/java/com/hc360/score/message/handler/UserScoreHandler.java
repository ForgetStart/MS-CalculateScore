package com.hc360.score.message.handler;

import java.util.Hashtable;
import java.util.Map;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.po.BaseQueryObject;
import com.hc360.hbase.po.BehaviourInfo;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.db.po.corpdb.OnCorTable;
import com.hc360.mmt.memcached.MemcachedHelper;
import com.hc360.mmt.memcached.mo.user.FreeUserStatusMO;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;
import com.hc360.score.db.dao.MatchDao;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.hbase.HbaseUtils;
import com.hc360.score.message.Transinformation;
import com.hc360.score.message.service.UserScoreManage;
import com.hc360.score.message.service.UserStateManage;
import com.hc360.score.utils.MessageException;

public class UserScoreHandler implements Handler{

	@Override
	public boolean handler(Transinformation traninfo) {
		try{
			BusinInfo businInfo = traninfo.getBusininfo();
			Map<Long,UserBusinScore> userlist = traninfo.getUserMap();
			CorpDao dao = CorpDao.getInstance(false);

			traninfo.record("循环处理每个用户："+userlist.keySet());
			for(UserBusinScore userScore:userlist.values()){
				long userid = 0;
				if(businInfo.getUserid()>0){
					userid = businInfo.getUserid();
				}else if(userScore.getUserid()>0){
					userid = userScore.getUserid();
				}
				OnCorTable corTable=dao.getOnCorTable(userid);
				if(corTable==null){
					traninfo.record("单个用户："+userid+",未查到数据，不处理");
					continue;
				}
				if(corTable!=null&&"2".equals(corTable.getChecked())){
					traninfo.record("单个用户："+userid+",已被拘审，不处理");
					continue;
				}
				traninfo.record("处理单个用户："+userid);
				/**
				 * 用户分数
				 */
				UserBusinScore userBusinScore = new UserBusinScore();	
				userBusinScore.setUserid(userid);
				//用户总分
				double score = 0;
				double oldscore = 0;
				long oldcount = 0;
				long start = System.currentTimeMillis();
				
				TableInfo tableInfo = new TableInfo();
				tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
				tableInfo.setRowKey(String.valueOf(userid));

				Map<String,String> table = HBaseUtilHelper.getRecordByRowkey(tableInfo,true);
				/**
				 * 如果该用户没有消息，初始化
				 */
				if(table == null || table.size()==0){
					//如果为空,初始化
					//包括用户商机总分、平均分、总数userBusinScore
					//和重新初始化所有商机分数，以防止脏数据
					//新线程跑初始化该用户商机质量
					traninfo.record("hbase中未找到此用户");
					userBusinScore = UserScoreManage.getInstance().initUserBusinScore(traninfo, userBusinScore,userid);
				}else{
					//如果不为空,将本次修改的分数、数量入库
					for(String key:table.keySet()){
						if(key.equals("info.sumScore")){
							//本次操作后分数
							userBusinScore.setBusinscore(Double.parseDouble(table.get(key))+userScore.getBusinscore());
						}else if(key.equals("info.businCount")){
							//本次操作后数量
							oldcount = Long.parseLong(table.get(key));
							userBusinScore.setBusincount(oldcount+userScore.getBusincount());
						}else if(key.equals("info.averageScore")){
							//上次的平均分
							oldscore = Double.parseDouble(table.get(key));
						}
					}
					//本次操作后平均分
					if(userBusinScore.getBusincount()<=0){
						userBusinScore.setBusinarvgscore(0.0);
					}else{
						userBusinScore.setBusinarvgscore(userBusinScore.getBusinscore()/userBusinScore.getBusincount());
					}
					//本次操作后数据入库
					UserScoreManage.getInstance().setUserBusinScore(tableInfo,userBusinScore);
					//校验用户分数 
				}
			
				
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
				traninfo.record("做升级判断 status="+status);
				if(status==null){
					buildBolot(traninfo, queryobject,userBusinScore,5,3);								
				}else if(String.valueOf(AppContent.USER_STATUS_INTRO).equals(status)){
					buildBolot(traninfo, queryobject,userBusinScore,5,3);
				}else if(String.valueOf(AppContent.USER_STATUS_DEVE).equals(status)){
					buildBolot(traninfo, queryobject,userBusinScore,5,4);
				}
				//所有都做降级判断
				traninfo.record("所有都做降级判断 status="+status);
				UserStateManage.getInstance().downlevel(status,userBusinScore);
			}	
			traninfo.setSuccess(true);
			return true;
		
		}catch(Exception e){
			traninfo.setSuccess(false);
			traninfo.setReason(MessageException.getStackTrace(e));
			return false;
		}
	}
	/**
	 * 根据 商机数和平均质量分，判断是否出发升级判断 
	 * @param input  
	 * @param message
	 * @param queryobject
	 * @param userBusinScore
	 * @param businCount  
	 * @param avergscore
	 * @throws Exception 
	 */
	public void buildBolot(Transinformation traninfo,
			BaseQueryObject queryobject, UserBusinScore userBusinScore,
			int businCount, int avergscore) throws Exception {
		
		if(userBusinScore.getBusincount()>=businCount && (  (int)(userBusinScore.getFivestarcount())>=avergscore)  ){
			
			queryobject.queryList.add(AppContent.QUERYTYPE.READUSERSTATES.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READBUSINCOUNT.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READUSERAVERAGESCORE.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READCOMANYCOMPASS.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READMATCHINFO.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READMATCHREAD.name());
			queryobject.queryList.add(AppContent.QUERYTYPE.READUSERLOGIN.name());
			
			traninfo.record("查询浏览量");
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERSTATES.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READBUSINCOUNT.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERAVERAGESCORE.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READCOMANYCOMPASS.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READMATCHINFO.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READMATCHREAD.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERLOGIN.name());
			
			traninfo.record("处理用户升级或降级");
			UserStateManage.getInstance().handleUserState(queryobject);
		}
	}
	
	private void handleUserStateMap(BaseQueryObject queryobject,String querytype) throws Exception{
		Object query = query(queryobject,querytype);
		if(query==null){
			query="0";
		}
		if(queryobject.getQueryMap() ==null){
			queryobject.setQueryMap(new Hashtable() );
		}
		queryobject.getQueryMap().put(querytype, query);
	}
	
	/**
	 * 查询方法
	 * @param id
	 * @param querytype
	 * @return
	 * @throws Exception 
	 */
	private Object query(Object object, String querytype) throws Exception {
		if (querytype == null || "".equals(querytype)) {
			return 0;
		} else if (querytype.equals(AppContent.QUERYTYPE.READLOGCOUNT.name())) {
			// 读取浏览量
			long count = 0;
			String key = HbaseUtils.getRowKey(object);
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitlog");
			tableInfo.setRowKey(key);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("businTime");
			count = HBaseUtilHelper.getCountByDays(tableInfo);
			return count;
		} else if (querytype.equals(AppContent.QUERYTYPE.READNOTCOUNT.name())) {
			// 读取浏览量
			long count = 0;
			String key = HbaseUtils.getRowKey(object);
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_notelog");
			tableInfo.setRowKey(key);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("businopenTime");
			count = HBaseUtilHelper.getCountByDays(tableInfo);
			return count;
		} else if (querytype.equals(AppContent.QUERYTYPE.READUSERSTATES.name())) {
			// 用户成长状态读取,默认为1
			long status = AppContent.USER_STATUS_INTRO;
			BaseQueryObject baseQueryObject = (BaseQueryObject) object;
			if (baseQueryObject == null) {
				return status;
			}
			String key = String.valueOf(baseQueryObject.getKey());
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); // 由hrtc_rdsl_growstatus修改为hrtc_rdsl_growstatus_reverse
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo, true); // 需要反转
			if (map == null) {
				return status;
			} else {
				String infostatus = map.get("info.status");
				if (infostatus != null) {
					status = Long.parseLong(infostatus);
				}
				return status;
			}
		} else if (querytype.equals(AppContent.QUERYTYPE.READBUSINCOUNT.name())) {
			// 商机数量
			long count = 0;
			BaseQueryObject baseQueryObject = (BaseQueryObject) object;
			if (baseQueryObject == null) {
				return count;
			}
			String key = String.valueOf(baseQueryObject.getKey());
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo, true);
			if (map == null) {
				return count;
			} else {
				String businCount = map.get("info.businCount");
				if (businCount != null) {
					count = Long.parseLong(businCount);
				}
				return count;
			}
		} else if (querytype.equals(AppContent.QUERYTYPE.READUSERAVERAGESCORE.name())) {
			// 商机平均质量
			double score = 0;
			BaseQueryObject baseQueryObject = (BaseQueryObject) object;
			if (baseQueryObject == null) {
				return score;
			}
			String key = String.valueOf(baseQueryObject.getKey());
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo, true);
			if (map == null) {
				return score;
			} else {
				String averageScore = map.get("info.averageScore");
				if (averageScore != null) {
					score = Double.parseDouble(averageScore);
				}
				return score;
			}
		} else if (querytype.equals(AppContent.QUERYTYPE.READCOMANYCOMPASS.name())) {
			// 公司信息完整度,0都不完整，1一类完整，2二类完整，3都完整
			int compass = 0;
			BaseQueryObject baseQueryObject = (BaseQueryObject) object;
			if (baseQueryObject == null) {
				return compass;
			}
			String key = String.valueOf(baseQueryObject.getKey());
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_coinfolog_reverse");
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo, true);
			if (map == null) {
				return compass;
			} else {
				int type1compass = 0;
				String type1 = map.get("info.type1");
				if (type1 != null) {
					type1compass = Integer.parseInt(type1);
				}
				int type2compass = 0;
				String type2 = map.get("info.type2");
				if (type2 != null) {
					type2compass = Integer.parseInt(type2);
				}
				int fullcompass = 0;
				String fulltype = map.get("info.fullType");
				if (fulltype != null) {
					fullcompass = Integer.parseInt(fulltype);
				}
				if (fullcompass == 1) {
					compass = 3;
				} else if (type1compass == 0 && type2compass == 0) {
					compass = 0;
				} else if (type1compass == 1 && type2compass == 0) {
					compass = 1;
				} else if (type1compass == 0 && type2compass == 1) {
					compass = 2;
				}
				return compass;
			}
		}
		// //供应搜索
		else if (querytype.equals(AppContent.QUERYTYPE.READSUPPLYVISIT.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supplycount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READBUYVISIT.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buycount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READCOMPANYVISIT.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("companycount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READINFOVISIT.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("infocount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READSUPPLYFINAL.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("finalsupplycount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READBUYFINAL.name())) {
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorpath");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("finalbuycount");
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// 查询今日访问供应搜索页数量
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILALBUM.name())) {
			// 查询今日是否访问过公司相册，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("photo");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILBUY.name())) {
			// 查询今日是否访问过公司采购，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buy");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILCOMPANY.name())) {
			// 查询今日是否访问过公司首页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("home");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILCONTACT.name())) {
			// 查询今日是否访问过联系我们，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("contact");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILINTRO.name())) {
			// 查询今日是否访问过公司介绍，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("intro");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILNEWS.name())) {
			// 查询今日是否访问过公司动态，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("dynamic");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILSUPPLY.name())) {
			// 查询今日是否访问过供应产品页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supply");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// 公司首页
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYEDITBUY.name())) {
			// 查询今日是否访问过买家发布采购信息页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buy_issue");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYEDITSUPPLY.name())) {
			// 查询今日是否访问卖家发布采购信息页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supply_issue");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYMANAGEBUY.name())) {
			// 查询今日是否访问过买家发布管理信息页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buy_manage");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYEDITMANAGESUPPLY.name())) {
			// 查询今日是否访问过卖家发布管理信息页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supply_manage");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYBUYOTHER.name())) {
			// 查询今日是否访问过商务中心其他，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buy_other");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYSUPPLYOTHER.name())) {
			// 查询今日是否访问过商务中心其他，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitormy");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supply_other");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READOTHERHOME.name())) {
			// 查询今日是否访问过慧聪首页，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorother");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("home");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READOTHERTRADE.name())) {
			// 查询今日是否访问行业网站，返回值为1，表示是，否则不是。
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitorother");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("tradeweb");
			String value = HBaseUtilHelper.getValueByTTLToday(tableInfo);
			return value;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMATCHINFO.name())) {
			String key = ((BaseQueryObject) object).getKey();
			boolean has = false;
			long providerid = ProdDao.getInstance(false).getProviderid(Long.parseLong(key));
			if (providerid > 0) {
				has = MatchDao.getInstance(false).getMatchInfoCount(providerid);
			}
			return has;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMATCHREAD.name())) {
			boolean level = false;
			String key = ((BaseQueryObject) object).getKey();
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_irsl_readmatch");
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo);
			if (map != null) {
				String status = map.get("info.status");
				if (status != null && "1".equals(status)) {
					level = true;
				}
			}
			return level;
		} else if (querytype.equals(AppContent.QUERYTYPE.READUSERLOGIN.name())) {
			String key = ((BaseQueryObject) object).getKey();
			long days = 0;
			long myuserid = Long.parseLong(key);

			FreeUserStatusMO freeUserStatusMO = (FreeUserStatusMO) MemcachedHelper.get(myuserid, FreeUserStatusMO.class);
			if (freeUserStatusMO != null) {
				days = freeUserStatusMO.getLogoCnt();
			} else {
				/** 2013-11-25 jiangnan 30天内登陆次数放入usersession */
				days = CorpDao.getInstance(false).getUserLoginCount(myuserid);
				freeUserStatusMO = new FreeUserStatusMO();
				freeUserStatusMO.setUserid(myuserid);
				freeUserStatusMO.setLogoCnt(days);
				MemcachedHelper.put(myuserid, freeUserStatusMO, true);
			}

			return days;
		}
		return 0;
	}
	
	
	
	
}
