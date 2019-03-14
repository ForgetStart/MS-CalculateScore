package com.hc360.score.utils.export;

import com.hc360.b2b.exception.MmtException;
import com.hc360.mmt.common.bean.Page;
import com.hc360.score.utils.export.common.ExportExcelBuilder;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

/**
 * Excel文档构建类
 * 
 * @project trademanage
 * @author chenxinwei
 * @version 1.0
 * @date Feb 26, 2013 3:04:35 PM
 */

public class ExportExcelDirector {

	private ExportExcelBuilder builder;
	private boolean is2007Excel = false;
	public ExportExcelDirector(ExportExcelBuilder builder) {
		this.builder = builder;
	}
	public ExportExcelDirector(ExportExcelBuilder builder, boolean is2007Excel) {
		this.is2007Excel = is2007Excel;
		this.builder = builder;
	}
	public void constructToppingInfo() throws MmtException {
		builder.setIs2007Excel(is2007Excel);
		builder.createSheet();
		builder.createSheetHeader();
		
	}

    public void constructToppingInfoIncludeCondition() throws MmtException {
        builder.setIs2007Excel(is2007Excel);
        builder.createSheet();
        builder.createSheetCondition();
        builder.createSheetHeader();

    }

	public void constructBodyInfo(List<Map> bodyList) throws MmtException {
		builder.setIs2007Excel(is2007Excel);
		builder.createSheetBody(bodyList);
	}

	
	public void constructBodyInfo(Page page) throws MmtException {
		builder.setIs2007Excel(is2007Excel);
		builder.createSheetBody(page);
		
	}
	public void constructBodyNextRowInfo(Page page) throws MmtException{
		builder.setIs2007Excel(is2007Excel);
		builder.createSheetBodyNextRow(page);
		
	}
	
	public void write() throws MmtException {
		builder.writerExportExcel();
	}
	public FileInputStream getFileInputStream() throws MmtException {
		return builder.getFileInputStream();
	}
	public byte[] writeToBytes() throws Exception {
		return builder.writerExportExcelBytes();
	}
	/*public Workbook getWorkbook(){
		return builder.getWorkbook();
	}*/
	public ByteArrayOutputStream getByteArrayOutputStream() throws MmtException{
		return builder.getByteArrayOutputStream();
	}
	public byte[] getByte() throws MmtException{
		return builder.getByte();
	}
	/*public static void main(String[] args) throws Exception {
		List<ExcelHeader> headList = new ArrayList<ExcelHeader>();
		ExcelHeader header = new ExcelHeader();
		header.setName("12");
		header.setColumn("22");
		headList.add(header);
		header = new ExcelHeader();
		header.setName("23");
		header.setColumn("33");
		headList.add(header);
		List<Map> bodyList = new ArrayList<Map>();
		RSFExportExcelParam param = new RSFExportExcelParam();
		ExcelExport beanExport = new ExcelExport(null);
		beanExport.setSheetName("aaaa");
		ExportExcelBuilder builder = new CommonExportExcelBuilder(headList, param, beanExport);
		ExportExcelDirector director = new ExportExcelDirector(builder);
		director.constructToppingInfo();
		director.constructBodyInfo(bodyList);
//		log.debug(director.getByteArrayOutputStream());
		director.write();
	}*/
	
}
