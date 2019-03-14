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
     * �����̻�����
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
        String context = "<P>����˾��Ӫ�����㲥/��ƺ���䣬������֤����ӭ��ѯǢ̸��\n" +
                "\n" +
                "<P><IMG alt=\"10 ��ƺ����\" src=\"http://www.dahaaudio.com/UploadFile/2010030911003072084.jpg\" border=1>\n" +
                "\n" +
                "<P><FONT face=���� color=#0000ff size=3>��&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;�ţ�CPY-10</FONT></P>\n" +
                "\n" +
                "<P><FONT face=���� color=#0000ff size=3>��&nbsp;&nbsp;&nbsp;&nbsp; �ʣ�20W/30W</FONT></P>\n" +
                "\n" +
                "<P><FONT face=���� color=#0000ff size=3>��&nbsp;&nbsp;&nbsp;&nbsp; �磺320*350mm</FONT></P>\n" +
                "\n" +
                "<P><FONT face=���� color=#0000ff size=3>��&nbsp;&nbsp;&nbsp;&nbsp; �ϣ�ABS</FONT></P>";
        Map<String,Integer> businDealMap=new HashMap<String, Integer>();
        if(StringUtil.isNotBlank(context)){
            businDealMap.put("bc_id",(int)bcid);
            //�̻�������Ϣ��Ϊ�գ�״̬����Ϊ10
            businDealMap.put("isdo", 10);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
        }else{
            businDealMap.put("bc_id",(int)bcid);
            //�̻�������ϢΪ�գ�״̬����Ϊ30
            businDealMap.put("isdo", 30);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
            logger.error("bcid:"+bcid+",�̻�����Ϊ��");
        }
        return context;
    }

    /**
     * �����̻�ͼƬ
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
            //�̻�������Ϣ��Ϊ�գ�״̬����Ϊ1
            businDealMap.put("isdo", isdo+1);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
        }else{
            businDealMap.put("bc_id",(int)bcid);
            //�̻�������ϢΪ�գ�״̬����Ϊ3
            businDealMap.put("isdo", isdo+3);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
            logger.error("bcid:"+bcid+",�̻�����Ϊ��");
        }
        return bmList;
    }

}
