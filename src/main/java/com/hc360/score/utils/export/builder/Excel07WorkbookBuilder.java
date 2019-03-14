package com.hc360.score.utils.export.builder;

import com.hc360.b2b.exception.MmtException;
import com.hc360.b2b.util.DateUtils;
import com.hc360.score.utils.export.builder.domain.ExcelHeader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;

/**
 * ����EXCEL�ĵ�
 * 
 * @project manage_backup
 * @author chenxinwei
 * @version 1.0
 * @date 2014��3��16�� ����4:37:32
 */
public class Excel07WorkbookBuilder {

	Logger logger = LoggerFactory.getLogger(Excel07WorkbookBuilder.class);
	private Workbook workbook;
	private Sheet sheet;
	private CellStyle cellStyle;
	private Row row;
	private int rowNumber = 0;
	private short cellNumber = 0;
	private short high = 30;
	private short width = 5000;
	private int columnNumber = 0;
	private InputStream inputStream;//��дExcel������
	private int now_sheet = 0;
	private final long MAX_SHEET_ROW_2003 = 60000L;//2003�����65536��
	private final long MAX_SHEET_ROW_2007 = 1000000L;//2007�����1048576��
	private final long CLEAR_SHEET_ROW = 5000L;//�����ڴ�����
	private boolean is2007Excel = false;
	public Workbook createWorkbook() throws MmtException{
		return createWorkbook(this.is2007Excel);
	}
	public Workbook createWorkbook(boolean is2007Excel) throws MmtException{
		this.is2007Excel = is2007Excel;
		if(inputStream != null){//excel����
			try {
				workbook = getExcelWorkbook(inputStream);
			} catch (IOException e) {
				logger.error("��ȡExcelʧ�ܣ�", e);
				e.printStackTrace();
				throw new MmtException("��ȡExcelʧ�ܣ�", e);
			}
		} else {
			
			workbook = getExcelWorkbook();
		}
		return workbook;
	}
	/**
	 * ���Excel ������
	 * @return
	 * @throws java.io.IOException
	 */
	private Workbook getExcelWorkbook(InputStream inputStream) throws IOException{
		if(is2007Excel){
			return new SXSSFWorkbook(new XSSFWorkbook(inputStream), 100);
		} else {
			return new HSSFWorkbook(inputStream);
		}
	}
	/**
	 * ���Excel ������
	 * @return
	 */
	private Workbook getExcelWorkbook(){
		if(is2007Excel){
			return new SXSSFWorkbook(new XSSFWorkbook(), 100);
		} else {
			return new HSSFWorkbook();
		}
	}
	public Sheet createSheet(String name) throws MmtException {
		if (workbook == null) {
			createWorkbook();
		}
		if(inputStream != null){//excel����
			int sheetIdx = workbook.getNumberOfSheets();
			if (sheetIdx > 0) {
				now_sheet = sheetIdx;
			}

			System.out.println("excel����Sheet������" + sheetIdx);
			sheet = workbook.getSheetAt(sheetIdx-1);
			setNowRowNumber(sheet.getLastRowNum());

			if(this.rowNumber == getMaxSheetRow()) {
				sheet = workbook.createSheet(getNowSheetName());
				rowNumber = 0;
			}
		} else {

			sheet = workbook.createSheet(name);
		}
		if (width != 0 && columnNumber != 0) {
			setColumnWidth(sheet, columnNumber);
		}
		return sheet;
	}
	public CellStyle createCellStyle() throws MmtException{
		return this.createCellStyle(CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_BOLD);
	}

	public CellStyle createCellStyle(short align, short boldweight) throws MmtException{
		if (sheet == null) {
			createSheet(getNowSheetName());
		}


		if (cellStyle == null) {
			Font font = workbook.createFont();//��������
			// font.setColor(Font.COLOR_RED);
			font.setBoldweight(boldweight);
//			 ������ʽ
			cellStyle = workbook.createCellStyle();
			cellStyle.setFont(font);
			cellStyle.setAlignment(align);
		}

		return cellStyle;

	}

	public Row createRow()  throws MmtException{
		if (sheet == null) {
			createSheet(getNowSheetName());
		}
		row = sheet.createRow(getNewRowNumber());

		cellNumber = 0;
		return row;
	}
	private String getNowSheetName() {

		return "export sheet " + (this.now_sheet++);

	}
	private String getNowSheetName(String name) {

		return name + " " + (this.now_sheet++);

	}
	private void setNowRowNumber(int lastRowNum) throws MmtException {

		this.rowNumber = lastRowNum;
	}
	private int getNewRowNumber() throws MmtException {
		if(is2007Excel && this.rowNumber%CLEAR_SHEET_ROW == 0) {
			try {
				((SXSSFSheet)sheet).flushRows();
			} catch (IOException e) {
				logger.error("�����ڴ�ʧ�ܣ�", e);
				e.printStackTrace();
				throw new MmtException("�����ڴ�ʧ�ܣ�", e);
			}
		}
		if(this.rowNumber == getMaxSheetRow()) {
			sheet = workbook.createSheet(getNowSheetName());
			rowNumber = 0;
		}
		return rowNumber++;
	}
	/**
	 * ������excel����
	 * @return
	 */
	private long getMaxSheetRow(){
		if(is2007Excel){
			return MAX_SHEET_ROW_2007;
		} else {
			return MAX_SHEET_ROW_2003;
		}
	}
	public Cell createCell()  throws MmtException{
		return createCell(CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_NORMAL);
	}
	public Cell createCell(short align, short boldweight)  throws MmtException{
		if (row == null) {
			createRow();
		}
		Cell cell = row.createCell(cellNumber++);
		if (cellStyle == null) {
				createCellStyle(align, boldweight);
		}
		cell.setCellStyle(cellStyle);

		return cell;
	}
	/**
	 * ��Cell��ֵ
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 26, 2013 2:52:26 PM
	 * @param obj void
	 */
	public void setCellValue(Object obj)  throws MmtException{
		if (row == null) {
			createRow();
		}

		Cell cell = row.createCell(cellNumber++);
		setCellValue(cell, obj);
		if (obj == null) {
			logger.debug("����excel body��Ϣ����Ϊ��null" );
		} else {
			logger.debug("����excel body��Ϣ����Ϊ��" + obj.getClass()!=null?obj.getClass().toString():"class�ǿ�");
		}

	}
	/**
	 * ��Cell��ֵ
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 26, 2013 2:52:26 PM
	 * @param obj void
	 */
	public void setCellValue(Object obj, ExcelHeader header) throws MmtException {
		if (row == null) {
			createRow();
		}
		if (header != null && header.getHigh() != 0) {
			row.setHeight(header.getHigh());
		}
		Cell cell = row.createCell(cellNumber++);



		setCellValue(cell, obj);
		if (obj == null) {
			logger.debug("����excel body��Ϣ����Ϊ��null" );
		} else {
			logger.debug("����excel body��Ϣ����Ϊ��" + obj.getClass()!=null?obj.getClass().toString():"class�ǿ�");
		}

	}

