package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.*;
import org.apache.log4j.Logger;

public class BusinchanceIntroduce {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	/**
     * 抽样算分
     * 标王、极度标王、黄金展位、超级展位、滚动排名、慧商宝、普通商机
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("BusinchanceIntroduce start............");
            if(args!=null&&args.length>0){
                String sampleSource = args[0];
                if("bw".equals(sampleSource)){//标王
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("bw");
                }
                else if("jdbw".equals(sampleSource)){//极度标王
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("jdbw");
                }
                else if("hz".equals(sampleSource)){//黄金展位、超级展位
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hz");
                }
                else if("gp".equals(sampleSource)){//滚动排名
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("gp");
                }
                else if("hsb".equals(sampleSource)){//慧商宝
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hsb");
                }
                else if("pt".equals(sampleSource)){//普通
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("pt");
                }
            }
            else{
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("bw");
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("jdbw");
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hz");
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("gp");
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hsb");
                BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("pt");
            }

            logger.info("BusinchanceIntroduce end...............");
            logger.info("BusinchanceIntroduce use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
