package com.hc360.score.statistics;

public class Statistics {
	private String name;
	private long time;
	private boolean result;
	private int type; //1:单纯自增计数；2：带结果自增计数；3：带结果和响应时间的自增计数

	public Statistics(String name, int type) {
		this.name = name;
		this.type = type;
	}

	public Statistics(String name, boolean result, int type) {
		this.name = name;
		this.result = result;
		this.type = type;
	}

	public void end(boolean result) {
		this.result = result;
		this.time = System.currentTimeMillis() - this.time;
		StatisticsHandler.stQueue.add(this);
	}
	public void end(boolean result, long time) {
		this.result = result;
		this.time = time;
		StatisticsHandler.stQueue.add(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "Statistics [name=" + name + ", time=" + time + ", result="
				+ result + ", type=" + type + "]";
	}

}
