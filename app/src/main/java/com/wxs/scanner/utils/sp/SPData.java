package com.wxs.scanner.utils.sp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2017/8/24 0024.
 */

public class SPData {

    /**
     * 保存设置的修改后的数据
     * @param context
     * @param json
     */
    public static void WriteWSjson(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wsjson", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("wsjson", json);
        editor.commit();
    }

    /**
     * 取出设置中修改后的数据
     * @param context
     * @return
     */
    public static String ReadWSjson(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("wsjson", Context.MODE_PRIVATE);
        return sharedPreferences.getString("wsjson", "");
    }


    /**
     * 保存设置的修改后的数据
     * @param context
     * @param json
     */
    public static void WriteEquipment(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Equipment1", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Equipment", json);
        editor.commit();
    }

    /**
     * 取出设置中修改后的数据
     * @param context
     * @return
     */
    public static String ReadEquipment(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Equipment1", Context.MODE_PRIVATE);
        return sharedPreferences.getString("Equipment", null);
    }

    /**
     * 保存条码规则数据
     * @param context
     * @param json
     */
    public static void WriteGC(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("gc", json);
        editor.commit();
    }

    /**
     * 取出条码规则数据
     * @param context
     * @return
     */
    public static String ReadGC(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("gc", Context.MODE_PRIVATE);
        return sharedPreferences.getString("gc", "");
    }

    /**
     * 保存条码规则数据
     * @param context
     * @param json
     */
    public static void WriteCHGC(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("allgc", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("allgc", json);
        editor.commit();
    }

    /**
     * 取出条码规则数据
     * @param context
     * @return
     */
    public static String ReadCHGC(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("allgc", Context.MODE_PRIVATE);
        return sharedPreferences.getString("allgc", "");
    }



    /**
     * 保存登录后的数据
     * @param context
     * @param json
     */
    public static void WriteLoingData(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoingData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LoingData", json);
        editor.commit();
    }
    /**
     * 取出登录后的数据
     * @param context
     * @return
     */
    public static String ReadLoingData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoingData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LoingData", "");
    }

    /**
     * 保存扫码一组的数据
     * @param context
     * @param json
     */
    public static void WriteScanCodeData(Context context, String json) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScanCodeData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ScanCodeData", json);
        editor.commit();
    }

    /**
     * 取出设置中修改后的数据
     * @param context
     * @return
     */
    public static String ReadScanCodeData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("ScanCodeData", Context.MODE_PRIVATE);
        return sharedPreferences.getString("ScanCodeData", "");
    }




}
