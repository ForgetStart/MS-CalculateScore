package com.hc360.score.message.service;

import com.hc360.b2b.B2BConstants;
import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.netWorker.GetUrl;
import com.hc360.b2b.util.DateUtils;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteUtils;
import com.hc360.bcs.utils.DeadlineRectificationUtils;
import com.hc360.hbase.domain.TableInfo;
import com.hc360.hbase.utils.HBaseUtilHelper;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.proddb.*;
import com.hc360.rsf.kvdb.service.KVDBResult;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.CorpDao;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.db.rsf.RSFService;
import com.hc360.score.message.Transinformation;
import com.lowagie.text.Image;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * �ƽ�����manage
 * 
 * @author saiwengang
 *
 */
public class BusinessScoreManageModify {

	private static BusinessScoreManageModify instance = new BusinessScoreManageModify();
	public static BusinessScoreManageModify getInstance(){
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
	public BusinScore saveBusinInfo(TableInfo tableInfo,Transinformation traninfo,BusinScore businScore,String bcid,int status) throws Exception{
		BusinInfo busininfo = traninfo.getBusininfo();
		/**
		 * �������,�����0�֣������ݿ��,�������
		 */
		if(businScore==null || businScore.getScore()==0.0 ){
			traninfo.record("���㵥���̻�����");
			businScore = initSingleBusinScore(bcid);
		}
		//���浽hbase��
		addHBaseBusinInfo(tableInfo,busininfo, businScore,status);

		//��д��
		traninfo.record("��д�����ݿ��̻�����");
		reSetDBBusinScore(Long.parseLong(bcid),busininfo,businScore);

		return businScore;
	}
	private void addHBaseBusinInfo(TableInfo tableInfo,BusinInfo busininfo, BusinScore businScore,int status) throws MmtException{
		Map<String, String> hbaseMap = new HashMap<String, String>();
		if(businScore!=null){
			hbaseMap.put("score", String.valueOf(businScore.getScore()));

			hbaseMap.put("quality", businScore.getQuality());

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
	 * @throws com.hc360.b2b.exception.MmtException
	 */
	public BusinScore initSingleBusinScore(String bcid) throws Exception {

		BusinSourceInfo myBusinSourceInfo = getBusinInfoForCc(Long.parseLong(bcid));
		BusinScore  businScore = BusinCompleCaluteUtils.caluteScore(myBusinSourceInfo);

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
	 * @throws com.hc360.b2b.exception.MmtException
	 */
	public boolean reSetDBBusinScore(long bcid,BusinInfo busininfo,BusinScore businScore)throws MmtException{
		if(busininfo==null){
			return false;
		}
		boolean result = true;
		//��д�ƽ������̻�����
		reSetCompass(bcid, businScore);
	
		//��д�����̻�����
		addBusinQualityStar(bcid, businScore);
		
		 //��дonbusinchance �� businchance
		 reSetBusinChance(bcid, businScore);
		 
		return result;
	}
	private void reSetBusinChance(long bcid, BusinScore businScore) throws MmtException{
		ProdDao daoprod = ProdDao.getInstance(false);
		
		BusinChance businChance = daoprod.getBusinChance(bcid);
		if(businChance !=null){
			businChance.setStar(String.valueOf(businScore.getStar()));
			daoprod.saveBusinChance(businChance);
		}
		
		OnBusinChance onBusinChance = daoprod.getOnBusinChance(bcid);
		if(onBusinChance !=null){
			onBusinChance.setStar(String.valueOf(businScore.getStar()));
			daoprod.saveOnBusinChance(onBusinChance);
		}
	}
	private void addBusinQualityStar(long bcid, BusinScore businScore) throws MmtException{
		BusinQualityStar businQualityStar = new BusinQualityStar();
		businQualityStar.setBcid(bcid);
		businQualityStar.setScore( chanceNum(businScore.getScore()) );
		businQualityStar.setStar(  businScore.getStar());
		businQualityStar.setModifydate(DateUtils.getSysTimestamp());
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
		
		ProdDao.getInstance(false).resetFreeBusinComplete(businQualityStar);
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
	
	public BigDecimal chanceNum(double d){
		BigDecimal s = BigDecimal.valueOf( d );
		return s;
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
			//���ùؼ��֡����⡢��С�����͹�Ӧ��������Ϣ
			bsi.setTitle(busin.getTitle());
			bsi.setKeyword(busin.getKeyword());
			bsi.setMinOrderNum(busin.getMinordernum());
			bsi.setNum((busin.getNum())!=null?Long.valueOf(busin.getNum()):0l);
			bsi.setPriceType(busin.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
			bsi.setIntroduce(context );
			supcatid = busin.getSupcatid();
			businFlag = true;
		}else{//BusinChance��û����OnBusinChance����ȥ��
			OnBusinChance obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
			if(obc!=null){
				bsi = new BusinSourceInfo();
				bsi.setTitle(obc.getTitle());
				bsi.setKeyword(obc.getKeyword());
				/******************ע�⣺��С�����͹�Ӧ�����򱨼����Ͳ�ͬ��ȡ��ʽ��ͬOnbsinchance����������Ӧֵ
				 * �����ֻ�жϡ����ޡ����ɣ����Դ������ȡ�Ϳ���******************/
				bsi.setMinOrderNum(obc.getMinordernum());
				bsi.setNum((obc.getNum())!=null?Long.valueOf(obc.getNum()):0l);
//				bsi.setPriceType((obc.getIssupporttrade()!=null&&"1".equals(obc.getIssupporttrade()))?1:0);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
				bsi.setPriceType(obc.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
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
                        setImageHeightAndWidth(bsi,bmList.get(0));
						/*bsi.setFirstImageHeight(Integer.valueOf(Long.toString(bmList.get(0).getImageheight())));
						bsi.setFirstImageWidth(Integer.valueOf(Long.toString(bmList.get(0).getImagewidth())));*/
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
								nortparamnum++; // �Ǳ����Ʒ�����
							}
						}
						if (baList != null && baList.size() > 0) {
							for (BusinAttValue bav : baList) {
								String attname = bav.getAttname();
								for (PageRecordBean bean : cpList) {
									String name = bean.getString("name");
									if (attname.equals(name)) {
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
     * ����ͼƬ���
     * @param bsi
     * @param bm
     */
    private void setImageHeightAndWidth(BusinSourceInfo bsi, BusinMultimedia bm) {
        try{
            if(bm.getImageheight()==0||bm.getImagewidth()==0){
                String urlStr = GetUrl.getPicUrl(String.valueOf(B2BConstants.IMAGE_ALBUM_PIC), bm.getFilename());
                URL url=new URL(urlStr);
                HttpURLConnection connect=(HttpURLConnection)url.openConnection();
                connect.setConnectTimeout(500);//�������ӳ�ʱ:500ms
                connect.setReadTimeout(500);//���ö�ȡ��ʱ:500ms
                Image img = Image.getInstance(url);
                /*bsi.setFirstImageHeight(Integer.valueOf(String.valueOf(img.height())));
                bsi.setFirstImageWidth(Integer.valueOf(String.valueOf(img.width())));*/
                bsi.setFirstImageHeight((int)(img.height()));
                bsi.setFirstImageWidth((int)(img.width()));
            }else{
                bsi.setFirstImageHeight(Integer.valueOf(String.valueOf(bm.getImageheight())));
                bsi.setFirstImageWidth(Integer.valueOf(String.valueOf(bm.getImagewidth())));
            }
        }catch (Exception e){//����ȡ����ͼƬ��ߣ�������Ϊ0
            bsi.setFirstImageHeight(0);
            bsi.setFirstImageWidth(0);
        }
    }

    /**
	 * ��ʼ�������̻�����
	 * @param busin   �����̻���Ϣ
	 * @param userid  
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

}
