package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.score.utils.BusinChance;

/**
 * Created by whc on 2016/7/8.
 * 计算分值模板类
 */
public abstract class CalculateTemplate {

    /**
     * 获取数据
     *
     * @param bcid
     * @return
     */
    void obtainData(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd) throws MmtException {
    }

    /**
     * 处理数据
     *
     * @param businSourceInfo
     * @return
     */
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException {
        return null;
    }

    /**
     * 计算
     *
     * @return
     */
    BusinScore calculate(BusinInfo businInfo) throws MmtException {
        return null;
    }


    public BusinScore calculateProcess(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd) throws MmtException {
        //获取数据
        obtainData(bcid, bsi, bsd);
        //处理数据
        BusinInfo bi = dealData(bsi);
        //计算
        BusinScore bs = calculate(bi);
        return bs;
    }

    BusinChance transmitParam(BusinInfo businInfo, BusinChance businChance) throws MmtException {

        return null;
    }

    /**
     * 新的算分流程，各项不同维度初始化好算分的条件后，统一封装到BusinChance中，
     * 最后调用drools，执行算分
     * @param bcid
     * @param bsi
     * @param bsd
     * @param businChance
     * @return
     * @throws MmtException
     */
    public BusinChance calculateInitParam(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd, BusinChance businChance) throws MmtException {
        //获取数据
        obtainData(bcid, bsi, bsd);
        //处理数据
        BusinInfo bi = dealData(bsi);
        //初始化需要算分的各项参数
        businChance = transmitParam(bi, businChance);

        return businChance;
    }

}
