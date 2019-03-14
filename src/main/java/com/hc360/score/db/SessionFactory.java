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
 * 会话管理
 * @author andy
 *
 */
public class SessionFactory {
	/** 各数据库Hibernate工厂引用对象 */
	private static final Map<String, org.hibernate.SessionFactory> factorys = new HashMap<String, org.hibernate.SessionFactory>();
	/** 按线程存储使用的库连接对象 */
	private static final ThreadLocal<Map<String, MmtSessionBean>> sessionLocal = new ThreadLocal<Map<String, MmtSessionBean>>();
	
	/**
	 * 取得买卖通产品各DB会话对象
	 * @param dbServer DB名
	 * @param dbSchema DB计划名
	 * @return DB操作对象
	 * @throws MmtException 取得DB会话时发生异常
	 */
	public static MmtSessionBean openSession(String dbconfig) throws MmtException {

		//取得本地DB会话线程组中已使用的DB会话对象集合
		Map<String, MmtSessionBean> sessions = sessionLocal.get();
		if (sessions == null) {
			sessions = new HashMap<String, MmtSessionBean>();
			sessionLocal.set(sessions);
		}
		
		//取得会话Bean与事务封装Bean
		MmtSessionBean sessionBean = (sessions.containsKey(dbconfig)) 
                                   ? sessions.get(dbconfig) : null;
        if (sessionBean == null) {
        	sessionBean = new MmtSessionBean();
        	sessions.put(dbconfig, sessionBean);
        }
        
        //取得指定DB名和计划名所对应的DB会话对象
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
	 * 从连接池中取得指定DB的会话对象
	 * <p>dixingxing 20130131 ： <font color="red">此方法会创建新的connection和session，
	 * 也就是创建了新的事务，不要轻易调用此方法，尽量使用 {@link #openSession(String, String)} </font></p>
	 * @param dbServer DB服务名
	 * @param dbSchema DB用户名
	 * @return DB会话对象
	 * @throws MmtException 取得DB会话对象时发生异常
	 */
	public static Session buildSession(String dbconfig) throws MmtException {

		//如果Hibernate未加载该DB工厂，加载DB工厂
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
		
		
		//从Hibernate工厂中取得DB会话对象
		Session session = factorys.get(dbconfig).openSession();
		
		//add by sunbaoming 增加性能跟踪的代码，创建Session的代理
		if(TraceSwitch.TRACE_ENABLED){
			SessionProxy proxy=new SessionProxy((org.hibernate.Session)session);
			session =(org.hibernate.Session) Proxy.newProxyInstance(
					org.hibernate.Session.class.getClassLoader(), 
					new Class[]{org.hibernate.Session.class}, proxy);
		}
		
		return session;
	}
	
	/**
	 * 创建服务的DB工厂
	 * @param dbServer DB服务名
	 * @param dbSchema DB用户名
	 * @throws MmtException 创建服务的DB工厂时发生异常
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
	 * 回滚当前线程所有已经创建的DB事务
	 * @throws MmtException 回滚事务时发生异常
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
	 * 提交当前线程所有已经创建的DB事务
	 * @throws MmtException 提交事务时发生异常
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
			//判断当前事务模式是否只读
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
			// 数据库主动探测(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
		}
		
		return commitFlg;
	}
	
	/**
	 * 关闭当前线程所有DB会话对象
	 * @throws MmtException 关闭DB会话对象时发生异常
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
