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
     * 处理商机图片
     * @param threadno
     * @return
     */
    public boolean dealBusinMultimediaInfo(int threadno){

        int count=0;

        // 总分
        try {
            long startTask = System.currentTimeMillis();
            //设置分页
            PageBean pageBean=initPageBean();

            do{
                pageBean.setPage(pageBean.getNextPageNo());
                Page page= StatDao.getInstance(false).getBusinHistoryInfoByPage(pageBean,"10,20,30", threadno);
                List<PageRecordBean> prdList= page.getLstResult();
                if (prdList != null && prdList.size() > 0) {
                    List<Map<String,Integer>> businDealList=new ArrayList<Map<String,Integer>>();
                    for (PageRecordBean prb : prdList) {
                        //处理商机
                        dealBusin(threadno,prb,businDealList);
                    }
                    //批量修改商机
                    batchUpdateBusinHistoryInfo(threadno,businDealList);
                }else{
                    logger.error("Multimedia-没有了threadno:="+threadno+"商机信息");
                    break;
                }
            }while(true);

            logger.info("threadno:"+threadno+" Multimedia-处理了" + count + "条商机信息,use time:" + (System.currentTimeMillis() - startTask));

        } catch (Exception e) {
            logger.error("threadno:"+threadno+"Multimedia-处理商机信息异常"+e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 设置分页
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
     * 处理商机
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
                    //处理图片
                    dealBusinMultimedia(threadno,bcid,index+1,bm);
                }
            }

            //TODO
            /*BusinMultimedia bm=new BusinMultimedia();
            bm.setFilename("https://www.baidu.com/img/bd_logo1.png");
            dealBusinMultimediaTest(threadno,bcid,1,bm);*/

            logger.info("threadno:" + threadno + " Multimedia-处理商机，bcid:" + bcid + ",use time:" + (System.currentTimeMillis() - start));
        }catch (Exception e) {
            businDealMap.put("bc_id", (int) bcid);
            //处理商机图片异常，状态设置为2
            businDealMap.put("isdo",isdo+2);
            //添加进列表，做批量处理
            businDealList.add(businDealMap);
            logger.error("threadno:"+threadno+" Multimedia-处理商机异常，bcid:"+bcid);
            e.printStackTrace();
        }
    }

    /**
     * 处理图片
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
                    //new一个文件对象用来保存图片，默认保存当前工程根目录
                    String outImgpath="/tmp/whc/multimedia/";
                    String outImgname=bcid+"_"+index+filename.substring(filename.lastIndexOf("."));
                    File imageFile = new File(outImgpath+outImgname);
                    //创建输出流
                    FileOutputStream outStream = new FileOutputStream(imageFile);
                    //写入数据
                    outStream.write(imgContent);
                    //关闭输出流
                    outStream.close();
                }
            }
        }catch (Exception e){
            logger.error("threadno:" + threadno + " Multimedia-处理商机图片异常，bcid:" + bcid + ",bimid:" + bm.getId() + "," +e.getMessage());
        }
    }


    /**
     * 处理图片
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
                    //new一个文件对象用来保存图片，默认保存当前工程根目录
                    String outImgpath="/tmp/whc/multimedia";
                    String outImgname=bcid+"_"+index+filename.substring(filename.lastIndexOf("."));
                    File imageFile = new File(outImgpath+outImgname);
                    //创建输出流
                    FileOutputStream outStream = new FileOutputStream(imageFile);
                    //写入数据
                    outStream.write(imgContent);
                    //关闭输出流
                    outStream.close();
                }
            }
        }catch (Exception e){
            logger.error("threadno:" + threadno + " Multimedia-处理商机图片异常，bcid:" + bcid + ",bimid:" + bm.getId() + "," +e.getMessage());
        }
    }

    /**
     * 批量修改商机
     * @param threadno
     * @param businDealList
     */
    private void batchUpdateBusinHistoryInfo(int threadno ,List<Map<String,Integer>> businDealList) {
        try {
            StatDao statDao = StatDao.getInstance(false);
            statDao.batchUpdateBusinHistoryInfo(businDealList);
            logger.info("threadno:" + threadno + "Multimedia-批量修改商机成功，修改了" + businDealList.size() + "商机");
        }catch (Exception e) {
            logger.error("threadno:" + threadno + "Multimedia-批量修改商机失败,"+e.getMessage());
        }
    }

}
