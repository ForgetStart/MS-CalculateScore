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
 * hbase������
 * @author andy
 *
 */
public class HbaseUtils {
	
	/**
	 * ����PO��Ӧ�ı�����Сд��
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
	 * ����PO
	 * @param record<columnfamily:column,value>
	 * @return
	 */
	public static Object getPOFromMap(String tablename,String rowkeyvalue,Map<String,String> record){
		if(tablename==null || rowkeyvalue==null || record==null){
			return null;
		}
		try{
			//�ȴ�������
			HbaseMap map = MapUtils.getHbaseMapFromTablename(tablename);
			Object object = Class.forName(map.getClassname()).newInstance();
			//������ֵ
			
			Field field = object.getClass().getDeclaredField(map.getKeyparam());
			field.set(object,rowkeyvalue);
			
			//������ֵ
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
	 * ����<coulmnfamilly:column,value>
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
		 * ѭ����������
		 */
		for(String param:hbasemap.params.keySet()){
			//����ֵ
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
	 * �õ���������
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
