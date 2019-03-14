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

			traninfo.record("ѭ������ÿ���û���"+userlist.keySet());
			for(UserBusinScore userScore:userlist.values()){
				long userid = 0;
				if(businInfo.getUserid()>0){
					userid = businInfo.getUserid();
				}else if(userScore.getUserid()>0){
					userid = userScore.getUserid();
				}
				OnCorTable corTable=dao.getOnCorTable(userid);
				if(corTable==null){
					traninfo.record("�����û���"+userid+",δ�鵽���ݣ�������");
					continue;
				}
				if(corTable!=null&&"2".equals(corTable.getChecked())){
					traninfo.record("�����û���"+userid+",�ѱ����󣬲�����");
					continue;
				}
				traninfo.record("�������û���"+userid);
				/**
				 * �û�����
				 */
				UserBusinScore userBusinScore = new UserBusinScore();	
				userBusinScore.setUserid(userid);
				//�û��ܷ�
				double score = 0;
				double oldscore = 0;
				long oldcount = 0;
				long start = System.currentTimeMillis();
				
				TableInfo tableInfo = new TableInfo();
				tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
				tableInfo.setRowKey(String.valueOf(userid));

				Map<String,String> table = HBaseUtilHelper.getRecordByRowkey(tableInfo,true);
				/**
				 * ������û�û����Ϣ����ʼ��
				 */
				if(table == null || table.size()==0){
					//���Ϊ��,��ʼ��
					//�����û��̻��ܷ֡�ƽ���֡�����userBusinScore
					//�����³�ʼ�������̻��������Է�ֹ������
					//���߳��ܳ�ʼ�����û��̻�����
					traninfo.record("hbase��δ�ҵ����û�");
					userBusinScore = UserScoreManage.getInstance().initUserBusinScore(traninfo, userBusinScore,userid);
				}else{
					//�����Ϊ��,�������޸ĵķ������������
					for(String key:table.keySet()){
						if(key.equals("info.sumScore")){
							//���β��������
							userBusinScore.setBusinscore(Double.parseDouble(table.get(key))+userScore.getBusinscore());
						}else if(key.equals("info.businCount")){
							//���β���������
							oldcount = Long.parseLong(table.get(key));
							userBusinScore.setBusincount(oldcount+userScore.getBusincount());
						}else if(key.equals("info.averageScore")){
							//�ϴε�ƽ����
							oldscore = Double.parseDouble(table.get(key));
						}
					}
					//���β�����ƽ����
					if(userBusinScore.getBusincount()<=0){
						userBusinScore.setBusinarvgscore(0.0);
					}else{
						userBusinScore.setBusinarvgscore(userBusinScore.getBusinscore()/userBusinScore.getBusincount());
					}
					//���β������������
					UserScoreManage.getInstance().setUserBusinScore(tableInfo,userBusinScore);
					//У���û����� 
				}
			
				
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
				traninfo.record("�������ж� status="+status);
				if(status==null){
					buildBolot(traninfo, queryobject,userBusinScore,5,3);								
				}else if(String.valueOf(AppContent.USER_STATUS_INTRO).equals(status)){
					buildBolot(traninfo, queryobject,userBusinScore,5,3);
				}else if(String.valueOf(AppContent.USER_STATUS_DEVE).equals(status)){
					buildBolot(traninfo, queryobject,userBusinScore,5,4);
				}
				//���ж��������ж�
				traninfo.record("���ж��������ж� status="+status);
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
	 * ���� �̻�����ƽ�������֣��ж��Ƿ���������ж� 
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
			
			traninfo.record("��ѯ�����");
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERSTATES.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READBUSINCOUNT.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERAVERAGESCORE.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READCOMANYCOMPASS.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READMATCHINFO.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READMATCHREAD.name());
			handleUserStateMap(queryobject,AppContent.QUERYTYPE.READUSERLOGIN.name());
			
			traninfo.record("�����û������򽵼�");
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
	 * ��ѯ����
	 * @param id
	 * @param querytype
	 * @return
	 * @throws Exception 
	 */
	private Object query(Object object, String querytype) throws Exception {
		if (querytype == null || "".equals(querytype)) {
			return 0;
		} else if (querytype.equals(AppContent.QUERYTYPE.READLOGCOUNT.name())) {
			// ��ȡ�����
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
			// ��ȡ�����
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
			// �û��ɳ�״̬��ȡ,Ĭ��Ϊ1
			long status = AppContent.USER_STATUS_INTRO;
			BaseQueryObject baseQueryObject = (BaseQueryObject) object;
			if (baseQueryObject == null) {
				return status;
			}
			String key = String.valueOf(baseQueryObject.getKey());
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_rdsl_growstatus_reverse"); // ��hrtc_rdsl_growstatus�޸�Ϊhrtc_rdsl_growstatus_reverse
			tableInfo.setRowKey(key);
			Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo, true); // ��Ҫ��ת
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
			// �̻�����
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
			// �̻�ƽ������
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
			// ��˾��Ϣ������,0����������1һ��������2����������3������
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
		// //��Ӧ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
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
			String supplycount = HBaseUtilHelper.getValueByRowPreDay(tableInfo);// ��ѯ���շ��ʹ�Ӧ����ҳ����
			return supplycount;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILALBUM.name())) {
			// ��ѯ�����Ƿ���ʹ���˾��ᣬ����ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("photo");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILBUY.name())) {
			// ��ѯ�����Ƿ���ʹ���˾�ɹ�������ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("buy");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILCOMPANY.name())) {
			// ��ѯ�����Ƿ���ʹ���˾��ҳ������ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("home");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILCONTACT.name())) {
			// ��ѯ�����Ƿ���ʹ���ϵ���ǣ�����ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("contact");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILINTRO.name())) {
			// ��ѯ�����Ƿ���ʹ���˾���ܣ�����ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("intro");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILNEWS.name())) {
			// ��ѯ�����Ƿ���ʹ���˾��̬������ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("dynamic");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READDETAILSUPPLY.name())) {
			// ��ѯ�����Ƿ���ʹ���Ӧ��Ʒҳ������ֵΪ1����ʾ�ǣ������ǡ�
			if (object == null) {
				return null;
			}
			BehaviourInfo behaviourinfo = (BehaviourInfo) object;
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName("hrtc_sdsl_visitordetail");
			tableInfo.setRowKey(behaviourinfo.visitid);
			tableInfo.setFamilyName("info");
			tableInfo.setColumnName("supply");
			String home = HBaseUtilHelper.getValueByTTLToday(tableInfo);// ��˾��ҳ
			return home;
		} else if (querytype.equals(AppContent.QUERYTYPE.READMYEDITBUY.name())) {
			// ��ѯ�����Ƿ���ʹ���ҷ����ɹ���Ϣҳ������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ�������ҷ����ɹ���Ϣҳ������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ���ʹ���ҷ���������Ϣҳ������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ���ʹ����ҷ���������Ϣҳ������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ���ʹ�������������������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ���ʹ�������������������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ���ʹ��۴���ҳ������ֵΪ1����ʾ�ǣ������ǡ�
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
			// ��ѯ�����Ƿ������ҵ��վ������ֵΪ1����ʾ�ǣ������ǡ�
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
				/** 2013-11-25 jiangnan 30���ڵ�½��������usersession */
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
