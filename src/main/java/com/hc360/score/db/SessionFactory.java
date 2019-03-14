/**
 * 
 */
package com.hc360.score.db;


import java.io.File;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import com.hc360.b2b.exception.MmtException;
import com.hc360.mmt.db.dao.util.MmtSessionBean;
import com.hc360.mmt.db.trace.SessionProxy;
import com.hc360.mmt.db.trace.TraceSwitch;
import com.hc360.mmt.j2ee.SessionInfo;

/**
 * �Ự����
 * @author andy
 *
 */
public class SessionFactory {
	/** �����ݿ�Hibernate�������ö��� */
	private static final Map<String, org.hibernate.SessionFactory> factorys = new HashMap<String, org.hibernate.SessionFactory>();
	/** ���̴߳洢ʹ�õĿ����Ӷ��� */
	private static final ThreadLocal<Map<String, MmtSessionBean>> sessionLocal = new ThreadLocal<Map<String, MmtSessionBean>>();
	
	/**
	 * ȡ������ͨ��Ʒ��DB�Ự����
	 * @param dbServer DB��
	 * @param dbSchema DB�ƻ���
	 * @return DB��������
	 * @throws MmtException ȡ��DB�Ựʱ�����쳣
	 */
	public static MmtSessionBean openSession(String dbconfig) throws MmtException {

		//ȡ�ñ���DB�Ự�߳�������ʹ�õ�DB�Ự���󼯺�
		Map<String, MmtSessionBean> sessions = sessionLocal.get();
		if (sessions == null) {
			sessions = new HashMap<String, MmtSessionBean>();
			sessionLocal.set(sessions);
		}
		
		//ȡ�ûỰBean�������װBean
		MmtSessionBean sessionBean = (sessions.containsKey(dbconfig)) 
                                   ? sessions.get(dbconfig) : null;
        if (sessionBean == null) {
        	sessionBean = new MmtSessionBean();
        	sessions.put(dbconfig, sessionBean);
        }
        
        //ȡ��ָ��DB���ͼƻ�������Ӧ��DB�Ự����
		Session s = sessionBean.getSession();
        if ((s == null) || (!s.isConnected()) || (!s.isOpen())) {
        	sessionBean.setTransaction(null);
        	//shiwenfeng add(2012-12-25)
        	sessionBean.setDbServer(dbconfig);
        	sessionBean.setSession(buildSession(dbconfig));
        	System.out.println("open session============"+sessionBean.getSession().hashCode());
        }
		
		return sessionBean;
	}
	
	/**
	 * �����ӳ���ȡ��ָ��DB�ĻỰ����
	 * <p>dixingxing 20130131 �� <font color="red">�˷����ᴴ���µ�connection��session��
	 * Ҳ���Ǵ������µ����񣬲�Ҫ���׵��ô˷���������ʹ�� {@link #openSession(String, String)} </font></p>
	 * @param dbServer DB������
	 * @param dbSchema DB�û���
	 * @return DB�Ự����
	 * @throws MmtException ȡ��DB�Ự����ʱ�����쳣
	 */
	public static Session buildSession(String dbconfig) throws MmtException {

		//���Hibernateδ���ظ�DB����������DB����
		if (!factorys.containsKey(dbconfig)) {
			buildFactory(dbconfig);
		}
		
		// ------------------- shiwenfeng modify (2012-12-25) --------------
		/*if (SystemDecoupling.isDeouplingStatus()) {
			String jndiSuffixName = getJndiSuffix(dbServer);
			if (ConnectionConfigUtils.getConfigByURL(jndiSuffixName) != null) {
				ConnectionHelper.validateConnection(jndiSuffixName);
			}
		}*/
		// ------------------- modify end -----------------------
		
		
		//��Hibernate������ȡ��DB�Ự����
		Session session = factorys.get(dbconfig).openSession();
		
		//add by sunbaoming �������ܸ��ٵĴ��룬����Session�Ĵ���
		if(TraceSwitch.TRACE_ENABLED){
			SessionProxy proxy=new SessionProxy((org.hibernate.Session)session);
			session =(org.hibernate.Session) Proxy.newProxyInstance(
					org.hibernate.Session.class.getClassLoader(), 
					new Class[]{org.hibernate.Session.class}, proxy);
		}
		
		return session;
	}
	
