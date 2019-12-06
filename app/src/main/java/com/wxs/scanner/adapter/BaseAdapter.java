package com.wxs.scanner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wxs.scanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztz on 2017/3/29 0029.
 */

public class BaseAdapter extends RecyclerView.Adapter<BaseAdapter.BaseHolder> {
    private Context mContext;
    private List<String> mResult=new ArrayList<>();

    public BaseAdapter(Context context) {
        this.mContext=context;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recyclerview_scan, parent,false);
        return new BaseHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseHolder holder, int position) {

        holder.mTv_result.setText(mResult.get(position));
    }

    @Override
    public int getItemCount() {
        return mResult.size();
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        private  TextView mTv_result;

        public BaseHolder(View itemView) {
            super(itemView);
            mTv_result = (TextView) itemView.findViewById(R.id.tv_result);
        }
    }

    public void addAll(String scanResult,RecyclerView recyclerView,TextView textView) {
        mResult.add(scanResult);
        notifyDataSetChanged();

        recyclerView.smoothScrollToPosition(getItemCount()-1);

        textView.setText(mContext.getString(R.string.result_num,getItemCount()));

    }

}
