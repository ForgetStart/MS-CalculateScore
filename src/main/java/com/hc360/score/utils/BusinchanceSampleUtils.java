package com.hc360.score.utils;

import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.proddb.BusinIntroducePicWh;
import com.hc360.mmt.db.po.proddb.OnBusinChance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by wanghuacun on 2016/5/17.
 */
public class BusinchanceSampleUtils {

    public static List<OnBusinChance> convert(List<PageRecordBean> prdList){
        if(prdList!=null&&prdList.size()>0){
            List<OnBusinChance> obcList=new ArrayList<OnBusinChance>();
            for(PageRecordBean prd:prdList){
                try{
                    OnBusinChance obc=new OnBusinChance();
                    obc.setId(prd.getLong("bc_id"));
                    obc.setStates(prd.getString("states"));
                    obc.setChecked(prd.getString("checked"));
                    obc.setUnchecked(prd.getString("unchecked"));
                    obc.setEnddate(prd.getTimestamp("enddate"));
                    obcList.add(obc);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            return obcList;
        }
        return null;
    }


    public static BusinIntroducePicWh convert(Map<String,Integer> imgWhMap,Long bcid,String source){
        BusinIntroducePicWh bipWh=new BusinIntroducePicWh();
        bipWh.setBcid(bcid);
        bipWh.setSource(source);
        if(imgWhMap!=null&&imgWhMap.size()>0){
            bipWh.setPic(imgWhMap.get("pic"));
            bipWh.setW100(imgWhMap.get("w100"));
            bipWh.setW100_200(imgWhMap.get("w100_200"));
            bipWh.setW200_300(imgWhMap.get("w200_300"));
            bipWh.setW300_400(imgWhMap.get("w300_400"));
            bipWh.setW400_500(imgWhMap.get("w400_500"));
            bipWh.setW500_600(imgWhMap.get("w500_600"));
            bipWh.setW600_700(imgWhMap.get("w600_700"));
            bipWh.setW700_800(imgWhMap.get("w700_800"));
            bipWh.setW800(imgWhMap.get("w800"));

            bipWh.setH100(imgWhMap.get("h100"));
            bipWh.setH100_200(imgWhMap.get("h100_200"));
            bipWh.setH200_300(imgWhMap.get("h200_300"));
            bipWh.setH300_400(imgWhMap.get("h300_400"));
            bipWh.setH400_500(imgWhMap.get("h400_500"));
            bipWh.setH500_600(imgWhMap.get("h500_600"));
            bipWh.setH600_700(imgWhMap.get("h600_700"));
            bipWh.setH700_800(imgWhMap.get("h700_800"));
            bipWh.setH800(imgWhMap.get("h800"));
            return bipWh;
        }
        return bipWh;
    }

    public static BusinIntroducePicWh convert(BusinIntroducePicWh sbipWh,BusinIntroducePicWh dbipWh){
        dbipWh.setPic(sbipWh.getPic());
        dbipWh.setW100(sbipWh.getW100());
        dbipWh.setW100_200(sbipWh.getW100_200());
        dbipWh.setW200_300(sbipWh.getW200_300());
        dbipWh.setW300_400(sbipWh.getW300_400());
        dbipWh.setW400_500(sbipWh.getW400_500());
        dbipWh.setW500_600(sbipWh.getW500_600());
        dbipWh.setW600_700(sbipWh.getW600_700());
        dbipWh.setW700_800(sbipWh.getW700_800());
        dbipWh.setW800(sbipWh.getW800());

        dbipWh.setH100(sbipWh.getH100());
        dbipWh.setH100_200(sbipWh.getH100_200());
        dbipWh.setH200_300(sbipWh.getH200_300());
        dbipWh.setH300_400(sbipWh.getH300_400());
        dbipWh.setH400_500(sbipWh.getH400_500());
        dbipWh.setH500_600(sbipWh.getH500_600());
        dbipWh.setH600_700(sbipWh.getH600_700());
        dbipWh.setH700_800(sbipWh.getH700_800());
        dbipWh.setH800(sbipWh.getH800());
        return dbipWh;
    }

}
