package com.hc360.score.utils;

import com.hc360.score.utils.export.builder.domain.ExcelHeader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * ��Ʒ����EXCEL������
 * Created by wanghuacun on 2015/7/8.
 */
public class ExportExcelUtil {

    /**
     * �����ļ���
     * @param dir
     */
    public static void isExistsFile(String dir) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * �жϵ��յ��ļ��Ƿ���ڣ�ɾ����ǰ�ļ�
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
     * ���Զ�Ӧ
     * @author chenxinwei
     * @version 1.0
     * @date 2014��3��16�� ����4:53:10
     * @return
     * List
     */

    public static List<ExcelHeader> getExcelHeadList4BusinIntro() {
        List<ExcelHeader> headList = new ArrayList<ExcelHeader>();
        ExcelHeader header = new ExcelHeader();
        header.setName("�̻�ID");
        header.setColumn("BC_ID");
        headList.add(header);
        header = new ExcelHeader();
        header.setName("�̻�����");
        header.setColumn("INTRODUCE");
        headList.add(header);
        return headList;
    }

}
