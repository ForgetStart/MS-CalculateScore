/*
 * Copyright(c) 2000-2007 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.db.dao;

import com.hc360.b2b.exception.MmtException;
import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.db.po.statdb.*;
import com.hc360.score.db.StatDBSource;
import org.hibernate.SessionFactory;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.engine.SessionFactoryImplementor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatDao extends StatDBSource {

    private static StatDao masterInstance = new StatDao(false);
    private static StatDao slaveInstance = new StatDao(true);

    private StatDao(boolean isSlave) {
        super(isSlave);
    }

    public static StatDao getInstance(boolean isSlave){
        return isSlave ? slaveInstance : masterInstance;
    }

    /**
     * 分页查询BCID
     * @param pageBean
     * @param threadno
     * @return
     * @throws MmtException
     */
    public Page getBusinIntroduceCsinfoByPage(PageBean pageBean,int threadno) throws MmtException {
        String sql = "select ci.bc_id from busin_introduce_cs_info ci where (ci.states is null or ci.states=0) and ci.threadno=? order by ci.bc_id desc";
        //String sql = "select ci.bc_id from busin_introduce_cs_info ci where ci.states=0 and ci.threadno=?";
        List<Object> param=new ArrayList<Object>();
        param.add(threadno);
        return this.getPageQueryRawByJDBC(sql,param,pageBean);
    }

    /**
     * 修改详情信息
     * @param biCsinfo
     * @return
     * @throws MmtException
     */
    public boolean updateBusinIntroduceCsinfo(BusinIntroduceCsinfo biCsinfo) throws MmtException{
        super.update(biCsinfo);
        return true;
    }

    /**
     * 获取商机详情信息
     * @param bcid
     * @return
     * @throws MmtException
     */
    public BusinIntroduceCsinfo getBusinIntroduceCsinfo(long bcid,int threadno) throws MmtException{
        String sql = " from BusinIntroduceCsinfo p where p.bcid=:bcid and p.threadno=:threadno";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bcid", bcid);
        map.put("threadno", threadno);

        List rlist = super.query(sql, map);
        if (rlist != null && rlist.size() > 0){
            BusinIntroduceCsinfo  biCsinfo = (BusinIntroduceCsinfo)rlist.get(0);
            return biCsinfo;
        }else{
            return null;
        }
    }


    public void batchUpdateBusinIntroduceCsinfo(List<BusinIntroduceCsinfo> ciList) throws MmtException{
        Connection conn=null;
        PreparedStatement ps = null;
        try {
            SessionFactory sessionFactory= super.openSession().getSession().getSessionFactory();
            ConnectionProvider cp =((SessionFactoryImplementor)sessionFactory).getConnectionProvider();
            conn=cp.getConnection();
            ps = conn.prepareStatement("update busin_introduce_cs_info set pic=?,w100=?,w100_200=?,w200_300=?,w300_400=?,w400_500=?,w500_600=?,w600_700=?,w700_800=?,w800=?," +
                    "h100=?,h100_200=?,h200_300=?,h300_400=?,h400_500=?,h500_600=?,h600_700=?,h700_800=?,h800=?," +
                    "word_amount=?,states=?,encode=?,dealtime=sysdate where bc_id=? and threadno=?");
            conn.setAutoCommit(false);
            int size = ciList.size();
            BusinIntroduceCsinfo bici = null;
            for (int index = 0; index < size; index++) {
                bici = ciList.get(index);
                ps.setInt(1,bici.getPic());
                ps.setInt(2,bici.getW100());
                ps.setInt(3,bici.getW100_200());
                ps.setInt(4,bici.getW200_300());
                ps.setInt(5,bici.getW300_400());
                ps.setInt(6,bici.getW400_500());
                ps.setInt(7,bici.getW500_600());
                ps.setInt(8,bici.getW600_700());
                ps.setInt(9,bici.getW700_800());
                ps.setInt(10,bici.getW800());
                ps.setInt(11,bici.getH100());
                ps.setInt(12,bici.getH100_200());
                ps.setInt(13,bici.getH200_300());
                ps.setInt(14,bici.getH300_400());
                ps.setInt(15,bici.getH400_500());
                ps.setInt(16,bici.getH500_600());
                ps.setInt(17,bici.getH600_700());
                ps.setInt(18,bici.getH700_800());
                ps.setInt(19,bici.getH800());
                ps.setInt(20, bici.getWordAmount());
                ps.setString(21, bici.getStates());
//                ps.setString(22, bici.getEncode());
                ps.setLong(23, bici.getBcid());
                ps.setInt(24,bici.getThreadno());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit();
            //conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            /*try {
                if(conn!=null&&!conn.isClosed()){
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }*/
        } finally {
            try {
                if(ps!=null){
                    ps.close();
                    ps=null;
                }
                if(conn!=null){
                    conn.close();
                    conn=null;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取商机详情
     * @param bcid
     * @return
     * @throws MmtException
     */
    public BusinIntroduce getBusinIntroduce(long bcid) throws MmtException{
        String sql = " from BusinIntroduce bi where bi.bcid=:bcid";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bcid", bcid);

        List rlist = super.query(sql, map);
        if (rlist != null && rlist.size() > 0){
            BusinIntroduce  bi = (BusinIntroduce)rlist.get(0);
            return bi;
        }else{
            return null;
        }
    }

    /**
     * 保存详情
     * @param bi
     * @return
     * @throws MmtException
     */
    public boolean saveBusinIntroduce(BusinIntroduce bi) throws MmtException{
        super.save(bi);
        return true;
    }

    /**
     * 修改详情
     * @param bi
     * @return
     * @throws MmtException
     */
    public boolean updateBusinIntroduce(BusinIntroduce bi) throws MmtException{
        super.update(bi);
        return true;
    }


    /**
     * 获取商机详情信息
     * @param bcid
     * @return
     * @throws MmtException
     */
    public BusinIntroduceWHinfo getBusinIntroduceWHinfo(long bcid) throws MmtException{
        String sql = " from BusinIntroduceWHinfo p where p.bcid=:bcid";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bcid", bcid);

        List rlist = super.query(sql, map);
        if (rlist != null && rlist.size() > 0){
            BusinIntroduceWHinfo  biWHinfo = (BusinIntroduceWHinfo)rlist.get(0);
            return biWHinfo;
        }else{
            return null;
        }
    }

    /**
     * 修改详情信息
     * @param biWHinfo
     * @return
     * @throws MmtException
     */
    public boolean updateBusinIntroduceWHinfo(BusinIntroduceWHinfo biWHinfo) throws MmtException{
        super.update(biWHinfo);
        return true;
    }

    /**
     * 保存详情信息
     * @param biWHinfo
     * @return
     * @throws MmtException
     */
    public boolean saveBusinIntroduceWHinfo(BusinIntroduceWHinfo biWHinfo) throws MmtException{
        super.save(biWHinfo);
        return true;
    }

    /**
     * 获取商机详情信息任务
     * @param task
     * @return
     * @throws MmtException
     */
    public BusinDataTask getBusinIntroduceTask(String task) throws MmtException{
        String sql = " from BusinDataTask t where t.task=:task";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("task", task);

        List rlist = super.query(sql, map);
        if (rlist != null && rlist.size() > 0){
            BusinDataTask  bdt = (BusinDataTask)rlist.get(0);
            return bdt;
        }else{
            return null;
        }
    }

    /**
     * 修改商机详情信息任务
     * @param bdTask
     * @return
     * @throws MmtException
     */
    public boolean updateBusinIntroduceTask(BusinDataTask bdTask) throws MmtException{
        super.update(bdTask);
        return true;
    }

    /**
     * 保存商机详情信息任务
     * @param bdTask
     * @return
     * @throws MmtException
     */
    public boolean saveBusinIntroduceTask(BusinDataTask bdTask) throws MmtException{
        super.save(bdTask);
        return true;
    }

    /**
     * 删除商机参数
     * @param bcid
     * @return
     * @throws MmtException
     */
    public int deleteBusinAttValue(long bcid) throws MmtException{
        String sql = "delete from BusinAttValue t where t.bcid=:bcid";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bcid", bcid);
        int record = super.executeUpdate(sql,map);
        return record;
    }

    /**
     * 保存商机参数
     * @param bav
     * @return
     * @throws MmtException
     */
    public boolean saveBusinAttValue(BusinAttValue bav) throws MmtException{
        super.save(bav);
        return true;
    }

    /**
     * 分页查询BCID
     * @param pageBean
     * @param threadno
     * @return
     * @throws MmtException
     */
    public Page getBusinHistoryInfoByPage(PageBean pageBean,String isdo,int threadno) throws MmtException {
        String sql = "select bc_id,isdo from tmp_fyp_whc where isdo in ("+isdo+")";
        return this.getPageQueryRawByJDBC(sql,pageBean);
    }

    /**
     * 批量修改
     * @param businDealList
     * @throws MmtException
     */
    public void batchUpdateBusinHistoryInfo(List<Map<String,Integer>> businDealList) throws MmtException{
        Connection conn=null;
        PreparedStatement ps = null;
        try {
            SessionFactory sessionFactory= super.openSession().getSession().getSessionFactory();
            ConnectionProvider cp =((SessionFactoryImplementor)sessionFactory).getConnectionProvider();
            conn=cp.getConnection();
            ps = conn.prepareStatement("update tmp_fyp_whc set isdo=? where bc_id=?");
            conn.setAutoCommit(false);
            if(businDealList!=null&&businDealList.size()>0){
                for (int index = 0; index < businDealList.size(); index++) {
                    Map<String,Integer> businDealMap = businDealList.get(index);
                    ps.setInt(1, businDealMap.get("isdo"));
                    ps.setInt(2, businDealMap.get("bc_id"));
                    ps.addBatch();
                }

                ps.executeBatch();
                conn.commit();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(ps!=null){
                    ps.close();
                    ps=null;
                }
                if(conn!=null){
                    conn.close();
                    conn=null;
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}