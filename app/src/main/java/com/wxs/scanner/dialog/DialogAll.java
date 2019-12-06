package com.wxs.scanner.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wxs.scanner.bean.CheckBarcodeResult;
import com.wxs.scanner.utils.Constants;
import com.wxs.scanner.utils.http.HttpCallBack;
import com.wxs.scanner.utils.http.OkHttpUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;


/**
 * Created by Administrator on 2017/11/3 0003.
 */

public class DialogAll {

    public static void showNormalDialog(final String stations,final Context context, final String job, final String key, final Handler myhandler) {
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("友情提示！");
        normalDialog.setMessage("确认批退？");
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                batchAnnealing(workstation);
                        Map<String, String> map = new HashMap<>();
                        map.put("sn", key);//pcba
                        map.put("workerCode", job);//工号
                        map.put("station", stations);//工作站
                        OkHttpUtils.getInstance().post(Constants.url_batchAnnealing, map, new HttpCallBack() {
                            @Override
                            public void onFailure(IOException e) {
                                Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                msg.what = 10; //标志消息的标志
                                msg.obj = e.toString();
                                myhandler.sendMessage(msg);
                            }

                            @Override
                            public void onSuccess(String res, Response response) {
                                CheckBarcodeResult checkBarcodeResult = new Gson().fromJson(res, CheckBarcodeResult.class);
                                    if (checkBarcodeResult.getState() == 0) {
                                        Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                        msg.what = 0; //标志消息的标志
                                        msg.obj = checkBarcodeResult;
                                        myhandler.sendMessage(msg);
                                    } else if (checkBarcodeResult.getState() == 1){
                                        Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                        msg.what = 1; //标志消息的标志
                                        msg.obj = checkBarcodeResult;
                                        myhandler.sendMessage(msg);
                                    } else if (checkBarcodeResult.getState() == 2) {
                                        Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                        msg.what = 2; //标志消息的标志
                                        msg.obj = checkBarcodeResult;
                                        myhandler.sendMessage(msg);

                                    }else if(checkBarcodeResult.getState() ==4){
                                        Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                        msg.what = 4; //标志消息的标志
                                        msg.obj = checkBarcodeResult;
                                        myhandler.sendMessage(msg);
                                    }else if(checkBarcodeResult.getState() == 5){
                                        Message msg = Message.obtain(); //从全局池中返回一个message实例，避免多次创建message（如new Message）
                                        msg.what = 5; //标志消息的标志
                                        msg.obj = checkBarcodeResult;
                                        myhandler.sendMessage(msg);
                                    }
                            }
                        });
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }

    public static void showListDialog(final Context context, final TextView textView) {
        final String[] items = { "3","5","7"};
        final AlertDialog.Builder listDialog =
        new AlertDialog.Builder(context);
        listDialog.setTitle("请选择工作站：");
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // which 下标从0开始
                if(which==0){
                    textView.setText("3");
                }else if(which==1) {
                    textView.setText("5");
                }else{
                    textView.setText("7");
                }
                }
            });
        listDialog.show();
    }

}
