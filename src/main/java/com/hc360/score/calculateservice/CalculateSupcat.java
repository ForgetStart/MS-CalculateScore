package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.bcs.utils.StringUtil;
import com.hc360.mmt.db.po.proddb.BusinChance;
import com.hc360.mmt.db.po.proddb.OnBusinChance;
import com.hc360.score.db.dao.ProdDao;

/**
 * Created by whc on 2016/7/8.
 * 计算类目
 */
public class CalculateSupcat extends CalculateTemplate {

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //如果supcatid没值从库里获取
        if(bsi==null||StringUtil.isBlank(bsi.getSupcatid())){
            BusinChance busin=bsd.getBusin();
            if(busin==null){
                busin = ProdDao.getInstance(false).getBusinChance(bcid);
                bsd.setBusin(busin);
            }
            if(busin!=null){
                bsi.setSupcatid(busin.getSupcatid());
            }else{
                OnBusinChance obc=bsd.getObc();
                if(obc==null){
                    obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
                    bsd.setObc(obc);
                }
                if(obc!=null){
                    bsi.setSupcatid(obc.getSupcatid());
                }
            }
        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{
        BusinInfo bi=new BusinInfo();
        boolean isHasSupcat=BusinCompleCaluteNewUtils.doSupcatSource(businSourceInfo.getSupcatid());
        bi.setHasSupcat(isHasSupcat);
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException{
        BusinScore bs=new BusinScore();
        BusinCompleCaluteNewUtils.caluteSupcat(businInfo,bs);
        return bs;
    }

    @Override
    com.hc360.score.utils.BusinChance transmitParam(BusinInfo businInfo, com.hc360.score.utils.BusinChance businChance)throws MmtException{

        businChance.setHasSupCat(businInfo.isHasSupcat() ? 1 : 0);
        return businChance;
    }
}
