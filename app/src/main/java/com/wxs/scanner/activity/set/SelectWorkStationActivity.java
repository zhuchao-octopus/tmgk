package com.wxs.scanner.activity.set;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wxs.scanner.R;
import com.wxs.scanner.adapter.SwsAdaoter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/25 0025.
 */

public class SelectWorkStationActivity extends Activity implements View.OnClickListener{

    Gson gson = new Gson();
    private ListView lv_sws;
    private SwsAdaoter swsAdaoter;
    private Button sws_bt_ok,sws_bt_no;
    private List<String> lists =new ArrayList<>();//存储工作站名的集合
    private List<String> wslists = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectworkstation_layout);
        getList();//获取工作站列表
        initView();
        setAdapter();
    }

    /**
     * 配置适配器
     */
    private void setAdapter() {
        if(lists!=null||lists.size()!=0||!"".equals(lists.get(0))){
            swsAdaoter = new SwsAdaoter(SelectWorkStationActivity.this, lists);
            lv_sws.setAdapter(swsAdaoter);
        }
    }

    /**
     * 初始化UI
     */
    private void initView() {
        lv_sws = (ListView) findViewById(R.id.lv_sws);
        sws_bt_ok = (Button) findViewById(R.id.sws_bt_ok);
        sws_bt_ok.setOnClickListener(this);
        sws_bt_no = (Button) findViewById(R.id.sws_bt_no);
        sws_bt_no.setOnClickListener(this);
        lv_sws.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                SwsAdaoter.ViewHolder holder = (SwsAdaoter.ViewHolder) view.getTag();
                // 改变CheckBox的状态
                holder.item_cb.toggle();
                // 将CheckBox的选中状况记录下来
                SwsAdaoter.getIsSelected().put(position, holder.item_cb.isChecked());
            }
        });
    }

    /**
     * 获取产品列表
     */
    public void getList() {
        Intent intent = getIntent();
        String qcname = intent.getStringExtra("qcname");
//        Map<String, String> params = new HashMap<>();
//        params.put("pname", qcname);
//        OkHttpUtils.getInstance().get(Constants.url_getBarcodeRules, new HttpCallBack() {
//            @Override
//            public void onFailure(IOException e) {
//                Toast.makeText(getApplicationContext(), "链接服务器失败", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onSuccess(String res, Response response) {
//                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
//                if (checkBarcodeResult.getState() == 0) {
//                    String str = (String) checkBarcodeResult.getDate();
//                    // 需要做的事:发送消息
//                    Message message = new Message();
//                    message.what = 2;
//                    message.obj = str;
//                    handler.sendMessage(message);
//                }
//            }
//        });
        if(qcname.equals("ZQC")){
            lists.add("绑定");
            lists.add("测试统计");
        }else if(qcname.equals("WQC")){
            lists.add("绑定");
            lists.add("测试统计");
            lists.add("投入统计");
        }else if(qcname.equals("OQC")){
            lists.add("绑定");
            lists.add("测试统计");
            lists.add("投入统计");
            lists.add("机重");
            lists.add("绑箱");
            lists.add("箱重");
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
            } else if (msg.what == 2) {//得到工作站列表
                String str = (String) msg.obj;
                lists =gson.fromJson(str, new TypeToken<ArrayList<String>>() {
                }.getType());
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sws_bt_ok:
                SaveSet();
                break;
            case R.id.sws_bt_no:
                // 遍历list的长度，将已选的按钮设为未选
                for (int i = 0; i < lists.size(); i++) {
                    if (SwsAdaoter.getIsSelected().get(i)) {
                        SwsAdaoter.getIsSelected().put(i, false);
                    }
                }
                // 刷新listview和TextView的显示
                dataChanged();
                this.finish();
                break;
        }
    }

    // 刷新listview和TextView的显示
    private void dataChanged() {
        // 通知listView刷新
        swsAdaoter.notifyDataSetChanged();
    };
    /**
     * 保存选择好的数据
     */
    private void SaveSet() {
        if(lists!=null||lists.size()!=0||!"".equals(lists.get(0))){
            wslists =new ArrayList<>();
            for(int i = 0 ;i<lists.size();i++){
                if (SwsAdaoter.getIsSelected().get(i)) {
                    wslists.add(lists.get(i));
                }
            }
            Gson gson = new Gson();
            String json = gson.toJson(wslists);//返回设置的数据
            Intent intent = new Intent();
            intent.putExtra("wsjson", json);
            //设置结果信息
            setResult(2, intent);//需要提前定义变量resultCode，初始值大于0
            this.finish();
        }
    }
}
