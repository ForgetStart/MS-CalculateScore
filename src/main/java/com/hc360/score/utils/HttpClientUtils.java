/**
 * HttpClientUtil.java   2016��5��24��
 * Copyright(c) 2000-2016 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.utils;

import net.sf.json.JSONObject;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * class_description
 * 
 * @author dongjian
 * @version 1.0
 * @date 2016��5��24��
 * @param
 * @result
 */
public class HttpClientUtils {
	public static void main(String arg[]) throws Exception {
		String url = "http://order.b2b.hc360.com/orderservice/qidian/user?openid=2FECEC7D7BA73EB3485BADB20C9D0103";
		StringBuffer params = new StringBuffer();

		String ret = doGet(url, "UTF-8").toString();
		System.out.println(ret);
	}

	/**
	 * httpClient��get����ʽ2
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String url, String charset) throws Exception {
		/*
		 * ʹ�� GetMethod ������һ�� URL ��Ӧ����ҳ,ʵ�ֲ���: 1:����һ�� HttpClinet ����������Ӧ�Ĳ�����
		 * 2:����һ�� GetMethod ����������Ӧ�Ĳ����� 3:�� HttpClinet ���ɵĶ�����ִ�� GetMethod ���ɵ�Get
		 * ������ 4:������Ӧ״̬�롣 5:����Ӧ���������� HTTP ��Ӧ���ݡ� 6:�ͷ����ӡ�
		 */
		/* 1 ���� HttpClinet �������ò��� */
		HttpClient httpClient = new HttpClient();
		// ���� Http ���ӳ�ʱΪ5��
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		/* 2 ���� GetMethod �������ò��� */
		GetMethod getMethod = new GetMethod(url);
		// ���� get ����ʱΪ 5 ��
		getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, 5000);
		// �����������Դ����õ���Ĭ�ϵ����Դ�����������
		getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
		String response = "";
		/* 3 ִ�� HTTP GET ���� */
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			/* 4 �жϷ��ʵ�״̬�� */
			if (statusCode != HttpStatus.SC_OK) {
				System.err.println("�������: " + getMethod.getStatusLine());
			}
			/* 5 ���� HTTP ��Ӧ���� */
			// HTTP��Ӧͷ����Ϣ������򵥴�ӡ
			Header[] headers = getMethod.getResponseHeaders();
			for (Header h : headers)
				System.out.println(h.getName() + "------------ " + h.getValue());
			// ��ȡ HTTP ��Ӧ���ݣ�����򵥴�ӡ��ҳ����
			byte[] responseBody = getMethod.getResponseBody();// ��ȡΪ�ֽ�����
			response = new String(responseBody, charset);
			System.out.println("----------response:" + response);
			// ��ȡΪ InputStream������ҳ������������ʱ���Ƽ�ʹ��
			// InputStream response = getMethod.getResponseBodyAsStream();
		} catch (HttpException e) {
			// �����������쳣��������Э�鲻�Ի��߷��ص�����������
			System.out.println("���������URL!");
			e.printStackTrace();
		} catch (IOException e) {
			// ���������쳣
			System.out.println("���������쳣!");
			e.printStackTrace();
		} finally {
			/* 6 .�ͷ����� */
			getMethod.releaseConnection();
		}
		return response;
	}

	/**
	 * post����
	 * 
	 * @param url
	 * @param json
	 * @return
	 */

	public static JSONObject doPost(String url, JSONObject json) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		JSONObject response = null;
		try {
			StringEntity s = new StringEntity(json.toString());
			s.setContentEncoding("UTF-8");
			s.setContentType("application/json");// ����json������Ҫ����contentType
			post.setEntity(s);
			HttpResponse res = client.execute(post);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				String result = EntityUtils.toString(res.getEntity());// ����json��ʽ��
				response = JSONObject.fromObject(result);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return response;
	}
}
