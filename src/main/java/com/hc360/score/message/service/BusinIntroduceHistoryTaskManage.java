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

        // �ܷ�
        try {
            long startTask = System.currentTimeMillis();
            PageBean pageBean = new PageBean();
            pageBean.setPageSize(200);

            do{
            /*while(true){//�ж��̻�������Ϣ����״̬
                BusinDataTask bdt=StatDao.getInstance(false).getBusinIntroduceTask("BusinIntroduceTask");
                if(bdt!=null&&"0".equals(bdt.getStates())){//Ϊ0��ִ��
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
                            logger.info("threadno:"+threadno+" �����̻����飬bcid:"+bcid+",use time:" + (System.currentTimeMillis() - start));
                        }catch (Exception e) {
                            StatDao statDao = StatDao.getInstance(false);
                            BusinIntroduceCsinfo dbiCsinfo=statDao.getBusinIntroduceCsinfo(bcid,threadno);
                            if(dbiCsinfo!=null){
                                //�����̻������쳣��״̬����Ϊ8
                                dbiCsinfo.setStates("8");
                                //��ӽ��б�����������
                                ciList.add(dbiCsinfo);
                                //statDao.updateBusinIntroduceCsinfo(dbiCsinfo);
                            }
                            logger.error("threadno:"+threadno+" �����̻������쳣��bcid:"+bcid);
                            e.printStackTrace();
                        }
                    }

                    try {
                        StatDao statDao = StatDao.getInstance(false);
                        statDao.batchUpdateBusinIntroduceCsinfo(ciList);
                        logger.info("threadno:" + threadno + "�����޸��̻�����ɹ����޸���" + ciList.size() + "�̻�");
                    }catch (Exception e) {
                        logger.error("threadno:" + threadno + "�����޸��̻�����ʧ��,"+e.getMessage());
                    }finally{
                        ciList = null;
                    }
                }else{
                    logger.error("û��threadno:="+threadno+"�̻���Ϣ");
                    return false;
                }
            }while(pageBean.getCount()>0);


            logger.info("threadno:"+threadno+" ������" + count + "���̻���Ϣ,use time:" + (System.currentTimeMillis() - startTask));

        } catch (Exception e) {
            logger.error("threadno:"+threadno+"�����̻�������Ϣ�쳣"+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
