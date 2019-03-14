package com.hc360.score.task;

import com.hc360.score.common.AppContent;
import com.hc360.score.message.service.*;
import org.apache.log4j.Logger;

public class BusinchanceSample {

    private static Logger logger = Logger.getLogger(AppContent.calculatescorelog);

	/**
     * �������
     * ���������ȱ������ƽ�չλ������չλ���������������̱�����ͨ�̻�
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			long start = System.currentTimeMillis();
            logger.info("BusinchanceSample start............");
            if(args!=null&&args.length>0){
                String sampleSource = args[0];
                if("bw".equals(sampleSource)){//����
                    BusinchanceSampleBWTaskManage.getInstance().sampleBusinScore("bw");
                }
                else if("jdbw".equals(sampleSource)){//���ȱ���
                    BusinchanceSampleJDBWTaskManage.getInstance().sampleBusinScore("jdbw");
                }
                else if("hz".equals(sampleSource)){//�ƽ�չλ������չλ
                    BusinchanceSampleHZTaskManage.getInstance().sampleBusinScore("hz");
                }
                else if("gp".equals(sampleSource)){//��������
                    BusinchanceSampleGPTaskManage.getInstance().sampleBusinScore("gp");
                }
                else if("hsb".equals(sampleSource)){//���̱�
                    BusinchanceSampleHSBTaskManage.getInstance().sampleBusinScore("hsb");
                }
                else if("pt".equals(sampleSource)){//��ͨ
                    BusinchanceSamplePTTaskManage.getInstance().sampleBusinScore("pt");
                }
            }
            else{
                BusinchanceSampleBWTaskManage.getInstance().sampleBusinScore("bw");
                BusinchanceSampleJDBWTaskManage.getInstance().sampleBusinScore("jdbw");
                BusinchanceSampleHZTaskManage.getInstance().sampleBusinScore("hz");
                BusinchanceSampleGPTaskManage.getInstance().sampleBusinScore("gp");
                BusinchanceSampleHSBTaskManage.getInstance().sampleBusinScore("hsb");
                BusinchanceSamplePTTaskManage.getInstance().sampleBusinScore("pt");
            }

            logger.info("BusinchanceSample end...............");
            logger.info("BusinchanceSample use time:" + (System.currentTimeMillis() - start));

		}catch(Exception e){
			e.printStackTrace();
		}
		System.exit(0);
	}

}
