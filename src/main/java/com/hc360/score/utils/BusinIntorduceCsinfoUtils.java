package com.hc360.score.utils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hc360.bcs.bo.BusinScore;
import com.hc360.mmt.db.po.proddb.BusinAttValue;
import com.hc360.mmt.db.po.statdb.BusinIntroduceCsinfo;
import com.hc360.mmt.db.po.statdb.BusinIntroduceWHinfo;

/**
 * Created by wanghuacun on 2016/7/21
 */
public class BusinIntorduceCsinfoUtils {

    public static BusinIntroduceCsinfo convert2BusinIntroduceCsinfo(Map<String,Integer> imgWhMap,Integer wordAmount,Long bcid){
        BusinIntroduceCsinfo biCsinfo=new BusinIntroduceCsinfo();
        biCsinfo.setBcid(bcid);
        biCsinfo.setStates("1");
        biCsinfo.setDealtime(new Timestamp(new Date().getTime()));
        if(imgWhMap!=null&&imgWhMap.size()>0){
            biCsinfo.setPic(imgWhMap.get("pic"));
            biCsinfo.setW100(imgWhMap.get("w100"));
            biCsinfo.setW100_200(imgWhMap.get("w100_200"));
            biCsinfo.setW200_300(imgWhMap.get("w200_300"));
            biCsinfo.setW300_400(imgWhMap.get("w300_400"));
            biCsinfo.setW400_500(imgWhMap.get("w400_500"));
            biCsinfo.setW500_600(imgWhMap.get("w500_600"));
            biCsinfo.setW600_700(imgWhMap.get("w600_700"));
            biCsinfo.setW700_800(imgWhMap.get("w700_800"));
            biCsinfo.setW800(imgWhMap.get("w800"));

            biCsinfo.setH100(imgWhMap.get("h100"));
            biCsinfo.setH100_200(imgWhMap.get("h100_200"));
            biCsinfo.setH200_300(imgWhMap.get("h200_300"));
            biCsinfo.setH300_400(imgWhMap.get("h300_400"));
            biCsinfo.setH400_500(imgWhMap.get("h400_500"));
            biCsinfo.setH500_600(imgWhMap.get("h500_600"));
            biCsinfo.setH600_700(imgWhMap.get("h600_700"));
            biCsinfo.setH700_800(imgWhMap.get("h700_800"));
            biCsinfo.setH800(imgWhMap.get("h800"));
        }
        biCsinfo.setWordAmount(wordAmount);
        return biCsinfo;
    }

    public static BusinIntroduceWHinfo convert2BusinIntroduceWHinfo(BusinScore bs){
        BusinIntroduceWHinfo biWHinfo=new BusinIntroduceWHinfo();
        biWHinfo.setBcid(bs.getBcid());
        biWHinfo.setProviderid(bs.getProviderid());
        biWHinfo.setModifydate(new Timestamp(new Date().getTime()));
        Map<String,Integer> imgWhMap=bs.getDeatilImageWHs();
        if(imgWhMap!=null&&imgWhMap.size()>0){
            biWHinfo.setPic(imgWhMap.get("pic"));
            biWHinfo.setW100(imgWhMap.get("w100"));
            biWHinfo.setW100_200(imgWhMap.get("w100_200"));
            biWHinfo.setW200_300(imgWhMap.get("w200_300"));
            biWHinfo.setW300_400(imgWhMap.get("w300_400"));
            biWHinfo.setW400_500(imgWhMap.get("w400_500"));
            biWHinfo.setW500_600(imgWhMap.get("w500_600"));
            biWHinfo.setW600_700(imgWhMap.get("w600_700"));
            biWHinfo.setW700_800(imgWhMap.get("w700_800"));
            biWHinfo.setW800(imgWhMap.get("w800"));

            biWHinfo.setH100(imgWhMap.get("h100"));
            biWHinfo.setH100_200(imgWhMap.get("h100_200"));
            biWHinfo.setH200_300(imgWhMap.get("h200_300"));
            biWHinfo.setH300_400(imgWhMap.get("h300_400"));
            biWHinfo.setH400_500(imgWhMap.get("h400_500"));
            biWHinfo.setH500_600(imgWhMap.get("h500_600"));
            biWHinfo.setH600_700(imgWhMap.get("h600_700"));
            biWHinfo.setH700_800(imgWhMap.get("h700_800"));
            biWHinfo.setH800(imgWhMap.get("h800"));

            biWHinfo.setWordAmount(bs.getDetailWordAmount());
            return biWHinfo;
        }
        return biWHinfo;
    }