	// ����Cell ���
	@SuppressWarnings("deprecation")
	private void setColumnWidth(Sheet sheet, Integer listsize) {
		for (int i = 0; i < listsize; i++) {
			sheet.setColumnWidth((short) i, width);
		}
	}

	/**
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date 2013-4-28 ����07:52:16
	 * @param cell
	 * @param obj void
	 */

	private void setCellValue(Cell cell, Object obj) throws MmtException {
		//cell.setEncoding((short) 1);
		if (obj == null) {
			cell.setCellValue("");
		}else if(obj instanceof Long){
			cell.setCellValue(Long.parseLong(obj.toString()));
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL));
		} else if(obj instanceof String) {
			if (cellStyle == null) {
				createCellStyle();
			}
			cellStyle.setWrapText(true);
			cell.setCellValue((String.valueOf(obj)));
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_NORMAL));
		} else if(obj instanceof Date) {
			cell.setCellValue(DateUtils.getString((Date)obj, DateUtils.DEF_DATE_TIME_FORMAT));
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL));
		} else if(obj instanceof BigDecimal) {//���Ƽ������Ȳ�׼ȷ
			cell.setCellValue(((BigDecimal)obj).doubleValue());
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL));
		} else if(obj instanceof Double) {
			cell.setCellValue(((Double)obj).doubleValue());
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_RIGHT, Font.BOLDWEIGHT_NORMAL));
		} else if(obj instanceof Boolean) {
			cell.setCellValue(((Boolean)obj).booleanValue());
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_CENTER, Font.BOLDWEIGHT_NORMAL));
		} else {
			cell.setCellValue(new String(obj.toString()));
			cell.setCellStyle(createCellStyle(CellStyle.ALIGN_LEFT, Font.BOLDWEIGHT_NORMAL));
		}
	}

	/**
	 * ��������������д��excel�ĵ���
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 23, 2013 10:31:22 AM
	 * @param fOut
	 * @throws java.io.IOException void
	 */
	public void write(FileOutputStream fOut)  throws MmtException {
		if (workbook == null) {
			createWorkbook();
		}
		try {
			workbook.write(fOut);
		} catch (IOException e) {
			logger.error("дExcelʧ�ܣ�", e);
			e.printStackTrace();
			throw new MmtException("дExcelʧ�ܣ�", e);
		}
	}
	/**
	 * ��������������д���ֽ�����
	 *
	 * @author chenxinwei
	 * @version 1.0
	 * @date Feb 23, 2013 10:42:33 AM
	 * @param bout
	 * @throws java.io.IOException void
	 */
	public void write(ByteArrayOutputStream bout) throws MmtException {
		if (workbook == null) {
			createWorkbook();
		}
		try {
			workbook.write(bout);
		} catch (IOException e) {
			logger.error("дExcelʧ�ܣ�", e);
			e.printStackTrace();
			throw new MmtException("дExcelʧ�ܣ�", e);
		}
	}
	public CellStyle getCellStyle() {
		return cellStyle;
	}

	public void setCellStyle(CellStyle cellStyle) {
		this.cellStyle = cellStyle;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public void setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
	}

	public Sheet getSheet() {
		return sheet;
	}
	public void setSheet(Sheet sheet) {
		this.sheet = sheet;
	}
	public Workbook getWorkbook() {
		return workbook;
	}
	public void setWorkbook(Workbook workbook) {
		this.workbook = workbook;
	}

	public int getCellNumber() {
		return cellNumber;
	}

	public void setCellNumber(short cellNumber) {
		this.cellNumber = cellNumber;
	}

	public short getHigh() {
		return high;
	}

	public void setHigh(short high) {
		this.high = high;
	}

	public short getWidth() {
		return width;
	}

	public void setWidth(short width) {
		this.width = width;
	}

	public Row getRow() {
		return row;
	}

	public void setRow(Row row) {
		this.row = row;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
	public InputStream getInputStream() {
		return inputStream;
	}
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	public boolean isIs2007Excel() {
		return is2007Excel;
	}
	public void setIs2007Excel(boolean is2007Excel) {
		this.is2007Excel = is2007Excel;
	}
}
