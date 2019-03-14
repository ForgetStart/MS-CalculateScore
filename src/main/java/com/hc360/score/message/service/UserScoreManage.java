package com.hc360.score.message.service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.DateUtils;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.db.po.corpdb.CorTableCommon;
import com.hc360.mmt.db.po.corpdb.OnCorTable;
import com.hc360.mmt.db.po.corpdb.OnCorTableCommon;
import com.hc360.mmt.db.po.proddb.BusinQualityStar;
import com.hc360.mmt.db.po.proddb.OnBusinChance;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.message.Transinformation;
import com.hc360.score.utils.MessageException;

public class UserScoreManage {

	Logger logger = Logger.getLogger("memberswitch");
	private static UserScoreManage instance = new UserScoreManage();
	public static UserScoreManage getInstance(){
		return instance;
	}

	/**
	 * ��ʼ���û�userBusinScore
	 * �����hbase��û��userBusinScore��Ϊ��ʼ���������ݣ�Ϊ���������ݣ������ݿ������¼����̻����ݣ����¼���userBusinScore
	 * ������Ч�̻�
	 * @param userBusinScore
	 * @throws Exception
	 */
	public UserBusinScore initUserBusinScore(Transinformation traninfo, UserBusinScore userBusinScore,long userId) throws Exception{
		if(userBusinScore==null || userId<=0){
			return userBusinScore;
		}

//		Map<Long,Double> startMap = ProdDao.getInstance(false).getBusinQualityStarMap(userId);
		Map<Long,OnBusinChance> list = ProdDao.getInstance(false).getAllBusinChanceList(userId);

		TableInfo tableInfo = new TableInfo();
//		userBusinScore = new UserBusinScore();
		tableInfo.setTableName(AppContent.HRTC_RDSL_AVERAGEQUALITYLOG);
		tableInfo.setRowKey(String.valueOf(userId));

		traninfo.record("�û�"+userId+"��Ч�̻���"+ (list==null?0:list.size()));
		/*** ѭ������ */
		if(list!=null && list.size()>0){
			double newsumscore = 0.0;
			traninfo.record("ѭ���ж�ÿ���̻��Ǽ�");
			for(OnBusinChance busin:list.values()) {
				try {
					//BusinessScoreManage.getInstance().initBusinScore(traninfo, busin, userId);
					traninfo.record("bcId=" + busin.getId());
					if (busin.getStar() == null) {
						traninfo.record("�Ǽ�Ϊ��");
						BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
						newsumscore += userSingleBusin.getScore();
					} else {
						/*** ֻ���̻�û�������� */
						TableInfo tableconfig = new TableInfo();
						tableconfig.setTableName("hrtc_irsl_busininfolog");
						tableconfig.setRowKey(String.valueOf(busin.getId()));
						Map<String, String> busininfo = HBaseUtilHelper.getRecordByRowkey(tableconfig);

						if (busininfo == null || busininfo.size() <= 0) {
							traninfo.record("hbase��δ�ҵ����̻�");
							BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
							if (userSingleBusin != null) {
								newsumscore += userSingleBusin.getScore();
							}
						} else {
							BusinQualityStar quality = ProdDao.getInstance(false).getBusinQualityStar(busin.getId());
							double hbasescore = 0.0;
//						qualityscore = startMap.get(busin.getId()) == null ? 0.0 : startMap.get(busin.getId()) ;

							String infoscore = busininfo.get("info.score") == null ? "0" : busininfo.get("info.score");
							hbasescore = Double.parseDouble(infoscore);

							if (quality == null) {
								traninfo.record("����δ�ҵ����̻�");
								BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
								newsumscore += userSingleBusin.getScore();
							} else if (quality.getScore_new().doubleValue() != hbasescore) {
								traninfo.record("hbase�з���:" + hbasescore + "������̻�����" + quality.getScore_new().doubleValue() + "��һ��");
								BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
								newsumscore += userSingleBusin.getScore();
							} else if (!"3".equals(busininfo.get("info.status"))) {
								tableconfig.setFamilyName("info");
								tableconfig.setColumnName("status");
								tableconfig.setValue("3");
								HBaseUtilHelper.addRecord(tableconfig);
								newsumscore += hbasescore;
							} else {
								newsumscore += hbasescore;
							}
						}
					}
				} catch (Exception e) {
					traninfo.record("�����̻��쳣bcid:" + busin.getId());
					traninfo.setReason(MessageException.getStackTrace(e));
				}
			}
			/**
			 * �û��̻��ܷ֡�ƽ���֡��������
			 */
			//����
			traninfo.record("�����û��̻��ܷ֡�ƽ����");
			userBusinScore.setUserid(userId);
			userBusinScore.setBusincount(list.size());
			userBusinScore.setBusinscore(newsumscore);
			if(userBusinScore.getBusincount()<=0){
				userBusinScore.setBusinarvgscore(0.0);
			}else{
				userBusinScore.setBusinarvgscore(userBusinScore.getBusinscore()/userBusinScore.getBusincount());
			}
		}
		userBusinScore.setUserid(userId);
		traninfo.record("���û��̻��ܷ֡�ƽ���������ݿ�");
		setUserBusinScore(tableInfo,userBusinScore);

		return userBusinScore;
	}
	/**
	 * ����UserBusinScore
	 * @param tableInfo
	 * @param userBusinScore
	 * @throws Exception
	 * @throws MmtException
	 */

