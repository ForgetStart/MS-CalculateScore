/**
 * 
 */
package com.hc360.score.db;

import com.hc360.b2b.exception.MmtException;

import com.hc360.mmt.db.dao.util.MmtSessionBean;

public class ProdDBSource extends BaseDao{

	public boolean isSlave = false;
	
	public ProdDBSource(boolean isSlave){
		this.isSlave = isSlave;
	}
	/**
	 * ȡ��MarketDB�Ự
	 * @throws MmtException ȡ��MrketDB�Ựʱ�����쳣
	 * @see com.hc360.mmt.db.dao.MmtDao#openSession()
	 */
	MmtSessionBean openSession() throws MmtException {
		if(isSlave){
			return SessionFactory.openSession("/configfile/calculatescore/dbcfg/slave_proddb.cfg.xml");
		}
		return SessionFactory.openSession("/configfile/calculatescore/dbcfg/master_proddb.cfg.xml");
	}
	

}
