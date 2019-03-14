package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.bcs.utils.StringUtil;
import com.hc360.rsf.imgup.FileStorageService;
import com.hc360.rsf.kvdb.service.KVDBResult;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.rsf.RSFService;
import com.hc360.score.utils.BusinChance;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by whc on 2016/7/8.
 * 计算类目
 */
public class CalculateIntroduce extends CalculateTemplate {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //如果supcatid没值从库里获取
        try{
            if(bsi==null||StringUtil.isBlank(bsi.getIntroduce())){
                String context = bsd.getIntroduce();
                if(StringUtil.isBlank(context)){
                    KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
                    if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
                        if (oldResult.getValue() != null) {
                            context = new String(oldResult.getValue());
                            bsd.setIntroduce(context);
                        }
                    }
                }
                bsi.setIntroduce(context);
            }
        }catch(Exception e){
            throw new MmtException("bcid:"+bcid+",获取详情异常，error:"+e.getMessage());
        }

    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{

        logger.info("bcid:"+businSourceInfo.getBcid()+"处理详情begin");
        long start=System.currentTimeMillis();
        BusinInfo bi=new BusinInfo();
        FileStorageService fss=RSFService.getFilestorageService();
        int amount= BusinCompleCaluteNewUtils.obtainIntroduceWordAmount(businSourceInfo.getIntroduce());
        bi.setDetailWordAmount(amount);//详情字数
        int amountType=BusinCompleCaluteNewUtils.doIntroduceSource(amount);
        bi.setDetailWordAmountType(amountType);//详情字数类型
        List<float[]> imgWHList=BusinCompleCaluteNewUtils.doIntroduceImgWH(businSourceInfo.getIntroduce(), fss);
        if(imgWHList!=null&&imgWHList.size()>10){
            imgWHList=imgWHList.subList(0,10);//取前十张图片
        }
        Map<String,Integer> imgWHMap=BusinCompleCaluteNewUtils.obtainIntroduceImgWHInterval(imgWHList);
        bi.setDeatilImageWHs(imgWHMap);//详情图片宽高
        String imgType=BusinCompleCaluteNewUtils.doIntroduceImg(imgWHList);
        bi.setDeatilImageType(imgType);//详情图片宽高类型

        logger.info("bcid:"+businSourceInfo.getBcid()+"处理详情end use time：" + (System.currentTimeMillis() - start));
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException{
        BusinScore bs=new BusinScore();
        bs.setDetailWordAmount(businInfo.getDetailWordAmount());
        bs.setDeatilImageWHs(businInfo.getDeatilImageWHs());
        BusinCompleCaluteNewUtils.caluteDetail(businInfo,bs);
        return bs;
    }

    @Override
    BusinChance transmitParam(BusinInfo businInfo, BusinChance businChance)throws MmtException{

        businChance.setDetailWordAmountType(businInfo.getDetailWordAmountType());
        String detailImageType = businInfo.getDeatilImageType();
        if(StringUtils.isNotBlank(detailImageType)){
            String types[] = detailImageType.split(",");

            try {
                businChance.setThanSevenHundred(Integer.valueOf(types[0]));
            }catch (Exception e){
                logger.error("转换详细信息图片分布 700图片异常", e);
            }

            try {
                businChance.setFiveBetweenSeven(Integer.valueOf(types[1]));
            }catch (Exception e){
                logger.error("转换详细信息图片分布 500-700图片异常", e);
            }

            try {
                businChance.setThreeBetweenFive(Integer.valueOf(types[2]));
            }catch (Exception e){
                logger.error("转换详细信息图片分布 300-500图片异常", e);
            }

            try {
                businChance.setLessThreeHundred(Integer.valueOf(types[3]));
            }catch (Exception e){
                logger.error("转换详细信息图片分布 小于300图片异常", e);
            }
        }
        return businChance;
    }

}
