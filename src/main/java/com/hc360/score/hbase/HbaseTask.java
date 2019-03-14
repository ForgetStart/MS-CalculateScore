/**
 * 
 */
package com.hc360.score.hbase;

import java.util.Map;

import org.apache.hadoop.hbase.client.HTableInterface;

import com.hc360.storm.hbase.JsonUtils;

/**
 * @author andy
 *
 */
public class HbaseTask implements Runnable {

	int team;
	HTableInterface table;
	
	public HbaseTask(HTableInterface table,int i){
		this.table = table;
		this.team = i;
	}
	
	@Override
	public void run() {
		long startall = System.currentTimeMillis();
		try{
			for(int i=0;i<2500;i++){
				long start = System.currentTimeMillis();
				String info = "IM_ARCHIVE{\"collection_owner\":\"hcxiemu@hcim.b2b.hc360.com\",\"collection_with\":\"jinjiangjiayi@hcim.b2b.hc360.com\",\"ip\":\"127.0.0.1\",\"message\":\"hc360hc360hc360\",\"time\":\"" +
						System.currentTimeMillis() +
						"\",\"type\":\"char\",\"uuid\":\"5977baa3f8ce0ca1c96d6ac9a40c9a91\",\"version\":\"2.8\"}";
				Object object = JsonUtils.changeJson(info);
				//对象主键
				String rowkey = HbaseUtils.getRowKey(object);
				//得到表名
				String tablename = HbaseUtils.getTablenameFromPO(object);
				/**
				 * 将对象转为hbase语句
				 */
				Map<String,String> records =  HbaseUtils.getMapFromPO(object);
				for(String key:records.keySet()){
					try{
						//HbaseHelper.addData(HbaseHelper.getHtable(tablename), rowkey, key.split(":")[0],key.split(":")[1], records.get(key));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				long end = System.currentTimeMillis();
				////System.out.println(team+"-"+i+",send time="+(end-start));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		long endall = System.currentTimeMillis();
		//System.out.println(team+" all send time="+(endall-startall));
	}

}
