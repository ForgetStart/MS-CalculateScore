/**
 * 
 */
package com.hc360.score.hbase.po.map;

import java.util.HashMap;
import java.util.Map;

/**
 * HBASE��Ӧ��ϵ
 * @author andy
 *
 */
public class HbaseMap {
	//����
	private String tablename;
	//����
	private String classname;
	//����������
	private String keyparam;
	//��������
	private String keyrow;
	//��<param,columnfamily:column>
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
