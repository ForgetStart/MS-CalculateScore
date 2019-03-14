/*
 * @(#) MmtDao.java 4.0 2007-3-15
 * Copyright(c) 2000-2007 HC360.COM, All Rights Reserved.
 */
package com.hc360.score.db;

import static com.hc360.mmt.MmtConstants.CFG_MATCH_DB_READ;
import static com.hc360.mmt.MmtConstants.CFG_MATCH_DB_WRITE;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_CORP;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_PROD;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_ALARM;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_AUDIT;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_BUSIN;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_BUYERRADAR;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_CHAT;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_DETAIL;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_DETAIL_TIMESTEN;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_HCSCHEDULE;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_KEYWORD;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_LOGREAD;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_MAIL;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_MATCH_MANUAL;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_MEET;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_ONLINEVIEW;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_ORDER;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_SEARCH;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_SHOW;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_SITE_CONFIG;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_SMS;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_STAT;
import static com.hc360.mmt.MmtConstants.CFG_PROP_DB_ZSHOW;

import static com.hc360.mmt.MmtConstants.DB_TYPE_PROD;
import static com.hc360.mmt.MmtConstants.DB_TYPE_CORP;
import static com.hc360.mmt.MmtConstants.DB_TYPE_ALARM;
import static com.hc360.mmt.MmtConstants.DB_TYPE_AUDIT;
import static com.hc360.mmt.MmtConstants.DB_TYPE_BUSIN;
import static com.hc360.mmt.MmtConstants.DB_TYPE_BUYERRADAR;
import static com.hc360.mmt.MmtConstants.DB_TYPE_CHAT;
import static com.hc360.mmt.MmtConstants.DB_TYPE_DETAIL;
import static com.hc360.mmt.MmtConstants.DB_TYPE_DETAIL_TIMESTEN;
import static com.hc360.mmt.MmtConstants.DB_TYPE_HCSCHEDULEDB;
import static com.hc360.mmt.MmtConstants.DB_TYPE_KEYWORD;
import static com.hc360.mmt.MmtConstants.DB_TYPE_LOGREAD;
import static com.hc360.mmt.MmtConstants.DB_TYPE_MAIL;
import static com.hc360.mmt.MmtConstants.DB_TYPE_MATCHMANUALDB;
import static com.hc360.mmt.MmtConstants.DB_TYPE_MATCHRESULTDB;
import static com.hc360.mmt.MmtConstants.DB_TYPE_MATCHWRITEDB;
import static com.hc360.mmt.MmtConstants.DB_TYPE_MEET;
import static com.hc360.mmt.MmtConstants.DB_TYPE_ONLINEVIEW;
import static com.hc360.mmt.MmtConstants.DB_TYPE_ORDER;
import static com.hc360.mmt.MmtConstants.DB_TYPE_SEARCH;
import static com.hc360.mmt.MmtConstants.DB_TYPE_SHOW;
import static com.hc360.mmt.MmtConstants.DB_TYPE_SITE_CONFIG;
import static com.hc360.mmt.MmtConstants.DB_TYPE_SMS;
import static com.hc360.mmt.MmtConstants.DB_TYPE_STAT;
import static com.hc360.mmt.MmtConstants.DB_TYPE_ZSHOW;
import static com.hc360.mmt.MmtConstants.PROCEDURE_RETURN_TYPE_NORET;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.hc360.b2b.exception.MmtException;
import com.hc360.mmt.common.bean.Page;
import com.hc360.mmt.common.bean.PageBean;
import com.hc360.mmt.common.bean.PageRecordBean;
import com.hc360.mmt.db.dao.MmtDaoManager;
import com.hc360.mmt.db.dao.util.MmtSessionBean;
import com.hc360.mmt.db.dao.util.MmtSessionFactory;
import com.hc360.mmt.db.po.corpdb.UserAveragequalitylog;

/**
 * ����DB�Ự��
 * @author zhsosy 2007-3-22
 * @version 1.0 JDK1.5.0_06
 */
public abstract class BaseDao implements MmtDaoManager {
	/** ������Ϣ---�־��������Ϊnull */
	private static final String ERR_MSG_NULL_PERSISTENCE_OBJECT = "�־��������Ϊnull";
	/** SQL���---ȡ�����к� */
	private static final String SQL_GET_SEQUENCE = "select %%1%%.nextval as cnt from dual";
	/** SQL���---�洢����ģ�� */
	private static final String PROCEDURE_TEMPLATE = "{call %%1%%(%%2%%)}";	
	/** ��־���� */
	protected final Logger logger = Logger.getLogger(this.getClass());

	
	/**
	 * ȡ�õ�ǰ������Ĭ��DB�Ự����
	 * @return ��ǰ������Ĭ��DB�Ự����
	 * @throws MmtException ȡ��Ĭ��DB�Ựʱ�����쳣
	 */
	abstract MmtSessionBean openSession() throws MmtException;
	
	/**
	 * ���ݳ־�����������¼����ֵȡ��Ĭ��DB��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @return ��¼�־������
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣��־�������Ӧ�����޴�������¼
	 * @see #load(Class, Serializable, int)
	 * @see #find(Class, Serializable)
	 * @see #find(Class, Serializable, int)
	 */
	public final Serializable load(Class theClass, Serializable id) throws MmtException {
		return load(theClass, id, openSession());
	}
	/**
	 * ���ݳ־�����������¼����ֵȡ��ָ��DB��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @param dbType ָ��������DB����
	 * @return ��¼�־������
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣��־�������Ӧ�����޴�������¼
	 * @see #load(Class, Serializable)
	 * @see #find(Class, Serializable)
	 * @see #find(Class, Serializable, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final Serializable load(Class theClass, Serializable id, int dbType) throws MmtException {
		return load(theClass, id, getDbTypeSession(dbType));
	}
	/**
	 * ���ݳ־���������¼����ֵȡ��ָ��DB�Ự��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @param sessionBean ָ��DB�Ự����
	 * @return ��¼�־������
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣��־�������Ӧ�����޴�������¼
	 */
	private Serializable load(Class theClass, Serializable id, MmtSessionBean sessionBean) throws MmtException {
		try {
			Session session = sessionBean.getSession();
			Serializable po = (Serializable) session.load(theClass, id);
			// session.flush();
			return po;
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ݳ־���������¼����ֵȡ��ָ��DB�Ự��Ӧ���Ψһ��¼�쳣", e);
		}
	}
	
