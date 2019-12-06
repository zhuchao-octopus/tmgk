package com.wxs.scanner.utils;

import android.accounts.NetworkErrorException;
import android.util.Log;

import com.google.gson.Gson;
import com.wxs.scanner.bean.FileBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FilesUploadUtils {
    public static final String TAG = "UploadHelper";
    private static final MediaType MEDIA_TYPE_TXT = MediaType.parse("file/txt");
    private final OkHttpClient client = new OkHttpClient();

    public boolean upload(String fileName,File file) throws NetworkErrorException {
        boolean b = false;
        RequestBody fileBody = RequestBody.create(MEDIA_TYPE_TXT, file);

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, fileBody)
                .addFormDataPart("folderName", "boxNum")
                .addFormDataPart("pictureName", fileName.replace(".txt",""))
                .build();

        Request request = new Request.Builder()
                .url(Constants.url_file_upload)
                .post(requestBody)
                .build();

        Response response;
        try {
            response = client.newCall(request).execute();
            String jsonString = response.body().string();
//            Log.e(TAG, " upload jsonString =" + jsonString);
            FileBean bean = new Gson().fromJson(jsonString,FileBean.class);
//            Log.e("tag","response="+response.isSuccessful());
            if (!bean.getMsg().equals("success!")){
                b = false;
                throw new NetworkErrorException("upload error code " + response);
            } else {
                b = true;
                JSONObject jsonObject = new JSONObject(jsonString);
                int errorCode = jsonObject.getInt("errorCode");
//                Log.e("Tag","errorCode="+errorCode);
                if (errorCode == 0) {
                    Log.e(TAG, " upload data =" + jsonObject.getString("data"));
                } else {
                    throw new NetworkErrorException("upload error code " + errorCode + ",errorInfo=" + jsonObject.getString("errorInfo"));
                }
            }

        } catch (IOException e) {
            Log.d(TAG, "upload IOException ", e);
        } catch (JSONException e) {
            Log.d(TAG, "upload JSONException ", e);
        }
       return b;
    }
}
