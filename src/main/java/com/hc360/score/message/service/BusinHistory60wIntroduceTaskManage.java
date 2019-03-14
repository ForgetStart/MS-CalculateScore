package com.hc360.score.message.service;

import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.rsf.mmt.trademanage.export.domain.RSFExportExcelParam;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.StatDao;
import com.hc360.score.utils.ExportExcelUtil;
import com.hc360.score.utils.export.ExportExcelDirector;
import com.hc360.score.utils.export.builder.domain.ExcelExport;
import com.hc360.score.utils.export.builder.domain.ExcelHeader;
import com.hc360.score.utils.export.common.CommonExportExcelBuilder;
import com.hc360.score.utils.export.common.ExportExcelBuilder;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.log4j.Logger;

import java.util.*;

public class BusinHistory60wIntroduceTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.businhistory60w);

    private static BusinHistory60wIntroduceTaskManage instance = new BusinHistory60wIntroduceTaskManage();
    public static BusinHistory60wIntroduceTaskManage getInstance(){
        return instance;
    }

    /**
     * �����̻�����
     * @param threadno
     * @return
     */
    public boolean dealBusinIntroduceInfo(int threadno){

        int count=0;

        // �ܷ�
        try {
            long startTask = System.currentTimeMillis();
            int no=0;
            do{
                //����Excel
                ExportExcelDirector director=openExcel(++no);
                //���÷�ҳ
                PageBean pageBean=initPageBean();

                boolean hasData=false;
                boolean nohasData=true;
                for(int index=0;index<25;index++){
                    pageBean.setPage(pageBean.getNextPageNo());
                    Page page= StatDao.getInstance(false).getBusinHistoryInfoByPage(pageBean,"0", threadno);
                    List<PageRecordBean> prdList= page.getLstResult();
                    if (prdList != null && prdList.size() > 0) {
                        List<Map<String,Integer>> businDealList=new ArrayList<Map<String,Integer>>();
                        List<Map> businList=new ArrayList<Map>();
                        for (PageRecordBean prb : prdList) {
                            //�����̻�
                            dealBusin(threadno,prb,businList,businDealList);
                        }
                        //ƴװExcel
                        assembleExcel(threadno, director, businList);
                        //�����޸��̻�
                        batchUpdateBusinHistoryInfo(threadno,businDealList);

                        hasData=true;
                        nohasData=false;
                    }else{
                        logger.error("Introduce-û����threadno:="+threadno+"�̻���Ϣ");
                        nohasData=true;
                        break;
                    }
                }
                if(hasData){
                    //���Excel
                    writeExcel(threadno ,director);
                }
                if(nohasData){
                    break;
                }
            }while(true);

            logger.info("threadno:"+threadno+" Introduce-������" + count + "���̻���Ϣ,use time:" + (System.currentTimeMillis() - startTask));

        } catch (Exception e) {
            logger.error("threadno:"+threadno+"Introduce-�����̻���Ϣ�쳣"+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * ���÷�ҳ
     * @return
     * @throws Exception
     */
    private PageBean initPageBean() throws Exception{
        PageBean pageBean = new PageBean();
        pageBean.setPageSize(200);
        pageBean.setPage(0);
        return pageBean;
    }

    /**
     * ����Excel
     * @return
     * @throws Exception
     */
    private ExportExcelDirector openExcel(int no) throws Exception{
        String excelNamePostfix= DateUtil.formatDate(new Date(), "yyyyMMdd_"+no);
        String excelName = "busin_intro_"+excelNamePostfix+".xlsx";
        String dirName = "/tmp/whc/businintro/";

        ExportExcelUtil.isExistsFile(dirName);
        List<ExcelHeader> headList = ExportExcelUtil.getExcelHeadList4BusinIntro();
        RSFExportExcelParam excelParam = new RSFExportExcelParam();

        ExcelExport beanExport = new ExcelExport(excelName, "businintro", dirName);
        ExportExcelBuilder builder = new CommonExportExcelBuilder(headList, excelParam, beanExport);
        ExportExcelDirector director = new ExportExcelDirector(builder, true);

        director.constructToppingInfo();

        return director;
    }

    /**
     * �����̻�
     * @param threadno
     * @param prb
     * @param businList
     * @param businDealList
     * @throws Exception
     */
    private void dealBusin(int threadno,PageRecordBean prb,List<Map> businList,List<Map<String,Integer>> businDealList) throws Exception{
        long bcid= prb.getLong("bc_id");
        Map<String,Integer> businDealMap=new HashMap<String, Integer>();
        try{
            long start = System.currentTimeMillis();
            Map businMap=new HashMap();
            String introduce=BusinHistory60wManage.getInstance().dealBusinIntroduceInfo(bcid, threadno, businDealList);
            businMap.put("BC_ID",String.valueOf(bcid));
            businMap.put("INTRODUCE", introduce);
            businList.add(businMap);
            logger.info("threadno:"+threadno+" Introduce-�����̻���bcid:"+bcid+",use time:" + (System.currentTimeMillis() - start));
        }catch (Exception e) {
            businDealMap.put("bc_id", (int) bcid);
            //�����̻��쳣��״̬����Ϊ20
            businDealMap.put("isdo",20);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
            logger.error("threadno:"+threadno+" Introduce-�����̻��쳣��bcid:"+bcid);
            e.printStackTrace();
        }
    }

    /**
     * ƴװExcel
     * @param threadno
     * @param director
     * @param businList
     * @throws Exception
     */
    private void assembleExcel(int threadno ,ExportExcelDirector director,List<Map> businList) throws Exception{
        try{
            director.constructBodyInfo(businList);
        }catch (Exception e) {
            logger.error("threadno:" + threadno + "Introduce-���������̻�ʧ��,"+e.getMessage());
        }
    }

    /**
     * ���Excel
     * @param threadno
     * @param director
     * @throws Exception
     */
    private void writeExcel(int threadno ,ExportExcelDirector director) throws Exception{
        try{
            director.write();
        }catch (Exception e) {
            logger.error("threadno:" + threadno + "Introduce-����̻�Excelʧ��,"+e.getMessage());
        }
    }

    /**
     * �����޸��̻�
     * @param threadno
     * @param businDealList
     */
    private void batchUpdateBusinHistoryInfo(int threadno ,List<Map<String,Integer>> businDealList) {
        try {
            StatDao statDao = StatDao.getInstance(false);
            statDao.batchUpdateBusinHistoryInfo(businDealList);
            logger.info("threadno:" + threadno + "Introduce-�����޸��̻��ɹ����޸���" + businDealList.size() + "�̻�");
        }catch (Exception e) {
            logger.error("threadno:" + threadno + "Introduce-�����޸��̻�ʧ��,"+e.getMessage());
        }
    }

}
