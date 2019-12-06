package com.wxs.scanner.bean;

/**
 * 条码规则表
 */
public class ProductScheduling {
	
	private Integer pid; //工艺排程ID
	private String snumber; //排程编码
	private String codeName;//条码名
	private Integer codeLength;//条码长度
	private String pname;//产品名
	private Integer serialLength;//流水号长度
	private String serialSystem;//流水号进制
	private String keyLocatlOne;//关键字位1
	private String keyLocatlTwo;//关键字位2
	private String keyCharOne;//关键字符1
	private String keyCharTwo;//关键字符2
	private String pclass;//工艺
	private Double pcost;//加工费


	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getSnumber() {
		return snumber;
	}
	public void setSnumber(String snumber) {
		this.snumber = snumber;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public Integer getCodeLength() {
		return codeLength;
	}
	public void setCodeLength(Integer codeLength) {
		this.codeLength = codeLength;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public Integer getSerialLength() {
		return serialLength;
	}
	public void setSerialLength(Integer serialLength) {
		this.serialLength = serialLength;
	}
	public String getSerialSystem() {
		return serialSystem;
	}
	public void setSerialSystem(String serialSystem) {
		this.serialSystem = serialSystem;
	}
	public String getKeyLocatlOne() {
		return keyLocatlOne;
	}
	public void setKeyLocatlOne(String keyLocatlOne) {
		this.keyLocatlOne = keyLocatlOne;
	}
	public String getKeyLocatlTwo() {
		return keyLocatlTwo;
	}
	public void setKeyLocatlTwo(String keyLocatlTwo) {
		this.keyLocatlTwo = keyLocatlTwo;
	}
	public String getKeyCharOne() {
		return keyCharOne;
	}
	public void setKeyCharOne(String keyCharOne) {
		this.keyCharOne = keyCharOne;
	}
	public String getKeyCharTwo() {
		return keyCharTwo;
	}
	public void setKeyCharTwo(String keyCharTwo) {
		this.keyCharTwo = keyCharTwo;
	}
	public String getPclass() {
		return pclass;
	}
	public void setPclass(String pclass) {
		this.pclass = pclass;
	}
	public Double getPcost() {
		return pcost;
	}
	public void setPcost(Double pcost) {
		this.pcost = pcost;
	}
	@Override
	public String toString() {
		return "ProductScheduling [pid=" + pid + ", snumber=" + snumber
				+ ", codeName=" + codeName + ", codeLength=" + codeLength
				+ ", pname=" + pname + ", serialLength=" + serialLength
				+ ", serialSystem=" + serialSystem + ", keyLocatlOne="
				+ keyLocatlOne + ", keyLocatlTwo=" + keyLocatlTwo
				+ ", keyCharOne=" + keyCharOne + ", keyCharTwo=" + keyCharTwo
				+ ", pclass=" + pclass + ", pcost=" + pcost + "]";
	}

}