    public static BusinIntroduceCsinfo convertS2D(BusinIntroduceCsinfo sbiCsinfo,BusinIntroduceCsinfo dbiCsinfo){
        dbiCsinfo.setStates(sbiCsinfo.getStates());
        dbiCsinfo.setDealtime(sbiCsinfo.getDealtime());
        dbiCsinfo.setPic(sbiCsinfo.getPic());
        dbiCsinfo.setW100(sbiCsinfo.getW100());
        dbiCsinfo.setW100_200(sbiCsinfo.getW100_200());
        dbiCsinfo.setW200_300(sbiCsinfo.getW200_300());
        dbiCsinfo.setW300_400(sbiCsinfo.getW300_400());
        dbiCsinfo.setW400_500(sbiCsinfo.getW400_500());
        dbiCsinfo.setW500_600(sbiCsinfo.getW500_600());
        dbiCsinfo.setW600_700(sbiCsinfo.getW600_700());
        dbiCsinfo.setW700_800(sbiCsinfo.getW700_800());
        dbiCsinfo.setW800(sbiCsinfo.getW800());

        dbiCsinfo.setH100(sbiCsinfo.getH100());
        dbiCsinfo.setH100_200(sbiCsinfo.getH100_200());
        dbiCsinfo.setH200_300(sbiCsinfo.getH200_300());
        dbiCsinfo.setH300_400(sbiCsinfo.getH300_400());
        dbiCsinfo.setH400_500(sbiCsinfo.getH400_500());
        dbiCsinfo.setH500_600(sbiCsinfo.getH500_600());
        dbiCsinfo.setH600_700(sbiCsinfo.getH600_700());
        dbiCsinfo.setH700_800(sbiCsinfo.getH700_800());
        dbiCsinfo.setH800(sbiCsinfo.getH800());

        dbiCsinfo.setWordAmount(sbiCsinfo.getWordAmount());
//        dbiCsinfo.setEncode(sbiCsinfo.getEncode());
        return dbiCsinfo;
    }

    public static BusinIntroduceWHinfo convertS2D(BusinIntroduceWHinfo sbiWHinfo,BusinIntroduceWHinfo dbiWHinfo){
        dbiWHinfo.setModifydate(sbiWHinfo.getModifydate());
        dbiWHinfo.setPic(sbiWHinfo.getPic());
        dbiWHinfo.setW100(sbiWHinfo.getW100());
        dbiWHinfo.setW100_200(sbiWHinfo.getW100_200());
        dbiWHinfo.setW200_300(sbiWHinfo.getW200_300());
        dbiWHinfo.setW300_400(sbiWHinfo.getW300_400());
        dbiWHinfo.setW400_500(sbiWHinfo.getW400_500());
        dbiWHinfo.setW500_600(sbiWHinfo.getW500_600());
        dbiWHinfo.setW600_700(sbiWHinfo.getW600_700());
        dbiWHinfo.setW700_800(sbiWHinfo.getW700_800());
        dbiWHinfo.setW800(sbiWHinfo.getW800());

        dbiWHinfo.setH100(sbiWHinfo.getH100());
        dbiWHinfo.setH100_200(sbiWHinfo.getH100_200());
        dbiWHinfo.setH200_300(sbiWHinfo.getH200_300());
        dbiWHinfo.setH300_400(sbiWHinfo.getH300_400());
        dbiWHinfo.setH400_500(sbiWHinfo.getH400_500());
        dbiWHinfo.setH500_600(sbiWHinfo.getH500_600());
        dbiWHinfo.setH600_700(sbiWHinfo.getH600_700());
        dbiWHinfo.setH700_800(sbiWHinfo.getH700_800());
        dbiWHinfo.setH800(sbiWHinfo.getH800());

        dbiWHinfo.setWordAmount(sbiWHinfo.getWordAmount());
        return dbiWHinfo;
    }

    public static List<com.hc360.mmt.db.po.statdb.BusinAttValue> convertS2D(List<BusinAttValue> sbavList,List<com.hc360.mmt.db.po.statdb.BusinAttValue> dbavList){
        if(sbavList!=null&&sbavList.size()>0){
            for(BusinAttValue sbav:sbavList){
                com.hc360.mmt.db.po.statdb.BusinAttValue dbav=new com.hc360.mmt.db.po.statdb.BusinAttValue();
                dbav.setId(sbav.getId());
                dbav.setBcid(sbav.getBcid());
                dbav.setAttid(sbav.getAttid());
                dbav.setAttvalue(sbav.getAttvalue());
                dbav.setYdirect(sbav.getYdirect());
                dbav.setL3supcatid(sbav.getL3supcatid());
                dbav.setYfill(sbav.getYfill());
                dbav.setAttname(sbav.getAttname());
                dbav.setUnit(sbav.getUnit());
                dbav.setPublishtime(sbav.getPublishtime());
                dbav.setModifytime(new Timestamp(new Date().getTime()));
                dbav.setStatus(sbav.getStatus());
                dbav.setIsnormsparam(sbav.getIsnormsparam());
                dbav.setPicfile(sbav.getPicfile());
                dbavList.add(dbav);
            }
        }
        return dbavList;
    }
    
    public static void main(String[] args) {
    	String a = "stast";
		int hashCode = a.hashCode();
//		System.out.println(hash(hashCode));
		//109757569
		//116331481
		Map map = new HashMap();
		map.put("1","1");
		TreeMap<Integer,String> t = new TreeMap<Integer,String>();
		t.put(1,"first");
		t.put(2,"second");
		for(Map.Entry<Integer, String> s : t.entrySet()){
			System.out.println(s.getKey()+":"+s.getValue()+"\r\n");
			
		}
	}
    
    static int hash(int h){
    	h ^= (h >>> 20) ^ (h >>> 12);
    	return h ^ (h >>> 7) ^ (h >>> 4);
    }
}
