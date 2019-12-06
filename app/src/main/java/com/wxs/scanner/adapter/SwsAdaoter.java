package com.wxs.scanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.wxs.scanner.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/1/25 0025.
 */

public class SwsAdaoter extends android.widget.BaseAdapter  {
    private Context mContext;
    private List<String> dataLsit = new ArrayList<>();
    private LayoutInflater mInflater;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected;

    public SwsAdaoter(Context context, List<String> list) {
        this.mContext = context;
        this.dataLsit = list;
        this.mInflater = LayoutInflater.from(context);
        this.isSelected = new HashMap<Integer, Boolean>();
        // 初始化数据
        initDate();
    }
    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < dataLsit.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return dataLsit == null ? 0 : dataLsit.size();
    }

    @Override
    public Object getItem(int position) {
        return dataLsit.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwsAdaoter.ViewHolder holder;
        //观察convertView随ListView滚动情况
        Log.v("MyListViewBase", "getView " + position + " " + convertView);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.tv_rbitem, null);
            holder = new SwsAdaoter.ViewHolder();
            /*得到各个控件的对象*/
            holder.data = (TextView) convertView.findViewById(R.id.tv_rb_item);
            holder.item_cb = (CheckBox) convertView.findViewById(R.id.item_cb);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (SwsAdaoter.ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.data.setText(dataLsit.get(position));
        // 根据isSelected来设置checkbox的选中状况
        holder.item_cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        public TextView data;
        public CheckBox item_cb;
    }
    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        SwsAdaoter.isSelected = isSelected;
    }
}
