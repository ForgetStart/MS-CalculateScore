package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.mmt.db.po.proddb.BusinMultimedia;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.utils.BusinChance;

import java.util.List;

/**
 * Created by whc on 2016/7/8.
 * 计算图片
 */
public class CalculateMultimedia extends CalculateTemplate {

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //如果supcatid没值从库里获取
        if(bsi==null||bsi.getImgNum()<=0){
            //图片。通过bc_id查找图片信息（设置是否有图片、图片数量和第一张图片的宽高等）
            List<BusinMultimedia> bmList=bsd.getBmList();
            if(bmList==null||bmList.size()<=0){
                bmList =ProdDao.getInstance(false).getBusinPicdetailList(bcid);
                bsd.setBmList(bmList);
            }
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
        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{
        BusinInfo bi=new BusinInfo();
        /******************图片信息*******************/
        bi.setHasImage(businSourceInfo.getImgNum() > 0 ? true : false);
        bi.setImageAmount(businSourceInfo.getImgNum());

        /******************************计算第一张图片最大尺寸最为算分项（20160516需求修改）***********************************/
        if(	businSourceInfo.getFirstImageWidth()>=300&&businSourceInfo.getFirstImageHeight()>=300){
            bi.setFirstImageType(1);
        }
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException{
        BusinScore bs=new BusinScore();
        BusinCompleCaluteNewUtils.caluteImage(businInfo,bs);
        return bs;
    }


    @Override
    BusinChance transmitParam(BusinInfo businInfo, BusinChance businChance)throws MmtException{

        businChance.setImageAmount(businInfo.getImageAmount());
        businChance.setFirstInmageSize(businInfo.getFirstImageType());
        return businChance;
    }

}
