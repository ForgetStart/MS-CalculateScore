package com.hc360.score.db;

import com.hc360.b2b.exception.MmtException;
import com.hc360.mmt.db.dao.util.MmtSessionBean;

public class CorpDBSource extends BaseDao{

	public boolean isSlave = false;
	
	public CorpDBSource(boolean isSlave){
		this.isSlave = isSlave;
	}
	/**
	 * ȡ��MarketDB�Ự
	 * @throws MmtException ȡ��MrketDB�Ựʱ�����쳣
	 * @see com.hc360.mmt.db.dao.MmtDao#openSession()
	 */
	MmtSessionBean openSession() throws MmtException {
		if(isSlave){
			return SessionFactory.openSession("/configfile/calculatescore/dbcfg/slave_corpdb.cfg.xml");
		}
		return SessionFactory.openSession("/configfile/calculatescore/dbcfg/master_corpdb.cfg.xml");
	}
	
}
