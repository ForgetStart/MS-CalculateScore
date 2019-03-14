/**
 * 
 */
package com.hc360.score.hbase.po.map;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * po和table对应关系工具类
 * @author andy
 *
 */
public class MapUtils {
	public static final String HBASEMAP = "hbase-map.xml";
	/**
	 * element属性枚举
	 * @author andy
	 *
	 */
	private static enum BeanElement {
		name, key, param;    
    }
	
	public static List<HbaseMap> maps = new ArrayList<HbaseMap>();
	
	static{
		InputStream mapstream = null;
		try{
			mapstream = MapUtils.class.getResourceAsStream(HBASEMAP);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(mapstream);
			Element root = doc.getRootElement();
			List<Element> childs = root.getChildren();
			InputStream xmlstream = null;
			for(Element element:childs){
				HbaseMap hbasemap = new HbaseMap();
				String xmlname = element.getText();
				try{
					xmlstream = MapUtils.class.getResourceAsStream(xmlname);
					doc = builder.build(xmlstream);
					root = doc.getRootElement();
					List<Element> cs = root.getChildren();
					for(Element e:cs){
						if(e.getName().equals(BeanElement.name.toString())){
							hbasemap.setClassname(e.getValue());
							hbasemap.setTablename(e.getAttributeValue("table"));
						}
						else if(e.getName().equals(BeanElement.key.toString())){
							hbasemap.setKeyparam(e.getValue());
							hbasemap.setKeyrow(e.getAttributeValue("rowkey"));
						}
						else if(e.getName().equals(BeanElement.param.toString())){
							hbasemap.params.put(e.getValue(), e.getAttributeValue("columnfamily")+":"+e.getAttributeValue("column"));
						}
					}
					maps.add(hbasemap);
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					try{
						if(xmlstream!=null){
							xmlstream.close();
						}
					}catch(Exception e){}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(mapstream!=null){
					mapstream.close();
				}
			}catch(Exception e){}
		}
	}
	
	/**
	 * 从对象获得map
	 * @param object
	 * @return
	 */
	public static HbaseMap getHbaseMapFromPO(Object object){
		if(object == null){
			return null;
		}
		for(HbaseMap map:maps){
			if(map.getClassname().equals(object.getClass().getName())){
				return map;
			}
		}
		return null;
	}
	
	/**
	 * 从tablename获得map
	 * @param tablename
	 * @return
	 */
	public static HbaseMap getHbaseMapFromTablename(String tablename){
		if(tablename==null){
			return null;
		}
		for(HbaseMap map:maps){
			if(map.getTablename().equals(tablename)){
				return map;
			}
		}
		return null;
	}

	
	/**
	 * 从column获得paramname
	 * @param columnname 格式"columnfamily:column"
	 * @return
	 */
	public static String getParamFromColumn(HbaseMap hbasemap,String columnname){
		if(hbasemap==null || columnname==null){
			return null;
		}
		for(String key:hbasemap.params.keySet()){
			if(hbasemap.params.get(key).equals(columnname)){
				return key;
			}
		}
		return null;
	}
}
