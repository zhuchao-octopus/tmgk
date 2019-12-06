package com.wxs.scanner.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxs.scanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/6 0006.
 */

public class MainAdapter extends android.widget.BaseAdapter {

    private Context mContext;
    private List<String> dataLsit = new ArrayList<>();
    private LayoutInflater mInflater;

    public MainAdapter(Context context, List<String> list) {
        this.mContext = context;
        this.dataLsit = list;
        this.mInflater = LayoutInflater.from(context);
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
        ViewHolder holder;
        //观察convertView随ListView滚动情况
        Log.v("MyListViewBase", "getView " + position + " " + convertView);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            /*得到各个控件的对象*/
            holder.data = (TextView) convertView.findViewById(R.id.tv_item);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
        holder.data.setText(dataLsit.get(position));
        return convertView;
    }

    /*存放控件*/
    public final class ViewHolder {
        public TextView data;
    }

}