	/**
	 * ���ݳ־�����������¼����ֵȡ��ָ��DB��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @return ��¼����ʱ�����ؼ�¼�־�����󣻷��򣬷���null
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣
	 * @see #find(Class, Serializable, int)
	 * @see #load(Class, Serializable)
	 * @see #load(Class, Serializable, int)
	 */
	public final Serializable find(Class theClass, Serializable id) throws MmtException {
		return find(theClass, id, openSession());
	}
	/**
	 * ���ݳ־�����������¼����ֵȡ��ָ��DB��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @param dbType ָ��������DB����
	 * @return ��¼����ʱ�����ؼ�¼�־�����󣻷��򣬷���null
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣
	 * @see #find(Class, Serializable)
	 * @see #load(Class, Serializable)
	 * @see #load(Class, Serializable, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final Serializable find(Class theClass, Serializable id, int dbType) throws MmtException {
		return find(theClass, id, getDbTypeSession(dbType));
	}
	/**
	 * ���ݳ־���������¼����ֵȡ��ָ��DB�Ự��Ӧ���Ψһ��¼��
	 * @param theClass �־��������
	 * @param id ��¼����ֵ
	 * @param sessionBean ָ��DB�Ự����
	 * @return ��¼����ʱ�����ؼ�¼�־�����󣻷��򣬷���null
	 * @throws MmtException ȡ�ü�¼ʱ�����쳣
	 */
	private Serializable find(Class theClass, Serializable id, MmtSessionBean sessionBean) throws MmtException {
		try {
			Session session = sessionBean.getSession();
			Serializable po = (Serializable) session.get(theClass, id);
			// session.flush();
			return po;
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ݳ־���������¼����ֵȡ��ָ��DB�Ự��Ӧ���Ψһ��¼�쳣", e);
		}
	}
	
