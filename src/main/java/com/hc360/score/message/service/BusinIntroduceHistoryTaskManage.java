package com.hc360.score.message.service;

import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.statdb.BusinIntroduceCsinfo;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.StatDao;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class BusinIntroduceHistoryTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

    private static BusinIntroduceHistoryTaskManage instance = new BusinIntroduceHistoryTaskManage();
    public static BusinIntroduceHistoryTaskManage getInstance(){
        return instance;
    }
    //public static boolean TASK_STATES=true;


    public boolean dealBusinIntroduceInfo(int threadno){

        int count=0;

        // 总分
        try {
            long startTask = System.currentTimeMillis();
            PageBean pageBean = new PageBean();
            pageBean.setPageSize(200);

            do{
            /*while(true){//判断商机详情信息任务状态
                BusinDataTask bdt=StatDao.getInstance(false).getBusinIntroduceTask("BusinIntroduceTask");
                if(bdt!=null&&"0".equals(bdt.getStates())){//为0可执行
                   break;
                }
                Thread.sleep(5*60*1000);
            }*/
                pageBean.setPage(1);
                Page page= StatDao.getInstance(false).getBusinIntroduceCsinfoByPage(pageBean, threadno);
                List<PageRecordBean> prdList= page.getLstResult();
                if (prdList != null && prdList.size() > 0) {
                    List<BusinIntroduceCsinfo> ciList=new ArrayList<BusinIntroduceCsinfo>();
                    for (PageRecordBean prb : prdList) {
                    /*if(!TASK_STATES){
                        break;
                    }*/
                        long bcid= prb.getLong("bc_id");
                        try{
                            long start = System.currentTimeMillis();
                            BusinessScoreManage.getInstance().dealIntroduceCsInfo(bcid,threadno,ciList);
                            count++;
                            logger.info("threadno:"+threadno+" 处理商机详情，bcid:"+bcid+",use time:" + (System.currentTimeMillis() - start));
                        }catch (Exception e) {
                            StatDao statDao = StatDao.getInstance(false);
                            BusinIntroduceCsinfo dbiCsinfo=statDao.getBusinIntroduceCsinfo(bcid,threadno);
                            if(dbiCsinfo!=null){
                                //处理商机详情异常，状态设置为8
                                dbiCsinfo.setStates("8");
                                //添加进列表，做批量处理
                                ciList.add(dbiCsinfo);
                                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
                            }
                            logger.error("threadno:"+threadno+" 处理商机详情异常，bcid:"+bcid);
                            e.printStackTrace();
                        }
                    }

                    try {
                        StatDao statDao = StatDao.getInstance(false);
                        statDao.batchUpdateBusinIntroduceCsinfo(ciList);
                        logger.info("threadno:" + threadno + "批量修改商机详情成功，修改了" + ciList.size() + "商机");
                    }catch (Exception e) {
                        logger.error("threadno:" + threadno + "批量修改商机详情失败,"+e.getMessage());
                    }finally{
                        ciList = null;
                    }
                }else{
                    logger.error("没有threadno:="+threadno+"商机信息");
                    return false;
                }
            }while(pageBean.getCount()>0);


            logger.info("threadno:"+threadno+" 处理了" + count + "条商机信息,use time:" + (System.currentTimeMillis() - startTask));

        } catch (Exception e) {
            logger.error("threadno:"+threadno+"处理商机详情信息异常"+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
