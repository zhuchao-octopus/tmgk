package com.wxs.scanner.bean;

/**
 * 工位表
 */
public class WorkLocation {
	private String sid;
	private String locationCode;
	private String sjob;
	private String workerCode;
	private String workStationCode;
	private String lineCode;
	private String locationState;
	private String nowTime;
	private Integer mid;
	public Integer getMid() {
		return mid;
	}
	public void setMid(Integer mid) {
		this.mid = mid;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}
	public String getSjob() {
		return sjob;
	}
	public void setSjob(String sjob) {
		this.sjob = sjob;
	}
	public String getWorkerCode() {
		return workerCode;
	}
	public void setWorkerCode(String workerCode) {
		this.workerCode = workerCode;
	}
	public String getWorkStationCode() {
		return workStationCode;
	}
	public void setWorkStationCode(String workStationCode) {
		this.workStationCode = workStationCode;
	}
	public String getLineCode() {
		return lineCode;
	}
	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	public String getLocationState() {
		return locationState;
	}
	public void setLocationState(String locationState) {
		this.locationState = locationState;
	}
	public String getNowTime() {
		return nowTime;
	}
	public void setNowTime(String nowTime) {
		this.nowTime = nowTime;
	}

	@Override
	public String toString() {
		return "WorkLocation [sid=" + sid + ", locationCode=" + locationCode
				+ ", sjob=" + sjob + ", workerCode=" + workerCode
				+ ", workStationCode=" + workStationCode + ", lineCode="
				+ lineCode + ", locationState=" + locationState + ", nowTime="
				+ nowTime + ", mid=" + mid + "]";
	}
}
