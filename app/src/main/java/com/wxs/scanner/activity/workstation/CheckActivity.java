package com.wxs.scanner.activity.workstation;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxs.scanner.BuildConfig;
import com.wxs.scanner.R;
import com.wxs.scanner.activity.set.SelectWorkStationActivity;
import com.wxs.scanner.activity.set.SettingsActivity;
import com.wxs.scanner.adapter.MainAdapter;
import com.wxs.scanner.bean.CheckBarcodeResult;
import com.wxs.scanner.bean.CheckMacBean;
import com.wxs.scanner.bean.CodeStation;
import com.wxs.scanner.bean.NumberCountCode;
import com.wxs.scanner.bean.ProductCode;
import com.wxs.scanner.bean.ProductScheduling;
import com.wxs.scanner.bean.RecommendversionBean;
import com.wxs.scanner.dialog.DialogAll;
import com.wxs.scanner.utils.AppManager;
import com.wxs.scanner.utils.Constants;
import com.wxs.scanner.utils.FilesUploadUtils;
import com.wxs.scanner.utils.Utils;
import com.wxs.scanner.utils.http.HttpCallBack;
import com.wxs.scanner.utils.http.OkHttpUtils;
import com.wxs.scanner.utils.sp.SPData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * 工作站兼容
 * Created by hl on 2017/10/10 0010.
 */

public class CheckActivity extends Activity implements TextWatcher, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private EditText et_result;
    private TextView et_SN1, et_SN2, et_SN3, no_bindnum, title_smjl;
    private TextView tv_station, modelinfo, check_title;
    private TextView set, count, all_count, fallcount, SN1_PASS, SN2_PASS, SN3_PASS, data_pass, check_result;
    private TextView thisboxcount, boxcount, title_xs, ptcount;
    private TextView SN1Name, SN2Name, SN3Name, gather;
    private TextView station1, station2, station3, station4, station5, station6;
    private TextView station1count, station2count, station3count, station4count, station5count, station6count;
    private TextView machine_weight,machine_weight_error,box_weight,box_weight_error;
    private Gson gson = new Gson();
    private int bs = 1;
    private String key = "";
    private String key1 = "";
    private String key2 = "";
    private ListView lv_nodata;
    private String mOneScan;//当前扫描出来的码值
    List<String> dataList = new ArrayList<>();
    ArrayList<String> allcord = new ArrayList<>();
    List<ProductCode> bindingCodelist = new ArrayList<>();
    ArrayList<NumberCountCode> numberCountCodeList = new ArrayList<>();
    private TextView model, part_number;
    private TextView uphss, uphpj, uphmax, uphmix;
    private LinearLayout check_Inputnumber, llboxcount, llthisboxcount;
    Timer timer = new Timer();//站2定时
    TimerTaskTest task = new TimerTaskTest();
    private List<ProductScheduling> gclists = new ArrayList<>();//存储规则的集合
    Map<String, String> tmgzmaps = new HashMap<>();//存储解析好的规则
    private String boxNumCount = "";//装箱个数
    private List<String> wslists = null;
    private CodeStation codeStation = null;

    private String soft_name = "scanner";

    private String diqu = "";
    private String xinghao = "";
    private String shijian = "";
    private String xuliehao = "00001";  //序列号

    private String FilePath = "";//路径
    private String FileName = ""; //文件名
    private boolean isFirst = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private TextView tv_cpname,tv_cxname;
    private Spinner spinner;
    IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checklayout);
        initView();
