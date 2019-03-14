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
 * Reason: TODO ADD REASON(可选). <br/>
 * date: 2015年8月24日 上午11:53:44 <br/>
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
			traninfo.record("处理商机bcids="+busininfo.getBcid()+",操作类型="+oper+",算分标识="+scoreIdentity);
			
			//解析bcid
			traninfo.record("解析bcid");
			String[] bcids = getBusiListByBcid(busininfo.getBcid());
			
			//多个validate
			List<String> validates = convertValid(busininfo);
			
			traninfo.record("循环执行每条商机");
			// 循环
			for(int i=0;i<bcids.length;i++){
                BusinScore businScore = null;
				String bcid = bcids[i];
				traninfo.record("执行单条商机 bcid="+bcid);
				//有效商机数量变化+1-1=0
				long busincountchange=0;
				//有效商机分数变化
				double businscorechange=0.0;
				
				TableInfo tableInfo = new TableInfo();
				tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
				tableInfo.setRowKey(bcid);
				/**
				 * 原始商机状态
				 * 如果原来商机状态为3，不添加数量
				 */
				String oldstatus = null;
				double oldscore = 0.0;
				long userid = 0;
				/*******************************************
				 * 容错，如果hbase中没有该商机，先初始化该商机
				 * 审核、拘审都重新计算分数
				 *******************************************/
				if(oper!=CommContent.BUSIN_OPER_NEWPENDING && oper!=CommContent.BUSIN_OPER_NEWPENDED){
					Map<String, String> map = HBaseUtilHelper.getRecordByRowkey(tableInfo);
					if(map==null || oper==CommContent.BUSIN_OPER_PENDED || oper==CommContent.BUSIN_OPER_REFUSE){
						map = HBaseUtilHelper.getRecordByRowkey(tableInfo);
					}
					if(map != null && map.size()>0){
						oldstatus = map.get("info.status");
						//从hbase商机获得userid
						String iuserid = map.get("info.userid");
						userid = Long.parseLong(iuserid==null ? "0":iuserid);
						String ioldscore = map.get("info.score");
						oldscore = Double.parseDouble(ioldscore==null ? "0":ioldscore);
					}
				}
				tableInfo.setFamilyName("info");
				
				//如果是重发，只更改有效期
				switch(oper){
					/**
					 * 新发待审
					 */
					case CommContent.BUSIN_OPER_NEWPENDING:
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_NEW);
						//用户分数数量不变
						break;
					/**
					 * 新发免审
					 */
					case CommContent.BUSIN_OPER_NEWPENDED:
						buildValidate(busininfo, validates, i);
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						//用户数量分数都增加
						busincountchange+=1;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * 修改待审
					 */
					case CommContent.BUSIN_OPER_UPDATEPENDING:
						buildValidate(busininfo, validates, i);						
						//状态新发未审
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_NEW);						
						break;
					/**
					 * 修改免审
					 */
					case CommContent.BUSIN_OPER_UPDATEPENDED:
						//新增有效时间
						buildValidate(busininfo, validates, i);
						//状态新发未审
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
		//				//状态审核通过
						//如果原来状态status不是3,数量增加
						if(!"3".equals(oldstatus)){
							busincountchange+=1;
						}
						//分数变化,减去老的，加上新的
						businscorechange = businscorechange - oldscore;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * 未过期重发
					 */
					case CommContent.BUSIN_OPER_UNOVERRESEND:
						buildValidate(busininfo, validates, i);
						//新增有效时间
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						break;
					/**
					 * 过期重发
					 */
					case CommContent.BUSIN_OPER_OVERRESEND:
						buildValidate(busininfo, validates, i);
						//新增有效时间
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
		//				//数量+1，分数增加
						busincountchange+=1;
						businscorechange+=oldscore;
						break;
					/**
					 * 转过期
					 */
					case CommContent.BUSIN_OPER_SETOVER:
						//新增有效时间
						try{
							tableInfo.setColumnName("validate");
							tableInfo.setValue(DateUtils.getString(new Date(),"yyyy-MM-dd"));
							HBaseUtilHelper.addRecord(tableInfo);							
						//状态过期							
							tableInfo.setColumnName("status");
							tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_OVER));
							HBaseUtilHelper.addRecord(tableInfo);
							
						}catch(Exception e){
							e.printStackTrace();
						}
						//数量-1，分数减少
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
					/**
					 * 删除
					 */
					case CommContent.BUSIN_OPER_DEL:
						//状态过期
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_DEL));
						HBaseUtilHelper.addRecord(tableInfo);
						//数量-1，分数减少
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
					/**
					 * 审核通过,审核通过，也计算一次商机分数，以防审核修改
					 */
					case CommContent.BUSIN_OPER_PENDED:
						//状态过期
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_PENDED));
						HBaseUtilHelper.addRecord(tableInfo);
						
						businScore = BusinessScoreManage.getInstance().saveBusinInfo(tableInfo,traninfo,businScore,bcid,AppContent.BUSINESS_STATUS_PENDED);
						//数量+1，分数增加
						busincountchange+=1;
						businscorechange+=businScore.getScore();
						break;
					/**
					 * 拒审，拘审也计算一次商机分数，以防拘审修改
					 */
					case CommContent.BUSIN_OPER_REFUSE:
						//状态过期
						tableInfo.setColumnName("status");
						tableInfo.setValue(String.valueOf(AppContent.BUSINESS_STATUS_OVER));
						HBaseUtilHelper.addRecord(tableInfo);
						//数量-1，分数减少
						busincountchange-=1;
						businscorechange-=oldscore;
						break;
				}
				UserBusinScore userBusinScore = new UserBusinScore();
				//多个用户
				if(userBusinScore.getUserid()<=0){
					if(businScore!=null && businScore.getUserid()>0){
						userBusinScore.setUserid(businScore.getUserid());
					}else if(busininfo!=null && busininfo.getUserid()>0){
						userBusinScore.setUserid(busininfo.getUserid());
					}else if(userid!=0){
						userBusinScore.setUserid(userid);
					}else{
						//最后查数据库 
						userid = BusinessScoreManage.getInstance().getUserFromBusin(Long.parseLong(bcid));
						userBusinScore.setUserid(userid);
					}
				}
				//这个数量是修改的数量
				userBusinScore.setBusincount(busincountchange);
				//这个分数是修改的分数
				userBusinScore.setBusinscore(businscorechange);
				//清除缓存 
				MemcachedHelper.remove(userid,FreeUserRealtimeDataMO.class );
				MemcachedHelper.flush();
				/**
				 * 保存至map
				 */
				if(userlist.containsKey( userBusinScore.getUserid() )){
					UserBusinScore userBusin = userlist.get(userBusinScore.getUserid() );
					userBusinScore.setBusincount(userBusinScore.getBusincount() + userBusin.getBusincount());
					userBusinScore.setBusinscore(userBusinScore.getBusinscore() + userBusin.getBusinscore());							
				}
				//临时设置
				userlist.put(userBusinScore.getUserid(),userBusinScore);
			}
			traninfo.record("获取的用户有："+userlist.keySet());
			traninfo.setUserMap(userlist);
				
			//end==========================================
			if(userlist==null || userlist.size()<=0){
				traninfo.setSuccess(false);
				traninfo.setReason("用户列表为空！！");
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
	 * 生成有效期 方法 
	   下午08:28:58
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
	// 将商机ID转换为数组形式
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

	// 转换valiate
	public List<String> convertValid(BusinInfo businInfo) {
		List<String> validateList = new ArrayList<String>();
		// 多个validate
		if (businInfo.getValidates() != null
				&& businInfo.getValidates().indexOf(";") > 0) {
			validateList = Arrays.asList(businInfo.getValidates().split(";"));
		} else {// 单个validate
			validateList.add(businInfo.getValidate());
		}
		return validateList;
	}
}
