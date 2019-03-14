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
     * 抽样算分
     * 标王、极度标王、黄金展位、超级展位、滚动排名、慧商宝、普通商机
     * @return
     */
	public boolean sampleBusinScore(String source){

		Transinformation traninfo = new Transinformation(new BusinInfo());
		traninfo.setBusinessRecord(new BusinessRecord("0",0,System.currentTimeMillis()));

        int count=0;

		// 总分
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
                        logger.info(source+"商机处理详情图片，bcid:"+bcid+",use time:" + (System.currentTimeMillis() - start));
                    }catch (Exception e) {
                        logger.error(source+"商机算分异常，bcid:"+bcid);
                        e.printStackTrace();
                    }
				}
			}else{
				logger.error("没有"+source+"商机信息");
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
                            logger.error(source+"商机算分异常，bcid:"+bcid);
                            e.printStackTrace();
                        }
                    }
                }
            }

            logger.info("处理了" + count + "条"+source+"商机信息");

		} catch (Exception e) {
            logger.error(source+"商机算分处理异常"+e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
