package com.wxs.scanner.bean;

public class NumberCountCode {
	private Integer countNum;
	private String workerCode;
	private String locationCode;
	public Integer getCountNum() {
		return countNum;
	}
	public void setCountNum(Integer countNum) {
		this.countNum = countNum;
	}
	public String getWorkerCode() {
		return workerCode;
	}
	public void setWorkerCode(String workerCode) {
		this.workerCode = workerCode;
	}
	public String getLocationCode() {
		return locationCode;
	}
	public void setLocationCode(String locationCode) {
		this.locationCode = locationCode;
	}

	@Override
	public String toString() {
		return "NumberCountCode{" +
				"countNum=" + countNum +
				", workerCode='" + workerCode + '\'' +
				", locationCode='" + locationCode + '\'' +
				'}';
	}
}
