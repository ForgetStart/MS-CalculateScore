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
 * ����ͼƬ
 */
public class CalculateMultimedia extends CalculateTemplate {

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //���supcatidûֵ�ӿ����ȡ
        if(bsi==null||bsi.getImgNum()<=0){
            //ͼƬ��ͨ��bc_id����ͼƬ��Ϣ�������Ƿ���ͼƬ��ͼƬ�����͵�һ��ͼƬ�Ŀ�ߵȣ�
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
                //����ͼƬ����
                bsi.setImgNum(size);
            }
        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{
        BusinInfo bi=new BusinInfo();
        /******************ͼƬ��Ϣ*******************/
        bi.setHasImage(businSourceInfo.getImgNum() > 0 ? true : false);
        bi.setImageAmount(businSourceInfo.getImgNum());

        /******************************�����һ��ͼƬ���ߴ���Ϊ����20160516�����޸ģ�***********************************/
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
