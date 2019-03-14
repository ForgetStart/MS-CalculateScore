/**
 * 
 */
package com.hc360.score.hbase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.hc360.score.hbase.po.map.HbaseMap;
import com.hc360.score.hbase.po.map.MapUtils;

/**
 * hbase表工具类
 * @author andy
 *
 */
public class HbaseUtils {
	
	/**
	 * 返回PO对应的表名（小写）
	 * @param object
	 * @return
	 */
	public static String getTablenameFromPO(Object object){
		if(object == null){
			return null;
		}
		return object.getClass().getSimpleName().toLowerCase();
	}

	/**
	 * 返回PO
	 * @param record<columnfamily:column,value>
	 * @return
	 */
	public static Object getPOFromMap(String tablename,String rowkeyvalue,Map<String,String> record){
		if(tablename==null || rowkeyvalue==null || record==null){
			return null;
		}
		try{
			//先创建对象
			HbaseMap map = MapUtils.getHbaseMapFromTablename(tablename);
			Object object = Class.forName(map.getClassname()).newInstance();
			//主键赋值
			
			Field field = object.getClass().getDeclaredField(map.getKeyparam());
			field.set(object,rowkeyvalue);
			
			//给对象赋值
			for(String key:record.keySet()){
				String param = MapUtils.getParamFromColumn(map, key);
				field = object.getClass().getDeclaredField(param);
				field.set(object, record.get(key));
			}
			return object;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 返回<coulmnfamilly:column,value>
	 * @param object
	 * @return
	 */
	public static Map<String,String> getMapFromPO(Object object){
		if(object == null){
			return null;
		}
		HbaseMap hbasemap = MapUtils.getHbaseMapFromPO(object);
		Map<String,String> map = new HashMap<String,String>();
		/**
		 * 循环各个属性
		 */
		for(String param:hbasemap.params.keySet()){
			//属性值
			try{
				Field field = object.getClass().getDeclaredField(param);
				String column = hbasemap.params.get(param);
				map.put(column, (String)field.get(object));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return map;
	}
	
	/**
	 * 得到对象主键
	 * @return
	 */
	public static String getRowKey(Object object){
		HbaseMap map = MapUtils.getHbaseMapFromPO(object);
//		//System.out.println("getRowKey: map=" + map);
		if(map!=null){
			String keyparam = map.getKeyparam();
//			//System.out.println("keyparam=" + keyparam);
			try{
				Field field = object.getClass().getDeclaredField(keyparam);
//				//System.out.println("(String)field.get(object):" + (String)field.get(object));
				return (String)field.get(object);
			}catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}else{
			return null;
		}
		
	}
	
	
}
