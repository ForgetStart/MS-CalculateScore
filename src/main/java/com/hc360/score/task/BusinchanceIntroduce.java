package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.*;
import org.apache.log4j.Logger;

public class BusinchanceIntroduce {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	/**
     * �������
     * ���������ȱ������ƽ�չλ������չλ���������������̱�����ͨ�̻�
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("BusinchanceIntroduce start............");
            if(args!=null&&args.length>0){
                String sampleSource = args[0];
                if("bw".equals(sampleSource)){//����
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("bw");
                }
                else if("jdbw".equals(sampleSource)){//���ȱ���
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("jdbw");
                }
                else if("hz".equals(sampleSource)){//�ƽ�չλ������չλ
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hz");
                }
                else if("gp".equals(sampleSource)){//��������
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("gp");
                }
                else if("hsb".equals(sampleSource)){//���̱�
                    BusinchanceIntroduceTaskManage.getInstance().sampleBusinScore("hsb");
                }
                else if("pt".equals(sampleSource)){//��ͨ
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
