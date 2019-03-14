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
 * 计算历史
 */
public class CalculateHistory {

    /*Bcid|
    类目|
    参数名1:参数值1;参数名2:参数值2|
    标题|
    关键词|
    是否支持在线交易|
    报价方式|
    价格1_价格2|
    宽1_高1;宽2_高2|
    描述文字字数|
    宽1_高1;宽2_高2*/


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
        //解析数据
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

        //处理数据 封装到BusinInfo
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
     * 处理类目
     * @param isHasSupcat
     * @param bi
     */
    private void dealSupcate(String isHasSupcat,BusinInfo bi) throws MmtException{
        //处理类目
        //bi.setHasSupcat(BusinCompleCaluteNewUtils.doSupcatSource(supercatid));
        if("1".equals(isHasSupcat)){
            bi.setHasSupcat(true);
        }else{
            bi.setHasSupcat(false);
        }
    }

    /**
     * 处理参数
     * @param paramAmount
     * @param paramRepetition
     * @param bi
     * @throws MmtException
     */
    private void dealBusinAtt(String paramAmount,String paramRepetition,BusinInfo bi) throws MmtException{
        //处理参数
        bi.setParamAmount(Integer.valueOf(paramAmount));
        bi.setParamRepetition(BusinCompleCaluteNewUtils.doParamRepetitionSource(Double.valueOf(paramRepetition)));
    }

    /**
     * 处理参数
     * @param param
     * @param bi
     */
    /*private String[] dealBusinAtt(String param,BusinInfo bi) throws MmtException{
        //处理参数
        String[] paramNVs=param.split(";");
        List<BusinAttValue> bavList=null;
        String brand="";
        String model="";
        if(paramNVs!=null&&paramNVs.length>0){
            bavList=new ArrayList<BusinAttValue>();
            for(String paramNV:paramNVs){
                String[] oneparam=paramNV.split(":");
                BusinAttValue bav=new BusinAttValue();
                //设置品牌型号
                if(StringUtil.isNotBlank(oneparam[0])&&"品牌".equals(oneparam[0])){
                    brand=oneparam[1];
                }
                if(StringUtil.isNotBlank(oneparam[0])&&"型号".equals(oneparam[0])){
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
     * 处理标题
     * @param isContainKeyword
     * @param isContainBrand
     * @param isContainModel
     * @param bi
     */
    private void dealTitle(String isContainKeyword,String isContainBrand,String isContainModel,BusinInfo bi){
        //处理标题
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
     * 处理价格
     * @param priceItemsCount
     * @param issupporttrade
     * @param priceType
     * @param bi
     */
    private void dealPrice(String priceItemsCount,String issupporttrade,String priceType,BusinInfo bi){
        //处理价格
        bi.setPriceTypeNew(BusinCompleCaluteNewUtils.doPriceType(issupporttrade, Integer.valueOf(priceType), Integer.valueOf(priceItemsCount)));
    }

    /**
     * 处理图片
     * @param imageAmount
     * @param isGreate300
     * @param bi
     */
    private void dealPic(String imageAmount,String isGreate300,BusinInfo bi){
        //处理图片
        if("1".equals(isGreate300)){
            bi.setFirstImageType(1);
        }
        bi.setImageAmount(Integer.valueOf(imageAmount));
    }

    /**
     * 处理详情
     * @param detailAmount
     * @param detailPic
     * @param bi
     */
    private void dealDetail(String detailAmount,String detailPic,BusinInfo bi){
        //处理详情
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
            //获取详情图片算分类型
            detailImageType=BusinCompleCaluteNewUtils.obtainIntroduceImgNum(imgWidthMap);
        }
        bi.setDeatilImageType(detailImageType);
    }

    public BusinScore calculateScore(BusinInfo businInfo){
        //算分处理
        BusinScore businScore = new BusinScore();

        /************************计算类目****************************/
        BusinCompleCaluteNewUtils.caluteSupcat(businInfo, businScore);
        /************************计算基本参数*********************/
        BusinCompleCaluteNewUtils.caluteBaseParam(businInfo, businScore);
        /************************计算标题****************************/
        BusinCompleCaluteNewUtils.caluteTitle(businInfo, businScore);
        /************************计算报价方式***********************/
        BusinCompleCaluteNewUtils.calutePriceType(businInfo, businScore);
        /************************计算图片****************************/
        BusinCompleCaluteNewUtils.caluteImage(businInfo, businScore);
        /************************计算详细信息***********************/
        //BusinCompleCaluteNewUtils.caluteDetail(businInfo, businScore);

        return businScore;
    }

    /**
     * 历史数据算分程序
     * @param baseData
     * @return
     * @throws MmtException
     */
    public BusinScore calculateProcess(List<String> baseData)throws MmtException{
        //处理数据
        BusinInfo bi=dealData(baseData);
        //计算
        BusinScore bs=calculateScore(bi);
        return bs;
    }

}
