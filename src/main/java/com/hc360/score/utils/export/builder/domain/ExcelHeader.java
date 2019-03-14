package com.hc360.score.utils.export.builder.domain;

public class ExcelHeader {

	/**
	 * Excelͷ��Ϣ
	 */
	private String name;
	/**
	 * ��Excel��Map�е�KEY
	 */
	private String column;
	
	private short width;
	
	private short high;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String toString(){
		return "Header--name:"+this.name+",column:"+this.column+"\r\n";
	}
	public short getWidth() {
		return width;
	}
	public void setWidth(short width) {
		this.width = width;
	}
	public short getHigh() {
		return high;
	}
	public void setHigh(short high) {
		this.high = high;
	}
}
