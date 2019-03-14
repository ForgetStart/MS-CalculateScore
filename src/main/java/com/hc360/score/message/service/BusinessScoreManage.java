package com.hc360.score.message.service;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.DateUtils;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.bcs.utils.BusinCompleCaluteUtils;
import com.hc360.bcs.utils.StringUtil;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.utils.CommContent;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.proddb.*;
import com.hc360.mmt.db.po.proddb.BusinAttValue;
import com.hc360.mmt.db.po.proddb.BusinChance;
import com.hc360.mmt.db.po.statdb.*;
import com.hc360.rsf.imgup.FileStorageService;
import com.hc360.rsf.imgup.FileStorageService2WH;
import com.hc360.rsf.kvdb.service.KVDBResult;
import com.hc360.score.calculateservice.CalculateFactory;
import com.hc360.score.calculateservice.CalculateTemplate;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.db.dao.ProdSampleDao;
import com.hc360.score.db.dao.StatDao;
import com.hc360.score.db.rsf.RSFService;
import com.hc360.score.message.Transinformation;
import com.hc360.score.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;


/**
 * 黄金罗盘manage
 *
 * @author saiwengang
 *
 */
public class BusinessScoreManage {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	private static BusinessScoreManage instance = new BusinessScoreManage();
	public static BusinessScoreManage getInstance(){
		return instance;
	}

	/**
	 * 保存商机
	 * @param tableInfo
	 * @param traninfo
	 * @param businScore
	 * @param bcid
	 * @return
	 */
	public BusinScore saveBusinInfo(TableInfo tableInfo,Transinformation traninfo,BusinScore businScore,String bcid,int status,String source) throws Exception{
		BusinInfo busininfo = traninfo.getBusininfo();
        /**
         * 计算分数,如果是0分，从数据库查,重新算分
         */
		if(businScore==null || businScore.getScore()==0.0 ){
            traninfo.record("计算单条商机分数");
			businScore = initSingleBusinScore(bcid,"111111");
		}
        //保存到hbase中
		//addHBaseBusinInfo(tableInfo,busininfo, businScore,status);

        //回写库
        traninfo.record("回写入数据库商机分数");
		reSetDBBusinScore(Long.parseLong(bcid),busininfo,businScore,source);

		return businScore;
	}


    public BusinScore saveBusinInfo(TableInfo tableInfo,Transinformation traninfo,BusinScore businScore,String bcid,int status) throws Exception{
        BusinInfo busininfo = traninfo.getBusininfo();
        /**
		 * 计算分数,如果是0分，从数据库查,重新算分
         */
        String scoreIdentityMessage=null;
        if(businScore==null || businScore.getScore()==0.0 ){
			traninfo.record("计算单条商机分数");
            scoreIdentityMessage=busininfo.getScoreIdentity();
            if(StringUtil.isBlank(busininfo.getScoreIdentity())){
                busininfo.setScoreIdentity("111111");
            }
            //TODO 为了处理历史数据先设置成111111，每项都算分
            busininfo.setScoreIdentity("111111");
            businScore = initSingleBusinScore(bcid,busininfo.getScoreIdentity());
        }
		//保存到hbase中
        addHBaseBusinInfo(tableInfo,busininfo, businScore,status);

		//回写库
		traninfo.record("回写入数据库商机分数");
        reSetDBBusinScore(Long.parseLong(bcid),scoreIdentityMessage,busininfo,businScore);

        return businScore;
    }

