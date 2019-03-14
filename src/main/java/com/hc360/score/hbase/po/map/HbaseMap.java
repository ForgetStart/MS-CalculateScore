/**
 * 
 */
package com.hc360.score.hbase.po.map;

import java.util.HashMap;
import java.util.Map;

/**
 * HBASE对应关系
 * @author andy
 *
 */
public class HbaseMap {
	//表名
	private String tablename;
	//类名
	private String classname;
	//主键属性名
	private String keyparam;
	//主键列名
	private String keyrow;
	//列<param,columnfamily:column>
	public Map<String,String> params = new HashMap<String,String>();

	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
	public String getClassname() {
		return classname;
	}
	public void setClassname(String classname) {
		this.classname = classname;
	}
	public String getKeyparam() {
		return keyparam;
	}
	public void setKeyparam(String keyparam) {
		this.keyparam = keyparam;
	}
	public String getKeyrow() {
		return keyrow;
	}
	public void setKeyrow(String keyrow) {
		this.keyrow = keyrow;
	}
}
