package com.hc360.score.utils.export.common;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.Convert;
import com.hc360.mmt.common.bean.Page;
import com.hc360.rsf.mmt.trademanage.export.domain.RSFExportExcelParam;
import com.hc360.score.utils.export.builder.Excel07WorkbookBuilder;
import com.hc360.score.utils.export.builder.domain.ExcelCondition;
import com.hc360.score.utils.export.builder.domain.ExcelExport;
import com.hc360.score.utils.export.builder.domain.ExcelHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 业务抽象构建EXCEL文档
 * 
 * @project manage_backup
 * @author chenxinwei
 * @version 1.0
 * @date 2014年3月16日 下午4:38:17
 */
public abstract class ExportExcelBuilder extends Excel07WorkbookBuilder {
	/** Excel 文件要存放的位置，假定在D盘下 */
	public static String outputFile = "d:\\";

    protected List<ExcelCondition> conditionList;
    protected Integer conditionCellOnRow;
	protected List<ExcelHeader> headList;
	protected RSFExportExcelParam param;
	protected ExcelExport beanExport;
	
	Logger logger = LoggerFactory.getLogger(ExportExcelBuilder.class);
	
	public ExportExcelBuilder(List<ExcelHeader> headList, RSFExportExcelParam param, ExcelExport beanExport)  throws MmtException{
		this.headList = headList;
		this.param = param;
		this.beanExport = beanExport;
		setColumnNumber(headList.size());
	}

    public ExportExcelBuilder(List<ExcelCondition> conditionList,Integer conditionCellOnRow,List<ExcelHeader> headList, RSFExportExcelParam param, ExcelExport beanExport)  throws MmtException{
        this.conditionList = conditionList;
        this.conditionCellOnRow=conditionCellOnRow;
        this.headList = headList;
        this.param = param;
        this.beanExport = beanExport;
        setColumnNumber(headList.size());
    }


	/**
	 * 生成excel工作簿
	 * function description
	 *
	 */
	public void createSheet() throws MmtException{
		createSheet(beanExport.getSheetName()!=null?beanExport.getSheetName():"test");
		
	}

    /**
     * 生成excel条件
     * @throws com.hc360.b2b.exception.MmtException
     */
    public void createSheetCondition() throws MmtException{
        createCellStyle();

        createSheetTitle();
        for(int conditionIndex=0;conditionIndex<conditionList.size();conditionIndex++){
            if(conditionIndex%conditionCellOnRow==0){
                createRow();
            }
            setCellValue(conditionList.get(conditionIndex).getName(), null);
            setCellValue(conditionList.get(conditionIndex).getValue(), null);
        }
        createRow();//条件与列头之间的空行
    }


	/**
	 * 生成excel列头
	 * function description
	 *
	 */
	public void createSheetHeader() throws MmtException{
		createCellStyle();

		createSheetTitle();
		createRow();
		for (ExcelHeader header : headList) {
//			HSSFCell cell = createCell();
			setCellValue(header.getName(), header);
		}

	}
	private void createSheetTitle()  throws MmtException{
		if (!Convert.isEmpty(param.getTitle())) {
			createRow();
//			HSSFCell cell = createCell();
			setCellValue(param.getTitle(), null);
		}
	}
	/**
	 * 生成excel主体
	 * function description
	 *
	 */
	public abstract void createSheetBody(List<Map> bodyList) throws MmtException;

	/**
	 * 生成excel主体
	 * function description
	 *
	 */
	/**
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date 2014年3月16日 下午4:59:10
	 * @param page
	 * void
	 */

	public abstract void createSheetBody(Page page) throws MmtException;

	/**
	 * 生成excel合计
	 * function description
	 *
	 */
	public abstract void createSheetFooter();
	/**
	 * 生成excel文档
	 * function description
	 *
	 * @throws Exception
	 */
	public void writerExportExcel() throws MmtException {
//		logger.debug(new String(getByte()));
	}
	public abstract FileInputStream getFileInputStream() throws MmtException;
	/**
	 * 生成excel文档获得excel字节码
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 26, 2013 3:30:35 PM
	 * @return
	 * @throws Exception byte[]
	 */
	public byte[] writerExportExcelBytes() throws MmtException {
		byte[] result = getByte();
		logger.debug(new String(result));
		return result;

	}
	/**
	 * 获得excel文件输出字节流
	 * function description
	 *
	 * @return
	 * @throws java.io.IOException
	 */
	public ByteArrayOutputStream getByteArrayOutputStream()  throws MmtException {
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			write(bos);
			bos.flush();
		} catch (IOException e) {
			throw new MmtException("获得excel文件输出字节流失败！", e);
		} finally {
//			 操作结束，关闭文件
			try {bos.close();} catch (Exception e) {}
		}
		return bos;
	}
	/**
	 * 获得excel文件byte数组
	 * function description
	 *
	 * @return
	 * @throws java.io.IOException
	 */
	public byte[] getByte()  throws MmtException {
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			write(bos);
			bos.flush();
		} catch (IOException e) {
			throw new MmtException("获得excel文件byte数组失败！", e); 
		} finally {
//			 操作结束，关闭文件
			try {bos.close();} catch (Exception e) {}
		}
		return bos.toByteArray();
	}
	/**
	 * 获得导出excel文件名称
	 * 
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 26, 2013 3:16:34 PM
	 * @return String
	 */
	public String getOutputFileName() {
		
		StringBuffer nameString = new StringBuffer();
		if (this.beanExport != null && this.beanExport.getDirName() != null && !"".equals(this.beanExport.getDirName().trim())) {

			outputFile = this.beanExport.getDirName();
		}
		//xml中定义的文件名
		if (this.beanExport != null && this.beanExport.getName() != null && !"".equals(this.beanExport.getName().trim())) {
			nameString.append(this.beanExport.getName());
		}
		//传参中定义的文件名
		if (nameString.length() == 0 && this.param != null && this.param.getExcelName() != null && !"".equals(this.param.getExcelName().trim())) {
			nameString.append(this.param.getExcelName());
		}
		
		if (nameString.length() == 0) {
			nameString.append("test01");
			logger.error("xml中未定义Excel文件名，并且传参中未定义excelname内容");
		}
//		nameString.append(".xls");
		
		logger.info("EXCEL文件名称为：" + nameString.toString());
		
		return outputFile + nameString.toString();
	}
	public ExcelExport getBeanExport() {
		return beanExport;
	}
	public void setBeanExport(ExcelExport beanExport) {
		this.beanExport = beanExport;
	}
	
	public List getHeadList() {
		return headList;
	}
	public void setHeadList(List headList) {
		this.headList = headList;
	}
	public RSFExportExcelParam getParam() {
		return param;
	}
	public void setParam(RSFExportExcelParam request) {
		this.param = param;
	}
	public abstract void createSheetBodyNextRow(Page page) throws MmtException;
	
	
}
