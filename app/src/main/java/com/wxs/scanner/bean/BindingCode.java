package com.wxs.scanner.bean;

public class BindingCode {
    private String bindingCodeId;
    private String modelCode; //型号
    private String pnCode;    //料号
    private String pcba_SN;   //PCBASN
    private String plt_SN;    //盘心SN
    private String disk_SN;   //小板SN
    private String bxk_SN;    //保修卡SN
    private String boxNum;    //箱号
    private String boardNum;  //栈板码
    private String ch_SN;     //彩盒SN
    private String creatime;
    private String techId;
    private String batch;  //批次
    private String chStatus;  //彩盒状态



    public String getBindingCodeId() {
        return bindingCodeId;
    }

    public void setBindingCodeId(String bindingCodeId) {
        this.bindingCodeId = bindingCodeId;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getPnCode() {
        return pnCode;
    }

    public void setPnCode(String pnCode) {
        this.pnCode = pnCode;
    }

    public String getPcba_SN() {
        return pcba_SN;
    }

    public void setPcba_SN(String pcba_SN) {
        this.pcba_SN = pcba_SN;
    }

    public String getPlt_SN() {
        return plt_SN;
    }

    public void setPlt_SN(String plt_SN) {
        this.plt_SN = plt_SN;
    }

    public String getDisk_SN() {
        return disk_SN;
    }

    public void setDisk_SN(String disk_SN) {
        this.disk_SN = disk_SN;
    }

    public String getBxk_SN() {
        return bxk_SN;
    }

    public void setBxk_SN(String bxk_SN) {
        this.bxk_SN = bxk_SN;
    }

    public String getCh_SN() {
        return ch_SN;
    }

    public void setCh_SN(String ch_SN) {
        this.ch_SN = ch_SN;
    }

    public String getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(String boxNum) {
        this.boxNum = boxNum;
    }

    public String getBoardNum() {
        return boardNum;
    }

    public void setBoardNum(String boardNum) {
        this.boardNum = boardNum;
    }


    public String getCreatime() {
        return creatime;
    }

    public void setCreatime(String creatime) {
        this.creatime = creatime;
    }

    public String getTechId() {
        return techId;
    }

    public void setTechId(String techId) {
        this.techId = techId;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getChStatus() {
        return chStatus;
    }

    public void setChStatus(String chStatus) {
        this.chStatus = chStatus;
    }

    @Override
    public String toString() {
        return "BindingCode{" +
                "bindingCodeId='" + bindingCodeId + '\'' +
                ", modelCode='" + modelCode + '\'' +
                ", pnCode='" + pnCode + '\'' +
                ", pcba_SN='" + pcba_SN + '\'' +
                ", plt_SN='" + plt_SN + '\'' +
                ", disk_SN='" + disk_SN + '\'' +
                ", bxk_SN='" + bxk_SN + '\'' +
                ", boxNum='" + boxNum + '\'' +
                ", boardNum='" + boardNum + '\'' +
                ", ch_SN='" + ch_SN + '\'' +
                ", creatime='" + creatime + '\'' +
                ", techId='" + techId + '\'' +
                ", batch='" + batch + '\'' +
                ", chStatus='" + chStatus + '\'' +
                '}';
    }
}
