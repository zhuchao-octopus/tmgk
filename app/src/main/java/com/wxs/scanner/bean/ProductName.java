package com.wxs.scanner.bean;


import java.util.List;

public class ProductName {
	private String msg;
	private int state;
	private List<Date> date;
	private String date1;
	private String date2;
	private String date3;
	private String date4;
	private String date5;

	public List<Date> getDate() {
		return date;
	}

	public void setDate(List<Date> date) {
		this.date = date;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}



	public String getDate1() {
		return date1;
	}

	public void setDate1(String date1) {
		this.date1 = date1;
	}

	public String getDate2() {
		return date2;
	}

	public void setDate2(String date2) {
		this.date2 = date2;
	}

	public String getDate3() {
		return date3;
	}

	public void setDate3(String date3) {
		this.date3 = date3;
	}

	public String getDate4() {
		return date4;
	}

	public void setDate4(String date4) {
		this.date4 = date4;
	}

	public String getDate5() {
		return date5;
	}

	public void setDate5(String date5) {
		this.date5 = date5;
	}

	public static class Date{
		private int pid;
		private String pname;
		private String price;
		private double pweight;
		private double perror;
		private double bweight;
		private double berror;

		public int getPid() {
			return pid;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public String getPname() {
			return pname;
		}

		public void setPname(String pname) {
			this.pname = pname;
		}

		public String getPrice() {
			return price;
		}

		public void setPrice(String price) {
			this.price = price;
		}

		public double getPweight() {
			return pweight;
		}

		public void setPweight(double pweight) {
			this.pweight = pweight;
		}

		public double getPerror() {
			return perror;
		}

		public void setPerror(double perror) {
			this.perror = perror;
		}

		public double getBweight() {
			return bweight;
		}

		public void setBweight(double bweight) {
			this.bweight = bweight;
		}

		public double getBerror() {
			return berror;
		}

		public void setBerror(double berror) {
			this.berror = berror;
		}
	}

}
