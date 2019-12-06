package com.wxs.scanner.activity.set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxs.scanner.R;
import com.wxs.scanner.bean.CheckBarcodeResult;
import com.wxs.scanner.bean.ProductName;
import com.wxs.scanner.bean.ProductScheduling;
import com.wxs.scanner.bean.WorkLocation;
import com.wxs.scanner.bean.WorkStationCode;
import com.wxs.scanner.utils.Constants;
import com.wxs.scanner.utils.http.HttpCallBack;
import com.wxs.scanner.utils.http.OkHttpUtils;
import com.wxs.scanner.utils.sp.SPData;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;


/**
 * hl
 * 设置页面
 * Created by Administrator on 2017/8/24 0024.
 */

public class SettingsActivity extends Activity implements View.OnClickListener {


    private TextView advancedsettings, btok, btno, sladvancedfile, slbtsx;
    private TextView sledtsn1, sledtsn2, sledtsn3, sledtsn4, sledtsn5, sledtsn6;
    private TextView sledtname, sledtid1, sledtid2, sledtid3, sledtid4, sledtid5, sledtid6;
    private Spinner slemodel,spinner,spi;
    private EditText set_myid;
    private Gson gson = new Gson();
    private Map<String, String> maps;
    private List<WorkLocation> numberCountCodeList = new ArrayList<>();//存储工位信息
    private String model;//所选模式
    private List<ProductScheduling> rulelists = new ArrayList<>();;//存储规则的集合
    private List<String> lists =new ArrayList<>();//存储产品名的集合
    private List<String> cpnamelists = new ArrayList<>();//存放工作站名的集合
    private Map<String,String> workmaps = new HashMap<>();//key 工作站名，values 工作站编号
    private String boxNumCount = "";
    private Map<String,List<Double>> wMap = new HashMap<>();