//        registerReceiver(mNetworkStatusReceiver, filter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }


    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String str = (String) msg.obj;
                String strs[] = str.split("@");
                //更新UI
                uphss.setText(strs[0]);
                uphmax.setText(strs[1]);
                uphmix.setText(strs[2]);
                uphpj.setText(strs[3]);
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 初始化UI
     */
    private void initView() {
        spinner = (Spinner) findViewById(R.id.spinnerinfo);
        spinner.setOnItemSelectedListener(this);
        tv_cpname = (TextView) findViewById(R.id.tv_cpname);
        tv_cxname = findViewById(R.id.tv_cxname);
        check_title = (TextView) findViewById(R.id.check_title);
        gather = (TextView) findViewById(R.id.check_gather);
        uphss = (TextView) findViewById(R.id.check_uph_ss);
        uphpj = (TextView) findViewById(R.id.check_uph_pj);
        uphmix = (TextView) findViewById(R.id.check_uph_mix);
        uphmax = (TextView) findViewById(R.id.check_uph_max);
        tv_station = (TextView) findViewById(R.id.check_station);
        modelinfo = (TextView) findViewById(R.id.check_model_info);
        set = (TextView) findViewById(R.id.check_set);
        model = (TextView) findViewById(R.id.check_model);
        part_number = (TextView) findViewById(R.id.check_partnumber);
        et_result = (EditText) findViewById(R.id.check_et_result);
        et_result.addTextChangedListener(this);
        et_SN1 = (TextView) findViewById(R.id.check_et_SN1);
        et_SN2 = (TextView) findViewById(R.id.check_et_SN2);
        et_SN3 = (TextView) findViewById(R.id.check_et_SN3);
        no_bindnum = (TextView) findViewById(R.id.check_no_bindnum);
        count = (TextView) findViewById(R.id.check_count);
        boxcount = (TextView) findViewById(R.id.check_boxcount);
        thisboxcount = (TextView) findViewById(R.id.thisboxcount);
        all_count = (TextView) findViewById(R.id.check_allcount);
        fallcount = (TextView) findViewById(R.id.fallcount);
        ptcount = (TextView) findViewById(R.id.ptcount);
        SN1_PASS = (TextView) findViewById(R.id.check_SN1_PASS);
        SN2_PASS = (TextView) findViewById(R.id.check_SN2_PASS);
        SN3_PASS = (TextView) findViewById(R.id.check_SN3_PASS);
        data_pass = (TextView) findViewById(R.id.check_data_pass);
        check_result = (TextView) findViewById(R.id.check_result_data);
        SN1Name = (TextView) findViewById(R.id.check_pcba);
        SN2Name = (TextView) findViewById(R.id.check_px);
        SN3Name = (TextView) findViewById(R.id.check_xb);
        lv_nodata = (ListView) findViewById(R.id.check_lv_nodata);
        lv_nodata.setOnItemClickListener(this);
        station1 = (TextView) findViewById(R.id.check_station1);
        station2 = (TextView) findViewById(R.id.check_station2);
        station3 = (TextView) findViewById(R.id.check_station3);
        station4 = (TextView) findViewById(R.id.check_station4);
        station5 = (TextView) findViewById(R.id.check_station5);
        station6 = (TextView) findViewById(R.id.check_station6);

        station1count = (TextView) findViewById(R.id.check_station1_count);
        station2count = (TextView) findViewById(R.id.check_station2_count);
        station3count = (TextView) findViewById(R.id.check_station3_count);
        station4count = (TextView) findViewById(R.id.check_station4_count);
        station5count = (TextView) findViewById(R.id.check_station5_count);
        station6count = (TextView) findViewById(R.id.check_station6_count);
        check_Inputnumber = (LinearLayout) findViewById(R.id.check_Inputnumber);
        llboxcount = (LinearLayout) findViewById(R.id.ll_box_count);
        llthisboxcount = (LinearLayout) findViewById(R.id.ll_thisbox_count);
        title_xs = (TextView) findViewById(R.id.title_xs);
        title_smjl = (TextView) findViewById(R.id.title_smjl);

        machine_weight = findViewById(R.id.tv_machine_weight);
        machine_weight_error = findViewById(R.id.tv_machine_weight_error);
        box_weight = findViewById(R.id.tv_box_weight);
        box_weight_error = findViewById(R.id.tv_box_weight_error);


        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> maps = new HashMap<>();
                //保存设置的数据
                String g = SPData.ReadEquipment(CheckActivity.this);
                Log.e("tag"," gggg="+g);
                if (!"".equals(g) && null != g) {
                    Map<String, String> maps1 = gson.fromJson(g, new TypeToken<Map<String, String>>() {
                    }.getType());
                    Log.e("Tag","maps1="+maps1);
                    if (maps1.get("sledtsn1") != null && !maps1.get("sledtsn1").equals("空闲")) {
                        String id1[] = maps1.get("sledtsn1").split("#");
                        maps.put("sledtsn1", station1.getText().toString() + "#" + id1[1]);
                    } else {
                        maps.put("sledtsn1", station1.getText().toString()+ "#空闲");
                        maps.put("sledtsn1", "空闲");
                    }
                    if (maps1.get("sledtsn2") != null && !maps1.get("sledtsn2").equals("空闲")) {
                        String id2[] = maps1.get("sledtsn2").split("#");
                        maps.put("sledtsn2", station2.getText().toString() + "#" + id2[1]);
                    } else {
                        maps.put("sledtsn2", station2.getText().toString()+ "#空闲");
                        maps.put("sledtsn2", "空闲");
                    }
                    if (maps1.get("sledtsn3") != null && !maps1.get("sledtsn3").equals("空闲")) {
                        String id3[] = maps1.get("sledtsn3").split("#");
                        maps.put("sledtsn3", station3.getText().toString() + "#" + id3[1]);
                    } else {
                        maps.put("sledtsn3", station3.getText().toString()+ "#空闲");
                        maps.put("sledtsn3", "空闲");
                    }
                    if (maps1.get("sledtsn4") != null && !maps1.get("sledtsn4").equals("空闲")) {
                        String id4[] = maps1.get("sledtsn4").split("#");
                        maps.put("sledtsn4", station4.getText().toString() + "#" + id4[1]);
                    } else {
                        maps.put("sledtsn4", station4.getText().toString()+ "#空闲");
                        maps.put("sledtsn4", "空闲");
                    }
                    if (maps1.get("sledtsn5") != null && !maps1.get("sledtsn5").equals("空闲")) {
                        String id5[] = maps1.get("sledtsn5").split("#");
                        maps.put("sledtsn5", station5.getText().toString() + "#" + id5[1]);
                    } else {
                        maps.put("sledtsn5", station5.getText().toString()+ "#空闲");
                        maps.put("sledtsn5", "空闲");
                    }
                    if (maps1.get("sledtsn6") != null && !maps1.get("sledtsn6").equals("空闲")) {
                        String id6[] = maps1.get("sledtsn6").split("#");
                        maps.put("sledtsn6", station6.getText().toString() + "#" + id6[1]);
                    } else {
                        maps.put("sledtsn6", station6.getText().toString()+ "#空闲");
                        maps.put("sledtsn6", "空闲");
                    }
                    maps.put("setmyid", maps1.get("setmyid"));//工位编号
                } else {
                    maps.put("sledtsn1", station1.getText().toString());
                    maps.put("sledtsn2", station2.getText().toString());
                    maps.put("sledtsn3", station3.getText().toString());
                    maps.put("sledtsn4", station4.getText().toString());
                    maps.put("sledtsn5", station5.getText().toString());
                    maps.put("sledtsn6", station6.getText().toString());
                    maps.put("setmyid", "A1");
                }
                maps.put("sledtname", tv_station.getText().toString());//工作站编号
                maps.put("slemodel", modelinfo.getText().toString());//模式
                maps.put("cpname", tv_cpname.getText().toString());//产品名
                maps.put("cxname", tv_cxname.getText().toString());//产线
                Gson gson = new Gson();
                String json = gson.toJson(maps);
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);//NewActivity是目标Activity
                //要传递的值
                intent.putExtra("json", json);
                //启动Activity
                startActivityForResult(intent, 0);//使用时需定义变量requestCode
            }
        });
        //调用SP
        String g = SPData.ReadEquipment(CheckActivity.this);
        String g1 = SPData.ReadGC(this);
        Map<String, String> maps = gson.fromJson(g, new TypeToken<Map<String, String>>() {
        }.getType());
        gclists = gson.fromJson(g1, new TypeToken<ArrayList<ProductScheduling>>() {
        }.getType());
        Log.e("Tag","gclists=sp>"+gclists);
        setData(maps, gclists);
        String wsjson = SPData.ReadWSjson(CheckActivity.this);
        if (!"".equals(wsjson)) {
            wslists = gson.fromJson(wsjson, new TypeToken<ArrayList<String>>() {
            }.getType());
            codeStation = new CodeStation();
            for(int i = 0 ;i < wslists.size();i++){
                if(wslists.get(i).equals("绑定")){
                    codeStation.setOneString("绑定");
                }else if(wslists.get(i).equals("测试统计")){
                    codeStation.setTwoString("测试统计");
                }else if(wslists.get(i).equals("ZQC")){
                    codeStation.setThreeString("ZQC");
                }else if(wslists.get(i).equals("投入统计")){
                    codeStation.setFourString("投入统计");
                }else if(wslists.get(i).equals("WQC")){
                    codeStation.setFiveString("WQC");
                }else if(wslists.get(i).equals("机重")){
                    codeStation.setSixString("机重");
                }else if(wslists.get(i).equals("箱重")){
                    codeStation.setEightString("箱重");
                }else if(wslists.get(i).equals("绑箱")){
                    codeStation.setSevenString("绑箱");
                } else if(wslists.get(i).equals("维修统计")){
                    codeStation.setNineString("维修统计");
                }else if(wslists.get(i).equals("产出统计")){
                    codeStation.setTenString("产出统计");
                } else if(wslists.get(i).contains("QC")){
                    codeStation.setTenString("QC抽检");
                } else if(wslists.get(i).contains("QA")){
                    codeStation.setTenString("QA抽检");
                }else if(wslists.get(i).equals("入库")){
                    codeStation.setTenString("入库");
                }else if(wslists.get(i).equals("出库")){
                    codeStation.setTenString("出库");
                }
            }
            Log.e("tag","codeStation="+codeStation);

        }
        et_result.setFocusable(true);
        et_result.setFocusableInTouchMode(true);
        et_result.requestFocus();
        et_result.requestFocusFromTouch();
    }

    String str0 = "";
    String str1 = "";
    String str3 = "";
    String str4 = "";

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        String scanStr = String.valueOf(s);
        if (scanStr.endsWith("\n")) {
            String station = tv_station.getText().toString();
            String gzzname = modelinfo.getText().toString();//工作站名称
            if (station.equals("")) {
                Toast.makeText(getApplicationContext(), "请先设置工作站", Toast.LENGTH_SHORT).show();
                et_result.setText("");
                return;
            }
            String[] split = scanStr.split("\n");
            //当前扫描出来的码值
            mOneScan = split[split.length - 1].trim();
            str0 = mOneScan.substring(0, 2);
            str1 = mOneScan.substring(0, 1);
//            str4 = mOneScan.substring(8, 9);
            if (mOneScan.length() > 12) {
                str3 = mOneScan.substring(11, 12);
            }
            if (mOneScan.equals("Fail")) {
                setInitialise();//扫描返回到初始状态
            } else {
                Log.e("tag","gzzname="+gzzname);
                String str[] = gzzname.split("重");
                if (gzzname.indexOf("重") != -1) {
                    gzzname = "称重";
                } else if (gzzname.indexOf("测试") != -1) {
                    gzzname = "测试";
                } else if (gzzname.indexOf("投入") != -1) {
                    gzzname = "投入";
                }else if (gzzname.indexOf("QA") != -1) {
                    gzzname = "QA";
                }else if (gzzname.indexOf("QC") != -1) {
                    gzzname = "QC";
                } else if (gzzname.indexOf("维修") != -1) {
                    gzzname = "维修";
                } else if (gzzname.indexOf("产出") != -1) {
                    gzzname = "产出";
                }else if (gzzname.indexOf("入库") != -1){
                    gzzname = "入库";
                } else if (gzzname.indexOf("出库") != -1){
                    gzzname = "出库";
                }
                switch (gzzname) {
                    case "投入":
                        //根据绑定工作站所涉及到的条码数进行动态选择
                        getBinding(gclists.size());
                        break;
                    case "装箱":
                        getBindNumber();
                        break;
                    case "称重":
                        getWigh(str[0]);
                        break;
                    case "测试":
                        getTest();
                        break;
                    case "产出":
                        getStatistics();
                        break;
                    case "出货":
                        getShipment();
                        break;
                    case "QA":
                        getQACJ();
                        break;
                    case "维修":
                        getMaintain();
                        break;
                    case "QC":
                        getQCCJ();
                        break;
                    case "入库":
                        getPut();
                        break;
                    case "出库":
                        getOut();
                        break;


//  else if (station.equals("7")) {//QC检测
//                            int i = 0;
//                            if (bs == 1) {//查询
//                                if (str4.equals("C") && mOneScan.length() == 17) {
//                                    i = 1;
//                                } else if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH) {
//                                    i = 2;
//                                }
//                                if (i == 1) {
//                                    if (str4.equals("C") && mOneScan.length() == 17) {
//                                        key = "boxNum@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是箱号，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else if (i == 2) {
//                                    if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH || mOneScan.length() == 25) {
//                                        key = "ch@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是彩盒码，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    data_pass.setText("FALL");
//                                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
//                                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不符合要求，请重新扫描", Toast.LENGTH_SHORT).show();
//                                }
//                                if (i == 1 || i == 2) {
//                                    et_SN3.setText(mOneScan);
//                                    SN3_PASS.setText("PASS");
//                                    SN3_PASS.setTextColor(getResources().getColor(R.color.bg_green));
//                                    clearData_station();
//                                    selectPCBA(station, i);//调用查询接口
//                                }
//                            } else if (bs == 6) {//抽检
//                                if (str4.equals("C") && mOneScan.length() == 17) {
//                                    i = 1;
//                                } else if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH) {
//                                    i = 2;
//                                }
//                                if (i == 1) {
//                                    if (str4.equals("C") && mOneScan.length() == 17) {
//                                        key = "boxNum@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是箱号，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else if (i == 2) {
//                                    if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH) {
//                                        key = "ch@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是彩盒码，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    data_pass.setText("FALL");
//                                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
//                                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不符合要求，请重新扫描", Toast.LENGTH_SHORT).show();
//                                }
//                                if (i == 1 || i == 2) {
//                                    et_SN3.setText(mOneScan);
//                                    SN3_PASS.setText("PASS");
//                                    SN3_PASS.setTextColor(getResources().getColor(R.color.bg_green));
//                                    clearData_station();
//                                    casualInspection(station, i);//调用查询接口
//                                }
//                            } else if (bs == 7) {//批退
//                                if (str4.equals("C") && mOneScan.length() == 17) {
//                                    i = 1;
//                                } else if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH) {
//                                    i = 2;
//                                }
//                                if (i == 1) {
//                                    if (str4.equals("C") && mOneScan.length() == 17) {
//                                        key = "boxNum@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是箱号，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else if (i == 2) {
//                                    if (mOneScan.length() == BarCodeRule.FOURSCAN_LENGTH || mOneScan.length() == 25) {
//                                        key = "ch@" + mOneScan;
//                                    } else {
//                                        Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是彩盒码，请重新扫描", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    data_pass.setText("FALL");
//                                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
//                                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不符合要求，请重新扫描", Toast.LENGTH_SHORT).show();
//                                }
//                                if (i == 1 || i == 2) {
//                                    et_SN1.setText(mOneScan);
//                                    SN1_PASS.setText("PASS");
//                                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
//                                    String job = station1.getText().toString();
//                                    DialogAll.showNormalDialog(this, station, job, key, myhandler, i);
//                                }
//
//                            }
//                        }


                }
            }
        }
    }



    private void getQCp() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = "SN@"+mOneScan;
                Log.e("tag","mOneScan>"+mOneScan+"    key="+key+ "   g="+gclists.size());
                if (gclists.size() == 1) {
                    getOneQC();
                } else {
                    SN1_2Text();
                    bs++;
                }
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 2) {
            String a = "";
            String strs[] = mOneScan.split(":");
            for (int i = 0; i < strs.length; i++) {
                a = a + strs[i];
            }
            if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && a.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key1 = mOneScan;
                SN2Text();
                getOneQC();
                bs = 1;
            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getQC(){
        int i = 0;
        if (str0.equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))&& mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh))||
                (str1 + str3).equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh))) {
            i = 2;
        } else if(str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
            i=1;
        }
        if (i == 2) {
            if (str0.equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))&& mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh))||
                    (str1 + str3).equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))
                            && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh))) {
                key = "boxNum@" + mOneScan;
            } else {
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是箱号，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (i == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                key = "SN@" + mOneScan;
            } else {
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是"+SN1Name.getText().toString()+"码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else {
            data_pass.setText("FALL");
            data_pass.setTextColor(getResources().getColor(R.color.red_fail));
            Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不符合要求，请重新扫描", Toast.LENGTH_SHORT).show();
        }
        if (bs == 1) {//抽检
            resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
            et_SN1.setText(mOneScan);
            SN1_PASS.setText("PASS");
            SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
            getOneQC();
        }else if(bs==2){//批退
            if(i!=0){
                resultDataText2();
                String job = station1.getText().toString();
                String stations = gson.toJson(codeStation);
                DialogAll.showNormalDialog(stations,this, job, key, myhandler);
            }else{
                Toast.makeText(getApplicationContext(), "操作失误，无法批退", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void getShipment() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh)) ||
                    (str1 + str3).equals(tmgzmaps.get("zf1" + xh) + tmgzmaps.get("zf2" + xh))
                            && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + xh))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                if (dataList != null || dataList.size() != 0) {
                    lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, null));
                }
                key = mOneScan;
                getOneShipment();
            } else {
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是箱号", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getStatistics() {
        Log.e("tag","tmgzmaps>"+tmgzmaps+"    bs="+bs);
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                if (gclists.size() == 1) {
                    getOneStatistics(key, "");
                } else {
                    SN1_2Text();
                    bs++;
                }
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 2) {
            String a = "";
            String strs[] = mOneScan.split(":");
            for (int i = 0; i < strs.length; i++) {
                a = a + strs[i];
            }
            if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && a.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key1 = mOneScan;
                SN2Text();
                getOneStatistics(key, key1);
                bs = 1;
            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getPut() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                getOnePut(key);
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getOut() {
            if (bs == 1) {
                //扫“over”码，上传文件
                if (mOneScan.equals("over")){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            if (!"".equals(FilePath) && !"".equals(FileName)) {
                                FilesUploadUtils filesUploadUtils = new FilesUploadUtils();
                                try {
                                    if (filesUploadUtils.upload(FileName, new File(FilePath))){
                                        deleteAllFile(new File(FilePath));
                                        deleteAllFile(getExternalCacheDir());
                                        FilePath = "";
                                        FileName = "";
                                    }
//                                    getSharedPreferences("xuliehao",MODE_PRIVATE).edit().putString("xlh",FileName).commit();
                                } catch (NetworkErrorException e) {
                                    e.printStackTrace();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(CheckActivity.this,"您扫了“over”码，正在上传文件！",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }.start();
                    SN2_1Text();
                }
//                else if (mOneScan.equals("reset")){
////                    getSharedPreferences("xuliehao",MODE_PRIVATE).edit().putString("xlh","").commit();
//                    xuliehao = "00001";
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(CheckActivity.this,"您扫了“reset”码，已重置序列号！",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
                else if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                    resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("PASS");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    key = mOneScan;
                    getOneOut(key);
                }else {
                    SN1_PASS.setText("FAIL");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            }
        }

    private void getQCCJ() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                Log.e("tag","mOneScan>"+mOneScan+"    key="+key+ "   g="+gclists.size());
                if (gclists.size() == 1) {
                    getQCCJ(key, "");
                } else {
                    SN1_2Text();
                    bs++;
                }
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 2) {
            String a = "";
            String strs[] = mOneScan.split(":");
            for (int i = 0; i < strs.length; i++) {
                a = a + strs[i];
            }
            if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && a.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key1 = mOneScan;
                SN2Text();
                getQCCJ(key, key1);
                bs = 1;
            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void getQACJ() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                Log.e("tag","mOneScan>"+mOneScan+"    key="+key+ "   g="+gclists.size());
                if (gclists.size() == 1) {
                    getQACJ(key, "");
                } else {
                    SN1_2Text();
                    bs++;
                }
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 2) {
            String a = "";
            String strs[] = mOneScan.split(":");
            for (int i = 0; i < strs.length; i++) {
                a = a + strs[i];
            }
            if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && a.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key1 = mOneScan;
                SN2Text();
                getQACJ(key, key1);
                bs = 1;
            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getTest() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                getOneTest();
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 维修统计
     */
    private void getMaintain() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                getOneMaintain();
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 称重
     */
    private void getWigh(String s) {
        if (bs == 1) {
            resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
            String strs[] = mOneScan.split("\\.");
            double wight = 0;
            double error = 0;
            if (!"".equals(uphss.getText().toString()) && null != uphss.getText().toString()) {
                if (s.equals("机")) {
                    wight = Double.parseDouble(uphss.getText().toString());
                    error = Double.parseDouble(uphpj.getText().toString());
                } else if (s.equals("箱")) {
                    wight = Double.parseDouble(uphmax.getText().toString());
                    error = Double.parseDouble(uphmix.getText().toString());
                }
            }
            if (strs != null && strs.length != 0) {
                if (Double.parseDouble(mOneScan) >= (wight - error) && Double.parseDouble(mOneScan) <= (wight + error)) {
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("PASS");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    key = mOneScan;
                    Log.e("tag","key="+key+"   mOneScan="+mOneScan);
                    SN1_2Text();
                    bs++;
                }else if (Double.parseDouble(mOneScan) < (wight - error)){
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("FAIL，重量误差小于标准重量");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    bs = 1;
                } else if (Double.parseDouble(mOneScan) > (wight + error)){
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("FAIL，重量误差大于标准重量");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    bs = 1;
                }
            } else {
                data_pass.setText("FAIL，请先称重");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "请先称重", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 2) {
            if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString())) ||
                    (str1 + str3).equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                            && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                SN2Text();
//                SN2_1Text();
                Log.e("tag","s==="+s);
                if (s.equals("机")) {
                    key1 = mOneScan;
                } else if (s.equals("箱")) {
                    key1 = "box@" + mOneScan;
                }
                Log.e("tag","key1="+key1+"   mOneScan="+mOneScan);
                getOneWigh();
                bs = 1;
            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 装箱功能
     */
    private void getBindNumber() {
        if (bs == 1) {
            if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString())) ||
                    (str1 + str3).equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                            && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                et_SN1.setText(mOneScan);
                SN1_PASS.setText("PASS");
                SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                key = mOneScan;
                bs = 6;
                SN1_2Text();
            } else {
                SN1_PASS.setText("FAIL");
                SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        } else if (bs == 6) {
            if (mOneScan.equals("over")) {
                bs = 1;
                Toast.makeText(getApplicationContext(), "你已经扫了“over”码，该箱已经结束或扫码错误已返回！", Toast.LENGTH_LONG).show();
//                SN2_3Text();
                SN2_1Text();
            } else if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                    && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                //清空上一次数据
                clearData_station6();
                key1 = mOneScan;
                et_SN2.setText(mOneScan);
                SN2_PASS.setText("PASS");
                SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                int il = Integer.parseInt(thisboxcount.getText().toString());
                if("".equals(boxNumCount)){
                    Toast.makeText(getApplicationContext(), "装箱设置已过期，请重新设置", Toast.LENGTH_LONG).show();
                }else{
                    int ill = Integer.parseInt(boxNumCount);
                    if (il <= ill) {
                        uploadbox_ch(ill);
                    }
                }

            } else {
                SN2_PASS.setText("FAIL");
                SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 绑定功能  (现在为投入统计)
     *
     * @param i
     */
    private void getBinding(int i) {
        if (i == 1) {
            if (bs == 1) {
                if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                    resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("PASS");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    getOneBinding(mOneScan, "", "", "");
                } else {
                    SN1_PASS.setText("FAIL");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (i == 2){
            if (bs == 1) {
                if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                    resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("PASS");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    SN1_2Text();
                    bs++;
                } else {
                    SN1_PASS.setText("FAIL");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            } else if (bs == 2) {
                if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                    et_SN2.setText(mOneScan);
                    SN2_PASS.setText("PASS");
                    SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    String sn1 = et_SN1.getText().toString();
                    String sn2 = et_SN2.getText().toString();
                    getOneBinding(sn1, sn2, "", "");
                    SN2Text();
                    bs = 1;
                } else {
                    SN2_PASS.setText("FAIL");
                    SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (i == 3) {
            if (bs == 1) {
                if (str0.equals(tmgzmaps.get("zf1" + SN1Name.getText().toString()) + tmgzmaps.get("zf2" + SN1Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN1Name.getText().toString()))) {
                    resultDataText2();//当扫描第二轮时，清空第一轮的数据，包含数据返回的数据
                    et_SN1.setText(mOneScan);
                    SN1_PASS.setText("PASS");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    SN1_2Text();
                    bs++;
                } else {
                    SN1_PASS.setText("FAIL");
                    SN1_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN1Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            } else if (bs == 2) {
                if (str0.equals(tmgzmaps.get("zf1" + SN2Name.getText().toString()) + tmgzmaps.get("zf2" + SN2Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN2Name.getText().toString()))) {
                    et_SN2.setText(mOneScan);
                    SN2_PASS.setText("PASS");
                    SN2_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    SN2_3Text();
                    bs++;
                } else {
                    SN2_PASS.setText("FAIL");
                    SN2_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN2Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            } else if (bs == 3) {
                if (str0.equals(tmgzmaps.get("zf1" + SN3Name.getText().toString()) + tmgzmaps.get("zf2" + SN3Name.getText().toString()))
                        && mOneScan.length() == Integer.parseInt(tmgzmaps.get("cd" + SN3Name.getText().toString()))) {
                    et_SN3.setText(mOneScan);
                    SN3_PASS.setText("PASS");
                    SN3_PASS.setTextColor(getResources().getColor(R.color.bg_green));
                    SN3Text();
                    String sn1 = et_SN1.getText().toString();
                    String sn2 = et_SN2.getText().toString();
                    String sn3 = et_SN3.getText().toString();
                    getOneBinding(sn1, sn2, sn3, "");
                    bs = 1;
                } else {
                    SN3_PASS.setText("FAIL");
                    SN3_PASS.setTextColor(getResources().getColor(R.color.red_fail));
                    Toast.makeText(getApplicationContext(), "正在扫描的码：" + mOneScan + "不是" + SN3Name.getText().toString() + "码，请重新扫描", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    private void clearData_station() {
        et_SN1.setText("");
        et_SN2.setText("");
        et_SN3.setText("");
        data_pass.setText("");
        SN1_PASS.setText("");
        SN2_PASS.setText("");
        SN3_PASS.setText("");
        Log.e("TAG","dataList="+dataList);
        if (dataList != null && dataList.size() != 0) {
            lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, null));
        }
    }

    private void clearData_station6() {
        et_SN2.setText("");
        data_pass.setText("");
        SN2_PASS.setText("");
    }

    private void getAllCode(String cpname){
        Map<String, String> map6 = new HashMap<>();
        map6.put("pname", cpname);
        OkHttpUtils.getInstance().post(Constants.url_allcode, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    allcord.clear();
                    Log.e("tag","条码："+checkBarcodeResult.getDate());
                    if (checkBarcodeResult.getDate() != null && !"".equals(checkBarcodeResult.getDate())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        ArrayList<String>  as = new Gson().fromJson((String)checkBarcodeResult.getDate(), listType);
                        if(as!=null||as.size()!=0){
                            for(int i = 0;i<as.size();i++){
                                if(!as.get(i).contains("箱号")){
                                    if(!as.get(i).contains("MAC")){
                                        if(!as.get(i).contains("彩盒")){
                                            allcord.add(as.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(allcord!=null||allcord.size()!=0){
                        SN1Name.setText("");
                        SN2Name.setText("");
                        SN3Name.setText("");

                        for(int i = 0 ;i<allcord.size();i++){
                            if(i==0){
                                SN1Name.setText(allcord.get(i));
                            }else if(i==1){
                                SN2Name.setText(allcord.get(i));
                            }else if(i==2){
                                SN3Name.setText(allcord.get(i));
                            }
                        }
                        String json = gson.toJson(allcord);
                        //保存条码规则数据
                        SPData.WriteCHGC(CheckActivity.this,json );
                    }

                } else  {
                    Toast.makeText(getApplicationContext(), "设置失败，请重新设置", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getOneStatistics(String a, String b) {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn1", a);
        map6.put("sn2", b);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.InputStatistics, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1())) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4())) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码已扫入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL，对比失败");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 10) {
                    data_pass.setText("FAIL,SN未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 12) {
                    data_pass.setText("FAIL,未测试统计");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 7) {
                    data_pass.setText("FAIL,未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void getOnePut(String a) {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("boxNum", a);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_put, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getMsg()+"     "+checkBarcodeResult.getState());
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    for (int i = 0; i < str.length; i++) {
                        Log.e("TAG","str="+str[i]+"    str4="+str4);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已入库数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未入库数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(1,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码已入库");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void getOneOut(final String a){
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("boxNum", a);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_out, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getState());
                if (checkBarcodeResult.getState() == 0) {
                     //后台成功返回数据后，将箱号写入指定TXT文档
                    String str3[] = null;
                    Log.e("atag","str3="+checkBarcodeResult.getDate3());
                    if (!"".equals(checkBarcodeResult.getDate3()) && null != checkBarcodeResult.getDate3()) {
                        str3 = checkBarcodeResult.getDate3().split("@");
                        String date = str3[0];  //日期
                        String area = str3[1];  //地区
                        String type = str3[2];  //型号

                         //将箱号写入指定TXT文档
                        saveTheCartonNO(a,date+"_"+area+"_"+type+".txt");
                    }



                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
//                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
//                        Type listType = new TypeToken<ArrayList<String>>() {
//                        }.getType();
//                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
//                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    Log.e("TAG","out  CodeList="+numberCountCodeList+"    str4="+str4);
                    if (null != str ) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
//                    Log.e("TAG","dataList="+dataList);
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该箱号未装箱");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该箱号已出库");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }
     int t = 0;
    private void saveTheCartonNO(String boxNum, final String fileName){
        //判断是否有指定文件夹，没有就新建
        String filePath = Environment.getExternalStorageDirectory()+"/箱号/";
           makeFilePath(filePath, fileName);
           Log.e("Tag","fn="+makeFilePath(filePath,fileName).getName()+"    >"+ t++);
           final String strFilePath = filePath + fileName;
           // 每次写入时，都换行写
           String strContent = boxNum + "\r\n";
           try {
               File file = new File(strFilePath);
               if (!file.exists()) {
                   file.getParentFile().mkdirs();
                   file.createNewFile();
               }
               Log.e("TestFile", "Create the file:" + strFilePath);
               RandomAccessFile raf = new RandomAccessFile(file, "rwd");
               raf.seek(file.length());
               raf.write(strContent.getBytes());
               raf.close();
           } catch (Exception e) {
               Log.e("TestFile", "Error on write File:" + e);
           }

           FilePath = strFilePath;
           FileName = fileName;
    }

    //删除整个文件夹方法
    public boolean deleteAllFile(File file) {
        //file目标文件夹绝对路径
        if (file.exists()) { //指定文件是否存在
            if (file.isFile()) { //该路径名表示的文件是否是一个标准文件
                file.delete(); //删除该文件
                Log.e("TRg","delete");
            } else if (file.isDirectory()) { //该路径名表示的文件是否是一个目录（文件夹）
                File[] files = file.listFiles(); //列出当前文件夹下的所有文件
                for (File f : files) {
                    deleteAllFile(f); //递归删除
                    Log.e("fileName", f.getName()); //打印文件名
                }
            }
//            file.delete(); //删除文件夹（song,art,lyric）
        }
        return true;
    }



    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e+"");
        }
    }
    // 生成文件
    public File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }


    private void getQCCJ(String a, String b) {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn1", a);
//        map6.put("", b);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_QC, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","State="+checkBarcodeResult.getState()+"   res="+res);
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码已扫入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL，对比失败");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 10) {
                    data_pass.setText("FAIL,SN未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 12) {
                    data_pass.setText("FAIL,未测试统计");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 7) {
                    data_pass.setText("FAIL,未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void getQACJ(String a, String b) {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn1", a);
//        map6.put("", b);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_QA, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","State="+checkBarcodeResult.getState()+"   res="+res);
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码已扫入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL，对比失败");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 10) {
                    data_pass.setText("FAIL,SN未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 12) {
                    data_pass.setText("FAIL,未测试统计");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 7) {
                    data_pass.setText("FAIL,未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void getOneTest() {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn", key);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.test_url, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getState());
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1())) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4())) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    for (int i = 0; i < str.length; i++) {
                        Log.e("tag","str="+str[i]+"     str4="+str4);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }


    private void getOneMaintain() {
        String cxname = tv_cxname.getText().toString();
        String job = station1.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn", key);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_maintain, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
                public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getState());
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    Log.e("TAG","str="+str+"    str4="+str4);
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void getOneWigh() {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn1", key1);
        map6.put("weight", key);
        map6.put("workerCode", job);
        map6.put("lineCode",cxname);
        Log.e("wight","key1="+key1+"   key="+key);
        OkHttpUtils.getInstance().post(Constants.weigh_url, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","Date1="+checkBarcodeResult.getDate1());
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该SN码未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL，暂无该箱号");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL，重量误差大于标准重量");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if(checkBarcodeResult.getState() == 6){
                    data_pass.setText("FAIL，重量误差小于标准重量");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    private void uploadbox_ch(final int num) {
        String job = station1.getText().toString();
        Map<String, String> map6 = new HashMap<>();
        map6.put("sn", key1);
        map6.put("sn6", key);
        map6.put("workerCode", job);
        OkHttpUtils.getInstance().post(Constants.station4_box_pcba_url, map6, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("访问服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "访问服务器失败,请重新访问", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = checkBarcodeResult.getDate1().split("@");
                    String str5[] = null;
                    if (!"".equals(checkBarcodeResult.getDate5())) {
                        str5 = checkBarcodeResult.getDate5().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4())) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    data_pass.setText("PASS，箱号绑定成功");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    bs = 6;
                    if (null != str5) {
                        if (str5.length >= 2) {
                            thisboxcount.setText(str5[1]);//本箱数量
                        }
                        if (str5.length >= 1) {
                            boxcount.setText(str5[0]);//箱数
                        }
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已统计数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);//未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    if (str[1].equals("" + num)) {
                        //当扫到20的时候高光就跳到箱号
                        SN2_3Text();
                        bs = 1;
                        boxcount.setText(str[0]);
                    }
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL，未录入该SN码");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL，该SN码已绑定箱号");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 3) {
                    data_pass.setText("绑定箱号FAIL，请重试");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL，未绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 12) {
                    data_pass.setText("FAIL，未测试统计");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 14) {
                    data_pass.setText("FAIL，未投入统计");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
            }
        });
    }


    private Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {      //判断标志位
                case 10:
                    data_pass.setText("连接服务器失败");
                    String n = (String) msg.obj;
                    Toast.makeText(CheckActivity.this, "连接服务失败\n" + n, Toast.LENGTH_LONG).show();
                    break;
                case 0:
                    CheckBarcodeResult checkBarcodeResult = (CheckBarcodeResult) msg.obj;
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1())) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4())) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }

                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }
                    Type listType = new TypeToken<ArrayList<ProductCode>>() {
                    }.getType();
                    bindingCodelist = new Gson().fromJson((String) checkBarcodeResult.getDate(), listType);
                    dataList.clear();
                    if (bindingCodelist.size() != 0 && bindingCodelist != null) {
                        for (ProductCode productCode : bindingCodelist) {
                            dataList.add(productCode.getSn5());
                        }
                    }
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//总检测数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                    break;
                case 1:
                    data_pass.setText("FAIL,该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                    break;
                case 2:
                    data_pass.setText("FAIL,该SN码已批退");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                    break;
                case 5:
                    data_pass.setText("FAIL，该箱号未绑定产品");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                    break;
                case 4:
                    data_pass.setText("批退失败，请重新操作");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                    break;
            }
        }
    };



    private void SN3Text_PCBA1() {
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));

    }

    private void resultDataText2() {
        et_SN1.setText("");
        SN1_PASS.setText("");
        et_SN2.setText("");
        SN2_PASS.setText("");
        et_SN3.setText("");
        SN3_PASS.setText("");
        data_pass.setText("");
    }

    //即将扫描位置显示高光背景
    private void SN1_2Text() {
        SN1Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN2Name.setTextColor(getResources().getColor(R.color.bg_bs));
    }


    private void SN3_2Text() {
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN2Name.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    private void SN2_3Text() {
        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN3Name.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    private void SN2_1Text() {
        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));
        et_SN1.setText("");
        SN1_PASS.setText("");
        et_SN2.setText("");
        SN2_PASS.setText("");
        data_pass.setText("");
    }

    private void SN2Text() {
        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        check_result.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        check_result.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    private void SN3Text() {
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        check_result.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        check_result.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    private void SNText() {
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        check_result.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        check_result.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    private void SN4Text() {
        SN1Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN3Name.setTextColor(getResources().getColor(R.color.bg_bs));
    }

    /**
     * 扫描返回后初始化数据
     */
    private void setInitialise() {
        bs = 1;
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));
        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
        et_SN1.setText("");
        et_SN2.setText("");
        et_SN3.setText("");
        data_pass.setText("");
        SN1_PASS.setText("");
        SN2_PASS.setText("");
        SN3_PASS.setText("");
    }

    private void resultDataText1() {
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));
        check_result.setTextColor(getResources().getColor(R.color.bg_black));
        check_result.setBackgroundColor(getResources().getColor(R.color.bg_bs));
    }





    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if(resultCode==1){
                String json = data.getStringExtra("json");
                String json1 = data.getStringExtra("json1");
                String json2 = data.getStringExtra("json2");
                boxNumCount = data.getStringExtra("boxNumCount");
                Map<String, String> maps = gson.fromJson(json, new TypeToken<Map<String, String>>() {
                }.getType());
                Map<String,List<Double>> wMap = gson.fromJson(json2,new TypeToken<Map<String,List<Double>>>(){
                }.getType());

                Log.e("tag","json1="+json1+"     wMap="+wMap+"    maps="+maps);
                String model = maps.get("slemodel");//模式
                if(model.equals("出货")||model.indexOf("QC") != -1 && bs==1){
                    String cpname = maps.get("cpname");
                    getAllCode(cpname);
                }
                String cpname = maps.get("cpname");//产品名称
                if (model.contains("机重") || model.contains("箱重")){
                    uphss.setText(wMap.get(cpname).get(0).toString());
                    uphpj.setText(wMap.get(cpname).get(1).toString());
                    uphmax.setText(wMap.get(cpname).get(2).toString());
                    uphmix.setText(wMap.get(cpname).get(3).toString());
                }

                //得到工作站所涉及的条码数据
                gclists = gson.fromJson(json1, new TypeToken<ArrayList<ProductScheduling>>() {
                }.getType());
//                Log.e("tag","gclists22="+gclists);
                setData(maps, gclists);
                //保存设置的数据
                SPData.WriteEquipment(CheckActivity.this, json);
                //保存条码规则数据
                SPData.WriteGC(CheckActivity.this, json1);
                //显示光标
                et_result.requestFocus();//获取焦点 光标出现
            }else if(resultCode==2){
                String wsjson = data.getStringExtra("wsjson");
                wslists = gson.fromJson(wsjson, new TypeToken<ArrayList<String>>() {
                }.getType());//得到所要批退的功能
                codeStation = new CodeStation();
                for(int i = 0 ;i < wslists.size();i++){
                    if(wslists.get(i).equals("绑定")){
                        codeStation.setOneString("绑定");
                    }else if(wslists.get(i).equals("测试统计")){
                        codeStation.setTwoString("测试统计");
                    }else if(wslists.get(i).equals("ZQC")){
                        codeStation.setThreeString("ZQC");
                    }else if(wslists.get(i).equals("投入统计")){
                        codeStation.setFourString("投入统计");
                    }else if(wslists.get(i).equals("WQC")){
                        codeStation.setFiveString("WQC");
                    }else if(wslists.get(i).equals("机重")){
                        codeStation.setSixString("机重");
                    }else if(wslists.get(i).equals("箱重")){
                        codeStation.setEightString("箱重");
                    }else if(wslists.get(i).equals("绑箱")){
                        codeStation.setSevenString("绑箱");
                    }else if(wslists.get(i).equals("维修统计")){
                        codeStation.setNineString("维修统计");
                    } else if(wslists.get(i).equals("产出统计")){
                        codeStation.setTenString("产出统计");
                    } else if(wslists.get(i).equals("QC抽检")){
                        codeStation.setTenString("QC抽检");
                    }else if(wslists.get(i).equals("QA抽检")){
                        codeStation.setTenString("QA抽检");
                    } else if(wslists.get(i).equals("入库")){
                        codeStation.setTenString("入库");
                    }else if(wslists.get(i).equals("出库")){
                        codeStation.setTenString("出库");
                    }
                }
//                Log.e("tag","codeStation22="+codeStation);
                //保存设置的数据
                SPData.WriteWSjson(CheckActivity.this, wsjson);
            }


        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String xh = "";

    private void setData(Map<String, String> maps, List<ProductScheduling> gclists) {
        bs = 1;
        tmgzmaps.clear();
        if (gclists != null) {
            for (int i = 0; i < gclists.size(); i++) {
                tmgzmaps.put("zf1" + gclists.get(i).getCodeName(), gclists.get(i).getKeyCharOne());
                tmgzmaps.put("zf2" + gclists.get(i).getCodeName(), gclists.get(i).getKeyCharTwo());
                tmgzmaps.put("cd" + gclists.get(i).getCodeName(), gclists.get(i).getCodeLength() + "");
                if(gclists.get(i).getCodeName().contains("箱号")){
                    xh = gclists.get(i).getCodeName();
                }
            }
        }
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));
        title_smjl.setTextColor(getResources().getColor(R.color.bg_black));
        title_smjl.setBackgroundColor(getResources().getColor(R.color.bg_buleinfo));
        clearData_station();
        if (maps != null && gclists != null) {
            String str = maps.get("sledtname");
            if (!maps.get("sledtname").equals("")) {
                tv_station.setText(maps.get("sledtname"));//工作站
            }
            String cpname = "";
            if (maps.get("cpname") != null) {
                cpname = maps.get("cpname");
            }
            String cxname = "";
            if (maps.get("cxname") != null) {
                cxname = maps.get("cxname");
            }
            if (cpname != "" && cxname != "") {
                tv_cpname.setText(cpname);//产品名
                tv_cxname.setText(cxname); //产线
                if (!maps.get("slemodel").equals("")) {
                    String model = maps.get("slemodel");//模式
                    modelinfo.setText(model);
                    SN1Name.setText("");
                    SN2Name.setText("");
                    SN3Name.setText("");
                    if (model.indexOf("重") != -1) {
                        model = "称重";
                    } else if (model.indexOf("测试") != -1) {
                        model = "测试";
                    } else if (model.indexOf("投入") != -1) {
                        model = "投入";
                    }else if (model.indexOf("QC") != -1) {
                        model = "QC";
                    }else if (model.indexOf("QA") != -1) {
                        model = "QA";
                    } else if (model.indexOf("产出") != -1) {
                        model = "产出";
                    }else if (model.indexOf("维修") != -1) {
                        model = "维修";
                    } else if (model.indexOf("入库") != -1) {
                        model = "入库";
                    }else if (model.indexOf("出库") != -1) {
                        model = "出库";
                    }
                    switch (model) {
                        case "绑定":
                            check_title.setText("扫码绑定");
                            gather.setText("未绑定");
                            ptcount.setText("已绑定数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 1) {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 2) {
                                        SN3Name.setText(gclists.get(i).getCodeName());
                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "装箱":
                            check_title.setText("扫码装箱");
                            gather.setText("未装箱");
                            ptcount.setText("已装箱数:");
                            setHighlight();
                            llboxcount.setVisibility(View.VISIBLE);
                            llthisboxcount.setVisibility(View.VISIBLE);
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (gclists.get(i).getCodeName().contains("箱号")) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    } else {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    }
                                }
                            }
                            break;
                        case "称重":
                            check_title.setText("扫码称重");
                            gather.setText("未称重");
                            ptcount.setText("已称重数:");
                            setHighlight();
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    }
                                }
                            }
                            SN1Name.setText("重量");
                            break;
                        case "测试":
                            check_title.setText("扫码测试统计");
                            gather.setText("未测试统计");
                            ptcount.setText("已统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 1) {
//                                        SN2Name.setText(gclists.get(i).getCodeName());
//                                    } else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "维修":
                            check_title.setText("扫码维修统计");
                            gather.setText("未维修统计");
                            ptcount.setText("已维修数量:");
                            setHighlight();
//                            Log.e("tag","gclists=>>>"+gclists);
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 1) {
//                                        SN2Name.setText(gclists.get(i).getCodeName());
//                                    } else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "投入":
                            check_title.setText("扫码投入统计");
                            gather.setText("未投入统计");
                            ptcount.setText("已统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 1) {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 2) {
                                        SN3Name.setText(gclists.get(i).getCodeName());
                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "入库":
                            check_title.setText("扫码入库统计");
                            gather.setText("未入库统计");
                            ptcount.setText("已入库统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 1) {
//                                        SN2Name.setText(gclists.get(i).getCodeName());
//                                    } else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "出库":
                            check_title.setText("扫码出库统计");
                            gather.setText("未出库统计");
                            ptcount.setText("已出库统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 1) {
//                                        SN2Name.setText(gclists.get(i).getCodeName());
//                                    } else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;

                        case "QC":
                            check_title.setText("扫码QC抽检");
                            gather.setText("未抽检统计");
                            ptcount.setText("已统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 1) {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "产出":
                            check_title.setText("扫码产出统计");
                            gather.setText("未产出统计");
                            ptcount.setText("已统计数:");
                            setHighlight();
                            if (gclists.size() != 0) {
                                for (int i = 0; i < gclists.size(); i++) {
                                    if (i == 0) {
                                        SN1Name.setText(gclists.get(i).getCodeName());
                                    } else if (i == 1) {
                                        SN2Name.setText(gclists.get(i).getCodeName());
                                    }
//                                    else if (i == 2) {
//                                        SN3Name.setText(gclists.get(i).getCodeName());
//                                    }
                                }
                            }
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llboxcount.setVisibility(View.GONE);
                            llthisboxcount.setVisibility(View.GONE);
                            break;
                        case "出货":
                            check_title.setText("扫码出货");
                            ptcount.setText("已出货数:");
                            llboxcount.setVisibility(View.GONE);
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llthisboxcount.setVisibility(View.VISIBLE);
                            title_xs.setText("未出货数:");
                            gather.setText("本箱");
                            String g = SPData.ReadCHGC(this);
                            if(!g.equals("")){
                                ArrayList <String> m = gson.fromJson(g, new TypeToken<ArrayList<String>>() {
                                }.getType());
                                if (m.size() != 0) {
                                    for (int i = 0; i < m.size(); i++) {
                                        if (i == 0) {
                                            SN1Name.setText(m.get(i));
                                        } else if (i == 1) {
                                            SN2Name.setText(m.get(i));
                                        } else if (i == 2) {
                                            SN3Name.setText(m.get(i));
                                        }
                                    }
                                }
                            }
                            setHighlight();
                            title_smjl.setBackgroundColor(getResources().getColor(R.color.bg_bule));
                            title_smjl.setTextColor(getResources().getColor(R.color.bg_bs));
                            SN1Name.setTextColor(getResources().getColor(R.color.bg_black));
                            SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_buleinfo));
                            break;
                        case "QA":
                            check_title.setText("扫码QA抽检");
                            gather.setText("本次抽检数");
                            title_xs.setText("未抽检数:");
                            ptcount.setText("已抽检数:");
                            llboxcount.setVisibility(View.GONE);
                            check_Inputnumber.setVisibility(View.VISIBLE);
                            llthisboxcount.setVisibility(View.VISIBLE);
                            String gg = SPData.ReadCHGC(this);
                            if(!gg.equals("")){
                                ArrayList <String> m = gson.fromJson(gg, new TypeToken<ArrayList<String>>() {
                                }.getType());
                                if (m.size() != 0) {
                                    for (int i = 0; i < m.size(); i++) {
                                        if (i == 0) {
                                            SN1Name.setText(m.get(i));
                                        } else if (i == 1) {
                                            SN2Name.setText(m.get(i));
                                        }
//                                        else if (i == 2) {
//                                            SN3Name.setText(m.get(i));
//                                        }
                                    }
                                }
                            }
                            setHighlight();
                            title_smjl.setBackgroundColor(getResources().getColor(R.color.bg_bule));
                            title_smjl.setTextColor(getResources().getColor(R.color.bg_bs));
                            SN1Name.setTextColor(getResources().getColor(R.color.bg_black));
                            SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_buleinfo));

                            break;
                    }
                }
                if (maps.get("sledtsn1")!=null&&!maps.get("sledtsn1").equals("空闲")) {
                    String str1[] = maps.get("sledtsn1").split("#");
                    station1.setText(str1[0]);
                    station1.setBackgroundColor(getResources().getColor(R.color.bg_bule));
                    station1.setTextColor(getResources().getColor(R.color.bg_bs));
                } else {
                    station1.setText("空闲");
                    station1count.setText("0");
                }
                if (maps.get("sledtsn2")!=null&&!maps.get("sledtsn2").equals("空闲")) {
                    String str2[] = maps.get("sledtsn2").split("#");
                    station2.setText(str2[0]);
                } else {
                    station2.setText("空闲");
                    station2count.setText("0");
                }
                if (maps.get("sledtsn3")!=null&&!maps.get("sledtsn3").equals("空闲")) {
                    String str3[] = maps.get("sledtsn3").split("#");
                    station3.setText(str3[0]);
                } else {
                    station3.setText("空闲");
                    station3count.setText("0");
                }
                if (maps.get("sledtsn4")!=null&&!maps.get("sledtsn4").equals("空闲")) {
                    String str4[] = maps.get("sledtsn4").split("#");
                    station4.setText(str4[0]);
                } else {
                    station4.setText("空闲");
                    station4count.setText("0");
                }
                if (maps.get("sledtsn5")!=null&&!maps.get("sledtsn5").equals("空闲")) {
                    String str5[] = maps.get("sledtsn5").split("#");
                    station5.setText(str5[0]);
                } else {
                    station5.setText("空闲");
                    station5count.setText("0");
                }
                if (maps.get("sledtsn6")!=null&&!maps.get("sledtsn6").equals("空闲")) {
                    String str6[] = maps.get("sledtsn6").split("#");
                    station6.setText(str6[0]);
                } else {
                    station6.setText("空闲");
                    station6count.setText("0");
                }
            }

        } else {
            Toast.makeText(getApplicationContext(), "设置数据过期，请重新设置", Toast.LENGTH_LONG).show();
        }

    }

    BroadcastReceiver mNetworkStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo etherNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
                NetworkInfo mobileNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                //网络是否连接
                boolean isConnect = (wifiNetInfo != null && wifiNetInfo.isConnected())
                        || (etherNetInfo != null && etherNetInfo.isConnected())
                        || (mobileNetInfo != null && mobileNetInfo.isConnected());
                if (isConnect) {
                    Toast.makeText(context, "网络正常！", Toast.LENGTH_SHORT).show();
                    //这里需要mac验证，就添加checkMac()方法；
                    //不通过mac验证的版本更新，就添加checkVersion()方法
                    checkMac(Utils.getDevID().toUpperCase());
                    Log.e("Tag","mac= "+Utils.getDevID().toUpperCase());
                } else {
                    Toast.makeText(context, "请检查网络设置！", Toast.LENGTH_SHORT).show();
                }

            }
        }

    };

    public void getData() {
        if (numberCountCodeList != null && numberCountCodeList.size() != 0) {
            String g = SPData.ReadEquipment(CheckActivity.this);
            Map<String, String> maps = gson.fromJson(g, new TypeToken<Map<String, String>>() {
            }.getType());
            List<String> listid = new ArrayList<>();
            List<String> listidone = new ArrayList<>();
            for (int i = 0; i < numberCountCodeList.size(); i++) {
                listidone.add(numberCountCodeList.get(i).getLocationCode());
            }
            Map<String, Integer> mapslist = new HashMap<String, Integer>();
            Log.e("tag","maps="+maps);
            String id1[] = maps.get("sledtsn1").split("#");
                if (!id1[0].equals("空闲")) {
                    listid.add(id1[1]);
                    mapslist.put(id1[1], 1);
                }

            String id2[] = maps.get("sledtsn2").split("#");
            if (!id2[0].equals("空闲")) {
                listid.add(id2[1]);
                mapslist.put(id2[1], 2);
            }
            String id3[] = maps.get("sledtsn3").split("#");
            if (!id3[0].equals("空闲")) {
                listid.add(id3[1]);
                mapslist.put(id3[1], 3);
            }
            String id4[] = maps.get("sledtsn4").split("#");
            if (!id4[0].equals("空闲")) {
                listid.add(id4[1]);
                mapslist.put(id4[1], 4);
            }
            String id5[] = maps.get("sledtsn5").split("#");
            if (!id5[0].equals("空闲")) {
                listid.add(id5[1]);
                mapslist.put(id5[1], 5);
            }
            String id6[] = maps.get("sledtsn6").split("#");
            if (!id6[0].equals("空闲")) {
                listid.add(id6[1]);
                mapslist.put(id6[1], 6);
            }
            Log.e("tag","mapslist="+mapslist);
            if (numberCountCodeList.size() > listid.size()) {
                for (int i = 0; i < numberCountCodeList.size(); i++) {
                    if (!listid.contains(numberCountCodeList.get(i).getLocationCode())) {
                        listid.add(numberCountCodeList.get(i).getLocationCode());
                        mapslist.put(numberCountCodeList.get(i).getLocationCode(), listid.size() + 1);
                    }
                }
            } else if (numberCountCodeList.size() < listid.size()) {
                for (int i = 0; i < listid.size(); i++) {
                    if (!listidone.contains(listid.get(i))) {
                        mapslist.remove(listid.get(i));
                        listid.remove(listid.get(i));
                    }
                }
            }
            for (int i = 0; i < numberCountCodeList.size(); i++) {
                if (listid.contains(numberCountCodeList.get(i).getLocationCode())) {
                    int j = mapslist.get(numberCountCodeList.get(i).getLocationCode());

                    if (j == 1) {
                        station1.setText(numberCountCodeList.get(i).getWorkerCode());
                        station1count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    } else if (j == 2) {
                        station2.setText(numberCountCodeList.get(i).getWorkerCode());
                        station2count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    } else if (j == 3) {
                        station3.setText(numberCountCodeList.get(i).getWorkerCode());
                        station3count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    } else if (j == 4) {
                        station4.setText(numberCountCodeList.get(i).getWorkerCode());
                        station4count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    } else if (j == 5) {
                        station5.setText(numberCountCodeList.get(i).getWorkerCode());
                        station5count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    } else if (j == 6) {
                        station6.setText(numberCountCodeList.get(i).getWorkerCode());
                        station6count.setText(numberCountCodeList.get(i).getCountNum() + "");
                    }
                }
            }

            Map<String, String> maps1 = new HashMap<>();
            maps1.put("sledtname", tv_station.getText().toString());//工作站编号
            maps1.put("slemodel", modelinfo.getText().toString());//模式
            maps1.put("cpname", tv_cpname.getText().toString());//产品名
            maps1.put("setmyid", maps.get("setmyid"));
            for (int i = 0; i < numberCountCodeList.size(); i++) {
                if (listid.contains(numberCountCodeList.get(i).getLocationCode())) {
                    int j = mapslist.get(numberCountCodeList.get(i).getLocationCode());
                    if (j == 1) {
                        maps1.put("sledtsn1", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    } else if (j == 2) {
                        maps1.put("sledtsn2", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    } else if (j == 3) {
                        maps1.put("sledtsn3", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    } else if (j == 4) {
                        maps1.put("sledtsn4", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    } else if (j == 5) {
                        maps1.put("sledtsn5", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    } else if (j == 6) {
                        maps1.put("sledtsn6", numberCountCodeList.get(i).getWorkerCode() + "#" + numberCountCodeList.get(i).getLocationCode());
                    }
                }
            }
            if (numberCountCodeList.size() == 1) {
                maps1.put("sledtsn2", "空闲");
                maps1.put("sledtsn3", "空闲");
                maps1.put("sledtsn4", "空闲");
                maps1.put("sledtsn5", "空闲");
                maps1.put("sledtsn6", "空闲");
            } else if (numberCountCodeList.size() == 2) {
                maps1.put("sledtsn3", "空闲");
                maps1.put("sledtsn4", "空闲");
                maps1.put("sledtsn5", "空闲");
                maps1.put("sledtsn6", "空闲");
            } else if (numberCountCodeList.size() == 3) {
                maps1.put("sledtsn4", "空闲");
                maps1.put("sledtsn5", "空闲");
                maps1.put("sledtsn6", "空闲");
            } else if (numberCountCodeList.size() == 4) {
                maps1.put("sledtsn5", "空闲");
                maps1.put("sledtsn6", "空闲");
            } else if (numberCountCodeList.size() == 5) {
                maps1.put("sledtsn6", "空闲");
            }
            Gson gson = new Gson();
            String json = gson.toJson(maps1);
            //保存设置的数据
            SPData.WriteEquipment(CheckActivity.this, json);
        }
    }

    /**
     * item 点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for (int i = 0; i < bindingCodelist.size(); i++) {
            if (bindingCodelist.get(i).getSn5().equals(dataList.get(position))) {
                et_SN1.setText(bindingCodelist.get(i).getSn1());
                et_SN2.setText(bindingCodelist.get(i).getSn2());
                et_SN3.setText(bindingCodelist.get(i).getSn3());
            }
        }
        dataList.get(position);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Check Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("Tag","onStart");
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    protected void onRestart() {
        Log.e("Tag","onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.e("Tag","onResume");

        //获取报存的序列号
//        String s = getSharedPreferences("xuliehao",MODE_PRIVATE).getString("xlh","");
//        if (!"".equals(s) && null != s){
//            int i = Integer.parseInt(s.replace(".txt","").split("_")[3])+1;
//            String s1 ="0000"+ i;
//            if (s1.length() > 5){
//                s1 = s1.substring(s1.length()-5);
//            }
//            xuliehao = s1;
//        }

        registerReceiver(mNetworkStatusReceiver, filter);
        String model = modelinfo.getText().toString();
        if (!"".equals(model) && null != model){
            if (model.contains("机重") || model.contains("箱重")){
                weights();
            }else {
                uph();
            }
        } else {
            uph();
        }
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("Tag","onStop");
        unregisterReceiver(mNetworkStatusReceiver);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    protected void onDestroy() {
        Log.e("Tag","onDestroy");
        super.onDestroy();
    }

    /**
     * QA
     */
    private void getOneQC(){
        String job = station1.getText().toString();
        String qc = modelinfo.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("sn", key);
        map.put("qc",qc);
        map.put("workerCode", job);//工号
        map.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_casualInspection, map, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("连接服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "连接服务失败\n" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getState());
                if (checkBarcodeResult.getState() == 0) {
                    dataList = new ArrayList<String>();
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    Type listType = new TypeToken<ProductCode>() {
                    }.getType();
                    Log.e("tag","getDate="+checkBarcodeResult.getDate());
                    bindingCodelist = new Gson().fromJson(String.valueOf(checkBarcodeResult.getDate()), listType);
                    Log.e("tag","bindingCodelist="+bindingCodelist);
                    if (null != bindingCodelist && bindingCodelist.size() != 0) {
                        for (ProductCode productCode : bindingCodelist) {
                            dataList.add(productCode.getSn5());
                        }
                    }
                    if (dataList != null && dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType1 = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType1);
                    }
                    if (null != bindingCodelist && bindingCodelist.size() != 0) {
                        no_bindnum.setText(bindingCodelist.size() + "");//本箱数量
                    }
                    for (int i = 0; i < str.length; i++) {
                        Log.e("tag","str="+str[i]+"    str4="+str4);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已抽检数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]);  //未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    getData();
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,未录入该产品");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    dataList = new ArrayList<String>();
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    Type listType = new TypeToken<List<ProductCode>>() {
                    }.getType();
                    bindingCodelist = new Gson().fromJson(String.valueOf(checkBarcodeResult.getDate()), listType);
                    if (bindingCodelist.size() != 0 && bindingCodelist != null) {
                        for (ProductCode productCode : bindingCodelist) {
                            dataList.add(productCode.getSn5());
                        }
                    }
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    no_bindnum.setText(bindingCodelist.size() + "");//本箱数量
                    for (int i = 0; i < str.length; i++) {
                        Log.e("Ta","str="+str[i]+"    str4="+str4);
                    }
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已抽检数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]); //未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("FAIL,已检测");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 4) {
                    data_pass.setText("FAIL,抽检失败");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL,该箱号未绑定产品");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
            }
        });
    }

    /**
     * 扫箱出货
     */
    public void getOneShipment() {
        String job = station1.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("boxNum", key);//箱号
        map.put("workerCode", job);//工号
        OkHttpUtils.getInstance().post(Constants.url_shipment, map, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("连接服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "连接服务失败\n" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    dataList = new ArrayList<String>();
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1()) && null != checkBarcodeResult.getDate1()) {
                        str = checkBarcodeResult.getDate1().split("@");
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4()) && null != checkBarcodeResult.getDate4()) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    Type listType = new TypeToken<ArrayList<ProductCode>>() {
                    }.getType();
                    bindingCodelist = new Gson().fromJson((String) checkBarcodeResult.getDate(), listType);
                    if (bindingCodelist.size() != 0 && bindingCodelist != null) {
                        for (ProductCode productCode : bindingCodelist) {
                            dataList.add(productCode.getSn5());
                        }
                    }
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType1 = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType1);
                    }
                    no_bindnum.setText(bindingCodelist.size() + "");//本箱数量
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已抽检数量
                        }
                        if (str.length >= 4) {
                            thisboxcount.setText(str[3]);
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS,[" + key + "]出货成功");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    getData();
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("FAIL,该箱号暂无库存");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("FAIL,该箱已出货");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 10) {
                    data_pass.setText("FAIL,出货失败");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
            }
        });
    }

    public void getOneBinding(String sn1, String sn2, String sn3, String sn4) {
        String job = station1.getText().toString();
        String cxname = tv_cxname.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("sn1", sn1);
        if(!"".equals(sn2)){
            map.put("sn2", sn2);
        }
        if(!"".equals(sn3)){
            map.put("sn3", sn3);
        }
        if(!"".equals(sn4)){
            map.put("sn4", sn4);
        }
        map.put("workerCode", job);//工号
        map.put("lineCode",cxname);//产线
        OkHttpUtils.getInstance().post(Constants.binding_url, map, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                data_pass.setText("连接服务器失败");
                data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                Toast.makeText(getApplicationContext(), "连接服务失败\n" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("TAG","State=="+checkBarcodeResult.getState()+"    Date1="+checkBarcodeResult.getDate1());
                if (checkBarcodeResult.getState() == 0) {
                    String str[] = null;//@左边是计划数量，@右边是生产数量
                    if (!"".equals(checkBarcodeResult.getDate1())) {
                        str = checkBarcodeResult.getDate1().split("@");
//                        Log.e("TAG","str=="+str);
                    }
                    String[] str4 = null;
                    if (!"".equals(checkBarcodeResult.getDate4())) {
                        str4 = checkBarcodeResult.getDate4().split("@");
                    }
                    if (checkBarcodeResult.getDate3() != null && !"".equals(checkBarcodeResult.getDate3())) {
                        Type listType = new TypeToken<ArrayList<String>>() {
                        }.getType();
                        dataList = new Gson().fromJson(checkBarcodeResult.getDate3(), listType);
                    }
                    if (checkBarcodeResult.getDate2() != null && !"".equals(checkBarcodeResult.getDate2())) {
                        Type listType = new TypeToken<ArrayList<NumberCountCode>>() {
                        }.getType();
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate2(), listType);
                    }

                    Log.e("tag","str="+str[3]+"    str4="+str4.toString());
                    if (null != str) {
                        if (str.length >= 1) {
                            all_count.setText(str[0]);//计划数量
                        }
                        if (str.length >= 2) {
                            count.setText(str[1]);//投入数量
                        }
                        if (str.length >= 3) {
                            fallcount.setText(str[2]);//已抽检数量
                        }
                        if (str.length >= 4) {
                            no_bindnum.setText(str[3]); //未统计数量
                        }
                    }
                    if (null != str4) {
                        if (str4.length >= 1) {
                            model.setText(str4[0]);//型号
                        }
                        if (str4.length >= 2) {
                            part_number.setText(str4[1]);//料号
                        }
                    }
                    data_pass.setText("PASS");
                    data_pass.setTextColor(getResources().getColor(R.color.bg_green));
                    if (dataList != null || dataList.size() != 0) {
                        lv_nodata.setAdapter(new MainAdapter(CheckActivity.this, dataList));
                    }
                    getData();
//                    task.setDate(3,mOneScan);
                } else if (checkBarcodeResult.getState() == 1) {
                    data_pass.setText("该SN码未录入");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 2) {
                    data_pass.setText("该SN码已绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                } else if (checkBarcodeResult.getState() == 3) {
                    data_pass.setText("FAIL");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 5) {
                    data_pass.setText("FAIL，小板已绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 6) {
                    data_pass.setText("FAIL，盘芯已绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }else if (checkBarcodeResult.getState() == 7) {
                    data_pass.setText("FAIL，SN4已绑定");
                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
                }
                resultDataText1();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String[] languages = getResources().getStringArray(R.array.languages);
        if(languages[position].equals("抽检")){
            bs=1;
        }else if(languages[position].equals("批退")){
            bs=2;
            Intent intent = new Intent(getApplicationContext(), SelectWorkStationActivity.class);//NewActivity是目标Activity
            //要传递的值
            intent.putExtra("qcname",modelinfo.getText().toString() );
            //启动Activity
            startActivityForResult(intent, 0);//使用时需定义变量requestCode
        }
        Toast.makeText(getApplicationContext(),languages[position],Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /**
     * 检查mac是否可用
     */
    private void checkMac(String mac) {
        String url = Constants.url_mac_verify+"?mac1="+mac;
        OkHttpUtils.getInstance().get(url, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Log.e("scanner","网络访问失败!");
            }

            @Override
            public void onSuccess(String res, Response response) {
                 if (null != res && !"".equals(res) && response.isSuccessful()){
                     Log.e("Tag", "res=" + res);
                     final CheckMacBean checkMacBean = new Gson().fromJson(res, CheckMacBean.class);
                     if (checkMacBean.getState() != 0) {
                         //设备不可用
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 new AlertDialog.Builder(CheckActivity.this)
                                         .setTitle(R.string.hint)
                                         .setMessage(checkMacBean.getMsg()+"\nMAC:  "+Utils.getDevID().toUpperCase())
                                         .setCancelable(false)
                                         .show();
                             }
                         });
                     }
                     if (!isFirst) {
                         checkVersion();
                     }
                 }
            }
        });
    }



    class TimerTaskTest extends TimerTask {
        private int bs = 0;
        private String sn = "";

        public TimerTaskTest() {
        }

        public void setDate(int bs, String sn) {
            this.bs = bs;
            this.sn = sn;
        }

        @Override
        public void run() {
//            selectUHP(bs, sn);//调用接口获取数据
        }

//        private void selectUHP(int bs, String sn) {
//            Map<String, String> params = new HashMap<>();
//            params.put("bs", String.valueOf(bs));
//            params.put("SN", sn);
//            OkHttpUtils.getInstance().post(Constants.url_select_uph, params, new HttpCallBack() {
//                @Override
//                public void onFailure(IOException e) {
//                    data_pass.setText("访问出错");
//                    data_pass.setTextColor(getResources().getColor(R.color.red_fail));
//                    Toast.makeText(getApplicationContext(), "访问出错\n" + e.toString(), Toast.LENGTH_LONG).show();
//                }
//
//                @Override
//                public void onSuccess(String res, Response response) {
//                    CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
//                    if (checkBarcodeResult.getState() == 0) {
//                        String str = checkBarcodeResult.getDate1();
//                        // 需要做的事:发送消息
//                        Message message = new Message();
//                        message.what = 1;
//                        message.obj = str;
//                        handler.sendMessage(message);
//                    }
//                }
//            });
//        }
    }


    private void setHighlight() {
        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
        SN1Name.setTextColor(getResources().getColor(R.color.bg_bs));
        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_buleinfo));
        SN3Name.setTextColor(getResources().getColor(R.color.bg_black));
        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_buleinfo));
    }

