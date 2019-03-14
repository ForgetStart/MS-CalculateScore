package com.hc360.score.message.service;

import com.hc360.bcs.utils.StringUtil;
import com.hc360.mmt.db.po.proddb.BusinMultimedia;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.ProdDao;
import org.apache.log4j.Logger;
import java.util.*;

public class BusinHistory60wManage {

    private static Logger logger = Logger.getLogger(AppContent.businhistory60w);

	private static BusinHistory60wManage instance = new BusinHistory60wManage();
	public static BusinHistory60wManage getInstance(){
		return instance;
	}


    /**
     * 处理商机详情
     * @param bcid
     * @throws Exception
     */
    public String dealBusinIntroduceInfo(long bcid,int threadno,List<Map<String,Integer>> businDealList) throws Exception {
        /*String context = "";
        KVDBResult oldResult = RSFService.getKvdbbcService().getBakOrOn("CalculateScore",""+ bcid);
        if (oldResult.getState() == KVDBResult.STATE_SUCCESS) {
            if (oldResult.getValue() != null) {
                context = new String(oldResult.getValue(),"GBK");
            }
        }*/
        String context = "<P>本公司经营公共广播/草坪音箱，质量保证，欢迎咨询洽谈。\n" +
                "\n" +
                "<P><IMG alt=\"10 草坪音箱\" src=\"http://www.dahaaudio.com/UploadFile/2010030911003072084.jpg\" border=1>\n" +
                "\n" +
                "<P><FONT face=宋体 color=#0000ff size=3>型&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;号：CPY-10</FONT></P>\n" +
                "\n" +
                "<P><FONT face=宋体 color=#0000ff size=3>功&nbsp;&nbsp;&nbsp;&nbsp; 率：20W/30W</FONT></P>\n" +
                "\n" +
                "<P><FONT face=宋体 color=#0000ff size=3>尺&nbsp;&nbsp;&nbsp;&nbsp; 寸：320*350mm</FONT></P>\n" +
                "\n" +
                "<P><FONT face=宋体 color=#0000ff size=3>材&nbsp;&nbsp;&nbsp;&nbsp; 料：ABS</FONT></P>";
        Map<String,Integer> businDealMap=new HashMap<String, Integer>();
        if(StringUtil.isNotBlank(context)){
            businDealMap.put("bc_id",(int)bcid);
            //商机详情信息不为空，状态设置为10
            businDealMap.put("isdo", 10);
            //添加进列表，做批量处理
            businDealList.add(businDealMap);
        }else{
            businDealMap.put("bc_id",(int)bcid);
            //商机详情信息为空，状态设置为30
            businDealMap.put("isdo", 30);
            //添加进列表，做批量处理
            businDealList.add(businDealMap);
            logger.error("bcid:"+bcid+",商机详情为空");
        }
        return context;
    }

    /**
     * 处理商机图片
     * @param bcid
     * @param isdo
     * @param threadno
     * @param businDealList
     * @return
     * @throws Exception
     */
    public List<BusinMultimedia> dealBusinMultimediaInfo(long bcid,int isdo,int threadno,List<Map<String,Integer>> businDealList) throws Exception {
        List<BusinMultimedia> bmList = ProdDao.getInstance(false).getBusinPicdetailList(bcid);
        Map<String,Integer> businDealMap=new HashMap<String, Integer>();

        if(bmList!=null&&bmList.size()>0){
            businDealMap.put("bc_id",(int)bcid);
            //商机详情信息不为空，状态设置为1
            businDealMap.put("isdo", isdo+1);
            //添加进列表，做批量处理
            businDealList.add(businDealMap);
        }else{
            businDealMap.put("bc_id",(int)bcid);
            //商机详情信息为空，状态设置为3
            businDealMap.put("isdo", isdo+3);
            //添加进列表，做批量处理
            businDealList.add(businDealMap);
            logger.error("bcid:"+bcid+",商机详情为空");
        }
        return bmList;
    }

}
