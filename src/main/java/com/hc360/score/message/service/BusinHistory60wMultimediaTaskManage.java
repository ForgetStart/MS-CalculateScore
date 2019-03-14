package com.hc360.score.message.service;

import com.hc360.b2b.netWorker.GetUrl;
import com.hc360.imgup.common.bean.ImageInfo;
import com.hc360.imgup.common.io.CommonImageOperateGM;
import com.hc360.imgup.common.util.ImageIOUtil;
import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.po.proddb.BusinMultimedia;
import com.hc360.rsf.imgup.FileStorageService2WH;
import com.hc360.score.common.AppContent;
import com.hc360.score.db.dao.StatDao;
import com.hc360.score.db.rsf.RSFService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileOutputStream;
import java.util.*;

public class BusinHistory60wMultimediaTaskManage {

    private static Logger logger = Logger.getLogger(AppContent.businhistory60w);

    private static BusinHistory60wMultimediaTaskManage instance = new BusinHistory60wMultimediaTaskManage();
    public static BusinHistory60wMultimediaTaskManage getInstance(){
        return instance;
    }

    /**
     * �����̻�ͼƬ
     * @param threadno
     * @return
     */
    public boolean dealBusinMultimediaInfo(int threadno){

        int count=0;

        // �ܷ�
        try {
            long startTask = System.currentTimeMillis();
            //���÷�ҳ
            PageBean pageBean=initPageBean();

            do{
                pageBean.setPage(pageBean.getNextPageNo());
                Page page= StatDao.getInstance(false).getBusinHistoryInfoByPage(pageBean,"10,20,30", threadno);
                List<PageRecordBean> prdList= page.getLstResult();
                if (prdList != null && prdList.size() > 0) {
                    List<Map<String,Integer>> businDealList=new ArrayList<Map<String,Integer>>();
                    for (PageRecordBean prb : prdList) {
                        //�����̻�
                        dealBusin(threadno,prb,businDealList);
                    }
                    //�����޸��̻�
                    batchUpdateBusinHistoryInfo(threadno,businDealList);
                }else{
                    logger.error("Multimedia-û����threadno:="+threadno+"�̻���Ϣ");
                    break;
                }
            }while(true);

            logger.info("threadno:"+threadno+" Multimedia-������" + count + "���̻���Ϣ,use time:" + (System.currentTimeMillis() - startTask));

        } catch (Exception e) {
            logger.error("threadno:"+threadno+"Multimedia-�����̻���Ϣ�쳣"+e.getMessage());
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
     * �����̻�
     * @param threadno
     * @param prb
     * @param businList
     * @param businDealList
     * @throws Exception
     */
    private void dealBusin(int threadno,PageRecordBean prb,List<Map<String,Integer>> businDealList) throws Exception{
        long bcid= prb.getLong("bc_id");
        int isdo= (int)prb.getLong("isdo");
        Map<String,Integer> businDealMap=new HashMap<String, Integer>();
        try{
            long start = System.currentTimeMillis();
            List<BusinMultimedia> bmList=BusinHistory60wManage.getInstance().dealBusinMultimediaInfo(bcid, isdo, threadno, businDealList);
            if(bmList!=null&&bmList.size()>0){
                for(int index=0;index<bmList.size();index++){
                    BusinMultimedia bm=bmList.get(index);
                    //����ͼƬ
                    dealBusinMultimedia(threadno,bcid,index+1,bm);
                }
            }

            //TODO
            /*BusinMultimedia bm=new BusinMultimedia();
            bm.setFilename("https://www.baidu.com/img/bd_logo1.png");
            dealBusinMultimediaTest(threadno,bcid,1,bm);*/

            logger.info("threadno:" + threadno + " Multimedia-�����̻���bcid:" + bcid + ",use time:" + (System.currentTimeMillis() - start));
        }catch (Exception e) {
            businDealMap.put("bc_id", (int) bcid);
            //�����̻�ͼƬ�쳣��״̬����Ϊ2
            businDealMap.put("isdo",isdo+2);
            //��ӽ��б�����������
            businDealList.add(businDealMap);
            logger.error("threadno:"+threadno+" Multimedia-�����̻��쳣��bcid:"+bcid);
            e.printStackTrace();
        }
    }

    /**
     * ����ͼƬ
     * @param threadno
     * @param bcid
     * @param index
     * @param bm
     * @throws Exception
     */
    private void dealBusinMultimedia(int threadno,long bcid,int index,BusinMultimedia bm) throws Exception{
        try{
            String filename=bm.getFilename();
            if(StringUtils.isNotBlank(filename)){
                byte[] imgContent;
                if(GetUrl.isFastDFS(filename)){
                    FileStorageService2WH fss= RSFService.getFilestorageService2WH();
                    //fss.readImgFile(filename);
                    imgContent= fss.read(filename);
                }else{
                    String url=GetUrl.getPicUrl("0", filename);
                    imgContent=ImageIOUtil.readImageFromHttpUrl(url);
                }
                if(imgContent!=null&&imgContent.length>0){
                    //newһ���ļ�������������ͼƬ��Ĭ�ϱ��浱ǰ���̸�Ŀ¼
                    String outImgpath="/tmp/whc/multimedia/";
                    String outImgname=bcid+"_"+index+filename.substring(filename.lastIndexOf("."));
                    File imageFile = new File(outImgpath+outImgname);
                    //���������
                    FileOutputStream outStream = new FileOutputStream(imageFile);
                    //д������
                    outStream.write(imgContent);
                    //�ر������
                    outStream.close();
                }
            }
        }catch (Exception e){
            logger.error("threadno:" + threadno + " Multimedia-�����̻�ͼƬ�쳣��bcid:" + bcid + ",bimid:" + bm.getId() + "," +e.getMessage());
        }
    }


    /**
     * ����ͼƬ
     * @param threadno
     * @param bcid
     * @param index
     * @param bm
     * @throws Exception
     */
    private void dealBusinMultimediaTest(int threadno,long bcid,int index,BusinMultimedia bm) throws Exception{
        try{
            String filename=bm.getFilename();
            if(StringUtils.isNotBlank(filename)){
                byte[] imgContent;

                imgContent=ImageIOUtil.readImageFromHttpUrl(filename);
                if(imgContent!=null&&imgContent.length>0){
                    //newһ���ļ�������������ͼƬ��Ĭ�ϱ��浱ǰ���̸�Ŀ¼
                    String outImgpath="/tmp/whc/multimedia";
                    String outImgname=bcid+"_"+index+filename.substring(filename.lastIndexOf("."));
                    File imageFile = new File(outImgpath+outImgname);
                    //���������
                    FileOutputStream outStream = new FileOutputStream(imageFile);
                    //д������
                    outStream.write(imgContent);
                    //�ر������
                    outStream.close();
                }
            }
        }catch (Exception e){
            logger.error("threadno:" + threadno + " Multimedia-�����̻�ͼƬ�쳣��bcid:" + bcid + ",bimid:" + bm.getId() + "," +e.getMessage());
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
            logger.info("threadno:" + threadno + "Multimedia-�����޸��̻��ɹ����޸���" + businDealList.size() + "�̻�");
        }catch (Exception e) {
            logger.error("threadno:" + threadno + "Multimedia-�����޸��̻�ʧ��,"+e.getMessage());
        }
    }

}
