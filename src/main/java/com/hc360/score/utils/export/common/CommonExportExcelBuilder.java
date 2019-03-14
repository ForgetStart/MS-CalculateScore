package com.hc360.score.utils.export.common;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.StringUtils;
import com.hc360.mmt.common.bean.Page;
import com.hc360.rsf.mmt.trademanage.export.domain.RSFExportExcelParam;
import com.hc360.score.utils.export.builder.domain.ExcelExport;
import com.hc360.score.utils.export.builder.domain.ExcelHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * 通用构建EXCEL
 * 
 * @project manage_backup
 * @author chenxinwei
 * @version 1.0
 * @date 2014年3月16日 下午4:39:15
 */
public class CommonExportExcelBuilder extends ExportExcelBuilder {
	Logger logger = LoggerFactory.getLogger(CommonExportExcelBuilder.class);
	
	public CommonExportExcelBuilder(List headList, RSFExportExcelParam param, ExcelExport beanExport) throws MmtException {
		super(headList, param, beanExport);
	}
	public CommonExportExcelBuilder(List headList, ExcelExport beanExport) throws MmtException {
		super(headList, new RSFExportExcelParam(), beanExport);
	}
    public CommonExportExcelBuilder(List conditionList, Integer conditionCellOnRow, List headList, RSFExportExcelParam param, ExcelExport beanExport) throws MmtException {
        super(conditionList,conditionCellOnRow, headList, param, beanExport);
    }
	@Override
	public void createSheetBody(List<Map> bodyList)  throws MmtException{
		
		for(Map map:bodyList){
			if (map == null) {
				break;
			}
			String numberLimit = this.beanExport.getNumberLimit();
			if(StringUtils.String2Long(numberLimit) > 0) {
				if(getRowNumber() - 1 > StringUtils.String2Long(numberLimit)) {
					break;
				}
			}
			createCellStyle();
			createRow();
			for(int i = 0; i < this.headList.size(); i++){
				ExcelHeader header = (ExcelHeader)this.headList.get(i);
				Object obj = map.get(header.getColumn()!=null?header.getColumn().toUpperCase():"");
				setCellValue(obj, header);
				
			}
		}

	}

	@Override
	public void createSheetFooter() {
		// TODO Auto-generated method stub

	}
	public void writerExportExcel() throws MmtException {
		getFileOutputStream();
		
		
	}
	public FileOutputStream getFileOutputStream()  throws MmtException {
		FileOutputStream fOut = null;
		try {
			String filePathString = getOutputFile();
			 //新建一输出文件流
			fOut = new FileOutputStream(filePathString);
			// 把相应的Excel 工作簿存盘
			write(fOut);
			
			fOut.flush();
			//--------------------------------------------------
			
		} catch (IOException e) {
			throw new MmtException("输出Excel失败！", e);
		} finally {
//			 操作结束，关闭文件
			try {fOut.close();} catch (Exception e) {}
			
		}
		return fOut;
	}
	/**
	 * 获取Excel输入流
	 */
	public FileInputStream getFileInputStream()  throws MmtException {
		FileInputStream fIn = null;
		try {
			String filePathString = getOutputFile();
			 //新建一输入文件流
			fIn = new FileInputStream(filePathString);
			
			
		} catch (IOException e) {
			throw new MmtException("获取Excel输入流失败！", e);
		} 
		return fIn;
	}
	public String getOutputFile() {
		
		StringBuffer nameString = new StringBuffer();
		if (this.beanExport != null && this.beanExport.getDirName() != null && !"".equals(this.beanExport.getDirName().trim())) {

			outputFile = this.beanExport.getDirName();
		}
		if (this.beanExport != null && this.beanExport.getName() != null && !"".equals(this.beanExport.getName().trim())) {
			nameString.append(this.beanExport.getName());
		}
		if (this.param != null && this.param.getExcelName() != null && !"".equals(this.param.getExcelName().trim())) {
			nameString.append(this.param.getExcelName());
		}
		if (nameString.length() == 0) {
			nameString.append("test");
		}
//		nameString.append(".xls");
		
		logger.info("EXCEL文件名称为：" + nameString.toString());
		
		return  this.outputFile + File.separator + nameString.toString();
	}

	
	/**
	 * 
	 * @author chenxinwei
	 * @version 1.0
	 * @date 2014年3月16日 下午5:02:34
	 * @see com.hc360.manage.common.util.export.common.ExportExcelBuilder#createSheetBody(com.hc360.mmt.common.bean.Page)
	 */
	@Override
	public void createSheetBody(Page page) throws MmtException {
		List list = page.getLstResult();
        for (Object o:list) {
            createRow();
            for(int j = 0; j < this.headList.size(); j++){
                ExcelHeader header = (ExcelHeader)this.headList.get(j);
                Object obj = getBodyValue(o, header);
                setCellValue(obj, header);
            }

        }

		/*for (int i=0; i<list.size(); i++) {
			Object o = list.get(i);
			row = createRow();
			for(int j = 0; j < this.headList.size(); j++){
				ExcelHeader header = (ExcelHeader)this.headList.get(j);
				Object obj = getBodyValue(o, header);
				setCellValue(obj, header);

			}

		}*/
	}

	@Override
	public void createSheetBodyNextRow(Page page) throws MmtException {
		List list = page.getLstResult();
		for (Object o : list) {
			createRow();
			for (int j = 0; j < this.headList.size(); j++) {
				ExcelHeader header = (ExcelHeader) this.headList.get(j);
				Object obj = getBodyValue(o, header);
				setCellValue(obj, header);
			}

		}
	}
	
	/**
	 * 
	 * @author chenxinwei
	 * @version 1.0
	 * @date 2014年3月16日 下午5:54:17
	 * @param o
	 * @return 
	 * Object
	 */
	
	private Object getBodyValue(Object o, ExcelHeader header) {
		Object obj = null;
		try {
			obj = o.getClass().getMethod(getMethodName(header.getColumn()!=null?header.getColumn():""), null).invoke(o, null);
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * 
	 * @author chenxinwei
	 * @version 1.0
	 * @date 2014年3月16日 下午5:45:03
	 * @param string
	 * @return 
	 * String
	 */
	
	private String getMethodName(String field) {
		if (field!=null && !"".equals(field)) {
			return "get" + field.substring(0,1).toUpperCase() + field.substring(1);
		}
		return null;
	}
	
	
	

	
	

}
