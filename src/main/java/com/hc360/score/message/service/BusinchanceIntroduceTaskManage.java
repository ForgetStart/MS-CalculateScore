package com.hc360.score.message.service;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.ProdSampleDao;
import com.hc360.score.message.Transinformation;
import com.hc360.score.statistics.BusinessRecord;
import org.apache.log4j.Logger;

import java.util.List;

public class BusinchanceIntroduceTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	private static BusinchanceIntroduceTaskManage instance = new BusinchanceIntroduceTaskManage();
	public static BusinchanceIntroduceTaskManage getInstance(){
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
            Page page=ProdSampleDao.getInstance(false).getBusinchanceSampleByPage(pageBean, source);
            List<PageRecordBean> prdList= page.getLstResult();
            if (prdList != null && prdList.size() > 0) {
				for (PageRecordBean prb : prdList) {
                    long bcid= prb.getLong("bc_id");
                    try{
                        long start = System.currentTimeMillis();
                        BusinessScoreManage.getInstance().caculateIntroducePic(bcid,source);
                        count++;
                        logger.info(source+"�̻���������ͼƬ��bcid:"+bcid+",use time:" + (System.currentTimeMillis() - start));
                    }catch (Exception e) {
                        logger.error(source+"�̻�����쳣��bcid:"+bcid);
                        e.printStackTrace();
                    }
				}
			}else{
				logger.error("û��"+source+"�̻���Ϣ");
                return false;
			}

            while (pageBean.getCount()>pageBean.getEndNo()){
                pageBean.setPage(pageBean.getNextPageNo());
                page=ProdSampleDao.getInstance(false).getBusinchanceSampleByPage(pageBean, source);
                prdList= page.getLstResult();
                if (prdList != null && prdList.size() > 0) {
                    for (PageRecordBean prb : prdList) {
                        long bcid= prb.getLong("bc_id");
                        try{
                            BusinessScoreManage.getInstance().caculateIntroducePic(bcid,source);
                            count++;
                        }catch (Exception e) {
                            logger.error(source+"�̻�����쳣��bcid:"+bcid);
                            e.printStackTrace();
                        }
                    }
                }
            }

            logger.info("������" + count + "��"+source+"�̻���Ϣ");

		} catch (Exception e) {
            logger.error(source+"�̻���ִ����쳣"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
