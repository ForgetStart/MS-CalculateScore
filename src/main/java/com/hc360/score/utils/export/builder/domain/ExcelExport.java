package com.hc360.score.utils.export.builder.domain;

import java.util.List;

public class ExcelExport {

	private String id;
	private String name;
	private String dirName;
	private String sheetName;
	private String numberLimit;
	private String portalId;
	private String isTitle;
	private String dataSource;
	private String sqlId;
	private String serviceClass;

	private List<ExcelHeader> headers;
	
	public ExcelExport(String sqlId) {
		this.sqlId = sqlId;
	}
	
	/**
	 * @param excelName
	 * @param sheetName
	 * @param dirName2
	 */
	public ExcelExport(String excelName, String sheetName, String dirName) {
		setName(excelName);
		setSheetName(sheetName);
		setDirName(dirName);
	}

	public String getSqlId() {
		return sqlId;
	}

	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}

	public String getPortalId() {
		return portalId;
	}
	public void setPortalId(String portalId) {
		this.portalId = portalId;
	}
	public String getIsTitle() {
		return isTitle;
	}
	public void setIsTitle(String isTitle) {
		this.isTitle = isTitle;
	}
	public List<ExcelHeader> getHeaders() {
		return headers;
	}
	public void setHeaders(List<ExcelHeader> headers) {
		this.headers = headers;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNumberLimit() {
		return numberLimit;
	}
	public void setNumberLimit(String numberLimit) {
		this.numberLimit = numberLimit;
	}
	public String getSheetName() {
		return sheetName;
	}
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	
	/**
	 * 根据查询数据条数获得剩余时间
	 * function description
	 * 
	 * @param bodyNumber
	 * @return
	 */
	public static long getLeavingTime(int bodyNumber) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("id:").append(this.id).append(",").append("\t");
		buffer.append("name:").append(this.name).append(",").append("\t");
		buffer.append("portalId:").append(this.portalId).append(",").append("\t");
		buffer.append("numberLimit:").append(this.numberLimit).append(",").append("\t");
		buffer.append("isTitle:").append(this.isTitle).append(",").append("\t");
		buffer.append("sheetName:").append(this.sheetName).append(",").append("\t");
		if(this.headers != null){
			for(ExcelHeader header:headers){
				buffer.append(header.toString());
			}
		}
		
		return buffer.toString();
	}
	

	public String getServiceClass() {
		return serviceClass;
	}

	public void setServiceClass(String serviceClass) {
		this.serviceClass = serviceClass;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
}
