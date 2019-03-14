package com.hc360.score.calculateservice;

import com.hc360.score.common.AppContent;

/**
 * Created by whc on 2016/7/8.
 * 计算类工厂
 */
public class CalculateFactory {

    //标识
    private String identity;

    public CalculateFactory(String identity) {
        this.identity =identity;
    }

    public CalculateTemplate getInstance(){
        if(AppContent.CALCULATE_SUPCAT.equalsIgnoreCase(identity)){
            //类目计算
            return new CalculateSupcat();
        }else if(AppContent.CALCULATE_ATT.equalsIgnoreCase(identity)){
            //参数计算
            return new CalculateAtt();
        }else if(AppContent.CALCULATE_TITLE.equalsIgnoreCase(identity)){
            //标题计算
            return new CalculateTitle();
        }else if(AppContent.CALCULATE_PRICE.equalsIgnoreCase(identity)){
            //价格计算
            return new CalculatePrice();
        }else if(AppContent.CALCULATE_MULTIMEDIA.equalsIgnoreCase(identity)){
            //图片计算
            return new CalculateMultimedia();
        }else if(AppContent.CALCULATE_INTRODUCE.equalsIgnoreCase(identity)){
            //详情计算
            return new CalculateIntroduce();
        }
        return null;
    }

}
