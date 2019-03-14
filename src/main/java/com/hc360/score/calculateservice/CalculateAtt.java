package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.bcs.utils.BusinCompleCaluteNewUtils;
import com.hc360.bcs.utils.BusinCompleCaluteUtils;
import com.hc360.mmt.db.po.proddb.BusinAttValue;
import com.hc360.score.db.dao.ProdDao;
import com.hc360.score.utils.BusinChance;

import java.util.List;

/**
 * Created by whc on 2016/7/8.
 * 计算参数
 */
public class CalculateAtt extends CalculateTemplate {

    @Override
    void obtainData(Long bcid, BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException {
        if(bsi==null||bsi.getParamAmount()<=0||bsi.getParamRepetition()<=0){
            //添加已填写的参数数量 参数是否含有限期整改词 已填写的参数重复度
            List<BusinAttValue> bavList=bsd.getBavList();
            if(bavList==null||bavList.size()<=0){
                bavList= ProdDao.getInstance(false).getCommonBusinAtt(bcid);
                bsd.setBavList(bavList);
            }
            if (bavList != null && bavList.size() > 0) {
                bsi.setParamAmount(bavList.size());
                bsi.setParamRepetition(BusinCompleCaluteUtils.obtainParamRepetition(bavList));
            }

        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo)throws MmtException {
        BusinInfo bi=new BusinInfo();
        bi.setParamAmount(businSourceInfo.getParamAmount());
        int type =BusinCompleCaluteNewUtils.doParamRepetitionSource(businSourceInfo.getParamRepetition());
        bi.setParamRepetition(type);
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException {
        BusinScore bs=new BusinScore();
        BusinCompleCaluteNewUtils.caluteBaseParam(businInfo,bs);
        return bs;
    }

    @Override
    BusinChance transmitParam(BusinInfo businInfo, BusinChance businChance)throws MmtException{

        businChance.setParamAmount(businInfo.getParamAmount());
        businChance.setRepetitorAmount(businInfo.getParamRepetition());
        return businChance;
    }

}
