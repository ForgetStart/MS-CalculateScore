package com.hc360.score.message.service;


import com.hc360.score.common.AppContent;
import org.apache.log4j.Logger;

public class BusinHistory60wTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.businhistory60w);

    private static BusinHistory60wTaskManage instance = new BusinHistory60wTaskManage();
    public static BusinHistory60wTaskManage getInstance(){
        return instance;
    }

    /**
     * �����̻�
     * @param threadno
     * @return
     */
    public boolean dealBusinInfo(int threadno) throws Exception{
        long startTask = System.currentTimeMillis();
        //�����̻�����
        BusinHistory60wIntroduceTaskManage.getInstance().dealBusinIntroduceInfo(threadno);
        //�����̻�ͼƬ
        //BusinHistory60wMultimediaTaskManage.getInstance().dealBusinMultimediaInfo(threadno);

        logger.info("threadno:"+threadno+"�����̻���Ϣ,use time:" + (System.currentTimeMillis() - startTask));
        return true;
    }

}
