package com.wxs.scanner.utils;

/*
 * Created by ztz on 2017/4/13 0013.
 */

public class Constants {

    /**
     *服务器地址
     */
//    private static final String host = "http://192.168.0.105:8080/";  //客户
//    private static final String host = "http://cy-hitech.com:8080/";
//    private static final String host = "http://120.76.100.26:8080/";   //阿里云
    private static final String host = "http://192.168.0.159:8080/";
//    private static final String host = "http://192.168.5.38:8080/";

    /**
     * 获取产品列表
     * 120.76.100.26
     */
    public static final String url_getlist = host+"bds/code/loadPName.do";

    /**
     *获取工作站列表
     */
    public static final String url_getBarcodeRules = host+"bds/code/codeName.do";

    /**
     * 获取工位信息
     */
    public static final String url_select_bh = host+"bds/code/loadPS.do";

    /**
     * 投入统计
     */
    public static final String binding_url = host+"bds/code/codeOne.do";

    /**
     * 测试统计
     *http://localhost:8080/bds/code/codeTwo.do?sn=OA0569974800002&workerCode=002438
     */
    public static final String test_url = host+"bds/code/codeTwo.do";

    /**
     * 绑箱
     *http://192.168.0.25:8080/bds/code/codeSix.do?sn=WE111130P17090700012&sn6=1234234&workerCode=001804
     */
    public static final String station4_box_pcba_url=host+"bds/code/codeSix.do";

    /**
     * 称重a
     * http://192.168.0.25:8080/bds/code/weight.do?sn=sn1@OA0569974800002&weight=2&workerCode=002438
     */
    public static final String weigh_url=host+"bds/code/weight.do";

    /**
     * 产出统计
     * http://192.168.0.25:8080/bds/code/codeFour.do?sn1=OA0569974800001&workerCode=001804
     */
    public static final String InputStatistics=host+"bds/code/codeFour.do";

    /**
     * 出货
     * http://localhost:8080/bds/code/chuhuo.do?boxNum=1234234&workerCode=002438
     */
    public static final String url_shipment = host+"bds/code/chuhuo.do";

    /**
     * 出货工作站获取所有条码
     */
    public static final String url_allcode = host+"bds/code/loadCN.do";

    /**
     * 原QC_抽检
     *
     */

     public static final String url_casualInspection = host+"bds/code/codeQC.do";

    /**
     * QC _抽检
     *
     */

    public static final String url_QC= host+"bds/code/qcStation.do";
    /**
     * QA _抽检
     *
     */

    public static final String url_QA= host+"bds/code/codeQA.do";

    /**
     * QC——批退
     */
    public static final String url_batchAnnealing = host+"bds/code/codeQuit.do";

    /**
     * 维修统计
     */
    public static final String url_maintain = host+"bds/code/codeMaintain.do";

    /**
     * 产线
     */
    public static final String url_line = host+"bds/code/loadLineCode.do";
    /**
     * 入库
     */
    public static final String url_put = host+"bds/code/putStorage.do";
    /**
     * 出库
     */
    public static final String url_out = host+"bds/code/outStorage.do";

    /**
     * 版本更新
     */
    public static final String url_version_updating = host+"jhz_product/version/loadLastVersion.do";

    /**
     * 上传文件
     */
    public static final String url_file_upload = host+"bds/file/upLoadPicture.do";

    /**
     * MAC验证
     */
    public static final String url_mac_verify = host+"bds/equipment/getEquiment.do";

}