    private String cx = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        getResult();
        initView();
        getList();//获取产品列表
        getLine();//获取产线列表
        setOnClick();
    }

    private void setUI() {
        if (maps != null) {
            Log.e("tag","maps="+maps);
            sledtname.setText(maps.get("sledtname"));
            set_myid.setText(maps.get("setmyid"));
            for(int i = 0 ;i<lists.size();i++){
                if(maps.get("cpname").equals(lists.get(i))){
                    spi.setSelection(i, true);
                }
            }
            for(int i = 0 ;i<lists.size();i++){
                if(maps.get("cpname").equals(lists.get(i))){
                    spinner.setSelection(i, true);
                }
            }
            if (!maps.get("sledtsn1").equals("空闲")) {
                String id1[] = maps.get("sledtsn1").split("#");
                sledtsn1.setText(id1[0]);
                sledtid1.setText(id1[1]);
            } else {
                sledtsn1.setText("空闲");
                sledtid1.setText("空闲");
            }
            if (!maps.get("sledtsn2").equals("空闲")) {
                String id2[] = maps.get("sledtsn2").split("#");
                sledtsn2.setText(id2[0]);
                sledtid2.setText(id2[1]);
            } else {
                sledtsn2.setText("空闲");
                sledtid2.setText("空闲");
            }
            if (!maps.get("sledtsn3").equals("空闲")) {
                String id3[] = maps.get("sledtsn3").split("#");
                sledtsn3.setText(id3[0]);
                sledtid3.setText(id3[1]);
            } else {
                sledtsn3.setText("空闲");
                sledtid3.setText("空闲");
            }
            if (!maps.get("sledtsn4").equals("空闲")) {
                String id4[] = maps.get("sledtsn4").split("#");
                sledtsn4.setText(id4[0]);
                sledtid4.setText(id4[1]);
            } else {
                sledtsn4.setText("空闲");
                sledtid4.setText("空闲");
            }
            if (!maps.get("sledtsn5").equals("空闲")) {
                String id5[] = maps.get("sledtsn5").split("#");
                sledtsn5.setText(id5[0]);
                sledtid5.setText(id5[1]);
            } else {
                sledtsn5.setText("空闲");
                sledtid5.setText("空闲");
            }
            if (!maps.get("sledtsn6").equals("空闲")) {
                String id6[] = maps.get("sledtsn6").split("#");
                sledtsn6.setText(id6[0]);
                sledtid6.setText(id6[1]);
            } else {
                sledtsn6.setText("空闲");
                sledtid6.setText("空闲");
            }

        }
    }

    private void setOnClick() {
        /**
         * 选择模式
         */
        slemodel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model = (String) slemodel.getSelectedItem();
                if(workmaps!=null||workmaps.size()!=0){
                    for (Map.Entry<String, String> entry : workmaps.entrySet()) {
                        if(model.equals(entry.getKey())){
                            sledtname.setText(entry.getValue().toString());
                            break;
                        }
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * 选择产品
         */
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String model = (String) spinner.getSelectedItem();
//                if (!"".equals(cx)){
                    getBarcodeRules(model);//获取产品对应的工作站
//                }else {
//                    Toast.makeText(SettingsActivity.this,"请先选择产线！",Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        /**
         * 选择产线
         */
        spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String model = (String) spi.getSelectedItem();
                cx = model;    //获取产线对应的工作站
                Log.e("TAG","cx="+cx);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /**
     * 初始化UI
     */
    private void initView() {
        spi = findViewById(R.id.spi);
        sledtname = (TextView) findViewById(R.id.sl_edt_name);
        spinner = (Spinner) findViewById(R.id.spinner);
        set_myid = (EditText) findViewById(R.id.set_myid);
        slemodel = (Spinner) findViewById(R.id.spinner2);
        sledtsn1 = (TextView) findViewById(R.id.sl_edt_sn1);
        sledtsn2 = (TextView) findViewById(R.id.sl_edt_sn2);
        sledtsn3 = (TextView) findViewById(R.id.sl_edt_sn3);
        sledtsn4 = (TextView) findViewById(R.id.sl_edt_sn4);
        sledtsn5 = (TextView) findViewById(R.id.sl_edt_sn5);
        sledtsn6 = (TextView) findViewById(R.id.sl_edt_sn6);
        sledtid1 = (TextView) findViewById(R.id.sl_edt_id1);
        sledtid2 = (TextView) findViewById(R.id.sl_edt_id2);
        sledtid3 = (TextView) findViewById(R.id.sl_edt_id3);
        sledtid4 = (TextView) findViewById(R.id.sl_edt_id4);
        sledtid5 = (TextView) findViewById(R.id.sl_edt_id5);
        sledtid6 = (TextView) findViewById(R.id.sl_edt_id6);
        advancedsettings = (TextView) findViewById(R.id.sl_advanced_settings);
        sladvancedfile = (TextView) findViewById(R.id.sl_advanced_file);
        slbtsx = (TextView) findViewById(R.id.sl_bt_sx);
        btok = (TextView) findViewById(R.id.sl_bt_ok);
        btno = (TextView) findViewById(R.id.sl_bt_no);
        advancedsettings.setOnClickListener(this);
        sladvancedfile.setOnClickListener(this);
        btok.setOnClickListener(this);
        btno.setOnClickListener(this);
        slbtsx.setOnClickListener(this);

    }


    /**
     * 初始化数据
     */
    public void getResult() {
        Intent intent = getIntent();
        String json = intent.getStringExtra("json");
//        SPData.WriteEquipment(SettingsActivity.this,json);
        maps = gson.fromJson(json, new TypeToken<Map<String, String>>() {
        }.getType());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sl_bt_sx:
                String workstation = sledtname.getText().toString();
                String cxname = spi.getSelectedItem().toString();
                if (workstation.equals("0")) {
                    Toast.makeText(getApplication(), "请先选择模式", Toast.LENGTH_LONG).show();
                } else {
                    selectPCBA(workstation,cxname);
                }
                break;
            case R.id.sl_bt_ok://保存设置
                SaveSet();
                break;
            case R.id.sl_bt_no://取消设置
                finish();
                break;
            case R.id.sl_advanced_settings:
                Intent intent1 = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent1);
                break;
            case R.id.sl_advanced_file:
                Intent intent2 = new Intent();
                intent2.setClassName("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI");
                startActivity(intent2);
                break;
        }
    }


    private void SaveSet() {
        if (set_myid.getText().toString().equals("")) {
            Toast.makeText(getApplication(), "请先输入对应工位编号", Toast.LENGTH_LONG).show();
        } else {
            Map<String, String> maps = new HashMap<>();
            maps.put("sledtname", sledtname.getText().toString());//工作站
            maps.put("slemodel", slemodel.getSelectedItem().toString());//当前模式
            maps.put("setmyid", set_myid.getText().toString());//当前的工位编号
            maps.put("cpname",spinner.getSelectedItem().toString());//产品名
            maps.put("cxname",spi.getSelectedItem().toString());//产线
            if (!sledtsn1.getText().toString().equals("空闲") && !sledtid1.getText().toString().equals("空闲")) {
                maps.put("sledtsn1", sledtsn1.getText().toString() + "#" + sledtid1.getText().toString());
            } else {
                maps.put("sledtsn1", "空闲");
            }
            if (!sledtsn2.getText().toString().equals("空闲") && !sledtid2.getText().toString().equals("空闲")) {
                maps.put("sledtsn2", sledtsn2.getText().toString() + "#" + sledtid2.getText().toString());
            } else {
                maps.put("sledtsn2", "空闲");
            }
            if (!sledtsn3.getText().toString().equals("空闲") && !sledtid3.getText().toString().equals("空闲")) {
                maps.put("sledtsn3", sledtsn3.getText().toString() + "#" + sledtid3.getText().toString());
            } else {
                maps.put("sledtsn3", "空闲");
            }
            if (!sledtsn4.getText().toString().equals("空闲") && !sledtid4.getText().toString().equals("空闲")) {
                maps.put("sledtsn4", sledtsn4.getText().toString() + "#" + sledtid4.getText().toString());
            } else {
                maps.put("sledtsn4", "空闲");
            }
            if (!sledtsn5.getText().toString().equals("空闲") && !sledtid5.getText().toString().equals("空闲")) {
                maps.put("sledtsn5", sledtsn5.getText().toString() + "#" + sledtid5.getText().toString());
            } else {
                maps.put("sledtsn5", "空闲");
            }
            if (!sledtsn6.getText().toString().equals("空闲") && !sledtid6.getText().toString().equals("空闲")) {
                maps.put("sledtsn6", sledtsn6.getText().toString() + "#" + sledtid6.getText().toString());
            } else {
                maps.put("sledtsn6", "空闲");
            }
            Gson gson = new Gson();
            String json = gson.toJson(maps);//返回设置的数据
            String json1 = gson.toJson(rulelists);//返回条码规则
            String json2 = gson.toJson(wMap);//标准重量与允许误差
            Intent intent = new Intent();
            intent.putExtra("json", json);
            intent.putExtra("json1", json1);
            intent.putExtra("json2",json2);
            intent.putExtra("boxNumCount",boxNumCount);
            //设置结果信息
            setResult(1, intent);//需要提前定义变量resultCode，初始值大于0
            /**隐藏软键盘**/
            View view = getWindow().peekDecorView();
            if (view != null) {
                InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            finish();
        }


    }

    /**
     * 调用接口获取数据
     */
    private void selectPCBA(String key,String cxname) {
        Map<String, String> map = new HashMap<>();
        map.put("wsNumber", key);
        map.put("lineCode",cxname);
        OkHttpUtils.getInstance().post(Constants.url_select_bh, map, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(getApplicationContext(), "访问出错\n" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                Log.e("tag","checkBarcodeResult="+checkBarcodeResult.getDate1());

                if (checkBarcodeResult.getState() == 0){
                    if(checkBarcodeResult.getDate() != null && !"".equals(checkBarcodeResult.getDate())){//条码规则
                        Type listType = new TypeToken<ArrayList<ProductScheduling>>() {
                        }.getType();
                        //保存工作站涉及到的条码的规则数据
                        rulelists = new Gson().fromJson((String) checkBarcodeResult.getDate(), listType);
                    }
                    if (checkBarcodeResult.getDate1() != null && !"".equals(checkBarcodeResult.getDate1())) {//工位数据
                        Type listType = new TypeToken<ArrayList<WorkLocation>>() {
                        }.getType();
                        //保存工位数据
                        numberCountCodeList = new Gson().fromJson(checkBarcodeResult.getDate1(), listType);
                    }
//                    Log.e("tag","numberCountCodeList="+numberCountCodeList);
                    for (int i = 0; i < numberCountCodeList.size(); i++) {
                        if (i == 0) {
                            sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                            sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                        } else if (i == 1) {
                            if (numberCountCodeList.get(i).getLocationCode().equals(set_myid.getText().toString())) {
                                sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                                sledtsn2.setText(numberCountCodeList.get(i - 1).getWorkerCode());
                                sledtid2.setText(numberCountCodeList.get(i - 1).getLocationCode());
                            } else {
                                sledtsn2.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid2.setText(numberCountCodeList.get(i).getLocationCode());
                            }
                        } else if (i == 2) {
                            if (numberCountCodeList.get(i).getLocationCode().equals(set_myid.getText().toString())) {
                                sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                                sledtsn3.setText(numberCountCodeList.get(i - 2).getWorkerCode());
                                sledtid3.setText(numberCountCodeList.get(i - 2).getLocationCode());
                            } else {
                                sledtsn3.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid3.setText(numberCountCodeList.get(i).getLocationCode());
                            }
                        } else if (i == 3) {
                            if (numberCountCodeList.get(i).getLocationCode().equals(set_myid.getText().toString())) {
                                sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                                sledtsn4.setText(numberCountCodeList.get(i - 3).getWorkerCode());
                                sledtid4.setText(numberCountCodeList.get(i - 3).getLocationCode());
                            } else {
                                sledtsn4.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid4.setText(numberCountCodeList.get(i).getLocationCode());
                            }
                        } else if (i == 4) {
                            if (numberCountCodeList.get(i).getLocationCode().equals(set_myid.getText().toString())) {
                                sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                                sledtsn5.setText(numberCountCodeList.get(i - 4).getWorkerCode());
                                sledtid5.setText(numberCountCodeList.get(i - 4).getLocationCode());
                            } else {
                                sledtsn5.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid5.setText(numberCountCodeList.get(i).getLocationCode());
                            }
                        } else if (i == 5) {
                            if (numberCountCodeList.get(i).getLocationCode().equals(set_myid.getText().toString())) {
                                sledtsn1.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid1.setText(numberCountCodeList.get(i).getLocationCode());
                                sledtsn6.setText(numberCountCodeList.get(i - 5).getWorkerCode());
                                sledtid6.setText(numberCountCodeList.get(i - 5).getLocationCode());
                            } else {
                                sledtsn6.setText(numberCountCodeList.get(i).getWorkerCode());
                                sledtid6.setText(numberCountCodeList.get(i).getLocationCode());
                            }
                        }
                    }
                    setWorkDate(numberCountCodeList.size());
                } else {
                    setWorkDate(0);
                    Toast.makeText(getApplicationContext(), "该工作站未启用", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setWorkDate(int i) {
        if (i == 0) {
            sledtsn1.setText("空闲");
            sledtid1.setText("空闲");
            sledtsn2.setText("空闲");
            sledtid2.setText("空闲");
            sledtsn3.setText("空闲");
            sledtid3.setText("空闲");
            sledtsn4.setText("空闲");
            sledtid4.setText("空闲");
            sledtsn5.setText("空闲");
            sledtid5.setText("空闲");
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        } else if (i == 1) {
            sledtsn2.setText("空闲");
            sledtid2.setText("空闲");
            sledtsn3.setText("空闲");
            sledtid3.setText("空闲");
            sledtsn4.setText("空闲");
            sledtid4.setText("空闲");
            sledtsn5.setText("空闲");
            sledtid5.setText("空闲");
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        } else if (i == 2) {
            sledtsn3.setText("空闲");
            sledtid3.setText("空闲");
            sledtsn4.setText("空闲");
            sledtid4.setText("空闲");
            sledtsn5.setText("空闲");
            sledtid5.setText("空闲");
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        } else if (i == 3) {
            sledtsn4.setText("空闲");
            sledtid4.setText("空闲");
            sledtsn5.setText("空闲");
            sledtid5.setText("空闲");
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        } else if (i == 4) {
            sledtsn5.setText("空闲");
            sledtid5.setText("空闲");
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        } else if (i == 5) {
            sledtsn6.setText("空闲");
            sledtid6.setText("空闲");
        }

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            }else if(msg.what == 2){//得到产品列表
                lists = (List<String>) msg.obj;
                Log.e("tag","lists="+lists);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_dropdown_item);
                for(String strs :lists){
                    dataAdapter.add(strs);
                }
                spinner.setAdapter(dataAdapter);
                setUI();
            }else if(msg.what == 4){//得到产线列表
                String str = (String) msg.obj;
                lists =gson.fromJson(str, new TypeToken<ArrayList<String>>() {
                }.getType());
                Log.e("Tag","lists="+lists);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_dropdown_item);
                for(String strs :lists){
                    dataAdapter.add(strs);
                }
                spi.setAdapter(dataAdapter);
                setUI();
            }else if(msg.what == 3){//得到所选产品对应的工作站
                String str = (String) msg.obj;
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SettingsActivity.this,android.R.layout.simple_spinner_dropdown_item);
                    List<WorkStationCode> workStationCodes = gson.fromJson(str, new TypeToken<ArrayList<WorkStationCode>>() {
                    }.getType());
                    if(workStationCodes!=null && workStationCodes.size()!=0){
                        for(int i = 0;i<workStationCodes.size();i++){
                            workmaps.put(workStationCodes.get(i).getWsFunction(),workStationCodes.get(i).getWsNumber());
                            dataAdapter.add(workStationCodes.get(i).getWsFunction());
                            if(!workStationCodes.get(i).getBoxNumCount().equals("")){
                                boxNumCount = workStationCodes.get(i).getBoxNumCount();
                            }
                        }
                    }
                    slemodel.setAdapter(dataAdapter);
            }else if(msg.what == 10){
                slemodel.setAdapter(null);
                Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 获取产品列表
     */
    public void getList() {
        OkHttpUtils.getInstance().get(Constants.url_getlist, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(getApplicationContext(), "链接服务器失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
//                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                ProductName productName = new Gson().fromJson(res,ProductName.class);
                if (productName.getState() == 0) {
//                    String str = (String) checkBarcodeResult.getDate();
                    List<ProductName.Date> dateList = productName.getDate();
                    List<String> pList = new ArrayList<>();
                    if (dateList.size() != 0){
                        for (int i = 0; i < dateList.size(); i++) {
                             List<Double> list = new ArrayList<>();
                             String pname = dateList.get(i).getPname();
                             int pid = dateList.get(i).getPid();
                             String price = dateList.get(i).getPrice();
                             double pweight = dateList.get(i).getPweight();
                             double perror = dateList.get(i).getPerror();
                             double bweight = dateList.get(i).getBweight();
                             double berror = dateList.get(i).getBerror();

                             list.add(pweight);
                             list.add(perror);
                             list.add(bweight);
                             list.add(berror);
                             wMap.put(pname,list);

                             pList.add(pname);
                        }
                    }
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 2;
                    message.obj = pList;
                    handler.sendMessage(message);
                }
            }
        });
    }



    /**
     * 获取工作站列表
     * @param pname
     */
    private void getBarcodeRules(String pname){
        final Map<String, String> params = new HashMap<>();
        params.put("pname", pname);
        OkHttpUtils.getInstance().post(Constants.url_getBarcodeRules, params, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(getApplicationContext(), "获取产品条码规则失败" + e.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    String str = (String) checkBarcodeResult.getDate();
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 3;
                    message.obj = str;
                    handler.sendMessage(message);
                }else{
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 10;
                    message.obj = "请重新选择产品";
                    handler.sendMessage(message);
                }
            }
        });
    }


    /**
     * 获取产线列表
     */
    public void getLine() {
        OkHttpUtils.getInstance().get(Constants.url_line, new HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(getApplicationContext(), "链接服务器失败", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(String res, Response response) {
                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                if (checkBarcodeResult.getState() == 0) {
                    String str = (String) checkBarcodeResult.getDate();
                    // 需要做的事:发送消息
                    Message message = new Message();
                    message.what = 4;
                    message.obj = str;
                    handler.sendMessage(message);
                }
            }
        });
    }
}
