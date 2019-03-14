package com.hc360.score.statistics;

public 
class BusinessRecord{
	private String dateTime;
	private long useTime;
	private String bcids;
	private int operType;
    private String scoreIdentity;
	private String stepState = "开始处理消息 ";
	private String reason;
	
	public BusinessRecord(String bcids, int operType, long useTime) {
		super();
		this.bcids = bcids;
		this.operType = operType;
		this.useTime = useTime;
	}

    public BusinessRecord(String bcids, int operType, String scoreIdentity, long useTime) {
        super();
        this.bcids = bcids;
        this.operType = operType;
        this.scoreIdentity = scoreIdentity;
        this.useTime = useTime;
    }

    public void stateAppend(String state){
		this.setStepState(getStepState()+" ==> "+state);
	}
	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public long getUseTime() {
		return useTime;
	}


	public void setUseTime(long useTime) {
		this.useTime = useTime;
	}

	public String getBcids() {
		return bcids;
	}

	public void setBcids(String bcids) {
		this.bcids = bcids;
	}

	public int getOperType() {
		return operType;
	}

	public void setOperType(int operType) {
		this.operType = operType;
	}

    public String getScoreIdentity() {
        return scoreIdentity;
    }

    public void setScoreIdentity(String scoreIdentity) {
        this.scoreIdentity = scoreIdentity;
    }

    public String getStepState() {
		return stepState;
	}

	public void setStepState(String stepState) {
		this.stepState = stepState;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "Business [dateTime=" + dateTime + ", useTime=" + useTime
				+ ", bcids=" + bcids + ", operType=" + operType+ ", scoreIdentity=" + scoreIdentity
				+ ", stepState=" + stepState + ", reason=" + reason + "]";
	}
}
