package com.wxs.scanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxs.scanner.R;
import com.wxs.scanner.bean.BindingCode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/2 0002.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {

    private Context mContext;
    private List<BindingCode> bindingCodeslist = new ArrayList<>();
    public HomeAdapter(Context context,List<BindingCode> list){
        this.mContext = context;
        this.bindingCodeslist = list;
    }

//    public void getData(List<BindingCode> list){
//        bindingCodeslist = list;
//        notifyDataSetChanged();
//    }
    @Override
    public HomeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.id_num, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        BindingCode bindingCode1 = bindingCodeslist.get(position);
            holder.tv1.setText(bindingCode1.getPcba_SN());//\获取PCBA码
            holder.tv2.setText(bindingCode1.getPlt_SN());//获取盘心码
            holder.tv3.setText(bindingCode1.getDisk_SN());//获取小板码
            holder.tv4.setText(bindingCode1.getCh_SN());//获取彩盒码
            holder.tv5.setText(bindingCode1.getBoxNum());//获取箱号
//        if(!"".equals(bindingCode1.getBoardNum())){
//            holder.tv6.setText(bindingCode1.getBoardNum());
//        }

    }

    @Override
    public int getItemCount() {
        if(bindingCodeslist==null){
            return 0;
        }else {
            return bindingCodeslist.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv1,tv2,tv3,tv4,tv5;
//        ,tv6;

        public MyViewHolder(View view)
        {
            super(view);
            tv1 = (TextView) view.findViewById(R.id.id_num1);
            tv2 = (TextView) view.findViewById(R.id.id_num2);
            tv3 = (TextView) view.findViewById(R.id.id_num3);
            tv4 = (TextView) view.findViewById(R.id.id_num4);
            tv5 = (TextView) view.findViewById(R.id.id_num5);
//            tv6 = (TextView) view.findViewById(R.id.id_num6);

        }
    }
}
