package com.wxs.scanner.utils.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by ztz on 2017/5/11 0011.
 */

public class OkHttpUtils {
    OkHttpClient.Builder mOkHttpClientBuilder =null;
    Handler mHandler=null;
    private OkHttpUtils() {
        mOkHttpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils getInstance() {
        return Holder.okHttpUtils;
    }

    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return mOkHttpClientBuilder;
    }
    private static class Holder {
        private static OkHttpUtils okHttpUtils = new OkHttpUtils();
    }


    public void get(String url, final HttpCallBack httpCallBack) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        mOkHttpClientBuilder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final String res = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                            httpCallBack.onSuccess(res,response);
                    }
                });
            }
        });
    }

    public void post(String url, Map<String,String> params, final HttpCallBack httpCallBack) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
//            Log.e("TAG","entry="+entry.getKey()+"   params="+entry.getValue());
            builder.add(entry.getKey(),entry.getValue().toString());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())
                .build();
        mOkHttpClientBuilder.build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        httpCallBack.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
                final String res = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                            httpCallBack.onSuccess(res,response);
                    }
                });
            }
        });
    }


}