	/**
	 * ���־��������ݱ��浽Ĭ��DB
	 * @param obj �־����������
	 * @return �����ɵļ�¼����
	 * @throws MmtException ����־�������ʱ�����쳣
	 * @see #save(Serializable, int)
	 * @see #saveOrUpdate(Serializable)
	 * @see #saveOrUpdate(Serializable, int)
	 */
	public final Serializable save(Serializable obj) throws MmtException {
		return save(obj, openSession());
	}
	/**
	 * ���־��������ݱ��浽Ĭ��DB
	 * @param obj �־����������
	 * @param dbType ָ��������DB����
	 * @return �����ɵļ�¼����
	 * @throws MmtException ����־�������ʱ�����쳣
	 * @see #save(Serializable)
	 * @see #saveOrUpdate(Serializable)
	 * @see #saveOrUpdate(Serializable, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final Serializable save(Serializable obj, int dbType) throws MmtException {
		return save(obj, getDbTypeSession(dbType));
	}
	/**
	 * ���־��������ݱ��浽Ĭ��DB
	 * @param obj �־����������
	 * @param sessionBean ָ��DB�Ự����
	 * @return �����ɵļ�¼����
	 * @throws MmtException ����־�������ʱ�����쳣
	 */
	private Serializable save(Serializable obj, MmtSessionBean sessionBean) throws MmtException {
		if (obj == null) throw new MmtException(ERR_MSG_NULL_PERSISTENCE_OBJECT);
		try {
			beginTransaction(sessionBean);
			Session session = sessionBean.getSession();
			Serializable key = session.save(obj);
			session.flush();
			return key;
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���־��������ݱ��浽Ĭ��DB�쳣", e);
		}finally{
			/**
			 * �����Ự
			 */
			try{
				commit(sessionBean);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				clear(sessionBean);
			}
		}
	}
	
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���Ĭ��DB�ļ�¼ִ����������²���
	 * @param obj �־������
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #saveOrUpdate(Serializable, int)
	 * @see #save(Serializable)
	 * @see #save(Serializable, int)
	 * @see #update(Serializable)
	 * @see #update(Serializable, int)
	 */
	public final void saveOrUpdate(Serializable obj) throws MmtException {
		saveOrUpdate(obj, openSession());
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���Ĭ��DB��¼ִ����������²���
	 * @param obj �־������
	 * @param dbType ָ��������DB����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #saveOrUpdate(Serializable)
	 * @see #save(Serializable)
	 * @see #save(Serializable, int)
	 * @see #update(Serializable)
	 * @see #update(Serializable, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final void saveOrUpdate(Serializable obj, int dbType) throws MmtException {
		saveOrUpdate(obj, getDbTypeSession(dbType));
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB�ļ�¼ִ����������²���
	 * @param obj �־������
	 * @param sessionBean ָ��DB�Ự����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 */
	private void saveOrUpdate(Serializable obj, MmtSessionBean sessionBean) throws MmtException {
		if (obj == null) throw new MmtException(ERR_MSG_NULL_PERSISTENCE_OBJECT);
		try {
			beginTransaction(sessionBean);
			Session session = sessionBean.getSession();
			session.saveOrUpdate(obj);
			session.flush();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB�ļ�¼ִ����������²����쳣", e);
		}finally{
			/**
			 * �����Ự
			 */
			try{
				commit(sessionBean);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				clear(sessionBean);
			}
		}
	}
	
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���Ĭ��DB��¼ִ�и��²���
	 * @param obj �־������
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #update(Serializable, int)
	 * @see #saveOrUpdate(Serializable)
	 * @see #saveOrUpdate(Serializable, int)
	 */
	public final void update(Serializable obj) throws MmtException {
		update(obj, openSession());
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���Ĭ��DB��¼ִ�и��²���
	 * @param obj �־������
	 * @param dbType ָ��������DB����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #update(Serializable)
	 * @see #saveOrUpdate(Serializable)
	 * @see #saveOrUpdate(Serializable, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final void update(Serializable obj, int dbType) throws MmtException {
		update(obj, getDbTypeSession(dbType));
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB��¼ִ�и��²���
	 * @param obj �־������
	 * @param sessionBean ָ��DB�Ự����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 */
	private void update(Serializable obj, MmtSessionBean sessionBean) throws MmtException {
		if (obj == null) throw new MmtException(ERR_MSG_NULL_PERSISTENCE_OBJECT);
		try{
			beginTransaction(sessionBean);
			Session session = sessionBean.getSession();
			session.update(obj);
			session.flush();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB��¼ִ�и��²����쳣", e);
		}finally{
			/**
			 * �����Ự
			 */
			try{
				commit(sessionBean);
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				clear(sessionBean);
			}
		}
	}
	
	/**
	 * ����SQL������Ĭ��DB��¼
	 * @param sql SQL���
	 * @return ���¼�¼��
	 * @throws MmtException ����DBʱ�����쳣
	 * @see #update(String, int)
	 * @see #update(String, Map)
	 * @see #update(String, Map, int)
	 */
	protected final int update(String sql) throws MmtException {
		return update(sql, null, openSession());
	}
	
	/**
	 * ����SQL������ָ��DB��¼
	 * @param sql SQL���
	 * @param dbType DB����
	 * @return ���¼�¼��
	 * @throws MmtException ����DBʱ�����쳣
	 * @see #update(String)
	 * @see #update(String, Map)
	 * @see #update(String, Map, int)
	 */
	protected final int update(String sql, int dbType) throws MmtException {
		return update(sql, null, getDbTypeSession(dbType));
	}
	
	/**
	 * ����SQL������Ĭ��DB��¼
	 * @param sql SQL���
	 * @param params ��������
	 * @return ���¼�¼��
	 * @throws MmtException ����DBʱ�����쳣
	 * @see #update(String)
	 * @see #update(String, int)
	 * @see #update(String, Map, int)
	 */
	protected final int update(String sql, Map<String, Object> params) throws MmtException {
		return update(sql, params, openSession());
	}
	
	/**
	 * ����SQL������ָ��DB��¼
	 * @param sql SQL���
	 * @param params ��������
	 * @param dbType ָ��DB����
	 * @return ���¼�¼��
	 * @throws MmtException ����DBʱ�����쳣
	 * @see #update(String)
	 * @see #update(String, int)
	 * @see #update(String, Map)
	 */
	protected final int update(String sql, Map<String, Object> params, int dbType) throws MmtException {
		return update(sql, params, getDbTypeSession(dbType));
	}
	
	/**
	 * ����SQL������ָ��DB��¼
	 * @param sql SQL���
	 * @param params ��������
	 * @param sessionBean DB�Ự����
	 * @return ���¼�¼��
	 * @throws MmtException ����DBʱ�����쳣
	 */
	private int update(String sql, Map<String, Object> params, MmtSessionBean sessionBean) throws MmtException {
		try {
			this.beginTransaction(sessionBean);
			Query q = this.createQuery(sql, params, sessionBean.getSession());
			return q.executeUpdate();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("����SQL������ָ��DB��¼�쳣", e);
		}
	}
	
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���Ĭ��DB��¼ִ��ɾ������
	 * @param obj �־������
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #delete(Serializable, int)
	 */
	public final void delete(Serializable obj) throws MmtException {
		delete(obj, openSession());
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB��¼ִ��ɾ������
	 * @param obj �־������
	 * @param dbType ָ��������DB����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 * @see #delete(Serializable)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final void delete(Serializable obj, int dbType) throws MmtException {
		delete(obj, getDbTypeSession(dbType));
	}
	/**
	 * ����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB��¼ִ��ɾ������
	 * @param obj �־������
	 * @param sessionBean ָ��DB�Ự����
	 * @throws MmtException ִ��DB����ʱ�����쳣
	 */ 
	private void delete(Serializable obj, MmtSessionBean sessionBean) throws MmtException {
		if (obj == null) throw new MmtException(ERR_MSG_NULL_PERSISTENCE_OBJECT);
		try {
			beginTransaction(sessionBean);
			Session session = sessionBean.getSession();
			session.delete(obj);
			session.flush();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("����HIBERNATE��ѯ���еĻ�����־������״̬�Ƚϣ���ָ��DB��¼ִ��ɾ�������쳣", e);
		}
	}
	
	/**
	 * ���ݲ�ѯ��������Ĭ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @return ��ѯ�����¼��(Iterator)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 */
	protected final Iterator iteratorQuery(String queryString) throws MmtException {
		List lst = query(queryString, null, 0, openSession());
		return (lst == null) ? null : lst.iterator();
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������Ĭ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @return ��ѯ�����¼��(Iterator)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 */
	protected final Iterator iteratorQuery(String queryString, Map<String, Object>bean) throws MmtException {
		List lst = query(queryString, bean, 0, openSession());
		return (lst == null) ? null : lst.iterator();
	}
	/**
	 * ���ݲ�ѯ��������ָ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param dbType ָ��DB����
	 * @return ��ѯ�����¼��(Iterator)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final Iterator iteratorQuery(String queryString, int dbType) throws MmtException {
    	List lst = query(queryString, null, 0, getDbTypeSession(dbType));
    	return (lst == null) ? null : lst.iterator();
    }
	/**
	 * ���ݲ�ѯ�������ѯ��������ָ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ�����¼��(Iterator)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final Iterator iteratorQuery(String queryString, Map<String, Object>bean, int dbType) throws MmtException {
		List lst = query(queryString, bean, 0, getDbTypeSession(dbType));
		return (lst == null) ? null : lst.iterator();
	}
	
	/**
	 * ���ݲ�ѯ��������Ĭ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @return ��ѯ�����¼��(List)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 */
	protected final List query(String queryString) throws MmtException {
		return query(queryString, null, 0, openSession());
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������Ĭ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @return ��ѯ�����¼��(List)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, int)
	 * @see #query(String, Map, int)
	 */
	protected final List query(String queryString, Map<String, Object>bean) throws MmtException {
		return query(queryString, bean, 0, openSession());
	}
	protected final List queryWithListParams(String queryString, Map<String, Object>params) throws MmtException {
		return query(queryString, params, 1, openSession());
	}
	/**
	 * ���ݲ�ѯ��������ָ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param dbType ָ��DB����
	 * @return ��ѯ�����¼��(List)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, Map, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final List query(String queryString, int dbType) throws MmtException {
		return query(queryString, null, 0, getDbTypeSession(dbType));
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������ָ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ�����¼��(List)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 * @see #iteratorQuery(String)
	 * @see #iteratorQuery(String, Map)
	 * @see #iteratorQuery(String, int)
	 * @see #iteratorQuery(String, Map, int)
	 * @see #query(String)
	 * @see #query(String, Map)
	 * @see #query(String, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final List query(String queryString, Map<String, Object>bean, int dbType) throws MmtException {
		return query(queryString, bean, 0, getDbTypeSession(dbType));
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������ָ��DB���м���
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param sessionBean DB�Ự����
	 * @return ��ѯ�����¼��(List)
	 * @throws MmtException ��ѯ�����¼ʱ�����쳣
	 */
	private List query(String queryString, Map<String, Object>bean, int paramType, MmtSessionBean sessionBean) throws MmtException {
		try {
			Session session = sessionBean.getSession();
			Query q;
			if (paramType == 0) {
				q = createQuery(queryString, bean, session);
			} else {
				q = createQueryWithListParams(queryString, bean, session);
			}
			return q.list();
		} catch (Exception e) {
			e.printStackTrace();
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ݲ�ѯ�������ѯ��������ָ��DB���м����쳣", e);
		}
	}
			
	/**
	 * ʹ��JDBC��ʽ��ѯ���ݿ�
	 * @param queryString ��ѯ���
	 * @return ��ѯ���
	 * @throws MmtException ��ѯʱ�����쳣
	 * @see #queryByJDBC(String, List)
	 * @see #queryByJDBC(String, int)
	 * @see #queryByJDBC(String, List, int)
	 */
	public List<PageRecordBean> queryByJDBC(String queryString) throws MmtException {
		return queryByJDBC(queryString, null, openSession());
	}
	/**
	 * ʹ��JDBC��ʽ��ѯ���ݿ�
	 * @param queryString ��ѯ���
	 * @param params ��ѯ����
	 * @return ��ѯ���
	 * @throws MmtException ��ѯʱ�����쳣
	 * @see #queryByJDBC(String)
	 * @see #queryByJDBC(String, int)
	 * @see #queryByJDBC(String, List, int)
	 */
	protected List<PageRecordBean> queryByJDBC(String queryString, List<Object> params) throws MmtException {
		return queryByJDBC(queryString, params, openSession());
	}
	/**
	 * ʹ��JDBC��ʽ��ѯ���ݿ�
	 * @param queryString ��ѯ���
	 * @param dbType ָ��DB����
	 * @return ��ѯ��� ��ѯ����
	 * @throws MmtException ��ѯʱ�����쳣
	 * @see #queryByJDBC(String)
	 * @see #queryByJDBC(String, List)
	 * @see #queryByJDBC(String, List, int)
	 */
	protected List<PageRecordBean> queryByJDBC(String queryString, int dbType) throws MmtException {
		return queryByJDBC(queryString, null, getDbTypeSession(dbType));
	}
	/**
	 * ʹ��JDBC��ʽ��ѯ���ݿ�
	 * @param queryString ��ѯ���
	 * @param params ��ѯ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ���
	 * @throws MmtException ��ѯʱ�����쳣
	 * @see #queryByJDBC(String)
	 * @see #queryByJDBC(String, List)
	 * @see #queryByJDBC(String, int)
	 */
	protected List<PageRecordBean> queryByJDBC(String queryString, List<Object> params, int dbType) throws MmtException {
		return queryByJDBC(queryString, params, getDbTypeSession(dbType));
	}
	
	private List<PageRecordBean> queryByJDBC(String queryString, List<Object> params, MmtSessionBean sessionBean) throws MmtException {
		List<PageRecordBean> lstResult;
		PreparedStatement stmt = null;
		try {
			stmt = getPreparedStatement(queryString, params, sessionBean.getSession().connection());
			ResultSet rs = stmt.executeQuery();
			lstResult = new ArrayList<PageRecordBean>();
			while (rs != null && rs.next()) {
				lstResult.add(new PageRecordBean(rs));
			}
			rs.close();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ȡ�ü�¼����ʱ�����쳣", e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�ر�״̬ʱ�����쳣", e);
			}
		}
		return lstResult;
	}
	
	/**
	 * ���ݲ�ѯ��������Ĭ��DB����Ψһ��¼����
	 * @param queryString ��ѯSQL���
	 * @return ��ѯ��¼�־������
	 * @throws MmtException ��ѯ��¼ʱ�����쳣���ߵ�ǰ��ѯ��������������������ϵĽ����
	 * @see #uniqueQuery(String, int)
	 * @see #uniqueQuery(String, Map)
	 * @see #uniqueQuery(String, Map, int)
	 */
	protected final Object uniqueQuery(String queryString) throws MmtException {
		return uniqueQuery(queryString, null, openSession());
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������Ĭ��DB����Ψһ��¼����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @return ��ѯ��¼�־������
	 * @throws MmtException ��ѯ��¼ʱ�����쳣���ߵ�ǰ��ѯ��������������������ϵĽ����
	 * @see #uniqueQuery(String)
	 * @see #uniqueQuery(String, int)
	 * @see #uniqueQuery(String, Map, int)
	 */
	protected final Object uniqueQuery(String queryString, Map<String, Object>bean) throws MmtException {
		return uniqueQuery(queryString, bean, openSession());
	}
	/**
	 * ���ݲ�ѯ��������Ĭ��DB����Ψһ��¼����
	 * @param queryString ��ѯSQL���
	 * @param dbType ָ��DB����
	 * @return ��ѯ��¼�־������
	 * @throws MmtException ��ѯ��¼ʱ�����쳣���ߵ�ǰ��ѯ��������������������ϵĽ����
	 * @see #uniqueQuery(String)
	 * @see #uniqueQuery(String, Map)
	 * @see #uniqueQuery(String, Map, int)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final Object uniqueQuery(String queryString, int dbType) throws MmtException {
		return uniqueQuery(queryString, null, getDbTypeSession(dbType));
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������Ĭ��DB����Ψһ��¼����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ��¼�־������
	 * @throws MmtException ��ѯ��¼ʱ�����쳣���ߵ�ǰ��ѯ��������������������ϵĽ����
	 * @see #uniqueQuery(String)
	 * @see #uniqueQuery(String, int)
	 * @see #uniqueQuery(String, Map)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final Object uniqueQuery(String queryString, Map<String, Object>bean, int dbType) throws MmtException {
		return uniqueQuery(queryString, bean, getDbTypeSession(dbType));
	}
	/**
	 * ���ݲ�ѯ�������ѯ��������Ĭ��DB����Ψһ��¼����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param sessionBean ָ��DB�Ự
	 * @return ��ѯ��¼�־������
	 * @throws MmtException ��ѯ��¼ʱ�����쳣���ߵ�ǰ��ѯ��������������������ϵĽ����
	 */
	private Object uniqueQuery(String queryString, Map<String, Object>bean, MmtSessionBean sessionBean) throws MmtException {
		try{
			Session session = sessionBean.getSession();
			Query q = createQuery(queryString, bean, session);
			return q.uniqueResult();
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ݲ�ѯ�������ѯ��������Ĭ��DB����Ψһ��¼�����쳣", e);
		}
	}
	
	/**
	 * ���ݲ�ѯ��估��ѯ��������Ĭ��DB���ݽ��з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param pageBean ��ҳ��Ϣ
	 * @return ��ѯ��ҳ���
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 * @see #getPageQuery(String, Map, PageBean, int)
	 */
	protected final Page getPageQuery(String queryString, Map<String, Object>bean, PageBean pageBean) throws MmtException {
		return getPageQuery(queryString, bean, pageBean, openSession());
	}
	/**
	 * ���ݲ�ѯ��估��ѯ��������ָ��DB���ݽ��з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param pageBean ��ҳ��Ϣ
	 * @param dbType ָ��DB����
	 * @return ��ѯ��ҳ���
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 * @see #getPageQuery(String, Map, PageBean)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	protected final Page getPageQuery(String queryString, Map<String, Object>bean, PageBean pageBean, int dbType) throws MmtException {
		return getPageQuery(queryString, bean, pageBean, getDbTypeSession(dbType));
	}
	/**
	 * ���ݲ�ѯ��估��ѯ��������ָ��DB���ݽ��з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param pageBean ��ҳ��Ϣ
	 * @param sessionBean ָ��DB����
	 * @return ��ѯ��ҳ���
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 */
	private Page getPageQuery(String queryString, Map<String, Object>bean, PageBean pageBean, MmtSessionBean sessionBean) throws MmtException {
		try{
			Session session = sessionBean.getSession();
			//ȡ�ñ��β�ѯ���صļ�¼��
			pageBean.setCount(getResultCnt(queryString, bean, sessionBean));
			if (pageBean.getCount() == 0) {
				return new Page(new ArrayList(), pageBean);
			}
			//ȡ�ñ��β�ѯ���ؽ����
			Query q = createQuery(queryString, bean, session);
			q.setFirstResult(pageBean.getStartNo() - 1);
			q.setMaxResults(pageBean.getEndNo() - pageBean.getStartNo() + 1);
			return new Page(q.list(), pageBean);
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ݲ�ѯ��估��ѯ��������ָ��DB���ݽ��з�ҳ��ѯ�쳣", e);
		}
	}
	
	private Query createQuery(String queryString, Map<String, Object>bean, Session session) throws MmtException {
		Query q = session.createQuery(queryString);
		String[] keys = q.getNamedParameters();
		if (keys == null || bean == null) return q;
		for (String key : keys) {
			if (!bean.containsKey(key))
				throw new MmtException("û�����ò���" + key + "��ֵ");
			Object value = bean.get(key);
			int flg = getParameterType(value);
			switch (flg) {
			case 0:
				q.setParameter(key, value);
				break;
			case 1:
				q.setParameterList(key, (Collection)value);
				break;
			case 2:
				q.setParameterList(key, (Object[])value);
				break;
			}
		}
		return q;
	}
	
	private Query createQueryWithListParams(String queryString, Map<String, Object>bean, Session session) throws MmtException {
		Query q = session.createQuery(queryString);
		String[] keys = q.getNamedParameters();
		if (keys == null || bean == null) return q;
		for (String key : keys) {
			if (!bean.containsKey(key))
				throw new MmtException("û�����ò���" + key + "��ֵ");
			Object value = bean.get(key);
			int flg = getParameterType(value);
			switch (flg) {
			case 0:
				q.setParameter(key, value);
				break;
			case 1:
				q.setParameterList(key, (Collection)value);
				break;
			case 2:
				q.setParameterList(key, (Object[])value);
				break;
			}
			
		}
		return q;
	}
	
	private int getParameterType(Object param) {
		if (param == null) return 0;
		if (param.getClass().isArray()) return 2;
		Class[] clss = param.getClass().getInterfaces();
		if (clss == null) return 0;
		for (Class cls : clss) {
			if (cls == Collection.class) return 1;
		}
		return 0;
	}
	
	/**
	 * ʹ��JDBC��ʽ��Ĭ��DB���з�ҳ��ѯ��
     * <p><pre>
     * String sql = "select * from on_cor_table, on_cor_table_shop";<br>
     * com.hc360.mmt.common.bean.PageBean pageBean = new PageBean();<br>
     * pageBean.setPage(1);<br>
     * pageBean.setPageSize(10);<br>
     * List lstResult = getPageQueryByJDBC(sql, pageBean);<br>
     * if (lstResult == null) return;<br>
     * for (com.hc360.mmt.common.bean.PageRecordBean rb : lstResult) {<br>
     *   Map<String, Object> onCorTable = rb.getTableRecord("on_cor_table");<br>
     *   System.out.println(onCorTable.get("providerid"));  //�����ҵID<br>
     *   Map<String, Object> onCorTableShop = rb.getTableRecord("on_cor_table_shop");<br>
     *   System.out.println(onCorTableShop.get("providerid"));  //�����ҵID<br>
     *   System.out.println(rb.getValue("on_cor_table", "providerid");  //�����ҵID<br>
     * }
     * </pre></p>
	 * @param queryString ��ѯSQL���
	 * @param pageBean ��ҳ����
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	protected final Page getPageQueryByJDBC(String queryString, PageBean pageBean) throws MmtException {
		return getPageQueryByJDBC(queryString, null, pageBean, openSession(), 0);
	}
	
	protected final Page getPageQueryRawByJDBC(String queryString, PageBean pageBean) throws MmtException {
		return getPageQueryByJDBC(queryString, null, pageBean, openSession(), 1);
	}
	protected final Page getPageQueryRawByJDBC(String queryString, List<Object> params, PageBean pageBean) throws MmtException {
		return getPageQueryByJDBC(queryString, params, pageBean, openSession(), 1);
	}
	/**
	 * ʹ��JDBC��ʽ��ָ��DB���з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param pageBean ��ҳ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	protected final Page getPageQueryRawByJDBC(String queryString, List<Object> params, PageBean pageBean, int dbType) throws MmtException {
		return getPageQueryByJDBC(queryString, params, pageBean, getDbTypeSession(dbType), 1);
	}
	
	/**
	 * ʹ��JDBC��ʽ��ָ��DB���з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param pageBean ��ҳ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	protected final Page getPageQueryByJDBC(String queryString, PageBean pageBean, int dbType) throws MmtException {
		return getPageQueryByJDBC(queryString, null, pageBean, getDbTypeSession(dbType), 0);
	}
	/**
	 * ʹ��JDBC��ʽ��Ĭ��DB���з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param params ��ѯ����
	 * @param pageBean ��ҳ����
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	protected final Page getPageQueryByJDBC(String queryString, List<Object>params, PageBean pageBean) throws MmtException {
		return getPageQueryByJDBC(queryString, params, pageBean, openSession(), 0);
	}
	/**
	 * ʹ��JDBC��ʽ��ָ��DB���з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param params ��ѯ����
	 * @param pageBean ��ҳ����
	 * @param dbType ָ��DB����
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	protected final Page getPageQueryByJDBC(String queryString, List<Object>params, PageBean pageBean, int dbType) throws MmtException {
		return getPageQueryByJDBC(queryString, params, pageBean, getDbTypeSession(dbType), 0);
	}
	/**
	 * ʹ��JDBC��ʽ��ָ��DB���з�ҳ��ѯ
	 * @param queryString ��ѯSQL���
	 * @param params ��ѯ����
	 * @param pageBean ��ҳ����
	 * @param sessionBean DB�Ự����
	 * @param type ��ѯ���
	 * @return ��ѯ����б�
	 * @throws MmtException ��ҳ��ѯʱ�����쳣
	 */
	private Page getPageQueryByJDBC(String queryString, List<Object>params, PageBean pageBean, MmtSessionBean sessionBean, int type) throws MmtException {
		
		/** ��ȫ�� */
		if (params == null){
			params = new ArrayList<Object>();
		}
		
		//ȡ�ñ��β�ѯ���صļ�¼��
		pageBean.setCount(getResultCntByJDBC(queryString, params, sessionBean, type));
		if (pageBean.getCount() == 0) {
			return new Page(new ArrayList(), pageBean);
		}
		
		//ȡ�ñ��β�ѯ���ؽ����
		String newSql = getLimitString(queryString, pageBean,params);
		
		List<PageRecordBean> lstResult;
		PreparedStatement stmt = null;
		try {
			stmt = getPreparedStatement(newSql, params, sessionBean.getSession().connection());
			ResultSet rs = stmt.executeQuery();
			lstResult = new ArrayList<PageRecordBean>();
			while (rs != null && rs.next()) {
				lstResult.add(new PageRecordBean(rs));
			}
			rs.close();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ȡ�ü�¼����ʱ�����쳣", e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�ر�״̬ʱ�����쳣", e);
			}
		}
		return new Page(lstResult, pageBean);
	}
	
