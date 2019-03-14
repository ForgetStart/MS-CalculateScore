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
 * ����۸�
 */
public class CalculatePrice extends CalculateTemplate {

    @Override
    void obtainData(Long bcid,BusinSourceInfo bsi,BusinScoreData bsd) throws MmtException{
        //���supcatidûֵ�ӿ����ȡ
        if(bsi==null||StringUtil.isBlank(bsi.getIssupporttrade())){
            BusinChance busin=bsd.getBusin();
            if(busin==null){
                busin = ProdDao.getInstance(false).getBusinChance(bcid);
                bsd.setBusin(busin);
            }
            if(busin!=null){
                //�������߽���
                bsi.setIssupporttrade(busin.getIssupporttrade());
                bsi.setPriceType(busin.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
            }else{
                OnBusinChance obc=bsd.getObc();
                if(obc==null){
                    obc = ProdDao.getInstance(false).getOnBusinChance(bcid);
                    bsd.setObc(obc);
                }
                if(obc!=null){
                    //�������߽���
                    bsi.setIssupporttrade(obc.getIssupporttrade());
                    bsi.setPriceType(obc.getPricerange().doubleValue()==0?0:1);//�Ƿ��ǵ绰���ۣ�ͨ���Ƿ�֧�����߽��׼���
                }
            }
            //�۸�����
            int priceItemsCount=ProdDao.getInstance(false).getPriceItemsCount(bcid);
            bsi.setPriceItemsCount(priceItemsCount);
        }
    }

    @Override
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException{
        BusinInfo bi=new BusinInfo();
        int type=BusinCompleCaluteNewUtils.doPriceType(businSourceInfo.getIssupporttrade(),
                businSourceInfo.getPriceType(),businSourceInfo.getPriceItemsCount());
        bi.setPriceTypeNew(type);
        return bi;
    }

    @Override
    BusinScore calculate(BusinInfo businInfo) throws MmtException{
        BusinScore bs=new BusinScore();
        BusinCompleCaluteNewUtils.calutePriceType(businInfo,bs);
        return bs;
    }

    @Override
    com.hc360.score.utils.BusinChance transmitParam(BusinInfo businInfo, com.hc360.score.utils.BusinChance businChance)throws MmtException{

        businChance.setPriceTypeNew(businInfo.getPriceTypeNew());
        return businChance;
    }
}
