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
 * �ƽ�����manage
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
	 * �����̻�
	 * @param tableInfo
	 * @param traninfo
	 * @param businScore
	 * @param bcid
	 * @return
	 */
	public BusinScore saveBusinInfo(TableInfo tableInfo,Transinformation traninfo,BusinScore businScore,String bcid,int status,String source) throws Exception{
		BusinInfo busininfo = traninfo.getBusininfo();
        /**
         * �������,�����0�֣������ݿ��,�������
         */
		if(businScore==null || businScore.getScore()==0.0 ){
            traninfo.record("���㵥���̻�����");
			businScore = initSingleBusinScore(bcid,"111111");
		}
        //���浽hbase��
		//addHBaseBusinInfo(tableInfo,busininfo, businScore,status);

        //��д��
        traninfo.record("��д�����ݿ��̻�����");
		reSetDBBusinScore(Long.parseLong(bcid),busininfo,businScore,source);

		return businScore;
	}


    public BusinScore saveBusinInfo(TableInfo tableInfo,Transinformation traninfo,BusinScore businScore,String bcid,int status) throws Exception{
        BusinInfo busininfo = traninfo.getBusininfo();
        /**
		 * �������,�����0�֣������ݿ��,�������
         */
        String scoreIdentityMessage=null;
        if(businScore==null || businScore.getScore()==0.0 ){
			traninfo.record("���㵥���̻�����");
            scoreIdentityMessage=busininfo.getScoreIdentity();
            if(StringUtil.isBlank(busininfo.getScoreIdentity())){
                busininfo.setScoreIdentity("111111");
            }
            //TODO Ϊ�˴�����ʷ���������ó�111111��ÿ����
            busininfo.setScoreIdentity("111111");
            businScore = initSingleBusinScore(bcid,busininfo.getScoreIdentity());
        }
		//���浽hbase��
        addHBaseBusinInfo(tableInfo,busininfo, businScore,status);

		//��д��
		traninfo.record("��д�����ݿ��̻�����");
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
			//��������ʱ��
			hbaseMap.put("time", DateUtils.getString(busininfo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));
		}
		//������Чʱ��
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
	 * ��ʼ�������̻�
	 * @param bcid
	 * @return  �����쳣�󣬷��ؿ�ָ��
	 * @throws MmtException
	 */
	public BusinScore initSingleBusinScore(String bcid,String scoreIdentity) throws Exception {

        /*FileStorageService fss=RSFService.getFilestorageService();
		BusinSourceInfo myBusinSourceInfo = getBusinInfoForCc(Long.parseLong(bcid));
		BusinScore  businScore = BusinCompleCaluteNewUtils.caluteScore(myBusinSourceInfo,fss);*/

        //modify by whc 2016/07/20 ������ֹ���3.0
        BusinScore  businScore =caluteScoreNew(bcid,scoreIdentity);

		//����û�userId��Ϊ�գ��ͱ�������ݿ��л�ȡһ��ֵ
		if(businScore.getUserid()==0){
			long userid =  getUserFromBusin(Long.parseLong(bcid));
			businScore.setUserid(userid);
		}
		//����bc_id����Userid
		return businScore;
	}
	/**
	 * ��д�ƽ������̻�����
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
        //��д�ƽ������̻�����
		//reSetCompass(bcid, businScore);

        //��д�����̻�����
		addBusinQualityStar(bcid, businScore,source);

         //��дonbusinchance �� businchance
		 //reSetBusinChance(bcid, businScore);

		return result;
	}

    public boolean reSetDBBusinScore(long bcid,String scoreIdentityMessage,BusinInfo busininfo,BusinScore businScore)throws MmtException{
        if(busininfo==null){
            return false;
        }
        boolean result = true;
		//��д�ƽ������̻�����
        reSetCompass(bcid, businScore);

		//��д�����̻�����
        addBusinQualityStarNew(bcid, businScore);

        //��дonbusinchance �� businchance
        //reSetBusinChance(bcid, businScore);
        //modify by whc 2016/06/23
        reSetBusinChance(bcid,busininfo, businScore);

        //���������޸�
        //annotation by whc 2017/03/06
        /*if(CommContent.BUSIN_OPER_NEWPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_NEWPENDED==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDED==busininfo.getOper()){
            //д������
            if(StringUtil.isBlank(scoreIdentityMessage)||(StringUtil.isNotBlank(scoreIdentityMessage)&&'1'==scoreIdentityMessage.charAt(5))){
                addBusinIntroduceWHInfo(bcid,businScore);
            }
            //д�̻�����
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
                businChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by whc 2016/06/23 �̻��޸�ʱ��
            }
            daoprod.saveBusinChance(businChance);
        }

        OnBusinChance onBusinChance = daoprod.getOnBusinChance(bcid);
        if(onBusinChance !=null){
            onBusinChance.setStar(String.valueOf(businScore.getStar()));
            if(busininfo!=null&& (CommContent.BUSIN_OPER_UPDATEPENDING==busininfo.getOper()||CommContent.BUSIN_OPER_UPDATEPENDED==busininfo.getOper())){
                onBusinChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by whc 2016/06/23 �̻��޸�ʱ��
            }
            daoprod.saveOnBusinChance(onBusinChance);
        }
    }

    private void reSetBusinChance(long bcid, BusinScore businScore) throws MmtException{
		ProdDao daoprod = ProdDao.getInstance(false);

		BusinChance businChance = daoprod.getBusinChance(bcid);
		if(businChance !=null){
			businChance.setStar(String.valueOf(businScore.getStar()));
            businChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by wanghuacun 2016/06/14 �̻��޸�ʱ��
            daoprod.saveBusinChance(businChance);
        }

        OnBusinChance onBusinChance = daoprod.getOnBusinChance(bcid);
        if(onBusinChance !=null){
            onBusinChance.setStar(String.valueOf(businScore.getStar()));
            onBusinChance.setUpdatetime(new Timestamp(new Date().getTime()));//add by wanghuacun 2016/06/14 �̻��޸�ʱ��
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
        // ���۷�ʽ�����
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //��Ӧ�����ķ�
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
		 * ������շѻ�Ա�ƽ����̱��в�����ݣ���д������дBUSIN_QUALITY_STAR
		 */
		if(businComplete!=null){
			//��д
			businComplete.setStar(businScore.getStar());
			businComplete.setScore(BigDecimal.valueOf(businScore.getScore()));
			/**
			 * ϸ����
			 */
			//�ϴ���ƷͼƬ
			businComplete.setHasphoto(BigDecimal.valueOf(businScore.getHasphoto()));
			//�Ż���ƷͼƬ
			businComplete.setFirstphoto(BigDecimal.valueOf(businScore.getFirstphoto()));
			//ѡ��ֱ�ӱ���
			businComplete.setHasprice(BigDecimal.valueOf(businScore.getHasprice()));
			//��д��С����
			businComplete.setHasminordernum(BigDecimal.valueOf(businScore.getHasMinNum() ));
			//������Ϣ����
			businComplete.setHaslongtitle(BigDecimal.valueOf(businScore.getHaslongtitle()));
			//������ϸ˵��
			businComplete.setHaslongintroduce(BigDecimal.valueOf(businScore.getHaslongintroduce()));
			//��д��������
			businComplete.setHastype(BigDecimal.valueOf(businScore.getHasnum()));
			//����ϸ��ͼƬ
			businComplete.setPhotocount(BigDecimal.valueOf(businScore.getPhotocount()));
			//���Ʋ�Ʒ����
			businComplete.setHasparam(BigDecimal.valueOf(businScore.getHasnoparam()));
			daoprod.resetCompassBusinComplete(businComplete);
			/**
			 * ��д�ƽ������û��̻������ܷ�
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
        // ���۷�ʽ�����
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //��Ӧ�����ķ�
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
        // ���۷�ʽ�����
//		businQualityStar.setHasbrand(BigDecimal.valueOf(businScore.getHasprice() ));
        //��Ӧ�����ķ�
        businQualityStar.setHastype_new(chanceNum(businScore.getHasnum()));
        businQualityStar.setHasprice_new(chanceNum(businScore.getHasprice()));

        ProdDao.getInstance(false).resetFreeBusinComplete(businQualityStar);

    }

	public BigDecimal chanceNum(double d){
		BigDecimal s = BigDecimal.valueOf( d );
		return s;
	}

    /**
     * ���������
     * @param bcid
     * @param businScore
     * @throws MmtException
     */
    private void addBusinIntroduceWHInfo(long bcid, BusinScore businScore) throws MmtException{
        BusinIntroduceWHinfo sbiWHinfo= BusinIntorduceCsinfoUtils.convert2BusinIntroduceWHinfo(businScore);
        StatDao statDao = StatDao.getInstance(false);
        BusinIntroduceWHinfo dbiWHinfo=statDao.getBusinIntroduceWHinfo(bcid);
        if(dbiWHinfo!=null){
            //�����̻�������Ϣ����ߣ�
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
     * ����̻�����
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
	 * ���̻�����û�id
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
	 * businchance��onbusinchance��ת
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
	 * ����bcid��ȡʵʱ����Դ������Ϣ��������ֺ��Ǽ�����Ʒ����Ŀ��
	 * ��һ������ѯbusin����ȡ������Ϣ
	 * �ڶ�������ȡ��ϸ��Ϣ
	 * ����������ȡͼƬ��Ϣ
	 * ���Ĳ�����ȡ������Ϣ
	 * @author Gao xingkun
	 * @version 1.0
	 * @date 2014-5-9 ����10:02:19
	 * @param bcid
	 * @return
	 * @throws Exception
	 */
	public BusinSourceInfo getBusinInfoForCc(long bcid) throws Exception {
		//��ȡ�̻�������Ϣ(�Ȳ�ѯbusin�����û���ڲ�ѯon�� ��˵��̻���Ϊ�����и����͵Ĺ���)
		if(bcid == 0){
			return null;
		}

		/**ע�⣺��Ϊ�÷���ֻ�����ͨ�������󣩵��̻��Ż���ã����Բ��ÿ��ǲ�ѯ�̻�������
		*-----��һ������ѯbusin����ȡ������Ϣ**/

		String context = "";
		KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
		if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
			if (oldResult.getValue() != null) {
				context = new String(oldResult.getValue());
			}
		}

		boolean businFlag = false;//������Ϣ�Ƿ����
		BusinSourceInfo bsi = null;
		String supcatid = "";//Ʒ��id ��ȡ��ѯ���̻������в�����
		BusinChance busin = ProdDao.getInstance(false).getBusinChance(bcid);
		if(busin!=null){
			bsi = new BusinSourceInfo();
            //������Ŀ
            bsi.setSupcatid(busin.getSupcatid());
            //�������߽���
            bsi.setIssupporttrade(busin.getIssupporttrade());
            //���ùؼ��֡����⡢��С�����͹�Ӧ��������Ϣ
			bsi.setTitle(busin.getTitle());
			bsi.setKeyword(busin.getKeyword());
			bsi.setMinOrderNum(busin.getMinordernum());
			bsi.setNum((busin.getNum())!=null?Long.valueOf(busin.getNum()):0l);
			bsi.setPriceType(busin.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
            //�۸�����
            int priceItemsCount=ProdDao.getInstance(false).getPriceItemsCount(bcid);
            bsi.setPriceItemsCount(priceItemsCount);
            bsi.setIntroduce(context );
			supcatid = busin.getSupcatid();
			businFlag = true;
		}else{//BusinChance��û����OnBusinChance����ȥ��
			OnBusinChance obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
			if(obc!=null){
				bsi = new BusinSourceInfo();
                //������Ŀ
                bsi.setSupcatid(obc.getSupcatid());
                //�������߽���
                bsi.setIssupporttrade(obc.getIssupporttrade());
				bsi.setTitle(obc.getTitle());
				bsi.setKeyword(obc.getKeyword());
				/******************ע�⣺��С�����͹�Ӧ�����򱨼����Ͳ�ͬ��ȡ��ʽ��ͬOnbsinchance����������Ӧֵ
				 * �����ֻ�жϡ����ޡ����ɣ����Դ������ȡ�Ϳ���******************/
				bsi.setMinOrderNum(obc.getMinordernum());
				bsi.setNum(StringUtil.isNotBlank(obc.getNum())?Long.valueOf(obc.getNum()):0l);
//				bsi.setPriceType((obc.getIssupporttrade()!=null&&"1".equals(obc.getIssupporttrade()))?1:0);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
				bsi.setPriceType(obc.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
                //�۸�����
                int priceItemsCount=ProdDao.getInstance(false).getPriceItemsCount(bcid);
                bsi.setPriceItemsCount(priceItemsCount);
                supcatid = obc.getSupcatid();
				bsi.setIntroduce(context );
				businFlag = true;
			}
		}
		/*******************************��ѯ�̻�������Ϣ����*********************************************/
		if(businFlag){
			/*** �ڶ�������ȡ��ϸ��Ϣ */
			bsi.setIntroduce(context);
			/** * ����������ȡͼƬ��Ϣ */
			//ͼƬ��ͨ��bc_id����ͼƬ��Ϣ�������Ƿ���ͼƬ��ͼƬ�����͵�һ��ͼƬ�Ŀ�ߵȣ�
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
				//����ͼƬ����
				bsi.setImgNum(size);
			}

			/*** ���Ĳ�����ȡ������Ϣ*/
			//ͨ��supcatid��ȡcatid  100004460
			if (supcatid != null && !"".equals(supcatid)) {
				String catid = CorpDao.getInstance(false).getCatidBySupcatid(supcatid);
				if (catid != null && !"".equals(catid)) {
					// ͨ��catid��ȡƷ����������(����������)
					List<PageRecordBean> cpList = ProdDao.getInstance(false).getNortCategoryParam1(catid);
					// ��ȡ����д�Ĳ����� a.pi_id,i.name,a.is_required
					List<BusinAttValue> baList = ProdDao.getInstance(false).getNorBusinAtt(bcid);
					int nortparamnum = 0;
					int norparamnum = 0;
					int userparamnum = 0;
					if (cpList != null && cpList.size() > 0) {
						for (PageRecordBean bean : cpList) {
							String isrequired = bean.getString("is_required");
							if (isrequired.equals("0")) {
								nortparamnum++;// �Ǳ����Ʒ�����
							}
						}
						if (baList != null && baList.size() > 0) {
							for (BusinAttValue bav : baList) {
								String attname = bav.getAttname();
                                //����Ʒ���ͺ�
                                if(StringUtil.isNotBlank(attname)&&"Ʒ��".equals(attname)){
                                    bsi.setBrand(bav.getAttvalue());
                                }
                                if(StringUtil.isNotBlank(attname)&&"�ͺ�".equals(attname)){
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
							norparamnum = baList.size() - norparamnum; // �����Ѿ���д��-������=���зǱ�����
							userparamnum = baList.size() - userparamnum; // �����Ѿ���д��-��Ʒ���������һ����=�û��Զ�����
						}
					} else {
						if (baList != null && baList.size() > 0) {
							userparamnum = baList.size();
						}
					}
					bsi.setNortParamNum(nortparamnum);
					bsi.setNorParamNum(norparamnum);
					bsi.setUsrParamNum(userparamnum);


                    //�������д�Ĳ������� �����Ƿ����������Ĵ� ����д�Ĳ����ظ���
                    List<BusinAttValue> cbaList=ProdDao.getInstance(false).getCommonBusinAtt(bcid);
                    if (cbaList != null && cbaList.size() > 0) {
                        /*String paramVal="";
                        for(BusinAttValue cbav : cbaList){
                                paramVal+=cbav.getAttvalue()+",";
                        }*/
                        bsi.setParamAmount(cbaList.size());
                        //ȥ�� �������Ĵ��ж� 2016/02/020
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
     * ��ʼ�������̻�����
     * @param busin   �����̻���Ϣ
     * @param traninfo
     * @throws Exception
     */
    public BusinScore initBusinScore(Transinformation traninfo, OnBusinChance busin) throws Exception{

        int status = -1;
        //�ж��̻�״̬,��Ч��
        if(busin==null){
            return null;
        }
        if("1".equals(busin.getStates())){
            //��Ч
            status = AppContent.BUSINESS_STATUS_DEL;
        }
        //���ͨ��
        else if("1".equals(busin.getChecked())){
            //�Ƿ����,û����
            if(DateUtils.comDate(DateUtils.parseDate(busin.getEnddate(), "yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(DateUtils.getSysTimestamp(), "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss")){
                status = AppContent.BUSINESS_STATUS_PENDED;
            }else{
                //û����Ч�ڣ������
                status = AppContent.BUSINESS_STATUS_OVER;
            }
        }
        //�������
        else if("1".equals(busin.getUnchecked())){
            status = AppContent.BUSINESS_STATUS_REFUSE;
        }
        //����
        else{
            status = AppContent.BUSINESS_STATUS_NEW;
        }
        /*** ��hbase */
        TableInfo tableInfo = new TableInfo();
//		tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
        tableInfo.setTableName("hrtc_irsl_busininfolog");
        tableInfo.setRowKey(String.valueOf(busin.getId()));
        tableInfo.setFamilyName("info");

//		BusinSourceInfo myBusinSourceInfo;
//		BusinScore mySingleBusinScore ;
//		/** * �����̻�������Ϣ*/
//		myBusinSourceInfo =	getBusinInfoForCc(Long.parseLong(""+busin.getId()));
//		mySingleBusinScore = BusinCompleCaluteUtils.caluteScore(myBusinSourceInfo);
//		//����û��userid
//		BusinScore businScore = new BusinScore();
//		BusinInfo businInfo = new BusinInfo();
//		businInfo.setUserid(userid);
        /*** ��� */
        BusinScore mySingleBusinScore = saveBusinInfo(tableInfo, traninfo, null,String.valueOf(busin.getId()),status);
        /**
         * ����̻���Ч��׷���û��ܷ�
         */
        return mySingleBusinScore;
    }

	/**
	 * ��ʼ�������̻�����
	 * @param busin   �����̻���Ϣ
	 * @param source
	 * @throws Exception
	 */
	public BusinScore initBusinScore(Transinformation traninfo, OnBusinChance busin,String source) throws Exception{

		int status = -1;
		//�ж��̻�״̬,��Ч��
		if(busin==null){
			return null;
		}
		if("1".equals(busin.getStates())){
			//��Ч
			status = AppContent.BUSINESS_STATUS_DEL;
		}
		//���ͨ��
		else if("1".equals(busin.getChecked())){
			//�Ƿ����,û����
			if(DateUtils.comDate(DateUtils.parseDate(busin.getEnddate(), "yyyy-MM-dd HH:mm:ss"),DateUtils.parseDate(DateUtils.getSysTimestamp(), "yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd HH:mm:ss")){
				status = AppContent.BUSINESS_STATUS_PENDED;
			}else{
				//û����Ч�ڣ������
				status = AppContent.BUSINESS_STATUS_OVER;
			}
		}
		//�������
		else if("1".equals(busin.getUnchecked())){
			status = AppContent.BUSINESS_STATUS_REFUSE;
		}
		//����
		else{
			status = AppContent.BUSINESS_STATUS_NEW;
		}
		/*** ��hbase */
		TableInfo tableInfo = new TableInfo();
//		tableInfo.setTableName(AppContent.HRTC_IRSL_BUSININFOLOG);
		tableInfo.setTableName("hrtc_irsl_busininfolog");
		tableInfo.setRowKey(String.valueOf(busin.getId()));
		tableInfo.setFamilyName("info");

//		BusinSourceInfo myBusinSourceInfo;
//		BusinScore mySingleBusinScore ;
//		/** * �����̻�������Ϣ*/
//		myBusinSourceInfo =	getBusinInfoForCc(Long.parseLong(""+busin.getId()));
//		mySingleBusinScore = BusinCompleCaluteUtils.caluteScore(myBusinSourceInfo);
//		//����û��userid
//		BusinScore businScore = new BusinScore();
//		BusinInfo businInfo = new BusinInfo();
//		businInfo.setUserid(userid);
		/*** ��� */
		BusinScore mySingleBusinScore = saveBusinInfo(tableInfo, traninfo, null,String.valueOf(busin.getId()),status,source);
		/**
		 * ����̻���Ч��׷���û��ܷ�
		 */
		return mySingleBusinScore;
	}


    /**
     * ��ȡ����ͼƬ��Ϣ
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
     * �����̻������Ϣ
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
                //�����̻�����
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
                //�����̻�������Ϣ����ߣ�
//                sbiCsinfo.setEncode(encode);
                BusinIntorduceCsinfoUtils.convertS2D(sbiCsinfo,dbiCsinfo);
//                logger.error("bcid=" + bcid + ",wordAmount=" + dbiCsinfo.getWordAmount()+",encode=" + dbiCsinfo.getEncode());
                //��ӽ��б�����������
                ciList.add(dbiCsinfo);
                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
            }
        }else{
            StatDao statDao = StatDao.getInstance(false);
            BusinIntroduceCsinfo dbiCsinfo=statDao.getBusinIntroduceCsinfo(bcid,threadno);
            if(dbiCsinfo!=null){
                //�̻�������ϢΪ�գ�״̬����Ϊ9
                dbiCsinfo.setStates("9");
                //��ӽ��б�����������
                ciList.add(dbiCsinfo);
                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
            }
            logger.error("bcid:"+bcid+",�̻�����Ϊ��");
        }
    }

    /**
     * ���
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
        //������Ŀ
        if('1'==scoreIdentity.charAt(0)){
            CalculateTemplate ctSupcat=new CalculateFactory(AppContent.CALCULATE_SUPCAT).getInstance();
			businChance = ctSupcat.calculateInitParam(Long.valueOf(bcid),bsi,bsd, businChance);
        }
        //�������
        if('1'==scoreIdentity.charAt(1)){
            CalculateTemplate ctAtt=new CalculateFactory(AppContent.CALCULATE_ATT).getInstance();
			businChance = ctAtt.calculateInitParam(Long.valueOf(bcid),bsi,bsd, businChance);
        }
        //�������
        if('1'==scoreIdentity.charAt(2)){
            CalculateTemplate ctTitle=new CalculateFactory(AppContent.CALCULATE_TITLE).getInstance();
			businChance = ctTitle.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //����۸�
        if('1'==scoreIdentity.charAt(3)){
            CalculateTemplate ctPrice=new CalculateFactory(AppContent.CALCULATE_PRICE).getInstance();
			businChance = ctPrice.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //����ͼƬ
        if('1'==scoreIdentity.charAt(4)){
            CalculateTemplate ctMultimedia=new CalculateFactory(AppContent.CALCULATE_MULTIMEDIA).getInstance();
            businChance = ctMultimedia.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }
        //��������
        if('1'==scoreIdentity.charAt(5)){
            CalculateTemplate ctIntroduce=new CalculateFactory(AppContent.CALCULATE_INTRODUCE).getInstance();
			bsi.setBcid(bcid);
			businChance = ctIntroduce.calculateInitParam(Long.valueOf(bcid),bsi,bsd,businChance);
        }

        /**************************�����Ǽ�*************************/

		businScore = getBusinScoreByDrools(businChance, businScore, bsi);
        return businScore;
    }

	/**
	 * ������ַ��񣬻�ȡ �̻�����������Ǽ�
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
				businScore.setHasSupcate(businChance.getHasSupCatScore());	//��Ŀ�÷�
				businScore.setHasnoparam(businChance.getHasnoParam());		//�����÷�
				businScore.setHaslongtitle(businChance.getHaslongtitle());	//����÷�
				businScore.setHasprice(businChance.getHasPrice());			//���۵÷�
				businScore.setPhotocount(businChance.getPhotoCount());		//ͼƬ�����÷�
				businScore.setFirstphoto(businChance.getFirstPhoto());		//��һ��ͼƬ�÷�
				businScore.setHaslongintroduce(businChance.getDetailImages());		//��ϸ����ͼƬ�÷�
				businScore.setDetailWordAmount(businChance.getDetailWordAmount());	//��ϸ���������÷�

				businScore.setStar(businChance.getStart());
				businScore.setScore(businChance.getScore());
				businScore.setUserid(bsi.getUserid());
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("������ַ����쳣, userId : " + bsi.getUserid() ,e);
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
