package com.hc360.score;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lowagie.text.Image;
import org.apache.activemq.command.ActiveMQDestination;

import com.hc360.jms.JMSProducer;
import com.hc360.jms.activemq.ActiveMQ;

public class SendMessageTest {

	//oper 操作类型，0新发待审，1新发免审，2修改待审，3修改免审，4未过期重发，5过期重发，6转过期，7删除，8审核通过，9拒审
	public static void send(){
		try {
			int num  = 1;
			int userid = 8133034;
			int bcid = 30494343;
			int[] bcids = new int[]{47402409,47402375,47403120,47401062,47399267,47398868,30494438,30494439,30494455,1212121};
			
			JMSProducer jmsProducer = ActiveMQ.createProducer("CalculateScoreConsumer","storm.business.queue.s",ActiveMQDestination.QUEUE_TYPE);
			for(int i=0;i<num;i++){
				bcid = bcids[i];
				String message = "{\"@type\":\"com.hc360.bcs.bo.BusinInfo\",\"bcid\":\""+bcid
						+"\",\"detailAmount\":2,\"detailImageAmount\":0,\"firstImageType\":2,\"groupId\":0,\"hA\":false,\"hasImage\":true,\"hasMinOrderNum\":true,\"hasNum\":true,\"hasOtherTitleDetail\":2,\"imageAmount\":1,\"noRequiredParam\":6," +
						"\"oper\":3,\"priceType\":true,\"userParamAmount\":0,\"userid\":"+userid+",\"validate\":\"2035-08-26 15:37:37\"}";
				boolean ret = jmsProducer.synchSend(message);
				System.out.println(ret);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		//send();
		testMap();
	}

	private static void testMap() throws Exception{
		send();
		/*Map<Integer,List<Long>> map = new HashMap<Integer,List<Long>>();
		map.put(1, Arrays.asList(1l,2l,3l));
		map.put(2, Arrays.asList(21l,22l,23l));
		map.put(3, Arrays.asList(31l,32l,33l));
		System.out.println(map);
		for(Map.Entry<Integer,List<Long>> kv:map.entrySet()){
			System.out.println(kv.getKey()+""+kv.getValue());
		}
        System.out.println("test");*/
//        for (int i=0;i<1000;i++){
//            Image img = Image.getInstance(new URL("http://img000.hc360.cn/m8/M08/20/DF/wKhQpVUghqKEHYByAAAAACoy1oE534.jpg"));
//            System.out.println("img.width="+img.width()+" img.hight="+img.height());
//        }


    }
}