	/**
	 * ���������DB����
	 * @param dbServer DB������
	 * @param dbSchema DB�û���
	 * @throws MmtException ���������DB����ʱ�����쳣
	 */
	private static synchronized void buildFactory(String dbconfig) throws MmtException {
		
		if (factorys.containsKey(dbconfig)) return;

		
//		Configuration conf = new Configuration().configure(SessionFactory.class.getResource(dbconfig));
		Configuration conf = new Configuration();
		conf.configure(new File(dbconfig));
		
		//if (dbSchema != null) conf.setProperty(HIBERNATE_DEFAULT_SCHEMA, schema);
		
		org.hibernate.SessionFactory factory = conf.buildSessionFactory();
		factorys.put(dbconfig, factory);
	}
	
	/**
	 * �ع���ǰ�߳������Ѿ�������DB����
	 * @throws MmtException �ع�����ʱ�����쳣
	 */
	public static void rollbackAllTransaction() throws MmtException {
		Map<String, MmtSessionBean> sessions = sessionLocal.get();
		if (sessions == null) {
			return;
		}
		
		Iterator<String> iter = sessions.keySet().iterator();
		boolean rollbackFlg = false;
		while (iter.hasNext()) {
			MmtSessionBean sessionBean = sessions.get(iter.next());
			Session s = sessionBean.getSession();
			try {
				if (s != null && s.isOpen()) {
					if (sessionBean.getTransaction() != null) {
						rollbackFlg = true;
						sessionBean.getTransaction().rollback();
						sessionBean.setTransaction(null);
					}
				}
			} catch (HibernateException e) {
				continue;
			}
		}
		
	}
	
	/**
	 * �ύ��ǰ�߳������Ѿ�������DB����
	 * @throws MmtException �ύ����ʱ�����쳣
	 */
	public static void commitAllTransaction() throws MmtException {
		Map<String, MmtSessionBean> sessions = sessionLocal.get();
		if (sessions == null) {
			return;
		}
		
		Iterator<String> iter = sessions.keySet().iterator();
		boolean commitFlg = false;
		while (iter.hasNext()) {
			MmtSessionBean sessionBean = sessions.get(iter.next());
			commitFlg = commit(sessionBean);
		}
	}
	
	public static boolean commit(MmtSessionBean sessionBean) throws MmtException {
		boolean commitFlg = false;
		Session s = sessionBean.getSession();
		try {
			//�жϵ�ǰ����ģʽ�Ƿ�ֻ��
			if(!SessionInfo.isReadOnly()){
				if (s != null && s.isOpen()) {
					if (sessionBean.getTransaction() != null) {
						commitFlg = true;
						sessionBean.getTransaction().commit();
						sessionBean.setTransaction(null);
					}
				}
			}
			
		} catch (HibernateException e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
		}
		
		return commitFlg;
	}
	
	/**
	 * �رյ�ǰ�߳�����DB�Ự����
	 * @throws MmtException �ر�DB�Ự����ʱ�����쳣
	 */
	public static void releaseAllSession() throws MmtException {
		Map<String, MmtSessionBean> sessions = sessionLocal.get();
		if (sessions == null) {
			System.out.println("close session============null");
			return;
		}
		
		Iterator<String> iter = sessions.keySet().iterator();
		boolean releaseFlg = false;
		while (iter.hasNext()) {
			MmtSessionBean sessionBean = sessions.get(iter.next());
			Session s = sessionBean.getSession();
			if (s != null && s.isOpen()) {
				System.out.println("close session============"+s.hashCode());
				releaseFlg = true;
				s.close();
			}
		}

		sessionLocal.remove();
	}
}