	private PreparedStatement getPreparedStatement(String queryString, List<Object> params, Connection conn) throws MmtException {
		PreparedStatement stmt = null;
		
		try {
			stmt = conn.prepareStatement(queryString);
			if (params == null) return stmt;
			
			int idx = 1;
			for (Object obj : params) {
				if (obj != null) {
					stmt.setObject(idx, obj);
				} else {
					stmt.setNull(idx, Types.NULL);
				}
				idx++;
			}
		} catch (SQLException e) {
			throw new MmtException("����JDBC��ѯʱ�����쳣[sql = " + queryString + "]", e);
		}

		return stmt;
	}
	
	protected String getLimitString(String sql, PageBean pageBean,List<Object>params) {
		StringBuffer pagingSelect = new StringBuffer(sql.length() + 100);
		if (pageBean.getStartNo() > 1) {
			pagingSelect.append(
			  "select * from ( select row_.*, rownum rownum_ from ( ");
		} else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (pageBean.getStartNo() > 1) {
			pagingSelect.append(" ) row_ where rownum <= ")
			            .append("?")
			            .append(") where rownum_ >= ")
			            .append("?");
			   			params.add(pageBean.getEndNo());
			   		   	params.add(pageBean.getStartNo());
		} else {
			pagingSelect.append(" ) where rownum <= ")
			            .append("?");
						params.add(pageBean.getEndNo());
		}
		return pagingSelect.toString();
	}
	
