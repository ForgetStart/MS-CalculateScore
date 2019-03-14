package com.hc360.score.message.handler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.hc360.b2b.util.DateUtils;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.hbase.utils.CommContent;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.memcached.MemcachedHelper;
import com.hc360.mmt.memcached.mo.user.FreeUserRealtimeDataMO;
import com.hc360.score.common.AppContent;
import com.hc360.score.message.Transinformation;
import com.hc360.score.message.service.BusinessScoreManage;
import com.hc360.score.utils.MessageException;

/**
 * ClassName: BusinessScoreHandler <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON(��ѡ). <br/>
 * date: 2015��8��24�� ����11:53:44 <br/>
 * 
 * @author saiwengang
 * @version
 * @since JDK 1.6
 */
public class BusinessScoreHandler implements Handler {

	@Override
	public boolean handler(Transinformation traninfo) {
		try{
			Map<Long,UserBusinScore> userlist = new Hashtable<Long,UserBusinScore>();

			BusinInfo busininfo = traninfo.getBusininfo();
			int oper = busininfo.getOper();
            String scoreIdentity=busininfo.getScoreIdentity();
            if(StringUtils.isBlank(scoreIdentity)){
                scoreIdentity="";
            }
			traninfo.record("�����̻�bcids="+busininfo.getBcid()+",��������="+oper+",��ֱ�ʶ="+scoreIdentity);
			
			//����bcid
			traninfo.record("����bcid");
			String[] bcids = getBusiListByBcid(busininfo.getBcid());
			
			//���validate
			List<String> validates = convertValid(busininfo);
			
			traninfo.record("ѭ��ִ��ÿ���̻�");
			// ѭ��
			for(int i=0;i<bcids.length;i++){
                BusinScore businScore = null;
				String bcid = bcids[i];
				traninfo.record("ִ�е����̻� bcid="+bcid);
				//��Ч�̻������仯+1-1=0
				long busincountchange=0;
				//��Ч�̻������仯
				double businscorechange=0.0;
				
				TableInfo tableInfo = new TableInfo();
				tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
				tableInfo.setRowKey(bcid);
				/**
				 * ԭʼ�̻�״̬
				 * ���ԭ���̻�״̬Ϊ3�����������
				 */
				String oldstatus = null;
				double oldscore = 0.0;
				long userid = 0;
				/*******************************************
				 * �ݴ����hbase��û�и��̻����ȳ�ʼ�����̻�
				 * ��ˡ��������¼������
				 *******************************************/
				if(oper!=CommContent.BUSIN_OPER_NEWPENDING && oper!=CommContent.BUSIN_OPER_NEWPENDED){
					Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo);
					if(map==null || oper==CommContent.BUSIN_OPER_PENDED || oper==CommContent.BUSIN_OPER_REFUSE){
						map = HBaseUtilHelper.getRecordByRowkey(tableInfo);
					}
					if(map != null && map.size()>0){
						oldstatus = map.get("info.status");
						//��hbase�̻����userid
						String iuserid = map.get("info.userid");
						userid = Long.parseLong(iuserid==null ? "0":iuserid);
						String ioldscore = map.get("info.score");
						oldscore = Double.parseDouble(ioldscore==null ? "0":ioldscore);
					}
				}
				tableInfo.setFamilyName("info");
				
				//������ط���ֻ������Ч��
				switch(oper){
					/**
					 * �·�����
					 */
					case CommContent.BUSIN_OPER_NEWPENDING:
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_NEW);
						//�û�������������
						break;
					/**
					 * �·�����
					 */
					case CommContent.BUSIN_OPER_NEWPENDED:
						buildValidate(busininfo, validates, i);
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						//�û���������������
						busincountchange+=1;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * �޸Ĵ���
					 */
					case CommContent.BUSIN_OPER_UPDATEPENDING:
						buildValidate(busininfo, validates, i);						
						//״̬�·�δ��
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_NEW);						
						break;
					/**
					 * �޸�����
					 */
					case CommContent.BUSIN_OPER_UPDATEPENDED:
						//������Чʱ��
						buildValidate(busininfo, validates, i);
						//״̬�·�δ��
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
		//				//״̬���ͨ��
						//���ԭ��״̬status����3,��������
						if(!"3".equals(oldstatus)){
							busincountchange+=1;
						}
						//�����仯,��ȥ�ϵģ������µ�
						businscorechange = businscorechange - oldscore;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * δ�����ط�
					 */
					case CommContent.BUSIN_OPER_UNOVERRESEND:
						buildValidate(busininfo, validates, i);
						//������Чʱ��
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						break;
					/**
					 * �����ط�
					 */
					case CommContent.BUSIN_OPER_OVERRESEND:
						buildValidate(busininfo, validates, i);
						//������Чʱ��
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
		//				//����+1����������
						busincountchange+=1;
						businscorechange+=oldscore;
						break;
					/**
					 * ת����
					 */
					case CommContent.BUSIN_OPER_SETOVER:
						//������Чʱ��
						try{
							tableInfo.setColumnName("validate");
							tableInfo.setValue(DateUtils.getString(new Date(),"yyyy-MM-dd"));
							HBaseUtilHelper.addRecord(tableInfo);							
						//״̬����							
							tableInfo.setColumnName("status");
							tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_OVER));
							HBaseUtilHelper.addRecord(tableInfo);
							
						}catch(Exception e){
							e.printStackTrace();
						}
						//����-1����������
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
					/**
					 * ɾ��
					 */
					case CommContent.BUSIN_OPER_DEL:
						//״̬����
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_DEL));
						HBaseUtilHelper.addRecord(tableInfo);
						//����-1����������
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
					/**
					 * ���ͨ��,���ͨ����Ҳ����һ���̻��������Է�����޸�
					 */
					case CommContent.BUSIN_OPER_PENDED:
						//״̬����
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_PENDED));
						HBaseUtilHelper.addRecord(tableInfo);
						
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						//����+1����������
						busincountchange+=1;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * ���󣬾���Ҳ����һ���̻��������Է������޸�
					 */
					case CommContent.BUSIN_OPER_REFUSE:
						//״̬����
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_OVER));
						HBaseUtilHelper.addRecord(tableInfo);
						//����-1����������
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
				}
				UserBusinScore userBusinScore = new UserBusinScore();
				//����û�
				if(userBusinScore.getUserid()<=0){
					if(businScore!=null && businScore.getUserid()>0){
						userBusinScore.setUserid(businScore.getUserid());
					}else if(busininfo!=null && busininfo.getUserid()>0){
						userBusinScore.setUserid(busininfo.getUserid());
					}else if(userid!=0){
						userBusinScore.setUserid(userid);
					}else{
						//�������ݿ� 
						userid = BusinessScoreManage.getInstance().getUserFromBusin(Long.parseLong(bcid));
						userBusinScore.setUserid(userid);
					}
				}
				//����������޸ĵ�����
				userBusinScore.setBusincount(busincountchange);
				//����������޸ĵķ���
				userBusinScore.setBusinscore(businscorechange);
				//������� 
				MemcachedHelper.remove(userid,FreeUserRealtimeDataMO.class );
				MemcachedHelper.flush();
				/**
				 * ������map
				 */
				if(userlist.containsKey( userBusinScore.getUserid() )){
					UserBusinScore userBusin = userlist.get(userBusinScore.getUserid() );
					userBusinScore.setBusincount(userBusinScore.getBusincount() + userBusin.getBusincount());
					userBusinScore.setBusinscore(userBusinScore.getBusinscore() + userBusin.getBusinscore());							
				}
				//��ʱ����
				userlist.put(userBusinScore.getUserid(),userBusinScore);
			}
			traninfo.record("��ȡ���û��У�"+userlist.keySet());
			traninfo.setUserMap(userlist);
				
			//end==========================================
			if(userlist==null || userlist.size()<=0){
				traninfo.setSuccess(false);
				traninfo.setReason("�û��б�Ϊ�գ���");
				return false;
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
	 * ������Ч�� ���� 
	   ����08:28:58
	 * @param busininfo
	 * @param validates
	 * @param i
	 */
	private void buildValidate(BusinInfo busininfo, List<String> validates, int i) {
		
		try{
			String validate = validates.get(i);
			if(validate!=null && validate.length()>10){
				validate = validate.substring(0,10);
			}
			busininfo.setValidate(validate);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	// ���̻�IDת��Ϊ������ʽ
	public String[] getBusiListByBcid(String bcids) {
		if (StringUtils.isNotBlank(bcids)) {
			String[] bcidArr = null;
			if (bcids.indexOf("-") > 0) {
				bcidArr = bcids.split("-");
			} else {
				bcidArr = new String[] { bcids };
			}

			boolean bcidIsok = true;
			for (String bcid : bcidArr) {
				if (StringUtils.isNotBlank(bcid)) {
					if (Long.parseLong(bcid.trim()) == 0) {
						bcidIsok = false;
						break;
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
			if (bcidIsok) {
				return bcidArr;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	// ת��valiate
	public List<String> convertValid(BusinInfo businInfo) {
		List<String> validateList = new ArrayList<String>();
		// ���validate
		if (businInfo.getValidates() != null
				&& businInfo.getValidates().indexOf(";") > 0) {
			validateList = Arrays.asList(businInfo.getValidates().split(";"));
		} else {// ����validate
			validateList.add(businInfo.getValidate());
		}
		return validateList;
	}
}
