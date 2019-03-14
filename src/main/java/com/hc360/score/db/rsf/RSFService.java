package com.hc360.score.db.rsf;

import com.hc360.rsf.config.ConfigLoader;
import com.hc360.rsf.imgup.FileStorageService;
import com.hc360.rsf.imgup.FileStorageService2WH;
import com.hc360.rsf.kvdb.service.KVDBResult;
import com.hc360.rsf.kvdb.service.KVDBbcService;

public class RSFService {
	
	public static ConfigLoader configLoader = null;
	static{
		String xmlPath = "classpath:rsfclient.xml";
		configLoader = new ConfigLoader(xmlPath);
		configLoader.start();
	}
	
	public static KVDBbcService getKvdbbcService(){
        return (KVDBbcService) configLoader.getServiceProxyBean("kvdbService");//�����ļ��е�id
    }

    public static FileStorageService getFilestorageService(){
        return (FileStorageService) configLoader.getServiceProxyBean("fileStorageService");//�����ļ��е�id
    }

    public static FileStorageService2WH getFilestorageService2WH(){
        return (FileStorageService2WH) configLoader.getServiceProxyBean("fileStorageService2WH");//�����ļ��е�id
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println(1);
			KVDBResult result = RSFService.getKvdbbcService().getOn("CalculateScore", "12121");
			if(result.getState() == 0){
				System.out.println(new String(result.getValue()));
			}else{
				System.out.println("��ȡʧ�ܣ�"+result.getExType()+"-----"+result.getExReason());
			}

			System.out.println(2);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