//    private void setHighlightTwo() {
//        SN3Name.setBackgroundColor(getResources().getColor(R.color.bg_bule));
//        SN3Name.setTextColor(getResources().getColor(R.color.bg_bs));
//        SN2Name.setTextColor(getResources().getColor(R.color.bg_black));
//        SN2Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
//        SN1Name.setTextColor(getResources().getColor(R.color.bg_black));
//        SN1Name.setBackgroundColor(getResources().getColor(R.color.bg_bs));
//    }

    private void weights(){
        machine_weight.setText("机重：");
        machine_weight_error.setText("允许误差：");
        box_weight.setText("箱重：");
        box_weight_error.setText("允许误差：");
    }

    private void uph(){
        machine_weight.setText("实时：");
        machine_weight_error.setText("平均：");
        box_weight.setText("最大：");
        box_weight_error.setText("最小：");
    }

    /**
     * 检查更新版本
     */
    private void checkVersion() {
        isFirst = true;
        String url = Constants.url_version_updating + "?soft_name=" + soft_name + "&soft_version=" + BuildConfig.VERSION_NAME;
        OkHttpUtils.getInstance().get(url, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Log.e("scanner","网络访问失败!");
            }

            @Override
            public void onSuccess(String res, Response response) {
                if (res != null && !"".equals(res) && response.isSuccessful()) {
                    Log.e(TAG, "onResponse:version= " + response+"   res="+res);
                    final RecommendversionBean versionBean = new Gson().fromJson(res, RecommendversionBean.class);
                    if (versionBean.getCode() == 1) {
                        final RecommendversionBean.DataBean data = versionBean.getData();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(CheckActivity.this)
                                        .setTitle("版本更新")
                                        .setMessage(versionBean.getMsg())
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        })
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Toast.makeText(CheckActivity.this, "后台下载中...", Toast.LENGTH_SHORT).show();
                                                downloadApk(data.getSoft_url());
                                            }
                                        }).show();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 普通下载apk安装
     *
     * @param url
     */
    private void downloadApk(final String url) {
        OkHttpUtils.getInstance().get(url, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CheckActivity.this, "下载失败!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSuccess(String res, Response response) {
                if (response != null && response.isSuccessful()) {
                    InputStream inputStream = response.body().byteStream();

                    final String filePath = AppManager.getAppDir() + url.substring(url.lastIndexOf("/") + 1);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(filePath);
                    int len = 0;
                    byte[] buffer = new byte[1024 * 10];
                    while ((len = inputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.flush();
                    fos.close();
                    inputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //安装
                            AppManager.install(CheckActivity.this, filePath);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CheckActivity.this, "下载失败!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }) ;
    }

}
