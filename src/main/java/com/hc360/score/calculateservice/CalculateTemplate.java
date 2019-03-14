package com.hc360.score.calculateservice;

import com.hc360.b2b.exception.MmtException;
import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.bcs.bo.BusinScoreData;
import com.hc360.bcs.bo.BusinSourceInfo;
import com.hc360.score.utils.BusinChance;

/**
 * Created by whc on 2016/7/8.
 * �����ֵģ����
 */
public abstract class CalculateTemplate {

    /**
     * ��ȡ����
     *
     * @param bcid
     * @return
     */
    void obtainData(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd) throws MmtException {
    }

    /**
     * ��������
     *
     * @param businSourceInfo
     * @return
     */
    BusinInfo dealData(BusinSourceInfo businSourceInfo) throws MmtException {
        return null;
    }

    /**
     * ����
     *
     * @return
     */
    BusinScore calculate(BusinInfo businInfo) throws MmtException {
        return null;
    }


    public BusinScore calculateProcess(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd) throws MmtException {
        //��ȡ����
        obtainData(bcid, bsi, bsd);
        //��������
        BusinInfo bi = dealData(bsi);
        //����
        BusinScore bs = calculate(bi);
        return bs;
    }

    BusinChance transmitParam(BusinInfo businInfo, BusinChance businChance) throws MmtException {

        return null;
    }

    /**
     * �µ�������̣����ͬά�ȳ�ʼ������ֵ�������ͳһ��װ��BusinChance�У�
     * ������drools��ִ�����
     * @param bcid
     * @param bsi
     * @param bsd
     * @param businChance
     * @return
     * @throws MmtException
     */
    public BusinChance calculateInitParam(Long bcid, BusinSourceInfo bsi, BusinScoreData bsd, BusinChance businChance) throws MmtException {
        //��ȡ����
        obtainData(bcid, bsi, bsd);
        //��������
        BusinInfo bi = dealData(bsi);
        //��ʼ����Ҫ��ֵĸ������
        businChance = transmitParam(bi, businChance);

        return businChance;
    }

}
