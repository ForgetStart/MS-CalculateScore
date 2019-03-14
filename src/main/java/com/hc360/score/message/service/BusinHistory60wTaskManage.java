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
     * 处理商机
     * @param threadno
     * @return
     */
    public boolean dealBusinInfo(int threadno) throws Exception{
        long startTask = System.currentTimeMillis();
        //处理商机详情
        BusinHistory60wIntroduceTaskManage.getInstance().dealBusinIntroduceInfo(threadno);
        //处理商机图片
        //BusinHistory60wMultimediaTaskManage.getInstance().dealBusinMultimediaInfo(threadno);

        logger.info("threadno:"+threadno+"处理商机信息,use time:" + (System.currentTimeMillis() - startTask));
        return true;
    }

}
