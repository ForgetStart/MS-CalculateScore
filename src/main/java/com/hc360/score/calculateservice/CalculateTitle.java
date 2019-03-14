package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.bcs.utils.StringUtil;
import com.hc360.mmt.db.po.proddb.BusinAttValue;
import com.hc360.mmt.db.po.proddb.BusinChance;
import com.hc360.mmt.db.po.proddb.OnBusinChance;
import com.hc360.score.db.dao.ProdDao;

import java.util.List;

/**
 * Created by whc on 2016/7/8.
 * 计算标题
 */
public class CalculateTitle extends CalculateTemplate {

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //如果supcatid没值从库里获取
        if(bsi==null||StringUtil.isBlank(bsi.getTitle())){
            BusinChance busin=bsd.getBusin();
            if(busin==null){
                busin = ProdDao.getInstance(false).getBusinChance(bcid);
                bsd.setBusin(busin);
            }
            if(busin!=null){
                bsi.setTitle(busin.getTitle());
                bsi.setKeyword(busin.getKeyword());
            }else{
                OnBusinChance obc=bsd.getObc();
                if(obc==null){
                    obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
                    bsd.setObc(obc);
                }
                if(obc!=null){
                    bsi.setTitle(obc.getTitle());
                    bsi.setKeyword(obc.getKeyword());
                }
            }

            List<BusinAttValue> bavList= ProdDao.getInstance(false).getCommonBusinAtt(bcid);
            String[] brandAndModel=BusinCompleCaluteNewUtils.obtainBrandAndModel(bavList);
            if(brandAndModel!=null&&brandAndModel.length>0){
                bsi.setBrand(brandAndModel[0]);
                bsi.setModel(brandAndModel[1]);
            }
        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{
        BusinInfo bi=new BusinInfo();
        int type=BusinCompleCaluteNewUtils.doTitleSourceNew(businSourceInfo.getTitle(),businSourceInfo.getKeyword(),
                businSourceInfo.getBrand(),businSourceInfo.getModel());
        bi.setHasOtherTitleDetail(type);
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException{
        BusinScore bs=new BusinScore();
        BusinCompleCaluteNewUtils.caluteTitle(businInfo,bs);
        return bs;
    }

    @Override
    com.hc360.score.utils.BusinChance transmitParam(BusinInfo businInfo, com.hc360.score.utils.BusinChance businChance)throws MmtException{

       businChance.setHasOtherTitleDetail(businInfo.getHasOtherTitleDetail());
        return businChance;
    }

}
