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
	 * 初始化用户userBusinScore
	 * 如果从hbase中没有userBusinScore，为初始化后脏数据，为避免脏数据，从数据库中重新加载商机数据，重新计算userBusinScore
	 * 包括有效商机
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

		traninfo.record("用户"+userId+"有效商机数"+ (list==null?0:list.size()));
		/*** 循环保存 */
		if(list!=null && list.size()>0){
			double newsumscore = 0.0;
			traninfo.record("循环判断每条商机星级");
			for(OnBusinChance busin:list.values()) {
				try {
					//BusinessScoreManage.getInstance().initBusinScore(traninfo, busin, userId);
					traninfo.record("bcId=" + busin.getId());
					if (busin.getStar() == null) {
						traninfo.record("星级为空");
						BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
						newsumscore += userSingleBusin.getScore();
					} else {
						/*** 只有商机没分数才算 */
						TableInfo tableconfig = new TableInfo();
						tableconfig.setTableName("hrtc_irsl_busininfolog");
						tableconfig.setRowKey(String.valueOf(busin.getId()));
						Map<String, String> busininfo = HBaseUtilHelper.getRecordByRowkey(tableconfig);

						if (busininfo == null || busininfo.size() <= 0) {
							traninfo.record("hbase中未找到此商机");
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
								traninfo.record("表中未找到此商机");
								BusinScore userSingleBusin = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin);
								newsumscore += userSingleBusin.getScore();
							} else if (quality.getScore_new().doubleValue() != hbasescore) {
								traninfo.record("hbase中分数:" + hbasescore + "与表中商机分数" + quality.getScore_new().doubleValue() + "不一致");
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
					traninfo.record("计算商机异常bcid:" + busin.getId());
					traninfo.setReason(MessageException.getStackTrace(e));
				}
			}
			/**
			 * 用户商机总分、平均分、总数入库
			 */
			//更新
			traninfo.record("计算用户商机总分、平均分");
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
		traninfo.record("将用户商机总分、平均分入数据库");
		setUserBusinScore(tableInfo,userBusinScore);

		return userBusinScore;
	}
	/**
	 * 保存UserBusinScore
	 * @param tableInfo
	 * @param userBusinScore
	 * @throws Exception
	 * @throws MmtException
	 */

	public void setUserBusinScore(TableInfo tableInfo,UserBusinScore userBusinScore) throws Exception{
		/** * 回写入库 */

		//获取用户的总星数和商机数
		long userId = userBusinScore.getUserid();

		Map<String,Long> map = ProdDao.getInstance(false).findBusinStarSum(userId);
		long sumStar = map.get("sumStar");
		long sumBusin = map.get("sumBusin");

		//查找总分 
		long sumscore = ProdDao.getInstance(false).findBusinScoreSum(userId);
		userBusinScore.setBusinscore(sumscore);//设置总分
		userBusinScore.setBusincount(sumBusin);

		double d1 = 0.0;
		if(sumStar!=0  && sumBusin!=0){
			 d1 = (sumStar *0.1)/( sumBusin* 0.1);
		}
		userBusinScore.setFivestarcount(Long.parseLong("" + new DecimalFormat("0").format(d1)  ));

		// my添加排名，新加
		OnCorTable oct = CorpDao.getInstance(false).getOnCorTable(userId);

		//设置当前商机总数，总分数，商机条数
		CorpDao.getInstance(false).saveUpdateUserAveragequality(userBusinScore,sumBusin,sumStar,oct);

		//add by whc 20170620
		//买家发了5条商机
		/*if(oct!=null&&!"0".equals(oct.getIdentity())&&sumBusin>=5){
			OnCorTableAnother another=CorpDao.getInstance(false).getOnCorTableAnother(userId);
			if(another==null||another.getBuyerIdentity()==null){
				CorTableAnother anotherbak=CorpDao.getInstance(false).getCorTableAnother(userId);
				if(anotherbak==null||anotherbak.getBuyerIdentity()==null){
					anotherbak=new CorTableAnother();
					//设置参数
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

		/**买家发送的商机数>=5条，升级买家为卖家s*/
		try {
			logger.info("买家发送的商机数>=5条，升级买家为卖家-------开始");
			if(oct != null && !"0".equals(oct.getIdentity()) && sumBusin >= 5){
				CorTableCommon ctc = CorpDao.getInstance(false).getCorTableCommon(oct.getId());
				if(ctc != null){
					logger.info("买家发送的商机数>=5条，升级买家为卖家 pid-------"+oct.getId());
					String memberidentity = ctc.getMemberidentity();
					if(memberidentity != null && !"1".equals(memberidentity)){
						ctc.setMemberidentity("1");
						CorpDao.getInstance(false).saveOrUpdateCorTableCommon(ctc);
					}
				}else{
					logger.info("买家发送的商机数>=5条，升级买家为卖家 pid-------"+oct.getId());
					ctc = new CorTableCommon();
					ctc.setAreaid(oct.getAreaid());
					ctc.setCreatedate(DateUtils.getSysTimestamp());
					ctc.setId(oct.getId());
					ctc.setMemberidentity("1");
					ctc.setOperstate("0");
					ctc.setSearchstate("0");
					CorpDao.getInstance(false).saveOrUpdateCorTableCommon(ctc);
				}
				logger.info("买家发送的商机数>=5条，升级买家为卖家-------结束");
			}
		} catch (Exception e) {
			logger.error("买家发送的商机数>=5条，升级买家为卖家-------出现异常："+e);

		}
		/**买家发送的商机数>=5条，升级买家为卖家e*/

	}

	public static void main(String[] args) {
		String s = new Date()+"";
		System.out.println(DateUtils.getSysTimestamp());
	}

}
