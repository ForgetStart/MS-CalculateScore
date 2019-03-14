package com.hc360.score.utils;

import com.hc360.score.utils.export.builder.domain.ExcelHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 产品导出EXCEL工具类
 * Created by wanghuacun on 2015/7/8.
 */
public class ExportExcelUtil {

    /**
     * 创建文件夹
     * @param dir
     */
    public static void isExistsFile(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * 判断当日的文件是否存在，删除以前文件
     * @param dirpath
     * @param filepath
     * @return
     */
    public static boolean isExistsLoadFile(String dirpath,String filepath){
        boolean flag=false;
        File file = new File(filepath);
        if(!file.exists()){
            File dir=new File(dirpath);
            if(dir.exists()&&dir.isDirectory()){
                File[] allFiles=dir.listFiles();
                if(allFiles!=null&&allFiles.length>0){
                    for(File oneFile:allFiles){
                        oneFile.delete();
                    }
                }
            }
        }else{
            flag=true;
        }
        return flag;
    }

    /**
     * 属性对应
     * @author chenxinwei
     * @version 1.0
     * @date 2014年3月16日 下午4:53:10
     * @return
     * List
     */

    public static List<ExcelHeader> getExcelHeadList4BusinIntro() {
        List<ExcelHeader> headList = new ArrayList<ExcelHeader>();
        ExcelHeader header = new ExcelHeader();
        header.setName("商机ID");
        header.setColumn("BC_ID");
        headList.add(header);
        header = new ExcelHeader();
        header.setName("商机详情");
        header.setColumn("INTRODUCE");
        headList.add(header);
        return headList;
    }

}
