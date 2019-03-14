package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by whc on 2016/7/8.
 * ������ʷ
 */
public class CalculateHistory {

    /*Bcid|
    ��Ŀ|
    ������1:����ֵ1;������2:����ֵ2|
    ����|
    �ؼ���|
    �Ƿ�֧�����߽���|
    ���۷�ʽ|
    �۸�1_�۸�2|
    ��1_��1;��2_��2|
    ������������|
    ��1_��1;��2_��2*/


    //bcid|
    //providerid|
    //isHasSupcat|
    //isContainKeyword|
    //isContainBrand|
    //isContainModel/
    //isSupportTrade|
    //priceType|
    //paramAmount|
    //paramRepetition|
    //priceItemsCount|
    //imageAmount|
    //isGreate300

    public BusinInfo dealData(List<String> baseData) throws MmtException{
        //��������
        String bcid=baseData.get(0);
        String providerid=baseData.get(1);
        String isHasSupcat=baseData.get(2);
        String isContainKeyword=baseData.get(3);
        String isContainBrand=baseData.get(4);
        String isContainModel=baseData.get(5);
        String issupporttrade=baseData.get(6);
        String priceType=baseData.get(7);
        String paramAmount=baseData.get(8);
        String paramRepetition=baseData.get(9);
        String priceItemsCount=baseData.get(10);
        String imageAmount=baseData.get(11);
        String isGreate300=baseData.get(12);
        //String detailAmount=baseData.get(9);
        //String detailPic=baseData.get(10);

        //�������� ��װ��BusinInfo
        BusinInfo bi =new BusinInfo();
        bi.setBcid(bcid);
        dealSupcate(isHasSupcat, bi);
        dealBusinAtt(paramAmount,paramRepetition,bi);
        dealTitle(isContainKeyword,isContainBrand,isContainModel,bi);
        dealPrice(priceItemsCount,issupporttrade,priceType,bi);
        dealPic(imageAmount,isGreate300,bi);
        //dealDetail(detailAmount,detailPic,bi);

        return bi;
    }

    /**
     * ������Ŀ
     * @param isHasSupcat
     * @param bi
     */
    private void dealSupcate(String isHasSupcat,BusinInfo bi) throws MmtException{
        //������Ŀ
        //bi.setHasSupcat(BusinCompleCaluteNewUtils.doSupcatSource(supercatid));
        if("1".equals(isHasSupcat)){
            bi.setHasSupcat(true);
        }else{
            bi.setHasSupcat(false);
        }
    }

    /**
     * �������
     * @param paramAmount
     * @param paramRepetition
     * @param bi
     * @throws MmtException
     */
    private void dealBusinAtt(String paramAmount,String paramRepetition,BusinInfo bi) throws MmtException{
        //�������
        bi.setParamAmount(Integer.valueOf(paramAmount));
        bi.setParamRepetition(BusinCompleCaluteNewUtils.doParamRepetitionSource(Double.valueOf(paramRepetition)));
    }

    /**
     * �������
     * @param param
     * @param bi
     */
    /*private String[] dealBusinAtt(String param,BusinInfo bi) throws MmtException{
        //�������
        String[] paramNVs=param.split(";");
        List<BusinAttValue> bavList=null;
        String brand="";
        String model="";
        if(paramNVs!=null&&paramNVs.length>0){
            bavList=new ArrayList<BusinAttValue>();
            for(String paramNV:paramNVs){
                String[] oneparam=paramNV.split(":");
                BusinAttValue bav=new BusinAttValue();
                //����Ʒ���ͺ�
                if(StringUtil.isNotBlank(oneparam[0])&&"Ʒ��".equals(oneparam[0])){
                    brand=oneparam[1];
                }
                if(StringUtil.isNotBlank(oneparam[0])&&"�ͺ�".equals(oneparam[0])){
                    model=oneparam[1];
                }
                bav.setAttname(oneparam[0]);
                bav.setAttvalue(oneparam[1]);
                bavList.add(bav);
            }
        }

        if (bavList != null && bavList.size() > 0) {
            bi.setParamAmount(bavList.size());
            bi.setParamRepetition(BusinCompleCaluteNewUtils.doParamRepetitionSource(BusinCompleCaluteUtils.obtainParamRepetition(bavList)));
        }

        String[] brandAndModel=new String[2];
        brandAndModel[0]=brand;
        brandAndModel[1]=model;
        return brandAndModel;
    }*/