	public void setUserBusinScore(TableInfo tableInfo,UserBusinScore userBusinScore) throws Exception{
		/** * ��д��� */

		//��ȡ�û������������̻���
		long userId = userBusinScore.getUserid();

		Map<String,Long> map = ProdDao.getInstance(false).findBusinStarSum(userId);
		long sumStar = map.get("sumStar");
		long sumBusin = map.get("sumBusin");

		//�����ܷ� 
		long sumscore = ProdDao.getInstance(false).findBusinScoreSum(userId);
		userBusinScore.setBusinscore(sumscore);//�����ܷ�
		userBusinScore.setBusincount(sumBusin);

		double d1 = 0.0;
		if(sumStar!=0  && sumBusin!=0){
			 d1 = (sumStar *0.1)/( sumBusin* 0.1);
		}
		userBusinScore.setFivestarcount(Long.parseLong("" + new DecimalFormat("0").format(d1)  ));

		// my����������¼�
		OnCorTable oct = CorpDao.getInstance(false).getOnCorTable(userId);

		//���õ�ǰ�̻��������ܷ������̻�����
		CorpDao.getInstance(false).saveUpdateUserAveragequality(userBusinScore,sumBusin,sumStar,oct);

		//add by whc 20170620
		//��ҷ���5���̻�
		/*if(oct!=null&&!"0".equals(oct.getIdentity())&&sumBusin>=5){
			OnCorTableAnother another=CorpDao.getInstance(false).getOnCorTableAnother(userId);
			if(another==null||another.getBuyerIdentity()==null){
				CorTableAnother anotherbak=CorpDao.getInstance(false).getCorTableAnother(userId);
				if(anotherbak==null||anotherbak.getBuyerIdentity()==null){
					anotherbak=new CorTableAnother();
					//���ò���
					CorpDao.getInstance(false).saveCorTableAnother();
				}
			}
		}*/

		tableInfo.setFamilyName("info");
		tableInfo.setColumnName("averageScore");
		tableInfo.setValue(String.valueOf(userBusinScore.getBusinarvgscore()));
		HBaseUtilHelper.addRecord(tableInfo,true);

		tableInfo.setColumnName("sumScore");
		tableInfo.setValue(String.valueOf(userBusinScore.getBusinscore()));
		HBaseUtilHelper.addRecord(tableInfo,true);

		tableInfo.setColumnName("businCount");
		tableInfo.setValue(String.valueOf(userBusinScore.getBusincount()));
		HBaseUtilHelper.addRecord(tableInfo,true);

		tableInfo.setColumnName("lastTime");
		if(userBusinScore.getLasttime()==null){
			tableInfo.setValue(DateUtils.getString(new Date(),"yyyy-MM-dd HH:mm:ss"));
		}else{
			tableInfo.setValue(DateUtils.getString(userBusinScore.getLasttime(),"yyyy-MM-dd HH:mm:ss"));
		}
		HBaseUtilHelper.addRecord(tableInfo,true);

		/**��ҷ��͵��̻���>=5�����������Ϊ����s*/
		try {
			logger.info("��ҷ��͵��̻���>=5�����������Ϊ����-------��ʼ");
			if(oct != null && !"0".equals(oct.getIdentity()) && sumBusin >= 5){
				CorTableCommon ctc = CorpDao.getInstance(false).getCorTableCommon(oct.getId());
				if(ctc != null){
					logger.info("��ҷ��͵��̻���>=5�����������Ϊ���� pid-------"+oct.getId());
					String memberidentity = ctc.getMemberidentity();
					if(memberidentity != null && !"1".equals(memberidentity)){
						ctc.setMemberidentity("1");
						CorpDao.getInstance(false).saveOrUpdateCorTableCommon(ctc);
					}
				}else{
					logger.info("��ҷ��͵��̻���>=5�����������Ϊ���� pid-------"+oct.getId());
					ctc = new CorTableCommon();
					ctc.setAreaid(oct.getAreaid());
					ctc.setCreatedate(DateUtils.getSysTimestamp());
					ctc.setId(oct.getId());
					ctc.setMemberidentity("1");
					ctc.setOperstate("0");
					ctc.setSearchstate("0");
					CorpDao.getInstance(false).saveOrUpdateCorTableCommon(ctc);
				}
				logger.info("��ҷ��͵��̻���>=5�����������Ϊ����-------����");
			}
		} catch (Exception e) {
			logger.error("��ҷ��͵��̻���>=5�����������Ϊ����-------�����쳣��"+e);

		}
		/**��ҷ��͵��̻���>=5�����������Ϊ����e*/

	}

	public static void main(String[] args) {
		String s = new Date()+"";
		System.out.println(DateUtils.getSysTimestamp());
	}

}
