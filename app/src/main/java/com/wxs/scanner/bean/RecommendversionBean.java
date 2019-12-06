package com.wxs.scanner.bean;

/**
 * Created by ZTZ on 2018/3/21.
 */

public class RecommendversionBean {

    private int status;
    private String msg;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }



    public static class DataBean {
        private int soft_id;
        private String soft_name;
        private double soft_version;
        private String soft_description;
        private String soft_url;

        public int getSoft_id() {
            return soft_id;
        }

        public void setSoft_id(int soft_id) {
            this.soft_id = soft_id;
        }

        public String getSoft_name() {
            return soft_name;
        }

        public void setSoft_name(String soft_name) {
            this.soft_name = soft_name;
        }

        public double getSoft_version() {
            return soft_version;
        }

        public void setSoft_version(double soft_version) {
            this.soft_version = soft_version;
        }

        public String getSoft_description() {
            return soft_description;
        }

        public void setSoft_description(String soft_description) {
            this.soft_description = soft_description;
        }

        public String getSoft_url() {
            return soft_url;
        }

        public void setSoft_url(String soft_url) {
            this.soft_url = soft_url;
        }
    }
}
