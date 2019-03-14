package com.hc360.score.message;

import java.util.Map;

import com.hc360.bcs.bo.BusinInfo;
import com.hc360.hbase.po.UserBusinScore;
import com.hc360.score.statistics.BusinessRecord;

public class Transinformation {
	
	/**
	 * {"@type":"com.hc360.bcs.bo.BusinInfo","bcid":"30511147","detailAmount":2,"detailImageAmount":0,"firstImageType":2,"groupId":0,
	 * "hA":false,"hasImage":true,"hasMinOrderNum":true,"hasNum":true,"hasOtherTitleDetail":3,"imageAmount":1,"noRequiredParam":0,
	 * "oper":1,"priceType":true,"userParamAmount":0,"userid":30001987,"validate":"2035-08-14 14:19:03"} 
	 */
	private BusinInfo busininfo;
	
	private Map<Long, UserBusinScore> userMap;
	
	private BusinessRecord businessRecord;
	
	//消息的整个处理过程是否成功
	private boolean isSuccess;

	//记录失败的原因详细信息
	private String reason;
	
	public Transinformation(BusinInfo busininfo) {
		super();
		this.busininfo = busininfo;
	}

	public BusinInfo getBusininfo() {
		return busininfo;
	}

	public void setBusininfo(BusinInfo busininfo) {
		this.busininfo = busininfo;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
		this.businessRecord.setReason(reason);
	}
	
	public Map<Long, UserBusinScore> getUserMap() {
		return userMap;
	}

	public void setUserMap(Map<Long, UserBusinScore> userMap) {
		this.userMap = userMap;
	}

	public BusinessRecord getBusinessRecord() {
		return businessRecord;
	}

	public void setBusinessRecord(BusinessRecord businessRecord) {
		this.businessRecord = businessRecord;
	}

	@Override
	public String toString() {
		return "BusinessMessage [busininfo=" + busininfo.toString() + ", isSuccess="
				+ isSuccess + ", reason=" + reason+ "]";
	}


	public void record(String message){
		this.getBusinessRecord().stateAppend(message);
	}
}