    /**
     * �������
     * @param isContainKeyword
     * @param isContainBrand
     * @param isContainModel
     * @param bi
     */
    private void dealTitle(String isContainKeyword,String isContainBrand,String isContainModel,BusinInfo bi){
        //�������
        //bi.setHasOtherTitleDetail(BusinCompleCaluteNewUtils.doTitleSourceNew(title,keyword,brand,model));
        int type = 0;
        if("1".equals(isContainKeyword)){
            type=1;
            if("1".equals(isContainBrand)||"1".equals(isContainModel)){
                type=2;
            }
            if("1".equals(isContainBrand)&&"1".equals(isContainModel)){
                type=3;
            }
        }
        bi.setHasOtherTitleDetail(type);
    }

    /**
     * ����۸�
     * @param priceItemsCount
     * @param issupporttrade
     * @param priceType
     * @param bi
     */
    private void dealPrice(String priceItemsCount,String issupporttrade,String priceType,BusinInfo bi){
        //����۸�
        bi.setPriceTypeNew(BusinCompleCaluteNewUtils.doPriceType(issupporttrade, Integer.valueOf(priceType), Integer.valueOf(priceItemsCount)));
    }

    /**
     * ����ͼƬ
     * @param imageAmount
     * @param isGreate300
     * @param bi
     */
    private void dealPic(String imageAmount,String isGreate300,BusinInfo bi){
        //����ͼƬ
        if("1".equals(isGreate300)){
            bi.setFirstImageType(1);
        }
        bi.setImageAmount(Integer.valueOf(imageAmount));
    }

    /**
     * ��������
     * @param detailAmount
     * @param detailPic
     * @param bi
     */
    private void dealDetail(String detailAmount,String detailPic,BusinInfo bi){
        //��������
        bi.setDetailImageAmount(Integer.valueOf(detailAmount));
        String[] detailPicwhs=detailPic.split(";");
        List<Integer> detailWidthList=null;
        if(detailPicwhs!=null||detailPicwhs.length>0){
            detailWidthList=new ArrayList<Integer>();
            for(String detailPicwh:detailPicwhs){
                String[] detailPicwharr=detailPicwh.split("_");
                detailWidthList.add(Integer.valueOf(detailPicwharr[0]));
            }
        }
        Map<String,Integer> imgWidthMap=BusinCompleCaluteNewUtils.obtainIntroduceImgWidthMap(detailWidthList);
        String detailImageType="0,0,0,0";
        if(imgWidthMap!=null&&imgWidthMap.size()>0){
            //��ȡ����ͼƬ�������
            detailImageType=BusinCompleCaluteNewUtils.obtainIntroduceImgNum(imgWidthMap);
        }
        bi.setDeatilImageType(detailImageType);
    }

    public BusinScore calculateScore(BusinInfo businInfo){
        //��ִ���
        BusinScore businScore = new BusinScore();

        /************************������Ŀ****************************/
        BusinCompleCaluteNewUtils.caluteSupcat(businInfo, businScore);
        /************************�����������*********************/
        BusinCompleCaluteNewUtils.caluteBaseParam(businInfo, businScore);
        /************************�������****************************/
        BusinCompleCaluteNewUtils.caluteTitle(businInfo, businScore);
        /************************���㱨�۷�ʽ***********************/
        BusinCompleCaluteNewUtils.calutePriceType(businInfo, businScore);
        /************************����ͼƬ****************************/
        BusinCompleCaluteNewUtils.caluteImage(businInfo, businScore);
        /************************������ϸ��Ϣ***********************/
        //BusinCompleCaluteNewUtils.caluteDetail(businInfo, businScore);

        return businScore;
    }

    /**
     * ��ʷ������ֳ���
     * @param baseData
     * @return
     * @throws MmtException
     */
    public BusinScore calculateProcess(List<String> baseData)throws MmtException{
        //��������
        BusinInfo bi=dealData(baseData);
        //����
        BusinScore bs=calculateScore(bi);
        return bs;
    }

}
