package com.hc360.score.utils.export.builder.domain;

/**
 * 条件实体类
 */
public class ExcelCondition {

	/**
	 * 条件名
	 */
	private String name;
	/**
	 * 条件值
	 */
	private String value;
	
	private short width;
	
	private short high;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
