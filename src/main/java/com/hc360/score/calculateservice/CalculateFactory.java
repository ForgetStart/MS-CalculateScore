package com.hc360.score.calculateservice;

import com.hc360.score.common.AppContent;

/**
 * Created by whc on 2016/7/8.
 * �����๤��
 */
public class CalculateFactory {

    //��ʶ
    private String identity;

    public CalculateFactory(String identity) {
        this.identity =identity;
    }

    public CalculateTemplate getInstance(){
        if(AppContent.CALCULATE_SUPCAT.equalsIgnoreCase(identity)){
            //��Ŀ����
            return new CalculateSupcat();
        }else if(AppContent.CALCULATE_ATT.equalsIgnoreCase(identity)){
            //��������
            return new CalculateAtt();
        }else if(AppContent.CALCULATE_TITLE.equalsIgnoreCase(identity)){
            //�������
            return new CalculateTitle();
        }else if(AppContent.CALCULATE_PRICE.equalsIgnoreCase(identity)){
            //�۸����
            return new CalculatePrice();
        }else if(AppContent.CALCULATE_MULTIMEDIA.equalsIgnoreCase(identity)){
            //ͼƬ����
            return new CalculateMultimedia();
        }else if(AppContent.CALCULATE_INTRODUCE.equalsIgnoreCase(identity)){
            //�������
            return new CalculateIntroduce();
        }
        return null;
    }

}
