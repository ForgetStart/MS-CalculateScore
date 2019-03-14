package com.hc360.score.message.service;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.bcs.bo.BusinScore;
import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.proddb.OnBusinChance;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.ProdSampleDao;
import com.hc360.score.message.Transinformation;
import com.hc360.score.statistics.BusinessRecord;
import com.hc360.score.utils.BusinchanceSampleUtils;
import org.apache.log4j.Logger;

import java.util.List;

public class BusinchanceSampleHZTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	private static BusinchanceSampleHZTaskManage instance = new BusinchanceSampleHZTaskManage();
	public static BusinchanceSampleHZTaskManage getInstance(){
		return instance;
	}

    /**
     * �������
     * ���������ȱ������ƽ�չλ������չλ���������������̱�����ͨ�̻�
     * @return
     */
	public boolean sampleBusinScore(String source){

		Transinformation traninfo = new Transinformation(new BusinInfo());
		traninfo.setBusinessRecord(new BusinessRecord("0",0,System.currentTimeMillis()));

        int count=0;

		// �ܷ�
		try {
            PageBean pageBean = new PageBean();
            pageBean.setPage(1);
            pageBean.setPageSize(5000);
            Page page=ProdSampleDao.getInstance(false).getHuangzhangBusinchanceByPage(pageBean);
            List<PageRecordBean> prdList= page.getLstResult();
            List<OnBusinChance> obcList=BusinchanceSampleUtils.convert(prdList);
            if (obcList != null && obcList.size() > 0) {
				for (OnBusinChance busin : obcList) {
                    try{
                        BusinScore bs = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin,source);
                        count++;
                    }catch (Exception e) {
                        logger.error("��չ/��չ�̻�����쳣��OnBusinChance:"+busin);
                        e.printStackTrace();
                    }
				}
			}else{
				logger.error("û�л�չ/��չ�̻���Ϣ");
			}

            while (2000>= pageBean.getEndNo()&&pageBean.getCount()!=pageBean.getEndNo()){
                pageBean.setPage(pageBean.getNextPageNo());
                page=ProdSampleDao.getInstance(false).getHuangzhangBusinchanceByPage(pageBean);
                prdList= page.getLstResult();
                obcList=BusinchanceSampleUtils.convert(prdList);
                if (obcList != null && obcList.size() > 0) {
                    for (OnBusinChance busin : obcList) {
                        try{
                            BusinScore bs = BusinessScoreManage.getInstance().initBusinScore(traninfo, busin,source);
                            count++;
                        }catch (Exception e) {
                            logger.error("��չ/��չ�̻�����쳣��OnBusinChance:"+busin);
                            e.printStackTrace();
                        }
                    }
                }
            }

            logger.info("������"+count+"����չ/��չ�̻���Ϣ");

		} catch (Exception e) {
            logger.error("��չ/��չ�̻���ִ����쳣"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