	private void addHBaseBusinInfo(TableInfo tableInfo,BusinInfo busininfo, BusinScore businScore,int status) throws MmtException{
		Map<String, String> hbaseMap = new HashMap<String, String>();
		if(businScore!=null){
			hbaseMap.put("score", String.valueOf(businScore.getScore()));

            //modify by whc 2016/07/29
			//hbaseMap.put("quality", businScore.getQuality());

			hbaseMap.put("star", "" + businScore.getStar() );

			hbaseMap.put("userid", "" + businScore.getUserid() );

			hbaseMap.put("state", "" + status);
		}

		if(busininfo!=null && busininfo.getCreateTime()!=null ){
			//新增发布时间
			hbaseMap.put("time", DateUtils.getString(busininfo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
		}
		//新增有效时间
		if(busininfo!=null && busininfo.getValidate()!=null ){
			hbaseMap.put("validate", busininfo.getValidate());
		}
		for(Map.Entry<String, String> kv : hbaseMap.entrySet()){
			tableInfo.setColumnName(kv.getKey());
			tableInfo.setValue(kv.getValue());
			HBaseUtilHelper.addRecord(tableInfo);
		}
	}
	/**
	 * 初始化单条商机
	 * @param bcid
	 * @return  出现异常后，返回空指针
	 * @throws MmtException
	 */
	public BusinScore initSingleBusinScore(String bcid,String scoreIdentity) throws Exception {

        /*FileStorageService fss=RSFService.getFilestorageService();
		BusinSourceInfo myBusinSourceInfo = getBusinInfoForCc(Long.parseLong(bcid));
		BusinScore  businScore = BusinCompleCaluteNewUtils.caluteScore(myBusinSourceInfo,fss);*/

        //modify by whc 2016/07/20 采用算分规则3.0
        BusinScore  businScore =caluteScoreNew(bcid,scoreIdentity);

		//如果用户userId不为空，就必须从数据库中获取一次值
		if(businScore.getUserid()==0){
			long userid =  getUserFromBusin(Long.parseLong(bcid));
			businScore.setUserid(userid);
		}
		//根据bc_id查找Userid
		return businScore;
	}
	/**
	 * 回写黄金罗盘商机分数
	 * @param bcid
	 * @param busininfo
	 * @param businScore
	 * @return
	 * @throws MmtException
	 */
	public boolean reSetDBBusinScore(long bcid,BusinInfo busininfo,BusinScore businScore,String source)throws MmtException{
		if(busininfo==null){
			return false;
		}
		boolean result = true;
        //回写黄金罗盘商机分数
		//reSetCompass(bcid, businScore);

        //回写单个商机分数
		addBusinQualityStar(bcid, businScore,source);

         //回写onbusinchance 和 businchance
		 //reSetBusinChance(bcid, businScore);

		return result;
	}

    public boolean reSetDBBusinScore(long bcid,String scoreIdentityMessage,BusinInfo busininfo,BusinScore businScore)throws MmtException{
        if(busininfo==null){
            return false;
        }
        boolean result = true;
		//回写黄金罗盘商机分数
        reSetCompass(bcid, businScore);

		//回写单个商机分数
        addBusinQualityStarNew(bcid, businScore);

        //回写onbusinchance 和 businchance
        //reSetBusinChance(bcid, businScore);
        //modify by whc 2016/06/23
        reSetBusinChance(bcid,busininfo, businScore);

        //新增或者修改
        //annotation by whc 2017/03/06
        /*if(CommContent.BUSIN_OPER_NEWPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_NEWPENDED==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDED==busininfo.getOper()){
            //写详情宽高
            if(StringUtil.isBlank(scoreIdentityMessage)||(StringUtil.isNotBlank(scoreIdentityMessage)&&'1'==scoreIdentityMessage.charAt(5))){
                addBusinIntroduceWHInfo(bcid,businScore);
            }
            //写商机参数
            if(StringUtil.isBlank(scoreIdentityMessage)||(StringUtil.isNotBlank(scoreIdentityMessage)&&'1'==scoreIdentityMessage.charAt(1))){
                addBusinAttValue(bcid);
            }
        }*/

        return result;
    }
    private void reSetBusinChance(long bcid, BusinInfo busininfo,BusinScore businScore) throws MmtException{
        ProdDao daoprod = ProdDao.getInstance(false);

        BusinChance businChance = daoprod.getBusinChance(bcid);
        if(businChance !=null){
            businChance.setStar(String.valueOf(businScore.getStar()));
            if(busininfo!=null&& (CommContent.BUSIN_OPER_UPDATEPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDED==busininfo.getOper())){
                businChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by whc 2016/06/23 商机修改时间
            }
            daoprod.saveBusinChance(businChance);
        }

        OnBusinChance onBusinChance = daoprod.getOnBusinChance(bcid);
        if(onBusinChance !=null){
            onBusinChance.setStar(String.valueOf(businScore.getStar()));
            if(busininfo!=null&& (CommContent.BUSIN_OPER_UPDATEPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDED==busininfo.getOper())){
                onBusinChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by whc 2016/06/23 商机修改时间
            }
            daoprod.saveOnBusinChance(onBusinChance);
        }
    }

    private void reSetBusinChance(long bcid, BusinScore businScore) throws MmtException{
		ProdDao daoprod = ProdDao.getInstance(false);

		BusinChance businChance = daoprod.getBusinChance(bcid);
		if(businChance !=null){
			businChance.setStar(String.valueOf(businScore.getStar()));
            businChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by wanghuacun 2016/06/14 商机修改时间
            daoprod.saveBusinChance(businChance);
        }

        OnBusinChance onBusinChance = daoprod.getOnBusinChance(bcid);
        if(onBusinChance !=null){
            onBusinChance.setStar(String.valueOf(businScore.getStar()));
            onBusinChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by wanghuacun 2016/06/14 商机修改时间
            daoprod.saveOnBusinChance(onBusinChance);
        }
    }
	private void addBusinQualityStar(long bcid, BusinScore businScore,String source) throws MmtException{
        BusinQualityStarSample businQualityStar = new BusinQualityStarSample();
        businQualityStar.setBcid(bcid);
        businQualityStar.setScore( chanceNum(businScore.getScore()) );
        businQualityStar.setStar(  businScore.getStar());
        businQualityStar.setModifydate(DateUtils.getSysTimestamp());
        businQualityStar.setHassupcat(chanceNum(businScore.getHasSupcate()));
        businQualityStar.setHasphoto(  chanceNum(businScore.getHasphoto()));
        businQualityStar.setFirstphoto( chanceNum(businScore.getFirstphoto()));
        businQualityStar.setHasprice( chanceNum( businScore.getHasprice()));
        businQualityStar.setHasminordernum(chanceNum(businScore.getHasMinNum() ));
        businQualityStar.setHaslongtitle(chanceNum(businScore.getHaslongtitle()));
        businQualityStar.setHaslongintroduce(chanceNum(businScore.getHaslongintroduce()));

        businQualityStar.setPhotocount(chanceNum(businScore.getPhotocount()));
        businQualityStar.setHasparam(chanceNum(businScore.getHasnoparam()));
        businQualityStar.setUserid(businScore.getUserid() );
        // 报价方式的算分
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //供应总量的分
        businQualityStar.setHastype(chanceNum(businScore.getHasnum() ));
        businQualityStar.setHasprice(chanceNum(businScore.getHasprice()));
        businQualityStar.setSource(source);

        //ProdDao.getInstance(false).resetFreeBusinComplete(businQualityStar);

        ProdSampleDao.getInstance(false).resetFreeBusinCompleteSample(businQualityStar);
    }

	private void reSetCompass(long bcid, BusinScore businScore) throws MmtException{
		ProdDao daoprod = ProdDao.getInstance(false);
		CompassBusinComplete businComplete = daoprod.getCompassBusinComplete(bcid);
		/**
		 * 如果从收费会员黄金罗盘表中查出数据，回写，并回写BUSIN_QUALITY_STAR
		 */
		if(businComplete!=null){
			//回写
			businComplete.setStar(businScore.getStar());
			businComplete.setScore(BigDecimal.valueOf(businScore.getScore()));
			/**
			 * 细分项
			 */
			//上传产品图片
			businComplete.setHasphoto(BigDecimal.valueOf(businScore.getHasphoto()));
			//优化产品图片
			businComplete.setFirstphoto(BigDecimal.valueOf(businScore.getFirstphoto()));
			//选择直接报价
			businComplete.setHasprice(BigDecimal.valueOf(businScore.getHasprice()));
			//填写最小起订量
			businComplete.setHasminordernum(BigDecimal.valueOf(businScore.getHasMinNum() ));
			//完善信息标题
			businComplete.setHaslongtitle(BigDecimal.valueOf(businScore.getHaslongtitle()));
			//补充详细说明
			businComplete.setHaslongintroduce(BigDecimal.valueOf(businScore.getHaslongintroduce()));
			//填写供货总量
			businComplete.setHastype(BigDecimal.valueOf(businScore.getHasnum()));
			//增加细节图片
			businComplete.setPhotocount(BigDecimal.valueOf(businScore.getPhotocount()));
			//完善产品参数
			businComplete.setHasparam(BigDecimal.valueOf(businScore.getHasnoparam()));
			daoprod.resetCompassBusinComplete(businComplete);
			/**
			 * 回写黄金罗盘用户商机质量总分
			 */
		}
	}

    private void addBusinQualityStar(long bcid, BusinScore businScore) throws MmtException{
        BusinQualityStarSample businQualityStar = new BusinQualityStarSample();
        businQualityStar.setBcid(bcid);
        businQualityStar.setScore( chanceNum(businScore.getScore()) );
        businQualityStar.setStar(  businScore.getStar());
        businQualityStar.setModifydate(DateUtils.getSysTimestamp());
        businQualityStar.setHassupcat(chanceNum(businScore.getHasSupcate()));
        businQualityStar.setHasphoto(  chanceNum(businScore.getHasphoto()));
        businQualityStar.setFirstphoto( chanceNum(businScore.getFirstphoto()));
        businQualityStar.setHasprice( chanceNum( businScore.getHasprice()));
        businQualityStar.setHasminordernum(chanceNum(businScore.getHasMinNum() ));
        businQualityStar.setHaslongtitle(chanceNum(businScore.getHaslongtitle()));
        businQualityStar.setHaslongintroduce(chanceNum(businScore.getHaslongintroduce()));

        businQualityStar.setPhotocount(chanceNum(businScore.getPhotocount()));
        businQualityStar.setHasparam(chanceNum(businScore.getHasnoparam()));
        businQualityStar.setUserid(businScore.getUserid() );
        // 报价方式的算分
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //供应总量的分
        businQualityStar.setHastype(chanceNum(businScore.getHasnum() ));
        businQualityStar.setHasprice(chanceNum(businScore.getHasprice()));

        //ProdDao.getInstance(false).resetFreeBusinComplete(businQualityStar);

        ProdSampleDao.getInstance(false).resetFreeBusinCompleteSample(businQualityStar);
    }

    private void addBusinQualityStarNew(long bcid, BusinScore businScore) throws MmtException{
        BusinQualityStar businQualityStar = new BusinQualityStar();
        businQualityStar.setBcid(bcid);
        businQualityStar.setScore_new(chanceNum(businScore.getScore()));
        businQualityStar.setStar_new((long)businScore.getStar());
        businQualityStar.setModifydate_new(DateUtils.getSysTimestamp());
        businQualityStar.setHassupcat_new(chanceNum(businScore.getHasSupcate()));
        businQualityStar.setHasphoto_new(chanceNum(businScore.getHasphoto()));
        businQualityStar.setFirstphoto_new(chanceNum(businScore.getFirstphoto()));
        businQualityStar.setHasprice_new(chanceNum(businScore.getHasprice()));
        businQualityStar.setHasminordernum_new(chanceNum(businScore.getHasMinNum()));
        businQualityStar.setHaslongtitle_new(chanceNum(businScore.getHaslongtitle()));
        businQualityStar.setHaslongintroduce_new(chanceNum(businScore.getHaslongintroduce()));

        businQualityStar.setPhotocount_new(chanceNum(businScore.getPhotocount()));
        businQualityStar.setHasparam_new(chanceNum(businScore.getHasnoparam()));
        businQualityStar.setUserid(businScore.getUserid() );
        // 报价方式的算分
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //供应总量的分
        businQualityStar.setHastype_new(chanceNum(businScore.getHasnum()));
        businQualityStar.setHasprice_new(chanceNum(businScore.getHasprice()));

        ProdDao.getInstance(false).resetFreeBusinComplete(businQualityStar);

    }

	public BigDecimal chanceNum(double d){
		BigDecimal s = BigDecimal.valueOf( d );
		return s;
	}

    /**
     * 添加详情宽高
     * @param bcid
     * @param businScore
     * @throws MmtException
     */
    private void addBusinIntroduceWHInfo(long bcid, BusinScore businScore) throws MmtException{
        BusinIntroduceWHinfo sbiWHinfo= BusinIntorduceCsinfoUtils.convert2BusinIntroduceWHinfo(businScore);
        StatDao statDao = StatDao.getInstance(false);
        BusinIntroduceWHinfo dbiWHinfo=statDao.getBusinIntroduceWHinfo(bcid);
        if(dbiWHinfo!=null){
            //保存商机详情信息（宽高）
            BusinIntorduceCsinfoUtils.convertS2D(sbiWHinfo,dbiWHinfo);
            statDao.updateBusinIntroduceWHinfo(dbiWHinfo);
        }else{
            long providerid=ProdDao.getInstance(false).getProviderid(businScore.getUserid());
            sbiWHinfo.setProviderid(providerid);
            sbiWHinfo.setBcid(bcid);
            statDao.saveBusinIntroduceWHinfo(sbiWHinfo);
        }

    }

    /**
     * 添加商机参数
     * @param bcid
     * @throws MmtException
     */
    private void addBusinAttValue(long bcid) throws MmtException{
        List<BusinAttValue> sbavList=ProdDao.getInstance(false).getAllBusinAtt(bcid);
        if(sbavList!=null&&sbavList.size()>0){
            List<com.hc360.mmt.db.po.statdb.BusinAttValue> dbavList=new ArrayList<com.hc360.mmt.db.po.statdb.BusinAttValue>();
            BusinIntorduceCsinfoUtils.convertS2D(sbavList, dbavList);
            StatDao.getInstance(false).deleteBusinAttValue(bcid);
            if(dbavList!=null&&dbavList.size()>0){
                for(com.hc360.mmt.db.po.statdb.BusinAttValue dbav:dbavList){
                    StatDao.getInstance(false).saveBusinAttValue(dbav);
                }
            }
        }
    }

	/**
	 * 从商机获得用户id
	 * @param bcid
	 * @return
	 */
	public long getUserFromBusin(long bcid) throws Exception{
		if(bcid==0){
			return 0;
		}

		ProdDao daoprod = ProdDao.getInstance(false);
		CorpDao dao = CorpDao.getInstance(false);

		OnBusinChance onbusinchance = daoprod.getOnBusinChance(bcid);
		if(onbusinchance!=null){
			return dao.getUserid(onbusinchance.getProviderid());
		}else{
			BusinChance businchance = daoprod.getBusinChance(bcid);
			if(businchance!=null){
				return dao.getUserid(businchance.getProviderid());
			}
		}
		return 0;
	}
	/**
	 * businchance和onbusinchance互转
	 * @param businchance
	 * @return
	 */
	public static OnBusinChance chgOnBusinChance(BusinChance businchance){
		if(businchance==null){
			return null;
		}
		OnBusinChance onbusinchance = new OnBusinChance();
		onbusinchance.setStates(businchance.getStates());
		onbusinchance.setId(businchance.getId());
		onbusinchance.setUnchecked(businchance.getUnchecked());
		onbusinchance.setChecked(businchance.getChecked());
		onbusinchance.setCreatedate(businchance.getCreatedate());
		onbusinchance.setValiddate(businchance.getValiddate());
		onbusinchance.setSupcatid(businchance.getSupcatid());
		onbusinchance.setType(businchance.getType());
		onbusinchance.setPricerange(businchance.getPricerange());
		onbusinchance.setBrandid(businchance.getBrandid());
		onbusinchance.setMinordernum(businchance.getMinordernum());
		onbusinchance.setTitle(businchance.getTitle());
		onbusinchance.setKeyword(businchance.getKeyword());
		onbusinchance.setSupcatid(businchance.getSupcatid());
		onbusinchance.setProviderid(businchance.getProviderid());
		onbusinchance.setEnddate(businchance.getEnddate());
		return onbusinchance;
	}
	/**
	 * 根据bcid获取实时计算源数据信息，用于算分和星级（产品库项目）
	 * 第一步：查询busin表，获取基本信息
	 * 第二步：获取详细信息
	 * 第三步：获取图片信息
	 * 第四步：获取参数信息
	 * @author Gao xingkun
	 * @version 1.0
	 * @date 2014-5-9 上午10:02:19
	 * @param bcid
	 * @return
	 * @throws Exception
	 */
	public BusinSourceInfo getBusinInfoForCc(long bcid) throws Exception {
		//获取商机基本信息(先查询busin表，如果没有在查询on表 审核的商机因为数据有个推送的过程)
		if(bcid == 0){
			return null;
		}

		/**注意：因为该方法只有审核通过（免审）的商机才会调用，所以不用考虑查询商机的条件
		*-----第一步：查询busin表，获取基本信息**/

		String context = "";
		KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
		if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
			if (oldResult.getValue() != null) {
				context = new String(oldResult.getValue());
			}
		}

		boolean businFlag = false;//基本信息是否存在
		BusinSourceInfo bsi = null;
		String supcatid = "";//品类id 获取查询该商机的所有参数项
		BusinChance busin = ProdDao.getInstance(false).getBusinChance(bcid);
		if(busin!=null){
			bsi = new BusinSourceInfo();
            //设置类目
            bsi.setSupcatid(busin.getSupcatid());
            //设置在线交易
            bsi.setIssupporttrade(busin.getIssupporttrade());
            //设置关键字、标题、最小起订量和供应总量等信息
			bsi.setTitle(busin.getTitle());
			bsi.setKeyword(busin.getKeyword());
			bsi.setMinOrderNum(busin.getMinordernum());
			bsi.setNum((busin.getNum())!=null?Long.valueOf(busin.getNum()):0l);
			bsi.setPriceType(busin.getPricerange().doubleValue()==0?0:1);//是否是电话报价，通过是否支持在线交易即可
            //价格条数
            int priceItemsCount=ProdDao.getInstance(false).getPriceItemsCount(bcid);
            bsi.setPriceItemsCount(priceItemsCount);
            bsi.setIntroduce(context );
			supcatid = busin.getSupcatid();
			businFlag = true;
		}else{//BusinChance里没有则到OnBusinChance表里去找
			OnBusinChance obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
			if(obc!=null){
				bsi = new BusinSourceInfo();
                //设置类目
                bsi.setSupcatid(obc.getSupcatid());
                //设置在线交易
                bsi.setIssupporttrade(obc.getIssupporttrade());
				bsi.setTitle(obc.getTitle());
				bsi.setKeyword(obc.getKeyword());
				/******************注意：最小起订量和供应总量因报价类型不同获取方式不同Onbsinchance表设置了响应值
				 * 算分项只判断【有无】即可，所以从这里获取就可以******************/
				bsi.setMinOrderNum(obc.getMinordernum());
				bsi.setNum(StringUtil.isNotBlank(obc.getNum())?Long.valueOf(obc.getNum()):0l);
//				bsi.setPriceType((obc.getIssupporttrade()!=null&&"1".equals(obc.getIssupporttrade()))?1:0);//是否是电话报价，通过是否支持在线交易即可
				bsi.setPriceType(obc.getPricerange().doubleValue()==0?0:1);//是否是电话报价，通过是否支持在线交易即可
                //价格条数
                int priceItemsCount=ProdDao.getInstance(false).getPriceItemsCount(bcid);
                bsi.setPriceItemsCount(priceItemsCount);
                supcatid = obc.getSupcatid();
				bsi.setIntroduce(context );
				businFlag = true;
			}
		}
		/*******************************查询商机基本信息结束*********************************************/
		if(businFlag){
			/*** 第二步：获取详细信息 */
			bsi.setIntroduce(context);
			/** * 第三步：获取图片信息 */
			//图片。通过bc_id查找图片信息（设置是否有图片、图片数量和第一张图片的宽高等）
			List<BusinMultimedia> bmList =ProdDao.getInstance(false).getBusinPicdetailList(bcid);
			if(bmList!=null){
				int size = bmList.size();
				if(size>0){
					bsi.setImgNum(size);
					if(size>=1){
						bsi.setFirstImageHeight(Integer.valueOf(Long.toString(bmList.get(0).getImageheight())));
						bsi.setFirstImageWidth(Integer.valueOf(Long.toString(bmList.get(0).getImagewidth())));
					}
				}
				//设置图片数量
				bsi.setImgNum(size);
			}

			/*** 第四步：获取参数信息*/
			//通过supcatid获取catid  100004460
			if (supcatid != null && !"".equals(supcatid)) {
				String catid = CorpDao.getInstance(false).getCatidBySupcatid(supcatid);
				if (catid != null && !"".equals(catid)) {
					// 通过catid获取品类下所有项(包括规格参数)
					List<PageRecordBean> cpList = ProdDao.getInstance(false).getNortCategoryParam1(catid);
					// 获取已填写的参数项 a.pi_id,i.name,a.is_required
					List<BusinAttValue> baList = ProdDao.getInstance(false).getNorBusinAtt(bcid);
					int nortparamnum = 0;
					int norparamnum = 0;
					int userparamnum = 0;
					if (cpList != null && cpList.size() > 0) {
						for (PageRecordBean bean : cpList) {
							String isrequired = bean.getString("is_required");
							if (isrequired.equals("0")) {
								nortparamnum++;// 非必填的品类参数
							}
						}
						if (baList != null && baList.size() > 0) {
							for (BusinAttValue bav : baList) {
								String attname = bav.getAttname();
                                //设置品牌型号
                                if(StringUtil.isNotBlank(attname)&&"品牌".equals(attname)){
                                    bsi.setBrand(bav.getAttvalue());
                                }
                                if(StringUtil.isNotBlank(attname)&&"型号".equals(attname)){
                                    bsi.setModel(bav.getAttvalue());
                                }
								for (PageRecordBean bean : cpList) {
									String name = bean.getString("name");
									if (StringUtil.isNotBlank(attname)&&attname.equals(name)) {
										userparamnum++;
										String isrequired = bean.getString("is_required");
										if (isrequired.equals("1"))
											norparamnum++;
										break;
									}
								}
							}
							norparamnum = baList.size() - norparamnum; // 所有已经填写的-必填项=所有非必填项
							userparamnum = baList.size() - userparamnum; // 所有已经填写的-和品类参数名称一样的=用户自定义项
						}
					} else {
						if (baList != null && baList.size() > 0) {
							userparamnum = baList.size();
						}
					}
					bsi.setNortParamNum(nortparamnum);
					bsi.setNorParamNum(norparamnum);
					bsi.setUsrParamNum(userparamnum);


                    //添加已填写的参数数量 参数是否含有限期整改词 已填写的参数重复度
                    List<BusinAttValue> cbaList=ProdDao.getInstance(false).getCommonBusinAtt(bcid);
                    if (cbaList != null && cbaList.size() > 0) {
                        /*String paramVal="";
                        for(BusinAttValue cbav : cbaList){
                                paramVal+=cbav.getAttvalue()+",";
                        }*/
                        bsi.setParamAmount(cbaList.size());
                        //去掉 限期整改词判断 2016/02/020
                        /*boolean paramDeadlineRectification=DeadlineRectificationUtils.isDeadlineRectification(paramVal);
                        bsi.setHasParamDeadlineRectification(paramDeadlineRectification);*/
                        bsi.setParamRepetition(BusinCompleCaluteUtils.obtainParamRepetition(cbaList));
                    }

				}
			}
		}
		return bsi;
	}

    /**
     * 初始化单条商机分数
     * @param busin   单条商机信息
     * @param traninfo
     * @throws Exception
     */
    public BusinScore initBusinScore(Transinformation traninfo, OnBusinChance busin) throws Exception{

        int status = -1;
        //判断商机状态,有效否
        if(busin==null){
            return null;
        }
        if("1".equals(busin.getStates())){
            //无效
            status = AppContent.BUSINESS_STATUS_DEL;
        }
        //审核通过
        else if("1".equals(busin.getChecked())){
            //是否过期,没过期
            if(DateUtils.comDate(DateUtils.parseDate(busin.getEnddate(), "yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(DateUtils.getSysTimestamp(), "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss")){
                status = AppContent.BUSINESS_STATUS_PENDED;
            }else{
                //没有有效期，算过期
                status = AppContent.BUSINESS_STATUS_OVER;
            }
        }
        //免审拒审
        else if("1".equals(busin.getUnchecked())){
            status = AppContent.BUSINESS_STATUS_REFUSE;
        }
        //待审
        else{
            status = AppContent.BUSINESS_STATUS_NEW;
        }
        /*** 入hbase */
        TableInfo tableInfo = new TableInfo();
//		tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
        tableInfo.setTableName("hrtc_irsl_busininfolog");
        tableInfo.setRowKey(String.valueOf(busin.getId()));
        tableInfo.setFamilyName("info");

//		BusinSourceInfo myBusinSourceInfo;
//		BusinScore mySingleBusinScore ;
//		/** * 计算商机完整信息*/
//		myBusinSourceInfo =	getBusinInfoForCc(Long.parseLong(""+busin.getId()));
//		mySingleBusinScore = BusinCompleCaluteUtils.caluteScore(myBusinSourceInfo);
//		//可能没有userid
//		BusinScore businScore = new BusinScore();
//		BusinInfo businInfo = new BusinInfo();
//		businInfo.setUserid(userid);
        /*** 入库 */
        BusinScore mySingleBusinScore = saveBusinInfo(tableInfo, traninfo, null,String.valueOf(busin.getId()),status);
        /**
         * 如果商机有效，追加用户总分
         */
        return mySingleBusinScore;
    }

	/**
	 * 初始化单条商机分数
	 * @param busin   单条商机信息
	 * @param source
	 * @throws Exception
	 */
	public BusinScore initBusinScore(Transinformation traninfo, OnBusinChance busin,String source) throws Exception{

		int status = -1;
		//判断商机状态,有效否
		if(busin==null){
			return null;
		}
		if("1".equals(busin.getStates())){
			//无效
			status = AppContent.BUSINESS_STATUS_DEL;
		}
		//审核通过
		else if("1".equals(busin.getChecked())){
			//是否过期,没过期
			if(DateUtils.comDate(DateUtils.parseDate(busin.getEnddate(), "yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(DateUtils.getSysTimestamp(), "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss")){
				status = AppContent.BUSINESS_STATUS_PENDED;
			}else{
				//没有有效期，算过期
				status = AppContent.BUSINESS_STATUS_OVER;
			}
		}
		//免审拒审
		else if("1".equals(busin.getUnchecked())){
			status = AppContent.BUSINESS_STATUS_REFUSE;
		}
		//待审
		else{
			status = AppContent.BUSINESS_STATUS_NEW;
		}
		/*** 入hbase */
		TableInfo tableInfo = new TableInfo();
//		tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
		tableInfo.setTableName("hrtc_irsl_busininfolog");
		tableInfo.setRowKey(String.valueOf(busin.getId()));
		tableInfo.setFamilyName("info");

//		BusinSourceInfo myBusinSourceInfo;
//		BusinScore mySingleBusinScore ;
//		/** * 计算商机完整信息*/
//		myBusinSourceInfo =	getBusinInfoForCc(Long.parseLong(""+busin.getId()));
//		mySingleBusinScore = BusinCompleCaluteUtils.caluteScore(myBusinSourceInfo);
//		//可能没有userid
//		BusinScore businScore = new BusinScore();
//		BusinInfo businInfo = new BusinInfo();
//		businInfo.setUserid(userid);
		/*** 入库 */
		BusinScore mySingleBusinScore = saveBusinInfo(tableInfo, traninfo, null,String.valueOf(busin.getId()),status,source);
		/**
		 * 如果商机有效，追加用户总分
		 */
		return mySingleBusinScore;
	}


    /**
     * 获取详情图片信息
     * @param bcid
     * @throws Exception
     */
    public void caculateIntroducePic(long bcid,String source) throws Exception {
        String context = "";
        KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
        if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
            if (oldResult.getValue() != null) {
                context = new String(oldResult.getValue());
            }
        }
        FileStorageService fss=RSFService.getFilestorageService();
        Map<String,Integer> imgWhMap=BusinCompleCaluteNewUtils.doIntroduceImgInterval(context,fss);
        BusinIntroducePicWh bipWh=BusinchanceSampleUtils.convert(imgWhMap,bcid,source);
        ProdDao daoprod = ProdDao.getInstance(false);
        BusinIntroducePicWh bipWhExist=daoprod.getBusinIntroducePicWh(bcid);
        if(bipWhExist!=null){
            BusinchanceSampleUtils.convert(bipWh,bipWhExist);
            daoprod.updateBusinIntroducePicWh(bipWhExist);
        }else{
            daoprod.saveBusinIntroducePicWh(bipWh);
        }

    }

    /**
     * 处理商机算分信息
     * @param bcid
     * @throws Exception
     */
    public void dealIntroduceCsInfo(long bcid,int threadno,List<BusinIntroduceCsinfo> ciList) throws Exception {
        String context = "";
        String encode="";
        KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
        if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
            if (oldResult.getValue() != null) {
                try{
                    encode=EncodeUtil.guessEncoding(oldResult.getValue());
                    //encode=EncodeUtil.getEncoding(new String(oldResult.getValue()));
                }catch(Exception e){
                    e.printStackTrace();
                    encode="error";
                }
                context = new String(oldResult.getValue(),"GBK");
                //logger.info("context:"+context);
            }
        }
        if(StringUtil.isNotBlank(context)){
            FileStorageService2WH fss=RSFService.getFilestorageService2WH();
            Map<String,Integer> imgWhMap=BusinCompleCaluteNewUtils.doIntroduceImgInterval(context,fss);
            int wordAmount=BusinCompleCaluteNewUtils.obtainIntroduceWordAmount(context);
            BusinIntroduceCsinfo sbiCsinfo= BusinIntorduceCsinfoUtils.convert2BusinIntroduceCsinfo(imgWhMap, wordAmount, bcid);
            StatDao statDao = StatDao.getInstance(false);
            BusinIntroduceCsinfo dbiCsinfo=statDao.getBusinIntroduceCsinfo(bcid,threadno);
            if(sbiCsinfo!=null){
                //保存商机详情
                /*BusinIntroduce bi=statDao.getBusinIntroduce(bcid);
                if(bi!=null){
                    bi.setProviderid(dbiCsinfo.getProviderid());
                    bi.setIntroduce(Hibernate.createClob(context));
                    bi.setModifydate(new Timestamp(new Date().getTime()));
                    statDao.updateBusinIntroduce(bi);
                }else{
                    bi=new BusinIntroduce();
                    bi.setBcid(bcid);
                    bi.setProviderid(dbiCsinfo.getProviderid());
                    bi.setIntroduce(Hibernate.createClob(context));
                    bi.setModifydate(new Timestamp(new Date().getTime()));
                    statDao.saveBusinIntroduce(bi);
                }*/
                //保存商机详情信息（宽高）
//                sbiCsinfo.setEncode(encode);
                BusinIntorduceCsinfoUtils.convertS2D(sbiCsinfo,dbiCsinfo);
//                logger.error("bcid=" + bcid + ",wordAmount=" + dbiCsinfo.getWordAmount()+",encode=" + dbiCsinfo.getEncode());
                //添加进列表，做批量处理
                ciList.add(dbiCsinfo);
                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
            }
        }else{
            StatDao statDao = StatDao.getInstance(false);
            BusinIntroduceCsinfo dbiCsinfo=statDao.getBusinIntroduceCsinfo(bcid,threadno);
            if(dbiCsinfo!=null){
                //商机详情信息为空，状态设置为9
                dbiCsinfo.setStates("9");
                //添加进列表，做批量处理
                ciList.add(dbiCsinfo);
                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
            }
            logger.error("bcid:"+bcid+",商机详情为空");
        }
    }

    /**
     * 算分
     * @param bcid
     * @param scoreIdentity
     * @return
     * @throws MmtException
     */
    public static BusinScore caluteScoreNew(String bcid,String scoreIdentity) throws MmtException{

        BusinSourceInfo bsi=new BusinSourceInfo();
        bsi.setBcid(bcid);
        BusinScoreData bsd=new BusinScoreData();
        BusinScore businScore=new BusinScore();
		com.hc360.score.utils.BusinChance businChance = new com.hc360.score.utils.BusinChance();
        //计算类目
        if('1'==scoreIdentity.charAt(0)){
            CalculateTemplate ctSupcat=new CalculateFactory(AppContent.CALCULATE_SUPCAT).getInstance();
			businChance = ctSupcat.calculateInitParam(Long.valueOf(bcid),bsi,bsd, businChance);
        }
        //计算参数
        if('1'==scoreIdentity.charAt(1)){
            CalculateTemplate ctAtt=new CalculateFactory(AppContent.CALCULATE_ATT).getInstance();
			businChance = ctAtt.calculateInitParam(Long.valueOf(bcid),bsi,bsd, businChance);
        }
        //计算标题
        if('1'==scoreIdentity.charAt(2)){
            CalculateTemplate ctTitle=new CalculateFactory(AppContent.CALCULATE_TITLE).getInstance();
			businChance = ctTitle.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //计算价格
        if('1'==scoreIdentity.charAt(3)){
            CalculateTemplate ctPrice=new CalculateFactory(AppContent.CALCULATE_PRICE).getInstance();
			businChance = ctPrice.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //计算图片
        if('1'==scoreIdentity.charAt(4)){
            CalculateTemplate ctMultimedia=new CalculateFactory(AppContent.CALCULATE_MULTIMEDIA).getInstance();
            businChance = ctMultimedia.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //计算详情
        if('1'==scoreIdentity.charAt(5)){
            CalculateTemplate ctIntroduce=new CalculateFactory(AppContent.CALCULATE_INTRODUCE).getInstance();
			bsi.setBcid(bcid);
			businChance = ctIntroduce.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }

        /**************************计算星级*************************/

		businScore = getBusinScoreByDrools(businChance, businScore, bsi);
        return businScore;
    }

	/**
	 * 调用算分服务，获取 商机各项分数和星级
	 * @param businChanceVo
	 * @param businScore
	 * @return
	 */
    private static BusinScore getBusinScoreByDrools(com.hc360.score.utils.BusinChance businChanceVo, BusinScore businScore, BusinSourceInfo bsi){
		BaseResult<com.hc360.score.utils.BusinChance> result = new BaseResult<com.hc360.score.utils.BusinChance>();
    	StringBuilder url = new StringBuilder();

    	url.append("http://api.hc360.com/product/s1/businscore?");
		url.append("hasSupCat="+businChanceVo.getHasSupCat());
		url.append("&paramAmount="+businChanceVo.getParamAmount()+"&repetitorAmount="+businChanceVo.getRepetitorAmount());
		url.append("&hasOtherTitleDetail="+businChanceVo.getHasOtherTitleDetail());
		url.append("&priceTypeNew="+businChanceVo.getPriceTypeNew());
		url.append("&imageAmount="+businChanceVo.getImageAmount()+"&firstInmageSize="+businChanceVo.getFirstInmageSize());
		url.append("&detailWordAmountType="+businChanceVo.getDetailWordAmountType()+"&thanSevenHundred="+businChanceVo.getThanSevenHundred());
		url.append("&fiveBetweenSeven="+businChanceVo.getFiveBetweenSeven() + "&threeBetweenFive="+businChanceVo.getThreeBetweenFive()+"&lessThreeHundred="+businChanceVo.getLessThreeHundred());
		try {
			String scoreResult = HttpClientUtils.doGet(url.toString(), "UTF-8");
			result = JSON.parseObject(scoreResult, BaseResult.class);

			if(result.getErrcode() == 0 && null != result.getData()){
				com.hc360.score.utils.BusinChance businChance = JSON.parseObject(String.valueOf(result.getData()),com.hc360.score.utils.BusinChance.class);
				businScore.setHasSupcate(businChance.getHasSupCatScore());	//类目得分
				businScore.setHasnoparam(businChance.getHasnoParam());		//参数得分
				businScore.setHaslongtitle(businChance.getHaslongtitle());	//标题得分
				businScore.setHasprice(businChance.getHasPrice());			//报价得分
				businScore.setPhotocount(businChance.getPhotoCount());		//图片数量得分
				businScore.setFirstphoto(businChance.getFirstPhoto());		//第一张图片得分
				businScore.setHaslongintroduce(businChance.getDetailImages());		//详细内容图片得分
				businScore.setDetailWordAmount(businChance.getDetailWordAmount());	//详细内容字数得分

				businScore.setStar(businChance.getStart());
				businScore.setScore(businChance.getScore());
				businScore.setUserid(bsi.getUserid());
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("调用算分服务异常, userId : " + bsi.getUserid() ,e);
		}
		return businScore;
	}

	public static void main(String[] args) {
    	try {
			com.hc360.score.utils.BusinChance businChanceVo = new com.hc360.score.utils.BusinChance();
			businChanceVo.setHasSupCat(1);
			businChanceVo.setParamAmount(6);
			businChanceVo.setRepetitorAmount(2);
			businChanceVo.setHasOtherTitleDetail(1);
			businChanceVo.setPriceTypeNew(2);
			businChanceVo.setImageAmount(6);
			businChanceVo.setFirstInmageSize(1);
			businChanceVo.setDetailWordAmountType(2);
			businChanceVo.setThanSevenHundred(5);
			BusinScore businScore = new BusinScore();

			BusinSourceInfo bsi = new BusinSourceInfo();
			bsi.setUserid(23);

			BusinScore businScores = getBusinScoreByDrools(businChanceVo, businScore, bsi);
			System.out.println(businScores);
		}catch (Exception e){
    		e.printStackTrace();
		}
	}
}
