package com.wxs.scanner.bean;

import java.io.Serializable;

public class WorkStationCode implements Serializable{
	

	private static final long serialVersionUID = 1L;
	private Integer mid;
	private String wsNumber;//工作站编号
	private String wsFunction;//功能
	private String pname;//产品名
	private String boxNumCount;//装箱数
	private String sn1;//操作码1
	private String sn2;//操作码2
	private String sn3;//操作码3
	private String sn4;//操作码4
	private String sn5;//操作码5
	private String sn6;//操作码6
	private String sn7;//操作码7
	private String data1;//操作码8
	private String data2;//操作码9
	private String missStation;//是否过站
	private String inputStation;//投入工作站
	private String outputStation;//产出工作站
	private String employ;//是否使用

	public String getInputStation() {
		return inputStation;
	}
	public void setInputStation(String inputStation) {
		this.inputStation = inputStation;
	}
	public String getOutputStation() {
		return outputStation;
	}
	public void setOutputStation(String outputStation) {
		this.outputStation = outputStation;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getMissStation() {
		return missStation;
	}
	public void setMissStation(String missStation) {
		this.missStation = missStation;
	}
	public String getWsNumber() {
		return wsNumber;
	}
	public void setWsNumber(String wsNumber) {
		this.wsNumber = wsNumber;
	}
	public String getWsFunction() {
		return wsFunction;
	}
	public void setWsFunction(String wsFunction) {
		this.wsFunction = wsFunction;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public String getBoxNumCount() {
		return boxNumCount;
	}
	public void setBoxNumCount(String boxNumCount) {
		this.boxNumCount = boxNumCount;
	}
	public String getSn1() {
		return sn1;
	}
	public void setSn1(String sn1) {
		this.sn1 = sn1;
	}
	public String getSn2() {
		return sn2;
	}
	public void setSn2(String sn2) {
		this.sn2 = sn2;
	}
	public String getSn3() {
		return sn3;
	}
	public void setSn3(String sn3) {
		this.sn3 = sn3;
	}
	public String getSn4() {
		return sn4;
	}
	public void setSn4(String sn4) {
		this.sn4 = sn4;
	}
	public String getSn5() {
		return sn5;
	}
	public void setSn5(String sn5) {
		this.sn5 = sn5;
	}
	public String getSn6() {
		return sn6;
	}
	public void setSn6(String sn6) {
		this.sn6 = sn6;
	}
	public String getSn7() {
		return sn7;
	}
	public void setSn7(String sn7) {
		this.sn7 = sn7;
	}
	public String getData1() {
		return data1;
	}
	public void setData1(String data1) {
		this.data1 = data1;
	}
	public String getData2() {
		return data2;
	}
	public void setData2(String data2) {
		this.data2 = data2;
	}
	
	
	public Integer getMid() {
		return mid;
	}
	public void setMid(Integer mid) {
		this.mid = mid;
	}
	public String getEmploy() {
		return employ;
	}
	public void setEmploy(String employ) {
		this.employ = employ;
	}
	@Override
	public String toString() {
		return "WorkStationCode [mid=" + mid + ", wsNumber=" + wsNumber
				+ ", wsFunction=" + wsFunction + ", pname=" + pname
				+ ", boxNumCount=" + boxNumCount + ", sn1=" + sn1 + ", sn2="
				+ sn2 + ", sn3=" + sn3 + ", sn4=" + sn4 + ", sn5=" + sn5
				+ ", sn6=" + sn6 + ", sn7=" + sn7 + ", data1=" + data1
				+ ", data2=" + data2 + ", missStation=" + missStation
				+ ", inputStation=" + inputStation + ", outputStation="
				+ outputStation + ", employ=" + employ + "]";
	}
	
	
}
