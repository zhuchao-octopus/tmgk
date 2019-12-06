package com.wxs.scanner.utils.http;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by ztz on 2017/4/13 0013.
 */

public abstract class HttpCallBack {
    public abstract void onFailure(IOException e);

    public abstract void onSuccess(String res, Response response);

}