	/**
	 * ȡ��Ĭ��DBָ���������ֵ
	 * @param seqName Ҫȡ�õ���������
	 * @return ȡ�õ�����ֵ
	 * @throws MmtException ȡ������ֵʱ�����쳣
	 * @see #getSequence(String, int)
	 */
	public final long getSequence(String seqName) throws MmtException {
		return getSequence(seqName, openSession());
	}
	/**
	 * ȡ��ָ��DBָ���������ֵ
	 * @param seqName Ҫȡ�õ���������
	 * @param dbType ָ��DB����
	 * @return ȡ�õ�����ֵ
	 * @throws MmtException ȡ������ֵʱ�����쳣
	 * @see #getSequence(String)
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	public final long getSequence(String seqName, int dbType) throws MmtException {
		return getSequence(seqName, getDbTypeSession(dbType));
	}
	
	/**
	 * ȡ��ָ��DBָ���������ֵ
	 * @param seqName Ҫȡ�õ���������
	 * @param sessionBean ָ��DB�Ự
	 * @return ȡ�õ�����ֵ
	 * @throws MmtException ȡ������ֵʱ�����쳣
	 */
	private long getSequence(String seqName, MmtSessionBean sessionBean) throws MmtException {
		Session session = sessionBean.getSession();
		String sql = SQL_GET_SEQUENCE.replaceAll("%%1%%", seqName);
		Connection conn = session.connection();
		Statement stmt = null;
		ResultSet rs = null;
		long count;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			count = (rs.next()) ? rs.getLong("cnt") : 0L;
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ȡ������ֵʱ�����쳣[seq_name : " + seqName + "]", e);
		} finally {
			try {
				if (rs != null){
					rs.close();
				}
				if (stmt != null){
					stmt.close();
				}
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�ر�״̬ʱ�����쳣", e);
			}
		}
		return count;
	}
	
	/**
	 * ����Ĭ��DB�Ĵ洢����
	 * @param procName �洢������
	 * @param params �洢���̲���
	 * @param returnTypes �洢���̷���ֵ���ͣ�����Ϊ����ֵ֮һ��
	 * <li>java.sql.Types�������е����</li>
	 * <li>�޷���ֵʱ������com.hc360.mmt.MmtConstants.PROCEDURE_RETURN_TYPE_NORET</li>
	 * @return �ɹ�ʱ������true��ʧ��ʱ������false
	 * @throws MmtException ���ô洢����ʱ�����쳣
	 * @see #callProcedure(int, String, List, int...)
	 * @see com.hc360.mmt.MmtConstants#PROCEDURE_RETURN_TYPE_NORET
	 */
	protected final Object callProcedure(String procName, List<Object> params, int... returnTypes) throws MmtException {
		return callProcedure(procName, params, openSession(), returnTypes);
	}
	/**
	 * ����Ĭ��DB�Ĵ洢����
	 * @param procName �洢������
	 * @param params �洢���̲���
	 * @param returnTypes �洢���̷���ֵ���ͣ�����Ϊ����ֵ֮һ��
	 * <li>java.sql.Types�������е����</li>
	 * <li>�޷���ֵʱ������com.hc360.mmt.MmtConstants.PROCEDURE_RETURN_TYPE_NORET</li>
	 * @param dbType ָ��DB����
	 * @return �ɹ�ʱ������true��ʧ��ʱ������false
	 * @throws MmtException ���ô洢����ʱ�����쳣
	 * @see #callProcedure(String, List, int...)
	 * @see com.hc360.mmt.MmtConstants#PROCEDURE_RETURN_TYPE_NORET
	 */
	protected final Object callProcedure(int dbType, String procName, List<Object> params, int... returnTypes) throws MmtException {
		return callProcedure(procName, params, getDbTypeSession(dbType), returnTypes);
	}
	/**
	 * ����Ĭ��DB�Ĵ洢����
	 * @param procName �洢������
	 * @param params �洢���̲���
	 * @param returnTypes �洢���̷���ֵ���ͣ�����Ϊ����ֵ֮һ��
	 * <li>java.sql.Types�������е����</li>
	 * <li>�޷���ֵʱ������com.hc360.mmt.MmtConstants.PROCEDURE_RETURN_TYPE_NORET</li>
	 * @param sessionBean ָ��DB�Ự
	 * @return �ɹ�ʱ������true��ʧ��ʱ������false
	 * @throws MmtException ���ô洢����ʱ�����쳣
	 */
	private Object callProcedure(String procName, List<Object> params, MmtSessionBean sessionBean, int... returnTypes) throws MmtException {
		//��ϴ洢���̵������
		StringBuffer paramSql = new StringBuffer();
		if (params != null && params.size() > 0) {
			for (int i = 0; i < params.size(); i++) {
				if (paramSql.length() > 0) paramSql.append(",");
				paramSql.append("?");
			}
		}
		if (returnTypes != null) {
			for (int returnType : returnTypes) {
				if (returnType != PROCEDURE_RETURN_TYPE_NORET) {
					if (paramSql.length() > 0) paramSql.append(",");
					paramSql.append("?");
				}
			}
		}
		String procSql = PROCEDURE_TEMPLATE.replaceAll("%%1%%", procName)
		                .replaceAll("%%2%%", paramSql.toString());

		beginTransaction(sessionBean);
		Connection conn = sessionBean.getSession().connection();
		
		Object ret;
		CallableStatement cStmt = null;
		try {
			//���õ��ô洢���̲���
			cStmt = conn.prepareCall(procSql);
			if (params != null & params.size() > 0) {
				for (int i = 0; i < params.size(); i++) {
					cStmt.setObject((i + 1), params.get(i));
				}
			}
			int retPos = (params == null) ? 0 : params.size();
			int outCnt = 0;
			if (returnTypes != null) {
				for (int returnType : returnTypes) {
					/**
					 * û�з���ֵʱ��лĻadd
					 */
					if (returnType != PROCEDURE_RETURN_TYPE_NORET) {
						outCnt++;
						cStmt.registerOutParameter(retPos + outCnt, returnType);
					}
					
				}
			}
			
			//���ô洢����
			cStmt.execute();
			if (outCnt > 0) {
				if (outCnt > 1) {
					Object[] retObj = new Object[outCnt];
					for (int i = 1; i <= outCnt; i++) {
						retObj[i -1] = cStmt.getObject(retPos + i);
					}
					ret = retObj;
				} else {
					ret = cStmt.getObject(retPos + 1);
				}
			} else {
				ret = null;
			}
			
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("���ô洢���̷����쳣[sql = " + procSql + "]", e);
		} finally {
			try {
				if (cStmt != null) cStmt.close();
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�رմ洢����״̬�쳣", e);
			}
		}
		
		return ret;
	}

	/**
	 * ���ݲ�ѯ��������ѯ����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @return ��ѯ�����¼���ļ�¼��
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 */
	protected final int getResultCnt(String queryString, Map<String, Object>bean) throws MmtException {
		return getResultCnt(queryString, bean, openSession());
	}
	
	/**
	 * �������Ķ����Ƶ��־������
	 * @param object ��������
	 * @return �־������
	 * @throws MmtException ����ʱ�����쳣
	 * @see #merge(Object, int)
	 */
	protected final Object merge(Object object) throws MmtException {
		return merge(object, openSession());
	}
	/**
	 * �������Ķ����Ƶ��־������
	 * @param object ��������
	 * @param dbType ָ��DB����
	 * @return �־������
	 * @throws MmtException ����ʱ�����쳣
	 * @see #merge(Object)
	 */
	protected final Object merge(Object object, int dbType) throws MmtException {
		return merge(object, getDbTypeSession(dbType));
	}
	private Object merge(Object object, MmtSessionBean sessionBean) throws MmtException {
		try{
			this.beginTransaction(sessionBean);
			return sessionBean.getSession().merge(object);
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("�������Ķ����Ƶ��־�������쳣", e);
		}
	}
	
	/**
	 * ָ����������־������
	 * @param po �־������
	 * @param id ����
	 * @throws MmtException �������ʱ�����쳣
	 * @see #classicSave(Object, Serializable, int)
	 */
	public final void classicSave(Object po, Serializable id) throws MmtException {
		classicSave(po, id, openSession());
	}
	/**
	 * ָ����������־������
	 * @param po �־������
	 * @param dbType ָ��DB����
	 * @param id ����
	 * @throws MmtException �������ʱ�����쳣
	 * @see #classicSave(Object, Serializable)
	 */
	public final void classicSave(Object po, Serializable id, int dbType) throws MmtException {
		classicSave(po, id, getDbTypeSession(dbType));
	}
	private void classicSave(Object po, Serializable id, MmtSessionBean sessionBean) throws MmtException {
		try{
			this.beginTransaction(sessionBean);
			((org.hibernate.classic.Session)sessionBean.getSession()).save(po, id);
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ָ����������־�������쳣", e);
		}
	}
	
	/**
	 * ���ݲ�ѯ��������ѯ����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param dbType DB����
	 * @return ��ѯ�����¼���ļ�¼��
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 */
	protected final int getResultCnt(String queryString, Map<String, Object>bean, int dbType) throws MmtException {
		return getResultCnt(queryString, bean, getDbTypeSession(dbType));
	}
	
	/**
	 * ���ݲ�ѯ��������ѯ����
	 * @param queryString ��ѯSQL���
	 * @param bean ��ѯ����
	 * @param session DB�Ự����
	 * @return ��ѯ�����¼���ļ�¼��
	 * @throws MmtException ��ѯ����ʱ�����쳣
	 */
	private int getResultCnt(String queryString, Map<String, Object>bean, MmtSessionBean session) throws MmtException {
		try{
			//ɾ����ѯ����е�order by�Ӿ�
			String newSql =  delOrderbySQL(queryString);
			
			//�޸�SQL���
			int idx = newSql.toUpperCase().indexOf("FROM ");
			newSql = "select count(*) " + newSql.substring(idx);
			
			//ȡ�õ�ǰ��ѯ�ܼ�¼��
			Query q = createQuery(newSql, bean, session.getSession());
			List lst = q.list();
			return (lst == null || lst.size() == 0) ? 0 : Integer.parseInt(lst.get(0).toString());
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(session.getDbServer());
			throw new MmtException("��ѯ�����¼���ļ�¼���쳣", e);
		}
	}
	
	/**
	 * ʹ��JDBC��ʽȡ�ò�ѯ���ļ�¼��
	 * @param queryString SQL���
	 * @param params ��ѯ����
	 * @param sessionBean DB�Ự����
	 * @param type ��ѯ���
	 * @return ��ѯ��䷵�صļ�¼��
	 * @throws MmtException ִ�в�ѯ���ʱ�����쳣
	 */
	private int getResultCntByJDBC(String queryString, List<Object>params, MmtSessionBean sessionBean, int type) throws MmtException {
		//ɾ����ѯ����е�order by�Ӿ�
		String newSql;
		
		//�޸�SQL���
		if (type == 0) {
			newSql = delOrderbySQL(queryString);
			int idx = newSql.toUpperCase().indexOf("FROM ");
			newSql = "select count(*) as cnt " + newSql.substring(idx);
		} else {
			newSql = "select count(*) as cnt from (" + queryString + ")";
		}

		//ȡ�õ�ǰ��ѯ�ܼ�¼��
		Connection conn = sessionBean.getSession().connection();
		PreparedStatement stmt = null;
		int count;
		try {
			stmt = this.getPreparedStatement(newSql, params, conn);
			ResultSet rs = stmt.executeQuery();
			count = (rs.next()) ? rs.getInt("cnt") : 0;
			rs.close();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ȡ�ü�¼����ʱ�����쳣", e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�ر�״̬ʱ�����쳣", e);
			}
		}
		return count;
	}
	
	/**
	 * ɾ����ѯ�����е�order by�Ӿ�
	 * @param queryString ��ѯSQL���
	 * @return ɾ����ѯ����е�order by�Ӿ��Ĳ�ѯ���
	 */
	private String delOrderbySQL(String queryString) {
		StringBuffer temp = new StringBuffer();
		queryString = "(" + queryString + ")";

		String[] strArray = queryString.split("order by");
		for (int i = 1; i < strArray.length; i++) {
			strArray[i] = ")";
		}
		for (int i = 0; i < strArray.length; i++) {
			temp.append(strArray[i]);
		}
		String result = temp.toString();
		if (temp.length() >= 2) {
			result = result.substring(1, result.length() - 1);
		}
		return result;
	}
	
	/**
	 * ����DB����ȡ��DB�Ự
	 * @param dbType DB����ֵ
	 * @return DB�Ự����
	 * @throws MmtException ȡ��DB�Ự����ʱ�����쳣
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_BUSIN
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_CHAT
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_DETAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_KEYWORD
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MAIL
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_MARKET
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_ORDER
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SEARCH
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SHOW
	 * @see com.hc360.mmt.MmtConstants#DB_TYPE_SMS
	 */
	private MmtSessionBean getDbTypeSession(int dbType) throws MmtException {
		MmtSessionBean sessionBean;
		switch (dbType) {
		case DB_TYPE_BUSIN:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_BUSIN, null);
			break;
		case DB_TYPE_CHAT:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_CHAT, null);
			break;
		case DB_TYPE_DETAIL:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_DETAIL, null);
			break;
		case DB_TYPE_KEYWORD:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_KEYWORD, null);
			break;
		case DB_TYPE_MAIL:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_MAIL, null);
			break;
		case DB_TYPE_CORP:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_CORP, null);
			break;
		case DB_TYPE_PROD:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_PROD, null);
			break;	
			
		case DB_TYPE_ORDER:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_ORDER, null);
			break;
		case DB_TYPE_SEARCH:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_SEARCH, null);
			break;
		case DB_TYPE_SHOW:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_SHOW, null);
			break;
		case DB_TYPE_SMS:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_SMS, null);
			break;
		case DB_TYPE_STAT:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_STAT, null);
			break;
		case DB_TYPE_AUDIT:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_AUDIT, null);
			break;
		case DB_TYPE_ZSHOW:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_ZSHOW, null);
			break;
		case DB_TYPE_MATCHRESULTDB:
			sessionBean = MmtSessionFactory.openSession(CFG_MATCH_DB_READ, null);
			break;
		case DB_TYPE_MATCHWRITEDB:
			sessionBean = MmtSessionFactory.openSession(CFG_MATCH_DB_WRITE, null);
			break;
		case DB_TYPE_MATCHMANUALDB:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_MATCH_MANUAL, null);
			break;
		case DB_TYPE_ONLINEVIEW:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_ONLINEVIEW, null);
			break;
		case DB_TYPE_LOGREAD:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_LOGREAD, null);
			break;
		case DB_TYPE_DETAIL_TIMESTEN:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_DETAIL_TIMESTEN, null);
			break;
		case DB_TYPE_MEET:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_MEET, null);
			break;
		case DB_TYPE_SITE_CONFIG:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_SITE_CONFIG, null);
			break;
		case DB_TYPE_ALARM:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_ALARM, null);
			break;
		case DB_TYPE_BUYERRADAR:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_BUYERRADAR, null);
			break;
		case DB_TYPE_HCSCHEDULEDB:
			sessionBean = MmtSessionFactory.openSession(CFG_PROP_DB_HCSCHEDULE, null);
			break;
		default:
			throw new MmtException("�����DB����[DB_TYPE = " + dbType + "]");
		}
		return sessionBean;
	}
	
	/**
	 * ��ʼһ�����ݿ�����
	 * @param sessionBean DB�Ự����
	 * @throws MmtException ��ʼ���ݿ�����ʱ�����쳣
	 */
	private void beginTransaction(MmtSessionBean sessionBean) throws MmtException {
		Transaction tx = sessionBean.getTransaction();
		if (tx != null && tx.isActive() 
		&& !tx.wasCommitted() && !tx.wasRolledBack()) {
			return;
		}
		sessionBean.setTransaction(sessionBean.getSession().beginTransaction());
	}
	
	/**
	 * ˢ��Ĭ��DB�Ự
	 * @throws MmtException ˢ�»Ự�����쳣
	 */
	public final void flush() throws MmtException {
		flush(openSession());
	}
	/**
	 * ˢ��ָ��DB�Ự
	 * @param dbType ָ��DB����
	 * @throws MmtException ˢ�»Ự�����쳣
	 */
	public final void flush(int dbType) throws MmtException {
		flush(getDbTypeSession(dbType));
	}
	private void flush(MmtSessionBean sessionBean) throws MmtException {
		Session session = sessionBean.getSession();
		session.flush();
	}
	
	/**
	 * �����ǰDBĬ�ϻỰ�Ļ�������
	 * @throws MmtException �����������ʱ�����쳣
	 */
	public final void clear() throws MmtException {
		clear(openSession());
	}
	/**
	 * ���ָ��DB�Ự�Ļ�������
	 * @throws MmtException �����������ʱ�����쳣
	 */
	public final void clear(int dbType) throws MmtException {
		clear(getDbTypeSession(dbType));
	}
	private final void clear(MmtSessionBean sessionBean) throws MmtException {
		sessionBean.getSession().clear();
	}
	
	public final void commit() throws MmtException {
		commit(openSession());
	}
	public final void commit(int dbType) throws MmtException {
		commit(getDbTypeSession(dbType));
	}
	private void commit(MmtSessionBean sessionBean) throws MmtException {
		MmtSessionFactory.commit(sessionBean);
	}
	public final void rollback() throws MmtException {
		rollback(openSession());
	}
	public final void rollback(int dbType) throws MmtException {
		rollback(getDbTypeSession(dbType));
	}
	private void rollback(MmtSessionBean sessionBean) throws MmtException {
		MmtSessionFactory.rollback(sessionBean);
	}
	
	/**
	 * ֱ��ʹ��HSQL�����в�ѯ������hibernate��executeUpdate�ķ�ʽ
	 * @param queryString Ҫ��ѯ��HSQL���
	 * @param bean Ҫ��ѯ�Ĳ���
	 * @return int ��Ӱ��ļ�¼����
	 * @throws MmtException ����ʧ�ܻᷢ���쳣
	 */
	protected final int executeUpdate(String queryString, Map<String, Object>bean) throws MmtException {
		MmtSessionBean sessionBean = openSession(); 
		try{
			beginTransaction(sessionBean);
			Session session = sessionBean.getSession();
			return this.createQuery(queryString, bean, session).executeUpdate();
		}catch(Exception e){
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("ֱ��ʹ��HSQL�����в�ѯ������hibernate��executeUpdate�ķ�ʽ�쳣", e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.hc360.mmt.db.dao.MmtDaoManager#queryList(java.lang.String, java.util.Map)
	 */
	public List queryList(String queryString, Map<String, Object> bean) throws MmtException {		
		return query(queryString, bean);
	}
	
	/**
	 * ʹ��JDBC��ʽ��ѯ���ݿ�
	 * @param queryString ��ѯ���
	 * @return ��ѯ���
	 * @throws MmtException ��ѯʱ�����쳣
	 * @see #queryByJDBC(String, List)
	 * @see #queryByJDBC(String, int)
	 * @see #queryByJDBC(String, List, int)
	 */
	protected final int executeUpdateByJDBC(String queryString, List<Object> params) throws MmtException{
		PreparedStatement stmt = null;
		int updated=0;
		MmtSessionBean sessionBean = openSession(); 
		this.beginTransaction(sessionBean);
		try {
			stmt = getPreparedStatement(queryString, params, openSession().getSession().connection());
			updated = stmt.executeUpdate();
		} catch (Exception e) {
			// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
			//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
			throw new MmtException("��������ʱ�����쳣", e);
		} finally {
			try {
				if (stmt != null) stmt.close();
			} catch (Exception e) {
				// ���ݿ�����̽��(shiwenfeng add 2012-12-25)
				//MmtSessionFactory.activeDetectionDB(sessionBean.getDbServer());
				throw new MmtException("�ر�״̬ʱ�����쳣", e);
			}
		}
		return updated;
	}

	public UserAveragequalitylog saveUpdateUserAveragequalityTemp(long userId,
			long sumBusin, long sumstar) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
